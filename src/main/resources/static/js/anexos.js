(() => {

    const anexoToken =
        localStorage.getItem("token");

    if (!anexoToken) {
        window.location.href = "/login";
        return;
    }


    const form =
        document.getElementById("anexoForm");

    const nomeInput =
        document.getElementById("nomeArquivo");

    const tipoInput =
        document.getElementById("tipoArquivo");

    const urlInput =
        document.getElementById("url");

    const tamanhoInput =
        document.getElementById("tamanhoKb");

    const dataInput =
        document.getElementById("dataCriacao");

    const usuarioInput =
        document.getElementById("usuarioId");

    const salvar =
        document.getElementById("salvarAnexo");

    const cancelar =
        document.getElementById("cancelarEdicao");

    const mensagem =
        document.getElementById("mensagem");

    const lista =
        document.getElementById("anexosList");


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
                `Bearer ${anexoToken}`
        };
    }


    async function carregar() {

        try {

            const response =
                await fetch(
                    "/anexos?page=0&size=100",
                    {
                        headers: {
                            "Authorization":
                                `Bearer ${anexoToken}`
                        }
                    }
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
                            Nenhum anexo cadastrado.
                        </div>
                    `;

                return;
            }


            itens.forEach(
                (anexo) => {

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
                                    ${anexo.nomeArquivo || "-"}
                                </h3>

                                <p>
                                    <strong>Tipo:</strong>
                                    ${anexo.tipoArquivo || "-"}
                                </p>

                                <p>
                                    <strong>URL:</strong>
                                    ${anexo.url || "-"}
                                </p>

                                <p>
                                    <strong>Tamanho:</strong>
                                    ${anexo.tamanhoKb ?? "-"} KB
                                </p>

                                <p>
                                    <strong>ID usuário:</strong>
                                    ${anexo.usuarioId ?? "-"}
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
                            anexo
                        )
                    );


                    item.querySelector(
                        ".btn-delete"
                    ).addEventListener(
                        "click",
                        () => {

                            idExcluir =
                                anexo.anexoId;

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
                        Não foi possível carregar os anexos.
                    </div>
                `;
        }
    }


    form.addEventListener(
        "submit",
        async (event) => {

            event.preventDefault();


            const request = {

                nomeArquivo:
                    nomeInput.value.trim(),

                tipoArquivo:
                    tipoInput.value,

                url:
                    urlInput.value.trim(),

                tamanhoKb:
                    tamanhoInput.value
                        ? Number(
                            tamanhoInput.value
                        )
                        : null,

                dataCriacao:
                    dataInput.value ||
                    null,

                usuarioId:
                    Number(
                        usuarioInput.value
                    )
            };


            const endpoint =
                idEdicao === null
                    ? "/anexos"
                    : `/anexos/${idEdicao}`;


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
                        ? "Anexo cadastrado com sucesso."
                        : "Anexo atualizado com sucesso.";


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
        anexo
    ) {

        idEdicao =
            anexo.anexoId;


        nomeInput.value =
            anexo.nomeArquivo || "";

        tipoInput.value =
            anexo.tipoArquivo || "";

        urlInput.value =
            anexo.url || "";

        tamanhoInput.value =
            anexo.tamanhoKb || "";

        dataInput.value =
            anexo.dataCriacao
                ? anexo.dataCriacao.substring(
                    0,
                    16
                )
                : "";

        usuarioInput.value =
            anexo.usuarioId || "";


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
                    `/anexos/${idExcluir}`,
                    {
                        method: "DELETE",

                        headers: {
                            "Authorization":
                                `Bearer ${anexoToken}`
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