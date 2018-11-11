package l2trunk.gameserver.model;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.skills.AbnormalEffect;

import java.util.concurrent.ScheduledFuture;

public class PlayerVar {
    private final Player owner;
    private final String name;
    private String value;
    private final long expire_time;

    @SuppressWarnings("rawtypes")
    private ScheduledFuture task;

    public PlayerVar(Player owner, String name, String value, long expire_time) {
        this.owner = owner;
        this.name = name;
        this.value = value;
        this.expire_time = expire_time;

        if (expire_time > 0) // if expires schedule expiration
        {
            task = ThreadPoolManager.getInstance().schedule(new PlayerVarExpireTask(this), expire_time - System.currentTimeMillis());
        }
    }

    private String getName() {
        return name;
    }

    private Player getOwner() {
        return owner;
    }

    public boolean hasExpired() {
        return task == null || task.isDone();
    }

    public long getTimeToExpire() {
        return expire_time - System.currentTimeMillis();
    }

    /**
     * @return возвращает значение переменной
     */
    public String getValue() {
        return value;
    }

    public void setValue(String val) {
        value = val;
    }

    /**
     * @return возвращает значение в виде логической переменной
     */
    public boolean getValueBoolean() {
        return value.equals("1") || value.equalsIgnoreCase("true");
    }

    public void stopExpireTask() {
        if (task != null && !task.isDone()) {
            task.cancel(true);
        }
    }

    private static class PlayerVarExpireTask extends RunnableImpl {
        private final PlayerVar _pv;

        private PlayerVarExpireTask(PlayerVar pv) {
            _pv = pv;
        }

        private static void onUnsetVar(PlayerVar var) {
            switch (var.getName()) {
                case "Para":
                    if (!var.getOwner().isBlocked())
                        return;
                    var.getOwner().unblock();
                    var.getOwner().stopAbnormalEffect(AbnormalEffect.HOLD_1);
                    if (var.getOwner().isPlayable())
                        var.getOwner().getPlayer().unsetVar("Para");
                    break;
            }
        }

        @Override
        public void runImpl() {
            Player pc = _pv.getOwner();
            if (pc == null) {
                return;
            }

            pc.unsetVar(_pv.getName());

            onUnsetVar(_pv);
        }
    }
}
