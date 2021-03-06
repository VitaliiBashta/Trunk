package l2trunk.scripts.quests;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;

public final class _712_PathToBecomingALordOren extends Quest {
    private static final int Brasseur = 35226;
    private static final int Croop = 30676;
    private static final int Marty = 30169;
    private static final int Valleria = 30176;

    private static final int NebuliteOrb = 13851;

    private static final int[] OelMahims = {20575, 20576};

    private static final int OrenCastle = 4;

    public _712_PathToBecomingALordOren() {
        addStartNpc(Brasseur, Marty);
        addTalkId(Croop, Marty, Valleria);
        addQuestItem(NebuliteOrb);
        addKillId(OelMahims);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        Castle castle = ResidenceHolder.getCastle(OrenCastle);
        if (castle.getOwner() == null)
            return "Castle has no lord";
        Player castleOwner = castle.getOwner().getLeader().player;
        switch (event) {
            case "brasseur_q712_03.htm":
                st.start();
                st.setCond(1);
                st.playSound(SOUND_ACCEPT);
                break;
            case "croop_q712_03.htm":
                st.setCond(3);
                break;
            case "marty_q712_02.htm":
                if (isLordAvailable(3, st)) {
                    castleOwner.getQuestState(this).setCond(4);
                    st.start();
                }
                break;
            case "valleria_q712_02.htm":
                if (isLordAvailable(4, st)) {
                    castleOwner.getQuestState(this).setCond(5);
                    st.exitCurrentQuest();
                }
                break;
            case "croop_q712_05.htm":
                st.setCond(6);
                break;
            case "croop_q712_07.htm":
                st.setCond(8);
                break;
            case "brasseur_q712_06.htm":
                Functions.npcSay(npc, NpcString.S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_OREN, st.player.getName());
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
        Castle castle = ResidenceHolder.getCastle(OrenCastle);
        if (castle.getOwner() == null)
            return "Castle has no lord";
        Player castleOwner = castle.getOwner().getLeader().player;

        if (npcId == Brasseur) {
            if (cond == 0) {
                if (castleOwner == st.player) {
                    if (castle.getDominion().getLordObjectId() != st.player.objectId())
                        htmltext = "brasseur_q712_01.htm";
                    else {
                        htmltext = "brasseur_q712_00.htm";
                        st.exitCurrentQuest();
                    }
                } else {
                    htmltext = "brasseur_q712_00a.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1) {
                st.setCond(2);
                htmltext = "brasseur_q712_04.htm";
            } else if (cond == 2)
                htmltext = "brasseur_q712_04.htm";
            else if (cond == 8)
                htmltext = "brasseur_q712_05.htm";
        } else if (npcId == Croop) {
            if (cond == 2)
                htmltext = "croop_q712_01.htm";
            else if (cond == 3 || cond == 4)
                htmltext = "croop_q712_03.htm";
            else if (cond == 5)
                htmltext = "croop_q712_04.htm";
            else if (cond == 6)
                htmltext = "croop_q712_05.htm";
            else if (cond == 7)
                htmltext = "croop_q712_06.htm";
            else if (cond == 8)
                htmltext = "croop_q712_08.htm";
        } else if (npcId == Marty) {
            if (cond == 0) {
                if (isLordAvailable(3, st))
                    htmltext = "marty_q712_01.htm";
                else
                    htmltext = "marty_q712_00.htm";
            }
        } else if (npcId == Valleria) {
            if (st.getState() == STARTED && isLordAvailable(4, st))
                htmltext = "valleria_q712_01.htm";
        }

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 6) {
            if (st.getQuestItemsCount(NebuliteOrb) < 300)
                st.giveItems(NebuliteOrb);
            if (st.getQuestItemsCount(NebuliteOrb) >= 300)
                st.setCond(7);
        }
    }

    private boolean isLordAvailable(int cond, QuestState st) {
        Castle castle = ResidenceHolder.getCastle(OrenCastle);
        Clan owner = castle.getOwner();
        Player castleOwner = castle.getOwner().getLeader().player;
        if (owner != null)
            return castleOwner != null && castleOwner != st.player && owner == st.player.getClan() && castleOwner.getQuestState(this) != null && castleOwner.getQuestState(this).getCond() == cond;
        return false;
    }
}
