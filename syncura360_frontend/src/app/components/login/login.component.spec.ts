import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { LoginService } from '../../services/login.service';
import { of, throwError } from 'rxjs';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let mockLoginService: jasmine.SpyObj<LoginService>;
  let router: Router;

  beforeEach(async () => {
    mockLoginService = jasmine.createSpyObj('LoginService', ['login', 'isAuthenticated']);

    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        HttpClientTestingModule,
        RouterTestingModule,
        NoopAnimationsModule, // 👈 fixes @transitionMessages errors
        LoginComponent
      ],
      providers: [
        { provide: LoginService, useValue: mockLoginService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should show error if form is invalid on submit', () => {
    component.loginForm.setValue({ username: '', password: '' });
    component.onSubmit();
    expect(component.errorMessage).toBe('Please enter both username and password');
  });

  it('should login and navigate based on role', fakeAsync(() => {
    const navigateSpy = spyOn(router, 'navigate');
    component.loginForm.setValue({ username: 'user', password: 'pass' });

    const responseMock: any = {
      body: { message: 'Authentication successful.', role: 'Admin' },
      headers: { get: () => 'Bearer token' }
    };
    mockLoginService.login.and.returnValue(of(responseMock));

    component.onSubmit();
    tick();

    expect(component.successMessage).toContain('Login Successful');
    expect(navigateSpy).toHaveBeenCalledWith(['/dashboard']);
  }));

  it('should show error on login failure', fakeAsync(() => {
    component.loginForm.setValue({ username: 'fail', password: 'fail' });
    mockLoginService.login.and.returnValue(throwError(() => new Error('Login failed. Please try again.')));

    component.onSubmit();
    tick();

    expect(component.errorMessage).toBe('Login failed. Please try again.');
    expect(component.loading).toBeFalse();
  }));

  it('should toggle password visibility', () => {
    expect(component.passwordVisible).toBeFalse();
    component.togglePassword();
    expect(component.passwordVisible).toBeTrue();
  });

  it('should redirect if already authenticated on init', () => {
    const navigateSpy = spyOn(router, 'navigate');
    mockLoginService.isAuthenticated.and.returnValue(true);
    localStorage.setItem('role', 'Doctor');
    component.ngOnInit();
    expect(navigateSpy).toHaveBeenCalledWith(['/doctor']);
  });

  it('should navigate to register page', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.navigateToRegister();
    expect(navigateSpy).toHaveBeenCalledWith(['/register']);
  });
});
