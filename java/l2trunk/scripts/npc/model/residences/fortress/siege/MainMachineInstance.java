package l2trunk.scripts.npc.model.residences.fortress.siege;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Spawner;
import l2trunk.gameserver.model.entity.events.impl.FortressSiegeEvent;
import l2trunk.gameserver.model.entity.events.objects.SpawnExObject;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.templates.npc.NpcTemplate;

public final class MainMachineInstance extends NpcInstance {
    private int powerUnits;

    public MainMachineInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onSpawn() {
        super.onSpawn();

        powerUnits = 3;
        FortressSiegeEvent siegeEvent = getEvent(FortressSiegeEvent.class);
        if (siegeEvent == null)
            return;

        siegeEvent.barrackAction(3, false);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (powerUnits != 0)
            return;

        Functions.npcShout(this, NpcString.FORTRESS_POWER_DISABLED);

        FortressSiegeEvent siegeEvent = getEvent(FortressSiegeEvent.class);
        if (siegeEvent == null)
            return;

        siegeEvent.spawnAction(FortressSiegeEvent.IN_POWER_UNITS, false);
        siegeEvent.barrackAction(3, true);

        siegeEvent.broadcastTo(SystemMsg.THE_BARRACKS_HAVE_BEEN_SEIZED, FortressSiegeEvent.ATTACKERS, FortressSiegeEvent.DEFENDERS);

        onDecay();

        siegeEvent.checkBarracks();
    }

    void powerOff(PowerControlUnitInstance powerUnit) {
        FortressSiegeEvent event = getEvent(FortressSiegeEvent.class);
        SpawnExObject exObject = event.getFirstObject(FortressSiegeEvent.IN_POWER_UNITS);

        int machineNumber = -1;
        for (int i = 0; i < 3; i++) {
            Spawner spawn = exObject.getSpawns().get(i);
            if (spawn == powerUnit.getSpawn())
                machineNumber = i;
        }

        NpcString msg;
        switch (machineNumber) {
            case 0:
                msg = NpcString.MACHINE_NO_1_POWER_OFF;
                break;
            case 1:
                msg = NpcString.MACHINE_NO_2_POWER_OFF;
                break;
            case 2:
                msg = NpcString.MACHINE_NO_3_POWER_OFF;
                break;
            default:
                throw new IllegalArgumentException("Wrong spawn at fortress: " + event.getName());
        }

        powerUnits--;
        Functions.npcShout(this, msg);
    }

    @Override
    public void showChatWindow(Player player, int val) {
        NpcHtmlMessage message = new NpcHtmlMessage(player, this);
        if (powerUnits != 0)
            message.setFile("residence2/fortress/fortress_mainpower002.htm");
        else
            message.setFile("residence2/fortress/fortress_mainpower001.htm");

        player.sendPacket(message);
    }
}
