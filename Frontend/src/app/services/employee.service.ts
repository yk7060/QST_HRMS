import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {
  private apiUrl = '/api/employees';

  constructor(private http: HttpClient) { }

  getAllEmployees(): Observable<any> {
    return this.http.get(`${this.apiUrl}/all`);
  }

  getEmployeeById(empId: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/single/${empId}`);
  }

  createEmployee(employeeData: any, photo: File): Observable<any> {
    const formData = new FormData();
    formData.append('employee', JSON.stringify(employeeData));
    if (photo) {
      formData.append('photo', photo);
    }
    return this.http.post(this.apiUrl, formData);
  }

  updateEmployee(empId: string, employeeData: any, photo?: File): Observable<any> {
    const formData = new FormData();
    formData.append('employee', JSON.stringify(employeeData));
    if (photo) {
      formData.append('photo', photo);
    }
    return this.http.put(`${this.apiUrl}/${empId}`, formData);
  }

  deleteEmployee(empId: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${empId}`);
  }
}