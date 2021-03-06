package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _042_HelpTheUncle extends Quest {
    private static final int WATERS = 30828;
    private static final int SOPHYA = 30735;

    private static final int TRIDENT = 291;
    private static final int MAP_PIECE = 7548;
    private static final int MAP = 7549;
    private static final int PET_TICKET = 7583;

    private static final int MONSTER_EYE_DESTROYER = 20068;
    private static final int MONSTER_EYE_GAZER = 20266;

    private static final int MAX_COUNT = 30;

    public _042_HelpTheUncle() {
        super(false);

        addStartNpc(WATERS);

        addTalkId(WATERS,SOPHYA);

        addKillId(MONSTER_EYE_DESTROYER,MONSTER_EYE_GAZER);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("1".equals(event)) {
            htmltext = "pet_manager_waters_q0042_0104.htm";
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("3".equals(event) && st.haveQuestItem(TRIDENT) ) {
            htmltext = "pet_manager_waters_q0042_0201.htm";
            st.takeItems(TRIDENT, 1);
            st.setCond(2);
        } else if ("4".equals(event) && st.haveQuestItem(MAP_PIECE, MAX_COUNT)) {
            htmltext = "pet_manager_waters_q0042_0301.htm";
            st.takeItems(MAP_PIECE);
            st.giveItems(MAP);
            st.setCond(4);
        } else if ("5".equals(event) && st.haveQuestItem(MAP)) {
            htmltext = "sophia_q0042_0401.htm";
            st.takeItems(MAP);
            st.setCond(5);
        } else if (event.equals("7")) {
            htmltext = "pet_manager_waters_q0042_0501.htm";
            st.giveItems(PET_TICKET);
            st.unset("cond");
            st.finish();
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int id = st.getState();
        int cond = st.getCond();
        if (id == CREATED) {
            if (st.player.getLevel() >= 25)
                htmltext = "pet_manager_waters_q0042_0101.htm";
            else {
                htmltext = "pet_manager_waters_q0042_0103.htm";
                st.exitCurrentQuest();
            }
        } else if (id == STARTED)
            if (npcId == WATERS) {
                if (cond == 1)
                    if (st.haveQuestItem(TRIDENT)) {
                        htmltext = "pet_manager_waters_q0042_0105.htm";
                    } else {
                        htmltext = "pet_manager_waters_q0042_0106.htm";
                    }
                else if (cond == 2)
                    htmltext = "pet_manager_waters_q0042_0204.htm";
                else if (cond == 3)
                    htmltext = "pet_manager_waters_q0042_0203.htm";
                else if (cond == 4)
                    htmltext = "pet_manager_waters_q0042_0303.htm";
                else if (cond == 5)
                    htmltext = "pet_manager_waters_q0042_0401.htm";
            } else if (npcId == SOPHYA)
                if (cond == 4 && st.haveQuestItem(MAP))
                    htmltext = "sophia_q0042_0301.htm";
                else if (cond == 5)
                    htmltext = "sophia_q0042_0402.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int cond = st.getCond();
        if (cond == 2) {
            long pieces = st.getQuestItemsCount(MAP_PIECE);
            if (pieces < MAX_COUNT - 1) {
                st.giveItems(MAP_PIECE);
                st.playSound(SOUND_ITEMGET);
            } else if (pieces == MAX_COUNT - 1) {
                st.giveItems(MAP_PIECE);
                st.playSound(SOUND_MIDDLE);
                st.setCond(3);
            }
        }
    }
}