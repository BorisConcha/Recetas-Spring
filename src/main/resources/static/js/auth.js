// Almacenamiento en memoria (más seguro que localStorage)
let tokenEnMemoria = null;

function guardarToken(token) {
    tokenEnMemoria = token;
    // Nota: Para producción, el servidor debe enviar el token como cookie HttpOnly
    console.info('Token almacenado en memoria de forma segura');
}

function obtenerToken() {
    return tokenEnMemoria;
}

function eliminarToken() {
    tokenEnMemoria = null;
}

// Función auxiliar para manejar respuestas de API
async function manejarRespuesta(response) {
    if (!response.ok) {
        const contentType = response.headers.get('content-type');
        let errorMsg;
        
        if (contentType && contentType.includes('application/json')) {
            const data = await response.json();
            errorMsg = data.error || data.mensaje || 'Error en la solicitud';
        } else {
            errorMsg = await response.text();
        }
        
        throw new Error(errorMsg);
    }
    
    return response.json();
}

// API de Login
async function loginAPI(username, password) {
    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });
        
        const data = await manejarRespuesta(response);
        
        if (data.token) {
            guardarToken(data.token);
            return data;
        }
        
        throw new Error('No se recibió token de autenticación');
    } catch (error) {
        console.error('Error en login:', error.message);
        throw error;
    }
}

// API de Registro
async function registroAPI(nombreCompleto, username, email, password) {
    try {
        const response = await fetch('/api/auth/registro', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                nombreCompleto,
                username,
                email,
                password
            })
        });
        
        const data = await manejarRespuesta(response);
        
        if (data.token) {
            guardarToken(data.token);
            return data;
        }
        
        throw new Error('No se recibió token de autenticación');
    } catch (error) {
        console.error('Error en registro:', error.message);
        throw error;
    }
}

// API de Recuperar Password
async function recuperarPasswordAPI(email) {
    try {
        const response = await fetch('/api/auth/recuperar-password', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email })
        });
        
        return await manejarRespuesta(response);
    } catch (error) {
        console.error('Error en recuperación:', error.message);
        throw error;
    }
}

// API de Obtener Perfil
async function obtenerPerfilAPI() {
    const token = obtenerToken();
    
    if (!token) {
        throw new Error('No hay sesión activa');
    }
    
    try {
        const response = await fetch('/api/auth/perfil', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        return await manejarRespuesta(response);
    } catch (error) {
        console.error('Error al obtener perfil:', error.message);
        throw error;
    }
}

// API de Actualizar Perfil
async function actualizarPerfilAPI(nombreCompleto, email) {
    const token = obtenerToken();
    
    if (!token) {
        throw new Error('No hay sesión activa');
    }
    
    try {
        const response = await fetch('/api/auth/perfil', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ nombreCompleto, email })
        });
        
        return await manejarRespuesta(response);
    } catch (error) {
        console.error('Error al actualizar perfil:', error.message);
        throw error;
    }
}

// API de Cambiar Password
async function cambiarPasswordAPI(passwordActual, nuevaPassword) {
    const token = obtenerToken();
    
    if (!token) {
        throw new Error('No hay sesión activa');
    }
    
    try {
        const response = await fetch('/api/auth/cambiar-password', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ passwordActual, nuevaPassword })
        });
        
        return await manejarRespuesta(response);
    } catch (error) {
        console.error('Error al cambiar password:', error.message);
        throw error;
    }
}

// Función de Logout
function logout() {
    eliminarToken();
    // Usar replace en lugar de href para evitar historial
    window.location.replace('/login');
}

// Función para mostrar mensajes
function mostrarMensaje(elementoId, mensaje, esError = false) {
    const elemento = document.getElementById(elementoId);
    if (elemento) {
        elemento.textContent = mensaje;
        elemento.style.display = 'block';
    }
}

function ocultarMensajes() {
    const errorDiv = document.getElementById('error-message');
    const successDiv = document.getElementById('success-message');
    
    if (errorDiv) errorDiv.style.display = 'none';
    if (successDiv) successDiv.style.display = 'none';
}

// Cargar datos del perfil
async function loadProfileData() {
    const token = obtenerToken();
    
    if (!token) {
        window.location.replace('/login');
        return;
    }
    
    try {
        const data = await obtenerPerfilAPI();
        
        const nombreCompletoInput = document.getElementById('nombreCompleto');
        const emailInput = document.getElementById('email');
        
        if (nombreCompletoInput && data.nombreCompleto) {
            nombreCompletoInput.value = data.nombreCompleto;
        }
        
        if (emailInput && data.email) {
            emailInput.value = data.email;
        }
    } catch (error) {
        console.error('Error al cargar el perfil:', error.message);
        
        if (error.message.includes('No hay sesión activa') || 
            error.message.includes('No autorizado')) {
            window.location.replace('/login');
        }
    }
}

// Manejo de formularios
document.addEventListener('DOMContentLoaded', function() {
    // Formulario de registro
    const registroForm = document.getElementById('registro-form');
    if (registroForm) {
        registroForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            ocultarMensajes();
            
            const nombreCompleto = document.getElementById('nombreCompleto').value;
            const username = document.getElementById('username').value;
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            
            try {
                await registroAPI(nombreCompleto, username, email, password);
                mostrarMensaje('success-message', 'Registro exitoso. Redirigiendo...');
                
                setTimeout(() => {
                    window.location.replace('/inicio');
                }, 1500);
            } catch (error) {
                mostrarMensaje('error-message', error.message);
            }
        });
    }
    
    // Formulario de recuperar contraseña
    const recuperarForm = document.getElementById('recuperar-password-form');
    if (recuperarForm) {
        recuperarForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            ocultarMensajes();
            
            const email = document.getElementById('email').value;
            
            try {
                const data = await recuperarPasswordAPI(email);
                const mensaje = data.mensaje || 
                    'Si el email existe, se enviará un enlace de recuperación';
                mostrarMensaje('success-message', mensaje);
            } catch (error) {
                mostrarMensaje('error-message', error.message);
            }
        });
    }
    
    // Formulario de actualizar perfil
    const actualizarPerfilForm = document.getElementById('actualizar-perfil-form');
    if (actualizarPerfilForm) {
        actualizarPerfilForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            ocultarMensajes();
            
            const nombreCompleto = document.getElementById('nombreCompleto').value;
            const email = document.getElementById('email').value;
            
            try {
                const data = await actualizarPerfilAPI(nombreCompleto, email);
                const mensaje = data.mensaje || 'Perfil actualizado correctamente';
                mostrarMensaje('success-message', mensaje);
            } catch (error) {
                mostrarMensaje('error-message', error.message);
            }
        });
    }
    
    // Formulario de cambiar contraseña
    const cambiarPasswordForm = document.getElementById('cambiar-password-form');
    if (cambiarPasswordForm) {
        cambiarPasswordForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            ocultarMensajes();
            
            const passwordActual = document.getElementById('passwordActual').value;
            const nuevaPassword = document.getElementById('nuevaPassword').value;
            
            try {
                const data = await cambiarPasswordAPI(passwordActual, nuevaPassword);
                const mensaje = data.mensaje || 'Contraseña actualizada correctamente';
                mostrarMensaje('success-message', mensaje);
                cambiarPasswordForm.reset();
            } catch (error) {
                mostrarMensaje('error-message', error.message);
            }
        });
    }
});