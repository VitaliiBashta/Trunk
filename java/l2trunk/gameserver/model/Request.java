package l2trunk.gameserver.model;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.lang.reference.HardReference;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public final class Request extends StatsSet {
    private final static AtomicInteger _nextId = new AtomicInteger();
    private final int id;
    private final L2RequestType _type;
    private final HardReference<Player> requestor;
    private HardReference<Player> reciever;
    private boolean _isRequestorConfirmed;
    private boolean _isRecieverConfirmed;
    private boolean _isCancelled;
    private boolean _isDone;
    private long _timeout;
    private Future<?> _timeoutTask;

    /**
     * Создает запрос
     */
    public Request(L2RequestType type, Player requestor, Player reciever) {
        id = _nextId.incrementAndGet();
        this.requestor = requestor.getRef();
        this.reciever = reciever.getRef();
        _type = type;
        requestor.setRequest(this);
        reciever.setRequest(this);
    }

    public Request setTimeout(long timeout) {
        _timeout = timeout > 0 ? System.currentTimeMillis() + timeout : 0;
        _timeoutTask = ThreadPoolManager.INSTANCE.schedule(this::timeout, timeout);
        return this;
    }

    public int getId() {
        return id;
    }

    /**
     * Отменяет запрос и очищает соответствующее поле у участников.
     */
    public void cancel() {
        _isCancelled = true;
        if (_timeoutTask != null)
            _timeoutTask.cancel(false);
        _timeoutTask = null;
        Player player = requestor.get();
        if (player != null && player.getRequest() == this)
            player.setRequest(null);
        player = getReciever();
        if (player != null && player.getRequest() == this)
            player.setRequest(null);
    }

    /**
     * Заканчивает запрос и очищает соответствующее поле у участников.
     */
    public void done() {
        _isDone = true;
        if (_timeoutTask != null)
            _timeoutTask.cancel(false);
        _timeoutTask = null;
        Player player = getRequestor();
        if (player != null && player.getRequest() == this)
            player.setRequest(null);
        player = getReciever();
        if (player != null && player.getRequest() == this)
            player.setRequest(null);
    }

    /**
     * Действие при таймауте.
     */
    private void timeout() {
        Player player = getReciever();
        if (player != null)
            if (player.getRequest() == this)
                player.sendPacket(SystemMsg.TIME_EXPIRED);
        cancel();
    }

    public Player getOtherPlayer(Player player) {
        if (player == getRequestor())
            return getReciever();
        if (player == getReciever())
            return getRequestor();
        return null;
    }

    public Player getRequestor() {
        return requestor.get();
    }

    private Player getReciever() {
        return reciever.get();
    }

    /**
     * Проверяет не просрочен ли запрос.
     */
    public boolean isInProgress() {
        if (_isCancelled)
            return false;
        if (_isDone)
            return false;
        if (_timeout == 0)
            return true;
        return _timeout > System.currentTimeMillis();
    }

    /**
     * Проверяет тип запроса.
     */
    public boolean isTypeOf(L2RequestType type) {
        return _type == type;
    }

    /**
     * Помечает участника как согласившегося.
     */
    public void confirm(Player player) {
        if (player == getRequestor())
            _isRequestorConfirmed = true;
        else if (player == getReciever())
            _isRecieverConfirmed = true;
    }

    /**
     * Проверяет согласился ли игрок с запросом.
     */
    public boolean isConfirmed(Player player) {
        if (player == getRequestor())
            return _isRequestorConfirmed;
        else if (player == getReciever())
            return _isRecieverConfirmed;
        return false; // WTF???
    }

    public enum L2RequestType {
        CUSTOM,
        PARTY,
        PARTY_ROOM,
        CLAN,
        ALLY,
        TRADE,
        TRADE_REQUEST,
        FRIEND,
        CHANNEL,
        DUEL,
        POST,
        COUPLE_ACTION,
        AUCTION_ITEM_ADD
    }
}