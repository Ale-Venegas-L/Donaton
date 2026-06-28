-- =============================================================
-- Seed Data for Donaton Platform
-- Run: docker compose exec -T mysql mysql --default-character-set=utf8mb4 -udonaton -pdonaton123 < mysql/seed-reference.sql
-- =============================================================

-- CAMPAIGNS ----------------------------------------------------
INSERT INTO campaigndb.campaign (nombre, descripcion, estado, fecha_inicio, fecha_fin) VALUES
('Campaña de Invierno', 'Recolectar frazadas y ropa de abrigo para familias en situacion de calle durante el invierno', 'ACTIVE', '2025-06-01', '2025-08-31'),
('Juguete para Todos', 'Campania de donacion de juguetes para ninos de escasos recursos en Navidad', 'ACTIVE', '2025-10-01', '2025-12-24'),
('Fondo de Becas', 'Recaudar fondos para becas educativas de estudiantes destacados con recursos limitados', 'ACTIVE', '2026-01-15', '2026-06-30'),
('Alimentos para Mascotas', 'Colecta de alimentos y articulos para mascotas de albergues animales', 'PLANNED', '2026-04-01', '2026-06-01');

-- DONATIONS (JOINED inheritance: donation base + sub-tables) -----
INSERT INTO donationdb.donation (id, campaign_id, donor_name, description, registration_date) VALUES
(1, 1, 'Carlos Munoz', 'Donacion de frazadas para campana de invierno', '2025-07-15 10:30:00'),
(2, 1, 'Maria Gonzalez', 'Donacion economica para compra de abrigos', '2025-07-20 14:00:00'),
(3, 2, 'Pedro Soto', 'Juguetes educativos para ninos', '2025-11-10 09:15:00'),
(4, 2, 'Ana Lopez', 'Donacion de libros y juegos de mesa', '2025-11-25 16:45:00'),
(5, 2, 'Roberto Diaz', 'Pelotas y material deportivo', '2025-12-05 11:00:00'),
(6, 3, 'Fundacion Esperanza', 'Donacion corporativa para becas', '2026-02-01 08:00:00');

INSERT INTO donationdb.monetary_donation (id, amount, currency) VALUES
(2, 50000, 'CLP'),
(6, 500000, 'CLP');

INSERT INTO donationdb.object_donation (id, object_name, category, estimated_value, quantity) VALUES
(1, 'Frazadas', 'Ropa de abrigo', 15000, 10),
(3, 'Juguetes educativos', 'Juguetes', 25000, 5),
(4, 'Libros infantiles', 'Educacion', 12000, 8),
(5, 'Pelotas de futbol', 'Deportes', 35000, 4);

-- Update Hibernate sequence to avoid conflicts
UPDATE donationdb.donation_seq SET next_val = 100;

-- VOLUNTEERS ----------------------------------------------------
INSERT INTO volunteersdb.volunteers (nombre, apellido, email, telefono, direccion, fecha_registro) VALUES
('Maria', 'Gonzalez', 'maria.gonzalez@email.com', '+56912345678', 'Av. Providencia 1234, Santiago', '2025-06-15 10:00:00'),
('Juan', 'Perez', 'juan.perez@email.com', '+56998765432', 'Calle Los Olivos 567, Nunoa', '2025-07-01 14:30:00'),
('Catalina', 'Rojas', 'catalina.rojas@email.com', '+56955556666', 'Pasaje El Sol 890, La Florida', '2025-08-20 09:00:00'),
('Andres', 'Torres', 'andres.torres@email.com', '+56977778888', 'Av. Matta 234, Santiago', '2025-09-10 11:15:00'),
('Valentina', 'Molina', 'valentina.molina@email.com', '+56933334444', 'Calle Las Rosas 456, Vitacura', '2025-10-05 16:00:00');

INSERT INTO volunteersdb.volunteer_campaigns (volunteer_id, campaign_id) VALUES
(1, 1), (2, 1), (3, 1),
(2, 2), (3, 2), (4, 2), (5, 2),
(2, 3), (4, 3), (5, 3);
