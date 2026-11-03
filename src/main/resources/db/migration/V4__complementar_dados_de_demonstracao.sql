-- =============================================================================
-- Dados complementares para a demonstração da aplicação.
-- =============================================================================

-- Raças mais comuns, para o tutor conseguir cadastrar pets reais.
INSERT INTO TB_RACA (ID_RACA, NOME, PORTE, ID_ESPECIE) VALUES (SEQ_RACA.NEXTVAL, 'SRD (sem raça definida)', 'MEDIO', 1);
INSERT INTO TB_RACA (ID_RACA, NOME, PORTE, ID_ESPECIE) VALUES (SEQ_RACA.NEXTVAL, 'Golden Retriever', 'GRANDE', 1);
INSERT INTO TB_RACA (ID_RACA, NOME, PORTE, ID_ESPECIE) VALUES (SEQ_RACA.NEXTVAL, 'Shih Tzu', 'PEQUENO', 1);
INSERT INTO TB_RACA (ID_RACA, NOME, PORTE, ID_ESPECIE) VALUES (SEQ_RACA.NEXTVAL, 'Bulldog Francês', 'PEQUENO', 1);
INSERT INTO TB_RACA (ID_RACA, NOME, PORTE, ID_ESPECIE) VALUES (SEQ_RACA.NEXTVAL, 'SRD (sem raça definida)', 'PEQUENO', 2);
INSERT INTO TB_RACA (ID_RACA, NOME, PORTE, ID_ESPECIE) VALUES (SEQ_RACA.NEXTVAL, 'Maine Coon', 'MEDIO', 2);
INSERT INTO TB_RACA (ID_RACA, NOME, PORTE, ID_ESPECIE) VALUES (SEQ_RACA.NEXTVAL, 'Periquito', 'PEQUENO', 3);
INSERT INTO TB_RACA (ID_RACA, NOME, PORTE, ID_ESPECIE) VALUES (SEQ_RACA.NEXTVAL, 'Hamster Sírio', 'PEQUENO', 4);
INSERT INTO TB_RACA (ID_RACA, NOME, PORTE, ID_ESPECIE) VALUES (SEQ_RACA.NEXTVAL, 'Porquinho-da-índia', 'PEQUENO', 4);
INSERT INTO TB_RACA (ID_RACA, NOME, PORTE, ID_ESPECIE) VALUES (SEQ_RACA.NEXTVAL, 'Jabuti', 'PEQUENO', 5);

-- Reforços previstos para as vacinas do Thor, que aparecem no painel do tutor.
UPDATE TB_HISTORICO_SAUDE
   SET PROXIMA_DOSE = DATE '2027-05-02', ID_CONSULTA = 1
 WHERE ID_PET = 1 AND EVENTO IN ('Vacina Múltipla V10', 'Vacina Antirrábica');
