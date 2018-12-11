package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.tables.SkillTable;

import java.util.Arrays;
import java.util.List;

public final class _609_MagicalPowerofWater1 extends Quest implements ScriptFile {
    // NPC
    private static final int WAHKAN = 31371;
    private static final int ASEFA = 31372;
    private static final int UDANS_MARDUI_BOX = 31561;

    // Quest items
    private static final int STOLEN_GREEN_TOTEM = 7237;
    private static final int DIVINE_STONE_OF_WISDOM = 7081;
    private static final int GREEN_TOTEM = 7238;

    // etc
    private static final int MARK_OF_KETRA_ALLIANCE1 = 7211;
    private static final int MARK_OF_KETRA_ALLIANCE2 = 7212;
    private static final int MARK_OF_KETRA_ALLIANCE3 = 7213;
    private static final int MARK_OF_KETRA_ALLIANCE4 = 7214;
    private static final int MARK_OF_KETRA_ALLIANCE5 = 7215;
    private static final int THIEF_KEY = 1661;

    private static final List<Integer> VARKA_NPC_LIST = Arrays.asList(21350, 21351, 21353, 21354, 21355, 21357, 21358, 21360, 21361, 21362, 21364, 21365, 21366, 21368, 21369, 21370, 21371, 21372, 21373, 21374);

    public _609_MagicalPowerofWater1() {
        super(false);
        addStartNpc(WAHKAN);
        addTalkId(ASEFA);
        addTalkId(UDANS_MARDUI_BOX);
        VARKA_NPC_LIST.forEach(this::addAttackId);
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equals("quest_accept")) {
            htmltext = "herald_wakan_q0609_02.htm";
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equals("609_1"))
            if (st.getCond() == 2)
                if (st.getQuestItemsCount(THIEF_KEY) < 1)
                    htmltext = "udans_box_q0609_02.htm";
                else if (st.getInt("proval") == 1) {
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
        int proval = st.getInt("proval");
        if (npcId == WAHKAN) {
            if (cond == 0)
                if (st.getPlayer().getLevel() >= 74)
                    if (st.getQuestItemsCount(MARK_OF_KETRA_ALLIANCE1) == 1 || st.getQuestItemsCount(MARK_OF_KETRA_ALLIANCE2) == 1 || st.getQuestItemsCount(MARK_OF_KETRA_ALLIANCE3) == 1 || st.getQuestItemsCount(MARK_OF_KETRA_ALLIANCE4) == 1 || st.getQuestItemsCount(MARK_OF_KETRA_ALLIANCE5) == 1) {
                        if (st.getQuestItemsCount(DIVINE_STONE_OF_WISDOM) == 0)
                            htmltext = "herald_wakan_q0609_01.htm";
                        else {
                            htmltext = "completed";
                            st.exitCurrentQuest(true);
                        }
                    } else {
                        htmltext = "herald_wakan_q0609_01a.htm";
                        st.exitCurrentQuest(true);
                    }
                else {
                    htmltext = "herald_wakan_q0609_01b.htm";
                    st.exitCurrentQuest(true);
                }
            else if (cond == 1)
                htmltext = "herald_wakan_q0609_03.htm";
        } else if (npcId == ASEFA) {
            if (cond == 1) {
                htmltext = "shaman_asefa_q0609_01.htm";
                st.setCond(2);
            } else if (cond == 2 && proval == 1) {
                htmltext = "shaman_asefa_q0609_03.htm";
                npc.doCast(SkillTable.INSTANCE.getInfo(4548), st.getPlayer(), true);
                st.set("proval", "0");
            } else if (cond == 3 && st.getQuestItemsCount(STOLEN_GREEN_TOTEM) >= 1) {
                htmltext = "shaman_asefa_q0609_04.htm";
                st.takeItems(STOLEN_GREEN_TOTEM, st.getQuestItemsCount(STOLEN_GREEN_TOTEM));
                st.giveItems(GREEN_TOTEM, 1);
                st.giveItems(DIVINE_STONE_OF_WISDOM, 1);
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(true);
            }
        } else if (npcId == UDANS_MARDUI_BOX && cond == 2)
            htmltext = "udans_box_q0609_01.htm";
        return htmltext;
    }

    @Override
    public String onAttack(NpcInstance npc, QuestState st) {
        if (st.getCond() == 2 && st.getInt("proval") == 0) {
            npc.doCast(SkillTable.INSTANCE.getInfo(4547), st.getPlayer(), true);
            st.set("proval", "1");
        }
        return null;
    }
}