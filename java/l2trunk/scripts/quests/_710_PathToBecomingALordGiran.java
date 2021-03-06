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

public final class _710_PathToBecomingALordGiran extends Quest {
    private static final int Saul = 35184;
    private static final int Gesto = 30511;
    private static final int Felton = 30879;
    private static final int CargoBox = 32243;

    private static final int FreightChest = 13014;
    private static final int GestoBox = 13013;

    private static final List<Integer> Mobs = List.of(
            20832, 20833, 20835, 21602, 21603, 21604, 21605, 21606, 21607, 21608, 21609);

    private static final int GiranCastle = 3;

    public _710_PathToBecomingALordGiran() {
        addStartNpc(Saul);
        addTalkId(Gesto, Felton, CargoBox);
        addQuestItem(FreightChest, GestoBox);
        addKillId(Mobs);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        Castle castle = ResidenceHolder.getCastle(GiranCastle);
        if (castle.getOwner() == null)
            return "Castle has no lord";
        Player castleOwner = castle.getOwner().getLeader().getPlayer();
        switch (event) {
            case "saul_q710_03.htm":
                st.start();
                st.setCond(1);
                st.playSound(SOUND_ACCEPT);
                break;
            case "gesto_q710_03.htm":
                st.setCond(3);
                break;
            case "felton_q710_02.htm":
                st.setCond(4);
                break;
            case "saul_q710_07.htm":
                Functions.npcSay(npc, NpcString.S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_GIRAN, st.player.getName());
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
        Castle castle = ResidenceHolder.getCastle(GiranCastle);
        if (castle.getOwner() == null)
            return "Castle has no lord";
        Player castleOwner = castle.getOwner().getLeader().getPlayer();
        if (npcId == Saul) {
            if (cond == 0) {
                if (castleOwner == st.player) {
                    if (castle.getDominion().getLordObjectId() != st.player.objectId())
                        htmltext = "saul_q710_01.htm";
                    else {
                        htmltext = "saul_q710_00.htm";
                        st.exitCurrentQuest();
                    }
                } else {
                    htmltext = "saul_q710_00a.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1) {
                st.setCond(2);
                htmltext = "saul_q710_04.htm";
            } else if (cond == 2)
                htmltext = "saul_q710_05.htm";
            else if (cond == 9)
                htmltext = "saul_q710_06.htm";
        } else if (npcId == Gesto) {
            if (cond == 2)
                htmltext = "gesto_q710_01.htm";
            else if (cond == 3 || cond == 4)
                htmltext = "gesto_q710_04.htm";
            else if (cond == 5) {
                st.takeItems(FreightChest);
                st.setCond(7);
                htmltext = "gesto_q710_05.htm";
            } else if (cond == 7)
                htmltext = "gesto_q710_06.htm";
            else if (cond == 8) {
                st.takeItems(GestoBox);
                st.setCond(9);
                htmltext = "gesto_q710_07.htm";
            } else if (cond == 9)
                htmltext = "gesto_q710_07.htm";

        } else if (npcId == Felton) {
            if (cond == 3)
                htmltext = "felton_q710_01.htm";
            else if (cond == 4)
                htmltext = "felton_q710_03.htm";
        } else if (npcId == CargoBox) {
            if (cond == 4) {
                st.setCond(5);
                st.giveItems(FreightChest);
                htmltext = "box_q710_01.htm";
            } else if (cond == 5)
                htmltext = "box_q710_02.htm";
        }

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 7) {
            st.giveItemIfNotHave(GestoBox, 300);
            if (st.getQuestItemsCount(GestoBox) >= 300)
                st.setCond(8);
        }
    }
}