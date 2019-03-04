package l2trunk.gameserver.scripts;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.dao.CharacterDAO;
import l2trunk.gameserver.instancemanager.ServerVariables;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.instances.PetInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.mail.Mail;
import l2trunk.gameserver.network.serverpackets.ExNoticePostArrived;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.NpcSay;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.utils.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Functions {
    protected Player player = null;
    protected NpcInstance npc = null;

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setNpc(NpcInstance npc) {
        this.npc = npc;
    }

    public static void show(String text, Player self, NpcInstance npc) {
        show(text, self, npc, null);
    }

    public static void show(String text, Player self, NpcInstance npc, Map<String, String> replace) {
        if (text == null || self == null)
            return;

        NpcHtmlMessage msg = new NpcHtmlMessage(self, npc);

        if (text.endsWith(".html") || text.endsWith(".htm"))
            msg.setFile(text);
        else
            msg.setHtml(Strings.bbParse(text));

        if (replace != null)
            replace.forEach(msg::replace);

        self.sendPacket(msg);
    }

    public static void show(CustomMessage message, Player self) {
        show(message.toString(), self, null, null);
    }

    public static void npcSay(NpcInstance npc, String text) {
        npcSayInRange(npc, 1500, NpcString.NONE, text);
    }


    public static void npcSayInRange(NpcInstance npc, int range, NpcString fStringId) {
        npcSayInRange(npc, range, fStringId, "");
    }

    private static void npcSayInRange(NpcInstance npc, int range, NpcString fStringId, String text) {
        if (npc == null)
            return;
        NpcSay cs = new NpcSay(npc, ChatType.ALL, fStringId, text);
        World.getAroundPlayers(npc, range, Math.max(range / 2, 200))
                .filter(player -> npc.getReflection() == player.getReflection())
                .forEach(player -> player.sendPacket(cs));
    }


    public static void npcSay(NpcInstance npc, NpcString npcString) {
        npcSay(npc, npcString, "");
    }

    public static void npcSay(NpcInstance npc, NpcString npcString, String text) {
        npcSayInRange(npc, 1500, npcString, text);
    }

    public static void npcSayInRangeCustomMessage(NpcInstance npc, int range, String address, Object... replacements) {
        if (npc == null)
            return;
        World.getAroundPlayers(npc, range, Math.max(range / 2, 200))
                .filter(player -> npc.getReflection() == player.getReflection())
                .forEach(player -> player.sendPacket(new NpcSay(npc, ChatType.ALL, new CustomMessage(address, replacements).toString())));
    }

    public static void npcSayCustomMessage(NpcInstance npc, String address, Object... replacements) {
        npcSayInRangeCustomMessage(npc, 1500, address, replacements);
    }

    // private message
    public static void npcSayToPlayer(NpcInstance npc, Player player, String text) {
        player.sendPacket(new NpcSay(npc, ChatType.TELL, NpcString.NONE, text));
    }

    public static void npcShout(NpcInstance npc, String text) {
        npcShout(npc, NpcString.NONE, text);
    }

    public static void npcShout(NpcInstance npc, NpcString npcString) {
        npcShout(npc, npcString, "");
    }

    public static void npcShout(NpcInstance npc, NpcString npcString, String params) {
        if (npc == null)
            return;
        NpcSay cs = new NpcSay(npc, ChatType.SHOUT, npcString, params);

        int rx = MapUtils.regionX(npc);
        int ry = MapUtils.regionY(npc);
        int offset = Config.SHOUT_OFFSET;

        GameObjectsStorage.getAllPlayersStream()
                .filter(player -> player.getReflection() == npc.getReflection())
                .forEach(player -> {

                    int tx = MapUtils.regionX(player);
                    int ty = MapUtils.regionY(player);

                    if (tx >= rx - offset && tx <= rx + offset && ty >= ry - offset && ty <= ry + offset)
                        player.sendPacket(cs);
                });
    }

    public static void npcShoutCustomMessage(NpcInstance npc, String address) {
        if (npc == null)
            return;

        int rx = MapUtils.regionX(npc);
        int ry = MapUtils.regionY(npc);
        int offset = Config.SHOUT_OFFSET;

        GameObjectsStorage.getAllPlayersStream()
                .filter(p -> p.getReflection() == npc.getReflection())
                .forEach(p -> {
                    int tx = MapUtils.regionX(p);
                    int ty = MapUtils.regionY(p);

                    if (tx >= rx - offset && tx <= rx + offset && ty >= ry - offset && ty <= ry + offset || npc.isInRange(p, Config.CHAT_RANGE))
                        p.sendPacket(new NpcSay(npc, ChatType.SHOUT, new CustomMessage(address).toString()));
                });
    }

    public static void npcSay(NpcInstance npc, NpcString address, ChatType type, int range) {
        if (npc == null)
            return;
        World.getAroundPlayers(npc, range, Math.max(range / 2, 200))
                .filter(player -> player.getReflection() == npc.getReflection())
                .forEach(player ->
                        player.sendPacket(new NpcSay(npc, type, address, "")));
    }

    protected static boolean ride(Player player, int pet) {
        if (player.isMounted())
            player.dismount();

        if (player.getPet() != null) {
            player.sendPacket(SystemMsg.YOU_ALREADY_HAVE_A_PET);
            return false;
        }

        player.setMount(pet, 0, 0);
        return true;
    }

    protected static void unRide(Player player) {
        if (player.isMounted())
            player.dismount();
    }

    public static void unSummonPet(Player player, boolean onlyPets) {
        Summon pet = player.getPet();
        if (pet == null)
            return;
        if (pet instanceof PetInstance || !onlyPets)
            pet.unSummon();
    }

    protected static List<SimpleSpawner> SpawnNPCs(int npcId, List<Location> locations) {
        return locations.stream()
                .peek(location -> NpcUtils.spawnSingle(npcId, location))
                .map(location -> new SimpleSpawner(npcId))
                .collect(Collectors.toList());
    }

    protected static void SpawnNPCs(int npcId, List<Location> locations, List<SimpleSpawner> list, int respawn) {
        locations.forEach(loc -> {
            SimpleSpawner sp = new SimpleSpawner(npcId);
            sp.setLoc(loc);
            sp.setAmount(1);
            sp.setRespawnDelay(respawn);
            sp.init();
            if (list != null)
                list.add(sp);
        });
    }

    public static void deSpawnNPCs(List<SimpleSpawner> list) {
        list.forEach(Spawner::deleteAll);
        list.clear();
    }

    protected static boolean isActive(String name) {
        return ServerVariables.isSet(name);
    }

    protected static boolean setActive(String name, boolean active) {
        if (active == isActive(name))
            return false;
        if (active)
            ServerVariables.set(name);
        else
            ServerVariables.unset(name);
        return true;
    }

    protected static boolean simpleCheckDrop(Creature mob, Playable killer) {
        return mob instanceof MonsterInstance && !mob.isRaid() && killer != null && killer.getPlayer() != null && killer.getLevel() - mob.getLevel() < 9;
    }

    public static void sendDebugMessage(Player player, String message) {
        if (player.isGM()) player.sendMessage(message);
    }

    public static void sendSystemMail(Player receiver, String title, String body, Map<Integer, Long> items) {
        if (receiver == null || !receiver.isOnline())
            return;
        if (title == null)
            return;
        if (items.keySet().size() > 8)
            return;

        Mail mail = new Mail();
        mail.setSenderId(1);
        mail.setSenderName("Admin");
        mail.setReceiverId(receiver.objectId());
        mail.setReceiverName(receiver.getName());
        mail.setTopic(title);
        mail.setBody(body);
        for (Map.Entry<Integer, Long> itm : items.entrySet()) {
            ItemInstance item = ItemFunctions.createItem(itm.getKey());
            item.setLocation(ItemInstance.ItemLocation.MAIL);
            item.setCount(itm.getValue());
            item.save();
            mail.addAttachment(item);
        }
        mail.setType(Mail.SenderType.NEWS_INFORMER);
        mail.setUnread(true);
        mail.setExpireTime(720 * 3600 + (int) (System.currentTimeMillis() / 1000L));
        mail.save();

        receiver.sendPacket(ExNoticePostArrived.STATIC_TRUE);
        receiver.sendPacket(SystemMsg.THE_MAIL_HAS_ARRIVED);
    }

    public static boolean sendSystemMail(String receiverName, String title, String body, Map<Integer, Long> items) {
        if (title == null)
            return false;
        if (items.keySet().size() > 8)
            return false;

        Player receiver = GameObjectsStorage.getPlayer(receiverName);
        int objectId = receiver != null ? receiver.objectId() : CharacterDAO.getObjectIdByName(receiverName);

        if (objectId <= 0)
            return false;

        Mail mail = new Mail();
        mail.setSenderId(1);
        mail.setSenderName("Admin");
        mail.setReceiverId(objectId);
        mail.setReceiverName(receiverName);
        mail.setTopic(title);
        mail.setBody(body);
        for (Map.Entry<Integer, Long> itm : items.entrySet()) {
            ItemInstance item = ItemFunctions.createItem(itm.getKey());
            item.setLocation(ItemInstance.ItemLocation.MAIL);
            item.setCount(itm.getValue());
            item.save();
            mail.addAttachment(item);
        }
        mail.setType(Mail.SenderType.NEWS_INFORMER);
        mail.setUnread(true);
        mail.setExpireTime(720 * 3600 + (int) (System.currentTimeMillis() / 1000L));
        mail.save();

        if (receiver != null) {
            receiver.sendPacket(ExNoticePostArrived.STATIC_TRUE);
            receiver.sendPacket(SystemMsg.THE_MAIL_HAS_ARRIVED);
        }

        return true;
    }

    public static String GetStringCount(long count) {
        String scount = Long.toString(count);
        if (count < 1000)
            return scount;
        if (count < 1_000_000)
            return scount.substring(0, scount.length() - 3) + " k";
        if (count < 1_000_000_000)
            return scount.substring(0, scount.length() - 6) + " kk";
        return scount.substring(0, scount.length() - 9) + " kkk";
    }

    protected void show(String text, Player self) {
        show(text, self, npc);
    }


}