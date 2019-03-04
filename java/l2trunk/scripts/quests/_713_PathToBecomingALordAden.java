package l2trunk.scripts.quests;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;

import java.util.List;

public final class _713_PathToBecomingALordAden extends Quest {
    private static final int Logan = 35274;
    private static final int Orven = 30857;
    private static final List<Integer> Orcs = List.of(20669, 20665);

    private static final int AdenCastle = 5;
    private int mobs = 0;

    public _713_PathToBecomingALordAden() {
        super(false);
        addStartNpc(Logan);
        addTalkId(Orven);
        addKillId(Orcs);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        Castle castle = ResidenceHolder.getCastle(AdenCastle);
        if (castle.getOwner() == null)
            return "Castle has no lord";
        Player castleOwner = castle.getOwner().getLeader().getPlayer();

        switch (event) {
            case "logan_q713_02.htm":
                st.start();
                st.setCond(1);
                st.playSound(SOUND_ACCEPT);
                break;
            case "orven_q713_03.htm":
                st.setCond(2);
                break;
            case "logan_q713_05.htm":
                Functions.npcSay(npc, NpcString.S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_ADEN, st.player.getName());
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
        Castle castle = ResidenceHolder.getCastle(AdenCastle);
        if (castle.getOwner() == null)
            return "Castle has no lord";
        Player castleOwner = castle.getOwner().getLeader().getPlayer();

        if (npcId == Logan) {
            if (cond == 0) {
                if (castleOwner == st.player) {
                    if (castle.getDominion().getLordObjectId() != st.player.objectId())
                        htmltext = "logan_q713_01.htm";
                    else {
                        htmltext = "logan_q713_00.htm";
                        st.exitCurrentQuest();
                    }
                } else {
                    htmltext = "logan_q713_00a.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "logan_q713_03.htm";
            else if (cond == 7)
                htmltext = "logan_q713_04.htm";
        } else if (npcId == Orven) {
            if (cond == 1)
                htmltext = "orven_q713_01.htm";
            else if (cond == 2)
                htmltext = "orven_q713_04.htm";
            else if (cond == 4)
                htmltext = "orven_q713_05.htm";
            else if (cond == 5) {
                st.setCond(7);
                htmltext = "orven_q713_06.htm";
            } else if (cond == 7)
                htmltext = "orven_q713_06.htm";
        }

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 4)
            if (mobs++ >= 100)
                st.setCond(5);
    }
}