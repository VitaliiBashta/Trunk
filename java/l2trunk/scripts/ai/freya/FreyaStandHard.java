package l2trunk.scripts.ai.freya;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.NpcString;

public final class FreyaStandHard extends Fighter {
    private static final int Skill_EternalBlizzard = 6275; // Мощнейшая атака ледяного урагана с силой 45к по площади в 3000 радиуса
    private static final int Skill_IceBall = 6278; // Мощный клинок льда по Топ дамагеру
    private static final int Skill_SummonElemental = 6277; // Вселяет мощь льда в окружающих гвардов
    private static final int Skill_SelfNova = 6279; // Детонация льда, наносит АоЕ урон всем целям в радиусе 350
    private static final int Skill_DeathSentence = 6280; // Суровый вердикт Фреи, по истечении 10 секунд наносит мощный урон случайной цели
    private static final int Skill_ReflectMagic = 6282; // Щит, отражающий магию
    private static final int Skill_IceStorm = 6283; // Ледяной шторм по площади 1200 радиуса
    private static final int Skill_Anger = 6285; // Селф-бафф Фреи, призывает силы зимы
    private long _eternalblizzardReuseTimer = 0; // Таймер отката умения
    private long _iceballReuseTimer = 0;
    private long _summonReuseTimer = 0;
    private long _selfnovaReuseTimer = 0;
    private long _deathsentenceReuseTimer = 0;
    private long _reflectReuseTimer = 0;
    private long _icestormReuseTimer = 0;
    private long _angerReuseTimer = 0;

    private long _idleDelay = 0;
    private long _lastFactionNotifyTime = 0;

    public FreyaStandHard(NpcInstance actor) {
        super(actor);
        MAX_PURSUE_RANGE = 7000;
    }

    @Override
    public void thinkAttack() {
        NpcInstance actor = getActor();
        Creature topDamager = actor.getAggroList().getTopDamager();
        Creature randomHated = actor.getAggroList().getRandomHated();
        Creature mostHated = actor.getAggroList().getMostHated();
        //Eternal Blizzard cast
        if (!actor.isCastingNow() && _eternalblizzardReuseTimer < System.currentTimeMillis()) {
            actor.doCast(Skill_EternalBlizzard, actor, true);
            Reflection r = getActor().getReflection();
            r.getPlayers().forEach(p ->
                    p.sendPacket(new ExShowScreenMessage(NpcString.I_FEEL_STRONG_MAGIC_FLOW, 3000, ScreenMessageAlign.MIDDLE_CENTER, true)));
            // Откат умения в секундах
            int _eternalblizzardReuseDelay = 50;
            _eternalblizzardReuseTimer = System.currentTimeMillis() + _eternalblizzardReuseDelay * 1000L;
        }

        // Ice Ball cast
        if (!actor.isCastingNow() && !actor.isMoving && _iceballReuseTimer < System.currentTimeMillis()) {
            if (topDamager != null && !topDamager.isDead() && topDamager.isInRangeZ(actor, 1000)) {
                actor.doCast(Skill_IceBall, topDamager, true);
                int _iceballReuseDelay = 7;
                _iceballReuseTimer = System.currentTimeMillis() + _iceballReuseDelay * 1000L;
            }
        }

        // Summon Buff cast
        if (!actor.isCastingNow() && _summonReuseTimer < System.currentTimeMillis()) {
            actor.doCast(Skill_SummonElemental, actor, true);
            getActor().getAroundNpc(800, 100)
                    .forEach(n -> n.altOnMagicUseTimer(n, Skill_SummonElemental));
            int _summonReuseDelay = 40;
            _summonReuseTimer = System.currentTimeMillis() + _summonReuseDelay * 1000L;
        }

        // Self Nova
        if (!actor.isCastingNow() && _selfnovaReuseTimer < System.currentTimeMillis()) {
            actor.doCast(Skill_SelfNova, actor, true);
            int _selfnovaReuseDelay = 40;
            _selfnovaReuseTimer = System.currentTimeMillis() + _selfnovaReuseDelay * 1000L;
        }

        // Reflect
        if (!actor.isCastingNow() && _reflectReuseTimer < System.currentTimeMillis()) {
            actor.doCast(Skill_ReflectMagic, actor, true);
            int _reflectReuseDelay = 30;
            _reflectReuseTimer = System.currentTimeMillis() + _reflectReuseDelay * 1000L;
        }

        // Ice Storm
        if (!actor.isCastingNow() && _icestormReuseTimer < System.currentTimeMillis()) {
            actor.doCast(Skill_IceStorm, actor, true);
            int _icestormReuseDelay = 40;
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
                actor.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, randomHated, 1500000);
            }
        }

        // Обновление таймера
        if (_idleDelay > 0)
            _idleDelay = 0;

        // Оповещение минионов
        if (System.currentTimeMillis() - _lastFactionNotifyTime > _minFactionNotifyInterval) {
            _lastFactionNotifyTime = System.currentTimeMillis();
            actor.getReflection().getNpcs()
                    .filter(o -> o instanceof MonsterInstance)
                    .filter(npc -> npc != actor)
                    .forEach(npc ->
                            npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, actor.getAggroList().getMostHated(), 5));
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

        getActor().getReflection().getPlayers().forEach(p ->
                this.notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 2));
    }

    @Override
    public boolean canTeleWhenCannotSeeTarget() {
        teleportHome();
        return false;
    }

    @Override
    public boolean thinkActive() {
        // Если все атакующе погибли, покинули инстанс etc, в течение 60 секунд закрываем рефлект.
        if (_idleDelay == 0 && !getActor().isCurrentHpFull())
            _idleDelay = System.currentTimeMillis();
        Reflection ref = getActor().getReflection();
        if (!getActor().isDead() && _idleDelay > 0 && _idleDelay + 60000 < System.currentTimeMillis())
            if (!ref.isDefault()) {
                ref.getPlayers().forEach(p ->
                        p.sendMessage(new CustomMessage("scripts.ai.freya.FreyaFailure")));
                ref.collapse();
            }

        super.thinkActive();
        return true;
    }
}