package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.utils.Location;
import l2trunk.scripts.bosses.AntharasManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Antharas extends DefaultAI {
    private static final int FWA_INTERVAL_MINION_NORMAL = 5 * 60000;
    //Вестника Невитта и длительность его спавна
    private static final int INVOKER_NEVIT_HERALD = 4326;
    private static final int DESPAWN_TIME = 180 * 60 * 1000; // 3 часа = 180 минут
    private static long _minionsSpawnDelay = 0;
    // debuffs
    private static final int s_fear = 4108;
    private static final int s_fear2 = 5092;
    private static final int s_curse = 4109;
    private static final int s_paralyze = 4111;
    // damage skills
    private static final int s_shock = 4106;
    private static final int s_shock2 = 4107;
    private static final int s_antharas_ordinary_attack = 4112;
    private static final int s_antharas_ordinary_attack2 = 4113;
    private static final int s_meteor = 5093;
    private static final int s_breath = 4110;
    // regen skills
    private static final int s_regen1 = 4239;
    private static final int s_regen2 = 4240;
    private static final int s_regen3 = 4241;
    private final List<NpcInstance> minions = new ArrayList<>();
    // Vars
    private int _hpStage = 0;
    private int DAMAGE_COUNTER = 0;


    public Antharas(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (DAMAGE_COUNTER == 0)
            actor.getAI().startAITask();

        AntharasManager.setLastAttackTime();
        AntharasManager.getZone().getInsidePlayables().forEach(p -> notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 1));
        DAMAGE_COUNTER++;
        super.onEvtAttacked(attacker, damage);
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        setNextMinionSpawnDelay();
    }

    @Override
    public boolean createNewTask() {
        clearTasks();
        Creature target;
        if ((target = prepareTarget()) == null)
            return false;

        NpcInstance actor = getActor();
        if (actor.isDead())
            return false;

        double distance = actor.getDistance(target);

        // Buffs and stats
        double chp = actor.getCurrentHpPercents();
        if (_hpStage == 0) {
            actor.altOnMagicUseTimer(actor, s_regen1);
            _hpStage = 1;
        } else if (chp < 75 && _hpStage == 1) {
            actor.altOnMagicUseTimer(actor, s_regen2);
            _hpStage = 2;
        } else if (chp < 50 && _hpStage == 2) {
            actor.altOnMagicUseTimer(actor, s_regen3);
            _hpStage = 3;
        } else if (chp < 30 && _hpStage == 3) {
            actor.altOnMagicUseTimer(actor, s_regen3);
            _hpStage = 4;
        }

        // Minions spawn
        if (_minionsSpawnDelay < System.currentTimeMillis() && getAliveMinionsCount() < 30 && Rnd.chance(5)) {
            NpcInstance minion = Functions.spawn(Location.findPointToStay(actor.getLoc(), 400, 700, actor.getGeoIndex()), Rnd.chance(50) ? 29190 : 29069);  // Antharas Minions
            minions.add(minion);
            AntharasManager.addSpawnedMinion(minion);
            setNextMinionSpawnDelay();
        }

        // Basic Attack
        if (Rnd.chance(50))
            return chooseTaskAndTargets(Rnd.chance(50) ? s_antharas_ordinary_attack : s_antharas_ordinary_attack2, target, distance);

        // Stage based skill attacks
        Map<Skill, Integer> d_skill = new HashMap<>();
        switch (_hpStage) {
            case 1:
                addDesiredSkill(d_skill, target, distance, s_curse);
                addDesiredSkill(d_skill, target, distance, s_paralyze);
                addDesiredSkill(d_skill, target, distance, s_meteor);
                break;
            case 2:
                addDesiredSkill(d_skill, target, distance, s_curse);
                addDesiredSkill(d_skill, target, distance, s_paralyze);
                addDesiredSkill(d_skill, target, distance, s_meteor);
                addDesiredSkill(d_skill, target, distance, s_fear2);
                break;
            case 3:
                addDesiredSkill(d_skill, target, distance, s_curse);
                addDesiredSkill(d_skill, target, distance, s_paralyze);
                addDesiredSkill(d_skill, target, distance, s_meteor);
                addDesiredSkill(d_skill, target, distance, s_fear2);
                addDesiredSkill(d_skill, target, distance, s_shock2);
                addDesiredSkill(d_skill, target, distance, s_breath);
                break;
            case 4:
                addDesiredSkill(d_skill, target, distance, s_curse);
                addDesiredSkill(d_skill, target, distance, s_paralyze);
                addDesiredSkill(d_skill, target, distance, s_meteor);
                addDesiredSkill(d_skill, target, distance, s_fear2);
                addDesiredSkill(d_skill, target, distance, s_shock2);
                addDesiredSkill(d_skill, target, distance, s_fear);
                addDesiredSkill(d_skill, target, distance, s_shock);
                addDesiredSkill(d_skill, target, distance, s_breath);
                break;
            default:
                break;
        }

        int r_skill = selectTopSkill(d_skill);
        if (r_skill != 0 && !SkillTable.INSTANCE.getInfo(r_skill).isOffensive())
            target = actor;

        return chooseTaskAndTargets(r_skill, target, distance);
    }

    /**
     * Устанавливает задержку на спаун миньена в зависимости от типа Антараса
     */
    private void setNextMinionSpawnDelay() {
        _minionsSpawnDelay = System.currentTimeMillis() + FWA_INTERVAL_MINION_NORMAL;
    }

    private int getAliveMinionsCount() {
        return (int) minions.stream()
                .filter(n -> !n.isDead())
                .count();
    }

//    private Skill int id) {
//        return SkillTable.INSTANCE.getInfo(id);
//    }

    @Override
    public void onEvtDead(Creature killer) {
        if (minions != null && !minions.isEmpty())
            minions.forEach(GameObject::deleteMe);
        //Спавним Вестника Невитта
        List<NpcInstance> heralds = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            heralds.add(NpcHolder.getTemplate(INVOKER_NEVIT_HERALD).getNewInstance());
        }
        heralds.get(0).spawnMe(new Location(82152, 148488, -3492, 60699));//Giran
        heralds.get(1).spawnMe(new Location(147048, 25608, -2038, 16383)); //Aden
        heralds.get(2).spawnMe(new Location(147384, -55400, -2759, 57343)); //Goddard
        heralds.get(3).spawnMe(new Location(42904, -47912, -822, 49151)); //Rune
        heralds.get(4).spawnMe(new Location(15736, 142744, -2731, 16383)); //Dion
        heralds.get(5).spawnMe(new Location(82120, 53224, -1521, 16383)); //Oren
        heralds.get(6).spawnMe(new Location(-14168, 121192, -3014, 16383));//Gludio
        heralds.get(7).spawnMe(new Location(80920, 149464, -3069, 16383));//Gludin
        heralds.get(8).spawnMe(new Location(87608, -141320, -1364, 49151)); //Schuttgart
        heralds.get(9).spawnMe(new Location(110552, 219848, -3696, 57343));//Hein
        heralds.get(10).spawnMe(new Location(116824, 77400, -2722, 40959));//Hunter


        ThreadPoolManager.INSTANCE.schedule(() -> heralds.forEach(GameObject::deleteMe), DESPAWN_TIME);
        super.onEvtDead(killer);
    }

}