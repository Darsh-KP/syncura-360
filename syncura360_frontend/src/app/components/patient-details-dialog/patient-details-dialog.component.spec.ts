import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PatientDetailsDialogComponent } from './patient-details-dialog.component';

describe('PatientDetailsDialogComponent', () => {
  let component: PatientDetailsDialogComponent;
  let fixture: ComponentFixture<PatientDetailsDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PatientDetailsDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PatientDetailsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
