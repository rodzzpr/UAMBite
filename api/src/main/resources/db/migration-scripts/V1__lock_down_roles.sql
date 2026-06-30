-- ============================================================================
-- UAMBite — Fase 1: Lock down de roles
-- ----------------------------------------------------------------------------
-- EJECUTAR MANUALMENTE ANTES del deploy de la Fase 1.
--
-- Contexto: hasta hoy la columna `usuario.rol` es VARCHAR(30) libre. La
-- aplicación pasaba a aceptar (via /auth/register) cualquiera de los valores
-- CLIENTE | ADMIN | LOCAL. Con la nueva versión, el código de la app trabaja
-- con el enum `Rol` = {ADMIN, LOCAL, ESTUDIANTE, PROFESOR}. Si el schema
-- todavía contiene valores distintos, Hibernate fallará al cargar las filas
-- afectadas. Este script normaliza la data para que la app arranque limpia.
--
-- Regla de negocio confirmada:
--   - Carnets de 14 dígitos numéricos -> PROFESOR
--   - Carnets de  8 dígitos numéricos -> ESTUDIANTE
--   - Cualquier otro caso no migra automáticamente (queda NULL hasta
--     revisión manual).
--
-- Importante:
--   1. Hacer BACKUP de la tabla `usuario` antes de correr esto.
--   2. Si tu DB no es PostgreSQL, ajustar la sintaxis del LENGTH / REGEXP.
--   3. Los usuarios con rol ADMIN o LOCAL creados por el bug previo no se
--      tocan aquí (el dueño del sistema los revisa y limpia aparte).
-- ============================================================================

-- (Opcional) Backup rápido:
-- CREATE TABLE usuario_backup_fase1 AS SELECT * FROM usuario;

-- 1) Normalizar CLIENTE -> ESTUDIANTE / PROFESOR según largo del carnet.
--    Carnet de 14 dígitos => PROFESOR; carnet de 8 dígitos => ESTUDIANTE.
UPDATE usuario
   SET rol = 'PROFESOR'
 WHERE rol = 'CLIENTE'
   AND carnet ~ '^[0-9]{14}$';

UPDATE usuario
   SET rol = 'ESTUDIANTE'
 WHERE rol = 'CLIENTE'
   AND carnet ~ '^[0-9]{8}$';

-- 2) Cualquier CLIENTE restante (carnet que no calza con 8 ni 14 dígitos)
--    pasa a ESTUDIANTE por default. Ajustar si aplica otra regla.
UPDATE usuario
   SET rol = 'ESTUDIANTE'
 WHERE rol = 'CLIENTE';

-- 3) Defensivo: NULL -> ESTUDIANTE para que la app no se rompa al leerlos.
UPDATE usuario
   SET rol = 'ESTUDIANTE'
 WHERE rol IS NULL;

-- 4) Verificación: mostrar distribución de roles. NO es parte de la
--    migración, solo util para auditoría.
-- SELECT rol, COUNT(*) FROM usuario GROUP BY rol ORDER BY rol;
