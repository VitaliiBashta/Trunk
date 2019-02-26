package l2trunk.gameserver.model.instances;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.reward.RewardList;
import l2trunk.gameserver.model.reward.RewardType;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.ItemFunctions;

import java.util.Map;


public final class FestivalMonsterInstance extends MonsterInstance {
    private int _bonusMultiplier = 1;

    public FestivalMonsterInstance(int objectId, NpcTemplate template) {
        super(objectId, template);

        _hasRandomWalk = false;
    }

    public void setOfferingBonus(int bonusMultiplier) {
        _bonusMultiplier = bonusMultiplier;
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();

        World.getAroundPlayers(this)
                .filter(p -> !p.isDead())
                .findAny().ifPresent(p -> getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 1));
    }

    /**
     * Actions:
     * <li>Check if the killing object is a getPlayer, and then find the party they belong to.</li>
     * <li>Add a blood offering item to the leader of the party.</li>
     * <li>Update the party leader's inventory to show the new item addition.</li>
     */
    @Override
    public void rollRewards(Map.Entry<RewardType, RewardList> entry, final Creature lastAttacker, Creature topDamager) {
        super.rollRewards(entry, lastAttacker, topDamager);

        if (entry.getKey() != RewardType.RATED_GROUPED)
            return;
        if (topDamager instanceof Playable) {
            Party associatedParty = topDamager.getPlayer().getParty();

            if (associatedParty == null)
                return;

            Player partyLeader = associatedParty.getLeader();
            if (partyLeader == null)
                return;

            ItemInstance bloodOfferings = ItemFunctions.createItem(SevenSignsFestival.FESTIVAL_BLOOD_OFFERING);

            bloodOfferings.setCount(_bonusMultiplier);
            partyLeader.getInventory().addItem(bloodOfferings, "FestivalMonster Offerings");
            partyLeader.sendPacket(SystemMessage2.obtainItems(SevenSignsFestival.FESTIVAL_BLOOD_OFFERING, _bonusMultiplier, 0));
        }

    }

    @Override
    public boolean isAggressive() {
        return true;
    }

    @Override
    public int getAggroRange() {
        return 1000;
    }

    @Override
    public boolean hasRandomAnimation() {
        return false;
    }

    @Override
    public boolean canChampion() {
        return false;
    }
}