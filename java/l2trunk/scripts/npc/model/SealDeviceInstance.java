package l2trunk.scripts.npc.model;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.network.serverpackets.ExStartScenePlayer;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.gameserver.utils.Location;

public final class SealDeviceInstance extends MonsterInstance {
    private boolean _gaveItem = false;

    public SealDeviceInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void reduceCurrentHp(double i, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage) {
        if (this.getCurrentHp() < i) {
            if (!_gaveItem && ItemFunctions.getItemCount(attacker.getPlayer(), 13846) < 4) {
                this.setRHandId(15281);
                this.broadcastCharInfo();
                ItemFunctions.addItem(attacker.getPlayer(), 13846, 1, true, "SealDeviceInstance");
                _gaveItem = true;

                if (ItemFunctions.getItemCount(attacker.getPlayer(), 13846) >= 4) {
                    attacker.getPlayer().showQuestMovie(ExStartScenePlayer.SCENE_SSQ_SEALING_EMPEROR_2ND);
                    ThreadPoolManager.INSTANCE.schedule(new TeleportPlayer(attacker.getPlayer()), 26500L);
                }
            }
            i = this.getCurrentHp() - 1;
        }
        attacker.reduceCurrentHp(450, this, null, true, false, true, false, false, false, true);
        super.reduceCurrentHp(i, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage);
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
    public boolean isLethalImmune() {
        return true;
    }

    @Override
    public boolean isMovementDisabled() {
        return true;
    }

    private class TeleportPlayer extends RunnableImpl {
        final Player pl;

        TeleportPlayer(Player pl) {
            this.pl = pl;
        }

        @Override
        public void runImpl() {
            pl.getReflection().getNpcs()
                    .filter(n -> n.getNpcId() != 32586)
                    .filter(n -> n.getNpcId() != 32587)
                    .forEach(GameObject::deleteMe);
            pl.getPlayer().teleToLocation(new Location(-89560, 215784, -7488));
        }
    }
}