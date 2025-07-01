import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService, Department, User } from '../../app/auths/auth.service';

@Component({
  selector: 'app-department',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './department.component.html',
  styleUrl: './department.component.css',
})
export class DepartmentComponent implements OnInit {
  departments: Department[] = [];
  departmentForm!: FormGroup;
  isEditMode = false;
  currentEditId: number | null = null;
  userRole: string = '';
  managers: User[] = [];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.userRole = this.authService.getUserRole();
    this.loadDepartments();
    this.loadManagers();

    this.departmentForm = this.fb.group({
      departmentCode: ['', Validators.required],
      name: ['', Validators.required],
      description: [''],
      location: [''],
      departmentHeadId: ['', Validators.required],
    });
  }

  loadDepartments() {
    this.authService.getAllDepartments().subscribe((data) => {
      this.departments = data;
    });
  }

  loadManagers() {
    this.authService.getAllUsers().subscribe((users) => {
      this.managers = users.filter((u) => u.role === 'MANAGER');
    });
  }

  onSubmit() {
    if (this.departmentForm.invalid) return;

    const formValue = this.departmentForm.value;

    if (this.isEditMode && this.currentEditId !== null) {
      this.authService.updateDepartment(this.currentEditId, formValue).subscribe(() => {
        this.resetForm();
        this.loadDepartments();
      });
    } else {
      this.authService.createDepartment(formValue).subscribe(() => {
        this.resetForm();
        this.loadDepartments();
      });
    }
  }

  editDepartment(dept: Department) {
    this.departmentForm.patchValue(dept);
    this.isEditMode = true;
    this.currentEditId = dept.departmentId ?? null;
  }

  deleteDepartment(id: number) {
    if (confirm('Are you sure you want to delete this department?')) {
      this.authService.deleteDepartment(id).subscribe(() => {
        this.loadDepartments();
      });
    }
  }

  resetForm() {
    this.departmentForm.reset();
    this.isEditMode = false;
    this.currentEditId = null;
  }

  canModify(): boolean {
    return this.authService.isAdmin() || this.authService.isHR();
  }

  getManagerNameById(userId: string | undefined): string {
    if (!userId) return 'N/A';
    const manager = this.managers.find((m) => m.userId === userId);
    return manager ? manager.username : 'Unknown';
  }
}
