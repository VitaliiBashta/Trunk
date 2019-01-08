package l2trunk.gameserver.network.clientpackets;

import Elemental.datatables.OfflineBuffersTable;
import l2trunk.commons.lang.Pair;
import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.dao.MailDAO;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.instancemanager.CursedWeaponsManager;
import l2trunk.gameserver.instancemanager.PetitionManager;
import l2trunk.gameserver.instancemanager.PlayerMessageStack;
import l2trunk.gameserver.instancemanager.QuestManager;
import l2trunk.gameserver.listener.actor.player.OnAnswerListener;
import l2trunk.gameserver.listener.actor.player.impl.ReviveAnswerListener;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.base.InvisibleType;
import l2trunk.gameserver.model.entity.CCPHelpers.CCPSecondaryPassword;
import l2trunk.gameserver.model.entity.Hero;
import l2trunk.gameserver.model.entity.SevenSigns;
import l2trunk.gameserver.model.entity.events.impl.ClanHallAuctionEvent;
import l2trunk.gameserver.model.entity.olympiad.Olympiad;
import l2trunk.gameserver.model.entity.residence.ClanHall;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.mail.Mail;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.pledge.SubUnit;
import l2trunk.gameserver.model.pledge.UnitMember;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.network.GameClient;
import l2trunk.gameserver.network.serverpackets.ConfirmDlg;
import l2trunk.gameserver.network.serverpackets.*;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.IStaticPacket;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.skills.AbnormalEffect;
import l2trunk.gameserver.templates.item.ItemTemplate;
import l2trunk.gameserver.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

public final class EnterWorld extends L2GameClientPacket {
    private static final Object _lock = new Object();

    private static final Logger LOG = LoggerFactory.getLogger(EnterWorld.class);

    private static void notifyClanMembers(Player activeChar) {
        Clan clan = activeChar.getClan();
        SubUnit subUnit = activeChar.getSubUnit();
        if (clan == null || subUnit == null)
            return;

        UnitMember member = subUnit.getUnitMember(activeChar.getObjectId());
        if (member == null)
            return;

        member.setPlayerInstance(activeChar, false);

        int sponsor = activeChar.getSponsor();
        int apprentice = activeChar.getApprentice();
        L2GameServerPacket msg = new SystemMessage2(SystemMsg.CLAN_MEMBER_S1_HAS_LOGGED_INTO_GAME).addName(activeChar);
        IStaticPacket memberUpdate = new PledgeShowMemberListUpdate(activeChar);

        for (Player clanMember : clan.getOnlineMembers(activeChar.getObjectId())) {
            clanMember.sendPacket(memberUpdate);
            if (clanMember.getObjectId() == sponsor)
                clanMember.sendPacket(new SystemMessage2(SystemMsg.YOUR_APPRENTICE_C1_HAS_LOGGED_OUT).addName(activeChar));
            else if (clanMember.getObjectId() == apprentice)
                clanMember.sendPacket(new SystemMessage2(SystemMsg.YOUR_SPONSOR_C1_HAS_LOGGED_IN).addName(activeChar));
            else
                clanMember.sendPacket(msg);
        }

        if (!activeChar.isClanLeader())
            return;

        ClanHall clanHall = clan.getHasHideout() > 0 ? ResidenceHolder.getResidence(ClanHall.class, clan.getHasHideout()) : null;
        if (clanHall == null || clanHall.getAuctionLength() != 0)
            return;

        if (clanHall.getSiegeEvent().getClass() != ClanHallAuctionEvent.class)
            return;

        if (clan.getWarehouse().getCountOf(ItemTemplate.ITEM_ID_ADENA) < clanHall.getRentalFee())
            activeChar.sendPacket(new SystemMessage2(SystemMsg.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_ME_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW).addLong(clanHall.getRentalFee()));
    }

    private static void loadTutorial(Player player) {
        Quest q = QuestManager.getQuest(255);
        if (q != null) {
            if (CCPSecondaryPassword.hasPassword(player)) {
                player.processQuestEvent(q.getName(), "CheckPass", null, false);

            } else {
                player.processQuestEvent(q.getName(), "ProposePass", null, false);

            }
			/*
			else if (player.getLevel() == 1 || Rnd.get(10) == 1)
			{
				player.processQuestEvent(q.getName(), "ProposePass", null, false);
			}
			else
			{
				player.processQuestEvent(q.getName(), "UC", null, false);
			}
			*/
            player.processQuestEvent(q.getName(), "OpenClassMaster", null, false);
            player.processQuestEvent(q.getName(), "ShowChangeLog", null, false);


        }
    }

