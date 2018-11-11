package l2trunk.gameserver.model.actor.recorder;

import l2trunk.commons.collections.CollectionUtils;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.Element;
import l2trunk.gameserver.model.matching.MatchingRoom;
import l2trunk.gameserver.network.serverpackets.ExStorageMaxCount;

public final class PlayerStatsChangeRecorder extends CharStatsChangeRecorder<Player> {
    public static final int BROADCAST_KARMA = 8;
    public static final int SEND_STORAGE_INFO = 16;
    public static final int SEND_MAX_LOAD = 32;
    public static final int SEND_CUR_LOAD = 64;
    public static final int BROADCAST_CHAR_INFO2 = 128;
    private int _maxCp;
    private int _maxLoad;
    private int _curLoad;
    private final int[] _attackElement = new int[6];
    private final int[] _defenceElement = new int[6];
    private long _exp;
    private int _sp;
    private int _karma;
    private int _pk;
    private int _pvp;
    private int _fame;
    private int _inventory;
    private int _warehouse;
    private int _clan;
    private int _trade;
    private int _recipeDwarven;
    private int _recipeCommon;
    private int _partyRoom;
    private String _title = "";
    private int _cubicsHash;

    public PlayerStatsChangeRecorder(Player activeChar) {
        super(activeChar);
    }

    protected void refreshStats() {
        this._maxCp = set(4, this._maxCp, this.activeChar.getMaxCp());

        super.refreshStats();

        this._maxLoad = set(34, this._maxLoad, this.activeChar.getMaxLoad());
        this._curLoad = set(64, this._curLoad, this.activeChar.getCurrentLoad());

        for (Element e : Element.VALUES) {
            this._attackElement[e.getId()] = set(2, this._attackElement[e.getId()], this.activeChar.getAttack(e));
            this._defenceElement[e.getId()] = set(2, this._defenceElement[e.getId()], this.activeChar.getDefence(e));
        }

        this._exp = set(2, this._exp, this.activeChar.getExp());
        this._sp = set(2, this._sp, this.activeChar.getIntSp());
        this._pk = set(2, this._pk, this.activeChar.getPkKills());
        this._pvp = set(2, this._pvp, this.activeChar.getPvpKills());
        this._fame = set(2, this._fame, this.activeChar.getFame());

        this._karma = set(8, this._karma, this.activeChar.getKarma());

        this._inventory = set(16, this._inventory, this.activeChar.getInventoryLimit());
        this._warehouse = set(16, this._warehouse, this.activeChar.getWarehouseLimit());
        this._clan = set(16, this._clan, Config.WAREHOUSE_SLOTS_CLAN);
        this._trade = set(16, this._trade, this.activeChar.getTradeLimit());
        this._recipeDwarven = set(16, this._recipeDwarven, this.activeChar.getDwarvenRecipeLimit());
        this._recipeCommon = set(16, this._recipeCommon, this.activeChar.getCommonRecipeLimit());
        this._cubicsHash = set(1, this._cubicsHash, CollectionUtils.hashCode(this.activeChar.getCubics()));
        this._partyRoom = set(1, this._partyRoom, ((this.activeChar.getMatchingRoom() != null) && (this.activeChar.getMatchingRoom().getType() == MatchingRoom.PARTY_MATCHING) && (this.activeChar.getMatchingRoom().getLeader() == this.activeChar)) ? this.activeChar.getMatchingRoom().getId() : 0);
        this._team = set(128, this._team, this.activeChar.getTeam());
        this._title = set(1, this._title, this.activeChar.getTitle());
    }

    protected void onSendChanges() {
        super.onSendChanges();

        if ((this._changes & 0x80) == 128) {
            this.activeChar.broadcastCharInfo();
            if (this.activeChar.getPet() != null)
                this.activeChar.getPet().broadcastCharInfo();
        }
        /* 100 */
        if ((this._changes & 0x1) == 1)
            this.activeChar.broadcastCharInfo();
        else if ((this._changes & 0x2) == 2) {
            this.activeChar.sendUserInfo();
        }
        if ((this._changes & 0x40) == 64) {
            this.activeChar.sendStatusUpdate(false, false, 14);
        }
        if ((this._changes & 0x20) == 32) {
            this.activeChar.sendStatusUpdate(false, false, 15);
        }
        if ((this._changes & 0x8) == 8) {
            this.activeChar.sendStatusUpdate(true, false, 27);
        }
        if ((this._changes & 0x10) == 16)
            this.activeChar.sendPacket(new ExStorageMaxCount(this.activeChar));
    }
}