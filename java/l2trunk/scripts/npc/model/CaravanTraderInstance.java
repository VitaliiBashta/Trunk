package l2trunk.scripts.npc.model;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.data.xml.holder.MultiSellHolder;
import l2trunk.gameserver.instancemanager.HellboundManager;
import l2trunk.gameserver.instancemanager.ServerVariables;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.ReflectionUtils;
import l2trunk.gameserver.utils.Util;
import l2trunk.scripts.quests._132_MatrasCuriosity;

import java.util.StringTokenizer;

import static l2trunk.gameserver.utils.ItemFunctions.addItem;
import static l2trunk.gameserver.utils.ItemFunctions.removeItem;

public final class CaravanTraderInstance extends NpcInstance {
    private static final int NativeTreasure = 9684;
    private static final int HolyWater = 9673;
    private static final int DarionsBadge = 9674;
    private static final int FirstMark = 9850;
    private static final int SecondMark = 9851;
    private static final int ThirdMark = 9852;
    private static final int ForthMark = 9853;

    private static final int ScorpionPoisonStinger = 10012;
    private static final int MarkOfBetrayal = 9676;
    private static final int MagicBottle = 9672;
    private static final int NativeHelmet = 9669;
    private static final int NativeTunic = 9670;
    private static final int NativePants = 9671;

    private static final int LifeForce = 9681;
    private static final int DimLifeForce = 9680;
    private static final int ContainedLifeForce = 9682;

    private static final int FieryDemonBloodSkill = 2357;

