package l2trunk.gameserver.scripts;

import l2trunk.commons.lang.FileUtils;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ai.CharacterAI;
import l2trunk.gameserver.data.xml.Parsers;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.*;

public enum Scripts {
    INSTANCE;
    public static final Map<Integer, List<ScriptClassAndMethod>> dialogAppends = new HashMap<>();
    public static final Map<String, ScriptClassAndMethod> onAction = new HashMap<>();
    public static final Map<String, ScriptClassAndMethod> onActionShift = new HashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(Scripts.class);
    private final Map<String, Class<?>> classes = new TreeMap<>();
    private Map<String, Class<? extends CharacterAI>> AIs = new HashMap<>();
    private Map<String, Class<? extends Creature>> npcInstances = new HashMap<>();
    private Map<String, Class<? extends ScriptFile>> scripts = new HashMap<>();

    public static void main(String[] args) {
        Parsers.parseAll();
//        Scripts.INSTANCE.load2();
        Scripts.INSTANCE.init2();
    }


    public Class<? extends Creature> getNpcInstanceAI(String npcName) {
        if (!npcInstances.containsKey(npcName)) {
            LOG.error("not found npcInstance: " + npcName);
            System.exit(1);
        }
        return npcInstances.get(npcName);
    }

    public Class<? extends CharacterAI> getAI(String aiName) {
        if (!AIs.containsKey(aiName)) {
            LOG.error("not found AI: " + aiName);
            System.exit(1);
        }
        return AIs.get(aiName);
    }

//    public Class<?> getClazz(String className) {
//        return classes.get(className);
//    }

    private void addHandlers(Class<?> clazz) {
        try {
            for (Method method : clazz.getMethods())
                if (method.getName().contains("DialogAppend_")) {
                    Integer id = Integer.parseInt(method.getName().substring(13));
                    List<ScriptClassAndMethod> handlers = dialogAppends.computeIfAbsent(id, k -> new ArrayList<>());
                    handlers.add(new ScriptClassAndMethod(clazz.getName(), method.getName()));
                } else if (method.getName().contains("OnAction_")) {
                    String name = method.getName().substring(9);
                    onAction.put(name, new ScriptClassAndMethod(clazz.getSimpleName(), method.getName()));
                } else if (method.getName().contains("OnActionShift_")) {
                    String name = method.getName().substring(14);
                    onActionShift.put(name, new ScriptClassAndMethod(clazz.getName(), method.getName()));
                }
        } catch (NumberFormatException | SecurityException e) {
            LOG.error("Exception while adding Handlers ", e);
        } catch (Exception e) {
            LOG.error("something went wrong: " + e);
        }
    }

    /**
     * Calling Method in Script file with variables, argument and Player caller as NULL
     *
     * @param caller     Player calling class(can be used later with getSelf())
     * @param className  Class to call
     * @param methodName Method to call
     * @param args       Arguments that method takes
     * @return Object returned from method
     */
    public Object callScripts(Player caller, String className, String methodName, Object[] args) {
        return callScripts(caller, className, methodName, args, null);
    }

    /**
     * Calling Method in Script file with variables, argument and Player caller as NULL
     *
     * @param caller     Player calling class(can be used later with getSelf())
     * @param className  Class to call
     * @param methodName Method to call
     * @param variables  Additional arguments that may be called in Script file
     * @return Object returned from method
     */
    public Object callScripts(Player caller, String className, String methodName, Map<String, Object> variables) {
        return callScripts(caller, className, methodName, new Object[0], variables);
    }

    /**
     * Calling Method in Script file with variables, argument and Player caller as NULL
     *
     * @param caller     Player calling class(can be used later with getSelf())
     * @param className  Class to call
     * @param methodName Method to call
     * @param args       Arguments that method takes
     * @param variables  Additional arguments that may be called in Script file
     * @return Object returned from method
     */
    public Object callScripts(Player caller, String className, String methodName, Object[] args, Map<String, Object> variables) {
        Object o =null;
        Class<?> clazz;

        clazz = classes.get(className);
        if (clazz == null) {
            LOG.error("Script class " + className + " not found!");
            return null;
        }

        try {
            o = clazz.getDeclaredConstructor().newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            LOG.error("Scripts: Failed creating instance of " + clazz.getName(), e);
            return null;
        } catch (NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }

        if (variables != null && !variables.isEmpty())
            for (Map.Entry<String, Object> param : variables.entrySet())
                try {
                    FieldUtils.writeField(o, param.getKey(), param.getValue());
                } catch (IllegalAccessException e) {
                    LOG.error("Scripts: Failed setting fields for " + clazz.getName(), e);
                }

        if (caller != null)
            try {
                Field field;
                if ((field = FieldUtils.getField(clazz, "self")) != null)
                    FieldUtils.writeField(field, o, caller.getRef());
            } catch (IllegalAccessException e) {
                LOG.error("Scripts: Failed setting field for " + clazz.getName(), e);
            }

        Object ret = null;
        try {
            Class<?>[] parameterTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++)
                parameterTypes[i] = args[i] != null ? args[i].getClass() : null;

            ret = MethodUtils.invokeMethod(o, methodName, args, parameterTypes);
        } catch (NoSuchMethodException nsme) {
            LOG.error("Scripts: No such method " + clazz.getName() + "." + methodName + "()!", nsme);
        } catch (InvocationTargetException ite) {
            LOG.error("Scripts: Error while calling " + clazz.getName() + "." + methodName + "()", ite.getTargetException());
        } catch (IllegalAccessException e) {
            LOG.error("Scripts: Failed calling " + clazz.getName() + "." + methodName + "()", e);
        }

