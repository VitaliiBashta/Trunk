package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;
import java.util.Map;

import static l2trunk.gameserver.model.base.ClassId.darkMage;
import static l2trunk.gameserver.model.base.ClassId.darkWizard;

public final class _412_PathToDarkwizard extends Quest {
    private static final int LUCKY_KEY_ID = 1277;
    private static final int CANDLE_ID = 1278;
    private static final int HUB_SCENT_ID = 1279;
    //npc
    private final int CHARKEREN = 30415;
    private final int ANNIKA = 30418;
    private final int ARKENIA = 30419;
    private final int VARIKA = 30421;
    //items
    private final int SEEDS_OF_DESPAIR_ID = 1254;
    private final int SEEDS_OF_ANGER_ID = 1253;
    private final int SEEDS_OF_HORROR_ID = 1255;
    private final int SEEDS_OF_LUNACY_ID = 1256;
    private final int FAMILYS_ASHES_ID = 1257;
    private final int KNEE_BONE_ID = 1259;
    private final int HEART_OF_LUNACY_ID = 1260;
    private final int JEWEL_OF_DARKNESS_ID = 1261;
    private final Map<Integer, List<Integer>> DROPLIST = Map.of(

            20015, List.of(LUCKY_KEY_ID, FAMILYS_ASHES_ID, 3),
            20020, List.of(LUCKY_KEY_ID, FAMILYS_ASHES_ID, 3),
            20517, List.of(CANDLE_ID, KNEE_BONE_ID, 2),
            20518, List.of(CANDLE_ID, KNEE_BONE_ID, 2),
            20022, List.of(CANDLE_ID, KNEE_BONE_ID, 2),
            20045, List.of(HUB_SCENT_ID, HEART_OF_LUNACY_ID, 3));

