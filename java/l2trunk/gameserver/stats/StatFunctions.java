package l2trunk.gameserver.stats;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Summon;
import l2trunk.gameserver.model.base.BaseStats;
import l2trunk.gameserver.model.base.ClassType2;
import l2trunk.gameserver.model.base.Element;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.entity.SevenSigns;
import l2trunk.gameserver.model.instances.PetInstance;
import l2trunk.gameserver.model.instances.SummonInstance;
import l2trunk.gameserver.model.items.Inventory;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.stats.conditions.ConditionPlayerState;
import l2trunk.gameserver.stats.conditions.ConditionPlayerState.CheckPlayerState;
import l2trunk.gameserver.stats.funcs.Func;
import l2trunk.gameserver.templates.PlayerTemplate;
import l2trunk.gameserver.templates.item.WeaponTemplate;
import l2trunk.gameserver.templates.item.WeaponTemplate.WeaponType;

public final class StatFunctions {
    private static final int[] ACCURACY_LEVEL_BONUS = {0,
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, //  1-10
            11, 12, 13, 14, 15, 16, 17, 18, 18, 20, // 11-20
            21, 22, 23, 24, 25, 26, 27, 28, 29, 30, // 21-30
            31, 32, 33, 34, 35, 36, 37, 38, 39, 40, // 31-40
            41, 42, 43, 44, 45, 46, 47, 48, 49, 50, // 41-50
            51, 52, 53, 54, 55, 56, 57, 58, 59, 60, // 51-60
            61, 62, 63, 64, 65, 66, 67, 68, 69, 71, // 61-70
            73, 75, 77, 79, 81, 83, 85, 89, 91, 94, // 71-80
            96, 98, 100, 103, 105, 107, 109, 112, 114, 116  // 81-90
    };

    private static final int[] EVASION_LEVEL_BONUS = {0,
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, //  1-10
            11, 12, 13, 14, 15, 16, 17, 18, 18, 20, // 11-20
            21, 22, 23, 24, 25, 26, 27, 28, 29, 30, // 21-30
            31, 32, 33, 34, 35, 36, 37, 38, 39, 40, // 31-40
            41, 42, 43, 44, 45, 46, 47, 48, 49, 50, // 41-50
            51, 52, 53, 54, 55, 56, 57, 58, 59, 60, // 51-60
            61, 62, 63, 64, 65, 66, 67, 68, 69, 71, // 61-70
            73, 75, 77, 79, 81, 83, 85, 89, 91, 94, // 71-80
            96, 98, 100, 103, 105, 107, 109, 112, 114, 116  // 81-90
    };

