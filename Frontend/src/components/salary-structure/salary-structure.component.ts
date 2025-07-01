import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../app/auths/auth.service';
import { CommonModule } from '@angular/common';
 
@Component({
  selector: 'app-salary-structure',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './salary-structure.component.html',
  styleUrl: './salary-structure.component.css'
})
export class SalaryStructureComponent implements OnInit {
  salaryForm!: FormGroup;
  fetchedData: any;
  updateMode: boolean = false;
  employees: any[] = [];
 
  constructor(private fb: FormBuilder, private authService: AuthService) {}
 
  ngOnInit(): void {
    this.salaryForm = this.fb.group({
      salaryStructureId: [''],
      employeeId: ['', Validators.required],
      basicSalary: ['', Validators.required],
      hra: ['', Validators.required],
      specialAllowance: ['', Validators.required],
      bonus: ['', Validators.required],
      pfDeduction: ['', Validators.required],
      taxDeduction: ['', Validators.required],
      effectiveFrom: ['', Validators.required],
    });
 
    this.loadEmployees();
  }
 
  loadEmployees(): void {
    this.authService.getAllEmployees().subscribe({
      next: (res) => {
        this.employees = res;
      },
      error: (err) => {
        alert('Failed to load employees');
        console.error(err);
      }
    });
  }
 
assignSalary() {
  const empId = this.salaryForm.value.employeeId;
 
  const payload = {
    employee: { empId },
    basicSalary: this.salaryForm.value.basicSalary,
    hra: this.salaryForm.value.hra,
    specialAllowance: this.salaryForm.value.specialAllowance,
    bonus: this.salaryForm.value.bonus,
    pfDeduction: this.salaryForm.value.pfDeduction,
    taxDeduction: this.salaryForm.value.taxDeduction,
    effectiveFrom: this.salaryForm.value.effectiveFrom
  };
 
  this.authService.assignSalaryStructure(payload).subscribe(() => {
    alert('Salary structure assigned successfully!');
    this.salaryForm.reset();
  });
}
 
fetchSalaryStructureByEmpId() {
  const empId = this.salaryForm.value.employeeId;
  if (!empId) return;
 
  this.authService.getSalaryStructureByEmployeeId(empId).subscribe({
    next: (data) => {
      this.fetchedData = data;
      this.salaryForm.patchValue({
        salaryStructureId: data.salaryStructureId,
        basicSalary: data.basicSalary,
        hra: data.hra,
        specialAllowance: data.specialAllowance,
        bonus: data.bonus,
        pfDeduction: data.pfDeduction,
        taxDeduction: data.taxDeduction,
        effectiveFrom: data.effectiveFrom,
      });
      this.updateMode = true;
    },
    error: (err) => {
      alert('No salary structure found for this employee.');
      this.salaryForm.patchValue({ salaryStructureId: '' });
      this.fetchedData = null;
    }
  });
}
 
 
 
  updateSalaryStructure() {
    const id = this.salaryForm.value.salaryStructureId;
    const empId = this.salaryForm.value.employeeId;
    if (!id || !empId) return;
 
    const updatedPayload = {
      employee: { empId },
      basicSalary: this.salaryForm.value.basicSalary,
      hra: this.salaryForm.value.hra,
      specialAllowance: this.salaryForm.value.specialAllowance,
      bonus: this.salaryForm.value.bonus,
      pfDeduction: this.salaryForm.value.pfDeduction,
      taxDeduction: this.salaryForm.value.taxDeduction,
      effectiveFrom: this.salaryForm.value.effectiveFrom
    };
 
    this.authService.updateSalaryStructure(id, updatedPayload).subscribe(() => {
      alert('Salary structure updated successfully!');
      this.salaryForm.reset();
      this.updateMode = false;
    });
  }
 
  deleteSalaryStructure() {
    const id = this.salaryForm.value.salaryStructureId;
    if (!id) return;
 
    this.authService.deleteSalaryStructure(id).subscribe(() => {
      alert('Salary structure deleted successfully!');
      this.salaryForm.reset();
      this.updateMode = false;
    });
  }
}
 
 