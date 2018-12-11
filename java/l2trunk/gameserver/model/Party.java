package l2trunk.gameserver.model;


import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.base.Experience;
import l2trunk.gameserver.model.entity.DimensionalRift;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.entity.SevenSignsFestival.DarknessFestival;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.*;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.taskmanager.LazyPrecisionTaskManager;
import l2trunk.gameserver.templates.item.ItemTemplate;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.Log;

import java.util.*;
import java.util.concurrent.*;

public final class Party implements PlayerGroup {
    public static final int MAX_SIZE = 9;

    public static final int ITEM_LOOTER = 0;
    public static final int ITEM_RANDOM = 1;
    public static final int ITEM_RANDOM_SPOIL = 2;
    public static final int ITEM_ORDER = 3;
    public static final int ITEM_ORDER_SPOIL = 4;
    private static final int[] LOOT_SYSSTRINGS = {487, 488, 798, 799, 800};
    // Ady - Static party manager
    private static final Map<Integer, Party> parties = new ConcurrentHashMap<>();
    private final List<Player> members = new CopyOnWriteArrayList<>();
    public double _rateExp;
    public double _rateSp;
    public double _rateDrop;
    public double _rateAdena;
    public double _rateSpoil;
    private int _partyLvl;
    private int _itemDistribution;
    private int _itemOrder = 0;
    private int _dimentionalRift;
    private Reflection reflection;
    private CommandChannel commandChannel;
    private ScheduledFuture<?> positionTask;
    private int _requestChangeLoot = -1;
    private long _requestChangeLootTimer = 0;
    private Set<Integer> _changeLootAnswers = null;
    private Future<?> _checkTask = null;

    /**
     * constructor ensures party has always one member - leader
     *
     * @param leader           создатель парти
     * @param itemDistribution режим распределения лута
     */
    public Party(Player leader, int itemDistribution) {
        _itemDistribution = itemDistribution;
        members.add(leader);
        _partyLvl = leader.getLevel();
        _rateExp = 1;
        _rateSp = 1;
        _rateAdena = 1;
        _rateDrop = 1;
        _rateSpoil = 1;

        // Ady - Add this party to the static manager
        parties.put(leader.getStoredId(), this);
    }

    private static void TeleportParty(List<Player> members, Location dest) {
        members.forEach(pl -> pl.teleToLocation(dest));
    }

    private static void TeleportParty(List<Player> members, Territory territory, Location dest) {
        if (!territory.isInside(dest.x, dest.y)) {
            Log.add("errors", "TeleportParty: dest is out of territory");
            Thread.dumpStack();
            return;
        }
        int base_x = members.get(0).getX();
        int base_y = members.get(0).getY();

        for (Player _member : members) {
            if (_member == null)
                continue;
            int diff_x = _member.getX() - base_x;
            int diff_y = _member.getY() - base_y;
            Location loc = new Location(dest.x + diff_x, dest.y + diff_y, dest.z);
            while (!territory.isInside(loc.x, loc.y)) {
                diff_x = loc.x - dest.x;
                diff_y = loc.y - dest.y;
                if (diff_x != 0)
                    loc.x -= diff_x / Math.abs(diff_x);
                if (diff_y != 0)
                    loc.y -= diff_y / Math.abs(diff_y);
            }
            _member.teleToLocation(loc);
        }
    }

    private static void RandomTeleportParty(List<Player> members, Territory territory) {
        for (Player member : members)
            member.teleToLocation(Territory.getRandomLoc(territory, member.getGeoIndex()));
    }

    public static Map<Integer, Party> getParties() {
        return parties;
    }


    @Override
    public List<Player> getMembers() {
        return members;
    }

    private Player getNextLooterInRange(Player player, ItemInstance item, int range) {
        synchronized (members) {
            int antiloop = members.size();
            while (--antiloop > 0) {
                int looter = _itemOrder;
                _itemOrder++;
                if (_itemOrder > (members.size() - 1)) {
                    _itemOrder = 0;
                }

                Player ret = looter < members.size() ? members.get(looter) : player;

                if ((ret != null) && ret.isInRangeZ(player, range) && ret.getInventory().validateCapacity(item) && ret.getInventory().validateWeight(item)) {
                    return ret;
                }
            }
        }
        return player;
    }

