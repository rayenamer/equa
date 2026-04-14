import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChartLinesStyleComponent } from './chart-lines-style.component';

describe('ChartLinesStyleComponent', () => {
  let component: ChartLinesStyleComponent;
  let fixture: ComponentFixture<ChartLinesStyleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChartLinesStyleComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChartLinesStyleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
