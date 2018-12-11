package l2trunk.scripts.npc.model.events;

import l2trunk.commons.collections.CollectionUtils;
import l2trunk.commons.lang.StringUtils;
import l2trunk.gameserver.data.xml.holder.EventHolder;
import l2trunk.gameserver.model.Party;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.EventType;
import l2trunk.gameserver.model.entity.events.impl.UndergroundColiseumEvent;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.util.List;

/**
 * events/kerthang_manager004.htm  - не лидер пати, но в пати
 * events/kerthang_manager008.htm  - нету пати
 * events/kerthang_manager011.htm  - C1 непохдодит уровнем
 */
public final class ColiseumManagerInstance extends ColiseumHelperInstance {
    private final String _startHtm;
    private final int _coliseumId;

    public ColiseumManagerInstance(int objectId, NpcTemplate template) {
        super(objectId, template);

        _startHtm = getParameter("start_htm", StringUtils.EMPTY);
        _coliseumId = getParameter("coliseum_id", 0);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        UndergroundColiseumEvent coliseumEvent = EventHolder.getEvent(EventType.MAIN_EVENT, _coliseumId);

        switch (command) {
            case "register":
                Party party = player.getParty();
                if (party == null)
                    showChatWindow(player, "events/kerthang_manager008.htm");
                else if (party.getLeader() != player)
                    showChatWindow(player, "events/kerthang_manager004.htm");
                else {
                    for (Player $player : party) {
                        if ($player.getLevel() < coliseumEvent.getMinLevel() || $player.getLevel() > coliseumEvent.getMaxLevel()) {
                            showChatWindow(player, "events/kerthang_manager011.htm", "%name%", $player.getName());
                            return;
                        }
                    }
                }
                break;
            case "viewTeams":

                List<Player> reg = coliseumEvent.getRegisteredPlayers();

                NpcHtmlMessage msg = new NpcHtmlMessage(player, this);
                msg.setFile("events/kerthang_manager003.htm");
                for (int i = 0; i < 5; i++) {
                    Player $player = CollectionUtils.safeGet(reg, i);

                    msg.replace("%team" + i + "%", $player == null ? StringUtils.EMPTY : $player.getName());
                }

                player.sendPacket(msg);
                break;
            default:
                super.onBypassFeedback(player, command);
                break;
        }
    }

    @Override
    public void showChatWindow(Player player, int val, Object... ar) {
        showChatWindow(player, _startHtm);
    }
}
