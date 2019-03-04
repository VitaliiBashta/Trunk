package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.Summon;
import l2trunk.gameserver.model.instances.PetInstance;
import l2trunk.gameserver.model.instances.SummonInstance;
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
        useSummon = set.isSet("useSummon");
        isDisguise = set.isSet("isDisguise");
        transformationName = set.getString("transformationName");
    }

    @Override
    public boolean checkCondition(final Player activeChar, final Creature target, boolean forceUse, boolean dontMove, boolean first) {
        Player player;
        if (!(target instanceof Player)) {
            return false;
        } else {
            player = (Player)target;
        }
        Summon summon = player.getPet();

        if (player.getActiveWeaponFlagAttachment() != null)
            return false;

        if (player.isTrasformed() && id != SKILL_TRANSFORM_DISPEL) {
            // Для всех скилов кроме Transform Dispel
            activeChar.sendPacket(SystemMsg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
            return false;
        }

        // Нельзя использовать летающую трансформу на территории Aden, или слишком высоко/низко, или при вызванном пете/саммоне, или в инстансе
        if ((id == SKILL_FINAL_FLYING_FORM || id == SKILL_AURA_BIRD_FALCON || id == SKILL_AURA_BIRD_OWL) && (player.getX() > -166168 || player.getZ() <= 0 || player.getZ() >= 6000 || summon != null || player.getReflection() != ReflectionManager.DEFAULT)) {
            activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(id, level));
            return false;
        }

        // Нельзя отменять летающую трансформу слишком высоко над землей
        if (player.isInFlyingTransform() && id == SKILL_TRANSFORM_DISPEL && Math.abs(player.getZ() - player.getLoc().correctGeoZ().z) > 333) {
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
            if (!(summon instanceof SummonInstance) || summon.isDead()) {
                activeChar.sendPacket(SystemMsg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
                return false;
            }
        } else if (summon instanceof PetInstance && id != SKILL_TRANSFORM_DISPEL && !isBaseTransformation()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_POLYMORPH_WHEN_YOU_HAVE_SUMMONED_A_SERVITORPET);
            return false;
        }
        // The ban on the use of a transform zone ant queen
        Zone QueenAntZone = ReflectionUtils.getZone("[queen_ant_epic]");
        if (player.isInZone(QueenAntZone) && id != SKILL_TRANSFORM_DISPEL && !isBaseTransformation() && !isSummonerTransformation() && !isCursedTransformation()) {
            player.sendMessage("It is forbidden to be in transformation.");
            return false;
        }
        return super.checkCondition(activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        Summon pet = ((Player)activeChar).getPet();
        if (useSummon) {
            if (!(pet instanceof SummonInstance) || pet.isDead()) {
                activeChar.sendPacket(SystemMsg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
                return;
            } else {
                pet.unSummon();
            }
        }

        if (isSummonerTransformation() && pet instanceof SummonInstance)
            pet.unSummon();

        targets.stream()
                .filter(Objects::nonNull)
                .filter(target -> target instanceof Player)
                .forEach(target -> getEffects(activeChar, target));

        if (isSSPossible())
            activeChar.unChargeShots(isMagic());
    }
}