(() => {

    const agendaToken =
        localStorage.getItem("token");

    if (!agendaToken) {
        window.location.href = "/login";
        return;
    }


    const agendaForm =
        document.getElementById("agendaForm");

    const tituloInput =
        document.getElementById("titulo");

    const dataHoraInput =
        document.getElementById("dataHora");

    const anotacaoInput =
        document.getElementById("anotacao");

    const usuarioIdInput =
        document.getElementById("usuarioId");

    const salvarAgendaButton =
        document.getElementById("salvarAgenda");

    const cancelarEdicaoButton =
        document.getElementById("cancelarEdicao");

    const mensagemAgenda =
        document.getElementById("mensagem");

    const agendaList =
        document.getElementById("agendaList");


    const modalExcluirAgenda =
        document.getElementById("modalExcluir");

    const cancelarExclusaoAgenda =
        document.getElementById("cancelarExclusao");

    const confirmarExclusaoAgenda =
        document.getElementById("confirmarExclusao");


    let agendaEmEdicao =
        null;

    let agendaParaExcluir =
        null;


    function headersAgenda() {
        return {
            "Content-Type":
                "application/json",

            "Authorization":
                `Bearer ${agendaToken}`
        };
    }


    function mostrarMensagemAgenda(
        texto,
        tipo
    ) {

        mensagemAgenda.textContent =
            texto;

        mensagemAgenda.className =
            `mensagem ${tipo}`;


        setTimeout(
            () => {

                mensagemAgenda.textContent =
                    "";

                mensagemAgenda.className =
                    "mensagem";

            },
            3500
        );
    }


    async function carregarAgendas() {

        console.log(
            "Carregando agenda..."
        );

        try {

            const response =
                await fetch(
                    "/agendas?page=0&size=100",
                    {
                        headers: {
                            "Authorization":
                                `Bearer ${agendaToken}`
                        }
                    }
                );


            console.log(
                "Status GET agenda:",
                response.status
            );


            if (!response.ok) {

                throw new Error(
                    `Erro HTTP ${response.status}`
                );
            }


            const data =
                await response.json();


            console.log(
                "Resposta agenda:",
                data
            );


            const agendas =
                Array.isArray(data)
                    ? data
                    : data.content || [];


            renderizarAgendas(
                agendas
            );


        } catch (error) {

            console.error(
                error
            );

            agendaList.innerHTML =
                `
                    <div class="empty-message">
                        Não foi possível carregar a agenda.
                    </div>
                `;
        }
    }


    function renderizarAgendas(
        agendas
    ) {

        agendaList.innerHTML =
            "";


        if (
            !agendas ||
            agendas.length === 0
        ) {

            agendaList.innerHTML =
                `
                    <div class="empty-message">
                        Nenhum compromisso cadastrado.
                    </div>
                `;

            return;
        }


        agendas.forEach(
            (agenda) => {

                const item =
                    document.createElement(
                        "div"
                    );


                item.className =
                    "agenda-item";


                item.innerHTML =
                    `
                        <div class="agenda-info">

                            <h3>
                                ${agenda.titulo || "Sem título"}
                            </h3>

                            <p>
                                <strong>Data:</strong>
                                ${formatarDataHoraAgenda(
                                    agenda.dataHora
                                )}
                            </p>

                            <p>
                                <strong>Anotação:</strong>
                                ${agenda.anotacao || "-"}
                            </p>

                            <p>
                                <strong>ID usuário:</strong>
                                ${agenda.usuarioId ?? "-"}
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


                item.querySelector(
                    ".btn-editar"
                ).addEventListener(
                    "click",
                    () => iniciarEdicaoAgenda(
                        agenda
                    )
                );


                item.querySelector(
                    ".btn-delete"
                ).addEventListener(
                    "click",
                    () => abrirModalAgenda(
                        agenda.agendaId
                    )
                );


                agendaList.appendChild(
                    item
                );
            }
        );
    }


    function formatarDataHoraAgenda(
        data
    ) {

        if (!data) {
            return "-";
        }

        return new Date(
            data
        ).toLocaleString(
            "pt-BR"
        );
    }


    agendaForm.addEventListener(
        "submit",
        async (event) => {

            event.preventDefault();


            const request = {

                titulo:
                    tituloInput.value.trim(),

                dataHora:
                    dataHoraInput.value,

                anotacao:
                    anotacaoInput.value.trim(),

                usuarioId:
                    Number(
                        usuarioIdInput.value
                    )
            };


            if (
                agendaEmEdicao === null
            ) {

                await criarAgenda(
                    request
                );

            } else {

                await atualizarAgenda(
                    agendaEmEdicao,
                    request
                );
            }
        }
    );


    async function criarAgenda(
        request
    ) {

        try {

            const response =
                await fetch(
                    "/agendas",
                    {
                        method: "POST",

                        headers:
                            headersAgenda(),

                        body:
                            JSON.stringify(
                                request
                            )
                    }
                );


            if (!response.ok) {

                throw new Error(
                    await lerErroAgenda(
                        response
                    )
                );
            }


            mostrarMensagemAgenda(
                "Agenda cadastrada com sucesso.",
                "sucesso"
            );


            limparAgenda();

            await carregarAgendas();


        } catch (error) {

            mostrarMensagemAgenda(
                error.message,
                "erro"
            );
        }
    }


    function iniciarEdicaoAgenda(
        agenda
    ) {

        agendaEmEdicao =
            agenda.agendaId;


        tituloInput.value =
            agenda.titulo || "";

        dataHoraInput.value =
            agenda.dataHora
                ? agenda.dataHora.substring(
                    0,
                    16
                )
                : "";

        anotacaoInput.value =
            agenda.anotacao || "";

        usuarioIdInput.value =
            agenda.usuarioId || "";


        salvarAgendaButton.textContent =
            "Atualizar";


        cancelarEdicaoButton.style.display =
            "inline-block";


        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });
    }


    async function atualizarAgenda(
        id,
        request
    ) {

        try {

            const response =
                await fetch(
                    `/agendas/${id}`,
                    {
                        method: "PUT",

                        headers:
                            headersAgenda(),

                        body:
                            JSON.stringify(
                                request
                            )
                    }
                );


            if (!response.ok) {

                throw new Error(
                    await lerErroAgenda(
                        response
                    )
                );
            }


            mostrarMensagemAgenda(
                "Agenda atualizada com sucesso.",
                "sucesso"
            );


            limparAgenda();

            await carregarAgendas();


        } catch (error) {

            mostrarMensagemAgenda(
                error.message,
                "erro"
            );
        }
    }


    cancelarEdicaoButton.addEventListener(
        "click",
        limparAgenda
    );


    function limparAgenda() {

        agendaForm.reset();

        agendaEmEdicao =
            null;

        salvarAgendaButton.textContent =
            "Salvar";

        cancelarEdicaoButton.style.display =
            "none";
    }


    function abrirModalAgenda(
        id
    ) {

        agendaParaExcluir =
            id;

        modalExcluirAgenda.style.display =
            "flex";
    }


    cancelarExclusaoAgenda.addEventListener(
        "click",
        () => {

            agendaParaExcluir =
                null;

            modalExcluirAgenda.style.display =
                "none";
        }
    );


    confirmarExclusaoAgenda.addEventListener(
        "click",
        async () => {

            if (
                agendaParaExcluir === null
            ) {
                return;
            }

            await excluirAgenda(
                agendaParaExcluir
            );
        }
    );


    async function excluirAgenda(
        id
    ) {

        try {

            const response =
                await fetch(
                    `/agendas/${id}`,
                    {
                        method: "DELETE",

                        headers: {
                            "Authorization":
                                `Bearer ${agendaToken}`
                        }
                    }
                );


            if (!response.ok) {

                throw new Error(
                    await lerErroAgenda(
                        response
                    )
                );
            }


            modalExcluirAgenda.style.display =
                "none";

            agendaParaExcluir =
                null;


            mostrarMensagemAgenda(
                "Agenda excluída com sucesso.",
                "sucesso"
            );


            await carregarAgendas();


        } catch (error) {

            mostrarMensagemAgenda(
                error.message,
                "erro"
            );
        }
    }


    async function lerErroAgenda(
        response
    ) {

        try {

            const data =
                await response.json();

            return (
                data.message ||
                data.mensagem ||
                data.error ||
                `Erro HTTP ${response.status}`
            );

        } catch {

            return (
                `Erro HTTP ${response.status}`
            );
        }
    }


    carregarAgendas();

})();