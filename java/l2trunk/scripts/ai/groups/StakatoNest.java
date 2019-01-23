package l2trunk.scripts.ai.groups;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.instances.MinionInstance;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.utils.PositionUtils;
import l2trunk.gameserver.utils.ReflectionUtils;

import java.util.List;

public final class StakatoNest extends Fighter {
    private static final List<Integer> BIZARRE_COCOON = List.of(18793, 18794, 18795, 18796, 18797, 18798);
    private static final int CANNIBALISTIC_STAKATO_LEADER = 22625;
    private static final int SPIKE_STAKATO_NURSE = 22630;
    private static final int SPIKE_STAKATO_NURSE_CHANGED = 22631;
    private static final int SPIKED_STAKATO_BABY = 22632;
    private static final int SPIKED_STAKATO_CAPTAIN = 22629;
    private static final int FEMALE_SPIKED_STAKATO = 22620;
    private static final int MALE_SPIKED_STAKATO = 22621;
    private static final int MALE_SPIKED_STAKATO_2 = 22622;
    private static final int SPIKED_STAKATO_GUARD = 22619;
    private static final int SKILL_GROWTH_ACCELERATOR = 2905;
    private static final int CANNIBALISTIC_STAKATO_CHIEF = 25667;
    private static final int QUEEN_SHYEED = 25671;

    private static final int FAIL_COCOON_CHANCE = 6;
    private static final int ABSORB_MINION_CHANCE = 20;
    private static boolean _debuffed = false;
    // Queen Shyeed Management
    private final Zone _zone_mob_buff = ReflectionUtils.getZone("[stakato_mob_buff]");
    private final Zone _zone_mob_buff_pc_display = ReflectionUtils.getZone("[stakato_mob_buff_display]");
    private final Zone _zone_pc_buff = ReflectionUtils.getZone("[stakato_pc_buff]");

    public StakatoNest(NpcInstance actor) {
        super(actor);
        if (BIZARRE_COCOON.contains(actor.getNpcId())) {
            actor.setInvul(true);
            actor.startImmobilized();
        }
    }

    @Override
    public void onEvtSpawn() {
        NpcInstance actor = getActor();
        if (actor.getNpcId() != QUEEN_SHYEED) {
            super.onEvtSpawn();
            return;
        }
        if (!_debuffed) {
            _debuffed = true;
            _zone_mob_buff.setActive(true);
            _zone_mob_buff_pc_display.setActive(true);
            _zone_pc_buff.setActive(false);
        }
        World.getAroundPlayers(actor)
                .forEach(p -> p.sendPacket(Msg.SHYEED_S_ROAR_FILLED_WITH_WRATH_RINGS_THROUGHOUT_THE_STAKATO_NEST));
        super.onEvtSpawn();
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        MonsterInstance _mob = (MonsterInstance) actor;

        if (_mob.getNpcId() == CANNIBALISTIC_STAKATO_LEADER && Rnd.chance(ABSORB_MINION_CHANCE) && _mob.getCurrentHpPercents() < 30) {
            MonsterInstance _follower = getAliveMinion(actor);

            if (_follower != null && _follower.getCurrentHpPercents() > 30) {
                _mob.abortAttack(true, false);
                _mob.abortCast(true, false);
                _mob.setHeading(PositionUtils.getHeadingTo(_mob, _follower));
                _mob.doCast(4485, _follower, false);
                _mob.setCurrentHp(_mob.getCurrentHp() + _follower.getCurrentHp(), false);
                _follower.doDie(_follower);
                _follower.deleteMe();
            }
        }
        super.onEvtAttacked(attacker, damage);
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();

        MinionInstance _minion = getAliveMinion(actor);
        MonsterInstance _leader;

        switch (actor.getNpcId()) {
            case SPIKE_STAKATO_NURSE:
                if (_minion == null)
                    break;
                actor.broadcastPacket(new MagicSkillUse(actor, 2046, 1, 1000));
                for (int i = 0; i < 3; i++)
                    spawnMonster(SPIKED_STAKATO_CAPTAIN, _minion, killer);
                break;
            case SPIKED_STAKATO_BABY:
                _leader = ((MinionInstance) actor).getLeader();
                if (_leader != null && !_leader.isDead())
                    ThreadPoolManager.INSTANCE.schedule(new ChangeMonster(SPIKE_STAKATO_NURSE_CHANGED, actor, killer), 3000L);
                break;
            case MALE_SPIKED_STAKATO:
                if (_minion == null)
                    break;
                actor.broadcastPacket(new MagicSkillUse(actor, 2046, 1, 1000));
                for (int i = 0; i < 3; i++)
                    spawnMonster(SPIKED_STAKATO_GUARD, _minion, killer);
                break;
            case FEMALE_SPIKED_STAKATO:
                _leader = ((MinionInstance) actor).getLeader();
                if (_leader != null && !_leader.isDead())
                    ThreadPoolManager.INSTANCE.schedule(new ChangeMonster(MALE_SPIKED_STAKATO_2, actor, killer), 3000L);
                break;
			/*
			case CANNIBALISTIC_STAKATO_CHIEF:
			if (killer.isPlayer())
			if (killer.getPlayer().getParty() != null)
			{
				List<L2Player> party = killer.getPlayer().getParty().getMembers();
				for (L2Player member : party)
					giveCocoon(member);
			}
			else
				giveCocoon(killer.getPlayer());
			break;
			 */
            case QUEEN_SHYEED:
                if (_debuffed) {
                    _debuffed = false;
                    _zone_pc_buff.setActive(true);
                    _zone_mob_buff.setActive(false);
                    _zone_mob_buff_pc_display.setActive(false);
                }
                break;
            default:
                break;
        }
        super.onEvtDead(killer);
    }

