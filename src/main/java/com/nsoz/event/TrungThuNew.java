package com.nsoz.event;

import com.nsoz.constants.*;
import com.nsoz.event.eventpoint.EventPoint;
import com.nsoz.item.*;
import com.nsoz.lib.RandomCollection;
import com.nsoz.model.*;
import com.nsoz.option.ItemOption;
import com.nsoz.store.*;
import com.nsoz.util.NinjaUtils;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/** Trung Thu 2026. Public reward lists; unpublished rates retain configurable local weights. */
public class TrungThuNew extends TrungThu {
    public static final String COMMON_USED = "tt2026_common_used";
    public static final String PREMIUM_USED = "tt2026_premium_used";
    public static final String LANTERNS = "tt2026_lanterns";
    private static final String LANTERN_TIME = "tt2026_lantern_time";
    private static final String DAILY_DATE = "tt2026_daily_date";
    private static final int COIN = -2, YEN = -3;
    private final RandomCollection<Integer> escortRewards = new RandomCollection<>();
    private final RandomCollection<Integer> bossRewards = new RandomCollection<>();
    private final List<String> skipped = new ArrayList<>();
    private static final long DAY = 86400000L;
    private final List<com.nsoz.server.SpawnBoss> seasonalBosses = new ArrayList<>();
    private static final int[] CAKES = {ItemName.BANH_THAP_CAM, ItemName.BANH_DEO, ItemName.BANH_DAU_XANH, ItemName.BANH_PIA};
    private static final int[] LANTERN_ITEMS = {ItemName.LONG_DEN_TRON, ItemName.LONG_DEN_CA_CHEP, ItemName.LONG_DEN_NGOI_SAO, ItemName.LONG_DEN_MAT_TRANG};

    public TrungThuNew() {
        keyEventPoint.clear();
        Collections.addAll(keyEventPoint, COMMON_USED, PREMIUM_USED, LANTERNS, LANTERN_TIME, DAILY_DATE);
        for (int id : CAKES) keyEventPoint.add("tt2026_cake_" + id);
        endTime.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        endTime.set(2026, Calendar.OCTOBER, 15, 23, 59, 59);
        endTime.set(Calendar.MILLISECOND, 999);
        populateRewards();
    }

    @Override public EventPoint createEventPoint() {
        EventPoint ep = new EventPoint(); ep.addIfMissing(keyEventPoint); return ep;
    }
    private synchronized int nextLanternOrder() {
        return eventPoints.stream().mapToInt(e -> e.getPoint(LANTERN_TIME)).max().orElse(0) + 1;
    }
    // Event's constructor calls this before subclass initialization: populate only after construction.
    @Override public void initRandomItem() { }

