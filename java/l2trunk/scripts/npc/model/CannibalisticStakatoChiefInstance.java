package l2trunk.scripts.npc.model;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Party;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.RaidBossInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.util.List;

public final class CannibalisticStakatoChiefInstance extends RaidBossInstance {
    private static final List<Integer> ITEMS = List.of(14833, 14834);

    public CannibalisticStakatoChiefInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    protected void onDeath(Creature killer) {
        super.onDeath(killer);
        if (!(killer instanceof Playable))
            return;
        Creature topdam = getAggroList().getTopDamager();
        if (topdam == null)
            topdam = killer;
        Player pc = topdam.getPlayer();
        if (pc == null)
            return;
        Party party = pc.getParty();
        int itemId;
        if (party != null) {
            for (Player partyMember : party.getMembersStream())
                if (pc.isInRange(partyMember, Config.ALT_PARTY_DISTRIBUTION_RANGE)) {
                    itemId = Rnd.get(ITEMS);
                    partyMember.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_OBTAINED_S1).addItemName(itemId));
                    partyMember.getInventory().addItem(itemId, 1, "CannibalisticStakatoChiefInstance");
                }
        } else {
            itemId = Rnd.get(ITEMS);
            pc.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_OBTAINED_S1).addItemName(itemId));
            pc.getInventory().addItem(itemId, 1, "CannibalisticStakatoChiefInstance");
        }
    }
}