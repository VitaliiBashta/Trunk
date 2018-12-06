package l2trunk.gameserver.network.clientpackets;

import l2trunk.commons.lang.ArrayUtils;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.cache.ItemInfoCache;
import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2trunk.gameserver.instancemanager.PetitionManager;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.entity.olympiad.OlympiadGame;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.matching.MatchingRoom;
import l2trunk.gameserver.network.serverpackets.ActionFail;
import l2trunk.gameserver.network.serverpackets.Say2;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.Log;
import l2trunk.gameserver.utils.MapUtils;
import l2trunk.gameserver.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Say2C extends L2GameClientPacket {
    private static final Logger _log = LoggerFactory.getLogger(Say2C.class);

    /**
     * RegExp для кэширования ссылок на предметы, пример ссылки: \b\tType=1 \tID=268484598 \tColor=0 \tUnderline=0 \tTitle=\u001BAdena\u001B\b
     */
    private static final Pattern EX_ITEM_LINK_PATTERN = Pattern.compile("[\b]\tType=[0-9]+[\\s]+\tID=([0-9]+)[\\s]+\tColor=[0-9]+[\\s]+\tUnderline=[0-9]+[\\s]+\tTitle=\u001B(.[^\u001B]*)[^\b]");
    private static final Pattern SKIP_ITEM_LINK_PATTERN = Pattern.compile("[\b]\tType=[0-9]+(.[^\b]*)[\b]");
    public static boolean LOCK_HERO_VOICE = false;
    public static boolean LOCK_SHOUT_VOICE = false;

    private String _text;
    private ChatType _type;
    private String _target;

    private static void checkAutoRecall(Player sender, Player receiver) {
        if (receiver.isGM() && receiver.getQuickVarB("autoRecall", false)) {
            if (receiver.getDistance(sender) < 500 || sender.isTeleporting() || sender.isInOlympiadMode() || sender.getReflection() != ReflectionManager.DEFAULT)
                return;

            sender.teleToLocation(Location.findAroundPosition(receiver, 100));
            receiver.sendMessage("Recalled " + sender.getName() + ". Use \"//autorecall false\" to disable it!");
        }
    }

    private static void shout(Player activeChar, Say2 cs) {
        int rx = MapUtils.regionX(activeChar);
        int ry = MapUtils.regionY(activeChar);
        int offset = Config.SHOUT_OFFSET;

        for (Player player : GameObjectsStorage.getAllPlayers()) {
            if (player == activeChar || activeChar.getReflection() != player.getReflection() || player.isBlockAll() || player.isInBlockList(activeChar))
                continue;

            int tx = MapUtils.regionX(player);
            int ty = MapUtils.regionY(player);

            if (tx >= rx - offset && tx <= rx + offset && ty >= ry - offset && ty <= ry + offset || activeChar.isInRangeZ(player, Config.CHAT_RANGE))
                player.sendPacket(cs);
        }
    }

    private static void announce(Player activeChar, Say2 cs) {
        GameObjectsStorage.getAllPlayers().stream()
                .filter(player -> player != activeChar)
                .filter(player -> activeChar.getReflection() == player.getReflection())
                .filter(player -> !player.isBlockAll())
                .filter(player -> !player.isInBlockList(activeChar))
                .forEach(player -> player.sendPacket(cs));
    }

    @Override
    protected void readImpl() {
        _text = readS(Config.CHAT_MESSAGE_MAX_LEN);
        _type = l2trunk.commons.lang.ArrayUtils.valid(ChatType.VALUES, readD());
        _target = _type == ChatType.TELL ? readS(Config.CNAME_MAXLEN) : null;
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        if (_type == null || _text == null || _text.length() == 0) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isBlocked()) {
            activeChar.sendActionFailed();
            return;
        }

        _text = _text.replaceAll("\\n", "").replaceAll("\n", "");

        if (_text.length() == 0) {
            activeChar.sendActionFailed();
            return;
        }

        Player receiver = World.getPlayer(_target);
        if (activeChar.getLevel() <= Config.SHOUT_REQUIRED_LEVEL && activeChar.getSubClasses().size() <= 1 && ((_type == ChatType.SHOUT && (receiver == null || !receiver.isGM())) || _type == ChatType.TRADE)) {
            activeChar.sendMessage("This Shouting Chat is allowed only for characters with level higher than " + Config.SHOUT_REQUIRED_LEVEL + " to avoid spam!");
            activeChar.sendActionFailed();
            return;
        }

        if (Functions.isEventStarted("events.Viktorina.Viktorina")) {
            String answer = _text.trim();
            if (answer.length() > 0) {
                Object[] objects = {answer, activeChar};
                Functions.callScripts("events.Viktorina.Viktorina", "checkAnswer", objects);
            }
        }

        if (_text.startsWith(".") && !Config.NOT_USE_USER_VOICED) // If available Voice Commands for configuration, process them
        {
            String fullcmd = _text.substring(1).trim();
            String command = fullcmd.split("\\s+")[0];
            String args = fullcmd.substring(command.length()).trim();

            if (command.length() > 0) {
                // then check for VoicedCommands
                IVoicedCommandHandler vch = VoicedCommandHandler.INSTANCE.getVoicedCommandHandler(command);
                if (vch != null) {
                    vch.useVoicedCommand(command, activeChar, args);
                    return;
                }
            }

            // Check again for min lvl if there is no voiced with that text
            if (activeChar.getSubClasses().size() <= 1 && (_type == ChatType.ALL && (receiver == null || !receiver.isGM())))
                return;
        } else if (_text.startsWith("==") && !activeChar.isBlocked()) {
            String expression = _text.substring(2);
//            Expression expr = null;

            if (!expression.isEmpty()) {
//                try {
//                    expr = ExpressionTree.parse(expression);
//                } catch (ExpressionParseException epe) {
//
//                }

//                if (expr != null) {
                double result;

                try {
//                        VarMap vm = new VarMap();
//                        vm.setValue("adena", activeChar.getAdena());
//                        result = expr.eval(vm, null);
                    activeChar.sendMessage(expression);
//                        activeChar.sendMessage("=" + Util.formatDouble(result, "NaN", false));
                } catch (RuntimeException e) {

                }
//                }
            }

            return;
        }

        if (Config.CHATFILTER_MIN_LEVEL > 0 && ArrayUtils.contains(Config.CHATFILTER_CHANNELS, _type.ordinal()) && activeChar.getLevel() < Config.CHATFILTER_MIN_LEVEL) {
            if (Config.CHATFILTER_WORK_TYPE == 1)
                _type = ChatType.ALL;
            else if (Config.CHATFILTER_WORK_TYPE == 2) {
                activeChar.sendMessage(new CustomMessage("chat.NotHavePermission", activeChar).addNumber(Config.CHATFILTER_MIN_LEVEL));
                return;
            }
        }

        boolean globalchat = _type != ChatType.ALLIANCE && _type != ChatType.CLAN && _type != ChatType.PARTY;

        if (Config.TRADE_CHATS_REPLACE && globalchat)
            for (String s : Config.TRADE_WORDS)
                if (_text.contains(s)) {
                    _type = ChatType.TRADE;
                    break;
                }

        // Caching objects links
        Matcher m = EX_ITEM_LINK_PATTERN.matcher(_text);
        ItemInstance item;
        int objectId;

        while (m.find()) {
            objectId = Integer.parseInt(m.group(1));
            item = activeChar.getInventory().getItemByObjectId(objectId);

            if (item == null) {
                activeChar.sendActionFailed();
                break;
            }

            ItemInfoCache.getInstance().put(item);
        }

        String translit = activeChar.getVar("translit");
        if (translit != null) {
            //Rule of transliteration references to objects
            m = SKIP_ITEM_LINK_PATTERN.matcher(_text);
            StringBuilder sb = new StringBuilder();
            int end = 0;
            while (m.find()) {
                sb.append(Strings.fromTranslit(_text.substring(end, end = m.start()), translit.equals("tl") ? 1 : 2));
                sb.append(_text, end, end = m.end());
            }

            _text = sb.append(Strings.fromTranslit(_text.substring(end), translit.equals("tl") ? 1 : 2)).toString();
        }

        Log.LogChat(_type.name(), activeChar.getName(), _target, _text);

        final Say2 cs = new Say2(activeChar.getObjectId(), _type, activeChar.getName(), _text);

        switch (_type) {
            case TELL:
                if ((receiver == null)) {
                    Say2 cs1 = new Say2(activeChar.getObjectId(), _type, "->" + _target, _text);
                    activeChar.sendPacket(cs1);
                    return;
                }

                if (!receiver.isInBlockList(activeChar) && !receiver.isBlockAll()) {
                    if (!receiver.getMessageRefusal()) {
                        if (activeChar.antiFlood.canTell(receiver.getObjectId(), _text))
                            receiver.sendPacket(cs);

                        checkAutoRecall(activeChar, receiver);

                        Say2 cs2 = new Say2(activeChar.getObjectId(), _type, "->" + receiver.getName(), _text);
                        activeChar.sendPacket(cs2);
                    } else
                        activeChar.sendPacket(SystemMsg.THAT_PERSON_IS_IN_MESSAGE_REFUSAL_MODE);
                } else
                    activeChar.sendPacket(SystemMsg.YOU_HAVE_BEEN_BLOCKED_FROM_CHATTING_WITH_THAT_CONTACT, ActionFail.STATIC);
                break;
            case SHOUT:
                if (LOCK_SHOUT_VOICE) {
                    activeChar.sendMessage("Shout chat is disabled by admin right now.");
                    return;
                }

                if (activeChar.isCursedWeaponEquipped()) {
                    activeChar.sendPacket(SystemMsg.SHOUT_AND_TRADE_CHATTING_CANNOT_BE_USED_WHILE_POSSESSING_A_CURSED_WEAPON);
                    return;
                }
                if (activeChar.isInObserverMode()) {
                    activeChar.sendPacket(SystemMsg.YOU_CANNOT_CHAT_WHILE_IN_OBSERVATION_MODE);
                    return;
                }

                if (!activeChar.isGM() && !activeChar.antiFlood.canShout(_text)) {
                    activeChar.sendMessage("Shout chat is allowed once per 5 seconds.");
                    return;
                }

                if (Config.GLOBAL_SHOUT)
                    announce(activeChar, cs);
                else
                    shout(activeChar, cs);

                activeChar.sendPacket(cs);
                break;
            case TRADE:
                if (activeChar.isCursedWeaponEquipped()) {
                    activeChar.sendPacket(SystemMsg.SHOUT_AND_TRADE_CHATTING_CANNOT_BE_USED_WHILE_POSSESSING_A_CURSED_WEAPON);
                    return;
                }
                if (activeChar.isInObserverMode()) {
                    activeChar.sendPacket(SystemMsg.YOU_CANNOT_CHAT_WHILE_IN_OBSERVATION_MODE);
                    return;
                }

                if (!activeChar.isGM() && !activeChar.antiFlood.canTrade(_text)) {
                    activeChar.sendMessage("Trade chat is allowed once per 5 seconds.");
                    return;
                }

                if (Config.GLOBAL_TRADE_CHAT)
                    announce(activeChar, cs);
                else
                    shout(activeChar, cs);

                activeChar.sendPacket(cs);
                break;
            case ALL:
                if (activeChar.isCursedWeaponEquipped()) {
                    Say2 cs3 = new Say2(activeChar.getObjectId(), _type, activeChar.getTransformationName(), _text);

                    List<Player> list = null;

                    if (activeChar.isInObserverMode() && activeChar.getObserverRegion() != null && activeChar.getOlympiadObserveGame() != null) {
                        OlympiadGame game = activeChar.getOlympiadObserveGame();
                        list = game.getAllPlayers();
                    } else if (activeChar.isInOlympiadMode()) {
                        OlympiadGame game = activeChar.getOlympiadGame();
                        if (game != null)
                            list = game.getAllPlayers();
                    } else if (activeChar.isInFightClub()) {
                        list = activeChar.getFightClubEvent().getAllFightingPlayers();
                    } else
                        list = World.getAroundPlayers(activeChar);

                    if (list != null) {
                        final boolean isGmInvis = activeChar.isInvisible() && activeChar.getAccessLevel() > 0;
                        for (Player player : list) {
                            if (player == activeChar || player.getReflection() != activeChar.getReflection() || player.isBlockAll() || player.isInBlockList(activeChar))
                                continue;

                            // Ady - If a gm talks in all when he is invisible, only other gms will be able to read him
                            if (isGmInvis && player.getAccessLevel() < 1)
                                continue;

                            player.sendPacket(cs3);
                        }
                    }

                    activeChar.sendPacket(cs3);
                }
                break;
            case CLAN:
                if (activeChar.getClan() != null)
                    activeChar.getClan().broadcastToOnlineMembers(cs);
                break;
            case ALLIANCE:
                if (activeChar.getClan() != null && activeChar.getClan().getAlliance() != null)
                    activeChar.getClan().getAlliance().broadcastToOnlineMembers(cs);
                break;
            case PARTY:
                if (activeChar.isInParty())
                    activeChar.getParty().sendPacket(cs);
                break;
            case PARTY_ROOM:
                MatchingRoom r = activeChar.getMatchingRoom();
                if (r != null && r.getType() == MatchingRoom.PARTY_MATCHING)
                    r.sendPacket(cs);
                break;
            case COMMANDCHANNEL_ALL:
                if (!activeChar.isInParty() || !activeChar.getParty().isInCommandChannel()) {
                    activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_USE_THE_COMMAND_CHANNEL);
                    return;
                }
                if (activeChar.getParty().getCommandChannel().getLeader() == activeChar)
                    activeChar.getParty().getCommandChannel().sendPacket(cs);
                else
                    activeChar.sendPacket(SystemMsg.ONLY_THE_COMMAND_CHANNEL_CREATOR_CAN_USE_THE_RAID_LEADER_TEXT);
                break;
            case COMMANDCHANNEL_COMMANDER:
                if (!activeChar.isInParty() || !activeChar.getParty().isInCommandChannel()) {
                    activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_USE_THE_COMMAND_CHANNEL);
                    return;
                }
                if (activeChar.getParty().isLeader(activeChar))
                    activeChar.getParty().getCommandChannel().broadcastToChannelPartyLeaders(cs);
                else
                    activeChar.sendPacket(SystemMsg.ONLY_A_PARTY_LEADER_CAN_ACCESS_THE_COMMAND_CHANNEL);
                break;
            case HERO_VOICE:
                if (LOCK_HERO_VOICE) {
                    activeChar.sendMessage("Hero chat is disabled by admin right now.");
                    return;
                }

                if (activeChar.isHero() || activeChar.FakeHeroChat() || activeChar.getPlayerAccess().CanAnnounce) {
                    // The only limitation for the characters, um, let us say.
                    if (!activeChar.getPlayerAccess().CanAnnounce)
                        if (!activeChar.antiFlood.canHero(_text)) {
                            activeChar.sendMessage("Hero chat is allowed once per 10 seconds.");
                            return;
                        }
                    GameObjectsStorage.getAllPlayers().stream()
                            .filter(player -> !player.isInBlockList(activeChar))
                            .filter(player -> !player.isBlockAll())
                            .forEach(player -> player.sendPacket(cs));
                }
                break;
            case PETITION_PLAYER:
            case PETITION_GM:
                if (!PetitionManager.getInstance().isPlayerInConsultation(activeChar)) {
                    activeChar.sendPacket(new SystemMessage2(SystemMsg.YOU_ARE_CURRENTLY_NOT_IN_A_PETITION_CHAT));
                    return;
                }

                PetitionManager.getInstance().sendActivePetitionMessage(activeChar, _text);
                break;
            case BATTLEFIELD:
                if (activeChar.isInFightClub()) {
                    activeChar.getFightClubEvent().getMyTeamFightingPlayers(activeChar).forEach(player -> player.sendPacket(cs));
                    return;
                }
                if (activeChar.getBattlefieldChatId() == 0)
                    return;

                GameObjectsStorage.getAllPlayers().stream()
                        .filter(player -> !player.isInBlockList(activeChar))
                        .filter(player -> !player.isBlockAll())
                        .filter(player -> player.getBattlefieldChatId() == activeChar.getBattlefieldChatId())
                        .forEach(player -> player.sendPacket(cs));
                break;
            case MPCC_ROOM:
                MatchingRoom r2 = activeChar.getMatchingRoom();
                if (r2 != null && r2.getType() == MatchingRoom.CC_MATCHING)
                    r2.sendPacket(cs);
                break;
            default:
                _log.warn("Character " + activeChar.getName() + " used unknown chat type: " + _type.ordinal() + ".");
        }
    }
}