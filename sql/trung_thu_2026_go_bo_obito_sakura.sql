-- Stop the server cleanly before applying. Re-runnable, with original rows preserved.
-- Do not DELETE item template rows: ItemManager indexes the item list by ID;
-- holes would shift every later template and can corrupt inventory display.
CREATE TABLE IF NOT EXISTS `tt2026_removed_item_backup` LIKE `item`;
INSERT IGNORE INTO `tt2026_removed_item_backup`
SELECT * FROM `item` WHERE LOWER(TRIM(`name`)) REGEXP '(^|[^a-z])(obito|sakura)([^a-z]|$)';
CREATE TABLE IF NOT EXISTS `tt2026_removed_store_backup` LIKE `store_data`;
INSERT IGNORE INTO `tt2026_removed_store_backup`
SELECT s.* FROM `store_data` s JOIN `tt2026_removed_item_backup` i ON i.`id`=s.`item_id`;
INSERT IGNORE INTO `tt2026_removed_store_backup`
SELECT * FROM `store_data` WHERE `item_id` IN (1068,1069,1070,665,795,796) AND `store` IN (8,9,14);
START TRANSACTION;
DELETE s FROM `store_data` s JOIN `tt2026_removed_item_backup` i ON i.`id`=s.`item_id`;
UPDATE `item` i JOIN `tt2026_removed_item_backup` b ON b.`id`=i.`id`
SET i.`name`=CONCAT('[REMOVED] ',b.`name`),i.`description`='Removed from shop/event; item ID reserved for existing inventory compatibility.';
-- Seasonal shop entries are supplied by TrungThuNew.initStore in the correct NPC tabs.
DELETE FROM `store_data` WHERE `item_id` IN (1068,1069,1070,665,795,796) AND `store` IN (8,9,14);
COMMIT;
SELECT `id`,`name` FROM `tt2026_removed_item_backup`;
