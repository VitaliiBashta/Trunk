package l2trunk.gameserver.skills.skillclasses;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.network.serverpackets.Say2;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.templates.StatsSet;
import l2trunk.gameserver.utils.AutoBan;
import l2trunk.gameserver.utils.TimeUtils;

import java.util.List;

public class Imprison extends Skill {
    public Imprison(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        for (Creature target : targets) {
            if (target != null) {
                if (!target.isPlayer())
                    continue;

                Player player = target.getPlayer();
                AutoBan.doJailPlayer(player, (int) getPower() * 1000L, false);
                player.sendPacket(new Say2(0, ChatType.TELL, "♦", "Персонаж " + activeChar.getName() + " наложил на Вас проклятие заточения. Вы посажены в тюрьму на срок " + TimeUtils.minutesToFullString((int) getPower() / 60)));
            }
        }
    }
}