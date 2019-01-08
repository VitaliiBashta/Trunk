package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Mystic;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.tables.SkillTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * AI for:
 * Hot Springs Atrox (id 21321)
 * Hot Springs Atroxspawn (id 21317)
 * Hot Springs Bandersnatch (id 21322)
 * Hot Springs Bandersnatchling (id 21314)
 * Hot Springs Flava (id 21316)
 * Hot Springs Nepenthes (id 21319)
 */
public final class HotSpringsMob extends Mystic {
    private static final List<Integer> DeBuffs = List.of(4554, 4552);

    public HotSpringsMob(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (attacker != null && Rnd.chance(5)) {
            Integer DeBuff = Rnd.get(DeBuffs);
            List<Effect> effect = attacker.getEffectList().getEffectsBySkillId(DeBuff);
            if (effect != null) {
                int level = effect.get(0).getSkill().getLevel();
                if (level < 10) {
                    effect.get(0).exit();
                    SkillTable.INSTANCE.getInfo(DeBuff, level + 1).getEffects(actor, attacker);
                }
            } else {
                SkillTable.INSTANCE.getInfo(DeBuff).getEffects(actor, attacker);
            }
        }
        super.onEvtAttacked(attacker, damage);
    }
}