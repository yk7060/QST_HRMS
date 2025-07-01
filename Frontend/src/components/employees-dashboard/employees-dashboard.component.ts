import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { EmployeeSidebarComponent } from "../employee-sidebar/employee-sidebar.component";

@Component({
  selector: 'app-employees-dashboard',
  standalone: true,
  imports: [CommonModule, EmployeeSidebarComponent],
  templateUrl: './employees-dashboard.component.html',
  styleUrls: ['./employees-dashboard.component.css']
})
export class EmployeesDashboardComponent implements OnInit {

  leaveBalance = [
    { type: 'Annual Leave', total: 12, used: 5 },
    { type: 'Sick Leave', total: 8, used: 2 },
    { type: 'Casual Leave', total: 5, used: 1 }
  ];

  attendance = {
    daysPresent: 18,
    daysAbsent: 2,
    workFromHome: 3
  };

  holidays = [
    { name: 'Independence Day', date: '2025-08-15' },
    { name: 'Ganesh Chaturthi', date: '2025-09-06' },
    { name: 'Diwali', date: '2025-10-29' }
  ];

  payrollDate = '2025-06-30';

  leaveHistory = [
    { type: 'Annual Leave', from: '2025-06-10', to: '2025-06-12', status: 'Approved' },
    { type: 'Sick Leave', from: '2025-06-17', to: '2025-06-18', status: 'Pending' },
    { type: 'Casual Leave', from: '2025-05-05', to: '2025-05-06', status: 'Rejected' }
  ];

  constructor() {}

  ngOnInit(): void {}
}
