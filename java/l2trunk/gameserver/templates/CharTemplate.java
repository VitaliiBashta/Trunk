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
        collisionRadius = set.getDouble("collision_radius", 5);
        collisionHeight = set.getDouble("collision_height", 5);
    }

    public static StatsSet getEmptyStatsSet() {
        return new StatsSet()
                .set("baseSTR", 0)
                .set("baseCON", 0)
                .set("baseDEX", 0)
                .set("baseINT", 0)
                .set("baseWIT", 0)
                .set("baseMEN", 0)
                .set("baseHpMax", 0)
                .set("baseCpMax", 0)
                .set("baseMpMax", 0)
                .set("baseHpReg", 3.e-3f)
                .set("baseCpReg", 0)
                .set("baseMpReg", 3.e-3f)
                .set("basePAtk", 0)
                .set("baseMAtk", 0)
                .set("basePDef", 100)
                .set("baseMDef", 100)
                .set("basePAtkSpd", 0)
                .set("baseMAtkSpd", 0)
                .set("baseShldDef", 0)
                .set("baseAtkRange", 0)
                .set("baseShldRate", 0)
                .set("baseCritRate", 0)
                .set("baseRunSpd", 0)
                .set("baseWalkSpd", 0);
    }

    public int getNpcId() {
        return 0;
    }
}