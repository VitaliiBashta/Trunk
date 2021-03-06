package l2trunk.scripts.handler.admincommands;

import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.dao.SiegeClanDAO;
import l2trunk.gameserver.data.xml.holder.EventHolder;
import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.handler.admincommands.AdminCommandHandler;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.EventType;
import l2trunk.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2trunk.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import l2trunk.gameserver.model.entity.events.impl.FortressSiegeEvent;
import l2trunk.gameserver.model.entity.events.impl.SiegeEvent;
import l2trunk.gameserver.model.entity.events.objects.SiegeClanObject;
import l2trunk.gameserver.model.entity.residence.Dominion;
import l2trunk.gameserver.model.entity.residence.Fortress;
import l2trunk.gameserver.model.entity.residence.Residence;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.tables.ClanTable;
import l2trunk.gameserver.utils.HtmlUtils;
import l2trunk.scripts.npc.model.residences.fortress.siege.BackupPowerUnitInstance;
import l2trunk.scripts.npc.model.residences.fortress.siege.PowerControlUnitInstance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class AdminResidence implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {
        if (!activeChar.getPlayerAccess().CanEditNPC)
            return false;

        boolean timed = false;
        final Residence r;
        final SiegeEvent<?, ?> event;
        Calendar calendar;
        NpcHtmlMessage msg;
        final DominionSiegeRunnerEvent runnerEvent;
        switch (comm) {
            case "admin_residence_list":
                msg = new NpcHtmlMessage(5);
                msg.setFile("admin/residence/residence_list.htm");

                StringBuilder replyMSG = new StringBuilder(200);
                for (Residence residence : ResidenceHolder.getResidences())
                    if (residence != null) {
                        replyMSG.append("<tr><td>");
                        replyMSG.append("<a action=\"bypass -h admin_residence ").append(residence.getId()).append("\">").append(HtmlUtils.htmlResidenceName(residence.getId())).append("</a>");
                        replyMSG.append("</td><td>");

                        Clan owner = residence.getOwner();
                        if (owner == null)
                            replyMSG.append("NPC");
                        else
                            replyMSG.append(owner.getName());

                        replyMSG.append("</td></tr>");
                    }
                msg.replace("%residence_list%", replyMSG);
                activeChar.sendPacket(msg);
                break;
            case "admin_residence":
                if (wordList.length != 2)
                    return false;
                r = ResidenceHolder.getResidence(toInt(wordList[1]));
                if (r == null)
                    return false;
                event = r.getSiegeEvent();
                msg = new NpcHtmlMessage(5);
                if (r instanceof Dominion) {
                    msg.setFile("admin/residence/dominion_siege_info.htm");
                    msg.replace("%residence%", HtmlUtils.htmlResidenceName(r.getId()));
                    msg.replace("%id%", r.getId());
                    msg.replace("%owner%", r.getOwner() == null ? "NPC" : r.getOwner().getName());

                    StringBuilder builder = new StringBuilder(100);
                    List<SiegeClanObject> clans = event.getObjects(SiegeEvent.ATTACKERS);
                    for (SiegeClanObject clan : clans)
                        builder.append("<tr>").append("<td>").append(clan.getClan().getName()).append("</td>").append("<td>").append(clan.getClan().getLeaderName()).append("</td>").append("<td>").append(SiegeEvent.ATTACKERS).append("</td>").append("</tr>");

                    clans = event.getObjects(SiegeEvent.DEFENDERS);
                    for (SiegeClanObject clan : clans)
                        builder.append("<tr>").append("<td>").append(clan.getClan().getName()).append("</td>").append("<td>").append(clan.getClan().getLeaderName()).append("</td>").append("<td>").append(SiegeEvent.DEFENDERS).append("</td>").append("</tr>");

                    msg.replace("%clans%", builder);

                    builder = new StringBuilder(100);
                    List<Integer> players = event.getObjects(DominionSiegeEvent.ATTACKER_PLAYERS);
                    for (int i : players) {
                        Player player = GameObjectsStorage.getPlayer(i);
                        builder.append("<tr>").append("<td>").append(i).append("</td>").append("<td>").append(player == null ? "null" : player.getName()).append("</td>").append("<td>").append(DominionSiegeEvent.ATTACKER_PLAYERS).append("</td>").append("</tr>");
                    }

                    players = event.getObjects(DominionSiegeEvent.DEFENDER_PLAYERS);
                    for (int i : players) {
                        Player player = GameObjectsStorage.getPlayer(i);
                        builder.append("<tr>").append("<td>").append(i).append("</td>").append("<td>").append(player == null ? "null" : player.getName()).append("</td>").append("<td>").append(DominionSiegeEvent.DEFENDER_PLAYERS).append("</td>").append("</tr>");
                    }
                    msg.replace("%players%", builder);
                } else {
                    msg.setFile("admin/residence/siege_info.htm");
                    msg.replace("%residence%", HtmlUtils.htmlResidenceName(r.getId()));
                    msg.replace("%id%", "" + r.getId());
                    msg.replace("%owner%", r.getOwner() == null ? "NPC" : r.getOwner().getName());
                    msg.replace("%cycle%", r.getCycle());
                    msg.replace("%paid_cycle%", r.getPaidCycle());
                    msg.replace("%reward_count%", r.getRewardCount());
                    msg.replace("%left_time%", r.getCycleDelay());

                    StringBuilder clans = new StringBuilder(100);
                    for (List<?> objects : event.getObjects().values()) {
                        objects.stream()
                                .filter(o -> o instanceof SiegeClanObject)
                                .map(o -> (SiegeClanObject) o)
                                .forEach(siegeClanObject ->
                                        clans.append("<tr>").append("<td>").append(siegeClanObject.getClan().getName()).append("</td>").append("<td>").append(siegeClanObject.getClan().getLeaderName()).append("</td>").append("<td>").append(siegeClanObject.getType()).append("</td>").append("</tr>"));
                    }
                    msg.replace("%clans%", clans);
                }

                msg.replace("%hour%", r.getSiegeDate().get(Calendar.HOUR_OF_DAY));
                msg.replace("%minute%", r.getSiegeDate().get(Calendar.MINUTE));
                msg.replace("%day%", r.getSiegeDate().get(Calendar.DAY_OF_MONTH));
                msg.replace("%month%", r.getSiegeDate().get(Calendar.MONTH) + 1);
                msg.replace("%year%", r.getSiegeDate().get(Calendar.YEAR));
                activeChar.sendPacket(msg);
                break;
            case "admin_set_owner":
                if (wordList.length != 3)
                    return false;
                r = ResidenceHolder.getResidence(toInt(wordList[1]));
                if (r == null)
                    return false;
                Clan clan = null;
                String clanName = wordList[2];
                if (!"npc".equalsIgnoreCase(clanName)) {
                    clan = ClanTable.INSTANCE.getClanByName(clanName);
                    if (clan == null) {
                        activeChar.sendPacket(SystemMsg.INCORRECT_NAME);
                        AdminCommandHandler.INSTANCE.useAdminCommandHandler(activeChar, "admin_residence " + r.getId());
                        return false;
                    }
                }

                event = r.getSiegeEvent();

                event.clearActions();

                if (r instanceof Dominion)
                    r.changeOwner(clan);
                else {
                    r.getLastSiegeDate().setTimeInMillis(clan == null ? 0 : System.currentTimeMillis());
                    r.getOwnDate().setTimeInMillis(clan == null ? 0 : System.currentTimeMillis());
                    r.changeOwner(clan);

                    event.reCalcNextTime(false);
                }
                break;
            case "admin_set_siege_time":
                r = ResidenceHolder.getResidence(toInt(wordList[1]));
                if (r == null)
                    return false;

                calendar = (Calendar) r.getSiegeDate().clone();
                for (int i = 2; i < wordList.length; i++) {
                    int type;
                    int val = toInt(wordList[i]);
                    switch (i) {
                        case 2:
                            type = Calendar.HOUR_OF_DAY;
                            break;
                        case 3:
                            type = Calendar.MINUTE;
                            break;
                        case 4:
                            type = Calendar.DAY_OF_MONTH;
                            break;
                        case 5:
                            type = Calendar.MONTH;
                            val -= 1;
                            break;
                        case 6:
                            type = Calendar.YEAR;
                            break;
                        default:
                            continue;
                    }
                    calendar.set(type, val);
                }
                event = r.getSiegeEvent();

                event.clearActions();
                r.getSiegeDate().setTimeInMillis(calendar.getTimeInMillis());
                event.registerActions();
                r.setJdbcState(JdbcEntityState.UPDATED);
                r.update();

                AdminCommandHandler.INSTANCE.useAdminCommandHandler(activeChar, "admin_residence " + r.getId());
                break;
            case "admin_quick_siege_start":
                r = ResidenceHolder.getResidence(toInt(wordList[1]));
                if (r == null)
                    return false;

                calendar = Calendar.getInstance();
                if (wordList.length >= 3)
                    calendar.set(Calendar.SECOND, -toInt(wordList[2]));
                event = r.getSiegeEvent();

                event.clearActions();
                r.getSiegeDate().setTimeInMillis(calendar.getTimeInMillis());
                event.registerActions();
                r.setJdbcState(JdbcEntityState.UPDATED);
                r.update();

                AdminCommandHandler.INSTANCE.useAdminCommandHandler(activeChar, "admin_residence " + r.getId());
                break;
            case "admin_quick_siege_stop":
                r = ResidenceHolder.getResidence(toInt(wordList[1]));
                if (r == null)
                    return false;

                event = r.getSiegeEvent();

                event.clearActions();
                ThreadPoolManager.INSTANCE.execute(event::stopEvent);

                AdminCommandHandler.INSTANCE.useAdminCommandHandler(activeChar, "admin_residence " + r.getId());
                break;
            case "admin_start_dominion_war":
                calendar = Calendar.getInstance();
                if (wordList.length >= 2)
                    calendar.set(Calendar.SECOND, -toInt(wordList[1]));

                runnerEvent = EventHolder.getEvent(EventType.MAIN_EVENT, 1);
                runnerEvent.clearActions();

                ResidenceHolder.getFortresses().forEach(f -> {
                    f.getSiegeEvent().clearActions();
                    if (f.getSiegeEvent().isInProgress())
                        f.getSiegeEvent().stopEvent();

                    f.getSiegeEvent().removeObjects(SiegeEvent.ATTACKERS);
                    SiegeClanDAO.INSTANCE.delete(f);
                });

                for (Dominion d : runnerEvent.getRegisteredDominions()) {
                    d.getSiegeEvent().clearActions();
                    d.getSiegeDate().setTimeInMillis(calendar.getTimeInMillis());
                }
                runnerEvent.getSiegeDate().setTimeInMillis(calendar.getTimeInMillis());
                runnerEvent.registerActions();
                break;
            case "admin_stop_dominion_war":
                runnerEvent = EventHolder.getEvent(EventType.MAIN_EVENT, 1);
                runnerEvent.clearActions();
                ThreadPoolManager.INSTANCE.execute(() -> {
                    ResidenceHolder.getFortresses()
                            .filter(f -> f.getSiegeEvent().isInProgress())
                            .forEach(f -> f.getSiegeEvent().stopEvent());
                    runnerEvent.getRegisteredDominions().forEach(d -> {
                        d.getSiegeEvent().clearActions();
                        d.getSiegeEvent().stopEvent();
                    });
                    runnerEvent.stopEvent();
                });
                break;
            case "admin_restart_dominion_one_time":
                timed = true;
            case "admin_restart_dominion_one":
                int castleId = toInt(wordList[1]);

                runnerEvent = EventHolder.getEvent(EventType.MAIN_EVENT, 1);
                Dominion ourDominion = runnerEvent.getRegisteredDominions().stream()
                        .filter(d -> d.getCastle().getId() == castleId)
                        .findFirst().orElse(null);

                if (ourDominion != null) {
                    //Stopping if in progress
                    ourDominion.getSiegeEvent().clearActions();
                    if (ourDominion.getSiegeEvent().isInProgress())
                        ourDominion.getSiegeEvent().stopEvent();

                    if (timed) {
                        calendar = Calendar.getInstance();
                        ourDominion.getSiegeDate().setTimeInMillis(calendar.getTimeInMillis());
                    }

                    //Starting again
                    ourDominion.getSiegeEvent().registerActions();
                }
                break;
            case "admin_backup_unit_info":
                GameObject target = activeChar.getTarget();
                if (!(target instanceof PowerControlUnitInstance) && !(target instanceof BackupPowerUnitInstance))
                    return false;

                List<String> t = new ArrayList<>(3);
                if (target instanceof PowerControlUnitInstance)
                    for (int i : ((PowerControlUnitInstance) target).getGenerated())
                        t.add(String.valueOf(i));
                else
                    for (int i : ((BackupPowerUnitInstance) target).getGenerated())
                        t.add(i == 0 ? "A" : i == 1 ? "B" : i == 2 ? "C" : "D");

                activeChar.sendMessage("Password: " + t.toString());
                return true;
            case "admin_fortress_spawn_flags":
                if (wordList.length != 2)
                    return false;
                Fortress fortress = ResidenceHolder.getFortress(toInt(wordList[1]));
                if (fortress == null)
                    return false;
                FortressSiegeEvent siegeEvent = fortress.getSiegeEvent();
                if (!siegeEvent.isInProgress())
                    return false;
                boolean[] f = siegeEvent.getBarrackStatus();
                for (int i = 0; i < f.length; i++)
                    siegeEvent.barrackAction(i, true);
                siegeEvent.spawnFlags();
                return true;
        }
        return true;
    }

    @Override
    public List<String> getAdminCommands() {
        return List.of(
                "admin_residence_list",
                "admin_residence",
                "admin_set_owner",
                "admin_set_siege_time",
                // dominion
                "admin_start_dominion_war",
                "admin_stop_dominion_war",
                "admin_restart_dominion_one_time",
                "admin_restart_dominion_one",
                "admin_set_dominion_time",
                //
                "admin_quick_siege_start",
                "admin_quick_siege_stop",
                // fortress
                "admin_backup_unit_info",
                "admin_fortress_spawn_flags");
    }
}
