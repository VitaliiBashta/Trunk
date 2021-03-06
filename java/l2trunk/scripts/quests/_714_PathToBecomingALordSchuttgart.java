package l2trunk.scripts.quests;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;

import java.util.stream.IntStream;

public final class _714_PathToBecomingALordSchuttgart extends Quest {
    private static final int August = 35555;
    private static final int Newyear = 31961;
    private static final int Yasheni = 31958;
    private static final int GolemShard = 17162;

    private static final int ShuttgartCastle = 9;

    public _714_PathToBecomingALordSchuttgart() {
        super(false);
        addStartNpc(August);
        addTalkId(Newyear, Yasheni);
        addKillId(IntStream.rangeClosed(22801, 22812).toArray());
        addQuestItem(GolemShard);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        Castle castle = ResidenceHolder.getCastle(ShuttgartCastle);
        if (castle.getOwner() == null)
            return "Castle has no lord";
        Player castleOwner = castle.getOwner().getLeader().getPlayer();

        switch (event) {
            case "august_q714_03.htm":
                st.start();
                st.setCond(1);
                st.playSound(SOUND_ACCEPT);
                break;
            case "august_q714_05.htm":
                st.setCond(2);
                break;
            case "newyear_q714_03.htm":
                st.setCond(3);
                break;
            case "yasheni_q714_02.htm":
                st.setCond(5);
                break;
            case "august_q714_08.htm":
                Functions.npcSay(npc, NpcString.S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_SCHUTTGART, st.player.getName());
                castle.getDominion().changeOwner(castleOwner.getClan());
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest();
                break;
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        Castle castle = ResidenceHolder.getCastle(ShuttgartCastle);
        if (castle.getOwner() == null)
            return "Castle has no lord";
        Player castleOwner = castle.getOwner().getLeader().getPlayer();

        if (npcId == August) {
            if (cond == 0) {
                if (castleOwner == st.player) {
                    if (castle.getDominion().getLordObjectId() != st.player.objectId())
                        htmltext = "august_q714_01.htm";
                    else {
                        htmltext = "august_q714_00.htm";
                        st.exitCurrentQuest();
                    }
                } else {
                    htmltext = "august_q714_00a.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "august_q714_04.htm";
            else if (cond == 2)
                htmltext = "august_q714_06.htm";
            else if (cond == 7) {
                htmltext = "august_q714_07.htm";
            }

        } else if (npcId == Newyear) {
            if (cond == 2) {
                htmltext = "newyear_q714_01.htm";
            } else if (cond == 3) {
                if (st.player.isQuestCompleted(_121_PavelTheGiants.class)) {
                    if (st.player.isQuestCompleted(_114_ResurrectionOfAnOldManager.class)) {
                        if (st.player.isQuestCompleted(_120_PavelsResearch.class)) {
                            st.setCond(4);
                            htmltext = "newyear_q714_04.htm";
                        } else
                            htmltext = "newyear_q714_04a.htm";
                    } else
                        htmltext = "newyear_q714_04b.htm";
                } else
                    htmltext = "newyear_q714_04c.htm";
            }
        } else if (npcId == Yasheni) {
            if (cond == 4)
                htmltext = "yasheni_q714_01.htm";
            else if (cond == 5)
                htmltext = "yasheni_q714_03.htm";
            else if (cond == 6) {
                st.takeItems(GolemShard);
                st.setCond(7);
                htmltext = "yasheni_q714_04.htm";
            }
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 5) {
            st.giveItemIfNotHave(GolemShard, 300);
            if (st.haveQuestItem(GolemShard, 300))
                st.setCond(6);
        }
    }

}