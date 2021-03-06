package l2trunk.scripts.events.CustomDropItems;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.actor.listener.CharListenerList;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class CustomDropItems extends Functions implements ScriptFile, OnDeathListener {
    private static final Logger _log = LoggerFactory.getLogger(CustomDropItems.class);

    private static final List<Integer> DROP = Config.CDItemsId;
    private static final List<Integer> CDItemsCountDropMin = Config.CDItemsCountDropMin;
    private static final List<Integer> CDItemsCountDropMax = Config.CDItemsCountDropMax;
    private static final List<Integer> CustomDropItemsChance = Config.CustomDropItemsChance;
    private static final boolean ALLOW_MIN_MAX_PLAYER_LVL = Config.CDItemsAllowMinMaxPlayerLvl;
    private static final int MIN_PLAYER_LVL = Config.CDItemsMinPlayerLvl;
    private static final int MAX_PLAYER_LVL = Config.CDItemsMaxPlayerLvl;
    private static final boolean ALLOW_MIN_MAX_MOB_LVL = Config.CDItemsAllowMinMaxMobLvl;
    private static final int MIN_MOB_LVL = Config.CDItemsMinMobLvl;
    private static final int MAX_MOB_LVL = Config.CDItemsMaxMobLvl;
    private static final boolean ALLOW_ONLY_RB_DROPS = Config.CDItemsAllowOnlyRbDrops;
    private static boolean _active = false;

    @Override
    public void onLoad() {
        CharListenerList.addGlobal(this);
        if (Config.AllowCustomDropItems) {
            _active = true;
            _log.info("Loaded CustomDropItems: CustomDropItems [state: activated]");
        } else
            _log.info("Loaded CustomDropItems: CustomDropItems [state: deactivated]");
    }

    @Override
    public void onDeath(Creature cha, Creature killer) {
        if (!ALLOW_ONLY_RB_DROPS) {
            if ((ALLOW_MIN_MAX_PLAYER_LVL && checkValidate(killer, cha, true, false)) && (ALLOW_MIN_MAX_MOB_LVL && checkValidate(killer, cha, false, true))) {
                dropItemMob(cha, killer);
            } else if ((ALLOW_MIN_MAX_PLAYER_LVL && checkValidate(killer, cha, true, false)) && !ALLOW_MIN_MAX_MOB_LVL) {
                dropItemMob(cha, killer);
            } else if (!ALLOW_MIN_MAX_PLAYER_LVL && (ALLOW_MIN_MAX_MOB_LVL && checkValidate(killer, cha, false, true))) {
                dropItemMob(cha, killer);
            } else if (!ALLOW_MIN_MAX_PLAYER_LVL && !ALLOW_MIN_MAX_MOB_LVL) {
                dropItemMob(cha, killer);
            }
        } else if (cha.isRaid() || cha.isBoss()) {
            if ((ALLOW_MIN_MAX_PLAYER_LVL && checkValidate(killer, cha, true, false)) && (ALLOW_MIN_MAX_MOB_LVL && checkValidate(killer, cha, false, true))) {
                dropItemRb(cha, killer);
            } else if ((ALLOW_MIN_MAX_PLAYER_LVL && checkValidate(killer, cha, true, false)) && !ALLOW_MIN_MAX_MOB_LVL) {
                dropItemRb(cha, killer);
            } else if (!ALLOW_MIN_MAX_PLAYER_LVL && (ALLOW_MIN_MAX_MOB_LVL && checkValidate(killer, cha, false, true))) {
                dropItemRb(cha, killer);
            } else if (!ALLOW_MIN_MAX_PLAYER_LVL && !ALLOW_MIN_MAX_MOB_LVL) {
                dropItemRb(cha, killer);
            }
        }
    }

    private boolean checkValidate(Creature killer, Creature mob, boolean lvlPlayer, boolean lvlMob) {
        if (mob == null || killer == null)
            return false;

        if (lvlPlayer && (killer.getLevel() >= MIN_PLAYER_LVL && killer.getLevel() <= MAX_PLAYER_LVL))
            return true;

        return lvlMob && (mob.getLevel() >= MIN_MOB_LVL && mob.getLevel() <= MAX_MOB_LVL);

    }

    private void dropItemMob(Creature cha, Creature killer) {
        if (killer instanceof Playable) {
            Playable  playable= (Playable) killer;
            if (_active && simpleCheckDrop(cha, playable))
                for (int i = 0; i < DROP.size(); i++)
                    if (Rnd.chance(CustomDropItemsChance.get(i) * playable.getPlayer().getRateItems() * ((MonsterInstance) cha).getTemplate().rateHp))
                        ((MonsterInstance) cha).dropItem(playable.getPlayer(), DROP.get(i), Rnd.get(CDItemsCountDropMin.get(i), CDItemsCountDropMax.get(i)));
                    else
                        return;
        }
    }

    private void dropItemRb(Creature cha, Creature killer) {
        if (killer instanceof Playable) {
            Playable playable = (Playable) killer;
            if (_active)
                for (int i = 0; i < DROP.size(); i++)
                    if (Rnd.chance(CustomDropItemsChance.get(i) * playable.getPlayer().getRateItems() * ((NpcInstance) cha).getTemplate().rateHp))
                        ((NpcInstance) cha).dropItem(playable.getPlayer(), DROP.get(i), Rnd.get(CDItemsCountDropMin.get(i), CDItemsCountDropMax.get(i)));
                    else
                        return;
        }
    }
}