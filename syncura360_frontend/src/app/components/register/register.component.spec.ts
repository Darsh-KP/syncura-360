import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RegisterComponent } from './register.component';
import { RegisterService } from '../../services/register.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { ReactiveFormsModule } from '@angular/forms';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let mockRegisterService: jasmine.SpyObj<RegisterService>;
  let mockRouter: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    mockRegisterService = jasmine.createSpyObj('RegisterService', ['registerHospital']);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [RegisterComponent, ReactiveFormsModule, NoopAnimationsModule],
      providers: [
        { provide: RegisterService, useValue: mockRegisterService },
        { provide: Router, useValue: mockRouter }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the RegisterComponent', () => {
    expect(component).toBeTruthy();
  });

  it('should toggle password visibility', () => {
    expect(component.passwordVisible).toBe(false);
    component.togglePassword();
    expect(component.passwordVisible).toBe(true);
  });

  it('should show error if form is invalid on submit', () => {
    component.registerForm.patchValue({ username: '' });  // make form invalid
    component.onSubmit();
    expect(component.errorMessage).toContain('All fields must be correctly filled');
  });

  it('should call register service and navigate on successful registration', fakeAsync(() => {
    mockRegisterService.registerHospital.and.returnValue(of('Registration successful'));
    component.registerForm.markAllAsTouched();
    component.onSubmit();
    tick(3000);
    expect(mockRegisterService.registerHospital).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);
  }));

  it('should set errorMessage on registration failure', fakeAsync(() => {
    mockRegisterService.registerHospital.and.returnValue(throwError(() => new Error('fail')));
    component.onSubmit();
    tick();
    expect(component.errorMessage).toBe('fail');
  }));

  it('should navigate to login on cancel', () => {
    component.navigateToLogin();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);
  });
});
