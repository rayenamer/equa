import { Injectable, ElementRef } from '@angular/core';
import * as THREE from 'three';
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js';
import { EffectComposer } from 'three/examples/jsm/postprocessing/EffectComposer.js';
import { RenderPass } from 'three/examples/jsm/postprocessing/RenderPass.js';
import { UnrealBloomPass } from 'three/examples/jsm/postprocessing/UnrealBloomPass.js';
import gsap from 'gsap';

@Injectable({
  providedIn: 'root'
})
export class ThreeService {
  private scene!: THREE.Scene;
  private camera!: THREE.PerspectiveCamera;
  private renderer!: THREE.WebGLRenderer;
  private composer!: EffectComposer;
  private mainObject!: THREE.Object3D;
  private raycaster = new THREE.Raycaster();
  private mouse = new THREE.Vector2();
  private animationId?: number;
  private tokenTexture?: THREE.Texture;
  private canvasEl: HTMLCanvasElement | null = null;

  constructor() {}

  private getCanvasSize(): { width: number; height: number } {
    if (this.canvasEl) {
      const w = this.canvasEl.clientWidth || 1;
      const h = this.canvasEl.clientHeight || 1;
      return { width: w, height: h };
    }
    return { width: window.innerWidth, height: window.innerHeight };
  }

  init(canvas: ElementRef<HTMLCanvasElement>, tokenImagePath: string): void {
    this.canvasEl = canvas.nativeElement;
    const size = this.getCanvasSize();

    // Scene — fond transparent pour laisser voir le letter glitch derrière
    this.scene = new THREE.Scene();
    this.scene.background = null;
    this.scene.fog = new THREE.Fog(0x0a0a12, 8, 45);

    // Camera - FOV réduit pour un cercle symétrique sans déformation
    this.camera = new THREE.PerspectiveCamera(
      40,
      size.width / size.height,
      0.1,
      1000
    );
    this.camera.position.set(0, 0, 10);
    this.camera.lookAt(0, 0, 0);

    // Renderer — alpha: true pour fond transparent (letter glitch visible derrière)
    this.renderer = new THREE.WebGLRenderer({
      canvas: this.canvasEl,
      antialias: true,
      alpha: true
    });
    this.renderer.setClearColor(0x000000, 0);
    this.renderer.setSize(size.width, size.height);
    this.renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
    this.renderer.toneMapping = THREE.ACESFilmicToneMapping;
    this.renderer.toneMappingExposure = 1.3;

    // Post-processing (Bloom)
    this.composer = new EffectComposer(this.renderer);
    const renderPass = new RenderPass(this.scene, this.camera);
    this.composer.addPass(renderPass);

    const bloomPass = new UnrealBloomPass(
      new THREE.Vector2(size.width, size.height),
      0.6,
      0.4,
      0.92
    );
    this.composer.addPass(bloomPass);

    // Lights
    this.setupLights();

    // Load token as main object
    this.loadTokenModel(tokenImagePath);

    // Add floating particles
    this.addParticles();

    // Handle resize
    this.boundOnResize = () => this.onResize();
    window.addEventListener('resize', this.boundOnResize);
    requestAnimationFrame(() => this.onResize());
  }

  private boundOnResize = (): void => {};

  private setupLights(): void {
    // Ambient light - Base douce
    const ambientLight = new THREE.AmbientLight(0xffffff, 0.7);
    this.scene.add(ambientLight);

    // Lumière principale (face avant)
    const mainLight = new THREE.DirectionalLight(0xffffff, 1.2);
    mainLight.position.set(0, 5, 10);
    mainLight.castShadow = false;
    this.scene.add(mainLight);

    // Lumière dorée pour l'effet métallique
    const goldLight = new THREE.DirectionalLight(0xffd700, 0.5);
    goldLight.position.set(-5, 3, 5);
    this.scene.add(goldLight);

    // Lumière de remplissage (fill light)
    const fillLight = new THREE.DirectionalLight(0xffffff, 0.4);
    fillLight.position.set(5, -3, 3);
    this.scene.add(fillLight);

    // Rim light pour le contour
    const rimLight = new THREE.DirectionalLight(0xffd700, 0.6);
    rimLight.position.set(0, 0, -5);
    this.scene.add(rimLight);
  }

