import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MsChartCotacaoCompraComponent } from './ms-chart-cotacao-compra/ms-chart-cotacao-compra.component';
import { MsChartCotacaoVendaComponent } from './ms-chart-cotacao-venda/ms-chart-cotacao-venda.component';



@NgModule({
  declarations: [MsChartCotacaoCompraComponent, MsChartCotacaoVendaComponent],
  imports: [
    CommonModule
  ],
  exports: [
    MsChartCotacaoCompraComponent,
    MsChartCotacaoVendaComponent
  ]
})

export class MsCambioChartsModule { }
