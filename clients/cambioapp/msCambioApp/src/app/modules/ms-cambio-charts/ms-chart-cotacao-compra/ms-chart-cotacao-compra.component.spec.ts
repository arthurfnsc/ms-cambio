import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MsChartCotacaoCompraComponent } from './ms-chart-cotacao-compra.component';

describe('MsChartCotacaoCompraComponent', () => {
  let component: MsChartCotacaoCompraComponent;
  let fixture: ComponentFixture<MsChartCotacaoCompraComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MsChartCotacaoCompraComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MsChartCotacaoCompraComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