    public _412_PathToDarkwizard() {
        addStartNpc(VARIKA);

        addTalkId(CHARKEREN, ANNIKA, ARKENIA);

        addQuestItem(LUCKY_KEY_ID, CANDLE_ID, HUB_SCENT_ID, SEEDS_OF_ANGER_ID, SEEDS_OF_HORROR_ID, SEEDS_OF_LUNACY_ID,
                SEEDS_OF_DESPAIR_ID, FAMILYS_ASHES_ID, KNEE_BONE_ID, HEART_OF_LUNACY_ID);
        addKillId(DROPLIST.keySet());
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        switch (event) {
            case "1":
                if (st.player.getLevel() >= 18 && st.player.getClassId() == darkMage && !st.haveQuestItem(JEWEL_OF_DARKNESS_ID)) {
                    st.setCond(1);
                    st.start();
                    st.playSound(SOUND_ACCEPT);
                    st.giveItems(SEEDS_OF_DESPAIR_ID);
                    htmltext = "varika_q0412_05.htm";
                } else if (st.player.getClassId() != darkMage) {
                    if (st.player.getClassId() == darkWizard)
                        htmltext = "varika_q0412_02a.htm";
                    else
                        htmltext = "varika_q0412_03.htm";
                } else if (st.player.getLevel() < 18)
                    htmltext = "varika_q0412_02.htm";
                else if (st.haveQuestItem(JEWEL_OF_DARKNESS_ID))
                    htmltext = "varika_q0412_04.htm";
                break;
            case "412_1":
                if (st.haveQuestItem(SEEDS_OF_ANGER_ID))
                    htmltext = "varika_q0412_06.htm";
                else
                    htmltext = "varika_q0412_07.htm";
                break;
            case "412_2":
                if (st.haveQuestItem(SEEDS_OF_HORROR_ID))
                    htmltext = "varika_q0412_09.htm";
                else
                    htmltext = "varika_q0412_10.htm";
                break;
            case "412_3":
                if (st.haveQuestItem(SEEDS_OF_LUNACY_ID))
                    htmltext = "varika_q0412_12.htm";
                else if (!st.haveQuestItem(SEEDS_OF_LUNACY_ID) && st.haveQuestItem(SEEDS_OF_DESPAIR_ID))
                    htmltext = "varika_q0412_13.htm";
                break;
            case "412_4":
                htmltext = "charkeren_q0412_03.htm";
                st.giveItems(LUCKY_KEY_ID);
                break;
            case "30418_1":
                htmltext = "annsery_q0412_02.htm";
                st.giveItems(CANDLE_ID);
                break;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == VARIKA) {
            if (cond < 1) {
                if (st.haveQuestItem(JEWEL_OF_DARKNESS_ID)) {
                    htmltext = "varika_q0412_04.htm";
                } else {
                    htmltext = "varika_q0412_01.htm";
                }
            } else if (st.haveAllQuestItems(SEEDS_OF_DESPAIR_ID, SEEDS_OF_HORROR_ID, SEEDS_OF_LUNACY_ID, SEEDS_OF_ANGER_ID)) {
                htmltext = "varika_q0412_16.htm";
                if (st.player.getClassId().occupation() == 0) {
                    st.giveItems(JEWEL_OF_DARKNESS_ID);
                    st.addExpAndSp(228064, 16455);
                    st.giveAdena(81900);
                }
                st.exitCurrentQuest();
                st.playSound(SOUND_FINISH);
            } else if (st.haveQuestItem(SEEDS_OF_DESPAIR_ID))
                if (!st.haveQuestItem(FAMILYS_ASHES_ID) && !st.haveQuestItem(LUCKY_KEY_ID) && st.getQuestItemsCount(CANDLE_ID) < 1 && st.getQuestItemsCount(HUB_SCENT_ID) < 1 && st.getQuestItemsCount(KNEE_BONE_ID) < 1 && st.getQuestItemsCount(HEART_OF_LUNACY_ID) < 1)
                    htmltext = "varika_q0412_17.htm";
                else if (!st.haveQuestItem(SEEDS_OF_ANGER_ID))
                    htmltext = "varika_q0412_08.htm";
                else if (st.haveQuestItem(SEEDS_OF_HORROR_ID))
                    htmltext = "varika_q0412_19.htm";
                else if (!st.haveQuestItem(HEART_OF_LUNACY_ID))
                    htmltext = "varika_q0412_13.htm";
        } else if (npcId == ARKENIA && cond > 0 && !st.haveQuestItem(SEEDS_OF_LUNACY_ID)) {
            if (!st.haveQuestItem(HUB_SCENT_ID) && !st.haveQuestItem(HEART_OF_LUNACY_ID)) {
                htmltext = "arkenia_q0412_01.htm";
                st.giveItems(HUB_SCENT_ID);
            } else if (st.haveQuestItem(HUB_SCENT_ID) && !st.haveQuestItem(HEART_OF_LUNACY_ID))
                htmltext = "arkenia_q0412_02.htm";
            else if (st.haveQuestItem(HUB_SCENT_ID) && st.haveQuestItem(HEART_OF_LUNACY_ID, 3)) {
                htmltext = "arkenia_q0412_03.htm";
                st.giveItems(SEEDS_OF_LUNACY_ID);
                st.takeItems(HEART_OF_LUNACY_ID);
                st.takeItems(HUB_SCENT_ID);
            }
        } else if (npcId == CHARKEREN && cond > 0) {
            if (!st.haveQuestItem(SEEDS_OF_ANGER_ID)) {
                if (st.haveQuestItem(SEEDS_OF_DESPAIR_ID) && !st.haveQuestItem(FAMILYS_ASHES_ID) && !st.haveQuestItem(LUCKY_KEY_ID))
                    htmltext = "charkeren_q0412_01.htm";
                else if (st.haveQuestItem(SEEDS_OF_DESPAIR_ID) && !st.haveQuestItem(FAMILYS_ASHES_ID, 3) && st.haveQuestItem(LUCKY_KEY_ID))
                    htmltext = "charkeren_q0412_04.htm";
                else if (st.haveQuestItem(SEEDS_OF_DESPAIR_ID) && st.haveQuestItem(FAMILYS_ASHES_ID, 3) && st.haveQuestItem(LUCKY_KEY_ID)) {
                    htmltext = "charkeren_q0412_05.htm";
                    st.giveItems(SEEDS_OF_ANGER_ID);
                    st.takeItems(FAMILYS_ASHES_ID);
                    st.takeItems(LUCKY_KEY_ID);
                }
            } else
                htmltext = "charkeren_q0412_06.htm";
        } else if (npcId == ANNIKA && cond > 0 && !st.haveQuestItem(SEEDS_OF_HORROR_ID))
            if (st.haveQuestItem(SEEDS_OF_DESPAIR_ID) && !st.haveQuestItem(CANDLE_ID) && st.getQuestItemsCount(KNEE_BONE_ID) < 1)
                htmltext = "annsery_q0412_01.htm";
            else if (st.haveQuestItem(SEEDS_OF_DESPAIR_ID) && st.haveQuestItem(CANDLE_ID) && st.getQuestItemsCount(KNEE_BONE_ID) < 2)
                htmltext = "annsery_q0412_03.htm";
            else if (st.haveQuestItem(SEEDS_OF_DESPAIR_ID) && st.haveQuestItem(CANDLE_ID) && st.haveQuestItem(KNEE_BONE_ID, 2)) {
                htmltext = "annsery_q0412_04.htm";
                st.giveItems(SEEDS_OF_HORROR_ID);
                st.takeItems(CANDLE_ID);
                st.takeItems(KNEE_BONE_ID);
            }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        //DROPLIST [MOB_ID, REQUIRED, ITEM, NEED_COUNT]
        if (st.getCond() == 1)
            DROPLIST.forEach((k, v) -> {
                if (npc.getNpcId() == k && st.haveQuestItem(v.get(0)))
                    if (Rnd.chance(50) && st.haveQuestItem(v.get(1), v.get(2))) {
                        st.giveItems(v.get(1));
                        if (st.haveQuestItem(v.get(1), v.get(2)))
                            st.playSound(SOUND_MIDDLE);
                        else
                            st.playSound(SOUND_ITEMGET);
                    }
            });
    }
}