    public static void addPredefinedFuncs(Creature cha) {
        if (cha instanceof Player) {
            cha.addStatFunc(FuncMultRegenResting.getFunc(Stats.REGENERATE_CP_RATE));
            cha.addStatFunc(FuncMultRegenStanding.getFunc(Stats.REGENERATE_CP_RATE));
            cha.addStatFunc(FuncMultRegenRunning.getFunc(Stats.REGENERATE_CP_RATE));
            cha.addStatFunc(FuncMultRegenResting.getFunc(Stats.REGENERATE_HP_RATE));
            cha.addStatFunc(FuncMultRegenStanding.getFunc(Stats.REGENERATE_HP_RATE));
            cha.addStatFunc(FuncMultRegenRunning.getFunc(Stats.REGENERATE_HP_RATE));
            cha.addStatFunc(FuncMultRegenResting.getFunc(Stats.REGENERATE_MP_RATE));
            cha.addStatFunc(FuncMultRegenStanding.getFunc(Stats.REGENERATE_MP_RATE));
            cha.addStatFunc(FuncMultRegenRunning.getFunc(Stats.REGENERATE_MP_RATE));

            cha.addStatFunc(FuncMaxCpAdd.func);
            cha.addStatFunc(FuncMaxHpAdd.func);
            cha.addStatFunc(FuncMaxMpAdd.func);

            cha.addStatFunc(FuncMaxCpMul.func);
            cha.addStatFunc(FuncMaxHpMul.func);
            cha.addStatFunc(FuncMaxMpMul.func);

            cha.addStatFunc(FuncAttackRange.func);

            cha.addStatFunc(FuncMoveSpeedMul.func);

            cha.addStatFunc(FuncHennaSTR.func);
            cha.addStatFunc(FuncHennaDEX.func);
            cha.addStatFunc(FuncHennaINT.func);
            cha.addStatFunc(FuncHennaMEN.func);
            cha.addStatFunc(FuncHennaCON.func);
            cha.addStatFunc(FuncHennaWIT.func);

            cha.addStatFunc(FuncInventory.func);
            cha.addStatFunc(FuncWarehouse.func);
            cha.addStatFunc(FuncTradeLimit.func);

            cha.addStatFunc(FuncSDefPlayers.func);

        }

        if (cha instanceof Player || cha instanceof PetInstance) {
            cha.addStatFunc(FuncPAtkMul.func);
            cha.addStatFunc(FuncMAtkMul.func);
            cha.addStatFunc(FuncPDefMul.func);
            cha.addStatFunc(FuncMDefMul.func);
        }

        if (cha instanceof SummonInstance) {
            cha.addStatFunc(FuncAttributeAttackSet.getFunc(Element.FIRE));
            cha.addStatFunc(FuncAttributeAttackSet.getFunc(Element.WATER));
            cha.addStatFunc(FuncAttributeAttackSet.getFunc(Element.EARTH));
            cha.addStatFunc(FuncAttributeAttackSet.getFunc(Element.WIND));
            cha.addStatFunc(FuncAttributeAttackSet.getFunc(Element.HOLY));
            cha.addStatFunc(FuncAttributeAttackSet.getFunc(Element.UNHOLY));

            cha.addStatFunc(FuncAttributeDefenceSet.getFunc(Element.FIRE));
            cha.addStatFunc(FuncAttributeDefenceSet.getFunc(Element.WATER));
            cha.addStatFunc(FuncAttributeDefenceSet.getFunc(Element.EARTH));
            cha.addStatFunc(FuncAttributeDefenceSet.getFunc(Element.WIND));
            cha.addStatFunc(FuncAttributeDefenceSet.getFunc(Element.HOLY));
            cha.addStatFunc(FuncAttributeDefenceSet.getFunc(Element.UNHOLY));
        }

        if (!(cha instanceof PetInstance)) {
            cha.addStatFunc(FuncAccuracyAdd.func);
            cha.addStatFunc(FuncEvasionAdd.func);
        }

        if (!(cha instanceof Summon)) {
            cha.addStatFunc(FuncPAtkSpeedMul.func);
            cha.addStatFunc(FuncMAtkSpeedMul.func);
            cha.addStatFunc(FuncSDefInit.func);
            cha.addStatFunc(FuncSDefAll.func);
        }


        cha.addStatFunc(FuncMCriticalRateMul.func);
        cha.addStatFunc(FuncPCriticalRateMul.func);
        cha.addStatFunc(FuncPDamageResists.func);
        cha.addStatFunc(FuncMDamageResists.func);

        cha.addStatFunc(FuncAttributeAttackInit.getFunc(Element.FIRE));
        cha.addStatFunc(FuncAttributeAttackInit.getFunc(Element.WATER));
        cha.addStatFunc(FuncAttributeAttackInit.getFunc(Element.EARTH));
        cha.addStatFunc(FuncAttributeAttackInit.getFunc(Element.WIND));
        cha.addStatFunc(FuncAttributeAttackInit.getFunc(Element.HOLY));
        cha.addStatFunc(FuncAttributeAttackInit.getFunc(Element.UNHOLY));

        cha.addStatFunc(FuncAttributeDefenceInit.getFunc(Element.FIRE));
        cha.addStatFunc(FuncAttributeDefenceInit.getFunc(Element.WATER));
        cha.addStatFunc(FuncAttributeDefenceInit.getFunc(Element.EARTH));
        cha.addStatFunc(FuncAttributeDefenceInit.getFunc(Element.WIND));
        cha.addStatFunc(FuncAttributeDefenceInit.getFunc(Element.HOLY));
        cha.addStatFunc(FuncAttributeDefenceInit.getFunc(Element.UNHOLY));
    }

    private static class FuncMultRegenResting extends Func {
        static final FuncMultRegenResting[] func = new FuncMultRegenResting[Stats.NUM_STATS];

        private FuncMultRegenResting(Stats stat) {
            super(stat, 0x30, null, 1.0);
            setCondition(new ConditionPlayerState(CheckPlayerState.RESTING, true));
        }

