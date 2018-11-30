package l2trunk.commons.data.xml;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHolder  {
    public static final Logger LOG = LoggerFactory.getLogger(AbstractHolder.class);

    public void log() {
        LOG.info(String.format("loaded %d %s(s) count.", size(), getClass().getSimpleName()));
    }

    protected abstract int size();

    public abstract void clear();
}