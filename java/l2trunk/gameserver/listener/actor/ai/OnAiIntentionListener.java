package l2trunk.gameserver.listener.actor.ai;

import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.listener.AiListener;
import l2trunk.gameserver.model.Creature;

public interface OnAiIntentionListener extends AiListener {
    void onAiIntention(Creature actor, CtrlIntention intention, Object arg0, Object arg1);
}