  private loadTokenModel(imagePath: string): void {
        // Prendre le centre de l’image (token) et l’étendre sur tout le disque

    const radius = 1.65;
    const thickness = 0.22;
    const halfThickness = thickness / 2;

    const textureLoader = new THREE.TextureLoader();
    textureLoader.load(
      imagePath,
      (texture) => {
        texture.wrapS = texture.wrapT = THREE.ClampToEdgeWrapping;
        texture.repeat.set(1, 1);
        texture.offset.set(0, 0);
        texture.center.set(0.5, 0.5);
        texture.flipY = false;
        texture.needsUpdate = true;

        this.mainObject = new THREE.Group();

        const edgeGeometry = new THREE.CylinderGeometry(radius, radius, thickness, 128);
    const edgeMaterial = new THREE.MeshStandardMaterial({
      color: 0xffd700,
      metalness: 0.7,
      roughness: 0.25,
      emissive: new THREE.Color(0xffd700),
      emissiveIntensity: 0.08
    });
    const edge = new THREE.Mesh(edgeGeometry, edgeMaterial);
    edge.rotation.x = Math.PI / 2;
    this.mainObject.add(edge);

    const faceGeometry = new THREE.CircleGeometry(radius, 128);
    const faceMaterial = new THREE.MeshStandardMaterial({
      map: texture,
      metalness: 0.6,
      roughness: 0.25,
      emissive: new THREE.Color(0xffd700),
      emissiveIntensity: 0.15,
      transparent: true
    });

    const frontFace = new THREE.Mesh(faceGeometry, faceMaterial);
    frontFace.position.z = halfThickness + 0.002;
    this.mainObject.add(frontFace);

    const backFaceGeometry = new THREE.CircleGeometry(radius, 128);
    const backFace = new THREE.Mesh(backFaceGeometry, faceMaterial.clone());
    backFace.position.z = -halfThickness - 0.002;
    backFace.rotation.y = Math.PI;
    this.mainObject.add(backFace);

    this.mainObject.rotation.x = -0.2;

        this.scene.add(this.mainObject);
      },
      undefined,
      () => this.createFallbackToken()
    );
  }

  private createFallbackToken(): void {
    // Fallback: golden coin
    const geometry = new THREE.CylinderGeometry(2, 2, 0.2, 64);
    const material = new THREE.MeshStandardMaterial({
      color: 0xffd700,
      metalness: 0.9,
      roughness: 0.1,
      emissive: new THREE.Color(0xffd700),
      emissiveIntensity: 0.3
    });

    this.mainObject = new THREE.Mesh(geometry, material);
    this.mainObject.rotation.x = Math.PI / 2;
    this.scene.add(this.mainObject);

    // Add "E" text on coin
    this.addCoinText();
    this.addGlowRing();
  }

  private addCoinText(): void {
    // Add a simple plane with "EQUA" text (would need TextGeometry for 3D text)
    const canvas = document.createElement('canvas');
    canvas.width = 512;
    canvas.height = 512;
    const ctx = canvas.getContext('2d')!;
    
    ctx.fillStyle = '#0a0a0a';
    ctx.fillRect(0, 0, 512, 512);
    ctx.fillStyle = '#ffd700';
    ctx.font = 'bold 120px Syne';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.fillText('EQUA', 256, 256);
    
    const texture = new THREE.CanvasTexture(canvas);
    const geometry = new THREE.CircleGeometry(1.8, 64);
    const material = new THREE.MeshBasicMaterial({ map: texture });
    const textMesh = new THREE.Mesh(geometry, material);
    textMesh.position.z = 0.11;
    this.mainObject.add(textMesh);
  }

  private addGlowRing(): void {
    const ringGeometry = new THREE.TorusGeometry(2.4, 0.08, 16, 100);
    const ringMaterial = new THREE.MeshBasicMaterial({
      color: 0xffd700,
      transparent: true,
      opacity: 0.5
    });
    const ring = new THREE.Mesh(ringGeometry, ringMaterial);
    ring.rotation.x = Math.PI / 2; // Placer l'anneau dans le même plan que le token
    this.scene.add(ring);

    // Animate ring
    gsap.to(ring.rotation, {
      z: Math.PI * 2,
      duration: 4,
      repeat: -1,
      ease: 'none'
    });
  }

