package l2trunk.gameserver.model;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.skills.EffectType;
import l2trunk.gameserver.skills.effects.EffectTemplate;
import l2trunk.gameserver.skills.skillclasses.Transformation;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.stats.funcs.FuncTemplate;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

public final class EffectList {
    private static final int NONE_SLOT_TYPE = -1;
    private static final int BUFF_SLOT_TYPE = 0;
    private static final int MUSIC_SLOT_TYPE = 1;
    private static final int TRIGGER_SLOT_TYPE = 2;
    private static final int DEBUFF_SLOT_TYPE = 3;
    private final Creature actor;
    private final Lock lock = new ReentrantLock();
    private List<Effect> effects = new CopyOnWriteArrayList<>();

    EffectList(Creature owner) {
        actor = owner;
    }

    private static int getSlotType(Effect e) {
        if (e.getSkill().isPassive() || e.getSkill().isToggle() || (e.getSkill() instanceof Transformation) || e.getStackType().equals(EffectTemplate.HP_RECOVER_CAST) || (e.getEffectType() == EffectType.Cubic)) {
            return NONE_SLOT_TYPE;
        } else if (e.getSkill().isOffensive()) {
            return DEBUFF_SLOT_TYPE;
        } else if (e.getSkill().isMusic()) {
            return MUSIC_SLOT_TYPE;
        } else if (e.getSkill().isTrigger) {
            return TRIGGER_SLOT_TYPE;
        } else {
            return BUFF_SLOT_TYPE;
        }
    }

    public static boolean checkStackType(EffectTemplate ef1, EffectTemplate ef2) {
        if (!ef1._stackType.equals(EffectTemplate.NO_STACK) && ef1._stackType.equalsIgnoreCase(ef2._stackType)) {
            return true;
        }
        if (!ef1._stackType.equals(EffectTemplate.NO_STACK) && ef1._stackType.equalsIgnoreCase(ef2._stackType2)) {
            return true;
        }
        if (!ef1._stackType2.equals(EffectTemplate.NO_STACK) && ef1._stackType2.equalsIgnoreCase(ef2._stackType)) {
            return true;
        }
        return !ef1._stackType2.equals(EffectTemplate.NO_STACK) && ef1._stackType2.equalsIgnoreCase(ef2._stackType2);
    }

    /**
     * Возвращает число эффектов соответствующее данному скиллу
     */
    public int getEffectsCountForSkill(int skill_id) {
        return (int) effects.stream()
                .filter(e -> e.getSkill().id == skill_id)
                .count();
    }

    public Effect getEffectByType(EffectType et) {
        return effects.stream()
                .filter(e -> e.getEffectType() == et)
                .findFirst().orElse(null);
    }

    public Stream<Effect> getEffectsBySkill(Skill skill) {
        if (skill == null) return Stream.empty();
        return getEffectsBySkillId(skill.id);
    }

    public Stream<Effect> getEffectsBySkillId(Integer skillId) {
        return effects.stream()
                .filter(e -> e.getSkill().id == skillId);
    }

    Effect getEffectOfFishPot() {
        return effects.stream()
                .filter(e -> e.getStackType().equals("fishPot"))
                .findFirst().orElse(null);
    }

    public boolean containEffectFromSkills(List<Integer> skillIds) {
        return effects.stream()
                .map(e -> e.getSkill().id)
                .anyMatch(skillIds::contains);
    }

    public Stream<Effect> getAllEffects() {
        return effects.stream();
    }

    /**
     * Возвращает первые эффекты для всех скиллов. Нужно для отображения не более чем 1 иконки для каждого скилла.
     */
    public List<Effect> getAllFirstEffects() {
        if (effects.isEmpty())
            return List.of();

        Map<Integer, Effect> map = new LinkedHashMap<>();

        effects.forEach(e -> map.put(e.getSkill().id, e)); // putIfAbsent

        return new ArrayList<>(map.values());
    }

