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

public final class _708_PathToBecomingALordGludio extends Quest {
    private static final int Sayres = 35100;
    private static final int Pinter = 30298;
    private static final int Bathis = 30332;

    private static final int HeadlessKnightsArmor = 13848;

    private static final List<Integer> MOBS = List.of(20045, 20051, 20099);

    private static final int GludioCastle = 1;

    public _708_PathToBecomingALordGludio() {
        addStartNpc(Sayres);
        addTalkId(Pinter, Bathis);
        addQuestItem(HeadlessKnightsArmor);
        addKillId(MOBS);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        Castle castle = ResidenceHolder.getCastle(GludioCastle);
        if (castle.getOwner() == null)
            return "Castle has no lord";
        Player castleOwner = castle.getOwner().getLeader().player;
        switch (event) {
            case "sayres_q708_03.htm":
                st.start();
                st.setCond(1);
                st.playSound(SOUND_ACCEPT);
                break;
            case "sayres_q708_05.htm":
                st.setCond(2);
                break;
            case "sayres_q708_08.htm":
                if (isLordAvailable(2, st)) {
                    castleOwner.getQuestState(this).set("confidant", st.player.objectId());
                    castleOwner.getQuestState(this).setCond(3);
                    st.start();
                } else
                    htmltext = "sayres_q708_05a.htm";
                break;
            case "pinter_q708_03.htm":
                if (isLordAvailable(3, st)) {
                    castleOwner.getQuestState(this).setCond(4);
                } else
                    htmltext = "pinter_q708_03a.htm";
                break;
            case "bathis_q708_02.htm":
                st.setCond(6);
                break;
            case "bathis_q708_05.htm":
                st.setCond(8);
                Functions.npcSay(npc, NpcString.LISTEN_YOU_VILLAGERS_OUR_LIEGE_WHO_WILL_SOON_BECAME_A_LORD_HAS_DEFEATED_THE_HEADLESS_KNIGHT);
                break;
            case "pinter_q708_05.htm":
                if (isLordAvailable(8, st)) {
                    st.takeItems(1867, 100);
                    st.takeItems(1865, 100);
                    st.takeItems(1869, 100);
                    st.takeItems(1879, 50);
                    castleOwner.getQuestState(this).setCond(9);
                } else
                    htmltext = "pinter_q708_03a.htm";
                break;
            case "sayres_q708_12.htm":
                Functions.npcSay(npc, NpcString.S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_GLUDIO, st.player.getName());
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
        Castle castle = ResidenceHolder.getCastle(GludioCastle);
        if (castle.getOwner() == null)
            return "Castle has no lord";
        Player castleOwner = castle.getOwner().getLeader().player;
        if (npcId == Sayres) {
            if (cond == 0) {
                if (castleOwner == st.player) {
                    if (castle.getDominion().getLordObjectId() != st.player.objectId())
                        htmltext = "sayres_q708_01.htm";
                    else {
                        htmltext = "sayres_q708_00.htm";
                        st.exitCurrentQuest();
                    }
                } else if (isLordAvailable(2, st)) {
                    if (castleOwner.isInRangeZ(npc, 200))
                        htmltext = "sayres_q708_07.htm";
                    else
                        htmltext = "sayres_q708_05a.htm";
                } else if (st.getState() == STARTED)
                    htmltext = "sayres_q708_08a.htm";
                else {
                    htmltext = "sayres_q708_00a.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1) {
                htmltext = "sayres_q708_04.htm";
            } else if (cond == 2)
                htmltext = "sayres_q708_06.htm";
            else if (cond == 4) {
                st.setCond(5);
                htmltext = "sayres_q708_09.htm";
            } else if (cond == 5)
                htmltext = "sayres_q708_10.htm";
            else if (cond > 5 && cond < 9)
                htmltext = "sayres_q708_08.htm";
            else if (cond == 9)
                htmltext = "sayres_q708_11.htm";

        } else if (npcId == Pinter) {
            if (st.getState() == STARTED && cond == 0 && isLordAvailable(3, st)) {
                if (castleOwner.getQuestState(this).getInt("confidant") == st.player.objectId())
                    htmltext = "pinter_q708_01.htm";
            } else if (st.getState() == STARTED && cond == 0 && isLordAvailable(8, st)) {
                if (st.getQuestItemsCount(1867) >= 100 && st.getQuestItemsCount(1865) >= 100 && st.getQuestItemsCount(1869) >= 100 && st.getQuestItemsCount(1879) >= 50)
                    htmltext = "pinter_q708_04.htm";
                else
                    htmltext = "pinter_q708_04a.htm";
            } else if (st.getState() == STARTED && cond == 0 && isLordAvailable(9, st)) {
                htmltext = "pinter_q708_06.htm";
            }

        } else if (npcId == Bathis) {
            if (cond == 5)
                htmltext = "bathis_q708_01.htm";
            else if (cond == 6)
                htmltext = "bathis_q708_03.htm";
            else if (cond == 7)
                htmltext = "bathis_q708_04.htm";
            else if (cond == 8)
                htmltext = "sophia_q709_06.htm";

        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 6) {
            if (Rnd.chance(10)) {
                st.giveItems(HeadlessKnightsArmor);
                st.setCond(7);
            }
        }
    }

    private boolean isLordAvailable(int cond, QuestState st) {
        Castle castle = ResidenceHolder.getCastle(GludioCastle);
        Clan owner = castle.getOwner();
        Player castleOwner = castle.getOwner().getLeader().getPlayer();
        if (owner != null)
            return castleOwner != null && castleOwner != st.player && owner == st.player.getClan() && castleOwner.getQuestState(this) != null && castleOwner.getQuestState(this).getCond() == cond;
        return false;
    }
}