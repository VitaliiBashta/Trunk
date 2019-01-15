package l2trunk.gameserver.model.entity.events.actions;

import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.entity.events.EventAction;
import l2trunk.gameserver.model.entity.events.GlobalEvent;
import l2trunk.gameserver.network.serverpackets.PlaySound;

public final class PlaySoundAction implements EventAction {
    private final int range;
    private final String sound;
    private final PlaySound.Type type;

    public PlaySoundAction(int range, String s, PlaySound.Type type) {
        this.range = range;
        sound = s;
        this.type = type;
    }

    @Override
    public void call(GlobalEvent event) {
        GameObject object = event.getCenterObject();
        PlaySound packet;
        if (object != null)
            packet = new PlaySound(type, sound, 1, object.getObjectId(), object.getLoc());
        else
            packet = new PlaySound(type, sound, 0, 0, 0, 0, 0);

        event.broadcastPlayers(range)
                .forEach(p -> p.sendPacket(packet));
    }
}
