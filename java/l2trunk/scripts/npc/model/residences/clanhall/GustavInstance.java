package l2trunk.scripts.npc.model.residences.clanhall;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.entity.events.impl.ClanHallSiegeEvent;
import l2trunk.gameserver.model.entity.events.impl.SiegeEvent;
import l2trunk.gameserver.model.entity.events.objects.SpawnExObject;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;
import l2trunk.scripts.npc.model.residences.SiegeGuardInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public final class GustavInstance extends SiegeGuardInstance implements _34SiegeGuard {
    private final AtomicBoolean _canDead = new AtomicBoolean();
    private Future<?> _teleportTask;

    public GustavInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onSpawn() {
        super.onSpawn();

        _canDead.set(false);

        Functions.npcShout(this, NpcString.PREPARE_TO_DIE_FOREIGN_INVADERS_I_AM_GUSTAV_THE_ETERNAL_RULER_OF_THIS_FORTRESS_AND_I_HAVE_TAKEN_UP_MY_SWORD_TO_REPEL_THEE);
    }

    @Override
    public void onDeath(Creature killer) {
        if (!_canDead.get()) {
            _canDead.set(true);
            setCurrentHp(1, true);

            // Застваляем снять таргет и остановить аттаку
            World.getAroundCharacters(this).forEach(cha ->
                    ThreadPoolManager.INSTANCE.execute(new GameObjectTasks.NotifyAITask(cha, CtrlEvent.EVT_FORGET_OBJECT, this)));

            ClanHallSiegeEvent siegeEvent = getEvent(ClanHallSiegeEvent.class);
            if (siegeEvent == null)
                return;

            SpawnExObject obj = siegeEvent.getFirstObject(ClanHallSiegeEvent.BOSS);

            for (int i = 0; i < 3; i++) {
                final NpcInstance npc = obj.getSpawns().get(i).getFirstSpawned();

                Functions.npcSay(npc, ((_34SiegeGuard) npc).teleChatSay());
                npc.broadcastPacket(new MagicSkillUse(npc, 4235, 1, 10000));

                _teleportTask = ThreadPoolManager.INSTANCE.schedule(() -> {
                    Location loc = Location.findAroundPosition(Location.of(177134, -18807, -2256), 50, 100, npc.getGeoIndex());

                    npc.teleToLocation(loc);

                    if (npc == GustavInstance.this)
                        npc.reduceCurrentHp(npc.getCurrentHp(), npc, null, false, false, false, false, false, false, false);
                }, 10000L);
            }
        } else {
            if (_teleportTask != null) {
                _teleportTask.cancel(false);
                _teleportTask = null;
            }

            SiegeEvent siegeEvent = getEvent(SiegeEvent.class);
            if (siegeEvent == null)
                return;

            siegeEvent.processStep(getMostDamagedClan());

            super.onDeath(killer);
        }
    }

    private Clan getMostDamagedClan() {
        ClanHallSiegeEvent siegeEvent = getEvent(ClanHallSiegeEvent.class);

        Player temp;

        Map<Player, Integer> damageMap = new HashMap<>();

        for (AggroList.HateInfo info : getAggroList().getPlayableMap().values()) {
            Playable killer = (Playable) info.attacker;
            int damage = info.damage;
            temp = killer.getPlayer();

            if (temp == null || siegeEvent.getSiegeClan(SiegeEvent.ATTACKERS, temp.getClan()) == null)
                continue;

            if (!damageMap.containsKey(temp))
                damageMap.put(temp, damage);
            else {
                int dmg = damageMap.get(temp) + damage;
                damageMap.put(temp, dmg);
            }
        }

        int mostDamage = 0;
        Player player = null;

        for (Map.Entry<Player, Integer> entry : damageMap.entrySet()) {
            int damage = entry.getValue();
            Player t = entry.getKey();
            if (damage > mostDamage) {
                mostDamage = damage;
                player = t;
            }
        }

        return player == null ? null : player.getClan();
    }

    @Override
    public NpcString teleChatSay() {
        return NpcString.THIS_IS_UNBELIEVABLE_HAVE_I_REALLY_BEEN_DEFEATED_I_SHALL_RETURN_AND_TAKE_YOUR_HEAD;
    }

    @Override
    public boolean isEffectImmune() {
        return true;
    }
}
