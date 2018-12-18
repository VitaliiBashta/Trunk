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
import l2trunk.gameserver.scripts.ScriptFile;

import java.util.List;

public abstract class Dominion_KillSpecialUnitQuest extends Quest implements ScriptFile {
    private final List<ClassId> _classIds;

    public Dominion_KillSpecialUnitQuest() {
        super(PARTY_ALL);

        _classIds = getTargetClassIds();
        DominionSiegeRunnerEvent runnerEvent = EventHolder.getEvent(EventType.MAIN_EVENT, 1);
        for (ClassId c : _classIds)
            runnerEvent.addClassQuest(c, this);
    }

    protected abstract NpcString startNpcString();

    protected abstract NpcString progressNpcString();

    protected abstract NpcString doneNpcString();

    protected abstract int getRandomMin();

    protected abstract int getRandomMax();

    protected abstract List<ClassId> getTargetClassIds();

    @Override
    public String onKill(Player killed, QuestState qs) {
        Player player = qs.getPlayer();
        if (player == null)
            return null;

        DominionSiegeEvent event1 = player.getEvent(DominionSiegeEvent.class);
        if (event1 == null)
            return null;
        DominionSiegeEvent event2 = killed.getEvent(DominionSiegeEvent.class);
        if (event2 == null || event2 == event1)
            return null;

        if (!_classIds.contains(killed.getClassId()))
            return null;

        int max_kills = qs.getInt("max_kills");
        if (max_kills == 0) {
            qs.setState(STARTED);
            qs.setCond(1);

            max_kills = Rnd.get(getRandomMin(), getRandomMax());
            qs.set("max_kills", max_kills);
            qs.set("current_kills", 1);
            if (player.getParty() == null)
                player.sendPacket(new ExShowScreenMessage(startNpcString(), 2000, String.valueOf(max_kills)));
            else {
                int max = max_kills;
                player.getParty().getMembers().forEach(member ->
                        member.sendPacket(new ExShowScreenMessage(startNpcString(), 2000, String.valueOf(max))));
            }
        } else {
            int current_kills = qs.getInt("current_kills") + 1;
            if (current_kills >= max_kills) {
                event1.addReward(player, DominionSiegeEvent.STATIC_BADGES, 10);

                qs.setState(COMPLETED);
                qs.addExpAndSp(534000, 51000);
                qs.exitCurrentQuest(true);

                if (player.getParty() == null)
                    player.sendPacket(new ExShowScreenMessage(doneNpcString(), 2000));
                else
                    for (Player member : player.getParty().getMembers())
                        member.sendPacket(new ExShowScreenMessage(doneNpcString(), 2000));

            } else {
                qs.set("current_kills", current_kills);
                if (player.getParty() == null)
                    player.sendPacket(new ExShowScreenMessage(progressNpcString(), 2000, String.valueOf(max_kills), String.valueOf(current_kills)));
                else
                    for (Player member : player.getParty().getMembers())
                        member.sendPacket(new ExShowScreenMessage(progressNpcString(), 2000, String.valueOf(max_kills), String.valueOf(current_kills)));

            }
        }

        return null;
    }

    @Override
    public boolean canAbortByPacket() {
        return false;
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onReload() {

    }

    @Override
    public void onShutdown() {

    }
}
