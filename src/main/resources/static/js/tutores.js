// =============================================
// SUPERVET - TUTORES
// =============================================


// =============================================
// TOKEN
// =============================================

const token =
    localStorage.getItem("token");


if (!token) {

    window.location.href =
        "/login";
}


// =============================================
// ELEMENTOS
// =============================================

const tutorForm =
    document.getElementById("tutorForm");

const nomeTutorInput =
    document.getElementById("nomeTutor");

const cpfInput =
    document.getElementById("cpf");

const telefoneInput =
    document.getElementById("telefone");

const dataNascimentoInput =
    document.getElementById("dataNascimento");

const usuarioIdInput =
    document.getElementById("usuarioId");

const salvarTutorButton =
    document.getElementById("salvarTutor");

const cancelarEdicaoButton =
    document.getElementById("cancelarEdicao");

const mensagem =
    document.getElementById("mensagem");

const tutoresList =
    document.getElementById("tutoresList");


// =============================================
// MODAL
// =============================================

const modalExcluir =
    document.getElementById("modalExcluir");

const cancelarExclusao =
    document.getElementById("cancelarExclusao");

const confirmarExclusao =
    document.getElementById("confirmarExclusao");


// =============================================
// CONTROLE
// =============================================

let tutorEmEdicao =
    null;

let tutorParaExcluir =
    null;


// =============================================
// HEADERS
// =============================================

function getHeaders() {

    return {

        "Content-Type":
            "application/json",

        "Authorization":
            `Bearer ${token}`
    };
}


// =============================================
// MENSAGEM
// =============================================

function mostrarMensagem(
    texto,
    tipo
) {

    mensagem.textContent =
        texto;

    mensagem.className =
        `mensagem ${tipo}`;


    setTimeout(
        function () {

            mensagem.textContent =
                "";

            mensagem.className =
                "mensagem";

        },
        3500
    );
}


// =============================================
// CARREGAR TUTORES
// GET
// =============================================

async function carregarTutores() {

    try {

        const response =
            await fetch(
                "/tutores?page=0&size=100",
                {
                    method: "GET",

                    headers: {
                        "Authorization":
                            `Bearer ${token}`
                    }
                }
            );


        if (!response.ok) {

            console.error(
                "Erro GET /tutores:",
                response.status
            );

            throw new Error(
                "Erro ao carregar tutores."
            );
        }


        const data =
            await response.json();


        console.log(
            "Resposta GET tutores:",
            data
        );


        /*
         * Spring Page:
         *
         * {
         *   content: [...]
         * }
         *
         * ou lista normal:
         *
         * [...]
         */

        const tutores =
            Array.isArray(data)
                ? data
                : data.content || [];


        renderizarTutores(
            tutores
        );


    } catch (error) {

        console.error(
            "Erro ao carregar tutores:",
            error
        );


        tutoresList.innerHTML =
            `
                <div class="empty-message">
                    Não foi possível carregar os tutores.
                </div>
            `;
    }
}


// =============================================
// RENDERIZAR
// =============================================

function renderizarTutores(
    tutores
) {

    tutoresList.innerHTML =
        "";


    if (
        !tutores ||
        tutores.length === 0
    ) {

        tutoresList.innerHTML =
            `
                <div class="empty-message">

                    Nenhum tutor cadastrado.

                </div>
            `;

        return;
    }


    tutores.forEach(
        function (tutor) {

            const item =
                document.createElement("div");


            item.className =
                "agenda-item";


            item.innerHTML =
                `
                    <div class="agenda-info">

                        <h3>
                            ${tutor.nomeTutor ?? ""}
                        </h3>

                        <p>
                            <strong>CPF:</strong>
                            ${tutor.cpf ?? "-"}
                        </p>

                        <p>
                            <strong>Telefone:</strong>
                            ${tutor.telefone ?? "-"}
                        </p>

                        <p>
                            <strong>Data de nascimento:</strong>
                            ${formatarData(
                                tutor.dataNascimento
                            )}
                        </p>

                        <p>
                            <strong>ID do usuário:</strong>
                            ${tutor.usuarioId ?? "-"}
                        </p>

                    </div>


                    <div class="agenda-actions">

                        <button
                            type="button"
                            class="btn-editar">

                            Editar

                        </button>


                        <button
                            type="button"
                            class="btn-delete">

                            Excluir

                        </button>

                    </div>
                `;


            // EDITAR

            const editarButton =
                item.querySelector(
                    ".btn-editar"
                );


            editarButton.addEventListener(
                "click",
                function () {

                    iniciarEdicao(
                        tutor
                    );
                }
            );


            // EXCLUIR

            const excluirButton =
                item.querySelector(
                    ".btn-delete"
                );


            excluirButton.addEventListener(
                "click",
                function () {

                    abrirModalExcluir(
                        tutor.tutorId
                    );
                }
            );


            tutoresList.appendChild(
                item
            );
        }
    );
}


// =============================================
// FORMATAR DATA
// =============================================

function formatarData(
    data
) {

    if (!data) {

        return "-";
    }


    const partes =
        data.split("-");


    if (
        partes.length !== 3
    ) {

        return data;
    }


    return (
        partes[2] +
        "/" +
        partes[1] +
        "/" +
        partes[0]
    );
}


// =============================================
// FORM SUBMIT
// CREATE / UPDATE
// =============================================

tutorForm.addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();


        const tutorRequest = {

            nomeTutor:
                nomeTutorInput.value.trim(),

            cpf:
                cpfInput.value.trim(),

            telefone:
                telefoneInput.value.trim(),

            dataNascimento:
                dataNascimentoInput.value
                    ? dataNascimentoInput.value
                    : null,

            usuarioId:
                Number(
                    usuarioIdInput.value
                )
        };


        console.log(
            "Tutor enviado:",
            tutorRequest
        );


        if (
            tutorEmEdicao === null
        ) {

            await criarTutor(
                tutorRequest
            );

        } else {

            await atualizarTutor(
                tutorEmEdicao,
                tutorRequest
            );
        }
    }
);


