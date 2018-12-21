package l2trunk.gameserver.scripts;

import l2trunk.commons.lang.reference.HardReference;
import l2trunk.commons.lang.reference.HardReferences;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.dao.CharacterDAO;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.instancemanager.ServerVariables;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
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

public class Functions {
    public HardReference<Player> self = HardReferences.emptyRef();
    public HardReference<NpcInstance> npc = HardReferences.emptyRef();

    public static void show(String text, Player self, NpcInstance npc, Object... arg) {
        if (text == null || self == null)
            return;

        NpcHtmlMessage msg = new NpcHtmlMessage(self, npc);

        if (text.endsWith(".html") || text.endsWith(".htm"))
            msg.setFile(text);
        else
            msg.setHtml(Strings.bbParse(text));

        if (arg != null && arg.length % 2 == 0) {
            for (int i = 0; i < arg.length; i = +2) {
                msg.replace(String.valueOf(arg[i]), String.valueOf(arg[i + 1]));
            }
        }

        self.sendPacket(msg);
    }

    public static void show(CustomMessage message, Player self) {
        show(message.toString(), self, null);
    }

    protected static void sendMessage(String text, Player self) {
        self.sendMessage(text);
    }

    public static void sendMessage(CustomMessage message, Player self) {
        self.sendMessage(message);
    }

    private static void npcSayInRange(NpcInstance npc, String text, int range) {
        npcSayInRange(npc, range, NpcString.NONE, text);
    }

    public static void npcSayInRange(NpcInstance npc, int range, NpcString fStringId, String... params) {
        if (npc == null)
            return;
        NpcSay cs = new NpcSay(npc, ChatType.ALL, fStringId, params);
        for (Player player : World.getAroundPlayers(npc, range, Math.max(range / 2, 200)))
            if (npc.getReflection() == player.getReflection())
                player.sendPacket(cs);
    }

    public static void npcSay(NpcInstance npc, String text) {
        npcSayInRange(npc, text, 1500);
    }

    public static void npcSay(NpcInstance npc, NpcString npcString, String... params) {
        npcSayInRange(npc, 1500, npcString, params);
    }

    public static void npcSayInRangeCustomMessage(NpcInstance npc, int range, String address, Object... replacements) {
        if (npc == null)
            return;
        for (Player player : World.getAroundPlayers(npc, range, Math.max(range / 2, 200)))
            if (npc.getReflection() == player.getReflection())
                player.sendPacket(new NpcSay(npc, ChatType.ALL, new CustomMessage(address, player, replacements).toString()));
    }

    public static void npcSayCustomMessage(NpcInstance npc, String address, Object... replacements) {
        npcSayInRangeCustomMessage(npc, 1500, address, replacements);
    }

    // private message
    public static void npcSayToPlayer(NpcInstance npc, Player player, String text) {
        npcSayToPlayer(npc, player, NpcString.NONE, text);
    }

    // private message
    private static void npcSayToPlayer(NpcInstance npc, Player player, NpcString npcString, String... params) {
        if (npc == null)
            return;
        player.sendPacket(new NpcSay(npc, ChatType.TELL, npcString, params));
    }

    public static void npcShout(NpcInstance npc, String text) {
        npcShout(npc, NpcString.NONE, text);
    }

    public static void npcShout(NpcInstance npc, NpcString npcString, String... params) {
        if (npc == null)
            return;
        NpcSay cs = new NpcSay(npc, ChatType.SHOUT, npcString, params);

        int rx = MapUtils.regionX(npc);
        int ry = MapUtils.regionY(npc);
        int offset = Config.SHOUT_OFFSET;

        for (Player player : GameObjectsStorage.getAllPlayers()) {
            if (player.getReflection() != npc.getReflection())
                continue;

            int tx = MapUtils.regionX(player);
            int ty = MapUtils.regionY(player);

            if (tx >= rx - offset && tx <= rx + offset && ty >= ry - offset && ty <= ry + offset)
                player.sendPacket(cs);
        }
    }

