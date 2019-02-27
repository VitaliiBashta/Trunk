package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.base.InvisibleType;
import l2trunk.gameserver.network.serverpackets.Earthquake;
import l2trunk.gameserver.network.serverpackets.Say2;
import l2trunk.gameserver.network.serverpackets.SocialAction;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.network.serverpackets.components.IStaticPacket;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.skills.AbnormalEffect;
import l2trunk.gameserver.utils.Util;

import java.util.List;
import java.util.stream.Stream;

import static l2trunk.commons.lang.NumberUtils.toInt;


public final class AdminEffects implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {
        if (!activeChar.getPlayerAccess().GodMode)
            return false;

        int val, id, lvl;
        AbnormalEffect ae = AbnormalEffect.NULL;
        GameObject target = activeChar.getTarget();

        switch (comm) {
            case "admin_invis":
            case "admin_vis":

                if (activeChar.isInvisible()) {
                    activeChar.setInvisibleType(InvisibleType.NONE);
                    activeChar.stopAbnormalEffect(AbnormalEffect.STEALTH);
                    activeChar.broadcastCharInfo();
                    if (activeChar.getPet() != null) {
                        activeChar.getPet().broadcastCharInfo();
                    }
                } else {
                    activeChar.setInvisibleType(InvisibleType.EFFECT);
                    activeChar.startAbnormalEffect(AbnormalEffect.STEALTH);
                    activeChar.sendUserInfo(true);
                    if (activeChar.isGM()) {
                        World.removeObjectFromPlayers(activeChar);
                    }
                }
                break;
            case "admin_gmspeed":
                val = toInt(wordList[1], 0);
                int sh_level = activeChar.getEffectList().getEffectsBySkillId(7029).map(e -> e.skill.level).findFirst().orElse(0);

                if (val == 0) {
                    if (sh_level != 0) {
                        activeChar.doCast(7029, sh_level, activeChar, true); // снимаем еффект
                    }
                    activeChar.unsetVar("gm_gmspeed");
                } else if ((val >= 1) && (val <= 4)) {
                    if (Config.SAVE_GM_EFFECTS) {
                        activeChar.setVar("gm_gmspeed", val);
                    }
                    if (val != sh_level) {
                        if (sh_level != 0) {
                            activeChar.doCast(7029, sh_level, activeChar, true); // снимаем еффект
                        }
                        activeChar.doCast(7029, val, activeChar, true);
                    }
                } else {
                    activeChar.sendMessage("USAGE: //gmspeed value=[0 1 2 3 4]");
                }
                break;
            case "admin_invul":
                handleInvul(activeChar, activeChar);
                if (activeChar.isInvul()) {
                    if (Config.SAVE_GM_EFFECTS)
                        activeChar.setVar("gm_invul");
                } else
                    activeChar.unsetVar("gm_invul");
                break;
        }

        if (!activeChar.isGM())
            return false;

