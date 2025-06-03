-- Este archivo se ejecutará automáticamente si spring.jpa.hibernate.ddl-auto=create o create-drop

-- Insertar roles si no existen
INSERT INTO roles (nombre) VALUES ('ROL_ADMIN') ON CONFLICT DO NOTHING;
INSERT INTO roles (nombre) VALUES ('ROL_CLIENTE') ON CONFLICT DO NOTHING;

-- Verificar si las tablas están vacías antes de insertar datos
-- En una aplicación real, estos datos solo deberían insertarse en un entorno de desarrollo

-- Comentado para evitar inserción duplicada ya que DatabaseInitializer ya realiza esta operación
-- Descomenta si prefieres usar SQL en lugar de DatabaseInitializer

/*
-- Crear usuario administrador
INSERT INTO usuarios (username, password, email, nombre, apellido, activo)
SELECT 'admin', '$2a$10$yb2Kd2CrF7ke9zYbfqOBp.MSwe5TwlIVWszm0XyhOQcKBNqxGG1Ay', 'admin@hotel.com', 'Administrador', 'Sistema', true
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'admin');

-- Asignar rol de administrador
INSERT INTO usuario_roles (usuario_id, rol_id)
SELECT u.id, r.id FROM usuarios u, roles r
WHERE u.username = 'admin' AND r.nombre = 'ROL_ADMIN'
AND NOT EXISTS (SELECT 1 FROM usuario_roles WHERE usuario_id = u.id AND rol_id = r.id);

-- Crear usuario cliente de prueba
INSERT INTO usuarios (username, password, email, nombre, apellido, activo)
SELECT 'cliente', '$2a$10$f5yTRess9DoRVvMMsuyDEOpLN28RGMRsxFVpWePgfM3Qz/2QOK7N.', 'cliente@hotel.com', 'Cliente', 'Prueba', true
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'cliente');

-- Asignar rol de cliente
INSERT INTO usuario_roles (usuario_id, rol_id)
SELECT u.id, r.id FROM usuarios u, roles r
WHERE u.username = 'cliente' AND r.nombre = 'ROL_CLIENTE'
AND NOT EXISTS (SELECT 1 FROM usuario_roles WHERE usuario_id = u.id AND rol_id = r.id);

-- Crear perfil de cliente
INSERT INTO clientes (nombre, email, telefono, usuario_id)
SELECT 'Cliente Prueba', 'cliente@hotel.com', '555-1234', u.id
FROM usuarios u WHERE u.username = 'cliente'
AND NOT EXISTS (SELECT 1 FROM clientes WHERE usuario_id = u.id);
*/
