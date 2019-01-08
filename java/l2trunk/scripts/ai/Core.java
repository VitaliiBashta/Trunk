package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.PlaySound;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Core extends Fighter {
    private static final int TELEPORTATION_CUBIC_ID = 31842;
    private static final Location CUBIC_1_POSITION = new Location(16502, 110165, -6394, 0);
    private static final Location CUBIC_2_POSITION = new Location(18948, 110165, -6394, 0);
    private static final int CUBIC_DESPAWN_TIME = 15 * 60 * 1000; // 15 min
    private boolean _firstTimeAttacked = true;

    public Core(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (_firstTimeAttacked) {
            Functions.npcSay(actor, NpcString.A_NONPERMITTED_TARGET_HAS_BEEN_DISCOVERED);
            Functions.npcSay(actor, NpcString.INTRUDER_REMOVAL_SYSTEM_INITIATED);
            _firstTimeAttacked = false;
        } else if (Rnd.chance(1))
            Functions.npcSay(actor, NpcString.REMOVING_INTRUDERS);
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();

        actor.broadcastPacket(new PlaySound(PlaySound.Type.MUSIC, "BS02_D", 1, 0, actor.getLoc()));
        Functions.npcSay(actor, NpcString.A_FATAL_ERROR_HAS_OCCURRED);
        Functions.npcSay(actor, NpcString.SYSTEM_IS_BEING_SHUT_DOWN);
        Functions.npcSay(actor, NpcString.CORE_);

        NpcInstance cubic1 = NpcHolder.getTemplate(TELEPORTATION_CUBIC_ID).getNewInstance();
        cubic1.setReflection(actor.getReflection());
        cubic1.setFullHpMp();
        cubic1.spawnMe(CUBIC_1_POSITION);

        NpcInstance cubic2 = NpcHolder.getTemplate(TELEPORTATION_CUBIC_ID).getNewInstance();
        cubic2.setReflection(actor.getReflection());
        cubic2.setFullHpMp();
        cubic2.spawnMe(CUBIC_2_POSITION);

        ThreadPoolManager.INSTANCE.schedule(() -> {
            cubic1.deleteMe();
            cubic2.deleteMe();
        }, CUBIC_DESPAWN_TIME);

        _firstTimeAttacked = true;
        super.onEvtDead(killer);
    }

}