    public CaravanTraderInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (command.startsWith("Chat")) {
            int val = 0;
            try {
                val = Integer.parseInt(command.substring(5));
            } catch (IndexOutOfBoundsException | NumberFormatException ignored) {
            }
            showDialog(player, getHtmlPath(getNpcId(), val, player));
        } else if (command.startsWith("give_treasures")) //Jude
        {
            if (player.inventory.getCountOf(NativeTreasure) >= 40) {
                removeItem(player, NativeTreasure, 40, "CaravanTraderInstance");
                ServerVariables.set("HB_judesBoxes");
                showDialog(player, getHtmlPath(getNpcId(), 3, player));
            } else {
                showDialog(player, getHtmlPath(getNpcId(), 4, player));
            }
        } else if (command.startsWith("buy_holy_water")) //Bernarde
        {
            if (player.haveItem(HolyWater)) {
                showDialog(player, getHtmlPath(getNpcId(), 10, player));
                return;
            }
            if (player.haveItem(DarionsBadge, 5)) {
                removeItem(player, DarionsBadge, 5, "CaravanTraderInstance");
                addItem(player, HolyWater, 1);
                showDialog(player, getHtmlPath(getNpcId(), 6, player));
            } else {
                showDialog(player, getHtmlPath(getNpcId(), 3, player));
            }
        } else if (command.startsWith("one_treasure")) //Bernarde
        {
            if (player.haveItem(NativeTreasure)  && !ServerVariables.isSet("HB_bernardBoxes")) {
                removeItem(player, NativeTreasure, 1, "CaravanTraderInstance");
                ServerVariables.set("HB_bernardBoxes");
                showDialog(player, getHtmlPath(getNpcId(), 8, player));
            } else {
                showDialog(player, getHtmlPath(getNpcId(), 9, player));
            }
        } else if (command.startsWith("request_1_badge")) //Falk
        {
            if (hasProperMark(player, 1)) //has any mark
            {
                showDialog(player, getHtmlPath(getNpcId(), 3, player));
                return;
            }
            if (player.haveItem(DarionsBadge, 20)) {//trade mark
                removeItem(player, DarionsBadge, 20, "CaravanTraderInstance");
                addItem(player, FirstMark, 1);
                showDialog(player, getHtmlPath(getNpcId(), 4, player));
            } else
            // not enough badges
            {
                showDialog(player, getHtmlPath(getNpcId(), 5, player));
            }
        } else if (command.startsWith("bdgc"))
            try {
                StringTokenizer st = new StringTokenizer(command);
                st.nextToken();
                if (!st.hasMoreTokens())
                    return;
                String param = st.nextToken();
                if (param.length() < 1 || !Util.isNumber(param)) {
                    player.sendMessage("Incorrect count");
                    return;
                }
                int val = Integer.parseInt(param);
                if (val <= 0) {
                    player.sendMessage("Incorrect count");
                    return;
                }
                if (player.getInventory().getCountOf(DarionsBadge) < val) {
                    showDialog(player, getHtmlPath(getNpcId(), 2, player));
                    return;
                }
                removeItem(player, DarionsBadge, val, "CaravanTraderInstance");
                HellboundManager.addConfidence(val * 10L);
                showDialog(player, getHtmlPath(getNpcId(), 3, player));
            } catch (NumberFormatException nfe) {
                showDialog(player, getHtmlPath(getNpcId(), 4, player));
            }
        else if (command.startsWith("buy_magic_bottle")) // Kief
        {
            if (player.getInventory().getCountOf(ScorpionPoisonStinger) >= 20 && hasProperMark(player, 1)) {
                removeItem(player, ScorpionPoisonStinger, 20, "CaravanTraderInstance");
                addItem(player, MagicBottle, 1);
                showDialog(player, getHtmlPath(getNpcId(), 6, player));
            } else
            // not enough
            {
                showDialog(player, getHtmlPath(getNpcId(), 7, player));
            }
        } else if (command.startsWith("cntf"))
            try {
                int val = Integer.parseInt(command.substring(5));
                if (val <= 0)
                    return;

                switch (val) {
                    case 1:
                        if (player.getInventory().getCountOf(LifeForce) < 10) {
                            showDialog(player, getHtmlPath(getNpcId(), 2, player));
                            return;
                        }
                        removeItem(player, LifeForce, 10, "CaravanTraderInstance");
                        HellboundManager.addConfidence(100);
                        showDialog(player, getHtmlPath(getNpcId(), 3, player));
                        break;
                    case 2:
                        if (player.getInventory().getCountOf(DimLifeForce) < 5) {
                            showDialog(player, getHtmlPath(getNpcId(), 2, player));
                            return;
                        }
                        removeItem(player, DimLifeForce, 5, "CaravanTraderInstance");
                        HellboundManager.addConfidence(100);
                        showDialog(player, getHtmlPath(getNpcId(), 3, player));
                        break;
                    case 3:
                        if (player.getInventory().getCountOf(ContainedLifeForce) < 1) {
                            showDialog(player, getHtmlPath(getNpcId(), 2, player));
                            return;
                        }
                        removeItem(player, ContainedLifeForce, 1, "CaravanTraderInstance");
                        HellboundManager.addConfidence(50);
                        showDialog(player, getHtmlPath(getNpcId(), 3, player));
                        break;
                }
            } catch (NumberFormatException ignored) {
            }
        else if (command.startsWith("getc"))
            try {
                int val = Integer.parseInt(command.substring(5));
                if (val <= 0)
                    return;

                if (player.getInventory().getCountOf(DarionsBadge) < 10) {
                    showDialog(player, getHtmlPath(getNpcId(), 3, player));
                    return;
                }
                switch (val) {
                    case 1:
                        removeItem(player, DarionsBadge, 10, "CaravanTraderInstance");
                        addItem(player, NativeHelmet, 1);
                        showDialog(player, getHtmlPath(getNpcId(), 4, player));
                        break;
                    case 2:
                        removeItem(player, DarionsBadge, 10, "CaravanTraderInstance");
                        addItem(player, NativeTunic, 1);
                        showDialog(player, getHtmlPath(getNpcId(), 4, player));
                        break;
                    case 3:
                        removeItem(player, DarionsBadge, 10, "CaravanTraderInstance");
                        addItem(player, NativePants, 1);
                        showDialog(player, getHtmlPath(getNpcId(), 4, player));
                        break;
                }
            } catch (NumberFormatException ignored) {
            }
        else if (command.startsWith("get_second")) // Hude
        {
            if (player.getInventory().getCountOf(FirstMark) >= 1 && player.getInventory().getCountOf(MarkOfBetrayal) >= 30 && player.getInventory().getCountOf(ScorpionPoisonStinger) >= 60) {
                removeItem(player, FirstMark, 1, "CaravanTraderInstance");
                removeItem(player, MarkOfBetrayal, 30, "CaravanTraderInstance");
                removeItem(player, ScorpionPoisonStinger, 60, "CaravanTraderInstance");
                addItem(player, SecondMark, 1);
                showDialog(player, getHtmlPath(getNpcId(), 3, player));
            } else {
                showDialog(player, getHtmlPath(getNpcId(), 4, player));
            }
        } else if (command.startsWith("secret_med")) // Hude
        {
            MultiSellHolder.INSTANCE.SeparateAndSend(250980014, player, 0);
        } else if (command.startsWith("get_third")) // Hude
        {
            if (player.getInventory().getCountOf(SecondMark) >= 1 && player.getInventory().getCountOf(LifeForce) >= 56 && player.getInventory().getCountOf(ContainedLifeForce) >= 14) {
                removeItem(player, SecondMark, 1, "CaravanTraderInstance");
                removeItem(player, LifeForce, 56, "CaravanTraderInstance");
                removeItem(player, ContainedLifeForce, 14, "CaravanTraderInstance");
                addItem(player, ThirdMark, 1);
                addItem(player, 9994, 1); // Hellbound Map
                showDialog(player, getHtmlPath(getNpcId(), 6, player));
            } else {
                showDialog(player, getHtmlPath(getNpcId(), 4, player));
            }
        } else if (command.startsWith("s80_trade")) // Hude
        {
            MultiSellHolder.INSTANCE.SeparateAndSend(250980013, player, 0);
        } else if (command.startsWith("try_open_door")) // Traitor
        {
            if (player.inventory.getCountOf(MarkOfBetrayal) >= 10) {
                removeItem(player, MarkOfBetrayal, 10, "CaravanTraderInstance");
                ReflectionUtils.getDoor(19250003).openMe();
                ReflectionUtils.getDoor(19250004).openMe();
                ThreadPoolManager.INSTANCE.schedule(() -> {
                    ReflectionUtils.getDoor(19250003).closeMe();
                    ReflectionUtils.getDoor(19250004).closeMe();
                }, 60 * 1000L);
            } else {
                showDialog(player, getHtmlPath(getNpcId(), 4, player));
            }
        } else if (command.startsWith("supply_badges")) // Native Slave
        {
            if (player.getInventory().getCountOf(DarionsBadge) >= 5) {
                removeItem(player, DarionsBadge, 5, "CaravanTraderInstance");
                HellboundManager.addConfidence(20);
                showDialog(player, getHtmlPath(getNpcId(), 2, player));
            } else {
                showDialog(player, getHtmlPath(getNpcId(), 3, player));
            }
        } else if (command.startsWith("tully_entrance")) // Deltuva
        {
            if (player.isQuestCompleted(_132_MatrasCuriosity.class)) {
                player.teleToLocation(new Location(17947, 283205, -9696));
            } else {
                showDialog(player, getHtmlPath(getNpcId(), 1, player));
            }
        } else if (command.startsWith("infinitum_entrance")) // Jerian
        {
            if (player.getParty() == null || !player.getParty().isLeader(player)) {
                showDialog(player, getHtmlPath(getNpcId(), 1, player));
                return;
            }

            if (player.getParty().getMembersStream()
                    .filter(member -> (!isInRange(member, 500) || member.getEffectList().getEffectsBySkillId(FieryDemonBloodSkill) == null))
                    .peek(member -> showDialog(player, getHtmlPath(getNpcId(), 2, player)))
                    .findFirst().isPresent()) {
                return;
            }
            player.getParty().getMembersStream().forEach(m -> m.teleToLocation(new Location(-22204, 277056, -15045)));
        } else if (command.startsWith("tully_dorian_entrance")) { // Dorian
            if (player.getParty() == null || !player.getParty().isLeader(player)) {
                showDialog(player, getHtmlPath(getNpcId(), 2, player));
                return;
            }

            if (player.getParty().getMembersStream()
                    .filter(member -> !isInRange(member, 500) || !member.isQuestCompleted(_132_MatrasCuriosity.class))
                    .peek(member -> showDialog(player, getHtmlPath(getNpcId(), 1, player)))
                    .findFirst().isPresent())
                return;


            player.getParty().getMembersStream().forEach(member -> member.teleToLocation(new Location(-13400, 272827, -15304)));
        } else if (command.startsWith("enter_urban")) // Kanaf - urban area instance
        {
            Reflection r = player.getActiveReflection();
            if (r != null) {
                if (player.canReenterInstance(2))
                    player.teleToLocation(r.getTeleportLoc(), r);
            } else if (player.canEnterInstance(2)) {
                ReflectionUtils.enterReflection(player, 2);
            }
        } else
            super.onBypassFeedback(player, command);
    }

    @Override
    public void showChatWindow(Player player, int val) {
        String htmlpath = null;
        switch (getNpcId()) {
            case 32356: // Jude
                if (HellboundManager.getHellboundLevel() <= 1)
                    htmlpath = getHtmlPath(getNpcId(), 0, player);
                else if (HellboundManager.getHellboundLevel() == 5)
                    htmlpath = getHtmlPath(getNpcId(), 5, player);
                else if (!ServerVariables.isSet("HB_judesBoxes"))
                    htmlpath = getHtmlPath(getNpcId(), 1, player);
                else
                    htmlpath = getHtmlPath(getNpcId(), 2, player);
                break;
            case 32300: // Bernarde
                if (player.getTransformation() != 101)
                    htmlpath = getHtmlPath(getNpcId(), 5, player);
                else if (HellboundManager.getHellboundLevel() < 2)
                    htmlpath = getHtmlPath(getNpcId(), 0, player);
                else if (HellboundManager.getHellboundLevel() == 2)
                    htmlpath = getHtmlPath(getNpcId(), 1, player);
                else if (HellboundManager.getHellboundLevel() == 3 && !ServerVariables.isSet("HB_bernardBoxes"))
                    htmlpath = getHtmlPath(getNpcId(), 2, player);
                else if (HellboundManager.getHellboundLevel() >= 3)
                    htmlpath = getHtmlPath(getNpcId(), 7, player);
                break;
            case 32297: // Falk
                if (HellboundManager.getHellboundLevel() <= 1)
                    htmlpath = getHtmlPath(getNpcId(), 0, player);
                else if (HellboundManager.getHellboundLevel() > 1)
                    htmlpath = getHtmlPath(getNpcId(), 1, player);
                break;
            case 32354: // Kief
                if (HellboundManager.getHellboundLevel() <= 1)
                    htmlpath = getHtmlPath(getNpcId(), 0, player);
                else if (HellboundManager.getHellboundLevel() == 2 || HellboundManager.getHellboundLevel() == 3)
                    htmlpath = getHtmlPath(getNpcId(), 1, player);
                else if (HellboundManager.getHellboundLevel() == 6)
                    htmlpath = getHtmlPath(getNpcId(), 9, player);
                else if (HellboundManager.getHellboundLevel() == 7)
                    htmlpath = getHtmlPath(getNpcId(), 10, player);
                else if (HellboundManager.getHellboundLevel() > 7)
                    htmlpath = getHtmlPath(getNpcId(), 5, player);
                else
                    htmlpath = getHtmlPath(getNpcId(), 8, player);
                break;
            case 32345: // Buron
                if (HellboundManager.getHellboundLevel() <= 1)
                    htmlpath = getHtmlPath(getNpcId(), 0, player);
                else if (HellboundManager.getHellboundLevel() == 5)
                    htmlpath = getHtmlPath(getNpcId(), 7, player);
                else if (HellboundManager.getHellboundLevel() == 6)
                    htmlpath = getHtmlPath(getNpcId(), 5, player);
                else if (HellboundManager.getHellboundLevel() == 8)
                    htmlpath = getHtmlPath(getNpcId(), 6, player);
                else
                    htmlpath = getHtmlPath(getNpcId(), 1, player);
                break;
            case 32355: // Solomon
                if (HellboundManager.getHellboundLevel() == 5)
                    htmlpath = getHtmlPath(getNpcId(), 1, player);
                else
                    htmlpath = getHtmlPath(getNpcId(), 0, player);
                break;
            case 32298: // Hude
                if (HellboundManager.getHellboundLevel() <= 1)
                    htmlpath = getHtmlPath(getNpcId(), 0, player);
                else if (!hasProperMark(player, 1))
                    htmlpath = getHtmlPath(getNpcId(), 1, player);
                else if (player.haveItem(FirstMark) )
                    htmlpath = getHtmlPath(getNpcId(), 2, player);
                else if (player.haveItem(SecondMark) )
                    htmlpath = getHtmlPath(getNpcId(), 5, player);
                else if (player.haveItem(ThirdMark))
                    htmlpath = getHtmlPath(getNpcId(), 8, player);
                break;
            case 32364: // Traitor
                if (HellboundManager.getHellboundLevel() == 5)
                    htmlpath = getHtmlPath(getNpcId(), 0, player);
                else
                    htmlpath = getHtmlPath(getNpcId(), 6, player);
                break;
            case 32357: // Native Slave
                if (HellboundManager.getHellboundLevel() == 9)
                    htmlpath = getHtmlPath(getNpcId(), 1, player);
                else if (HellboundManager.getHellboundLevel() == 10)
                    htmlpath = getHtmlPath(getNpcId(), 4, player);
                else
                    htmlpath = getHtmlPath(getNpcId(), 0, player);
                break;
            case 32346: // Kanaf
                if (HellboundManager.getHellboundLevel() >= 10)
                    htmlpath = getHtmlPath(getNpcId(), 0, player);
                else
                    htmlpath = getHtmlPath(getNpcId(), 3, player);
                break;
            case 32313: // Deltuva
                if (HellboundManager.getHellboundLevel() >= 11)
                    htmlpath = getHtmlPath(getNpcId(), 0, player);
                else
                    htmlpath = getHtmlPath(getNpcId(), 2, player);
                break;
            case 32302: // Jerian
                if (HellboundManager.getHellboundLevel() >= 11)
                    htmlpath = getHtmlPath(getNpcId(), 0, player);
                else
                    htmlpath = getHtmlPath(getNpcId(), 3, player);
                break;
            case 32373: // Dorian
                if (HellboundManager.getHellboundLevel() >= 11)
                    htmlpath = getHtmlPath(getNpcId(), 0, player);
                else
                    htmlpath = getHtmlPath(getNpcId(), 3, player);
                break;
        }
        NpcHtmlMessage html = new NpcHtmlMessage(player, this);
        html.setFile(htmlpath);
        html.replace("%objectId%", objectId());
        html.replace("%npcname%", getName());
        player.sendPacket(html);
    }

    @Override
    public String getHtmlPath(int npcId, int val, Player player) {
        String pom;
        if (val == 0)
            pom = "" + npcId;
        else
            pom = npcId + "-" + val;
        return "hellbound/" + pom + ".htm";
    }

    private void showDialog(Player player, String path) {
        NpcHtmlMessage html = new NpcHtmlMessage(player, this);
        html.setFile(path);
        html.replace("%objectId%", objectId());
        player.sendPacket(html);
    }

    private boolean hasProperMark(Player player, int mark) {
        switch (mark) {
            case 1:
                if (player.haveAnyItem(FirstMark,SecondMark,ThirdMark,ForthMark))
                    return true;
                break;
            case 2:
                if (player.haveAnyItem(SecondMark,ThirdMark,ForthMark) )
                    return true;
                break;
            case 3:
                if (player.haveAnyItem(ThirdMark,ForthMark))
                    return true;
                break;
            case 4:
                if (player.haveItem(ForthMark) )
                    return true;
                break;
            default:
                break;
        }
        return false;
    }

}