import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { MsLayoutComponent } from './ms-layout/ms-layout.component';
import { MsDashBoardPageComponent } from './modules/ms-dash-board/ms-dash-board-page/ms-dash-board-page.component';


const routes: Routes = [
  {
      path: '', 
      component: MsLayoutComponent, 
      children: [
         {path: 'DashBoard', component: MsDashBoardPageComponent}
      ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