        static Func getFunc(Stats stat) {
            int pos = stat.ordinal();
            if (func[pos] == null)
                func[pos] = new FuncMultRegenResting(stat);
            return func[pos];
        }

        @Override
        public void calc(Env env) {
            if (env.character instanceof Player && stat == Stats.REGENERATE_HP_RATE)
                env.value *= 1.5;
        }
    }

    private static class FuncMultRegenStanding extends Func {
        static final FuncMultRegenStanding[] func = new FuncMultRegenStanding[Stats.NUM_STATS];

        private FuncMultRegenStanding(Stats stat) {
            super(stat, 0x30, null,1.0);
            setCondition(new ConditionPlayerState(CheckPlayerState.STANDING, true));
        }

        static Func getFunc(Stats stat) {
            int pos = stat.ordinal();
            if (func[pos] == null)
                func[pos] = new FuncMultRegenStanding(stat);
            return func[pos];
        }

        @Override
        public void calc(Env env) {
            env.value *= 1.1;
        }
    }

    private static class FuncMultRegenRunning extends Func {
        static final FuncMultRegenRunning[] func = new FuncMultRegenRunning[Stats.NUM_STATS];

        private FuncMultRegenRunning(Stats stat) {
            super(stat, 0x30, null,1.0);
            setCondition(new ConditionPlayerState(CheckPlayerState.RUNNING, true));
        }

        static Func getFunc(Stats stat) {
            int pos = stat.ordinal();
            if (func[pos] == null)
                func[pos] = new FuncMultRegenRunning(stat);
            return func[pos];
        }

        @Override
        public void calc(Env env) {
            env.value *= 0.7;
        }
    }

    private static class FuncPAtkMul extends Func {
        static final FuncPAtkMul func = new FuncPAtkMul();

        private FuncPAtkMul() {
            super(Stats.POWER_ATTACK, 0x20, null,1.0); // TODO edited to 1.0
        }

        @Override
        public void calc(Env env) {
            env.value *= BaseStats.STR.calcBonus(env.character) * env.character.getLevelMod();
        }
    }

    private static class FuncMAtkMul extends Func {
        static final FuncMAtkMul func = new FuncMAtkMul();

        private FuncMAtkMul() {
            super(Stats.MAGIC_ATTACK, 0x20, null,1.0);
        }

        @Override
        public void calc(Env env) {
            //{Wpn*(lvlbn^2)*[(1+INTbn)^2]+Msty}
            final double ib = BaseStats.INT.calcBonus(env.character);
            final double lvlb = env.character.getLevelMod();
            env.value *= lvlb * lvlb * ib * ib;
        }
    }

    private static class FuncPDefMul extends Func {
        static final FuncPDefMul func = new FuncPDefMul();

        private FuncPDefMul() {
            super(Stats.POWER_DEFENCE, 0x20, null, 1.0);
        }

        @Override
        public void calc(Env env) {
            env.value *= env.character.getLevelMod();
        }
    }

    private static class FuncMDefMul extends Func {
        static final FuncMDefMul func = new FuncMDefMul();

        private FuncMDefMul() {
            super(Stats.MAGIC_DEFENCE, 0x20, null,1.0);
        }

        @Override
        public void calc(Env env) {
            env.value *= BaseStats.MEN.calcBonus(env.character) * env.character.getLevelMod();
        }
    }

    private static class FuncAttackRange extends Func {
        static final FuncAttackRange func = new FuncAttackRange();

        private FuncAttackRange() {
            super(Stats.POWER_ATTACK_RANGE, 0x20, null,1.0);
        }

        @Override
        public void calc(Env env) {
            WeaponTemplate weapon = env.character.getActiveWeaponItem();
            if (weapon != null)
                env.value += weapon.getAttackRange();
        }
    }

    private static class FuncAccuracyAdd extends Func {
        static final FuncAccuracyAdd func = new FuncAccuracyAdd();

        private FuncAccuracyAdd() {
            super(Stats.ACCURACY_COMBAT, 0x10, null, 1.0);
        }

        @Override
        public void calc(Env env) {
            if (env.character instanceof PetInstance)
                return;

            //[Square(DEX)]*6 + lvl + weapon hitbonus;
            env.value += Math.sqrt(env.character.getDEX()) * 6 + ACCURACY_LEVEL_BONUS[env.character.getLevel()];

            if (env.character instanceof SummonInstance )
                env.value += env.character.getLevel() < 60 ? 4 : 5;
        }
    }

