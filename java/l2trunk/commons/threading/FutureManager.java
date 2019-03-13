package l2trunk.commons.threading;

import java.util.concurrent.Future;

public final class FutureManager {
    private FutureManager() {
    }

    public static void cancel(Future<?> future) {
        if (future != null)
            future.cancel(false);
    }

    public static void cancel(Future<?>... futures) {
        for (Future<?> future : futures) {
            if (future != null)
                future.cancel(false);
        }
    }
}
