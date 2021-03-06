package l2trunk.scripts.npc.model;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.ChestInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;


public final class TreasureChestInstance extends ChestInstance {
    private static final int TREASURE_BOMB_ID = 4143;

    public TreasureChestInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void tryOpen(Player opener, Skill skill) {
        double chance = calcChance(opener, skill);
        if (Rnd.chance(chance)) {
            getAggroList().addDamageHate(opener, 10000, 0);
            doDie(opener);
        } else
            fakeOpen(opener);
    }

    private double calcChance(Player opener, Skill skill) {

        double chance = skill.activateRate;
        int npcLvl = getLevel();
        if (!isCommonTreasureChest()) {
            double levelmod = (double) skill.magicLevel - npcLvl;
            chance += levelmod * skill.levelModifier;
            if (npcLvl - opener.getLevel() >= 5) // Custom way to prevent low occupation players opening top occupation chests.
                chance += (opener.getLevel() - npcLvl) * 10; // 10% penalty for each next occupation.
        } else {
            int openerLvl = opener.getLevel();
            int lvlDiff = Math.max(openerLvl - npcLvl, 0);
            if ((openerLvl <= 77 && lvlDiff >= 6) || (openerLvl >= 78 && lvlDiff >= 5))
                chance = 0;
        }
        if (chance < 0)
            chance = 1;
        return chance;
    }

    private void fakeOpen(Creature opener) {
        doCast(TREASURE_BOMB_ID, getBombLvl(), opener, false);
        onDecay();
    }

    private int getBombLvl() {
        int npcLvl = getLevel();
        int lvl = 1;
        if (npcLvl >= 78)
            lvl = 10;
        else if (npcLvl >= 72)
            lvl = 9;
        else if (npcLvl >= 66)
            lvl = 8;
        else if (npcLvl >= 60)
            lvl = 7;
        else if (npcLvl >= 54)
            lvl = 6;
        else if (npcLvl >= 48)
            lvl = 5;
        else if (npcLvl >= 42)
            lvl = 4;
        else if (npcLvl >= 36)
            lvl = 3;
        else if (npcLvl >= 30)
            lvl = 2;
        return lvl;
    }

    private boolean isCommonTreasureChest() {
        int npcId = getNpcId();
        return npcId >= 18265 && npcId <= 18286;
    }


    @Override
    public void onReduceCurrentHp(final double damage, final Creature attacker, Skill skill, final boolean awake, final boolean standUp, boolean directHp) {
        if (!isCommonTreasureChest())
            fakeOpen(attacker);
    }
}