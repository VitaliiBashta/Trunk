package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.Location;

public final class _182_NewRecruits extends Quest {
    // NPC's
    private static final int Kekropus = 32138;
    private static final int Mother_Nornil = 32239;
    // ITEMS
    private static final int Ring_of_Devotion = 10124;
    private static final int Red_Crescent_Earring = 10122;
    // teleport to garden w/o instance initialize
    private static final Location TELEPORT_POSITION = new Location(-119544, 87176, -12619);

    public _182_NewRecruits() {
        super(false);

        addStartNpc(Kekropus);
        addTalkId(Kekropus);
        addTalkId(Mother_Nornil);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int cond = st.getCond();
        String htmltext = event;

        if (event.equals("take") && cond == 0) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
            htmltext = "kekropus_q182_2.htm";
        } else if (event.equals("mother_nornil_q182_2.htm") && cond == 1) {
            st.giveItems(Ring_of_Devotion, 2);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest(false);
        } else if (event.equals("mother_nornil_q182_3.htm") && cond == 1) {
            st.giveItems(Red_Crescent_Earring, 2);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest(false);
        } else if (event.equals("EnterNornilsGarden") && cond == 1)
            st.getPlayer().teleToLocation(TELEPORT_POSITION);

        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();

        if (npcId == Kekropus) {
            if (cond == 0 && st.getPlayer().getRace() != Race.kamael && st.getPlayer().getLevel() >= 17)
                htmltext = "kekropus_q182_1.htm";
            else {
                htmltext = "kekropus_q182_1a.htm";
                st.exitCurrentQuest(true);
            }
        } else if (npcId == Mother_Nornil)
            if (cond == 1)
                htmltext = "mother_nornil_q182_1.htm";
        return htmltext;
    }
}