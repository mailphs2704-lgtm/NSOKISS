-- NSOKISS / Gosho > Thời trang (store = 32)
-- Bán 4 vật phẩm có chỉ số mặc định xác định trong Item.initOption().
-- 1000 lượng, 15 ngày kể từ khi mua, cấp sử dụng 10.
-- Cần dùng cùng bản src đã sửa Converter.toItem(ItemStore, byte) để
-- không cộng trùng option mặc định khi mua từ cửa hàng.
-- Không thêm Jiraiya/Konan vì đã có; không thêm Akasuki vì thiếu chỉ số gốc.

START TRANSACTION;
UPDATE `item` SET `level` = 10 WHERE `id` IN (1047,820,407,408);

-- Cửu Vĩ Hồ Ly Siêu Cấp: tấn công +2000, chí mạng +50%, HP/MP +2000,
-- tiềm năng tất cả +80.
-- Tôn Hành Giả: HP +1000, tấn công +1000, chí mạng +10,
-- cộng thêm tiềm năng +20%.
-- Super Broly / Onna Bugeisha: tiềm năng +20%, HP +500.
CREATE TEMPORARY TABLE `gosho_costume_patch` (
 `item_id` INT NOT NULL PRIMARY KEY,
 `options` TEXT NOT NULL
);
INSERT INTO `gosho_costume_patch` (`item_id`,`options`) VALUES
 (1047, '[{"id":73,"param":2000},{"id":67,"param":50},{"id":6,"param":2000},{"id":7,"param":2000},{"id":57,"param":80}]'),
 (820,  '[{"id":82,"param":1000},{"id":87,"param":1000},{"id":69,"param":10},{"id":58,"param":20}]'),
 (407,  '[{"id":58,"param":20},{"id":6,"param":500}]'),
 (408,  '[{"id":58,"param":20},{"id":6,"param":500}]');

UPDATE `store_data` AS s JOIN `gosho_costume_patch` AS p ON p.`item_id`=s.`item_id`
SET s.`sys`=0, s.`lock`=1, s.`coin`=0, s.`gold`=1000, s.`yen`=0,
    s.`expire`=1296000000, s.`options`=p.`options`
WHERE s.`store`=32;

INSERT INTO `store_data` (`item_id`,`sys`,`store`,`lock`,`coin`,`gold`,`yen`,`expire`,`options`)
SELECT p.`item_id`,0,32,1,0,1000,0,1296000000,p.`options`
FROM `gosho_costume_patch` AS p JOIN `item` AS i ON i.`id`=p.`item_id`
WHERE NOT EXISTS (
 SELECT 1 FROM `store_data` AS s WHERE s.`store`=32 AND s.`item_id`=p.`item_id`
);
DROP TEMPORARY TABLE `gosho_costume_patch`;
COMMIT;

SELECT i.`id`,i.`name`,i.`level`,s.`store`,s.`gold`,s.`expire`,s.`options`
FROM `store_data` AS s JOIN `item` AS i ON i.`id`=s.`item_id`
WHERE s.`store`=32 AND i.`id` IN (1047,820,407,408,613,614,1092,1093)
ORDER BY i.`id`;
