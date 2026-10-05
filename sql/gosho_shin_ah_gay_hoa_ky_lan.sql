-- Gosho / Cua hang dau: 1000 luong, 15 ngay tu khi mua, cap 10.
-- Shin Ah (814): options rong de random khi trang bi lan dau.
-- Hoa ky lan (1076): giu 2 option goc cua Item.initOption(), random khi cuoi lan dau.
-- Gay mat trang nam (799), Gay trai tim nu (800): chi so co dinh goc.
-- Build Converter.java moi truoc khi mo lai server de tranh nhan doi option gay.
START TRANSACTION;
UPDATE `item` SET `level`=10 WHERE `id` IN (799,800,814,1076);

CREATE TEMPORARY TABLE `gosho_new_items` (
 `item_id` INT NOT NULL PRIMARY KEY,
 `options` TEXT NOT NULL
);
INSERT INTO `gosho_new_items` (`item_id`,`options`) VALUES
 (799,'[{"id":94,"param":15},{"id":92,"param":100},{"id":86,"param":200}]'),
 (800,'[{"id":94,"param":15},{"id":92,"param":100},{"id":86,"param":200}]'),
 (814,'[]'),
 (1076,'[]');

UPDATE `store_data` AS s JOIN `gosho_new_items` AS p ON p.`item_id`=s.`item_id`
SET s.`sys`=0,s.`lock`=1,s.`coin`=0,s.`gold`=1000,s.`yen`=0,
 s.`expire`=1296000000,s.`options`=p.`options`
WHERE s.`store`=14;

INSERT INTO `store_data` (`item_id`,`sys`,`store`,`lock`,`coin`,`gold`,`yen`,`expire`,`options`)
SELECT p.`item_id`,0,14,1,0,1000,0,1296000000,p.`options`
FROM `gosho_new_items` AS p JOIN `item` AS i ON i.`id`=p.`item_id`
WHERE NOT EXISTS (SELECT 1 FROM `store_data` AS s WHERE s.`item_id`=p.`item_id` AND s.`store`=14);
DROP TEMPORARY TABLE `gosho_new_items`;
COMMIT;

SELECT i.`id`,i.`name`,i.`level`,s.`store`,s.`gold`,s.`expire`,s.`options`
FROM `store_data` AS s JOIN `item` AS i ON i.`id`=s.`item_id`
WHERE s.`store`=14 AND s.`item_id` IN (799,800,814,1076)
ORDER BY s.`item_id`;
