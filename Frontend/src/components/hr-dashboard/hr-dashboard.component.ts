import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { NgChartsModule } from 'ng2-charts'; // ✅ Added import
import { ChartConfiguration, ChartType } from 'chart.js';
import { SidebarComponent } from "../sidebar/sidebar.component";
import { HrSidebarComponent } from "../hr-sidebar/hr-sidebar.component";

@Component({
  selector: 'app-hr-dashboard',
  standalone: true,
  imports: [CommonModule, NgChartsModule, HrSidebarComponent],
  templateUrl: './hr-dashboard.component.html',
  styleUrls: ['./hr-dashboard.component.css']
})
export class HrDashboardComponent implements OnInit {

  // Employee Statistics
  totalEmployees = 1245;
  newHiresThisMonth = 42;
  employeesOnLeave = 28;
  attritionRate = 8.5;

  // Department Distribution Doughnut Chart
  departmentChartType: ChartType = 'doughnut';
  departmentData: ChartConfiguration<'doughnut'>['data'] = {
    labels: ['Engineering', 'Marketing', 'Sales', 'HR', 'Operations', 'Finance'],
    datasets: [{
      data: [420, 180, 250, 60, 200, 135],
      backgroundColor: [
        '#4e73df',
        '#1cc88a',
        '#36b9cc',
        '#f6c23e',
        '#e74a3b',
        '#858796'
      ]
    }]
  };

  departmentOptions: ChartConfiguration<'doughnut'>['options'] = {
    responsive: true,
    maintainAspectRatio: false
  };

  // Performance Ratings Bar Chart
  performanceChartType: ChartType = 'bar';
  performanceData: ChartConfiguration<'bar'>['data'] = {
    labels: ['Q1', 'Q2', 'Q3', 'Q4'],
    datasets: [
      {
        label: 'Exceeds Expectations',
        data: [25, 28, 30, 32],
        backgroundColor: '#1cc88a'
      },
      {
        label: 'Meets Expectations',
        data: [60, 58, 55, 53],
        backgroundColor: '#36b9cc'
      },
      {
        label: 'Needs Improvement',
        data: [15, 14, 15, 15],
        backgroundColor: '#f6c23e'
      }
    ]
  };

  performanceOptions: ChartConfiguration<'bar'>['options'] = {
    responsive: true,
    scales: {
      x: { stacked: true },
      y: { stacked: true }
    }
  };

  // Hiring Pipeline
  hiringStages = [
    { stage: 'Applied', count: 156 },
    { stage: 'Screening', count: 78 },
    { stage: 'Interview', count: 45 },
    { stage: 'Offer', count: 12 },
    { stage: 'Hired', count: 8 }
  ];

  // Leave Balance
  leaveBalance = [
    { type: 'Annual Leave', balance: 12, used: 8 },
    { type: 'Sick Leave', balance: 10, used: 3 },
    { type: 'Personal Leave', balance: 5, used: 1 }
  ];

  // Upcoming Events
  upcomingEvents = [
    { title: 'Quarterly Review', date: new Date('2023-12-15'), type: 'meeting' },
    { title: 'HR Policy Update', date: new Date('2023-12-18'), type: 'announcement' },
    { title: 'Team Building', date: new Date('2023-12-22'), type: 'event' },
    { title: 'Payroll Processing', date: new Date('2023-12-25'), type: 'reminder' }
  ];

  // Training Status
  trainingStatus = {
    completed: 65,
    inProgress: 23,
    notStarted: 12
  };

  constructor() {}

  ngOnInit(): void {}

  getEventIcon(type: string): string {
    switch (type) {
      case 'meeting': return 'fa-calendar-check';
      case 'announcement': return 'fa-bullhorn';
      case 'event': return 'fa-users';
      case 'reminder': return 'fa-bell';
      default: return 'fa-calendar';
    }
  }
}
