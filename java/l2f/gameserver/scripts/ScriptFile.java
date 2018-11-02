package l2f.gameserver.scripts;

public interface ScriptFile {
    void onLoad();

    void onReload();

    void onShutdown();
}