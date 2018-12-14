package l2trunk.gameserver.model.instances.residences.clanhall;

import l2trunk.commons.lang.StringUtils;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.entity.events.impl.ClanHallTeamBattleEvent;
import l2trunk.gameserver.model.entity.events.objects.CTBSiegeClanObject;
import l2trunk.gameserver.model.entity.events.objects.CTBTeamObject;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.templates.npc.NpcTemplate;

public abstract class CTBBossInstance extends MonsterInstance {
    private static final int SKILL = 5456;
    private CTBTeamObject _matchTeamObject;

    protected CTBBossInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        setHasChatWindow(false);
    }

    @Override
    public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage) {
        if (attacker.getLevel() > (getLevel() + 8) && attacker.getEffectList().getEffectsCountForSkill(SKILL) == 0) {
            doCast(SKILL, attacker, false);
            return;
        }

        super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage);
    }

    @Override
    public boolean isAttackable(Creature attacker) {
        CTBSiegeClanObject clan = _matchTeamObject.getSiegeClan();
        if (clan != null && attacker.isPlayable()) {
            Player player = attacker.getPlayer();
            return player.getClan() != clan.getClan();
        }
        return true;
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        return isAttackable(attacker);
    }

    @Override
    public void onDeath(Creature killer) {
        ClanHallTeamBattleEvent event = getEvent(ClanHallTeamBattleEvent.class);
        event.processStep(_matchTeamObject);

        super.onDeath(killer);
    }

    @Override
    public String getTitle() {
        CTBSiegeClanObject clan = _matchTeamObject.getSiegeClan();
        return clan == null ? StringUtils.EMPTY : clan.getClan().getName();
    }

    public void setMatchTeamObject(CTBTeamObject matchTeamObject) {
        _matchTeamObject = matchTeamObject;
    }
}
