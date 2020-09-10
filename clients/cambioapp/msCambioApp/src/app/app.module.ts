import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

// Material Design Imports
import { LayoutModule } from '@angular/cdk/layout';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

// Modules imports
import { AppRoutingModule } from './app-routing.module';
import { MsLayoutComponent } from './ms-layout/ms-layout.component';
import { MsDashBoardModule } from './modules/ms-dash-board/ms-dash-board.module';

// Components imports
import { AppComponent } from './app.component';

@NgModule({
  declarations: [
    AppComponent,
    MsLayoutComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatMenuModule,
    MatIconModule,
    MatButtonModule,
    LayoutModule,
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    MsDashBoardModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
