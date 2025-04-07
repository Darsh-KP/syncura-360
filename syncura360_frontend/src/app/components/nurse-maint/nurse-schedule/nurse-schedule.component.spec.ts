import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NurseScheduleComponent } from './nurse-schedule.component';

describe('NurseScheduleComponent', () => {
  let component: NurseScheduleComponent;
  let fixture: ComponentFixture<NurseScheduleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NurseScheduleComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NurseScheduleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
