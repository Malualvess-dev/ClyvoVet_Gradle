-- Migration repetível (só Oracle): recriada pelo Flyway sempre que este arquivo mudar.
-- Código PL/SQL do grupo de Banco de Dados, sem alteração de lógica.

CREATE OR REPLACE PACKAGE PKG_PETS_JSON AS
    -- Converte manualmente os dados de um pet para uma string JSON.
    FUNCTION FN_PET_TUTOR_TO_JSON (
        p_id_pet        IN NUMBER,
        p_nome_pet      IN VARCHAR2,
        p_dt_nascimento IN DATE,
        p_sexo          IN CHAR,
        p_nome_tutor    IN VARCHAR2,
        p_raca          IN VARCHAR2,
        p_especie       IN VARCHAR2
    ) RETURN VARCHAR2;

    -- Lista pets com tutor, raça e espécie em JSON via DBMS_OUTPUT.
    PROCEDURE PR_LISTAR_PETS_JSON;
    -- Exporta todos os pets em um único array JSON.
    PROCEDURE PR_EXPORTAR_PETS_JSON;
END PKG_PETS_JSON;
/

CREATE OR REPLACE PACKAGE BODY PKG_PETS_JSON AS

FUNCTION FN_PET_TUTOR_TO_JSON (
    p_id_pet        IN NUMBER,
    p_nome_pet      IN VARCHAR2,
    p_dt_nascimento IN DATE,
    p_sexo          IN CHAR,
    p_nome_tutor    IN VARCHAR2,
    p_raca          IN VARCHAR2,
    p_especie       IN VARCHAR2

) RETURN VARCHAR2 IS

    v_json          VARCHAR2(4000);

    v_dt_str        VARCHAR2(10);

    -- Exceções personalizadas

    e_dados_incompletos EXCEPTION;

    e_sexo_invalido     EXCEPTION;

    e_id_invalido       EXCEPTION;

BEGIN

    -- Validação 1: ID do pet deve existir e ser positivo

    IF p_id_pet IS NULL OR p_id_pet <= 0 THEN

        RAISE e_id_invalido;

    END IF;

    -- Validação 2: campos obrigatórios não podem ser nulos

    IF p_nome_pet IS NULL OR p_nome_tutor IS NULL OR p_dt_nascimento IS NULL THEN

        RAISE e_dados_incompletos;

    END IF;

    -- Validação 3: sexo deve ser 'M' ou 'F'

    IF p_sexo IS NULL OR p_sexo NOT IN ('M', 'F') THEN

        RAISE e_sexo_invalido;

    END IF;

    -- Formata a data no padrão ISO (YYYY-MM-DD) para o JSON

    v_dt_str := TO_CHAR(p_dt_nascimento, 'YYYY-MM-DD');

    -- Monta a string JSON manualmente, concatenando cada par chave/valor

    -- REPLACE escapa aspas duplas internas, evitando quebra do JSON

    v_json := '{' ||

                '"id_pet": ' || p_id_pet || ',' ||

                '"nome_pet": "' || REPLACE(p_nome_pet, '"', '\"') || '",' ||

                '"dt_nascimento": "' || v_dt_str || '",' ||

                '"sexo": "' || p_sexo || '",' ||

                '"tutor": "' || REPLACE(p_nome_tutor, '"', '\"') || '",' ||

                '"raca": "' || REPLACE(p_raca, '"', '\"') || '",' ||

                '"especie": "' || REPLACE(p_especie, '"', '\"') || '"' ||

              '}';

    RETURN v_json;

EXCEPTION

    WHEN e_id_invalido THEN

        RAISE_APPLICATION_ERROR(-20001, 'Erro no JSON: O ID do pet informado e invalido.');

    WHEN e_dados_incompletos THEN

        RAISE_APPLICATION_ERROR(-20002, 'Erro no JSON: Nome do Pet ou Tutor nao podem ser nulos.');

    WHEN e_sexo_invalido THEN

        RAISE_APPLICATION_ERROR(-20003, 'Erro no JSON: O campo sexo deve ser M ou F.');

    WHEN OTHERS THEN

        -- Captura qualquer erro não previsto, evitando falha silenciosa

        RAISE_APPLICATION_ERROR(-20099, 'Erro inesperado durante a conversao manual para JSON: ' || SQLERRM);

