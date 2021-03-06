package l2trunk.gameserver.model.instances;

import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.commons.lang.StringUtils;
import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.base.BaseStats;
import l2trunk.gameserver.model.base.Experience;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.items.PetInventory;
import l2trunk.gameserver.model.items.attachment.FlagItemAttachment;
import l2trunk.gameserver.network.serverpackets.InventoryUpdate;
import l2trunk.gameserver.network.serverpackets.SocialAction;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.tables.PetDataTable;
import l2trunk.gameserver.templates.item.WeaponTemplate;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Future;

public class PetInstance extends Summon {
    private static final Logger _log = LoggerFactory.getLogger(PetInstance.class);

    private static final int DELUXE_FOOD_FOR_STRIDER = 5169;
    private final int controlItemObjId;
    public final PetInventory inventory;
    private PetData petData;
    private int _curFed;
    private Future<?> _feedTask;
    private int level;
    private boolean _respawned;
    private int lostExp;

    /**
     * Create a new pet
     */

    public PetInstance(int objectId, NpcTemplate template, Player owner, ItemInstance control) {
        this(objectId, template, owner, control, 0, 0);
    }

    /**
     * Loading an existing pet
     */
    public PetInstance(int objectId, NpcTemplate template, Player owner, ItemInstance control, int currentLevel, long exp) {
        super(objectId, template, owner);

        controlItemObjId = control.objectId();
        _exp = exp;
        level = control.getEnchantLevel();

        if (level <= 0) {
            if (template.npcId == PetDataTable.SIN_EATER_ID)
                level = owner.getLevel();
            else
                level = template.level;
            _exp = getExpForThisLevel();
        }

        int minLevel = PetDataTable.getMinLevel(template.npcId);
        if (level < minLevel)
            level = minLevel;

        if (_exp < getExpForThisLevel())
            _exp = getExpForThisLevel();

        while (_exp >= getExpForNextLevel() && level < Experience.getMaxLevel())
            level++;

        while (_exp < getExpForThisLevel() && level > minLevel)
            level--;

        if (PetDataTable.isVitaminPet(template.npcId)) {
            level = owner.getLevel();
            _exp = getExpForNextLevel();
        }

        petData = PetDataTable.INSTANCE.getInfo(template.npcId, level);
        inventory = new PetInventory(this);
    }

    public static PetInstance restore(ItemInstance control, NpcTemplate template, Player owner) {
        PetInstance pet;
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT objId, name, level, curHp, curMp, exp, sp, fed FROM pets WHERE item_obj_id=?")) {
            statement.setInt(1, control.objectId());
            ResultSet rset = statement.executeQuery();

            if (!rset.next()) {
                if (PetDataTable.isBabyPet(template.getNpcId()) || PetDataTable.isImprovedBabyPet(template.getNpcId()))
                    pet = new PetBabyInstance(IdFactory.getInstance().getNextId(), template, owner, control);
                else
                    pet = new PetInstance(IdFactory.getInstance().getNextId(), template, owner, control);
                return pet;
            }

            if (PetDataTable.isBabyPet(template.getNpcId()) || PetDataTable.isImprovedBabyPet(template.getNpcId()))
                pet = new PetBabyInstance(rset.getInt("objId"), template, owner, control, rset.getInt("level"), rset.getLong("exp"));
            else
                pet = new PetInstance(rset.getInt("objId"), template, owner, control, rset.getInt("level"), rset.getLong("exp"));

            pet.setRespawned(true);

            String name = rset.getString("name");
            pet.setName(name == null || name.isEmpty() ? template.name : name);
            pet.setFullHpMp();
            pet.setFullCp();
            pet.setSp(rset.getInt("sp"));
            pet.setCurrentFed(rset.getInt("fed"));
        } catch (SQLException e) {
            _log.error("Could not restore Pet data from item: " + control + '!', e);
            throw new RuntimeException("Could not restore Pet data from item: " + control + '!', e);
        }

        return pet;
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();

