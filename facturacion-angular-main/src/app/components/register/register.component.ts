import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LoginService } from 'src/app/service/login.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  registerForm: FormGroup;
  mensajeRegistro: string = '';

  constructor(
    private formBuilder: FormBuilder,
    private loginService: LoginService
  ) {
    this.registerForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(4)]],
      password: ['', [Validators.required, Validators.minLength(4)]]
    });
  }

  register(): void {
    if (this.registerForm.valid) {
      this.loginService.register(this.registerForm.value).subscribe({
        next: (response) => {
          this.mensajeRegistro = 'Usuario registrado exitosamente';
          this.registerForm.reset();
        },
        error: (error) => {
          this.mensajeRegistro = 'Error al registrar usuario';
        }
      });
    }
  }
}
