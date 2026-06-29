-- ============================================================================
-- UAMBite — Fase 3: Soporte de imágenes
-- ----------------------------------------------------------------------------
-- Agrega columnas para almacenar imágenes como BYTEA en las tablas de
-- LocalComida, Producto e IngredienteExtra.
--
-- Las columnas son NULLABLE, por lo que no rompen los registros existentes.
-- imagen_tipo almacena el MIME type (image/jpeg, image/png, image/webp)
-- para que el endpoint GET pueda devolver el Content-Type correcto.
--
-- Ejecutar sobre una base limpia (o con datos: la migración es aditiva).
-- ============================================================================

ALTER TABLE local_comida
    ADD COLUMN imagen BYTEA,
    ADD COLUMN imagen_tipo VARCHAR(50);

ALTER TABLE producto
    ADD COLUMN imagen BYTEA,
    ADD COLUMN imagen_tipo VARCHAR(50);

ALTER TABLE ingrediente_extra
    ADD COLUMN imagen BYTEA,
    ADD COLUMN imagen_tipo VARCHAR(50);
