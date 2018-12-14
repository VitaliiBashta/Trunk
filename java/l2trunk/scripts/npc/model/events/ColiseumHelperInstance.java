package l2trunk.scripts.npc.model.events;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;
import l2trunk.scripts.services.TeleToFantasyIsle;

import java.util.Arrays;
import java.util.List;

public class ColiseumHelperInstance extends NpcInstance {
    private final List<List<Location>> LOCS = Arrays.asList(
            Arrays.asList(new Location(-84451, -45452, -10728), new Location(-84580, -45587, -10728)),
            Arrays.asList(new Location(-86154, -50429, -10728), new Location(-86118, -50624, -10728)),
            Arrays.asList(new Location(-82009, -53652, -10728), new Location(-81802, -53665, -10728)),
            Arrays.asList(new Location(-77603, -50673, -10728), new Location(-77586, -50503, -10728)),
            Arrays.asList(new Location(-79186, -45644, -10728), new Location(-79309, -45561, -10728))
    );

    public ColiseumHelperInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (command.equals("teleOut"))
            player.teleToLocation(Rnd.get(TeleToFantasyIsle.POINTS));
        else if (command.startsWith("coliseum")) {
            int a = Integer.parseInt(String.valueOf(command.charAt(9)));
            List<Location> locs = LOCS.get(a);

            player.teleToLocation(Rnd.get(locs));
        } else
            super.onBypassFeedback(player, command);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        showChatWindow(player, "events/guide_gcol001.htm");
    }
}
