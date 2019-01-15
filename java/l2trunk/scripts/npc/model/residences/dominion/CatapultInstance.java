package l2trunk.scripts.npc.model.residences.dominion;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2trunk.gameserver.model.instances.residences.SiegeToggleNpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestEventType;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class CatapultInstance extends SiegeToggleNpcInstance {
    public CatapultInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onDeathImpl(Creature lastAttacker) {
        DominionSiegeEvent siegeEvent = getEvent(DominionSiegeEvent.class);
        if (siegeEvent == null)
            return;

        ThreadPoolManager.INSTANCE.execute(new GameObjectTasks.NotifyAITask(this, CtrlEvent.EVT_DEAD, lastAttacker));

        Player killer = lastAttacker.getPlayer();
        if (killer == null)
            return;

        Map<Playable, AggroList.HateInfo> aggroMap = getAggroList().getPlayableMap();

        List<Quest> quests = getTemplate().getEventQuests(QuestEventType.MOB_KILLED_WITH_QUEST);
        if (!quests.isEmpty()) {
            List<Player> players = null; // массив с игроками, которые могут быть заинтересованы в квестах
            if (isRaid() && Config.ALT_NO_LASTHIT) // Для альта на ластхит берем всех игроков вокруг
            {
                players = new ArrayList<>();
                for (Playable pl : aggroMap.keySet())
                    if (!pl.isDead() && (isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE) || killer.isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE)))
                        players.add(pl.getPlayer());
            } else if (killer.getParty() != null) // если пати то собираем всех кто подходит
            {
                players = new ArrayList<>(killer.getParty().size());
                for (Player pl : killer.getParty().getMembers())
                    if (!pl.isDead() && (isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE) || killer.isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE)))
                        players.add(pl);
            }

            for (Quest quest : quests) {
                Player toReward = killer;
                if (quest.getParty() != Quest.PARTY_NONE && players != null)
                    if (isRaid() || quest.getParty() == Quest.PARTY_ALL) // если цель рейд или квест для всей пати награждаем всех участников
                    {
                        for (Player pl : players) {
                            QuestState qs = pl.getQuestState(quest.getName());
                            if (qs != null && !qs.isCompleted())
                                quest.notifyKill(this, qs);
                        }
                        toReward = null;
                    } else { // иначе выбираем одного
                        List<Player> interested = new ArrayList<>(players.size());
                        for (Player pl : players) {
                            QuestState qs = pl.getQuestState(quest.getName());
                            if (qs != null && !qs.isCompleted()) // из тех, у кого взят квест
                                interested.add(pl);
                        }

                        if (interested.isEmpty())
                            continue;

                        toReward = interested.get(Rnd.get(interested.size()));
                        if (toReward == null)
                            toReward = killer;
                    }

                if (toReward != null) {
                    QuestState qs = toReward.getQuestState(quest.getName());
                    if (qs != null && !qs.isCompleted())
                        quest.notifyKill(this, qs);
                }
            }
        }
    }
}
