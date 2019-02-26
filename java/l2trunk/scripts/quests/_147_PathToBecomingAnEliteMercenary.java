package l2trunk.scripts.quests;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;
import l2trunk.gameserver.network.serverpackets.components.NpcString;

import java.util.List;

public final class _147_PathToBecomingAnEliteMercenary extends Quest {
    private static final List<Integer> MERCENARY_CAPTAINS = List.of(
            36481, 36482, 36483, 36484, 36485, 36486, 36487, 36488, 36489);

    private static final List<Integer> CATAPULTAS = List.of(
            36499, 36500, 36501, 36502, 36503, 36504, 36505, 36506, 36507);

    public _147_PathToBecomingAnEliteMercenary() {
        super(PARTY_ALL);
        addStartNpc(MERCENARY_CAPTAINS);
        addKillId(CATAPULTAS);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("gludio_merc_cap_q0147_04b.htm".equalsIgnoreCase(event)) {
            st.giveItems(13766);
        } else if ("gludio_merc_cap_q0147_07.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        Player player = st.player;
        Castle castle = npc.getCastle();
        String htmlText = NO_QUEST_DIALOG;

        int cond = st.getCond();
        if (cond == 0) {
            if (player.getClan() != null) {
                if (player.getClan().getCastle() == castle.getId())
                    return "gludio_merc_cap_q0147_01.htm";
                else if (player.getClan().getCastle() > 0)
                    return "gludio_merc_cap_q0147_02.htm";
            }

            if (player.getLevel() < 40 || player.getClassId().occupation() < 2)
                htmlText = "gludio_merc_cap_q0147_03.htm";
            else if (!st.haveQuestItem(13766) )
                htmlText = "gludio_merc_cap_q0147_04a.htm";
            else
                htmlText = "gludio_merc_cap_q0147_04.htm";
        } else if (cond == 1 || cond == 2 || cond == 3)
            htmlText = "gludio_merc_cap_q0147_08.htm";
        else if (cond == 4) {
            htmlText = "gludio_merc_cap_q0147_09.htm";
            st.takeItems(13766);
            st.giveItems(13767);
            st.complete();
            st.playSound(SOUND_FINISH);
            st.finish();
        }

        return htmlText;
    }

    @Override
    public void onKill(Player killed, QuestState st) {
        if (st.getCond() == 1 || st.getCond() == 3) {
            if (isValidKill(killed, st.player)) {
                int killedCount = st.getInt("enemies");
                int maxCount = 10;
                killedCount++;
                if (killedCount < maxCount) {
                    st.set("enemies", killedCount);
                    st.player.sendPacket(new ExShowScreenMessage(NpcString.YOU_HAVE_DEFEATED_S2_OF_S1_ENEMIES, 4000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, String.valueOf(maxCount), String.valueOf(killedCount)));
                } else {
                    if (st.getCond() == 1)
                        st.setCond(2);
                    else if (st.getCond() == 3)
                        st.setCond(4);
                    st.unset("enemies");
                    st.player.sendPacket(new ExShowScreenMessage(NpcString.YOU_WEAKENED_THE_ENEMYS_ATTACK, 4000));
                }
            }
        }
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (isValidNpcKill(st.player, npc)) {
            if (st.getCond() == 1)
                st.setCond(3);
            else if (st.getCond() == 2)
                st.setCond(4);
        }
    }

    private boolean isValidKill(Player killed, Player killer) {
        DominionSiegeEvent killedSiegeEvent = killed.getEvent(DominionSiegeEvent.class);
        DominionSiegeEvent killerSiegeEvent = killer.getEvent(DominionSiegeEvent.class);

        if (killedSiegeEvent == null || killerSiegeEvent == null)
            return false;
        if (killedSiegeEvent == killerSiegeEvent)
            return false;
        return killed.getLevel() >= 61;
    }

    private boolean isValidNpcKill(Player killer, NpcInstance npc) {
        DominionSiegeEvent npcSiegeEvent = npc.getEvent(DominionSiegeEvent.class);
        DominionSiegeEvent killerSiegeEvent = killer.getEvent(DominionSiegeEvent.class);

        if (npcSiegeEvent == null || killerSiegeEvent == null)
            return false;
        return npcSiegeEvent != killerSiegeEvent;
    }

    @Override
    public void onCreate(QuestState qs) {
        qs.addPlayerOnKillListener();
    }

    @Override
    public void onAbort(QuestState qs) {
        qs.removePlayerOnKillListener();
        super.onAbort(qs);
    }
}
