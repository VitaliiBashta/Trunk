package l2trunk.gameserver.scripts;

public interface ScriptFile {
    void onLoad();

    void onReload();

    void onShutdown();
}