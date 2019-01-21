package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.ReflectionUtils;

import java.util.List;

public final class _129_PailakaDevilsLegacy extends Quest {
    // NPC
    private static final int DISURVIVOR = 32498;
    private static final int SUPPORTER = 32501;
    private static final int DADVENTURER = 32508;
    private static final int DADVENTURER2 = 32511;
    private static final int CHEST = 32495;
    private static final List<Integer> Pailaka2nd = List.of(
            18623, 18624, 18625, 18626, 18627);

    // BOSS
    private static final int KAMS = 18629;
    private static final int ALKASO = 18631;
    private static final int LEMATAN = 18633;

    // ITEMS
    private static final int ScrollOfEscape = 736;
    private static final int SWORD = 13042;
    private static final int ENCHSWORD = 13043;
    private static final int LASTSWORD = 13044;
    private static final int KDROP = 13046;
    private static final int ADROP = 13047;
    private static final int KEY = 13150;
    private static final List<Integer> HERBS = List.of(
            8601, 8602, 8604, 8605);
    private static final List<Integer> CHESTDROP = List.of(
            13033, 13048, 13049);

    // REWARDS
    private static final int PBRACELET = 13295;
    private static final int izId = 44;

    public _129_PailakaDevilsLegacy() {
        super(false);

        addStartNpc(DISURVIVOR);
        addTalkId(SUPPORTER, DADVENTURER, DADVENTURER2);
        addKillId(KAMS, ALKASO, LEMATAN, CHEST);
        addKillId(Pailaka2nd);
        addQuestItem(SWORD, ENCHSWORD, LASTSWORD, KDROP, ADROP, KEY);
        addQuestItem(CHESTDROP);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        Player player = st.getPlayer();
        if (event.equalsIgnoreCase("Enter")) {
            enterInstance(player);
            return null;
        } else if (event.equalsIgnoreCase("32498-02.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("32498-05.htm")) {
            st.setCond(2);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("32501-03.htm")) {
            st.setCond(3);
            st.playSound(SOUND_MIDDLE);
            st.giveItems(SWORD, 1);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        int id = st.getState();
        Player player = st.getPlayer();
        if (npcId == DISURVIVOR) {
            if (cond == 0)
                if (player.getLevel() < 61 || player.getLevel() > 67) {
                    htmltext = "32498-no.htm";
                    st.exitCurrentQuest(true);
                } else
                    return "32498-01.htm";
            else if (id == COMPLETED)
                htmltext = "32498-no.htm";
            else if (cond == 1 || cond == 2)
                htmltext = "32498-06.htm";
            else
                htmltext = "32498-07.htm";
        } else if (npcId == SUPPORTER) {
            if (cond == 1 || cond == 2)
                htmltext = "32501-01.htm";
            else
                htmltext = "32501-04.htm";
        } else if (npcId == DADVENTURER) {
            if (st.getQuestItemsCount(SWORD) > 0 && st.getQuestItemsCount(KDROP) == 0)
                htmltext = "32508-01.htm";
            if (st.getQuestItemsCount(ENCHSWORD) > 0 && st.getQuestItemsCount(ADROP) == 0)
                htmltext = "32508-01.htm";
            if (st.getQuestItemsCount(SWORD) == 0 && st.getQuestItemsCount(KDROP) > 0)
                htmltext = "32508-05.htm";
            if (st.getQuestItemsCount(ENCHSWORD) == 0 && st.getQuestItemsCount(ADROP) > 0)
                htmltext = "32508-05.htm";
            if (st.getQuestItemsCount(SWORD) == 0 && st.getQuestItemsCount(ENCHSWORD) == 0)
                htmltext = "32508-05.htm";
            if (st.getQuestItemsCount(KDROP) == 0 && st.getQuestItemsCount(ADROP) == 0)
                htmltext = "32508-01.htm";
            if (player.getPet() != null)
                htmltext = "32508-04.htm";
            if (st.getQuestItemsCount(SWORD) > 0 && st.getQuestItemsCount(KDROP) > 0) {
                st.takeItems(SWORD, 1);
                st.takeItems(KDROP, 1);
                st.giveItems(ENCHSWORD, 1);
                htmltext = "32508-02.htm";
            }
            if (st.getQuestItemsCount(ENCHSWORD) > 0 && st.getQuestItemsCount(ADROP) > 0) {
                st.takeItems(ENCHSWORD, 1);
                st.takeItems(ADROP, 1);
                st.giveItems(LASTSWORD, 1);
                htmltext = "32508-03.htm";
            }
            if (st.getQuestItemsCount(LASTSWORD) > 0)
                htmltext = "32508-03.htm";
        } else if (npcId == DADVENTURER2)
            if (cond == 4) {
                if (player.getPet() != null)
                    htmltext = "32511-03.htm";
                else {
                    st.giveItems(ScrollOfEscape, 1);
                    st.giveItems(PBRACELET, 1);
                    st.addExpAndSp(10810000, 950000);
                    st.setCond(5);
                    st.setState(COMPLETED);
                    st.playSound(SOUND_FINISH);
                    st.exitCurrentQuest(false);
                    player.setVitality(Config.VITALITY_LEVELS.get(4));
                    player.getReflection().startCollapseTimer(60000);
                    htmltext = "32511-01.htm";
                }
            } else if (id == COMPLETED)
                htmltext = "32511-02.htm";
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        Player player = st.getPlayer();
        int npcId = npc.getNpcId();
        int refId = player.getReflectionId();
        if (npcId == KAMS && st.getQuestItemsCount(KDROP) == 0)
            st.giveItems(KDROP, 1);
        else if (npcId == ALKASO && st.getQuestItemsCount(ADROP) == 0)
            st.giveItems(ADROP, 1);
        else if (npcId == LEMATAN) {
            st.setCond(4);
            st.playSound(SOUND_MIDDLE);
            addSpawnToInstance(DADVENTURER2, new Location(84990, -208376, -3342, 55000), 0, refId);
        } else if (Pailaka2nd.contains(npcId)) {
            if (Rnd.get(100) < 80)
                st.dropItem(npc, Rnd.get(HERBS), Rnd.get(1, 2));
        } else if (npcId == CHEST)
            if (Rnd.get(100) < 80)
                st.dropItem(npc, Rnd.get(CHESTDROP), Rnd.get(1, 10));
        // TODO вернуть когда будут работать двери
        //else
        //	dropItem(npc, KEY, 1);
        return null;
    }

    private void enterInstance(Player player) {
        Reflection r = player.getActiveReflection();
        if (r != null) {
            if (player.canReenterInstance(izId))
                player.teleToLocation(r.getTeleportLoc(), r);
        } else if (player.canEnterInstance(izId)) {
            ReflectionUtils.enterReflection(player, izId);
        }
    }
}