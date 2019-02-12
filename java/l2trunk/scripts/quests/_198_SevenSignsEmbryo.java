package l2trunk.scripts.quests;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExStartScenePlayer;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.ReflectionUtils;

public final class _198_SevenSignsEmbryo extends Quest {
    // NPCs
    private static final int Wood = 32593;
    private static final int Franz = 32597;
    private static final int Jaina = 32582;
    private static final int ShilensEvilThoughtsCapt = 27346;

    // ITEMS
    private static final int PieceOfDoubt = 14355;
    private static final int DawnsBracelet = 15312;
    private static final int AncientAdena = 5575;

    private static final int izId = 113;

    private final Location setcloc = new Location(-23734, -9184, -5384, 0);

    public _198_SevenSignsEmbryo() {
        super(false);

        addStartNpc(Wood);
        addTalkId(Wood, Franz, Jaina);
        addKillId(ShilensEvilThoughtsCapt);
        addQuestItem(PieceOfDoubt);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        Player player = st.player;
        if ("wood_q198_2.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        } else if ("wood_q198_3.htm".equalsIgnoreCase(event)) {
            enterInstance(player);
            if (st.getInt("embryo") != 0)
                st.unset("embryo");
        } else if ("franz_q198_3.htm".equalsIgnoreCase(event)) {
            NpcInstance embryo = player.getReflection().addSpawnWithoutRespawn(ShilensEvilThoughtsCapt, setcloc);
            st.set("embryo", 1);
            Functions.npcSay(npc, player.getName() + "! You should kill this monster! I'll try to help!");
            Functions.npcSay(embryo, "This is not yours.");
            embryo.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, 500);
        } else if ("wood_q198_8.htm".equalsIgnoreCase(event))
            enterInstance(player);
        else if ("franz_q198_5.htm".equalsIgnoreCase(event)) {
            Functions.npcSay(npc, "We will be with you always...");
            st.takeItems(PieceOfDoubt);
            st.setCond(3);
            st.playSound(SOUND_MIDDLE);
        } else if ("jaina_q198_2.htm".equalsIgnoreCase(event))
            player.getReflection().collapse();
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
                if (player.getLevel() >= 79 && player.isQuestCompleted(_197_SevenSignsTheSacredBookofSeal.class))
                    htmltext = "wood_q198_1.htm";
                else {
                    htmltext = "wood_q198_0.htm";
                    st.exitCurrentQuest(true);
                }
            } else if (cond == 1 || cond == 2)
                htmltext = "wood_q198_2a.htm";
            else if (cond == 3) {
                if (player.getBaseClassId() == player.getActiveClassId()) {
                    st.addExpAndSp(315108090, 34906059);
                    st.giveItems(DawnsBracelet);
                    st.giveItems(AncientAdena, 1500000);
                    st.setState(COMPLETED);
                    st.playSound(SOUND_FINISH);
                    st.exitCurrentQuest(false);
                    htmltext = "wood_q198_4.htm";
                } else
                    htmltext = "subclass_forbidden.htm";
            }
        } else if (npcId == Franz) {
            if (cond == 1) {
                if (st.getInt("embryo") != 1)
                    htmltext = "franz_q198_1.htm";
                else
                    htmltext = "franz_q198_3a.htm";
            } else if (cond == 2)
                htmltext = "franz_q198_4.htm";
            else
                htmltext = "franz_q198_6.htm";
        } else if (npcId == Jaina)
            htmltext = "jaina_q198_1.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        Player player = st.player;
        if (player == null)
            return;

        if (npcId == ShilensEvilThoughtsCapt && cond == 1) {
            Functions.npcSay(npc, player.getName() + ", I'm leaving now. But we shall meet again!");
            st.set("embryo", 2);
            st.setCond(2);
            st.giveItems(PieceOfDoubt);
            player.showQuestMovie(ExStartScenePlayer.SCENE_SSQ_EMBRYO);
        }
    }

    private void enterInstance(Player player) {
        Reflection r = player.getActiveReflection();
        if (r != null) {
            if (player.canReenterInstance(izId))
                player.teleToLocation(r.getTeleportLoc(), r);
        } else if (player.canEnterInstance(izId)) {
            ReflectionUtils.enterReflection(player, izId);
        }
    }
}