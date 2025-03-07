import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserMaintComponent } from './user-maint.component';

describe('UserMaintComponent', () => {
  let component: UserMaintComponent;
  let fixture: ComponentFixture<UserMaintComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserMaintComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserMaintComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
