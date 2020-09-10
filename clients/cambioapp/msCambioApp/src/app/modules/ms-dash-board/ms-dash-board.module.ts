import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MatGridListModule } from '@angular/material/grid-list';
import { MatCardModule } from '@angular/material/card';

import { MsDashBoardPageComponent } from './ms-dash-board-page/ms-dash-board-page.component';
import { MsCambioChartsModule } from '../ms-cambio-charts/ms-cambio-charts.module';
import { MatTableModule } from '@angular/material/table';
import { MsDashBoardTableCompraComponent } from './ms-dash-board-table-compra/ms-dash-board-table-compra.component';
import { MsDashBoardTableVendaComponent } from './ms-dash-board-table-venda/ms-dash-board-table-venda.component';

@NgModule({
  declarations: [MsDashBoardPageComponent, MsDashBoardTableCompraComponent, MsDashBoardTableVendaComponent],
  imports: [
    CommonModule,
    MsCambioChartsModule,
    MatGridListModule,
    MatCardModule,
    MatTableModule
  ],
  exports: [
    MsDashBoardPageComponent
  ]
})

export class MsDashBoardModule { }
