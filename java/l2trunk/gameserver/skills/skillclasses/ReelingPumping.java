package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Fishing;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.templates.item.WeaponTemplate;

import java.util.List;

public final class ReelingPumping extends Skill {

    public ReelingPumping(StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(Player player, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if (!player.isFishing()) {
            player.sendPacket(skillType == SkillType.PUMPING ? SystemMsg.YOU_MAY_ONLY_USE_THE_PUMPING_SKILL_WHILE_YOU_ARE_FISHING : SystemMsg.YOU_MAY_ONLY_USE_THE_REELING_SKILL_WHILE_YOU_ARE_FISHING);
            player.sendActionFailed();
            return false;
        }
        return super.checkCondition(player, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(Creature caster, List<Creature> targets) {
        if (!(caster instanceof Player))
            return;

        Player player = (Player) caster;
        Fishing fishing = player.getFishing();
        if (fishing == null || !fishing.isInCombat())
            return;

        WeaponTemplate weaponItem = player.getActiveWeaponItem();
        int SS = player.getChargedFishShot() ? 2 : 1;
        int pen = 0;
        double gradebonus = 1 + weaponItem.getCrystalType().ordinal() * 0.1;
        int dmg = (int) (power * gradebonus * SS);

        if (player.getSkillLevel(1315) < level - 2) { // 1315 - Fish Expertise
            // Penalty
            player.sendPacket(SystemMsg.DUE_TO_YOUR_REELING_ANDOR_PUMPING_SKILL_BEING_THREE_OR_MORE_LEVELS_HIGHER_THAN_YOUR_FISHING_SKILL_A_50_DAMAGE_PENALTY_WILL_BE_APPLIED);
            pen = 50;
            dmg = dmg - pen;
        }

        if (SS == 2)
            player.unChargeFishShot();

        fishing.useFishingSkill(dmg, pen, skillType);
    }
}