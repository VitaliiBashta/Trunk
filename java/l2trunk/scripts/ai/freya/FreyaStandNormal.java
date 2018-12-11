package l2trunk.scripts.ai.freya;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.tables.SkillTable;

public final class FreyaStandNormal extends Fighter {
    private final Skill Skill_EternalBlizzard = SkillTable.INSTANCE.getInfo(6274, 1); // Мощнейшая атака ледяного урагана с силой 38к по площади в 3000 радиуса
    private final Skill Skill_IceBall = SkillTable.INSTANCE.getInfo(6278); // Мощный клинок льда по Топ дамагеру
    private final Skill Skill_SummonElemental = SkillTable.INSTANCE.getInfo(6277, 1); // Вселяет мощь льда в окружающих гвардов
    private final Skill Skill_SelfNova = SkillTable.INSTANCE.getInfo(6279, 1); // Детонация льда, наносит АоЕ урон всем целям в радиусе 350
    private final Skill Skill_DeathSentence = SkillTable.INSTANCE.getInfo(6280, 1); // Суровый вердикт Фреи, по истечении 10 секунд наносит мощный урон случайной цели
    private final Skill Skill_ReflectMagic = SkillTable.INSTANCE.getInfo(6282, 1); // Щит, отражающий магию
    private final Skill Skill_IceStorm = SkillTable.INSTANCE.getInfo(6283, 1); // Ледяной шторм по площади 1200 радиуса
    private final Skill Skill_Anger = SkillTable.INSTANCE.getInfo(6285, 1); // Селф-бафф Фреи, призывает силы зимы
    private long _eternalblizzardReuseTimer = 0; // Таймер отката умения
    private long _iceballReuseTimer = 0;
    private long _summonReuseTimer = 0;
    private long _selfnovaReuseTimer = 0;
    private long _deathsentenceReuseTimer = 0;
    private long _reflectReuseTimer = 0;
    private long _icestormReuseTimer = 0;
    private long _angerReuseTimer = 0;

    private long _dispelTimer = 0;

    private long _idleDelay = 0;
    private long _lastFactionNotifyTime = 0;

    public FreyaStandNormal(NpcInstance actor) {
        super(actor);
        MAX_PURSUE_RANGE = 7000;
    }

    @Override
    public boolean canTeleWhenCannotSeeTarget() {
        teleportHome();
        return false;
    }

    @Override
    public void thinkAttack() {
        NpcInstance actor = getActor();
        Creature topDamager = actor.getAggroList().getTopDamager();
        Creature randomHated = actor.getAggroList().getRandomHated();
        Creature mostHated = actor.getAggroList().getMostHated();

        //Eternal Blizzard Cast
        if (!actor.isCastingNow() && _eternalblizzardReuseTimer < System.currentTimeMillis()) {
            actor.doCast(Skill_EternalBlizzard, actor, true);
            Reflection r = getActor().getReflection();
            for (Player p : r.getPlayers())
                p.sendPacket(new ExShowScreenMessage(NpcString.I_FEEL_STRONG_MAGIC_FLOW, 3000, ScreenMessageAlign.MIDDLE_CENTER, true));
            // Откат умения в секундах
            int _eternalblizzardReuseDelay = 60;
            _eternalblizzardReuseTimer = System.currentTimeMillis() + _eternalblizzardReuseDelay * 1000L;
        }

        // Ice Ball Cast
        if (!actor.isCastingNow() && !actor.isMoving && _iceballReuseTimer < System.currentTimeMillis()) {
            if (topDamager != null && !topDamager.isDead() && topDamager.isInRangeZ(actor, 1000)) {
                actor.doCast(Skill_IceBall, topDamager, true);
                int _iceballReuseDelay = 10;
                _iceballReuseTimer = System.currentTimeMillis() + _iceballReuseDelay * 1000L;
            }
        }

        // Summon Buff Cast
        if (!actor.isCastingNow() && _summonReuseTimer < System.currentTimeMillis()) {
            actor.doCast(Skill_SummonElemental, actor, true);
            getActor().getAroundNpc(800, 100).forEach(guard ->
                    guard.altOnMagicUseTimer(guard, Skill_SummonElemental));
            int _summonReuseDelay = 50;
            _summonReuseTimer = System.currentTimeMillis() + _summonReuseDelay * 1000L;
        }

        // Self Nova
        if (!actor.isCastingNow() && _selfnovaReuseTimer < System.currentTimeMillis()) {
            actor.doCast(Skill_SelfNova, actor, true);
            int _selfnovaReuseDelay = 60;
            _selfnovaReuseTimer = System.currentTimeMillis() + _selfnovaReuseDelay * 1000L;
        }

        // Reflect
        if (!actor.isCastingNow() && _reflectReuseTimer < System.currentTimeMillis()) {
            actor.doCast(Skill_ReflectMagic, actor, true);
            int _reflectReuseDelay = 40;
            _reflectReuseTimer = System.currentTimeMillis() + _reflectReuseDelay * 1000L;
        }

        // Ice Storm
        if (!actor.isCastingNow() && _icestormReuseTimer < System.currentTimeMillis()) {
            actor.doCast(Skill_IceStorm, actor, true);
            int _icestormReuseDelay = 50;
            _icestormReuseTimer = System.currentTimeMillis() + _icestormReuseDelay * 1000L;
        }

        // Death Sentence
        if (!actor.isCastingNow() && !actor.isMoving && _deathsentenceReuseTimer < System.currentTimeMillis()) {
            if (randomHated != null && !randomHated.isDead() && randomHated.isInRangeZ(actor, 1000)) {
                actor.doCast(Skill_DeathSentence, randomHated, true);
                int _deathsentenceReuseDelay = 40;
                _deathsentenceReuseTimer = System.currentTimeMillis() + _deathsentenceReuseDelay * 1000L;
            }
        }

        // Freya Anger
        if (!actor.isCastingNow() && !actor.isMoving && _angerReuseTimer < System.currentTimeMillis()) {
            actor.doCast(Skill_Anger, actor, true);
            int _angerReuseDelay = 30;
            _angerReuseTimer = System.currentTimeMillis() + _angerReuseDelay * 1000L;
            //Random agro
            if (mostHated != null && randomHated != null && actor.getAggroList().getCharMap().size() > 1) {
                actor.getAggroList().remove(mostHated, true);
                actor.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, randomHated, 900000);
            }
        }

