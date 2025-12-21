import { Component, OnInit } from '@angular/core';
import { ProductoService } from '../../services/producto.service';
import { PedidoService } from '../../services/pedido.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-productos',
  templateUrl: './productos.component.html',
  styleUrls: ['./productos.component.css']
})
export class ProductosComponent implements OnInit {
  productos: any[] = [];
  carrito: any[] = [];
  direccionEntrega: string = '';
  telefonoContacto: string = '';
  mostrarCarrito: boolean = false;

  constructor(
    private productoService: ProductoService,
    private pedidoService: PedidoService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    if (!this.authService.isAuthenticated()) {
      window.location.href = '/login';
      return;
    }
    this.cargarProductos();
  }

  cargarProductos(): void {
    this.productoService.obtenerProductos().subscribe({
      next: (data) => {
        this.productos = data;
      },
      error: (error) => {
        console.error('Error al cargar productos:', error);
      }
    });
  }

  agregarAlCarrito(producto: any): void {
    const item = this.carrito.find(p => p.id === producto.id);
    if (item) {
      item.cantidad++;
    } else {
      this.carrito.push({ ...producto, cantidad: 1 });
    }
  }

  eliminarDelCarrito(productoId: number): void {
    this.carrito = this.carrito.filter(p => p.id !== productoId);
  }

  actualizarCantidad(productoId: number, cantidad: number): void {
    const item = this.carrito.find(p => p.id === productoId);
    if (item) {
      if (cantidad <= 0) {
        this.eliminarDelCarrito(productoId);
      } else {
        item.cantidad = cantidad;
      }
    }
  }

  calcularTotal(): number {
    return this.carrito.reduce((total, item) => total + (item.precio * item.cantidad), 0);
  }

  realizarPedido(): void {
    if (this.carrito.length === 0) {
      alert('El carrito está vacío');
      return;
    }

    if (!this.direccionEntrega || !this.telefonoContacto) {
      alert('Por favor completa la dirección de entrega y teléfono de contacto');
      return;
    }

    const pedido = {
      detalles: this.carrito.map(item => ({
        productoId: item.id,
        cantidad: item.cantidad
      })),
      direccionEntrega: this.direccionEntrega,
      telefonoContacto: this.telefonoContacto
    };

    this.pedidoService.crearPedido(pedido).subscribe({
      next: () => {
        alert('Pedido realizado con éxito');
        this.carrito = [];
        this.mostrarCarrito = false;
        this.direccionEntrega = '';
        this.telefonoContacto = '';
      },
      error: (error) => {
        alert('Error al realizar el pedido: ' + (error.error || error.message));
      }
    });
  }
}

