package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Summon;
import l2trunk.gameserver.model.base.TeamType;
import l2trunk.gameserver.model.instances.PetInstance;
import l2trunk.gameserver.tables.PetDataTable;
import l2trunk.gameserver.utils.Location;

public final class PetInfo extends L2GameServerPacket {
    private final int _runSpd;
    private final int _walkSpd;
    private final int MAtkSpd;
    private final int PAtkSpd;
    private final int pvp_flag;
    private final int karma;
    private final int rideable;
    private final int _type;
    private final int obj_id;
    private final int npc_id;
    private final int runing;
    private final int incombat;
    private final int dead;
    private final int _sp;
    private final int level;
    private final int _abnormalEffect;
    private final int _abnormalEffect2;
    private final int curFed;
    private final int maxFed;
    private final int curHp;
    private final int maxHp;
    private final int curMp;
    private final int maxMp;
    private final int curLoad;
    private final int maxLoad;
    private final int PAtk;
    private final int PDef;
    private final int MAtk;
    private final int MDef;
    private final int Accuracy;
    private final int Evasion;
    private final int Crit;
    private final int sps;
    private final int ss;
    private final int type;
    private final Location _loc;
    private final double col_redius;
    private final double col_height;
    private final long exp;
    private final long exp_this_lvl;
    private final long exp_next_lvl;
    private final TeamType _team;
    private int _showSpawnAnimation;
    private String _name, title;

    public PetInfo(Summon summon) {
        _type = summon.getSummonType();
        obj_id = summon.objectId();
        npc_id = summon.getTemplate().npcId;
        _loc = summon.getLoc();
        MAtkSpd = summon.getMAtkSpd();
        PAtkSpd = summon.getPAtkSpd();
        _runSpd = summon.getRunSpeed();
        _walkSpd = summon.getWalkSpeed();
        col_redius = summon.getColRadius();
        col_height = summon.getColHeight();
        runing = summon.isRunning() ? 1 : 0;
        incombat = summon.isInCombat() ? 1 : 0;
        dead = summon.isAlikeDead() ? 1 : 0;
        _name = summon.getName().equalsIgnoreCase(summon.getTemplate().name) ? "" : summon.getName();
        title = summon.getTitle();
        pvp_flag = summon.getPvpFlag();
        karma = summon.getKarma();
        curFed = summon.getCurrentFed();
        maxFed = summon.getMaxFed();
        curHp = (int) summon.getCurrentHp();
        maxHp = summon.getMaxHp();
        curMp = (int) summon.getCurrentMp();
        maxMp = summon.getMaxMp();
        _sp = summon.getSp();
        level = summon.getLevel();
        exp = summon.getExp();
        exp_this_lvl = summon.getExpForThisLevel();
        exp_next_lvl = summon.getExpForNextLevel();
        curLoad = summon instanceof PetInstance ? ((PetInstance)summon).getInventory().getTotalWeight() : 0;
        maxLoad = summon.getMaxLoad();
        PAtk = summon.getPAtk(null);
        PDef = summon.getPDef(null);
        MAtk = summon.getMAtk(null, null);
        MDef = summon.getMDef(null, null);
        Accuracy = summon.getAccuracy();
        Evasion = summon.getEvasionRate(null);
        Crit = summon.getCriticalHit(null, null);
        _abnormalEffect = summon.getAbnormalEffect();
        _abnormalEffect2 = summon.getAbnormalEffect2();
        // В режиме трансформации значек mount/dismount не отображается
        if (summon.owner.isTrasformed())
            rideable = 0; //not rideable
        else
            rideable = PetDataTable.isMountable(npc_id) ? 1 : 0;
        _team = summon.getTeam();
        ss = summon.getSoulshotConsumeCount();
        sps = summon.getSpiritshotConsumeCount();
        _showSpawnAnimation = summon.getSpawnAnimation();
        type = summon.getFormId();

    }

    public PetInfo update(Integer _showSpawnAnimation) {
        this._showSpawnAnimation = _showSpawnAnimation;
        return this;
    }

    @Override
    protected final void writeImpl() {
        writeC(0xb2);
        writeD(_type);
        writeD(obj_id);
        writeD(npc_id + 1000000);
        writeD(0); // 1=attackable
        writeD(_loc.x);
        writeD(_loc.y);
        writeD(_loc.z);
        writeD(_loc.h);
        writeD(0);
        writeD(MAtkSpd);
        writeD(PAtkSpd);
        writeD(_runSpd);
        writeD(_walkSpd);
        writeD(_runSpd/*_swimRunSpd*/);
        writeD(_walkSpd/*_swimWalkSpd*/);
        writeD(_runSpd/*_flRunSpd*/);
        writeD(_walkSpd/*_flWalkSpd*/);
        writeD(_runSpd/*_flyRunSpd*/);
        writeD(_walkSpd/*_flyWalkSpd*/);
        writeF(1/*_cha.getProperMultiplier()*/);
        writeF(1/*_cha.getAttackSpeedMultiplier()*/);
        writeF(col_redius);
        writeF(col_height);
        writeD(0); // right hand weapon
        writeD(0);
        writeD(0); // left hand weapon
        writeC(1); // name above char 1=true ... ??
        writeC(runing); // running=1
        writeC(incombat); // attacking 1=true
        writeC(dead); // dead 1=true
        writeC(_showSpawnAnimation); // invisible ?? 0=false  1=true   2=summoned (only works if model has a summon animation)
        writeD(-1);
        writeS(_name);
        writeD(-1);
        writeS(title);
        writeD(1);
        writeD(pvp_flag); //0=white, 1=purple, 2=purpleblink, if its greater then karma = purple
        writeD(karma); // hmm karma ??
        writeD(curFed); // how fed it is
        writeD(maxFed); //max fed it can be
        writeD(curHp); //current hp
        writeD(maxHp); // max hp
        writeD(curMp); //current mp
        writeD(maxMp); //max mp
        writeD(_sp); //sp
        writeD(level);// lvl
        writeQ(exp);
        writeQ(exp_this_lvl); // 0%  absolute value
        writeQ(exp_next_lvl); // 100% absoulte value
        writeD(curLoad); //weight
        writeD(maxLoad); //max weight it can carry
        writeD(PAtk);//patk
        writeD(PDef);//pdef
        writeD(MAtk);//matk
        writeD(MDef);//mdef
        writeD(Accuracy);//accuracy
        writeD(Evasion);//evasion
        writeD(Crit);//critical
        writeD(_runSpd);//speed
        writeD(PAtkSpd);//atkspeed
        writeD(MAtkSpd);//casting speed
        writeD(_abnormalEffect); //c2  abnormal visual effect... bleed=1; poison=2; bleed?=4;
        writeH(rideable);
        writeC(0); // c2
        writeH(0); // ??
        writeC(_team.ordinal()); // team aura (1 = blue, 2 = red)
        writeD(ss);
        writeD(sps);
        writeD(type);
        writeD(_abnormalEffect2);
    }
}