package l2trunk.gameserver.handler.admincommands.impl;


import l2trunk.gameserver.Config;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.VillageMasterInstance;
import l2trunk.gameserver.model.items.Warehouse.WarehouseType;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.pledge.SubUnit;
import l2trunk.gameserver.model.pledge.UnitMember;
import l2trunk.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import l2trunk.gameserver.network.serverpackets.PledgeStatusChanged;
import l2trunk.gameserver.network.serverpackets.WareHouseWithdrawList;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.tables.ClanTable;
import l2trunk.gameserver.templates.item.ItemTemplate.ItemClass;
import l2trunk.gameserver.utils.Util;

import java.util.StringTokenizer;

/**
 * Pledge Manipulation //pledge <create|setlevel|resetcreate|resetwait|addrep|setleader>
 */
public final class AdminPledge implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
        GameObject target1 = activeChar.getTarget();
        if (activeChar.getPlayerAccess() == null || !activeChar.getPlayerAccess().CanEditPledge || !(target1 instanceof Player))
            return false;

        Player target = (Player) target1;

        if (fullString.startsWith("admin_pledge")) {
            StringTokenizer st = new StringTokenizer(fullString);
            st.nextToken();

            String action = st.nextToken(); // setlevel|resetcreate|resetwait|addrep

            switch (action) {
                case "create":
                    try {
                        if (target.getLevel() < 10) {
                            activeChar.sendPacket(SystemMsg.YOU_DO_NOT_MEET_THE_CRITERIA_IN_ORDER_TO_CREATE_A_CLAN);
                            return false;
                        }
                        String pledgeName = st.nextToken();
                        if (pledgeName.length() > 16) {
                            activeChar.sendPacket(SystemMsg.CLAN_NAMES_LENGTH_IS_INCORRECT);
                            return false;
                        }
                        if (!Util.isMatchingRegexp(pledgeName, Config.CLAN_NAME_TEMPLATE)) {
                            // clan name is not matching template
                            activeChar.sendPacket(SystemMsg.CLAN_NAME_IS_INVALID);
                            return false;
                        }

                        Clan clan = ClanTable.INSTANCE.createClan(target, pledgeName);
                        if (clan != null) {
                            target.sendPacket(clan.listAll());
                            target.sendPacket(new PledgeShowInfoUpdate(clan), SystemMsg.YOUR_CLAN_HAS_BEEN_CREATED);
                            target.updatePledgeClass();
                            target.sendUserInfo(true);
                            return true;
                        } else {
                            activeChar.sendPacket(SystemMsg.THIS_NAME_ALREADY_EXISTS);
                            return false;
                        }
                    } catch (Exception e) {
                    }
                    break;
                case "setlevel":
                    if (target.getClan() == null) {
                        activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                        return false;
                    }

                    try {
                        int level = Integer.parseInt(st.nextToken());
                        Clan clan = target.getClan();

                        activeChar.sendMessage("You set occupation " + level + " for clan " + clan.getName());
                        clan.setLevel(level);
                        clan.updateClanInDB();

				/*	if (occupation < CastleSiegeManager.getSiegeClanMinLevel())
						SiegeUtils.removeSiegeSkills(target);
					else
						SiegeUtils.addSiegeSkills(target);   */

                        if (level == 5)
                            target.sendPacket(SystemMsg.NOW_THAT_YOUR_CLAN_LEVEL_IS_ABOVE_LEVEL_5_IT_CAN_ACCUMULATE_CLAN_REPUTATION_POINTS);

                        PledgeShowInfoUpdate pu = new PledgeShowInfoUpdate(clan);
                        PledgeStatusChanged ps = new PledgeStatusChanged(clan);

                        for (Player member : clan.getOnlineMembers(0)) {
                            member.updatePledgeClass();
                            member.sendPacket(SystemMsg.YOUR_CLANS_LEVEL_HAS_INCREASED, pu, ps);
                            member.broadcastUserInfo(true);
                        }

                        return true;
                    } catch (Exception e) {
                    }
                    break;
                case "resetcreate":
                    if (target.getClan() == null) {
                        activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                        return false;
                    }
                    target.getClan().setExpelledMemberTime(0);
                    activeChar.sendMessage("The penalty for creating a clan has been lifted for " + target.getName());
                    break;
                case "resetwait":
                    target.setLeaveClanTime(0);
                    activeChar.sendMessage("The penalty for leaving a clan has been lifted for " + target.getName());
                    break;
                case "addrep":
                    try {
                        int rep = Integer.parseInt(st.nextToken());

                        if (target.getClan() == null || target.getClan().getLevel() < 5) {
                            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                            return false;
                        }
                        target.getClan().incReputation(rep, false, "admin_manual");
                        activeChar.sendMessage("Added " + rep + " clan points to clan " + target.getClan().getName() + ".");
                    } catch (NumberFormatException nfe) {
                        activeChar.sendMessage("Please specify a number of clan points to add.");
                    }
                    break;
                case "setleader": {
                    Clan clan = target.getClan();
                    if (target.getClan() == null) {
                        activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                        return false;
                    }
                    String newLeaderName = null;
                    if (st.hasMoreTokens())
                        newLeaderName = st.nextToken();
                    else
                        newLeaderName = target.getName();
                    SubUnit mainUnit = clan.getSubUnit(Clan.SUBUNIT_MAIN_CLAN);
                    UnitMember newLeader = mainUnit.getUnitMember(newLeaderName);
                    if (newLeader == null) {
                        activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                        return false;
                    }
                    VillageMasterInstance.setLeader(activeChar, clan, mainUnit, newLeader);
                    break;
                }
                case "setname": {
                    Clan clan = target.getClan();
                    if (target.getClan() == null || !st.hasMoreTokens()) {
                        activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                        return false;
                    }
                    String newClanName = st.nextToken();

                    clan.getSubUnit(Clan.SUBUNIT_MAIN_CLAN).setName(newClanName, true);
                    activeChar.sendMessage("Clan name changed!");
                    break;
                }
            }
        } else if (fullString.equalsIgnoreCase("admin_restore_cwh")) {
            if (target.getClan() == null) {
                activeChar.sendMessage("His clan is null!");
                return false;
            }
            target.getClan().restoreCWH();
            activeChar.sendMessage("CWH restored! Found " + target.getClan().getWarehouse().getSize() + " items!");
        } else if (fullString.startsWith("admin_show_cwh")) {
            Clan clan;
            if (wordList.length >= 2)
                clan = ClanTable.INSTANCE.getClanByName(wordList[1]);
            else
                clan = target.getClan();

            if (clan == null) {
                activeChar.sendMessage("Target clan is null!");
                return false;
            }

            activeChar.setWithdrawWarehouse(clan.getWarehouse());
            activeChar.setUsingWarehouseType(WarehouseType.CLAN);
            activeChar.sendPacket(new WareHouseWithdrawList(clan, ItemClass.ALL));
        }

        return false;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private enum Commands {
        admin_pledge,
        admin_restore_cwh,
        admin_show_cwh
    }
}