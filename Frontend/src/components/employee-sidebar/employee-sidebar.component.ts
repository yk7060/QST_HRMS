import { Component } from '@angular/core';

@Component({
  selector: 'app-employee-sidebar',
  standalone: true,
  imports: [],
  templateUrl: './employee-sidebar.component.html',
  styleUrl: './employee-sidebar.component.css'
})
export class EmployeeSidebarComponent {
   isCollapsed = false;

  toggleSidebar() {
    this.isCollapsed = !this.isCollapsed;
  }
}