END FN_PET_TUTOR_TO_JSON;

PROCEDURE PR_LISTAR_PETS_JSON

IS

    -- Exceções personalizadas

    e_sem_dados EXCEPTION;

    e_dados_invalidos EXCEPTION;

    e_erro_json EXCEPTION;

    v_contador NUMBER := 0;

    v_json VARCHAR2(4000);

BEGIN

    -- JOIN entre pet, tutor, raça e espécie

    FOR r IN (

        SELECT

            p.ID_PET,

            p.NOME AS NOME_PET,

            p.DT_NASCIMENTO,

            p.SEXO,

            t.NOME_TUTOR,

            r.NOME AS NOME_RACA,

            e.NOME AS NOME_ESPECIE

        FROM TB_PET p

        INNER JOIN TB_TUTOR t

            ON p.ID_TUTOR = t.ID_TUTOR

        INNER JOIN TB_RACA r

            ON p.ID_RACA = r.ID_RACA

        INNER JOIN TB_ESPECIE e

            ON r.ID_ESPECIE = e.ID_ESPECIE

        ORDER BY p.ID_PET

    )

    LOOP

        -- Validação 1: ID do pet deve ser válido

        IF r.ID_PET IS NULL OR r.ID_PET <= 0 THEN

            RAISE e_dados_invalidos;

        END IF;

        -- Validação 2: campos essenciais não podem ser nulos

        IF r.NOME_PET IS NULL OR r.NOME_TUTOR IS NULL THEN

            RAISE e_dados_invalidos;

        END IF;

        -- Bloco isolado para capturar falhas específicas da conversão em JSON

        BEGIN

            v_json := FN_PET_TUTOR_TO_JSON(

                r.ID_PET,

                r.NOME_PET,

                r.DT_NASCIMENTO,

                r.SEXO,

                r.NOME_TUTOR,

                r.NOME_RACA,

                r.NOME_ESPECIE

            );

            DBMS_OUTPUT.PUT_LINE(v_json);

            v_contador := v_contador + 1;

        EXCEPTION

            WHEN OTHERS THEN

                RAISE e_erro_json;

        END;
    END LOOP;

    -- Validação 3: verifica se algum registro foi processado

    IF v_contador = 0 THEN

        RAISE e_sem_dados;

    END IF;

EXCEPTION

    WHEN e_sem_dados THEN

        RAISE_APPLICATION_ERROR(-20101, 'Nenhum pet encontrado para gerar o JSON.');

    WHEN e_dados_invalidos THEN

        RAISE_APPLICATION_ERROR(-20102, 'Foram encontrados dados inválidos ou incompletos no cadastro do pet.');

    WHEN e_erro_json THEN

        RAISE_APPLICATION_ERROR(-20103, 'Erro durante a conversão dos dados relacionais para JSON.');

    WHEN OTHERS THEN

        RAISE_APPLICATION_ERROR(-20199, 'Erro inesperado no procedimento PR_LISTAR_PETS_JSON: ' || SQLERRM);

END PR_LISTAR_PETS_JSON;

PROCEDURE PR_EXPORTAR_PETS_JSON IS
    v_json       VARCHAR2(32767);
    v_contador   PLS_INTEGER := 0;
