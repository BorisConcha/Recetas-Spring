import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-registro',
  templateUrl: './registro.component.html',
  styleUrls: ['./registro.component.css']
})
export class RegistroComponent {
  registroForm: FormGroup;
  errorMessage: string = '';
  passwordErrors: string[] = [];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registroForm = this.fb.group({
      nombreCompleto: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(200)]],
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(50)]]
    });
  }

  validatePassword(): boolean {
    const password = this.registroForm.get('password')?.value;
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

  onSubmit(): void {
    if (this.registroForm.valid && this.validatePassword()) {
      const { nombreCompleto, username, email, password } = this.registroForm.value;
      this.authService.registro(nombreCompleto, username, email, password).subscribe({
        next: () => {
          this.router.navigate(['/inicio']);
        },
        error: (error) => {
          if (error.error && error.error.errores) {
            this.passwordErrors = error.error.errores;
          } else {
            this.errorMessage = error.error?.error || error.error || 'Error al registrar usuario';
          }
        }
      });
    } else {
      this.validatePassword();
    }
  }
}