    private boolean available(int id) {
        try {
            ItemTemplate t = ItemManager.getInstance().getItemTemplate(id);
            return t != null && t.id == id && !t.name.toLowerCase(Locale.ROOT).contains("obito")
                    && !t.name.toLowerCase(Locale.ROOT).contains("sakura") && !t.name.startsWith("[REMOVED]");
        } catch (IndexOutOfBoundsException e) { return false; }
    }
    private void add(RandomCollection<Integer> pool, int... ids) {
        for (int id : ids) {
            if (id < 0 || available(id)) pool.add(10, id);
            else skipped.add("item " + id);
        }
    }
    private void rare(RandomCollection<Integer> pool, int... ids) {
        for (int id : ids) if (available(id)) pool.add(1, id); else skipped.add("item " + id);
    }
    private void optional(RandomCollection<Integer> pool, String... names) {
        for (String name : names) {
            int id = findItem(name);
            if (id >= 0) pool.add(1, id); else skipped.add(name);
        }
    }
    private int findItem(String name) {
        for (int id = 0; id < 3000; id++) {
            try {
                ItemTemplate t = ItemManager.getInstance().getItemTemplate(id);
                if (t.id == id && t.name.equalsIgnoreCase(name) && available(id)) return id;
            } catch (IndexOutOfBoundsException e) { break; }
        }
        return -1;
    }
    private void populateRewards() {
        add(itemsRecFromCoinItem, ItemName.BANH_TRUNG_THU_PHONG_LOI, ItemName.BANH_TRUNG_THU_BANG_HOA,
            ItemName.THE_BAI_KINH_NGHIEM_GIA_TOC_SO, ItemName.THE_BAI_KINH_NGHIEM_GIA_TOC_TRUNG,
            ItemName.DA_DANH_VONG_CAP_1, ItemName.DA_DANH_VONG_CAP_2, ItemName.MANH_GIAY_JIRAI_,
            ItemName.MANH_DAY_CHUYEN_JIRAI_, ItemName.MANH_GIAY_JUMITO, ItemName.MANH_DAY_CHUYEN_JUMITO,
            ItemName.MANH_NGOC_BOI_JIRAI_, ItemName.MANH_NGOC_BOI_JUMITO, ItemName.BAT_BAO,
            ItemName.RUONG_BACH_NGAN, ItemName.RUONG_HUYEN_BI, ItemName.LANG_HON_THAO, ItemName.LANG_HON_MOC,
            ItemName.TUONG_LINH_THAO, ItemName.THONG_LINH_THAO, ItemName.HOA_TUYET, ItemName.NHAM_THACH_, ItemName.PHA_LE);
        rare(itemsRecFromCoinItem, ItemName.LONG_DEN_TRON, ItemName.LONG_DEN_CA_CHEP, ItemName.LONG_DEN_NGOI_SAO,
            ItemName.LONG_DEN_MAT_TRANG, ItemName.MAT_NA_SUPER_BROLY, ItemName.MAT_NA_VEGETA,
            ItemName.MAT_NA_ONNA_BUGEISHA, ItemName.MAT_NA_KUNOICHI, ItemName.NGU_HANH_HOA, ItemName.KIM_THAC_HO_PHU);
        optional(itemsRecFromCoinItem, "Quả chakra xanh", "Minh Giác Cốt Ngọc Hạ Giáp", "Minh Giác Táng Hồn Dao");
        add(itemsRecFromGoldItem, ItemName.RUONG_BACH_NGAN, ItemName.RUONG_HUYEN_BI, ItemName.BAT_BAO,
            ItemName.BANH_TRUNG_THU_PHONG_LOI, ItemName.BANH_TRUNG_THU_BANG_HOA,
            ItemName.NHAM_THACH_, ItemName.PHA_LE, ItemName.HOA_TUYET, ItemName.LONG_LUC_DAN,
            ItemName.MINH_MAN_DAN, ItemName.KHANG_THE_DAN, ItemName.SINH_MENH_DAN,
            ItemName.MANH_GIAY_JIRAI_, ItemName.MANH_DAY_CHUYEN_JIRAI_, ItemName.MANH_GIAY_JUMITO,
            ItemName.MANH_DAY_CHUYEN_JUMITO, ItemName.TU_TINH_THACH_SO_CAP, ItemName.TU_TINH_THACH_TRUNG_CAP,
            ItemName.BAO_HIEM_SO_CAP, ItemName.MANH_PHU_JIRAI_, ItemName.MANH_PHU_JUMITO, COIN);
        rare(itemsRecFromGoldItem, ItemName.LONG_DEN_TRON, ItemName.LONG_DEN_CA_CHEP, ItemName.LONG_DEN_NGOI_SAO,
            ItemName.LONG_DEN_MAT_TRANG, ItemName.HAKAIRO_YOROI, ItemName.LAN_SU_VU, ItemName.GA_TAY,
            ItemName.TOM_HUM, ItemName.CHIM_TINH_ANH, ItemName.VI_THU_LENH, ItemName.MAT_NA_THO,
            ItemName.MAT_NA_THO_NU, ItemName.MAT_NA_KUMA, ItemName.MAT_NA_INU, ItemName.KIM_THAC_DI_TRAO);
        optional(itemsRecFromGoldItem, "Quả chakra xanh", "Quả chakra vàng", "Bảo hiểm dung hợp");
        add(itemsRecFromGold2Item, ItemName.TRUNG, ItemName.BOT_MI, ItemName.HAT_SEN, ItemName.DUONG,
            ItemName.DAU_XANH, ItemName.MUT, ItemName.DA_CAP_5, ItemName.DA_CAP_6, ItemName.DA_CAP_7,
            ItemName.DA_CAP_8, ItemName.LUC_NGOC, ItemName.BANH_RANG, ItemName.MANH_GIAY_VUN,
            ItemName.BINH_MP_CAO_CAP, ItemName.BINH_HP_CAO_CAP, ItemName.HOA_TUYET, ItemName.NHAM_THACH_,
            ItemName.PHA_LE, ItemName.MANH_NGOC_BOI_JIRAI_, ItemName.MANH_NGOC_BOI_JUMITO,
            ItemName.THONG_LINH_THAO, ItemName.KIM_TUOC_THAO, ItemName.TU_HOA_DIA_DINH,
            ItemName.LONG_LUC_DAN, ItemName.MINH_MAN_DAN, ItemName.KHANG_THE_DAN, ItemName.SINH_MENH_DAN,
            ItemName.DA_DANH_VONG_CAP_1, ItemName.DA_DANH_VONG_CAP_2, ItemName.MANH_NHAN_JIRAI_, ItemName.MANH_NHAN_JUMITO, COIN, YEN);
        rare(itemsRecFromGold2Item, ItemName.HOAN_LUONG_CHI_THAO, ItemName.HAGGIS, ItemName.TUI_VAI_CAP_4,
            ItemName.GAY_MAT_TRANG, ItemName.GAY_TRAI_TIM);
        optional(itemsRecFromGold2Item, "Quả chakra xanh", "Quả chakra vàng", "Lồng đèn rồng",
            "Minh Giác Cốt Ngọc Tuyến", "Minh Giác Cốt Ngọc Trâm", "Minh Giác Thiên Hỏa Tiêu");
        add(escortRewards, ItemName.BI_KIP_KIEM_THUAT, ItemName.BI_KIP_TIEU_THUAT, ItemName.BI_KIP_KUNAI,
            ItemName.BI_KIP_CUNG, ItemName.BI_KIP_DAO, ItemName.BI_KIP_QUAT, ItemName.GA_TAY, ItemName.TOM_HUM,
            ItemName.THE_BAI_KINH_NGHIEM_GIA_TOC_TRUNG, ItemName.DIA_LANG_THAO, ItemName.TAM_LUC_DIEP,
            ItemName.CHIM_TINH_ANH, ItemName.HOAN_COT_CHI_CHU_SO_CAP, ItemName.BAO_HIEM_TRUNG_CAP,
            ItemName.BAO_HIEM_CAO_CAP, ItemName.LINH_LANG_HO_DIEP, ItemName.CHUYEN_TINH_THACH,
            ItemName.BO_CAI_THIEN_GIAM_XOC, ItemName.BO_CAI_THIEN_DANH_LUA, ItemName.BO_CAI_THIEN_DONG_CO,
            ItemName.KHI_BAO, ItemName.LANG_BAO, ItemName.BANH_RANG, ItemName.THUOC_CAI_TIEN,
            ItemName.KIM_TUOC_THAO, ItemName.TU_HOA_DIA_DINH, ItemName.NGU_HANH_HOA,
            ItemName.KHANG_THE_DAN, ItemName.SINH_MENH_DAN, ItemName.MINH_MAN_DAN, ItemName.LONG_LUC_DAN,
            ItemName.DA_CAP_6, ItemName.DA_CAP_7, COIN, YEN);
        rare(escortRewards, ItemName.THAI_DUONG_VO_CUC_KIEM, ItemName.THAI_DUONG_TANG_HON_DAO,
            ItemName.THAI_DUONG_CHIEN_LUC_DAO, ItemName.THAI_DUONG_THIEN_HOA_TIEU,
            ItemName.THAI_DUONG_BANG_THAN_CUNG, ItemName.THAI_DUONG_HOANG_PHONG_PHIEN,
            ItemName.XE_MAY, ItemName.LONG_DEN_CA_CHEP, ItemName.LONG_DEN_NGOI_SAO,
            ItemName.LONG_DEN_TRON, ItemName.XICH_NHAN_NGAN_LANG, ItemName.HARLEY_DAVIDSON, ItemName.TUYET_SA_NGU);
        optional(escortRewards, "IK", "Dung dịch cải tạo", "Minh Giác Vô Cực Kiếm", "Minh Giác Chiến Lục Đao", "Minh Giác Hoàng Phong Phiến");
        add(bossRewards, ItemName.XICH_NHAN_NGAN_LANG, ItemName.DA_CAP_6, ItemName.HAGGIS, YEN,
            ItemName.THAI_DUONG_COT_NGOC_PHU, ItemName.THAI_DUONG_COT_NGOC_BOI,
            ItemName.THAI_DUONG_COT_NGOC_GIOI, ItemName.THAI_DUONG_COT_NGOC_LIEN);
        optional(bossRewards, "Phiếu may mắn", "Minh Giác Cốt Ngọc Giới", "Minh Giác Băng Thần Cung");
    }

