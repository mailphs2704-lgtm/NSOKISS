package com.nsoz.event;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nsoz.constants.ItemName;
import com.nsoz.event.eventpoint.*;
import com.nsoz.item.*;
import com.nsoz.model.Char;
import com.nsoz.model.User;
import com.nsoz.network.Service;
import com.nsoz.lib.RandomCollection;
import java.lang.reflect.Field;
import java.util.*;

/** Run with java -cp target/test-classes:target/Nso-jar-with-dependencies.jar com.nsoz.event.TrungThu2026Check. */
public final class TrungThu2026Check {
    static void check(boolean ok, String label) { if (!ok) throw new AssertionError(label); }
    static class Player extends Char {
        long earnedExp; List<Item> prizes = new ArrayList<>(); String message;
        Player(int id, TrungThuNew event) throws Exception {
            super(id); name="player" + id; bag=new Item[24]; numberCellBag=24; yen=1000000;
            Field singleton = sun.misc.Unsafe.class.getDeclaredField("theUnsafe"); singleton.setAccessible(true);
            user=(User)((sun.misc.Unsafe)singleton.get(null)).allocateInstance(User.class); user.gold=10000;
            EventPoint ep=event.createEventPoint(); ep.setPlayerID(id); ep.setPlayerName(name); setEventPoint(ep); event.addEventPoint(ep);
        }
        @Override public void addExp(long exp) { earnedExp+=exp; }
        @Override public void addYen(long value) { yen+=value; }
        @Override public void addCoin(long value) { coin+=value; }
        @Override public void addGold(int value) { user.gold+=value; }
        @Override public boolean addItemToBag(Item item) { prizes.add(item); return true; }
        @Override public void removeItem(int index, int quantity, boolean update) { bag[index].reduce(quantity); if(!bag[index].has())bag[index]=null; }
        @Override public void serverDialog(String text) { message=text; }
        @Override public Service getService() { return new Service(null); }
        void put(int slot,int id,int amount) { Item item=new Item(id);item.index=slot;item.setQuantity(amount);bag[slot]=item; }
    }
    @SuppressWarnings("unchecked") static Set<Integer> entries(RandomCollection<Integer> pool) throws Exception {
        Field map=RandomCollection.class.getDeclaredField("map");map.setAccessible(true);
        return new HashSet<>(((NavigableMap<Double,Integer>)map.get(pool)).values());
    }
    public static void main(String[] args) throws Exception {
        ItemManager manager=ItemManager.getInstance();
        for(int i=0;i<1300;i++){ItemTemplate t=new ItemTemplate();t.id=i;t.name="fixture "+i;t.type=27;t.gender=2;t.fashion=-1;t.level=10;t.isUpToUp=true;manager.add(t);}
        for(int i=0;i<180;i++){ItemOptionTemplate t=new ItemOptionTemplate();t.id=i;t.name="#";manager.add(t);}
        manager.getItemTemplate(799).type=1; manager.getItemTemplate(800).type=1; manager.getItemTemplate(1111).type=1;
        manager.getItemTemplate(1200).name="[REMOVED] Obito";
        TrungThuNew event=new TrungThuNew();
        check(event.createEventPoint()!=null,"event point factory must never return null");
        check(!event.isEnded(),"2026 event window");
        Set<Integer> common=entries(event.getItemsRecFromCoinItem());
        check(common.contains(ItemName.LANG_HON_THAO),"official common reward included");
        check(!common.contains(ItemName.MAT_NA_SHIN_AH)&&!common.contains(ItemName.HOA_KY_LAN),"legacy rewards removed");
        check(entries(event.getItemsRecFromGoldItem()).contains(ItemName.VI_THU_LENH),"premium pool independent");
        check(entries(event.getItemsRecFromGold2Item()).contains(ItemName.GAY_MAT_TRANG),"lantern pool populated");
        Player p=new Player(1,event);p.put(0,ItemName.BOT_MI,10);p.put(1,ItemName.TRUNG,5);p.put(2,ItemName.HAT_SEN,5);p.put(3,ItemName.DUONG,5);p.put(4,ItemName.MUT,5);
        long before=p.yen;event.banhThapCam(p,1);check(p.yen==before-15000,"craft must charge yen");
        check(p.prizes.get(0).id==ItemName.BANH_THAP_CAM&&p.prizes.get(0).isLock,"cake output locked");
        check(p.bag[0]==null&&p.bag[4]==null,"ingredients consumed");
        p.put(0,ItemName.BANH_THAP_CAM,1501);for(int i=0;i<1501;i++)event.useItem(p,p.bag[0]);
        check(p.bag[0].getQuantity()==1,"1500 cake daily cap");
        check(p.getEventPoint().getPoint("tt2026_cake_"+ItemName.BANH_THAP_CAM)==1500,"daily counter");
        String json=new Gson().toJson(p.getEventPoint().getPoints());EventPoint restored=event.createEventPoint();
        restored.setPoints(new Gson().fromJson(json,new TypeToken<List<Point>>(){}.getType()));
        check(restored.getPoint("tt2026_cake_"+ItemName.BANH_THAP_CAM)==1500,"counter survives serialized reload");
        p.getEventPoint().setPoint("tt2026_daily_date",20000101);event.useItem(p,p.bag[0]);check(p.bag[0]==null,"counter resets on a new day");
        p.put(0,ItemName.HOP_BANH_THUONG,1);event.useItem(p,p.bag[0]);check(p.getEventPoint().getPoint(TrungThuNew.COMMON_USED)==1,"box milestones count use");
        Player second=new Player(2,event);p.getEventPoint().setPoint(TrungThuNew.LANTERNS,5000);second.getEventPoint().setPoint(TrungThuNew.LANTERNS,5000);
        p.getEventPoint().setPoint("tt2026_lantern_time",2);second.getEventPoint().setPoint("tt2026_lantern_time",1);
        check(event.getRanking(second,TrungThuNew.LANTERNS)==1&&event.getRanking(p,TrungThuNew.LANTERNS)==2,"earlier equal-score player wins");
        p.getEventPoint().setPoint(TrungThuNew.LANTERNS,4999);check(event.getRanking(p,TrungThuNew.LANTERNS)==99,"5000 lantern threshold");
        check(!entries(event.getItemsRecFromGoldItem()).contains(1200),"removed costume excluded");
        p.classId=1; p.getEventPoint().setPoint(TrungThuNew.COMMON_USED,3000);
        java.lang.reflect.Method milestone=TrungThuNew.class.getDeclaredMethod("claimMilestone",Char.class,boolean.class);milestone.setAccessible(true);
        int prizeCount=p.prizes.size();milestone.invoke(event,p,false);milestone.invoke(event,p,false);
        check(p.prizes.size()==prizeCount+1,"milestone cannot be claimed twice");
        check(p.prizes.get(p.prizes.size()-1).options.size()==11,"10x milestone full options");
        event.endTime.setTimeInMillis(System.currentTimeMillis()-1000);
        java.lang.reflect.Method top=TrungThuNew.class.getDeclaredMethod("claimTop",Char.class,int.class,int.class);top.setAccessible(true);
        top.invoke(event,second,ItemName.GAY_MAT_TRANG,-1);int topCount=second.prizes.size();top.invoke(event,second,ItemName.GAY_TRAI_TIM,-1);
        check(topCount>0&&second.prizes.size()==topCount,"TOP cannot be claimed twice");
        System.out.println("PASS: crafting, reward pools, daily cap/reload, milestones and ranking");
    }
}
