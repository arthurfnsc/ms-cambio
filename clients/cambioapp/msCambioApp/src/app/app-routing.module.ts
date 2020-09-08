import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { MsLayoutComponent } from './ms-layout/ms-layout.component';
import { MsDashBoardComponent } from './ms-dash-board/ms-dash-board.component';


const routes: Routes = [
  {
      path: '', 
      component: MsLayoutComponent, 
      children: [
         {path: 'DashBoard', component: MsDashBoardComponent}
      ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
