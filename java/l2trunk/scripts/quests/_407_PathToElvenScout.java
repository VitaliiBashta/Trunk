package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _407_PathToElvenScout extends Quest {

    private static final int REORIA_LETTER2_ID = 1207;
    private static final int MORETTIS_HERB_ID = 1212;
    private static final int MORETTIS_LETTER_ID = 1214;
    private static final int PRIGUNS_LETTER_ID = 1215;
    private static final int HONORARY_GUARD_ID = 1216;
    private final int REISA = 30328;
    private final int MORETTI = 30337;
    private final int PIPPEN = 30426;
    private final int OL_MAHUM_SENTRY = 27031;
    private final int OL_MAHUM_PATROL = 20053;
    private final int PRIGUNS_TEAR_LETTER1_ID = 1208;
    private final int PRIGUNS_TEAR_LETTER2_ID = 1209;
    private final int PRIGUNS_TEAR_LETTER3_ID = 1210;
    private final int PRIGUNS_TEAR_LETTER4_ID = 1211;
    private final int REORIA_RECOMMENDATION_ID = 1217;
    private final int RUSTED_KEY_ID = 1293;

    public _407_PathToElvenScout() {
        super(false);

        addStartNpc(REISA);

        addTalkId(MORETTI,PIPPEN);

        addKillId(OL_MAHUM_SENTRY,OL_MAHUM_PATROL);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equals("1")) {
            if (st.player.getClassId().id == 0x12) {
                if (st.player.getLevel() >= 18) {
                    if (st.getQuestItemsCount(REORIA_RECOMMENDATION_ID) > 0) {
                        htmltext = "master_reoria_q0407_04.htm";
                        st.exitCurrentQuest();
                    } else {
                        htmltext = "master_reoria_q0407_05.htm";
                        st.giveItems(REORIA_LETTER2_ID, 1);
                        st.setCond(1);
                        st.start();
                        st.playSound(SOUND_ACCEPT);
                    }
                } else {
                    htmltext = "master_reoria_q0407_03.htm";
                    st.exitCurrentQuest();
                }
            } else if (st.player.getClassId().id == 0x16) {
                htmltext = "master_reoria_q0407_02a.htm";
                st.exitCurrentQuest();
            } else {
                htmltext = "master_reoria_q0407_02.htm";
                st.exitCurrentQuest();
            }
        } else if ("30337_1".equals(event)) {
            st.takeItems(REORIA_LETTER2_ID, 1);
            st.setCond(2);
            htmltext = "guard_moretti_q0407_03.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int id = st.getState();
        int cond = 0;
        if (id != CREATED)
            cond = st.getCond();
        if (npcId == REISA) {
            if (cond == 0)
                htmltext = "master_reoria_q0407_01.htm";
            else if (cond == 1)
                htmltext = "master_reoria_q0407_06.htm";
            else if (cond > 1 && !st.haveQuestItem(HONORARY_GUARD_ID))
                htmltext = "master_reoria_q0407_08.htm";
            else if (cond == 8 && st.haveQuestItem(HONORARY_GUARD_ID)) {
                htmltext = "master_reoria_q0407_07.htm";
                st.takeItems(HONORARY_GUARD_ID, 1);
                if (st.player.getClassId().occupation() == 0) {
                    st.giveItems(REORIA_RECOMMENDATION_ID);
                    if (!st.player.isVarSet("prof1")) {
                        st.player.setVar("prof1");
                        st.addExpAndSp(228064, 16455);
                        st.giveItems(ADENA_ID, 81900);
                    }
                }
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest();
            }
        } else if (npcId == MORETTI) {
            if (cond == 1)
                htmltext = "guard_moretti_q0407_01.htm";
            else if (cond == 2)
                htmltext = "guard_moretti_q0407_04.htm";
            else if (cond == 3) {
                if (st.haveAllQuestItems(PRIGUNS_TEAR_LETTER1_ID, PRIGUNS_TEAR_LETTER2_ID, PRIGUNS_TEAR_LETTER3_ID, PRIGUNS_TEAR_LETTER4_ID)) {
                    htmltext = "guard_moretti_q0407_06.htm";
                    st.takeItems(PRIGUNS_TEAR_LETTER1_ID, 1);
                    st.takeItems(PRIGUNS_TEAR_LETTER2_ID, 1);
                    st.takeItems(PRIGUNS_TEAR_LETTER3_ID, 1);
                    st.takeItems(PRIGUNS_TEAR_LETTER4_ID, 1);
                    st.giveItems(MORETTIS_HERB_ID);
                    st.giveItems(MORETTIS_LETTER_ID);
                    st.setCond(4);
                } else
                    htmltext = "guard_moretti_q0407_05.htm";
            } else if (cond == 7 && st.haveQuestItem(PRIGUNS_LETTER_ID)) {
                htmltext = "guard_moretti_q0407_07.htm";
                st.takeItems(PRIGUNS_LETTER_ID, 1);
                st.giveItems(HONORARY_GUARD_ID);
                st.setCond(8);
            } else if (cond > 8)
                htmltext = "guard_moretti_q0407_08.htm";
        } else if (npcId == PIPPEN)
            if (cond == 4) {
                htmltext = "prigun_q0407_01.htm";
                st.setCond(5);
            } else if (cond == 5)
                htmltext = "prigun_q0407_01.htm";
            else if (cond == 6 && st.haveAllQuestItems(RUSTED_KEY_ID, MORETTIS_HERB_ID, MORETTIS_LETTER_ID)) {
                htmltext = "prigun_q0407_02.htm";
                st.takeItems(RUSTED_KEY_ID, 1);
                st.takeItems(MORETTIS_HERB_ID, 1);
                st.takeItems(MORETTIS_LETTER_ID, 1);
                st.giveItems(PRIGUNS_LETTER_ID);
                st.setCond(7);
            } else if (cond == 7)
                htmltext = "prigun_q0407_04.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == OL_MAHUM_PATROL && cond == 2) {
            if (st.getQuestItemsCount(PRIGUNS_TEAR_LETTER1_ID) == 0) {
                st.giveItems(PRIGUNS_TEAR_LETTER1_ID);
                st.playSound(SOUND_ITEMGET);
                return;
            }
            if (st.getQuestItemsCount(PRIGUNS_TEAR_LETTER2_ID) == 0) {
                st.giveItems(PRIGUNS_TEAR_LETTER2_ID);
                st.playSound(SOUND_ITEMGET);
                return;
            }
            if (st.getQuestItemsCount(PRIGUNS_TEAR_LETTER3_ID) == 0) {
                st.giveItems(PRIGUNS_TEAR_LETTER3_ID);
                st.playSound(SOUND_ITEMGET);
                return;
            }
            if (st.getQuestItemsCount(PRIGUNS_TEAR_LETTER4_ID) == 0) {
                st.giveItems(PRIGUNS_TEAR_LETTER4_ID);
                st.playSound(SOUND_MIDDLE);
                st.setCond(3);
            }
        } else if (npcId == OL_MAHUM_SENTRY && cond == 5 && Rnd.chance(60)) {
            st.giveItems(RUSTED_KEY_ID);
            st.playSound(SOUND_MIDDLE);
            st.setCond(6);
        }
    }
}
