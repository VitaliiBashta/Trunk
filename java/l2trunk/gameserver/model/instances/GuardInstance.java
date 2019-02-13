package l2trunk.gameserver.model.instances;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.templates.npc.NpcTemplate;

public class GuardInstance extends NpcInstance {
    public GuardInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public final boolean isAutoAttackable(Creature attacker) {
        return attacker instanceof MonsterInstance && ((MonsterInstance) attacker).isAggressive() || attacker instanceof Playable && ((Playable)attacker).getKarma() > 0;
    }

    @Override
    public String getHtmlPath(int npcId, int val, Player player) {
        String pom;
        if (val == 0)
            pom = "" + npcId;
        else
            pom = npcId + "-" + val;
        return "guard/" + pom + ".htm";
    }

    @Override
    public boolean isInvul() {
        return false;
    }

    @Override
    public boolean isFearImmune() {
        return true;
    }

    @Override
    public boolean isParalyzeImmune() {
        return true;
    }

    @Override
    protected void onReduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp) {
        getAggroList().addDamageHate(attacker, (int) damage, 0);

        super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp);
    }
}