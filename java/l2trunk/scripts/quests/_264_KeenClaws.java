package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _264_KeenClaws extends Quest {
    //NPC
    private static final int Payne = 30136;
    //Quest items
    private static final int WolfClaw = 1367;
    //items
    private static final int LeatherSandals = 36;
    private static final int WoodenHelmet = 43;
    private static final int Stockings = 462;
    private static final int HealingPotion = 1061;
    private static final int ShortGloves = 48;
    private static final int ClothShoes = 35;
    //MOB
    private static final int Goblin = 20003;
    private static final int AshenWolf = 20456;

    public _264_KeenClaws() {
        addStartNpc(Payne);

        addKillId(Goblin, AshenWolf);

        addQuestItem(WolfClaw);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("paint_q0264_03.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
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
                if (st.player.getLevel() >= 3)
                    htmltext = "paint_q0264_02.htm";
                else {
                    st.exitCurrentQuest();
                    return "paint_q0264_01.htm";
                }
            } else if (cond == 1)
                htmltext = "paint_q0264_04.htm";
            else if (cond == 2) {
                st.takeItems(WolfClaw);
                int n = Rnd.get(17);
                if (n == 0) {
                    st.giveItems(WoodenHelmet);
                    st.playSound(SOUND_JACKPOT);
                } else if (n < 2)
                    st.giveAdena(1000);
                else if (n < 5)
                    st.giveItems(LeatherSandals);
                else if (n < 8) {
                    st.giveItems(Stockings);
                    st.giveAdena(50);
                } else if (n < 11)
                    st.giveItems(HealingPotion);
                else if (n < 14)
                    st.giveItems(ShortGloves);
                else
                    st.giveItems(ClothShoes);
                htmltext = "paint_q0264_05.htm";
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest();
            }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 1 && (npcId == Goblin || npcId == AshenWolf)) {
            if (st.rollAndGive(WolfClaw, 2, 2, 50, 50)) {
                st.setCond(2);
                st.start();
            }
        }
    }
}