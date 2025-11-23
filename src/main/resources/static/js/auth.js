function guardarToken(token) {
    localStorage.setItem('jwt_token', token);
}

function obtenerToken() {
    return localStorage.getItem('jwt_token');
}

function eliminarToken() {
    localStorage.removeItem('jwt_token');
}

document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.querySelector('form[action*="/login"]');
    if (loginForm) {
    }
});

function loginAPI(username, password) {
    return fetch('/api/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            username: username,
            password: password
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.token) {
            guardarToken(data.token);
            return data;
        } else {
            throw new Error('No se recibió token');
        }
    });
}