    private static class FuncEvasionAdd extends Func {
        static final FuncEvasionAdd func = new FuncEvasionAdd();

        private FuncEvasionAdd() {
            super(Stats.EVASION_RATE, 0x10, null,1.0);
        }

        @Override
        public void calc(Env env) {
            env.value += Math.sqrt(env.character.getDEX()) * 6 + EVASION_LEVEL_BONUS[env.character.getLevel()];
        }
    }

    private static class FuncMCriticalRateMul extends Func {
        static final FuncMCriticalRateMul func = new FuncMCriticalRateMul();

        private FuncMCriticalRateMul() {
            super(Stats.MCRITICAL_RATE, 0x10, null, 1.);
        }

        @Override
        public void calc(Env env) {
            env.value *= BaseStats.WIT.calcBonus(env.character);
        }
    }

    private static class FuncPCriticalRateMul extends Func {
        static final FuncPCriticalRateMul func = new FuncPCriticalRateMul();

        private FuncPCriticalRateMul() {
            super(Stats.CRITICAL_BASE, 0x10, null,1.0);
        }

        @Override
        public void calc(Env env) {
            if (!(env.character instanceof Summon))
                env.value *= BaseStats.DEX.calcBonus(env.character);
            env.value *= 0.01 * env.character.calcStat(Stats.CRITICAL_RATE, env.target, env.skill);
        }
    }

    private static class FuncMoveSpeedMul extends Func {
        static final FuncMoveSpeedMul func = new FuncMoveSpeedMul();

        private FuncMoveSpeedMul() {
            super(Stats.RUN_SPEED, 0x20, null,1.0);
        }

        @Override
        public void calc(Env env) {
            env.value *= BaseStats.DEX.calcBonus(env.character);
        }
    }

    private static class FuncPAtkSpeedMul extends Func {
        static final FuncPAtkSpeedMul func = new FuncPAtkSpeedMul();

        private FuncPAtkSpeedMul() {
            super(Stats.POWER_ATTACK_SPEED, 0x20, null,1.0);
        }

        @Override
        public void calc(Env env) {
            env.value *= BaseStats.DEX.calcBonus(env.character);
        }
    }

    private static class FuncMAtkSpeedMul extends Func {
        static final FuncMAtkSpeedMul func = new FuncMAtkSpeedMul();

        private FuncMAtkSpeedMul() {
            super(Stats.MAGIC_ATTACK_SPEED, 0x20, null,1.0);
        }

        @Override
        public void calc(Env env) {
            env.value *= BaseStats.WIT.calcBonus(env.character);
        }
    }

    private static class FuncHennaSTR extends Func {
        static final FuncHennaSTR func = new FuncHennaSTR();

        private FuncHennaSTR() {
            super(Stats.STAT_STR, 0x10, null);
        }

        @Override
        public void calc(Env env) {
            Player pc = (Player) env.character;
            if (pc != null)
                env.value = Math.max(1, env.value + pc.getHennaStatSTR());
        }
    }

    private static class FuncHennaDEX extends Func {
        static final FuncHennaDEX func = new FuncHennaDEX();

        private FuncHennaDEX() {
            super(Stats.STAT_DEX, 0x10, null);
        }

        @Override
        public void calc(Env env) {
            Player pc = (Player) env.character;
            if (pc != null)
                env.value = Math.max(1, env.value + pc.getHennaStatDEX());
        }
    }

    private static class FuncHennaINT extends Func {
        static final FuncHennaINT func = new FuncHennaINT();

        private FuncHennaINT() {
            super(Stats.STAT_INT, 0x10, null);
        }

        @Override
        public void calc(Env env) {
            Player pc = (Player) env.character;
            if (pc != null)
                env.value = Math.max(1, env.value + pc.getHennaStatINT());
        }
    }

    private static class FuncHennaMEN extends Func {
        static final FuncHennaMEN func = new FuncHennaMEN();

        private FuncHennaMEN() {
            super(Stats.STAT_MEN, 0x10, null);
        }

        @Override
        public void calc(Env env) {
            Player pc = (Player) env.character;
            if (pc != null)
                env.value = Math.max(1, env.value + pc.getHennaStatMEN());
        }
    }

    private static class FuncHennaCON extends Func {
        static final FuncHennaCON func = new FuncHennaCON();

