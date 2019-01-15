package l2trunk.gameserver.model.actor.listener;

import l2trunk.commons.listener.ListenerList;
import l2trunk.gameserver.listener.actor.player.*;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.utils.Location;

public final class PlayerListenerList extends CharListenerList {
    public PlayerListenerList(Player actor) {
        super(actor);
    }

    @Override
    public Player getActor() {
        return (Player) actor;
    }

    public void onEnter() {
        onEnter(global);
        onEnter(this);
    }

    public void onExit() {
        onExit(global);
        onExit(this);
    }

    public void onTeleport(Location loc, Reflection reflection) {
        onTeleport(global, loc, reflection);
        onTeleport(this, loc, reflection);
    }

    public void onPartyInvite() {
        onPartyInvite(global);
        onPartyInvite(this);
    }

    public void onPartyLeave() {
        onPartyLeave(global);
        onPartyLeave(this);
    }

    public void onQuestionMarkClicked(int questionMarkId) {
        onQuestionMarkClicked(global, questionMarkId);
        onQuestionMarkClicked(this, questionMarkId);
    }

    private void onTeleport(ListenerList list, Location loc, Reflection reflection) {
        list.getListeners().filter(l -> l instanceof OnTeleportListener)
                .map(l -> (OnTeleportListener) l)
                .forEach(l -> l.onTeleport(getActor(), loc, reflection));
    }

    private void onPartyInvite(ListenerList list) {
        list.getListeners().filter(l -> l instanceof OnPlayerPartyInviteListener)
                .map(l -> (OnPlayerPartyInviteListener) l)
                .forEach(l -> l.onPartyInvite(getActor()));
    }

    private void onEnter(ListenerList list) {
        list.getListeners().filter(l -> l instanceof OnPlayerEnterListener)
                .map(l -> (OnPlayerEnterListener) l)
                .forEach(l -> l.onPlayerEnter(getActor()));
    }

    private void onPartyLeave(ListenerList list) {
        list.getListeners().filter(l -> l instanceof OnPlayerPartyLeaveListener)
                .map(l -> (OnPlayerPartyLeaveListener) l)
                .forEach(l -> l.onPartyLeave(getActor()));
    }

    private void onExit(ListenerList list) {
        list.getListeners().filter(l -> l instanceof OnPlayerExitListener)
                .map(l -> (OnPlayerExitListener) l)
                .forEach(l -> l.onPlayerExit(getActor()));
    }

    private void onQuestionMarkClicked(ListenerList list, int questionMarkId) {
        list.getListeners().filter(l -> l instanceof OnQuestionMarkListener)
                .map(l -> (OnQuestionMarkListener) l)
                .forEach(l -> l.onQuestionMarkClicked(getActor(), questionMarkId));
    }
}
