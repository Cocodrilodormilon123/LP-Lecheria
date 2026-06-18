document.addEventListener('DOMContentLoaded', () => {
    const gMenu = document.getElementById('guest-menu');
    const uMenu = document.getElementById('user-menu');
    const uNameDisplay = document.getElementById('display-user-name');
    const triggerLogout = document.getElementById('logout-trigger');

    const activeUser = localStorage.getItem('usuario_nombre');

    if (activeUser) {
        if (gMenu) gMenu.classList.add('d-none');
        if (uMenu) uMenu.classList.remove('d-none');
        if (uNameDisplay) uNameDisplay.textContent = activeUser.toUpperCase();
    } else {
        if (gMenu) gMenu.classList.remove('d-none');
        if (uMenu) uMenu.classList.add('d-none');
    }

    if (triggerLogout) {
        triggerLogout.addEventListener('click', (e) => {
            e.preventDefault();
            Swal.fire({
                title: '¿CERRAR SESIÓN?',
                text: "Esperamos verte pronto por la lechería.",
                icon: 'question',
                showCancelButton: true,
                confirmButtonColor: '#000000',
                cancelButtonColor: '#d33',
                confirmButtonText: 'SALIR',
                cancelButtonText: 'CANCELAR'
            }).then((res) => {
                if (res.isConfirmed) {
                    localStorage.clear();
                    window.location.reload();
                }
            });
        });
    }

    actualizarCarritoUI();
});

let carrito = JSON.parse(localStorage.getItem('lecheria_carrito')) || [];

window.agregarAlCarrito = (id, nombre, precio, imagen) => {
    const itemExistente = carrito.find(item => item.id === id);

    if (itemExistente) {
        itemExistente.cantidad += 1;
    } else {
        carrito.push({ id, nombre, precio, imagen, cantidad: 1 });
    }

    localStorage.setItem('lecheria_carrito', JSON.stringify(carrito));
    
    Swal.fire({
        toast: true,
        position: 'top-end',
        icon: 'success',
        title: `${nombre} añadido al carrito`,
        showConfirmButton: false,
        timer: 1500
    });

    actualizarCarritoUI();
    
    const offcanvasElement = document.getElementById('offcanvasCart');
    if (offcanvasElement) {
        bootstrap.Offcanvas.getOrCreateInstance(offcanvasElement).show();
    }
};

window.eliminarDelCarrito = (id) => {
    carrito = carrito.filter(item => item.id !== id);
    localStorage.setItem('lecheria_carrito', JSON.stringify(carrito));
    
    Swal.fire({
        toast: true,
        position: 'top-end',
        icon: 'error',
        title: 'Producto retirado',
        showConfirmButton: false,
        timer: 1500
    });
    
    actualizarCarritoUI();
};

window.actualizarCantidad = (id, cambio) => {
    const item = carrito.find(item => item.id === id);
    if (item) {
        item.cantidad += cambio;
        if (item.cantidad <= 0) {
            eliminarDelCarrito(id);
        } else {
            localStorage.setItem('lecheria_carrito', JSON.stringify(carrito));
            actualizarCarritoUI();
        }
    }
};

window.actualizarCarritoUI = () => {
    const contenedor = document.getElementById('cart-items-container');
    const badge = document.getElementById('cart-badge');
    const totalElement = document.getElementById('cart-total');
    const btnProcesar = document.getElementById('btn-procesar-pago');
    
    if (!contenedor) return; 

    contenedor.innerHTML = '';
    let total = 0;
    let cantidadTotal = 0;

    if (carrito.length === 0) {
        contenedor.innerHTML = '<div class="text-center text-muted mt-5"><i class="fa-solid fa-basket-shopping fs-1 mb-3"></i><p>Tu carrito está vacío</p></div>';
        if (badge) badge.textContent = '0';
        if (totalElement) totalElement.textContent = 'S/ 0.00';
        if (btnProcesar) btnProcesar.disabled = true;
        return;
    }

    if (btnProcesar) btnProcesar.disabled = false;

    carrito.forEach(item => {
        const subtotal = item.precio * item.cantidad;
        total += subtotal;
        cantidadTotal += item.cantidad;

        const pathBase = window.location.pathname.includes('/cliente/') || window.location.pathname.includes('/auth/') ? '../assets/img/productos/' : 'assets/img/productos/';

        contenedor.innerHTML += `
            <div class="d-flex align-items-center mb-3 pb-3 border-bottom border-secondary opacity-75">
                <img src="${pathBase}${item.imagen}" class="border border-dark object-fit-cover me-3" width="60" height="60" style="border-radius: 5px;">
                <div class="flex-grow-1">
                    <h6 class="fw-bold mb-0 text-uppercase" style="font-size: 0.9rem;">${item.nombre}</h6>
                    <div class="text-success fw-bold small">S/ ${item.precio.toFixed(2)}</div>
                    
                    <div class="d-flex align-items-center mt-2">
                        <button class="btn btn-sm btn-outline-dark px-2 py-0 rounded-0" onclick="actualizarCantidad('${item.id}', -1)">-</button>
                        <span class="mx-2 fw-bold">${item.cantidad}</span>
                        <button class="btn btn-sm btn-outline-dark px-2 py-0 rounded-0" onclick="actualizarCantidad('${item.id}', 1)">+</button>
                    </div>
                </div>
                <button class="btn btn-link text-danger p-0 ms-2" onclick="eliminarDelCarrito('${item.id}')"><i class="fa-solid fa-trash-can"></i></button>
            </div>
        `;
    });

    if (badge) badge.textContent = cantidadTotal;
    if (totalElement) totalElement.textContent = `S/ ${total.toFixed(2)}`;
};

