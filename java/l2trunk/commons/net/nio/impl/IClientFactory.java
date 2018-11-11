package l2trunk.commons.net.nio.impl;

@SuppressWarnings("rawtypes")
public interface IClientFactory<T extends MMOClient> {
    T create(MMOConnection<T> con);
}