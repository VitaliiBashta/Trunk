package l2trunk.gameserver.model.instances;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.NpcInfo;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public final class TamedBeastInstance extends FeedableBeastInstance {
    private static final int MAX_DISTANCE_FROM_OWNER = 2000;
    private static final int MAX_DISTANCE_FOR_BUFF = 200;
    private static final int MAX_DURATION = 1200000; // 20 minutes
    private static final int DURATION_CHECK_INTERVAL = 60000; // 1 minute
    private static final int DURATION_INCREASE_INTERVAL = 20000; // 20 secs
    @SuppressWarnings("unchecked")
    private static final Map.Entry<NpcString, int[]>[] TAMED_DATA = new Map.Entry[6];

    static {
        TAMED_DATA[0] = new AbstractMap.SimpleImmutableEntry<>(NpcString.RECKLESS_S1, new int[]{6671});
        TAMED_DATA[1] = new AbstractMap.SimpleImmutableEntry<>(NpcString.S1_OF_BALANCE, new int[]{6431, 6666});
        TAMED_DATA[2] = new AbstractMap.SimpleImmutableEntry<>(NpcString.SHARP_S1, new int[]{6432, 6668});
        TAMED_DATA[3] = new AbstractMap.SimpleImmutableEntry<>(NpcString.USEFUL_S1, new int[]{6433, 6670});
        TAMED_DATA[4] = new AbstractMap.SimpleImmutableEntry<>(NpcString.S1_OF_BLESSING, new int[]{6669, 6672});
        TAMED_DATA[5] = new AbstractMap.SimpleImmutableEntry<>(NpcString.SWIFT_S1, new int[]{6434, 6667});
    }

    private final List<Integer> _skills = new ArrayList<>();
    private Player player = null;
    private int foodSkillId, remainingTime = MAX_DURATION;
    private Future<?> durationCheckTask = null;

    public TamedBeastInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        hasRandomWalk = false;
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        return false;
    }

    @Override
    public void onAction(final Player player, final boolean dontMove) {
        player.setObjectTarget(this);
        //TODO [VISTALL] action shift
    }

    private void onReceiveFood() {
        // Eating food extends the duration by 20secs, to a max of 20minutes
        remainingTime = remainingTime + DURATION_INCREASE_INTERVAL;
        if (remainingTime > MAX_DURATION)
            remainingTime = MAX_DURATION;
    }

    private int getRemainingTime() {
        return remainingTime;
    }

    private void setRemainingTime(int duration) {
        remainingTime = duration;
    }

    private int getFoodType() {
        return foodSkillId;
    }

    public void setFoodType(int foodItemId) {
        if (foodItemId > 0) {
            foodSkillId = foodItemId;

            // start the duration checks start the buff tasks
            if (durationCheckTask != null)
                durationCheckTask.cancel(false);
            durationCheckTask = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new CheckDuration(this), DURATION_CHECK_INTERVAL, DURATION_CHECK_INTERVAL);
        }
    }

    public void setTameType() {
        Map.Entry<NpcString, int[]> type = Rnd.get(TAMED_DATA);

        setNameNpcString(type.getKey());
        setName("#" + getNameNpcStringByNpcId().getId());

        for (int skillId : type.getValue()) {
            int sk = skillId;
            _skills.add(sk);
        }
    }

    private NpcString getNameNpcStringByNpcId() {
        switch (getNpcId()) {
            case 18869:
                return NpcString.ALPEN_KOOKABURRA;
            case 18870:
                return NpcString.ALPEN_COUGAR;
            case 18871:
                return NpcString.ALPEN_BUFFALO;
            case 18872:
                return NpcString.ALPEN_GRENDEL;
        }
        return NpcString.NONE;
    }

    public void buffOwner() {
        if (!isInRange(player, MAX_DISTANCE_FOR_BUFF)) {
            setFollowTarget(player);
            getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, player, Config.FOLLOW_RANGE);
            return;
        }

        int delay = 0;
        for (Integer skill : _skills) {
            ThreadPoolManager.INSTANCE.schedule(new Buff(this, player, skill), delay);
            delay = delay + SkillTable.INSTANCE.getInfo(skill).hitTime + 500;
        }
    }

    @Override
    protected void onDeath(Creature killer) {
        super.onDeath(killer);
        if (durationCheckTask != null) {
            durationCheckTask.cancel(false);
            durationCheckTask = null;
        }

        Player owner = player;
        if (owner != null)
            owner.removeTrainedBeast(objectId);

        foodSkillId = 0;
        remainingTime = 0;
    }

    public void setOwner(Player owner) {
        player = owner;
        if (owner != null) {
            setTitle(owner.getName());
            owner.addTrainedBeast(this);

            World.getAroundPlayers(this)
                    .forEach(p -> p.sendPacket(new NpcInfo(this, p)));

            // always and automatically follow the owner.
            setFollowTarget(player);
            getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, owner, Config.FOLLOW_RANGE);
        } else
            doDespawn(); // despawn if no owner
    }

    public void despawnWithDelay(int delay) {
        ThreadPoolManager.INSTANCE.schedule(this::doDespawn, delay);
    }

    public void doDespawn() {
        // stop running tasks
        stopMove();

        if (durationCheckTask != null) {
            durationCheckTask.cancel(false);
            durationCheckTask = null;
        }

        // clean up variables
        Player owner = player;
        if (owner != null)
            owner.removeTrainedBeast(objectId());

        setTarget(null);
        foodSkillId = 0;
        remainingTime = 0;

        // remove the spawn
        onDecay();
    }

    public static class Buff extends RunnableImpl {
        private final NpcInstance actor;
        private final Player owner;
        private final int skillId;

        Buff(NpcInstance actor, Player owner, int skillId) {
            this.actor = actor;
            this.owner = owner;
            this.skillId = skillId;
        }

        @Override
        public void runImpl() {
            if (actor != null)
                actor.doCast(skillId, owner, true);
        }
    }

    private static class CheckDuration extends RunnableImpl {
        private final TamedBeastInstance tamedBeast;

        CheckDuration(TamedBeastInstance tamedBeast) {
            this.tamedBeast = tamedBeast;
        }

        @Override
        public void runImpl() {
            Player owner = tamedBeast.player;

            if (owner == null || !owner.isOnline()) {
                tamedBeast.doDespawn();
                return;
            }

            if (tamedBeast.getDistance(owner) > MAX_DISTANCE_FROM_OWNER) {
                tamedBeast.doDespawn();
                return;
            }

            int foodTypeSkillId = tamedBeast.getFoodType();
            tamedBeast.setRemainingTime(tamedBeast.getRemainingTime() - DURATION_CHECK_INTERVAL);

            // I tried to avoid this as much as possible...but it seems I can't avoid hardcoding
            // ids further, except by carrying an additional variable just for these two lines...
            // Find which food item needs to be consumed.
            ItemInstance item = null;
            int foodItemId = tamedBeast.getItemIdBySkillId(foodTypeSkillId);
            if (foodItemId > 0)
                item = owner.getInventory().getItemByItemId(foodItemId);

            // if the owner has enough food, call the item handler (use the food and triffer all necessary actions)
            if (item != null && item.getCount() >= 1) {
                tamedBeast.onReceiveFood();
                owner.getInventory().destroyItem(item, 1, "Tamed Beast");
            } else // if the owner has no food, the beast immediately despawns, except when it was only
                // newly spawned. Newly spawned beasts can last up to 5 minutes
                if (tamedBeast.getRemainingTime() < MAX_DURATION - 300000)
                    tamedBeast.setRemainingTime(-1);

            if (tamedBeast.getRemainingTime() <= 0)
                tamedBeast.doDespawn();
        }
    }
}