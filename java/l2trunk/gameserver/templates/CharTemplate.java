package l2trunk.gameserver.templates;

import l2trunk.commons.collections.StatsSet;

import java.util.Collections;
import java.util.List;

public class CharTemplate {
    public int baseSTR;
    public int baseCON;
    public int baseDEX;
    public int baseINT;
    public int baseWIT;
    public int baseMEN;
    public double baseHpMax;
    public double baseCpMax;
    public double baseMpMax;
    public double baseHpReg;
    public double baseMpReg;
    public int basePAtk;
    public int baseMAtk;
    public int basePDef;
    public int baseMDef;
    public int basePAtkSpd;
    public int baseMAtkSpd;
    public int baseShldDef;
    public int baseAtkRange;
    public int baseShldRate;
    public int baseCritRate;
    public int baseRunSpd;
    public int baseWalkSpd;
    public List<Integer> baseAttributeAttack;
    public List<Integer> baseAttributeDefence;
    public double collisionRadius;
    public double collisionHeight;

    public CharTemplate(StatsSet set) {
        baseSTR = set.getInteger("baseSTR");
        baseCON = set.getInteger("baseCON");
        baseDEX = set.getInteger("baseDEX");
        baseINT = set.getInteger("baseINT");
        baseWIT = set.getInteger("baseWIT");
        baseMEN = set.getInteger("baseMEN");
        baseHpMax = set.getDouble("baseHpMax");
        baseCpMax = set.getDouble("baseCpMax");
        baseMpMax = set.getDouble("baseMpMax");
        baseHpReg = set.getDouble("baseHpReg");
        baseMpReg = set.getDouble("baseMpReg");
        basePAtk = set.getInteger("basePAtk");
        baseMAtk = set.getInteger("baseMAtk");
        basePDef = set.getInteger("basePDef");
        baseMDef = set.getInteger("baseMDef");
        basePAtkSpd = set.getInteger("basePAtkSpd");
        baseMAtkSpd = set.getInteger("baseMAtkSpd");
        baseShldDef = set.getInteger("baseShldDef");//
        baseAtkRange = set.getInteger("baseAtkRange");//
        baseShldRate = set.getInteger("baseShldRate");//
        baseCritRate = set.getInteger("baseCritRate");
        baseRunSpd = set.getInteger("baseRunSpd");
        baseWalkSpd = set.getInteger("baseWalkSpd");
        List<Integer> EMPTY_ATTRIBUTES = List.of(0,0,0,0,0,0);
        baseAttributeAttack = set.getIntegerList("baseAttributeAttack", EMPTY_ATTRIBUTES);
        baseAttributeDefence = set.getIntegerList("baseAttributeDefence", EMPTY_ATTRIBUTES);
        // Geometry
        collisionRadius = set.getDouble("collision_radius");
        if (collisionRadius == 0) collisionRadius=5;
        collisionHeight = set.getDouble("collision_height");
        if (collisionHeight == 0) collisionHeight =5;
    }

    public static StatsSet getEmptyStatsSet() {
        return new StatsSet()
                .set("baseHpReg", 3.e-3f)
                .set("baseMpReg", 3.e-3f)
                .set("basePDef", 100)
                .set("baseMDef", 100);
    }

    public int getNpcId() {
        return 0;
    }
}