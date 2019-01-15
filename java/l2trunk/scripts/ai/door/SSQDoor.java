package l2trunk.scripts.ai.door;

import l2trunk.commons.geometry.Rectangle;
import l2trunk.gameserver.ai.DoorAI;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Territory;
import l2trunk.gameserver.model.instances.DoorInstance;
import l2trunk.gameserver.model.instances.NpcInstance;

import java.util.List;
import java.util.stream.Stream;

public final class SSQDoor extends DoorAI {
    private static final Territory room1 = new Territory().add(new Rectangle(-89696, 217741, -88858, 218085).setZmin(-7520).setZmax(-7320));
    private static final Territory room2 = new Territory().add(new Rectangle(-88773, 220765, -88429, 219596).setZmin(-7520).setZmax(-7320));
    private static final Territory room3 = new Territory().add(new Rectangle(-87485, 220463, -86501, 220804).setZmin(-7520).setZmax(-7320));
    private static final Territory room4 = new Territory().add(new Rectangle(-85646, 219054, -84787, 219408).setZmin(-7520).setZmax(-7320));
    private static final Territory room5 = new Territory().add(new Rectangle(-87739, 216646, -87159, 217842).setZmin(-7520).setZmax(-7320));
    private static final List<Integer> ssqDoors = List.of(17240102, 17240104, 17240106, 17240108, 17240110);

    public SSQDoor(DoorInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtTwiceClick(final Player player) {
        final DoorInstance door = getActor();

        if (door.getReflection().isDefault())
            return;

        if (!ssqDoors.contains(door.getDoorId()))
            return;

        if (!player.isInRange(door, 150))
            return;
        Stream<NpcInstance> aliveNpcs = door.getReflection().getNpcs().filter(n -> !n.isDead());
        switch (door.getDoorId()) {
            case 17240102:
                if (aliveNpcs.anyMatch(n -> room1.isInside(n.getLoc())))
                    return;
            case 17240104:
                if (aliveNpcs.anyMatch(n -> room2.isInside(n.getLoc())))
                    return;
            case 17240106:
                if (aliveNpcs.anyMatch(n -> room3.isInside(n.getLoc())))
                    return;
            case 17240108:
                if (aliveNpcs.anyMatch(n -> (room4.isInside(n.getLoc()))))
                    return;
            case 17240110:
                if (aliveNpcs.anyMatch(n -> room5.isInside(n.getLoc())))
                    return;
        }
        door.getReflection().openDoor(door.getDoorId());
    }
}
