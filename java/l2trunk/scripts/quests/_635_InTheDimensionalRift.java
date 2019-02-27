package l2trunk.scripts.quests;

import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.utils.Location;

import java.util.List;
import java.util.stream.IntStream;

public final class _635_InTheDimensionalRift extends Quest {
    private static final int DIMENSION_FRAGMENT = 7079;

    // Rift Posts should take you back to the place you came from
    private static final List<Location> COORD = List.of(
            Location.of(0, 0, 0),            // filler
            Location.of(-41572, 209731, -5087),    //Necropolis of Sacrifice
            Location.of(42950, 143934, -5381),     //Catacomb of the Heretic
            Location.of(45256, 123906, -5411),     //Pilgrim's Necropolis
            Location.of(46192, 170290, -4981),     //Catacomb of the Branded
            Location.of(111273, 174015, -5437),    //Necropolis of Worship
            Location.of(-20221, -250795, -8160),   //Catacomb of Apostate
            Location.of(-21726, 77385, -5171),     //Patriot's Necropolis
            Location.of(140405, 79679, -5427),     //Catacomb of the Witch
            Location.of(-52366, 79097, -4741),     //Necropolis of Devotion (ex Ascetics)
            Location.of(118311, 132797, -4829),    //Necropolis of Martyrdom
            Location.of(172185, -17602, -4901),    //Disciple's Necropolis
            Location.of(83000, 209213, -5439),     //Saint's Necropolis
            Location.of(-19500, 13508, -4901),     //Catacomb of Dark Omens
            Location.of(113865, 84543, -6541));    //Catacomb of the Forbidden Path

    public _635_InTheDimensionalRift() {
        super(false);

        addStartNpc(IntStream.rangeClosed(31494, 31508).toArray()); // Dimensional Gate Keeper
        addStartNpc(IntStream.rangeClosed(31095, 31110).toArray()); // Gatekeeper Ziggurat
        addStartNpc(IntStream.rangeClosed(31114, 31126).toArray()); // Gatekeeper Ziggurat

        addTalkId(IntStream.rangeClosed(31488, 31494).toArray()); // Rift Post
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int id = st.getInt("id");
        String loc = st.get("loc");
        if (event.equals("5.htm"))
            if (id > 0 || loc != null) {
                if (isZiggurat(st.player.getLastNpc().getNpcId()) && !takeAdena(st)) {
                    htmltext = "Sorry...";
                    st.exitCurrentQuest();
                    return htmltext;
                }
                st.start();
                st.setCond(1);
                st.player.teleToLocation(-114790, -180576, -6781);
            } else {
                htmltext = "What are you trying to do?";
                st.exitCurrentQuest();
            }
        else if ("6.htm".equalsIgnoreCase(event))
            st.exitCurrentQuest();
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext;
        int npcId = npc.getNpcId();
        int id = st.getInt("id");
        String loc = st.get("loc");
        if (isZiggurat(npcId) || isKeeper(npcId)) {
            if (st.player.getLevel() < 20) {
                st.exitCurrentQuest();
                htmltext = "1.htm";
            } else if (!st.haveQuestItem(DIMENSION_FRAGMENT)) {
                if (isKeeper(npcId))
                    htmltext = "3.htm";
                else
                    htmltext = "3-ziggurat.htm";
            } else {
                st.set("loc", st.player.getLoc().toString());
                if (isKeeper(npcId))
                    htmltext = "4.htm";
                else
                    htmltext = "4-ziggurat.htm";
            }
        } else if (id > 0) {
            st.player.teleToLocation(COORD.get(id));
            htmltext = "7.htm";
            st.exitCurrentQuest();
        } else if (loc != null) {
            st.player.teleToLocation(Location.of(loc));
            htmltext = "7.htm";
            st.exitCurrentQuest();
        } else {
            htmltext = "Where are you from?";
            st.exitCurrentQuest();
        }
        return htmltext;
    }

    private boolean takeAdena(QuestState st) {
        int level = st.player.getLevel();
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
        if (!st.player.reduceAdena(fee, true, "_635_InTheDimensionalRift")) {
            st.player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
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