    private void checkSlotLimit(Effect newEffect) {
        int slotType = getSlotType(newEffect);
        if (slotType == NONE_SLOT_TYPE) {
            return;
        }

        int size = 0;
        List<Integer> skillIds = new ArrayList<>();
        for (Effect e : effects) {
            if (e.isInUse()) {
                if (e.getSkill().equals(newEffect.getSkill())) {
                    return;
                }

                if (!skillIds.contains(e.getSkill().id)) {
                    int subType = getSlotType(e);
                    if (subType == slotType) {
                        size++;
                        skillIds.add(e.getSkill().id);
                    }
                }
            }
        }

        int limit = 0;
        switch (slotType) {
            case BUFF_SLOT_TYPE:
                limit = actor.getBuffLimit();
                break;
            case MUSIC_SLOT_TYPE:
                limit = Config.ALT_MUSIC_LIMIT;
                break;
            case DEBUFF_SLOT_TYPE:
                limit = Config.ALT_DEBUFF_LIMIT;
                break;
            case TRIGGER_SLOT_TYPE:
                limit = Config.ALT_TRIGGER_LIMIT;
                break;
        }

        if (size < limit) {
            return;
        }

        effects.stream()
                .filter(Effect::isInUse)
                .filter(e -> getSlotType(e) == slotType)
                .map(e -> e.getSkill().id)
                .findFirst().ifPresent(this::stopEffect);
    }

    public void addEffect(Effect effect) {
        // TODO [G1ta0] gag on the stat increase HP / MP / CP
        double hp = actor.getCurrentHp();
        double mp = actor.getCurrentMp();
        double cp = actor.getCurrentCp();

        String stackType = effect.getStackType();
        boolean add;

        lock.lock();
        try {
            if (stackType.equals(EffectTemplate.NO_STACK)) {
                // Delete the same effects
                for (Effect e : effects) {
                    if (!e.isInUse()) {
                        continue;
                    }

                    if (e.getStackType().equals(EffectTemplate.NO_STACK) && (e.getSkill().id == effect.getSkill().id) && (e.getEffectType() == effect.getEffectType())) {
                        // If the remaining duration of the effect of the old more than the duration of the new, the old reserve.
                        if (effect.getTimeLeft() > e.getTimeLeft()) {
                            e.exit();
                        } else {
                            return;
                        }
                    }
                }
            } else {
                // Проверяем, нужно ли накладывать эффект, при совпадении StackType.
                // Новый эффект накладывается только в том случае, если у него больше StackOrder и больше длительность.
                // Если условия подходят - удаляем старый.
                for (Effect e : effects) {
                    if (!e.isInUse()) {
                        continue;
                    }

                    if (!checkStackType(e.getTemplate(), effect.getTemplate())) {
                        continue;
                    }

                    if ((e.getSkill().id == effect.getSkill().id) && (e.getEffectType() != effect.getEffectType())) {
                        break;
                    }

                    // Эффекты со StackOrder == -1 заменить нельзя (например, Root).
                    if (e.getStackOrder() == -1) {
                        return;
                    }

                    if (!e.maybeScheduleNext(effect)) {
                        return;
                    }
                }
            }

            // Проверяем на лимиты бафов/дебафов
            checkSlotLimit(effect);

            // Добавляем новый эффект
            if (add = effects.add(effect)) {
                effect.setInUse(true);
            }
        } finally {
            lock.unlock();
        }

        if (!add) {
            return;
        }

        // Запускаем эффект
        effect.start();

        // TODO [G1ta0] затычка на статы повышающие HP/MP/CP
        for (FuncTemplate ft : effect.getTemplate().getAttachedFuncs()) {
            if (ft.stat == Stats.MAX_HP) {
                actor.setCurrentHp(hp, false);
            } else if (ft.stat == Stats.MAX_MP) {
                actor.setCurrentMp(mp);
            } else if (ft.stat == Stats.MAX_CP) {
                actor.setCurrentCp(cp);
            }
        }

        // Обновляем иконки
        actor.updateStats();
        actor.updateEffectIcons();
    }

    void removeEffect(Effect effect) {
        lock.lock();
        try {
            if(!effects.remove(effect)) {
                return;
            }
        } finally {
            lock.unlock();
        }

        actor.updateStats();
        actor.updateEffectIcons();
    }

    public void stopAllEffects() {
        lock.lock();
        try {
            effects.forEach(Effect::exit);
        } finally {
            lock.unlock();
        }

        actor.updateStats();
        actor.updateEffectIcons();
    }

    public void stopEffect(int skillId) {
        effects.stream()
                .filter(e -> e.getSkill().id == skillId)
                .forEach(Effect::exit);
    }

    public void stopEffect(Skill skill) {
        if (skill != null) {
            stopEffect(skill.id);
        }
    }

    public void stopEffects(EffectType type) {
        effects.stream()
        .filter(e ->e.getEffectType() == type)
        .forEach(Effect::exit);
    }

    void stopAllSkillEffects(EffectType type) {
        effects.stream()
                .filter(e -> e.getEffectType() == type)
                .map(e -> e.getSkill().id)
                .forEach(this::stopEffect);

    }
}