    @Override
    protected void readImpl() {
        // readS(); - client always sends the String "narcasse"
    }

    @SuppressWarnings("null")
    @Override
    protected void runImpl() {
        long lastAccess = 0;
        final GameClient client = getClient();
        Player activeChar = client.getActiveChar();

        if (activeChar == null || Config.AUTH_SERVER_GM_ONLY && !activeChar.isGM()) {
            client.closeNow(false);
            return;
        }

        int myObjectId = activeChar.getObjectId();
        int myStoreId = activeChar.getStoredId();

        synchronized (_lock) { // TODO [G1ta0] Th is for garbage, and why is it here?
            GameObjectsStorage.getAllPlayersStream()
                    .filter(cha -> myStoreId != cha.getStoredId())
                    .filter(cha -> cha.getObjectId() == myObjectId)
                    .forEach(cha -> {
                        LOG.warn("Double EnterWorld for char: " + activeChar.getName());
                        cha.kick();
                    });
        }

        GameStats.incrementPlayerEnterGame();

        boolean first = activeChar.entering;

        if (!activeChar.isHero() || !activeChar.isFakeHero()) {
            ItemFunctions.removeItem(activeChar, 6842, 1, true, "RemoveCirclet");
            ItemFunctions.removeItem(activeChar, 37032, 1, true, "removeCloak");
        }

        if (activeChar.getTitle().equals("*Away*")) {
            activeChar.setTitle(null);
            activeChar.setTitleColor(Player.DEFAULT_TITLE_COLOR);
        }

        if (first) {
            activeChar.setUptime(System.currentTimeMillis());
            activeChar.setOnlineStatus(true);
            lastAccess = activeChar.getLastAccess();
            if (activeChar.getPlayerAccess().GodMode && !Config.SHOW_GM_LOGIN) {
                activeChar.setInvisibleType(InvisibleType.EFFECT);
                activeChar.startAbnormalEffect(AbnormalEffect.STEALTH);
                activeChar.sendUserInfo(true);
                if (activeChar.isGM()) {
                    World.removeObjectFromPlayers(activeChar);
                }
            }
            activeChar.setNonAggroTime(System.currentTimeMillis() + Config.NONAGGRO_TIME_ONTELEPORT);
            activeChar.spawnMe();
            activeChar.setPendingOlyEnd(false);

            if (activeChar.isInStoreMode() && !activeChar.isInBuffStore()) {
                if (!TradeHelper.checksIfCanOpenStore(activeChar, activeChar.getPrivateStoreType())) {
                    activeChar.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
                    activeChar.broadcastCharInfo();
                }
            }
            // Ady - If its in a buff store, remove it on login
            else if (activeChar.isInBuffStore()) {
                activeChar.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
                activeChar.broadcastCharInfo();
            }

            activeChar.setRunning();
            activeChar.standUp();
            activeChar.startTimers();
        }


        if (Config.ENTER_WORLD_ANNOUNCEMENTS_HERO_LOGIN) {
            if ((activeChar.isHero()) || (activeChar.isFakeHero())) {
                Announcements.INSTANCE.announceToAll(new CustomMessage("Hero {0} entered the game.").addString(activeChar.getName()).toString());
            }
        }
        if (Config.ENTER_WORLD_ANNOUNCEMENTS_LORD_LOGIN) {
            if ((activeChar.getClan() != null) && (activeChar.isClanLeader()) && (activeChar.getClan().getCastle() != 0)) {
                int id = activeChar.getCastle().getId();
                Announcements.INSTANCE.announceToAll(new CustomMessage("Lord {0} the owner of the castle {1} entered the game.").addString(activeChar.getName()).addString(new CustomMessage("common.castle." + id, activeChar).toString()).toString());
            }
        }
        activeChar.getMacroses().sendUpdate();
        activeChar.sendPacket(new SSQInfo(), new HennaInfo(activeChar));
        activeChar.sendItemList(false);
        activeChar.sendPacket(new ShortCutInit(activeChar), new SkillList(activeChar), new SkillCoolTime(activeChar));
        activeChar.sendPacket(SystemMsg.WELCOME_TO_THE_WORLD_OF_LINEAGE_II);

        // New char is Hero
        if (Config.NEW_CHAR_IS_HERO) {
            activeChar.setHero(true);
            Hero.addSkills(activeChar);
        }

        // Add Hero SKills on enter if character log in
        if (activeChar.isFakeHero()) {
            Hero.addSkills(activeChar);
        }

        // New char is NOBLE
        if (Config.NEW_CHAR_IS_NOBLE && !activeChar.isNoble()) {
            Olympiad.addNoble(activeChar);
            activeChar.setNoble(true);
            activeChar.updatePledgeClass();
            activeChar.updateNobleSkills();
            activeChar.sendPacket(new SkillList(activeChar));
            activeChar.broadcastUserInfo(true);
        }

        if (Config.HTML_WELCOME) {
            String html = HtmCache.INSTANCE.getNotNull("welcome.htm", activeChar);
            NpcHtmlMessage msg = new NpcHtmlMessage(5);
            html.replace("%name%", activeChar.getName());
            msg.setHtml(Strings.bbParse(html));
            activeChar.sendPacket(msg);
        }

        Announcements.INSTANCE.showAnnouncements(activeChar);

        if (first)
            activeChar.getListeners().onEnter();

        SevenSigns.INSTANCE.sendCurrentPeriodMsg(activeChar);

        if (first && activeChar.getCreateTime() > 0L) {
            Calendar create = Calendar.getInstance();
            create.setTimeInMillis(activeChar.getCreateTime());
            Calendar now = Calendar.getInstance();

            int day = create.get(Calendar.DAY_OF_MONTH);
            if (create.get(Calendar.MONTH) == Calendar.FEBRUARY && day == 29)
                day = 28;

        }

        if (activeChar.getClan() != null) {
            notifyClanMembers(activeChar);

            activeChar.sendPacket(activeChar.getClan().listAll());
            activeChar.sendPacket(new PledgeShowInfoUpdate(activeChar.getClan()), new PledgeSkillList(activeChar.getClan()));
        }

        if (first) {
            activeChar.getFriendList().notifyFriends(true);
            loadTutorial(activeChar);
            activeChar.restoreDisableSkills();

            if (activeChar.getVar("Para") != null) {
                if (!activeChar.isBlocked())
                    activeChar.setBlock(true);
                activeChar.startAbnormalEffect(AbnormalEffect.HOLD_1);
                activeChar.abortAttack(true, false);
                activeChar.abortCast(true, false);
                activeChar.sendPacket(new Say2(activeChar.getObjectId(), ChatType.TELL, "Paralyze", "You are paralyzed for " + (activeChar.getVarTimeToExpire("Para") / 60000L) + " more minutes!"));
            }

            if (Config.ALLOW_MAIL_OPTION)
                AccountEmail.checkEmail(activeChar);
        }

        sendPacket(new L2FriendList(activeChar), new QuestList(activeChar), new ExBasicActionList(activeChar), new EtcStatusUpdate(activeChar));

        activeChar.checkHpMessages(activeChar.getMaxHp(), activeChar.getCurrentHp());
        activeChar.checkDayNightMessages();

        if (Config.PETITIONING_ALLOWED)
            PetitionManager.getInstance().checkPetitionMessages(activeChar);

        if (!first) {
            if (activeChar.isCastingNow()) {
                Creature castingTarget = activeChar.getCastingTarget();
                Skill castingSkill = activeChar.getCastingSkill();
                long animationEndTime = activeChar.getAnimationEndTime();

                if (castingSkill != null && castingTarget != null && castingTarget.isCreature() && activeChar.getAnimationEndTime() > 0L)
                    sendPacket(new MagicSkillUse(activeChar, castingTarget, castingSkill.getId(), castingSkill.getLevel(), (int) (animationEndTime - System.currentTimeMillis()), 0));
            }

            if (activeChar.isInBoat())
                activeChar.sendPacket(activeChar.getBoat().getOnPacket(activeChar, activeChar.getInBoatPosition()));

            if (activeChar.isMoving || activeChar.isFollow)
                sendPacket(activeChar.movePacket());

            if (activeChar.getMountNpcId() != 0)
                sendPacket(new Ride(activeChar));

            if (activeChar.isFishing())
                activeChar.stopFishing();
        }

        activeChar.entering = false;
        activeChar.sendUserInfo(true);

        if (activeChar.isSitting())
            activeChar.sendPacket(new ChangeWaitType(activeChar, ChangeWaitType.WT_SITTING));

        if (activeChar.getPrivateStoreType() != Player.STORE_PRIVATE_NONE) {
            if (activeChar.getPrivateStoreType() == Player.STORE_PRIVATE_BUY)
                sendPacket(new PrivateStoreMsgBuy(activeChar));
            else if (activeChar.getPrivateStoreType() == Player.STORE_PRIVATE_SELL || activeChar.getPrivateStoreType() == Player.STORE_PRIVATE_SELL_PACKAGE)
                sendPacket(new PrivateStoreMsgSell(activeChar));
            else if (activeChar.getPrivateStoreType() == Player.STORE_PRIVATE_MANUFACTURE)
                sendPacket(new RecipeShopMsg(activeChar));
        }

        if (activeChar.isDead())
            sendPacket(new Die(activeChar));

        activeChar.unsetVar("offline");

        // just in case
        activeChar.sendActionFailed();

        if (first && activeChar.isGM() && Config.SAVE_GM_EFFECTS && activeChar.getPlayerAccess().CanUseGMCommand) {
            // gmspeed
            try {
                int var_gmspeed = Integer.parseInt(activeChar.getVar("gm_gmspeed"));
                if (var_gmspeed >= 1 && var_gmspeed <= 4) {
                    activeChar.doCast(7029, var_gmspeed, activeChar, true);
                }
            } catch (NumberFormatException e) {
                //LOG.error("Error while loading gmSpeed var ", e);
            }
            // silence
            if (activeChar.getVarB("gm_silence")) {
                activeChar.setMessageRefusal(true);
                activeChar.sendPacket(SystemMsg.MESSAGE_REFUSAL_MODE);
            }
            // invul
            if (activeChar.getVarB("gm_invul")) {
                activeChar.setInvul(true);
                activeChar.sendMessage(activeChar.getName() + " is now Spartan !!!");
            }
        }

        if (first && activeChar.isInJail()) {
            long period = activeChar.getVarTimeToExpire("jailed");
            if (period == -1) {
                activeChar.sendPacket(new Say2(0, ChatType.TELL, "Administration", " You are jailed forever !"));
            } else {
                period /= 1000; // to seconds
                period /= 60; // to minutes

                activeChar.sendPacket(new Say2(0, ChatType.TELL, "Administration", "Sit left " + TimeUtils.minutesToFullString((int) period)));
            }
        }
        PlayerMessageStack.getInstance().CheckMessages(activeChar);

        sendPacket(ClientSetTime.STATIC, new ExSetCompassZoneCode(activeChar));

        Pair<Integer, OnAnswerListener> entry = activeChar.getAskListener(false);
        if (entry != null && entry.getValue() instanceof ReviveAnswerListener)
            sendPacket(new ConfirmDlg(SystemMsg.C1_IS_MAKING_AN_ATTEMPT_TO_RESURRECT_YOU_IF_YOU_CHOOSE_THIS_PATH_S2_EXPERIENCE_WILL_BE_RETURNED_FOR_YOU, 0).addString("Other player").addString("some"));

        if (activeChar.isCursedWeaponEquipped())
            CursedWeaponsManager.INSTANCE.showUsageTime(activeChar, activeChar.getCursedWeaponEquippedId());

        if (first) {
            if (Config.BUFF_STORE_ENABLED)
                OfflineBuffersTable.getInstance().onLogin(activeChar);
        }

        if (first) {
            activeChar.sendUserInfo(); // Display right in clan
        } else {
            // Characters left while viewing
            if (activeChar.isInObserverMode()) {
                if (activeChar.getObserverMode() == Player.OBSERVER_LEAVING) {
                    activeChar.returnFromObserverMode();
                } else if (activeChar.getOlympiadObserveGame() != null) {
                    activeChar.leaveOlympiadObserverMode(true);
                } else {
                    activeChar.leaveObserverMode();
                }
            } else if (activeChar.isVisible()) {
                World.showObjectsToPlayer(activeChar);
            }

            if (activeChar.getPet() != null) {
                sendPacket(new PetInfo(activeChar.getPet()));
            }

            if (activeChar.isInParty()) {
                Summon memberPet;
                // sends new member party window for all members
                // we do all actions before adding member to a list, this speeds
                // things up a little
                sendPacket(new PartySmallWindowAll(activeChar.getParty(), activeChar));

                for (Player member : activeChar.getParty().getMembers()) {
                    if (member != activeChar) {
                        sendPacket(new PartySpelled(member, true));
                        if ((memberPet = member.getPet()) != null) {
                            sendPacket(new PartySpelled(memberPet, true));
                        }

                        sendPacket(RelationChanged.update(activeChar, member, activeChar));
                    }
                }

                // If the party is in the CC, the newcomer send the package open
                // the CC
                if (activeChar.getParty().isInCommandChannel()) {
                    sendPacket(ExMPCCOpen.STATIC);
                }
            }

            for (int shotId : activeChar.getAutoSoulShot()) {
                sendPacket(new ExAutoSoulShot(shotId, true));
            }

            for (Effect e : activeChar.getEffectList().getAllFirstEffects()) {
                if (e.getSkill().isToggle()) {
                    sendPacket(new MagicSkillLaunched(activeChar.getObjectId(), e.getSkill().getId(), e.getSkill().getLevel(), activeChar));
                }
            }

            activeChar.broadcastCharInfo();
        }

        activeChar.updateEffectIcons();
        activeChar.updateStats();

        if (activeChar.getVarB("soulshot")) {
            ItemInstance item = activeChar.getActiveWeaponInstance();
            if (item != null) {
                switch (item.getCrystalType().cry) {
                    case (ItemTemplate.CRYSTAL_NONE): {
                        boolean bActive = false;
                        ItemInstance shot = activeChar.getInventory().getItemByItemId(5789); // Beginner Soulshot
                        if (shot != null) {
                            activeChar.addAutoSoulShot(5789);
                            activeChar.sendPacket(new ExAutoSoulShot(shot.getItemId(), true));
                            activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(shot.getName()));
                            bActive = true;
                        }
                        shot = activeChar.getInventory().getItemByItemId(1835); // Soulshot no grade
                        if (shot != null) {
                            activeChar.addAutoSoulShot(1835);
                            activeChar.sendPacket(new ExAutoSoulShot(shot.getItemId(), true));
                            activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(shot.getName()));
                            bActive = true;
                        }
                        shot = activeChar.getInventory().getItemByItemId(5790); // Beginner spiritshot
                        if (shot != null) {
                            activeChar.addAutoSoulShot(5790);
                            activeChar.sendPacket(new ExAutoSoulShot(shot.getItemId(), true));
                            activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(shot.getName()));
                            bActive = true;
                        }
                        shot = activeChar.getInventory().getItemByItemId(2509); // Spiritshot no grade
                        if (shot != null) {
                            activeChar.addAutoSoulShot(2509);
                            activeChar.sendPacket(new ExAutoSoulShot(shot.getItemId(), true));
                            activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(shot.getName()));
                            bActive = true;
                        }
                        shot = activeChar.getInventory().getItemByItemId(3947); // Blessed spiritshot no grade
                        if (shot != null) {
                            activeChar.addAutoSoulShot(3947);
                            activeChar.sendPacket(new ExAutoSoulShot(shot.getItemId(), true));
                            activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(shot.getName()));
                            bActive = true;
                        }
                        if (bActive)
                            activeChar.autoShot();

                    }

                    case (ItemTemplate.CRYSTAL_D): {
                        boolean bActive = false;
                        ItemInstance shot = activeChar.getInventory().getItemByItemId(1463); // Soulshot d
                        if (shot != null) {
                            activeChar.addAutoSoulShot(1463);
                            activeChar.sendPacket(new ExAutoSoulShot(shot.getItemId(), true));
                            activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(shot.getName()));
                            bActive = true;
                        }
                        shot = activeChar.getInventory().getItemByItemId(3948); // Blessed Spiritshot d
                        if (shot != null) {
                            activeChar.addAutoSoulShot(3948);
                            activeChar.sendPacket(new ExAutoSoulShot(shot.getItemId(), true));
                            activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(shot.getName()));
                            bActive = true;
                        }
                        if (bActive)
                            activeChar.autoShot();
                    }

                    case (ItemTemplate.CRYSTAL_C): {
                        boolean bActive = false;
                        ItemInstance shot = activeChar.getInventory().getItemByItemId(1464); // Soulshot c
                        if (shot != null) {
                            activeChar.addAutoSoulShot(1464);
                            activeChar.sendPacket(new ExAutoSoulShot(shot.getItemId(), true));
                            activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(shot.getName()));
                            bActive = true;
                        }
                        shot = activeChar.getInventory().getItemByItemId(3949); // Blessed Spiritshot c
                        if (shot != null) {
                            activeChar.addAutoSoulShot(3949);
                            activeChar.sendPacket(new ExAutoSoulShot(shot.getItemId(), true));
                            activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(shot.getName()));
                            bActive = true;
                        }
                        if (bActive)
                            activeChar.autoShot();

                    }
                    case (ItemTemplate.CRYSTAL_B): {
                        boolean bActive = false;
                        ItemInstance shot = activeChar.getInventory().getItemByItemId(1465); // Soulshot b
                        if (shot != null) {
                            activeChar.addAutoSoulShot(1465);
                            activeChar.sendPacket(new ExAutoSoulShot(shot.getItemId(), true));
                            activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(shot.getName()));
                            bActive = true;
                        }
                        shot = activeChar.getInventory().getItemByItemId(3950); // Blessed Spiritshot b
                        if (shot != null) {
                            activeChar.addAutoSoulShot(3950);
                            activeChar.sendPacket(new ExAutoSoulShot(shot.getItemId(), true));
                            activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(shot.getName()));
                            bActive = true;
                        }
                        if (bActive)
                            activeChar.autoShot();


                    }
                    case (ItemTemplate.CRYSTAL_A): {
                        boolean bActive = false;
                        ItemInstance shot = activeChar.getInventory().getItemByItemId(1466); // Soulshot a
                        if (shot != null) {
                            activeChar.addAutoSoulShot(1466);
                            activeChar.sendPacket(new ExAutoSoulShot(shot.getItemId(), true));
                            activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(shot.getName()));
                            bActive = true;
                        }
                        shot = activeChar.getInventory().getItemByItemId(3951); // Blessed Spiritshot a
                        if (shot != null) {
                            activeChar.addAutoSoulShot(3951);
                            activeChar.sendPacket(new ExAutoSoulShot(shot.getItemId(), true));
                            activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(shot.getName()));
                            bActive = true;
                        }
                        if (bActive)
                            activeChar.autoShot();

                    }

                    case (ItemTemplate.CRYSTAL_S): {
                        boolean bActive = false;
                        ItemInstance shot = activeChar.getInventory().getItemByItemId(1467); // Soulshot s
                        if (shot != null) {
                            activeChar.addAutoSoulShot(1467);
                            activeChar.sendPacket(new ExAutoSoulShot(shot.getItemId(), true));
                            activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(shot.getName()));
                            bActive = true;
                        }
                        shot = activeChar.getInventory().getItemByItemId(3952); // Blessed Spiritshot s
                        if (shot != null) {
                            activeChar.addAutoSoulShot(3952);
                            activeChar.sendPacket(new ExAutoSoulShot(shot.getItemId(), true));
                            activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(shot.getName()));
                            bActive = true;
                        }
                        if (bActive)
                            activeChar.autoShot();

                    }
                }
            }
        }

        if (Config.ALT_PCBANG_POINTS_ENABLED)
            activeChar.sendPacket(new ExPCCafePointInfo(activeChar, 0, 1, 2, 12));

        if (!activeChar.getPremiumItemList().isEmpty())
            activeChar.sendPacket(Config.GOODS_INVENTORY_ENABLED ? ExGoodsInventoryChangedNotify.STATIC : ExNotifyPremiumItem.STATIC);

        if (activeChar.getVarB("HeroPeriod") && Config.SERVICES_HERO_SELL_ENABLED) {
            activeChar.setHero(true);
        }

        activeChar.sendVoteSystemInfo();
        activeChar.sendPacket(new ExReceiveShowPostFriend(activeChar));
        activeChar.getNevitSystem().onEnterWorld();

        checkNewMail(activeChar);

        String lastAccessDate = TimeUtils.convertDateToString(lastAccess * 1000);

        String ip = activeChar.getVar("LastIP");
        if (ip != null && !ip.isEmpty() && activeChar.getIP() != null) {
            if (!activeChar.getIP().equalsIgnoreCase(ip)) {
                activeChar.sendPacket(new Say2(activeChar.getObjectId(), ChatType.CRITICAL_ANNOUNCE, "SYS", "You are logging in from another IP. Last access: " + lastAccessDate));

                if (Config.ALLOW_MAIL_OPTION)
                    AccountEmail.verifyEmail(activeChar, null); // Send an e-mail verification html to this character so he can play only when he verifies his e-mail.
                else
                    activeChar.setVar("LastIP", activeChar.getIP()); // Handled in verifyEmail if the above is ran. It is used to not abuse character relog to escape the verifyEmail.
            }
        } else {
            // IP is null or empty, must populate the var for the next time.
            activeChar.setVar("LastIP", activeChar.getIP());
        }

    }

    private void checkNewMail(Player activeChar) {
        for (Mail mail : MailDAO.getInstance().getReceivedMailByOwnerId(activeChar.getObjectId()))
            if (mail.isUnread()) {
                sendPacket(ExNoticePostArrived.STATIC_FALSE);
                break;
            }
    }
}