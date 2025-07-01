import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { AuthService } from '../../app/auths/auth.service';

@Component({
  selector: 'app-attendance',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule, ToastModule],
  templateUrl: './attendance.component.html',
  styleUrls: ['./attendance.component.css'],
  providers: [MessageService]
})
export class AttendanceComponent implements OnInit {
  attendanceForm: FormGroup;
  reportForm: FormGroup;
  isHR = false;
  userId = '';

  private fb = inject(FormBuilder);
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private messageService = inject(MessageService);

  constructor() {
    this.attendanceForm = this.fb.group({
      workingFrom: [''],
      mode: [''],
      location: ['']
    });

    this.reportForm = this.fb.group({
      fromDate: [''],
      toDate: [''],
      employeeId: [''],
      departmentId: ['']
    });
  }

  ngOnInit(): void {
    this.userId = this.authService.getUserId();
    this.isHR = this.authService.hasRole('HR');
  }

  clockIn() {
    const city = this.attendanceForm.get('location')?.value;
    const workingFrom = this.attendanceForm.get('workingFrom')?.value;
    const mode = this.attendanceForm.get('mode')?.value;

    if (!city || !workingFrom || !mode) {
      this.messageService.add({ severity: 'warn', summary: 'All fields are required' });
      return;
    }

    this.http.get(`https://nominatim.openstreetmap.org/search?format=json&q=${city}`).subscribe({
      next: (res: any) => {
        if (res.length > 0) {
          const lat = parseFloat(res[0].lat);
          const lon = parseFloat(res[0].lon);
          const geoLocationString = `Latitude: ${lat}, Longitude: ${lon}`;

          const payload = {
            employeeId: this.userId,
            location: geoLocationString,
            workingFrom,
            mode
          };

          console.log('📤 Payload:', payload);

          this.authService.clockInAttendance(payload).subscribe({
            next: (res: any) => {
              const message = res?.message || 'Clocked In successfully';
              this.messageService.add({
                severity: 'success',
                summary: 'Success',
                detail: message
              });
              this.attendanceForm.reset();
            },
            error: (err) => {
              console.error("❌ Clock-In Error:", err);

              let errorMessage = 'Failed to Clock In';
              try {
                if (err.error instanceof ProgressEvent) {
                  errorMessage = 'Network Error or Server Unavailable';
                } else if (typeof err.error === 'string') {
                  errorMessage = err.error;
                } else if (err.error?.message) {
                  errorMessage = err.error.message;
                }
              } catch (e) {}

              this.messageService.add({
                severity: 'error',
                summary: 'Clock-In Failed',
                detail: errorMessage
              });
            }
          });

        } else {
          this.messageService.add({ severity: 'error', summary: 'Invalid Location!' });
        }
      },
      error: () => {
        this.messageService.add({ severity: 'error', summary: 'Location API failed' });
      }
    });
  }

  clockOut() {
    this.authService.clockOutAttendance().subscribe({
      next: (res: any) => {
        const message = res?.message || 'Clock-Out successful';
        this.messageService.add({ severity: 'success', summary: 'Success', detail: message });
      },
      error: (err: any) => {
        const errorMessage = err?.error?.message || err.message || 'Clock-Out failed';
        this.messageService.add({ severity: 'error', summary: 'Clock-Out Failed', detail: errorMessage });
      }
    });
  }

  generatePdfReport() {
    this.authService.downloadAttendancePdf(this.reportForm.value).subscribe((blob: Blob) => {
      this.downloadFile(blob, 'attendance-report.pdf');
    });
  }

  generateCsvReport() {
    this.authService.downloadAttendanceCsv(this.reportForm.value).subscribe((blob: Blob) => {
      this.downloadFile(blob, 'attendance-report.csv');
    });
  }

  fetchMonthlyStatus() {
    const { fromDate, toDate, employeeId } = this.reportForm.value;

    this.authService.getMonthlyAttendanceStatus({ fromDate, toDate, employeeId: employeeId }).subscribe({
      next: (res: any) => {
        this.messageService.add({
          severity: 'info',
          summary: 'Monthly Status Fetched',
          detail: 'Check console for details'
        });
        console.log('Monthly Status:', res);
      },
      error: (err: any) => this.messageService.add({
        severity: 'error',
        summary: 'Fetch Failed',
        detail: err?.error?.message || err.message
      })
    });
  }

  private downloadFile(blob: Blob, filename: string) {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.click();
    window.URL.revokeObjectURL(url);
  }
}