    public List<String> getSkippedRewards() { return skipped.stream().distinct().sorted().collect(Collectors.toList()); }
    private boolean active(Char p) {
        if (!isEnded()) return true;
        p.serverDialog("Sự kiện Trung Thu đã kết thúc."); return false;
    }
    private Item rewardItem(int id, long days) {
        Item item;
        if (id >= 632 && id <= 637) item = ItemFactory.getInstance().newItem9X(id);
        else if ((id >= 1111 && id <= 1116) || (id >= 1163 && id <= 1176)) item = ItemFactory.getInstance().newItem10X(id);
        else item = ItemFactory.getInstance().newItem(id);
        item.isLock = false;
        item.initExpire();
        if (days != 0) item.expire = days < 0 ? -1 : System.currentTimeMillis() + days * DAY;
        // Source's initExpire omits some event masks and lantern variants.
        if (days == 0 && (isLantern(id) || id == ItemName.MAT_NA_VEGETA || id == ItemName.MAT_NA_KUNOICHI
                || (id >= 1111 && id <= 1116) || (id >= 1163 && id <= 1176)
                || id == ItemName.HAKAIRO_YOROI || id == ItemName.MAT_NA_THO || id == ItemName.MAT_NA_THO_NU))
            item.expire = System.currentTimeMillis() + 3 * DAY;
        return item;
    }
    private boolean isLantern(int id) { for (int i : LANTERN_ITEMS) if (i == id) return true; return false; }
    public boolean handlesItem(int id) {
        for (int cake : CAKES) if (id == cake) return true;
        return id == ItemName.HOP_BANH_THUONG || id == ItemName.HOP_BANH_THUONG_HANG || id == ItemName.LONG_DEN;
    }
    private void give(Char p, int id, boolean premium) {
        if (id == COIN) { p.addCoin(1000000); return; }
        if (id == YEN) { p.addYen(1000000); return; }
        Item prize = rewardItem(id, 0);
        if (premium && isLantern(id) && NinjaUtils.nextInt(10000) == 0) prize.expire = -1;
        if (id == ItemName.BOT_MI || id == ItemName.TRUNG || id == ItemName.HAT_SEN || id == ItemName.DUONG
            || id == ItemName.DAU_XANH || id == ItemName.MUT) prize.isLock = true;
        if (id == ItemName.THONG_LINH_THAO) prize.setQuantity(NinjaUtils.nextInt(5, 10));
        p.addItemToBag(prize);
    }
    @Override public void useItem(Char p, Item item) {
        if (!active(p)) return;
        for (int cake : CAKES) if (item.id == cake) {
            EventPoint ep = p.getEventPoint();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
            int today = Integer.parseInt(dateFormat.format(new Date()));
            if (ep.getPoint(DAILY_DATE) != today) {
                ep.setPoint(DAILY_DATE, today);
                for (int id : CAKES) ep.setPoint("tt2026_cake_" + id, 0);
            }
            String key = "tt2026_cake_" + cake;
            if (ep.getPoint(key) >= 1500) { p.serverDialog("Mỗi ngày chỉ dùng 1.500 bánh mỗi loại."); return; }
            p.removeItem(item.index, 1, true);
            ep.addPoint(key, 1);
            p.addExp(NinjaUtils.nextInt(10000000, 15000000)); return;
        }
        if (p.getSlotNull() == 0) { p.warningBagFull(); return; }
        RandomCollection<Integer> pool;
        String counter;
        if (item.id == ItemName.HOP_BANH_THUONG) { pool = itemsRecFromCoinItem; counter = COMMON_USED; }
        else if (item.id == ItemName.HOP_BANH_THUONG_HANG) { pool = itemsRecFromGoldItem; counter = PREMIUM_USED; }
        else if (item.id == ItemName.LONG_DEN) { pool = itemsRecFromGold2Item; counter = LANTERNS; }
        else return;
        int prize = pool.next();
        p.removeItem(item.index, 1, true);
        if (item.id != ItemName.LONG_DEN) p.addExp(NinjaUtils.nextInt(10000000, 15000000));
        give(p, prize, item.id == ItemName.HOP_BANH_THUONG_HANG);
        p.getEventPoint().addPoint(counter, 1);
        if (counter.equals(LANTERNS)) {
            p.getEventPoint().setPoint(LANTERN_TIME, nextLanternOrder());
            p.zone.getService().addEffectAuto((byte) 7, (short) p.x, p.y, (byte) 0, (short) 1);
        }
    }
    @Override public boolean makeEventItem(Char p, int number, int[][] requires, int gold, int coin, int yen, int output) {
        if (!active(p) || number < 1 || number > 1000) { p.serverDialog("Số lượng từ 1 đến 1.000."); return false; }
        if (!available(output)) { p.serverDialog("Thiếu dữ liệu vật phẩm."); return false; }
        if (p.getSlotNull() < (ItemManager.getInstance().getItemTemplate(output).isUpToUp ? 1 : number)) { p.warningBagFull(); return false; }
        if (p.yen < number * yen || p.coin < number * coin || p.user.gold < number * gold) { p.serverDialog("Không đủ tiền làm bánh."); return false; }
        for (int[] requirement : requires) if (p.getQuantityItemById(requirement[0]) < requirement[1] * number) { p.serverDialog("Không đủ nguyên liệu."); return false; }
        p.addYen(-number * yen); p.addCoin(-number * coin); p.addGold(-number * gold);
        for (int[] requirement : requires) {
            int left = requirement[1] * number;
            for (Item item : p.getListItemByID(requirement[0])) {
                int take = Math.min(left, item.getQuantity()); p.removeItem(item.index, take, true); left -= take; if (left == 0) break;
            }
        }
        boolean locked = output != ItemName.HOP_BANH_THUONG && output != ItemName.HOP_BANH_THUONG_HANG;
        if (ItemManager.getInstance().getItemTemplate(output).isUpToUp) {
            Item item = ItemFactory.getInstance().newItem(output); item.setQuantity(number); item.isLock = locked; p.addItemToBag(item);
        } else {
            for (int i = 0; i < number; i++) { Item item = ItemFactory.getInstance().newItem(output); item.isLock = locked; p.addItemToBag(item); }
        }
        return true;
    }
    @Override public void banhThapCam(Char p, int amount) { if (active(p)) super.banhThapCam(p, amount); }
    @Override public void banhDeo(Char p, int amount) { if (active(p)) super.banhDeo(p, amount); }
    @Override public void banhDauXanh(Char p, int amount) { if (active(p)) super.banhDauXanh(p, amount); }
    @Override public void banhPia(Char p, int amount) { if (active(p)) super.banhPia(p, amount); }
    @Override public void hopBanhThuong(Char p, int amount) { if (active(p)) super.hopBanhThuong(p, amount); }
    @Override public void hopBanhThuongHang(Char p, int amount) { if (active(p)) super.hopBanhThuongHang(p, amount); }
    private void exchange(Char p, int input, int amount, int output, long days) {
        if (!active(p) || !available(output)) return;
        if (p.getSlotNull() < 1) { p.warningBagFull(); return; }
        if (p.getQuantityItemById(input) < amount) { p.serverDialog("Không đủ bánh đổi quà."); return; }
        // Consume across stacks, rather than counting inventory slots as cakes.
        int remaining = amount;
        for (Item item : p.getListItemByID(input)) {
            int take = Math.min(remaining, item.getQuantity()); p.removeItem(item.index, take, true);
            remaining -= take; if (remaining == 0) break;
        }
        Item prize = rewardItem(output, days);
        if (output == ItemName.BACH_HO) { prize.sys = 4; prize.randomOptionMount(); }
        p.addItemToBag(prize);
    }
    @Override public void doiBachHo(Char p) { exchange(p, ItemName.BANH_TRUNG_THU_PHONG_LOI, 30, ItemName.BACH_HO, 30); }
    @Override public void doiVuKhiThoiTrang(Char p, int id, int amount, long expire) {
        exchange(p, id, expire == EXPIRE_30_DAY ? 30 : 10,
            p.gender == 1 ? ItemName.GAY_MAT_TRANG : ItemName.GAY_TRAI_TIM, expire / DAY);
    }
    @Override public void doiLongDen(Char p, byte type, int index) {
        if (!active(p)) return;
        List<Item> list = p.getListItemByID(LANTERN_ITEMS);
        if (index < 0 || index >= list.size()) return;
        Item old = list.get(index);
        long eventStart = java.time.ZonedDateTime.of(2026, 9, 22, 15, 0, 0, 0, java.time.ZoneId.of("Asia/Ho_Chi_Minh")).toInstant().toEpochMilli(); // 2026-09-22 15:00 Asia/Ho_Chi_Minh
        if (old.getCreatedAt() < eventStart) { p.serverDialog("Chỉ đổi lồng đèn tạo trong Trung Thu 2026."); return; }
        int cost = type == DOI_BANG_LUONG ? 5 : 500000;
        if ((type == DOI_BANG_LUONG ? p.user.gold : p.coin) < cost) { p.serverDialog("Không đủ tiền đổi lồng đèn."); return; }
        int out = DOI_PHAN_TU.next();
        if (!available(out)) { p.serverDialog("Thiếu dữ liệu lồng đèn thời trang."); return; }
        Item prize = rewardItem(out, -1);
        prize.options.clear();
        for (ItemOption o : old.options) prize.options.add(new ItemOption(o.optionTemplate.id, o.param));
        if (!old.isForever()) prize.expire = Math.max(System.currentTimeMillis(), old.expire) + EXPIRE_30_DAY;
        if (type == DOI_BANG_LUONG) {
            if (NinjaUtils.nextInt(200) == 0) {
                prize.options.removeIf(o -> o.optionTemplate.id == ItemOptionName.MIEN_GIAM_SAT_THUONG_POINT_PERCENT_TYPE_8);
                prize.options.add(new ItemOption(ItemOptionName.MIEN_GIAM_SAT_THUONG_POINT_PERCENT_TYPE_8, 100));
            }
            prize.options.add(new ItemOption(ItemOptionName.KHONG_NHAN_EXP_TYPE_0, 1));
            p.addGold(-cost);
        } else p.addCoin(-cost);
        p.removeItem(old.index, 1, true); p.addItemToBag(prize); p.getService().endDlg(true);
    }
    @Override public void escortFinish(Char p) {
        if (!active(p)) return;
        if (p.getSlotNull() == 0) { p.warningBagFull(); return; }
        p.addExp(15000000); give(p, escortRewards.next(), false);
    }
    public int randomBossReward() { return bossRewards.next(); }
    public void bossReward(Char p) { if (p.getSlotNull() > 0) give(p, randomBossReward(), false); }

