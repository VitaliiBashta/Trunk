package l2trunk.gameserver.model;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.GameTimeController;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.instancemanager.games.FishingChampionShipManager;
import l2trunk.gameserver.model.Skill.SkillType;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.network.serverpackets.*;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.templates.FishTemplate;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.gameserver.utils.Location;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public final class Fishing {
    private final static int FISHING_NONE = 0;
    private final static int FISHING_STARTED = 1;
    private final static int FISHING_WAITING = 2;
    private final static int FISHING_COMBAT = 3;
    private final Player fisher;
    private final AtomicInteger _state;
    private final Location _fishLoc = new Location();
    private int time;
    private int stop;
    private int gooduse;
    private int anim;
    private int combatMode = -1;
    private boolean deceptiveMode;
    private int fishCurHP;
    private FishTemplate fish;
    private int lureId;
    private Future<?> _fishingTask;

    public Fishing(Player fisher) {
        this.fisher = fisher;
        _state = new AtomicInteger(FISHING_NONE);
    }

    private static void showMessage(Player fisher, int dmg, int pen, SkillType skillType, int messageId) {
        switch (messageId) {
            case 1:
                if (skillType == SkillType.PUMPING) {
                    fisher.sendPacket(new SystemMessage2(SystemMsg.PUMPING_IS_SUCCESSFUL_DAMAGE_S1).addInteger(dmg));
                    if (pen == 50)
                        fisher.sendPacket(new SystemMessage2(SystemMsg.YOUR_PUMPING_WAS_SUCCESSFUL_MASTERY_PENALTYS1_).addInteger(pen));
                } else {
                    fisher.sendPacket(new SystemMessage2(SystemMsg.REELING_IS_SUCCESSFUL_DAMAGE_S1).addInteger(dmg));
                    if (pen == 50)
                        fisher.sendPacket(new SystemMessage2(SystemMsg.YOUR_REELING_WAS_SUCCESSFUL_MASTERY_PENALTYS1_).addInteger(pen));
                }
                break;
            case 2:
                if (skillType == SkillType.PUMPING)
                    fisher.sendPacket(new SystemMessage2(SystemMsg.PUMPING_FAILED_DAMAGE_S1).addInteger(dmg));
                else
                    fisher.sendPacket(new SystemMessage2(SystemMsg.REELING_FAILED_DAMAGE_S1).addInteger(dmg));
                break;
            case 3:
                if (skillType == SkillType.PUMPING) {
                    fisher.sendPacket(new SystemMessage2(SystemMsg.PUMPING_IS_SUCCESSFUL_DAMAGE_S1).addInteger(dmg));
                    if (pen == 50)
                        fisher.sendPacket(new SystemMessage2(SystemMsg.YOUR_PUMPING_WAS_SUCCESSFUL_MASTERY_PENALTYS1_).addInteger(pen));
                } else {
                    fisher.sendPacket(new SystemMessage2(SystemMsg.REELING_IS_SUCCESSFUL_DAMAGE_S1).addInteger(dmg));
                    if (pen == 50)
                        fisher.sendPacket(new SystemMessage2(SystemMsg.YOUR_REELING_WAS_SUCCESSFUL_MASTERY_PENALTYS1_).addInteger(pen));
                }
                break;
            default:
                break;
        }
    }

    private static void spawnPenaltyMonster(Player fisher) {
        int npcId = 18319 + Math.min(fisher.getLevel() / 11, 7); // 18319-18326

        MonsterInstance npc = new MonsterInstance(IdFactory.getInstance().getNextId(), NpcHolder.getTemplate(npcId));
        npc.setSpawnedLoc(Location.findPointToStay(fisher, 100, 120));
        npc.setReflection(fisher.getReflection());
        npc.setHeading(fisher.getHeading() - 32768);
        npc.spawnMe(npc.getSpawnedLoc());
        npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, fisher, Rnd.get(1, 100));
    }

    public static int getRandomFishType(int lureId) {
        int check = Rnd.get(100);
        int type;

        switch (lureId) {
            case 7807: // Green Colored Lure - For Beginners, preferred by fast-moving (nimble) fish (type 5)
                if (check <= 54)
                    type = 5;
                else if (check <= 77)
                    type = 4;
                else
                    type = 6;
                break;
            case 7808: // Purple Colored Lure - For Beginners, preferred by fat fish (type 4)
                if (check <= 54)
                    type = 4;
                else if (check <= 77)
                    type = 6;
                else
                    type = 5;
                break;
            case 7809: // Yellow Colored Lure - For Beginners, preferred by ugly fish (type 6)
                if (check <= 54)
                    type = 6;
                else if (check <= 77)
                    type = 5;
                else
                    type = 4;
                break;
            case 8486: // Prize-Winning Novice Fishing Lure
                if (check <= 33)
                    type = 4;
                else if (check <= 66)
                    type = 5;
                else
                    type = 6;
                break;
            case 7610: // Wind Fishing Lure
            case 7611: // Icy Air Fishing Lure
            case 7612: // Earth Fishing Lure
            case 7613: // Flaming Fishing Lure

            case 8496: // Gludio's Luminous Lure
            case 8497: // Dion's Luminous Lure
            case 8498: // Giran's Luminous Lure
            case 8499: // Oren's Luminous Lure
            case 8500: // Aden's Luminous Lure
            case 8501: // Innadril's Luminous Lure
            case 8502: // Goddard's Luminous Lure
            case 8503: // Rune's Luminous Lure
            case 8504: // Schuttgart's Luminous Lure
            case 8548: // Hot Springs Lure
                type = 3;
                break;
            // all theese lures (green) are prefered by fast-moving (nimble) fish (type 1)
            case 6519: // Green Colored Lure - Low Grade
            case 8505: // Green Luminous Lure - Low Grade
            case 6520: // Green Colored Lure
            case 6521: // Green Colored Lure - High Grade
            case 8507: // Green Colored Lure - High Grade
                if (check <= 54)
                    type = 1;
                else if (check <= 74)
                    type = 0;
                else if (check <= 94)
                    type = 2;
                else
                    type = 3;
                break;
            // all theese lures (purple) are prefered by fat fish (type 0)
            case 6522: // Purple Colored Lure - Low Grade
            case 6523: // Purple Colored Lure
            case 6524: // Purple Colored Lure - High Grade
            case 8508: // Purple Luminous Lure - Low Grade
            case 8510: // Purple Luminous Lure - High Grade
                if (check <= 54)
                    type = 0;
                else if (check <= 74)
                    type = 1;
                else if (check <= 94)
                    type = 2;
                else
                    type = 3;
                break;
            // all theese lures (yellow) are prefered by ugly fish (type 2)
            case 6525: // Yellow Colored Lure - Low Grade
            case 6526: // Yellow Colored Lure
            case 6527: // Yellow Colored Lure - High Grade
            case 8511: // Yellow Luminous Lure - Low Grade
            case 8513: // Yellow Luminous Lure
                if (check <= 55)
                    type = 2;
                else if (check <= 74)
                    type = 1;
                else if (check <= 94)
                    type = 0;
                else
                    type = 3;
                break;
            case 8484: // Prize-Winning Fishing Lure
                if (check <= 33)
                    type = 0;
                else if (check <= 66)
                    type = 1;
                else
                    type = 2;
                break;
            case 8506: // Green Luminous Lure, preferred by fast-moving (nimble) fish (type 8)
                if (check <= 54)
                    type = 8;
                else if (check <= 77)
                    type = 7;
                else
                    type = 9;
                break;
            case 8509: // Purple Luminous Lure, preferred by fat fish (type 7)
                if (check <= 54)
                    type = 7;
                else if (check <= 77)
                    type = 9;
                else
                    type = 8;
                break;
            case 8512: // Yellow Luminous Lure, preferred by ugly fish (type 9)
                if (check <= 54)
                    type = 9;
                else if (check <= 77)
                    type = 8;
                else
                    type = 7;
                break;
            case 8485: // Prize-Winning Night Fishing Lure, prize-winning fishing lure
                if (check <= 33)
                    type = 7;
                else if (check <= 66)
                    type = 8;
                else
                    type = 9;
                break;
            default:
                type = 1;
                break;
        }

        return type;
    }

    public static int getRandomFishLvl(Player player) {
        int skilllvl;

        // Проверка на Fisherman's Potion
        Effect effect = player.getEffectList().getEffectOfFishPot();
        if (effect != null)
            skilllvl = (int) effect.skill.power;
        else
            skilllvl = player.getSkillLevel(1315);

        if (skilllvl <= 0)
            return 1;

        int randomlvl;
        int check = Rnd.get(100);

        if (check < 50)
            randomlvl = skilllvl;
        else if (check <= 85) {
            randomlvl = skilllvl - 1;
            if (randomlvl <= 0)
                randomlvl = 1;
        } else
            randomlvl = skilllvl + 1;

        randomlvl = Math.min(27, Math.max(1, randomlvl));

        return randomlvl;
    }

    public static int getFishGroup(int lureId) {
        switch (lureId) {
            case 7807: // Green Colored Lure - For Beginners
            case 7808: // Purple Colored Lure - For Beginners
            case 7809: // Yellow Colored Lure - For Beginners
            case 8486: // Prize-Winning Novice Fishing Lure
                return 0;
            case 8506: // Green Luminous Lure
            case 8509: // Purple Luminous Lure
            case 8512: // Yellow Luminous Lure
            case 8485: //	Prize-Winning Night Fishing Lure
                return 2;
            default:
                return 1;
        }
    }

    private static int getLureGrade(int lureId) {
        switch (lureId) {
            case 6519: // Green Colored Lure - Low Grade
            case 6522: // Purple Colored Lure - Low Grade
            case 6525: // Yellow Colored Lure - Low Grade
            case 8505: // Green Luminous Lure - Low Grade
            case 8508: // Purple Luminous Lure - Low Grade
            case 8511: // Yellow Luminous Lure - Low Grade
                return 0;
            case 6520: // Green Colored Lure
            case 6523: // Purple Colored Lure
            case 6526: // Yellow Colored Lure

            case 7610: // Wind Fishing Lure
            case 7611: // Icy Air Fishing Lure
            case 7612: // Earth Fishing Lure
            case 7613: // Flaming Fishing Lure

            case 7807: // Green Colored Lure - For Beginners
            case 7808: // Purple Colored Lure - For Beginners
            case 7809: // Yellow Colored Lure - For Beginners
            case 8484: // Prize-Winning Fishing Lure
            case 8485: //	Prize-Winning Night Fishing Lure
            case 8486: // Prize-Winning Novice Fishing Lure

            case 8496: // Gludio's Luminous Lure
            case 8497: // Dion's Luminous Lure
            case 8498: // Giran's Luminous Lure
            case 8499: // Oren's Luminous Lure
            case 8500: // Aden's Luminous Lure
            case 8501: // Innadril's Luminous Lure
            case 8502: // Goddard's Luminous Lure
            case 8503: // Rune's Luminous Lure
            case 8504: // Schuttgart's Luminous Lure
            case 8548: // Hot Springs Lure

            case 8506: // Green Luminous Lure
            case 8509: // Purple Luminous Lure
            case 8512: // Yellow Luminous Lure
                return 1;
            case 6521: // Green Colored Lure - High Grade
            case 6524: // Purple Colored Lure - High Grade
            case 6527: // Yellow Colored Lure - High Grade
            case 8507: // Green Colored Lure - High Grade
            case 8510: // Purple Luminous Lure - High Grade
            case 8513: // Yellow Luminous Lure - High Grade
                return 2;
            default:
                return -1;
        }
    }

    private static boolean isNightLure(int lureId) {
        switch (lureId) {
            case 8505: // Green Luminous Lure - Low Grade
            case 8508: // Purple Luminous Lure - Low Grade
            case 8511: // Yellow Luminous Lure - Low Grade
                return true;
            case 8496: // Gludio's Luminous Lure
            case 8497: // Dion's Luminous Lure
            case 8498: // Giran's Luminous Lure
            case 8499: // Oren's Luminous Lure
            case 8500: // Aden's Luminous Lure
            case 8501: // Innadril's Luminous Lure
            case 8502: // Goddard's Luminous Lure
            case 8503: // Rune's Luminous Lure
            case 8504: // Schuttgart's Luminous Lure
                return true;
            case 8506: // Green Luminous Lure
            case 8509: // Purple Luminous Lure
            case 8512: // Yellow Luminous Lure
                return true;
            case 8510: // Purple Luminous Lure - High Grade
            case 8513: // Yellow Luminous Lure - High Grade
                return true;
            case 8485: //	Prize-Winning Night Fishing Lure
                return true;
            default:
                return false;
        }
    }

    public void setFish(FishTemplate fish) {
        this.fish = fish;
    }

    public int getLureId() {
        return lureId;
    }

    public void setLureId(int lureId) {
        this.lureId = lureId;
    }

    public Location getFishLoc() {
        return _fishLoc;
    }

    public void setFishLoc(Location loc) {
        _fishLoc.x = loc.x;
        _fishLoc.y = loc.y;
        _fishLoc.z = loc.z;
    }

    /**
     * Начинаем рыбалку, запускаем задачу ожидания рыбешки
     */
    public void startFishing() {
        if (!_state.compareAndSet(FISHING_NONE, FISHING_STARTED))
            return;

        fisher.setFishing(true);
        fisher.broadcastCharInfo();
        fisher.broadcastPacket(new ExFishingStart(fisher, fish.type, fisher.getFishLoc(), isNightLure(lureId)));
        fisher.sendPacket(SystemMsg.STARTS_FISHING);

        startLookingForFishTask();
    }

    /**
     * Отменяем рыбалку, завершаем текущую задачу
     */
    public void stopFishing() {
        if (_state.getAndSet(FISHING_NONE) == FISHING_NONE)
            return;

        stopFishingTask();

        fisher.setFishing(false);
        fisher.broadcastPacket(new ExFishingEnd(fisher, false));
        fisher.broadcastCharInfo();
        fisher.sendPacket(SystemMsg.CANCELS_FISHING);
    }

    /**
     * Заканчиваем рыбалку, в случае удачи или неудачи, завершаем текущую задачу
     */
    private void endFishing(boolean win) {
        if (!_state.compareAndSet(FISHING_COMBAT, FISHING_NONE))
            return;

        stopFishingTask();

        fisher.setFishing(false);
        fisher.broadcastPacket(new ExFishingEnd(fisher, win));
        fisher.broadcastCharInfo();
        fisher.sendPacket(SystemMsg.ENDS_FISHING);

        if (win)
            fisher.getCounters().fishCaught++;

    }

    private void stopFishingTask() {
        if (_fishingTask != null) {
            _fishingTask.cancel(false);
            _fishingTask = null;
        }
    }

    private void startLookingForFishTask() {
        if (!_state.compareAndSet(FISHING_STARTED, FISHING_WAITING))
            return;

        long checkDelay = 10_000L;

        switch (fish.group) {
            case 0:
                checkDelay = Math.round(fish.gutsCheckTime * 1.33);
                break;
            case 1:
                checkDelay = fish.gutsCheckTime;
                break;
            case 2:
                checkDelay = Math.round(fish.gutsCheckTime * 0.66);
                break;
        }

        _fishingTask = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new LookingForFishTask(), 10000L, checkDelay);
    }

    public boolean isInCombat() {
        return _state.get() == FISHING_COMBAT;
    }

    private void startFishCombat() {
        if (!_state.compareAndSet(FISHING_WAITING, FISHING_COMBAT))
            return;

        stop = 0;
        gooduse = 0;
        anim = 0;
        time = fish.combatTime / 1000;
        fishCurHP = fish.hp;
        combatMode = Rnd.chance(20) ? 1 : 0;


        deceptiveMode = false;
        if (getLureGrade(lureId) == 2) {
            deceptiveMode = Rnd.chance(10);
        }

        ExFishingStartCombat efsc = new ExFishingStartCombat(fisher, time, fish.hp, combatMode, fish.group, deceptiveMode);
        fisher.broadcastPacket(efsc);
        fisher.sendPacket(SystemMsg.SUCCEEDED_IN_GETTING_A_BITE);

        _fishingTask = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new FishCombatTask(), 1000L, 1000L);
    }

    private void changeHp(int hp, int pen) {
        fishCurHP -= hp;
        if (fishCurHP < 0)
            fishCurHP = 0;

        fisher.broadcastPacket(new ExFishingHpRegen(fisher, time, fishCurHP, combatMode, gooduse, anim, pen, deceptiveMode));

        gooduse = 0;
        anim = 0;
        if (fishCurHP > fish.hp * 2) {
            fishCurHP = fish.hp * 2;
            doDie(false);
        } else if (fishCurHP == 0)
            doDie(true);
    }

    private void doDie(boolean win) {
        stopFishingTask();

        if (win)
            if (!fisher.isInPeaceZone() && Rnd.chance(5)) {
                win = false;
                fisher.sendPacket(SystemMsg.YOU_HAVE_CAUGHT_A_MONSTER);
                spawnPenaltyMonster(fisher);
            } else {
                fisher.sendPacket(SystemMsg.SUCCEEDED_IN_FISHING);
                //TODO [G1ta0] добавить проверку на перевес
                ItemFunctions.addItem(fisher, fish.id, 1, "Fishing");
                FishingChampionShipManager.INSTANCE.newFish(fisher, lureId);
            }

        endFishing(win);
    }

    public void useFishingSkill(int dmg, int pen, SkillType skillType) {
        if (!isInCombat())
            return;

        int mode;
        if (skillType == SkillType.REELING && !GameTimeController.INSTANCE.isNowNight())
            mode = 1;
        else if (skillType == SkillType.PUMPING && GameTimeController.INSTANCE.isNowNight())
            mode = 1;
        else
            mode = 0;

        anim = mode + 1;
        if (Rnd.chance(10)) {
            fisher.sendPacket(SystemMsg.FISH_HAS_RESISTED);
            gooduse = 0;
            changeHp(0, pen);
            return;
        }

        if (combatMode == mode) {
            if (deceptiveMode ) {
                showMessage(fisher, dmg, pen, skillType, 2);
                gooduse = 2;
                changeHp(-dmg, pen);
            } else {
                showMessage(fisher, dmg, pen, skillType, 1);
                gooduse = 1;
                changeHp(dmg, pen);
            }
        } else if (deceptiveMode) {
            showMessage(fisher, dmg, pen, skillType, 3);
            gooduse = 1;
            changeHp(dmg, pen);
        } else {
            showMessage(fisher, dmg, pen, skillType, 2);
            gooduse = 2;
            changeHp(-dmg, pen);
        }
    }

    /**
     * LookingForFishTask
     */
    protected class LookingForFishTask extends RunnableImpl {
        private final long endTaskTime;

        LookingForFishTask() {
            endTaskTime = System.currentTimeMillis() + fish.waitTime + 10000L;
        }

        @Override
        public void runImpl() {
            if (System.currentTimeMillis() >= endTaskTime) {
                fisher.sendPacket(SystemMsg.BAITS_HAVE_BEEN_LOST_BECAUSE_THE_FISH_GOT_AWAY);
                stopFishingTask();
                endFishing(false);
                return;
            }

            if (!GameTimeController.INSTANCE.isNowNight() && isNightLure(lureId)) {
                fisher.sendPacket(SystemMsg.BAITS_HAVE_BEEN_LOST_BECAUSE_THE_FISH_GOT_AWAY);
                stopFishingTask();
                endFishing(false);
                return;
            }

            int check = Rnd.get(1000);

            if (fish.fishGuts > check) {
                stopFishingTask();
                startFishCombat();
            }
        }
    }

    private class FishCombatTask extends RunnableImpl {
        @Override
        public void runImpl() {
            if (fishCurHP >= fish.hp * 2) {
                // The fish got away
                fisher.sendPacket(SystemMsg.THE_FISH_GOT_AWAY);
                doDie(false);
            } else if (time <= 0) {
                // Time is up, so that fish got away
                fisher.sendPacket(SystemMsg.TIME_IS_UP_SO_THAT_FISH_GOT_AWAY);
                doDie(false);
            } else {
                time--;

                if (combatMode == 1 && !deceptiveMode  || combatMode == 0 && deceptiveMode)
                    fishCurHP += fish.hpRegen;

                if (stop == 0) {
                    stop = 1;
                    if (Rnd.chance(30))
                        combatMode = combatMode == 0 ? 1 : 0;

                    if (fish.group == 2)
                        if (Rnd.chance(10))
                            deceptiveMode = !deceptiveMode;
                } else
                    stop--;

                ExFishingHpRegen efhr = new ExFishingHpRegen(fisher, time, fishCurHP, combatMode, 0, anim, 0, deceptiveMode);
                if (anim != 0)
                    fisher.broadcastPacket(efhr);
                else
                    fisher.sendPacket(efhr);
            }
        }
    }
}