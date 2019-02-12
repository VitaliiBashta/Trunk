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

import java.util.List;

public final class _711_PathToBecomingALordInnadril extends Quest {
    private static final int Neurath = 35316;
    private static final int IasonHeine = 30969;

    private static final int InnadrilCastle = 6;
    private static final List<Integer> mobs = List.of(
            20789, 20790, 20791, 20792, 20793, 20804, 20805, 20806, 20807, 20808);

    public _711_PathToBecomingALordInnadril() {
        super(false);
        addStartNpc(Neurath);
        addTalkId(IasonHeine);
        addKillId(mobs);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        Castle castle = ResidenceHolder.getResidence(InnadrilCastle);
        Player castleOwner = castle.getOwner().getLeader().player();
        String htmltext = event;
        switch (event) {
            case "neurath_q711_03.htm":
                st.setState(STARTED);
                st.setCond(1);
                st.playSound(SOUND_ACCEPT);
                break;
            case "neurath_q711_05.htm":
                st.setCond(2);
                break;
            case "neurath_q711_08.htm":
                if (isLordAvailable(2, st)) {
                    castleOwner.getQuestState(this).set("confidant", st.player.objectId());
                    castleOwner.getQuestState(this).setCond(3);
                    st.setState(STARTED);
                } else
                    htmltext = "neurath_q711_07a.htm";

                break;
            case "heine_q711_03.htm":
                if (isLordAvailable(3, st))
                    castleOwner.getQuestState(this).setCond(4);
                else
                    htmltext = "heine_q711_00a.htm";
                break;
            case "neurath_q711_12.htm":
                Functions.npcSay(npc, NpcString.S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_INNADRIL, st.player.getName());
                castle.getDominion().changeOwner(castleOwner.getClan());
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(true);
                break;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        Castle castle = ResidenceHolder.getResidence(InnadrilCastle);
        if (castle.getOwner() == null)
            return "Castle has no lord";
        Player castleOwner = castle.getOwner().getLeader().player();

        if (npcId == Neurath) {
            if (cond == 0) {
                if (castleOwner == st.player) {
                    if (castle.getDominion().getLordObjectId() != st.player.objectId())
                        htmltext = "neurath_q711_01.htm";
                    else {
                        htmltext = "neurath_q711_00.htm";
                        st.exitCurrentQuest(true);
                    }
                } else if (isLordAvailable(2, st)) {
                    if (castleOwner.isInRangeZ(npc, 200))
                        htmltext = "neurath_q711_07.htm";
                    else
                        htmltext = "neurath_q711_07a.htm";
                } else if (st.getState() == STARTED)
                    htmltext = "neurath_q711_00b.htm";
                else {
                    htmltext = "neurath_q711_00a.htm";
                    st.exitCurrentQuest(true);
                }
            } else if (cond == 1)
                htmltext = "neurath_q711_04.htm";
            else if (cond == 2)
                htmltext = "neurath_q711_06.htm";
            else if (cond == 3)
                htmltext = "neurath_q711_09.htm";
            else if (cond == 4) {
                st.setCond(5);
                htmltext = "neurath_q711_10.htm";
            } else if (cond == 5)
                htmltext = "neurath_q711_10.htm";
            else if (cond == 6)
                htmltext = "neurath_q711_11.htm";
        } else if (npcId == IasonHeine) {
            if (st.getState() == STARTED && cond == 0) {
                if (isLordAvailable(3, st)) {
                    if (castleOwner.getQuestState(this).getInt("confidant") == st.player.objectId())
                        htmltext = "heine_q711_01.htm";
                    else
                        htmltext = "heine_q711_00.htm";
                } else if (isLordAvailable(4, st)) {
                    if (castleOwner.getQuestState(this).getInt("confidant") == st.player.objectId())
                        htmltext = "heine_q711_03.htm";
                    else
                        htmltext = "heine_q711_00.htm";
                } else
                    htmltext = "heine_q711_00a.htm";
            }
        }

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 5) {
            if (st.getInt("mobs") < 99)
                st.set("mobs", st.getInt("mobs") + 1);
            else
                st.setCond(6);
        }
    }

    private boolean isLordAvailable(int cond, QuestState st) {
        Castle castle = ResidenceHolder.getResidence(InnadrilCastle);
        Clan owner = castle.getOwner();
        Player castleOwner = castle.getOwner().getLeader().player;
        if (owner != null)
            return castleOwner != null && castleOwner != st.player && owner == st.player.getClan() && castleOwner.getQuestState(this) != null && castleOwner.getQuestState(this).getCond() == cond;
        return false;
    }

}