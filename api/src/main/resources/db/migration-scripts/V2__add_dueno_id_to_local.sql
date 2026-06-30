-- ============================================================================
-- UAMBite — Fase 2: Ownership de locales
-- ----------------------------------------------------------------------------
-- EJECUTAR MANUALMENTE ANTES del deploy de la Fase 2.
--
-- Agrega la columna `dueno_id` a `local_comida` (FK a `usuario.id`). La
-- columna es NULLABLE durante la ventana de migración para no romper los
-- locales existentes; el `OwnershipService` permite que sólo ADMIN edite
-- locales con `dueno_id = NULL` (escape hatch). Ese escape se retira en
-- la Fase 4 cuando todos los locales tengan dueño asignado.
--
-- Backfill de locales existentes:
--   1) Crear manualmente un usuario con rol=LOCAL por cada local, o bien
--      un único usuario "admin-de-locales" si vas a reasignar después.
--   2) UPDATE local_comida SET dueno_id = '<uuid>' WHERE id = '<uuid>'.
--
-- Hacer BACKUP antes de ejecutar.
-- ============================================================================

ALTER TABLE local_comida
    ADD COLUMN dueno_id UUID NULL;

ALTER TABLE local_comida
    ADD CONSTRAINT fk_local_comida_dueno
        FOREIGN KEY (dueno_id) REFERENCES usuario(id);

CREATE INDEX ix_local_comida_dueno ON local_comida(dueno_id);
