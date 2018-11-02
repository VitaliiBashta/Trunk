package l2f.commons.net.nio.impl;

@SuppressWarnings("rawtypes")
public interface IMMOExecutor<T extends MMOClient> {
    void execute(Runnable r);
}