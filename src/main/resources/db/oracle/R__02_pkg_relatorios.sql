-- Migration repetível (só Oracle): recriada pelo Flyway sempre que este arquivo mudar.
-- Código PL/SQL do grupo de Banco de Dados, sem alteração de lógica.

CREATE OR REPLACE PACKAGE PKG_RELATORIOS AS
    -- Calcula idade aproximada em anos completos a partir da data de nascimento.
    FUNCTION FN_CALCULAR_IDADE_PET (
        p_dt_nascimento IN DATE
    ) RETURN NUMBER;

    -- Emite relatório de soma de batimentos por pet/alerta, subtotais e total geral.
    PROCEDURE PR_RELATORIO_COLEIRA;
END PKG_RELATORIOS;
/

CREATE OR REPLACE PACKAGE BODY PKG_RELATORIOS AS
FUNCTION FN_CALCULAR_IDADE_PET (

    p_dt_nascimento IN DATE

) RETURN NUMBER

IS

    v_idade NUMBER;

    -- Exceções personalizadas

    e_data_nula EXCEPTION;

    e_data_futura EXCEPTION;

    e_idade_invalida EXCEPTION;

BEGIN

    -- Validação 1: data obrigatória

    IF p_dt_nascimento IS NULL THEN

        RAISE e_data_nula;

    END IF;

    -- Validação 2: data não pode estar no futuro

    IF p_dt_nascimento > SYSDATE THEN

        RAISE e_data_futura;

    END IF;

    -- Cálculo da idade aproximada em anos completos

    v_idade := TRUNC(MONTHS_BETWEEN(SYSDATE, p_dt_nascimento) / 12);

    -- Validação 3: idade não pode ser negativa (segurança extra)

    IF v_idade < 0 THEN

        RAISE e_idade_invalida;

    END IF;

    RETURN v_idade;

EXCEPTION

    WHEN e_data_nula THEN

        RAISE_APPLICATION_ERROR(-20201, 'A data de nascimento do pet não pode ser nula.');

    WHEN e_data_futura THEN

        RAISE_APPLICATION_ERROR(-20202, 'A data de nascimento do pet não pode ser futura.');

    WHEN e_idade_invalida THEN

        RAISE_APPLICATION_ERROR(-20203, 'A idade calculada para o pet é inválida.');

    WHEN OTHERS THEN

        RAISE_APPLICATION_ERROR(-20299, 'Erro inesperado ao calcular a idade do pet: ' || SQLERRM);

END FN_CALCULAR_IDADE_PET;

PROCEDURE PR_RELATORIO_COLEIRA

IS

    -- Exceções personalizadas

    e_sem_dados EXCEPTION;

    e_pet_invalido EXCEPTION;

    e_medicao_invalida EXCEPTION;

    -- Controle dos agrupamentos (guardam a categoria "atual" durante o loop)

    v_pet_atual NUMBER := NULL;

    v_alerta_atual NUMBER := NULL;

    -- Acumuladores manuais (nenhuma função de agregação automática é usada)

    v_soma_combinacao NUMBER := 0;  -- soma da combinação (ID_PET, ALERTA)

    v_subtotal_pet NUMBER := 0;     -- soma de todas as combinações de um pet

    v_total_geral NUMBER := 0;      -- soma de todos os registros da tabela

    v_tem_dados BOOLEAN := FALSE;

