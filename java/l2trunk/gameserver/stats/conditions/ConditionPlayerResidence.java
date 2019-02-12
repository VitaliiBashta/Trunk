package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.residence.ResidenceType;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.stats.Env;

public final class ConditionPlayerResidence extends Condition {
    private final int id;
    private final ResidenceType type;

    public ConditionPlayerResidence(int id, ResidenceType type) {
        this.id = id;
        this.type = type;
    }

    @Override
    protected boolean testImpl(Env env) {
        if (env.character instanceof Player) {
            Player player = (Player) env.character;
            Clan clan = player.getClan();
            if (clan == null)
                return false;

            int residenceId = clan.getResidenceId(type);

            return id > 0 ? residenceId == id : residenceId > 0;
        } else {
            return false;
        }
    }
}
