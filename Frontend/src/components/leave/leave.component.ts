import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

// PrimeNG Modules
import { CalendarModule } from 'primeng/calendar';
import { DropdownModule } from 'primeng/dropdown';
import { InputNumberModule } from 'primeng/inputnumber';
import { ToastModule } from 'primeng/toast';
import { TableModule } from 'primeng/table';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { CheckboxModule } from 'primeng/checkbox';
import { TabViewModule } from 'primeng/tabview';
import { MessageService } from 'primeng/api';

// Services
import { LeaveService } from '../../app/services/leave.service';
import { AuthService } from '../../app/auths/auth.service';
import { EmployeeService } from '../../app/services/employee.service';

@Component({
  selector: 'app-leave',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    CalendarModule,
    DropdownModule,
    InputNumberModule,
    ToastModule,
    TableModule,
    DialogModule,
    ButtonModule,
    InputTextareaModule,
    CheckboxModule,
    TabViewModule
  ],
  templateUrl: './leave.component.html',
  styleUrls: ['./leave.component.css'],
  providers: [MessageService]
})
export class LeaveComponent implements OnInit {
  leaveTypes: any[] = [];
  leaveRequests: any[] = [];
  leaveBalance: any[] = [];
  optionalHolidays: any[] = [];
  selectedHolidays: any[] = [];

  leaveRequest: any = {
    leaveType: null,
    startDate: new Date(),
    endDate: new Date(),
    noOfHalfDays: 0,
    reason: ''
  };

  displayLeaveDialog = false;
  loading = false;
  userRole = '';
  employeeId = '';

  constructor(
    private leaveService: LeaveService,
    private messageService: MessageService,
    private authService: AuthService,
    private employeeService: EmployeeService
  ) {}

  ngOnInit() {
    this.userRole = this.authService.getUserRole();
    this.employeeId = this.authService.currentUserValue?.employeeId ?? '';

    if (this.employeeId) {
      this.loadLeaveBalance();
      this.loadLeaveRequests();
    }

    this.loadLeaveTypes();
    this.loadOptionalHolidays();
  }

  loadLeaveTypes() {
    this.leaveService.getLeaveTypes().subscribe(data => {
      this.leaveTypes = data || [];
    });
  }

  loadLeaveBalance() {
    this.leaveService.getLeaveBalance(this.employeeId).subscribe(data => {
      this.leaveBalance = data || [];
    });
  }

  loadLeaveRequests() {
    this.leaveService.getLeaveRequests(this.employeeId).subscribe(data => {
      this.leaveRequests = data || [];
    });
  }

  loadOptionalHolidays() {
    this.leaveService.getOptionalHolidays().subscribe(data => {
      this.optionalHolidays = data || [];
    });
  }

  submitLeaveRequest() {
    if (!this.leaveRequest.leaveType || !this.employeeId) return;

    this.loading = true;
    const payload = {
      employeeId: this.employeeId,
      leaveType: this.leaveRequest.leaveType.name,
      startDate: this.leaveRequest.startDate,
      endDate: this.leaveRequest.endDate,
      noOfHalfDays: this.leaveRequest.noOfHalfDays,
      reason: this.leaveRequest.reason
    };

    this.leaveService.createLeaveRequest(payload).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Leave request submitted successfully'
        });
        this.displayLeaveDialog = false;
        this.resetLeaveRequest();
        this.loadLeaveBalance();
        this.loadLeaveRequests();
        this.loading = false;
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: err?.error?.message || 'Failed to submit leave request'
        });
        this.loading = false;
      }
    });
  }

  resetLeaveRequest() {
    this.leaveRequest = {
      leaveType: null,
      startDate: new Date(),
      endDate: new Date(),
      noOfHalfDays: 0,
      reason: ''
    };
  }

  selectOptionalHolidays() {
    if (this.selectedHolidays.length === 0 || !this.employeeId) return;

    this.loading = true;
    const holidayIds = this.selectedHolidays.map(h => h.optionalHolidayId);

    this.leaveService.selectOptionalHolidays(this.employeeId, holidayIds).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Holiday request submitted successfully'
        });
        this.selectedHolidays = [];
        this.loadOptionalHolidays();
        this.loading = false;
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: err?.error?.message || 'Failed to submit holiday request'
        });
        this.loading = false;
      }
    });
  }

  approveRejectLeave(leaveRequestId: string, status: string) {
    this.leaveService.approveRejectLeave(leaveRequestId, status).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: `Leave request ${status.toLowerCase()} successfully`
        });
        this.loadLeaveRequests();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: err?.error?.message || `Failed to ${status.toLowerCase()} leave request`
        });
      }
    });
  }
}
