package l2trunk.scripts.npc.model.residences.clanhall;

import l2trunk.commons.lang.StringUtils;
import l2trunk.gameserver.dao.SiegeClanDAO;
import l2trunk.gameserver.dao.SiegePlayerDAO;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.impl.ClanHallTeamBattleEvent;
import l2trunk.gameserver.model.entity.events.objects.CTBSiegeClanObject;
import l2trunk.gameserver.model.entity.events.objects.SiegeClanObject;
import l2trunk.gameserver.model.entity.residence.ClanHall;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.templates.item.ItemTemplate;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.TimeUtils;
import l2trunk.scripts.quests._504_CompetitionForTheBanditStronghold;
import net.sf.ehcache.search.parser.MAggregate;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class BanditMessagerInstance extends NpcInstance {
    public BanditMessagerInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(final Player player, final String command) {
        ClanHall clanHall = getClanHall();
        ClanHallTeamBattleEvent siegeEvent = clanHall.getSiegeEvent();
        Clan clan = player.getClan();

        if (command.equalsIgnoreCase("registrationMenu")) {
            if (!checkCond(player, true))
                return;

            showChatWindow(player, "residence2/clanhall/agit_oel_mahum_messeger_1.htm");
        } else if (command.equalsIgnoreCase("registerAsClan")) {
            if (!checkCond(player, false))
                return;

            List<CTBSiegeClanObject> siegeClans = siegeEvent.getObjects(ClanHallTeamBattleEvent.ATTACKERS);

            CTBSiegeClanObject siegeClan = siegeEvent.getSiegeClan(ClanHallTeamBattleEvent.ATTACKERS, clan);
            if (siegeClan != null) {
                showFlagInfo(player, siegeClans.indexOf(siegeClan));
                return;
            }

            QuestState qs = player.getQuestState(_504_CompetitionForTheBanditStronghold.class);
            if (qs == null || qs.getQuestItemsCount(5009) != 1) {
                showChatWindow(player, "residence2/clanhall/agit_oel_mahum_messeger_24.htm");
                return;
            }

            qs.exitCurrentQuest();
            register(player);
        } else if (command.equalsIgnoreCase("registerByOffer")) {
            if (!checkCond(player, false))
                return;

            List<CTBSiegeClanObject> siegeClans = siegeEvent.getObjects(ClanHallTeamBattleEvent.ATTACKERS);

            CTBSiegeClanObject siegeClan = siegeEvent.getSiegeClan(ClanHallTeamBattleEvent.ATTACKERS, clan);
            if (siegeClan != null) {
                showFlagInfo(player, siegeClans.indexOf(siegeClan));
                return;
            }

            if (!player.consumeItem(ItemTemplate.ITEM_ID_ADENA, 200000)) {
                showChatWindow(player, "residence2/clanhall/agit_oel_mahum_messeger_26.htm");
                return;
            }
            register(player);
        } else if (command.equalsIgnoreCase("viewNpc")) {
            CTBSiegeClanObject siegeClan = siegeEvent.getSiegeClan(ClanHallTeamBattleEvent.ATTACKERS, player.getClan());
            if (siegeClan == null) {
                showChatWindow(player, "residence2/clanhall/agit_oel_mahum_messeger_7.htm");
                return;
            }

            String file;
            switch ((int) siegeClan.getParam()) {
                case 0:
                    file = "residence2/clanhall/agit_oel_mahum_messeger_10.htm";
                    break;
                case 35428: // berserker
                    file = "residence2/clanhall/agit_oel_mahum_messeger_11.htm";
                    break;
                case 35429: // scout
                    file = "residence2/clanhall/agit_oel_mahum_messeger_12.htm";
                    break;
                case 35430:
                    file = "residence2/clanhall/agit_oel_mahum_messeger_13.htm";
                    break;
                case 35431:
                    file = "residence2/clanhall/agit_oel_mahum_messeger_14.htm";
                    break;
                case 35432:
                    file = "residence2/clanhall/agit_oel_mahum_messeger_15.htm";
                    break;
                default:
                    return;
            }
            showChatWindow(player, file);
        } else if (command.startsWith("formAlliance")) {
            CTBSiegeClanObject siegeClan = siegeEvent.getSiegeClan(ClanHallTeamBattleEvent.ATTACKERS, player.getClan());
            if (siegeClan == null) {
                showChatWindow(player, "residence2/clanhall/agit_oel_mahum_messeger_7.htm");
                return;
            }
            if (siegeClan.getClan().getLeaderId() != player.objectId()) {
                showChatWindow(player, "residence2/clanhall/agit_oel_mahum_messeger_10.htm");
                return;
            }
            StringTokenizer t = new StringTokenizer(command);
            t.nextToken();
            int npcId = Integer.parseInt(t.nextToken());
            siegeClan.setParam(npcId);

            SiegeClanDAO.INSTANCE.update(clanHall, siegeClan);
            showChatWindow(player, "residence2/clanhall/agit_oel_mahum_messeger_9.htm");
        } else if (command.equalsIgnoreCase("registerAsMember")) {
            CTBSiegeClanObject siegeClan = siegeEvent.getSiegeClan(ClanHallTeamBattleEvent.ATTACKERS, player.getClan());
            if (siegeClan == null) {
                showChatWindow(player, "residence2/clanhall/agit_oel_mahum_messeger_7.htm");
                return;
            }
            if (siegeClan.getClan().getLeaderId() == player.objectId()) {
                showChatWindow(player, "residence2/clanhall/agit_oel_mahum_messeger_5.htm");
                return;
            }

            if (siegeClan.getPlayers().contains(player.objectId()))
                showChatWindow(player, "residence2/clanhall/agit_oel_mahum_messeger_9.htm");
            else {
                if (siegeClan.getPlayers().size() >= 18) {
                    showChatWindow(player, "residence2/clanhall/agit_oel_mahum_messeger_8.htm");
                    return;
                }

                siegeClan.getPlayers().add(player.objectId());
                SiegePlayerDAO.INSTANCE.insert(clanHall, clan.clanId(), player.objectId());
                showChatWindow(player, "residence2/clanhall/agit_oel_mahum_messeger_9.htm");
            }
        } else if (command.equalsIgnoreCase("listClans")) {
            NpcHtmlMessage msg = new NpcHtmlMessage(player, this);
            msg.setFile("residence2/clanhall/azit_messenger003.htm");

            List<CTBSiegeClanObject> siegeClans = siegeEvent.getObjects(ClanHallTeamBattleEvent.ATTACKERS);
            for (int i = 0; i < 5; i++) {
                CTBSiegeClanObject siegeClan = siegeClans.get(i);
                if (siegeClan != null)
                    msg.replace("%clan_" + i + "%", siegeClan.getClan().getName());
                else
                    msg.replaceNpcString("%clan_" + i + "%", NpcString.__UNREGISTERED__);
                msg.replace("%clan_count_" + i + "%", siegeClan == null ? 0 : siegeClan.getPlayers().size());
            }
            player.sendPacket(msg);
        } else
            super.onBypassFeedback(player, command);
    }

    private boolean checkCond(Player player, boolean regMenu) {
        Clan clan = player.getClan();
        ClanHall clanHall = getClanHall();
        ClanHallTeamBattleEvent siegeEvent = clanHall.getSiegeEvent();

        List<CTBSiegeClanObject> siegeClans = siegeEvent.getObjects(ClanHallTeamBattleEvent.ATTACKERS);

        SiegeClanObject siegeClan = siegeEvent.getSiegeClan(ClanHallTeamBattleEvent.ATTACKERS, clan);
        if (siegeEvent.isRegistrationOver()) {
            showChatWindow(player, "quests/_504_CompetitionForTheBanditStronghold/azit_messenger_q0504_03.htm", Map.of("%siege_time%", TimeUtils.toSimpleFormat(clanHall.getSiegeDate())));
            return false;
        }

        if (regMenu && siegeClan != null)
            return true;

        if (clan == null || player.objectId() != clan.getLeaderId()) {
            showChatWindow(player, "quests/_504_CompetitionForTheBanditStronghold/azit_messenger_q0504_05.htm");
            return false;
        }

        if (player.objectId() == clan.getLeaderId() && clan.getLevel() < 4) {
            showChatWindow(player, "quests/_504_CompetitionForTheBanditStronghold/azit_messenger_q0504_04.htm");
            return false;
        }

        if (clan.getHasHideout() == clanHall.getId()) {
            showChatWindow(player, "residence2/clanhall/agit_oel_mahum_messeger_22.htm");
            return false;
        }

        if (clan.getHasHideout() > 0) {
            showChatWindow(player, "quests/_504_CompetitionForTheBanditStronghold/azit_messenger_q0504_10.htm");
            return false;
        }

        if (siegeClans.size() >= 5) {
            showChatWindow(player, "residence2/clanhall/agit_oel_mahum_messeger_21.htm");
            return false;
        }

        return true;
    }

    private void register(Player player) {
        Clan clan = player.getClan();
        ClanHall clanHall = getClanHall();
        ClanHallTeamBattleEvent siegeEvent = clanHall.getSiegeEvent();

        CTBSiegeClanObject siegeClan = new CTBSiegeClanObject(ClanHallTeamBattleEvent.ATTACKERS, clan, 0);
        siegeClan.getPlayers().add(player.objectId());

        siegeEvent.addObject(ClanHallTeamBattleEvent.ATTACKERS, siegeClan);

        SiegeClanDAO.INSTANCE.insert(clanHall, siegeClan);
        SiegePlayerDAO.INSTANCE.insert(clanHall, clan.clanId(), player.objectId());

        List<CTBSiegeClanObject> siegeClans = siegeEvent.getObjects(ClanHallTeamBattleEvent.ATTACKERS);

        showFlagInfo(player, siegeClans.indexOf(siegeClan));
    }

    private void showFlagInfo(Player player, int index) {
        String file = null;
        switch (index) {
            case 0:
                file = "residence2/clanhall/agit_oel_mahum_messeger_4a.htm";
                break;
            case 1:
                file = "residence2/clanhall/agit_oel_mahum_messeger_4b.htm";
                break;
            case 2:
                file = "residence2/clanhall/agit_oel_mahum_messeger_4c.htm";
                break;
            case 3:
                file = "residence2/clanhall/agit_oel_mahum_messeger_4d.htm";
                break;
            case 4:
                file = "residence2/clanhall/agit_oel_mahum_messeger_4e.htm";
                break;
            default:
                return;
        }
        showChatWindow(player, file);
    }

    @Override
    public void showChatWindow(Player player, int val) {
        Clan clan = getClanHall().getOwner();
        if (clan != null)
            showChatWindow(player, "residence2/clanhall/azit_messenger001.htm", Map.of("%owner_name%", clan.getName()));
        else
            showChatWindow(player, "residence2/clanhall/azit_messenger002.htm");
    }
}
