import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { saveAs } from 'file-saver'; // ✅ Fix for saveAs

import { AuthService } from '../../app/auths/auth.service';

@Component({
  selector: 'app-payroll',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './payroll.component.html',
  styleUrls: ['./payroll.component.css']
})
export class PayrollComponent implements OnInit {
  payrollForm!: FormGroup;
  employeeId = '';
  employees: any[] = [];
  salaryStructure: any;
  payslipView: any;
  isAdmin = false;
  isEmployee = false;

  constructor(private fb: FormBuilder, private authService: AuthService) {}

  ngOnInit(): void {
    this.isAdmin = this.authService.isAdmin();
    this.isEmployee = this.authService.isEmployee();
    this.employeeId = this.isAdmin ? '' : this.authService.getEmployeeId();

    this.payrollForm = this.fb.group({
      empId: [this.employeeId, Validators.required],
      fromDate: ['', Validators.required],
      toDate: ['', Validators.required],
      month: ['JUNE', Validators.required]
    });

    if (this.isAdmin) {
      this.authService.getAllEmployees().subscribe({
        next: (res) => this.employees = res,
        error: () => alert('Failed to load employees')
      });
    }

    if (this.isEmployee) {
      this.viewPayslipEmployee('JUNE');
    }
  }

  getEffectiveEmpId(): string {
    return this.isAdmin ? this.payrollForm.value.empId : this.employeeId;
  }

  fetchStructure() {
    const id = this.getEffectiveEmpId();
    this.authService.fetchSalaryStructure(id).subscribe({
      next: (res) => this.salaryStructure = res,
      error: () => alert('Error fetching structure')
    });
  }

  addPayroll() {
    const payload = {
      empId: this.getEffectiveEmpId(),
      fromDate: this.payrollForm.value.fromDate,
      toDate: this.payrollForm.value.toDate
    };

    this.authService.addPayroll(payload).subscribe({
      next: () => alert('Payroll added'),
      error: () => alert('Error adding payroll')
    });
  }

  generatePayslip() {
    const id = this.getEffectiveEmpId();
    this.authService.generatePayslip(id, this.payrollForm.value.month).subscribe({
      next: () => alert('Payslip generated'),
      error: () => alert('Error generating payslip')
    });
  }

  downloadPayslip() {
    const month = this.payrollForm.value.month;

    if (this.isAdmin) {
      const id = this.getEffectiveEmpId();
      this.authService.downloadPayslipByAdmin(id, month).subscribe(blob => {
        saveAs(blob, `Payslip_${id}_${month}.pdf`);
      });
    } else {
      this.authService.downloadPayslipByEmployee(month).subscribe(blob => {
        saveAs(blob, `Payslip_${month}.pdf`);
      });
    }
  }

  viewPayslip() {
    const id = this.getEffectiveEmpId();
    const month = this.payrollForm.value.month;

    if (this.isAdmin) {
      this.authService.viewPayslipByAdmin(id, month).subscribe(res => {
        this.payslipView = res;
      });
    } else {
      this.viewPayslipEmployee(month);
    }
  }

  viewPayslipEmployee(month: string) {
    this.authService.viewPayslipByEmployee(month).subscribe(res => {
      this.payslipView = res;
    });
  }

  // ✅ Used in template to format field names into labels
  formatLabel(field: string): string {
    return field.replace(/([A-Z])/g, ' $1').replace(/^./, str => str.toUpperCase());
  }

  // ✅ Alias for viewing payslipView in template
  get payslipData() {
    return this.payslipView;
  }
}