        switch (comm) {
            case "admin_earthquake":
                try {
                    int intensity = toInt(wordList[1]);
                    int duration = toInt(wordList[2]);
                    activeChar.broadcastPacket(new Earthquake(activeChar.getLoc(), intensity, duration));
                } catch (Exception e) {
                    activeChar.sendMessage("USAGE: //earthquake intensity duration");
                    return false;
                }
                break;
            case "admin_para_everybody":
            case "admin_para":
                Stream<Player> targets;
                int minutes = -1;
                String reason = null;
                if ("admin_para_everybody".equals(comm)) {
                    targets = GameObjectsStorage.getAllPlayersStream()
                            .filter(Player::isOnline)
                            .filter(p -> p.getNetConnection() != null)
                            .filter(p -> !p.isGM());

                } else if (wordList.length == 2) {
                    int radius = toInt(wordList[1]);
                    targets = World.getAroundPlayers(activeChar, radius, 500);
                } else if (target instanceof Player) {
                    targets = Stream.of((Player) target);
                    if (wordList.length >= 3) {
                        minutes = toInt(wordList[1]);
                        StringBuilder reasonBuilder = new StringBuilder();
                        for (int i = 2; i < wordList.length; i++)
                            reasonBuilder.append(wordList[i]).append(' ');
                        reason = reasonBuilder.toString();
                    }
                } else {
                    activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                    return false;
                }

                IStaticPacket packet = new Say2(activeChar.objectId(), ChatType.TELL, "Paralyze", "You are paralyzed for " + minutes + " minutes! Reason: " + reason);
                int min = minutes;
                String r = reason;
                targets.filter(c -> !c.isBlocked())
                        .forEach(c -> {
                            c.startAbnormalEffect(AbnormalEffect.HOLD_1);
                            c.abortAttack(true, false);
                            c.abortCast(true, false);
                            c.setBlock(true);

                            if (min > 0) {
                                c.setVar("Para", r, System.currentTimeMillis() + min * 60000L);
                                c.sendPacket(packet);
                            }
                        });
                activeChar.sendMessage("All Targets blocked!");
                break;
            case "admin_unpara_everybody":
            case "admin_unpara":
                if ("admin_unpara_everybody".equals(comm)) {
                    targets = GameObjectsStorage.getAllPlayersStream()
                            .filter(Player::isOnline)
                            .filter(p -> p.getNetConnection() != null)
                            .filter(p -> !p.isGM());
                } else if (wordList.length > 1) {
                    int radius = toInt(wordList[1]);
                    targets = World.getAroundPlayers(activeChar, radius, 500);
                } else if (target instanceof Player) {
                    targets = Stream.of((Player) target);
                } else {
                    activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                    return false;
                }
                targets.filter(Player::isBlocked)
                        .forEach(c -> {
                            c.setBlock();
                            c.stopAbnormalEffect(AbnormalEffect.HOLD_1);
                            c.unsetVar("Para");
                        });

                activeChar.sendMessage("Targets unblocked");
                break;
            case "admin_flag":
                Stream<Player> players;
                if (wordList.length > 1) {
                    int radius = toInt(wordList[1]);
                    players = World.getAroundPlayers(activeChar, radius, 500);
                } else if (target instanceof Player) {
                    players = Stream.of((Player) target);
                } else {
                    activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                    return false;
                }
                players.forEach(c -> c.startPvPFlag(c));

                activeChar.sendMessage("Targets flagged");
                break;
            case "admin_unflag":
                if (wordList.length > 1) {
                    int radius = toInt(wordList[1]);
                    players = World.getAroundPlayers(activeChar, radius, 500);
                } else if (target instanceof Player) {
                    players = Stream.of((Player) target);
                } else {
                    activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                    return false;
                }
                players.forEach(Player::stopPvPFlag);

                activeChar.sendMessage("Targets unflagged");
                break;
            case "admin_changename":
                if (wordList.length < 2) {
                    activeChar.sendMessage("USAGE: //changename newName");
                    return false;
                }
                if (target == null)
                    target = activeChar;
                if (target instanceof Creature) {
                    String oldName = target.getName();
                    String newName = Util.joinStrings(" ", wordList, 1);

                    ((Creature) target).setName(newName);
                    ((Creature) target).broadcastCharInfo();

                    activeChar.sendMessage("Changed name from " + oldName + " to " + newName + ".");
                    break;
                } else {
                    activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                    return false;
                }
            case "admin_setinvul":
                if (!(target instanceof Player)) {
                    activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                    return false;
                }
                handleInvul(activeChar, (Player) target);
                break;
            case "admin_getinvul":
                if (target instanceof Creature)
                    activeChar.sendMessage("Target " + target.getName() + "(object ID: " + target.objectId() + ") is " + (!((Creature) target).isInvul() ? "NOT " : "") + "invul");
                break;
            case "admin_social":
                if (wordList.length < 2)
                    val = Rnd.get(1, 7);
                else
                    try {
                        val = toInt(wordList[1]);
                    } catch (NumberFormatException nfe) {
                        activeChar.sendMessage("USAGE: //social value");
                        return false;
                    }
                if (target == null || target == activeChar)
                    activeChar.broadcastPacket(new SocialAction(activeChar.objectId(), val));
                else if (target instanceof Creature)
                    ((Creature) target).broadcastPacket(new SocialAction(target.objectId(), val));
                break;
            case "admin_abnormal":
                try {
                    if (wordList.length > 1)
                        ae = AbnormalEffect.getByName(wordList[1]);
                } catch (Exception e) {
                    activeChar.sendMessage("USAGE: //abnormal name");
                    activeChar.sendMessage("//abnormal - Clears all abnormal effects");
                    return false;
                }

                Creature effectTarget = target == null ? activeChar : (Creature) target;

                if (ae == AbnormalEffect.NULL) {
                    effectTarget.startAbnormalEffect(AbnormalEffect.NULL);
                    if (effectTarget instanceof Player)
                        ((Player) effectTarget).sendMessage("Abnormal effects clearned by admin.");
                } else {
                    effectTarget.startAbnormalEffect(ae);
                    if (effectTarget instanceof Player)
                        ((Player) effectTarget).sendMessage("Admin added abnormal effect: " + ae.getName());
                    if (effectTarget != activeChar)
                        if (effectTarget instanceof Player)
                            ((Player) effectTarget).sendMessage("Added abnormal effect: " + ae.getName());
                }
                break;
            case "admin_liston":
                activeChar.setVar("gmOnList");
                break;
            case "admin_listoff":
                activeChar.unsetVar("gmOnList");
                break;
            case "admin_transform":
                try {
                    val = toInt(wordList[1]);
                } catch (Exception e) {
                    activeChar.sendMessage("USAGE: //transform transform_id");
                    return false;
                }
                activeChar.setTransformation(val);
                break;
            case "admin_callskill":
                id = toInt(wordList[1]);
                lvl = toInt(wordList[2]);
                activeChar.doCast(id, lvl, activeChar, true);
                break;
            case "admin_showmovie":
                if (wordList.length < 2) {
                    activeChar.sendMessage("USAGE: //showmovie id");
                    return false;
                }
                id = toInt(wordList[1]);
                activeChar.showQuestMovie(id);
                break;
        }

        return true;
    }

    private void handleInvul(Player activeChar, Player target) {
        if (target.isInvul()) {
            target.setInvul(false);
            if (target.getPet() != null) {
                target.getPet().setInvul(false);
            }
            activeChar.sendMessage(target.getName() + " is now mortal!");
        } else {
            target.setInvul(true);
            if (target.getPet() != null) {
                target.getPet().setInvul(true);
            }
            activeChar.sendMessage(target.getName() + " is now immortal!");
        }
    }

    @Override
    public List<String> getAdminCommands() {
        return List.of(
                "admin_invis",
                "admin_vis",
                "admin_offline_vis",
                "admin_offline_invis",
                "admin_earthquake",
                "admin_para_everybody",
                "admin_para",
                "admin_unpara_everybody",
                "admin_unpara",
                "admin_flag",
                "admin_unflag",
                "admin_changename",
                "admin_gmspeed",
                "admin_invul",
                "admin_setinvul",
                "admin_getinvul",
                "admin_social",
                "admin_abnormal",
                "admin_transform",
                "admin_callskill",
                "admin_showmovie",
                "admin_liston",
                "admin_listoff");
    }
}