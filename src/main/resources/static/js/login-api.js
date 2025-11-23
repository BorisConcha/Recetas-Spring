document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.querySelector('form[action*="/login"]');
    
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            const urlParams = new URLSearchParams(window.location.search);
            if (urlParams.get('api') === 'true') {
                e.preventDefault();
                loginAPI();
            }
        });
    }
});

function loginAPI() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    
    if (!username || !password) {
        mostrarError('Por favor, completa todos los campos');
        return;
    }
    
    fetch('/api/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            username: username,
            password: password
        })
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(data => {
                throw new Error(data.message || 'Error de autenticación');
            });
        }
        return response.json();
    })
    .then(data => {
        if (data.token) {
            localStorage.setItem('jwt_token', data.token);
            localStorage.setItem('username', data.username);
            
            window.location.href = '/inicio';
        } else {
            mostrarError('No se recibió token de autenticación');
        }
    })
    .catch(error => {
        console.error('Error en login:', error);
        mostrarError(error.message || 'Error al iniciar sesión');
    });
}

function mostrarError(mensaje) {
    let alertContainer = document.querySelector('.alert-container');
    if (!alertContainer) {
        alertContainer = document.createElement('div');
        alertContainer.className = 'alert-container';
        const loginBox = document.querySelector('.login-box');
        if (loginBox) {
            loginBox.insertBefore(alertContainer, loginBox.firstChild);
        }
    }
    
    alertContainer.innerHTML = `
        <div class="alert alert-error">
            ${mensaje}
        </div>
    `;
    
    setTimeout(() => {
        alertContainer.innerHTML = '';
    }, 5000);
}

function verificarToken() {
    const token = localStorage.getItem('jwt_token');
    if (token) {
        fetch('/api/valoraciones/receta/1', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
        .then(response => {
            if (response.status === 401) {
                localStorage.removeItem('jwt_token');
                localStorage.removeItem('username');
            }
        })
        .catch(error => {
            console.error('Error al verificar token:', error);
        });
    }
}

verificarToken();

