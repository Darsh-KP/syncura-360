import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NurseMaintComponent } from './nurse-maint.component';

describe('NurseMaintComponent', () => {
  let component: NurseMaintComponent;
  let fixture: ComponentFixture<NurseMaintComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NurseMaintComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NurseMaintComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
