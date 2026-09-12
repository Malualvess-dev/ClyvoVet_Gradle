// =============================================
// ELEMENTOS DA TELA
// =============================================

const tabLogin =
    document.getElementById("tabLogin");

const tabCadastro =
    document.getElementById("tabCadastro");

const loginSection =
    document.getElementById("loginSection");

const cadastroSection =
    document.getElementById("cadastroSection");

const abrirCadastro =
    document.getElementById("abrirCadastro");

const abrirLogin =
    document.getElementById("abrirLogin");


// =============================================
// LOGIN
// =============================================

const loginForm =
    document.getElementById("loginForm");

const emailInput =
    document.getElementById("email");

const senhaInput =
    document.getElementById("senha");

const errorMessage =
    document.getElementById("errorMessage");


// =============================================
// TOKEN
// =============================================

const tokenArea =
    document.getElementById("tokenArea");

const tokenGerado =
    document.getElementById("tokenGerado");

const tokenInformado =
    document.getElementById("tokenInformado");

const copiarToken =
    document.getElementById("copiarToken");

const validarToken =
    document.getElementById("validarToken");

const entrarSistema =
    document.getElementById("entrarSistema");

const tokenMessage =
    document.getElementById("tokenMessage");


// =============================================
// CADASTRO
// =============================================

const cadastroForm =
    document.getElementById("cadastroForm");

const cadastroEmailInput =
    document.getElementById("cadastroEmail");

const cadastroSenhaInput =
    document.getElementById("cadastroSenha");

const confirmarSenhaInput =
    document.getElementById("confirmarSenha");

const tipoUserInput =
    document.getElementById("tipoUser");

const cadastroMessage =
    document.getElementById("cadastroMessage");


// =============================================
// MOSTRAR LOGIN
// =============================================

function mostrarLogin() {

    tabLogin.classList.add("active");
    tabCadastro.classList.remove("active");

    loginSection.classList.add("active");
    cadastroSection.classList.remove("active");

    errorMessage.textContent = "";
    cadastroMessage.textContent = "";
}


// =============================================
// MOSTRAR CADASTRO
// =============================================

function mostrarCadastro() {

    tabCadastro.classList.add("active");
    tabLogin.classList.remove("active");

    cadastroSection.classList.add("active");
    loginSection.classList.remove("active");

    errorMessage.textContent = "";
    cadastroMessage.textContent = "";

    tokenArea.style.display = "none";
}


// =============================================
// ABAS
// =============================================

tabLogin.addEventListener(
    "click",
    mostrarLogin
);

tabCadastro.addEventListener(
    "click",
    mostrarCadastro
);

abrirCadastro.addEventListener(
    "click",
    mostrarCadastro
);

abrirLogin.addEventListener(
    "click",
    mostrarLogin
);


// =============================================
// LOGIN / GERAR TOKEN
// =============================================

loginForm.addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();

        errorMessage.textContent = "";
        errorMessage.className = "message";

        tokenMessage.textContent = "";
        tokenMessage.className = "message";

        tokenArea.style.display = "none";

        entrarSistema.disabled = true;

        const email =
            emailInput.value.trim();

        const senha =
            senhaInput.value;


        try {

            const response =
                await fetch(
                    "/auth/login",
                    {
                        method: "POST",

                        headers: {
                            "Content-Type":
                                "application/json"
                        },

                        body:
                            JSON.stringify({
                                email: email,
                                senha: senha
                            })
                    }
                );


            if (!response.ok) {

                errorMessage.textContent =
                    "E-mail ou senha inválidos.";

                errorMessage.className =
                    "message error";

                return;
            }


            const data =
                await response.json();


            // GUARDA O TOKEN GERADO

            localStorage.setItem(
                "token",
                data.token
            );


            // GUARDA PERFIL

            localStorage.setItem(
                "tipoUsuario",
                data.tipo
            );


            // EXIBE TOKEN

            tokenGerado.value =
                data.token;

            tokenInformado.value =
                "";

            tokenArea.style.display =
                "block";


            errorMessage.textContent =
                "Login realizado com sucesso.";

            errorMessage.className =
                "message success";


        } catch (error) {

            console.error(error);

            errorMessage.textContent =
                "Erro ao conectar com o servidor.";

            errorMessage.className =
                "message error";
        }
    }
);


// =============================================
// COPIAR TOKEN
// =============================================