// =============================================
// CREATE
// POST
// =============================================

async function criarTutor(
    tutorRequest
) {

    try {

        const response =
            await fetch(
                "/tutores",
                {
                    method:
                        "POST",

                    headers:
                        getHeaders(),

                    body:
                        JSON.stringify(
                            tutorRequest
                        )
                }
            );


        if (!response.ok) {

            const erro =
                await lerErro(
                    response
                );


            throw new Error(
                erro
            );
        }


        mostrarMensagem(
            "Tutor cadastrado com sucesso.",
            "sucesso"
        );


        limparFormulario();


        await carregarTutores();


    } catch (error) {

        console.error(
            "Erro ao criar tutor:",
            error
        );


        mostrarMensagem(
            error.message ||
            "Erro ao cadastrar tutor.",
            "erro"
        );
    }
}


// =============================================
// INICIAR EDIÇÃO
// =============================================

function iniciarEdicao(
    tutor
) {

    tutorEmEdicao =
        tutor.tutorId;


    nomeTutorInput.value =
        tutor.nomeTutor || "";

    cpfInput.value =
        tutor.cpf || "";

    telefoneInput.value =
        tutor.telefone || "";

    dataNascimentoInput.value =
        tutor.dataNascimento || "";

    usuarioIdInput.value =
        tutor.usuarioId || "";


    salvarTutorButton.textContent =
        "Atualizar";


    cancelarEdicaoButton.style.display =
        "inline-block";


    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });
}


// =============================================
// UPDATE
// PUT
// =============================================

async function atualizarTutor(
    tutorId,
    tutorRequest
) {

    try {

        const response =
            await fetch(
                `/tutores/${tutorId}`,
                {
                    method:
                        "PUT",

                    headers:
                        getHeaders(),

                    body:
                        JSON.stringify(
                            tutorRequest
                        )
                }
            );


        if (!response.ok) {

            const erro =
                await lerErro(
                    response
                );


            throw new Error(
                erro
            );
        }


        mostrarMensagem(
            "Tutor atualizado com sucesso.",
            "sucesso"
        );


        limparFormulario();


        await carregarTutores();


    } catch (error) {

        console.error(
            "Erro ao atualizar tutor:",
            error
        );


        mostrarMensagem(
            error.message ||
            "Erro ao atualizar tutor.",
            "erro"
        );
    }
}


// =============================================
// CANCELAR EDIÇÃO
// =============================================

cancelarEdicaoButton.addEventListener(
    "click",
    function () {

        limparFormulario();
    }
);


// =============================================
// LIMPAR FORM
// =============================================

function limparFormulario() {

    tutorForm.reset();


    tutorEmEdicao =
        null;


    salvarTutorButton.textContent =
        "Salvar";


    cancelarEdicaoButton.style.display =
        "none";
}


// =============================================
// MODAL EXCLUIR
// =============================================

function abrirModalExcluir(
    tutorId
) {

    tutorParaExcluir =
        tutorId;


    modalExcluir.style.display =
        "flex";
}


// =============================================
// CANCELAR DELETE
// =============================================

cancelarExclusao.addEventListener(
    "click",
    function () {

        tutorParaExcluir =
            null;


        modalExcluir.style.display =
            "none";
    }
);


// =============================================
// CONFIRMAR DELETE
// =============================================

confirmarExclusao.addEventListener(
    "click",
    async function () {

        if (
            tutorParaExcluir === null
        ) {

            return;
        }


        await excluirTutor(
            tutorParaExcluir
        );
    }
);


// =============================================
// DELETE
// =============================================

async function excluirTutor(
    tutorId
) {

    try {

        const response =
            await fetch(
                `/tutores/${tutorId}`,
                {
                    method:
                        "DELETE",

                    headers: {
                        "Authorization":
                            `Bearer ${token}`
                    }
                }
            );


        if (!response.ok) {

            const erro =
                await lerErro(
                    response
                );


            throw new Error(
                erro
            );
        }


        modalExcluir.style.display =
            "none";


        tutorParaExcluir =
            null;


        mostrarMensagem(
            "Tutor excluído com sucesso.",
            "sucesso"
        );


        await carregarTutores();


    } catch (error) {

        console.error(
            "Erro ao excluir tutor:",
            error
        );


        mostrarMensagem(
            error.message ||
            "Erro ao excluir tutor.",
            "erro"
        );
    }
}


// =============================================
// LER ERRO DO BACKEND
// =============================================

async function lerErro(
    response
) {

    try {

        const data =
            await response.json();


        if (data.message) {

            return data.message;
        }


        if (data.mensagem) {

            return data.mensagem;
        }


        if (data.error) {

            return data.error;
        }


    } catch (error) {

        console.error(
            "Não foi possível ler o JSON de erro.",
            error
        );
    }


    return (
        "Erro na requisição. Código HTTP: " +
        response.status
    );
}


// =============================================
// FECHAR MODAL CLICANDO FORA
// =============================================

modalExcluir.addEventListener(
    "click",
    function (event) {

        if (
            event.target ===
            modalExcluir
        ) {

            modalExcluir.style.display =
                "none";


            tutorParaExcluir =
                null;
        }
    }
);


// =============================================
// CARREGAR QUANDO A PÁGINA ABRIR
// =============================================

document.addEventListener(
    "DOMContentLoaded",
    function () {

        carregarTutores();
    }
);