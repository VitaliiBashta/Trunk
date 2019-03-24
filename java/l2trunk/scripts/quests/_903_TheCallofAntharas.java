package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _903_TheCallofAntharas extends Quest {
    private static final int Theodric = 30755;
    private static final int BehemothDragonLeather = 21992;
    private static final int TaraskDragonsLeatherFragment = 21991;

    private static final int TaraskDragon = 29190;
    private static final int BehemothDragon = 29069;


    public _903_TheCallofAntharas() {
        super(PARTY_ALL);
        addStartNpc(Theodric);
        addKillId(TaraskDragon, BehemothDragon);
        addQuestItem(BehemothDragonLeather, TaraskDragonsLeatherFragment);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("theodric_q903_03.htm".equalsIgnoreCase(event)) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("theodric_q903_06.htm".equalsIgnoreCase(event)) {
            st.takeAllItems(BehemothDragonLeather, TaraskDragonsLeatherFragment);
            st.giveItems(21897); // Scroll: Antharas Call
            st.complete();
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest(this);
        }

        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npc.getNpcId() == Theodric) {
            switch (st.getState()) {
                case CREATED:
                    if (st.isNowAvailable()) {
                        if (st.player.getLevel() >= 83) {
                            if (st.haveQuestItem(3865))
                                htmltext = "theodric_q903_01.htm";
                            else
                                htmltext = "theodric_q903_00b.htm";
                        } else {
                            htmltext = "theodric_q903_00.htm";
                            st.exitCurrentQuest();
                        }
                    } else
                        htmltext = "theodric_q903_00a.htm";
                    break;
                case STARTED:
                    if (cond == 1)
                        htmltext = "theodric_q903_04.htm";
                    else if (cond == 2)
                        htmltext = "theodric_q903_05.htm";
                    break;
            }
        }

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int cond = st.getCond();
        if (cond == 1) {
            int npcId = npc.getNpcId();
            if (npcId == TaraskDragon) {
                st.giveItemIfNotHave(TaraskDragonsLeatherFragment);
            }
            if (npcId == BehemothDragon) {
                st.giveItemIfNotHave(BehemothDragonLeather);
            }
            if (st.haveAllQuestItems(BehemothDragonLeather,TaraskDragonsLeatherFragment))
                st.setCond(2);
        }
    }

}