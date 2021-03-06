package l2trunk.gameserver.model;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.skills.AbnormalEffect;

import java.util.concurrent.ScheduledFuture;

public final class PlayerVar {
    private final Player owner;
    private final String name;
    private final long expireTime;
    private final String value;
    private final ScheduledFuture<?> task;

    PlayerVar(Player owner, String name, String value, long expireTime) {
        this.owner = owner;
        this.name = name;
        this.value = value;
        this.expireTime = expireTime;

        if (expireTime > 0) {
            task = ThreadPoolManager.INSTANCE.schedule(new PlayerVarExpireTask(this), expireTime - System.currentTimeMillis());
        } else
            task = null;
    }

    long getTimeToExpire() {
        return expireTime - System.currentTimeMillis();
    }

    public String getValue() {
        return value;
    }

//    public void setValue(String val) {
//        value = val;
//    }

    boolean getValueBoolean() {
        return value.equals("1") || Boolean.valueOf(value);
    }

    void stopExpireTask() {
        if (task != null && !task.isDone()) {
            task.cancel(true);
        }
    }

    private static class PlayerVarExpireTask extends RunnableImpl {
        private final PlayerVar playerVar;

        private PlayerVarExpireTask(PlayerVar pv) {
            playerVar = pv;
        }

        private static void onUnsetVar(PlayerVar var) {
            if ("Para".equals(var.name)) {
                if (!var.owner.isBlocked())
                    return;
                var.owner.setBlock(false);
                var.owner.stopAbnormalEffect(AbnormalEffect.HOLD_1);
                var.owner.unsetVar("Para");
            }
        }

        @Override
        public void runImpl() {
            playerVar.owner.unsetVar(playerVar.name);
            onUnsetVar(playerVar);
        }
    }
}
