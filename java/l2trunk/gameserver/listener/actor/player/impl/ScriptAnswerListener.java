package l2trunk.gameserver.listener.actor.player.impl;

import l2trunk.commons.lang.reference.HardReference;
import l2trunk.gameserver.listener.actor.player.OnAnswerListener;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.scripts.Scripts;

public class ScriptAnswerListener implements OnAnswerListener {
    private final HardReference<Player> _playerRef;
    private final String _scriptName;
    private final Object[] _arg;

    public ScriptAnswerListener(Player player, String scriptName, Object[] arg) {
        _scriptName = scriptName;
        _arg = arg;
        _playerRef = player.getRef();
    }

    @Override
    public void sayYes() {
        Player player = _playerRef.get();
        if (player == null)
            return;

        Scripts.INSTANCE.callScripts(player, _scriptName.split(":")[0], _scriptName.split(":")[1], _arg);
    }

    @Override
    public void sayNo() {
        //
    }
}
