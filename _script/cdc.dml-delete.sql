USE inventory;

START TRANSACTION;
INSERT INTO product_items
VALUES (20001, "scooter", "Small 4-wheel scooter", 3.14);
COMMIT;

START TRANSACTION;
DELETE FROM product_items WHERE id = 10002;
COMMIT;
