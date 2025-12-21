import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PedidoService {
  private apiUrl = 'http://localhost:8080/api/pedidos';

  constructor(private http: HttpClient) { }

  obtenerMisPedidos(): Observable<any[]> {
    const token = localStorage.getItem('jwt_token');
    return this.http.get<any[]>(this.apiUrl, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
  }

  obtenerPedidoPorId(id: number): Observable<any> {
    const token = localStorage.getItem('jwt_token');
    return this.http.get<any>(`${this.apiUrl}/${id}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
  }

  crearPedido(pedido: any): Observable<any> {
    const token = localStorage.getItem('jwt_token');
    return this.http.post<any>(this.apiUrl, pedido, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
  }

  cancelarPedido(id: number): Observable<any> {
    const token = localStorage.getItem('jwt_token');
    return this.http.put<any>(`${this.apiUrl}/${id}/cancelar`, {}, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
  }
}

