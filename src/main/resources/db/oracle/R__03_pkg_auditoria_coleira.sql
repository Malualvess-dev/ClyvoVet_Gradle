-- Migration repetível (só Oracle): recriada pelo Flyway sempre que este arquivo mudar.
-- Código PL/SQL do grupo de Banco de Dados, sem alteração de lógica.

CREATE OR REPLACE PACKAGE PKG_AUDITORIA_COLEIRA AS
    -- Registra os valores anteriores e posteriores de uma operação DML.
    PROCEDURE REGISTRAR_OPERACAO (
        p_operacao       IN VARCHAR2,
        p_id_old         IN NUMBER, p_id_new IN NUMBER,
        p_data_old       IN TIMESTAMP, p_data_new IN TIMESTAMP,
        p_latitude_old   IN NUMBER, p_latitude_new IN NUMBER,
        p_longitude_old  IN NUMBER, p_longitude_new IN NUMBER,
        p_batimento_old  IN NUMBER, p_batimento_new IN NUMBER,
        p_alerta_old     IN NUMBER, p_alerta_new IN NUMBER,
        p_id_pet_old     IN NUMBER, p_id_pet_new IN NUMBER
    );
END PKG_AUDITORIA_COLEIRA;
/

CREATE OR REPLACE PACKAGE BODY PKG_AUDITORIA_COLEIRA AS
    PROCEDURE REGISTRAR_OPERACAO (
        p_operacao       IN VARCHAR2,
        p_id_old         IN NUMBER, p_id_new IN NUMBER,
        p_data_old       IN TIMESTAMP, p_data_new IN TIMESTAMP,
        p_latitude_old   IN NUMBER, p_latitude_new IN NUMBER,
        p_longitude_old  IN NUMBER, p_longitude_new IN NUMBER,
        p_batimento_old  IN NUMBER, p_batimento_new IN NUMBER,
        p_alerta_old     IN NUMBER, p_alerta_new IN NUMBER,
        p_id_pet_old     IN NUMBER, p_id_pet_new IN NUMBER
    ) IS
    BEGIN
        INSERT INTO TB_AUDITORIA_COLEIRA (
            ID_AUDITORIA, USUARIO, OPERACAO, DATA_HORA,
            ID_COLEIRA_OLD, ID_COLEIRA_NEW,
            DATA_HORA_OLD, DATA_HORA_NEW,
            LATITUDE_OLD, LATITUDE_NEW,
            LONGITUDE_OLD, LONGITUDE_NEW,
            BATIMENTO_OLD, BATIMENTO_NEW,
            ALERTA_OLD, ALERTA_NEW,
            ID_PET_OLD, ID_PET_NEW
        ) VALUES (
            SEQ_AUDITORIA_COLEIRA.NEXTVAL,
            SYS_CONTEXT('USERENV', 'SESSION_USER'),
            p_operacao,
            SYSTIMESTAMP,
            p_id_old, p_id_new,
            p_data_old, p_data_new,
            p_latitude_old, p_latitude_new,
            p_longitude_old, p_longitude_new,
            p_batimento_old, p_batimento_new,
            p_alerta_old, p_alerta_new,
            p_id_pet_old, p_id_pet_new
        );
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20401,
                'Erro ao registrar auditoria da coleira: ' || SQLERRM);
    END REGISTRAR_OPERACAO;
END PKG_AUDITORIA_COLEIRA;
/
