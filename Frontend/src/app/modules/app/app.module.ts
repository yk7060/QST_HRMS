import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

// ✅ PrimeNG Modules
import { ToastModule } from 'primeng/toast';
import { DialogModule } from 'primeng/dialog';
import { FileUploadModule } from 'primeng/fileupload';
import { InputTextModule } from 'primeng/inputtext';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MessageService } from 'primeng/api';

import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';

// ✅ Standalone components (use import, not declare)

import { AppComponent } from '../../app.component';
import { LoginComponent } from '../../../components/login/login.component';
import { InputNumberModule } from 'primeng/inputnumber';


@NgModule({
  // ❌ REMOVE standalone components from declarations
  declarations: [
    
  ],
  // ✅ ADD standalone components to imports
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    HttpClientModule,
    BrowserAnimationsModule,
    ToastModule,
    DialogModule,
    FileUploadModule,
    InputTextModule,
    RouterModule,
    InputNumberModule,


    AppComponent,     // ✅ Standalone component imported
    LoginComponent    // ✅ Standalone component imported
  ],
  providers: [MessageService],
  bootstrap: []
})
export class AppModule {}
