import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MsDashBoardTableCompraComponent } from './ms-dash-board-table-compra.component';

describe('MsDashBoardTableCompraComponent', () => {
  let component: MsDashBoardTableCompraComponent;
  let fixture: ComponentFixture<MsDashBoardTableCompraComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MsDashBoardTableCompraComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MsDashBoardTableCompraComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
