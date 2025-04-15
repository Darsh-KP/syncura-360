import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VisitMgmtComponent } from './visit-mgmt.component';

describe('VisitMgmtComponent', () => {
  let component: VisitMgmtComponent;
  let fixture: ComponentFixture<VisitMgmtComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VisitMgmtComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VisitMgmtComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