BEGIN
    -- Cabeçalho do relatório

    DBMS_OUTPUT.PUT_LINE(

        RPAD('ID_PET', 10) ||

        RPAD('ALERTA', 10) ||

        RPAD('SOMA_BATIMENTO', 18)

    );


    DBMS_OUTPUT.PUT_LINE(

        RPAD('------', 10) ||

        RPAD('------', 10) ||

        RPAD('------------------', 18)

    );


    -- Percorre os dados já ordenados pela hierarquia das categorias

    FOR r IN (

        SELECT

            ID_PET,

            ALERTA,

            BATIMENTO_BPM

        FROM TB_COLEIRA

        ORDER BY ID_PET, ALERTA

    )

    LOOP

        v_tem_dados := TRUE;

        -- Validação 1: ID do pet deve ser válido

        IF r.ID_PET IS NULL OR r.ID_PET <= 0 THEN

            RAISE e_pet_invalido;

        END IF;

        -- Validação 2: alerta deve ser 0 (normal) ou 1 (alerta)

        IF r.ALERTA IS NULL OR r.ALERTA NOT IN (0, 1) THEN

            RAISE e_medicao_invalida;

        END IF;

        -- Validação 3: batimento não pode ser nulo ou negativo

        IF r.BATIMENTO_BPM IS NULL OR r.BATIMENTO_BPM < 0 THEN

            RAISE e_medicao_invalida;

        END IF;

        -- Primeiro registro do relatório: apenas inicializa o controle

        IF v_pet_atual IS NULL THEN

            v_pet_atual := r.ID_PET;

            v_alerta_atual := r.ALERTA;

        -- Quebra de controle: mudou o pet (categoria 1)

        ELSIF r.ID_PET <> v_pet_atual THEN

            -- Exibe a última combinação do pet anterior

            DBMS_OUTPUT.PUT_LINE(

                RPAD(TO_CHAR(v_pet_atual), 10) ||

                RPAD(TO_CHAR(v_alerta_atual), 10) ||

                RPAD(TO_CHAR(v_soma_combinacao, 'FM9999990.00'), 18)

            );

            -- Exibe o subtotal do pet anterior (categoria de agrupamento nula)

            DBMS_OUTPUT.PUT_LINE(

                RPAD('Sub Total', 10) ||

                RPAD(' ', 10) ||

                RPAD(TO_CHAR(v_subtotal_pet, 'FM9999990.00'), 18)

            );

            -- Reinicia o controle para o novo pet

            v_pet_atual := r.ID_PET;

            v_alerta_atual := r.ALERTA;

            v_soma_combinacao := 0;

            v_subtotal_pet := 0;

        -- Quebra de controle: mesmo pet, mudou o alerta (categoria 2)

        ELSIF r.ALERTA <> v_alerta_atual THEN

            -- Exibe a combinação anterior antes de trocar de alerta

            DBMS_OUTPUT.PUT_LINE(

                RPAD(TO_CHAR(v_pet_atual), 10) ||

                RPAD(TO_CHAR(v_alerta_atual), 10) ||

                RPAD(TO_CHAR(v_soma_combinacao, 'FM9999990.00'), 18)

            );

            -- Reinicia apenas o acumulador da combinação

            v_alerta_atual := r.ALERTA;

            v_soma_combinacao := 0;

        END IF;

        -- Soma manual (linha a linha) nos três níveis de agregação

        v_soma_combinacao := v_soma_combinacao + r.BATIMENTO_BPM;

        v_subtotal_pet := v_subtotal_pet + r.BATIMENTO_BPM;

        v_total_geral := v_total_geral + r.BATIMENTO_BPM;

    END LOOP;

    -- Verifica se a tabela retornou algum registro

    IF NOT v_tem_dados THEN

        RAISE e_sem_dados;

    END IF;

    -- Exibe a última combinação pendente após o fim do loop

    DBMS_OUTPUT.PUT_LINE(

        RPAD(TO_CHAR(v_pet_atual), 10) ||

        RPAD(TO_CHAR(v_alerta_atual), 10) ||

        RPAD(TO_CHAR(v_soma_combinacao, 'FM9999990.00'), 18)

    );

    -- Exibe o subtotal do último pet

    DBMS_OUTPUT.PUT_LINE(

        RPAD('Sub Total', 10) ||

        RPAD(' ', 10) ||

        RPAD(TO_CHAR(v_subtotal_pet, 'FM9999990.00'), 18)

    );

    -- Exibe o total geral, ao final de toda a listagem

    DBMS_OUTPUT.PUT_LINE(
        RPAD('Total Geral', 10) ||
        RPAD(' ', 10) ||
        RPAD(TO_CHAR(v_total_geral, 'FM9999990.00'), 18)
    );

EXCEPTION

    WHEN e_sem_dados THEN

        RAISE_APPLICATION_ERROR(-20301, 'Nenhum registro encontrado na tabela TB_COLEIRA.');

    WHEN e_pet_invalido THEN

        RAISE_APPLICATION_ERROR(-20302, 'Foi encontrado um ID_PET invalido na tabela TB_COLEIRA.');

    WHEN e_medicao_invalida THEN

        RAISE_APPLICATION_ERROR(-20303, 'Foi encontrada uma medicao invalida na tabela TB_COLEIRA.');

    WHEN OTHERS THEN

        RAISE_APPLICATION_ERROR(-20399, 'Erro inesperado no procedimento PR_RELATORIO_COLEIRA: ' || SQLERRM);

END PR_RELATORIO_COLEIRA;

END PKG_RELATORIOS;
/

-- [Java] Especificação que faltava no sprint4_integracao_json.sql (sem ela o corpo não compila).
CREATE OR REPLACE PACKAGE PKG_RELATORIOS_API AS
    -- Mesmo relatório de PKG_RELATORIOS.PR_RELATORIO_COLEIRA, devolvido em um CLOB.
    PROCEDURE PR_RELATORIO_COLEIRA (
        p_relatorio OUT CLOB
    );
END PKG_RELATORIOS_API;
/

