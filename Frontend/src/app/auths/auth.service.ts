import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable, catchError, map, of, switchMap, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { isPlatformBrowser } from '@angular/common';
import { environment } from '../../components/environment';

export interface Department {
  departmentId?: number;
  departmentCode: string;
  name: string;
  description?: string;
  location?: string;
  departmentHeadId?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface User {
  userId: string;
  username: string;
  email: string;
  role: string;
  status?: string;
}

export interface LeaveType {
  leaveTypeId?: string;
  name: string;
  description: string;
  maxDaysPerYear: number;
  carryForward?: boolean;
  encashable?: boolean;
  approvalFlow?: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = environment.apiBaseUrl;
  private currentUserSubject: BehaviorSubject<any>;
  public currentUser: Observable<any>;
  private isBrowser: boolean;

  constructor(
    private http: HttpClient,
    private router: Router,
    private messageService: MessageService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
    const storedUser = this.isBrowser ? localStorage.getItem('currentUser') : null;
    this.currentUserSubject = new BehaviorSubject<any>(
      storedUser ? JSON.parse(storedUser) : null
    );
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue() {
    return this.currentUserSubject.value;
  }

  getToken(): string | null {
    return this.isBrowser ? localStorage.getItem('token') : null;
  }

  getAuthHeaders(): HttpHeaders {
    const token = this.getToken();
    const headersConfig: any = {};
    if (token) headersConfig['Authorization'] = `Bearer ${token}`;
    headersConfig['Content-Type'] = 'application/json';
    return new HttpHeaders(headersConfig);
  }

  getMultipartAuthHeaders(): HttpHeaders {
    const token = this.getToken();
    return new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
  }

  getUserRole(): string {
    return this.currentUserValue?.role || '';
  }

  isAdmin(): boolean {
    return this.getUserRole() === 'ADMIN';
  }

  isHR(): boolean {
    return this.getUserRole() === 'HR';
  }

  isManager(): boolean {
    return this.getUserRole() === 'MANAGER';
  }

  isEmployee(): boolean {
    return this.getUserRole() === 'EMPLOYEE';
  }

  isLoggedIn(): boolean {
    return this.isBrowser && localStorage.getItem('token') !== null;
  }

  logout(): void {
    if (this.isBrowser) {
      localStorage.removeItem('token');
      localStorage.removeItem('currentUser');
      localStorage.removeItem('employeeId');
    }
    this.currentUserSubject.next(null);
    this.messageService.add({
      severity: 'info',
      summary: 'Logout',
      detail: 'You have been logged out',
    });
    this.router.navigate(['/login']);
  }

  private handleError(error: any, message: string): Observable<never> {
    const errMsg = error?.error || error?.message || 'Unknown error';
    this.messageService.add({
      severity: 'error',
      summary: message,
      detail: errMsg,
    });
    return throwError(() => error);
  }

adminLogin(username: string, password: string): Observable<any> {
  const headers = new HttpHeaders({ 'Content-Type': 'application/json' });

  return this.http.post(`${this.apiUrl}/admin/login`, { username, password }, {
    headers,
    responseType: 'text'  // you can keep this if your backend returns plain token
  }).pipe(
    map((token: string) => {
      if (this.isBrowser) {
        localStorage.setItem('token', token);
        const user = { username, role: 'ADMIN' };
        localStorage.setItem('currentUser', JSON.stringify(user));
        this.currentUserSubject.next(user);
      }

      this.messageService.add({
        severity: 'success',
        summary: 'Admin Login',
        detail: 'Login successful',
      });

      this.router.navigate(['/admin-dashboard']);
      return { token };
    }),
    catchError((error) => this.handleError(error, 'Admin login failed'))
  );
}


  userLogin(username: string, password: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/user/login`, { username, password }, {
      responseType: 'text'
    }).pipe(
      switchMap((token: string) => {
        if (this.isBrowser) {
          localStorage.setItem('token', token);

          const payload = JSON.parse(atob(token.split('.')[1]));
          const rawRole = payload.Role || payload.role || '';
          const normalizedRole = rawRole.replace('ROLE_', '').toUpperCase();

          const user = {
            username: payload.sub,
            role: normalizedRole
          };

          localStorage.setItem('currentUser', JSON.stringify(user));
          this.currentUserSubject.next(user);

          if (normalizedRole !== 'ADMIN') {
            return this.fetchEmployeeId().pipe(
              map(() => {
                this.messageService.add({
                  severity: 'success',
                  summary: 'Login',
                  detail: 'Login successful',
                });

                if (normalizedRole === 'HR') {
                  this.router.navigate(['/hr-dashboard']);
                } else if (normalizedRole === 'MANAGER') {
                  this.router.navigate(['/manager-dashboard']);
                } else if (normalizedRole === 'EMPLOYEE') {
                  this.router.navigate(['/employees-dashboard']);
                } else {
                  this.router.navigate(['/dashboard']);
                }

                return { token };
              }),
              catchError((err) => {
                console.warn('Failed to fetch employeeId during login', err);
                return of({ token });
              })
            );
          }

          this.messageService.add({
            severity: 'success',
            summary: 'Login',
            detail: 'Login successful',
          });
          this.router.navigate(['/dashboard']);
          return of({ token });
        }

        return of({ token });
      }),
      catchError((error) => {
        const errMsg = error.status === 406 ? error.error : 'Login failed';
        return this.handleError({ ...error, error: errMsg }, errMsg);
      })
    );
  }

  fetchEmployeeId(): Observable<any> {
    return this.http.get(`${this.apiUrl}/employees/myProfile`, {
      headers: this.getAuthHeaders()
    }).pipe(
      map((res: any) => {
        if (this.isBrowser) {
          localStorage.setItem('employeeId', res?.empId || '');
        }
        return res;
      }),
      catchError((err) => this.handleError(err, 'Fetching employee profile failed'))
    );
  }

  getEmployeeId(): string {
    if (!this.isBrowser) return '';
    const token = localStorage.getItem('token');
    if (!token) return '';
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.empId || payload.employeeId || '';
    } catch {
      return '';
    }
  }

  forgotPassword(email: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/user/forgotPwd`, {
      params: { email },
      responseType: 'text'
    }).pipe(
      map((res) => res),
      catchError((error) => this.handleError(error, 'Failed to send OTP'))
    );
  }