  private addParticles(): void {
    const particlesGeometry = new THREE.BufferGeometry();
    const particlesCount = 150; // Encore moins pour ne pas distraire
    const posArray = new Float32Array(particlesCount * 3);

    for (let i = 0; i < particlesCount * 3; i++) {
      posArray[i] = (Math.random() - 0.5) * 50;
    }

    particlesGeometry.setAttribute('position', new THREE.BufferAttribute(posArray, 3));

    const particlesMaterial = new THREE.PointsMaterial({
      size: 0.02, // Encore plus petites
      color: 0xffd700,
      transparent: true,
      opacity: 0.2, // Très discrètes
      blending: THREE.AdditiveBlending
    });

    const particlesMesh = new THREE.Points(particlesGeometry, particlesMaterial);
    this.scene.add(particlesMesh);

    // Animate particles
    gsap.to(particlesMesh.rotation, {
      y: Math.PI * 2,
      duration: 30, // Plus lent
      repeat: -1,
      ease: 'none'
    });
  }

  animate(): void {
    this.animationId = requestAnimationFrame(() => this.animate());

    // Rotation douce autour de l'axe Y (comme une vraie pièce qui tourne)
    if (this.mainObject) {
      this.mainObject.rotation.y += 0.01;
    }

    // Rendu direct pour garder le fond transparent (letter glitch visible).
    // Le composer (Bloom) remplirait le fond en opaque.
    this.renderer.render(this.scene, this.camera);
  }

  animateScroll(progress: number): void {
    if (!this.mainObject) return;

    // Rotation based on scroll
    gsap.to(this.mainObject.rotation, {
      y: progress * Math.PI * 4,
      duration: 0.5,
      ease: 'power2.out'
    });

    // Zoom based on scroll
    const targetZ = 5 + (1 - progress) * 10;
    gsap.to(this.camera.position, {
      z: targetZ,
      duration: 0.5,
      ease: 'power2.out'
    });
  }

  focusOnObject(position: THREE.Vector3): void {
    gsap.to(this.camera.position, {
      x: position.x + 3,
      y: position.y + 2,
      z: position.z + 3,
      duration: 1.5,
      ease: 'power3.inOut'
    });

    gsap.to(this.camera.rotation, {
      duration: 1.5,
      ease: 'power3.inOut',
      onUpdate: () => {
        this.camera.lookAt(position);
      }
    });
  }

  resetCamera(): void {
    gsap.to(this.camera.position, {
      x: 0,
      y: 0,
      z: 5,
      duration: 1.5,
      ease: 'power3.inOut'
    });

    gsap.to(this.camera.rotation, {
      x: 0,
      y: 0,
      z: 0,
      duration: 1.5,
      ease: 'power3.inOut'
    });
  }

  onMouseMove(event: MouseEvent): void {
    const size = this.getCanvasSize();
    const rect = this.canvasEl?.getBoundingClientRect();
    if (rect && size.width && size.height) {
      this.mouse.x = ((event.clientX - rect.left) / size.width) * 2 - 1;
      this.mouse.y = -((event.clientY - rect.top) / size.height) * 2 + 1;
    } else {
      this.mouse.x = (event.clientX / window.innerWidth) * 2 - 1;
      this.mouse.y = -(event.clientY / window.innerHeight) * 2 + 1;
    }
  }

  onClick(callback: (object: THREE.Object3D) => void): void {
    this.raycaster.setFromCamera(this.mouse, this.camera);
    const intersects = this.raycaster.intersectObjects(this.scene.children, true);

    if (intersects.length > 0) {
      const object = intersects[0].object;
      if (object === this.mainObject) {
        callback(object);
      }
    }
  }

  private onResize(): void {
    const size = this.getCanvasSize();
    this.camera.aspect = size.width / size.height;
    this.camera.updateProjectionMatrix();
    this.renderer.setSize(size.width, size.height);
    this.composer.setSize(size.width, size.height);
    const bloomPass = this.composer.passes[1] as UnrealBloomPass;
    if (bloomPass && bloomPass.resolution) {
      bloomPass.resolution.set(size.width, size.height);
    }
  }

  dispose(): void {
    window.removeEventListener('resize', this.boundOnResize);
    this.canvasEl = null;
    if (this.animationId) {
      cancelAnimationFrame(this.animationId);
    }
    this.scene.traverse((object) => {
      if (object instanceof THREE.Mesh) {
        object.geometry.dispose();
        if (object.material instanceof THREE.Material) {
          object.material.dispose();
        }
      }
    });
    this.renderer.dispose();
    this.composer.dispose();
  }

  getMainObject(): THREE.Object3D {
    return this.mainObject;
  }
}
