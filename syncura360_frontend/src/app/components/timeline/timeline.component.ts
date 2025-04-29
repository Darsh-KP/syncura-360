import { Component, OnInit , Input} from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { TimelineModule } from 'primeng/timeline';

import { VisitMgmtService } from '../../services/visit-mgmt.service';
import {
  drugPerVisit, servicePerVisit, roomForVisit, dischargeForVisit, notesForVisit
} from '../../services/visit-mgmt.service';

@Component({
  selector: 'app-timeline',
  standalone: true,
  templateUrl: './timeline.component.html',
  styleUrl: './timeline.component.css',
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    TimelineModule
  ]
})
export class TimelineComponent implements OnInit {
  @Input() readOnly: boolean = false;

  patientID!: number;
  admissionDateTime!: string;
  timelineEvents: { dateTime: string; title: string; description: string }[] = [];

  selectedSection: string | null = null;

  drugForm!: FormGroup;
  serviceForm!: FormGroup;
  roomForm!: FormGroup;
  dischargeForm!: FormGroup;
  drugs: any[] = [];
  doctors: any[] = [];
  services: any[] = [];
  rooms: any[] = [];
  note:String | undefined;
  details: any;


  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder,
    private visitService: VisitMgmtService
  ) {
    const nav = this.router.getCurrentNavigation();
    this.readOnly = nav?.extras.state?.['readOnly'] ?? false;
  }

  ngOnInit(): void {
    this.patientID = Number(this.route.snapshot.paramMap.get('patientID'));
    this.admissionDateTime = this.route.snapshot.paramMap.get('admissionDateTime')!;
    this.loadTimeline();

    this.visitService.getDrugs().subscribe((res) => (this.drugs = res.drugs || []));
    this.visitService.getDoctors().subscribe((res) => (this.doctors = res.doctors || []));
    this.visitService.getServices().subscribe((res) => (this.services = res.services || []));
    this.visitService.getRooms().subscribe((res) => (this.rooms = res.rooms || []));
    this.visitService.getPatientDetailsString(this.patientID).then((details) => {
      this.details = details;
    });
    this.drugForm = this.fb.group({
      drug: ['', Validators.required],
      quantity: [1, [Validators.required, Validators.min(1)]],
      administeredBy: ['', Validators.required]
    });

    this.serviceForm = this.fb.group({
      service: ['', Validators.required],
      performedBy: ['', Validators.required]
    });

    this.roomForm = this.fb.group({
      roomName: ['', Validators.required]
    });

    this.dischargeForm = this.fb.group({
      visitSummary: ['', Validators.required]
    });
  }

  selectSection(section: string) {
    this.selectedSection = section;
  }

  loadTimeline() {

    if (!this.readOnly) {

      this.visitService.getTimeline(this.patientID, this.admissionDateTime).subscribe({
        next: (res) => {
          (this.timelineEvents = res.timeline);
          this.note = res.visitNote;
        },
        error: (err) => console.error('Failed to load timeline', err),
      });
    } else {
      this.visitService.getRecord(this.patientID, this.admissionDateTime).subscribe({
        next: (res) => {
          (this.timelineEvents = res.timeline);
          this.note = res.visitNote;
        },
        error: (err) => console.error('Failed to load record timeline', err),
      });
    }
  }

  updateNote() {
    const data: notesForVisit = {
      patientID: this.patientID,
      visitAdmissionDateTime: this.admissionDateTime,
      note: this.note
    };
    this.visitService.updateNote(data).subscribe({
      next: () => this.loadTimeline(),
      error: (err) => console.error('Failed to update note', err)
    });
  }

  removeRoom() {
    if (!confirm('Are you sure you want to remove this room?')) return;
    this.visitService.removeRoom({
      patientID: this.patientID,
      visitAdmissionDateTime: this.admissionDateTime
    }).subscribe({
      next: () => this.loadTimeline(),
      error: (err) => console.error('Failed to remove room', err)
    });
  }

  submitDrug() {
    if (this.drugForm.invalid) return;
    const form = this.drugForm.value;
    const data: drugPerVisit = {
      patientID: this.patientID,
      visitAdmissionDateTime: this.admissionDateTime,
      drug: form.drug,
      quantity: form.quantity,
      administeredBy: form.administeredBy
    };
    this.visitService.addDrug(data).subscribe({
      next: () => {
        this.drugForm.reset();
        this.loadTimeline();
      },
      error: (err) => console.error('Failed to add drug', err)
    });
  }

  submitService() {
    if (this.serviceForm.invalid) return;
    const form = this.serviceForm.value;
    const data: servicePerVisit = {
      patientID: this.patientID,
      visitAdmissionDateTime: this.admissionDateTime,
      service: form.service,
      performedBy: form.performedBy
    };
    this.visitService.addService(data).subscribe({
      next: () => {
        this.serviceForm.reset();
        this.loadTimeline();
      },
      error: (err) => console.error('Failed to add service', err)
    });
  }

  submitRoom() {
    if (this.roomForm.invalid) return;
    const data: roomForVisit = {
      patientID: this.patientID,
      visitAdmissionDateTime: this.admissionDateTime,
      roomName: this.roomForm.value.roomName
    };
    this.visitService.assignRoom(data).subscribe({
      next: () => {
        this.roomForm.reset();
        this.loadTimeline();
      },
      error: (err) => console.error('Failed to assign room', err)
    });
  }

  submitDischarge() {
    if (this.dischargeForm.invalid) return;
    const data: dischargeForVisit = {
      patientID: this.patientID,
      visitAdmissionDateTime: this.admissionDateTime,
      visitSummary: this.dischargeForm.value.visitSummary
    };
    this.visitService.dischargeVisit(data).subscribe({
      next: () => this.router.navigate(['/visit-mgmt']),
      error: (err) => console.error('Failed to discharge', err)
    });
  }

  backButton(){
    if(this.readOnly){
      this.router.navigate(['/visit-records']);
    }
    else{
    this.router.navigate(['/visit-mgmt']);
    }
  }


}
