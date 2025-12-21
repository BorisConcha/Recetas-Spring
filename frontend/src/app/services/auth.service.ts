import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) { }

  login(username: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, { username, password })
      .pipe(tap(response => {
        if (response.token) {
          localStorage.setItem('jwt_token', response.token);
        }
      }));
  }

  registro(nombreCompleto: string, username: string, email: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/registro`, {
      nombreCompleto,
      username,
      email,
      password
    }).pipe(tap(response => {
      if (response.token) {
        localStorage.setItem('jwt_token', response.token);
      }
    }));
  }

  recuperarPassword(email: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/recuperar-password`, { email });
  }

  actualizarPerfil(nombreCompleto: string, email: string): Observable<any> {
    const token = localStorage.getItem('jwt_token');
    return this.http.put<any>(`${this.apiUrl}/perfil`, {
      nombreCompleto,
      email
    }, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
  }

  cambiarPassword(passwordActual: string, nuevaPassword: string): Observable<any> {
    const token = localStorage.getItem('jwt_token');
    return this.http.put<any>(`${this.apiUrl}/cambiar-password`, {
      passwordActual,
      nuevaPassword
    }, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
  }

  obtenerPerfil(): Observable<any> {
    const token = localStorage.getItem('jwt_token');
    return this.http.get<any>(`${this.apiUrl}/perfil`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
  }

  getToken(): string | null {
    return localStorage.getItem('jwt_token');
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    localStorage.removeItem('jwt_token');
  }
}

