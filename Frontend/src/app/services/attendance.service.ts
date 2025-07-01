import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AttendanceService {
  private apiUrl = '/api/attendance';

  constructor(private http: HttpClient) { }

  clockIn(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/clockIn`, data);
  }

  clockOut(): Observable<any> {
    return this.http.patch(`${this.apiUrl}/clockOut`, {});
  }

  generatePdfReport(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/pdf`, data, { responseType: 'blob' });
  }

  generateCsvReport(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/csv`, data, { responseType: 'blob' });
  }

  fetchMonthlyAttendanceStatus(fromDate: string, toDate: string, empId: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/monthlyStatus`, {
      params: { fromDate, toDate, empId }
    });
  }
}