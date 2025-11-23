const API_BASE_URL = window.location.origin;
let jwtToken = localStorage.getItem('jwt_token') || obtenerTokenDeSesion();
let recetaId = null;

function obtenerTokenDeSesion() {
    return null;
}

document.addEventListener('DOMContentLoaded', function() {
    recetaId = document.getElementById('receta-id')?.value;
    if (!recetaId) {
        console.error('No se encontró el ID de la receta');
        return;
    }
    
    cargarComentarios();
    cargarValoraciones();
    cargarFotos();
    cargarVideos();
    
    configurarEventListeners();
});

function configurarEventListeners() {
    const formComentario = document.getElementById('form-comentario');
    if (formComentario) {
        formComentario.addEventListener('submit', crearComentario);
    }
    
    const estrellas = document.querySelectorAll('.estrella');
    estrellas.forEach((estrella, index) => {
        estrella.addEventListener('click', () => crearValoracion(index + 1));
        estrella.addEventListener('mouseenter', () => resaltarEstrellas(index + 1));
    });
    
    const contenedorEstrellas = document.getElementById('contenedor-estrellas');
    if (contenedorEstrellas) {
        contenedorEstrellas.addEventListener('mouseleave', restaurarEstrellas);
    }
    
    document.getElementById('btn-compartir-facebook')?.addEventListener('click', () => compartir('facebook'));
    document.getElementById('btn-compartir-twitter')?.addEventListener('click', () => compartir('twitter'));
    document.getElementById('btn-compartir-whatsapp')?.addEventListener('click', () => compartir('whatsapp'));
    document.getElementById('btn-copiar-link')?.addEventListener('click', copiarLink);
    
    document.getElementById('input-foto')?.addEventListener('change', (e) => subirFoto(e.target.files[0]));
    document.getElementById('input-video')?.addEventListener('change', (e) => subirVideo(e.target.files[0]));
}

function cargarComentarios() {
    if (!jwtToken) return;
    
    fetch(`${API_BASE_URL}/api/comentarios/receta/${recetaId}`, {
        headers: {
            'Authorization': `Bearer ${jwtToken}`
        }
    })
    .then(response => response.json())
    .then(data => {
        mostrarComentarios(data);
    })
    .catch(error => {
        console.error('Error al cargar comentarios:', error);
    });
}

function crearComentario(e) {
    e.preventDefault();
    if (!jwtToken) {
        alert('Debes iniciar sesión para comentar');
        return;
    }
    
    const comentarioTexto = document.getElementById('comentario-texto').value;
    if (!comentarioTexto.trim()) {
        alert('El comentario no puede estar vacío');
        return;
    }
    
    fetch(`${API_BASE_URL}/api/comentarios`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwtToken}`
        },
        body: JSON.stringify({
            recetaId: parseInt(recetaId),
            comentario: comentarioTexto
        })
    })
    .then(response => {
        if (response.ok) {
            document.getElementById('comentario-texto').value = '';
            cargarComentarios();
        } else {
            alert('Error al crear comentario');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error al crear comentario');
    });
}

function mostrarComentarios(comentarios) {
    const contenedor = document.getElementById('lista-comentarios');
    if (!contenedor) return;
    
    if (comentarios.length === 0) {
        contenedor.innerHTML = '<p>No hay comentarios aún. ¡Sé el primero en comentar!</p>';
        return;
    }
    
    contenedor.innerHTML = comentarios.map(comentario => `
        <div class="comentario-item">
            <div class="comentario-header">
                <strong>${escapeHtml(comentario.usuarioNombre)}</strong>
                <span class="comentario-fecha">${formatearFecha(comentario.fechaCreacion)}</span>
            </div>
            <div class="comentario-texto">${escapeHtml(comentario.comentario)}</div>
        </div>
    `).join('');
}


function cargarValoraciones() {
    if (!jwtToken) return;
    
    fetch(`${API_BASE_URL}/api/valoraciones/receta/${recetaId}`, {
        headers: {
            'Authorization': `Bearer ${jwtToken}`
        }
    })
    .then(response => response.json())
    .then(data => {
        mostrarValoraciones(data);
    })
    .catch(error => {
        console.error('Error al cargar valoraciones:', error);
    });
}

function crearValoracion(puntuacion) {
    if (!jwtToken) {
        alert('Debes iniciar sesión para valorar');
        return;
    }
    
    fetch(`${API_BASE_URL}/api/valoraciones`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwtToken}`
        },
        body: JSON.stringify({
            recetaId: parseInt(recetaId),
            puntuacion: puntuacion
        })
    })
    .then(response => {
        if (response.ok) {
            cargarValoraciones();
        } else {
            alert('Error al guardar valoración');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error al guardar valoración');
    });
}

function mostrarValoraciones(data) {
    const promedio = document.getElementById('valoracion-promedio');
    const total = document.getElementById('valoracion-total');
    
    if (promedio) {
        promedio.textContent = data.promedio ? data.promedio.toFixed(1) : '0.0';
    }
    
    if (total) {
        total.textContent = data.totalValoraciones || 0;
    }
    
    if (data.miValoracion) {
        resaltarEstrellas(data.miValoracion, true);
    }
}

