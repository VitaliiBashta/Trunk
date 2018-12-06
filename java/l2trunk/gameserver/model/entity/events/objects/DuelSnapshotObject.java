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
    private final TeamType _team;
    private final Player _player;
    private final List<Effect> _effects;
    private final Location _returnLoc;
    private final double _currentHp;
    private final double _currentMp;
    private final double _currentCp;

    private boolean _isDead;

    public DuelSnapshotObject(Player player, TeamType team) {
        _player = player;
        _team = team;
        _returnLoc = player.getReflection().getReturnLoc() == null ? player.getLoc() : player.getReflection().getReturnLoc();

        _currentCp = player.getCurrentCp();
        _currentHp = player.getCurrentHp();
        _currentMp = player.getCurrentMp();

        List<Effect> effectList = player.getEffectList().getAllEffects();
        _effects = new ArrayList<>(effectList.size());
        for (Effect eff : effectList) {
            Effect effect = eff.getTemplate().getEffect(new Env(eff.getEffector(), eff.getEffected(), eff.getSkill()));
            effect.setCount(eff.getCount());
            effect.setPeriod(eff.getCount() == 1 ? eff.getPeriod() - eff.getTime() : eff.getPeriod());

            _effects.add(effect);
        }
    }

    public void restore(boolean abnormal) {
        if (!abnormal) {
            if (!_player.isInOlympiadMode()) {
                _player.getEffectList().stopAllEffects();
                for (Effect e : _effects)
                    _player.getEffectList().addEffect(e);


                _player.setCurrentCp(_currentCp);
                _player.setCurrentHpMp(_currentHp, _currentMp);
            }
        }
    }

    public void teleport() {
        _player._stablePoint = null;
        if (_player.isFrozen())
            _player.stopFrozen();

        ThreadPoolManager.INSTANCE.schedule(() -> _player.teleToLocation(_returnLoc, ReflectionManager.DEFAULT), 5000L);
    }

    public Player getPlayer() {
        return _player;
    }

    public boolean isDead() {
        return _isDead;
    }

    public void setDead() {
        _isDead = true;
    }

    public Location getLoc() {
        return _returnLoc;
    }

    public TeamType getTeam() {
        return _team;
    }
}
