package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.DecoyInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class Decoy extends Skill {
    private final int npcId;
    private final int lifeTime;

    public Decoy(StatsSet set) {
        super(set);

        npcId = set.getInteger("npcId", 0);
        lifeTime = set.getInteger("lifeTime", 1200) * 1000;
    }

    @Override

    public boolean checkCondition(Player player, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if (player.isAlikeDead() || player != target) // only TARGET_SELF
            return false;

        if (npcId <= 0)
            return false;

        if (player.isInObserverMode())
            return false;
        return super.checkCondition(player, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(Creature caster, List<Creature> targets) {
        Player activeChar = (Player)caster;

        NpcTemplate DecoyTemplate = NpcHolder.getTemplate(npcId);
        DecoyInstance decoy = new DecoyInstance(IdFactory.getInstance().getNextId(), DecoyTemplate, activeChar, lifeTime);

        decoy.setCurrentHp(decoy.getMaxHp(), false);
        decoy.setCurrentMp(decoy.getMaxMp());
        decoy.setHeading(activeChar.getHeading());
        decoy.setReflection(activeChar.getReflection());

        activeChar.setDecoy(decoy);

        decoy.spawnMe(Location.findAroundPosition(activeChar, 50, 70));

    }
}