        private FuncHennaCON() {
            super(Stats.STAT_CON, 0x10, null);
        }

        @Override
        public void calc(Env env) {
            Player pc = (Player) env.character;
            if (pc != null)
                env.value = Math.max(1, env.value + pc.getHennaStatCON());
        }
    }

    private static class FuncHennaWIT extends Func {
        static final FuncHennaWIT func = new FuncHennaWIT();

        private FuncHennaWIT() {
            super(Stats.STAT_WIT, 0x10, null);
        }

        @Override
        public void calc(Env env) {
            Player pc = (Player) env.character;
            if (pc != null)
                env.value = Math.max(1, env.value + pc.getHennaStatWIT());
        }
    }

    private static class FuncMaxHpAdd extends Func {
        static final FuncMaxHpAdd func = new FuncMaxHpAdd();

        private FuncMaxHpAdd() {
            super(Stats.MAX_HP, 0x10, null);
        }

        @Override
        public void calc(Env env) {
            PlayerTemplate t = (PlayerTemplate) env.character.getTemplate();

            // Alexander - Temporary fix for HP when players delevel from a third class to lvl 20 or close. HP were not decreasing from the base occupation, and from lower lvls they were going to 0
            final int lvl = env.character.getLevel() - t.classBaseLevel;
            final double hpAdd = (t.classBaseLevel >= 76 && env.character.getLevel() <= 20 ? t.lvlHpAdd * 0.9 : t.lvlHpAdd);

            double hpmod = t.lvlHpMod * lvl;
            double hpmax = (hpAdd + hpmod) * lvl;
            double hpmin = hpAdd * lvl + hpmod;

            env.value += (hpmax + hpmin) / 2;
        }
    }

    private static class FuncMaxHpMul extends Func {
        static final FuncMaxHpMul func = new FuncMaxHpMul();

        private FuncMaxHpMul() {
            super(Stats.MAX_HP, 0x20, null);
        }

        @Override
        public void calc(Env env) {
            env.value *= BaseStats.CON.calcBonus(env.character);
        }
    }

    private static class FuncMaxCpAdd extends Func {
        static final FuncMaxCpAdd func = new FuncMaxCpAdd();

        private FuncMaxCpAdd() {
            super(Stats.MAX_CP, 0x10, null);
        }

        @Override
        public void calc(Env env) {
            PlayerTemplate t = (PlayerTemplate) env.character.getTemplate();

            // Alexander - Temporary fix for CP when players delevel from a third class to lvl 20 or close. HP were not decreasing from the base occupation, and from lower lvls they were going to 0
            final int lvl = env.character.getLevel() - t.classBaseLevel;
            final double cpAdd = (t.classBaseLevel >= 76 && env.character.getLevel() <= 20 ? t.lvlCpAdd * 0.9 : t.lvlCpAdd);

            double cpmod = t.lvlCpMod * lvl;
            double cpmax = (cpAdd + cpmod) * lvl;
            double cpmin = cpAdd * lvl + cpmod;
            env.value += (cpmax + cpmin) / 2;
        }
    }

    private static class FuncMaxCpMul extends Func {
        static final FuncMaxCpMul func = new FuncMaxCpMul();

        private FuncMaxCpMul() {
            super(Stats.MAX_CP, 0x20, null);
        }

        @Override
        public void calc(Env env) {
            double cpSSmod = 1;
            int sealOwnedBy = SevenSigns.INSTANCE.getSealOwner(SevenSigns.SEAL_STRIFE);
            int playerCabal = SevenSigns.INSTANCE.getPlayerCabal((Player) env.character);

            if (sealOwnedBy != SevenSigns.CABAL_NULL)
                if (playerCabal == sealOwnedBy)
                    cpSSmod = 1.1;
                else
                    cpSSmod = 0.9;

            env.value *= BaseStats.CON.calcBonus(env.character) * cpSSmod;
        }
    }

    private static class FuncMaxMpAdd extends Func {
        static final FuncMaxMpAdd func = new FuncMaxMpAdd();

        private FuncMaxMpAdd() {
            super(Stats.MAX_MP, 0x10, null);
        }

