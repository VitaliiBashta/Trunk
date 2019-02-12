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

public final class FreyaThrone extends Fighter {
    private static final int Skill_IceBall = 6278; // Мощный клинок льда по Топ дамагеру
    private static final int Skill_SummonElemental = 6277; // Вселяет мощь льда в окружающих гвардов
    private static final int Skill_SelfNova = 6279; // Детонация льда, наносит АоЕ урон всем целям в радиусе 350
    private static final int Skill_DeathSentence = 6280; // Суровый вердикт Фреи, по истечении 10 секунд наносит мощный урон случайной цели
    private static final int Skill_Anger = 6285; // Селф-бафф Фреи, призывает силы зимы
    private static final int Skill_EternalBlizzard = 6274; // Мощнейшая атака ледяного урагана с силой 38к по площади в 3000 радиуса
    private final int _eternalblizzardReuseDelay = 60; // Откат умения в секундах ()
    private long _eternalblizzardReuseTimer = 0; // Таймер отката умения
    private long _iceballReuseTimer = 0;
    private long summonReuseTimer = 0;
    private long _selfnovaReuseTimer = 0;
    private long _deathsentenceReuseTimer = 0;
    private long _angerReuseTimer = 0;

    private long _idleDelay = 0;
    private long _lastFactionNotifyTime = 0;

    public FreyaThrone(NpcInstance actor) {
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
            _eternalblizzardReuseTimer = System.currentTimeMillis() + _eternalblizzardReuseDelay * 1000L;
        }

        // Ice Ball cast
        // Шанс активации
        int _iceballChance = 60;
        if (!actor.isCastingNow() && !actor.isMoving && _iceballReuseTimer < System.currentTimeMillis() && Rnd.chance(_iceballChance)) {
            if (topDamager != null && !topDamager.isDead() && topDamager.isInRangeZ(actor, 1000)) {
                actor.doCast(Skill_IceBall, topDamager, true);
                int _iceballReuseDelay = 20;
                _iceballReuseTimer = System.currentTimeMillis() + _iceballReuseDelay * 1000L;
            }
        }

        // Summon Buff cast
        int _summonChance = 70;
        if (!actor.isCastingNow() && summonReuseTimer < System.currentTimeMillis() && Rnd.chance(_summonChance)) {
            actor.doCast(Skill_SummonElemental, actor, true);
            getActor().getAroundNpc(800, 100)
                    .forEach(guard -> guard.altOnMagicUseTimer(guard, Skill_SummonElemental));
            summonReuseTimer = System.currentTimeMillis() + 60 * 1000L;
        }

        // Self Nova
        if (!actor.isCastingNow() && _selfnovaReuseTimer < System.currentTimeMillis()) {
            actor.doCast(Skill_SelfNova, actor, true);
            int _selfnovaReuseDelay = 70;
            _selfnovaReuseTimer = System.currentTimeMillis() + _selfnovaReuseDelay * 1000L;
        }

        // Death Sentence
        int _deathsentenceChance = 60;
        if (!actor.isCastingNow() && !actor.isMoving && _deathsentenceReuseTimer < System.currentTimeMillis() && Rnd.chance(_deathsentenceChance)) {
            if (randomHated != null && !randomHated.isDead() && randomHated.isInRangeZ(actor, 1000)) {
                actor.doCast(Skill_DeathSentence, randomHated, true);
                int _deathsentenceReuseDelay = 50;
                _deathsentenceReuseTimer = System.currentTimeMillis() + _deathsentenceReuseDelay * 1000L;
            }
        }

        // Freya Anger
        int _angerChance = 60;
        if (!actor.isCastingNow() && !actor.isMoving && _angerReuseTimer < System.currentTimeMillis() && Rnd.chance(_angerChance)) {
            actor.doCast(Skill_Anger, actor, true);
            int _angerReuseDelay = 50;
            _angerReuseTimer = System.currentTimeMillis() + _angerReuseDelay * 1000L;

            //Random agro
            if (mostHated != null && randomHated != null && actor.getAggroList().getCharMap().size() > 1) {
                actor.getAggroList().remove(mostHated, true);
                actor.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, randomHated, 300000);
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
    public boolean canTeleWhenCannotSeeTarget() {
        teleportHome();
        return false;
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();

        // Назначаем начальный откат всем умениям
        long generalReuse = System.currentTimeMillis() + 40000L;
        _eternalblizzardReuseTimer += generalReuse + Rnd.get(1, 20) * 1000L;
        _iceballReuseTimer += generalReuse + Rnd.get(1, 20) * 1000L;
        summonReuseTimer += generalReuse + Rnd.get(1, 20) * 1000L;
        _selfnovaReuseTimer += generalReuse + Rnd.get(1, 20) * 1000L;
        _deathsentenceReuseTimer += generalReuse + Rnd.get(1, 20) * 1000L;
        _angerReuseTimer += generalReuse + Rnd.get(1, 20) * 1000L;

        Reflection r = getActor().getReflection();
        r.getPlayers().forEach(p ->
            this.notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 2));
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