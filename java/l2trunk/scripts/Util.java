package l2trunk.scripts;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.data.xml.holder.BuyListHolder;
import l2trunk.gameserver.data.xml.holder.MultiSellHolder;
import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.handler.bbs.CommunityBoardManager;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SubClass;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.entity.SevenSigns;
import l2trunk.gameserver.model.entity.olympiad.Olympiad;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.*;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.WarehouseFunctions;
import l2trunk.scripts.quests._255_Tutorial;

import static l2trunk.commons.lang.NumberUtils.toInt;
import static l2trunk.gameserver.utils.ItemFunctions.addItem;
import static l2trunk.gameserver.utils.ItemFunctions.removeItem;

public final class Util extends Functions {
    public void Gatekeeper(String[] param) {
        if (param.length < 4)
            throw new IllegalArgumentException();

        if (player == null)
            return;

        long price = Long.parseLong(param[param.length - 1]);

        if (!NpcInstance.canBypassCheck(player, player.getLastNpc()))
            return;

        if (price > 0 && player.getAdena() < price) {
            player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            return;
        }

        if (player.isJailed()) {
            player.sendMessage("You cannot escape from Jail!");
            return;
        }

        if (player.getActiveWeaponFlagAttachment() != null) {
            player.sendPacket(SystemMsg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
            return;
        }

        if (player.getMountType() == 2) {
            player.sendMessage("Teleportation riding a Wyvern is not possible.");
            return;
        }

        /* Gag, npc Mozella not TPshit chars that exceed specified in the config file
         * Off Like> = 56 lvl, to limit the data set to lvl'a altsettings.ini.
         */
        final int npcId = (player.getLastNpc() != null ? player.getLastNpc().getNpcId() : 0);
        switch (npcId) {
            case 30483:
                if (player.getLevel() >= Config.CRUMA_GATEKEEPER_LVL) {
                    show("teleporter/30483-no.htm", player);
                    return;
                }
                break;
            case 32864:
            case 32865:
            case 32866:
            case 32867:
            case 32868:
            case 32869:
            case 32870:
                if ((player.getKarma() > 0) || (player.getPvpFlag() > 0)) {
                    show("I'm sorry, but you cannot use my services right now.", player);
                    return;
                }
                if (player.getLevel() < 80) {
                    show("teleporter/" + npcId + "-no.htm", player);
                    return;
                }
                break;
        }

        if (!player.isInPeaceZone() && (player.isInCombat() || player.getPvpFlag() > 0)) {
            player.sendMessage("You cannot teleport in this state!");
            return;
        }

        Location loc = new Location(param);
        int castleId = param.length > 4 ? toInt(param[3]) : 0;

        if (player.getReflection().isDefault()) {
            Castle castle = castleId > 0 ? ResidenceHolder.getCastle(castleId) : null;
            if (castle != null && castle.getSiegeEvent().isInProgress()) {
                player.sendPacket(Msg.YOU_CANNOT_TELEPORT_TO_A_VILLAGE_THAT_IS_IN_A_SIEGE);
                return;
            }
        }

        // Synerge - Epidos cube on teleport should add allowed getPlayer to beleth zone, so other players cannot exploit it
        //	if (npcId == 32376)
        //		BelethManager._allowedPlayers.add(getPlayer.objectId());

        Location pos = Location.findPointToStay(loc, 50, 100, player.getGeoIndex());

        if (price > 0)
            player.reduceAdena(price, true, "Gatekeeper");
        player.teleToLocation(pos);
    }

    public void EpicGatekeeper(String[] param) {
        if (param.length < 4)
            throw new IllegalArgumentException();

        if (player == null)
            return;

        long price = Long.parseLong(param[param.length - 1]);

        if (price > 0 && player.getAdena() < price) {
            player.sendPacket(new Say2(0, ChatType.COMMANDCHANNEL_ALL, "Error", "You dont have enough Adena!"));
            return;
        }

        if (player.isJailed()) {
            player.sendMessage("You cannot escape from Jail!");
            return;
        }

        if (player.getActiveWeaponFlagAttachment() != null) {
            player.sendPacket(SystemMsg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
            return;
        }

        if (player.getMountType() == 2) {
            player.sendMessage("Teleportation riding a Wyvern is not possible.");
            return;
        }

        if (!player.isInPeaceZone() && (player.isInCombat() || player.getPvpFlag() > 0 || !player.getReflection()
                .equals(ReflectionManager.DEFAULT) || player.isInOlympiadMode() || Olympiad.isRegistered(player))) {
            player.sendMessage("You cannot teleport in this state!");
            return;
        }
        Location pos = Location.findPointToStay(new Location(param), 50, 100, player.getGeoIndex());

        player.sendPacket(new HideBoard());
        if (price > 0)
            player.reduceAdena(price, true, "EpicGatekeeper");
        player.teleToLocation(pos);
    }

    public void CommunityGatekeeper(String[] param) {
        if (param.length < 4)
            throw new IllegalArgumentException();

        if (player == null)
            return;

        long price = Long.parseLong(param[param.length - 1]);

        if (price > 0 && player.getAdena() < price) {
            player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            return;
        }

        if (player.isJailed()) {
            player.sendMessage("You cannot escape from Jail!");
            return;
        }

        if (player.getActiveWeaponFlagAttachment() != null) {
            player.sendPacket(SystemMsg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
            return;
        }

        if (player.getMountType() == 2) {
            player.sendMessage("Teleportation riding a Wyvern is not possible.");
            return;
        }

        if (!player.isInPeaceZone() && (player.isInCombat() || player.getPvpFlag() > 0 || !player.getReflection()
                .equals(ReflectionManager.DEFAULT) || player.isInOlympiadMode() || Olympiad.isRegistered(player))) {
            player.sendMessage("You cannot teleport in this state!");
            return;
        }
        int castleId = param.length > 4 ? toInt(param[3]) : 0;
        final boolean closeTutorial = param.length > 5;

        if (player.getReflection().isDefault()) {
            Castle castle = castleId > 0 ? ResidenceHolder.getCastle(castleId) : null;
            if (castle != null && castle.getSiegeEvent().isInProgress()) {
                player.sendPacket(Msg.YOU_CANNOT_TELEPORT_TO_A_VILLAGE_THAT_IS_IN_A_SIEGE);
                return;
            }
        }

        Location pos = Location.findPointToStay(new Location(param), 50, 100, player.getGeoIndex());

        // Synerge - Extra parameter to close tutorial
        if (closeTutorial) {
            final QuestState qs = player.getQuestState(_255_Tutorial.class);
            if (qs != null)
                qs.closeTutorial();
        }

        player.sendPacket(new HideBoard());
        if (price > 0)
            player.reduceAdena(price, true, "CommunityGatekeeper");
        player.teleToLocation(pos);
    }

    public void SSGatekeeper(String[] param) {
        if (param.length < 4)
            throw new IllegalArgumentException();
        if (player == null)
            return;

        int type = toInt(param[3]);

        if (!NpcInstance.canBypassCheck(player, player.getLastNpc()))
            return;

        if (player.isJailed()) {
            player.sendMessage("You cannot escape from Jail!");
            return;
        }

        if (type > 0) {
            int player_cabal = SevenSigns.INSTANCE.getPlayerCabal(player);
            int period = SevenSigns.INSTANCE.getCurrentPeriod();
            if (period == SevenSigns.PERIOD_COMPETITION && player_cabal == SevenSigns.CABAL_NULL) {
                player.sendPacket(Msg.USED_ONLY_DURING_A_QUEST_EVENT_PERIOD);
                return;
            }

            int winner;
            if (period == SevenSigns.PERIOD_SEAL_VALIDATION && (winner = SevenSigns.INSTANCE.getCabalHighestScore()) != SevenSigns.CABAL_NULL) {
                if (winner != player_cabal)
                    return;
                if (type == 1 && SevenSigns.INSTANCE.getSealOwner(SevenSigns.SEAL_AVARICE) != player_cabal)
                    return;
                if (type == 2 && SevenSigns.INSTANCE.getSealOwner(SevenSigns.SEAL_GNOSIS) != player_cabal)
                    return;
            }
        }

        player.teleToLocation(new Location(param));
    }

    private void QuestGatekeeper(String[] param) {
        if (param.length < 5)
            throw new IllegalArgumentException();

        if (player == null)
            return;

        long count = Long.parseLong(param[3]);
        int item = toInt(param[4]);

        if (!NpcInstance.canBypassCheck(player, player.getLastNpc()))
            return;

        if (player.isJailed()) {
            player.sendMessage("You cannot escape from Jail!");
            return;
        }

        if (count > 0) {
            if (!player.getInventory().destroyItemByItemId(item, count, "QuestGatekeeper")) {
                player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
                return;
            }
            player.sendPacket(SystemMessage2.removeItems(item, count));
        }

        Location pos = Location.findPointToStay(new Location(param), 20, 70, player.getGeoIndex());

        player.teleToLocation(pos);
    }

    public void ReflectionGatekeeper(String[] param) {
        if (param.length < 5)
            throw new IllegalArgumentException();

        if (player == null)
            return;

        if (player.isJailed()) {
            player.sendMessage("You cannot escape from Jail!");
            return;
        }

        player.setReflection(toInt(param[4]));

        Gatekeeper(param);
    }

    public void CommunityMultisell(String[] param) {
        if (!player.isInZonePeace()) {
            player.sendMessage("It can be used only in Peaceful zone!");
            return;
        }

        if (player.isJailed()) {
            player.sendMessage("You cannot do it in Jail!");
            return;
        }

        String listId = param[0];
        MultiSellHolder.INSTANCE.SeparateAndSend(toInt(listId), player, 0);
    }

    public void CommunitySell() {
        if (!player.isInZonePeace()) {
            player.sendMessage("It can be used only in Peaceful zone!");
            return;
        }
        if (player.isJailed()) {
            player.sendMessage("You cannot do it in Jail!");
            return;
        }

        BuyListHolder.NpcTradeList list = BuyListHolder.INSTANCE.getBuyList(0);
        if (list == null)
            player.sendPacket(new ExBuySellList.BuyList(list, player, 0), new ExBuySellList.SellRefundList(player, false));
    }

    public void CommunityAugment() {
        if (!player.isInZonePeace()) {
            player.sendMessage("It can be used only in Peaceful zone!");
            return;
        }
        player.sendPacket(Msg.SELECT_THE_ITEM_TO_BE_AUGMENTED, ExShowVariationMakeWindow.STATIC);
    }

    public void CommunityRemoveAugment() {
        if (!player.isInZonePeace()) {
            player.sendMessage("It can be used only in Peaceful zone!");
            return;
        }
        player.sendPacket(Msg.SELECT_THE_ITEM_FROM_WHICH_YOU_WISH_TO_REMOVE_AUGMENTATION, ExShowVariationCancelWindow.STATIC);
    }

    public void CommunityPrivateWarehouseDeposit() {
        if (!player.isInZonePeace()) {
            player.sendMessage("It can be used only in Peaceful zone!");
            return;
        }
        if (player.isJailed()) {
            player.sendMessage("You cannot do it in Jail!");
            return;
        }

        WarehouseFunctions.showDepositWindow(player);
    }

    public void CommunityPrivateWarehouseRetrieve() {
        if (!player.isInZonePeace()) {
            player.sendMessage("It can be used only in Peaceful zone!");
            return;
        }
        if (player.isJailed()) {
            player.sendMessage("You cannot do it in Jail!");
            return;
        }

        WarehouseFunctions.showRetrieveWindow(player, 0);
    }

    public void CommunityClanWarehouseDeposit() {
        if (!player.isInZonePeace()) {
            player.sendMessage("It can be used only in Peaceful zone!");
            return;
        }
        if (player.isJailed()) {
            player.sendMessage("You cannot do it in Jail!");
            return;
        }

        WarehouseFunctions.showDepositWindowClan(player);
    }

    public void CommunityClanWarehouseWithdraw() {
        if (!player.isInZonePeace()) {
            player.sendMessage("It can be used only in Peaceful zone!");
            return;
        }
        if (player.isJailed()) {
            player.sendMessage("You cannot do it in Jail!");
            return;
        }

        WarehouseFunctions.showWithdrawWindowClan(player, 0);
    }

    public void CommunityDrawSymbol() {
        if (!player.isInZonePeace()) {
            player.sendMessage("It can be used only in Peaceful zone!");
            return;
        }
        player.sendPacket(new HennaEquipList(player));
    }

    public void CommunityRemoveSymbol() {
        if (!player.isInZonePeace()) {
            player.sendMessage("It can be used only in Peaceful zone!");
            return;
        }
        player.sendPacket(new HennaUnequipList(player));
    }

    public void CommunityCert65() {
        SubClass clzz = player.getActiveClass();
        if (!checkCertificationCondition(65, SubClass.CERTIFICATION_65))
            return;

        addItem(player, 10280, 1);
        clzz.addCertification(SubClass.CERTIFICATION_65);
        player.store(true);
        CommunityBoardManager.getCommunityHandler("_bbsChooseCertificate").onBypassCommand(player, "_bbsChooseCertificate");
    }

    public void CommunityCert70() {
        SubClass clzz = player.getActiveClass();
        if (!checkCertificationCondition(70, SubClass.CERTIFICATION_70))
            return;

        addItem(player, 10280, 1);
        clzz.addCertification(SubClass.CERTIFICATION_70);
        player.store(true);
        CommunityBoardManager.getCommunityHandler("_bbsChooseCertificate").onBypassCommand(player, "_bbsChooseCertificate");
    }

    public void CommunityCert75Class() {
        SubClass clzz = player.getActiveClass();
        if (!checkCertificationCondition(75, SubClass.CERTIFICATION_75))
            return;

        ClassId cl = clzz.getClassId();
        if (cl.getType2() == null)
            return;

        addItem(player, cl.getType2().certificate(), 1);
        clzz.addCertification(SubClass.CERTIFICATION_75);
        player.store(true);
        CommunityBoardManager.getCommunityHandler("_bbsChooseCertificate").onBypassCommand(player, "_bbsChooseCertificate");
    }

    public void CommunityCert75Master() {
        SubClass clzz = player.getActiveClass();
        if (!checkCertificationCondition(75, SubClass.CERTIFICATION_75))
            return;

        addItem(player, 10612, 1); // master ability
        clzz.addCertification(SubClass.CERTIFICATION_75);
        player.store(true);
        CommunityBoardManager.getCommunityHandler("_bbsChooseCertificate").onBypassCommand(player, "_bbsChooseCertificate");
    }

    public void CommunityCert80() {
        SubClass clzz = player.getActiveClass();
        if (!checkCertificationCondition(80, SubClass.CERTIFICATION_80))
            return;

        ClassId cl = clzz.getClassId();
        if (cl.getType2() == null)
            return;

        addItem(player, cl.getType2().transformation(), 1);
        clzz.addCertification(SubClass.CERTIFICATION_80);
        player.store(true);
        CommunityBoardManager.getCommunityHandler("_bbsChooseCertificate").onBypassCommand(player, "_bbsChooseCertificate");
    }

    private boolean checkCertificationCondition(int requiredLevel, int certificationIndex) {
        boolean failed = false;
        if (player.getLevel() < requiredLevel) {
            player.sendMessage("Your Level is too low!");
            failed = true;
        }
        SubClass clazz = player.getActiveClass();
        if (!failed && clazz.isCertificationGet(certificationIndex)) {
            player.sendMessage("You already have this Certification!");
            failed = true;
        }

        if (failed) {
            CommunityBoardManager.getCommunityHandler("_bbsfile").onBypassCommand(player, "_bbsfile:smallNpcs/subclassChanger");
            return false;
        }
        return true;
    }

    public void TokenJump(String[] param) {
        if (player == null)
            return;
        if (player.getLevel() <= 19)
            QuestGatekeeper(param);
        else
            show("Only for newbies", player);
    }

    public void NoblessTeleport() {
        if (player == null)
            return;
        if (player.isNoble() || Config.ALLOW_NOBLE_TP_TO_ALL)
            show("scripts/noble.htm", player);
        else
            show("scripts/nobleteleporter-no.htm", player);
    }

    public void PayPage(String[] param) {
        if (param.length < 2)
            throw new IllegalArgumentException();

        if (player == null)
            return;

        String page = param[0];
        int item = toInt(param[1]);
        long price = Long.parseLong(param[2]);

        if (!player.haveItem(item, price)) {
            player.sendPacket(item == 57 ? Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA : SystemMsg.INCORRECT_ITEM_COUNT);
            return;
        }

        removeItem(player, item, price, "PayPage");
        show(page, player);
    }

}