package Elemental.managers;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.CharTemplateHolder;
import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.Skill.SkillTargetType;
import l2trunk.gameserver.model.Skill.SkillType;
import l2trunk.gameserver.model.Zone.ZoneType;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.entity.olympiad.Olympiad;
import l2trunk.gameserver.model.entity.residence.ClanHall;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.utils.TradeHelper;
import l2trunk.gameserver.utils.Util;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static l2trunk.commons.lang.NumberUtils.toInt;

public enum OfflineBufferManager {
    INSTANCE;

    private static final int MAX_INTERACT_DISTANCE = 100;

    private final Map<Integer, BufferData> buffStores = new ConcurrentHashMap<>();

    OfflineBufferManager() {
    }


    public Map<Integer, BufferData> getBuffStores() {
        return buffStores;
    }

    public void processBypass(Player player, String command) {
        final StringTokenizer st = new StringTokenizer(command, " ");
        st.nextToken();

        switch (st.nextToken()) {
            // Sets a new buff store
            case "setstore": {
                try {
                    final int price = toInt(st.nextToken());
                    StringBuilder title = new StringBuilder(st.nextToken());
                    while (st.hasMoreTokens()) {
                        title.append(" ").append(st.nextToken());
                    }
                    title = new StringBuilder(title.toString().trim());

                    // Check if the player already has an active store, just in case
                    if (buffStores.containsKey(player.getObjectId())) {
                        //player.sendMessage("This buffer already exists. Cheater?");
                        break;
                    }

                    // Check for store
                    if (player.getPrivateStoreType() != Player.STORE_PRIVATE_NONE) {
                        player.sendMessage("You already have a store.");
                        break;
                    }

                    // Check if the player can set a store
                    if (!Config.BUFF_STORE_ALLOWED_CLASS_LIST.contains(player.getClassId().getId())) {
                        player.sendMessage("Your profession is not allowed to set an Buff Store.");
                        break;
                    }

                    // Check all the conditions to see if the player can open a private store
                    if (!TradeHelper.checksIfCanOpenStore(player, Player.STORE_PRIVATE_BUFF)) {
                        break;
                    }

                    // Check the title
                    if ((title.length() == 0) || title.length() >= 29) {
                        player.sendMessage("You must put a title for this store and it must have less than 29 characters.");
                        throw new Exception();
                    }

                    // Check price limits
                    if (price < 1 || price > 10000000) {
                        player.sendMessage("The price for each buff must be between 1 and 10kk.");
                        throw new Exception();
                    }

                    // Buff Stores can only be put inside areas designated to it and in clan halls
                    final ClanHall ch = ResidenceHolder.getResidenceByObject(ClanHall.class, player);
                    if (!player.isGM() && !player.isInZone(ZoneType.buff_store_only) && !player.isInZone(ZoneType.RESIDENCE) && ch == null) {
                        player.sendMessage("You can't put a buff store here. Look for special designated zones or clan halls.");
                        break;
                    }

                    // Check for conditions
                    if (player.isAlikeDead() || player.isInOlympiadMode() || player.isMounted() || player.isCastingNow()
                            || player.getOlympiadObserveGame() != null || player.getOlympiadGame() != null || Olympiad.isRegisteredInComp(player)) {
                        player.sendMessage("You don't meet the required conditions to put a buff store right now.");
                        break;
                    }

                    final BufferData buffer = new BufferData(player, title.toString(), price);

                    // Add all the buffs
                    for (Skill skill : player.getAllSkills()) {
                        // Only active skills
                        if (!skill.isActive())
                            continue;

                        // Only buffs
                        if (skill.skillType != SkillType.BUFF)
                            continue;

                        // Not triggered and hero skills
                        if (skill.isHeroic)
                            continue;

                        // Not only self skills
                        if (skill.targetType == SkillTargetType.TARGET_SELF)
                            continue;

                        // Not pet skills
                        if (skill.targetType == SkillTargetType.TARGET_PET)
                            continue;

                        // Avoid overlord skills when being a warcryer
                        if (player.getClassId().equalsOrChildOf(ClassId.doomcryer) && skill.targetType == SkillTargetType.TARGET_CLAN)
                            continue;

                        // Avoid warcryer skills when being a overlord
                        if (player.getClassId().equalsOrChildOf(ClassId.dominator)
                                && (skill.targetType == SkillTargetType.TARGET_PARTY || skill.targetType == SkillTargetType.TARGET_ONE))
                            continue;

                        // Forbidden skill list
                        if (Config.BUFF_STORE_FORBIDDEN_SKILL_LIST.contains(skill.id))
                            continue;

                        buffer.buffs.put(skill.id, skill);
                    }

                    // Case of empty buff list
                    if (buffer.buffs.isEmpty()) {
                        player.sendMessage("You don't have any available buff to put on sale in the store.");
                        break;
                    }

                    // Add the buffer data to the array
                    buffStores.put(player.getObjectId(), buffer);

                    // Sit the player, put it on store and and change the colors and titles
                    player.sitDown(null);

                    player.setVisibleTitleColor(Config.BUFF_STORE_TITLE_COLOR);
                    player.setVisibleTitle(title.toString());
                    player.setVisibleNameColor(Config.BUFF_STORE_NAME_COLOR);
                    player.broadcastUserInfo(true);

                    player.setPrivateStoreType(Player.STORE_PRIVATE_BUFF);

                    player.sendMessage("Your Buff Store was set succesfully.");
                } catch (NumberFormatException e) {
                    player.sendMessage("The price for each buff must be between 1 adena and 10kk.");

                    final NpcHtmlMessage html = new NpcHtmlMessage(0);
                    html.setFile("command/buffstore/buff_store_create.htm");
                    player.sendPacket(html);
                } catch (Exception e) {
                    final NpcHtmlMessage html = new NpcHtmlMessage(0);
                    html.setFile("command/buffstore/buff_store_create.htm");
                    player.sendPacket(html);
                }
                break;
            }
            // Stops the current store
            case "stopstore": {
                if (player.getPrivateStoreType() != Player.STORE_PRIVATE_BUFF) {
                    player.sendMessage("You dont have any store set right now.");
                    break;
                }

                // Remove the buffer from the array
                buffStores.remove(player.getObjectId());

                // Stand the player and put the original colors and title back
                player.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
                player.standUp();

                player.setVisibleTitleColor(0);
                player.setVisibleTitle(null);
                player.setVisibleNameColor(0);
                player.broadcastUserInfo(true);

                player.sendMessage("Your Buff Store was removed succesfuly.");

                break;
            }
            // Shows the buff list of the selected buffer
            case "bufflist": {
                try {
                    final int playerId = toInt(st.nextToken());
                    final boolean isPlayer = (!st.hasMoreTokens() || st.nextToken().equalsIgnoreCase("player"));
                    final int page = (st.hasMoreTokens() ? toInt(st.nextToken()) : 0);

                    // Check if the buffer exists
                    final BufferData buffer = buffStores.get(playerId);
                    if (buffer == null) {
                        //player.sendMessage("This buffer doesn't exists. Cheater?");
                        break;
                    }

                    // Check if the player is in the right distance from the buffer
                    if (Util.calculateDistance(player, buffer.owner, true) > MAX_INTERACT_DISTANCE) {
                        //player.sendMessage("Too far. Cheater?");
                        break;
                    }

                    // Check if the player has a summon before buffing
                    if (!isPlayer && player.getPet() == null) {
                        player.sendMessage("You don't have any active summon right now.");

                        // Send window again
                        showStoreWindow(player, buffer, true, page);
                        break;
                    }

                    showStoreWindow(player, buffer, isPlayer, page);
                } catch (Exception ignored) {

                }
                break;
            }
            // Purchases a particular buff of the store
            case "purchasebuff": {
                try {
                    final int playerId = toInt(st.nextToken());
                    final boolean isPlayer = (!st.hasMoreTokens() || st.nextToken().equalsIgnoreCase("player"));
                    final int buffId = toInt(st.nextToken());
                    final int page = (st.hasMoreTokens() ? toInt(st.nextToken()) : 0);

                    // Check if the buffer exists
                    final BufferData buffer = buffStores.get(playerId);
                    if (buffer == null) {
                        //player.sendMessage("This buffer doesn't exists. Cheater?");
                        break;
                    }

                    // Check if the buffer has this buff
                    if (!buffer.buffs.containsKey(buffId)) {
                        //player.sendMessage("This buff doesn't exists. Cheater?");
                        break;
                    }

                    // Check if the player is in the right distance from the buffer
                    if (Util.calculateDistance(player, buffer.owner, true) > MAX_INTERACT_DISTANCE) {
                        //player.sendMessage("Too far. Cheater?");
                        break;
                    }

                    // Check if the player has a summon before buffing
                    if (!isPlayer && player.getPet() == null) {
                        player.sendMessage("You don't have any active summon right now.");

                        // Send window again
                        showStoreWindow(player, buffer, true, page);
                        break;
                    }

                    // Check buffing conditions
                    if (player.getPvpFlag() > 0 || player.isInCombat() || player.getKarma() > 0 || player.isAlikeDead()
                            || player.isJailed() || player.isInOlympiadMode() || player.isCursedWeaponEquipped()
                            || player.isInStoreMode() || player.isInTrade() || player.getEnchantScroll() != null || player.isFishing()) {
                        player.sendMessage("You don't meet the required conditions to use the buffer right now.");
                        break;
                    }

                    final double buffMpCost = (Config.BUFF_STORE_MP_ENABLED ? buffer.buffs.get(buffId).getMpConsume() * Config.BUFF_STORE_MP_CONSUME_MULTIPLIER : 0);

                    // Check if the buffer has enough mp to sell this buff
                    if (buffMpCost > 0 && buffer.owner.getCurrentMp() < buffMpCost) {
                        player.sendMessage("This store doesn't have enough mp to give sell you this buff.");

                        // Send window again
                        showStoreWindow(player, buffer, isPlayer, page);
                        break;
                    }

                    // Clan Members of the buffer dont have to pay anything
                    final int buffPrice = player.getClanId() == buffer.owner.getClanId() && player.getClanId() != 0 ? 0 : buffer.buffPrice;

                    // Check if the player has enough adena to purchase this buff
                    if (buffPrice > 0 && player.getAdena() < buffPrice) {
                        player.sendMessage("You don't have enough adena to purchase a buff.");
                        break;
                    }

                    // Charge the adena needed for this buff
                    if (buffPrice > 0 && !player.reduceAdena(buffPrice, true, "BuffStore")) {
                        player.sendMessage("You don't have enough adena to purchase a buff.");
                        break;
                    }

                    // Give the adena to the buffer
                    if (buffPrice > 0)
                        buffer.owner.addAdena(buffPrice, true, "BuffStore");

                    // Reduce the buffer's mp if it consumes something
                    if (buffMpCost > 0)
                        buffer.owner.reduceCurrentMp(buffMpCost, null);

                    // Give the target the buff
                    if (isPlayer)
                        buffer.buffs.get(buffId).getEffects(player);
                    else
                        buffer.buffs.get(buffId).getEffects(player.getPet());

                    // Send message
                    player.sendMessage("You have bought " + buffer.buffs.get(buffId).name + " from " + buffer.owner.getName());

                    // Send the buff list again after buffing, exactly where it was before
                    showStoreWindow(player, buffer, isPlayer, page);
                } catch (Exception ignored) {

                }
                break;
            }
        }
    }