        //Dispel task
        if (_dispelTimer < System.currentTimeMillis()) {
            for (Effect e : actor.getEffectList().getAllEffects())
                if (e != null && e.isOffensive())
                    e.exit();
            int _dispelReuseDelay = 7;
            _dispelTimer = System.currentTimeMillis() + _dispelReuseDelay * 1000L;
        }

        // Обновление таймера
        if (_idleDelay > 0)
            _idleDelay = 0;

        // Оповещение минионов
        if (System.currentTimeMillis() - _lastFactionNotifyTime > _minFactionNotifyInterval) {
            _lastFactionNotifyTime = System.currentTimeMillis();
            for (NpcInstance npc : actor.getReflection().getNpcs())
                if (npc.isMonster() && npc != actor)
                    npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, actor.getAggroList().getMostHated(), 5);
        }
        super.thinkAttack();
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();

        // Назначаем начальный откат всем умениям
        long generalReuse = System.currentTimeMillis() + 30000L;
        _eternalblizzardReuseTimer += generalReuse + Rnd.get(1, 20) * 1000L;
        _iceballReuseTimer += generalReuse + Rnd.get(1, 20) * 1000L;
        _summonReuseTimer += generalReuse + Rnd.get(1, 20) * 1000L;
        _selfnovaReuseTimer += generalReuse + Rnd.get(1, 20) * 1000L;
        _reflectReuseTimer += generalReuse + Rnd.get(1, 20) * 1000L;
        _icestormReuseTimer += generalReuse + Rnd.get(1, 20) * 1000L;
        _deathsentenceReuseTimer += generalReuse + Rnd.get(1, 20) * 1000L;
        _angerReuseTimer += generalReuse + Rnd.get(1, 20) * 1000L;

        Reflection r = getActor().getReflection();
        for (Player p : r.getPlayers())
            this.notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 2);
    }

    @Override
    public boolean thinkActive() {
        // Если все атакующе погибли, покинули инстанс etc, в течение 60 секунд закрываем рефлект.
        if (_idleDelay == 0 && !getActor().isCurrentHpFull())
            _idleDelay = System.currentTimeMillis();
        Reflection ref = getActor().getReflection();
        if (!getActor().isDead() && _idleDelay > 0 && _idleDelay + 60000 < System.currentTimeMillis())
            if (!ref.isDefault()) {
                for (Player p : ref.getPlayers())
                    p.sendMessage(new CustomMessage("scripts.ai.freya.FreyaFailure", p));
                ref.collapse();
            }

        super.thinkActive();
        return true;
    }
}