        @Override
        public void calc(Env env) {
            PlayerTemplate t = (PlayerTemplate) env.character.getTemplate();

            // Alexander - Temporary fix for MP when players delevel from a third class to lvl 20 or close. HP were not decreasing from the base occupation, and from lower lvls they were going to 0
            final int lvl = env.character.getLevel() - t.classBaseLevel;
            final double mpAdd = (t.classBaseLevel >= 76 && env.character.getLevel() <= 20 ? t.lvlMpAdd * 0.9 : t.lvlMpAdd);

            double mpmod = t.lvlMpMod * lvl;
            double mpmax = (mpAdd + mpmod) * lvl;
            double mpmin = mpAdd * lvl + mpmod;
            env.value += (mpmax + mpmin) / 2;
        }
    }

    private static class FuncMaxMpMul extends Func {
        static final FuncMaxMpMul func = new FuncMaxMpMul();

        private FuncMaxMpMul() {
            super(Stats.MAX_MP, 0x20, null);
        }

        @Override
        public void calc(Env env) {
            env.value *= BaseStats.MEN.calcBonus(env.character);
        }
    }

    private static class FuncPDamageResists extends Func {
        static final FuncPDamageResists func = new FuncPDamageResists();

        private FuncPDamageResists() {
            super(Stats.PHYSICAL_DAMAGE, 0x30, null);
        }

        @Override
        public void calc(Env env) {
            if (env.target.isRaid() && env.character.getLevel() - env.target.getLevel() > Config.RAID_MAX_LEVEL_DIFF) {
                env.value = 1;
                return;
            }

            // TODO переделать на ту же систему, что у эффектов
            WeaponTemplate weapon = env.character.getActiveWeaponItem();
            if (weapon == null)
                env.value *= 0.01 * env.target.calcStat(Stats.FIST_WPN_VULNERABILITY, env.character, env.skill);
            else if (weapon.getItemType().getDefence() != null)
                env.value *= 0.01 * env.target.calcStat(weapon.getItemType().getDefence(), env.character, env.skill);

            env.value = Formulas.calcDamageResists(env.skill, env.character, env.target, env.value);
        }
    }

    private static class FuncMDamageResists extends Func {
        static final FuncMDamageResists func = new FuncMDamageResists();

        private FuncMDamageResists() {
            super(Stats.MAGIC_DAMAGE, 0x30, null);
        }

        @Override
        public void calc(Env env) {
            if (env.target.isRaid() && Math.abs(env.character.getLevel() - env.target.getLevel()) > Config.RAID_MAX_LEVEL_DIFF) {
                env.value = 1;
                return;
            }
            env.value = Formulas.calcDamageResists(env.skill, env.character, env.target, env.value);
        }
    }

    private static class FuncInventory extends Func {
        static final FuncInventory func = new FuncInventory();

        private FuncInventory() {
            super(Stats.INVENTORY_LIMIT, 0x01, null, 80);
        }

        @Override
        public void calc(Env env) {
            Player player = (Player) env.character;
            if (player.isGM())
                env.value = Config.INVENTORY_MAXIMUM_GM;
            else if (player.getTemplate().race == Race.dwarf)
                env.value = Config.INVENTORY_MAXIMUM_DWARF;
            else
                env.value = Config.INVENTORY_MAXIMUM_NO_DWARF;
            env.value += player.getExpandInventory();
            env.value = Math.min(env.value, Config.SERVICES_EXPAND_INVENTORY_MAX);
        }
    }

    private static class FuncWarehouse extends Func {
        static final FuncWarehouse func = new FuncWarehouse();

        private FuncWarehouse() {
            super(Stats.STORAGE_LIMIT, 0x01, null);
        }

        @Override
        public void calc(Env env) {
            Player player = (Player) env.character;
            if (player.getTemplate().race == Race.dwarf)
                env.value = Config.WAREHOUSE_SLOTS_DWARF;
            else
                env.value = Config.WAREHOUSE_SLOTS_NO_DWARF;
            env.value += player.getExpandWarehouse();
        }
    }

    private static class FuncTradeLimit extends Func {
        static final FuncTradeLimit func = new FuncTradeLimit();

        private FuncTradeLimit() {
            super(Stats.TRADE_LIMIT, 0x01, null);
        }

        @Override
        public void calc(Env env) {
            Player _cha = (Player) env.character;
            if (_cha.getRace() == Race.dwarf)
                env.value = Config.MAX_PVTSTORE_SLOTS_DWARF;
            else
                env.value = Config.MAX_PVTSTORE_SLOTS_OTHER;
        }
    }

    private static class FuncSDefInit extends Func {
        static final Func func = new FuncSDefInit();

