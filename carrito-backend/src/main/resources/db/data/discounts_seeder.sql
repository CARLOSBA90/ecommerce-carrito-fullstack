-- =========================================
-- SEEDER: DESCUENTOS DEL SISTEMA
-- =========================================
-- Script de inserción de descuentos para e-commerce
-- Tipos de carrito: COMMON, VIP, SPECIAL_DATE
-- NO SE PUEDEN COMBINAR VIP y SPECIAL_DATE

-- =========================================
-- DESCUENTOS APLICABLES A CUALQUIER CARRITO (ANY)
-- =========================================

-- Descuento 1: 25% por exactamente 4 productos
INSERT INTO discounts (code, name, description, discount_type, value, cart_type_applies, condition, priority, active)
VALUES (
    'DESC_4_PRODUCTS',
    '25% Descuento por 4 Productos',
    'Descuento del 25% al comprar exactamente 4 productos',
    'PERCENTAGE',
    25.00,
    'ANY',
    'EXACT_QTY_4',
    10,
    TRUE
);

-- Descuento 2: $100 por más de 10 productos
INSERT INTO discounts (code, name, description, discount_type, value, cart_type_applies, condition, priority, active)
VALUES (
    'DESC_10_PLUS',
    '$100 por Más de 10 Productos',
    'Descuento de $100 al comprar más de 10 productos',
    'FIXED',
    100.00,
    'ANY',
    'MIN_QTY_10',
    20,
    TRUE
);

-- =========================================
-- DESCUENTOS SOLO PARA SPECIAL_DATE
-- =========================================

-- Descuento 3: $300 por fecha especial
INSERT INTO discounts (code, name, description, discount_type, value, cart_type_applies, condition, priority, active)
VALUES (
    'DESC_SPECIAL_DATE',
    '$300 Descuento Fecha Especial',
    'Descuento de $300 en fechas promocionales',
    'FIXED',
    300.00,
    'SPECIAL_DATE',
    'ALWAYS',
    30,
    TRUE
);

-- =========================================
-- DESCUENTOS SOLO PARA VIP
-- =========================================

-- Descuento 4: Producto más barato gratis (VIP)
INSERT INTO discounts (code, name, description, discount_type, value, cart_type_applies, condition, priority, active)
VALUES (
    'DESC_VIP_FREE_PRODUCT',
    'Producto Más Barato Gratis (VIP)',
    'El producto más barato es gratis para clientes VIP',
    'FREE_PRODUCT',
    NULL,
    'VIP',
    'ALWAYS',
    40,
    TRUE
);

-- Descuento 5: $500 VIP
INSERT INTO discounts (code, name, description, discount_type, value, cart_type_applies, condition, priority, active)
VALUES (
    'DESC_VIP_500',
    '$500 Descuento VIP',
    'Descuento de $500 para clientes VIP',
    'FIXED',
    500.00,
    'VIP',
    'ALWAYS',
    50,
    TRUE
);

-- =========================================
-- VERIFICACIÓN
-- =========================================

-- Consultar todos los descuentos insertados
SELECT 
    code,
    name,
    discount_type,
    value,
    cart_type_applies,
    condition,
    priority,
    active
FROM discounts
ORDER BY priority;
