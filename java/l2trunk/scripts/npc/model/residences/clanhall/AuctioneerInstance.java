package l2trunk.scripts.npc.model.residences.clanhall;

import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.commons.lang.StringUtils;
import l2trunk.gameserver.dao.SiegeClanDAO;
import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.impl.ClanHallAuctionEvent;
import l2trunk.gameserver.model.entity.events.objects.AuctionSiegeClanObject;
import l2trunk.gameserver.model.entity.events.objects.SiegeClanObject;
import l2trunk.gameserver.model.entity.residence.ClanHall;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.pledge.Privilege;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.templates.item.ItemTemplate;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.HtmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class AuctioneerInstance extends NpcInstance {
    private static final Logger _log = LoggerFactory.getLogger(AuctioneerInstance.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yy");
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getIntegerInstance(Locale.KOREA);
    private final static long WEEK = 7 * 24 * 60 * 60 * 1000L;

    private final static int CH_PAGE_SIZE = 7;
    private final static String CH_IN_LIST =
            "\t<tr>\n" +
                    "\t\t<td width=50>\n" +
                    "\t\t\t<font color=\"aaaaff\">&^%id%;</font>\n" +
                    "\t\t</td>\n" + "\t\t<td width=100>\n" +
                    "\t\t\t<a action=\"bypass -h npc_%objectId%_info %id%\"><font color=\"ffffaa\">&%%id%;[%size%]</font></a>\n" +
                    "\t\t</td>\n" +
                    "\t\t<td width=50>%date%</td>\n" +
                    "\t\t<td width=70 align=right>\n" +
                    "\t\t\t<font color=\"aaffff\">%min_bid%</font>\n" +
                    "\t\t</td>\n" +
                    "\t</tr>";

    private final static int BIDDER_PAGE_SIZE = 10;
    private final static String BIDDER_IN_LIST =
            "\t<tr>\n" +
                    "\t\t<td width=100><font color=\"aaaaff\">&%%id%;</font></td>\n" +
                    "\t\t<td width=100><font color=\"ffffaa\">%clan_name%</font></td>\n" +
                    "\t\t<td width=70>%date%</td>\n" +
                    "\t</tr>";

    public AuctioneerInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        StringTokenizer tokenizer = new StringTokenizer(command.replace("\r\n", "<br1>"));
        String actualCommand = tokenizer.nextToken();
        if (actualCommand.equalsIgnoreCase("map"))
            showChatWindow(player, getMapDialog());
            //=============================================================================================
            //						Shows the list of Active Auctions
            //=============================================================================================
        else if (actualCommand.equalsIgnoreCase("list_all")) {
            int page = toInt(tokenizer.nextToken());

            List<ClanHallAuctionEvent> events = new ArrayList<>();
            for (ClanHall ch : ResidenceHolder.getResidenceList(ClanHall.class))
                if (ch.getSiegeEvent().getClass() == ClanHallAuctionEvent.class && ch.getSiegeEvent().isInProgress())
                    events.add(ch.getSiegeEvent());

            if (events.isEmpty()) {
                player.sendPacket(SystemMsg.THERE_ARE_NO_CLAN_HALLS_UP_FOR_AUCTION);
                showChatWindow(player, 0);
                return;
            }

            int min = CH_PAGE_SIZE * page;
            int max = min + CH_PAGE_SIZE;
            if (min > events.size()) {
                min = 0;
                max = min + CH_PAGE_SIZE;
            }

            if (max > events.size())
                max = events.size();

            NpcHtmlMessage msg = new NpcHtmlMessage(player, this);
            msg.setFile("residence2/clanhall/auction_list_clanhalls.htm");

            StringBuilder b = new StringBuilder();
            for (int i = min; i < max; i++) {
                ClanHallAuctionEvent event = events.get(i);
                List<AuctionSiegeClanObject> attackers = event.getObjects(ClanHallAuctionEvent.ATTACKERS);
                Calendar endDate = event.getEndSiegeDate();

                String out = CH_IN_LIST.replace("%id%", String.valueOf(event.getId())).replace("%min_bid%", String.valueOf(event.getResidence().getAuctionMinBid())).replace("%size%", String.valueOf(attackers.size())).replace("%date%", DATE_FORMAT.format(endDate.getTimeInMillis()));

                b.append(out);
            }

            msg.replace("%list%", b);
            if (events.size() > max) {
                msg.replace("%next_button%", "<td>" + HtmlUtils.NEXT_BUTTON + "</td>");
                msg.replace("%next_bypass%", "-h npc_%objectId%_list_all " + (page + 1));
            } else
                msg.replace("%next_button%", "");

            if (page != 0) {
                msg.replace("%prev_button%", "<td>" + HtmlUtils.PREV_BUTTON + "</td>");
                msg.replace("%prev_bypass%", "-h npc_%objectId%_list_all " + (page - 1));
            } else
                msg.replace("%prev_button%", "");

            player.sendPacket(msg);
        }
        //=============================================================================================
        //		Displays the standard of Old Clan Hall (selection) if one of the Bidder єto - there is a button to cancel
        //=============================================================================================
        else if (actualCommand.equalsIgnoreCase("info")) {
            String fileName;

            ClanHall clanHall;
            SiegeClanObject siegeClan = null;
            if (tokenizer.hasMoreTokens()) {
                int id = toInt(tokenizer.nextToken());
                clanHall = ResidenceHolder.getResidence(id);

                fileName = "residence2/clanhall/auction_clanhall_info_main.htm";
            } else {
                clanHall = player.getClan() == null ? null : player.getClan().getHasHideout() > 0 ? ResidenceHolder.getResidence(player.getClan().getHasHideout()) : null;
                if (clanHall != null && clanHall.getSiegeEvent().getClass() == ClanHallAuctionEvent.class) {
                    if (clanHall.getSiegeEvent().isInProgress())
                        fileName = "residence2/clanhall/auction_clanhall_info_owner_sell.htm";
                    else
                        fileName = "residence2/clanhall/auction_clanhall_info_owner.htm";
                } else {
                    for (ClanHall ch : ResidenceHolder.getResidenceList(ClanHall.class))
                        if (ch.getSiegeEvent().getClass() == ClanHallAuctionEvent.class && (siegeClan = ch.getSiegeEvent().getSiegeClan(ClanHallAuctionEvent.ATTACKERS, player.getClan())) != null) {
                            clanHall = ch;
                            break;
                        }

                    if (siegeClan == null) {
                        player.sendPacket(SystemMsg.THERE_ARE_NO_OFFERINGS_I_OWN_OR_I_MADE_A_BID_FOR);
                        showChatWindow(player, 0);
                        return;
                    }

                    fileName = "residence2/clanhall/auction_clanhall_info_bidded.htm";
                }
            }

            ClanHallAuctionEvent auctionEvent = clanHall.getSiegeEvent();
            List<AuctionSiegeClanObject> attackers = auctionEvent.getObjects(ClanHallAuctionEvent.ATTACKERS);

            NpcHtmlMessage msg = new NpcHtmlMessage(player, this);
            msg.setFile(fileName);
            msg.replace("%id%", clanHall.getId());
            msg.replace("%bigger_size%", attackers.size());
            msg.replace("%grade%", clanHall.getGrade());
            msg.replace("%rental_fee%", clanHall.getRentalFee());

            Clan owner = clanHall.getOwner();

            msg.replace("%owner%", owner == null ? "" : owner.getName());
            msg.replace("%owner_leader%", owner == null ? "" : owner.getLeaderName());
            msg.replace("%description%", clanHall.getAuctionDescription());
            msg.replace("%min_bid%", clanHall.getAuctionMinBid());

            Calendar c = auctionEvent.getEndSiegeDate();

            msg.replace("%date%", DATE_FORMAT.format(c.getTimeInMillis()));
            msg.replace("%hour%", c.get(Calendar.HOUR_OF_DAY));

            int remainingTime = (int) ((c.getTimeInMillis() - System.currentTimeMillis()) / 60000L);

            msg.replace("%remaining_hour%", remainingTime/ 60);
            msg.replace("%remaining_minutes%", remainingTime % 60);

            if (siegeClan != null)
                msg.replace("%my_bid%", siegeClan.getParam());

            player.sendPacket(msg);
        }
        //=============================================================================================
        //						Auctioneer displays a list of auction
        //=============================================================================================
        else if (actualCommand.equalsIgnoreCase("bidder_list")) {
            int id = toInt(tokenizer.nextToken());
            int page = toInt(tokenizer.nextToken());

            ClanHall clanHall = ResidenceHolder.getResidence(id);
            ClanHallAuctionEvent auctionEvent = clanHall.getSiegeEvent();
            List<AuctionSiegeClanObject> attackers = auctionEvent.getObjects(ClanHallAuctionEvent.ATTACKERS);

            if (!auctionEvent.isInProgress())
                return;

            int min = BIDDER_PAGE_SIZE * page;
            int max = min + BIDDER_PAGE_SIZE;
            if (min > attackers.size()) {
                min = 0;
                max = min + BIDDER_PAGE_SIZE;
            }

            if (max > attackers.size())
                max = attackers.size();

            NpcHtmlMessage msg = new NpcHtmlMessage(player, this);
            msg.setFile("residence2/clanhall/auction_bidder_list.htm");
            msg.replace("%id%", id);

            StringBuilder b = new StringBuilder();
            for (int i = min; i < max; i++) {
                AuctionSiegeClanObject siegeClan = attackers.get(i);
                String t = BIDDER_IN_LIST.replace("%id%", String.valueOf(id)).replace("%clan_name%", siegeClan.getClan().getName()).replace("%date%", DATE_FORMAT.format(siegeClan.getDate()));
                b.append(t);
            }
            msg.replace("%list%", b);

            if (attackers.size() > max) {
                msg.replace("%next_button%", "<td>" + HtmlUtils.NEXT_BUTTON + "</td>");
                msg.replace("%next_bypass%", "-h npc_%objectId%_bidder_list " + id + " " + (page + 1));
            } else
                msg.replace("%next_button%", "");

            if (page != 0) {
                msg.replace("%prev_button%", "<td>" + HtmlUtils.PREV_BUTTON + "</td>");
                msg.replace("%prev_bypass%", "-h npc_%objectId%_bidder_list " + id + " " + (page - 1));
            } else
                msg.replace("%prev_button%", "");

            player.sendPacket(msg);
        }
        //=============================================================================================
        //				Starting Position of the bid, a window for entering, gallop put
        //=============================================================================================
        else if (actualCommand.equalsIgnoreCase("bid_start")) {
            if (!firstChecks(player)) {
                showChatWindow(player, 0);
                return;
            }

            int id = toInt(tokenizer.nextToken());

            ClanHall clanHall = ResidenceHolder.getResidence(id);
            ClanHallAuctionEvent auctionEvent = clanHall.getSiegeEvent();

            if (!auctionEvent.isInProgress())
                return;

            long minBid = clanHall.getAuctionMinBid();
            AuctionSiegeClanObject siegeClan = auctionEvent.getSiegeClan(ClanHallAuctionEvent.ATTACKERS, player.getClan());
            if (siegeClan != null)
                minBid = siegeClan.getParam();

            NpcHtmlMessage msg = new NpcHtmlMessage(player, this);
            msg.setFile("residence2/clanhall/auction_bid_start.htm");
            msg.replace("%id%", id);
            msg.replace("%min_bid%", minBid);
            msg.replace("%clan_adena%",player.getClan().getWarehouse().getCountOf(ItemTemplate.ITEM_ID_ADENA));

            player.sendPacket(msg);
        }
        //=============================================================================================
        //							A confirmation window of the bid
        //=============================================================================================
        else if (actualCommand.equalsIgnoreCase("bid_next")) {
            if (!firstChecks(player)) {
                showChatWindow(player, 0);
                return;
            }

            int id = toInt(tokenizer.nextToken());
            long bid = 0;
            if (tokenizer.hasMoreTokens()) {
                try {
                    bid = NUMBER_FORMAT.parse(tokenizer.nextToken()).longValue();
                } catch (ParseException e) {
                    //
                }
            }

            ClanHall clanHall = ResidenceHolder.getResidence(id);
            ClanHallAuctionEvent auctionEvent = clanHall.getSiegeEvent();

            if (!auctionEvent.isInProgress())
                return;

            if (!checkBid(player, auctionEvent, bid))
                return;

            long minBid = clanHall.getAuctionMinBid();
            AuctionSiegeClanObject siegeClan = auctionEvent.getSiegeClan(ClanHallAuctionEvent.ATTACKERS, player.getClan());
            if (siegeClan != null)
                minBid = siegeClan.getParam();

            NpcHtmlMessage msg = new NpcHtmlMessage(player, this);
            msg.setFile("residence2/clanhall/auction_bid_confirm.htm");
            msg.replace("%id%", id);
            msg.replace("%bid%", bid);
            msg.replace("%min_bid%", minBid);

            Calendar c = auctionEvent.getEndSiegeDate();

            msg.replace("%date%", DATE_FORMAT.format(c.getTimeInMillis()));
            msg.replace("%hour%", c.get(Calendar.HOUR_OF_DAY));

            player.sendPacket(msg);
        }
        //=============================================================================================
        //						Confirms bin, and a menu appears Clan Hall
        //=============================================================================================
        else if (actualCommand.equalsIgnoreCase("bid_confirm")) {
            if (!firstChecks(player)) {
                showChatWindow(player, 0);
                return;
            }

            int id = toInt(tokenizer.nextToken());
            final long bid = Long.parseLong(tokenizer.nextToken());


            ClanHall clanHall = ResidenceHolder.getResidence(id);
            ClanHallAuctionEvent auctionEvent = clanHall.getSiegeEvent();

            if (!auctionEvent.isInProgress())
                return;

            for (ClanHall ch : ResidenceHolder.getResidenceList(ClanHall.class))
                if (clanHall != ch && ch.getSiegeEvent().getClass() == ClanHallAuctionEvent.class && ch.getSiegeEvent().isInProgress() && ch.getSiegeEvent().getSiegeClan(ClanHallAuctionEvent.ATTACKERS, player.getClan()) != null) {
                    player.sendPacket(SystemMsg.SINCE_YOU_HAVE_ALREADY_SUBMITTED_A_BID_YOU_ARE_NOT_ALLOWED_TO_PARTICIPATE_IN_ANOTHER_AUCTION_AT_THIS_TIME);
                    onBypassFeedback(player, "bid_start " + id);
                    return;
                }

            if (!checkBid(player, auctionEvent, bid))
                return;

            long consumeBid = bid;
            AuctionSiegeClanObject siegeClan = auctionEvent.getSiegeClan(ClanHallAuctionEvent.ATTACKERS, player.getClan());
            if (siegeClan != null) {
                consumeBid -= siegeClan.getParam();
                if (bid <= siegeClan.getParam()) {
                    player.sendPacket(SystemMsg.THE_BID_AMOUNT_MUST_BE_HIGHER_THAN_THE_PREVIOUS_BID);
                    onBypassFeedback(player, "bid_start " + auctionEvent.getId());
                    return;
                }
            }

            player.getClan().getWarehouse().destroyItemByItemId(ItemTemplate.ITEM_ID_ADENA, consumeBid, "Auctioneer Bid");

            if (siegeClan != null) {
                siegeClan.setParam(bid);

                SiegeClanDAO.INSTANCE.update(clanHall, siegeClan);
            } else {
                siegeClan = new AuctionSiegeClanObject(ClanHallAuctionEvent.ATTACKERS, player.getClan(), bid);
                auctionEvent.addObject(ClanHallAuctionEvent.ATTACKERS, siegeClan);

                SiegeClanDAO.INSTANCE.insert(clanHall, siegeClan);
            }

			/*clanHall.getSiegeDate().setTimeInMillis(System.currentTimeMillis());
			clanHall.setJdbcState(JdbcEntityState.UPDATED);
			clanHall.update();

			clanHall.getSiegeEvent().reCalcNextTime(false);*/

            player.sendPacket(SystemMsg.YOUR_BID_HAS_BEEN_SUCCESSFULLY_PLACED);

            onBypassFeedback(player, "info");
        }
        //=============================================================================================
        //						Opens to accept rejection of bids
        //=============================================================================================
        else if (actualCommand.equalsIgnoreCase("cancel_bid")) {
            if (!firstChecks(player)) {
                showChatWindow(player, 0);
                return;
            }
            int id = toInt(tokenizer.nextToken());

            ClanHall clanHall = ResidenceHolder.getResidence(id);
            ClanHallAuctionEvent auctionEvent = clanHall.getSiegeEvent();

            if (!auctionEvent.isInProgress())
                return;

            AuctionSiegeClanObject siegeClan = auctionEvent.getSiegeClan(ClanHallAuctionEvent.ATTACKERS, player.getClan());
            if (siegeClan == null)
                return;

            long returnVal = siegeClan.getParam() - (long) (siegeClan.getParam() * 0.1);
            NpcHtmlMessage msg = new NpcHtmlMessage(player, this);
            msg.setFile("residence2/clanhall/auction_bid_cancel.htm");
            msg.replace("%id%", id);
            msg.replace("%bid%",siegeClan.getParam());
            msg.replace("%return%", returnVal);

            player.sendPacket(msg);
        }
        //=============================================================================================
        //					Constitutes a waiver of the rate returns to 90% scrip
        //=============================================================================================
        else if (actualCommand.equalsIgnoreCase("cancel_bid_confirm")) {
            if (!firstChecks(player)) {
                showChatWindow(player, 0);
                return;
            }
            int id = toInt(tokenizer.nextToken());

            ClanHall clanHall = ResidenceHolder.getResidence(id);
            ClanHallAuctionEvent auctionEvent = clanHall.getSiegeEvent();

            if (!auctionEvent.isInProgress())
                return;

            AuctionSiegeClanObject siegeClan = auctionEvent.getSiegeClan(ClanHallAuctionEvent.ATTACKERS, player.getClan());
            if (siegeClan == null)
                return;

            long returnVal = siegeClan.getParam() - (long) (siegeClan.getParam() * 0.1);

            player.getClan().getWarehouse().addItem(ItemTemplate.ITEM_ID_ADENA, returnVal, "Auctioneer Cancel Bid");
            auctionEvent.removeObject(ClanHallAuctionEvent.ATTACKERS, siegeClan);
            SiegeClanDAO.INSTANCE.delete(clanHall, siegeClan);

            player.sendPacket(SystemMsg.YOU_HAVE_CANCELED_YOUR_BID);
            showChatWindow(player, 0);
        }
        //=============================================================================================
        //				Shows a window to enter, disk imaging of CH and confirms auction
        //=============================================================================================
        else if (actualCommand.equalsIgnoreCase("register_start")) {
            if (!firstChecks(player)) {
                showChatWindow(player, 0);
                return;
            }

            ClanHall clanHall = ResidenceHolder.getResidence(player.getClan().getHasHideout());
            if (clanHall.getSiegeEvent().getClass() != ClanHallAuctionEvent.class || clanHall.getSiegeEvent().isInProgress())
                return;

            if ((clanHall.getLastSiegeDate().getTimeInMillis() + WEEK) > System.currentTimeMillis()) {
                player.sendPacket(SystemMsg.IT_HAS_NOT_YET_BEEN_SEVEN_DAYS_SINCE_CANCELING_AN_AUCTION);
                onBypassFeedback(player, "info");
                return;
            }

            NpcHtmlMessage msg = new NpcHtmlMessage(player, this);
            msg.setFile("residence2/clanhall/auction_clanhall_register_start.htm");
            msg.replace("%id%",player.getClan().getHasHideout());
            msg.replace("%adena%", player.getClan().getWarehouse().getCountOf(ItemTemplate.ITEM_ID_ADENA));
            msg.replace("%deposit%", clanHall.getDeposit());

            player.sendPacket(msg);
        }
        //=============================================================================================
        //				Shows a window to enter, disk imaging of CH and confirms auction
        //=============================================================================================
        else if (actualCommand.equalsIgnoreCase("register_next")) {
            if (!firstChecks(player)) {
                showChatWindow(player, 0);
                return;
            }

            ClanHall clanHall = ResidenceHolder.getResidence(player.getClan().getHasHideout());
            if (clanHall.getSiegeEvent().getClass() != ClanHallAuctionEvent.class || clanHall.getSiegeEvent().isInProgress()) {
                showChatWindow(player, 0);
                return;
            }

            if (player.getClan().getWarehouse().getCountOf(ItemTemplate.ITEM_ID_ADENA) < clanHall.getDeposit()) {
                player.sendPacket(SystemMsg.THERE_IS_NOT_ENOUGH_ADENA_IN_THE_CLAN_HALL_WAREHOUSE);
                onBypassFeedback(player, "register_start");
                return;
            }

            NpcHtmlMessage msg = new NpcHtmlMessage(player, this);
            msg.setFile("residence2/clanhall/auction_clanhall_register_next.htm");
            msg.replace("%min_bid%", clanHall.getBaseMinBid());
            msg.replace("%last_bid%", clanHall.getBaseMinBid());

            player.sendPacket(msg);
        }
        //=============================================================================================
        //					Shows a window to enter, disk imaging of CH and confirms auction
        //=============================================================================================
        else if (actualCommand.equalsIgnoreCase("register_next2")) {
            if (!firstChecks(player)) {
                showChatWindow(player, 0);
                return;
            }

            ClanHall clanHall = ResidenceHolder.getResidence(player.getClan().getHasHideout());
            if (clanHall.getSiegeEvent().getClass() != ClanHallAuctionEvent.class || clanHall.getSiegeEvent().isInProgress()) {
                showChatWindow(player, 0);
                return;
            }

            int day = toInt(tokenizer.nextToken());
            int bid = -1;
            String comment = StringUtils.EMPTY;
            if (tokenizer.hasMoreTokens())
                bid = toInt(tokenizer.nextToken());


            if (tokenizer.hasMoreTokens()) {
                comment = tokenizer.nextToken();
                while (tokenizer.hasMoreTokens())
                    comment += " " + tokenizer.nextToken();
            }

            comment = comment.substring(0, Math.min(comment.length(), Byte.MAX_VALUE));
            if (bid <= -1) {
                onBypassFeedback(player, "register_next");
                return;
            }

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR_OF_DAY, day);

            NpcHtmlMessage msg = new NpcHtmlMessage(player, this);
            msg.setFile("residence2/clanhall/auction_clanhall_register_confirm.htm");
            msg.replace("%description%", comment);
            msg.replace("%day%", day);
            msg.replace("%bid%", bid);
            msg.replace("%base_bid%", clanHall.getBaseMinBid());
            msg.replace("%hour%", cal.get(Calendar.HOUR_OF_DAY));
            msg.replace("%date%", DATE_FORMAT.format(cal.getTimeInMillis()));

            player.sendPacket(msg);
        }
        //=============================================================================================
        //							Confirms sale the Clan Hall
        //=============================================================================================
        else if ("register_confirm".equalsIgnoreCase(actualCommand)) {
            if (!firstChecks(player)) {
                showChatWindow(player, 0);
                return;
            }

            ClanHall clanHall = ResidenceHolder.getResidence(player.getClan().getHasHideout());
            if (clanHall.getSiegeEvent().getClass() != ClanHallAuctionEvent.class || clanHall.getSiegeEvent().isInProgress()) {
                showChatWindow(player, 0);
                return;
            }

            if ((clanHall.getLastSiegeDate().getTimeInMillis() + WEEK) > System.currentTimeMillis()) {
                player.sendPacket(SystemMsg.IT_HAS_NOT_YET_BEEN_SEVEN_DAYS_SINCE_CANCELING_AN_AUCTION);
                onBypassFeedback(player, "info");
                return;
            }

            int day = toInt(tokenizer.nextToken());
            int bid = toInt(tokenizer.nextToken());
            StringBuilder comment = new StringBuilder(StringUtils.EMPTY);

            if (tokenizer.hasMoreTokens()) {
                comment = new StringBuilder(tokenizer.nextToken());
                while (tokenizer.hasMoreTokens())
                    comment.append(" ").append(tokenizer.nextToken());
            }

            if (bid <= -1) {
                onBypassFeedback(player, "register_next");
                return;
            }

            clanHall.setAuctionMinBid(bid);
            clanHall.setAuctionDescription(comment.toString());
            clanHall.setAuctionLength(day);
            clanHall.getSiegeDate().setTimeInMillis(System.currentTimeMillis());
            clanHall.setJdbcState(JdbcEntityState.UPDATED);
            clanHall.update();

            _log.info("2 sssssssssssssssssssssssssssss");
            clanHall.getSiegeEvent().reCalcNextTime(false);

            onBypassFeedback(player, "info");
            player.sendPacket(SystemMsg.YOU_HAVE_REGISTERED_FOR_A_CLAN_HALL_AUCTION);
        } else if (actualCommand.equals("cancel_start")) {
            if (!firstChecks(player)) {
                showChatWindow(player, 0);
                return;
            }

            ClanHall clanHall = ResidenceHolder.getResidence(player.getClan().getHasHideout());
            if (clanHall.getSiegeEvent().getClass() != ClanHallAuctionEvent.class || !clanHall.getSiegeEvent().isInProgress()) {
                showChatWindow(player, 0);
                return;
            }

            NpcHtmlMessage msg = new NpcHtmlMessage(player, this);
            msg.setFile("residence2/clanhall/auction_clanhall_cancel_confirm.htm");
            msg.replace("%deposit%", clanHall.getDeposit());

            player.sendPacket(msg);
        } else if (actualCommand.equals("cancel_confirm")) {
            if (!firstChecks(player)) {
                showChatWindow(player, 0);
                return;
            }

            ClanHall clanHall = ResidenceHolder.getResidence(player.getClan().getHasHideout());
            if (clanHall.getSiegeEvent().getClass() != ClanHallAuctionEvent.class || !clanHall.getSiegeEvent().isInProgress()) {
                showChatWindow(player, 0);
                return;
            }

            clanHall.getSiegeEvent().setInProgress(false);

            clanHall.getSiegeDate().setTimeInMillis(0);
            clanHall.getLastSiegeDate().setTimeInMillis(System.currentTimeMillis());
            clanHall.setAuctionDescription(StringUtils.EMPTY);
            clanHall.setAuctionLength(0);
            clanHall.setAuctionMinBid(0);
            clanHall.setJdbcState(JdbcEntityState.UPDATED);
            clanHall.update();

            ClanHallAuctionEvent auctionEvent = clanHall.getSiegeEvent();
            List<AuctionSiegeClanObject> siegeClans = auctionEvent.removeObjects(ClanHallAuctionEvent.ATTACKERS);
            SiegeClanDAO.INSTANCE.delete(clanHall);

            for (AuctionSiegeClanObject $siegeClan : siegeClans) {
                long returnBid = $siegeClan.getParam() - (long) ($siegeClan.getParam() * 0.1);

                $siegeClan.getClan().getWarehouse().addItem(ItemTemplate.ITEM_ID_ADENA, returnBid, "Auctioneer Cancel Bid");
            }

            clanHall.getSiegeEvent().reCalcNextTime(false);
            onBypassFeedback(player, "info");
        } else
            super.onBypassFeedback(player, command);
    }

    @Override
    public void showChatWindow(Player player, int val) {
        showChatWindow(player, "residence2/clanhall/auction_dealer001.htm");
    }

    private boolean firstChecks(Player player) {
        if (player.getClan() == null || player.getClan().getLevel() < 2) {
            player.sendPacket(SystemMsg.ONLY_A_CLAN_LEADER_WHOSE_CLAN_IS_OF_LEVEL_2_OR_HIGHER_IS_ALLOWED_TO_PARTICIPATE_IN_A_CLAN_HALL_AUCTION);
            return false;
        }

        if (!player.hasPrivilege(Privilege.CH_AUCTION)) {
            player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return false;
        }

        return true;
    }

    private boolean checkBid(Player player, ClanHallAuctionEvent auctionEvent, final long bid) {
        long consumeBid = bid;
        AuctionSiegeClanObject siegeClan = auctionEvent.getSiegeClan(ClanHallAuctionEvent.ATTACKERS, player.getClan());
        if (siegeClan != null)
            consumeBid -= siegeClan.getParam();

        if (consumeBid > player.getClan().getWarehouse().getCountOf(ItemTemplate.ITEM_ID_ADENA)) {
            player.sendPacket(SystemMsg.THERE_IS_NOT_ENOUGH_ADENA_IN_THE_CLAN_HALL_WAREHOUSE);
            onBypassFeedback(player, "bid_start " + auctionEvent.getId());
            return false;
        }

        long minBid = siegeClan == null ? auctionEvent.getResidence().getAuctionMinBid() : siegeClan.getParam();
        if (bid < minBid) {
            player.sendPacket(SystemMsg.YOUR_BID_PRICE_MUST_BE_HIGHER_THAN_THE_MINIMUM_PRICE_CURRENTLY_BEING_BID);
            onBypassFeedback(player, "bid_start " + auctionEvent.getId());
            return false;
        }
        return true;
    }

    private String getMapDialog() {
        //"gludio", "gludin", "dion", "giran", "adena", "rune", "goddard", "schuttgart"
        return String.format("residence2/clanhall/map_agit_%s.htm", getParameters().getString("town", "gludin"));
    }
}