    @Override
    public boolean containsMember(Player player) {
        return members.contains(player);
    }

    public boolean isFull() {
        return size() >= MAX_SIZE;
    }

    /**
     * adds new member to party
     *
     * @param player L2Player to add
     * @return
     */
    public boolean addPartyMember(Player player) {
        Player leader = getLeader();
        if (leader == null)
            return false;

        synchronized (members) {
            if (members.isEmpty())
                return false;
            if (members.contains(player))
                return false;
            if (members.size() == MAX_SIZE)
                return false;
            members.add(player);
        }

        if (_requestChangeLoot != -1)
            finishLootRequest(false); // cancel on invite

        player.setParty(this);
        player.getListeners().onPartyInvite();

        Summon pet;
        List<L2GameServerPacket> addInfo = new ArrayList<>(4 + (members.size() * 4));
        List<L2GameServerPacket> pplayer = new ArrayList<>(20);

        // sends new member party window for all members
        // we do all actions before adding member to a list, this speeds things up a little
        pplayer.add(new PartySmallWindowAll(this, player));
        pplayer.add(new SystemMessage2(SystemMsg.YOU_HAVE_JOINED_S1S_PARTY).addName(leader));

        addInfo.add(new SystemMessage2(SystemMsg.S1_HAS_JOINED_THE_PARTY).addName(player));
        addInfo.add(new PartySpelled(player, true));
        if ((pet = player.getPet()) != null) {
            addInfo.add(new ExPartyPetWindowAdd(pet));
            addInfo.add(new PartySpelled(pet, true));
        }

        PartyMemberPosition pmp = new PartyMemberPosition();
        List<L2GameServerPacket> pmember;
        for (Player member : members) {
            if (member != player) {
                pmember = new ArrayList<>(addInfo.size() + 4);
                pmember.addAll(addInfo);
                pmember.add(new PartySmallWindowAdd(member, player));
                pmember.add(new PartyMemberPosition().add(player));
                pmember.add(RelationChanged.update(member, player, member));
                member.sendPacket(pmember);

                pplayer.add(new PartySpelled(member, true));
                if ((pet = member.getPet()) != null) {
                    pplayer.add(new PartySpelled(pet, true));
                }
                pplayer.add(RelationChanged.update(player, member, player)); // FIXME
                pmp.add(member);
            }
        }

        pplayer.add(pmp);
        // Если партия уже в СС, то вновь прибывшем посылаем пакет открытия окна СС
        if (isInCommandChannel())
            pplayer.add(ExMPCCOpen.STATIC);

        player.sendPacket(pplayer);

        startUpdatePositionTask();
        recalculatePartyData();

        if (isInReflection() && getReflection() instanceof DimensionalRift)
            ((DimensionalRift) getReflection()).partyMemberInvited();

        return true;
    }

    /**
     * Удаляет все связи
     */
    private void dissolveParty() {
        // Ady - Remove this party from the static manager
        if (getLeader() != null)
            parties.remove(getLeader().getStoredId());

        for (Player p : members) {
            p.sendPacket(PartySmallWindowDeleteAll.STATIC);
            p.setParty(null);
        }

        synchronized (members) {
            members.clear();
        }

        setDimensionalRift(null);
        setCommandChannel(null);
        stopUpdatePositionTask();

    }

