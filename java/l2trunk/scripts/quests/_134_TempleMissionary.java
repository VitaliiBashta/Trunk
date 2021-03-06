package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _134_TempleMissionary extends Quest {
    // NPCs
    private final static int Glyvka = 30067;
    private final static int Rouke = 31418;
    // Mobs
    private final static int Cruma_Marshlands_Traitor = 27339;
    private final static List<Integer> mobs = List.of(
            20157, 20229, 20230, 20231, 20232, 20233, 20234);
    // Quest items
    private final static int Giants_Experimental_Tool_Fragment = 10335;
    private final static int Giants_Experimental_Tool = 10336;
    private final static int Giants_Technology_Report = 10337;
    private final static int Roukes_Report = 10338;
    // items
    private final static int Badge_Temple_Missionary = 10339;
    // Chances
    private final static int Giants_Experimental_Tool_Fragment_chance = 66;
    private final static int Cruma_Marshlands_Traitor_spawnchance = 45;

    public _134_TempleMissionary() {
        addStartNpc(Glyvka);
        addTalkId(Rouke);
        addKillId(mobs);
        addKillId(Cruma_Marshlands_Traitor);
        addQuestItem(Giants_Experimental_Tool_Fragment,Giants_Experimental_Tool,Giants_Technology_Report,Roukes_Report);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int state = st.getState();
        if ("glyvka_q0134_03.htm".equalsIgnoreCase(event) && state == CREATED) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("glyvka_q0134_06.htm".equalsIgnoreCase(event) && state == STARTED) {
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if ("glyvka_q0134_11.htm".equalsIgnoreCase(event) && state == STARTED && st.getCond() == 5) {
            st.playSound(SOUND_FINISH);
            st.unset("Report");
            st.giveAdena( 15100);
            st.giveItems(Badge_Temple_Missionary);
            st.addExpAndSp(30000, 2000);
            st.finish();
        } else if ("scroll_seller_rouke_q0134_03.htm".equalsIgnoreCase(event) && state == STARTED) {
            st.setCond(3);
            st.playSound(SOUND_MIDDLE);
        } else if ("scroll_seller_rouke_q0134_09.htm".equalsIgnoreCase(event) && state == STARTED && st.isSet("Report")) {
            st.setCond(5);
            st.playSound(SOUND_MIDDLE);
            st.giveItems(Roukes_Report);
            st.unset("Report");
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int state = st.getState();
        if (state == COMPLETED)
            return "completed";

        int npcId = npc.getNpcId();
        if (state == CREATED) {
            if (npcId != Glyvka)
                return "noquest";
            if (st.player.getLevel() < 35) {
                st.exitCurrentQuest();
                return "glyvka_q0134_02.htm";
            }
            st.setCond(0);
            return "glyvka_q0134_01.htm";
        }

        int cond = st.getCond();

        if (npcId == Glyvka && state == STARTED) {
            if (cond == 1)
                return "glyvka_q0134_03.htm";
            if (cond == 5) {
                if (st.isSet("Report") )
                    return "glyvka_q0134_09.htm";
                if (st.haveQuestItem(Roukes_Report)) {
                    st.takeItems(Roukes_Report);
                    st.set("Report");
                    return "glyvka_q0134_08.htm";
                }
                return "noquest";
            }
            return "glyvka_q0134_07.htm";
        }

        if (npcId == Rouke && state == STARTED) {
            if (cond == 2)
                return "scroll_seller_rouke_q0134_02.htm";
            if (cond == 5)
                return "scroll_seller_rouke_q0134_10.htm";
            if (cond == 3) {
                long tools = st.getQuestItemsCount(Giants_Experimental_Tool_Fragment) / 10;
                if (tools < 1)
                    return "scroll_seller_rouke_q0134_04.htm";
                st.takeItems(Giants_Experimental_Tool_Fragment, tools * 10);
                st.giveItems(Giants_Experimental_Tool, tools);
                return "scroll_seller_rouke_q0134_05.htm";
            }
            if (cond == 4) {
                if (st.isSet("Report"))
                    return "scroll_seller_rouke_q0134_07.htm";
                if (st.getQuestItemsCount(Giants_Technology_Report) > 2) {
                    st.takeItems(Giants_Experimental_Tool_Fragment);
                    st.takeItems(Giants_Experimental_Tool);
                    st.takeItems(Giants_Technology_Report);
                    st.set("Report");
                    return "scroll_seller_rouke_q0134_06.htm";
                }
                return "noquest";
            }
        }

        return "noquest";
    }

    @Override
    public void onKill(NpcInstance npc, QuestState qs) {
        if (qs.getState() == STARTED && qs.getCond() == 3)
            if (npc.getNpcId() == Cruma_Marshlands_Traitor) {
                qs.giveItems(Giants_Technology_Report);
                if (qs.getQuestItemsCount(Giants_Technology_Report) < 3)
                    qs.playSound(SOUND_ITEMGET);
                else {
                    qs.playSound(SOUND_MIDDLE);
                    qs.setCond(4);
                }
            } else if (qs.getQuestItemsCount(Giants_Experimental_Tool) < 1) {
                if (Rnd.chance(Giants_Experimental_Tool_Fragment_chance))
                    qs.giveItems(Giants_Experimental_Tool_Fragment);
            } else {
                qs.takeItems(Giants_Experimental_Tool, 1);
                if (Rnd.chance(Cruma_Marshlands_Traitor_spawnchance))
                    qs.addSpawn(Cruma_Marshlands_Traitor,  900000);
            }
    }
}