  resetPassword(email: string, otp: string, newPassword: string): Observable<any> {
    return this.http.patch(`${this.apiUrl}/user/resetPwd`, null, {
      params: { email, otp, newPassword },
      responseType: 'text'
    }).pipe(
      map((res) => res),
      catchError((error) => this.handleError(error, 'Password reset failed'))
    );
  }

  register(user: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/user/register`, user, {
      headers: this.getAuthHeaders(),
      responseType: 'text'
    }).pipe(
      map((res) => res),
      catchError((error) => this.handleError(error, 'Registration failed'))
    );
  }

  createEmployeeFormData(formData: FormData): Observable<any> {
    return this.http.post(`${this.apiUrl}/employees`, formData, {
      headers: this.getMultipartAuthHeaders()
    });
  }

  getAllEmployees(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/employees/all`, {
      headers: this.getAuthHeaders()
    });
  }

  getAllDepartments(): Observable<Department[]> {
    return this.http.get<Department[]>(`${this.apiUrl}/departments`, {
      headers: this.getAuthHeaders(),
    });
  }

  getDepartmentById(id: number): Observable<Department> {
    return this.http.get<Department>(`${this.apiUrl}/departments/${id}`, {
      headers: this.getAuthHeaders(),
    });
  }

  createDepartment(dept: Department): Observable<Department> {
    return this.http.post<Department>(`${this.apiUrl}/departments`, dept, {
      headers: this.getAuthHeaders(),
    });
  }

  updateDepartment(id: number, dept: Department): Observable<Department> {
    return this.http.put<Department>(`${this.apiUrl}/departments/${id}`, dept, {
      headers: this.getAuthHeaders(),
    });
  }

