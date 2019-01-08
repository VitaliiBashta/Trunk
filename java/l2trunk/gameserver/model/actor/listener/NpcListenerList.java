package l2trunk.gameserver.model.actor.listener;

import l2trunk.commons.listener.ListenerList;
import l2trunk.gameserver.listener.actor.npc.OnDecayListener;
import l2trunk.gameserver.listener.actor.npc.OnSpawnListener;
import l2trunk.gameserver.model.instances.NpcInstance;

public final class NpcListenerList extends CharListenerList {
    public NpcListenerList(NpcInstance actor) {
        super(actor);
    }

    @Override
    public NpcInstance getActor() {
        return (NpcInstance) actor;
    }

    public void onSpawn() {
        onSpawn(global);
        onSpawn(this);
    }

    public void onDecay() {
        onDecay(global);
        onDecay(this);
    }

    private void onSpawn(ListenerList list) {
        list.getListeners().stream()
                .filter(l -> l instanceof OnSpawnListener)
                .map(l -> (OnSpawnListener) l)
                .forEach(l -> l.onSpawn(getActor()));
    }

    private void onDecay(ListenerList list) {
        list.getListeners().stream()
                .filter(l -> l instanceof OnDecayListener)
                .map(l -> (OnDecayListener) l)
                .forEach(l -> l.onDecay(getActor()));
    }
}
