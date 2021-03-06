package l2trunk.scripts.npc.model.residences.fortress.peace;

import l2trunk.gameserver.dao.SiegeClanDAO;
import l2trunk.gameserver.data.xml.holder.EventHolder;
import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.EventType;
import l2trunk.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import l2trunk.gameserver.model.entity.events.impl.FortressSiegeEvent;
import l2trunk.gameserver.model.entity.events.impl.SiegeEvent;
import l2trunk.gameserver.model.entity.events.objects.SiegeClanObject;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.model.entity.residence.Fortress;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.pledge.Privilege;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.templates.item.ItemTemplate;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.util.List;
import java.util.Map;

public final class SuspiciousMerchantInstance extends NpcInstance {
    public SuspiciousMerchantInstance(int objectID, NpcTemplate template) {
        super(objectID, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        Fortress fortress = getFortress();
        FortressSiegeEvent siegeEvent = fortress.getSiegeEvent();

        if (command.equalsIgnoreCase("register")) {
            Clan clan = player.getClan();
            if (clan == null) {
                showChatWindow(player, "residence2/fortress/fortress_ordery002.htm");
                return;
            }

            if (clan.getHasFortress() == fortress.getId()) {
                showChatWindow(player, "residence2/fortress/fortress_ordery014.htm", Map.of("%clan_name%", clan.getName()));
                return;
            }

            if (!player.hasPrivilege(Privilege.CS_FS_SIEGE_WAR)) {
                showChatWindow(player, "residence2/fortress/fortress_ordery012.htm");
                return;
            }

            if (clan.getCastle() > 0) {
                Castle relatedCastle = null;
                for (Castle castle : fortress.getRelatedCastles())
                    if (castle.getId() == clan.getCastle())
                        relatedCastle = castle;

                if (relatedCastle != null) {
                    if (fortress.getContractState() == Fortress.CONTRACT_WITH_CASTLE) {
                        showChatWindow(player, "residence2/fortress/fortress_ordery022.htm");
                        return;
                    }

                    if (relatedCastle.getSiegeEvent().isRegistrationOver()) {
                        showChatWindow(player, "residence2/fortress/fortress_ordery022.htm");
                        return;
                    }
                } else {
                    showChatWindow(player, "residence2/fortress/fortress_ordery021.htm");
                    return;
                }
            }

            final List<SiegeClanObject> attackerList = siegeEvent.getObjects(SiegeEvent.ATTACKERS);
            int attackersSize = attackerList.size();

            if (attackersSize == 0)
                if (!player.consumeItem(ItemTemplate.ITEM_ID_ADENA, 250000L)) {
                    showChatWindow(player, "residence2/fortress/fortress_ordery003.htm");
                    return;
                }

            SiegeClanObject siegeClan = siegeEvent.getSiegeClan(FortressSiegeEvent.ATTACKERS, clan);
            if (siegeClan != null) {
                showChatWindow(player, "residence2/fortress/fortress_ordery007.htm");
                return;
            }

            // 1 рега возможна всего
            if (ResidenceHolder.getFortresses()
                    .anyMatch(fort -> fort.getSiegeEvent().getSiegeClan(FortressSiegeEvent.ATTACKERS, clan) != null)) {
                showChatWindow(player, "residence2/fortress/fortress_ordery006.htm");
                return;
            }

            if (clan.getLevel() < 4) {
                showChatWindow(player, "residence2/fortress/fortress_ordery006.htm");
                return;
            }

            // если у нас есть форт, запрещаем регатся на форт, если на носу осада своего форта(во избежания абуза, участия в 2 осадах)
            if (clan.getHasFortress() > 0) {
                Fortress clanFortress = ResidenceHolder.getFortress(clan.getHasFortress());
                if (clanFortress.getSiegeDate().getTimeInMillis() > 0) {
                    showChatWindow(player, "residence2/fortress/fortress_ordery006.htm");
                    return;
                }
            }

            DominionSiegeRunnerEvent runnerEvent = EventHolder.getEvent(EventType.MAIN_EVENT, 1);
            if (runnerEvent.isRegistrationOver() || siegeEvent.isRegistrationOver()) {
                showChatWindow(player, "residence2/fortress/fortress_ordery006.htm");
                return;
            }

            String clanList = "List of clans attacking this fortress: \n";
            for (SiegeClanObject clanTemp : attackerList) {
                if (clanTemp == null || clanTemp.getClan() == null)
                    continue;

                clanList += clanTemp.getClan().getName() + "\n";
            }

            siegeClan = new SiegeClanObject(FortressSiegeEvent.ATTACKERS, clan, 0);
            siegeEvent.addObject(FortressSiegeEvent.ATTACKERS, siegeClan);
            SiegeClanDAO.INSTANCE.insert(fortress, siegeClan);

            siegeEvent.reCalcNextTime(false);

            player.sendPacket(new SystemMessage2(SystemMsg.YOUR_CLAN_HAS_BEEN_REGISTERED_TO_S1S_FORTRESS_BATTLE).addResidenceName(fortress));
            showChatWindow(player, "residence2/fortress/fortress_ordery005.htm", Map.of("%clanList%", clanList));
        } else if ("cancel".equalsIgnoreCase(command)) {
            Clan clan = player.getClan();
            if (clan == null || !player.hasPrivilege(Privilege.CS_FS_SIEGE_WAR)) {
                showChatWindow(player, "residence2/fortress/fortress_ordery010.htm");
                return;
            }

            SiegeClanObject siegeClan = siegeEvent.getSiegeClan(FortressSiegeEvent.ATTACKERS, clan);
            if (siegeClan != null) {
                siegeEvent.removeObject(FortressSiegeEvent.ATTACKERS, siegeClan);
                SiegeClanDAO.INSTANCE.delete(fortress, siegeClan);

                siegeEvent.reCalcNextTime(false);

                showChatWindow(player, "residence2/fortress/fortress_ordery009.htm");
            } else
                showChatWindow(player, "residence2/fortress/fortress_ordery011.htm");
        } else if (command.equalsIgnoreCase("state")) {
            int attackersSize = siegeEvent.getObjects(SiegeEvent.ATTACKERS).size();
            if (attackersSize == 0)
                showChatWindow(player, "residence2/fortress/fortress_ordery019.htm");
            else
                showChatWindow(player, "residence2/fortress/fortress_ordery020.htm");
        } else
            super.onBypassFeedback(player, command);
    }

    @Override
    public void showChatWindow(Player player, int val) {
        NpcHtmlMessage html = new NpcHtmlMessage(player, this);
        Fortress fortress = getFortress();
        if (fortress.getOwner() != null) {
            html.setFile("residence2/fortress/fortress_ordery001a.htm");
            html.replace("%clan_name%", fortress.getOwner().getName());
        } else
            html.setFile("residence2/fortress/fortress_ordery001.htm");

        player.sendPacket(html);
    }
}