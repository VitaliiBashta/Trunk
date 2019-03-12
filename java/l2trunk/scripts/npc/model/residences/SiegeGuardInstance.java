package l2trunk.scripts.npc.model.residences;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.Experience;
import l2trunk.gameserver.model.entity.events.impl.SiegeEvent;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.reward.RewardItem;
import l2trunk.gameserver.model.reward.RewardList;
import l2trunk.gameserver.model.reward.RewardType;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.util.List;
import java.util.Map;

public class SiegeGuardInstance extends NpcInstance {
    public SiegeGuardInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        setHasChatWindow(false);
    }

    @Override
    public int getAggroRange() {
        return 1200;
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        if (!(attacker instanceof Playable)) {
            return false;
        }
        Player player = ((Playable) attacker).getPlayer();
        SiegeEvent<?, ?> siegeEvent = getEvent(SiegeEvent.class);
        SiegeEvent<?, ?> siegeEvent2 = attacker.getEvent(SiegeEvent.class);
        Clan clan = player.getClan();
        if (siegeEvent == null)
            return false;
        return clan == null || siegeEvent != siegeEvent2 || siegeEvent.getSiegeClan(SiegeEvent.DEFENDERS, clan) == null;
    }

    @Override
    public boolean hasRandomAnimation() {
        return false;
    }

    @Override
    public boolean isInvul() {
        return false;
    }

    @Override
    protected void onDeath(Creature killer) {
        SiegeEvent<?, ?> siegeEvent = getEvent(SiegeEvent.class);
        if (killer != null) {
            Player player = killer.getPlayer();
            if (siegeEvent != null && player != null) {
                Clan clan = player.getClan();
                SiegeEvent<?, ?> siegeEvent2 = killer.getEvent(SiegeEvent.class);
                if (clan != null && siegeEvent == siegeEvent2 && siegeEvent.getSiegeClan(SiegeEvent.DEFENDERS, clan) == null) {
                    Playable topdam = getAggroList().getTopDamager();
                    if (topdam == null)
                        topdam = player;
                    Playable top = topdam;
                    getTemplate().getRewards().values().forEach(list ->
                            rollRewards(list, top));
                }
            }
        }
        super.onDeath(killer);
    }

    private void rollRewards(RewardList list, Playable topDamager) {
        final Player activePlayer = topDamager.getPlayer();

        if (activePlayer == null)
            return;

        final int diff = calculateLevelDiffForDrop(topDamager.getLevel());
        double mod = calcStat(Stats.REWARD_MULTIPLIER, 1., topDamager, null);
        mod *= Experience.penaltyModifier(diff, 9);

        List<RewardItem> rewardItems = list.roll(activePlayer, mod, false, true);

        rewardItems.forEach(drop ->
                dropItem(activePlayer, drop.itemId, drop.count));
    }

    @Override
    public boolean isFearImmune() {
        return true;
    }

    @Override
    public boolean isParalyzeImmune() {
        return true;
    }

    @Override
    public Clan getClan() {
        return null;
    }
}