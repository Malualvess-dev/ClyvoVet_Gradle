-- Migration repetível (só Oracle): recriada pelo Flyway sempre que este arquivo mudar.
-- Código PL/SQL do grupo de Banco de Dados, sem alteração de lógica.

CREATE OR REPLACE TRIGGER TRG_AUDITORIA_COLEIRA
AFTER INSERT OR UPDATE OR DELETE ON TB_COLEIRA
FOR EACH ROW
BEGIN
    -- INSERT: somente valores NEW estão disponíveis.
    IF INSERTING THEN
        PKG_AUDITORIA_COLEIRA.REGISTRAR_OPERACAO(
            'INSERT',
            NULL, :NEW.ID_COLEIRA,
            NULL, :NEW.DATA_HORA,
            NULL, :NEW.LATITUDE,
            NULL, :NEW.LONGITUDE,
            NULL, :NEW.BATIMENTO_BPM,
            NULL, :NEW.ALERTA,
            NULL, :NEW.ID_PET
        );
    -- UPDATE: registra valores OLD e NEW para comparação.
    ELSIF UPDATING THEN
        PKG_AUDITORIA_COLEIRA.REGISTRAR_OPERACAO(
            'UPDATE',
            :OLD.ID_COLEIRA, :NEW.ID_COLEIRA,
            :OLD.DATA_HORA, :NEW.DATA_HORA,
            :OLD.LATITUDE, :NEW.LATITUDE,
            :OLD.LONGITUDE, :NEW.LONGITUDE,
            :OLD.BATIMENTO_BPM, :NEW.BATIMENTO_BPM,
            :OLD.ALERTA, :NEW.ALERTA,
            :OLD.ID_PET, :NEW.ID_PET
        );
    -- DELETE: somente valores OLD estão disponíveis.
    ELSIF DELETING THEN
        PKG_AUDITORIA_COLEIRA.REGISTRAR_OPERACAO(
            'DELETE',
            :OLD.ID_COLEIRA, NULL,
            :OLD.DATA_HORA, NULL,
            :OLD.LATITUDE, NULL,
            :OLD.LONGITUDE, NULL,
            :OLD.BATIMENTO_BPM, NULL,
            :OLD.ALERTA, NULL,
            :OLD.ID_PET, NULL
        );
    END IF;
END TRG_AUDITORIA_COLEIRA;
/
