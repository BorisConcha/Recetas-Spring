import { Component, OnInit } from '@angular/core';
import { PedidoService } from '../../services/pedido.service';
import { AuthService } from '../../services/auth.service';
import { PagoService } from '../../services/pago.service';

@Component({
  selector: 'app-pedidos',
  templateUrl: './pedidos.component.html',
  styleUrls: ['./pedidos.component.css']
})
export class PedidosComponent implements OnInit {
  pedidos: any[] = [];

  constructor(
    private pedidoService: PedidoService,
    private authService: AuthService,
    private pagoService: PagoService
  ) { }

  ngOnInit(): void {
    if (!this.authService.isAuthenticated()) {
      window.location.href = '/login';
      return;
    }
    this.cargarPedidos();
  }

  cargarPedidos(): void {
    this.pedidoService.obtenerMisPedidos().subscribe({
      next: (data) => {
        this.pedidos = data;
      },
      error: (error) => {
        console.error('Error al cargar pedidos:', error);
      }
    });
  }

  cancelarPedido(id: number): void {
    if (confirm('¿Estás seguro de que deseas cancelar este pedido?')) {
      this.pedidoService.cancelarPedido(id).subscribe({
        next: () => {
          alert('Pedido cancelado correctamente');
          this.cargarPedidos();
        },
        error: (error) => {
          alert('Error al cancelar el pedido: ' + (error.error || error.message));
        }
      });
    }
  }

  simularPago(pedidoId: number): void {
    if (confirm('¿Deseas proceder con el pago de este pedido?')) {
      this.pagoService.simularPago(pedidoId).subscribe({
        next: (data) => {
          alert(`Pago procesado exitosamente!\nNúmero de transacción: ${data.numeroTransaccion}\nMonto: $${data.monto}`);
          this.cargarPedidos();
        },
        error: (error) => {
          alert('Error al procesar el pago: ' + (error.error || error.message));
        }
      });
    }
  }
}