    @Override public void initMap(com.nsoz.map.zones.Zone zone) {
        super.initMap(zone);
        if (zone.map.id == MapName.TRUONG_HIROSAKI && zone.getNpc(NpcName.LONG_DEN_2) == null)
            zone.addNpc(com.nsoz.npc.NpcFactory.getInstance().newNpc(99, NpcName.LONG_DEN_2, 1307, 168, 0));
    }
    private void spawnSeasonalBosses() {
        if (!Event.isTrungThu()) return;
        for (com.nsoz.server.SpawnBoss boss : seasonalBosses) if (boss.getCurrMonster() != null) boss.getCurrMonster().die();
        seasonalBosses.clear();
        int[] maps = {MapName.RUNG_DAO_SAKURA, MapName.RUNG_TRUC_UTRA, MapName.RUNG_MISHIMA,
            MapName.RUNG_AOKIGAHARA, MapName.DOI_FUMIMEN, MapName.DOI_KOKORO, MapName.CANH_DONG_FUKI,
            MapName.SUOI_AKAGI, MapName.RUNG_MOSHIO, MapName.HANG_MEIRO, MapName.CANH_DONG_HIYA,
            MapName.HEM_NUI_TAKANA, MapName.DEN_AMATERASU, MapName.RUNG_KANASHII,
            MapName.CUA_BIEN_KAWAGUCHI, MapName.DONG_HACHI, MapName.RUNG_GIA};
        List<Integer> candidates = new ArrayList<>(); for (int id : maps) candidates.add(id);
        Collections.shuffle(candidates);
        for (int mobId : new int[]{MobName.HOA_KY_LAN, MobName.TU_HA_MA_THAN}) {
            for (int mapId : new ArrayList<>(candidates)) {
                com.nsoz.map.Map map = com.nsoz.map.MapManager.getInstance().find(mapId);
                if (map == null || map.getZones().isEmpty()) continue;
                List<com.nsoz.mob.Mob> mobs = map.getZones().get(0).getLivingMonsters();
                if (mobs.isEmpty()) continue;
                com.nsoz.mob.Mob position = mobs.get(NinjaUtils.nextInt(mobs.size()));
                com.nsoz.server.SpawnBoss boss = new com.nsoz.server.SpawnBoss(2026, map, position.x, position.y);
                boss.add(1, mobId); boss.spawn(); seasonalBosses.add(boss); candidates.remove(Integer.valueOf(mapId)); break;
            }
        }
    }
    @Override public void initStore() {
        if (isEnded()) return;
        for (int hour : new int[]{12, 19, 21, 23}) {
            java.time.ZonedDateTime now = java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh"));
            java.time.ZonedDateTime next = now.withHour(hour).withMinute(0).withSecond(0).withNano(0);
            if (!next.isAfter(now)) next = next.plusDays(1);
            java.util.concurrent.Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
                this::spawnSeasonalBosses, java.time.Duration.between(now, next).getSeconds(), 86400, java.util.concurrent.TimeUnit.SECONDS);
        }
        store(StoreManager.TYPE_FOOD, ItemName.GIAY_GOI_THUONG, 30000, 0);
        store(StoreManager.TYPE_FOOD, ItemName.LONG_DEN, 120000, 0);
        store(StoreManager.TYPE_MISCELLANEOUS, ItemName.GIAY_GOI_CAO_CAP, 0, 25);
        store(StoreManager.TYPE_MISCELLANEOUS, ItemName.GIAY_THONG_HANH, 0, 20);
        for (int id : new int[]{ItemName.NHAT_TU_LAM_PHONG, ItemName.THIEN_NGUYET_CHI_NU})
            if (available(id)) StoreManager.getInstance().addItem(StoreManager.TYPE_MISCELLANEOUS,
                ItemStore.builder().id(20000 + id).itemID(id).gold(100).isLock(true).expire(15 * DAY).options(new ArrayList<>()).build());
    }
    private void store(byte tab, int id, int coin, int gold) {
        if (available(id)) StoreManager.getInstance().addItem(tab, ItemStore.builder().id(20000 + id)
            .itemID(id).coin(coin).gold(gold).expire(-1).options(new ArrayList<>()).build());
    }
    private void input(Char p, String title, java.util.function.IntConsumer action) {
        p.setInput(new InputDialog(CMDInputDialog.EXECUTE, title, () -> {
            try { action.accept(p.getInput().intValue()); } catch (NumberFormatException e) { p.inputInvalid(); }
        })); p.getService().showInputDialog();
    }
    private void menuItem(Char p, String title, Runnable action) { p.menus.add(new Menu(CMDMenu.EXECUTE, title, action)); }
    @Override public void menu(Char p) {
        p.menus.clear();
        if (!isEnded()) {
            menuItem(p, "Làm bánh", () -> {
                p.menus.clear();
                menuItem(p, "Bánh Thập Cẩm", () -> input(p, "Số bánh", n -> banhThapCam(p, n)));
                menuItem(p, "Bánh Dẻo", () -> input(p, "Số bánh", n -> banhDeo(p, n)));
                menuItem(p, "Bánh Đậu xanh", () -> input(p, "Số bánh", n -> banhDauXanh(p, n)));
                menuItem(p, "Bánh Pía", () -> input(p, "Số bánh", n -> banhPia(p, n))); p.getService().openUIMenu();
            });
            menuItem(p, "Làm hộp bánh", () -> {
                p.menus.clear();
                menuItem(p, "Hộp bánh thường", () -> input(p, "Số hộp", n -> hopBanhThuong(p, n)));
                menuItem(p, "Hộp bánh thượng hạng", () -> input(p, "Số hộp", n -> hopBanhThuongHang(p, n))); p.getService().openUIMenu();
            });
            menuItem(p, "Đổi quà", () -> {
                p.menus.clear(); menuItem(p, "30 bánh phong lôi: Bạch hổ 30 ngày", () -> doiBachHo(p));
                menuItem(p, "10 bánh phong lôi: Gậy 7 ngày", () -> doiVuKhiThoiTrang(p, ItemName.BANH_TRUNG_THU_PHONG_LOI, 10, EXPIRE_7_DAY));
                menuItem(p, "30 bánh băng hỏa: Gậy 30 ngày", () -> doiVuKhiThoiTrang(p, ItemName.BANH_TRUNG_THU_BANG_HOA, 30, EXPIRE_30_DAY));
                p.getService().openUIMenu();
            });
            menuItem(p, "Đổi lồng đèn", () -> {
                p.menus.clear();
                menuItem(p, "500.000 xu", () -> lanternShop(p, Char.DOI_LONG_DEN_XU, "500.000 xu"));
                menuItem(p, "5 lượng", () -> lanternShop(p, Char.DOI_LONG_DEN_LUONG, "5 lượng")); p.getService().openUIMenu();
            });
            menuItem(p, "Nhận quà cán mốc", () -> milestoneMenu(p));
        }
        menuItem(p, "Đua TOP thả đèn", () -> {
            p.menus.clear(); menuItem(p, "Bảng xếp hạng", () -> viewTop(p, LANTERNS, "TOP thả đèn", "%d. %s: %s đèn"));
            menuItem(p, "Phần thưởng", () -> p.getService().showAlert("TOP Trung Thu 2026", topDescription()));
            if (isEnded()) menuItem(p, "Nhận thưởng", () -> topChoice(p)); p.getService().openUIMenu();
        });
        menuItem(p, "Vật phẩm thiếu dữ liệu", () -> p.getService().showAlert("Đã bỏ qua", String.join("\n", getSkippedRewards())));
        menuItem(p, "Hướng dẫn", () -> p.getService().showAlert("Trung Thu 2026",
            "Cấp 20, đánh quái lệch tối đa 7 cấp (nhãn phù: 10). Làm bánh tại Tiên Nữ, mỗi bánh 15.000 yên.\n"
            + "Mỗi loại bánh dùng tối đa 1.500/ngày. Hộp cần 4 loại bánh + giấy gói.\n"
            + "Tabemono: giấy thường và đèn; Gosho: giấy cao cấp, thông hành và thời trang.\n"
            + "TOP chỉ tính thả đèn, tối thiểu 5.000 đèn. Mốc hộp tính khi sử dụng, không tính khi làm.\n"
            + "Vật phẩm chưa có dữ liệu được bỏ qua; xác suất/EXP/tiền thưởng là cấu hình Kiss, bài gốc không công bố."));
    }
    private void lanternShop(Char p, byte command, String cost) {
        p.setCommandBox(command); p.getService().openUIShopTrungThu(p.getListItemByID(LANTERN_ITEMS), "Đổi lồng đèn " + cost, "Đổi");
    }
    private List<EventPoint> ranked() {
        return eventPoints.stream().filter(e -> e.getPoint(LANTERNS) >= 5000)
            .sorted(Comparator.comparingInt((EventPoint e) -> e.getPoint(LANTERNS)).reversed()
                .thenComparingInt(e -> e.getPoint(LANTERN_TIME)).thenComparingInt(EventPoint::getPlayerID))
            .limit(10).collect(Collectors.toList());
    }
    @Override public int getRanking(Char p, String key) {
        if (!LANTERNS.equals(key)) return 99;
        List<EventPoint> list = ranked();
        for (int i = 0; i < list.size(); i++) if (list.get(i).getPlayerID() == p.id) return i + 1;
        return 99;
    }
    @Override public void viewTop(Char p, String key, String title, String format) {
        StringBuilder text = new StringBuilder(); int rank = 1;
        for (EventPoint e : ranked()) text.append(String.format(format, rank++, e.getPlayerName(), NinjaUtils.getCurrency(e.getPoint(LANTERNS)))).append('\n');
        p.getService().showAlert(title, text.length() == 0 ? "Chưa có ninja đủ 5.000 đèn." : text.toString());
    }
    private String topDescription() {
        return "TOP 1: 3 rương huyền bí, gậy vĩnh viễn tự chọn, Hakairo Yoroi vĩnh viễn, 300 đá danh vọng cấp 2.\n"
            + "TOP 2: 2 rương huyền bí, gậy vĩnh viễn tự chọn, Hakairo Yoroi vĩnh viễn, 200 đá danh vọng cấp 2.\n"
            + "TOP 3-5: 2 rương bạch ngân, Lân sư vũ/Bạch hổ 6 tháng, thời trang Trung Thu vĩnh viễn, lồng đèn thời trang vĩnh viễn, 200 đá danh vọng cấp 2.\n"
            + "TOP 6-10: 2 Bát bảo, Lân sư vũ/Bạch hổ 6 tháng, pet Ứng Long 3 tháng có chống đồ sát, 200 đá danh vọng cấp 1.\n"
            + "Bỏ qua lồng đèn rồng và bí kíp TOP có chỉ số riêng do thiếu dữ liệu gốc. Thú dùng bộ chỉ số Kiss hiện có.";
    }
    private void topChoice(Char p) {
        if (!isEnded() || getRanking(p, LANTERNS) > 10 || p.getEventPoint().getRewarded(LANTERNS) != 0) { p.serverDialog("Không đủ điều kiện hoặc đã nhận thưởng."); return; }
        p.menus.clear(); int rank = getRanking(p, LANTERNS);
        if (rank <= 2) {
            menuItem(p, "Gậy mặt trăng", () -> claimTop(p, ItemName.GAY_MAT_TRANG, -1));
            menuItem(p, "Gậy trái tim", () -> claimTop(p, ItemName.GAY_TRAI_TIM, -1));
        } else {
            menuItem(p, "Lân sư vũ", () -> chooseLantern(p, ItemName.LAN_SU_VU));
            menuItem(p, "Bạch hổ", () -> chooseLantern(p, ItemName.BACH_HO));
        }
        p.getService().openUIMenu();
    }
    private void chooseLantern(Char p, int mount) {
        if (getRanking(p, LANTERNS) > 5) { claimTop(p, mount, -1); return; }
        p.menus.clear();
        for (int id : LANTERN_ITEMS) if (available(id)) menuItem(p, ItemManager.getInstance().getItemName(id), () -> claimTop(p, mount, id));
        p.getService().openUIMenu();
    }
    private void claimTop(Char p, int chosen, int lantern) {
        synchronized (p.getEventPoint()) {
            int rank = getRanking(p, LANTERNS);
            if (!isEnded() || rank > 10 || p.getEventPoint().getRewarded(LANTERNS) != 0) return;
            List<Item> prizes = new ArrayList<>();
            int chest = rank <= 2 ? ItemName.RUONG_HUYEN_BI : rank <= 5 ? ItemName.RUONG_BACH_NGAN : ItemName.BAT_BAO;
            for (int i = 0; i < (rank == 1 ? 3 : 2); i++) if (available(chest)) prizes.add(rewardItem(chest, -1));
            if (available(chosen)) {
                Item prize = rewardItem(chosen, rank <= 2 ? -1 : 180);
                if (rank >= 3) { prize.sys = 4; prize.randomOptionMount(); }
                prizes.add(prize);
            }
            if (rank <= 2 && available(ItemName.HAKAIRO_YOROI)) prizes.add(rewardItem(ItemName.HAKAIRO_YOROI, -1));
            if (rank >= 3 && rank <= 5) {
                int fashion = p.gender == 1 ? ItemName.NHAT_TU_LAM_PHONG : ItemName.THIEN_NGUYET_CHI_NU;
                if (available(fashion)) prizes.add(rewardItem(fashion, -1));
                if (available(lantern)) {
                    Item original = rewardItem(lantern, -1); original.randomOptionLongDen();
                    int convertedId = DOI_PHAN_TU.next();
                    if (available(convertedId)) { Item converted = rewardItem(convertedId, -1); converted.options.clear(); converted.options.addAll(original.options); prizes.add(converted); }
                }
            }
            if (rank >= 6 && available(ItemName.PET_UNG_LONG)) {
                Item pet = rewardItem(ItemName.PET_UNG_LONG, 90); pet.randomOption();
                pet.options.add(new ItemOption(ItemOptionName.MIEN_GIAM_SAT_THUONG_POINT_PERCENT_TYPE_8, 100)); prizes.add(pet);
            }
            int stone = rank <= 5 ? ItemName.DA_DANH_VONG_CAP_2 : ItemName.DA_DANH_VONG_CAP_1;
            if (available(stone)) { Item item = rewardItem(stone, -1); item.setQuantity(rank == 1 ? 300 : 200); prizes.add(item); }
            if (p.getSlotNull() < prizes.size()) { p.serverDialog("Hãy chừa " + prizes.size() + " ô trống."); return; }
            for (Item item : prizes) p.addItemToBag(item);
            p.getEventPoint().setRewarded(LANTERNS, 1);
        }
    }
    private void milestoneMenu(Char p) {
        p.menus.clear();
        menuItem(p, "3.000 hộp thường: vũ khí 10x", () -> claimMilestone(p, false));
        // Premium milestone requires seven-option costume + dragon lantern: unsupported data is skipped.
        menuItem(p, "3.000 hộp cao cấp", () -> p.serverDialog("Quà mốc cao cấp chưa có đủ dữ liệu mặt nạ 7 dòng/lồng đèn rồng, được bỏ qua."));
        p.getService().openUIMenu();
    }
    private void claimMilestone(Char p, boolean ignored) {
        synchronized (p.getEventPoint()) {
            if (!active(p) || p.getEventPoint().getPoint(COMMON_USED) < 3000 || p.getEventPoint().getRewarded(COMMON_USED) != 0) { p.serverDialog("Chưa đủ 3.000 hộp thường hoặc đã nhận."); return; }
            if (p.classId < 1 || p.classId > 6) { p.serverDialog("Hãy chọn hệ trước khi nhận vũ khí."); return; }
            int id = 1110 + p.classId;
            if (!available(id)) { p.serverDialog("Không có dữ liệu vũ khí 10x của hệ này."); return; }
            int chakra = findItem("Quả chakra vàng");
            if (p.getSlotNull() < (chakra >= 0 ? 2 : 1)) { p.warningBagFull(); return; }
            Item item = ItemFactory.getInstance().newItem10X(id, true); item.isLock = false; item.expire = System.currentTimeMillis() + 30 * DAY;
            p.addItemToBag(item);
            if (chakra >= 0) { Item fruit = rewardItem(chakra, -1); fruit.setQuantity(3); p.addItemToBag(fruit); }
            p.getEventPoint().setRewarded(COMMON_USED, 1);
        }
    }
}