  deleteDepartment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/departments/${id}`, {
      headers: this.getAuthHeaders(),
    });
  }

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/user/users`, {
      headers: this.getAuthHeaders(),
    });
  }

  getUsersByRole(role: string): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/user/role/${role}`, {
      headers: this.getAuthHeaders()
    });
  }

  getManagersByDepartment(departmentId: number): Observable<User[]> {
    return this.http.get<User[]>(`/api/user/managers/by-department/${departmentId}`);
  }

  getUserId(): string {
    return this.isBrowser ? localStorage.getItem('employeeId') || '' : '';
  }

  hasRole(role: string): boolean {
    if (!this.isBrowser) return false;
    const roles = JSON.parse(localStorage.getItem('roles') || '[]');
    return roles.includes(role);
  }

  clockInAttendance(payload: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/attendance/clockIn`, payload, {
      headers: this.getAuthHeaders()
    });
  }

  clockOutAttendance(): Observable<any> {
    return this.http.patch(`${this.apiUrl}/attendance/clockOut`, {}, {
      headers: this.getAuthHeaders()
    });
  }

  downloadAttendancePdf(data: any): Observable<Blob> {
    return this.http.post(`${this.apiUrl}/attendance/pdf`, data, {
      headers: this.getAuthHeaders(),
      responseType: 'blob'
    });
  }

  downloadAttendanceCsv(data: any): Observable<Blob> {
    return this.http.post(`${this.apiUrl}/attendance/csv`, data, {
      headers: this.getAuthHeaders(),
      responseType: 'blob'
    });
  }

  getMonthlyAttendanceStatus(params: { fromDate: string, toDate: string, employeeId: string }): Observable<any> {
    const httpParams = new HttpParams()
      .set('fromDate', params.fromDate)
      .set('toDate', params.toDate)
      .set('employeeId', params.employeeId);

    return this.http.get(`${this.apiUrl}/attendance/monthlyStatus`, {
      headers: this.getAuthHeaders(),
      params: httpParams
    });
  }

  addLeaveType(leaveType: LeaveType): Observable<any> {
    return this.http.post(`${this.apiUrl}/leaveType/add`, leaveType, {
      headers: this.getAuthHeaders(),
    }).pipe(
      catchError((error) => this.handleError(error, 'Adding leave type failed'))
    );
  }

  updateLeaveType(id: string, leaveType: LeaveType): Observable<any> {
    return this.http.put(`${this.apiUrl}/leaveType/update/${id}`, leaveType, {
      headers: this.getAuthHeaders(),
    }).pipe(
      catchError((error) => this.handleError(error, 'Updating leave type failed'))
    );
  }

  getLeaveTypeById(id: string): Observable<LeaveType> {
    return this.http.get<LeaveType>(`${this.apiUrl}/leaveType/get/${id}`, {
      headers: this.getAuthHeaders(),
    }).pipe(
      catchError((error) => this.handleError(error, 'Fetching leave type failed'))
    );
  }

  getAllLeaveTypes(): Observable<LeaveType[]> {
    return this.http.get<LeaveType[]>(`${this.apiUrl}/leaveType/get`, {
      headers: this.getAuthHeaders(),
    }).pipe(
      catchError((error) => this.handleError(error, 'Fetching all leave types failed'))
    );
  }

  deleteLeaveType(id: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/leaveType/delete/${id}`, {
      headers: this.getAuthHeaders(),
    }).pipe(
      catchError((error) => this.handleError(error, 'Deleting leave type failed'))
    );
  }

  assignSalaryStructure(payload: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/salaryStructure/assign`, payload, {
      headers: this.getAuthHeaders()
    }).pipe(
      catchError((error) => this.handleError(error, 'Assigning salary structure failed'))
    );
  }

  getSalaryStructureById(id: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/salaryStructure/get/${id}`, {
      headers: this.getAuthHeaders()
    }).pipe(
      catchError((error) => this.handleError(error, 'Fetching salary structure failed'))
    );
  }

  updateSalaryStructure(id: string, payload: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/salaryStructure/update/${id}`, payload, {
      headers: this.getAuthHeaders()
    }).pipe(
      catchError((error) => this.handleError(error, 'Updating salary structure failed'))
    );
  }

  deleteSalaryStructure(id: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/salaryStructure/update/${id}`, {
      headers: this.getAuthHeaders()
    }).pipe(
      catchError((error) => this.handleError(error, 'Deleting salary structure failed'))
    );
  }

  fetchSalaryStructure(empId: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/salaryStructure/${empId}`);
  }

  addPayroll(payload: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/addPayroll`, payload);
  }

  generatePayslip(empId: string, month: string): Observable<any> {
    const params = new HttpParams().set('month', month);
    return this.http.patch(`${this.apiUrl}/generatePayslip/${empId}`, {}, { params });
  }

  downloadPayslipByAdmin(empId: string, month: string): Observable<Blob> {
    const params = new HttpParams().set('month', month);
    return this.http.get(`${this.apiUrl}/downloadPayslip/${empId}`, { params, responseType: 'blob' });
  }

  downloadPayslipByEmployee(month: string): Observable<Blob> {
    const params = new HttpParams().set('month', month);
    return this.http.get(`${this.apiUrl}/downloadPayslip`, { params, responseType: 'blob' });
  }

  viewPayslipByAdmin(empId: string, month: string): Observable<any> {
    const params = new HttpParams().set('month', month);
    return this.http.get(`${this.apiUrl}/viewPayslip/${empId}`, { params });
  }

  viewPayslipByEmployee(month: string): Observable<any> {
    const params = new HttpParams().set('month', month);
    return this.http.get(`${this.apiUrl}/viewPayslip`, { params });
  }

  getSalaryStructureByEmployeeId(empId: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/salaryStructure/get/by-employee/${empId}`);
  }

  getRole(): string {
    if (!this.isBrowser) return '';
    const token = localStorage.getItem('token');
    if (!token) return '';
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.role || payload.Role || '';
    } catch {
      return '';
    }
  }

}
