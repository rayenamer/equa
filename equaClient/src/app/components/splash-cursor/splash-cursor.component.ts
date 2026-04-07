import {
  Component,
  ElementRef,
  ViewChild,
  AfterViewInit,
  OnDestroy,
} from '@angular/core';

@Component({
  selector: 'app-splash-cursor',
  standalone: true,
  templateUrl: './splash-cursor.component.html',
  styleUrls: ['./splash-cursor.component.scss'],
})
export class SplashCursorComponent implements AfterViewInit, OnDestroy {
  @ViewChild('fluidCanvas') canvasRef!: ElementRef<HTMLCanvasElement>;

  private cleanup: (() => void) | null = null;

  ngAfterViewInit(): void {
    const canvas = this.canvasRef?.nativeElement;
    if (!canvas) return;
    this.cleanup = this.initFluid(canvas);
  }

  ngOnDestroy(): void {
    this.cleanup?.();
    this.cleanup = null;
  }

  private initFluid(canvas: HTMLCanvasElement): () => void {
    const SIM_RESOLUTION = 128;
    const DYE_RESOLUTION = 1440;
    const DENSITY_DISSIPATION = 3.5;
    const VELOCITY_DISSIPATION = 2;
    const PRESSURE = 0.1;
    const PRESSURE_ITERATIONS = 20;
    const CURL = 3;
    const SPLAT_RADIUS = 0.2;
    const SPLAT_FORCE = 6000;
    const SHADING = true;
    const COLOR_UPDATE_SPEED = 10;
    const BACK_COLOR = { r: 0.5, g: 0, b: 0 };
    const TRANSPARENT = true;

    let isActive = true;

    interface PointerLike {
      id: number;
      texcoordX: number;
      texcoordY: number;
      prevTexcoordX: number;
      prevTexcoordY: number;
      deltaX: number;
      deltaY: number;
      down: boolean;
      moved: boolean;
      color: { r: number; g: number; b: number };
    }

    const config = {
      SIM_RESOLUTION,
      DYE_RESOLUTION,
      DENSITY_DISSIPATION,
      VELOCITY_DISSIPATION,
      PRESSURE,
      PRESSURE_ITERATIONS,
      CURL,
      SPLAT_RADIUS,
      SPLAT_FORCE,
      SHADING,
      COLOR_UPDATE_SPEED,
      PAUSED: false,
      BACK_COLOR,
      TRANSPARENT,
    };

    const pointers: PointerLike[] = [
      {
        id: -1,
        texcoordX: 0,
        texcoordY: 0,
        prevTexcoordX: 0,
        prevTexcoordY: 0,
        deltaX: 0,
        deltaY: 0,
        down: false,
        moved: false,
        color: [0, 0, 0] as unknown as { r: number; g: number; b: number },
      },
    ];

    const { gl, ext } = this.getWebGLContext(canvas);
    if (!gl || !ext) return () => {};

    if (!ext.supportLinearFiltering) {
      (config as { DYE_RESOLUTION: number }).DYE_RESOLUTION = 256;
      (config as { SHADING: boolean }).SHADING = false;
    }

    let halfFloat: { HALF_FLOAT_OES: number } | null = null;
    const isWebGL2 = !!gl.getParameter(0x1f02); // gl.VERSION
    if (!isWebGL2 && gl.getExtension('OES_texture_half_float')) {
      halfFloat = gl.getExtension('OES_texture_half_float');
    }

    const halfFloatTexType = isWebGL2
      ? (gl as WebGL2RenderingContext).HALF_FLOAT
      : halfFloat?.HALF_FLOAT_OES ?? 0x8d61;

    const supportRenderTextureFormat = (
      gl: WebGLRenderingContext | WebGL2RenderingContext,
      internalFormat: number,
      format: number,
      type: number
    ): boolean => {
      const texture = gl.createTexture();
      gl.bindTexture(gl.TEXTURE_2D, texture);
      gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.NEAREST);
      gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.NEAREST);
      gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, gl.CLAMP_TO_EDGE);
      gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, gl.CLAMP_TO_EDGE);
      gl.texImage2D(
        gl.TEXTURE_2D,
        0,
        internalFormat,
        4,
        4,
        0,
        format,
        type,
        null
      );
      const fbo = gl.createFramebuffer();
      gl.bindFramebuffer(gl.FRAMEBUFFER, fbo);
      gl.framebufferTexture2D(
        gl.FRAMEBUFFER,
        gl.COLOR_ATTACHMENT0,
        gl.TEXTURE_2D,
        texture,
        0
      );
      const status = gl.checkFramebufferStatus(gl.FRAMEBUFFER);
      return status === gl.FRAMEBUFFER_COMPLETE;
    };

    const getSupportedFormat = (
      gl: WebGLRenderingContext | WebGL2RenderingContext,
      internalFormat: number,
      format: number,
      type: number
    ): { internalFormat: number; format: number } | null => {
      if (!supportRenderTextureFormat(gl, internalFormat, format, type)) {
        const gl2 = gl as WebGL2RenderingContext;
        switch (internalFormat) {
          case gl2.R16F:
            return getSupportedFormat(
              gl,
              gl2.RG16F,
              gl2.RG,
              type
            );
          case gl2.RG16F:
            return getSupportedFormat(
              gl,
              gl2.RGBA16F,
              gl2.RGBA,
              type
            );
          default:
            return null;
        }
      }
      return { internalFormat, format };
    };

    const compileShader = (
      type: number,
      source: string,
      keywords?: string[]
    ): WebGLShader => {
      let src = source;
      if (keywords?.length) {
        src = keywords.map((k) => `#define ${k}\n`).join('') + src;
      }
      const shader = gl.createShader(type)!;
      gl.shaderSource(shader, src);
      gl.compileShader(shader);
      if (!gl.getShaderParameter(shader, gl.COMPILE_STATUS)) {
        console.warn(gl.getShaderInfoLog(shader));
      }
      return shader;
    };

    type ProgramWrapper = {
      program: WebGLProgram;
      uniforms: Record<string, WebGLUniformLocation | null>;
    };

    const createProgram = (
      vertexShader: WebGLShader,
      fragmentShader: WebGLShader
    ): ProgramWrapper => {
      const program = gl.createProgram()!;
      gl.attachShader(program, vertexShader);
      gl.attachShader(program, fragmentShader);
      gl.linkProgram(program);
      if (!gl.getProgramParameter(program, gl.LINK_STATUS)) {
        console.warn(gl.getProgramInfoLog(program));
      }
      return { program, uniforms: getUniforms(program) };
    };

    const getUniforms = (
      program: WebGLProgram
    ): Record<string, WebGLUniformLocation | null> => {
      const uniforms: Record<string, WebGLUniformLocation | null> = {};
      const count = gl.getProgramParameter(program, gl.ACTIVE_UNIFORMS);
      for (let i = 0; i < count; i++) {
        const info = gl.getActiveUniform(program, i)!;
        uniforms[info.name] = gl.getUniformLocation(program, info.name);
      }
      return uniforms;
    };

    const baseVertexSource = `
      precision highp float;
      attribute vec2 aPosition;
      varying vec2 vUv;
      varying vec2 vL;
      varying vec2 vR;
      varying vec2 vT;
      varying vec2 vB;
      uniform vec2 texelSize;
      void main () {
        vUv = aPosition * 0.5 + 0.5;
        vL = vUv - vec2(texelSize.x, 0.0);
        vR = vUv + vec2(texelSize.x, 0.0);
        vT = vUv + vec2(0.0, texelSize.y);
        vB = vUv - vec2(0.0, texelSize.y);
        gl_Position = vec4(aPosition, 0.0, 1.0);
      }
    `;

    const baseVertexShader = compileShader(gl.VERTEX_SHADER, baseVertexSource);

    const copyFrag = `
      precision mediump float;
      precision mediump sampler2D;
      varying highp vec2 vUv;
      uniform sampler2D uTexture;
      void main () { gl_FragColor = texture2D(uTexture, vUv); }
    `;
    const clearFrag = `
      precision mediump float;
      precision mediump sampler2D;
      varying highp vec2 vUv;
      uniform sampler2D uTexture;
      uniform float value;
      void main () { gl_FragColor = value * texture2D(uTexture, vUv); }
    `;
    const displayFrag = `
      precision highp float;
      precision highp sampler2D;
      varying vec2 vUv,vL,vR,vT,vB;
      uniform sampler2D uTexture;
      uniform vec2 texelSize;
      vec3 linearToGamma(vec3 c){c=max(c,vec3(0));return max(1.055*pow(c,vec3(0.416666667))-0.055,vec3(0));}
      void main(){
        vec3 c=texture2D(uTexture,vUv).rgb;
        #ifdef SHADING
          vec3 lc=texture2D(uTexture,vL).rgb,rc=texture2D(uTexture,vR).rgb,tc=texture2D(uTexture,vT).rgb,bc=texture2D(uTexture,vB).rgb;
          float dx=length(rc)-length(lc),dy=length(tc)-length(bc);
          vec3 n=normalize(vec3(dx,dy,length(texelSize))),l=vec3(0.,0.,1.);
          c*=clamp(dot(n,l)+0.7,0.7,1.0);
        #endif
        gl_FragColor=vec4(c,max(c.r,max(c.g,c.b)));
      }
    `;
    const splatFrag = `
      precision highp float;
      precision highp sampler2D;
      varying vec2 vUv;
      uniform sampler2D uTarget;
      uniform float aspectRatio;
      uniform vec3 color;
      uniform vec2 point;
      uniform float radius;
      void main(){
        vec2 p=vUv-point.xy;p.x*=aspectRatio;
        vec3 splat=exp(-dot(p,p)/radius)*color;
        gl_FragColor=vec4(texture2D(uTarget,vUv).xyz+splat,1.0);
      }
    `;
    const advectionFrag = `
      precision highp float;
      precision highp sampler2D;
      varying vec2 vUv;
      uniform sampler2D uVelocity;
      uniform sampler2D uSource;
      uniform vec2 texelSize;
      uniform vec2 dyeTexelSize;
      uniform float dt;
      uniform float dissipation;
      vec4 bilerp(sampler2D sam,vec2 uv,vec2 tsize){
        vec2 st=uv/tsize-0.5,iuv=floor(st),fuv=fract(st);
        vec4 a=texture2D(sam,(iuv+vec2(0.5,0.5))*tsize);
        vec4 b=texture2D(sam,(iuv+vec2(1.5,0.5))*tsize);
        vec4 c=texture2D(sam,(iuv+vec2(0.5,1.5))*tsize);
        vec4 d=texture2D(sam,(iuv+vec2(1.5,1.5))*tsize);
        return mix(mix(a,b,fuv.x),mix(c,d,fuv.x),fuv.y);
      }
      void main(){
        #ifdef MANUAL_FILTERING
          vec2 coord=vUv-dt*bilerp(uVelocity,vUv,texelSize).xy*texelSize;
          gl_FragColor=bilerp(uSource,coord,dyeTexelSize)/ (1.0+dissipation*dt);
        #else
          vec2 coord=vUv-dt*texture2D(uVelocity,vUv).xy*texelSize;
          gl_FragColor=texture2D(uSource,coord)/(1.0+dissipation*dt);
        #endif
      }
    `;
    const divergenceFrag = `
      precision mediump float;
      precision mediump sampler2D;
      varying highp vec2 vUv,vL,vR,vT,vB;
      uniform sampler2D uVelocity;
      void main(){
        float L=texture2D(uVelocity,vL).x,R=texture2D(uVelocity,vR).x,T=texture2D(uVelocity,vT).y,B=texture2D(uVelocity,vB).y;
        vec2 C=texture2D(uVelocity,vUv).xy;
        if(vL.x<0.)L=-C.x;if(vR.x>1.)R=-C.x;if(vT.y>1.)T=-C.y;if(vB.y<0.)B=-C.y;
        gl_FragColor=vec4(0.5*(R-L+T-B),0.,0.,1.);
      }
    `;
    const curlFrag = `
      precision mediump float;
      precision mediump sampler2D;
      varying highp vec2 vUv,vL,vR,vT,vB;
      uniform sampler2D uVelocity;
      void main(){
        float L=texture2D(uVelocity,vL).y,R=texture2D(uVelocity,vR).y,T=texture2D(uVelocity,vT).x,B=texture2D(uVelocity,vB).x;
        gl_FragColor=vec4(0.5*(R-L-T+B),0.,0.,1.);
      }
    `;
    const vorticityFrag = `
      precision highp float;
      precision highp sampler2D;
      varying vec2 vUv,vL,vR,vT,vB;
      uniform sampler2D uVelocity;
      uniform sampler2D uCurl;
      uniform float curl;
      uniform float dt;
      void main(){
        float L=texture2D(uCurl,vL).x,R=texture2D(uCurl,vR).x,T=texture2D(uCurl,vT).x,B=texture2D(uCurl,vB).x,C=texture2D(uCurl,vUv).x;
        vec2 force=0.5*vec2(abs(T)-abs(B),abs(R)-abs(L));
        force/=length(force)+0.0001;
        force*=curl*C;force.y*=-1.;
        vec2 velocity=texture2D(uVelocity,vUv).xy+force*dt;
        velocity=min(max(velocity,-1000.),1000.);
        gl_FragColor=vec4(velocity,0.,1.);
      }
    `;
    const pressureFrag = `
      precision mediump float;
      precision mediump sampler2D;
      varying highp vec2 vUv,vL,vR,vT,vB;
      uniform sampler2D uPressure;
      uniform sampler2D uDivergence;
      void main(){
        float L=texture2D(uPressure,vL).x,R=texture2D(uPressure,vR).x,T=texture2D(uPressure,vT).x,B=texture2D(uPressure,vB).x,C=texture2D(uPressure,vUv).x;
        gl_FragColor=vec4((L+R+B+T-texture2D(uDivergence,vUv).x)*0.25,0.,0.,1.);
      }
    `;
    const gradientSubtractFrag = `
      precision mediump float;
      precision mediump sampler2D;
      varying highp vec2 vUv,vL,vR,vT,vB;
      uniform sampler2D uPressure;
      uniform sampler2D uVelocity;
      void main(){
        float L=texture2D(uPressure,vL).x,R=texture2D(uPressure,vR).x,T=texture2D(uPressure,vT).x,B=texture2D(uPressure,vB).x;
        vec2 velocity=texture2D(uVelocity,vUv).xy-vec2(R-L,T-B);
        gl_FragColor=vec4(velocity,0.,1.);
      }
    `;

    const copyShader = compileShader(gl.FRAGMENT_SHADER, copyFrag);
    const clearShader = compileShader(gl.FRAGMENT_SHADER, clearFrag);
    const splatShader = compileShader(gl.FRAGMENT_SHADER, splatFrag);
    const advectionShader = compileShader(
      gl.FRAGMENT_SHADER,
      advectionFrag,
      ext.supportLinearFiltering ? undefined : ['MANUAL_FILTERING']
    );
    const divergenceShader = compileShader(gl.FRAGMENT_SHADER, divergenceFrag);
    const curlShader = compileShader(gl.FRAGMENT_SHADER, curlFrag);
    const vorticityShader = compileShader(gl.FRAGMENT_SHADER, vorticityFrag);
    const pressureShader = compileShader(gl.FRAGMENT_SHADER, pressureFrag);
    const gradientSubtractShader = compileShader(
      gl.FRAGMENT_SHADER,
      gradientSubtractFrag
    );
    const displayShader = compileShader(
      gl.FRAGMENT_SHADER,
      config.SHADING ? '#define SHADING\n' + displayFrag : displayFrag
    );

    const copyProgram = createProgram(baseVertexShader, copyShader);
    const clearProgram = createProgram(baseVertexShader, clearShader);
    const splatProgram = createProgram(baseVertexShader, splatShader);
    const advectionProgram = createProgram(baseVertexShader, advectionShader);
    const divergenceProgram = createProgram(baseVertexShader, divergenceShader);
    const curlProgram = createProgram(baseVertexShader, curlShader);
    const vorticityProgram = createProgram(baseVertexShader, vorticityShader);
    const pressureProgram = createProgram(baseVertexShader, pressureShader);
    const gradientSubtractProgram = createProgram(
      baseVertexShader,
      gradientSubtractShader
    );
    const displayProgram = createProgram(baseVertexShader, displayShader);

    interface FBO {
      texture: WebGLTexture;
      fbo: WebGLFramebuffer;
      width: number;
      height: number;
      texelSizeX: number;
      texelSizeY: number;
      attach: (id: number) => number;
    }

    const createFBO = (
      w: number,
      h: number,
      internalFormat: number,
      format: number,
      type: number,
      param: number
    ): FBO => {
      const texture = gl.createTexture()!;
      gl.bindTexture(gl.TEXTURE_2D, texture);
      gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, param);
      gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, param);
      gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, gl.CLAMP_TO_EDGE);
      gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, gl.CLAMP_TO_EDGE);
      gl.texImage2D(gl.TEXTURE_2D, 0, internalFormat, w, h, 0, format, type, null);
      const fbo = gl.createFramebuffer()!;
      gl.bindFramebuffer(gl.FRAMEBUFFER, fbo);
      gl.framebufferTexture2D(
        gl.FRAMEBUFFER,
        gl.COLOR_ATTACHMENT0,
        gl.TEXTURE_2D,
        texture,
        0
      );
      gl.viewport(0, 0, w, h);
      gl.clear(gl.COLOR_BUFFER_BIT);
      return {
        texture,
        fbo,
        width: w,
        height: h,
        texelSizeX: 1 / w,
        texelSizeY: 1 / h,
        attach(id: number) {
          gl.activeTexture(gl.TEXTURE0 + id);
          gl.bindTexture(gl.TEXTURE_2D, texture);
          return id;
        },
      };
    };

    interface DoubleFBO {
      width: number;
      height: number;
      texelSizeX: number;
      texelSizeY: number;
      read: FBO;
      write: FBO;
      swap: () => void;
    }

    const createDoubleFBO = (
      w: number,
      h: number,
      internalFormat: number,
      format: number,
      type: number,
      param: number
    ): DoubleFBO => {
      let read = createFBO(w, h, internalFormat, format, type, param);
      let write = createFBO(w, h, internalFormat, format, type, param);
      return {
        width: w,
        height: h,
        texelSizeX: read.texelSizeX,
        texelSizeY: read.texelSizeY,
        get read() {
          return read;
        },
        set read(v: FBO) {
          read = v;
        },
        get write() {
          return write;
        },
        set write(v: FBO) {
          write = v;
        },
        swap() {
          const t = read;
          read = write;
          write = t;
        },
      };
    };

    const resizeFBO = (
      target: FBO,
      w: number,
      h: number,
      internalFormat: number,
      format: number,
      type: number,
      param: number
    ): FBO => {
      const newFBO = createFBO(w, h, internalFormat, format, type, param);
      gl.useProgram(copyProgram.program);
      gl.uniform1i(copyProgram.uniforms['uTexture']!, target.attach(0));
      blit(newFBO);
      return newFBO;
    };

    const resizeDoubleFBO = (
      target: DoubleFBO,
      w: number,
      h: number,
      internalFormat: number,
      format: number,
      type: number,
      param: number
    ): DoubleFBO => {
      if (target.width === w && target.height === h) return target;
      target.read = resizeFBO(
        target.read,
        w,
        h,
        internalFormat,
        format,
        type,
        param
      );
      target.write = createFBO(w, h, internalFormat, format, type, param);
      target.width = w;
      target.height = h;
      target.texelSizeX = 1 / w;
      target.texelSizeY = 1 / h;
      return target;
    };

    const scaleByPixelRatio = (input: number): number =>
      Math.floor(input * (window.devicePixelRatio || 1));

    const getResolution = (resolution: number): { width: number; height: number } => {
      const aspectRatio =
        gl.drawingBufferWidth / gl.drawingBufferHeight;
      const ratio = aspectRatio < 1 ? 1 / aspectRatio : aspectRatio;
      const min = Math.round(resolution);
      const max = Math.round(resolution * ratio);
      return gl.drawingBufferWidth > gl.drawingBufferHeight
        ? { width: max, height: min }
        : { width: min, height: max };
    };

    const correctRadius = (r: number): number => {
      const ar = canvas.width / canvas.height;
      return ar > 1 ? r * ar : r;
    };

    let dye: DoubleFBO;
    let velocity: DoubleFBO;
    let divergence: FBO;
    let curl: FBO;
    let pressure: DoubleFBO;

    const initFramebuffers = () => {
      const simRes = getResolution(config.SIM_RESOLUTION);
      const dyeRes = getResolution(config.DYE_RESOLUTION);
      const rgba = ext.formatRGBA!;
      const rg = ext.formatRG!;
      const r = ext.formatR!;
      const filtering = ext.supportLinearFiltering ? gl.LINEAR : gl.NEAREST;
      const texType = ext.halfFloatTexType;

      if (!dye!) {
        dye = createDoubleFBO(
          dyeRes.width,
          dyeRes.height,
          rgba.internalFormat,
          rgba.format,
          texType,
          filtering
        );
      } else {
        dye = resizeDoubleFBO(
          dye,
          dyeRes.width,
          dyeRes.height,
          rgba.internalFormat,
          rgba.format,
          texType,
          filtering
        ) as DoubleFBO;
      }
      if (!velocity!) {
        velocity = createDoubleFBO(
          simRes.width,
          simRes.height,
          rg.internalFormat,
          rg.format,
          texType,
          filtering
        );
      } else {
        velocity = resizeDoubleFBO(
          velocity,
          simRes.width,
          simRes.height,
          rg.internalFormat,
          rg.format,
          texType,
          filtering
        ) as DoubleFBO;
      }
      divergence = createFBO(
        simRes.width,
        simRes.height,
        r.internalFormat,
        r.format,
        texType,
        gl.NEAREST
      );
      curl = createFBO(
        simRes.width,
        simRes.height,
        r.internalFormat,
        r.format,
        texType,
        gl.NEAREST
      );
      if (!pressure!) {
        pressure = createDoubleFBO(
          simRes.width,
          simRes.height,
          r.internalFormat,
          r.format,
          texType,
          gl.NEAREST
        );
      } else {
        pressure = resizeDoubleFBO(
          pressure,
          simRes.width,
          simRes.height,
          r.internalFormat,
          r.format,
          texType,
          gl.NEAREST
        ) as DoubleFBO;
      }
    };

    gl.bindBuffer(gl.ARRAY_BUFFER, gl.createBuffer());
    gl.bufferData(
      gl.ARRAY_BUFFER,
      new Float32Array([-1, -1, -1, 1, 1, 1, 1, -1]),
      gl.STATIC_DRAW
    );
    gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, gl.createBuffer());
    gl.bufferData(
      gl.ELEMENT_ARRAY_BUFFER,
      new Uint16Array([0, 1, 2, 0, 2, 3]),
      gl.STATIC_DRAW
    );
    gl.vertexAttribPointer(0, 2, gl.FLOAT, false, 0, 0);
    gl.enableVertexAttribArray(0);

    const blit = (target: FBO | null, clear = false) => {
      if (target == null) {
        gl.viewport(0, 0, gl.drawingBufferWidth, gl.drawingBufferHeight);
        gl.bindFramebuffer(gl.FRAMEBUFFER, null);
      } else {
        gl.viewport(0, 0, target.width, target.height);
        gl.bindFramebuffer(gl.FRAMEBUFFER, target.fbo);
      }
      if (clear) {
        gl.clearColor(0, 0, 0, 1);
        gl.clear(gl.COLOR_BUFFER_BIT);
      }
      gl.drawElements(gl.TRIANGLES, 6, gl.UNSIGNED_SHORT, 0);
    };

    const generateColor = (): { r: number; g: number; b: number } => {
      const h = Math.random();
      const s = 1;
      const v = 1;
      let r = 0,
        g = 0,
        b = 0;
      const i = Math.floor(h * 6);
      const f = h * 6 - i;
      const p = v * (1 - s);
      const q = v * (1 - f * s);
      const t = v * (1 - (1 - f) * s);
      switch (i % 6) {
        case 0: r = v; g = t; b = p; break;
        case 1: r = q; g = v; b = p; break;
        case 2: r = p; g = v; b = t; break;
        case 3: r = p; g = q; b = v; break;
        case 4: r = t; g = p; b = v; break;
        case 5: r = v; g = p; b = q; break;
      }
      return { r: r * 0.15, g: g * 0.15, b: b * 0.15 };
    };

    const splat = (
      x: number,
      y: number,
      dx: number,
      dy: number,
      color: { r: number; g: number; b: number }
    ) => {
      gl.useProgram(splatProgram.program);
      const u = splatProgram.uniforms;
      gl.uniform1i(u['uTarget']!, velocity.read.attach(0));
      gl.uniform1f(u['aspectRatio']!, canvas.width / canvas.height);
      gl.uniform2f(u['point']!, x, y);
      gl.uniform3f(u['color']!, dx, dy, 0);
      gl.uniform1f(u['radius']!, correctRadius(config.SPLAT_RADIUS / 100));
      blit(velocity.write);
      velocity.swap();
      gl.uniform1i(u['uTarget']!, dye.read.attach(0));
      gl.uniform3f(u['color']!, color.r, color.g, color.b);
      blit(dye.write);
      dye.swap();
    };

    const updatePointerDown = (p: PointerLike, id: number, posX: number, posY: number) => {
      p.id = id;
      p.down = true;
      p.moved = false;
      p.texcoordX = posX / canvas.width;
      p.texcoordY = 1 - posY / canvas.height;
      p.prevTexcoordX = p.texcoordX;
      p.prevTexcoordY = p.texcoordY;
      p.deltaX = 0;
      p.deltaY = 0;
      p.color = generateColor();
    };

    const updatePointerMove = (p: PointerLike, posX: number, posY: number, color?: { r: number; g: number; b: number }) => {
      p.prevTexcoordX = p.texcoordX;
      p.prevTexcoordY = p.texcoordY;
      p.texcoordX = posX / canvas.width;
      p.texcoordY = 1 - posY / canvas.height;
      let dx = p.texcoordX - p.prevTexcoordX;
      let dy = p.texcoordY - p.prevTexcoordY;
      const ar = canvas.width / canvas.height;
      if (ar < 1) dx *= ar;
      else dy /= ar;
      p.deltaX = dx;
      p.deltaY = dy;
      p.moved = Math.abs(dx) > 0 || Math.abs(dy) > 0;
      if (color) p.color = color;
    };

    const correctDeltaX = (d: number) => {
      const ar = canvas.width / canvas.height;
      return ar < 1 ? d * ar : d;
    };
    const correctDeltaY = (d: number) => {
      const ar = canvas.width / canvas.height;
      return ar > 1 ? d / ar : d;
    };

    const splatPointer = (p: PointerLike) => {
      const dx = p.deltaX * config.SPLAT_FORCE;
      const dy = p.deltaY * config.SPLAT_FORCE;
      splat(p.texcoordX, p.texcoordY, dx, dy, p.color);
    };

    const clickSplat = (p: PointerLike) => {
      const color = generateColor();
      splat(
        p.texcoordX,
        p.texcoordY,
        10 * (Math.random() - 0.5),
        30 * (Math.random() - 0.5),
        { r: color.r * 10, g: color.g * 10, b: color.b * 10 }
      );
    };

    let lastUpdateTime = Date.now();
    let colorUpdateTimer = 0;

    const calcDeltaTime = () => {
      const now = Date.now();
      let dt = (now - lastUpdateTime) / 1000;
      dt = Math.min(dt, 0.016666);
      lastUpdateTime = now;
      return dt;
    };

    const resizeCanvas = (): boolean => {
      const width = scaleByPixelRatio(canvas.clientWidth);
      const height = scaleByPixelRatio(canvas.clientHeight);
      if (canvas.width !== width || canvas.height !== height) {
        canvas.width = width;
        canvas.height = height;
        return true;
      }
      return false;
    };

    const step = (dt: number) => {
      gl.disable(gl.BLEND);
      gl.useProgram(curlProgram.program);
      gl.uniform2f(curlProgram.uniforms['texelSize']!, velocity.texelSizeX, velocity.texelSizeY);
      gl.uniform1i(curlProgram.uniforms['uVelocity']!, velocity.read.attach(0));
      blit(curl);

      gl.useProgram(vorticityProgram.program);
      gl.uniform2f(vorticityProgram.uniforms['texelSize']!, velocity.texelSizeX, velocity.texelSizeY);
      gl.uniform1i(vorticityProgram.uniforms['uVelocity']!, velocity.read.attach(0));
      gl.uniform1i(vorticityProgram.uniforms['uCurl']!, curl.attach(1));
      gl.uniform1f(vorticityProgram.uniforms['curl']!, config.CURL);
      gl.uniform1f(vorticityProgram.uniforms['dt']!, dt);
      blit(velocity.write);
      velocity.swap();

      gl.useProgram(divergenceProgram.program);
      gl.uniform2f(divergenceProgram.uniforms['texelSize']!, velocity.texelSizeX, velocity.texelSizeY);
      gl.uniform1i(divergenceProgram.uniforms['uVelocity']!, velocity.read.attach(0));
      blit(divergence);

      gl.useProgram(clearProgram.program);
      gl.uniform1i(clearProgram.uniforms['uTexture']!, pressure.read.attach(0));
      gl.uniform1f(clearProgram.uniforms['value']!, config.PRESSURE);
      blit(pressure.write);
      pressure.swap();

      gl.useProgram(pressureProgram.program);
      gl.uniform2f(pressureProgram.uniforms['texelSize']!, velocity.texelSizeX, velocity.texelSizeY);
      gl.uniform1i(pressureProgram.uniforms['uDivergence']!, divergence.attach(0));
      for (let i = 0; i < config.PRESSURE_ITERATIONS; i++) {
        gl.uniform1i(pressureProgram.uniforms['uPressure']!, pressure.read.attach(1));
        blit(pressure.write);
        pressure.swap();
      }

      gl.useProgram(gradientSubtractProgram.program);
      gl.uniform2f(gradientSubtractProgram.uniforms['texelSize']!, velocity.texelSizeX, velocity.texelSizeY);
      gl.uniform1i(gradientSubtractProgram.uniforms['uPressure']!, pressure.read.attach(0));
      gl.uniform1i(gradientSubtractProgram.uniforms['uVelocity']!, velocity.read.attach(1));
      blit(velocity.write);
      velocity.swap();

      gl.useProgram(advectionProgram.program);
      gl.uniform2f(advectionProgram.uniforms['texelSize']!, velocity.texelSizeX, velocity.texelSizeY);
      if (!ext.supportLinearFiltering)
        gl.uniform2f(advectionProgram.uniforms['dyeTexelSize']!, velocity.texelSizeX, velocity.texelSizeY);
      const velId = velocity.read.attach(0);
      gl.uniform1i(advectionProgram.uniforms['uVelocity']!, velId);
      gl.uniform1i(advectionProgram.uniforms['uSource']!, velId);
      gl.uniform1f(advectionProgram.uniforms['dt']!, dt);
      gl.uniform1f(advectionProgram.uniforms['dissipation']!, config.VELOCITY_DISSIPATION);
      blit(velocity.write);
      velocity.swap();
      if (!ext.supportLinearFiltering)
        gl.uniform2f(advectionProgram.uniforms['dyeTexelSize']!, dye.texelSizeX, dye.texelSizeY);
      gl.uniform1i(advectionProgram.uniforms['uVelocity']!, velocity.read.attach(0));
      gl.uniform1i(advectionProgram.uniforms['uSource']!, dye.read.attach(1));
      gl.uniform1f(advectionProgram.uniforms['dissipation']!, config.DENSITY_DISSIPATION);
      blit(dye.write);
      dye.swap();
    };

    const drawDisplay = (target: FBO | null) => {
      const width = target == null ? gl.drawingBufferWidth : target.width;
      const height = target == null ? gl.drawingBufferHeight : target.height;
      gl.useProgram(displayProgram.program);
      const du = displayProgram.uniforms;
      if (config.SHADING) gl.uniform2f(du['texelSize']!, 1 / width, 1 / height);
      gl.uniform1i(du['uTexture']!, dye.read.attach(0));
      blit(target);
    };

    const render = (target: FBO | null) => {
      gl.blendFunc(gl.ONE, gl.ONE_MINUS_SRC_ALPHA);
      gl.enable(gl.BLEND);
      drawDisplay(target);
    };

    let animationFrameId: number | null = null;

    const updateFrame = () => {
      if (!isActive) return;
      const dt = calcDeltaTime();
      if (resizeCanvas()) initFramebuffers();
      colorUpdateTimer += dt * config.COLOR_UPDATE_SPEED;
      if (colorUpdateTimer >= 1) {
        colorUpdateTimer = colorUpdateTimer % 1;
        pointers.forEach((p) => (p.color = generateColor()));
      }
      pointers.forEach((p) => {
        if (p.moved) {
          p.moved = false;
          splatPointer(p);
        }
      });
      step(dt);
      render(null);
      animationFrameId = requestAnimationFrame(updateFrame);
    };

    const handleMouseDown = (e: MouseEvent) => {
      const p = pointers[0];
      updatePointerDown(
        p,
        -1,
        scaleByPixelRatio(e.clientX),
        scaleByPixelRatio(e.clientY)
      );
      clickSplat(p);
    };

    let firstMove = false;
    const handleMouseMove = (e: MouseEvent) => {
      const p = pointers[0];
      const posX = scaleByPixelRatio(e.clientX);
      const posY = scaleByPixelRatio(e.clientY);
      if (!firstMove) {
        updatePointerMove(p, posX, posY, generateColor());
        firstMove = true;
      } else {
        updatePointerMove(p, posX, posY);
      }
    };

    const handleTouchStart = (e: TouchEvent) => {
      const touches = e.targetTouches;
      const p = pointers[0];
      if (touches.length) {
        updatePointerDown(
          p,
          touches[0].identifier,
          scaleByPixelRatio(touches[0].clientX),
          scaleByPixelRatio(touches[0].clientY)
        );
      }
    };

    const handleTouchMove = (e: TouchEvent) => {
      const p = pointers[0];
      if (e.targetTouches.length) {
        updatePointerMove(
          p,
          scaleByPixelRatio(e.targetTouches[0].clientX),
          scaleByPixelRatio(e.targetTouches[0].clientY)
        );
      }
    };

    const handleTouchEnd = () => {
      pointers[0].down = false;
    };

    window.addEventListener('mousedown', handleMouseDown);
    window.addEventListener('mousemove', handleMouseMove);
    window.addEventListener('touchstart', handleTouchStart, { passive: true });
    window.addEventListener('touchmove', handleTouchMove, { passive: true });
    window.addEventListener('touchend', handleTouchEnd);

    initFramebuffers();
    updateFrame();

    return () => {
      isActive = false;
      if (animationFrameId != null) cancelAnimationFrame(animationFrameId);
      window.removeEventListener('mousedown', handleMouseDown);
      window.removeEventListener('mousemove', handleMouseMove);
      window.removeEventListener('touchstart', handleTouchStart);
      window.removeEventListener('touchmove', handleTouchMove);
      window.removeEventListener('touchend', handleTouchEnd);
    };
  }

  private getWebGLContext(
    canvas: HTMLCanvasElement
  ): {
    gl: WebGLRenderingContext | WebGL2RenderingContext | null;
    ext: {
      formatRGBA: { internalFormat: number; format: number } | null;
      formatRG: { internalFormat: number; format: number } | null;
      formatR: { internalFormat: number; format: number } | null;
      halfFloatTexType: number;
      supportLinearFiltering: boolean;
    } | null;
  } {
    const params = {
      alpha: true,
      depth: false,
      stencil: false,
      antialias: false,
      preserveDrawingBuffer: false,
    };
    let gl: WebGLRenderingContext | WebGL2RenderingContext | null =
      (canvas.getContext('webgl2', params) as WebGL2RenderingContext | null) ||
      (canvas.getContext('webgl', params) as WebGLRenderingContext | null) ||
      (canvas.getContext('experimental-webgl', params) as WebGLRenderingContext | null);

    if (!gl) return { gl: null, ext: null };

    const isWebGL2 = gl.getParameter(0x1f02)?.toString().startsWith('WebGL 2');
    let halfFloat: { HALF_FLOAT_OES?: number } | null = null;
    let supportLinearFiltering = false;

    if (isWebGL2) {
      gl.getExtension('EXT_color_buffer_float');
      supportLinearFiltering =
        gl.getExtension('OES_texture_float_linear') != null;
    } else {
      halfFloat = gl.getExtension('OES_texture_half_float');
      supportLinearFiltering =
        gl.getExtension('OES_texture_half_float_linear') != null;
    }

    gl.clearColor(0, 0, 0, 1);
    const halfFloatTexType = isWebGL2
      ? (gl as WebGL2RenderingContext).HALF_FLOAT
      : (halfFloat?.HALF_FLOAT_OES ?? 0x8d61);

    const gl2 = gl as WebGL2RenderingContext;
    const formatRGBA = isWebGL2
      ? this.getSupportedFormat(
          gl,
          gl2.RGBA16F,
          gl2.RGBA,
          halfFloatTexType
        )
      : this.getSupportedFormat(gl, gl2.RGBA, gl2.RGBA, halfFloatTexType);
    const formatRG = isWebGL2
      ? this.getSupportedFormat(gl, gl2.RG16F, gl2.RG, halfFloatTexType)
      : this.getSupportedFormat(gl, gl2.RGBA, gl2.RGBA, halfFloatTexType);
    const formatR = isWebGL2
      ? this.getSupportedFormat(gl, gl2.R16F, gl2.RED, halfFloatTexType)
      : this.getSupportedFormat(gl, gl2.RGBA, gl2.RGBA, halfFloatTexType);

    return {
      gl,
      ext: {
        formatRGBA,
        formatRG,
        formatR,
        halfFloatTexType,
        supportLinearFiltering,
      },
    };
  }

  private getSupportedFormat(
    gl: WebGLRenderingContext | WebGL2RenderingContext,
    internalFormat: number,
    format: number,
    type: number
  ): { internalFormat: number; format: number } | null {
    const texture = gl.createTexture();
    gl.bindTexture(gl.TEXTURE_2D, texture);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.NEAREST);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.NEAREST);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, gl.CLAMP_TO_EDGE);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, gl.CLAMP_TO_EDGE);
    gl.texImage2D(
      gl.TEXTURE_2D,
      0,
      internalFormat,
      4,
      4,
      0,
      format,
      type,
      null
    );
    const fbo = gl.createFramebuffer();
    gl.bindFramebuffer(gl.FRAMEBUFFER, fbo);
    gl.framebufferTexture2D(
      gl.FRAMEBUFFER,
      gl.COLOR_ATTACHMENT0,
      gl.TEXTURE_2D,
      texture,
      0
    );
    const ok = gl.checkFramebufferStatus(gl.FRAMEBUFFER) === gl.FRAMEBUFFER_COMPLETE;
    if (!ok) {
      const gl2 = gl as WebGL2RenderingContext;
      switch (internalFormat) {
        case gl2.R16F:
          return this.getSupportedFormat(gl, gl2.RG16F, gl2.RG, type);
        case gl2.RG16F:
          return this.getSupportedFormat(gl, gl2.RGBA16F, gl2.RGBA, type);
        default:
          return null;
      }
    }
    return { internalFormat, format };
  }
}