    /**
     * removes player from party
     *
     * @param player L2Player to remove
     * @param kick
     * @return
     */
    public boolean removePartyMember(Player player, boolean kick) {
        boolean isLeader = isLeader(player);
        boolean dissolve;

        synchronized (members) {
            if (!members.remove(player))
                return false;
            dissolve = members.size() == 1;
        }

        player.getListeners().onPartyLeave();

        player.setParty(null);
        recalculatePartyData();

        List<L2GameServerPacket> pplayer = new ArrayList<>(4 + members.size() * 2);

        // Отсылаемы вышедшему пакет закрытия СС
        if (isInCommandChannel())
            pplayer.add(ExMPCCClose.STATIC);
        if (kick)
            pplayer.add(new SystemMessage2(SystemMsg.YOU_HAVE_BEEN_EXPELLED_FROM_THE_PARTY));
        else
            pplayer.add(new SystemMessage2(SystemMsg.YOU_HAVE_WITHDRAWN_FROM_THE_PARTY));
        pplayer.add(PartySmallWindowDeleteAll.STATIC);

        Summon pet;
        List<L2GameServerPacket> outsInfo = new ArrayList<>(3);
        if ((pet = player.getPet()) != null)
            outsInfo.add(new ExPartyPetWindowDelete(pet));
        outsInfo.add(new PartySmallWindowDelete(player));
        if (kick)
            outsInfo.add(new SystemMessage2(SystemMsg.C1_WAS_EXPELLED_FROM_THE_PARTY).addName(player));
        else
            outsInfo.add(new SystemMessage2(SystemMsg.S1_HAS_LEFT_THE_PARTY).addName(player));

        List<L2GameServerPacket> pmember;
        for (Player member : members) {
            pmember = new ArrayList<>(2 + outsInfo.size());
            pmember.addAll(outsInfo);
            pmember.add(RelationChanged.update(member, player, member));
            member.sendPacket(pmember);
            pplayer.add(RelationChanged.update(player, member, player));
        }

        player.sendPacket(pplayer);

        Reflection reflection = getReflection();

        if (reflection instanceof DarknessFestival)
            ((DarknessFestival) reflection).partyMemberExited();
        else if (isInReflection() && getReflection() instanceof DimensionalRift)
            ((DimensionalRift) getReflection()).partyMemberExited(player);
        if (reflection != null && player.getReflection() == reflection && reflection.getReturnLoc() != null)
            player.teleToLocation(reflection.getReturnLoc(), ReflectionManager.DEFAULT);

        Player leader = getLeader();

        if (dissolve) {
            synchronized (this) {
                // Если в партии остался 1 человек, то удаляем ее из СС
                if (isInCommandChannel())
                    commandChannel.removeParty(this);
                else if (reflection != null) {
                    //lastMember.teleToLocation(getReflection().getReturnLoc(), 0);
                    //getReflection().stopCollapseTimer();
                    //getReflection().collapse();
                    if (reflection.getInstancedZone() != null && reflection.getInstancedZone().isCollapseOnPartyDismiss()) {
                        //if (reflection.getPlayerGroup() == this) // TODO: убрать затычку
                        reflection.startCollapseTimer(reflection.getInstancedZone().getTimerOnCollapse() * 1000);
                        if (leader != null && leader.getReflection() == reflection)
                            leader.broadcastPacket(new SystemMessage2(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addInteger(1));
                    }
                }

                dissolveParty();
            }
        } else {
            if (isInCommandChannel() && commandChannel.isLeader(player))
                commandChannel.setChannelLeader(leader);

            if (isLeader)
                updateLeaderInfo();
        }

        if (_checkTask != null) {
            _checkTask.cancel(true);
            _checkTask = null;
        }

        return true;
    }

    public boolean changePartyLeader(Player player) {
        Player leader = getLeader();

        // Меняем местами нового и текущего лидера
        synchronized (members) {
            int index = members.indexOf(player);
            if (index == -1)
                return false;
            members.set(0, player);
            members.set(index, leader);
        }

        updateLeaderInfo();

        if (isInCommandChannel() && commandChannel.isLeader(leader))
            commandChannel.setChannelLeader(player);

        return true;
    }

    private void updateLeaderInfo() {
        Player leader = getLeader();
        if (leader == null) // некрасиво, но иначе NPE.
            return;

        SystemMessage2 msg = new SystemMessage2(SystemMsg.C1_HAS_BECOME_THE_PARTY_LEADER).addName(leader);

        for (Player member : members) {
            // индивидуальные пакеты - удаления и инициализация пати
            member.sendPacket(PartySmallWindowDeleteAll.STATIC, // Удаляем все окошки
                    new PartySmallWindowAll(this, member), // Показываем окошки
                    msg); // Сообщаем о смене лидера
        }

        // броадкасты состояний
        for (Player member : members) {
            sendPacket(member, new PartySpelled(member, true)); // Показываем иконки
            if (member.getPet() != null)
                this.sendPacket(new ExPartyPetWindowAdd(member.getPet())); // Показываем окошки петов
            // broadcastToPartyMembers(member, new PartyMemberPosition(member)); // Обновляем позицию на карте
        }
    }

    /**
     * distribute item(s) to party members
     *
     * @param player
     * @param item
     * @param fromNpc
     */
    public void distributeItem(Player player, ItemInstance item, NpcInstance fromNpc) {
        switch (item.getItemId()) {
            case ItemTemplate.ITEM_ID_ADENA:
                distributeAdena(player, item, fromNpc);
                break;
            default:
                Player target = null;

                List<Player> ret = null;
                switch (_itemDistribution) {
                    case ITEM_RANDOM:
                    case ITEM_RANDOM_SPOIL:
                        ret = new ArrayList<>(members.size());
                        for (Player member : members)
                            if (member.isInRangeZ(player, Config.ALT_PARTY_DISTRIBUTION_RANGE) && !member.isDead() && member.getInventory().validateCapacity(item) && member.getInventory().validateWeight(item))
                                ret.add(member);

                        target = ret.isEmpty() ? null : ret.get(Rnd.get(ret.size()));
                        break;
                    case ITEM_ORDER:
                    case ITEM_ORDER_SPOIL:
                        synchronized (members) {
                            ret = new CopyOnWriteArrayList<>(members);
                            while (target == null && !ret.isEmpty()) {
                                int looter = _itemOrder;
                                _itemOrder++;
                                if (_itemOrder > ret.size() - 1)
                                    _itemOrder = 0;

                                Player looterPlayer = looter < ret.size() ? ret.get(looter) : null;

                                if (looterPlayer != null) {
                                    if (!looterPlayer.isDead() && looterPlayer.isInRangeZ(player, Config.ALT_PARTY_DISTRIBUTION_RANGE) && ItemFunctions.canAddItem(looterPlayer, item))
                                        target = looterPlayer;
                                    else
                                        ret.remove(looterPlayer);
                                }
                            }
                        }

                        if (target == null)
                            return;
                        break;
                    case ITEM_LOOTER:
                    default:
                        target = player;
                        break;
                }

                if (target == null)
                    target = player;

                if (target.pickupItem(item, fromNpc == null ? "NULL NPC" : fromNpc.toString())) {
                    if (fromNpc == null)
                        player.broadcastPacket(new GetItem(item, player.getObjectId()));

                    player.broadcastPickUpMsg(item);
                    item.pickupMe();

                    sendPacket(target, SystemMessage2.obtainItemsBy(item, target));
                } else
                    item.dropToTheGround(player, fromNpc);
                break;
        }

    }

    private void distributeAdena(Player player, ItemInstance item, NpcInstance fromNpc) {
        if (player == null) {
            return;
        }

        List<Player> membersInRange = new ArrayList<>();

        if (item.getCount() < members.size()) {
            membersInRange.add(player);
        } else {
            for (Player member : members) {
                if (((member == player) || player.isInRangeZ(member, Config.ALT_PARTY_DISTRIBUTION_RANGE)) && ItemFunctions.canAddItem(player, item)) {
                    membersInRange.add(member);
                }
            }
        }

        if (membersInRange.isEmpty()) {
            membersInRange.add(player);
        }

        long totalAdena = item.getCount();
        long amount = totalAdena / membersInRange.size();
        long ost = totalAdena % membersInRange.size();

        for (Player member : membersInRange) {
            long count = member.equals(player) ? amount + ost : amount;
            member.getInventory().addAdena(count, "party");
            member.sendPacket(SystemMessage2.obtainItems(ItemTemplate.ITEM_ID_ADENA, count, 0));
        }

        if (fromNpc == null)
            player.broadcastPacket(new GetItem(item, player.getObjectId()));

        item.pickupMe();
    }

    public void distributeXpAndSp(double xpReward, double spReward, List<Player> rewardedMembers, Creature lastAttacker, MonsterInstance monster) {
        recalculatePartyData();

        List<Player> mtr = new ArrayList<>();
        int partyLevel = lastAttacker.getLevel();
        int partyLvlSum = 0;

        // consider the minimum / maximum
        for (Player member : rewardedMembers) {
            if (!monster.isInRangeZ(member, Config.ALT_PARTY_DISTRIBUTION_RANGE)) {
                continue;
            }
            partyLevel = Math.max(partyLevel, member.getLevel());
        }

        // make a list of players that satisfy
        for (Player member : rewardedMembers) {
            if (!monster.isInRangeZ(member, Config.ALT_PARTY_DISTRIBUTION_RANGE)) {
                continue;
            }
            if (member.getLevel() <= partyLevel - 31) {
                continue;
            }
            partyLvlSum += member.getLevel();
            mtr.add(member);
        }

        if (mtr.isEmpty())
            return;

        // bonus for the party
        double bonus = Config.ALT_PARTY_BONUS.get(mtr.size() - 1);

        // of exp and sp for distribution to all
        double XP = xpReward * bonus;
        double SP = spReward * bonus;

        for (Player member : mtr) {
            double lvlPenalty = Experience.penaltyModifier(monster.calculateLevelDiffForDrop(member.getLevel()), 9);
            int lvlDiff = partyLevel - member.getLevel();
            if ((lvlDiff >= 10) && (lvlDiff <= 30)) {
                lvlPenalty *= 0.3D;
            }

            // оgive part of it with the penalty
            double memberXp = (XP * lvlPenalty * member.getLevel()) / partyLvlSum;
            double memberSp = (SP * lvlPenalty * member.getLevel()) / partyLvlSum;

            // more than solos will not give
            memberXp = Math.min(memberXp, xpReward);
            memberSp = Math.min(memberSp, spReward);

            member.addExpAndCheckBonus(monster, (long) memberXp, (long) memberSp, memberXp / xpReward);
        }

        recalculatePartyData();
    }

    public void recalculatePartyData() {
        _partyLvl = 0;
        double rateExp = 0.;
        double rateSp = 0.;
        double rateDrop = 0.;
        double rateAdena = 0.;
        double rateSpoil = 0.;
        double minRateExp = Double.MAX_VALUE;
        double minRateSp = Double.MAX_VALUE;
        double minRateDrop = Double.MAX_VALUE;
        double minRateAdena = Double.MAX_VALUE;
        double minRateSpoil = Double.MAX_VALUE;
        int count = 0;

        for (Player member : members) {
            int level = member.getLevel();
            _partyLvl = Math.max(_partyLvl, level);
            count++;

        }

        _rateExp = Config.RATE_PARTY_MIN ? minRateExp : rateExp / count;
        _rateSp = Config.RATE_PARTY_MIN ? minRateSp : rateSp / count;
        _rateDrop = Config.RATE_PARTY_MIN ? minRateDrop : rateDrop / count;
        _rateAdena = Config.RATE_PARTY_MIN ? minRateAdena : rateAdena / count;
        _rateSpoil = Config.RATE_PARTY_MIN ? minRateSpoil : rateSpoil / count;
    }

    /**
     * @return Maximum level of all party members
     */
    @Override
    public int getLevel() {
        return _partyLvl;
    }

    public int getLootDistribution() {
        return _itemDistribution;
    }

    public boolean isDistributeSpoilLoot() {
        boolean rv = false;

        if ((_itemDistribution == ITEM_RANDOM_SPOIL) || (_itemDistribution == ITEM_ORDER_SPOIL)) {
            rv = true;
        }

        return rv;
    }

    public boolean isInDimensionalRift() {
        return _dimentionalRift > 0 && getDimensionalRift() != null;
    }

    public DimensionalRift getDimensionalRift() {
        return _dimentionalRift == 0 ? null : (DimensionalRift) ReflectionManager.INSTANCE.get(_dimentionalRift);
    }

    public void setDimensionalRift(DimensionalRift dr) {
        _dimentionalRift = dr == null ? 0 : dr.getId();
    }

    public boolean isInReflection() {
        if (reflection != null)
            return true;
        if (commandChannel != null)
            return commandChannel.isInReflection();
        return false;
    }

    public Reflection getReflection() {
        if (reflection != null)
            return reflection;
        if (commandChannel != null)
            return commandChannel.getReflection();
        return null;
    }

    @Override
    public Party setReflection(Reflection reflection) {
        this.reflection = reflection;
        return this;
    }

    public boolean isInCommandChannel() {
        return commandChannel != null;
    }

    public CommandChannel getCommandChannel() {
        return commandChannel;
    }

    void setCommandChannel(CommandChannel channel) {
        commandChannel = channel;
    }

    /**
     * Телепорт всей пати в одну точку dest
     *
     * @param dest
     */
    public void teleport(Location dest) {
        TeleportParty(getMembers(), dest);
    }

    public void teleport(Territory territory) {
        RandomTeleportParty(getMembers(), territory);
    }

    private void startUpdatePositionTask() {
        if (positionTask == null)
            positionTask = LazyPrecisionTaskManager.getInstance().scheduleAtFixedRate(new UpdatePositionTask(), 1000, 1000);
    }

    private void stopUpdatePositionTask() {
        if (positionTask != null)
            positionTask.cancel(false);
    }

    public void requestLootChange(byte type) {
        if (_requestChangeLoot != -1) {
            if (System.currentTimeMillis() > _requestChangeLootTimer)
                finishLootRequest(false);
            else
                return;
        }
        _requestChangeLoot = type;
        int additionalTime = 45000; // timeout 45sec, guess
        _requestChangeLootTimer = System.currentTimeMillis() + additionalTime;
        _changeLootAnswers = new CopyOnWriteArraySet<>();
        _checkTask = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new ChangeLootCheck(), additionalTime + 1000, 5000);
        sendPacket(getLeader(), new ExAskModifyPartyLooting(getLeader().getName(), type));
        SystemMessage2 sm = new SystemMessage2(SystemMsg.REQUESTING_APPROVAL_CHANGE_PARTY_LOOT_S1);
        sm.addSysString(LOOT_SYSSTRINGS[type]);
        getLeader().sendPacket(sm);
    }

