import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AccountDetailsComponent } from './account-details.component';
import { AccountSettingsService, HospitalInfo, StaffInfo, PasswordUpdateResponse } from '../../services/account-settings.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatDialog } from '@angular/material/dialog';
import { of, throwError } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('AccountDetailsComponent', () => {
  let component: AccountDetailsComponent;
  let fixture: ComponentFixture<AccountDetailsComponent>;
  let mockAccountService: jasmine.SpyObj<AccountSettingsService>;
  let mockDialog: jasmine.SpyObj<MatDialog>;

  beforeEach(async () => {
    mockAccountService = jasmine.createSpyObj('AccountSettingsService', [
      'getHospitalInfo', 'getStaffInfo', 'changePassword'
    ]);

    mockDialog = jasmine.createSpyObj('MatDialog', ['open', 'closeAll']);
    mockDialog.open.and.returnValue({ afterClosed: () => of(true) } as any);
    mockDialog.closeAll.and.stub();

    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, AccountDetailsComponent],
      providers: [
        { provide: AccountSettingsService, useValue: mockAccountService },
        { provide: MatDialog, useValue: mockDialog }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(AccountDetailsComponent);
    component = fixture.componentInstance;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch hospital info on init', () => {
    const dummyHospital: HospitalInfo = {
      hospitalName: 'Test Hospital',
      addressLine1: '', addressLine2: '', city: '', state: '', postal: '', telephone: '',
      type: '', traumaLevel: '', hasHelipad: false
    };
    mockAccountService.getHospitalInfo.and.returnValue(of(dummyHospital));
    mockAccountService.getStaffInfo.and.returnValue(of({} as StaffInfo));

    component.ngOnInit();

    expect(mockAccountService.getHospitalInfo).toHaveBeenCalled();
    expect(component.hospitalInfo).toEqual(dummyHospital);
  });

  it('should fetch staff info on init', () => {
    const dummyStaff: StaffInfo = {
      username: 'doc',
      role: 'Doctor',
      firstName: 'John', lastName: 'Doe', email: '', dateOfBirth: '',
      phone: '', addressLine1: '', addressLine2: '', city: '', state: '', postal: '', country: '',
      specialty: '', yearsExperience: 10
    };
    mockAccountService.getStaffInfo.and.returnValue(of(dummyStaff));
    mockAccountService.getHospitalInfo.and.returnValue(of({} as HospitalInfo));

    component.ngOnInit();

    expect(mockAccountService.getStaffInfo).toHaveBeenCalled();
    expect(component.staffInfo).toEqual(dummyStaff);
  });

  it('should not proceed if current password is empty', () => {
    component.currentPassword = ' ';
    component.newPassword = 'abc123';
    component.confirmPassword = 'abc123';

    component.changePassword();

    expect(component.passwordChangeMessage).toBe('Enter current password.');
    expect(mockAccountService.changePassword).not.toHaveBeenCalled();
  });

  it('should set error if new passwords don’t match', () => {
    component.currentPassword = 'oldpass';
    component.newPassword = 'new123';
    component.confirmPassword = 'wrong123';

    component.changePassword();

    expect(component.passwordChangeMessage).toBe("New passwords don't match.");
  });

  it('should show error message if changePassword API fails', () => {
    component.currentPassword = 'oldpass';
    component.newPassword = 'newpass';
    component.confirmPassword = 'newpass';

    mockAccountService.changePassword.and.returnValue(
      throwError(() => ({ error: { message: 'Failed' } }))
    );

    component.changePassword();

    expect(component.passwordChangeMessage).toBe('Failed');
  });

  it('should toggle password visibility', () => {
    component.passwordVisibleNew = false;
    component.togglePasswordNew();
    expect(component.passwordVisibleNew).toBeTrue();

    component.passwordVisibleConfirm = false;
    component.togglePasswordConfirm();
    expect(component.passwordVisibleConfirm).toBeTrue();
  });
});
