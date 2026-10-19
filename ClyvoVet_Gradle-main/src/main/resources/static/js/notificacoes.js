(() => {

    const notificacaoToken =
        localStorage.getItem("token");

    if (!notificacaoToken) {
        window.location.href = "/login";
        return;
    }


    const form =
        document.getElementById("notificacaoForm");

    const mensagemInput =
        document.getElementById("mensagemNotificacao");

    const dataInput =
        document.getElementById("dataEnvio");

    const lidaInput =
        document.getElementById("lida");

    const usuarioInput =
        document.getElementById("usuarioId");

    const salvar =
        document.getElementById("salvarNotificacao");

    const cancelar =
        document.getElementById("cancelarEdicao");

    const mensagem =
        document.getElementById("mensagem");

    const lista =
        document.getElementById("notificacoesList");


    const modal =
        document.getElementById("modalExcluir");

    const cancelarExclusao =
        document.getElementById("cancelarExclusao");

    const confirmarExclusao =
        document.getElementById("confirmarExclusao");


    let idEdicao =
        null;

    let idExcluir =
        null;


    function headers() {

        return {
            "Content-Type":
                "application/json",

            "Authorization":
                `Bearer ${notificacaoToken}`
        };
    }


    async function carregar() {

        try {

            const response =
                await fetch(
                    "/notificacoes?page=0&size=100",
                    {
                        headers: {
                            "Authorization":
                                `Bearer ${notificacaoToken}`
                        }
                    }
                );


            console.log(
                "GET notificações:",
                response.status
            );


            if (!response.ok) {

                throw new Error(
                    `HTTP ${response.status}`
                );
            }


            const data =
                await response.json();


            const itens =
                Array.isArray(data)
                    ? data
                    : data.content || [];


            lista.innerHTML =
                "";


            if (
                itens.length === 0
            ) {

                lista.innerHTML =
                    `
                        <div class="empty-message">
                            Nenhuma notificação cadastrada.
                        </div>
                    `;

                return;
            }


            itens.forEach(
                (notificacao) => {

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
                                    ${notificacao.mensagem || "Notificação"}
                                </h3>

                                <p>
                                    <strong>Data:</strong>
                                    ${formatarData(
                                        notificacao.dataEnvio
                                    )}
                                </p>

                                <p>
                                    <strong>Status:</strong>
                                    ${
                                        notificacao.lida === "S"
                                            ? "Lida"
                                            : "Não lida"
                                    }
                                </p>

                                <p>
                                    <strong>ID usuário:</strong>
                                    ${notificacao.usuarioId ?? "-"}
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
                        () => editar(
                            notificacao
                        )
                    );


                    item.querySelector(
                        ".btn-delete"
                    ).addEventListener(
                        "click",
                        () => {

                            idExcluir =
                                notificacao.notificacaoId;

                            modal.style.display =
                                "flex";
                        }
                    );


                    lista.appendChild(
                        item
                    );
                }
            );


        } catch (error) {

            console.error(error);

            lista.innerHTML =
                `
                    <div class="empty-message">
                        Não foi possível carregar as notificações.
                    </div>
                `;
        }
    }


    function formatarData(
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


    form.addEventListener(
        "submit",
        async (event) => {

            event.preventDefault();


            const request = {

                mensagem:
                    mensagemInput.value.trim(),

                dataEnvio:
                    dataInput.value ||
                    null,

                lida:
                    lidaInput.value,

                usuarioId:
                    Number(
                        usuarioInput.value
                    )
            };


            const endpoint =
                idEdicao === null
                    ? "/notificacoes"
                    : `/notificacoes/${idEdicao}`;


            const method =
                idEdicao === null
                    ? "POST"
                    : "PUT";


            try {

                const response =
                    await fetch(
                        endpoint,
                        {
                            method,

                            headers:
                                headers(),

                            body:
                                JSON.stringify(
                                    request
                                )
                        }
                    );


                if (!response.ok) {

                    throw new Error(
                        `HTTP ${response.status}`
                    );
                }


                mensagem.textContent =
                    idEdicao === null
                        ? "Notificação cadastrada com sucesso."
                        : "Notificação atualizada com sucesso.";


                mensagem.className =
                    "mensagem sucesso";


                limpar();

                await carregar();


            } catch (error) {

                mensagem.textContent =
                    error.message;

                mensagem.className =
                    "mensagem erro";
            }
        }
    );


    function editar(
        notificacao
    ) {

        idEdicao =
            notificacao.notificacaoId;


        mensagemInput.value =
            notificacao.mensagem || "";

        dataInput.value =
            notificacao.dataEnvio
                ? notificacao.dataEnvio.substring(
                    0,
                    16
                )
                : "";

        lidaInput.value =
            notificacao.lida || "N";

        usuarioInput.value =
            notificacao.usuarioId || "";


        salvar.textContent =
            "Atualizar";

        cancelar.style.display =
            "inline-block";
    }


    function limpar() {

        form.reset();

        idEdicao =
            null;

        salvar.textContent =
            "Salvar";

        cancelar.style.display =
            "none";
    }


    cancelar.addEventListener(
        "click",
        limpar
    );


    cancelarExclusao.addEventListener(
        "click",
        () => {

            idExcluir =
                null;

            modal.style.display =
                "none";
        }
    );


    confirmarExclusao.addEventListener(
        "click",
        async () => {

            if (
                idExcluir === null
            ) {
                return;
            }


            const response =
                await fetch(
                    `/notificacoes/${idExcluir}`,
                    {
                        method: "DELETE",

                        headers: {
                            "Authorization":
                                `Bearer ${notificacaoToken}`
                        }
                    }
                );


            if (
                response.ok
            ) {

                modal.style.display =
                    "none";

                idExcluir =
                    null;

                await carregar();
            }
        }
    );


    carregar();

})();