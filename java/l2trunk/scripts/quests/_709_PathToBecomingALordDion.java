package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
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

public final class _709_PathToBecomingALordDion extends Quest {
    private static final int Crosby = 35142;
    private static final int Rouke = 31418;
    private static final int Sophia = 30735;

    private static final int MandragoraRoot = 13849;
    private static final int Epaulette = 13850;

    private static final List<Integer> OlMahums = List.of(20208, 20209, 20210, 20211);
    private static final List<Integer> Manragoras = List.of(20154, 20155, 20156);

    private static final int DionCastle = 2;

    public _709_PathToBecomingALordDion() {
        super(false);
        addStartNpc(Crosby);
        addTalkId(Sophia, Rouke);
        addQuestItem(Epaulette, MandragoraRoot);
        addKillId(OlMahums);
        addKillId(Manragoras);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        Castle castle = ResidenceHolder.getCastle(DionCastle);
        if (castle.getOwner() == null)
            return "Castle has no lord";
        Player castleOwner = castle.getOwner().getLeader().getPlayer();
        switch (event) {
            case "crosby_q709_03.htm":
                st.start();
                st.setCond(1);
                st.playSound(SOUND_ACCEPT);
                break;
            case "crosby_q709_06.htm":
                if (isLordAvailable(2, st)) {
                    castleOwner.getQuestState(this).set("confidant", st.player.objectId());
                    castleOwner.getQuestState(this).setCond(3);
                    st.start();
                } else
                    htmltext = "crosby_q709_05a.htm";
                break;
            case "rouke_q709_03.htm":
                if (isLordAvailable(3, st)) {
                    castleOwner.getQuestState(this).setCond(4);
                } else
                    htmltext = "crosby_q709_05a.htm";
                break;
            case "sophia_q709_02.htm":
                st.setCond(6);
                break;
            case "sophia_q709_05.htm":
                st.setCond(8);
                break;
            case "rouke_q709_05.htm":
                if (isLordAvailable(8, st)) {
                    st.takeItems(MandragoraRoot);
                    castleOwner.getQuestState(this).setCond(9);
                }
                break;
            case "crosby_q709_10.htm":
                Functions.npcSay(npc, NpcString.S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_DION, st.player.getName());
                castle.getDominion().changeOwner(castleOwner.getClan());
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest();
                break;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        Castle castle = ResidenceHolder.getCastle(DionCastle);
        if (castle.getOwner() == null)
            return "Castle has no lord";
        Player castleOwner = castle.getOwner().getLeader().player;
        if (npcId == Crosby) {
            if (cond == 0) {
                if (castleOwner == st.player) {
                    if (castle.getDominion().getLordObjectId() != st.player.objectId())
                        htmltext = "crosby_q709_01.htm";
                    else {
                        htmltext = "crosby_q709_00.htm";
                        st.exitCurrentQuest();
                    }
                } else if (isLordAvailable(2, st)) {
                    if (castleOwner.isInRangeZ(npc, 200))
                        htmltext = "crosby_q709_05.htm";
                    else
                        htmltext = "crosby_q709_05a.htm";
                } else {
                    htmltext = "crosby_q709_00a.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1) {
                st.setCond(2);
                htmltext = "crosby_q709_04.htm";
            } else if (cond == 2 || cond == 3)
                htmltext = "crosby_q709_04a.htm";
            else if (cond == 4) {
                st.setCond(5);
                htmltext = "crosby_q709_07.htm";
            } else if (cond == 5)
                htmltext = "crosby_q709_07.htm";
            else if (cond > 5 && cond < 9)
                htmltext = "crosby_q709_08.htm";
            else if (cond == 9)
                htmltext = "crosby_q709_09.htm";

        } else if (npcId == Rouke) {
            if (st.getState() == STARTED && cond == 0 && isLordAvailable(3, st)) {
                if (castleOwner.getQuestState(this).getInt("confidant") == st.player.objectId())
                    htmltext = "rouke_q709_01.htm";
            } else if (st.getState() == STARTED && cond == 0 && isLordAvailable(8, st)) {
                if (st.getQuestItemsCount(MandragoraRoot) >= 100)
                    htmltext = "rouke_q709_04.htm";
                else
                    htmltext = "rouke_q709_04a.htm";
            } else if (st.getState() == STARTED && cond == 0 && isLordAvailable(9, st)) {
                htmltext = "rouke_q709_06.htm";
            }

        } else if (npcId == Sophia) {
            if (cond == 5)
                htmltext = "sophia_q709_01.htm";
            else if (cond == 6)
                htmltext = "sophia_q709_03.htm";
            else if (cond == 7)
                htmltext = "sophia_q709_04.htm";
            else if (cond == 8)
                htmltext = "sophia_q709_06.htm";

        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 6 && OlMahums.contains(npc.getNpcId())) {
            if (Rnd.chance(10)) {
                st.giveItems(Epaulette);
                st.setCond(7);
            }
        }
        if (st.getState() == STARTED && st.getCond() == 0 && isLordAvailable(8, st) && Manragoras.contains(npc.getNpcId())) {
            if (st.getQuestItemsCount(MandragoraRoot) < 100)
                st.giveItems(MandragoraRoot);
        }
    }

    private boolean isLordAvailable(int cond, QuestState st) {
        Castle castle = ResidenceHolder.getCastle(DionCastle);
        Clan owner = castle.getOwner();
        Player castleOwner = castle.getOwner().getLeader().player;
        if (owner != null)
            return castleOwner != null && castleOwner != st.player && owner == st.player.getClan() && castleOwner.getQuestState(this) != null && castleOwner.getQuestState(this).getCond() == cond;
        return false;
    }

}