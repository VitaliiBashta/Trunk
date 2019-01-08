package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.utils.ReflectionUtils;

import java.util.List;
import java.util.Objects;

public final class Transformation extends Skill {
    public final boolean isDisguise;
    public final String transformationName;
    private final boolean useSummon;

    public Transformation(StatsSet set) {
        super(set);
        useSummon = set.getBool("useSummon", false);
        isDisguise = set.getBool("isDisguise", false);
        transformationName = set.getString("transformationName", null);
    }

    @Override
    public boolean checkCondition(final Creature activeChar, final Creature target, boolean forceUse, boolean dontMove, boolean first) {
        Player player = target.getPlayer();

        if (player == null || player.getActiveWeaponFlagAttachment() != null)
            return false;

        if (player.getTransformation() != 0 && getId() != SKILL_TRANSFORM_DISPEL) {
            // Для всех скилов кроме Transform Dispel
            activeChar.sendPacket(SystemMsg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
            return false;
        }

        // Нельзя использовать летающую трансформу на территории Aden, или слишком высоко/низко, или при вызванном пете/саммоне, или в инстансе
        if ((getId() == SKILL_FINAL_FLYING_FORM || getId() == SKILL_AURA_BIRD_FALCON || getId() == SKILL_AURA_BIRD_OWL) && (player.getX() > -166168 || player.getZ() <= 0 || player.getZ() >= 6000 || player.getPet() != null || player.getReflection() != ReflectionManager.DEFAULT)) {
            activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(id, level));
            return false;
        }

        // Нельзя отменять летающую трансформу слишком высоко над землей
        if (player.isInFlyingTransform() && getId() == SKILL_TRANSFORM_DISPEL && Math.abs(player.getZ() - player.getLoc().correctGeoZ().z) > 333) {
            activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(id, level));
            return false;
        }

        if (player.isInWater()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_POLYMORPH_INTO_THE_DESIRED_FORM_IN_WATER);
            return false;
        }

        if (player.isRiding() || player.getMountType() == 2) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_POLYMORPH_WHILE_RIDING_A_PET);
            return false;
        }

        // Для трансформации у игрока не должно быть активировано умение Mystic Immunity.
        if (player.getEffectList().getEffectsBySkillId(Skill.SKILL_MYSTIC_IMMUNITY) != null) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_POLYMORPH_WHILE_UNDER_THE_EFFECT_OF_A_SPECIAL_SKILL);
            return false;
        }

        if (player.isInBoat()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_POLYMORPH_WHILE_RIDING_A_BOAT);
            return false;
        }

        if (useSummon) {
            if (player.getPet() == null || !player.getPet().isSummon() || player.getPet().isDead()) {
                activeChar.sendPacket(SystemMsg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
                return false;
            }
        } else if (player.getPet() != null && player.getPet().isPet() && getId() != SKILL_TRANSFORM_DISPEL && !isBaseTransformation()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_POLYMORPH_WHEN_YOU_HAVE_SUMMONED_A_SERVITORPET);
            return false;
        }
        // The ban on the use of a transform zone ant queen
        Zone QueenAntZone = ReflectionUtils.getZone("[queen_ant_epic]");
        if (player.isInZone(QueenAntZone) && getId() != SKILL_TRANSFORM_DISPEL && !isBaseTransformation() && !isSummonerTransformation() && !isCursedTransformation()) {
            player.sendMessage("It is forbidden to be in transformation.");
            return false;
        }
        return super.checkCondition(activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        if (useSummon) {
            if (activeChar.getPet() == null || !activeChar.getPet().isSummon() || activeChar.getPet().isDead()) {
                activeChar.sendPacket(SystemMsg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
                return;
            }
            activeChar.getPet().unSummon();
        }

        if (isSummonerTransformation() && activeChar.getPet() != null && activeChar.getPet().isSummon())
            activeChar.getPet().unSummon();

        targets.stream()
                .filter(Objects::nonNull)
                .filter(GameObject::isPlayer)
                .forEach(target -> getEffects(activeChar, target));

        if (isSSPossible())
            if (!(Config.SAVING_SPS && skillType == SkillType.BUFF))
                activeChar.unChargeShots(isMagic());
    }
}