function resaltarEstrellas(numero, permanente = false) {
    const estrellas = document.querySelectorAll('.estrella');
    estrellas.forEach((estrella, index) => {
        if (index < numero) {
            estrella.classList.add('activa');
        } else {
            if (!permanente) {
                estrella.classList.remove('activa');
            }
        }
    });
}

function restaurarEstrellas() {
    const valoracionUsuario = document.getElementById('valoracion-usuario')?.value;
    if (valoracionUsuario) {
        resaltarEstrellas(parseInt(valoracionUsuario), true);
    } else {
        document.querySelectorAll('.estrella').forEach(e => e.classList.remove('activa'));
    }
}


function cargarFotos() {
    if (!jwtToken) return;
    
    fetch(`${API_BASE_URL}/api/recetas/${recetaId}/fotos`, {
        headers: {
            'Authorization': `Bearer ${jwtToken}`
        }
    })
    .then(response => response.json())
    .then(data => {
        mostrarFotos(data);
    })
    .catch(error => {
        console.error('Error al cargar fotos:', error);
    });
}

function cargarVideos() {
    if (!jwtToken) return;
    
    fetch(`${API_BASE_URL}/api/recetas/${recetaId}/videos`, {
        headers: {
            'Authorization': `Bearer ${jwtToken}`
        }
    })
    .then(response => response.json())
    .then(data => {
        mostrarVideos(data);
    })
    .catch(error => {
        console.error('Error al cargar videos:', error);
    });
}

function subirFoto(archivo) {
    if (!archivo || !jwtToken) return;
    
    const formData = new FormData();
    formData.append('file', archivo);
    
    fetch(`${API_BASE_URL}/api/recetas/${recetaId}/fotos`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${jwtToken}`
        },
        body: formData
    })
    .then(response => {
        if (response.ok) {
            cargarFotos();
            document.getElementById('input-foto').value = '';
        } else {
            alert('Error al subir foto');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error al subir foto');
    });
}

function subirVideo(archivo) {
    if (!archivo || !jwtToken) return;
    
    const formData = new FormData();
    formData.append('file', archivo);
    
    fetch(`${API_BASE_URL}/api/recetas/${recetaId}/videos`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${jwtToken}`
        },
        body: formData
    })
    .then(response => {
        if (response.ok) {
            cargarVideos();
            document.getElementById('input-video').value = '';
        } else {
            alert('Error al subir video');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error al subir video');
    });
}

function mostrarFotos(fotos) {
    const contenedor = document.getElementById('galeria-fotos');
    if (!contenedor) return;
    
    if (fotos.length === 0) {
        contenedor.innerHTML = '<p>No hay fotos aún.</p>';
        return;
    }
    
    contenedor.innerHTML = fotos.map(foto => `
        <div class="foto-item">
            <img src="${API_BASE_URL}${foto.urlFoto}" alt="${foto.nombreArchivo || 'Foto'}">
            ${foto.esPrincipal ? '<span class="badge-principal">Principal</span>' : ''}
        </div>
    `).join('');
}

function mostrarVideos(videos) {
    const contenedor = document.getElementById('galeria-videos');
    if (!contenedor) return;
    
    if (videos.length === 0) {
        contenedor.innerHTML = '<p>No hay videos aún.</p>';
        return;
    }
    
    contenedor.innerHTML = videos.map(video => `
        <div class="video-item">
            <video controls>
                <source src="${API_BASE_URL}${video.urlVideo}" type="${video.tipoArchivo || 'video/mp4'}">
                Tu navegador no soporta el elemento video.
            </video>
        </div>
    `).join('');
}


function compartir(plataforma) {
    if (!jwtToken) {
        alert('Debes iniciar sesión para compartir');
        return;
    }
    
    fetch(`${API_BASE_URL}/api/compartir`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwtToken}`
        },
        body: JSON.stringify({
            recetaId: parseInt(recetaId),
            plataforma: plataforma
        })
    }).catch(error => console.error('Error al registrar compartido:', error));
    
    fetch(`${API_BASE_URL}/api/compartir/receta/${recetaId}/link`, {
        headers: {
            'Authorization': `Bearer ${jwtToken}`
        }
    })
    .then(response => response.json())
    .then(data => {
        const link = data.link;
        let urlCompartir = '';
        
        switch(plataforma) {
            case 'facebook':
                urlCompartir = data.facebook || `https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(link)}`;
                break;
            case 'twitter':
                urlCompartir = data.twitter || `https://twitter.com/intent/tweet?url=${encodeURIComponent(link)}`;
                break;
            case 'whatsapp':
                urlCompartir = data.whatsapp || `https://wa.me/?text=${encodeURIComponent(link)}`;
                break;
        }
        
        if (urlCompartir) {
            window.open(urlCompartir, '_blank', 'width=600,height=400');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error al compartir');
    });
}

function copiarLink() {
    if (!jwtToken) {
        alert('Debes iniciar sesión');
        return;
    }
    
    fetch(`${API_BASE_URL}/api/compartir/receta/${recetaId}/link`, {
        headers: {
            'Authorization': `Bearer ${jwtToken}`
        }
    })
    .then(response => response.json())
    .then(data => {
        navigator.clipboard.writeText(data.link).then(() => {
            alert('Link copiado al portapapeles');
        });
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error al copiar link');
    });
}


function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function formatearFecha(fechaString) {
    const fecha = new Date(fechaString);
    return fecha.toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

