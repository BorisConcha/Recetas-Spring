import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-recuperar-password',
  templateUrl: './recuperar-password.component.html',
  styleUrls: ['./recuperar-password.component.css']
})
export class RecuperarPasswordComponent {
  recuperarForm: FormGroup;
  errorMessage: string = '';
  successMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService
  ) {
    this.recuperarForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  onSubmit(): void {
    if (this.recuperarForm.valid) {
      const email = this.recuperarForm.get('email')?.value;
      this.authService.recuperarPassword(email).subscribe({
        next: (data) => {
          this.successMessage = data.mensaje || 'Si el email existe, se enviará un enlace de recuperación';
          this.errorMessage = '';
        },
        error: (error) => {
          this.errorMessage = error.error || 'Error al procesar la solicitud';
          this.successMessage = '';
        }
      });
    }
  }
}