window.irAlCarrito = () => {
    let activeUser = localStorage.getItem('usuario_nombre');
    let idUsuario = localStorage.getItem('usuario_id'); 
    const isSubfolder = window.location.pathname.includes('/cliente/') || window.location.pathname.includes('/auth/');

    if (!activeUser || !idUsuario) {
        const offcanvasElement = document.getElementById('offcanvasCart');
        if (offcanvasElement) bootstrap.Offcanvas.getOrCreateInstance(offcanvasElement).hide();
        Swal.fire({
            title: '¡Inicia sesión!',
            text: "Para procesar tu compra y asignar el pedido a tu nombre, debes ingresar a tu cuenta.",
            icon: 'info',
            confirmButtonColor: '#000',
            confirmButtonText: 'IR AL LOGIN'
        }).then(() => {
            window.location.href = isSubfolder ? '../auth/login.html' : 'auth/login.html';
        });
        return;
    }

    window.location.href = isSubfolder ? '../carrito.html' : 'carrito.html';
};

window.procesarPago = async () => {
    let activeUser = localStorage.getItem('usuario_nombre');
    let idUsuario = localStorage.getItem('usuario_id'); 
    const fileInput = document.getElementById('yape-comprobante');

    if (!activeUser || !idUsuario) { window.location.href = 'auth/login.html'; return; }
    if (carrito.length === 0) { Swal.fire('Carrito vacío', 'No tienes productos para procesar.', 'warning'); return; }

    Swal.fire({
        title: '¿Confirmar Compra?',
        text: "Se registrará tu orden en el sistema de La Lechería.",
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#000000',
        cancelButtonColor: '#d33',
        confirmButtonText: 'SÍ, CONFIRMAR',
        cancelButtonText: 'CANCELAR'
    }).then(async (result) => {
        if (result.isConfirmed) {
            Swal.fire({ title: 'Registrando orden...', didOpen: () => Swal.showLoading() });
            
            try {
                const infoPedido = {
                    id_usuario: idUsuario,
                    tipo_entrega: localStorage.getItem('tmp_tipo_entrega') || 'RECOJO',
                    direccion: localStorage.getItem('tmp_direccion') || '',
                    referencia: localStorage.getItem('tmp_referencia') || '',
                    telefono: localStorage.getItem('tmp_telefono') || '',
                    destinatario_nombre: localStorage.getItem('tmp_destinatario') || activeUser,
                    total: localStorage.getItem('total_final') || 0,
                    estado: localStorage.getItem('tmp_estado_inicial') || 'PENDIENTE_PAGO',
                    metodo_pago: localStorage.getItem('tmp_metodo_pago') || 'TIENDA',
                    items: carrito
                };

                let response;
                if (fileInput && fileInput.files.length > 0) {
                    const formData = new FormData();
                    formData.append("items", JSON.stringify(infoPedido));
                    formData.append("comprobante", fileInput.files[0]);
                    response = await fetch(window.location.pathname.includes('/cliente/') ? '../PedidoServlet' : 'PedidoServlet', {
                        method: 'POST',
                        body: formData
                    });
                } else {
                    response = await fetch(window.location.pathname.includes('/cliente/') ? '../PedidoServlet' : 'PedidoServlet', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json; charset=UTF-8' },
                        body: JSON.stringify(infoPedido)
                    });
                }

                if (response.ok) {
                    const keys = ['lecheria_carrito', 'total_final', 'descuento_aplicado', 'tmp_estado_inicial', 'tmp_metodo_pago', 'tmp_tipo_entrega', 'tmp_direccion', 'tmp_referencia', 'tmp_telefono', 'tmp_destinatario'];
                    keys.forEach(k => localStorage.removeItem(k));
                    carrito = [];
                    actualizarCarritoUI();
                    Swal.fire('¡Éxito!', 'Tu pedido ha sido registrado correctamente.', 'success').then(() => {
                        window.location.href = 'index.html';
                    });
                } else {
                    throw new Error();
                }
            } catch (error) {
                Swal.fire('Error', 'No se pudo procesar la transacción.', 'error');
            }
        }
    });
};