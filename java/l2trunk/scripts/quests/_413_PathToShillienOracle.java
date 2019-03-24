package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

import static l2trunk.gameserver.model.base.ClassId.darkMage;
import static l2trunk.gameserver.model.base.ClassId.shillienOracle;

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
    private final List<Integer> ASHEN_BONES_DROP = List.of(
            ZOMBIE_SOLDIER, ZOMBIE_WARRIOR, SHIELD_SKELETON, SKELETON_INFANTRYMAN);

    public _413_PathToShillienOracle() {
        addStartNpc(SIDRA);

        addTalkId(ADONIUS, TALBOT);

        addKillId(DARK_SUCCUBUS);

        addKillId(ASHEN_BONES_DROP);

        addQuestItem(ASHEN_BONES_ID, SIDRAS_LETTER1_ID, ANDARIEL_BOOK_ID, PENITENTS_MARK_ID, GARMIEL_BOOK_ID,
                PRAYER_OF_ADON_ID, BLANK_SHEET1_ID, BLOODY_RUNE1_ID);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        switch (event) {
            case "1":
                htmltext = "master_sidra_q0413_06.htm";
                st.setCond(1);
                st.start();
                st.playSound(SOUND_ACCEPT);
                st.giveItems(SIDRAS_LETTER1_ID);
                break;
            case "413_1":
                if (st.player.getLevel() >= 18 && st.player.getClassId() == darkMage && !st.haveQuestItem(ORB_OF_ABYSS_ID))
                    htmltext = "master_sidra_q0413_05.htm";
                else if (st.player.getClassId() != darkMage) {
                    if (st.player.getClassId() == shillienOracle)
                        htmltext = "master_sidra_q0413_02a.htm";
                    else
                        htmltext = "master_sidra_q0413_03.htm";
                } else if (st.player.getLevel() < 18)
                    htmltext = "master_sidra_q0413_02.htm";
                else if (st.haveQuestItem(ORB_OF_ABYSS_ID))
                    htmltext = "master_sidra_q0413_04.htm";
                break;
            case "30377_1":
                htmltext = "magister_talbot_q0413_02.htm";
                st.takeItems(SIDRAS_LETTER1_ID);
                st.giveItems(BLANK_SHEET1_ID, 5);
                st.playSound(SOUND_ITEMGET);
                st.setCond(2);
                break;
            case "30375_1":
                htmltext = "priest_adonius_q0413_02.htm";
                break;
            case "30375_2":
                htmltext = "priest_adonius_q0413_03.htm";
                break;
            case "30375_3":
                htmltext = "priest_adonius_q0413_04.htm";
                st.takeItems(PRAYER_OF_ADON_ID);
                st.giveItems(PENITENTS_MARK_ID);
                st.playSound(SOUND_ITEMGET);
                st.setCond(5);
                break;
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
            else if (cond < 7)
                htmltext = "master_sidra_q0413_09.htm";
            else if (cond == 7 && st.haveAllQuestItems(ANDARIEL_BOOK_ID, GARMIEL_BOOK_ID)) {
                htmltext = "master_sidra_q0413_10.htm";
                st.exitCurrentQuest();
                if (st.player.getClassId().occupation() == 0) {
                    st.giveItems(ORB_OF_ABYSS_ID);
                    st.addExpAndSp(228064, 16455);
                    st.giveAdena(81900);
                }
                st.playSound(SOUND_FINISH);
            }
        } else if (npcId == TALBOT) {
            if (cond == 1 && st.haveQuestItem(SIDRAS_LETTER1_ID))
                htmltext = "magister_talbot_q0413_01.htm";
            else if (cond == 2) {
                if (!st.haveQuestItem(BLOODY_RUNE1_ID))
                    htmltext = "magister_talbot_q0413_03.htm";
                else if (st.haveQuestItem(BLOODY_RUNE1_ID))
                    htmltext = "magister_talbot_q0413_04.htm";
            } else if (cond == 3 && st.haveQuestItem(BLOODY_RUNE1_ID, 5)) {
                htmltext = "magister_talbot_q0413_05.htm";
                st.takeItems(BLOODY_RUNE1_ID);
                st.giveItems(GARMIEL_BOOK_ID);
                st.giveItems(PRAYER_OF_ADON_ID);
                st.playSound(SOUND_ITEMGET);
                st.setCond(4);
            } else if (cond > 3 && cond < 7)
                htmltext = "magister_talbot_q0413_06.htm";
            else if (cond == 7)
                htmltext = "magister_talbot_q0413_07.htm";
        } else if (npcId == ADONIUS)
            if (cond == 4 && st.haveQuestItem(PRAYER_OF_ADON_ID))
                htmltext = "priest_adonius_q0413_01.htm";
            else if (cond == 5 && !st.haveQuestItem(ASHEN_BONES_ID))
                htmltext = "priest_adonius_q0413_05.htm";
            else if (cond == 5 && st.getQuestItemsCount(ASHEN_BONES_ID) < 10)
                htmltext = "priest_adonius_q0413_06.htm";
            else if (cond == 6 && st.getQuestItemsCount(ASHEN_BONES_ID) > 9) {
                htmltext = "priest_adonius_q0413_07.htm";
                st.takeAllItems(ASHEN_BONES_ID, PENITENTS_MARK_ID);
                st.giveItems(ANDARIEL_BOOK_ID);
                st.playSound(SOUND_ITEMGET);
                st.setCond(7);
            } else if (cond == 7 && st.haveQuestItem(ANDARIEL_BOOK_ID))
                htmltext = "priest_adonius_q0413_08.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == DARK_SUCCUBUS)
            if (cond == 2 && st.getQuestItemsCount(BLANK_SHEET1_ID) > 0) {
                st.giveItems(BLOODY_RUNE1_ID);
                st.takeItems(BLANK_SHEET1_ID);
                if (st.getQuestItemsCount(BLANK_SHEET1_ID) < 1) {
                    st.playSound(SOUND_MIDDLE);
                    st.setCond(3);
                } else
                    st.playSound(SOUND_ITEMGET);
            }
        if (ASHEN_BONES_DROP.contains(npcId) && cond == 5 && st.getQuestItemsCount(ASHEN_BONES_ID) < 10) {
            st.giveItems(ASHEN_BONES_ID);
            if (st.getQuestItemsCount(ASHEN_BONES_ID) > 9) {
                st.playSound(SOUND_MIDDLE);
                st.setCond(6);
            } else
                st.playSound(SOUND_ITEMGET);
        }
    }
}