BEGIN
    v_json := '[';

    FOR r IN (
        SELECT
            p.ID_PET,
            p.NOME AS NOME_PET,
            p.DT_NASCIMENTO,
            p.SEXO,
            t.NOME_TUTOR,
            ra.NOME AS NOME_RACA,
            e.NOME AS NOME_ESPECIE
        FROM TB_PET p
        INNER JOIN TB_TUTOR t
            ON p.ID_TUTOR = t.ID_TUTOR
        INNER JOIN TB_RACA ra
            ON p.ID_RACA = ra.ID_RACA
        INNER JOIN TB_ESPECIE e
            ON ra.ID_ESPECIE = e.ID_ESPECIE
        ORDER BY p.ID_PET
    ) LOOP
        IF v_contador > 0 THEN
            v_json := v_json || ',';
        END IF;

        v_json := v_json ||
            FN_PET_TUTOR_TO_JSON(
                r.ID_PET,
                r.NOME_PET,
                r.DT_NASCIMENTO,
                r.SEXO,
                r.NOME_TUTOR,
                r.NOME_RACA,
                r.NOME_ESPECIE
            );

        v_contador := v_contador + 1;
    END LOOP;

    IF v_contador = 0 THEN
        RAISE_APPLICATION_ERROR(
            -20101,
            'Nenhum pet encontrado para exportação.'
        );
    END IF;

    v_json := v_json || ']';

    DBMS_OUTPUT.PUT_LINE(v_json);

EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(
            -20104,
            'Erro ao exportar pets para JSON: ' || SQLERRM
        );
END PR_EXPORTAR_PETS_JSON;

END PKG_PETS_JSON;
/

-- Versão para integração com os backends: devolve o JSON em um CLOB.
CREATE OR REPLACE PACKAGE PKG_PETS_JSON_API AS

    PROCEDURE PR_EXPORTAR_PETS_JSON (
        p_json OUT CLOB
    );

END PKG_PETS_JSON_API;
/

CREATE OR REPLACE PACKAGE BODY PKG_PETS_JSON_API AS

    PROCEDURE PR_EXPORTAR_PETS_JSON (
        p_json OUT CLOB
    ) IS
        v_contador PLS_INTEGER := 0;
        v_item     VARCHAR2(4000);
    BEGIN
        DBMS_LOB.CREATETEMPORARY(p_json, TRUE);

        DBMS_LOB.WRITEAPPEND(p_json, 1, '[');

        FOR r IN (
            SELECT
                p.ID_PET,
                p.NOME AS NOME_PET,
                p.DT_NASCIMENTO,
                p.SEXO,
                t.NOME_TUTOR,
                ra.NOME AS NOME_RACA,
                e.NOME AS NOME_ESPECIE
            FROM TB_PET p
            INNER JOIN TB_TUTOR t
                ON p.ID_TUTOR = t.ID_TUTOR
            INNER JOIN TB_RACA ra
                ON p.ID_RACA = ra.ID_RACA
            INNER JOIN TB_ESPECIE e
                ON ra.ID_ESPECIE = e.ID_ESPECIE
            ORDER BY p.ID_PET
        ) LOOP

            IF v_contador > 0 THEN
                DBMS_LOB.WRITEAPPEND(p_json, 1, ',');
            END IF;

            v_item := PKG_PETS_JSON.FN_PET_TUTOR_TO_JSON(
                r.ID_PET,
                r.NOME_PET,
                r.DT_NASCIMENTO,
                r.SEXO,
                r.NOME_TUTOR,
                r.NOME_RACA,
                r.NOME_ESPECIE
            );

            DBMS_LOB.WRITEAPPEND(
                p_json,
                LENGTH(v_item),
                v_item
            );

            v_contador := v_contador + 1;

        END LOOP;

        IF v_contador = 0 THEN
            RAISE_APPLICATION_ERROR(
                -20101,
                'Nenhum pet encontrado para exportacao.'
            );
        END IF;

        DBMS_LOB.WRITEAPPEND(p_json, 1, ']');

    EXCEPTION
        WHEN OTHERS THEN
            IF p_json IS NOT NULL
               AND DBMS_LOB.ISTEMPORARY(p_json) = 1 THEN
                DBMS_LOB.FREETEMPORARY(p_json);
            END IF;

            RAISE_APPLICATION_ERROR(
                -20104,
                'Erro ao exportar pets para JSON: ' || SQLERRM
            );
    END PR_EXPORTAR_PETS_JSON;

END PKG_PETS_JSON_API;
/
