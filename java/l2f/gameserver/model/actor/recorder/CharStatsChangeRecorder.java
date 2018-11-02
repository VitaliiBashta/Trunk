package l2f.gameserver.model.actor.recorder;

import l2f.gameserver.model.Creature;
import l2f.gameserver.model.base.TeamType;

public class CharStatsChangeRecorder<T extends Creature> {
    public static final int BROADCAST_CHAR_INFO = 1 << 0; // Требуется обновить состояние персонажа у окружающих
    public static final int SEND_CHAR_INFO = 1 << 1; // Требуется обновить состояние только самому персонажу
    public static final int SEND_STATUS_INFO = 1 << 2; // Требуется обновить статус HP/MP/CP

    protected final T activeChar;

    protected int _level;

    protected int _accuracy;
    protected int _attackSpeed;
    protected int _castSpeed;
    protected int _criticalHit;
    protected int _evasion;
    protected int _magicAttack;
    protected int _magicDefence;
    protected int _maxHp;
    protected int _maxMp;
    protected int _physicAttack;
    protected int _physicDefence;
    protected int _runSpeed;

    protected int _abnormalEffects;
    protected int _abnormalEffects2;
    protected int _abnormalEffects3;

    protected TeamType _team;

    protected int _changes;

    public CharStatsChangeRecorder(T actor) {
        this.activeChar = actor;
    }

    protected int set(int flag, int oldValue, int newValue) {
        if (oldValue != newValue)
            _changes |= flag;
        return newValue;
    }

    protected long set(int flag, long oldValue, long newValue) {
        if (oldValue != newValue)
            _changes |= flag;
        return newValue;
    }

    protected String set(int flag, String oldValue, String newValue) {
        if (!oldValue.equals(newValue))
            _changes |= flag;
        return newValue;
    }

    protected <E extends Enum<E>> E set(int flag, E oldValue, E newValue) {
        if (oldValue != newValue)
            _changes |= flag;
        return newValue;
    }

    protected void refreshStats() {
        _accuracy = set(SEND_CHAR_INFO, _accuracy, activeChar.getAccuracy());
        _attackSpeed = set(BROADCAST_CHAR_INFO, _attackSpeed, activeChar.getPAtkSpd());
        _castSpeed = set(BROADCAST_CHAR_INFO, _castSpeed, activeChar.getMAtkSpd());
        _criticalHit = set(SEND_CHAR_INFO, _criticalHit, activeChar.getCriticalHit(null, null));
        _evasion = set(SEND_CHAR_INFO, _evasion, activeChar.getEvasionRate(null));
        _runSpeed = set(BROADCAST_CHAR_INFO, _runSpeed, activeChar.getRunSpeed());

        _physicAttack = set(SEND_CHAR_INFO, _physicAttack, activeChar.getPAtk(null));
        _physicDefence = set(SEND_CHAR_INFO, _physicDefence, activeChar.getPDef(null));
        _magicAttack = set(SEND_CHAR_INFO, _magicAttack, activeChar.getMAtk(null, null));
        _magicDefence = set(SEND_CHAR_INFO, _magicDefence, activeChar.getMDef(null, null));

        _maxHp = set(SEND_STATUS_INFO, _maxHp, activeChar.getMaxHp());
        _maxMp = set(SEND_STATUS_INFO, _maxMp, activeChar.getMaxMp());

        _level = set(SEND_CHAR_INFO, _level, activeChar.getLevel());

        _abnormalEffects = set(BROADCAST_CHAR_INFO, _abnormalEffects, activeChar.getAbnormalEffect());
        _abnormalEffects2 = set(BROADCAST_CHAR_INFO, _abnormalEffects2, activeChar.getAbnormalEffect2());
        _abnormalEffects3 = set(BROADCAST_CHAR_INFO, _abnormalEffects3, activeChar.getAbnormalEffect3());

        _team = set(BROADCAST_CHAR_INFO, _team, activeChar.getTeam());
    }

    public final void sendChanges() {
        refreshStats();
        onSendChanges();
        _changes = 0;
    }

    protected void onSendChanges() {
        if ((_changes & SEND_STATUS_INFO) == SEND_STATUS_INFO)
            activeChar.broadcastStatusUpdate();
    }
}