        private FuncSDefInit() {
            super(Stats.SHIELD_RATE, 0x01, null);
        }

        @Override
        public void calc(Env env) {
            Creature cha = env.character;
            env.value = cha.getTemplate().baseShldRate;
        }
    }

    private static class FuncSDefAll extends Func {
        static final FuncSDefAll func = new FuncSDefAll();

        private FuncSDefAll() {
            super(Stats.SHIELD_RATE, 0x20, null);
        }

        @SuppressWarnings("incomplete-switch")
        @Override
        public void calc(Env env) {
            if (env.value == 0)
                return;

            Creature target = env.target;
            if (target != null) {
                WeaponTemplate weapon = target.getActiveWeaponItem();
                if (weapon != null)
                    switch (weapon.getItemType()) {
                        case BOW:
                        case CROSSBOW:
                            env.value += 30.;
                            break;
                        case DAGGER:
                        case DUALDAGGER:
                            env.value += 12.;
                            break;
                    }
            }
        }
    }

    private static class FuncSDefPlayers extends Func {
        static final FuncSDefPlayers func = new FuncSDefPlayers();

        private FuncSDefPlayers() {
            super(Stats.SHIELD_RATE, 0x20, null);
        }

        @Override
        public void calc(Env env) {
            if (env.value == 0)
                return;

            Creature cha = env.character;
            ItemInstance shld = ((Player) cha).getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
            if (shld == null || shld.getItemType() != WeaponType.NONE)
                return;
            env.value *= BaseStats.DEX.calcBonus(env.character);
        }
    }

    private static class FuncAttributeAttackInit extends Func {
        static final Func[] func = new FuncAttributeAttackInit[Element.VALUES.length];

        static {
            for (int i = 0; i < Element.VALUES.length; i++)
                func[i] = new FuncAttributeAttackInit(Element.VALUES[i]);
        }

        private Element element;

        private FuncAttributeAttackInit(Element element) {
            super(element.getAttack(), 0x01, null);
            this.element = element;
        }

        static Func getFunc(Element element) {
            return func[element.getId()];
        }

        @Override
        public void calc(Env env) {
            env.value += env.character.getTemplate().baseAttributeAttack.get(element.getId());
        }
    }

    private static class FuncAttributeDefenceInit extends Func {
        static final Func[] func = new FuncAttributeDefenceInit[Element.VALUES.length];

        static {
            for (int i = 0; i < Element.VALUES.length; i++)
                func[i] = new FuncAttributeDefenceInit(Element.VALUES[i]);
        }

        private Element element;

        private FuncAttributeDefenceInit(Element element) {
            super(element.getDefence(), 0x01, null);
            this.element = element;
        }

        static Func getFunc(Element element) {
            return func[element.getId()];
        }

        @Override
        public void calc(Env env) {
            env.value += env.character.getTemplate().baseAttributeDefence.get(element.getId());
        }
    }

    private static class FuncAttributeAttackSet extends Func {
        static final Func[] func = new FuncAttributeAttackSet[Element.VALUES.length];

        static {
            for (int i = 0; i < Element.VALUES.length; i++)
                func[i] = new FuncAttributeAttackSet(Element.VALUES[i].getAttack());
        }

        private FuncAttributeAttackSet(Stats stat) {
            super(stat, 0x10, null);
        }

        static Func getFunc(Element element) {
            return func[element.getId()];
        }

        @Override
        public void calc(Env env) {
            if (env.character.getPlayer().getClassId().getType2() == ClassType2.Summoner)
                env.value = env.character.getPlayer().calcStat(stat, 0., env.target, env.skill);
        }
    }

    private static class FuncAttributeDefenceSet extends Func {
        static final Func[] func = new FuncAttributeDefenceSet[Element.VALUES.length];

        static {
            for (int i = 0; i < Element.VALUES.length; i++)
                func[i] = new FuncAttributeDefenceSet(Element.VALUES[i].getDefence());
        }

        private FuncAttributeDefenceSet(Stats stat) {
            super(stat, 0x10, null);
        }

        static Func getFunc(Element element) {
            return func[element.getId()];
        }

        @Override
        public void calc(Env env) {
            if (env.character.getPlayer().getClassId().getType2() == ClassType2.Summoner)
                env.value = env.character.getPlayer().calcStat(stat, 0., env.target, env.skill);
        }
    }
}