import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { AuthService, LeaveType } from '../../app/auths/auth.service';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-leave-type',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule],
  templateUrl: './leave-type.component.html',
  styleUrls: ['./leave-type.component.scss'],
  providers: [MessageService] // make sure this is provided
})
export class LeaveTypeComponent implements OnInit {
  leaveTypeForm!: FormGroup;

  // ✅ Dynamic dropdown list
  leaveTypeNames: string[] = [
    'SICK_LEAVE',
    'CASUAL_LEAVE',
    'ANNUAL_LEAVE',
    'MATERNITY_LEAVE',
    'PATERNITY_LEAVE',
    'COMP_OFF',
    'LOP',
    'EARNED_LEAVE'
  ];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    this.initializeForm();
  }

  initializeForm(): void {
    this.leaveTypeForm = this.fb.group({
      leaveTypeId: [''],
      name: ['', Validators.required],
      description: ['', Validators.required],
      maxDaysPerYear: [0, [Validators.required, Validators.min(1)]],
      carryForward: [false],
      encashable: [false],
      approvalFlow: ['']
    });
  }

  // ✅ Add or Update
  onSubmit(): void {
    if (this.leaveTypeForm.invalid) {
      this.show('Please complete all required fields.', 'warn');
      return;
    }

    const formValue: LeaveType = this.leaveTypeForm.value;

    if (formValue.leaveTypeId) {
      this.authService.updateLeaveType(formValue.leaveTypeId, formValue).subscribe({
        next: () => {
          this.show('Leave type updated successfully', 'success');
          this.leaveTypeForm.reset();
        },
        error: () => this.show('Failed to update leave type', 'error')
      });
    } else {
      this.authService.addLeaveType(formValue).subscribe({
        next: () => {
          this.show('Leave type added successfully', 'success');
          this.leaveTypeForm.reset();
        },
        error: () => this.show('Failed to add leave type', 'error')
      });
    }
  }

  // ✅ Get by ID
  fetchById(): void {
    const id = this.leaveTypeForm.get('leaveTypeId')?.value;
    if (!id) {
      this.show('Please enter Leave Type ID to fetch', 'warn');
      return;
    }

    this.authService.getLeaveTypeById(id).subscribe({
      next: (data: LeaveType) => this.leaveTypeForm.patchValue(data),
      error: () => this.show('Leave type not found', 'error')
    });
  }

  // ✅ Delete by ID
  deleteById(): void {
    const id = this.leaveTypeForm.get('leaveTypeId')?.value;
    if (!id) {
      this.show('Please enter Leave Type ID to delete', 'warn');
      return;
    }

    this.authService.deleteLeaveType(id).subscribe({
      next: () => {
        this.show('Leave type deleted successfully', 'warn');
        this.leaveTypeForm.reset();
      },
      error: () => this.show('Failed to delete leave type', 'error')
    });
  }

  // ✅ Toast Utility
  private show(detail: string, severity: 'success' | 'info' | 'warn' | 'error') {
    this.messageService.add({
      severity,
      summary: 'Leave Type',
      detail
    });
  }
}
