import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PagoService {
  private apiUrl = 'http://localhost:8080/api/pagos';

  constructor(private http: HttpClient) { }

  simularPago(pedidoId: number): Observable<any> {
    const token = localStorage.getItem('jwt_token');
    return this.http.post<any>(`${this.apiUrl}/simular`, {
      pedidoId: pedidoId
    }, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
  }

  obtenerEstadoPago(pedidoId: number): Observable<any> {
    const token = localStorage.getItem('jwt_token');
    return this.http.get<any>(`${this.apiUrl}/pedido/${pedidoId}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
  }
}

