import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VisitRecordsComponent } from './visit-records.component';

describe('VisitRecordsComponent', () => {
  let component: VisitRecordsComponent;
  let fixture: ComponentFixture<VisitRecordsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VisitRecordsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VisitRecordsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
