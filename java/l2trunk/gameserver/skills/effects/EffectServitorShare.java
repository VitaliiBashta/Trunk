package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.PetInstance;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.stats.funcs.Func;

import java.util.List;
import java.util.stream.Stream;

public final class EffectServitorShare extends Effect {
    public EffectServitorShare(Env paramEnv, EffectTemplate paramEffectTemplate) {
        super(paramEnv, paramEffectTemplate);
    }

    public void onStart() {
        super.onStart();
        onActionTime();
    }

    public void onExit() {
        super.onExit();
    }

    public Stream<Func> getStatFuncs() {
        return Stream.of((new Func(Stats.POWER_ATTACK, 64, this) {
                    public void calc(Env env) {
                        Player pc = env.character.getPlayer();
                        if (pc != null) {
                            GameObject target = env.character.getTarget();
                            env.value += pc.getPAtk((Creature) ((target instanceof PetInstance) ? target : null)) * 0.5D;
                        }
                    }
                })
                , new Func(Stats.POWER_DEFENCE, 64, this) {
                    public void calc(Env env) {
                        Player pc = env.character.getPlayer();
                        if (pc != null) {
                            GameObject target = env.character.getTarget();
                            env.value += pc.getPDef((Creature) ((target instanceof PetInstance) ? target : null)) * 0.5D;
                        }
                    }
                }
                , new Func(Stats.MAGIC_ATTACK, 64, this) {
                    public void calc(Env env) {
                        Player pc = env.character.getPlayer();
                        if (pc != null) {
                            GameObject target = env.character.getTarget();
                            env.value += pc.getMAtk((Creature) ((target instanceof PetInstance) ? target : null), env.skill) * 0.25D;
                        }
                    }
                }
                , new Func(Stats.MAGIC_DEFENCE, 64, this) {
                    public void calc(Env env) {
                        Player pc = env.character.getPlayer();
                        if (pc != null) {
                            GameObject target = env.character.getTarget();
                            env.value += pc.getMDef((Creature) ((target instanceof PetInstance) ? target : null), env.skill) * 0.25D;
                        }
                    }
                }
                , new Func(Stats.MAX_HP, 64, this) {
                    public void calc(Env env) {
                        Player pc = env.character.getPlayer();
                        if (pc != null)
                            env.value += pc.getMaxHp() * 0.1D;
                    }
                }
                , new Func(Stats.MAX_HP, 64, this) {
                    public void calc(Env env) {
                        Player pc = env.character.getPlayer();
                        if (pc != null)
                            env.value += pc.getMaxMp() * 0.1D;
                    }
                }
                , new Func(Stats.CRITICAL_BASE, 64, this) {
                    public void calc(Env env) {
                        Player pc = env.character.getPlayer();
                        if (pc != null) {
                            GameObject target = env.character.getTarget();
                            env.value += pc.getCriticalHit((Creature) ((target instanceof PetInstance) ? target : null), env.skill) * 0.2D;
                        }
                    }
                }
                , new Func(Stats.POWER_ATTACK_SPEED, 64, this) {
                    public void calc(Env env) {
                        Player pc = env.character.getPlayer();
                        if (pc != null)
                            env.value += pc.getPAtkSpd() * 0.1D;
                    }
                }
                , new Func(Stats.MAGIC_ATTACK_SPEED, 64, this) {
                    public void calc(Env env) {
                        Player pc = env.character.getPlayer();
                        if (pc != null)
                            env.value += pc.getMAtkSpd() * 0.03D;
                    }
                });
    }
    }