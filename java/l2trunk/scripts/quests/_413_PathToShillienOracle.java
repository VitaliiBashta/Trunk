package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _413_PathToShillienOracle extends Quest {
    //npc
    private final int SIDRA = 30330;
    private final int ADONIUS = 30375;
    private final int TALBOT = 30377;
    //mobs
    private final int ZOMBIE_SOLDIER = 20457;
    private final int ZOMBIE_WARRIOR = 20458;
    private final int SHIELD_SKELETON = 20514;
    private final int SKELETON_INFANTRYMAN = 20515;
    private final int DARK_SUCCUBUS = 20776;
    //items
    private final int SIDRAS_LETTER1_ID = 1262;
    private final int BLANK_SHEET1_ID = 1263;
    private final int BLOODY_RUNE1_ID = 1264;
    private final int GARMIEL_BOOK_ID = 1265;
    private final int PRAYER_OF_ADON_ID = 1266;
    private final int PENITENTS_MARK_ID = 1267;
    private final int ASHEN_BONES_ID = 1268;
    private final int ANDARIEL_BOOK_ID = 1269;
    private final int ORB_OF_ABYSS_ID = 1270;
    //ASHEN_BONES_DROP [moblist]
    private final int[] ASHEN_BONES_DROP = {
            ZOMBIE_SOLDIER,
            ZOMBIE_WARRIOR,
            SHIELD_SKELETON,
            SKELETON_INFANTRYMAN
    };

    public _413_PathToShillienOracle() {
        super(false);

        addStartNpc(SIDRA);

        addTalkId(ADONIUS);
        addTalkId(TALBOT);

        addKillId(DARK_SUCCUBUS);

        for (int i : ASHEN_BONES_DROP)
            addKillId(i);

        addQuestItem(ASHEN_BONES_ID);

        addQuestItem(SIDRAS_LETTER1_ID);
        addQuestItem(ANDARIEL_BOOK_ID);
        addQuestItem(PENITENTS_MARK_ID);
        addQuestItem(GARMIEL_BOOK_ID);
        addQuestItem(PRAYER_OF_ADON_ID);
        addQuestItem(BLANK_SHEET1_ID);
        addQuestItem(BLOODY_RUNE1_ID);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("1")) {
            htmltext = "master_sidra_q0413_06.htm";
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
            st.giveItems(SIDRAS_LETTER1_ID, 1);
        } else if (event.equalsIgnoreCase("413_1")) {
            if (st.getPlayer().getLevel() >= 18 && st.getPlayer().getClassId().id() == 0x26 && st.getQuestItemsCount(ORB_OF_ABYSS_ID) < 1)
                htmltext = "master_sidra_q0413_05.htm";
            else if (st.getPlayer().getClassId().id() != 0x26) {
                if (st.getPlayer().getClassId().id() == 0x2a)
                    htmltext = "master_sidra_q0413_02a.htm";
                else
                    htmltext = "master_sidra_q0413_03.htm";
            } else if (st.getPlayer().getLevel() < 18 && st.getPlayer().getClassId().id() == 0x26)
                htmltext = "master_sidra_q0413_02.htm";
            else if (st.getPlayer().getLevel() >= 18 && st.getPlayer().getClassId().id() == 0x26 && st.getQuestItemsCount(ORB_OF_ABYSS_ID) > 0)
                htmltext = "master_sidra_q0413_04.htm";
        } else if (event.equalsIgnoreCase("30377_1")) {
            htmltext = "magister_talbot_q0413_02.htm";
            st.takeItems(SIDRAS_LETTER1_ID, -1);
            st.giveItems(BLANK_SHEET1_ID, 5);
            st.playSound(SOUND_ITEMGET);
            st.setCond(2);
        } else if (event.equalsIgnoreCase("30375_1"))
            htmltext = "priest_adonius_q0413_02.htm";
        else if (event.equalsIgnoreCase("30375_2"))
            htmltext = "priest_adonius_q0413_03.htm";
        else if (event.equalsIgnoreCase("30375_3")) {
            htmltext = "priest_adonius_q0413_04.htm";
            st.takeItems(PRAYER_OF_ADON_ID, -1);
            st.giveItems(PENITENTS_MARK_ID, 1);
            st.playSound(SOUND_ITEMGET);
            st.setCond(5);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == SIDRA) {
            if (cond < 1)
                htmltext = "master_sidra_q0413_01.htm";
            else if (cond == 1)
                htmltext = "master_sidra_q0413_07.htm";
            else if (cond == 2 | cond == 3)
                htmltext = "master_sidra_q0413_08.htm";
            else if (cond > 3 && cond < 7)
                htmltext = "master_sidra_q0413_09.htm";
            else if (cond == 7 && st.getQuestItemsCount(ANDARIEL_BOOK_ID) > 0 && st.getQuestItemsCount(GARMIEL_BOOK_ID) > 0) {
                htmltext = "master_sidra_q0413_10.htm";
                st.exitCurrentQuest(true);
                if (st.getPlayer().getClassId().getLevel() == 1) {
                    st.giveItems(ORB_OF_ABYSS_ID, 1);
                    if (!st.getPlayer().getVarB("prof1")) {
                        st.getPlayer().setVar("prof1", "1", -1);
                        st.addExpAndSp(228064, 16455);
                        //FIXME [G1ta0] дать адены, только если первый чар на акке
                        st.giveItems(ADENA_ID, 81900);
                    }
                }
                st.playSound(SOUND_FINISH);
            }
        } else if (npcId == TALBOT) {
            if (cond == 1 && st.getQuestItemsCount(SIDRAS_LETTER1_ID) > 0)
                htmltext = "magister_talbot_q0413_01.htm";
            else if (cond == 2) {
                if (st.getQuestItemsCount(BLOODY_RUNE1_ID) < 1)
                    htmltext = "magister_talbot_q0413_03.htm";
                else if (st.getQuestItemsCount(BLOODY_RUNE1_ID) > 0)
                    htmltext = "magister_talbot_q0413_04.htm";
            } else if (cond == 3 && st.getQuestItemsCount(BLOODY_RUNE1_ID) > 4) {
                htmltext = "magister_talbot_q0413_05.htm";
                st.takeItems(BLOODY_RUNE1_ID, -1);
                st.giveItems(GARMIEL_BOOK_ID, 1);
                st.giveItems(PRAYER_OF_ADON_ID, 1);
                st.playSound(SOUND_ITEMGET);
                st.setCond(4);
            } else if (cond > 3 && cond < 7)
                htmltext = "magister_talbot_q0413_06.htm";
            else if (cond == 7)
                htmltext = "magister_talbot_q0413_07.htm";
        } else if (npcId == ADONIUS)
            if (cond == 4 && st.getQuestItemsCount(PRAYER_OF_ADON_ID) > 0)
                htmltext = "priest_adonius_q0413_01.htm";
            else if (cond == 5 && st.getQuestItemsCount(ASHEN_BONES_ID) < 1)
                htmltext = "priest_adonius_q0413_05.htm";
            else if (cond == 5 && st.getQuestItemsCount(ASHEN_BONES_ID) < 10)
                htmltext = "priest_adonius_q0413_06.htm";
            else if (cond == 6 && st.getQuestItemsCount(ASHEN_BONES_ID) > 9) {
                htmltext = "priest_adonius_q0413_07.htm";
                st.takeItems(ASHEN_BONES_ID, -1);
                st.takeItems(PENITENTS_MARK_ID, -1);
                st.giveItems(ANDARIEL_BOOK_ID, 1);
                st.playSound(SOUND_ITEMGET);
                st.setCond(7);
            } else if (cond == 7 && st.getQuestItemsCount(ANDARIEL_BOOK_ID) > 0)
                htmltext = "priest_adonius_q0413_08.htm";
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == DARK_SUCCUBUS)
            if (cond == 2 && st.getQuestItemsCount(BLANK_SHEET1_ID) > 0) {
                st.giveItems(BLOODY_RUNE1_ID, 1);
                st.takeItems(BLANK_SHEET1_ID, 1);
                if (st.getQuestItemsCount(BLANK_SHEET1_ID) < 1) {
                    st.playSound(SOUND_MIDDLE);
                    st.setCond(3);
                } else
                    st.playSound(SOUND_ITEMGET);
            }
        for (int i : ASHEN_BONES_DROP)
            if (npcId == i && cond == 5 && st.getQuestItemsCount(ASHEN_BONES_ID) < 10) {
                st.giveItems(ASHEN_BONES_ID, 1);
                if (st.getQuestItemsCount(ASHEN_BONES_ID) > 9) {
                    st.playSound(SOUND_MIDDLE);
                    st.setCond(6);
                } else
                    st.playSound(SOUND_ITEMGET);
            }
        return null;
    }
}