    public static void npcShoutCustomMessage(NpcInstance npc, String address, Object... replacements) {
        if (npc == null)
            return;

        int rx = MapUtils.regionX(npc);
        int ry = MapUtils.regionY(npc);
        int offset = Config.SHOUT_OFFSET;

        for (Player player : GameObjectsStorage.getAllPlayers()) {
            if (player.getReflection() != npc.getReflection())
                continue;

            int tx = MapUtils.regionX(player);
            int ty = MapUtils.regionY(player);

            if (tx >= rx - offset && tx <= rx + offset && ty >= ry - offset && ty <= ry + offset || npc.isInRange(player, Config.CHAT_RANGE))
                player.sendPacket(new NpcSay(npc, ChatType.SHOUT, new CustomMessage(address, player, replacements).toString()));
        }
    }

    public static void npcSay(NpcInstance npc, NpcString address, ChatType type, int range, String... replacements) {
        if (npc == null)
            return;
        for (Player player : World.getAroundPlayers(npc, range, Math.max(range / 2, 200))) {
            if (player.getReflection() == npc.getReflection())
                player.sendPacket(new NpcSay(npc, type, address, replacements));
        }
    }

    public static void addItem(Playable playable, int itemId, long count, String log) {
        ItemFunctions.addItem(playable, itemId, count, true, log);
    }

    protected static void addItem(Playable playable, int itemId, long count, boolean mess, String log) {
        ItemFunctions.addItem(playable, itemId, count, mess, log);
    }

    public static long getItemCount(Playable playable, int itemId) {
        return ItemFunctions.getItemCount(playable, itemId);
    }

    public static long removeItem(Playable playable, int itemId, long count, String log) {
        return ItemFunctions.removeItem(playable, itemId, count, true, log);
    }

    protected static boolean ride(Player player, int pet) {
        if (player.isMounted())
            player.setMount(0, 0, 0);

        if (player.getPet() != null) {
            player.sendPacket(SystemMsg.YOU_ALREADY_HAVE_A_PET);
            return false;
        }

        player.setMount(pet, 0, 0);
        return true;
    }

    public static void unRide(Player player) {
        if (player.isMounted())
            player.setMount(0, 0, 0);
    }

    public static void unSummonPet(Player player, boolean onlyPets) {
        Summon pet = player.getPet();
        if (pet == null)
            return;
        if (pet.isPet() || !onlyPets)
            pet.unSummon();
    }

    // @Deprecated
    // TODO [VISTALL] use NpcUtils
    public static NpcInstance spawn(Location loc, int npcId) {
        return spawn(loc, npcId, ReflectionManager.DEFAULT);
    }

    private static NpcInstance spawn(Location loc, int npcId, Reflection reflection) {
        return NpcUtils.spawnSingle(npcId, loc, reflection, 0);
    }

    protected static void SpawnNPCs(int npcId, List<Location> locations, List<SimpleSpawner> list) {
        for (Location location : locations) {
            NpcUtils.spawnSingle(npcId, location);
            if (list != null)
                list.add(new SimpleSpawner(npcId));
        }
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
        return ServerVariables.getString(name, "off").equalsIgnoreCase("on");
    }

    protected static boolean SetActive(String name, boolean active) {
        if (active == isActive(name))
            return false;
        if (active)
            ServerVariables.set(name, "on");
        else
            ServerVariables.unset(name);
        return true;
    }

    protected static boolean SimpleCheckDrop(Creature mob, Creature killer) {
        return mob != null && mob.isMonster() && !mob.isRaid() && killer != null && killer.getPlayer() != null && killer.getLevel() - mob.getLevel() < 9;
    }

    public static void sendDebugMessage(Player player, String message) {
        if (!player.isGM())
            return;
        player.sendMessage(message);
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
        mail.setReceiverId(receiver.getObjectId());
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
        int objectId = receiver != null ? receiver.getObjectId() : CharacterDAO.getObjectIdByName(receiverName);

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
        if (count < 1000000)
            return scount.substring(0, scount.length() - 3) + " k";
        if (count < 1000000000)
            return scount.substring(0, scount.length() - 6) + " kk";
        return scount.substring(0, scount.length() - 9) + " kkk";
    }

    protected void show(String text, Player self) {
        show(text, self, getNpc());
    }

    protected Player getSelf() {
        return self.get();
    }

    protected NpcInstance getNpc() {
        return npc.get();
    }
}