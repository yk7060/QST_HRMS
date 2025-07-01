import { Component } from '@angular/core';

@Component({
  selector: 'app-manager-sidebar',
  standalone: true,
  imports: [],
  templateUrl: './manager-sidebar.component.html',
  styleUrl: './manager-sidebar.component.css'
})
export class ManagerSidebarComponent {
  isCollapsed = false;

  toggleSidebar() {
    this.isCollapsed = !this.isCollapsed;
  }

}
