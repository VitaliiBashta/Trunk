package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _317_CatchTheWind extends Quest {
    //NPCs
    private static final int Rizraell = 30361;
    //Quest items
    private static final int WindShard = 1078;
    //Mobs
    private static final int Lirein = 20036;
    private static final int LireinElder = 20044;

    public _317_CatchTheWind() {
        addStartNpc(Rizraell);
        //mob Drop
        addKillId(Lirein, LireinElder);
        addQuestItem(WindShard);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("rizraell_q0317_04.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("rizraell_q0317_08.htm".equalsIgnoreCase(event)) {
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == Rizraell)
            if (cond == 0) {
                if (st.player.getLevel() >= 18)
                    htmltext = "rizraell_q0317_03.htm";
                else {
                    htmltext = "rizraell_q0317_02.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1) {
                long count = st.getQuestItemsCount(WindShard);
                if (st.haveQuestItem(WindShard)) {
                    st.takeItems(WindShard);
                    st.giveAdena(40 * count);
                    htmltext = "rizraell_q0317_07.htm";
                } else
                    htmltext = "rizraell_q0317_05.htm";
            }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 1 && (npcId == Lirein || npcId == LireinElder)) {
            st.rollAndGive(WindShard, 1, 60);
        }
    }
}