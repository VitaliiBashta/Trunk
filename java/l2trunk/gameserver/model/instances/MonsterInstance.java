package l2trunk.gameserver.model.instances;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.instancemanager.CursedWeaponsManager;
import l2trunk.gameserver.model.AggroList.HateInfo;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.base.Experience;
import l2trunk.gameserver.model.base.TeamType;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestEventType;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.model.reward.RewardItem;
import l2trunk.gameserver.model.reward.RewardList;
import l2trunk.gameserver.model.reward.RewardType;
import l2trunk.gameserver.network.serverpackets.SocialAction;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.templates.npc.Faction;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class MonsterInstance extends NpcInstance {
    private static final double MIN_DISTANCE_FOR_USE_UD = 200.0;
    private static final double MIN_DISTANCE_FOR_CANCEL_UD = 50.0;
    private static final double UD_USE_CHANCE = 30.0;
    private final MinionList minionList;
    private final Lock harvestLock = new ReentrantLock();
    private final Lock absorbLock = new ReentrantLock();
    private final Lock sweepLock = new ReentrantLock();
    private ScheduledFuture<?> minionMaintainTask;
    /**
     * crops
     */
    private boolean isSeeded;
    private int _seederId;
    private boolean _altSeed;
    private RewardItem _harvestItem;
    private int overhitAttackerId;
    /**
     * Stores the extra (over-hit) damage done to the L2NpcInstance when the attacker uses an over-hit enabled skill
     */
    private double _overhitDamage;
    /**
     * The table containing all players objectID that successfully absorbed the soul of this L2NpcInstance
     */
    private Set<Integer> _absorbersIds;
    /**
     * True if a Dwarf has used Spoil on this L2NpcInstance
     */
    private boolean isSpoiled;
    private int spoilerId;
    /**
     * Table containing all Items that a Dwarf can Sweep on this L2NpcInstance
     */
    private List<RewardItem> _sweepItems;
    private int isChampion;

    public MonsterInstance(int objectId, NpcTemplate template) {
        super(objectId, template);

        minionList = new MinionList(this);
    }

    @Override
    public boolean isMovementDisabled() {
        // Невозможность ходить для этих мобов
        return getNpcId() == 18344 || getNpcId() == 18345 || super.isMovementDisabled();
    }

    @Override
    public boolean isLethalImmune() {
        return isChampion > 0 || getNpcId() == 22215 || getNpcId() == 22216 || getNpcId() == 22217 || super.isLethalImmune();
    }

    @Override
    public boolean isFearImmune() {
        return isChampion > 0 || super.isFearImmune();
    }

    @Override
    public boolean isParalyzeImmune() {
        return isChampion > 0 || super.isParalyzeImmune();
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        return !(attacker instanceof MonsterInstance);
    }

    public int getChampion() {
        return isChampion;
    }

    public boolean isChampion() {
        return getChampion() > 0;
    }

    public void setChampion(int level) {
        if (level == 0) {
            removeSkill(4407);
            isChampion = 0;
        } else {
            addSkill(4407, level);
            isChampion = level;
        }
    }

    public void setChampion() {
        if (getReflection().canChampions() && canChampion()) {
            float random = Rnd.nextFloat();
            if (Config.ALT_CHAMPION_CHANCE2 / 100. >= random)
                setChampion(2);
            else if ((Config.ALT_CHAMPION_CHANCE1 + Config.ALT_CHAMPION_CHANCE2) / 100. >= random)
                setChampion(1);
            else
                setChampion(0);
        } else
            setChampion(0);
    }

    protected boolean canChampion() {
        return getTemplate().rewardExp > 0 && getTemplate().level <= Config.ALT_CHAMPION_TOP_LEVEL && getTemplate().level >= Config.ALT_CHAMPION_MIN_LEVEL;
    }

    @Override
    public TeamType getTeam() {
        return getChampion() == 2 ? TeamType.RED : getChampion() == 1 ? TeamType.BLUE : TeamType.NONE;
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();
        setFullHpMp();

        if (getMinionList().hasMinions()) {
            if (minionMaintainTask != null) {
                minionMaintainTask.cancel(false);
                minionMaintainTask = null;
            }
            minionMaintainTask = ThreadPoolManager.INSTANCE.schedule(() -> {
                if (isDead()) return;
                getMinionList().spawnMinions();
            }, 1000L);
        }

        // Ady - Custom respawn message for certain mobs/raids
        switch (getNpcId()) {
            case 25725: // Drake Lord
            case 25726: // Behemoth Leader
            case 25727: // Dragon Beast
                Announcements.INSTANCE.announceToAll(getName() + " has respawned", ChatType.COMMANDCHANNEL_COMMANDER);
                break;
        }
    }

    @Override
    protected void onDespawn() {
        setOverhitDamage(0);
        setOverhitAttacker(null);
        clearSweep();
        clearHarvest();
        clearAbsorbers();

        super.onDespawn();
    }

    @Override
    public final MinionList getMinionList() {
        return minionList;
    }

    public Location getMinionPosition() {
        return Location.findPointToStay(this, 100, 150);
    }

    void notifyMinionDied(MinionInstance minion) {

    }

    public void spawnMinion(MonsterInstance minion) {
        minion.setReflection(getReflection());
        if (getChampion() == 2)
            minion.setChampion(1);
        else
            minion.setChampion(0);
        minion.setHeading(getHeading());
        minion.setFullHpMp();
        minion.spawnMe(getMinionPosition());
    }

    @Override
    public boolean hasMinions() {
        return getMinionList().hasMinions();
    }

    @Override
    public MonsterInstance setReflection(Reflection reflection) {
        super.setReflection(reflection);

        if (hasMinions())
            getMinionList().getAliveMinions().forEach(m ->
                    m.setReflection(reflection));
        return this;
    }

    @Override
    protected void onDelete() {
        if (minionMaintainTask != null) {
            minionMaintainTask.cancel(false);
            minionMaintainTask = null;
        }

        getMinionList().deleteMinions();

        super.onDelete();
    }

    @Override
    protected void onDeath(Creature killer) {
        if (minionMaintainTask != null) {
            minionMaintainTask.cancel(false);
            minionMaintainTask = null;
        }

        calculateRewards(killer);

        super.onDeath(killer);
    }

    @Override
    protected void onReduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp) {
        if (skill != null && skill.isOverhit) {
            // Calculate the over-hit damage
            // Ex: mob had 10 HP left, over-hit skill did 50 damage total, over-hit damage is 40
            double overhitDmg = (getCurrentHp() - damage) * -1;
            if (overhitDmg <= 0) {
                setOverhitDamage(0);
                setOverhitAttacker(null);
            } else {
                setOverhitDamage(overhitDmg);
                setOverhitAttacker(attacker);
            }
        }

        // Alexander - We set that this getPlayer hit a monster. Used in the catpcha system to see if its fighting with mobs
        if (attacker instanceof Playable)
            attacker.getPlayer().setLastMonsterDamageTime();

        super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp);
    }

    public void calculateRewards(Creature lastAttacker) {
        Creature topDamager = getAggroList().getTopDamager();
        if ((!(lastAttacker instanceof Playable)) && getNpcId() != 22399)//Hardcoded Greater Evil
            lastAttacker = topDamager;

        if (!(lastAttacker instanceof Playable))
            return;

        Player killer = lastAttacker.getPlayer();
        if (killer == null)
            return;

        Map<Playable, HateInfo> aggroMap = getAggroList().getPlayableMap();

        Set<Quest> quests = getTemplate().getEventQuests(QuestEventType.MOB_KILLED_WITH_QUEST);
        if (!quests.isEmpty()) {
            List<Player> players = null; // массив с игроками, которые могут быть заинтересованы в квестах
            if (isRaid() && Config.ALT_NO_LASTHIT) { // Для альта на ластхит берем всех игроков вокруг
                players = new ArrayList<>();
                for (Playable pl : aggroMap.keySet())
                    if (!pl.isDead() && (isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE) || killer.isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE)))
                        if (!players.contains(pl.getPlayer())) // не добавляем дважды если есть пет
                            players.add(pl.getPlayer());
            } else if (killer.getParty() != null) {// если пати то собираем всех кто подходит
                players = killer.getParty().getMembersStream()
                        .filter(pl -> !pl.isDead())
                        .filter(pl -> isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE)
                                || killer.isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE))
                        .collect(Collectors.toList());

            }

            for (Quest quest : quests) {
                Player toReward = killer;
                if (quest.getParty() != Quest.PARTY_NONE && players != null)
                    if (isRaid() || quest.getParty() == Quest.PARTY_ALL) { // если цель рейд или квест для всей пати награждаем всех участников
                        for (Player pl : players) {
                            QuestState qs = pl.getQuestState(quest);
                            if (qs != null && !qs.isCompleted())
                                quest.notifyKill(this, qs);
                        }
                        toReward = null;
                    } else { // иначе выбираем одного
                        List<Player> interested = new ArrayList<>(players.size());
                        for (Player pl : players) {
                            QuestState qs = pl.getQuestState(quest);
                            if (qs != null && !qs.isCompleted()) // из тех, у кого взят квест
                                interested.add(pl);
                        }

                        if (interested.isEmpty())
                            continue;

                        toReward = Rnd.get(interested);
                        if (toReward == null)
                            toReward = killer;
                    }

                if (toReward != null) {
                    QuestState qs = toReward.getQuestState(quest);
                    if (qs != null && !qs.isCompleted())
                        quest.notifyKill(this, qs);
                }
            }
        }

        Map<Player, RewardInfo> rewards = new HashMap<>();
        for (HateInfo info : aggroMap.values()) {
            if (info.damage <= 1)
                continue;
            Playable attacker = (Playable) info.attacker;
            Player player = attacker.getPlayer();
            RewardInfo reward = rewards.get(player);
            if (reward == null)
                rewards.put(player, new RewardInfo(player, info.damage));
            else
                reward.addDamage(info.damage);
        }

        double[] xpsp = new double[2];

        for (Player attacker : rewards.keySet()) {
            if (attacker.isDead())
                continue;

            RewardInfo reward = rewards.get(attacker);

            if (reward == null)
                continue;

            Party party = attacker.getParty();
            int maxHp = getMaxHp();

            xpsp[0] = 0.;
            xpsp[1] = 0.;

            if (party == null) {
                int damage = Math.min(reward.dmg, maxHp);
                if (damage > 0) {
                    if (isInRangeZ(attacker, Config.ALT_PARTY_DISTRIBUTION_RANGE))
                        xpsp = calculateExpAndSp(attacker.getLevel(), damage);

                    xpsp[0] = applyOverhit(killer, xpsp[0]);

                    attacker.addExpAndCheckBonus(this, (long) xpsp[0], (long) xpsp[1], 1.);
                }
                rewards.remove(attacker);
            } else {
                int partyDmg = 0;
                int partylevel = 1;
                List<Player> rewardedMembers = new ArrayList<>();
                for (Player partyMember : party.getMembers()) {
                    RewardInfo ai = rewards.remove(partyMember);
                    if (!partyMember.isDead() && isInRangeZ(partyMember, Config.ALT_PARTY_DISTRIBUTION_RANGE)) {
                        if (ai != null)
                            partyDmg += ai.dmg;

                        rewardedMembers.add(partyMember);
                        if (partyMember.getLevel() > partylevel)
                            partylevel = partyMember.getLevel();
                    }
                }
                partyDmg = Math.min(partyDmg, maxHp);
                if (partyDmg > 0) {
                    xpsp = calculateExpAndSp(partylevel, partyDmg);
                    double partyMul = (double) partyDmg / maxHp;
                    xpsp[0] *= partyMul;
                    xpsp[1] *= partyMul;
                    xpsp[0] = applyOverhit(killer, xpsp[0]);
                    party.distributeXpAndSp(xpsp[0], xpsp[1], rewardedMembers, lastAttacker, this);
                }
            }
        }

        // Check the drop of a cursed weapon
        CursedWeaponsManager.INSTANCE.dropAttackable(this, killer);

        if (topDamager == null)
            return;

        for (Map.Entry<RewardType, RewardList> entry : getTemplate().getRewards().entrySet())
            rollRewards(entry, lastAttacker, topDamager);
    }

    @Override
    public void onRandomAnimation() {
        if (System.currentTimeMillis() - _lastSocialAction > 10000L) {
            broadcastPacket(new SocialAction(objectId(), 1));
            _lastSocialAction = System.currentTimeMillis();
        }
    }

    @Override
    public void startRandomAnimation() {
        //У мобов анимация обрабатывается в AI
    }

    public void addAbsorber(final Player attacker) {
        // The attacker must not be null
        if (attacker == null)
            return;

        if (getCurrentHpPercents() > 50)
            return;

        absorbLock.lock();
        try {
            if (_absorbersIds == null)
                _absorbersIds = new HashSet<>();

            _absorbersIds.add(attacker.objectId());
        } finally {
            absorbLock.unlock();
        }
    }

    public boolean isAbsorbed(Player player) {
        absorbLock.lock();
        try {
            if (_absorbersIds == null)
                return false;
            if (!_absorbersIds.contains(player.objectId()))
                return false;
        } finally {
            absorbLock.unlock();
        }
        return true;
    }

    private void clearAbsorbers() {
        absorbLock.lock();
        try {
            if (_absorbersIds != null)
                _absorbersIds.clear();
        } finally {
            absorbLock.unlock();
        }
    }

    public RewardItem takeHarvest() {
        harvestLock.lock();
        try {
            RewardItem harvest;
            harvest = _harvestItem;
            clearHarvest();
            return harvest;
        } finally {
            harvestLock.unlock();
        }
    }

    public void clearHarvest() {
        harvestLock.lock();
        try {
            _harvestItem = null;
            _altSeed = false;
            _seederId = 0;
            isSeeded = false;
        } finally {
            harvestLock.unlock();
        }
    }

    public boolean setSeeded(Player player, int seedId, boolean altSeed) {
        harvestLock.lock();
        try {
            if (isSeeded())
                return false;
            isSeeded = true;
            _altSeed = altSeed;
            _seederId = player.objectId();
            _harvestItem = new RewardItem(Manor.INSTANCE.getCropType(seedId));
            // Количество всходов от xHP до (xHP + xHP/2)
            if (getTemplate().rateHp > 1)
                _harvestItem.count = Rnd.get(Math.round(getTemplate().rateHp), Math.round(1.5 * getTemplate().rateHp));
        } finally {
            harvestLock.unlock();
        }

        return true;
    }

    public boolean isSeeded(Player player) {
        //засиден этим игроком, и смерть наступила не более 20 секунд назад
        return isSeeded() && _seederId == player.objectId() && getDeadTime() < 20000L;
    }

    public final boolean isSeeded() {
        return isSeeded;
    }

    /**
     * Return True if this L2NpcInstance has drops that can be sweeped.<BR><BR>
     */
    public boolean isSpoiled() {
        return isSpoiled;
    }

    public boolean isSpoiled(Player player) {
        if (!isSpoiled()) // если не заспойлен то false
            return false;

        //заспойлен этим игроком, и смерть наступила не более 20 секунд назад
        if (getDeadTime() < 15000L) {
            return player.objectId() == spoilerId;
        } else
            return true;
    }

    /**
     * Set the spoil state of this L2NpcInstance.<BR><BR>
     */
    public boolean setSpoiled(Player player) {
        sweepLock.lock();
        try {
            if (isSpoiled())
                return false;
            isSpoiled = true;
            spoilerId = player.objectId();
        } finally {
            sweepLock.unlock();
        }
        return true;
    }

    /**
     * Return True if a Dwarf use Sweep on the L2NpcInstance and if item can be spoiled.<BR><BR>
     */
    public boolean isSweepActive() {
        sweepLock.lock();
        try {
            return _sweepItems != null && _sweepItems.size() > 0;
        } finally {
            sweepLock.unlock();
        }
    }

    public List<RewardItem> takeSweep() {
        sweepLock.lock();
        try {
            List<RewardItem> sweep = _sweepItems;
            clearSweep();
            return sweep;
        } finally {
            sweepLock.unlock();
        }
    }

    private void clearSweep() {
        sweepLock.lock();
        try {
            isSpoiled = false;
            spoilerId = 0;
            _sweepItems = null;
        } finally {
            sweepLock.unlock();
        }
    }

    void rollRewards(Map.Entry<RewardType, RewardList> entry, final Creature lastAttacker, Creature topDamager) {
        RewardType type = entry.getKey();
        RewardList list = entry.getValue();

        if (type == RewardType.SWEEP && !isSpoiled())
            return;

        final Creature activeChar = (type == RewardType.SWEEP ? lastAttacker : topDamager);
        final Player activePlayer = activeChar.getPlayer();

        if (activePlayer == null)
            return;

        final int diff = calculateLevelDiffForDrop(topDamager.getLevel());
        double mod = calcStat(Stats.REWARD_MULTIPLIER, 1., activeChar, null);
        mod *= Experience.penaltyModifier(diff, 9);

        List<RewardItem> rewardItems = list.roll(activePlayer, mod, this instanceof RaidBossInstance, isChampion());
        if (type == RewardType.SWEEP) {
            _sweepItems = rewardItems;
        } else {
            rewardItems.stream()
                    .filter(drop -> !(isSeeded() && !_altSeed && !drop.isAdena))
                    // Если в моба посеяно семя, причем не альтернативное - не давать никакого дропа, кроме адены.
                    .forEach(drop -> dropItem(activePlayer, drop.itemId, drop.count));
        }
    }

    private double[] calculateExpAndSp(int level, long damage) {
        int diff = level - getLevel();
        if (level > 77 && diff > 3 && diff <= 5) // kamael exp penalty
            diff += 3;

        double xp = getExpReward() * damage / getMaxHp();
        double sp = getSpReward() * damage / getMaxHp();

        if (diff > 5) {
            double mod = Math.pow(.83, diff - 5);
            xp *= mod;
            sp *= mod;
        }

        xp = Math.max(0., xp);
        sp = Math.max(0., sp);

        return new double[]{xp, sp};
    }

    private double applyOverhit(Player killer, double xp) {
        if (xp > 0 && killer.objectId() == overhitAttackerId) {
            int overHitExp = calculateOverhitExp(xp);
            killer.sendPacket(Msg.OVER_HIT, new SystemMessage(SystemMessage.ACQUIRED_S1_BONUS_EXPERIENCE_THROUGH_OVER_HIT).addNumber(overHitExp));
            xp += overHitExp;
        }
        return xp;
    }

    @Override
    public void setOverhitAttacker(Creature attacker) {
        overhitAttackerId = attacker == null ? 0 : attacker.objectId();
    }

    public double getOverhitDamage() {
        return _overhitDamage;
    }

    @Override
    public void setOverhitDamage(double damage) {
        _overhitDamage = damage;
    }

    private int calculateOverhitExp(final double normalExp) {
        double overhitPercentage = getOverhitDamage() * 100 / getMaxHp();
        if (overhitPercentage > 25)
            overhitPercentage = 25;
        double overhitExp = overhitPercentage / 100 * normalExp;
        setOverhitAttacker(null);
        setOverhitDamage(0);
        return (int) Math.round(overhitExp);
    }

    @Override
    public boolean isAggressive() {
        return (Config.ALT_CHAMPION_CAN_BE_AGGRO || getChampion() == 0) && super.isAggressive();
    }

    @Override
    public Faction getFaction() {
        return Config.ALT_CHAMPION_CAN_BE_SOCIAL || getChampion() == 0 ? super.getFaction() : Faction.NONE;
    }

    @Override
    public void reduceCurrentHp(double i, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage) {
        checkUD(attacker, i);
        super.reduceCurrentHp(i, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage);
    }

    private void checkUD(Creature attacker, double damage) {
        if (getTemplate().baseAtkRange > MIN_DISTANCE_FOR_USE_UD || getLevel() < 20 || getLevel() > 78 || (attacker.getLevel() - getLevel()) > 9 || (getLevel() - attacker.getLevel()) > 9)
            return;

        if (this instanceof MinionInstance || getMinionList() != null || isRaid() || this instanceof ReflectionBossInstance || this instanceof ChestInstance || getChampion() > 0)
            return;

        int skillId = 5044;
        int skillLvl = 1;
        if (getLevel() >= 41 || getLevel() <= 60)
            skillLvl = 2;
        else if (getLevel() > 60)
            skillLvl = 3;

        double distance = getDistance(attacker);
        if (distance <= MIN_DISTANCE_FOR_CANCEL_UD) {
            getEffectList().getEffectsBySkillId(skillId)
                    .forEach(Effect::exit);
        } else if (distance >= MIN_DISTANCE_FOR_USE_UD) {
            double chance = UD_USE_CHANCE / (getMaxHp() / damage);
            if (Rnd.chance(chance)) {
                SkillTable.INSTANCE.getInfo(skillId, skillLvl).getEffects(this);
            }
        }
    }

    @Override
    @Deprecated
    public Clan getClan() {
        return null;
    }

    @Override
    public boolean isInvul() {
        return invul;
    }

    static final class RewardInfo {
        private final Creature attacker;
        int dmg;

        RewardInfo(final Creature attacker, final int dmg) {
            this.attacker = attacker;
            this.dmg = dmg;
        }

        void addDamage(int dmg) {
            if (dmg < 0)
                dmg = 0;

            this.dmg += dmg;
        }

        @Override
        public int hashCode() {
            return attacker.objectId();
        }
    }

    public class MinionMaintainTask extends RunnableImpl {
        @Override
        public void runImpl() {
            if (isDead())
                return;

            getMinionList().spawnMinions();
        }
    }
}