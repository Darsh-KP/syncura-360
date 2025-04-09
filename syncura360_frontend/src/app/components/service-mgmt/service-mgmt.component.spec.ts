import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceMgmtComponent } from './service-mgmt.component';

describe('ServiceMgmtComponent', () => {
  let component: ServiceMgmtComponent;
  let fixture: ComponentFixture<ServiceMgmtComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ServiceMgmtComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ServiceMgmtComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
