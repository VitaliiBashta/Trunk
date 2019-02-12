package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _287_FiguringItOut extends Quest {
    private static final int Laki = 32742;
    private static final List<Integer> TantaClan = List.of(
            22768, 22769, 22770, 22771, 22772, 22773, 22774);
    private static final int VialofTantaBlood = 15499;

    public _287_FiguringItOut() {
        super(true);
        addStartNpc(Laki);
        addKillId(TantaClan);
        addQuestItem(VialofTantaBlood);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("laki_q287_03.htm".equalsIgnoreCase(event)) {
            st.setState(STARTED);
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("request_spitter".equalsIgnoreCase(event)) {
            if (st.haveQuestItem(VialofTantaBlood, 500)) {
                st.takeItems(VialofTantaBlood, 500);
                switch (Rnd.get(1, 5)) {
                    case 1:
                        st.giveItems(10381);
                        break;
                    case 2:
                        st.giveItems(10405);
                        break;
                    case 3:
                        st.giveItems(10405, 4);
                        break;
                    case 4:
                        st.giveItems(10405, 4);
                        break;
                    case 5:
                        st.giveItems(10405, 6);
                        break;
                }
                htmltext = "laki_q287_07.htm";
            } else
                htmltext = "laki_q287_06.htm";
        } else if ("request_moirai".equalsIgnoreCase(event)) {
            if (st.haveQuestItem(VialofTantaBlood,100)) {
                st.takeItems(VialofTantaBlood, 100);
                switch (Rnd.get(1, 16)) {
                    case 1:
                        st.giveItems(15776, 1);
                        break;
                    case 2:
                        st.giveItems(15779, 1);
                        break;
                    case 3:
                        st.giveItems(15782, 1);
                        break;
                    case 4:
                        st.giveItems(15785, 1);
                        break;
                    case 5:
                        st.giveItems(15788, 1);
                        break;
                    case 6:
                        st.giveItems(15812, 1);
                        break;
                    case 7:
                        st.giveItems(15813, 1);
                        break;
                    case 8:
                        st.giveItems(15814, 5);
                        break;
                    case 9:
                        st.giveItems(15646, 5);
                        break;
                    case 10:
                        st.giveItems(15649, 5);
                        break;
                    case 11:
                        st.giveItems(15652, 5);
                        break;
                    case 12:
                        st.giveItems(15655, 5);
                        break;
                    case 13:
                        st.giveItems(15658, 5);
                        break;
                    case 14:
                        st.giveItems(15772, 1);
                        break;
                    case 15:
                        st.giveItems(15773, 1);
                        break;
                    case 16:
                        st.giveItems(15771, 1);
                        break;
                }
                htmltext = "laki_q287_07.htm";
            } else
                htmltext = "laki_q287_10.htm";
        } else if ("continue".equalsIgnoreCase(event))
            htmltext = "laki_q287_08.htm";
        else if ("quit".equalsIgnoreCase(event)) {
            htmltext = "laki_q287_09.htm";
            st.exitCurrentQuest(true);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == Laki) {
            if (cond == 0) {
                if (st.player.getLevel() >= 82 && st.player.isQuestCompleted(_250_WatchWhatYouEat.class))
                    htmltext = "laki_q287_01.htm";
                else {
                    htmltext = "laki_q287_00.htm";
                    st.exitCurrentQuest(true);
                }
            } else if (cond == 1 && st.getQuestItemsCount(VialofTantaBlood) < 100)
                htmltext = "laki_q287_04.htm";
            else if (cond == 1 && st.haveQuestItem(VialofTantaBlood, 100))
                htmltext = "laki_q287_05.htm";
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 1) {
            if (TantaClan.contains(npcId) && Rnd.chance(60))
                st.giveItems(VialofTantaBlood, 1, true);
        }
    }
}