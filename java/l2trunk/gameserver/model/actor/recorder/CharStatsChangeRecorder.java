package l2trunk.gameserver.model.actor.recorder;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.base.TeamType;

public class CharStatsChangeRecorder<T extends Creature> {
    private static final int BROADCAST_CHAR_INFO = 1; // Требуется обновить состояние персонажа у окружающих
    private static final int SEND_CHAR_INFO = 1 << 1; // Требуется обновить состояние только самому персонажу
    private static final int SEND_STATUS_INFO = 1 << 2; // Требуется обновить статус HP/MP/CP

    final T activeChar;

    private int _level;

    private int _accuracy;
    private int _attackSpeed;
    private int _castSpeed;
    private int _criticalHit;
    private int _evasion;
    private int _magicAttack;
    private int _magicDefence;
    private int _maxHp;
    private int _maxMp;
    private int _physicAttack;
    private int _physicDefence;
    private int _runSpeed;

    private int _abnormalEffects;
    private int _abnormalEffects2;
    private int _abnormalEffects3;

    TeamType _team;

    int _changes;

    public CharStatsChangeRecorder(T actor) {
        this.activeChar = actor;
    }

    int set(int flag, int oldValue, int newValue) {
        if (oldValue != newValue)
            _changes |= flag;
        return newValue;
    }

    long set(int flag, long oldValue, long newValue) {
        if (oldValue != newValue)
            _changes |= flag;
        return newValue;
    }

    String set(int flag, String oldValue, String newValue) {
        if (!oldValue.equals(newValue))
            _changes |= flag;
        return newValue;
    }

    <E extends Enum<E>> E set(int flag, E oldValue, E newValue) {
        if (oldValue != newValue)
            _changes |= flag;
        return newValue;
    }

    void refreshStats() {
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

    void onSendChanges() {
        if ((_changes & SEND_STATUS_INFO) == SEND_STATUS_INFO)
            activeChar.broadcastStatusUpdate();
    }
}
