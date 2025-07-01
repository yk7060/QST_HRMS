import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LeaveService {
  private baseUrl = '/api/leave'; // Adjust if needed

  constructor(private http: HttpClient) {}

  getLeaveTypes(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/types`);
  }

  getLeaveBalance(employeeId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/balance/${employeeId}`);
  }

  getLeaveRequests(employeeId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/requests/${employeeId}`);
  }

  getOptionalHolidays(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/optional-holidays`);
  }

  createLeaveRequest(payload: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/request`, payload);
  }

  selectOptionalHolidays(employeeId: string, holidayIds: string[]): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/optional-holidays/${employeeId}`, { holidayIds });
  }

  approveRejectLeave(leaveRequestId: string, status: string): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/approval/${leaveRequestId}`, { status });
  }
}
