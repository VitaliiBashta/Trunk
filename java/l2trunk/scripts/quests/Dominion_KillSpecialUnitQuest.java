package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.data.xml.holder.EventHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.entity.events.EventType;
import l2trunk.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2trunk.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;
import l2trunk.gameserver.network.serverpackets.components.NpcString;

import java.util.List;

public abstract class Dominion_KillSpecialUnitQuest extends Quest {
    private final List<ClassId> classIds;

    public Dominion_KillSpecialUnitQuest() {
        super(PARTY_ALL);

        classIds = getTargetClassIds();
        DominionSiegeRunnerEvent runnerEvent = EventHolder.getEvent(EventType.MAIN_EVENT, 1);
        classIds.forEach(c ->
                runnerEvent.addClassQuest(c, this));
    }

    protected abstract NpcString startNpcString();

    protected abstract NpcString progressNpcString();

    protected abstract NpcString doneNpcString();

    protected abstract int getRandomMin();

    protected abstract int getRandomMax();

    protected abstract List<ClassId> getTargetClassIds();

    @Override
    public void onKill(Player killed, QuestState st) {
        Player player = st.player;
        if (player == null)
            return ;

        DominionSiegeEvent event1 = player.getEvent(DominionSiegeEvent.class);
        if (event1 == null)
            return ;
        DominionSiegeEvent event2 = killed.getEvent(DominionSiegeEvent.class);
        if (event2 == null || event2 == event1)
            return ;

        if (!classIds.contains(killed.getClassId()))
            return ;

        int max_kills = st.getInt("max_kills");
        if (max_kills == 0) {
            st.start();
            st.setCond(1);

            max_kills = Rnd.get(getRandomMin(), getRandomMax());
            st.set("max_kills", max_kills);
            st.set("current_kills");
            if (player.getParty() == null)
                player.sendPacket(new ExShowScreenMessage(startNpcString(), 2000, String.valueOf(max_kills)));
            else {
                int max = max_kills;
                player.getParty().getMembers().forEach(member ->
                        member.sendPacket(new ExShowScreenMessage(startNpcString(), 2000, String.valueOf(max))));
            }
        } else {
            int current_kills = st.getInt("current_kills") + 1;
            if (current_kills >= max_kills) {
                event1.addReward(player, DominionSiegeEvent.STATIC_BADGES, 10);

                st.complete();
                st.addExpAndSp(534000, 51000);
                st.exitCurrentQuest();

                if (player.getParty() == null)
                    player.sendPacket(new ExShowScreenMessage(doneNpcString(), 2000));
                else
                    for (Player member : player.getParty().getMembers())
                        member.sendPacket(new ExShowScreenMessage(doneNpcString(), 2000));

            } else {
                st.inc("current_kills");
                if (player.getParty() == null)
                    player.sendPacket(new ExShowScreenMessage(progressNpcString(), 2000, String.valueOf(max_kills), String.valueOf(current_kills)));
                else
                    for (Player member : player.getParty().getMembers())
                        member.sendPacket(new ExShowScreenMessage(progressNpcString(), 2000, String.valueOf(max_kills), String.valueOf(current_kills)));

            }
        }
    }

    @Override
    public boolean canAbortByPacket() {
        return false;
    }
}
