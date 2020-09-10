import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MsDashBoardTableVendaComponent } from './ms-dash-board-table-venda.component';

describe('MsDashBoardTableVendaComponent', () => {
  let component: MsDashBoardTableVendaComponent;
  let fixture: ComponentFixture<MsDashBoardTableVendaComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MsDashBoardTableVendaComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MsDashBoardTableVendaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
