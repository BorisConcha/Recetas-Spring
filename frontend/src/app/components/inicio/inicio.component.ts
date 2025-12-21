import { Component, OnInit } from '@angular/core';
import { ProductoService } from '../../services/producto.service';

@Component({
  selector: 'app-inicio',
  templateUrl: './inicio.component.html',
  styleUrls: ['./inicio.component.css']
})
export class InicioComponent implements OnInit {
  productos: any[] = [];
  productosRecientes: any[] = [];

  constructor(private productoService: ProductoService) { }

  ngOnInit(): void {
    this.cargarProductos();
    this.cargarProductosRecientes();
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

  cargarProductosRecientes(): void {
    this.productoService.obtenerProductosRecientes().subscribe({
      next: (data) => {
        this.productosRecientes = data;
      },
      error: (error) => {
        console.error('Error al cargar productos recientes:', error);
      }
    });
  }
}

