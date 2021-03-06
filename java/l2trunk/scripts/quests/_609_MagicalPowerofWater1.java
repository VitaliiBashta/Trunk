package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

import static l2trunk.scripts.quests._611_AllianceWithVarkaSilenos.VARKA_NPC_LIST;

public final class _609_MagicalPowerofWater1 extends Quest {
    // NPC
    private static final int WAHKAN = 31371;
    private static final int ASEFA = 31372;
    private static final int UDANS_MARDUI_BOX = 31561;

    // Quest items
    private static final int STOLEN_GREEN_TOTEM = 7237;
    private static final int DIVINE_STONE_OF_WISDOM = 7081;
    private static final int GREEN_TOTEM = 7238;

    // etc
    private static final int THIEF_KEY = 1661;


    public _609_MagicalPowerofWater1() {
        addStartNpc(WAHKAN);
        addTalkId(ASEFA,UDANS_MARDUI_BOX);
        addAttackId(VARKA_NPC_LIST);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("quest_accept".equals(event)) {
            htmltext = "herald_wakan_q0609_02.htm";
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("609_1".equals(event))
            if (st.getCond() == 2)
                if (!st.haveQuestItem(THIEF_KEY))
                    htmltext = "udans_box_q0609_02.htm";
                else if (st.isSet("proval")) {
                    htmltext = "udans_box_q0609_04.htm";
                    st.takeItems(THIEF_KEY, 1);
                } else {
                    st.takeItems(THIEF_KEY, 1);
                    st.giveItems(STOLEN_GREEN_TOTEM, 1);
                    htmltext = "udans_box_q0609_03.htm";
                    st.setCond(3);
                }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == WAHKAN) {
            if (cond == 0)
                if (st.player.getLevel() >= 74)
                    if (st.player.getKetra()>0) {
                        if (st.haveQuestItem(DIVINE_STONE_OF_WISDOM))
                            htmltext = "herald_wakan_q0609_01.htm";
                        else {
                            htmltext = "completed";
                            st.exitCurrentQuest();
                        }
                    } else {
                        htmltext = "herald_wakan_q0609_01a.htm";
                        st.exitCurrentQuest();
                    }
                else {
                    htmltext = "herald_wakan_q0609_01b.htm";
                    st.exitCurrentQuest();
                }
            else if (cond == 1)
                htmltext = "herald_wakan_q0609_03.htm";
        } else if (npcId == ASEFA) {
            if (cond == 1) {
                htmltext = "shaman_asefa_q0609_01.htm";
                st.setCond(2);
            } else if (cond == 2 && st.isSet("proval")) {
                htmltext = "shaman_asefa_q0609_03.htm";
                npc.doCast(4548, st.player, true);
                st.unset("proval");
            } else if (cond == 3 && st.haveQuestItem(STOLEN_GREEN_TOTEM) ) {
                htmltext = "shaman_asefa_q0609_04.htm";
                st.takeItems(STOLEN_GREEN_TOTEM);
                st.giveItems(GREEN_TOTEM);
                st.giveItems(DIVINE_STONE_OF_WISDOM);
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest();
            }
        } else if (npcId == UDANS_MARDUI_BOX && cond == 2)
            htmltext = "udans_box_q0609_01.htm";
        return htmltext;
    }

    @Override
    public void onAttack(NpcInstance npc, QuestState st) {
        if (st.getCond() == 2 && !st.isSet("proval")) {
            npc.doCast(4547, st.player, true);
            st.set("proval");
        }
    }
}