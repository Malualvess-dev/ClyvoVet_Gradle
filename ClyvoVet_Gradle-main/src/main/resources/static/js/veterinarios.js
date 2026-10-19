(() => {

    const vetToken =
        localStorage.getItem("token");

    if (!vetToken) {
        window.location.href = "/login";
        return;
    }


    const form =
        document.getElementById("veterinarioForm");

    const nomeInput =
        document.getElementById("nomeVeterinario");

    const crmvInput =
        document.getElementById("crmv");

    const telefoneInput =
        document.getElementById("telefone");

    const especialidadeInput =
        document.getElementById("especialidade");

    const dataNascimentoInput =
        document.getElementById("dataNascimento");

    const usuarioIdInput =
        document.getElementById("usuarioId");

    const salvarButton =
        document.getElementById("salvarVeterinario");

    const cancelarButton =
        document.getElementById("cancelarEdicao");

    const mensagem =
        document.getElementById("mensagem");

    const lista =
        document.getElementById("veterinariosList");


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
                `Bearer ${vetToken}`
        };
    }


    function mostrarMensagem(
        texto,
        tipo
    ) {

        mensagem.textContent =
            texto;

        mensagem.className =
            `mensagem ${tipo}`;
    }


    async function carregar() {

        try {

            const response =
                await fetch(
                    "/veterinarios?page=0&size=100",
                    {
                        headers: {
                            "Authorization":
                                `Bearer ${vetToken}`
                        }
                    }
                );


            console.log(
                "GET veterinarios:",
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


            renderizar(
                itens
            );


        } catch (error) {

            console.error(error);

            lista.innerHTML =
                `
                    <div class="empty-message">
                        Não foi possível carregar os veterinários.
                    </div>
                `;
        }
    }


    function renderizar(
        itens
    ) {

        lista.innerHTML =
            "";


        if (
            itens.length === 0
        ) {

            lista.innerHTML =
                `
                    <div class="empty-message">
                        Nenhum veterinário cadastrado.
                    </div>
                `;

            return;
        }


        itens.forEach(
            (vet) => {

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
                                ${vet.nomeVeterinario || "-"}
                            </h3>

                            <p>
                                <strong>CRMV:</strong>
                                ${vet.crmv || "-"}
                            </p>

                            <p>
                                <strong>Telefone:</strong>
                                ${vet.telefone || "-"}
                            </p>

                            <p>
                                <strong>Especialidade:</strong>
                                ${vet.especialidade || "-"}
                            </p>

                            <p>
                                <strong>ID usuário:</strong>
                                ${vet.usuarioId ?? "-"}
                            </p>

                        </div>

                        <div class="agenda-actions">

                            <button
                                class="btn-editar"
                                type="button">
                                Editar
                            </button>

                            <button
                                class="btn-delete"
                                type="button">
                                Excluir
                            </button>

                        </div>
                    `;


                item.querySelector(
                    ".btn-editar"
                ).addEventListener(
                    "click",
                    () => editar(
                        vet
                    )
                );


                item.querySelector(
                    ".btn-delete"
                ).addEventListener(
                    "click",
                    () => {

                        idExcluir =
                            vet.veterinarioId;

                        modal.style.display =
                            "flex";
                    }
                );


                lista.appendChild(
                    item
                );
            }
        );
    }


    form.addEventListener(
        "submit",
        async (event) => {

            event.preventDefault();


            const request = {

                nomeVeterinario:
                    nomeInput.value.trim(),

                crmv:
                    crmvInput.value.trim(),

                telefone:
                    telefoneInput.value.trim(),

                especialidade:
                    especialidadeInput.value.trim(),

                dataNascimento:
                    dataNascimentoInput.value,

                usuarioId:
                    Number(
                        usuarioIdInput.value
                    )
            };


            try {

                const url =
                    idEdicao === null
                        ? "/veterinarios"
                        : `/veterinarios/${idEdicao}`;


                const method =
                    idEdicao === null
                        ? "POST"
                        : "PUT";


                const response =
                    await fetch(
                        url,
                        {
                            method: method,

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
                        `Erro HTTP ${response.status}`
                    );
                }


                mostrarMensagem(
                    idEdicao === null
                        ? "Veterinário cadastrado com sucesso."
                        : "Veterinário atualizado com sucesso.",
                    "sucesso"
                );


                limpar();

                await carregar();


            } catch (error) {

                mostrarMensagem(
                    error.message,
                    "erro"
                );
            }
        }
    );


    function editar(
        vet
    ) {

        idEdicao =
            vet.veterinarioId;


        nomeInput.value =
            vet.nomeVeterinario || "";

        crmvInput.value =
            vet.crmv || "";

        telefoneInput.value =
            vet.telefone || "";

        especialidadeInput.value =
            vet.especialidade || "";

        dataNascimentoInput.value =
            vet.dataNascimento || "";

        usuarioIdInput.value =
            vet.usuarioId || "";


        salvarButton.textContent =
            "Atualizar";

        cancelarButton.style.display =
            "inline-block";
    }


    function limpar() {

        form.reset();

        idEdicao =
            null;

        salvarButton.textContent =
            "Salvar";

        cancelarButton.style.display =
            "none";
    }


    cancelarButton.addEventListener(
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


            try {

                const response =
                    await fetch(
                        `/veterinarios/${idExcluir}`,
                        {
                            method: "DELETE",

                            headers: {
                                "Authorization":
                                    `Bearer ${vetToken}`
                            }
                        }
                    );


                if (!response.ok) {

                    throw new Error(
                        `Erro HTTP ${response.status}`
                    );
                }


                modal.style.display =
                    "none";

                idExcluir =
                    null;


                mostrarMensagem(
                    "Veterinário excluído com sucesso.",
                    "sucesso"
                );


                await carregar();


            } catch (error) {

                mostrarMensagem(
                    error.message,
                    "erro"
                );
            }
        }
    );


    carregar();

})();