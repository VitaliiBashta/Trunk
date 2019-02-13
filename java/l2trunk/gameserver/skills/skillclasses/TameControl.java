package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.TamedBeastInstance;

import java.util.List;

public final class TameControl extends Skill {
    private final int type;

    public TameControl(StatsSet set) {
        super(set);
        type = set.getInteger("type", 0);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {

        if (isSSPossible())
            activeChar.unChargeShots(isMagic());

        if (activeChar instanceof Player) {
            Player player = (Player)activeChar;
            if (player.getTrainedBeasts() == null)
                return;

            if (type == 0)
                targets.stream()
                        .filter(target -> target instanceof TamedBeastInstance)
                        .filter(target -> player.getTrainedBeasts().get(target.objectId()) != null)
                        .map(target -> (TamedBeastInstance) target)
                        .forEach(t -> t.despawnWithDelay(1000));
            if (type == 1) // Приказать бежать за хозяином.
                player.getTrainedBeasts().values().forEach(tamedBeast ->
                        tamedBeast.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, player, Config.FOLLOW_RANGE));
            else if (type == 3) // Использовать особое умение
                player.getTrainedBeasts().values().forEach(TamedBeastInstance::buffOwner);
            else if (type == 4) // Отпустить всех зверей.
                player.getTrainedBeasts().values().forEach(TamedBeastInstance::doDespawn);
        }

    }

}