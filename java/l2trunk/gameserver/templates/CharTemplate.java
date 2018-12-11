package l2trunk.gameserver.templates;


import java.util.ArrayList;
import java.util.List;

public class CharTemplate {
    public final int baseSTR;
    public final int baseCON;
    public final int baseDEX;
    public final int baseINT;
    public final int baseWIT;
    public final int baseMEN;
    public final double baseHpMax;
    public final double baseCpMax;
    public final double baseMpMax;
    /**
     * HP Regen base
     */
    public final double baseHpReg;
    /**
     * MP Regen base
     */
    public final double baseMpReg;
    public final int basePAtk;
    public final int baseMAtk;
    public final int basePDef;
    public final int baseMDef;
    public final int basePAtkSpd;
    public final int baseMAtkSpd;
    public final int baseShldDef;
    public final int baseAtkRange;
    public final int baseShldRate;
    public final int baseCritRate;
    public final int baseRunSpd;
    public final int baseWalkSpd;
    public final List<Integer> baseAttributeAttack;
    public final List<Integer> baseAttributeDefence;
    public final double collisionRadius;
    public final double collisionHeight;
    private final List<Integer> EMPTY_ATTRIBUTES = new ArrayList<>(6);
    /**
     * CP Regen base
     */
    private final double baseCpReg;

    public CharTemplate(StatsSet set) {
        for (int i = 0; i < 6; i++) {
            EMPTY_ATTRIBUTES.add(0);
        }
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
        baseCpReg = set.getDouble("baseCpReg");
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
        baseAttributeAttack = set.getIntegerList("baseAttributeAttack", EMPTY_ATTRIBUTES);
        baseAttributeDefence = set.getIntegerList("baseAttributeDefence", EMPTY_ATTRIBUTES);
        // Geometry
        collisionRadius = set.getDouble("collision_radius", 5);
        collisionHeight = set.getDouble("collision_height", 5);
    }

    public static StatsSet getEmptyStatsSet() {
        StatsSet npcDat = new StatsSet();
        npcDat.set("baseSTR", 0);
        npcDat.set("baseCON", 0);
        npcDat.set("baseDEX", 0);
        npcDat.set("baseINT", 0);
        npcDat.set("baseWIT", 0);
        npcDat.set("baseMEN", 0);
        npcDat.set("baseHpMax", 0);
        npcDat.set("baseCpMax", 0);
        npcDat.set("baseMpMax", 0);
        npcDat.set("baseHpReg", 3.e-3f);
        npcDat.set("baseCpReg", 0);
        npcDat.set("baseMpReg", 3.e-3f);
        npcDat.set("basePAtk", 0);
        npcDat.set("baseMAtk", 0);
        npcDat.set("basePDef", 100);
        npcDat.set("baseMDef", 100);
        npcDat.set("basePAtkSpd", 0);
        npcDat.set("baseMAtkSpd", 0);
        npcDat.set("baseShldDef", 0);
        npcDat.set("baseAtkRange", 0);
        npcDat.set("baseShldRate", 0);
        npcDat.set("baseCritRate", 0);
        npcDat.set("baseRunSpd", 0);
        npcDat.set("baseWalkSpd", 0);
        return npcDat;
    }

    public int getNpcId() {
        return 0;
    }
}