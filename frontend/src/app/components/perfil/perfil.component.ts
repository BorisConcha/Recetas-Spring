import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-perfil',
  templateUrl: './perfil.component.html',
  styleUrls: ['./perfil.component.css']
})
export class PerfilComponent implements OnInit {
  perfilForm: FormGroup;
  passwordForm: FormGroup;
  errorMessage: string = '';
  successMessage: string = '';
  passwordErrors: string[] = [];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService
  ) {
    this.perfilForm = this.fb.group({
      nombreCompleto: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(200)]],
      email: ['', [Validators.required, Validators.email]]
    });

    this.passwordForm = this.fb.group({
      passwordActual: ['', [Validators.required]],
      nuevaPassword: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(50)]]
    });
  }

  ngOnInit(): void {
    if (!this.authService.isAuthenticated()) {
      window.location.href = '/login';
      return;
    }
    this.cargarPerfil();
  }

  cargarPerfil(): void {
    this.authService.obtenerPerfil().subscribe({
      next: (data) => {
        this.perfilForm.patchValue({
          nombreCompleto: data.nombreCompleto,
          email: data.email
        });
      },
      error: (error) => {
        console.error('Error al cargar el perfil:', error);
        this.errorMessage = 'Error al cargar los datos del perfil';
      }
    });
  }

  validatePassword(): boolean {
    const password = this.passwordForm.get('nuevaPassword')?.value;
    this.passwordErrors = [];
    
    if (!password) return false;
    
    if (password.length < 8) {
      this.passwordErrors.push('La contraseña debe tener al menos 8 caracteres');
    }
    if (password.length > 50) {
      this.passwordErrors.push('La contraseña no puede tener más de 50 caracteres');
    }
    if (!/[A-Z]/.test(password)) {
      this.passwordErrors.push('La contraseña debe contener al menos una letra mayúscula');
    }
    if (!/[a-z]/.test(password)) {
      this.passwordErrors.push('La contraseña debe contener al menos una letra minúscula');
    }
    if (!/[0-9]/.test(password)) {
      this.passwordErrors.push('La contraseña debe contener al menos un número');
    }
    if (!/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>/?]/.test(password)) {
      this.passwordErrors.push('La contraseña debe contener al menos un carácter especial');
    }
    
    return this.passwordErrors.length === 0;
  }

  actualizarPerfil(): void {
    if (this.perfilForm.valid) {
      const { nombreCompleto, email } = this.perfilForm.value;
      this.authService.actualizarPerfil(nombreCompleto, email).subscribe({
        next: (data) => {
          this.successMessage = data.mensaje || 'Perfil actualizado correctamente';
          this.errorMessage = '';
        },
        error: (error) => {
          this.errorMessage = error.error || 'Error al actualizar el perfil';
          this.successMessage = '';
        }
      });
    }
  }

  cambiarPassword(): void {
    if (this.passwordForm.valid && this.validatePassword()) {
      const { passwordActual, nuevaPassword } = this.passwordForm.value;
      this.authService.cambiarPassword(passwordActual, nuevaPassword).subscribe({
        next: (data) => {
          this.successMessage = data.mensaje || 'Contraseña actualizada correctamente';
          this.errorMessage = '';
          this.passwordForm.reset();
        },
        error: (error) => {
          if (error.error && error.error.errores) {
            this.passwordErrors = error.error.errores;
          } else {
            this.errorMessage = error.error?.error || error.error || 'Error al cambiar la contraseña';
          }
          this.successMessage = '';
        }
      });
    } else {
      this.validatePassword();
    }
  }
}