copiarToken.addEventListener(
    "click",
    async function () {

        const token =
            tokenGerado.value.trim();


        if (!token) {
            return;
        }


        try {

            await navigator.clipboard.writeText(
                token
            );


            copiarToken.textContent =
                "Token copiado!";


            setTimeout(
                function () {

                    copiarToken.textContent =
                        "Copiar token";

                },
                1500
            );


        } catch (error) {

            console.error(error);

            tokenGerado.select();

            document.execCommand(
                "copy"
            );

            copiarToken.textContent =
                "Token copiado!";


            setTimeout(
                function () {

                    copiarToken.textContent =
                        "Copiar token";

                },
                1500
            );
        }
    }
);


// =============================================
// VALIDAR TOKEN INFORMADO
// =============================================

validarToken.addEventListener(
    "click",
    function () {

        const tokenOriginal =
            localStorage.getItem("token");

        const tokenDigitado =
            tokenInformado.value.trim();


        tokenMessage.textContent = "";

        tokenMessage.className =
            "message";


        entrarSistema.disabled =
            true;


        if (!tokenDigitado) {

            tokenMessage.textContent =
                "Cole o token JWT no campo acima.";

            tokenMessage.className =
                "message error";

            return;
        }


        if (
            tokenDigitado !==
            tokenOriginal
        ) {

            tokenMessage.textContent =
                "Token inválido. Verifique o token informado.";

            tokenMessage.className =
                "message error";

            return;
        }


        tokenMessage.textContent =
            "Token válido. Acesso autorizado.";

        tokenMessage.className =
            "message success";


        entrarSistema.disabled =
            false;
    }
);


// =============================================
// SE ALTERAR O TOKEN DEPOIS DE VALIDAR
// BLOQUEIA NOVAMENTE
// =============================================

tokenInformado.addEventListener(
    "input",
    function () {

        entrarSistema.disabled =
            true;

        tokenMessage.textContent =
            "";
    }
);


// =============================================
// ENTRAR NO SISTEMA
// =============================================

entrarSistema.addEventListener(
    "click",
    function () {

        const tokenOriginal =
            localStorage.getItem("token");

        const tokenDigitado =
            tokenInformado.value.trim();


        if (
            !tokenOriginal ||
            tokenDigitado !== tokenOriginal
        ) {

            tokenMessage.textContent =
                "Token inválido. Valide novamente.";

            tokenMessage.className =
                "message error";

            entrarSistema.disabled =
                true;

            return;
        }


        window.location.href =
            "/web/dashboard";
    }
);


// =============================================
// CADASTRO
// =============================================

cadastroForm.addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();


        cadastroMessage.textContent =
            "";

        cadastroMessage.className =
            "message";


        const email =
            cadastroEmailInput.value.trim();

        const senha =
            cadastroSenhaInput.value;

        const confirmarSenha =
            confirmarSenhaInput.value;

        const tipoUser =
            tipoUserInput.value;


        // =====================================
        // CONFIRMAR SENHA
        // =====================================

        if (
            senha !==
            confirmarSenha
        ) {

            cadastroMessage.textContent =
                "As senhas não são iguais.";

            cadastroMessage.className =
                "message error";

            return;
        }


        // =====================================
        // VALIDAR SENHA
        // =====================================

        const senhaValida =
            /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d).+$/;


        if (
            !senhaValida.test(
                senha
            )
        ) {

            cadastroMessage.textContent =
                "A senha deve conter letra maiúscula, minúscula e número.";

            cadastroMessage.className =
                "message error";

            return;
        }


        const usuarioRequest = {

            email: email,

            senha: senha,

            tipoUser: tipoUser
        };


        try {

            const response =
                await fetch(
                    "/usuarios",
                    {
                        method: "POST",

                        headers: {
                            "Content-Type":
                                "application/json"
                        },

                        body:
                            JSON.stringify(
                                usuarioRequest
                            )
                    }
                );


            if (!response.ok) {

                let mensagem =
                    "Não foi possível criar a conta.";


                try {

                    const erro =
                        await response.json();


                    if (erro.message) {

                        mensagem =
                            erro.message;
                    }

                } catch (error) {

                    console.error(error);
                }


                cadastroMessage.textContent =
                    mensagem;

                cadastroMessage.className =
                    "message error";

                return;
            }


            cadastroMessage.textContent =
                "Conta criada com sucesso! Faça o login.";

            cadastroMessage.className =
                "message success";


            emailInput.value =
                email;


            cadastroForm.reset();


            setTimeout(
                function () {

                    mostrarLogin();

                    errorMessage.textContent =
                        "Conta criada com sucesso. Faça o login para gerar seu token.";

                    errorMessage.className =
                        "message success";

                },
                1200
            );


        } catch (error) {

            console.error(error);

            cadastroMessage.textContent =
                "Erro ao conectar com o servidor.";

            cadastroMessage.className =
                "message error";
        }
    }
);