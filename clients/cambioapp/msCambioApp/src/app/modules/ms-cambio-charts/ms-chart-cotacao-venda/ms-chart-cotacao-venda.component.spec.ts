import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MsChartCotacaoVendaComponent } from './ms-chart-cotacao-venda.component';

describe('MsChartCotacaoVendaComponent', () => {
  let component: MsChartCotacaoVendaComponent;
  let fixture: ComponentFixture<MsChartCotacaoVendaComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MsChartCotacaoVendaComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MsChartCotacaoVendaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