    public synchronized void answerLootChangeRequest(Player member, boolean answer) {
        if (_requestChangeLoot == -1)
            return;
        if (_changeLootAnswers.contains(member.getObjectId()))
            return;
        if (!answer) {
            finishLootRequest(false);
            return;
        }
        _changeLootAnswers.add(member.getObjectId());
        if (_changeLootAnswers.size() >= size() - 1) {
            finishLootRequest(true);
        }
    }

    private synchronized void finishLootRequest(boolean success) {
        if (_requestChangeLoot == -1)
            return;
        if (_checkTask != null) {
            _checkTask.cancel(false);
            _checkTask = null;
        }
        if (success) {
            this.sendPacket(new ExSetPartyLooting(1, _requestChangeLoot));
            _itemDistribution = _requestChangeLoot;
            SystemMessage2 sm = new SystemMessage2(SystemMsg.PARTY_LOOT_CHANGED_S1);
            sm.addSysString(LOOT_SYSSTRINGS[_requestChangeLoot]);
            this.sendPacket(sm);
        } else {
            this.sendPacket(new ExSetPartyLooting(0, (byte) 0));
            this.sendPacket(new SystemMessage2(SystemMsg.PARTY_LOOT_CHANGE_CANCELLED));
        }
        _changeLootAnswers = null;
        _requestChangeLoot = -1;
        _requestChangeLootTimer = 0;
    }

    @Override
    public Iterator<Player> iterator() {
        return members.iterator();
    }

    @Override
    public int size() {
        return members.size();
    }

    @Override
    public Player getLeader() {
        return members.get(0);
    }

    private class UpdatePositionTask extends RunnableImpl {
        @Override
        public void runImpl() {
            List<Player> update = new ArrayList<>();

            for (Player member : members) {
                Location loc = member.getLastPartyPosition();
                if (loc == null || member.getDistance(loc) > 256) //TODO подкорректировать
                {
                    member.setLastPartyPosition(member.getLoc());
                    update.add(member);
                }
            }

            if (!update.isEmpty())
                for (Player member : members) {
                    PartyMemberPosition pmp = new PartyMemberPosition();
                    for (Player m : update)
                        if (m != member)
                            pmp.add(m);
                    if (pmp.size() > 0)
                        member.sendPacket(pmp);
                }
        }
    }

    private class ChangeLootCheck extends RunnableImpl {
        @Override
        public void runImpl() {
            if (System.currentTimeMillis() > _requestChangeLootTimer) {
                finishLootRequest(false);
            }
        }
    }
}