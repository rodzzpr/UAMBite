-- ============================================================================
-- UAMBite - Consultas de inspeccion
-- ----------------------------------------------------------------------------
-- Archivo generado para inspeccionar el estado actual de la BD despues de
-- las pruebas realizadas (creacion de admin via AdminBootstrap, locales,
-- productos, pedido).
--
-- Conexion sugerida:
--   PGPASSWORD='<tu_password>' psql -h 192.168.1.23 -p 8927 -U postgres -d uambite
--
-- Las queries son read-only. No modifican nada.
-- ============================================================================


-- ============================================================================
-- 1) Resumen ejecutivo: conteo por tabla y por rol
-- ============================================================================
SELECT 'usuarios'            AS tabla, COUNT(*) AS total FROM usuario
UNION ALL SELECT 'locales',            COUNT(*) FROM local_comida
UNION ALL SELECT 'productos',          COUNT(*) FROM producto
UNION ALL SELECT 'pedidos',            COUNT(*) FROM pedido
UNION ALL SELECT 'detalles_pedido',    COUNT(*) FROM detalle_pedido
UNION ALL SELECT 'usuarios ADMIN',     COUNT(*) FROM usuario WHERE rol = 'ADMIN'
UNION ALL SELECT 'usuarios LOCAL',     COUNT(*) FROM usuario WHERE rol = 'LOCAL'
UNION ALL SELECT 'usuarios ESTUDIANTE',COUNT(*) FROM usuario WHERE rol = 'ESTUDIANTE'
UNION ALL SELECT 'usuarios PROFESOR',  COUNT(*) FROM usuario WHERE rol = 'PROFESOR';


-- ============================================================================
-- 2) Todos los usuarios (incluye el admin creado por AdminBootstrap)
--    La columna password arranca con $2a$ o $2b$ si el BCrypt aplico bien.
-- ============================================================================
SELECT
    u.carnet,
    u.nombre || ' ' || u.apellido    AS nombre_completo,
    u.rol,
    u.correo,
    LEFT(u.password, 7) || '...'     AS password_bcrypt_prefix,
    u.created_at                     AS creado
FROM usuario u
ORDER BY u.created_at DESC;


-- ============================================================================
-- 3) Solo los administradores
-- ============================================================================
SELECT carnet, nombre, apellido, correo, created_at
FROM usuario
WHERE rol = 'ADMIN'
ORDER BY created_at;


-- ============================================================================
-- 4) Locales con su dueno (encargado) - LEFT JOIN por si hay locales sin dueno
-- ============================================================================
SELECT
    l.id,
    l.nombre                              AS local,
    l.ubicacion,
    l.horario,
    u.carnet                              AS encargado_carnet,
    u.nombre || ' ' || u.apellido         AS encargado_nombre,
    l.created_at                          AS creado
FROM local_comida l
LEFT JOIN usuario u ON u.id = l.dueno_id
ORDER BY l.created_at DESC;


-- ============================================================================
-- 5) Productos agrupados por local (ver el aislamiento entre locales)
-- ============================================================================
SELECT
    l.nombre                              AS local,
    p.nombre                              AS producto,
    p.descripcion,
    '$' || p.precio::text                 AS precio,
    p.stock,
    p.permite_personalizacion             AS personalizable,
    p.id                                  AS producto_id
FROM producto p
JOIN local_comida l ON l.id = p.local_id
ORDER BY l.nombre, p.nombre;


-- ============================================================================
-- 6) Pedido completo con sus lineas (cliente + local + productos)
-- ============================================================================
SELECT
    ped.id                                AS pedido_id,
    ped.estado,
    ped.tipo_entrega,
    u.carnet                              AS cliente,
    l.nombre                              AS local,
    ped.prioridad,
    ped.created_at                        AS creado,
    dp.cantidad,
    p.nombre                              AS producto,
    dp.precio_unitario,
    dp.subtotal
FROM pedido ped
JOIN usuario u              ON u.id = ped.usuario_id
LEFT JOIN local_comida l    ON l.id = ped.local_comida_id
LEFT JOIN detalle_pedido dp ON dp.pedido_id = ped.id
LEFT JOIN producto p        ON p.id = dp.producto_id
ORDER BY ped.created_at DESC, dp.id;


-- ============================================================================
-- 7) Vista combinada del pedido (mas legible, una fila por pedido)
-- ============================================================================
SELECT
    ped.id,
    ped.estado,
    u.carnet                                                            AS cliente,
    l.nombre                                                            AS local,
    ped.tipo_entrega,
    ped.total,
    ped.prioridad,
    STRING_AGG(p.nombre || ' x' || dp.cantidad, ' | ' ORDER BY p.nombre) AS productos
FROM pedido ped
JOIN usuario u              ON u.id = ped.usuario_id
LEFT JOIN local_comida l    ON l.id = ped.local_comida_id
LEFT JOIN detalle_pedido dp ON dp.pedido_id = ped.id
LEFT JOIN producto p        ON p.id = dp.producto_id
GROUP BY ped.id, ped.estado, u.carnet, l.nombre, ped.tipo_entrega,
         ped.total, ped.prioridad;


-- ============================================================================
-- 8) Stock total por local (vista de inventario)
-- ============================================================================
SELECT
    l.nombre                              AS local,
    COUNT(p.id)                           AS cantidad_productos,
    SUM(p.stock)                          AS stock_total_unidades,
    SUM(p.precio * p.stock)               AS valor_inventario
FROM local_comida l
LEFT JOIN producto p ON p.local_id = l.id
GROUP BY l.id, l.nombre
ORDER BY l.nombre;


-- ============================================================================
-- 9) Auditoria: quien creo cada cosa (created_at)
-- ============================================================================
SELECT 'usuario'   AS tipo, id, carnet     AS referencia, created_at FROM usuario
UNION ALL
SELECT 'local'     AS tipo, id, nombre     AS referencia, created_at FROM local_comida
UNION ALL
SELECT 'producto'  AS tipo, id, nombre     AS referencia, created_at FROM producto
UNION ALL
SELECT 'pedido'    AS tipo, id, estado     AS referencia, created_at FROM pedido
ORDER BY created_at DESC;
