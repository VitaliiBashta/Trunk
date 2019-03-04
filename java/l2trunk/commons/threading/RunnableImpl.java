package l2trunk.commons.threading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RunnableImpl implements Runnable {
    protected static final Logger LOG = LoggerFactory.getLogger(RunnableImpl.class);

    protected abstract void runImpl();

    @Override
    public final void run() {
        try {
            runImpl();
        } catch (Exception e) {
            LOG.warn("Exception: RunnableImpl.run()");
            e.printStackTrace();
        }
    }
}