        startFeed(false);
    }

    @Override
    protected void onDespawn() {
        super.onSpawn();

        stopFeed();
    }

    public boolean tryFeedItem(ItemInstance item) {
        if (item == null)
            return false;

        boolean deluxFood = PetDataTable.isStrider(getNpcId()) && item.getItemId() == DELUXE_FOOD_FOR_STRIDER;
        if (getFoodId() != item.getItemId() && !deluxFood)
            return false;

        int newFed = Math.min(getMaxFed(), getCurrentFed() + Math.max(getMaxFed() * getAddFed() * (deluxFood ? 2 : 1) / 100, 1));
        if (getCurrentFed() != newFed)
            if (inventory.destroyItem(item, 1L, null)) {
                owner.sendPacket(new SystemMessage(SystemMessage.PET_TOOK_S1_BECAUSE_HE_WAS_HUNGRY).addItemName(item.getItemId()));
                setCurrentFed(newFed);
                sendStatusUpdate();
            }
        return true;
    }

    private boolean tryFeed() {
        ItemInstance food = inventory.getItemByItemId(getFoodId());
        if (food == null && PetDataTable.isStrider(getNpcId()))
            food = inventory.getItemByItemId(DELUXE_FOOD_FOR_STRIDER);
        return tryFeedItem(food);
    }

    @Override
    public void addExpAndSp(long addToExp, long addToSp) {
        if (PetDataTable.isVitaminPet(getNpcId()))
            return;

        _exp += addToExp;
        _sp += addToSp;

        if (_exp > getMaxExp())
            _exp = getMaxExp();

        if (addToExp > 0 || addToSp > 0)
            owner.sendPacket(new SystemMessage(SystemMessage.THE_PET_ACQUIRED_EXPERIENCE_POINTS_OF_S1).addNumber(addToExp));

        int old_level = level;

        while (_exp >= getExpForNextLevel() && level < Experience.getMaxLevel())
            level++;

        while (_exp < getExpForThisLevel() && level > getMinLevel())
            level--;

        if (old_level < level) {
            owner.sendMessage(new CustomMessage("l2trunk.gameserver.model.instances.L2PetInstance.PetLevelUp").addNumber(level));
            broadcastPacket(new SocialAction(objectId(), SocialAction.LEVEL_UP));
            setFullHpMp();
        }

        if (old_level != level) {
            updateControlItem();
            updateData();
        }

        if (addToExp > 0 || addToSp > 0)
            sendStatusUpdate();
    }

    @Override
    public boolean consumeItem(int itemConsumeId, long itemCount) {
        return inventory.destroyItemByItemId(itemConsumeId, itemCount, "Consume");
    }

    private void deathPenalty() {
        if (isInZoneBattle())
            return;
        int lvl = getLevel();
        double percentLost = -0.07 * lvl + 6.5;
        // Calculate the Experience loss
        lostExp = (int) Math.round((getExpForNextLevel() - getExpForThisLevel()) * percentLost / 100);
        addExpAndSp(-lostExp, 0);
    }

    /**
     * Remove the Pet from DB and its associated item from the getPlayer inventory
     */
    private void destroyControlItem() {
        if (getControlItemObjId() == 0)
            return;
        if (!owner.getInventory().destroyItemByObjectId(getControlItemObjId(), 1L, "Destroy"))
            return;

        // pet control item no longer exists, delete the pet from the database
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("DELETE FROM pets WHERE item_obj_id=?")) {
            statement.setInt(1, getControlItemObjId());
            statement.execute();
        } catch (SQLException e) {
            _log.error("could not delete pet:", e);
        }
    }

    @Override
    protected void onDeath(Creature killer) {
        super.onDeath(killer);

        owner.sendPacket(Msg.THE_PET_HAS_BEEN_KILLED_IF_YOU_DO_NOT_RESURRECT_IT_WITHIN_24_HOURS_THE_PETS_BODY_WILL_DISAPPEAR_ALONG_WITH_ALL_THE_PETS_ITEMS);
        startDecay(86400000L);

        if (PetDataTable.isVitaminPet(getNpcId()))
            return;

        stopFeed();
        deathPenalty();
    }

    @Override
    public void doPickupItem(ItemInstance item) {
        stopMove();

        if (item.isCursed()) {
            owner.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_FAILED_TO_PICK_UP_S1).addItemName(item.getItemId()));
            return;
        }

        synchronized (item) {
            if (!item.isVisible())
                return;

            if (item.isHerb()) {
                item.getTemplate().getAttachedSkills().stream()
                        .mapToInt(s -> s.id)
                        .forEach(skill ->
                                altUseSkill(skill, this));
                item.deleteMe();
                return;
            }

            if (!inventory.validateWeight(item)) {
                sendPacket(Msg.EXCEEDED_PET_INVENTORYS_WEIGHT_LIMIT);
                return;
            }

            if (!inventory.validateCapacity(item)) {
                sendPacket(Msg.DUE_TO_THE_VOLUME_LIMIT_OF_THE_PETS_INVENTORY_NO_MORE_ITEMS_CAN_BE_PLACED_THERE);
                return;
            }

            if (!item.getTemplate().getHandler().pickupItem(this.owner, item))
                return;

            FlagItemAttachment attachment = item.getAttachment() instanceof FlagItemAttachment ? (FlagItemAttachment) item.getAttachment() : null;
            if (attachment != null)
                return;

            item.pickupMe();
        }

        if (owner.getParty() == null || owner.getParty().getLootDistribution() == Party.ITEM_LOOTER) {
            inventory.addItem(item, "PickUp");
            sendChanges();
        } else if (item.isCursed()) {
            owner.getInventory().addItem(item, "PickUp");
            owner.sendChanges();
        } else {
            owner.getParty().distributeItem(owner, item, null);
        }

        broadcastPickUpMsg(item);
    }


    public void doRevive(double percent) {
        restoreExp(percent);
        doRevive();
    }

    @Override
    public void doRevive() {
        stopDecay();
        super.doRevive();
        startFeed(false);
        setRunning();
    }

    @Override
    public int getAccuracy() {
        return (int) calcStat(Stats.ACCURACY_COMBAT, petData.accuracy);
    }

    @Override
    public ItemInstance getActiveWeaponInstance() {
        return null;
    }

    @Override
    public WeaponTemplate getActiveWeaponItem() {
        return null;
    }

    public ItemInstance getControlItem() {
        if (owner == null)
            return null;
        int item_obj_id = getControlItemObjId();
        if (item_obj_id == 0)
            return null;
        return owner.getInventory().getItemByObjectId(item_obj_id);
    }

    @Override
    public int getControlItemObjId() {
        return controlItemObjId;
    }

    @Override
    public int getCriticalHit(Creature target, Skill skill) {
        return (int) calcStat(Stats.CRITICAL_BASE, petData.critical, target, skill);
    }

    @Override
    public int getCurrentFed() {
        return _curFed;
    }

    public void setCurrentFed(int num) {
        _curFed = Math.min(getMaxFed(), Math.max(0, num));
    }

    @Override
    public int getEvasionRate(Creature target) {
        return (int) calcStat(Stats.EVASION_RATE, petData.evasion, target, null);
    }

    @Override
    public long getExpForNextLevel() {
        return PetDataTable.INSTANCE.getInfo(getNpcId(), level + 1).exp;
    }

    @Override
    public long getExpForThisLevel() {
        return PetDataTable.INSTANCE.getInfo(getNpcId(), level).exp;
    }

    private int getFoodId() {
        return petData.getFoodId();
    }

    private int getAddFed() {
        return petData.getAddFed();
    }

    public final PetInventory getInventory() {
        return inventory;
    }

    @Override
    public long getWearedMask() {
        return inventory.getWearedMask();
    }

    @Override
    public final int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public double getLevelMod() {
        return (89. + getLevel()) / 100.0;
    }

    int getMinLevel() {
        return petData.getMinLevel();
    }

    private long getMaxExp() {
        return PetDataTable.INSTANCE.getInfo(getNpcId(), Experience.getMaxLevel() + 1).exp;
    }

    @Override
    public int getMaxFed() {
        return petData.feedMax;
    }

    @Override
    public int getMaxLoad() {
        return (int) calcStat(Stats.MAX_LOAD, petData.getMaxLoad());
    }

    @Override
    public int getInventoryLimit() {
        return Config.ALT_PET_INVENTORY_LIMIT;
    }

    @Override
    public int getMaxHp() {
        return (int) calcStat(Stats.MAX_HP, petData.hp);
    }

    @Override
    public int getMaxMp() {
        return (int) calcStat(Stats.MAX_MP, petData.mp);
    }

    @Override
    public int getPAtk(Creature target) {
        // В базе указаны параметры, уже домноженные на этот модификатор, для удобства. Поэтому вычисляем и убираем его.
        double mod = BaseStats.STR.calcBonus(this) * getLevelMod();
        return (int) calcStat(Stats.POWER_ATTACK, petData.pAtk / mod, target, null);
    }

    @Override
    public int getPDef(Creature target) {
        // В базе указаны параметры, уже домноженные на этот модификатор, для удобства. Поэтому вычисляем и убираем его.
        double mod = getLevelMod();
        return (int) calcStat(Stats.POWER_DEFENCE, petData.pDef / mod, target, null);
    }

    @Override
    public int getMAtk(Creature target, Skill skill) {
        // В базе указаны параметры, уже домноженные на этот модификатор, для удобства. Поэтому вычисляем и убираем его.
        double ib = BaseStats.INT.calcBonus(this);
        double lvlb = getLevelMod();
        double mod = lvlb * lvlb * ib * ib;
        return (int) calcStat(Stats.MAGIC_ATTACK, petData.mAtk / mod, target, skill);
    }

    @Override
    public int getMDef(Creature target, Skill skill) {
        // В базе указаны параметры, уже домноженные на этот модификатор, для удобства. Поэтому вычисляем и убираем его.
        double mod = BaseStats.MEN.calcBonus(this) * getLevelMod();
        return (int) calcStat(Stats.MAGIC_DEFENCE, petData.mDef / mod, target, skill);
    }

    @Override
    public int getPAtkSpd() {
        return (int) calcStat(Stats.POWER_ATTACK_SPEED, calcStat(Stats.ATK_BASE, petData.atkSpeed));
    }

    @Override
    public int getMAtkSpd() {
        return (int) calcStat(Stats.MAGIC_ATTACK_SPEED, petData.castSpeed);
    }

    @Override
    public int getRunSpeed() {
        return getSpeed(petData.speed);
    }

    @Override
    public int getSoulshotConsumeCount() {
        return PetDataTable.getSoulshots(getNpcId());
    }

    @Override
    public int getSpiritshotConsumeCount() {
        return PetDataTable.getSpiritshots(getNpcId());
    }

    @Override
    public ItemInstance getSecondaryWeaponInstance() {
        return null;
    }

    @Override
    public WeaponTemplate getSecondaryWeaponItem() {
        return null;
    }

    public int getSkillLevel(int skillId) {
        if (skills == null || skills.get(skillId) == null)
            return -1;
        int lvl = getLevel();
        return lvl > 70 ? 7 + (lvl - 70) / 5 : lvl / 10;
    }

    @Override
    public int getSummonType() {
        return 2;
    }

    @Override
    public NpcTemplate getTemplate() {
        return (NpcTemplate) template;
    }

    @Override
    public boolean isMountable() {
        return petData.isMountable();
    }

    public boolean isRespawned() {
        return _respawned;
    }

    private void setRespawned(boolean respawned) {
        _respawned = respawned;
    }

    private void restoreExp(double percent) {
        if (lostExp != 0) {
            addExpAndSp((long) (lostExp * percent / 100.), 0);
            lostExp = 0;
        }
    }

    @Override
    public void setSp(int sp) {
        _sp = sp;
    }

    private void startFeed(boolean battleFeed) {
        boolean first = _feedTask == null;
        stopFeed();
        if (!isDead()) {
            int feedTime;
            if (PetDataTable.isVitaminPet(getNpcId()))
                feedTime = 10000;
            else
                feedTime = Math.max(first ? 15000 : 1000, 60000 / (battleFeed ? petData.feedBattle : petData.feedNormal));
            _feedTask = ThreadPoolManager.INSTANCE.schedule(new FeedTask(), feedTime);
        }
    }

    private void stopFeed() {
        if (_feedTask != null) {
            _feedTask.cancel(false);
            _feedTask = null;
        }
    }

    public void store() {
        if (getControlItemObjId() == 0 || _exp == 0)
            return;

        PreparedStatement statement = null;
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            String req;
            if (!isRespawned())
                req = "INSERT INTO pets (name,level,curHp,curMp,exp,sp,fed,objId,item_obj_id) VALUES (?,?,?,?,?,?,?,?,?)";
            else
                req = "UPDATE pets SET name=?,level=?,curHp=?,curMp=?,exp=?,sp=?,fed=?,objId=? WHERE item_obj_id = ?";
            statement = con.prepareStatement(req);
            if (getName().length() > 13) {
                setName("");
            }
            statement.setString(1, getName().equalsIgnoreCase(getTemplate().name) ? "" : getName());
            statement.setInt(2, level);
            statement.setDouble(3, getCurrentHp());
            statement.setDouble(4, getCurrentMp());
            statement.setLong(5, _exp);
            statement.setLong(6, _sp);
            statement.setInt(7, _curFed);
            statement.setInt(8, objectId());
            statement.setInt(9, controlItemObjId);
            statement.executeUpdate();
        } catch (SQLException e) {
            _log.error("Could not store pet data!", e);
        }
        _respawned = true;
    }

    @Override
    protected void onDecay() {
        inventory.store();
        destroyControlItem(); // this should also delete the pet from the db

        super.onDecay();
    }

    @Override
    public void unSummon() {
        stopFeed();
        inventory.store();
        store();

        super.unSummon();
    }

    public void updateControlItem() {
        ItemInstance controlItem = getControlItem();
        if (controlItem == null)
            return;
        controlItem.setEnchantLevel(level);
        controlItem.setCustomType2(isDefaultName() ? 0 : 1);
        controlItem.setJdbcState(JdbcEntityState.UPDATED);
        controlItem.update();
        owner.sendPacket(new InventoryUpdate().addModifiedItem(controlItem));
    }

    private void updateData() {
        petData = PetDataTable.INSTANCE.getInfo(getTemplate().npcId, level);
    }

    @Override
    public double getExpPenalty() {
        return PetDataTable.getExpPenalty(getTemplate().npcId);
    }

    @Override
    public void displayGiveDamageMessage(Creature target, int damage, boolean crit, boolean miss, boolean shld, boolean magic) {
        if (crit)
            owner.sendPacket(SystemMsg.SUMMONED_MONSTERS_CRITICAL_HIT);
        if (miss)
            owner.sendPacket(new SystemMessage(SystemMessage.C1S_ATTACK_WENT_ASTRAY).addName(this));
        else
            owner.sendPacket(new SystemMessage(SystemMessage.THE_PET_GAVE_DAMAGE_OF_S1).addNumber(damage));
    }

    @Override
    public void displayReceiveDamageMessage(Creature attacker, int damage) {
        if (!isDead()) {
            SystemMessage sm = new SystemMessage(SystemMessage.THE_PET_RECEIVED_DAMAGE_OF_S2_CAUSED_BY_S1);
            if (attacker instanceof NpcInstance)
                sm.addNpcName(((NpcInstance) attacker).getTemplate().npcId);
            else
                sm.addString(attacker.getName());
            sm.addNumber((long) damage);
            owner.sendPacket(sm);
        }
    }

    @Override
    public int getFormId() {
        switch (getNpcId()) {
            case PetDataTable.GREAT_WOLF_ID:
            case PetDataTable.WGREAT_WOLF_ID:
            case PetDataTable.FENRIR_WOLF_ID:
            case PetDataTable.WFENRIR_WOLF_ID:
                if (getLevel() >= 70)
                    return 3;
                else if (getLevel() >= 65)
                    return 2;
                else if (getLevel() >= 60)
                    return 1;
                break;
        }
        return 0;
    }

    public boolean isDefaultName() {
        return StringUtils.isEmpty(name) || getName().equalsIgnoreCase(getTemplate().name);
    }

    @Override
    public int getEffectIdentifier() {
        return 0;//TODO [VISTALL] objectId if buffs pets saved
    }

    public void changeTemplate(int npcId) {
        NpcTemplate template = NpcHolder.getTemplate(npcId);
        if (template == null) {
            throw new NullPointerException("Not find npc: " + npcId);
        }
        changeTemplate(template);
    }

    private void changeTemplate(NpcTemplate template) {
        this.template = template;
    }

    class FeedTask extends RunnableImpl {
        @Override
        public void runImpl() {
            while (getCurrentFed() <= 0.55 * getMaxFed() && tryFeed()) {
            }

            if (PetDataTable.isVitaminPet(getNpcId()) && getCurrentFed() <= 0)
                deleteMe();
            else if (getCurrentFed() <= 0.10 * getMaxFed()) {
                // If the food is over, withdraw pet
                owner.sendMessage(new CustomMessage("l2trunk.gameserver.model.instances.L2PetInstance.UnSummonHungryPet"));
                unSummon();
                return;
            }

            setCurrentFed(getCurrentFed() - 5);

            sendStatusUpdate();
            startFeed(isInCombat());
        }
    }
}