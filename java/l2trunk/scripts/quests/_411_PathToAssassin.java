package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _411_PathToAssassin extends Quest {
    //npc
    private final int TRISKEL = 30416;
    private final int LEIKAN = 30382;
    private final int ARKENIA = 30419;
    //mobs
    private final int MOONSTONE_BEAST = 20369;
    private final int CALPICO = 27036;
    //items
    private final int SHILENS_CALL_ID = 1245;
    private final int ARKENIAS_LETTER_ID = 1246;
    private final int LEIKANS_NOTE_ID = 1247;
    private final int ONYX_BEASTS_MOLAR_ID = 1248;
    private final int LEIKANS_KNIFE_ID = 1249;
    private final int SHILENS_TEARS_ID = 1250;
    private final int ARKENIA_RECOMMEND_ID = 1251;
    private final int IRON_HEART_ID = 1252;

    public _411_PathToAssassin() {
        super(false);

        addStartNpc(TRISKEL);

        addTalkId(LEIKAN,ARKENIA);

        addKillId(MOONSTONE_BEAST,CALPICO);

        addQuestItem(SHILENS_CALL_ID,
                LEIKANS_NOTE_ID,
                LEIKANS_KNIFE_ID,
                ARKENIA_RECOMMEND_ID,
                ARKENIAS_LETTER_ID,
                ONYX_BEASTS_MOLAR_ID,
                SHILENS_TEARS_ID);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("1".equals(event)) {
            if (st.player.getLevel() >= 18 && st.player.getClassId().id == 0x1f && st.getQuestItemsCount(IRON_HEART_ID) < 1) {
                st.setCond(1);
                st.start();
                st.playSound(SOUND_ACCEPT);
                st.giveItems(SHILENS_CALL_ID);
                htmltext = "triskel_q0411_05.htm";
            } else if (st.player.getClassId().id != 0x1f) {
                if (st.player.getClassId().id == 0x23)
                    htmltext = "triskel_q0411_02a.htm";
                else {
                    htmltext = "triskel_q0411_02.htm";
                    st.exitCurrentQuest();
                }
            } else if (st.player.getLevel() < 18) {
                htmltext = "triskel_q0411_03.htm";
                st.exitCurrentQuest();
            } else if (st.getQuestItemsCount(IRON_HEART_ID) > 0)
                htmltext = "triskel_q0411_04.htm";
        } else if (event.equalsIgnoreCase("30419_1")) {
            htmltext = "arkenia_q0411_05.htm";
            st.takeItems(SHILENS_CALL_ID);
            st.giveItems(ARKENIAS_LETTER_ID);
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if (event.equalsIgnoreCase("30382_1")) {
            htmltext = "guard_leikan_q0411_03.htm";
            st.takeItems(ARKENIAS_LETTER_ID);
            st.giveItems(LEIKANS_NOTE_ID);
            st.setCond(3);
            st.playSound(SOUND_MIDDLE);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == TRISKEL) {
            if (cond < 1) {
                if (st.getQuestItemsCount(IRON_HEART_ID) < 1)
                    htmltext = "triskel_q0411_01.htm";
                else
                    htmltext = "triskel_q0411_04.htm";
            } else if (cond == 7) {
                htmltext = "triskel_q0411_06.htm";
                st.takeItems(ARKENIA_RECOMMEND_ID);
                if (st.player.getClassId().occupation() == 0) {
                    st.giveItems(IRON_HEART_ID);
                    if (!st.player.isVarSet("prof1")) {
                        st.player.setVar("prof1");
                        st.addExpAndSp(228064, 16455);
                        st.giveItems(ADENA_ID, 81900);
                    }
                }
                st.exitCurrentQuest();
                st.playSound(SOUND_FINISH);
            } else if (cond == 2)
                htmltext = "triskel_q0411_07.htm";
            else if (cond == 1)
                htmltext = "triskel_q0411_11.htm";
            else if (cond < 7)
                if (cond < 5)
                    htmltext = "triskel_q0411_08.htm";
                else if (st.getQuestItemsCount(SHILENS_TEARS_ID) < 1)
                    htmltext = "triskel_q0411_09.htm";
                else
                    htmltext = "triskel_q0411_10.htm";
        } else if (npcId == ARKENIA) {
            if (cond == 1 && st.getQuestItemsCount(SHILENS_CALL_ID) > 0)
                htmltext = "arkenia_q0411_01.htm";
            else if (cond == 2 && st.getQuestItemsCount(ARKENIAS_LETTER_ID) > 0)
                htmltext = "arkenia_q0411_07.htm";
            else if (cond > 2 && cond < 5 && st.getQuestItemsCount(LEIKANS_NOTE_ID) > 0)
                htmltext = "arkenia_q0411_10.htm";
            else if (cond == 5 && st.getQuestItemsCount(LEIKANS_KNIFE_ID) > 0)
                htmltext = "arkenia_q0411_11.htm";
            else if (cond == 6 && st.getQuestItemsCount(SHILENS_TEARS_ID) > 0) {
                htmltext = "arkenia_q0411_08.htm";
                st.takeItems(SHILENS_TEARS_ID);
                st.takeItems(LEIKANS_KNIFE_ID);
                st.giveItems(ARKENIA_RECOMMEND_ID);
                st.setCond(7);
                st.playSound(SOUND_MIDDLE);
            } else if (cond == 7)
                htmltext = "arkenia_q0411_09.htm";
        } else if (npcId == LEIKAN)
            if (cond == 2 && st.getQuestItemsCount(ARKENIAS_LETTER_ID) > 0)
                htmltext = "guard_leikan_q0411_01.htm";
            else if (cond > 2 && cond < 4 && st.getQuestItemsCount(ONYX_BEASTS_MOLAR_ID) < 1) {
                htmltext = "guard_leikan_q0411_05.htm";
            } else if (cond > 2 && cond < 4 && st.getQuestItemsCount(ONYX_BEASTS_MOLAR_ID) < 10) {
                htmltext = "guard_leikan_q0411_06.htm";
            } else if (cond == 4 && st.getQuestItemsCount(ONYX_BEASTS_MOLAR_ID) > 9) {
                htmltext = "guard_leikan_q0411_07.htm";
                st.takeItems(ONYX_BEASTS_MOLAR_ID);
                st.takeItems(LEIKANS_NOTE_ID);
                st.giveItems(LEIKANS_KNIFE_ID);
                st.setCond(5);
                st.playSound(SOUND_MIDDLE);
            } else if (cond > 4 && cond < 7 && st.getQuestItemsCount(SHILENS_TEARS_ID) < 1) {
                htmltext = "guard_leikan_q0411_09.htm";
                if (cond == 6)
                    st.setCond(5);
            } else if (cond == 6 && st.getQuestItemsCount(SHILENS_TEARS_ID) > 0)
                htmltext = "guard_leikan_q0411_08.htm";

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == CALPICO) {
            if (cond == 5 && st.getQuestItemsCount(LEIKANS_KNIFE_ID) > 0 && st.getQuestItemsCount(SHILENS_TEARS_ID) < 1) {
                st.giveItems(SHILENS_TEARS_ID);
                st.playSound(SOUND_MIDDLE);
                st.setCond(6);
            }
        } else if (npcId == MOONSTONE_BEAST)
            if (cond == 3 && st.getQuestItemsCount(LEIKANS_NOTE_ID) > 0 && st.getQuestItemsCount(ONYX_BEASTS_MOLAR_ID) < 10) {
                st.giveItems(ONYX_BEASTS_MOLAR_ID);
                if (st.getQuestItemsCount(ONYX_BEASTS_MOLAR_ID) > 9) {
                    st.playSound(SOUND_MIDDLE);
                    st.setCond(4);
                } else
                    st.playSound(SOUND_ITEMGET);
            }
    }
}