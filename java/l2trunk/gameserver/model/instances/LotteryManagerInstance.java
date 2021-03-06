package l2trunk.gameserver.model.instances;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.instancemanager.games.LotteryManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.HtmlUtils;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.gameserver.utils.Log;

import java.text.DateFormat;

public final class LotteryManagerInstance extends NpcInstance {
    public LotteryManagerInstance(int objectID, NpcTemplate template) {
        super(objectID, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (command.startsWith("Loto")) {
            try {
                int val = Integer.parseInt(command.substring(5));
                showLotoWindow(player, val);
            } catch (NumberFormatException e) {
                Log.debug("L2LotteryManagerInstance: bypass: " + command + "; getPlayer: " + player, e);
            }
        } else
            super.onBypassFeedback(player, command);
    }

    @Override
    public String getHtmlPath(int npcId, int val, Player player) {
        String pom;
        if (val == 0)
            pom = "LotteryManager";
        else
            pom = "LotteryManager-" + val;

        return "lottery/" + pom + ".htm";
    }

    private void showLotoWindow(Player player, int val) {
        int npcId = getTemplate().npcId;
        String filename;
        SystemMessage sm;
        NpcHtmlMessage html = new NpcHtmlMessage(player, this);

        // if loto
        if (val == 0) {
            filename = getHtmlPath(npcId, 1, player);
            html.setFile(filename);
        } else if (val >= 1 && val <= 21) {
            if (!LotteryManager.INSTANCE.isStarted()) {
                /** LOTTERY_TICKETS_ARE_NOT_CURRENTLY_BEING_SOLD **/
                player.sendPacket(Msg.LOTTERY_TICKETS_ARE_NOT_CURRENTLY_BEING_SOLD);
                return;
            }
            if (!LotteryManager.INSTANCE.isSellableTickets()) {
                /** TICKETS_FOR_THE_CURRENT_LOTTERY_ARE_NO_LONGER_AVAILABLE **/
                player.sendPacket(Msg.TICKETS_FOR_THE_CURRENT_LOTTERY_ARE_NO_LONGER_AVAILABLE);
                return;
            }

            filename = getHtmlPath(npcId, 5, player);
            html.setFile(filename);

            int count = 0;
            int found = 0;

            // counting buttons and unsetting button if found
            for (int i = 0; i < 5; i++)
                if (player.getLoto(i) == val) {
                    // unsetting button
                    player.setLoto(i, 0);
                    found = 1;
                } else if (player.getLoto(i) > 0)
                    count++;

            // if not rearched limit 5 and not unseted value
            if (count < 5 && found == 0 && val <= 20)
                for (int i = 0; i < 5; i++)
                    if (player.getLoto(i) == 0) {
                        player.setLoto(i, val);
                        break;
                    }

            //setting pusshed buttons
            count = 0;
            for (int i = 0; i < 5; i++)
                if (player.getLoto(i) > 0) {
                    count++;
                    String button = String.valueOf(player.getLoto(i));
                    if (player.getLoto(i) < 10)
                        button = "0" + button;
                    String search = "fore=\"L2UI.lottoNum" + button + "\" back=\"L2UI.lottoNum" + button + "a_check\"";
                    String replace = "fore=\"L2UI.lottoNum" + button + "a_check\" back=\"L2UI.lottoNum" + button + "\"";
                    html.replace(search, replace);
                }
            if (count == 5) {
                String search = "0\">Return";
                String replace = "22\">The winner selected the numbers above.";
                html.replace(search, replace);
            }
            player.sendPacket(html);
        }

        if (val == 22) {
            if (!LotteryManager.INSTANCE.isStarted()) {
                /** LOTTERY_TICKETS_ARE_NOT_CURRENTLY_BEING_SOLD **/
                player.sendPacket(Msg.LOTTERY_TICKETS_ARE_NOT_CURRENTLY_BEING_SOLD);
                return;
            }
            if (!LotteryManager.INSTANCE.isSellableTickets()) {
                /** TICKETS_FOR_THE_CURRENT_LOTTERY_ARE_NO_LONGER_AVAILABLE **/
                player.sendPacket(Msg.TICKETS_FOR_THE_CURRENT_LOTTERY_ARE_NO_LONGER_AVAILABLE);
                return;
            }

            int price = Config.SERVICES_ALT_LOTTERY_PRICE;
            int lotonumber = LotteryManager.INSTANCE.getId();
            int enchant = 0;
            int type2 = 0;
            for (int i = 0; i < 5; i++) {
                if (player.getLoto(i) == 0)
                    return;
                if (player.getLoto(i) < 17)
                    enchant += Math.pow(2, player.getLoto(i) - 1);
                else
                    type2 += Math.pow(2, player.getLoto(i) - 17);
            }
            if (player.getAdena() < price) {
                player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                return;
            }
            player.reduceAdena(price, true, "Lottery Ticket");
            sm = new SystemMessage(SystemMessage.ACQUIRED__S1_S2);
            sm.addNumber(lotonumber);
            sm.addItemName(4442);
            player.sendPacket(sm);
            ItemInstance item = ItemFunctions.createItem(4442);
            item.setCustomType1(lotonumber);
            item.setEnchantLevel(enchant);
            item.setCustomType2(type2);
            player.getInventory().addItem(item, "Lottery Ticket");

            filename = getHtmlPath(npcId, 3, player);
            html.setFile(filename);
        } else if (val == 23) //23 - current lottery jackpot
        {
            filename = getHtmlPath(npcId, 3, player);
            html.setFile(filename);
        } else if (val == 24) {
            filename = getHtmlPath(npcId, 4, player);
            html.setFile(filename);

            int lotonumber = LotteryManager.INSTANCE.getId();
            String message = "";

            for (ItemInstance item : player.getInventory().getItems()) {
                if (item == null)
                    continue;
                if (item.getItemId() == 4442 && item.getCustomType1() < lotonumber) {
                    message += "<a action=\"bypass -h npc_%objectId%_Loto " + item.objectId() + "\">" + item.getCustomType1();
                    message += " " + HtmlUtils.htmlNpcString(NpcString.EVENT_NUMBER) + " ";
                    int[] numbers = LotteryManager.INSTANCE.decodeNumbers(item.getEnchantLevel(), item.getCustomType2());
                    for (int i = 0; i < 5; i++)
                        message += numbers[i] + " ";
                    int[] check = LotteryManager.INSTANCE.checkTicket(item);
                    if (check[0] > 0) {
                        message += "- ";
                        switch (check[0]) {
                            case 1:
                                message += HtmlUtils.htmlNpcString(NpcString.FIRST_PRIZE);
                                break;
                            case 2:
                                message += HtmlUtils.htmlNpcString(NpcString.SECOND_PRIZE);
                                break;
                            case 3:
                                message += HtmlUtils.htmlNpcString(NpcString.THIRD_PRIZE);
                                break;
                            case 4:
                                message += HtmlUtils.htmlNpcString(NpcString.FOURTH_PRIZE);
                                break;
                        }
                        message += " " + check[1] + "a.";
                    }
                    message += "</a>";
                }
            }
            if (message.length() == 0)
                message += HtmlUtils.htmlNpcString(NpcString.THERE_HAS_BEEN_NO_WINNING_LOTTERY_TICKET);

            html.replace("%result%", message);
        } else if (val == 25) {
            filename = getHtmlPath(npcId, 2, player);
            html.setFile(filename);
        } else if (val > 25) {
            int lotonumber = LotteryManager.INSTANCE.getId();
            ItemInstance item = player.getInventory().getItemByObjectId(val);
            if (item == null || item.getItemId() != 4442 || item.getCustomType1() >= lotonumber)
                return;
            int[] check = LotteryManager.INSTANCE.checkTicket(item);

            if (player.getInventory().destroyItem(item, 1L, "LotteryManager")) {
                player.sendPacket(SystemMessage2.removeItems(4442, 1));
                int adena = check[1];
                if (adena > 0) {
                    player.addAdena((long) adena, "LotteryManager");
                }
            }

            return;
        }

        html.replace("%objectId%", objectId());
        html.replace("%race%",  LotteryManager.INSTANCE.getId());
        html.replace("%adena%",  LotteryManager.INSTANCE.getPrize());
        html.replace("%ticket_price%",  Config.SERVICES_LOTTERY_TICKET_PRICE);
        html.replace("%prize5%",  Config.SERVICES_LOTTERY_5_NUMBER_RATE * 100);
        html.replace("%prize4%",  Config.SERVICES_LOTTERY_4_NUMBER_RATE * 100);
        html.replace("%prize3%",  Config.SERVICES_LOTTERY_3_NUMBER_RATE * 100);
        html.replace("%prize2%",  Config.SERVICES_LOTTERY_2_AND_1_NUMBER_PRIZE);
        html.replace("%enddate%", DateFormat.getDateInstance().format(LotteryManager.INSTANCE.getEndDate()));

        player.sendPacket(html);
        player.sendActionFailed();
    }
}