    /**
     * Sends the to the player the buffer store window with all the buffs and info
     */
    private void showStoreWindow(Player player, BufferData buffer, boolean isForPlayer, int page) {
        final NpcHtmlMessage html = new NpcHtmlMessage(0);
        html.setFile("command/buffstore/buff_store_buffer.htm");

        final int MAX_ENTRANCES_PER_ROW = 6; // buffs per page
        final double entrancesSize = buffer.buffs.size();
        final int maxPage = (int) Math.ceil(entrancesSize / MAX_ENTRANCES_PER_ROW) - 1;
        final int currentPage = Math.min(maxPage, page);

        // Creamos la lista de buffs
        final StringBuilder buffList = new StringBuilder();
        final Iterator<Skill> it = buffer.buffs.values().iterator();
        Skill buff;
        int i = 0;
        int baseMaxLvl;
        int enchantLvl;
        int enchantType;
        boolean changeColor = false;

        while (it.hasNext()) {
            if (i < currentPage * MAX_ENTRANCES_PER_ROW) {
                it.next();
                i++;
                continue;
            }

            // Si llegamos al final de la pagina salimos
            if (i >= (currentPage * MAX_ENTRANCES_PER_ROW + MAX_ENTRANCES_PER_ROW))
                break;

            buff = it.next();
            baseMaxLvl = SkillTable.INSTANCE.getBaseLevel(buff.id);

            buffList.append("<tr>");
            buffList.append("<td fixwidth=300>");
            buffList.append("<table height=35 border=0 cellspacing=2 cellpadding=0 bgcolor=").append(changeColor ? "171612" : "23221e").append(">");
            buffList.append("<tr>");
            buffList.append("<td width=5></td>");
            buffList.append("<td width=30 align=center background=").append(buff.icon).append("><button value=\"\" action=\"bypass -h BuffStore purchasebuff ").append(buffer.owner.getObjectId()).append(" ").append(isForPlayer ? "player" : "summon").append(" ").append(buff.id).append(" ").append(currentPage).append("\" width=32 height=32 back=L2UI_CT1.ItemWindow_DF_Frame_Down fore=L2UI_CT1.ItemWindow_DF_Frame></td>");
            buffList.append("<td width=12></td>");
            if (buff.level > baseMaxLvl) {
                // Buffs encantados
                enchantType = (buff.level - baseMaxLvl) / buff.getEnchantLevelCount();
                enchantLvl = (buff.level - baseMaxLvl) % buff.getEnchantLevelCount();
                enchantLvl = (enchantLvl == 0 ? buff.getEnchantLevelCount() : enchantLvl);

                buffList.append("<td fixwidth=240>" + "<font name=__SYSTEMWORLDFONT color=C73232>")
                        .append(buff.name).append("<font>")
                        .append(" - <font color=329231>Level</font> <font color=FFFFFF>")
                        .append(baseMaxLvl)
                        .append("</font>")
                        .append(" <br1> › <font color=F1C101 name=__SYSTEMWORLDFONT>Enchant: </font><font color=ffd969 name=CreditTextNormal>+").append(enchantLvl).append(" ").append(enchantType >= 3 ? "Power" : (enchantType >= 2 ? "Cost" : "Time")).append("</font></td>");
            } else {
                buffList.append("<td fixwidth=240>" + "<font name=__SYSTEMWORLDFONT color=C73232>").append(buff.name).append("<font>").append(" - <font color=329231>Level</font> <font color=FFFFFF>").append(buff.level).append("</font>");
                buffList.append(" <br1> › <font color=F1C101 name=__SYSTEMWORLDFONT>Enchant: </font><font color=FFFFFF name=CreditTextNormal> None</font></td>");

            }
            buffList.append("</tr>");
            buffList.append("<tr><td></td></tr>");
            buffList.append("</table>");
            buffList.append("</td>");
            buffList.append("</tr>");

            // Espacio entre cada linea de buff
            buffList.append("<tr>");
            buffList.append("<td height=10></td>");
            buffList.append("</tr>");

            i++;
            changeColor = !changeColor;
        }

        // Make the arrows buttons
        final String previousPageButton;
        final String nextPageButton;
        if (currentPage > 0)
            previousPageButton = "<button value=\"\" width=15 height=15 action=\"bypass -h BuffStore bufflist " + buffer.owner.getObjectId() + " " + (isForPlayer ? "player" : "summon") + " " + (currentPage - 1) + "\" back=L2UI_CT1.ItemWindow_DF_Frame_Down fore=L2UI_CT1.ItemWindow_DF_Frame>";
        else
            previousPageButton = "<button value=\"\" width=15 height=15 action=\"\" back=L2UI_CT1.ItemWindow_DF_Frame_Down fore=L2UI_CT1.ItemWindow_DF_Frame>";

        if (currentPage < maxPage)
            nextPageButton = "<button value=\"\" width=15 height=15 action=\"bypass -h BuffStore bufflist " + buffer.owner.getObjectId() + " " + (isForPlayer ? "player" : "summon") + " " + (currentPage + 1) + "\" back=L2UI_CT1.ItemWindow_DF_Frame_Down fore=L2UI_CT1.ItemWindow_DF_Frame>";
        else
            nextPageButton = "<button value=\"\" width=15 height=15 action=\"\" back=L2UI_CT1.ItemWindow_DF_Frame_Down fore=L2UI_CT1.ItemWindow_DF_Frame>";

        html.replace("%bufferId%", buffer.owner.getObjectId());
        html.replace("%bufferClass%", Util.toProperCaseAll(CharTemplateHolder.getTemplate(buffer.owner.getClassId(), false).className));
        html.replace("%bufferLvl%", (buffer.owner.getLevel() >= 76 && buffer.owner.getLevel() < 80 ? 76 : (buffer.owner.getLevel() >= 84 ? 84 : (int) Math.round(buffer.owner.getLevel() / 10.) * 10)));
        html.replace("%bufferName%", buffer.owner.getName());
        html.replace("%bufferMp%", (int) buffer.owner.getCurrentMp());
        html.replace("%buffPrice%", Util.convertToLineagePriceFormat(buffer.buffPrice));
        html.replace("%target%", (isForPlayer ? "Player" : "Summon"));
        html.replace("%page%", currentPage);
        html.replace("%buffs%", buffList.toString());
        html.replace("%previousPageButton%", previousPageButton);
        html.replace("%nextPageButton%", nextPageButton);
        html.replace("%pageCount%", (currentPage + 1) + "/" + (maxPage + 1));

        player.sendPacket(html);
    }

    public static class BufferData {
        private final Player owner;
        public final String saleTitle;
        public final int buffPrice;
        public final Map<Integer, Skill> buffs = new HashMap<>();

        public BufferData(Player player, String title, int price ) {
            owner = player;
            saleTitle = title;
            buffPrice = price;
        }
    }
}
