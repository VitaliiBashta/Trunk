package l2trunk.scripts.quests;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;

public final class _197_SevenSignsTheSacredBookofSeal extends Quest {
    // NPCs
    private static final int Wood = 32593;
    private static final int Orven = 30857;
    private static final int Leopard = 32594;
    private static final int Lawrence = 32595;
    private static final int ShilensEvilThoughts = 27396;
    private static final int Sofia = 32596;

    // ITEMS
    private static final int PieceofDoubt = 14354;
    private static final int MysteriousHandwrittenText = 13829;

    public _197_SevenSignsTheSacredBookofSeal() {
        addStartNpc(Wood);
        addTalkId(Orven, Leopard, Lawrence, Sofia);
        addKillId(ShilensEvilThoughts);
        addQuestItem(PieceofDoubt, MysteriousHandwrittenText);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        Player player = st.player;
        if ("wood_q197_2.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("orven_q197_2.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if ("leopard_q197_2.htm".equalsIgnoreCase(event)) {
            st.setCond(3);
            st.playSound(SOUND_MIDDLE);
        } else if ("lawrence_q197_2.htm".equalsIgnoreCase(event)) {
            NpcInstance mob = st.addSpawn(ShilensEvilThoughts, Location.of(152520, -57502, -3408), 0, 180000);
            Functions.npcSay(mob, "Shilen's power is endless!");
            mob.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, 100000);
            st.set("evilthought");
        } else if ("lawrence_q197_4.htm".equalsIgnoreCase(event)) {
            st.setCond(5);
            st.playSound(SOUND_MIDDLE);
        } else if ("sofia_q197_2.htm".equalsIgnoreCase(event)) {
            st.setCond(6);
            st.giveItems(MysteriousHandwrittenText);
            st.playSound(SOUND_MIDDLE);
        } else if ("wood_q197_4.htm".equalsIgnoreCase(event))
            if (player.getBaseClassId() == player.getActiveClassId()) {
                st.takeItems(PieceofDoubt);
                st.takeItems(MysteriousHandwrittenText);
                st.addExpAndSp(25000000, 2500000);
                st.complete();
                st.finish();
                st.playSound(SOUND_FINISH);
            } else
                return "subclass_forbidden.htm";
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        Player player = st.player;
        String htmltext = "noquest";
        if (npcId == Wood) {
            if (cond == 0) {
                if (player.getLevel() >= 79 && player.isQuestCompleted(_196_SevenSignsSealoftheEmperor.class))
                    htmltext = "wood_q197_1.htm";
                else {
                    htmltext = "wood_q197_0.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 6)
                htmltext = "wood_q197_3.htm";
            else
                htmltext = "wood_q197_5.htm";
        } else if (npcId == Orven) {
            if (cond == 1)
                htmltext = "orven_q197_1.htm";
            else if (cond == 2)
                htmltext = "orven_q197_3.htm";
        } else if (npcId == Leopard) {
            if (cond == 2)
                htmltext = "leopard_q197_1.htm";
            else if (cond == 3)
                htmltext = "leopard_q197_3.htm";
        } else if (npcId == Lawrence) {
            if (cond == 3) {
                if (st.isSet("evilthought") )
                    htmltext = "lawrence_q197_0.htm";
                else
                    htmltext = "lawrence_q197_1.htm";
            } else if (cond == 4)
                htmltext = "lawrence_q197_3.htm";
            else if (cond == 5)
                htmltext = "lawrence_q197_5.htm";
        } else if (npcId == Sofia)
            if (cond == 5)
                htmltext = "sofia_q197_1.htm";
            else if (cond == 6)
                htmltext = "sofia_q197_3.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int cond = st.getCond();
        if (st.player == null)
            return ;

        if (npc.getNpcId() == ShilensEvilThoughts && cond == 3) {
            st.setCond(4);
            st.playSound(SOUND_ITEMGET);
            st.giveItems(PieceofDoubt);
            st.set("evilthought", 2);
        }
    }
}