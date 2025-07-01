import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';



NgModule({
  declarations: [],
  imports: [
    CommonModule
  ]
})
export interface Employee {
  empId: string;
  firstName: string;
  lastName: string;
  email: string;
  departmentId: string;
  status: string;
}