        return ret;
    }

    public void init2() {
        int i = 0;
        for (Class<?> clazz : classes.values()) {
            addHandlers(clazz);
            if (ClassUtils.isAssignable(clazz, ScriptFile.class)) {
                if (Modifier.isAbstract(clazz.getModifiers())) continue;
                try {
                    i++;
                    ((ScriptFile) clazz.getDeclaredConstructor().newInstance()).onLoad();
                    if (i > 659)
                        System.out.println("instantiated " + clazz.getName() + " total " + i + " classes");
                } catch (IllegalAccessException | InstantiationException e) {
                    LOG.error("Scripts: Failed running " + clazz.getName() + ".onLoad()", e);
                } catch (NoSuchMethodException | InvocationTargetException e) {
                    LOG.error("No default constructor fonund in class " + clazz);
                }
            }
        }


    }

    public void load2() {
        loadAIs();
        loadNpcInstances();
        loadScripts();
        Path path = Config.CONFIG.resolve("scripts.txt");
        String[] scripts = FileUtils.readFileToString(path).split("\r\n");
//        Map <String, Class<?>> classes = new HashMap<>();
        int i = 0;
        for (String script : scripts) {
            Class<?> clazz;

            try {
                clazz = Class.forName("l2trunk.scripts." + script);
                classes.put(script, clazz);
                i++;
                if (i > 1276)
                    System.out.println("loaded: " + i);
            } catch (ClassNotFoundException e) {
                LOG.error("not found class for " + script);
                e.printStackTrace();
            }
        }
    }

    private void loadAIs() {
        AIs.put("ai.BoatAI", l2trunk.gameserver.ai.BoatAI.class);
        AIs.put("ai.CharacterAI", l2trunk.gameserver.ai.CharacterAI.class);
        AIs.put("ai.DefaultAI", l2trunk.gameserver.ai.DefaultAI.class);
        AIs.put("ai.DoorAI", l2trunk.gameserver.ai.DoorAI.class);
        AIs.put("ai.Fighter", l2trunk.gameserver.ai.Fighter.class);
        AIs.put("ai.Guard", l2trunk.gameserver.ai.Guard.class);
        AIs.put("ai.Mystic", l2trunk.gameserver.ai.Mystic.class);
        AIs.put("ai.PlayableAI", l2trunk.gameserver.ai.PlayableAI.class);
        AIs.put("ai.PlayerAI", l2trunk.gameserver.ai.PlayerAI.class);
        AIs.put("ai.Priest", l2trunk.gameserver.ai.Priest.class);
//        AIs.put("ai.RaceManager",l2trunk.gameserver.ai.RaceManager.class);
        AIs.put("ai.Ranger", l2trunk.gameserver.ai.Ranger.class);
        AIs.put("ai.SummonAI", l2trunk.gameserver.ai.SummonAI.class);


        AIs.put("ai.CharacterAI", l2trunk.gameserver.ai.CharacterAI.class);
        AIs.put("ai.adept.Adept", l2trunk.scripts.ai.adept.Adept.class);

        AIs.put("ai.adept.AdeptAden", l2trunk.scripts.ai.adept.AdeptAden.class);
        AIs.put("ai.adept.AdeptGiran", l2trunk.scripts.ai.adept.AdeptGiran.class);
        AIs.put("ai.adept.AdeptGiran1", l2trunk.scripts.ai.adept.AdeptGiran1.class);
        AIs.put("ai.adept.AdeptGiran2", l2trunk.scripts.ai.adept.AdeptGiran2.class);
        AIs.put("ai.adept.AdeptGiran3", l2trunk.scripts.ai.adept.AdeptGiran3.class);
        AIs.put("ai.adept.AdeptGiran4", l2trunk.scripts.ai.adept.AdeptGiran4.class);
        AIs.put("ai.adept.AdeptGludio", l2trunk.scripts.ai.adept.AdeptGludio.class);
        AIs.put("ai.adept.AdeptRune", l2trunk.scripts.ai.adept.AdeptRune.class);
        AIs.put("ai.Aenkinel", l2trunk.scripts.ai.Aenkinel.class);
        AIs.put("ai.AirshipGuard1", l2trunk.scripts.ai.AirshipGuard1.class);
        AIs.put("ai.AirshipGuard2", l2trunk.scripts.ai.AirshipGuard2.class);
        AIs.put("ai.Alhena", l2trunk.scripts.ai.Alhena.class);
        AIs.put("ai.Anais", l2trunk.scripts.ai.Anais.class);
        AIs.put("ai.AngerOfSplendor", l2trunk.scripts.ai.AngerOfSplendor.class);
        AIs.put("ai.Antharas", l2trunk.scripts.ai.Antharas.class);
        AIs.put("ai.Archangel", l2trunk.scripts.ai.Archangel.class);
        AIs.put("ai.AttackMobNotPlayerFighter", l2trunk.scripts.ai.AttackMobNotPlayerFighter.class);
        AIs.put("ai.AwakenedMucrokian", l2trunk.scripts.ai.AwakenedMucrokian.class);
        AIs.put("ai.Baium", l2trunk.scripts.ai.Baium.class);
        AIs.put("ai.BaiumNpc", l2trunk.scripts.ai.BaiumNpc.class);
        AIs.put("ai.Baylor", l2trunk.scripts.ai.Baylor.class);
        AIs.put("ai.BlacksmithMammon", l2trunk.scripts.ai.BlacksmithMammon.class);
        AIs.put("ai.BladeOfSplendor", l2trunk.scripts.ai.BladeOfSplendor.class);
        AIs.put("ai.CabaleBuffer", l2trunk.scripts.ai.CabaleBuffer.class);
        AIs.put("ai.CaughtFighter", l2trunk.scripts.ai.CaughtFighter.class);
        AIs.put("ai.CaughtMystic", l2trunk.scripts.ai.CaughtMystic.class);
        AIs.put("ai.ClawsOfSplendor", l2trunk.scripts.ai.ClawsOfSplendor.class);
        AIs.put("ai.ContaminatedMucrokian", l2trunk.scripts.ai.ContaminatedMucrokian.class);
        AIs.put("ai.Core", l2trunk.scripts.ai.Core.class);
        AIs.put("ai.crypts_of_disgrace.ContaminatedBaturCommander", l2trunk.scripts.ai.crypts_of_disgrace.ContaminatedBaturCommander.class);
        AIs.put("ai.crypts_of_disgrace.TurkaCommanderChief", l2trunk.scripts.ai.crypts_of_disgrace.TurkaCommanderChief.class);
        AIs.put("ai.CrystallineGolem", l2trunk.scripts.ai.CrystallineGolem.class);
        AIs.put("ai.custom.FreyaEventAI", l2trunk.scripts.ai.custom.FreyaEventAI.class);
        AIs.put("ai.custom.GvGBoss", l2trunk.scripts.ai.custom.GvGBoss.class);
        AIs.put("ai.custom.LabyrinthLostBeholder", l2trunk.scripts.ai.custom.LabyrinthLostBeholder.class);
        AIs.put("ai.custom.LabyrinthLostWarden", l2trunk.scripts.ai.custom.LabyrinthLostWarden.class);
        AIs.put("ai.custom.LabyrinthLostWatcher", l2trunk.scripts.ai.custom.LabyrinthLostWatcher.class);
        AIs.put("ai.custom.MutantChest", l2trunk.scripts.ai.custom.MutantChest.class);
        AIs.put("ai.custom.Scrubwoman", l2trunk.scripts.ai.custom.Scrubwoman.class);
        AIs.put("ai.custom.SSQAnakim", l2trunk.scripts.ai.custom.SSQAnakim.class);
        AIs.put("ai.custom.SSQAnakimMinion", l2trunk.scripts.ai.custom.SSQAnakimMinion.class);
        AIs.put("ai.custom.SSQLilimServantFighter", l2trunk.scripts.ai.custom.SSQLilimServantFighter.class);
        AIs.put("ai.custom.SSQLilimServantMage", l2trunk.scripts.ai.custom.SSQLilimServantMage.class);
        AIs.put("ai.custom.SSQLilith", l2trunk.scripts.ai.custom.SSQLilith.class);
        AIs.put("ai.custom.SSQLilithMinion", l2trunk.scripts.ai.custom.SSQLilithMinion.class);
        AIs.put("ai.DaimonTheWhiteEyed", l2trunk.scripts.ai.DaimonTheWhiteEyed.class);
        AIs.put("ai.DeluLizardmanSpecialAgent", l2trunk.scripts.ai.DeluLizardmanSpecialAgent.class);
        AIs.put("ai.DeluLizardmanSpecialCommander", l2trunk.scripts.ai.DeluLizardmanSpecialCommander.class);
        AIs.put("ai.den_of_evil.HestuiGuard", l2trunk.scripts.ai.den_of_evil.HestuiGuard.class);
        AIs.put("ai.door.ResidenceDoor", l2trunk.scripts.ai.door.ResidenceDoor.class);
        AIs.put("ai.door.SiegeDoor", l2trunk.scripts.ai.door.SiegeDoor.class);
        AIs.put("ai.door.SSQDoor", l2trunk.scripts.ai.door.SSQDoor.class);
        AIs.put("ai.dragonvalley.BatwingDrake", l2trunk.scripts.ai.dragonvalley.BatwingDrake.class);
        AIs.put("ai.dragonvalley.DragonKnight", l2trunk.scripts.ai.dragonvalley.DragonKnight.class);
        AIs.put("ai.dragonvalley.DragonRaid", l2trunk.scripts.ai.dragonvalley.DragonRaid.class);
        AIs.put("ai.dragonvalley.DragonScout", l2trunk.scripts.ai.dragonvalley.DragonScout.class);
        AIs.put("ai.dragonvalley.DragonTracker", l2trunk.scripts.ai.dragonvalley.DragonTracker.class);
        AIs.put("ai.dragonvalley.DrakeBosses", l2trunk.scripts.ai.dragonvalley.DrakeBosses.class);
        AIs.put("ai.dragonvalley.DrakeMagma", l2trunk.scripts.ai.dragonvalley.DrakeMagma.class);
        AIs.put("ai.dragonvalley.DrakeRunners", l2trunk.scripts.ai.dragonvalley.DrakeRunners.class);
        AIs.put("ai.dragonvalley.DrakosHunter", l2trunk.scripts.ai.dragonvalley.DrakosHunter.class);
        AIs.put("ai.dragonvalley.DrakosWarrior", l2trunk.scripts.ai.dragonvalley.DrakosWarrior.class);
        AIs.put("ai.dragonvalley.DustTracker", l2trunk.scripts.ai.dragonvalley.DustTracker.class);
        AIs.put("ai.dragonvalley.EmeraldDrake", l2trunk.scripts.ai.dragonvalley.EmeraldDrake.class);
        AIs.put("ai.dragonvalley.ExplodingOrcGhost", l2trunk.scripts.ai.dragonvalley.ExplodingOrcGhost.class);
        AIs.put("ai.dragonvalley.Howl", l2trunk.scripts.ai.dragonvalley.Howl.class);
        AIs.put("ai.dragonvalley.Knoriks", l2trunk.scripts.ai.dragonvalley.Knoriks.class);
        AIs.put("ai.dragonvalley.Knoriks1", l2trunk.scripts.ai.dragonvalley.Knoriks1.class);
        AIs.put("ai.dragonvalley.Knoriks2", l2trunk.scripts.ai.dragonvalley.Knoriks2.class);
        AIs.put("ai.dragonvalley.Knoriks3", l2trunk.scripts.ai.dragonvalley.Knoriks3.class);
        AIs.put("ai.dragonvalley.Knoriks4", l2trunk.scripts.ai.dragonvalley.Knoriks4.class);
        AIs.put("ai.dragonvalley.Knoriks5", l2trunk.scripts.ai.dragonvalley.Knoriks5.class);
        AIs.put("ai.dragonvalley.Necromancer", l2trunk.scripts.ai.dragonvalley.Necromancer.class);
        AIs.put("ai.dragonvalley.Patrollers", l2trunk.scripts.ai.dragonvalley.Patrollers.class);
        AIs.put("ai.dragonvalley.PatrollersNoWatch", l2trunk.scripts.ai.dragonvalley.PatrollersNoWatch.class);
        AIs.put("ai.dragonvalley.SandTracker", l2trunk.scripts.ai.dragonvalley.SandTracker.class);
        AIs.put("ai.DrakosWarrior", l2trunk.scripts.ai.DrakosWarrior.class);
        AIs.put("ai.Edwin", l2trunk.scripts.ai.Edwin.class);
        AIs.put("ai.EdwinFollower", l2trunk.scripts.ai.EdwinFollower.class);
        AIs.put("ai.ElcardiaAssistant", l2trunk.scripts.ai.ElcardiaAssistant.class);
        AIs.put("ai.Elpy", l2trunk.scripts.ai.Elpy.class);
        AIs.put("ai.EtisEtina", l2trunk.scripts.ai.EtisEtina.class);
        AIs.put("ai.EvasGiftBox", l2trunk.scripts.ai.EvasGiftBox.class);
        AIs.put("ai.events.SpecialTree", l2trunk.scripts.ai.events.SpecialTree.class);
        AIs.put("ai.EvilNpc", l2trunk.scripts.ai.EvilNpc.class);
        AIs.put("ai.EvilSpiritsMagicForce", l2trunk.scripts.ai.EvilSpiritsMagicForce.class);
        AIs.put("ai.FangOfSplendor", l2trunk.scripts.ai.FangOfSplendor.class);
        AIs.put("ai.FantasyIslePaddies", l2trunk.scripts.ai.FantasyIslePaddies.class);
        AIs.put("ai.FieldMachine", l2trunk.scripts.ai.FieldMachine.class);
        AIs.put("ai.fog.GroupAI", l2trunk.scripts.ai.fog.GroupAI.class);
        AIs.put("ai.fog.TarBeetle", l2trunk.scripts.ai.fog.TarBeetle.class);
        AIs.put("ai.FollowNpc", l2trunk.scripts.ai.FollowNpc.class);
        AIs.put("ai.FortuneBug", l2trunk.scripts.ai.FortuneBug.class);
        AIs.put("ai.freya.AnnihilationFighter", l2trunk.scripts.ai.freya.AnnihilationFighter.class);
        AIs.put("ai.freya.AntharasMinion", l2trunk.scripts.ai.freya.AntharasMinion.class);
        AIs.put("ai.freya.FreyaQuest", l2trunk.scripts.ai.freya.FreyaQuest.class);
        AIs.put("ai.freya.FreyaStandHard", l2trunk.scripts.ai.freya.FreyaStandHard.class);
        AIs.put("ai.freya.FreyaStandNormal", l2trunk.scripts.ai.freya.FreyaStandNormal.class);
        AIs.put("ai.freya.FreyaThrone", l2trunk.scripts.ai.freya.FreyaThrone.class);
        AIs.put("ai.freya.Glacier", l2trunk.scripts.ai.freya.Glacier.class);
        AIs.put("ai.freya.IceCaptainKnight", l2trunk.scripts.ai.freya.IceCaptainKnight.class);
        AIs.put("ai.freya.IceCastleBreath", l2trunk.scripts.ai.freya.IceCastleBreath.class);
        AIs.put("ai.freya.IceKnightNormal", l2trunk.scripts.ai.freya.IceKnightNormal.class);
        AIs.put("ai.freya.JiniaGuild", l2trunk.scripts.ai.freya.JiniaGuild.class);
        AIs.put("ai.freya.JiniaKnight", l2trunk.scripts.ai.freya.JiniaKnight.class);
        AIs.put("ai.freya.Maguen", l2trunk.scripts.ai.freya.Maguen.class);
        AIs.put("ai.freya.SeerUgoros", l2trunk.scripts.ai.freya.SeerUgoros.class);
        AIs.put("ai.freya.SolinaKnight", l2trunk.scripts.ai.freya.SolinaKnight.class);
        AIs.put("ai.freya.ValakasMinion", l2trunk.scripts.ai.freya.ValakasMinion.class);
        AIs.put("ai.FrightenedOrc", l2trunk.scripts.ai.FrightenedOrc.class);
        AIs.put("ai.FrostBuffalo", l2trunk.scripts.ai.FrostBuffalo.class);
        AIs.put("ai.Furance", l2trunk.scripts.ai.Furance.class);
        AIs.put("ai.Furnace", l2trunk.scripts.ai.Furnace.class);
        AIs.put("ai.Gargos", l2trunk.scripts.ai.Gargos.class);
        AIs.put("ai.GatekeeperZombie", l2trunk.scripts.ai.GatekeeperZombie.class);
        AIs.put("ai.GeneralDilios", l2trunk.scripts.ai.GeneralDilios.class);
        AIs.put("ai.GhostOfVonHellmannsPage", l2trunk.scripts.ai.GhostOfVonHellmannsPage.class);
        AIs.put("ai.Gordon", l2trunk.scripts.ai.Gordon.class);
        AIs.put("ai.GraveRobberSummoner", l2trunk.scripts.ai.GraveRobberSummoner.class);
//        AIs.put("ai.groups.FlyingGracia",l2trunk.scripts.ai.groups.FlyingGracia.class);
        AIs.put("ai.groups.ForgeoftheGods", l2trunk.scripts.ai.groups.ForgeoftheGods.class);
        AIs.put("ai.groups.PavelRuins", l2trunk.scripts.ai.groups.PavelRuins.class);
        AIs.put("ai.groups.StakatoNest", l2trunk.scripts.ai.groups.StakatoNest.class);
        AIs.put("ai.GuardianAltar", l2trunk.scripts.ai.GuardianAltar.class);
        AIs.put("ai.GuardianAngel", l2trunk.scripts.ai.GuardianAngel.class);
        AIs.put("ai.GuardianWaterspirit", l2trunk.scripts.ai.GuardianWaterspirit.class);
        AIs.put("ai.GuardofDawn", l2trunk.scripts.ai.GuardofDawn.class);
        AIs.put("ai.GuardofDawnFemale", l2trunk.scripts.ai.GuardofDawnFemale.class);
        AIs.put("ai.GuardofDawnStat", l2trunk.scripts.ai.GuardofDawnStat.class);
        AIs.put("ai.GuardoftheGrave", l2trunk.scripts.ai.GuardoftheGrave.class);
        AIs.put("ai.GuardRndWalkAndAnim", l2trunk.scripts.ai.GuardRndWalkAndAnim.class);
        AIs.put("ai.HandysBlock", l2trunk.scripts.ai.HandysBlock.class);
        AIs.put("ai.HekatonPrime", l2trunk.scripts.ai.HekatonPrime.class);
        AIs.put("ai.hellbound.Beleth", l2trunk.scripts.ai.hellbound.Beleth.class);
        AIs.put("ai.hellbound.BelethClone", l2trunk.scripts.ai.hellbound.BelethClone.class);
        AIs.put("ai.hellbound.Chimera", l2trunk.scripts.ai.hellbound.Chimera.class);
        AIs.put("ai.hellbound.CoralGardenGolem", l2trunk.scripts.ai.hellbound.CoralGardenGolem.class);
        AIs.put("ai.hellbound.Darion", l2trunk.scripts.ai.hellbound.Darion.class);
        AIs.put("ai.hellbound.DarionChallenger", l2trunk.scripts.ai.hellbound.DarionChallenger.class);
        AIs.put("ai.hellbound.DarionFaithfulServant", l2trunk.scripts.ai.hellbound.DarionFaithfulServant.class);
        AIs.put("ai.hellbound.DarionFaithfulServant6Floor", l2trunk.scripts.ai.hellbound.DarionFaithfulServant6Floor.class);
        AIs.put("ai.hellbound.DarionFaithfulServant8Floor", l2trunk.scripts.ai.hellbound.DarionFaithfulServant8Floor.class);
        AIs.put("ai.hellbound.Darnel", l2trunk.scripts.ai.hellbound.Darnel.class);
        AIs.put("ai.hellbound.DemonPrince", l2trunk.scripts.ai.hellbound.DemonPrince.class);
        AIs.put("ai.hellbound.Epidos", l2trunk.scripts.ai.hellbound.Epidos.class);
        AIs.put("ai.hellbound.FloatingGhost", l2trunk.scripts.ai.hellbound.FloatingGhost.class);
        AIs.put("ai.hellbound.FoundryWorker", l2trunk.scripts.ai.hellbound.FoundryWorker.class);
        AIs.put("ai.hellbound.GreaterEvil", l2trunk.scripts.ai.hellbound.GreaterEvil.class);
        AIs.put("ai.hellbound.Leodas", l2trunk.scripts.ai.hellbound.Leodas.class);
        AIs.put("ai.hellbound.MasterFestina", l2trunk.scripts.ai.hellbound.MasterFestina.class);
        AIs.put("ai.hellbound.MasterZelos", l2trunk.scripts.ai.hellbound.MasterZelos.class);
        AIs.put("ai.hellbound.MutatedElpy", l2trunk.scripts.ai.hellbound.MutatedElpy.class);
        AIs.put("ai.hellbound.NaiaCube", l2trunk.scripts.ai.hellbound.NaiaCube.class);
        AIs.put("ai.hellbound.NaiaLock", l2trunk.scripts.ai.hellbound.NaiaLock.class);
        AIs.put("ai.hellbound.NaiaRoomController", l2trunk.scripts.ai.hellbound.NaiaRoomController.class);
        AIs.put("ai.hellbound.NaiaSpore", l2trunk.scripts.ai.hellbound.NaiaSpore.class);
        AIs.put("ai.hellbound.OriginalSinWarden", l2trunk.scripts.ai.hellbound.OriginalSinWarden.class);
        AIs.put("ai.hellbound.OriginalSinWarden6Floor", l2trunk.scripts.ai.hellbound.OriginalSinWarden6Floor.class);
        AIs.put("ai.hellbound.OriginalSinWarden8Floor", l2trunk.scripts.ai.hellbound.OriginalSinWarden8Floor.class);
        AIs.put("ai.hellbound.OutpostCaptain", l2trunk.scripts.ai.hellbound.OutpostCaptain.class);
        AIs.put("ai.hellbound.OutpostGuards", l2trunk.scripts.ai.hellbound.OutpostGuards.class);
        AIs.put("ai.hellbound.Pylon", l2trunk.scripts.ai.hellbound.Pylon.class);
        AIs.put("ai.hellbound.Ranku", l2trunk.scripts.ai.hellbound.Ranku.class);
        AIs.put("ai.hellbound.RankuScapegoat", l2trunk.scripts.ai.hellbound.RankuScapegoat.class);
        AIs.put("ai.hellbound.Sandstorm", l2trunk.scripts.ai.hellbound.Sandstorm.class);
        AIs.put("ai.hellbound.SteelCitadelKeymaster", l2trunk.scripts.ai.hellbound.SteelCitadelKeymaster.class);
        AIs.put("ai.hellbound.TorturedNative", l2trunk.scripts.ai.hellbound.TorturedNative.class);
        AIs.put("ai.hellbound.TownGuard", l2trunk.scripts.ai.hellbound.TownGuard.class);
        AIs.put("ai.hellbound.Tully", l2trunk.scripts.ai.hellbound.Tully.class);
        AIs.put("ai.hellbound.Typhoon", l2trunk.scripts.ai.hellbound.Typhoon.class);
        AIs.put("ai.HotSpringsMob", l2trunk.scripts.ai.HotSpringsMob.class);
        AIs.put("ai.isle_of_prayer.DarkWaterDragon", l2trunk.scripts.ai.isle_of_prayer.DarkWaterDragon.class);
        AIs.put("ai.isle_of_prayer.EmeraldDoorController", l2trunk.scripts.ai.isle_of_prayer.EmeraldDoorController.class);
        AIs.put("ai.isle_of_prayer.EvasProtector", l2trunk.scripts.ai.isle_of_prayer.EvasProtector.class);
        AIs.put("ai.isle_of_prayer.FafurionKindred", l2trunk.scripts.ai.isle_of_prayer.FafurionKindred.class);
        AIs.put("ai.isle_of_prayer.IsleOfPrayerFighter", l2trunk.scripts.ai.isle_of_prayer.IsleOfPrayerFighter.class);
        AIs.put("ai.isle_of_prayer.IsleOfPrayerMystic", l2trunk.scripts.ai.isle_of_prayer.IsleOfPrayerMystic.class);
        AIs.put("ai.isle_of_prayer.Kechi", l2trunk.scripts.ai.isle_of_prayer.Kechi.class);
        AIs.put("ai.isle_of_prayer.Shade", l2trunk.scripts.ai.isle_of_prayer.Shade.class);
        AIs.put("ai.isle_of_prayer.WaterDragonDetractor", l2trunk.scripts.ai.isle_of_prayer.WaterDragonDetractor.class);
        AIs.put("ai.Jaradine", l2trunk.scripts.ai.Jaradine.class);
        AIs.put("ai.Kama56Boss", l2trunk.scripts.ai.Kama56Boss.class);
        AIs.put("ai.Kama56Minion", l2trunk.scripts.ai.Kama56Minion.class);
        AIs.put("ai.Kama63Minion", l2trunk.scripts.ai.Kama63Minion.class);
        AIs.put("ai.Kanabion", l2trunk.scripts.ai.Kanabion.class);
        AIs.put("ai.KanadisFollower", l2trunk.scripts.ai.KanadisFollower.class);
        AIs.put("ai.KanadisGuide", l2trunk.scripts.ai.KanadisGuide.class);
        AIs.put("ai.KarulBugbear", l2trunk.scripts.ai.KarulBugbear.class);
        AIs.put("ai.KashasEye", l2trunk.scripts.ai.KashasEye.class);
        AIs.put("ai.Kasiel", l2trunk.scripts.ai.Kasiel.class);
        AIs.put("ai.KrateisCubeWatcherBlue", l2trunk.scripts.ai.KrateisCubeWatcherBlue.class);
        AIs.put("ai.KrateisCubeWatcherRed", l2trunk.scripts.ai.KrateisCubeWatcherRed.class);
        AIs.put("ai.KrateisFighter", l2trunk.scripts.ai.KrateisFighter.class);
        AIs.put("ai.Kreed", l2trunk.scripts.ai.Kreed.class);
        AIs.put("ai.LafiLakfi", l2trunk.scripts.ai.LafiLakfi.class);
        AIs.put("ai.Leandro", l2trunk.scripts.ai.Leandro.class);
        AIs.put("ai.Leogul", l2trunk.scripts.ai.Leogul.class);
        AIs.put("ai.LeylaDancer", l2trunk.scripts.ai.LeylaDancer.class);
        AIs.put("ai.LeylaMira", l2trunk.scripts.ai.LeylaMira.class);
        AIs.put("ai.LizardmanSummoner", l2trunk.scripts.ai.LizardmanSummoner.class);
        AIs.put("ai.MasterYogi", l2trunk.scripts.ai.MasterYogi.class);
        AIs.put("ai.MCIndividual", l2trunk.scripts.ai.MCIndividual.class);
        AIs.put("ai.MCManager", l2trunk.scripts.ai.MCManager.class);
        AIs.put("ai.monas.FurnaceSpawnRoom.DivinityMonster", l2trunk.scripts.ai.monas.FurnaceSpawnRoom.DivinityMonster.class);
        AIs.put("ai.monas.FurnaceSpawnRoom.FurnaceBalance", l2trunk.scripts.ai.monas.FurnaceSpawnRoom.FurnaceBalance.class);
        AIs.put("ai.monas.FurnaceSpawnRoom.FurnaceMagic", l2trunk.scripts.ai.monas.FurnaceSpawnRoom.FurnaceMagic.class);
        AIs.put("ai.monas.FurnaceSpawnRoom.FurnaceProtection", l2trunk.scripts.ai.monas.FurnaceSpawnRoom.FurnaceProtection.class);
        AIs.put("ai.monas.FurnaceSpawnRoom.FurnaceWill", l2trunk.scripts.ai.monas.FurnaceSpawnRoom.FurnaceWill.class);
        AIs.put("ai.monas.Furnface", l2trunk.scripts.ai.monas.Furnface.class);
        AIs.put("ai.monastery_of_silence.DivinityMonster", l2trunk.scripts.ai.monastery_of_silence.DivinityMonster.class);
        AIs.put("ai.MoSMonk", l2trunk.scripts.ai.MoSMonk.class);
        AIs.put("ai.Mucrokian", l2trunk.scripts.ai.Mucrokian.class);
        AIs.put("ai.MusicBox", l2trunk.scripts.ai.MusicBox.class);
        AIs.put("ai.NightAgressionMystic", l2trunk.scripts.ai.NightAgressionMystic.class);
        AIs.put("ai.NihilInvaderChest", l2trunk.scripts.ai.NihilInvaderChest.class);
        AIs.put("ai.OiAriosh", l2trunk.scripts.ai.OiAriosh.class);
        AIs.put("ai.OlMahumGeneral", l2trunk.scripts.ai.OlMahumGeneral.class);
        AIs.put("ai.Orfen", l2trunk.scripts.ai.Orfen.class);
        AIs.put("ai.Orfen_RibaIren", l2trunk.scripts.ai.Orfen_RibaIren.class);
        AIs.put("ai.other.PailakaDevilsLegacy.FollowersLematan", l2trunk.scripts.ai.other.PailakaDevilsLegacy.FollowersLematan.class);
        AIs.put("ai.other.PailakaDevilsLegacy.Lematan", l2trunk.scripts.ai.other.PailakaDevilsLegacy.Lematan.class);
        AIs.put("ai.other.PailakaDevilsLegacy.PowderKeg", l2trunk.scripts.ai.other.PailakaDevilsLegacy.PowderKeg.class);
        AIs.put("ai.PaganGuard", l2trunk.scripts.ai.PaganGuard.class);
        AIs.put("ai.PaganTemplete.AltarGatekeeper", l2trunk.scripts.ai.PaganTemplete.AltarGatekeeper.class);
        AIs.put("ai.PaganTemplete.AndreasCaptainRoyalGuard", l2trunk.scripts.ai.PaganTemplete.AndreasCaptainRoyalGuard.class);
        AIs.put("ai.PaganTemplete.AndreasVanHalter", l2trunk.scripts.ai.PaganTemplete.AndreasVanHalter.class);
        AIs.put("ai.PaganTemplete.TriolsBeliever", l2trunk.scripts.ai.PaganTemplete.TriolsBeliever.class);
        AIs.put("ai.PaganTemplete.TriolsLayperson", l2trunk.scripts.ai.PaganTemplete.TriolsLayperson.class);
        AIs.put("ai.PiratesKing", l2trunk.scripts.ai.PiratesKing.class);
        AIs.put("ai.primeval_isle.SprigantPoison", l2trunk.scripts.ai.primeval_isle.SprigantPoison.class);
        AIs.put("ai.primeval_isle.SprigantStun", l2trunk.scripts.ai.primeval_isle.SprigantStun.class);
        AIs.put("ai.PrisonGuard", l2trunk.scripts.ai.PrisonGuard.class);
        AIs.put("ai.Pronghorn", l2trunk.scripts.ai.Pronghorn.class);
        AIs.put("ai.Pterosaur", l2trunk.scripts.ai.Pterosaur.class);
        AIs.put("ai.QueenAntNurse", l2trunk.scripts.ai.QueenAntNurse.class);
        AIs.put("ai.Quest024Fighter", l2trunk.scripts.ai.Quest024Fighter.class);
        AIs.put("ai.Quest024Mystic", l2trunk.scripts.ai.Quest024Mystic.class);
        AIs.put("ai.Quest421FairyTree", l2trunk.scripts.ai.Quest421FairyTree.class);
        AIs.put("ai.QuestNotAggroMob", l2trunk.scripts.ai.QuestNotAggroMob.class);
        AIs.put("ai.RagnaHealer", l2trunk.scripts.ai.RagnaHealer.class);
        AIs.put("ai.Remy", l2trunk.scripts.ai.Remy.class);
        AIs.put("ai.residences.castle.ArtefactAI", l2trunk.scripts.ai.residences.castle.ArtefactAI.class);
        AIs.put("ai.residences.castle.Venom", l2trunk.scripts.ai.residences.castle.Venom.class);
        AIs.put("ai.residences.clanhall.AlfredVonHellmann", l2trunk.scripts.ai.residences.clanhall.AlfredVonHellmann.class);
        AIs.put("ai.residences.clanhall.GiselleVonHellmann", l2trunk.scripts.ai.residences.clanhall.GiselleVonHellmann.class);
        AIs.put("ai.residences.clanhall.LidiaVonHellmann", l2trunk.scripts.ai.residences.clanhall.LidiaVonHellmann.class);
        AIs.put("ai.residences.clanhall.MatchBerserker", l2trunk.scripts.ai.residences.clanhall.MatchBerserker.class);
        AIs.put("ai.residences.clanhall.MatchCleric", l2trunk.scripts.ai.residences.clanhall.MatchCleric.class);
        AIs.put("ai.residences.clanhall.MatchFighter", l2trunk.scripts.ai.residences.clanhall.MatchFighter.class);
        AIs.put("ai.residences.clanhall.MatchLeader", l2trunk.scripts.ai.residences.clanhall.MatchLeader.class);
        AIs.put("ai.residences.clanhall.MatchScout", l2trunk.scripts.ai.residences.clanhall.MatchScout.class);
        AIs.put("ai.residences.clanhall.MatchTrief", l2trunk.scripts.ai.residences.clanhall.MatchTrief.class);
        AIs.put("ai.residences.clanhall.RainbowEnragedYeti", l2trunk.scripts.ai.residences.clanhall.RainbowEnragedYeti.class);
        AIs.put("ai.residences.clanhall.RainbowYeti", l2trunk.scripts.ai.residences.clanhall.RainbowYeti.class);
        AIs.put("ai.residences.dominion.Catapult", l2trunk.scripts.ai.residences.dominion.Catapult.class);
        AIs.put("ai.residences.dominion.EconomicAssociationLeader", l2trunk.scripts.ai.residences.dominion.EconomicAssociationLeader.class);
        AIs.put("ai.residences.dominion.MercenaryCaptain", l2trunk.scripts.ai.residences.dominion.MercenaryCaptain.class);
        AIs.put("ai.residences.dominion.MilitaryAssociationLeader", l2trunk.scripts.ai.residences.dominion.MilitaryAssociationLeader.class);
        AIs.put("ai.residences.dominion.ReligiousAssociationLeader", l2trunk.scripts.ai.residences.dominion.ReligiousAssociationLeader.class);
        AIs.put("ai.residences.dominion.SuppliesSafe", l2trunk.scripts.ai.residences.dominion.SuppliesSafe.class);
        AIs.put("ai.residences.fortress.siege.ArcherCaption", l2trunk.scripts.ai.residences.fortress.siege.ArcherCaption.class);
        AIs.put("ai.residences.fortress.siege.Ballista", l2trunk.scripts.ai.residences.fortress.siege.Ballista.class);
        AIs.put("ai.residences.fortress.siege.General", l2trunk.scripts.ai.residences.fortress.siege.General.class);
        AIs.put("ai.residences.fortress.siege.GuardCaption", l2trunk.scripts.ai.residences.fortress.siege.GuardCaption.class);
        AIs.put("ai.residences.fortress.siege.MercenaryCaption", l2trunk.scripts.ai.residences.fortress.siege.MercenaryCaption.class);
        AIs.put("ai.residences.fortress.siege.Minister", l2trunk.scripts.ai.residences.fortress.siege.Minister.class);
        AIs.put("ai.residences.fortress.siege.RebelCommander", l2trunk.scripts.ai.residences.fortress.siege.RebelCommander.class);
        AIs.put("ai.residences.fortress.siege.SupportUnitCaption", l2trunk.scripts.ai.residences.fortress.siege.SupportUnitCaption.class);
        AIs.put("ai.residences.SiegeGuard", l2trunk.scripts.ai.residences.SiegeGuard.class);
        AIs.put("ai.residences.SiegeGuardFighter", l2trunk.scripts.ai.residences.SiegeGuardFighter.class);
        AIs.put("ai.residences.SiegeGuardMystic", l2trunk.scripts.ai.residences.SiegeGuardMystic.class);
        AIs.put("ai.residences.SiegeGuardPriest", l2trunk.scripts.ai.residences.SiegeGuardPriest.class);
        AIs.put("ai.residences.SiegeGuardRanger", l2trunk.scripts.ai.residences.SiegeGuardRanger.class);
        AIs.put("ai.RndTeleportFighter", l2trunk.scripts.ai.RndTeleportFighter.class);
        AIs.put("ai.RndWalkAndAnim", l2trunk.scripts.ai.RndWalkAndAnim.class);
        AIs.put("ai.Rogin", l2trunk.scripts.ai.Rogin.class);
        AIs.put("ai.Rokar", l2trunk.scripts.ai.Rokar.class);
        AIs.put("ai.Rooney", l2trunk.scripts.ai.Rooney.class);
        AIs.put("ai.Scarecrow", l2trunk.scripts.ai.Scarecrow.class);
        AIs.put("ai.SealDevice", l2trunk.scripts.ai.SealDevice.class);
        AIs.put("ai.SeducedInvestigator", l2trunk.scripts.ai.SeducedInvestigator.class);
        AIs.put("ai.seedofdestruction.DimensionMovingDevice", l2trunk.scripts.ai.seedofdestruction.DimensionMovingDevice.class);
        AIs.put("ai.seedofdestruction.GreatPowerfulDevice", l2trunk.scripts.ai.seedofdestruction.GreatPowerfulDevice.class);
        AIs.put("ai.seedofdestruction.Obelisk", l2trunk.scripts.ai.seedofdestruction.Obelisk.class);
        AIs.put("ai.seedofdestruction.ThroneofDestruction", l2trunk.scripts.ai.seedofdestruction.ThroneofDestruction.class);
        AIs.put("ai.seedofdestruction.Tiat", l2trunk.scripts.ai.seedofdestruction.Tiat.class);
        AIs.put("ai.seedofdestruction.TiatCamera", l2trunk.scripts.ai.seedofdestruction.TiatCamera.class);
        AIs.put("ai.seedofdestruction.TiatsTrap", l2trunk.scripts.ai.seedofdestruction.TiatsTrap.class);
        AIs.put("ai.seedofinfinity.AliveTumor", l2trunk.scripts.ai.seedofinfinity.AliveTumor.class);
        AIs.put("ai.seedofinfinity.Ekimus", l2trunk.scripts.ai.seedofinfinity.Ekimus.class);
        AIs.put("ai.seedofinfinity.EkimusFood", l2trunk.scripts.ai.seedofinfinity.EkimusFood.class);
        AIs.put("ai.seedofinfinity.FeralHound", l2trunk.scripts.ai.seedofinfinity.FeralHound.class);
        AIs.put("ai.seedofinfinity.SoulCoffin", l2trunk.scripts.ai.seedofinfinity.SoulCoffin.class);
        AIs.put("ai.seedofinfinity.SymbolofCohemenes", l2trunk.scripts.ai.seedofinfinity.SymbolofCohemenes.class);
        AIs.put("ai.seedofinfinity.WardofDeath", l2trunk.scripts.ai.seedofinfinity.WardofDeath.class);
        AIs.put("ai.seedofinfinity.YehanBrother", l2trunk.scripts.ai.seedofinfinity.YehanBrother.class);
        AIs.put("ai.SeerFlouros", l2trunk.scripts.ai.SeerFlouros.class);
        AIs.put("ai.selmahum.DrillSergeant", l2trunk.scripts.ai.selmahum.DrillSergeant.class);
        AIs.put("ai.selmahum.Fireplace", l2trunk.scripts.ai.selmahum.Fireplace.class);
        AIs.put("ai.selmahum.SelChef", l2trunk.scripts.ai.selmahum.SelChef.class);
        AIs.put("ai.selmahum.SelSquadLeader", l2trunk.scripts.ai.selmahum.SelSquadLeader.class);
        AIs.put("ai.SkyshadowMeadow.DrillSergeant", l2trunk.scripts.ai.SkyshadowMeadow.DrillSergeant.class);
        AIs.put("ai.SkyshadowMeadow.Fire", l2trunk.scripts.ai.SkyshadowMeadow.Fire.class);
        AIs.put("ai.SkyshadowMeadow.FireFeed", l2trunk.scripts.ai.SkyshadowMeadow.FireFeed.class);
        AIs.put("ai.SkyshadowMeadow.SelMahumRecruit", l2trunk.scripts.ai.SkyshadowMeadow.SelMahumRecruit.class);
        AIs.put("ai.SkyshadowMeadow.SelMahumShef", l2trunk.scripts.ai.SkyshadowMeadow.SelMahumShef.class);
        AIs.put("ai.SkyshadowMeadow.SelMahumSquadLeader", l2trunk.scripts.ai.SkyshadowMeadow.SelMahumSquadLeader.class);
        AIs.put("ai.SkyshadowMeadow.SelMahumTrainer", l2trunk.scripts.ai.SkyshadowMeadow.SelMahumTrainer.class);
        AIs.put("ai.SolinaGuardian", l2trunk.scripts.ai.SolinaGuardian.class);
        AIs.put("ai.Suppressor", l2trunk.scripts.ai.Suppressor.class);
        AIs.put("ai.suspiciousmerchant.SuspiciousMerchantAaru", l2trunk.scripts.ai.suspiciousmerchant.SuspiciousMerchantAaru.class);
        AIs.put("ai.suspiciousmerchant.SuspiciousMerchantAntharas", l2trunk.scripts.ai.suspiciousmerchant.SuspiciousMerchantAntharas.class);
        AIs.put("ai.suspiciousmerchant.SuspiciousMerchantArchaic", l2trunk.scripts.ai.suspiciousmerchant.SuspiciousMerchantArchaic.class);
        AIs.put("ai.suspiciousmerchant.SuspiciousMerchantBayou", l2trunk.scripts.ai.suspiciousmerchant.SuspiciousMerchantBayou.class);
        AIs.put("ai.suspiciousmerchant.SuspiciousMerchantBorderland", l2trunk.scripts.ai.suspiciousmerchant.SuspiciousMerchantBorderland.class);
        AIs.put("ai.suspiciousmerchant.SuspiciousMerchantCloud", l2trunk.scripts.ai.suspiciousmerchant.SuspiciousMerchantCloud.class);
        AIs.put("ai.suspiciousmerchant.SuspiciousMerchantDemon", l2trunk.scripts.ai.suspiciousmerchant.SuspiciousMerchantDemon.class);
        AIs.put("ai.suspiciousmerchant.SuspiciousMerchantDragonspine", l2trunk.scripts.ai.suspiciousmerchant.SuspiciousMerchantDragonspine.class);
        AIs.put("ai.suspiciousmerchant.SuspiciousMerchantFloran", l2trunk.scripts.ai.suspiciousmerchant.SuspiciousMerchantFloran.class);
        AIs.put("ai.suspiciousmerchant.SuspiciousMerchantHive", l2trunk.scripts.ai.suspiciousmerchant.SuspiciousMerchantHive.class);
        AIs.put("ai.suspiciousmerchant.SuspiciousMerchantHunters", l2trunk.scripts.ai.suspiciousmerchant.SuspiciousMerchantHunters.class);
        AIs.put("ai.suspiciousmerchant.SuspiciousMerchantIvoryTower", l2trunk.scripts.ai.suspiciousmerchant.SuspiciousMerchantIvoryTower.class);
        AIs.put("ai.suspiciousmerchant.SuspiciousMerchantMarshland", l2trunk.scripts.ai.suspiciousmerchant.SuspiciousMerchantMarshland.class);
        AIs.put("ai.suspiciousmerchant.SuspiciousMerchantMonastic", l2trunk.scripts.ai.suspiciousmerchant.SuspiciousMerchantMonastic.class);
        AIs.put("ai.suspiciousmerchant.SuspiciousMerchantNarsell", l2trunk.scripts.ai.suspiciousmerchant.SuspiciousMerchantNarsell.class);
        AIs.put("ai.suspiciousmerchant.SuspiciousMerchantSGludio", l2trunk.scripts.ai.suspiciousmerchant.SuspiciousMerchantSGludio.class);
        AIs.put("ai.suspiciousmerchant.SuspiciousMerchantShanty", l2trunk.scripts.ai.suspiciousmerchant.SuspiciousMerchantShanty.class);
        AIs.put("ai.suspiciousmerchant.SuspiciousMerchantTanor", l2trunk.scripts.ai.suspiciousmerchant.SuspiciousMerchantTanor.class);
        AIs.put("ai.suspiciousmerchant.SuspiciousMerchantValley", l2trunk.scripts.ai.suspiciousmerchant.SuspiciousMerchantValley.class);
        AIs.put("ai.suspiciousmerchant.SuspiciousMerchantWestern", l2trunk.scripts.ai.suspiciousmerchant.SuspiciousMerchantWestern.class);
        AIs.put("ai.suspiciousmerchant.SuspiciousMerchantWhiteSands", l2trunk.scripts.ai.suspiciousmerchant.SuspiciousMerchantWhiteSands.class);
        AIs.put("ai.TalkingGuard", l2trunk.scripts.ai.TalkingGuard.class);
        AIs.put("ai.Tate", l2trunk.scripts.ai.Tate.class);
        AIs.put("ai.Taurin", l2trunk.scripts.ai.Taurin.class);
        AIs.put("ai.Tears", l2trunk.scripts.ai.Tears.class);
        AIs.put("ai.Thomas", l2trunk.scripts.ai.Thomas.class);
        AIs.put("ai.Tiberias", l2trunk.scripts.ai.Tiberias.class);
        AIs.put("ai.TimakOrcTroopLeader", l2trunk.scripts.ai.TimakOrcTroopLeader.class);
        AIs.put("ai.Toma", l2trunk.scripts.ai.Toma.class);
        AIs.put("ai.TotemSummon", l2trunk.scripts.ai.TotemSummon.class);
        AIs.put("ai.Valakas", l2trunk.scripts.ai.Valakas.class);
        AIs.put("ai.WatchmanMonster", l2trunk.scripts.ai.WatchmanMonster.class);
        AIs.put("ai.WitchWarder", l2trunk.scripts.ai.WitchWarder.class);
        AIs.put("ai.Yakand", l2trunk.scripts.ai.Yakand.class);
        AIs.put("ai.ZakenAnchor", l2trunk.scripts.ai.ZakenAnchor.class);
        AIs.put("ai.ZakenDaytime", l2trunk.scripts.ai.ZakenDaytime.class);
        AIs.put("ai.ZakenDaytime83", l2trunk.scripts.ai.ZakenDaytime83.class);
        AIs.put("ai.ZakenNightly", l2trunk.scripts.ai.ZakenNightly.class);
        AIs.put("ai.Zone.DragonValley.DV_RB.BlackdaggerWing", l2trunk.scripts.ai.Zone.DragonValley.DV_RB.BlackdaggerWing.class);
        AIs.put("ai.Zone.DragonValley.DV_RB.BleedingFly", l2trunk.scripts.ai.Zone.DragonValley.DV_RB.BleedingFly.class);
        AIs.put("ai.Zone.DragonValley.DV_RB.BleedingFlyMinion", l2trunk.scripts.ai.Zone.DragonValley.DV_RB.BleedingFlyMinion.class);
        AIs.put("ai.Zone.DragonValley.DV_RB.DustRider", l2trunk.scripts.ai.Zone.DragonValley.DV_RB.DustRider.class);
        AIs.put("ai.Zone.DragonValley.DV_RB.EmeraldHorn", l2trunk.scripts.ai.Zone.DragonValley.DV_RB.EmeraldHorn.class);
        AIs.put("ai.Zone.DragonValley.DV_RB.MuscleBomber", l2trunk.scripts.ai.Zone.DragonValley.DV_RB.MuscleBomber.class);
        AIs.put("ai.Zone.DragonValley.DV_RB.ShadowSummoner", l2trunk.scripts.ai.Zone.DragonValley.DV_RB.ShadowSummoner.class);
        AIs.put("ai.Zone.DragonValley.DV_RB.SpikeSlasher", l2trunk.scripts.ai.Zone.DragonValley.DV_RB.SpikeSlasher.class);
        AIs.put("ai.Zone.DragonValley.DV_RB.SpikeSlasherMinion", l2trunk.scripts.ai.Zone.DragonValley.DV_RB.SpikeSlasherMinion.class);
//        AIs.put("ai.Zone.HeineFields.HeineFieldsHerbs",l2trunk.scripts.ai.Zone.HeineFields.HeineFieldsHerbs.class);
        AIs.put("ai.Zone.LairOfAntharas.BloodyKarik", l2trunk.scripts.ai.Zone.LairOfAntharas.BloodyKarik.class);


    }

    private void loadNpcInstances() {

        npcInstances.put("PetBabyInstance", l2trunk.gameserver.model.instances.PetBabyInstance.class);
        npcInstances.put("PetInstance", l2trunk.gameserver.model.instances.PetInstance.class);

//        npcInstances.put("residences\",l2trunk.gameserver.model.instances.residences\.class);
        npcInstances.put("AdventurerInstance", l2trunk.gameserver.model.instances.AdventurerInstance.class);
        npcInstances.put("AirShipControllerInstance", l2trunk.gameserver.model.instances.AirShipControllerInstance.class);
        npcInstances.put("ArtefactInstance", l2trunk.gameserver.model.instances.ArtefactInstance.class);
        npcInstances.put("BetaNPCInstance", l2trunk.gameserver.model.instances.BetaNPCInstance.class);
        npcInstances.put("BlockInstance", l2trunk.gameserver.model.instances.BlockInstance.class);
        npcInstances.put("BossInstance", l2trunk.gameserver.model.instances.BossInstance.class);
        npcInstances.put("ChestInstance", l2trunk.gameserver.model.instances.ChestInstance.class);
        npcInstances.put("ClanAirShipControllerInstance", l2trunk.gameserver.model.instances.ClanAirShipControllerInstance.class);
        npcInstances.put("ClanRewardInstance", l2trunk.gameserver.model.instances.ClanRewardInstance.class);
        npcInstances.put("ClanTraderInstance", l2trunk.gameserver.model.instances.ClanTraderInstance.class);
//        npcInstances.put("ControlKeyInstance", l2trunk.gameserver.model.instances.ControlKeyInstance.class);
        npcInstances.put("DeadManInstance", l2trunk.gameserver.model.instances.DeadManInstance.class);
        npcInstances.put("DecoyInstance", l2trunk.gameserver.model.instances.DecoyInstance.class);
        npcInstances.put("DonateNPCInstance", l2trunk.gameserver.model.instances.DonateNPCInstance.class);
//        npcInstances.put("DoorInstance", l2trunk.gameserver.model.instances.DoorInstance.class);
        npcInstances.put("FameManagerInstance", l2trunk.gameserver.model.instances.FameManagerInstance.class);
        npcInstances.put("FeedableBeastInstance", l2trunk.gameserver.model.instances.FeedableBeastInstance.class);
        npcInstances.put("FestivalGuideInstance", l2trunk.gameserver.model.instances.FestivalGuideInstance.class);
        npcInstances.put("FestivalMonsterInstance", l2trunk.gameserver.model.instances.FestivalMonsterInstance.class);
        npcInstances.put("FishermanInstance", l2trunk.gameserver.model.instances.FishermanInstance.class);
        npcInstances.put("FurnaceInstance", l2trunk.gameserver.model.instances.FurnaceInstance.class);
        npcInstances.put("GuardInstance", l2trunk.gameserver.model.instances.GuardInstance.class);
        npcInstances.put("ItemAuctionBrokerInstance", l2trunk.gameserver.model.instances.ItemAuctionBrokerInstance.class);
        npcInstances.put("LotteryManagerInstance", l2trunk.gameserver.model.instances.LotteryManagerInstance.class);
        npcInstances.put("ManorManagerInstance", l2trunk.gameserver.model.instances.ManorManagerInstance.class);
        npcInstances.put("MerchantInstance", l2trunk.gameserver.model.instances.MerchantInstance.class);
        npcInstances.put("MercManagerInstance", l2trunk.gameserver.model.instances.MercManagerInstance.class);
        npcInstances.put("MinionInstance", l2trunk.gameserver.model.instances.MinionInstance.class);
        npcInstances.put("MonsterInstance", l2trunk.gameserver.model.instances.MonsterInstance.class);
//        npcInstances.put("NoActionNpcInstance", l2trunk.gameserver.model.instances.NoActionNpcInstance.class);
        npcInstances.put("NpcFriendInstance", l2trunk.gameserver.model.instances.NpcFriendInstance.class);
        npcInstances.put("NpcInstance", l2trunk.gameserver.model.instances.NpcInstance.class);
        npcInstances.put("NpcNotSayInstance", l2trunk.gameserver.model.instances.NpcNotSayInstance.class);
        npcInstances.put("ObservationInstance", l2trunk.gameserver.model.instances.ObservationInstance.class);
        npcInstances.put("OlympiadBufferInstance", l2trunk.gameserver.model.instances.OlympiadBufferInstance.class);
        npcInstances.put("OlympiadManagerInstance", l2trunk.gameserver.model.instances.OlympiadManagerInstance.class);
//        npcInstances.put("RaceManagerInstance", l2trunk.gameserver.model.instances.RaceManagerInstance.class);
        npcInstances.put("RaidBossInstance", l2trunk.gameserver.model.instances.RaidBossInstance.class);
        npcInstances.put("ReflectionBossInstance", l2trunk.gameserver.model.instances.ReflectionBossInstance.class);
        npcInstances.put("SchemeBufferInstance", l2trunk.gameserver.model.instances.SchemeBufferInstance.class);
        npcInstances.put("SiegeInformerInstance", l2trunk.gameserver.model.instances.SiegeInformerInstance.class);
        npcInstances.put("SignsPriestInstance", l2trunk.gameserver.model.instances.SignsPriestInstance.class);
        npcInstances.put("SpecialMonsterInstance", l2trunk.gameserver.model.instances.SpecialMonsterInstance.class);
//        npcInstances.put("StaticObjectInstance", l2trunk.gameserver.model.instances.StaticObjectInstance.class);
//        npcInstances.put("SummonInstance", l2trunk.gameserver.model.instances.SummonInstance.class);
        npcInstances.put("SymbolInstance", l2trunk.gameserver.model.instances.SymbolInstance.class);
        npcInstances.put("SymbolMakerInstance", l2trunk.gameserver.model.instances.SymbolMakerInstance.class);
        npcInstances.put("TamedBeastInstance", l2trunk.gameserver.model.instances.TamedBeastInstance.class);
        npcInstances.put("TerritoryWardInstance", l2trunk.gameserver.model.instances.TerritoryWardInstance.class);
        npcInstances.put("TrainerInstance", l2trunk.gameserver.model.instances.TrainerInstance.class);
        npcInstances.put("TrapInstance", l2trunk.gameserver.model.instances.TrapInstance.class);
        npcInstances.put("VillageMasterInstance", l2trunk.gameserver.model.instances.VillageMasterInstance.class);
        npcInstances.put("WarehouseInstance", l2trunk.gameserver.model.instances.WarehouseInstance.class);
        npcInstances.put("WeaverInstance", l2trunk.gameserver.model.instances.WeaverInstance.class);
        npcInstances.put("WyvernManagerInstance", l2trunk.gameserver.model.instances.WyvernManagerInstance.class);
        npcInstances.put("XmassTreeInstance", l2trunk.gameserver.model.instances.XmassTreeInstance.class);
        npcInstances.put("AbyssGazeInstance", l2trunk.scripts.npc.model.AbyssGazeInstance.class);
        npcInstances.put("AllenosInstance", l2trunk.scripts.npc.model.AllenosInstance.class);
        npcInstances.put("ArenaManagerInstance", l2trunk.scripts.npc.model.ArenaManagerInstance.class);
        npcInstances.put("AsamahInstance", l2trunk.scripts.npc.model.AsamahInstance.class);
        npcInstances.put("BaiumGatekeeperInstance", l2trunk.scripts.npc.model.BaiumGatekeeperInstance.class);
        npcInstances.put("BatracosInstance", l2trunk.scripts.npc.model.BatracosInstance.class);
        npcInstances.put("BelethCoffinInstance", l2trunk.scripts.npc.model.BelethCoffinInstance.class);
        npcInstances.put("birthday.AlegriaInstance", l2trunk.scripts.npc.model.birthday.AlegriaInstance.class);
        npcInstances.put("birthday.BirthDayCakeInstance", l2trunk.scripts.npc.model.birthday.BirthDayCakeInstance.class);
        npcInstances.put("BlackJudeInstance", l2trunk.scripts.npc.model.BlackJudeInstance.class);
        npcInstances.put("BorderOutpostDoormanInstance", l2trunk.scripts.npc.model.BorderOutpostDoormanInstance.class);
        npcInstances.put("CabaleBufferInstance", l2trunk.scripts.npc.model.CabaleBufferInstance.class);
        npcInstances.put("CannibalisticStakatoChiefInstance", l2trunk.scripts.npc.model.CannibalisticStakatoChiefInstance.class);
        npcInstances.put("CaravanTraderInstance", l2trunk.scripts.npc.model.CaravanTraderInstance.class);
        npcInstances.put("ClassMasterInstance", l2trunk.scripts.npc.model.ClassMasterInstance.class);
        npcInstances.put("CoralGardenGateInstance", l2trunk.scripts.npc.model.CoralGardenGateInstance.class);
        npcInstances.put("CrystalCavernControllerInstance", l2trunk.scripts.npc.model.CrystalCavernControllerInstance.class);
        npcInstances.put("DeadTumorInstance", l2trunk.scripts.npc.model.DeadTumorInstance.class);
        npcInstances.put("DelustionGatekeeperInstance", l2trunk.scripts.npc.model.DelustionGatekeeperInstance.class);
        npcInstances.put("DragonVortexInstance", l2trunk.scripts.npc.model.DragonVortexInstance.class);
        npcInstances.put("EkimusMouthInstance", l2trunk.scripts.npc.model.EkimusMouthInstance.class);
        npcInstances.put("ElcardiaAssistantInstance", l2trunk.scripts.npc.model.ElcardiaAssistantInstance.class);
        npcInstances.put("EmeraldSquareTrapInstance", l2trunk.scripts.npc.model.EmeraldSquareTrapInstance.class);
        npcInstances.put("EnergySeedInstance", l2trunk.scripts.npc.model.EnergySeedInstance.class);
        npcInstances.put("events.CleftVortexGateInstance", l2trunk.scripts.npc.model.events.CleftVortexGateInstance.class);
        npcInstances.put("events.ColiseumHelperInstance", l2trunk.scripts.npc.model.events.ColiseumHelperInstance.class);
        npcInstances.put("events.ColiseumManagerInstance", l2trunk.scripts.npc.model.events.ColiseumManagerInstance.class);
        npcInstances.put("events.FurnfaceInstance", l2trunk.scripts.npc.model.events.FurnfaceInstance.class);
        npcInstances.put("events.HitmanInstance", l2trunk.scripts.npc.model.events.HitmanInstance.class);
        npcInstances.put("events.KrateisCubeManagerInstance", l2trunk.scripts.npc.model.events.KrateisCubeManagerInstance.class);
        npcInstances.put("events.KrateisCubeMatchManagerInstance", l2trunk.scripts.npc.model.events.KrateisCubeMatchManagerInstance.class);
        npcInstances.put("events.SumielInstance", l2trunk.scripts.npc.model.events.SumielInstance.class);
        npcInstances.put("events.UndergroundColiseumInstance", l2trunk.scripts.npc.model.events.UndergroundColiseumInstance.class);
        npcInstances.put("FakeObeliskInstance", l2trunk.scripts.npc.model.FakeObeliskInstance.class);
        npcInstances.put("FreightSenderInstance", l2trunk.scripts.npc.model.FreightSenderInstance.class);
        npcInstances.put("FrintezzaGatekeeperInstance", l2trunk.scripts.npc.model.FrintezzaGatekeeperInstance.class);
        npcInstances.put("FrintezzaInstance", l2trunk.scripts.npc.model.FrintezzaInstance.class);
        npcInstances.put("GruffManInstance", l2trunk.scripts.npc.model.GruffManInstance.class);
        npcInstances.put("GuradsOfDawnInstance", l2trunk.scripts.npc.model.GuradsOfDawnInstance.class);
        npcInstances.put("GvGBossInstance", l2trunk.scripts.npc.model.GvGBossInstance.class);
        npcInstances.put("HandysBlockCheckerInstance", l2trunk.scripts.npc.model.HandysBlockCheckerInstance.class);
        npcInstances.put("HeartOfWardingInstance", l2trunk.scripts.npc.model.HeartOfWardingInstance.class);
        npcInstances.put("HellboundRemnantInstance", l2trunk.scripts.npc.model.HellboundRemnantInstance.class);
        npcInstances.put("ImmuneMonsterInstance", l2trunk.scripts.npc.model.ImmuneMonsterInstance.class);
        npcInstances.put("JiniaNpcInstance", l2trunk.scripts.npc.model.JiniaNpcInstance.class);
        npcInstances.put("Kama26BossInstance", l2trunk.scripts.npc.model.Kama26BossInstance.class);
        npcInstances.put("KamalokaBossInstance", l2trunk.scripts.npc.model.KamalokaBossInstance.class);
        npcInstances.put("KamalokaGuardInstance", l2trunk.scripts.npc.model.KamalokaGuardInstance.class);
        npcInstances.put("KegorNpcInstance", l2trunk.scripts.npc.model.KegorNpcInstance.class);
        npcInstances.put("KeplonInstance", l2trunk.scripts.npc.model.KeplonInstance.class);
        npcInstances.put("LekonInstance", l2trunk.scripts.npc.model.LekonInstance.class);
        npcInstances.put("LostCaptainInstance", l2trunk.scripts.npc.model.LostCaptainInstance.class);
        npcInstances.put("MaguenInstance", l2trunk.scripts.npc.model.MaguenInstance.class);
        npcInstances.put("MaguenTraderInstance", l2trunk.scripts.npc.model.MaguenTraderInstance.class);
        npcInstances.put("MeleonInstance", l2trunk.scripts.npc.model.MeleonInstance.class);
        npcInstances.put("MobInvulInstance", l2trunk.scripts.npc.model.MobInvulInstance.class);
        npcInstances.put("MoonlightTombstoneInstance", l2trunk.scripts.npc.model.MoonlightTombstoneInstance.class);
        npcInstances.put("MushroomInstance", l2trunk.scripts.npc.model.MushroomInstance.class);
        npcInstances.put("NaiaControllerInstance", l2trunk.scripts.npc.model.NaiaControllerInstance.class);
        npcInstances.put("NaiaRoomControllerInstance", l2trunk.scripts.npc.model.NaiaRoomControllerInstance.class);
        npcInstances.put("NativeCorpseInstance", l2trunk.scripts.npc.model.NativeCorpseInstance.class);
        npcInstances.put("NativePrisonerInstance", l2trunk.scripts.npc.model.NativePrisonerInstance.class);
        npcInstances.put("NevitHeraldInstance", l2trunk.scripts.npc.model.NevitHeraldInstance.class);
        npcInstances.put("NewbieGuideInstance", l2trunk.scripts.npc.model.NewbieGuideInstance.class);
        npcInstances.put("NihilInvaderChestInstance", l2trunk.scripts.npc.model.NihilInvaderChestInstance.class);
        npcInstances.put("OddGlobeInstance", l2trunk.scripts.npc.model.OddGlobeInstance.class);
        npcInstances.put("OrfenInstance", l2trunk.scripts.npc.model.OrfenInstance.class);
        npcInstances.put("PailakaGatekeeperInstance", l2trunk.scripts.npc.model.PailakaGatekeeperInstance.class);
        npcInstances.put("PassagewayMobWithHerbInstance", l2trunk.scripts.npc.model.PassagewayMobWithHerbInstance.class);
        npcInstances.put("PathfinderInstance", l2trunk.scripts.npc.model.PathfinderInstance.class);
        npcInstances.put("PriestAquilaniInstance", l2trunk.scripts.npc.model.PriestAquilaniInstance.class);
        npcInstances.put("PriestOfBlessingInstance", l2trunk.scripts.npc.model.PriestOfBlessingInstance.class);
        npcInstances.put("QuarrySlaveInstance", l2trunk.scripts.npc.model.QuarrySlaveInstance.class);
        npcInstances.put("QueenAntInstance", l2trunk.scripts.npc.model.QueenAntInstance.class);
        npcInstances.put("QueenAntLarvaInstance", l2trunk.scripts.npc.model.QueenAntLarvaInstance.class);
        npcInstances.put("RafortyInstance", l2trunk.scripts.npc.model.RafortyInstance.class);


        npcInstances.put("residences.SiegeFlagInstance", l2trunk.gameserver.model.instances.residences.SiegeFlagInstance.class);
        npcInstances.put("residences.dominion.OutpostInstance", l2trunk.gameserver.model.instances.residences.dominion.OutpostInstance.class);

        npcInstances.put("residences.castle.BlacksmithInstance", l2trunk.scripts.npc.model.residences.castle.BlacksmithInstance.class);
        npcInstances.put("residences.castle.CastleControlTowerInstance", l2trunk.scripts.npc.model.residences.castle.CastleControlTowerInstance.class);
        npcInstances.put("residences.castle.CastleFakeTowerInstance", l2trunk.scripts.npc.model.residences.castle.CastleFakeTowerInstance.class);
        npcInstances.put("residences.castle.CastleFlameTowerInstance", l2trunk.scripts.npc.model.residences.castle.CastleFlameTowerInstance.class);
        npcInstances.put("residences.castle.CastleMassTeleporterInstance", l2trunk.scripts.npc.model.residences.castle.CastleMassTeleporterInstance.class);
        npcInstances.put("residences.castle.CastleMessengerInstance", l2trunk.scripts.npc.model.residences.castle.CastleMessengerInstance.class);
        npcInstances.put("residences.castle.ChamberlainInstance", l2trunk.scripts.npc.model.residences.castle.ChamberlainInstance.class);
        npcInstances.put("residences.castle.CourtInstance", l2trunk.scripts.npc.model.residences.castle.CourtInstance.class);
        npcInstances.put("residences.castle.DoormanInstance", l2trunk.scripts.npc.model.residences.castle.DoormanInstance.class);
        npcInstances.put("residences.castle.MercenaryManagerInstance", l2trunk.scripts.npc.model.residences.castle.MercenaryManagerInstance.class);
        npcInstances.put("residences.castle.VenomTeleportCubicInstance", l2trunk.scripts.npc.model.residences.castle.VenomTeleportCubicInstance.class);
        npcInstances.put("residences.castle.VenomTeleporterInstance", l2trunk.scripts.npc.model.residences.castle.VenomTeleporterInstance.class);
        npcInstances.put("residences.castle.WarehouseInstance", l2trunk.scripts.npc.model.residences.castle.WarehouseInstance.class);
        npcInstances.put("residences.clanhall.AuctionedDoormanInstance", l2trunk.scripts.npc.model.residences.clanhall.AuctionedDoormanInstance.class);
        npcInstances.put("residences.clanhall.AuctionedManagerInstance", l2trunk.scripts.npc.model.residences.clanhall.AuctionedManagerInstance.class);
        npcInstances.put("residences.clanhall.AuctioneerInstance", l2trunk.scripts.npc.model.residences.clanhall.AuctioneerInstance.class);
        npcInstances.put("residences.clanhall.BanditMessagerInstance", l2trunk.scripts.npc.model.residences.clanhall.BanditMessagerInstance.class);
        npcInstances.put("residences.clanhall.BrakelInstance", l2trunk.scripts.npc.model.residences.clanhall.BrakelInstance.class);
        npcInstances.put("residences.clanhall.DietrichInstance", l2trunk.scripts.npc.model.residences.clanhall.DietrichInstance.class);
        npcInstances.put("residences.clanhall.DoormanInstance", l2trunk.scripts.npc.model.residences.clanhall.DoormanInstance.class);
        npcInstances.put("residences.clanhall.FarmMessengerInstance", l2trunk.scripts.npc.model.residences.clanhall.FarmMessengerInstance.class);
        npcInstances.put("residences.clanhall.GustavInstance", l2trunk.scripts.npc.model.residences.clanhall.GustavInstance.class);
        npcInstances.put("residences.clanhall.LidiaVonHellmannInstance", l2trunk.scripts.npc.model.residences.clanhall.LidiaVonHellmannInstance.class);
        npcInstances.put("residences.clanhall.ManagerInstance", l2trunk.scripts.npc.model.residences.clanhall.ManagerInstance.class);
        npcInstances.put("residences.clanhall.MatchBerserkerInstance", l2trunk.scripts.npc.model.residences.clanhall.MatchBerserkerInstance.class);
        npcInstances.put("residences.clanhall.MatchClericInstance", l2trunk.scripts.npc.model.residences.clanhall.MatchClericInstance.class);
        npcInstances.put("residences.clanhall.MatchLeaderInstance", l2trunk.scripts.npc.model.residences.clanhall.MatchLeaderInstance.class);
        npcInstances.put("residences.clanhall.MatchMassTeleporterInstance", l2trunk.scripts.npc.model.residences.clanhall.MatchMassTeleporterInstance.class);
        npcInstances.put("residences.clanhall.MatchScoutInstance", l2trunk.scripts.npc.model.residences.clanhall.MatchScoutInstance.class);
        npcInstances.put("residences.clanhall.MatchTriefInstance", l2trunk.scripts.npc.model.residences.clanhall.MatchTriefInstance.class);
        npcInstances.put("residences.clanhall.MessengerInstance", l2trunk.scripts.npc.model.residences.clanhall.MessengerInstance.class);
        npcInstances.put("residences.clanhall.MikhailInstance", l2trunk.scripts.npc.model.residences.clanhall.MikhailInstance.class);
        npcInstances.put("residences.clanhall.NurkaInstance", l2trunk.scripts.npc.model.residences.clanhall.NurkaInstance.class);
        npcInstances.put("residences.clanhall.RainbowChestInstance", l2trunk.scripts.npc.model.residences.clanhall.RainbowChestInstance.class);
        npcInstances.put("residences.clanhall.RainbowCoordinatorInstance", l2trunk.scripts.npc.model.residences.clanhall.RainbowCoordinatorInstance.class);
        npcInstances.put("residences.clanhall.RainbowGourdInstance", l2trunk.scripts.npc.model.residences.clanhall.RainbowGourdInstance.class);
        npcInstances.put("residences.clanhall.RainbowMessengerInstance", l2trunk.scripts.npc.model.residences.clanhall.RainbowMessengerInstance.class);
        npcInstances.put("residences.clanhall.RainbowYetiInstance", l2trunk.scripts.npc.model.residences.clanhall.RainbowYetiInstance.class);
        npcInstances.put("residences.clanhall._34BossMinionInstance", l2trunk.scripts.npc.model.residences.clanhall._34BossMinionInstance.class);
        npcInstances.put("residences.dominion.CatapultInstance", l2trunk.scripts.npc.model.residences.dominion.CatapultInstance.class);
        npcInstances.put("residences.dominion.MercenaryCaptainInstance", l2trunk.scripts.npc.model.residences.dominion.MercenaryCaptainInstance.class);
        npcInstances.put("residences.dominion.TerritoryManagerInstance", l2trunk.scripts.npc.model.residences.dominion.TerritoryManagerInstance.class);
        npcInstances.put("residences.DoormanInstance", l2trunk.scripts.npc.model.residences.DoormanInstance.class);
        npcInstances.put("residences.fortress.DoormanInstance", l2trunk.scripts.npc.model.residences.fortress.DoormanInstance.class);
        npcInstances.put("residences.fortress.EnvoyInstance", l2trunk.scripts.npc.model.residences.fortress.EnvoyInstance.class);
        npcInstances.put("residences.fortress.FacilityManagerInstance", l2trunk.scripts.npc.model.residences.fortress.FacilityManagerInstance.class);
        npcInstances.put("residences.fortress.LogisticsOfficerInstance", l2trunk.scripts.npc.model.residences.fortress.LogisticsOfficerInstance.class);
        npcInstances.put("residences.fortress.ManagerInstance", l2trunk.scripts.npc.model.residences.fortress.ManagerInstance.class);
        npcInstances.put("residences.fortress.peace.ArcherCaptionInstance", l2trunk.scripts.npc.model.residences.fortress.peace.ArcherCaptionInstance.class);
        npcInstances.put("residences.fortress.peace.GuardCaptionInstance", l2trunk.scripts.npc.model.residences.fortress.peace.GuardCaptionInstance.class);
        npcInstances.put("residences.fortress.peace.SupportUnitCaptionInstance", l2trunk.scripts.npc.model.residences.fortress.peace.SupportUnitCaptionInstance.class);
        npcInstances.put("residences.fortress.peace.SuspiciousMerchantInstance", l2trunk.scripts.npc.model.residences.fortress.peace.SuspiciousMerchantInstance.class);
        npcInstances.put("residences.fortress.siege.BackupPowerUnitInstance", l2trunk.scripts.npc.model.residences.fortress.siege.BackupPowerUnitInstance.class);
        npcInstances.put("residences.fortress.siege.BallistaInstance", l2trunk.scripts.npc.model.residences.fortress.siege.BallistaInstance.class);
        npcInstances.put("residences.fortress.siege.ControlUnitInstance", l2trunk.scripts.npc.model.residences.fortress.siege.ControlUnitInstance.class);
        npcInstances.put("residences.fortress.siege.MainMachineInstance", l2trunk.scripts.npc.model.residences.fortress.siege.MainMachineInstance.class);
        npcInstances.put("residences.fortress.siege.MercenaryCaptionInstance", l2trunk.scripts.npc.model.residences.fortress.siege.MercenaryCaptionInstance.class);
        npcInstances.put("residences.fortress.siege.PowerControlUnitInstance", l2trunk.scripts.npc.model.residences.fortress.siege.PowerControlUnitInstance.class);
        npcInstances.put("residences.QuestSiegeGuardInstance", l2trunk.scripts.npc.model.residences.QuestSiegeGuardInstance.class);
        npcInstances.put("residences.ResidenceManager", l2trunk.scripts.npc.model.residences.ResidenceManager.class);
        npcInstances.put("residences.SiegeGuardInstance", l2trunk.scripts.npc.model.residences.SiegeGuardInstance.class);
        npcInstances.put("residences.TeleportSiegeGuardInstance", l2trunk.scripts.npc.model.residences.TeleportSiegeGuardInstance.class);
        npcInstances.put("RiganInstance", l2trunk.scripts.npc.model.RiganInstance.class);
        npcInstances.put("RignosInstance", l2trunk.scripts.npc.model.RignosInstance.class);
        npcInstances.put("SairlenGatekeeperInstance", l2trunk.scripts.npc.model.SairlenGatekeeperInstance.class);
        npcInstances.put("SallyInstance", l2trunk.scripts.npc.model.SallyInstance.class);
        npcInstances.put("SandstormInstance", l2trunk.scripts.npc.model.SandstormInstance.class);
        npcInstances.put("SealDeviceInstance", l2trunk.scripts.npc.model.SealDeviceInstance.class);
        npcInstances.put("SeducedInvestigatorInstance", l2trunk.scripts.npc.model.SeducedInvestigatorInstance.class);
        npcInstances.put("SeedOfAnnihilationInstance", l2trunk.scripts.npc.model.SeedOfAnnihilationInstance.class);
        npcInstances.put("SepulcherMonsterInstance", l2trunk.scripts.npc.model.SepulcherMonsterInstance.class);
        npcInstances.put("SepulcherNpcInstance", l2trunk.scripts.npc.model.SepulcherNpcInstance.class);
        npcInstances.put("SepulcherRaidInstance", l2trunk.scripts.npc.model.SepulcherRaidInstance.class);
        npcInstances.put("SirraInstance", l2trunk.scripts.npc.model.SirraInstance.class);
        npcInstances.put("SnowmanInstance", l2trunk.scripts.npc.model.SnowmanInstance.class);
        npcInstances.put("SpecialMinionInstance", l2trunk.scripts.npc.model.SpecialMinionInstance.class);
        npcInstances.put("SquashInstance", l2trunk.scripts.npc.model.SquashInstance.class);
        npcInstances.put("StarStoneInstance", l2trunk.scripts.npc.model.StarStoneInstance.class);
        npcInstances.put("SteamCorridorControllerInstance", l2trunk.scripts.npc.model.SteamCorridorControllerInstance.class);
        npcInstances.put("SteelCitadelTeleporterInstance", l2trunk.scripts.npc.model.SteelCitadelTeleporterInstance.class);
        npcInstances.put("TepiosRewardInstance", l2trunk.scripts.npc.model.TepiosRewardInstance.class);
        npcInstances.put("ThomasInstance", l2trunk.scripts.npc.model.ThomasInstance.class);
        npcInstances.put("TreasureChestInstance", l2trunk.scripts.npc.model.TreasureChestInstance.class);
        npcInstances.put("TriolsMirrorInstance", l2trunk.scripts.npc.model.TriolsMirrorInstance.class);
        npcInstances.put("TullyWorkShopTeleporterInstance", l2trunk.scripts.npc.model.TullyWorkShopTeleporterInstance.class);
        npcInstances.put("ValakasGatekeeperInstance", l2trunk.scripts.npc.model.ValakasGatekeeperInstance.class);
        npcInstances.put("WarpgateInstance", l2trunk.scripts.npc.model.WarpgateInstance.class);
        npcInstances.put("WorkshopGatekeeperInstance", l2trunk.scripts.npc.model.WorkshopGatekeeperInstance.class);
        npcInstances.put("WorkshopServantInstance", l2trunk.scripts.npc.model.WorkshopServantInstance.class);
        npcInstances.put("YehanBrotherInstance", l2trunk.scripts.npc.model.YehanBrotherInstance.class);
        npcInstances.put("ZakenCandleInstance", l2trunk.scripts.npc.model.ZakenCandleInstance.class);
        npcInstances.put("ZakenGatekeeperInstance", l2trunk.scripts.npc.model.ZakenGatekeeperInstance.class);


    }

    private void loadScripts() {
//        scripts.put("actions.OnActionShift",l2trunk.scripts.actions.OnActionShift.class);
//        scripts.put("actions.RewardListInfo",l2trunk.scripts.actions.RewardListInfo.class);
        scripts.put("bosses.AntharasManager",l2trunk.scripts.bosses.AntharasManager.class);
        scripts.put("bosses.BaiumManager",l2trunk.scripts.bosses.BaiumManager.class);
        scripts.put("bosses.BaylorManager",l2trunk.scripts.bosses.BaylorManager.class);
        scripts.put("bosses.BelethManager",l2trunk.scripts.bosses.BelethManager.class);
        scripts.put("bosses.FourSepulchersManager",l2trunk.scripts.bosses.FourSepulchersManager.class);
        scripts.put("bosses.FourSepulchersSpawn",l2trunk.scripts.bosses.FourSepulchersSpawn.class);
        scripts.put("bosses.SailrenManager",l2trunk.scripts.bosses.SailrenManager.class);
        scripts.put("bosses.ValakasManager",l2trunk.scripts.bosses.ValakasManager.class);
        scripts.put("events.AprilFoolsDay.AprilFoolsDay",l2trunk.scripts.events.AprilFoolsDay.AprilFoolsDay.class);
        scripts.put("events.arena.DionArena",l2trunk.scripts.events.arena.DionArena.class);
        scripts.put("events.arena.GiranArena",l2trunk.scripts.events.arena.GiranArena.class);
        scripts.put("events.arena.GludinArena",l2trunk.scripts.events.arena.GludinArena.class);
        scripts.put("events.BossRandom.BossRandom",l2trunk.scripts.events.BossRandom.BossRandom.class);
        scripts.put("events.bountyhunters.HuntersGuild",l2trunk.scripts.events.bountyhunters.HuntersGuild.class);
        scripts.put("events.Christmas.Christmas",l2trunk.scripts.events.Christmas.Christmas.class);
        scripts.put("events.Christmas.NewYearTimer",l2trunk.scripts.events.Christmas.NewYearTimer.class);
        scripts.put("events.Christmas.Seed",l2trunk.scripts.events.Christmas.Seed.class);
        scripts.put("events.CofferofShadows.Coffer",l2trunk.scripts.events.CofferofShadows.Coffer.class);
        scripts.put("events.CofferofShadows.CofferofShadows",l2trunk.scripts.events.CofferofShadows.CofferofShadows.class);
        scripts.put("events.CustomDropItems.CustomDropItems",l2trunk.scripts.events.CustomDropItems.CustomDropItems.class);
        scripts.put("events.EventsConfig",l2trunk.scripts.events.EventsConfig.class);
        scripts.put("events.FreyaEvent.FreyaEvent",l2trunk.scripts.events.FreyaEvent.FreyaEvent.class);
        scripts.put("events.GiftOfVitality.GiftOfVitality",l2trunk.scripts.events.GiftOfVitality.GiftOfVitality.class);
        scripts.put("events.glitmedal.glitmedal",l2trunk.scripts.events.glitmedal.glitmedal.class);
        scripts.put("events.GvG.GvG",l2trunk.scripts.events.GvG.GvG.class);
        scripts.put("events.heart.Heart",l2trunk.scripts.events.heart.Heart.class);
        scripts.put("events.Hitman.Hitman",l2trunk.scripts.events.Hitman.Hitman.class);
        scripts.put("events.l2day.LettersCollection",l2trunk.scripts.events.l2day.LettersCollection.class);
        scripts.put("events.lastHero.LastHero",l2trunk.scripts.events.lastHero.LastHero.class);
        scripts.put("events.March8.March8",l2trunk.scripts.events.March8.March8.class);
        scripts.put("events.MasterOfEnchanting.EnchantingReward",l2trunk.scripts.events.MasterOfEnchanting.EnchantingReward.class);
        scripts.put("events.MasterOfEnchanting.MasterOfEnchanting",l2trunk.scripts.events.MasterOfEnchanting.MasterOfEnchanting.class);
        scripts.put("events.PcCafePointsExchange.PcCafePointsExchange",l2trunk.scripts.events.PcCafePointsExchange.PcCafePointsExchange.class);
        scripts.put("events.PiratesTreasure.PiratesTreasure",l2trunk.scripts.events.PiratesTreasure.PiratesTreasure.class);
        scripts.put("events.SantaEvent.SantaEvent",l2trunk.scripts.events.SantaEvent.SantaEvent.class);
        scripts.put("events.SavingSnowman.SavingSnowman",l2trunk.scripts.events.SavingSnowman.SavingSnowman.class);
        scripts.put("events.SummerMeleons.MeleonSeed",l2trunk.scripts.events.SummerMeleons.MeleonSeed.class);
        scripts.put("events.SummerMeleons.SummerMeleons",l2trunk.scripts.events.SummerMeleons.SummerMeleons.class);
        scripts.put("events.TheFallHarvest.Seed",l2trunk.scripts.events.TheFallHarvest.Seed.class);
        scripts.put("events.TheFallHarvest.TheFallHarvest",l2trunk.scripts.events.TheFallHarvest.TheFallHarvest.class);
//        scripts.put("events.TheFlowOfTheHorror.GilmoreAI",l2trunk.scripts.events.TheFlowOfTheHorror.GilmoreAI.class);
//        scripts.put("events.TheFlowOfTheHorror.MonstersAI",l2trunk.scripts.events.TheFlowOfTheHorror.MonstersAI.class);
        scripts.put("events.TheFlowOfTheHorror.TheFlowOfTheHorror",l2trunk.scripts.events.TheFlowOfTheHorror.TheFlowOfTheHorror.class);
        scripts.put("events.TrickOfTrans.TrickOfTrans",l2trunk.scripts.events.TrickOfTrans.TrickOfTrans.class);
        scripts.put("events.TvT.TvT",l2trunk.scripts.events.TvT.TvT.class);
        scripts.put("events.TvTArena.TvTArena1",l2trunk.scripts.events.TvTArena.TvTArena1.class);
        scripts.put("events.TvTArena.TvTArena2",l2trunk.scripts.events.TvTArena.TvTArena2.class);
        scripts.put("events.TvTArena.TvTArena3",l2trunk.scripts.events.TvTArena.TvTArena3.class);
        scripts.put("events.Viktorina.Viktorina",l2trunk.scripts.events.Viktorina.Viktorina.class);
        scripts.put("handler.admincommands.AdminBosses",l2trunk.scripts.handler.admincommands.AdminBosses.class);
        scripts.put("handler.admincommands.AdminEpic",l2trunk.scripts.handler.admincommands.AdminEpic.class);
        scripts.put("handler.admincommands.AdminResidence",l2trunk.scripts.handler.admincommands.AdminResidence.class);
        scripts.put("handler.bypass.TeleToFantasyIsle",l2trunk.scripts.handler.bypass.TeleToFantasyIsle.class);
        scripts.put("handler.items.AttributeStones",l2trunk.scripts.handler.items.AttributeStones.class);
        scripts.put("handler.items.Battleground",l2trunk.scripts.handler.items.Battleground.class);
        scripts.put("handler.items.BeastShot",l2trunk.scripts.handler.items.BeastShot.class);
        scripts.put("handler.items.BlessedSpiritShot",l2trunk.scripts.handler.items.BlessedSpiritShot.class);
        scripts.put("handler.items.Books",l2trunk.scripts.handler.items.Books.class);
        scripts.put("handler.items.Calculator",l2trunk.scripts.handler.items.Calculator.class);
        scripts.put("handler.items.CharChangePotions",l2trunk.scripts.handler.items.CharChangePotions.class);
        scripts.put("handler.items.Cocktails",l2trunk.scripts.handler.items.Cocktails.class);
        scripts.put("handler.items.DisguiseScroll",l2trunk.scripts.handler.items.DisguiseScroll.class);
        scripts.put("handler.items.EnchantScrolls",l2trunk.scripts.handler.items.EnchantScrolls.class);
        scripts.put("handler.items.EquipableItem",l2trunk.scripts.handler.items.EquipableItem.class);
        scripts.put("handler.items.Extractable",l2trunk.scripts.handler.items.Extractable.class);
        scripts.put("handler.items.FishItem",l2trunk.scripts.handler.items.FishItem.class);
        scripts.put("handler.items.FishShots",l2trunk.scripts.handler.items.FishShots.class);
        scripts.put("handler.items.Harvester",l2trunk.scripts.handler.items.Harvester.class);
        scripts.put("handler.items.HelpBook",l2trunk.scripts.handler.items.HelpBook.class);
        scripts.put("handler.items.HolyWater",l2trunk.scripts.handler.items.HolyWater.class);
        scripts.put("handler.items.ItemSkills",l2trunk.scripts.handler.items.ItemSkills.class);
        scripts.put("handler.items.Kamaloka",l2trunk.scripts.handler.items.Kamaloka.class);
        scripts.put("handler.items.Keys",l2trunk.scripts.handler.items.Keys.class);
        scripts.put("handler.items.MercTicket",l2trunk.scripts.handler.items.MercTicket.class);
        scripts.put("handler.items.NameColor",l2trunk.scripts.handler.items.NameColor.class);
        scripts.put("handler.items.NevitVoice",l2trunk.scripts.handler.items.NevitVoice.class);
        scripts.put("handler.items.PathfinderEquipment",l2trunk.scripts.handler.items.PathfinderEquipment.class);
        scripts.put("handler.items.PetSummon",l2trunk.scripts.handler.items.PetSummon.class);
        scripts.put("handler.items.Potions",l2trunk.scripts.handler.items.Potions.class);
        scripts.put("handler.items.Recipes",l2trunk.scripts.handler.items.Recipes.class);
        scripts.put("handler.items.RollingDice",l2trunk.scripts.handler.items.RollingDice.class);
        scripts.put("handler.items.Seed",l2trunk.scripts.handler.items.Seed.class);
        scripts.put("handler.items.SoulCrystals",l2trunk.scripts.handler.items.SoulCrystals.class);
        scripts.put("handler.items.SoulShots",l2trunk.scripts.handler.items.SoulShots.class);
        scripts.put("handler.items.Special",l2trunk.scripts.handler.items.Special.class);
        scripts.put("handler.items.Spellbooks",l2trunk.scripts.handler.items.Spellbooks.class);
        scripts.put("handler.items.SpiritShot",l2trunk.scripts.handler.items.SpiritShot.class);
        scripts.put("handler.items.SupportPower",l2trunk.scripts.handler.items.SupportPower.class);
        scripts.put("handler.items.TeleportBookmark",l2trunk.scripts.handler.items.TeleportBookmark.class);
        scripts.put("handler.items.WorldMap",l2trunk.scripts.handler.items.WorldMap.class);
        scripts.put("handler.voicecommands.CWHPrivileges",l2trunk.scripts.handler.voicecommands.CWHPrivileges.class);
        scripts.put("handler.voicecommands.DragonStatus",l2trunk.scripts.handler.voicecommands.DragonStatus.class);
        scripts.put("handler.voicecommands.Epics",l2trunk.scripts.handler.voicecommands.Epics.class);
        scripts.put("handler.voicecommands.Quiz",l2trunk.scripts.handler.voicecommands.Quiz.class);
        scripts.put("quests.Dominion_KillSpecialUnitQuest",l2trunk.scripts.quests.Dominion_KillSpecialUnitQuest.class);
        scripts.put("quests._001_LettersOfLove",l2trunk.scripts.quests._001_LettersOfLove.class);
        scripts.put("quests._002_WhatWomenWant",l2trunk.scripts.quests._002_WhatWomenWant.class);
        scripts.put("quests._003_WilltheSealbeBroken",l2trunk.scripts.quests._003_WilltheSealbeBroken.class);
        scripts.put("quests._004_LongLivethePaagrioLord",l2trunk.scripts.quests._004_LongLivethePaagrioLord.class);
        scripts.put("quests._005_MinersFavor",l2trunk.scripts.quests._005_MinersFavor.class);
        scripts.put("quests._006_StepIntoTheFuture",l2trunk.scripts.quests._006_StepIntoTheFuture.class);
        scripts.put("quests._007_ATripBegins",l2trunk.scripts.quests._007_ATripBegins.class);
        scripts.put("quests._008_AnAdventureBegins",l2trunk.scripts.quests._008_AnAdventureBegins.class);
        scripts.put("quests._009_IntoTheCityOfHumans",l2trunk.scripts.quests._009_IntoTheCityOfHumans.class);
        scripts.put("quests._010_IntoTheWorld",l2trunk.scripts.quests._010_IntoTheWorld.class);
        scripts.put("quests._011_SecretMeetingWithKetraOrcs",l2trunk.scripts.quests._011_SecretMeetingWithKetraOrcs.class);
        scripts.put("quests._012_SecretMeetingWithVarkaSilenos",l2trunk.scripts.quests._012_SecretMeetingWithVarkaSilenos.class);
        scripts.put("quests._013_ParcelDelivery",l2trunk.scripts.quests._013_ParcelDelivery.class);
        scripts.put("quests._014_WhereaboutsoftheArchaeologist",l2trunk.scripts.quests._014_WhereaboutsoftheArchaeologist.class);
        scripts.put("quests._015_SweetWhispers",l2trunk.scripts.quests._015_SweetWhispers.class);
        scripts.put("quests._016_TheComingDarkness",l2trunk.scripts.quests._016_TheComingDarkness.class);
        scripts.put("quests._017_LightAndDarkness",l2trunk.scripts.quests._017_LightAndDarkness.class);
        scripts.put("quests._018_MeetingwiththeGoldenRam",l2trunk.scripts.quests._018_MeetingwiththeGoldenRam.class);
        scripts.put("quests._019_GoToThePastureland",l2trunk.scripts.quests._019_GoToThePastureland.class);
        scripts.put("quests._020_BringUpWithLove",l2trunk.scripts.quests._020_BringUpWithLove.class);
        scripts.put("quests._021_HiddenTruth",l2trunk.scripts.quests._021_HiddenTruth.class);
        scripts.put("quests._022_TragedyInVonHellmannForest",l2trunk.scripts.quests._022_TragedyInVonHellmannForest.class);
        scripts.put("quests._023_LidiasHeart",l2trunk.scripts.quests._023_LidiasHeart.class);
        scripts.put("quests._024_InhabitantsOfTheForestOfTheDead",l2trunk.scripts.quests._024_InhabitantsOfTheForestOfTheDead.class);
        scripts.put("quests._025_HidingBehindTheTruth",l2trunk.scripts.quests._025_HidingBehindTheTruth.class);
        scripts.put("quests._026_TiredOfWaiting",l2trunk.scripts.quests._026_TiredOfWaiting.class);
        scripts.put("quests._027_ChestCaughtWithABaitOfWind",l2trunk.scripts.quests._027_ChestCaughtWithABaitOfWind.class);
        scripts.put("quests._028_ChestCaughtWithABaitOfIcyAir",l2trunk.scripts.quests._028_ChestCaughtWithABaitOfIcyAir.class);
        scripts.put("quests._029_ChestCaughtWithABaitOfEarth",l2trunk.scripts.quests._029_ChestCaughtWithABaitOfEarth.class);
        scripts.put("quests._030_ChestCaughtWithABaitOfFire",l2trunk.scripts.quests._030_ChestCaughtWithABaitOfFire.class);
        scripts.put("quests._031_SecretBuriedInTheSwamp",l2trunk.scripts.quests._031_SecretBuriedInTheSwamp.class);
        scripts.put("quests._032_AnObviousLie",l2trunk.scripts.quests._032_AnObviousLie.class);
        scripts.put("quests._033_MakeAPairOfDressShoes",l2trunk.scripts.quests._033_MakeAPairOfDressShoes.class);
        scripts.put("quests._034_InSearchOfClothes",l2trunk.scripts.quests._034_InSearchOfClothes.class);
        scripts.put("quests._035_FindGlitteringJewelry",l2trunk.scripts.quests._035_FindGlitteringJewelry.class);
        scripts.put("quests._036_MakeASewingKit",l2trunk.scripts.quests._036_MakeASewingKit.class);
        scripts.put("quests._037_PleaseMakeMeFormalWear",l2trunk.scripts.quests._037_PleaseMakeMeFormalWear.class);
        scripts.put("quests._038_DragonFangs",l2trunk.scripts.quests._038_DragonFangs.class);
        scripts.put("quests._039_RedEyedInvaders",l2trunk.scripts.quests._039_RedEyedInvaders.class);
        scripts.put("quests._040_ASpecialOrder",l2trunk.scripts.quests._040_ASpecialOrder.class);
        scripts.put("quests._042_HelpTheUncle",l2trunk.scripts.quests._042_HelpTheUncle.class);
        scripts.put("quests._043_HelpTheSister",l2trunk.scripts.quests._043_HelpTheSister.class);
        scripts.put("quests._044_HelpTheSon",l2trunk.scripts.quests._044_HelpTheSon.class);
        scripts.put("quests._045_ToTalkingIsland",l2trunk.scripts.quests._045_ToTalkingIsland.class);
        scripts.put("quests._046_OnceMoreInTheArmsOfTheMotherTree",l2trunk.scripts.quests._046_OnceMoreInTheArmsOfTheMotherTree.class);
        scripts.put("quests._047_IntoTheDarkForest",l2trunk.scripts.quests._047_IntoTheDarkForest.class);
        scripts.put("quests._048_ToTheImmortalPlateau",l2trunk.scripts.quests._048_ToTheImmortalPlateau.class);
        scripts.put("quests._049_TheRoadHome",l2trunk.scripts.quests._049_TheRoadHome.class);
        scripts.put("quests._050_LanoscosSpecialBait",l2trunk.scripts.quests._050_LanoscosSpecialBait.class);
        scripts.put("quests._051_OFullesSpecialBait",l2trunk.scripts.quests._051_OFullesSpecialBait.class);
        scripts.put("quests._052_WilliesSpecialBait",l2trunk.scripts.quests._052_WilliesSpecialBait.class);
        scripts.put("quests._053_LinnaeusSpecialBait",l2trunk.scripts.quests._053_LinnaeusSpecialBait.class);
        scripts.put("quests._060_GoodWorksReward",l2trunk.scripts.quests._060_GoodWorksReward.class);
        scripts.put("quests._061_LawEnforcement",l2trunk.scripts.quests._061_LawEnforcement.class);
        scripts.put("quests._062_PathOfTheDragoon",l2trunk.scripts.quests._062_PathOfTheDragoon.class);
        scripts.put("quests._063_PathToWarder",l2trunk.scripts.quests._063_PathToWarder.class);
        scripts.put("quests._064_CertifiedBerserker",l2trunk.scripts.quests._064_CertifiedBerserker.class);
        scripts.put("quests._065_PathToSoulBreaker",l2trunk.scripts.quests._065_PathToSoulBreaker.class);
        scripts.put("quests._066_CertifiedArbalester",l2trunk.scripts.quests._066_CertifiedArbalester.class);
        scripts.put("quests._067_SagaOfTheDoombringer",l2trunk.scripts.quests._067_SagaOfTheDoombringer.class);
        scripts.put("quests._068_SagaOfTheSoulHound",l2trunk.scripts.quests._068_SagaOfTheSoulHound.class);
        scripts.put("quests._069_SagaOfTheTrickster",l2trunk.scripts.quests._069_SagaOfTheTrickster.class);
        scripts.put("quests._070_SagaOfThePhoenixKnight",l2trunk.scripts.quests._070_SagaOfThePhoenixKnight.class);
        scripts.put("quests._071_SagaOfEvasTemplar",l2trunk.scripts.quests._071_SagaOfEvasTemplar.class);
        scripts.put("quests._072_SagaOfTheSwordMuse",l2trunk.scripts.quests._072_SagaOfTheSwordMuse.class);
        scripts.put("quests._073_SagaOfTheDuelist",l2trunk.scripts.quests._073_SagaOfTheDuelist.class);
        scripts.put("quests._074_SagaOfTheDreadnoughts",l2trunk.scripts.quests._074_SagaOfTheDreadnoughts.class);
        scripts.put("quests._075_SagaOfTheTitan",l2trunk.scripts.quests._075_SagaOfTheTitan.class);
        scripts.put("quests._076_SagaOfTheGrandKhavatari",l2trunk.scripts.quests._076_SagaOfTheGrandKhavatari.class);
        scripts.put("quests._077_SagaOfTheDominator",l2trunk.scripts.quests._077_SagaOfTheDominator.class);
        scripts.put("quests._078_SagaOfTheDoomcryer",l2trunk.scripts.quests._078_SagaOfTheDoomcryer.class);
        scripts.put("quests._079_SagaOfTheAdventurer",l2trunk.scripts.quests._079_SagaOfTheAdventurer.class);
        scripts.put("quests._080_SagaOfTheWindRider",l2trunk.scripts.quests._080_SagaOfTheWindRider.class);
        scripts.put("quests._081_SagaOfTheGhostHunter",l2trunk.scripts.quests._081_SagaOfTheGhostHunter.class);
        scripts.put("quests._082_SagaOfTheSagittarius",l2trunk.scripts.quests._082_SagaOfTheSagittarius.class);
        scripts.put("quests._083_SagaOfTheMoonlightSentinel",l2trunk.scripts.quests._083_SagaOfTheMoonlightSentinel.class);
        scripts.put("quests._084_SagaOfTheGhostSentinel",l2trunk.scripts.quests._084_SagaOfTheGhostSentinel.class);
        scripts.put("quests._085_SagaOfTheCardinal",l2trunk.scripts.quests._085_SagaOfTheCardinal.class);
        scripts.put("quests._086_SagaOfTheHierophant",l2trunk.scripts.quests._086_SagaOfTheHierophant.class);
        scripts.put("quests._087_SagaOfEvasSaint",l2trunk.scripts.quests._087_SagaOfEvasSaint.class);
        scripts.put("quests._088_SagaOfTheArchmage",l2trunk.scripts.quests._088_SagaOfTheArchmage.class);
        scripts.put("quests._089_SagaOfTheMysticMuse",l2trunk.scripts.quests._089_SagaOfTheMysticMuse.class);
        scripts.put("quests._090_SagaOfTheStormScreamer",l2trunk.scripts.quests._090_SagaOfTheStormScreamer.class);
        scripts.put("quests._091_SagaOfTheArcanaLord",l2trunk.scripts.quests._091_SagaOfTheArcanaLord.class);
        scripts.put("quests._092_SagaOfTheElementalMaster",l2trunk.scripts.quests._092_SagaOfTheElementalMaster.class);
        scripts.put("quests._093_SagaOfTheSpectralMaster",l2trunk.scripts.quests._093_SagaOfTheSpectralMaster.class);
        scripts.put("quests._094_SagaOfTheSoultaker",l2trunk.scripts.quests._094_SagaOfTheSoultaker.class);
        scripts.put("quests._095_SagaOfTheHellKnight",l2trunk.scripts.quests._095_SagaOfTheHellKnight.class);
        scripts.put("quests._096_SagaOfTheSpectralDancer",l2trunk.scripts.quests._096_SagaOfTheSpectralDancer.class);
        scripts.put("quests._097_SagaOfTheShillienTemplar",l2trunk.scripts.quests._097_SagaOfTheShillienTemplar.class);
        scripts.put("quests._098_SagaOfTheShillienSaint",l2trunk.scripts.quests._098_SagaOfTheShillienSaint.class);
        scripts.put("quests._099_SagaOfTheFortuneSeeker",l2trunk.scripts.quests._099_SagaOfTheFortuneSeeker.class);
        scripts.put("quests._100_SagaOfTheMaestro",l2trunk.scripts.quests._100_SagaOfTheMaestro.class);
        scripts.put("quests._101_SwordOfSolidarity",l2trunk.scripts.quests._101_SwordOfSolidarity.class);
        scripts.put("quests._10267_JourneyToGracia",l2trunk.scripts.quests._10267_JourneyToGracia.class);
        scripts.put("quests._10268_ToTheSeedOfInfinity",l2trunk.scripts.quests._10268_ToTheSeedOfInfinity.class);
        scripts.put("quests._10269_ToTheSeedOfDestruction",l2trunk.scripts.quests._10269_ToTheSeedOfDestruction.class);
        scripts.put("quests._10270_BirthOfTheSeed",l2trunk.scripts.quests._10270_BirthOfTheSeed.class);
        scripts.put("quests._10271_TheEnvelopingDarkness",l2trunk.scripts.quests._10271_TheEnvelopingDarkness.class);
        scripts.put("quests._10272_LightFragment",l2trunk.scripts.quests._10272_LightFragment.class);
        scripts.put("quests._10273_GoodDayToFly",l2trunk.scripts.quests._10273_GoodDayToFly.class);
        scripts.put("quests._10274_CollectingInTheAir",l2trunk.scripts.quests._10274_CollectingInTheAir.class);
        scripts.put("quests._10275_ContainingTheAttributePower",l2trunk.scripts.quests._10275_ContainingTheAttributePower.class);
        scripts.put("quests._10276_MutatedKaneusGludio",l2trunk.scripts.quests._10276_MutatedKaneusGludio.class);
        scripts.put("quests._10277_MutatedKaneusDion",l2trunk.scripts.quests._10277_MutatedKaneusDion.class);
        scripts.put("quests._10278_MutatedKaneusHeine",l2trunk.scripts.quests._10278_MutatedKaneusHeine.class);
        scripts.put("quests._10279_MutatedKaneusOren",l2trunk.scripts.quests._10279_MutatedKaneusOren.class);
        scripts.put("quests._10280_MutatedKaneusSchuttgart",l2trunk.scripts.quests._10280_MutatedKaneusSchuttgart.class);
        scripts.put("quests._10281_MutatedKaneusRune",l2trunk.scripts.quests._10281_MutatedKaneusRune.class);
        scripts.put("quests._10282_ToTheSeedOfAnnihilation",l2trunk.scripts.quests._10282_ToTheSeedOfAnnihilation.class);
        scripts.put("quests._10283_RequestOfIceMerchant",l2trunk.scripts.quests._10283_RequestOfIceMerchant.class);
        scripts.put("quests._10284_AcquisionOfDivineSword",l2trunk.scripts.quests._10284_AcquisionOfDivineSword.class);
        scripts.put("quests._10285_MeetingSirra",l2trunk.scripts.quests._10285_MeetingSirra.class);
        scripts.put("quests._10286_ReunionWithSirra",l2trunk.scripts.quests._10286_ReunionWithSirra.class);
        scripts.put("quests._10287_StoryOfThoseLeft",l2trunk.scripts.quests._10287_StoryOfThoseLeft.class);
        scripts.put("quests._10288_SecretMission",l2trunk.scripts.quests._10288_SecretMission.class);
        scripts.put("quests._10289_FadeToBlack",l2trunk.scripts.quests._10289_FadeToBlack.class);
        scripts.put("quests._10290_LandDragonConqueror",l2trunk.scripts.quests._10290_LandDragonConqueror.class);
        scripts.put("quests._10291_FireDragonDestroyer",l2trunk.scripts.quests._10291_FireDragonDestroyer.class);
        scripts.put("quests._10292_SevenSignsGirlOfDoubt",l2trunk.scripts.quests._10292_SevenSignsGirlOfDoubt.class);
        scripts.put("quests._10293_SevenSignsForbiddenBook",l2trunk.scripts.quests._10293_SevenSignsForbiddenBook.class);
        scripts.put("quests._10294_SevenSignsMonasteryofSilence",l2trunk.scripts.quests._10294_SevenSignsMonasteryofSilence.class);
        scripts.put("quests._10295_SevenSignsSolinasTomb",l2trunk.scripts.quests._10295_SevenSignsSolinasTomb.class);
        scripts.put("quests._10296_SevenSignsPoweroftheSeal",l2trunk.scripts.quests._10296_SevenSignsPoweroftheSeal.class);
        scripts.put("quests._102_SeaofSporesFever",l2trunk.scripts.quests._102_SeaofSporesFever.class);
        scripts.put("quests._103_SpiritOfCraftsman",l2trunk.scripts.quests._103_SpiritOfCraftsman.class);
        scripts.put("quests._104_SpiritOfMirror",l2trunk.scripts.quests._104_SpiritOfMirror.class);
        scripts.put("quests._10501_CapeEmbroideredSoulOne",l2trunk.scripts.quests._10501_CapeEmbroideredSoulOne.class);
        scripts.put("quests._10502_CapeEmbroideredSoulTwo",l2trunk.scripts.quests._10502_CapeEmbroideredSoulTwo.class);
        scripts.put("quests._10503_CapeEmbroideredSoulThree",l2trunk.scripts.quests._10503_CapeEmbroideredSoulThree.class);
        scripts.put("quests._10504_JewelOfAntharas",l2trunk.scripts.quests._10504_JewelOfAntharas.class);
        scripts.put("quests._10505_JewelOfValakas",l2trunk.scripts.quests._10505_JewelOfValakas.class);
        scripts.put("quests._105_SkirmishWithOrcs",l2trunk.scripts.quests._105_SkirmishWithOrcs.class);
        scripts.put("quests._106_ForgottenTruth",l2trunk.scripts.quests._106_ForgottenTruth.class);
        scripts.put("quests._107_MercilessPunishment",l2trunk.scripts.quests._107_MercilessPunishment.class);
        scripts.put("quests._108_JumbleTumbleDiamondFuss",l2trunk.scripts.quests._108_JumbleTumbleDiamondFuss.class);
        scripts.put("quests._109_InSearchOfTheNest",l2trunk.scripts.quests._109_InSearchOfTheNest.class);
        scripts.put("quests._1102_Nottingale",l2trunk.scripts.quests._1102_Nottingale.class);
        scripts.put("quests._1103_OracleTeleport",l2trunk.scripts.quests._1103_OracleTeleport.class);
        scripts.put("quests._110_ToThePrimevalIsle",l2trunk.scripts.quests._110_ToThePrimevalIsle.class);
        scripts.put("quests._111_ElrokianHuntersProof",l2trunk.scripts.quests._111_ElrokianHuntersProof.class);
        scripts.put("quests._112_WalkOfFate",l2trunk.scripts.quests._112_WalkOfFate.class);
        scripts.put("quests._113_StatusOfTheBeaconTower",l2trunk.scripts.quests._113_StatusOfTheBeaconTower.class);
        scripts.put("quests._114_ResurrectionOfAnOldManager",l2trunk.scripts.quests._114_ResurrectionOfAnOldManager.class);
        scripts.put("quests._115_TheOtherSideOfTruth",l2trunk.scripts.quests._115_TheOtherSideOfTruth.class);
        scripts.put("quests._116_BeyondtheHillsofWinter",l2trunk.scripts.quests._116_BeyondtheHillsofWinter.class);
        scripts.put("quests._117_OceanOfDistantStar",l2trunk.scripts.quests._117_OceanOfDistantStar.class);
        scripts.put("quests._118_ToLeadAndBeLed",l2trunk.scripts.quests._118_ToLeadAndBeLed.class);
        scripts.put("quests._119_LastImperialPrince",l2trunk.scripts.quests._119_LastImperialPrince.class);
        scripts.put("quests._1201_DarkCloudMansion",l2trunk.scripts.quests._1201_DarkCloudMansion.class);
        scripts.put("quests._1202_CrystalCaverns",l2trunk.scripts.quests._1202_CrystalCaverns.class);
        scripts.put("quests._120_PavelsResearch",l2trunk.scripts.quests._120_PavelsResearch.class);
        scripts.put("quests._121_PavelTheGiants",l2trunk.scripts.quests._121_PavelTheGiants.class);
        scripts.put("quests._122_OminousNews",l2trunk.scripts.quests._122_OminousNews.class);
        scripts.put("quests._123_TheLeaderAndTheFollower",l2trunk.scripts.quests._123_TheLeaderAndTheFollower.class);
        scripts.put("quests._124_MeetingTheElroki",l2trunk.scripts.quests._124_MeetingTheElroki.class);
        scripts.put("quests._125_InTheNameOfEvilPart1",l2trunk.scripts.quests._125_InTheNameOfEvilPart1.class);
        scripts.put("quests._126_IntheNameofEvilPart2",l2trunk.scripts.quests._126_IntheNameofEvilPart2.class);
        scripts.put("quests._128_PailakaSongofIceandFire",l2trunk.scripts.quests._128_PailakaSongofIceandFire.class);
        scripts.put("quests._129_PailakaDevilsLegacy",l2trunk.scripts.quests._129_PailakaDevilsLegacy.class);
        scripts.put("quests._130_PathToHellbound",l2trunk.scripts.quests._130_PathToHellbound.class);
        scripts.put("quests._131_BirdInACage",l2trunk.scripts.quests._131_BirdInACage.class);
        scripts.put("quests._132_MatrasCuriosity",l2trunk.scripts.quests._132_MatrasCuriosity.class);
        scripts.put("quests._133_ThatsBloodyHot",l2trunk.scripts.quests._133_ThatsBloodyHot.class);
        scripts.put("quests._134_TempleMissionary",l2trunk.scripts.quests._134_TempleMissionary.class);
        scripts.put("quests._135_TempleExecutor",l2trunk.scripts.quests._135_TempleExecutor.class);
        scripts.put("quests._136_MoreThanMeetsTheEye",l2trunk.scripts.quests._136_MoreThanMeetsTheEye.class);
        scripts.put("quests._137_TempleChampionPart1",l2trunk.scripts.quests._137_TempleChampionPart1.class);
        scripts.put("quests._138_TempleChampionPart2",l2trunk.scripts.quests._138_TempleChampionPart2.class);
        scripts.put("quests._139_ShadowFoxPart1",l2trunk.scripts.quests._139_ShadowFoxPart1.class);
        scripts.put("quests._140_ShadowFoxPart2",l2trunk.scripts.quests._140_ShadowFoxPart2.class);
        scripts.put("quests._141_ShadowFoxPart3",l2trunk.scripts.quests._141_ShadowFoxPart3.class);
        scripts.put("quests._142_FallenAngelRequestOfDawn",l2trunk.scripts.quests._142_FallenAngelRequestOfDawn.class);
        scripts.put("quests._143_FallenAngelRequestOfDusk",l2trunk.scripts.quests._143_FallenAngelRequestOfDusk.class);
        scripts.put("quests._144_PailakaInjuredDragon",l2trunk.scripts.quests._144_PailakaInjuredDragon.class);
        scripts.put("quests._146_TheZeroHour",l2trunk.scripts.quests._146_TheZeroHour.class);
        scripts.put("quests._147_PathToBecomingAnEliteMercenary",l2trunk.scripts.quests._147_PathToBecomingAnEliteMercenary.class);
        scripts.put("quests._148_PathToBecomingAnExaltedMercenary",l2trunk.scripts.quests._148_PathToBecomingAnExaltedMercenary.class);
        scripts.put("quests._151_CureforFeverDisease",l2trunk.scripts.quests._151_CureforFeverDisease.class);
        scripts.put("quests._152_ShardsOfGolem",l2trunk.scripts.quests._152_ShardsOfGolem.class);
        scripts.put("quests._153_DeliverGoods",l2trunk.scripts.quests._153_DeliverGoods.class);
        scripts.put("quests._154_SacrificeToSea",l2trunk.scripts.quests._154_SacrificeToSea.class);
        scripts.put("quests._155_FindSirWindawood",l2trunk.scripts.quests._155_FindSirWindawood.class);
        scripts.put("quests._156_MillenniumLove",l2trunk.scripts.quests._156_MillenniumLove.class);
        scripts.put("quests._157_RecoverSmuggled",l2trunk.scripts.quests._157_RecoverSmuggled.class);
        scripts.put("quests._158_SeedOfEvil",l2trunk.scripts.quests._158_SeedOfEvil.class);
        scripts.put("quests._159_ProtectHeadsprings",l2trunk.scripts.quests._159_ProtectHeadsprings.class);
        scripts.put("quests._160_NerupasFavor",l2trunk.scripts.quests._160_NerupasFavor.class);
        scripts.put("quests._161_FruitsOfMothertree",l2trunk.scripts.quests._161_FruitsOfMothertree.class);
        scripts.put("quests._162_CurseOfUndergroundFortress",l2trunk.scripts.quests._162_CurseOfUndergroundFortress.class);
        scripts.put("quests._163_LegacyOfPoet",l2trunk.scripts.quests._163_LegacyOfPoet.class);
        scripts.put("quests._164_BloodFiend",l2trunk.scripts.quests._164_BloodFiend.class);
        scripts.put("quests._165_ShilensHunt",l2trunk.scripts.quests._165_ShilensHunt.class);
        scripts.put("quests._166_DarkMass",l2trunk.scripts.quests._166_DarkMass.class);
        scripts.put("quests._167_DwarvenKinship",l2trunk.scripts.quests._167_DwarvenKinship.class);
        scripts.put("quests._168_DeliverSupplies",l2trunk.scripts.quests._168_DeliverSupplies.class);
        scripts.put("quests._169_OffspringOfNightmares",l2trunk.scripts.quests._169_OffspringOfNightmares.class);
        scripts.put("quests._170_DangerousSeduction",l2trunk.scripts.quests._170_DangerousSeduction.class);
        scripts.put("quests._171_ActsOfEvil",l2trunk.scripts.quests._171_ActsOfEvil.class);
        scripts.put("quests._172_NewHorizons",l2trunk.scripts.quests._172_NewHorizons.class);
        scripts.put("quests._173_ToTheIsleOfSouls",l2trunk.scripts.quests._173_ToTheIsleOfSouls.class);
        scripts.put("quests._174_SupplyCheck",l2trunk.scripts.quests._174_SupplyCheck.class);
        scripts.put("quests._175_TheWayOfTheWarrior",l2trunk.scripts.quests._175_TheWayOfTheWarrior.class);
        scripts.put("quests._176_StepsForHonor",l2trunk.scripts.quests._176_StepsForHonor.class);
        scripts.put("quests._178_IconicTrinity",l2trunk.scripts.quests._178_IconicTrinity.class);
        scripts.put("quests._179_IntoTheLargeCavern",l2trunk.scripts.quests._179_IntoTheLargeCavern.class);
        scripts.put("quests._182_NewRecruits",l2trunk.scripts.quests._182_NewRecruits.class);
        scripts.put("quests._183_RelicExploration",l2trunk.scripts.quests._183_RelicExploration.class);
        scripts.put("quests._184_NikolasCooperationContract",l2trunk.scripts.quests._184_NikolasCooperationContract.class);
        scripts.put("quests._185_NikolasCooperationConsideration",l2trunk.scripts.quests._185_NikolasCooperationConsideration.class);
        scripts.put("quests._186_ContractExecution",l2trunk.scripts.quests._186_ContractExecution.class);
        scripts.put("quests._187_NikolasHeart",l2trunk.scripts.quests._187_NikolasHeart.class);
        scripts.put("quests._188_SealRemoval",l2trunk.scripts.quests._188_SealRemoval.class);
        scripts.put("quests._189_ContractCompletion",l2trunk.scripts.quests._189_ContractCompletion.class);
        scripts.put("quests._190_LostDream",l2trunk.scripts.quests._190_LostDream.class);
        scripts.put("quests._191_VainConclusion",l2trunk.scripts.quests._191_VainConclusion.class);
        scripts.put("quests._192_SevenSignSeriesOfDoubt",l2trunk.scripts.quests._192_SevenSignSeriesOfDoubt.class);
        scripts.put("quests._193_SevenSignDyingMessage",l2trunk.scripts.quests._193_SevenSignDyingMessage.class);
        scripts.put("quests._194_SevenSignsMammonsContract",l2trunk.scripts.quests._194_SevenSignsMammonsContract.class);
        scripts.put("quests._195_SevenSignsSecretRitualofthePriests",l2trunk.scripts.quests._195_SevenSignsSecretRitualofthePriests.class);
        scripts.put("quests._196_SevenSignsSealoftheEmperor",l2trunk.scripts.quests._196_SevenSignsSealoftheEmperor.class);
        scripts.put("quests._197_SevenSignsTheSacredBookofSeal",l2trunk.scripts.quests._197_SevenSignsTheSacredBookofSeal.class);
        scripts.put("quests._198_SevenSignsEmbryo",l2trunk.scripts.quests._198_SevenSignsEmbryo.class);
        scripts.put("quests._211_TrialOfChallenger",l2trunk.scripts.quests._211_TrialOfChallenger.class);
        scripts.put("quests._212_TrialOfDuty",l2trunk.scripts.quests._212_TrialOfDuty.class);
        scripts.put("quests._213_TrialOfSeeker",l2trunk.scripts.quests._213_TrialOfSeeker.class);
        scripts.put("quests._214_TrialOfScholar",l2trunk.scripts.quests._214_TrialOfScholar.class);
        scripts.put("quests._215_TrialOfPilgrim",l2trunk.scripts.quests._215_TrialOfPilgrim.class);
        scripts.put("quests._216_TrialoftheGuildsman",l2trunk.scripts.quests._216_TrialoftheGuildsman.class);
        scripts.put("quests._217_TestimonyOfTrust",l2trunk.scripts.quests._217_TestimonyOfTrust.class);
        scripts.put("quests._218_TestimonyOfLife",l2trunk.scripts.quests._218_TestimonyOfLife.class);
        scripts.put("quests._219_TestimonyOfFate",l2trunk.scripts.quests._219_TestimonyOfFate.class);
        scripts.put("quests._220_TestimonyOfGlory",l2trunk.scripts.quests._220_TestimonyOfGlory.class);
        scripts.put("quests._221_TestimonyOfProsperity",l2trunk.scripts.quests._221_TestimonyOfProsperity.class);
        scripts.put("quests._222_TestOfDuelist",l2trunk.scripts.quests._222_TestOfDuelist.class);
        scripts.put("quests._223_TestOfChampion",l2trunk.scripts.quests._223_TestOfChampion.class);
        scripts.put("quests._224_TestOfSagittarius",l2trunk.scripts.quests._224_TestOfSagittarius.class);
        scripts.put("quests._225_TestOfTheSearcher",l2trunk.scripts.quests._225_TestOfTheSearcher.class);
        scripts.put("quests._226_TestOfHealer",l2trunk.scripts.quests._226_TestOfHealer.class);
        scripts.put("quests._227_TestOfTheReformer",l2trunk.scripts.quests._227_TestOfTheReformer.class);
        scripts.put("quests._228_TestOfMagus",l2trunk.scripts.quests._228_TestOfMagus.class);
        scripts.put("quests._229_TestOfWitchcraft",l2trunk.scripts.quests._229_TestOfWitchcraft.class);
        scripts.put("quests._230_TestOfSummoner",l2trunk.scripts.quests._230_TestOfSummoner.class);
        scripts.put("quests._231_TestOfTheMaestro",l2trunk.scripts.quests._231_TestOfTheMaestro.class);
        scripts.put("quests._232_TestOfLord",l2trunk.scripts.quests._232_TestOfLord.class);
        scripts.put("quests._233_TestOfWarspirit",l2trunk.scripts.quests._233_TestOfWarspirit.class);
        scripts.put("quests._234_FatesWhisper",l2trunk.scripts.quests._234_FatesWhisper.class);
        scripts.put("quests._235_MimirsElixir",l2trunk.scripts.quests._235_MimirsElixir.class);
        scripts.put("quests._236_SeedsOfChaos",l2trunk.scripts.quests._236_SeedsOfChaos.class);
        scripts.put("quests._237_WindsOfChange",l2trunk.scripts.quests._237_WindsOfChange.class);
        scripts.put("quests._238_SuccessFailureOfBusiness",l2trunk.scripts.quests._238_SuccessFailureOfBusiness.class);
        scripts.put("quests._239_WontYouJoinUs",l2trunk.scripts.quests._239_WontYouJoinUs.class);
        scripts.put("quests._240_ImTheOnlyOneYouCanTrust",l2trunk.scripts.quests._240_ImTheOnlyOneYouCanTrust.class);
        scripts.put("quests._241_PossessorOfaPreciousSoul1",l2trunk.scripts.quests._241_PossessorOfaPreciousSoul1.class);
        scripts.put("quests._242_PossessorOfaPreciousSoul2",l2trunk.scripts.quests._242_PossessorOfaPreciousSoul2.class);
        scripts.put("quests._246_PossessorOfaPreciousSoul3",l2trunk.scripts.quests._246_PossessorOfaPreciousSoul3.class);
        scripts.put("quests._247_PossessorOfaPreciousSoul4",l2trunk.scripts.quests._247_PossessorOfaPreciousSoul4.class);
        scripts.put("quests._249_PoisonedPlainsOfTheLizardmen",l2trunk.scripts.quests._249_PoisonedPlainsOfTheLizardmen.class);
        scripts.put("quests._250_WatchWhatYouEat",l2trunk.scripts.quests._250_WatchWhatYouEat.class);
        scripts.put("quests._251_NoSecrets",l2trunk.scripts.quests._251_NoSecrets.class);
        scripts.put("quests._252_GoodSmell",l2trunk.scripts.quests._252_GoodSmell.class);
        scripts.put("quests._254_LegendaryTales",l2trunk.scripts.quests._254_LegendaryTales.class);
        scripts.put("quests._255_Tutorial",l2trunk.scripts.quests._255_Tutorial.class);
        scripts.put("quests._257_GuardIsBusy",l2trunk.scripts.quests._257_GuardIsBusy.class);
        scripts.put("quests._258_BringWolfPelts",l2trunk.scripts.quests._258_BringWolfPelts.class);
        scripts.put("quests._259_RanchersPlea",l2trunk.scripts.quests._259_RanchersPlea.class);
        scripts.put("quests._260_HuntTheOrcs",l2trunk.scripts.quests._260_HuntTheOrcs.class);
        scripts.put("quests._261_CollectorsDream",l2trunk.scripts.quests._261_CollectorsDream.class);
        scripts.put("quests._262_TradewiththeIvoryTower",l2trunk.scripts.quests._262_TradewiththeIvoryTower.class);
        scripts.put("quests._263_OrcSubjugation",l2trunk.scripts.quests._263_OrcSubjugation.class);
        scripts.put("quests._264_KeenClaws",l2trunk.scripts.quests._264_KeenClaws.class);
        scripts.put("quests._265_ChainsOfSlavery",l2trunk.scripts.quests._265_ChainsOfSlavery.class);
        scripts.put("quests._266_PleaOfPixies",l2trunk.scripts.quests._266_PleaOfPixies.class);
        scripts.put("quests._267_WrathOfVerdure",l2trunk.scripts.quests._267_WrathOfVerdure.class);
        scripts.put("quests._268_TracesOfEvil",l2trunk.scripts.quests._268_TracesOfEvil.class);
        scripts.put("quests._269_InventionAmbition",l2trunk.scripts.quests._269_InventionAmbition.class);
        scripts.put("quests._270_TheOneWhoEndsSilence",l2trunk.scripts.quests._270_TheOneWhoEndsSilence.class);
        scripts.put("quests._271_ProofOfValor",l2trunk.scripts.quests._271_ProofOfValor.class);
        scripts.put("quests._272_WrathOfAncestors",l2trunk.scripts.quests._272_WrathOfAncestors.class);
        scripts.put("quests._273_InvadersOfHolyland",l2trunk.scripts.quests._273_InvadersOfHolyland.class);
        scripts.put("quests._274_SkirmishWithTheWerewolves",l2trunk.scripts.quests._274_SkirmishWithTheWerewolves.class);
        scripts.put("quests._275_BlackWingedSpies",l2trunk.scripts.quests._275_BlackWingedSpies.class);
        scripts.put("quests._276_HestuiTotem",l2trunk.scripts.quests._276_HestuiTotem.class);
        scripts.put("quests._277_GatekeepersOffering",l2trunk.scripts.quests._277_GatekeepersOffering.class);
        scripts.put("quests._278_HomeSecurity",l2trunk.scripts.quests._278_HomeSecurity.class);
        scripts.put("quests._279_TargetOfOpportunity",l2trunk.scripts.quests._279_TargetOfOpportunity.class);
        scripts.put("quests._280_TheFoodChain",l2trunk.scripts.quests._280_TheFoodChain.class);
        scripts.put("quests._281_HeadForTheHills",l2trunk.scripts.quests._281_HeadForTheHills.class);
        scripts.put("quests._283_TheFewTheProudTheBrave",l2trunk.scripts.quests._283_TheFewTheProudTheBrave.class);
        scripts.put("quests._284_MuertosFeather",l2trunk.scripts.quests._284_MuertosFeather.class);
        scripts.put("quests._286_FabulousFeathers",l2trunk.scripts.quests._286_FabulousFeathers.class);
        scripts.put("quests._287_FiguringItOut",l2trunk.scripts.quests._287_FiguringItOut.class);
        scripts.put("quests._288_HandleWithCare",l2trunk.scripts.quests._288_HandleWithCare.class);
        scripts.put("quests._289_DeliciousFoodsAreMine",l2trunk.scripts.quests._289_DeliciousFoodsAreMine.class);
        scripts.put("quests._290_ThreatRemoval",l2trunk.scripts.quests._290_ThreatRemoval.class);
        scripts.put("quests._291_RevengeOfTheRedbonnet",l2trunk.scripts.quests._291_RevengeOfTheRedbonnet.class);
        scripts.put("quests._292_BrigandsSweep",l2trunk.scripts.quests._292_BrigandsSweep.class);
        scripts.put("quests._293_HiddenVein",l2trunk.scripts.quests._293_HiddenVein.class);
        scripts.put("quests._294_CovertBusiness",l2trunk.scripts.quests._294_CovertBusiness.class);
        scripts.put("quests._295_DreamsOfTheSkies",l2trunk.scripts.quests._295_DreamsOfTheSkies.class);
        scripts.put("quests._296_SilkOfTarantula",l2trunk.scripts.quests._296_SilkOfTarantula.class);
        scripts.put("quests._297_GateKeepersFavor",l2trunk.scripts.quests._297_GateKeepersFavor.class);
        scripts.put("quests._298_LizardmensConspiracy",l2trunk.scripts.quests._298_LizardmensConspiracy.class);
        scripts.put("quests._299_GatherIngredientsforPie",l2trunk.scripts.quests._299_GatherIngredientsforPie.class);
        scripts.put("quests._300_HuntingLetoLizardman",l2trunk.scripts.quests._300_HuntingLetoLizardman.class);
        scripts.put("quests._303_CollectArrowheads",l2trunk.scripts.quests._303_CollectArrowheads.class);
        scripts.put("quests._306_CrystalOfFireice",l2trunk.scripts.quests._306_CrystalOfFireice.class);
        scripts.put("quests._307_ControlDeviceoftheGiants",l2trunk.scripts.quests._307_ControlDeviceoftheGiants.class);
        scripts.put("quests._308_ReedFieldMaintenance",l2trunk.scripts.quests._308_ReedFieldMaintenance.class);
        scripts.put("quests._309_ForAGoodCause",l2trunk.scripts.quests._309_ForAGoodCause.class);
        scripts.put("quests._310_OnlyWhatRemains",l2trunk.scripts.quests._310_OnlyWhatRemains.class);
        scripts.put("quests._311_ExpulsionOfEvilSpirits",l2trunk.scripts.quests._311_ExpulsionOfEvilSpirits.class);
        scripts.put("quests._312_TakeAdvantageOfTheCrisis",l2trunk.scripts.quests._312_TakeAdvantageOfTheCrisis.class);
        scripts.put("quests._313_CollectSpores",l2trunk.scripts.quests._313_CollectSpores.class);
        scripts.put("quests._316_DestroyPlaguebringers",l2trunk.scripts.quests._316_DestroyPlaguebringers.class);
        scripts.put("quests._317_CatchTheWind",l2trunk.scripts.quests._317_CatchTheWind.class);
        scripts.put("quests._319_ScentOfDeath",l2trunk.scripts.quests._319_ScentOfDeath.class);
        scripts.put("quests._320_BonesTellFuture",l2trunk.scripts.quests._320_BonesTellFuture.class);
        scripts.put("quests._324_SweetestVenom",l2trunk.scripts.quests._324_SweetestVenom.class);
        scripts.put("quests._325_GrimCollector",l2trunk.scripts.quests._325_GrimCollector.class);
        scripts.put("quests._326_VanquishRemnants",l2trunk.scripts.quests._326_VanquishRemnants.class);
        scripts.put("quests._327_ReclaimTheLand",l2trunk.scripts.quests._327_ReclaimTheLand.class);
        scripts.put("quests._328_SenseForBusiness",l2trunk.scripts.quests._328_SenseForBusiness.class);
        scripts.put("quests._329_CuriosityOfDwarf",l2trunk.scripts.quests._329_CuriosityOfDwarf.class);
        scripts.put("quests._330_AdeptOfTaste",l2trunk.scripts.quests._330_AdeptOfTaste.class);
        scripts.put("quests._331_ArrowForVengeance",l2trunk.scripts.quests._331_ArrowForVengeance.class);
        scripts.put("quests._333_BlackLionHunt",l2trunk.scripts.quests._333_BlackLionHunt.class);
        scripts.put("quests._334_TheWishingPotion",l2trunk.scripts.quests._334_TheWishingPotion.class);
        scripts.put("quests._335_TheSongOfTheHunter",l2trunk.scripts.quests._335_TheSongOfTheHunter.class);
        scripts.put("quests._336_CoinOfMagic",l2trunk.scripts.quests._336_CoinOfMagic.class);
        scripts.put("quests._337_AudienceWithLandDragon",l2trunk.scripts.quests._337_AudienceWithLandDragon.class);
        scripts.put("quests._338_AlligatorHunter",l2trunk.scripts.quests._338_AlligatorHunter.class);
        scripts.put("quests._340_SubjugationofLizardmen",l2trunk.scripts.quests._340_SubjugationofLizardmen.class);
        scripts.put("quests._341_HuntingForWildBeasts",l2trunk.scripts.quests._341_HuntingForWildBeasts.class);
        scripts.put("quests._343_UndertheShadowoftheIvoryTower",l2trunk.scripts.quests._343_UndertheShadowoftheIvoryTower.class);
        scripts.put("quests._344_1000YearsEndofLamentation",l2trunk.scripts.quests._344_1000YearsEndofLamentation.class);
        scripts.put("quests._345_MethodToRaiseTheDead",l2trunk.scripts.quests._345_MethodToRaiseTheDead.class);
        scripts.put("quests._347_GoGetTheCalculator",l2trunk.scripts.quests._347_GoGetTheCalculator.class);
        scripts.put("quests._348_ArrogantSearch",l2trunk.scripts.quests._348_ArrogantSearch.class);
        scripts.put("quests._350_EnhanceYourWeapon",l2trunk.scripts.quests._350_EnhanceYourWeapon.class);
        scripts.put("quests._351_BlackSwan",l2trunk.scripts.quests._351_BlackSwan.class);
        scripts.put("quests._352_HelpRoodRaiseANewPet",l2trunk.scripts.quests._352_HelpRoodRaiseANewPet.class);
        scripts.put("quests._354_ConquestofAlligatorIsland",l2trunk.scripts.quests._354_ConquestofAlligatorIsland.class);
        scripts.put("quests._355_FamilyHonor",l2trunk.scripts.quests._355_FamilyHonor.class);
        scripts.put("quests._356_DigUpTheSeaOfSpores",l2trunk.scripts.quests._356_DigUpTheSeaOfSpores.class);
        scripts.put("quests._357_WarehouseKeepersAmbition",l2trunk.scripts.quests._357_WarehouseKeepersAmbition.class);
        scripts.put("quests._358_IllegitimateChildOfAGoddess",l2trunk.scripts.quests._358_IllegitimateChildOfAGoddess.class);
        scripts.put("quests._359_ForSleeplessDeadmen",l2trunk.scripts.quests._359_ForSleeplessDeadmen.class);
        scripts.put("quests._360_PlunderTheirSupplies",l2trunk.scripts.quests._360_PlunderTheirSupplies.class);
        scripts.put("quests._362_BardsMandolin",l2trunk.scripts.quests._362_BardsMandolin.class);
        scripts.put("quests._363_SorrowfulSoundofFlute",l2trunk.scripts.quests._363_SorrowfulSoundofFlute.class);
        scripts.put("quests._364_JovialAccordion",l2trunk.scripts.quests._364_JovialAccordion.class);
        scripts.put("quests._365_DevilsLegacy",l2trunk.scripts.quests._365_DevilsLegacy.class);
        scripts.put("quests._366_SilverHairedShaman",l2trunk.scripts.quests._366_SilverHairedShaman.class);
        scripts.put("quests._367_ElectrifyingRecharge",l2trunk.scripts.quests._367_ElectrifyingRecharge.class);
        scripts.put("quests._368_TrespassingIntoTheSacredArea",l2trunk.scripts.quests._368_TrespassingIntoTheSacredArea.class);
        scripts.put("quests._369_CollectorOfJewels",l2trunk.scripts.quests._369_CollectorOfJewels.class);
        scripts.put("quests._370_AnElderSowsSeeds",l2trunk.scripts.quests._370_AnElderSowsSeeds.class);
        scripts.put("quests._371_ShriekOfGhosts",l2trunk.scripts.quests._371_ShriekOfGhosts.class);
        scripts.put("quests._372_LegacyOfInsolence",l2trunk.scripts.quests._372_LegacyOfInsolence.class);
        scripts.put("quests._373_SupplierOfReagents",l2trunk.scripts.quests._373_SupplierOfReagents.class);
        scripts.put("quests._376_GiantsExploration1",l2trunk.scripts.quests._376_GiantsExploration1.class);
        scripts.put("quests._377_GiantsExploration2",l2trunk.scripts.quests._377_GiantsExploration2.class);
        scripts.put("quests._378_MagnificentFeast",l2trunk.scripts.quests._378_MagnificentFeast.class);
        scripts.put("quests._379_FantasyWine",l2trunk.scripts.quests._379_FantasyWine.class);
        scripts.put("quests._380_BringOutTheFlavorOfIngredients",l2trunk.scripts.quests._380_BringOutTheFlavorOfIngredients.class);
        scripts.put("quests._381_LetsBecomeARoyalMember",l2trunk.scripts.quests._381_LetsBecomeARoyalMember.class);
        scripts.put("quests._382_KailsMagicCoin",l2trunk.scripts.quests._382_KailsMagicCoin.class);
        scripts.put("quests._383_SearchingForTreasure",l2trunk.scripts.quests._383_SearchingForTreasure.class);
        scripts.put("quests._384_WarehouseKeepersPastime",l2trunk.scripts.quests._384_WarehouseKeepersPastime.class);
        scripts.put("quests._385_YokeOfThePast",l2trunk.scripts.quests._385_YokeOfThePast.class);
        scripts.put("quests._386_StolenDignity",l2trunk.scripts.quests._386_StolenDignity.class);
        scripts.put("quests._401_PathToWarrior",l2trunk.scripts.quests._401_PathToWarrior.class);
        scripts.put("quests._402_PathToKnight",l2trunk.scripts.quests._402_PathToKnight.class);
        scripts.put("quests._403_PathToRogue",l2trunk.scripts.quests._403_PathToRogue.class);
        scripts.put("quests._404_PathToWizard",l2trunk.scripts.quests._404_PathToWizard.class);
        scripts.put("quests._405_PathToCleric",l2trunk.scripts.quests._405_PathToCleric.class);
        scripts.put("quests._406_PathToElvenKnight",l2trunk.scripts.quests._406_PathToElvenKnight.class);
        scripts.put("quests._407_PathToElvenScout",l2trunk.scripts.quests._407_PathToElvenScout.class);
        scripts.put("quests._408_PathToElvenwizard",l2trunk.scripts.quests._408_PathToElvenwizard.class);
        scripts.put("quests._409_PathToOracle",l2trunk.scripts.quests._409_PathToOracle.class);
        scripts.put("quests._410_PathToPalusKnight",l2trunk.scripts.quests._410_PathToPalusKnight.class);
        scripts.put("quests._411_PathToAssassin",l2trunk.scripts.quests._411_PathToAssassin.class);
        scripts.put("quests._412_PathToDarkwizard",l2trunk.scripts.quests._412_PathToDarkwizard.class);
        scripts.put("quests._413_PathToShillienOracle",l2trunk.scripts.quests._413_PathToShillienOracle.class);
        scripts.put("quests._414_PathToOrcRaider",l2trunk.scripts.quests._414_PathToOrcRaider.class);
        scripts.put("quests._415_PathToOrcMonk",l2trunk.scripts.quests._415_PathToOrcMonk.class);
        scripts.put("quests._416_PathToOrcShaman",l2trunk.scripts.quests._416_PathToOrcShaman.class);
        scripts.put("quests._417_PathToScavenger",l2trunk.scripts.quests._417_PathToScavenger.class);
        scripts.put("quests._418_PathToArtisan",l2trunk.scripts.quests._418_PathToArtisan.class);
        scripts.put("quests._419_GetaPet",l2trunk.scripts.quests._419_GetaPet.class);
        scripts.put("quests._420_LittleWings",l2trunk.scripts.quests._420_LittleWings.class);
        scripts.put("quests._421_LittleWingAdventures",l2trunk.scripts.quests._421_LittleWingAdventures.class);
        scripts.put("quests._422_RepentYourSins",l2trunk.scripts.quests._422_RepentYourSins.class);
        scripts.put("quests._423_TakeYourBestShot",l2trunk.scripts.quests._423_TakeYourBestShot.class);
        scripts.put("quests._426_QuestforFishingShot",l2trunk.scripts.quests._426_QuestforFishingShot.class);
        scripts.put("quests._431_WeddingMarch",l2trunk.scripts.quests._431_WeddingMarch.class);
        scripts.put("quests._432_BirthdayPartySong",l2trunk.scripts.quests._432_BirthdayPartySong.class);
        scripts.put("quests._450_GraveRobberMemberRescue",l2trunk.scripts.quests._450_GraveRobberMemberRescue.class);
        scripts.put("quests._451_LuciensAltar",l2trunk.scripts.quests._451_LuciensAltar.class);
        scripts.put("quests._452_FindingtheLostSoldiers",l2trunk.scripts.quests._452_FindingtheLostSoldiers.class);
        scripts.put("quests._453_NotStrongEnough",l2trunk.scripts.quests._453_NotStrongEnough.class);
        scripts.put("quests._454_CompletelyLost",l2trunk.scripts.quests._454_CompletelyLost.class);
        scripts.put("quests._455_WingsofSand",l2trunk.scripts.quests._455_WingsofSand.class);
        scripts.put("quests._456_DontKnowDontCare",l2trunk.scripts.quests._456_DontKnowDontCare.class);
        scripts.put("quests._457_LostAndFound",l2trunk.scripts.quests._457_LostAndFound.class);
        scripts.put("quests._458_PerfectForm",l2trunk.scripts.quests._458_PerfectForm.class);
        scripts.put("quests._461_RumbleInTheBase",l2trunk.scripts.quests._461_RumbleInTheBase.class);
        scripts.put("quests._463_IMustBeaGenius",l2trunk.scripts.quests._463_IMustBeaGenius.class);
        scripts.put("quests._464_Oath",l2trunk.scripts.quests._464_Oath.class);
        scripts.put("quests._501_ProofOfClanAlliance",l2trunk.scripts.quests._501_ProofOfClanAlliance.class);
        scripts.put("quests._503_PursuitClanAmbition",l2trunk.scripts.quests._503_PursuitClanAmbition.class);
        scripts.put("quests._504_CompetitionForTheBanditStronghold",l2trunk.scripts.quests._504_CompetitionForTheBanditStronghold.class);
        scripts.put("quests._508_TheClansReputation",l2trunk.scripts.quests._508_TheClansReputation.class);
        scripts.put("quests._509_TheClansPrestige",l2trunk.scripts.quests._509_TheClansPrestige.class);
        scripts.put("quests._510_AClansReputation",l2trunk.scripts.quests._510_AClansReputation.class);
        scripts.put("quests._511_AwlUnderFoot",l2trunk.scripts.quests._511_AwlUnderFoot.class);
        scripts.put("quests._512_AwlUnderFoot",l2trunk.scripts.quests._512_AwlUnderFoot.class);
        scripts.put("quests._551_OlympiadStarter",l2trunk.scripts.quests._551_OlympiadStarter.class);
        scripts.put("quests._552_OlympiadVeteran",l2trunk.scripts.quests._552_OlympiadVeteran.class);
        scripts.put("quests._553_OlympiadUndefeated",l2trunk.scripts.quests._553_OlympiadUndefeated.class);
        scripts.put("quests._601_WatchingEyes",l2trunk.scripts.quests._601_WatchingEyes.class);
        scripts.put("quests._602_ShadowofLight",l2trunk.scripts.quests._602_ShadowofLight.class);
        scripts.put("quests._603_DaimontheWhiteEyedPart1",l2trunk.scripts.quests._603_DaimontheWhiteEyedPart1.class);
        scripts.put("quests._604_DaimontheWhiteEyedPart2",l2trunk.scripts.quests._604_DaimontheWhiteEyedPart2.class);
        scripts.put("quests._605_AllianceWithKetraOrcs",l2trunk.scripts.quests._605_AllianceWithKetraOrcs.class);
        scripts.put("quests._606_WarwithVarkaSilenos",l2trunk.scripts.quests._606_WarwithVarkaSilenos.class);
        scripts.put("quests._607_ProveYourCourage",l2trunk.scripts.quests._607_ProveYourCourage.class);
        scripts.put("quests._608_SlayTheEnemyCommander",l2trunk.scripts.quests._608_SlayTheEnemyCommander.class);
        scripts.put("quests._609_MagicalPowerofWater1",l2trunk.scripts.quests._609_MagicalPowerofWater1.class);
        scripts.put("quests._610_MagicalPowerofWater2",l2trunk.scripts.quests._610_MagicalPowerofWater2.class);
        scripts.put("quests._611_AllianceWithVarkaSilenos",l2trunk.scripts.quests._611_AllianceWithVarkaSilenos.class);
        scripts.put("quests._612_WarwithKetraOrcs",l2trunk.scripts.quests._612_WarwithKetraOrcs.class);
        scripts.put("quests._613_ProveYourCourage",l2trunk.scripts.quests._613_ProveYourCourage.class);
        scripts.put("quests._614_SlayTheEnemyCommander",l2trunk.scripts.quests._614_SlayTheEnemyCommander.class);
        scripts.put("quests._615_MagicalPowerofFire1",l2trunk.scripts.quests._615_MagicalPowerofFire1.class);
        scripts.put("quests._616_MagicalPowerofFire2",l2trunk.scripts.quests._616_MagicalPowerofFire2.class);
        scripts.put("quests._617_GatherTheFlames",l2trunk.scripts.quests._617_GatherTheFlames.class);
        scripts.put("quests._618_IntoTheFlame",l2trunk.scripts.quests._618_IntoTheFlame.class);
        scripts.put("quests._619_RelicsOfTheOldEmpire",l2trunk.scripts.quests._619_RelicsOfTheOldEmpire.class);
        scripts.put("quests._620_FourGoblets",l2trunk.scripts.quests._620_FourGoblets.class);
        scripts.put("quests._621_EggDelivery",l2trunk.scripts.quests._621_EggDelivery.class);
        scripts.put("quests._622_DeliveryofSpecialLiquor",l2trunk.scripts.quests._622_DeliveryofSpecialLiquor.class);
        scripts.put("quests._623_TheFinestFood",l2trunk.scripts.quests._623_TheFinestFood.class);
        scripts.put("quests._624_TheFinestIngredientsPart1",l2trunk.scripts.quests._624_TheFinestIngredientsPart1.class);
        scripts.put("quests._625_TheFinestIngredientsPart2",l2trunk.scripts.quests._625_TheFinestIngredientsPart2.class);
        scripts.put("quests._626_ADarkTwilight",l2trunk.scripts.quests._626_ADarkTwilight.class);
        scripts.put("quests._627_HeartInSearchOfPower",l2trunk.scripts.quests._627_HeartInSearchOfPower.class);
        scripts.put("quests._628_HuntGoldenRam",l2trunk.scripts.quests._628_HuntGoldenRam.class);
        scripts.put("quests._629_CleanUpTheSwampOfScreams",l2trunk.scripts.quests._629_CleanUpTheSwampOfScreams.class);
        scripts.put("quests._631_DeliciousTopChoiceMeat",l2trunk.scripts.quests._631_DeliciousTopChoiceMeat.class);
        scripts.put("quests._632_NecromancersRequest",l2trunk.scripts.quests._632_NecromancersRequest.class);
        scripts.put("quests._633_InTheForgottenVillage",l2trunk.scripts.quests._633_InTheForgottenVillage.class);
        scripts.put("quests._634_InSearchofDimensionalFragments",l2trunk.scripts.quests._634_InSearchofDimensionalFragments.class);
        scripts.put("quests._635_InTheDimensionalRift",l2trunk.scripts.quests._635_InTheDimensionalRift.class);
        scripts.put("quests._636_TruthBeyond",l2trunk.scripts.quests._636_TruthBeyond.class);
        scripts.put("quests._637_ThroughOnceMore",l2trunk.scripts.quests._637_ThroughOnceMore.class);
        scripts.put("quests._638_SeekersOfTheHolyGrail",l2trunk.scripts.quests._638_SeekersOfTheHolyGrail.class);
        scripts.put("quests._639_GuardiansOfTheHolyGrail",l2trunk.scripts.quests._639_GuardiansOfTheHolyGrail.class);
        scripts.put("quests._640_TheZeroHour",l2trunk.scripts.quests._640_TheZeroHour.class);
        scripts.put("quests._641_AttackSailren",l2trunk.scripts.quests._641_AttackSailren.class);
        scripts.put("quests._642_APowerfulPrimevalCreature",l2trunk.scripts.quests._642_APowerfulPrimevalCreature.class);
        scripts.put("quests._643_RiseAndFallOfTheElrokiTribe",l2trunk.scripts.quests._643_RiseAndFallOfTheElrokiTribe.class);
        scripts.put("quests._644_GraveRobberAnnihilation",l2trunk.scripts.quests._644_GraveRobberAnnihilation.class);
        scripts.put("quests._645_GhostsOfBatur",l2trunk.scripts.quests._645_GhostsOfBatur.class);
        scripts.put("quests._646_SignsOfRevolt",l2trunk.scripts.quests._646_SignsOfRevolt.class);
        scripts.put("quests._647_InfluxOfMachines",l2trunk.scripts.quests._647_InfluxOfMachines.class);
        scripts.put("quests._648_AnIceMerchantsDream",l2trunk.scripts.quests._648_AnIceMerchantsDream.class);
        scripts.put("quests._649_ALooterandaRailroadMan",l2trunk.scripts.quests._649_ALooterandaRailroadMan.class);
        scripts.put("quests._650_ABrokenDream",l2trunk.scripts.quests._650_ABrokenDream.class);
        scripts.put("quests._651_RunawayYouth",l2trunk.scripts.quests._651_RunawayYouth.class);
        scripts.put("quests._652_AnAgedExAdventurer",l2trunk.scripts.quests._652_AnAgedExAdventurer.class);
        scripts.put("quests._653_WildMaiden",l2trunk.scripts.quests._653_WildMaiden.class);
        scripts.put("quests._654_JourneytoaSettlement",l2trunk.scripts.quests._654_JourneytoaSettlement.class);
        scripts.put("quests._655_AGrandPlanForTamingWildBeasts",l2trunk.scripts.quests._655_AGrandPlanForTamingWildBeasts.class);
        scripts.put("quests._659_IdRatherBeCollectingFairyBreath",l2trunk.scripts.quests._659_IdRatherBeCollectingFairyBreath.class);
        scripts.put("quests._660_AidingtheFloranVillage",l2trunk.scripts.quests._660_AidingtheFloranVillage.class);
        scripts.put("quests._661_TheHarvestGroundsSafe",l2trunk.scripts.quests._661_TheHarvestGroundsSafe.class);
        scripts.put("quests._662_AGameOfCards",l2trunk.scripts.quests._662_AGameOfCards.class);
        scripts.put("quests._663_SeductiveWhispers",l2trunk.scripts.quests._663_SeductiveWhispers.class);
        scripts.put("quests._688_DefeatTheElrokianRaiders",l2trunk.scripts.quests._688_DefeatTheElrokianRaiders.class);
        scripts.put("quests._690_JudesRequest",l2trunk.scripts.quests._690_JudesRequest.class);
        scripts.put("quests._691_MatrasSuspiciousRequest",l2trunk.scripts.quests._691_MatrasSuspiciousRequest.class);
        scripts.put("quests._692_HowtoOpposeEvil",l2trunk.scripts.quests._692_HowtoOpposeEvil.class);
        scripts.put("quests._694_BreakThroughTheHallOfSuffering",l2trunk.scripts.quests._694_BreakThroughTheHallOfSuffering.class);
        scripts.put("quests._695_DefendtheHallofSuffering",l2trunk.scripts.quests._695_DefendtheHallofSuffering.class);
        scripts.put("quests._696_ConquertheHallofErosion",l2trunk.scripts.quests._696_ConquertheHallofErosion.class);
        scripts.put("quests._697_DefendtheHallofErosion",l2trunk.scripts.quests._697_DefendtheHallofErosion.class);
        scripts.put("quests._698_BlocktheLordsEscape",l2trunk.scripts.quests._698_BlocktheLordsEscape.class);
        scripts.put("quests._699_GuardianoftheSkies",l2trunk.scripts.quests._699_GuardianoftheSkies.class);
        scripts.put("quests._700_CursedLife",l2trunk.scripts.quests._700_CursedLife.class);
        scripts.put("quests._701_ProofofExistence",l2trunk.scripts.quests._701_ProofofExistence.class);
        scripts.put("quests._702_ATrapForRevenge",l2trunk.scripts.quests._702_ATrapForRevenge.class);
        scripts.put("quests._704_Missqueen",l2trunk.scripts.quests._704_Missqueen.class);
        scripts.put("quests._708_PathToBecomingALordGludio",l2trunk.scripts.quests._708_PathToBecomingALordGludio.class);
        scripts.put("quests._709_PathToBecomingALordDion",l2trunk.scripts.quests._709_PathToBecomingALordDion.class);
        scripts.put("quests._710_PathToBecomingALordGiran",l2trunk.scripts.quests._710_PathToBecomingALordGiran.class);
        scripts.put("quests._711_PathToBecomingALordInnadril",l2trunk.scripts.quests._711_PathToBecomingALordInnadril.class);
        scripts.put("quests._712_PathToBecomingALordOren",l2trunk.scripts.quests._712_PathToBecomingALordOren.class);
        scripts.put("quests._713_PathToBecomingALordAden",l2trunk.scripts.quests._713_PathToBecomingALordAden.class);
        scripts.put("quests._714_PathToBecomingALordSchuttgart",l2trunk.scripts.quests._714_PathToBecomingALordSchuttgart.class);
        scripts.put("quests._715_PathToBecomingALordGoddard",l2trunk.scripts.quests._715_PathToBecomingALordGoddard.class);
        scripts.put("quests._716_PathToBecomingALordRune",l2trunk.scripts.quests._716_PathToBecomingALordRune.class);
        scripts.put("quests._717_ForTheSakeOfTheTerritoryGludio",l2trunk.scripts.quests._717_ForTheSakeOfTheTerritoryGludio.class);
        scripts.put("quests._718_ForTheSakeOfTheTerritoryDion",l2trunk.scripts.quests._718_ForTheSakeOfTheTerritoryDion.class);
        scripts.put("quests._719_ForTheSakeOfTheTerritoryGiran",l2trunk.scripts.quests._719_ForTheSakeOfTheTerritoryGiran.class);
        scripts.put("quests._720_ForTheSakeOfTheTerritoryOren",l2trunk.scripts.quests._720_ForTheSakeOfTheTerritoryOren.class);
        scripts.put("quests._721_ForTheSakeOfTheTerritoryAden",l2trunk.scripts.quests._721_ForTheSakeOfTheTerritoryAden.class);
        scripts.put("quests._722_ForTheSakeOfTheTerritoryInnadril",l2trunk.scripts.quests._722_ForTheSakeOfTheTerritoryInnadril.class);
        scripts.put("quests._723_ForTheSakeOfTheTerritoryGoddard",l2trunk.scripts.quests._723_ForTheSakeOfTheTerritoryGoddard.class);
        scripts.put("quests._724_ForTheSakeOfTheTerritoryRune",l2trunk.scripts.quests._724_ForTheSakeOfTheTerritoryRune.class);
        scripts.put("quests._725_ForTheSakeOfTheTerritoryShuttdart",l2trunk.scripts.quests._725_ForTheSakeOfTheTerritoryShuttdart.class);
        scripts.put("quests._726_LightwithintheDarkness",l2trunk.scripts.quests._726_LightwithintheDarkness.class);
        scripts.put("quests._727_HopewithintheDarkness",l2trunk.scripts.quests._727_HopewithintheDarkness.class);
        scripts.put("quests._729_ProtectTheTerritoryCatapult",l2trunk.scripts.quests._729_ProtectTheTerritoryCatapult.class);
        scripts.put("quests._730_ProtectTheSuppliesSafe",l2trunk.scripts.quests._730_ProtectTheSuppliesSafe.class);
        scripts.put("quests._731_ProtectTheMilitaryAssociationLeader",l2trunk.scripts.quests._731_ProtectTheMilitaryAssociationLeader.class);
        scripts.put("quests._732_ProtectTheReligiousAssociationLeader",l2trunk.scripts.quests._732_ProtectTheReligiousAssociationLeader.class);
        scripts.put("quests._733_ProtectTheEconomicAssociationLeader",l2trunk.scripts.quests._733_ProtectTheEconomicAssociationLeader.class);
        scripts.put("quests._734_PierceThroughAShield",l2trunk.scripts.quests._734_PierceThroughAShield.class);
        scripts.put("quests._735_MakeSpearsDull",l2trunk.scripts.quests._735_MakeSpearsDull.class);
        scripts.put("quests._736_WeakenTheMagic",l2trunk.scripts.quests._736_WeakenTheMagic.class);
        scripts.put("quests._737_DenyBlessings",l2trunk.scripts.quests._737_DenyBlessings.class);
        scripts.put("quests._738_DestroyKeyTargets",l2trunk.scripts.quests._738_DestroyKeyTargets.class);
        scripts.put("quests._901_HowLavasaurusesAreMade",l2trunk.scripts.quests._901_HowLavasaurusesAreMade.class);
        scripts.put("quests._902_ReclaimOurEra",l2trunk.scripts.quests._902_ReclaimOurEra.class);
        scripts.put("quests._903_TheCallofAntharas",l2trunk.scripts.quests._903_TheCallofAntharas.class);
        scripts.put("quests._904_DragonTrophyAntharas",l2trunk.scripts.quests._904_DragonTrophyAntharas.class);
        scripts.put("quests._905_RefinedDragonBlood",l2trunk.scripts.quests._905_RefinedDragonBlood.class);
        scripts.put("quests._906_TheCallofValakas",l2trunk.scripts.quests._906_TheCallofValakas.class);
        scripts.put("quests._907_DragonTrophyValakas",l2trunk.scripts.quests._907_DragonTrophyValakas.class);
        scripts.put("quests._999_T1Tutorial",l2trunk.scripts.quests._999_T1Tutorial.class);
        scripts.put("scriptconfig.ScriptConfig",l2trunk.scripts.scriptconfig.ScriptConfig.class);
        scripts.put("services.community.CareerManager",l2trunk.scripts.services.community.CareerManager.class);
        scripts.put("services.community.CommunityAuctionHouse",l2trunk.scripts.services.community.CommunityAuctionHouse.class);
        scripts.put("services.community.CommunityBoard",l2trunk.scripts.services.community.CommunityBoard.class);
        scripts.put("services.community.CommunityBosses",l2trunk.scripts.services.community.CommunityBosses.class);
        scripts.put("services.community.CommunityClan",l2trunk.scripts.services.community.CommunityClan.class);
        scripts.put("services.community.CommunityDropCalculator",l2trunk.scripts.services.community.CommunityDropCalculator.class);
        scripts.put("services.community.CommunityNpcs",l2trunk.scripts.services.community.CommunityNpcs.class);
        scripts.put("services.community.CommunityPartyMatching",l2trunk.scripts.services.community.CommunityPartyMatching.class);
        scripts.put("services.community.CommunityWarehouse",l2trunk.scripts.services.community.CommunityWarehouse.class);
        scripts.put("services.community.Forge",l2trunk.scripts.services.community.Forge.class);
        scripts.put("services.community.RankingCommunity",l2trunk.scripts.services.community.RankingCommunity.class);
        scripts.put("services.community.ServicesCommunity",l2trunk.scripts.services.community.ServicesCommunity.class);
        scripts.put("services.community.ShowInfo",l2trunk.scripts.services.community.ShowInfo.class);
        scripts.put("services.community.StatManager",l2trunk.scripts.services.community.StatManager.class);
        scripts.put("services.Delevel",l2trunk.scripts.services.Delevel.class);
        scripts.put("services.FantasyIsle",l2trunk.scripts.services.FantasyIsle.class);
        scripts.put("services.LindviorMovie",l2trunk.scripts.services.LindviorMovie.class);
        scripts.put("services.PurpleManedHorse",l2trunk.scripts.services.PurpleManedHorse.class);
        scripts.put("services.SellPcService",l2trunk.scripts.services.SellPcService.class);
        scripts.put("services.TeleToGH",l2trunk.scripts.services.TeleToGH.class);
        scripts.put("services.TeleToParnassus",l2trunk.scripts.services.TeleToParnassus.class);
        scripts.put("zones.DragonValley",l2trunk.scripts.zones.DragonValley.class);
        scripts.put("zones.EpicZone",l2trunk.scripts.zones.EpicZone.class);
        scripts.put("zones.KashaNegate",l2trunk.scripts.zones.KashaNegate.class);
        scripts.put("zones.MonsterTrap",l2trunk.scripts.zones.MonsterTrap.class);
        scripts.put("zones.SeedOfAnnihilation",l2trunk.scripts.zones.SeedOfAnnihilation.class);
        scripts.put("zones.TullyWorkshopZone",l2trunk.scripts.zones.TullyWorkshopZone.class);

    }
    public class ScriptClassAndMethod {
        public final String className;
        public final String methodName;

        ScriptClassAndMethod(String className, String methodName) {
            this.className = className;
            this.methodName = methodName;
        }
    }
}