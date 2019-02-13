package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.instances.DoorInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.instances.SymbolInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillLaunched;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class EffectSymbol extends Effect {
    private static final Logger _log = LoggerFactory.getLogger(EffectSymbol.class);

    private NpcInstance symbol = null;

    public EffectSymbol(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public boolean checkCondition() {
        if (skill.targetType != Skill.SkillTargetType.TARGET_SELF) {
            _log.error("Symbol skill with target != player, id = " + skill.id);
            return false;
        }

        Skill firstAddedSkill = skill.getFirstAddedSkill();
        if (firstAddedSkill == null) {
            _log.error("Not implemented symbol skill, id = " + skill.id);
            return false;
        }

        if (effector.isInZonePeace() && effector instanceof Player) {
            ((Player)effector).sendMessage("You cannot do that in Peace Zone!");
            return false;
        }

        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        Skill firstAddedSkill = skill.getFirstAddedSkill();

        firstAddedSkill.setMagicType(skill.getMagicType());

        Location loc = effected.getLoc();
        if (effected instanceof Player && ((Player) effected).getGroundSkillLoc() != null) {
            loc = ((Player) effected).getGroundSkillLoc();
            ((Player) effected).setGroundSkillLoc(null);
        }

        NpcTemplate template = NpcHolder.getTemplate(this.skill.symbolId);
        if (getTemplate().count <= 1)
            symbol = new SymbolInstance(IdFactory.getInstance().getNextId(), template, effected, firstAddedSkill);
        else
            symbol = new NpcInstance(IdFactory.getInstance().getNextId(), template);

        symbol.setLevel(effected.getLevel());
        symbol.setReflection(effected.getReflection());
        symbol.spawnMe(loc);
    }

    @Override
    public void onExit() {
        super.onExit();

        if (symbol != null && symbol.isVisible())
            symbol.deleteMe();

        symbol = null;
    }

    @Override
    public boolean onActionTime() {
        if (getTemplate().count <= 1)
            return false;

        Skill firstAddedSkill = skill.getFirstAddedSkill();
//        NpcInstance symbol = this.symbol;
        double mpConsume = skill.getMpConsume();

        if (effector == null || firstAddedSkill == null || symbol == null)
            return false;

        if (mpConsume > effector.getCurrentMp()) {
            effector.sendPacket(SystemMsg.NOT_ENOUGH_MP);
            return false;
        }

        effector.reduceCurrentMp(mpConsume, effector);

        World.getAroundCharacters(symbol, skill.skillRadius, 200)
                .filter(cha -> !(cha instanceof DoorInstance) )
                .filter(cha -> cha.getEffectList().getEffectsBySkill(firstAddedSkill) == null)
                .filter(cha -> firstAddedSkill.checkTarget(effector, cha, cha, false, false) == null)
                .filter(cha -> !firstAddedSkill.isOffensive || !GeoEngine.canSeeTarget(symbol, cha, false))
                .forEach(cha -> {
                    effector.callSkill(firstAddedSkill, List.of(cha), true);
                    effector.broadcastPacket(new MagicSkillLaunched(symbol.objectId(), skill.displayId, skill.getDisplayLevel(), cha));
                });

        return true;
    }
}