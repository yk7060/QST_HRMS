import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgChartsModule } from 'ng2-charts';
import { ChartConfiguration, ChartType } from 'chart.js';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { FinanceSidebarComponent } from "../finance-sidebar/finance-sidebar.component";

@Component({
  selector: 'app-finance-dashboard',
  standalone: true,
  imports: [CommonModule, NgChartsModule, FinanceSidebarComponent],
  templateUrl: './finance-dashboard.component.html',
  styleUrls: ['./finance-dashboard.component.css']
})
export class FinanceDashboardComponent implements OnInit {
  totalRevenue = 2450000;
  totalExpenses = 1870000;
  profitMargin = 23.5;
  pendingInvoices = 56;

  // Monthly Budget Chart
  budgetChartType: ChartType = 'bar';
  budgetChartData: ChartConfiguration<'bar'>['data'] = {
    labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
    datasets: [
      {
        label: 'Budget',
        data: [400000, 380000, 420000, 450000, 460000, 440000],
        backgroundColor: '#4e73df'
      },
      {
        label: 'Actual',
        data: [390000, 370000, 430000, 440000, 450000, 430000],
        backgroundColor: '#1cc88a'
      }
    ]
  };
  budgetChartOptions: ChartConfiguration<'bar'>['options'] = {
    responsive: true,
    maintainAspectRatio: false
  };

  // Expense Categories
  expenseChartType: ChartType = 'doughnut';
  expenseChartData: ChartConfiguration<'doughnut'>['data'] = {
    labels: ['Salaries', 'Operations', 'Marketing', 'R&D', 'Others'],
    datasets: [{
      data: [750000, 450000, 300000, 250000, 120000],
      backgroundColor: ['#f6c23e', '#e74a3b', '#36b9cc', '#858796', '#1cc88a']
    }]
  };
  expenseChartOptions: ChartConfiguration<'doughnut'>['options'] = {
    responsive: true,
    maintainAspectRatio: false
  };

  // Upcoming Payments
  upcomingPayments = [
    { vendor: 'Vendor A', amount: 15000, dueDate: new Date('2025-06-25') },
    { vendor: 'Vendor B', amount: 8200, dueDate: new Date('2025-06-27') },
    { vendor: 'Consultant Z', amount: 12000, dueDate: new Date('2025-07-01') }
  ];

  constructor() {}

  ngOnInit(): void {}
}
