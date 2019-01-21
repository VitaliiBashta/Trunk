package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _412_PathToDarkwizard extends Quest {
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
    private final int LUCKY_KEY_ID = 1277;
    private final int CANDLE_ID = 1278;
    private final int HUB_SCENT_ID = 1279;
    //DROPLIST [MOB_ID, REQUIRED, ITEM, NEED_COUNT]
    private final int[][] DROPLIST = {
            {
                    20015,
                    LUCKY_KEY_ID,
                    FAMILYS_ASHES_ID,
                    3
            },
            {
                    20020,
                    LUCKY_KEY_ID,
                    FAMILYS_ASHES_ID,
                    3
            },
            {
                    20517,
                    CANDLE_ID,
                    KNEE_BONE_ID,
                    2
            },
            {
                    20518,
                    CANDLE_ID,
                    KNEE_BONE_ID,
                    2
            },
            {
                    20022,
                    CANDLE_ID,
                    KNEE_BONE_ID,
                    2
            },
            {
                    20045,
                    HUB_SCENT_ID,
                    HEART_OF_LUNACY_ID,
                    3
            }
    };

    public _412_PathToDarkwizard() {
        super(false);

        addStartNpc(VARIKA);

        addTalkId(CHARKEREN);
        addTalkId(ANNIKA);
        addTalkId(ARKENIA);

        addQuestItem(SEEDS_OF_ANGER_ID);
        addQuestItem(LUCKY_KEY_ID);
        addQuestItem(SEEDS_OF_HORROR_ID);
        addQuestItem(CANDLE_ID);
        addQuestItem(SEEDS_OF_LUNACY_ID);
        addQuestItem(HUB_SCENT_ID);
        addQuestItem(SEEDS_OF_DESPAIR_ID);
        addQuestItem(FAMILYS_ASHES_ID);
        addQuestItem(KNEE_BONE_ID);
        addQuestItem(HEART_OF_LUNACY_ID);

        for (int[] element : DROPLIST)
            addKillId(element[0]);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("1")) {
            if (st.getPlayer().getLevel() >= 18 && st.getPlayer().getClassId().getId() == 0x26 && st.getQuestItemsCount(JEWEL_OF_DARKNESS_ID) < 1) {
                st.setCond(1);
                st.setState(STARTED);
                st.playSound(SOUND_ACCEPT);
                st.giveItems(SEEDS_OF_DESPAIR_ID);
                htmltext = "varika_q0412_05.htm";
            } else if (st.getPlayer().getClassId().getId() != 0x26) {
                if (st.getPlayer().getClassId().getId() == 0x27)
                    htmltext = "varika_q0412_02a.htm";
                else
                    htmltext = "varika_q0412_03.htm";
            } else if (st.getPlayer().getLevel() < 18)
                htmltext = "varika_q0412_02.htm";
            else if (st.getQuestItemsCount(JEWEL_OF_DARKNESS_ID) > 0)
                htmltext = "varika_q0412_04.htm";
        } else if (event.equalsIgnoreCase("412_1")) {
            if (st.getQuestItemsCount(SEEDS_OF_ANGER_ID) > 0)
                htmltext = "varika_q0412_06.htm";
            else
                htmltext = "varika_q0412_07.htm";
        } else if ("412_2".equalsIgnoreCase(event)) {
            if (st.getQuestItemsCount(SEEDS_OF_HORROR_ID) > 0)
                htmltext = "varika_q0412_09.htm";
            else
                htmltext = "varika_q0412_10.htm";
        } else if ("412_3".equalsIgnoreCase(event)) {
            if (st.getQuestItemsCount(SEEDS_OF_LUNACY_ID) > 0)
                htmltext = "varika_q0412_12.htm";
            else if (st.getQuestItemsCount(SEEDS_OF_LUNACY_ID) < 1 && st.getQuestItemsCount(SEEDS_OF_DESPAIR_ID) > 0)
                htmltext = "varika_q0412_13.htm";
        } else if ("412_4".equalsIgnoreCase(event)) {
            htmltext = "charkeren_q0412_03.htm";
            st.giveItems(LUCKY_KEY_ID, 1);
        } else if ("30418_1".equalsIgnoreCase(event)) {
            htmltext = "annsery_q0412_02.htm";
            st.giveItems(CANDLE_ID, 1);
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
                if (st.getQuestItemsCount(JEWEL_OF_DARKNESS_ID) < 1)
                    htmltext = "varika_q0412_01.htm";
                else
                    htmltext = "varika_q0412_04.htm";
            } else if (st.getQuestItemsCount(SEEDS_OF_DESPAIR_ID) > 0 && st.getQuestItemsCount(SEEDS_OF_HORROR_ID) > 0 && st.getQuestItemsCount(SEEDS_OF_LUNACY_ID) > 0 && st.getQuestItemsCount(SEEDS_OF_ANGER_ID) > 0) {
                htmltext = "varika_q0412_16.htm";
                if (st.getPlayer().getClassId().getLevel() == 1) {
                    st.giveItems(JEWEL_OF_DARKNESS_ID, 1);
                    if (!st.getPlayer().getVarB("prof1")) {
                        st.getPlayer().setVar("prof1", "1", -1);
                        st.addExpAndSp(228064, 16455);
                        //FIXME [G1ta0] дать адены, только если первый чар на акке
                        st.giveItems(ADENA_ID, 81900);
                    }
                }
                st.exitCurrentQuest(true);
                st.playSound(SOUND_FINISH);
            } else if (st.getQuestItemsCount(SEEDS_OF_DESPAIR_ID) > 0)
                if (st.getQuestItemsCount(FAMILYS_ASHES_ID) < 1 && st.getQuestItemsCount(LUCKY_KEY_ID) < 1 && st.getQuestItemsCount(CANDLE_ID) < 1 && st.getQuestItemsCount(HUB_SCENT_ID) < 1 && st.getQuestItemsCount(KNEE_BONE_ID) < 1 && st.getQuestItemsCount(HEART_OF_LUNACY_ID) < 1)
                    htmltext = "varika_q0412_17.htm";
                else if (st.getQuestItemsCount(SEEDS_OF_ANGER_ID) < 1)
                    htmltext = "varika_q0412_08.htm";
                else if (st.getQuestItemsCount(SEEDS_OF_HORROR_ID) > 0)
                    htmltext = "varika_q0412_19.htm";
                else if (st.getQuestItemsCount(HEART_OF_LUNACY_ID) < 1)
                    htmltext = "varika_q0412_13.htm";
        } else if (npcId == ARKENIA && cond > 0 && st.getQuestItemsCount(SEEDS_OF_LUNACY_ID) < 1) {
            if (st.getQuestItemsCount(HUB_SCENT_ID) < 1 && st.getQuestItemsCount(HEART_OF_LUNACY_ID) < 1) {
                htmltext = "arkenia_q0412_01.htm";
                st.giveItems(HUB_SCENT_ID);
            } else if (st.getQuestItemsCount(HUB_SCENT_ID) > 0 && st.getQuestItemsCount(HEART_OF_LUNACY_ID) < 3)
                htmltext = "arkenia_q0412_02.htm";
            else if (st.getQuestItemsCount(HUB_SCENT_ID) > 0 && st.getQuestItemsCount(HEART_OF_LUNACY_ID) >= 3) {
                htmltext = "arkenia_q0412_03.htm";
                st.giveItems(SEEDS_OF_LUNACY_ID);
                st.takeItems(HEART_OF_LUNACY_ID);
                st.takeItems(HUB_SCENT_ID);
            }
        } else if (npcId == CHARKEREN && cond > 0) {
            if (st.getQuestItemsCount(SEEDS_OF_ANGER_ID) < 1) {
                if (st.getQuestItemsCount(SEEDS_OF_DESPAIR_ID) > 0 && st.getQuestItemsCount(FAMILYS_ASHES_ID) < 1 && st.getQuestItemsCount(LUCKY_KEY_ID) < 1)
                    htmltext = "charkeren_q0412_01.htm";
                else if (st.getQuestItemsCount(SEEDS_OF_DESPAIR_ID) > 0 && st.getQuestItemsCount(FAMILYS_ASHES_ID) < 3 && st.getQuestItemsCount(LUCKY_KEY_ID) > 0)
                    htmltext = "charkeren_q0412_04.htm";
                else if (st.getQuestItemsCount(SEEDS_OF_DESPAIR_ID) > 0 && st.getQuestItemsCount(FAMILYS_ASHES_ID) >= 3 && st.getQuestItemsCount(LUCKY_KEY_ID) > 0) {
                    htmltext = "charkeren_q0412_05.htm";
                    st.giveItems(SEEDS_OF_ANGER_ID);
                    st.takeItems(FAMILYS_ASHES_ID);
                    st.takeItems(LUCKY_KEY_ID);
                }
            } else
                htmltext = "charkeren_q0412_06.htm";
        } else if (npcId == ANNIKA && cond > 0 && st.getQuestItemsCount(SEEDS_OF_HORROR_ID) < 1)
            if (st.getQuestItemsCount(SEEDS_OF_DESPAIR_ID) > 0 && st.getQuestItemsCount(CANDLE_ID) < 1 && st.getQuestItemsCount(KNEE_BONE_ID) < 1)
                htmltext = "annsery_q0412_01.htm";
            else if (st.getQuestItemsCount(SEEDS_OF_DESPAIR_ID) > 0 && st.getQuestItemsCount(CANDLE_ID) > 0 && st.getQuestItemsCount(KNEE_BONE_ID) < 2)
                htmltext = "annsery_q0412_03.htm";
            else if (st.getQuestItemsCount(SEEDS_OF_DESPAIR_ID) > 0 && st.getQuestItemsCount(CANDLE_ID) > 0 && st.getQuestItemsCount(KNEE_BONE_ID) >= 2) {
                htmltext = "annsery_q0412_04.htm";
                st.giveItems(SEEDS_OF_HORROR_ID);
                st.takeItems(CANDLE_ID);
                st.takeItems(KNEE_BONE_ID);
            }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        //DROPLIST [MOB_ID, REQUIRED, ITEM, NEED_COUNT]
        for (int[] element : DROPLIST)
            if (st.getCond() == 1 && npc.getNpcId() == element[0] && st.getQuestItemsCount(element[1]) > 0)
                if (Rnd.chance(50) && st.getQuestItemsCount(element[2]) < element[3]) {
                    st.giveItems(element[2]);
                    if (st.getQuestItemsCount(element[2]) == element[3])
                        st.playSound(SOUND_MIDDLE);
                    else
                        st.playSound(SOUND_ITEMGET);
                }
        return null;
    }
}