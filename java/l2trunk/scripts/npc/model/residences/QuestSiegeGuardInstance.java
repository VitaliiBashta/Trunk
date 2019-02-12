package l2trunk.scripts.npc.model.residences;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.AggroList;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestEventType;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class QuestSiegeGuardInstance extends SiegeGuardInstance {
    public QuestSiegeGuardInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onDeath(Creature lastAttacker) {
        super.onDeath(lastAttacker);
        if (!(lastAttacker instanceof Playable))
            return;

        Player killer = ((Playable) lastAttacker).getPlayer();
        if (killer == null)
            return;

        Map<Playable, AggroList.HateInfo> aggroMap = getAggroList().getPlayableMap();

        List<Quest> quests = getTemplate().getEventQuests(QuestEventType.MOB_KILLED_WITH_QUEST);
        if (!quests.isEmpty()) {
            final List<Player> players = new ArrayList<>(); // массив с игроками, которые могут быть заинтересованы в квестах
            if (isRaid() && Config.ALT_NO_LASTHIT) {// Для альта на ластхит берем всех игроков вокруг
                for (Playable pl : aggroMap.keySet())
                    if (!pl.isDead() && (isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE) || killer.isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE)))
                        players.add(pl.getPlayer());
            } else if (killer.getParty() != null) {// если пати то собираем всех кто подходит
                for (Player pl : killer.getParty().getMembers())
                    if (!pl.isDead() && (isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE) || killer.isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE)))
                        players.add(pl);
            }

            for (Quest quest : quests) {
                Player toReward = killer;
                if (quest.getParty() != Quest.PARTY_NONE)
                    if (isRaid() || quest.getParty() == Quest.PARTY_ALL) {// если цель рейд или квест для всей пати награждаем всех участников
                        for (Player pl : players) {
                            QuestState qs = pl.getQuestState(quest);
                            if (qs != null && !qs.isCompleted())
                                quest.notifyKill(this, qs);
                        }
                        toReward = null;
                    } else { // иначе выбираем одного
                        List<Player> interested = players.stream()
                                .filter(pl -> pl.getQuestState(quest) != null)
                                .filter(pl -> !pl.getQuestState(quest).isCompleted())
                                .collect(Collectors.toList());// из тех, у кого взят квест
                        if (interested.isEmpty())
                            continue;

                        toReward = Rnd.get(interested);
                        if (toReward == null)
                            toReward = killer;
                    }

                if (toReward != null) {
                    QuestState qs = toReward.getQuestState(quest);
                    if (qs != null && !qs.isCompleted())
                        quest.notifyKill(this, qs);
                }
            }
        }
    }
}
