package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import static l2trunk.scripts.quests._605_AllianceWithKetraOrcs.KETRA_NPC_LIST;

public final class _615_MagicalPowerofFire1 extends Quest {
    private static final int DIVINE_STONE_OF_WISDOM = 7081;
    private static final int RED_TOTEM = 7243;
    private static final int THIEF_KEY = 1661;
    // NPC
    private final int NARAN = 31378;
    private final int UDAN = 31379;
    private final int BOX_OF_ASEFA = 31559;
    // Quest items
    private final int STOLEN_RED_TOTEM = 7242;


    public _615_MagicalPowerofFire1() {
        addStartNpc(NARAN);

        addTalkId(UDAN,BOX_OF_ASEFA);

        addAttackId(KETRA_NPC_LIST);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("quest_accept".equalsIgnoreCase(event)) {
            htmltext = "herald_naran_q0615_02.htm";
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("615_1".equals(event) && st.getCond() == 2)
            if (st.getQuestItemsCount(THIEF_KEY) < 1)
                htmltext = "asefas_box_q0615_02.htm";
            else if (st.isSet("proval") ) {
                htmltext = "asefas_box_q0615_04.htm";
                st.takeItems(THIEF_KEY, 1);
            } else {
                st.takeItems(THIEF_KEY, 1);
                st.giveItems(STOLEN_RED_TOTEM);
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
        switch (npcId) {
            case NARAN:
                if (cond == 0)
                    if (st.player.getLevel() >= 74)
                        if (st.player.getKetra()> 1) {
                            if (st.haveQuestItem(DIVINE_STONE_OF_WISDOM)) {
                                htmltext = "completed";
                                st.exitCurrentQuest();
                            } else {
                                htmltext = "herald_naran_q0615_01.htm";
                            }
                        } else {
                            htmltext = "herald_naran_q0615_01a.htm";
                            st.exitCurrentQuest();
                        }
                    else {
                        htmltext = "herald_naran_q0615_01b.htm";
                        st.exitCurrentQuest();
                    }
                else if (cond == 1)
                    htmltext = "herald_naran_q0615_03.htm";
                break;
            case UDAN:
                if (cond == 1) {
                    htmltext = "shaman_udan_q0615_01.htm";
                    st.setCond(2);
                } else if (cond == 2 && st.isSet("proval")) {
                    htmltext = "shaman_udan_q0615_03.htm";
                    npc.doCast(4548, st.player, true);
                    st.unset("proval");
                } else if (cond == 3 && st.haveQuestItem(STOLEN_RED_TOTEM)) {
                    htmltext = "shaman_udan_q0615_04.htm";
                    st.takeItems(STOLEN_RED_TOTEM);
                    st.giveItems(RED_TOTEM);
                    st.giveItems(DIVINE_STONE_OF_WISDOM);
                    st.playSound(SOUND_FINISH);
                    st.exitCurrentQuest();
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
    public void onAttack(NpcInstance npc, QuestState st) {
        int cond = st.getCond();
        if (cond == 2 && !st.isSet("proval")) {
            npc.doCast(4547, st.player, true);
            st.set("proval");
        }
    }
}