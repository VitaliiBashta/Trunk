package l2trunk.scripts.quests;

import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class _635_InTheDimensionalRift extends Quest {
    private static final int DIMENSION_FRAGMENT = 7079;

    // Rift Posts should take you back to the place you came from
    private static final List<Location> COORD = List.of(
            new Location(),            // filler
            new Location(-41572, 209731, -5087),    //Necropolis of Sacrifice
            new Location(42950, 143934, -5381),     //Catacomb of the Heretic
            new Location(45256, 123906, -5411),     //Pilgrim's Necropolis
            new Location(46192, 170290, -4981),     //Catacomb of the Branded
            new Location(111273, 174015, -5437),    //Necropolis of Worship
            new Location(-20221, -250795, -8160),   //Catacomb of Apostate
            new Location(-21726, 77385, -5171),     //Patriot's Necropolis
            new Location(140405, 79679, -5427),     //Catacomb of the Witch
            new Location(-52366, 79097, -4741),     //Necropolis of Devotion (ex Ascetics)
            new Location(118311, 132797, -4829),    //Necropolis of Martyrdom
            new Location(172185, -17602, -4901),    //Disciple's Necropolis
            new Location(83000, 209213, -5439),     //Saint's Necropolis
            new Location(-19500, 13508, -4901),     //Catacomb of Dark Omens
            new Location(113865, 84543, -6541));    //Catacomb of the Forbidden Path

    public _635_InTheDimensionalRift() {
        super(false);

        for (int npcId = 31494; npcId < 31508; npcId++)
            addStartNpc(npcId); // Dimensional Gate Keeper

        for (int npcId = 31095; npcId <= 31126; npcId++)
            if (npcId != 31111 && npcId != 31112 && npcId != 31113)
                addStartNpc(npcId); // Gatekeeper Ziggurat

        for (int npcId = 31488; npcId < 31494; npcId++)
            addTalkId(npcId); // Rift Post
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int id = st.getInt("id");
        String loc = st.get("loc");
        if (event.equals("5.htm"))
            if (id > 0 || loc != null) {
                if (isZiggurat(st.getPlayer().getLastNpc().getNpcId()) && !takeAdena(st)) {
                    htmltext = "Sorry...";
                    st.exitCurrentQuest(true);
                    return htmltext;
                }
                st.setState(STARTED);
                st.setCond(1);
                st.getPlayer().teleToLocation(-114790, -180576, -6781);
            } else {
                htmltext = "What are you trying to do?";
                st.exitCurrentQuest(true);
            }
        else if (event.equalsIgnoreCase("6.htm"))
            st.exitCurrentQuest(true);
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext;
        int npcId = npc.getNpcId();
        int id = st.getInt("id");
        String loc = st.get("loc");
        if (isZiggurat(npcId) || isKeeper(npcId)) {
            if (st.getPlayer().getLevel() < 20) {
                st.exitCurrentQuest(true);
                htmltext = "1.htm";
            } else if (st.getQuestItemsCount(DIMENSION_FRAGMENT) == 0) {
                if (isKeeper(npcId))
                    htmltext = "3.htm";
                else
                    htmltext = "3-ziggurat.htm";
            } else {
                st.set("loc", st.getPlayer().getLoc().toString());
                if (isKeeper(npcId))
                    htmltext = "4.htm";
                else
                    htmltext = "4-ziggurat.htm";
            }
        } else if (id > 0) {
            st.getPlayer().teleToLocation(COORD.get(id));
            htmltext = "7.htm";
            st.exitCurrentQuest(true);
        } else if (loc != null) {
            st.getPlayer().teleToLocation(Location.parseLoc(loc));
            htmltext = "7.htm";
            st.exitCurrentQuest(true);
        } else {
            htmltext = "Where are you from?";
            st.exitCurrentQuest(true);
        }
        return htmltext;
    }

    private boolean takeAdena(QuestState st) {
        int level = st.getPlayer().getLevel();
        int fee;
        if (level < 30)
            fee = 2000;
        else if (level < 40)
            fee = 4500;
        else if (level < 50)
            fee = 8000;
        else if (level < 60)
            fee = 12500;
        else if (level < 70)
            fee = 18000;
        else
            fee = 24500;
        if (!st.getPlayer().reduceAdena(fee, true, "_635_InTheDimensionalRift")) {
            st.getPlayer().sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            return false;
        }
        return true;
    }

    private boolean isZiggurat(int id) {
        return id >= 31095 && id <= 31126;
    }

    private boolean isKeeper(int id) {
        return id >= 31494 && id <= 31508;
    }
}