CREATE OR REPLACE PACKAGE BODY PKG_RELATORIOS_API AS

    PROCEDURE PR_RELATORIO_COLEIRA (
        p_relatorio OUT CLOB
    ) IS
        e_sem_dados       EXCEPTION;
        e_pet_invalido    EXCEPTION;
        e_medicao_invalida EXCEPTION;

        v_pet_atual       NUMBER := NULL;
        v_alerta_atual    NUMBER := NULL;
        v_soma_combinacao NUMBER := 0;
        v_subtotal_pet    NUMBER := 0;
        v_total_geral     NUMBER := 0;
        v_tem_dados       BOOLEAN := FALSE;

        PROCEDURE ADICIONAR_LINHA(p_linha IN VARCHAR2) IS
        BEGIN
            DBMS_LOB.WRITEAPPEND(
                p_relatorio,
                LENGTH(p_linha) + 1,
                p_linha || CHR(10)
            );
        END ADICIONAR_LINHA;

        PROCEDURE EXIBIR_COMBINACAO IS
        BEGIN
            ADICIONAR_LINHA(
                RPAD(TO_CHAR(v_pet_atual), 10) ||
                RPAD(TO_CHAR(v_alerta_atual), 10) ||
                RPAD(TO_CHAR(v_soma_combinacao, 'FM9999990.00'), 18)
            );
        END EXIBIR_COMBINACAO;

        PROCEDURE EXIBIR_SUBTOTAL IS
        BEGIN
            ADICIONAR_LINHA(
                RPAD('Sub Total', 10) ||
                RPAD(' ', 10) ||
                RPAD(TO_CHAR(v_subtotal_pet, 'FM9999990.00'), 18)
            );
        END EXIBIR_SUBTOTAL;

    BEGIN
        DBMS_LOB.CREATETEMPORARY(p_relatorio, TRUE);

        ADICIONAR_LINHA(
            RPAD('ID_PET', 10) ||
            RPAD('ALERTA', 10) ||
            RPAD('SOMA_BATIMENTO', 18)
        );

        ADICIONAR_LINHA(
            RPAD('------', 10) ||
            RPAD('------', 10) ||
            RPAD('------------------', 18)
        );

        FOR r IN (
            SELECT ID_PET, ALERTA, BATIMENTO_BPM
            FROM TB_COLEIRA
            ORDER BY ID_PET, ALERTA
        ) LOOP

            v_tem_dados := TRUE;

            IF r.ID_PET IS NULL OR r.ID_PET <= 0 THEN
                RAISE e_pet_invalido;
            END IF;

            IF r.ALERTA IS NULL OR r.ALERTA NOT IN (0, 1) THEN
                RAISE e_medicao_invalida;
            END IF;

            IF r.BATIMENTO_BPM IS NULL OR r.BATIMENTO_BPM < 0 THEN
                RAISE e_medicao_invalida;
            END IF;

            IF v_pet_atual IS NULL THEN
                v_pet_atual := r.ID_PET;
                v_alerta_atual := r.ALERTA;

            ELSIF r.ID_PET <> v_pet_atual THEN
                EXIBIR_COMBINACAO;
                EXIBIR_SUBTOTAL;

                v_pet_atual := r.ID_PET;
                v_alerta_atual := r.ALERTA;
                v_soma_combinacao := 0;
                v_subtotal_pet := 0;

            ELSIF r.ALERTA <> v_alerta_atual THEN
                EXIBIR_COMBINACAO;

                v_alerta_atual := r.ALERTA;
                v_soma_combinacao := 0;
            END IF;

            v_soma_combinacao := v_soma_combinacao + r.BATIMENTO_BPM;
            v_subtotal_pet := v_subtotal_pet + r.BATIMENTO_BPM;
            v_total_geral := v_total_geral + r.BATIMENTO_BPM;

        END LOOP;

        IF NOT v_tem_dados THEN
            RAISE e_sem_dados;
        END IF;

        EXIBIR_COMBINACAO;
        EXIBIR_SUBTOTAL;

        ADICIONAR_LINHA(
            RPAD('Total Geral', 10) ||
            RPAD(' ', 10) ||
            RPAD(TO_CHAR(v_total_geral, 'FM9999990.00'), 18)
        );

    EXCEPTION
        WHEN e_sem_dados THEN
            RAISE_APPLICATION_ERROR(
                -20301,
                'Nenhum registro encontrado na tabela TB_COLEIRA.'
            );

        WHEN e_pet_invalido THEN
            RAISE_APPLICATION_ERROR(
                -20302,
                'Foi encontrado um ID_PET invalido na tabela TB_COLEIRA.'
            );

        WHEN e_medicao_invalida THEN
            RAISE_APPLICATION_ERROR(
                -20303,
                'Foi encontrada uma medicao invalida na tabela TB_COLEIRA.'
            );

        WHEN OTHERS THEN
            IF p_relatorio IS NOT NULL
               AND DBMS_LOB.ISTEMPORARY(p_relatorio) = 1 THEN
                DBMS_LOB.FREETEMPORARY(p_relatorio);
            END IF;

            RAISE_APPLICATION_ERROR(
                -20399,
                'Erro inesperado no relatorio de coleiras: ' || SQLERRM
            );
    END PR_RELATORIO_COLEIRA;

END PKG_RELATORIOS_API;
/
