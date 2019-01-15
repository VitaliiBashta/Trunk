package l2trunk.gameserver.scripts;

public interface ScriptFile {
    void onLoad();

    default void onReload() {
    }

    default void onShutdown() {
    }
}