const token =
    localStorage.getItem("token");

const tipoUsuario =
    localStorage.getItem("tipoUsuario");

// Se não estiver autenticado
if (!token || !tipoUsuario) {

    window.location.href = "/login";
}

// Exibe o tipo de usuário
const tipoUsuarioElement =
    document.getElementById("tipoUsuario");

if (tipoUsuarioElement) {

    tipoUsuarioElement.textContent =
        tipoUsuario;
}

// Elementos exclusivos do veterinário
const menuTutores =
    document.getElementById("menuTutores");

const menuVeterinarios =
    document.getElementById("menuVeterinarios");

const cardTutores =
    document.getElementById("cardTutores");

const cardVeterinarios =
    document.getElementById("cardVeterinarios");

// Permissões de tela
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

// Logout
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