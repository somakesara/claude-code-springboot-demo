-- V2: Seed data for demo and local development

INSERT INTO customers (id, email, first_name, last_name, phone, tier) VALUES
    ('a0000000-0000-0000-0000-000000000001', 'alice.johnson@example.com', 'Alice',   'Johnson', '+14155550100', 'VIP'),
    ('a0000000-0000-0000-0000-000000000002', 'bob.smith@example.com',     'Bob',     'Smith',   '+14155550101', 'PREMIUM'),
    ('a0000000-0000-0000-0000-000000000003', 'carol.white@example.com',   'Carol',   'White',   '+14155550102', 'STANDARD');

INSERT INTO inventory_items (id, product_id, product_sku, product_name, quantity_on_hand, quantity_reserved, reorder_threshold, unit_cost, warehouse_location) VALUES
    ('b0000000-0000-0000-0000-000000000001',
     'c0000000-0000-0000-0000-000000000001',
     'WIDGET-001', 'Standard Widget',        500, 20, 50, 4.99,  'WAREHOUSE-A'),
    ('b0000000-0000-0000-0000-000000000002',
     'c0000000-0000-0000-0000-000000000002',
     'WIDGET-PRO', 'Professional Widget',    200, 10, 25, 12.49, 'WAREHOUSE-A'),
    ('b0000000-0000-0000-0000-000000000003',
     'c0000000-0000-0000-0000-000000000003',
     'GADGET-001', 'Smart Gadget',            80,  5, 20, 49.99, 'WAREHOUSE-B'),
    ('b0000000-0000-0000-0000-000000000004',
     'c0000000-0000-0000-0000-000000000004',
     'GADGET-PRO', 'Professional Smart Gadget', 8,  3, 15, 149.99, 'WAREHOUSE-B');
