package l2trunk.scripts.quests;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _715_PathToBecomingALordGoddard extends Quest {
    private static final int Alfred = 35363;

    private static final int WaterSpiritAshutar = 25316;
    private static final int FireSpiritNastron = 25306;

    private static final int GoddardCastle = 7;

    public _715_PathToBecomingALordGoddard() {
        addStartNpc(Alfred);
        addKillId(WaterSpiritAshutar, FireSpiritNastron);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        Castle castle = ResidenceHolder.getCastle(GoddardCastle);
        if (castle.getOwner() == null)
            return "Castle has no lord";
        Player castleOwner = castle.getOwner().getLeader().getPlayer();

        switch (event) {
            case "alfred_q715_03.htm":
                st.start();
                st.setCond(1);
                st.playSound(SOUND_ACCEPT);
                break;
            case "alfred_q715_04a.htm":
                st.setCond(3);
                break;
            case "alfred_q715_04b.htm":
                st.setCond(2);
                break;
            case "alfred_q715_08.htm":
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
        int cond = st.getCond();
        Castle castle = ResidenceHolder.getCastle(GoddardCastle);
        if (castle.getOwner() == null)
            return "Castle has no lord";
        Player castleOwner = castle.getOwner().getLeader().player;

        if (cond == 0) {
            if (castleOwner == st.player) {
                if (castle.getDominion().getLordObjectId() != st.player.objectId())
                    htmltext = "alfred_q715_01.htm";
                else {
                    htmltext = "alfred_q715_00.htm";
                    st.exitCurrentQuest();
                }
            } else {
                htmltext = "alfred_q715_00a.htm";
                st.exitCurrentQuest();
            }
        } else if (cond == 1)
            htmltext = "alfred_q715_03.htm";
        else if (cond == 2)
            htmltext = "alfred_q715_05b.htm";
        else if (cond == 3)
            htmltext = "alfred_q715_05a.htm";
        else if (cond == 4) {
            st.setCond(6);
            htmltext = "alfred_q715_06b.htm";
        } else if (cond == 5) {
            st.setCond(7);
            htmltext = "alfred_q715_06a.htm";
        } else if (cond == 6)
            htmltext = "alfred_q715_06b.htm";
        else if (cond == 7)
            htmltext = "alfred_q715_06a.htm";
        else if (cond == 8 || cond == 9)
            htmltext = "alfred_q715_07.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 2 && npc.getNpcId() == FireSpiritNastron)
            st.setCond(4);
        else if (st.getCond() == 3 && npc.getNpcId() == WaterSpiritAshutar)
            st.setCond(5);

        if (st.getCond() == 6 && npc.getNpcId() == WaterSpiritAshutar)
            st.setCond(9);
        else if (st.getCond() == 7 && npc.getNpcId() == FireSpiritNastron)
            st.setCond(8);
    }

}