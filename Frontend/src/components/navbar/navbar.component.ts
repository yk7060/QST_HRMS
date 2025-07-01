import { Component, ViewChild } from '@angular/core';

import { ToastModule } from 'primeng/toast';

import { MessageService } from 'primeng/api';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { CommonModule } from '@angular/common';

@Component({

  selector: 'app-navbar',

  standalone: true,

  imports: [ToastModule, SidebarComponent,CommonModule],

  providers: [MessageService],

  templateUrl: './navbar.component.html',

  styleUrls: ['./navbar.component.css']

})

export class NavbarComponent {
    @ViewChild(SidebarComponent)

  sidebar!: SidebarComponent;
 
  notificationMenuOpen = false;

  userMenuOpen = false;

  notificationCount = 3;

  constructor(private messageService: MessageService) { }

  toggleNotificationMenu() {

    this.notificationMenuOpen = !this.notificationMenuOpen;

    this.messageService.add({

      severity: 'info',

      summary: 'Notification',

      detail: 'Toggled notifications menu'

    });

  }

  toggleUserMenu() {

    this.userMenuOpen = !this.userMenuOpen;

    this.messageService.add({

      severity: 'success',

      summary: 'User',

      detail: 'Toggled user menu'

    });

  }

  logout() {

    this.messageService.add({

      severity: 'warn',

      summary: 'Logout',

      detail: 'Logging out...'

    });

  }

}

