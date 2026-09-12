const loginForm = document.getElementById("loginForm");
const errorMessage = document.getElementById("errorMessage");

loginForm.addEventListener("submit", async function (event) {

    event.preventDefault();

    errorMessage.style.display = "none";

    const email =
        document.getElementById("email").value;

    const senha =
        document.getElementById("senha").value;

    try {

        const response = await fetch("/auth/login", {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify({
                email: email,
                senha: senha
            })
        });

        if (!response.ok) {

            errorMessage.textContent =
                "E-mail ou senha inválidos.";

            errorMessage.style.display = "block";

            return;
        }

        const data = await response.json();

        // Salva o JWT
        localStorage.setItem(
            "token",
            data.token
        );

        // Salva o perfil
        localStorage.setItem(
            "tipoUsuario",
            data.tipo
        );

        // Redireciona para o novo caminho
        window.location.href =
            "/web/dashboard";

    } catch (error) {

        errorMessage.textContent =
            "Erro ao conectar com o servidor.";

        errorMessage.style.display = "block";
    }
});