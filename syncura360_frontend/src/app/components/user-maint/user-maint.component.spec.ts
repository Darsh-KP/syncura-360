import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { UserMaintComponent } from './user-maint.component';
import { StaffService } from '../../services/staff.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('UserMaintComponent', () => {
  let component: UserMaintComponent;
  let fixture: ComponentFixture<UserMaintComponent>;
  let mockStaffService: jasmine.SpyObj<StaffService>;
  let mockDialogRef: jasmine.SpyObj<MatDialogRef<UserMaintComponent>>;

  const dummyStaff = {
    username: 'nurse1',
    role: 'Nurse',
    firstName: 'Alice',
    lastName: 'Johnson',
    email: 'alice.johnson@example.com',
    phone: '1234567890',
    addressLine1: '123 Main St',
    addressLine2: '',
    city: 'New York',
    state: 'NY',
    postal: '10001',
    country: 'USA',
    dateOfBirth: '1990-01-01'
  };

  describe('Create Mode', () => {
    beforeEach(async () => {
      mockStaffService = jasmine.createSpyObj('StaffService', ['createStaff', 'updateStaff']);
      mockDialogRef = jasmine.createSpyObj('MatDialogRef', ['close']);

      await TestBed.configureTestingModule({
        imports: [UserMaintComponent, ReactiveFormsModule, NoopAnimationsModule],
        providers: [
          { provide: StaffService, useValue: mockStaffService },
          { provide: MatDialogRef, useValue: mockDialogRef },
          { provide: MAT_DIALOG_DATA, useValue: null } // Create mode
        ]
      }).compileComponents();

      fixture = TestBed.createComponent(UserMaintComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should create component', () => {
      expect(component).toBeTruthy();
    });

    it('should toggle password visibility', () => {
      expect(component.passwordVisible).toBeFalse();
      component.togglePassword();
      expect(component.passwordVisible).toBeTrue();
    });

    it('should close dialog on closeDialog()', () => {
      component.closeDialog();
      expect(mockDialogRef.close).toHaveBeenCalledWith(false);
    });

    it('should show error if form is invalid on submit', () => {
      component.staffForm.patchValue({ firstName: '' });
      component.submitForm();
      expect(component.createStaffError).toBe('All fields are required.');
    });

    it('should call createStaff when creating new staff', fakeAsync(() => {
      component.staffForm.setValue({
        username: 'newuser',
        passwordHash: 'abcdef',
        role: 'Doctor',
        firstName: 'John',
        lastName: 'Doe',
        email: 'john@example.com',
        phone: '1234567890',
        addressLine1: '123 Elm St',
        addressLine2: '',
        city: 'Boston',
        state: 'MA',
        postal: '02134',
        country: 'USA',
        dateOfBirth: '1985-06-15'
      });

      mockStaffService.createStaff.and.returnValue(of({ message: 'Created', staffUsernames: [] }));
      component.submitForm();
      tick();

      expect(mockStaffService.createStaff).toHaveBeenCalled();
      expect(mockDialogRef.close).toHaveBeenCalledWith(true);
    }));

    it('should show error on createStaff failure', fakeAsync(() => {
      mockStaffService.createStaff.and.returnValue(throwError(() => new Error('Create failed')));
      component.staffForm.setValue({
        username: 'newuser',
        passwordHash: 'abcdef',
        role: 'Doctor',
        firstName: 'John',
        lastName: 'Doe',
        email: 'john@example.com',
        phone: '1234567890',
        addressLine1: '123 Elm St',
        addressLine2: '',
        city: 'Boston',
        state: 'MA',
        postal: '02134',
        country: 'USA',
        dateOfBirth: '1985-06-15'
      });

      component.submitForm();
      tick();
      expect(component.createStaffError).toBe('Create failed');
    }));
  });

  describe('Edit Mode', () => {
    beforeEach(async () => {
      mockStaffService = jasmine.createSpyObj('StaffService', ['createStaff', 'updateStaff']);
      mockDialogRef = jasmine.createSpyObj('MatDialogRef', ['close']);

      await TestBed.configureTestingModule({
        imports: [UserMaintComponent, ReactiveFormsModule, NoopAnimationsModule],
        providers: [
          { provide: StaffService, useValue: mockStaffService },
          { provide: MatDialogRef, useValue: mockDialogRef },
          { provide: MAT_DIALOG_DATA, useValue: dummyStaff } // Edit mode
        ]
      }).compileComponents();

      fixture = TestBed.createComponent(UserMaintComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should call updateStaff when editing staff', fakeAsync(() => {
      component.staffForm.patchValue({ firstName: 'Alice' });
      mockStaffService.updateStaff.and.returnValue(of({ message: 'Updated', staffUsernames: [] }));

      component.submitForm();
      tick();

      expect(mockStaffService.updateStaff).toHaveBeenCalled();
      expect(mockDialogRef.close).toHaveBeenCalledWith(true);
    }));

    it('should show error on updateStaff failure', fakeAsync(() => {
      mockStaffService.updateStaff.and.returnValue(throwError(() => new Error('Update failed')));
      component.staffForm.patchValue({ firstName: 'Alice' });

      component.submitForm();
      tick();

      expect(component.createStaffError).toBe('Update failed');
    }));
  });
});