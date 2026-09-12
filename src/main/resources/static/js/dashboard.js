const token =
    localStorage.getItem("token");

const tipoUsuario =
    localStorage.getItem("tipoUsuario");


// VERIFICA LOGIN

if (!token || !tipoUsuario) {

    window.location.href = "/login";
}


// MOSTRA PERFIL

const tipoUsuarioElement =
    document.getElementById("tipoUsuario");

if (tipoUsuarioElement) {

    tipoUsuarioElement.textContent =
        tipoUsuario;
}


// CONTROLE DE PERMISSÕES VISUAIS

const menuTutores =
    document.getElementById("menuTutores");

const menuVeterinarios =
    document.getElementById("menuVeterinarios");

const cardTutores =
    document.getElementById("cardTutores");

const cardVeterinarios =
    document.getElementById("cardVeterinarios");


// TUTOR NÃO VÊ ÁREAS ADMINISTRATIVAS

if (tipoUsuario === "TUTOR") {

    if (menuTutores) {
        menuTutores.style.display = "none";
    }

    if (menuVeterinarios) {
        menuVeterinarios.style.display = "none";
    }

    if (cardTutores) {
        cardTutores.style.display = "none";
    }

    if (cardVeterinarios) {
        cardVeterinarios.style.display = "none";
    }
}


// LOGOUT

const logoutButton =
    document.getElementById("logoutButton");

if (logoutButton) {

    logoutButton.addEventListener(
        "click",
        function () {

            localStorage.removeItem("token");
            localStorage.removeItem("tipoUsuario");

            window.location.href = "/login";
        }
    );
}