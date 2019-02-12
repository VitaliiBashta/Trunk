package l2trunk.scripts.npc.model;

import l2trunk.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.EventTrigger;
import l2trunk.gameserver.network.serverpackets.ExStartScenePlayer;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.ReflectionUtils;

public final class OddGlobeInstance extends NpcInstance {
    private static final int instancedZoneId = 151;

    public OddGlobeInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if ("monastery_enter".equalsIgnoreCase(command)) {
            Reflection r = player.getActiveReflection();
            if (r != null) {
                if (player.canReenterInstance(instancedZoneId))
                    player.teleToLocation(r.getTeleportLoc(), r);
            } else if (player.canEnterInstance(instancedZoneId)) {
                Reflection newfew = ReflectionUtils.enterReflection(player, instancedZoneId);
                ZoneListener zoneL = new ZoneListener();
                newfew.getZone("[ssq_holy_burial_ground]").addListener(zoneL);
                ZoneListener2 zoneL2 = new ZoneListener2();
                newfew.getZone("[ssq_holy_seal]").addListener(zoneL2);
            }
        } else
            super.onBypassFeedback(player, command);
    }

    public class ZoneListener implements OnZoneEnterLeaveListener {
        private boolean done = false;

        @Override
        public void onZoneEnter(Zone zone, Player player) {
            if (player == null || done)
                return;
            done = true;
            player.showQuestMovie(ExStartScenePlayer.SCENE_SSQ2_HOLY_BURIAL_GROUND_OPENING);
        }

    }

    public class ZoneListener2 implements OnZoneEnterLeaveListener {
        private boolean done = false;

        @Override
        public void onZoneEnter(Zone zone, Player player) {
            player.broadcastPacket(new EventTrigger(21100100, true));
            if (!done) {
                done = true;
                player.showQuestMovie(ExStartScenePlayer.SCENE_SSQ2_SOLINA_TOMB_OPENING);
            }
        }

    }


}