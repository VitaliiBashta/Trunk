package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.ScriptFile;

/**
 * Квест Keen Claws
 *
 * @author Sergey Ibryaev aka Artful
 */

public class _264_KeenClaws extends Quest implements ScriptFile {
    //NPC
    private static final int Payne = 30136;
    //Quest Items
    private static final int WolfClaw = 1367;
    //Items
    private static final int LeatherSandals = 36;
    private static final int WoodenHelmet = 43;
    private static final int Stockings = 462;
    private static final int HealingPotion = 1061;
    private static final int ShortGloves = 48;
    private static final int ClothShoes = 35;
    //MOB
    private static final int Goblin = 20003;
    private static final int AshenWolf = 20456;
    //Drop Cond
    //# [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
    private static final int[][] DROPLIST_COND = {
            {
                    1,
                    2,
                    Goblin,
                    0,
                    WolfClaw,
                    50,
                    50,
                    2
            },
            {
                    1,
                    2,
                    AshenWolf,
                    0,
                    WolfClaw,
                    50,
                    50,
                    2
            }
    };

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _264_KeenClaws() {
        super(false);

        addStartNpc(Payne);

        addKillId(Goblin);
        addKillId(AshenWolf);

        addQuestItem(WolfClaw);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("paint_q0264_03.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == Payne)
            if (cond == 0) {
                if (st.getPlayer().getLevel() >= 3)
                    htmltext = "paint_q0264_02.htm";
                else {
                    st.exitCurrentQuest(true);
                    return "paint_q0264_01.htm";
                }
            } else if (cond == 1)
                htmltext = "paint_q0264_04.htm";
            else if (cond == 2) {
                st.takeItems(WolfClaw, -1);
                int n = Rnd.get(17);
                if (n == 0) {
                    st.giveItems(WoodenHelmet, 1);
                    st.playSound(SOUND_JACKPOT);
                } else if (n < 2)
                    st.giveItems(ADENA_ID, 1000);
                else if (n < 5)
                    st.giveItems(LeatherSandals, 1);
                else if (n < 8) {
                    st.giveItems(Stockings, 1);
                    st.giveItems(ADENA_ID, 50);
                } else if (n < 11)
                    st.giveItems(HealingPotion, 1);
                else if (n < 14)
                    st.giveItems(ShortGloves, 1);
                else
                    st.giveItems(ClothShoes, 1);
                htmltext = "paint_q0264_05.htm";
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(true);
            }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        for (int[] aDROPLIST_COND : DROPLIST_COND)
            if (cond == aDROPLIST_COND[0] && npcId == aDROPLIST_COND[2])
                if (aDROPLIST_COND[3] == 0 || st.getQuestItemsCount(aDROPLIST_COND[3]) > 0)
                    if (aDROPLIST_COND[5] == 0)
                        st.rollAndGive(aDROPLIST_COND[4], aDROPLIST_COND[7], aDROPLIST_COND[6]);
                    else if (st.rollAndGive(aDROPLIST_COND[4], aDROPLIST_COND[7], aDROPLIST_COND[7], aDROPLIST_COND[5], aDROPLIST_COND[6]))
                        if (aDROPLIST_COND[1] != cond && aDROPLIST_COND[1] != 0) {
                            st.setCond(aDROPLIST_COND[1]);
                            st.setState(STARTED);
                        }
        return null;
    }
}