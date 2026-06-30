-- ============================================================================
-- UAMBite — Fase 4: Contraseña temporal del admin inicial
-- ----------------------------------------------------------------------------
-- Agrega la columna `password_temporal` a `usuario`. Se setea en TRUE solo
-- para el admin que crea AdminBootstrap al primer arranque; el resto de los
-- usuarios quedan en FALSE. El flag obliga a cambiar la contraseña en el
-- primer login (flujo gestionado en AuthController y el frontend).
--
-- DEFAULT FALSE para no afectar a usuarios existentes.
-- ============================================================================

ALTER TABLE usuario
    ADD COLUMN password_temporal BOOLEAN NOT NULL DEFAULT FALSE;
