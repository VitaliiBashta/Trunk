package l2trunk.gameserver.model;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.base.Experience;

public final class SubClass {
    public static final int CERTIFICATION_65 = 1;
    public static final int CERTIFICATION_70 = 1 << 1;
    public static final int CERTIFICATION_75 = 1 << 2;
    public static final int CERTIFICATION_80 = 1 << 3;

    private int classId = 0;
    private long _exp = Experience.LEVEL[Config.ALT_GAME_START_LEVEL_TO_SUBCLASS], minExp = Experience.LEVEL[Config.ALT_GAME_START_LEVEL_TO_SUBCLASS], maxExp = Experience.LEVEL[Experience.LEVEL.length - 1];
    private int _sp = 0;
    private int _level = Config.ALT_GAME_START_LEVEL_TO_SUBCLASS, certification;
    private double _Hp = 1, _Mp = 1, _Cp = 1;
    private boolean _active = false, base = false;
    private DeathPenalty deathPenalty;

    public SubClass() {
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public long getExp() {
        return _exp;
    }

    public void setExp(long val) {
        val = Math.max(val, minExp);
        val = Math.min(val, maxExp);

        _exp = val;
        _level = Experience.getLevel(_exp);
    }

    public long getMaxExp() {
        return maxExp;
    }

    public void addExp(long val) {
        setExp(_exp + val);
    }

    public long getSp() {
        return Math.min(_sp, Integer.MAX_VALUE);
    }

    public void setSp(long spValue) {
        spValue = Math.max(spValue, 0);
        spValue = Math.min(spValue, Integer.MAX_VALUE);

        _sp = (int) spValue;
    }

    public void addSp(long val) {
        setSp(_sp + val);
    }

    public int getLevel() {
        return _level;
    }

    public double getHp() {
        return _Hp;
    }

    public void setHp(double hpValue) {
        _Hp = hpValue;
    }

    public double getMp() {
        return _Mp;
    }

    public void setMp(final double mpValue) {
        _Mp = mpValue;
    }

    public double getCp() {
        return _Cp;
    }

    public void setCp(final double cpValue) {
        _Cp = cpValue;
    }

    public boolean isActive() {
        return _active;
    }

    public void setActive(final boolean active) {
        _active = active;
    }

    public boolean isBase() {
        return base;
    }

    public void setBase(final boolean base) {
        this.base = base;
        minExp = Experience.LEVEL[this.base ? 1 : Config.ALT_GAME_START_LEVEL_TO_SUBCLASS];
        maxExp = Experience.LEVEL[(this.base ? Experience.getMaxLevel() : Experience.getMaxSubLevel()) + 1] - 1;
    }

    DeathPenalty getDeathPenalty(Player player) {
        if (deathPenalty == null)
            deathPenalty = new DeathPenalty(player, 0);
        return deathPenalty;
    }

    void setDeathPenalty(DeathPenalty dp) {
        deathPenalty = dp;
    }

    public int getCertification() {
        return certification;
    }

    public void setCertification(int certification) {
        this.certification = certification;
    }

    public void addCertification(int c) {
        certification |= c;
    }

    public boolean isCertificationGet(int v) {
        return (certification & v) == v;
    }

    @Override
    public String toString() {
        return ClassId.VALUES[classId].toString() + " " + _level;
    }

    public String toStringCB() {
        return ClassId.VALUES[classId].toString();
    }
}