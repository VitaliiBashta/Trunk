package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _615_MagicalPowerofFire1 extends Quest {
    private static final int DIVINE_STONE_OF_WISDOM = 7081;
    private static final int RED_TOTEM = 7243;
    // etc
    private static final int MARK_OF_VARKA_ALLIANCE1 = 7221;
    private static final int MARK_OF_VARKA_ALLIANCE2 = 7222;
    private static final int MARK_OF_VARKA_ALLIANCE3 = 7223;
    private static final int MARK_OF_VARKA_ALLIANCE4 = 7224;
    private static final int MARK_OF_VARKA_ALLIANCE5 = 7225;
    private static final int THIEF_KEY = 1661;
    // NPC
    private final int NARAN = 31378;
    private final int UDAN = 31379;
    private final int BOX_OF_ASEFA = 31559;
    // Quest items
    private final int STOLEN_RED_TOTEM = 7242;
    static final List<Integer> KETRA_NPC_LIST = List.of(
            21324, 21325, 21327, 21328, 21329, 21331, 21332, 21334, 21335, 21336,
            21338, 21339, 21340, 21342, 21343, 21344, 21345, 21346, 21347);

    public _615_MagicalPowerofFire1() {
        super(false);

        addStartNpc(NARAN);

        addTalkId(NARAN);
        addTalkId(UDAN);
        addTalkId(BOX_OF_ASEFA);


        for (int npcId : KETRA_NPC_LIST)
            addAttackId(npcId);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("quest_accept")) {
            htmltext = "herald_naran_q0615_02.htm";
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("615_1") && st.getCond() == 2)
            if (st.getQuestItemsCount(THIEF_KEY) < 1)
                htmltext = "asefas_box_q0615_02.htm";
            else if (st.getInt("proval") == 1) {
                htmltext = "asefas_box_q0615_04.htm";
                st.takeItems(THIEF_KEY, 1);
            } else {
                st.takeItems(THIEF_KEY, 1);
                st.giveItems(STOLEN_RED_TOTEM, 1);
                htmltext = "asefas_box_q0615_03.htm";
                st.setCond(3);
            }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        int proval = st.getInt("proval");
        switch (npcId) {
            case NARAN:
                if (cond == 0)
                    if (st.getPlayer().getLevel() >= 74)
                        if (st.getQuestItemsCount(MARK_OF_VARKA_ALLIANCE1) == 1 || st.getQuestItemsCount(MARK_OF_VARKA_ALLIANCE2) == 1 || st.getQuestItemsCount(MARK_OF_VARKA_ALLIANCE3) == 1 || st.getQuestItemsCount(MARK_OF_VARKA_ALLIANCE4) == 1 || st.getQuestItemsCount(MARK_OF_VARKA_ALLIANCE5) == 1) {
                            if (st.getQuestItemsCount(DIVINE_STONE_OF_WISDOM) == 0)
                                htmltext = "herald_naran_q0615_01.htm";
                            else {
                                htmltext = "completed";
                                st.exitCurrentQuest(true);
                            }
                        } else {
                            htmltext = "herald_naran_q0615_01a.htm";
                            st.exitCurrentQuest(true);
                        }
                    else {
                        htmltext = "herald_naran_q0615_01b.htm";
                        st.exitCurrentQuest(true);
                    }
                else if (cond == 1)
                    htmltext = "herald_naran_q0615_03.htm";
                break;
            case UDAN:
                if (cond == 1) {
                    htmltext = "shaman_udan_q0615_01.htm";
                    st.setCond(2);
                } else if (cond == 2 && proval == 1) {
                    htmltext = "shaman_udan_q0615_03.htm";
                    npc.doCast(4548, st.getPlayer(), true);
                    st.set("proval", "0");
                } else if (cond == 3 && st.getQuestItemsCount(STOLEN_RED_TOTEM) >= 1) {
                    htmltext = "shaman_udan_q0615_04.htm";
                    st.takeItems(STOLEN_RED_TOTEM, st.getQuestItemsCount(STOLEN_RED_TOTEM));
                    st.giveItems(RED_TOTEM, 1);
                    st.giveItems(DIVINE_STONE_OF_WISDOM, 1);
                    st.playSound(SOUND_FINISH);
                    st.exitCurrentQuest(true);
                }
                break;
            case BOX_OF_ASEFA:
                if (cond == 2)
                    htmltext = "asefas_box_q0615_01.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onAttack(NpcInstance npc, QuestState st) {
        int cond = st.getCond();
        int proval = st.getInt("proval");
        if (cond == 2 && proval == 0) {
            npc.doCast(4547, st.getPlayer(), true);
            st.set("proval", "1");
        }
        return null;
    }
}