package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _164_BloodFiend extends Quest {
    //NPC
    private static final int Creamees = 30149;
    //Quest items
    private static final int KirunakSkull = 1044;
    //MOB
    private static final int Kirunak = 27021;

    public _164_BloodFiend() {
        super(false);

        addStartNpc(Creamees);
        addTalkId(Creamees);
        addKillId(Kirunak);
        addQuestItem(KirunakSkull);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("30149-04.htm")) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == Creamees)
            if (cond == 0) {
                if (st.player.getRace() == Race.darkelf) {
                    htmltext = "30149-00.htm";
                    st.exitCurrentQuest();
                } else if (st.player.getLevel() < 21) {
                    htmltext = "30149-02.htm";
                    st.exitCurrentQuest();
                } else
                    htmltext = "30149-03.htm";
            } else if (cond == 1)
                htmltext = "30149-05.htm";
            else if (cond == 2) {
                st.takeItems(KirunakSkull);
                st.giveItems(ADENA_ID, 42130, true);
                st.addExpAndSp(35637, 1854);
                htmltext = "30149-06.htm";
                st.playSound(SOUND_FINISH);
                st.finish();
            }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 1 && npcId == Kirunak) {
            if (st.getQuestItemsCount(KirunakSkull) == 0)
                st.giveItems(KirunakSkull);
            st.playSound(SOUND_MIDDLE);
            st.setCond(2);
            st.start();
        }
    }
}