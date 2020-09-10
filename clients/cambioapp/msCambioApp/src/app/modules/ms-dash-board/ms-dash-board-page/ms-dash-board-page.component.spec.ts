import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MsDashBoardPageComponent } from './ms-dash-board-page.component';

describe('MsDashBoardPageComponent', () => {
  let component: MsDashBoardPageComponent;
  let fixture: ComponentFixture<MsDashBoardPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MsDashBoardPageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MsDashBoardPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
