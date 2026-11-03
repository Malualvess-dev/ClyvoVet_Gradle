-- =============================================================================
-- Limpeza ÚNICA do usuário Oracle antes da primeira execução com o banco completo.
--
-- Remove as tabelas antigas da aplicação (TB_CV_*), as tabelas, sequences, packages
-- e trigger do modelo do grupo e o histórico do Flyway. Na próxima vez que a
-- aplicação subir com o perfil "oracle", o Flyway recria tudo (estrutura, dados do
-- grupo, ajustes da aplicação, packages e trigger).
--
-- ATENÇÃO: apaga os dados dessas tabelas. Rode no SQL Developer com F5 (Executar Script).
-- Tabelas de outras disciplinas (que não estão nas listas abaixo) não são tocadas.
-- =============================================================================

SET SERVEROUTPUT ON;

DECLARE
    TYPE t_nomes IS TABLE OF VARCHAR2(128);

    v_tabelas t_nomes := t_nomes(
        -- tabelas antigas da aplicação Java
        'TB_CV_VACINA', 'TB_CV_NOTIFICACAO', 'TB_CV_CONSULTA', 'TB_CV_PET',
        'TB_CV_VETERINARIO', 'TB_CV_TUTOR', 'TB_CV_USUARIO',
        -- modelo do grupo
        'TB_AUDITORIA_COLEIRA', 'TB_COLEIRA', 'TB_PRESCRICAO', 'TB_PRONTUARIO', 'TB_HISTORICO_SAUDE',
        'TB_CONSULTA', 'TB_PET', 'TB_RACA', 'TB_ESPECIE', 'TB_AGENDA', 'TB_NOTIFICACAO', 'TB_ANEXO',
        'TB_ENDERECO', 'TB_BAIRRO', 'TB_CIDADE', 'TB_ESTADO', 'TB_TUTOR', 'TB_VETERINARIO', 'TB_USUARIO',
        'TB_LOG_ERRO');

    v_sequences t_nomes := t_nomes(
        'SEQ_ESTADO', 'SEQ_CIDADE', 'SEQ_BAIRRO', 'SEQ_ENDERECO', 'SEQ_USUARIO', 'SEQ_TUTOR',
        'SEQ_VETERINARIO', 'SEQ_ESPECIE', 'SEQ_RACA', 'SEQ_PET', 'SEQ_CONSULTA', 'SEQ_PRONTUARIO',
        'SEQ_PRESCRICAO', 'SEQ_HISTORICO_SAUDE', 'SEQ_COLEIRA', 'SEQ_AGENDA', 'SEQ_NOTIFICACAO',
        'SEQ_ANEXO', 'SEQ_LOG_ERRO', 'SEQ_AUDITORIA_COLEIRA');

    v_packages t_nomes := t_nomes(
        'PKG_PETS_JSON_API', 'PKG_PETS_JSON', 'PKG_RELATORIOS_API', 'PKG_RELATORIOS', 'PKG_AUDITORIA_COLEIRA');

    PROCEDURE executar(p_comando IN VARCHAR2) IS
        e_nao_existe EXCEPTION;
        PRAGMA EXCEPTION_INIT(e_nao_existe, -942);
        e_sequence_nao_existe EXCEPTION;
        PRAGMA EXCEPTION_INIT(e_sequence_nao_existe, -2289);
        e_objeto_nao_existe EXCEPTION;
        PRAGMA EXCEPTION_INIT(e_objeto_nao_existe, -4043);
        e_trigger_nao_existe EXCEPTION;
        PRAGMA EXCEPTION_INIT(e_trigger_nao_existe, -4080);
    BEGIN
        EXECUTE IMMEDIATE p_comando;
        DBMS_OUTPUT.PUT_LINE('OK      ' || p_comando);
    EXCEPTION
        WHEN e_nao_existe OR e_sequence_nao_existe OR e_objeto_nao_existe OR e_trigger_nao_existe THEN
            DBMS_OUTPUT.PUT_LINE('ignorado (não existe) ' || p_comando);
    END executar;
BEGIN
    executar('DROP TRIGGER TRG_AUDITORIA_COLEIRA');
    FOR i IN 1 .. v_packages.COUNT LOOP
        executar('DROP PACKAGE ' || v_packages(i));
    END LOOP;
    FOR i IN 1 .. v_tabelas.COUNT LOOP
        executar('DROP TABLE ' || v_tabelas(i) || ' CASCADE CONSTRAINTS PURGE');
    END LOOP;
    FOR i IN 1 .. v_sequences.COUNT LOOP
        executar('DROP SEQUENCE ' || v_sequences(i));
    END LOOP;
    executar('DROP TABLE "flyway_schema_history" PURGE');
END;
/
