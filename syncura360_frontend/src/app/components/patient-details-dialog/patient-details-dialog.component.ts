import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-patient-details-dialog',
  standalone: true,
  imports: [CommonModule, FormsModule, MatDialogModule],
  template: `
    <h2 class="text-lg font-bold mb-4">Patient Info</h2>
    <div class="space-y-2">
      <p><strong>Name:</strong> {{ data.firstName }} {{ data.lastName }}</p>
      <p><strong>DOB:</strong> {{ data.dateOfBirth }}</p>
      <p><strong>Gender:</strong> {{ data.gender }}</p>
      <p><strong>Blood Type:</strong> {{ data.bloodType }}</p>
      <p><strong>Height:</strong> {{ data.height }} in</p>
      <p><strong>Weight:</strong> {{ data.weight }} lbs</p>
      <p><strong>Notes:</strong></p>
      <textarea [(ngModel)]="note" rows="5" class="w-full p-2 border border-gray-300 rounded"></textarea>
    </div>
    <div class="text-right mt-4">
      <button class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600" (click)="saveNote()">Save Note</button>
    </div>
  `
})
export class PatientDetailsDialogComponent {
  note: string = '';

  constructor(@Inject(MAT_DIALOG_DATA) public data: any, private http: HttpClient) {
  }

  saveNote() {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    this.http.post(`http://localhost:8080/patient/${this.data.patientID}/notes`, {note: this.note}, {headers})
      .subscribe({
        next: () => alert('Note saved successfully!'),
        error: (err) => alert('Error saving note: ' + err.message)
      });
  }
}
