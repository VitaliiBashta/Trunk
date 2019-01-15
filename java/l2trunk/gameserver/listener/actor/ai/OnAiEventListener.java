package l2trunk.gameserver.listener.actor.ai;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.listener.AiListener;
import l2trunk.gameserver.model.Creature;

import java.util.List;

public interface OnAiEventListener extends AiListener {
    void onAiEvent(Creature actor, CtrlEvent evt, List<Object> args);
}