    @Override
    public void onEvtSeeSpell(Skill skill, Creature caster) {
        NpcInstance actor = getActor();
        if (actor == null || !BIZARRE_COCOON.contains(actor.getNpcId()) || caster == null || skill.id != SKILL_GROWTH_ACCELERATOR) {
            super.onEvtSeeSpell(skill, caster);
            return;
        }
        if (FAIL_COCOON_CHANCE > Rnd.get(100)) {
            caster.getPlayer().sendPacket(Msg.NOTHING_HAPPENED);
            return;
        }
        actor.doDie(null);
        actor.endDecayTask();
        NpcInstance mob = (NpcInstance) NpcHolder.getTemplate(CANNIBALISTIC_STAKATO_CHIEF).getNewInstance()
                .setSpawnedLoc(actor.getLoc())
                .setFullHpMp()
                .setReflection(actor.getReflection());
        mob.spawnMe(mob.getSpawnedLoc());
        mob.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, caster.getPlayer(), Rnd.get(1, 100));
        super.onEvtSeeSpell(skill, caster);
    }

    private MinionInstance getAliveMinion(NpcInstance npc) {
        MinionList ml = npc.getMinionList();
        if (ml != null && ml.hasAliveMinions())
            return ml.getAliveMinions().get(0);

        return null;
    }

    private void spawnMonster(int mobId, NpcInstance actor, Creature killer) {
        NpcInstance npc = (NpcInstance) NpcHolder.getTemplate(mobId).getNewInstance()
                .setSpawnedLoc(actor.getSpawnedLoc())
                .setFullHpMp()
                .setReflection(actor.getReflection());
        npc.spawnMe(actor.getSpawnedLoc());
        if (killer != null)
            npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, Rnd.get(1, 100));
    }

	/*
		private void giveCocoon(L2Player player)
		{
			if (Rnd.chance(20))
				player.getInventory().addItem(LARGE_STAKATO_COCOON, 1);
			else
				player.getInventory().addItem(SMALL_STAKATO_COCOON, 1);
		}
	 */

    @Override
    public boolean randomWalk() {
        return !(BIZARRE_COCOON.contains(getActor().getNpcId()) || getActor().getNpcId() == QUEEN_SHYEED);
    }

    @Override
    public boolean randomAnimation() {
        return !(BIZARRE_COCOON.contains(getActor().getNpcId()));
    }

    private class ChangeMonster extends RunnableImpl {
        private final int _monsterId;
        private final Creature _killer;
        private final NpcInstance _npc;

        ChangeMonster(int mobId, NpcInstance npc, Creature killer) {
            _monsterId = mobId;
            _npc = npc;
            _killer = killer;
        }

        @Override
        public void runImpl() {
            spawnMonster(_monsterId, _npc, _killer);
        }
    }
}