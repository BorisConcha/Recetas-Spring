import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  template: `
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
      <div class="container">
        <a class="navbar-brand" routerLink="/inicio">🛒 Tienda Online</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav ms-auto">
            <li class="nav-item">
              <a class="nav-link" routerLink="/inicio" routerLinkActive="active">Inicio</a>
            </li>
            <li class="nav-item" *ngIf="isAuthenticated()">
              <a class="nav-link" routerLink="/productos" routerLinkActive="active">Productos</a>
            </li>
            <li class="nav-item" *ngIf="isAuthenticated()">
              <a class="nav-link" routerLink="/pedidos" routerLinkActive="active">Mis Pedidos</a>
            </li>
            <li class="nav-item" *ngIf="isAuthenticated()">
              <a class="nav-link" routerLink="/perfil" routerLinkActive="active">Mi Perfil</a>
            </li>
            <li class="nav-item" *ngIf="!isAuthenticated()">
              <a class="nav-link" routerLink="/login" routerLinkActive="active">Iniciar Sesión</a>
            </li>
            <li class="nav-item" *ngIf="isAuthenticated()">
              <a class="nav-link" (click)="logout()" style="cursor: pointer;">Cerrar Sesión</a>
            </li>
          </ul>
        </div>
      </div>
    </nav>
    <router-outlet></router-outlet>
  `,
  styles: []
})
export class AppComponent {
  title = 'Tienda Online';

  isAuthenticated(): boolean {
    return !!localStorage.getItem('jwt_token');
  }

  logout(): void {
    localStorage.removeItem('jwt_token');
    window.location.href = '/login';
  }
}

