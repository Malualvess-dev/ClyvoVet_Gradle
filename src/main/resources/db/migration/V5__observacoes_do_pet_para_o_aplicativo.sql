-- =============================================================================
-- V5: campo usado pelo aplicativo mobile (SuperVet) no cadastro do pet.
-- Alteração só aditiva: a coluna é opcional e não afeta os dados do grupo.
-- =============================================================================

ALTER TABLE TB_PET ADD OBSERVACOES VARCHAR2(500);
