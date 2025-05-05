import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-patient-details-dialog',
  standalone: true,
  imports: [CommonModule, FormsModule, MatDialogModule],
  template: `
    <div class="p-4 w-full max-w-md mx-auto">
      <h2 class="text-xl font-bold mb-4">Patient Info</h2>
      <ng-container *ngIf="patientData; else loadingTpl">
        <div class="space-y-2 text-sm text-gray-700">
          <p><strong>Name:</strong> {{ patientData.firstName }} {{ patientData.lastName }}</p>
          <p><strong>DOB:</strong> {{ patientData.dateOfBirth }}</p>
          <p><strong>Gender:</strong> {{ patientData.gender }}</p>
          <p><strong>Blood Type:</strong> {{ patientData.bloodType || 'N/A' }}</p>
          <p><strong>Height:</strong> {{ patientData.height || 'N/A' }} in</p>
          <p><strong>Weight:</strong> {{ patientData.weight || 'N/A' }} lbs</p>
          <p><strong>Phone:</strong> {{ patientData.phone }}</p>
          <p>
            <strong>Address:</strong>
            {{ patientData.addressLine1 }}, {{ patientData.addressLine2 || '' }},
            {{ patientData.city }}, {{ patientData.state }}, {{ patientData.postal }},
            {{ patientData.country }}
          </p>
          <p>
            <strong>Emergency Contact:</strong>
            {{ patientData.emergencyContactName || 'N/A' }} ({{ patientData.emergencyContactPhone || 'N/A' }})
          </p>
          <p><strong>Notes:</strong></p>
          <textarea [(ngModel)]="note" rows="5"
                    class="w-full p-2 border border-gray-300 rounded"></textarea>
        </div>
      </ng-container>
      <ng-template #loadingTpl>
        <p>Loading patient details...</p>
      </ng-template>
      <div class="text-right mt-4">
        <button class="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700"
                (click)="saveNote()">
          Save Note
        </button>
      </div>
    </div>
  `
})
export class PatientDetailsDialogComponent implements OnInit {
  note: string = '';
  patientData: any;

  constructor(@Inject(MAT_DIALOG_DATA) public data: any, private http: HttpClient) {
  }

  ngOnInit() {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    this.http.get<any>(`http://localhost:8080/patient/${this.data.patientId}`, { headers })
      .subscribe({
        next: (fullData) => {
          console.log('Full Patient data:', fullData);
          this.patientData = fullData;
        },
        error: (err) => console.error('Failed to fetch patient details', err)
      });
  }

  saveNote() {
    if (!this.patientData || !this.patientData.patientId) {
      alert('Patient data is incomplete. Cannot update notes.');
      return;
    }
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    const updatePayload = {
      patientId: this.patientData.patientId,  // now using the full data
      firstName: this.patientData.firstName,
      lastName: this.patientData.lastName,
      dateOfBirth: this.patientData.dateOfBirth,
      gender: this.patientData.gender,
      bloodType: this.patientData.bloodType,
      height: this.patientData.height,
      weight: this.patientData.weight,
      phone: this.patientData.phone,
      addressLine1: this.patientData.addressLine1,
      addressLine2: this.patientData.addressLine2,
      city: this.patientData.city,
      state: this.patientData.state,
      postal: this.patientData.postal,
      country: this.patientData.country,
      emergencyContactName: this.patientData.emergencyContactName,
      emergencyContactPhone: this.patientData.emergencyContactPhone,
      medicalNotes: this.note
    };

    console.log('Update payload', updatePayload);


    this.http.put(`http://localhost:8080/patient`, updatePayload, {headers})
      .subscribe({
        next: () => alert('Note saved successfully!'),
        error: (err) => alert('Error saving note: ' + err.message)
      });
  }
}
