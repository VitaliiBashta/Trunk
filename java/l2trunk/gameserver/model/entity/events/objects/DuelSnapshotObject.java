package l2trunk.gameserver.model.entity.events.objects;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.TeamType;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.utils.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class DuelSnapshotObject implements Serializable {
    private final TeamType team;
    private final Player player;
    private final List<Effect> _effects;
    private final Location returnLoc;
    private final double _currentHp;
    private final double _currentMp;
    private final double _currentCp;

    private boolean isDead;

    public DuelSnapshotObject(Player player, TeamType team) {
        this.player = player;
        this.team = team;
        returnLoc = player.getReflection().getReturnLoc() == null ? player.getLoc() : player.getReflection().getReturnLoc();

        _currentCp = player.getCurrentCp();
        _currentHp = player.getCurrentHp();
        _currentMp = player.getCurrentMp();

        _effects = new ArrayList<>();
        player.getEffectList().getAllEffects().forEach(eff -> {
            Effect effect = eff.getTemplate().getEffect(new Env(eff.getEffector(), eff.getEffected(), eff.getSkill()));
            effect.setCount(eff.getCount());
            effect.setPeriod(eff.getCount() == 1 ? eff.getPeriod() - eff.getTime() : eff.getPeriod());

            _effects.add(effect);
        });
    }

    public void restore(boolean abnormal) {
        if (!abnormal) {
            if (!player.isInOlympiadMode()) {
                player.getEffectList().stopAllEffects();
                for (Effect e : _effects)
                    player.getEffectList().addEffect(e);


                player.setCurrentCp(_currentCp);
                player.setCurrentHpMp(_currentHp, _currentMp);
            }
        }
    }

    public void teleport() {
        player.stablePoint = null;
        if (player.isFrozen())
            player.stopFrozen();

        ThreadPoolManager.INSTANCE.schedule(() -> player.teleToLocation(returnLoc, ReflectionManager.DEFAULT), 5000L);
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead() {
        isDead = true;
    }

    public Location getLoc() {
        return returnLoc;
    }

    public TeamType getTeam() {
        return team;
    }
}
