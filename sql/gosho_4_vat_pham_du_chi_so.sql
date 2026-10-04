-- NSOKISS / Gosho: cập nhật chỉ số và vị trí bán.
-- Cửa hàng đầu = store 14; tab Thời trang = store 32.
-- 1000 lượng, 15 ngày kể từ khi mua, cấp sử dụng 10.
-- Cần build lại src sau khi cập nhật Converter.java và Item.java.

START TRANSACTION;
UPDATE `item` SET `level`=10 WHERE `id` IN (407,408,742,820,851,1047);

CREATE TEMPORARY TABLE `gosho_item_patch` (
 `item_id` INT NOT NULL PRIMARY KEY,
 `store_type` INT NOT NULL,
 `options` TEXT NOT NULL
);
INSERT INTO `gosho_item_patch` (`item_id`,`store_type`,`options`) VALUES
 (407, 32, '[{"id":58,"param":20},{"id":6,"param":500}]'),
 (408, 32, '[{"id":58,"param":20},{"id":6,"param":500}]'),
 (820, 32, '[{"id":125,"param":3000},{"id":117,"param":3000},{"id":94,"param":10},{"id":136,"param":100},{"id":127,"param":10},{"id":130,"param":10},{"id":131,"param":10}]'),
 (742, 14, '[{"id":73,"param":5000},{"id":6,"param":5000}]'),
 (851, 14, '[{"id":6,"param":3000},{"id":7,"param":3000}]'),
 (1047,14, '[{"id":73,"param":2000},{"id":67,"param":50},{"id":6,"param":2000},{"id":7,"param":2000},{"id":57,"param":80}]');

-- Chuyển Cửu Vĩ đã bán ở tab Thời trang sang Cửa hàng đầu.
DELETE FROM `store_data` WHERE `item_id`=1047 AND `store`=32;

UPDATE `store_data` AS s JOIN `gosho_item_patch` AS p
 ON p.`item_id`=s.`item_id` AND p.`store_type`=s.`store`
SET s.`sys`=0, s.`lock`=1, s.`coin`=0, s.`gold`=1000,
 s.`yen`=0, s.`expire`=1296000000, s.`options`=p.`options`;

INSERT INTO `store_data` (`item_id`,`sys`,`store`,`lock`,`coin`,`gold`,`yen`,`expire`,`options`)
SELECT p.`item_id`,0,p.`store_type`,1,0,1000,0,1296000000,p.`options`
FROM `gosho_item_patch` AS p JOIN `item` AS i ON i.`id`=p.`item_id`
WHERE NOT EXISTS (SELECT 1 FROM `store_data` AS s
 WHERE s.`item_id`=p.`item_id` AND s.`store`=p.`store_type`);
DROP TEMPORARY TABLE `gosho_item_patch`;
COMMIT;

SELECT i.`id`,i.`name`,i.`level`,s.`store`,s.`gold`,s.`expire`,s.`options`
FROM `store_data` AS s JOIN `item` AS i ON i.`id`=s.`item_id`
WHERE i.`id` IN (407,408,742,820,851,1047) AND s.`store` IN (14,32)
ORDER BY s.`store`,i.`id`;
