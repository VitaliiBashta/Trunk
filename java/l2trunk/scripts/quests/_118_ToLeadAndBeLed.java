package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _118_ToLeadAndBeLed extends Quest {
    private static final int PINTER = 30298;
    private static final int MAILLE_LIZARDMAN = 20919;
    private static final int BLOOD_OF_MAILLE_LIZARDMAN = 8062;
    private static final int KING_OF_THE_ARANEID = 20927;
    private static final int KING_OF_THE_ARANEID_LEG = 8063;
    private static final int D_CRY = 1458;
    private static final int D_CRY_COUNT_HEAVY = 721;
    private static final int D_CRY_COUNT_LIGHT_MAGIC = 604;

    private static final int CLAN_OATH_HELM = 7850;

    private static final int CLAN_OATH_ARMOR = 7851;
    private static final int CLAN_OATH_GAUNTLETS = 7852;
    private static final int CLAN_OATH_SABATON = 7853;

    private static final int CLAN_OATH_BRIGANDINE = 7854;
    private static final int CLAN_OATH_LEATHER_GLOVES = 7855;
    private static final int CLAN_OATH_BOOTS = 7856;

    private static final int CLAN_OATH_AKETON = 7857;
    private static final int CLAN_OATH_PADDED_GLOVES = 7858;
    private static final int CLAN_OATH_SANDALS = 7859;

    public _118_ToLeadAndBeLed() {
        addStartNpc(PINTER);

        addKillId(MAILLE_LIZARDMAN,KING_OF_THE_ARANEID);

        addQuestItem(BLOOD_OF_MAILLE_LIZARDMAN, KING_OF_THE_ARANEID_LEG);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        switch (event) {
            case "30298-02.htm":
                st.setCond(1);
                st.start();
                st.playSound(SOUND_ACCEPT);
                break;
            case "30298-05a.htm":
                st.set("choose", 1);
                st.setCond(3);
                break;
            case "30298-05b.htm":
                st.set("choose", 2);
                st.setCond(4);
                break;
            case "30298-05c.htm":
                st.set("choose", 3);
                st.setCond(5);
                break;
            case "30298-08.htm":
                int choose = st.getInt("choose");
                int need_dcry = choose == 1 ? D_CRY_COUNT_HEAVY : D_CRY_COUNT_LIGHT_MAGIC;
                if (st.getQuestItemsCount(D_CRY) < need_dcry)
                    return "30298-07.htm";
                st.setCond(7);
                st.takeItems(D_CRY, need_dcry);
                st.playSound(SOUND_MIDDLE);
                break;
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        if (npc.getNpcId() != PINTER)
            return "noquest";
        int state = st.getState();
        if (state == CREATED) {
            if (st.player.getLevel() < 19) {
                st.exitCurrentQuest();
                return "30298-00.htm";
            }
            if (st.player.getClanId() == 0) {
                st.exitCurrentQuest();
                return "30298-00a.htm";
            }
            if (st.player.getSponsor() == 0) {
                st.exitCurrentQuest();
                return "30298-00b.htm";
            }
            st.setCond(0);
            return "30298-01.htm";
        }

        int cond = st.getCond();

        if (cond == 1 && state == STARTED)
            return "30298-02a.htm";

        if (cond == 2 && state == STARTED) {
            if (st.getQuestItemsCount(BLOOD_OF_MAILLE_LIZARDMAN) < 10) {
                st.setCond(1);
                return "30298-02a.htm";
            }
            st.takeItems(BLOOD_OF_MAILLE_LIZARDMAN);
            return "30298-04.htm";
        }

        if (cond == 3 && state == STARTED)
            return "30298-05a.htm";

        if (cond == 4 && state == STARTED)
            return "30298-05b.htm";

        if (cond == 5 && state == STARTED)
            return "30298-05c.htm";

        if (cond == 7 && state == STARTED)
            return "30298-08a.htm";

        if (cond == 8 && state == STARTED) {
            if (st.getQuestItemsCount(KING_OF_THE_ARANEID_LEG) < 8) {
                st.setCond(7);
                return "30298-08a.htm";
            }
            st.takeItems(KING_OF_THE_ARANEID_LEG);
            st.giveItems(CLAN_OATH_HELM);
            int choose = st.getInt("choose");
            if (choose == 1) {
                st.giveItems(CLAN_OATH_ARMOR);
                st.giveItems(CLAN_OATH_GAUNTLETS);
                st.giveItems(CLAN_OATH_SABATON);
            } else if (choose == 2) {
                st.giveItems(CLAN_OATH_BRIGANDINE);
                st.giveItems(CLAN_OATH_LEATHER_GLOVES);
                st.giveItems(CLAN_OATH_BOOTS);
            } else {
                st.giveItems(CLAN_OATH_AKETON);
                st.giveItems(CLAN_OATH_PADDED_GLOVES);
                st.giveItems(CLAN_OATH_SANDALS);
            }
            st.unset("cond");
            st.playSound(SOUND_FINISH);
            st.finish();
            return "30298-09.htm";
        }

        return "noquest";
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == MAILLE_LIZARDMAN && st.getQuestItemsCount(BLOOD_OF_MAILLE_LIZARDMAN) < 10 && cond == 1 && Rnd.chance(50)) {
            st.giveItems(BLOOD_OF_MAILLE_LIZARDMAN);
            if (st.getQuestItemsCount(BLOOD_OF_MAILLE_LIZARDMAN) == 10) {
                st.playSound(SOUND_MIDDLE);
                st.setCond(2);
            } else
                st.playSound(SOUND_ITEMGET);
        } else if (npcId == KING_OF_THE_ARANEID && st.getQuestItemsCount(KING_OF_THE_ARANEID_LEG) < 8 && cond == 7 && Rnd.chance(50)) {
            st.giveItems(KING_OF_THE_ARANEID_LEG);
            if (st.getQuestItemsCount(KING_OF_THE_ARANEID_LEG) == 8) {
                st.playSound(SOUND_MIDDLE);
                st.setCond(8);
            } else
                st.playSound(SOUND_ITEMGET);
        }
    }
}