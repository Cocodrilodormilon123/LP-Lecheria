document.addEventListener('DOMContentLoaded', () => {
    const formLogin = document.getElementById('form-login');
    if (formLogin) {
        formLogin.addEventListener('submit', async (e) => {
            e.preventDefault();

            const datos = new URLSearchParams();
            datos.append('correo', document.getElementById('correo').value);
            datos.append('contrasena', document.getElementById('password').value);

            Swal.fire({
                title: 'Procesando...',
                text: 'Verificando credenciales',
                icon: 'info',
                showConfirmButton: false,
                allowOutsideClick: false,
                didOpen: () => {
                    Swal.showLoading();
                }
            });

            try {
                const respuesta = await fetch('../LoginServlet', {
                    method: 'POST',
                    body: datos,
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
                });

                if (respuesta.status === 200) {
                    const data = await respuesta.json();
                    
                    localStorage.setItem('session_activa', 'true');
                    localStorage.setItem('usuario_nombre', data.nombre);
                    localStorage.setItem('usuario_rol', data.rol);
                    localStorage.setItem('usuario_id', data.id_persona || data.id_usuario);

                    if (data.rol === 'ADMIN') {
                        window.location.href = '../admin/panel.html';
                    } else {
                        window.location.href = '../index.html';
                    }
                } else if (respuesta.status === 401) {
                    Swal.fire('Error', 'Correo o contraseña incorrectos', 'error');
                } else {
                    throw new Error();
                }
            } catch (error) {
                Swal.fire('Error', 'No se pudo conectar con el servidor', 'error');
            }
        });
    }

    const formRegistro = document.getElementById('form-registro');
    if (formRegistro) {
        formRegistro.addEventListener('submit', async (e) => {
            e.preventDefault();

            const datos = new URLSearchParams();
            datos.append('nombre', document.getElementById('nombre').value);
            datos.append('apellido', document.getElementById('apellido').value);
            datos.append('dni', document.getElementById('dni').value);
            datos.append('telefono', document.getElementById('telefono').value);
            datos.append('direccion', document.getElementById('direccion').value);
            datos.append('correo', document.getElementById('correo').value);
            datos.append('contrasena', document.getElementById('password').value);

            Swal.fire({
                title: 'Guardando...',
                text: 'Procesando registro en el sistema',
                icon: 'info',
                showConfirmButton: false,
                allowOutsideClick: false,
                didOpen: () => {
                    Swal.showLoading();
                }
            });

            try {
                const respuesta = await fetch('../RegistroServlet', {
                    method: 'POST',
                    body: datos,
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
                });

                if (respuesta.status === 200) {
                    Swal.fire({
                        title: '¡Registro Exitoso!',
                        text: 'Tu cuenta ha sido creada correctamente.',
                        icon: 'success',
                        confirmButtonColor: '#000000'
                    }).then(() => {
                        window.location.href = 'login.html';
                    });
                } else if (respuesta.status === 409) {
                    Swal.fire('Atención', 'El DNI o Correo ya están registrados', 'warning');
                } else {
                    throw new Error();
                }
            } catch (error) {
                Swal.fire('Error', 'No se pudo completar el registro', 'error');
            }
        });
    }
});