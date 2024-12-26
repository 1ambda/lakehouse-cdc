USE inventory;

START TRANSACTION;
INSERT INTO product_items
VALUES (10001, "scooter", "Small 4-wheel scooter", 3.14);
COMMIT;

START TRANSACTION;
UPDATE product_items SET description = "Small 3-wheel scooter" WHERE id = 10001;
COMMIT;
