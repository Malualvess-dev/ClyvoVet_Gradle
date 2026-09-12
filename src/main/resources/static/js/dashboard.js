(() => {

    // =============================================
    // SUPERVET - CONTROLE DE SESSÃO E PERFIS
    // =============================================


    // =============================================
    // DADOS SALVOS NO LOGIN
    // =============================================

    const dashboardToken =
        localStorage.getItem("token");

    const dashboardTipoUsuario =
        localStorage.getItem("tipoUsuario");


    // =============================================
    // VERIFICAR LOGIN
    // =============================================

    if (
        !dashboardToken ||
        !dashboardTipoUsuario
    ) {

        localStorage.removeItem("token");
        localStorage.removeItem("tipoUsuario");

        window.location.replace(
            "/login"
        );

        return;
    }


    // =============================================
    // NORMALIZAR PERFIL
    // =============================================

    const perfil =
        dashboardTipoUsuario
            .trim()
            .toUpperCase();


    // =============================================
    // ELEMENTOS
    // =============================================

    const tipoUsuarioElement =
        document.getElementById(
            "tipoUsuario"
        );


    const menuTutores =
        document.getElementById(
            "menuTutores"
        );


    const menuVeterinarios =
        document.getElementById(
            "menuVeterinarios"
        );


    const cardTutores =
        document.getElementById(
            "cardTutores"
        );


    const cardVeterinarios =
        document.getElementById(
            "cardVeterinarios"
        );


    const logoutButton =
        document.getElementById(
            "logoutButton"
        );


    // =============================================
    // MOSTRAR PERFIL
    // =============================================

    if (tipoUsuarioElement) {

        if (
            perfil ===
            "VETERINARIO"
        ) {

            tipoUsuarioElement.textContent =
                "VETERINÁRIO";

        } else if (
            perfil ===
            "TUTOR"
        ) {

            tipoUsuarioElement.textContent =
                "TUTOR";

        } else {

            tipoUsuarioElement.textContent =
                perfil;
        }
    }


    // =============================================
    // PERMISSÕES DO TUTOR
    // =============================================

    if (
        perfil !==
        "VETERINARIO"
    ) {

        // ESCONDER MENU TUTORES

        if (menuTutores) {

            menuTutores.style.display =
                "none";
        }


        // ESCONDER MENU VETERINÁRIOS

        if (menuVeterinarios) {

            menuVeterinarios.style.display =
                "none";
        }


        // ESCONDER CARD TUTORES

        if (cardTutores) {

            cardTutores.style.display =
                "none";
        }


        // ESCONDER CARD VETERINÁRIOS

        if (cardVeterinarios) {

            cardVeterinarios.style.display =
                "none";
        }
    }


    // =============================================
    // BLOQUEAR PÁGINAS EXCLUSIVAS
    // =============================================

    const paginaAtual =
        window.location.pathname;


    const paginasExclusivasVeterinario = [

        "/web/tutores",

        "/web/veterinarios"

    ];


    if (
        perfil !== "VETERINARIO" &&
        paginasExclusivasVeterinario.includes(
            paginaAtual
        )
    ) {

        window.location.replace(
            "/web/dashboard"
        );

        return;
    }


    // =============================================
    // LOGOUT
    // =============================================

    if (logoutButton) {

        logoutButton.addEventListener(
            "click",
            function () {

                // APAGA JWT

                localStorage.removeItem(
                    "token"
                );


                // APAGA PERFIL

                localStorage.removeItem(
                    "tipoUsuario"
                );


                // LIMPA DADOS EXTRAS
                // CASO EXISTAM

                sessionStorage.clear();


                // VOLTA PARA LOGIN
                // replace impede voltar
                // para a tela protegida
                // usando o botão voltar

                window.location.replace(
                    "/login"
                );
            }
        );
    }


    // =============================================
    // IMPEDIR ACESSO APÓS LOGOUT
    // PELO CACHE DO NAVEGADOR
    // =============================================

    window.addEventListener(
        "pageshow",
        function () {

            const tokenAtual =
                localStorage.getItem(
                    "token"
                );


            if (!tokenAtual) {

                window.location.replace(
                    "/login"
                );
            }
        }
    );


    // =============================================
    // DEBUG
    // =============================================

    console.log(
        "SuperVet - Perfil:",
        perfil
    );

    console.log(
        "SuperVet - Sessão JWT ativa:",
        Boolean(
            dashboardToken
        )
    );

})();