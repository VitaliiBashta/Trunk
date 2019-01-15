package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.PlaySound;
import l2trunk.gameserver.utils.Location;

import static l2trunk.scripts.ai.ZakenDaytime.scheduleTeleport;

public final class ZakenNightly extends Fighter {
    private static final int doll_blader_b = 29023;
    private static final int vale_master_b = 29024;
    private static final int pirates_zombie_captain_b = 29026;
    private static final int pirates_zombie_b = 29027;

    private static final long _teleportSelfReuse = 30000L;          // 30 secs
    private final NpcInstance actor = getActor();
    private int _stage = 0;

    public ZakenNightly(NpcInstance actor) {
        super(actor);
        MAX_PURSUE_RANGE = Integer.MAX_VALUE / 2;
    }

    @Override
    public void thinkAttack() {
        scheduleTeleport(0, _teleportSelfReuse, actor);

        double actor_hp_precent = actor.getCurrentHpPercents();
        Reflection r = actor.getReflection();
        switch (_stage) {
            case 0:
                if (actor_hp_precent < 90) {
                    r.addSpawnWithoutRespawn(pirates_zombie_captain_b, actor.getLoc(), 300);
                    _stage++;
                }
                break;
            case 1:
                if (actor_hp_precent < 80) {
                    r.addSpawnWithoutRespawn(doll_blader_b, actor.getLoc(), 300);
                    _stage++;
                }
                break;
            case 2:
                if (actor_hp_precent < 70) {
                    r.addSpawnWithoutRespawn(vale_master_b, actor.getLoc(), 300);
                    r.addSpawnWithoutRespawn(vale_master_b, actor.getLoc(), 300);
                    _stage++;
                }
                break;
            case 3:
                if (actor_hp_precent < 60) {
                    for (int i = 0; i < 5; i++)
                        r.addSpawnWithoutRespawn(pirates_zombie_b, actor.getLoc(), 300);
                    _stage++;
                }
                break;
            case 4:
                if (actor_hp_precent < 50) {
                    for (int i = 0; i < 5; i++) {
                        r.addSpawnWithoutRespawn(doll_blader_b, actor.getLoc(), 300);
                        r.addSpawnWithoutRespawn(pirates_zombie_b, actor.getLoc(), 300);
                        r.addSpawnWithoutRespawn(vale_master_b, actor.getLoc(), 300);
                        r.addSpawnWithoutRespawn(pirates_zombie_captain_b, actor.getLoc(), 300);
                    }
                    _stage++;
                }
                break;
            case 5:
                if (actor_hp_precent < 40) {
                    for (int i = 0; i < 6; i++) {
                        r.addSpawnWithoutRespawn(doll_blader_b, actor.getLoc(), 300);
                        r.addSpawnWithoutRespawn(pirates_zombie_b, actor.getLoc(), 300);
                        r.addSpawnWithoutRespawn(vale_master_b, actor.getLoc(), 300);
                        r.addSpawnWithoutRespawn(pirates_zombie_captain_b, actor.getLoc(), 300);
                    }
                    _stage++;
                }
                break;
            case 6:
                if (actor_hp_precent < 30) {
                    for (int i = 0; i < 7; i++) {
                        r.addSpawnWithoutRespawn(doll_blader_b, actor.getLoc(), 300);
                        r.addSpawnWithoutRespawn(pirates_zombie_b, actor.getLoc(), 300);
                        r.addSpawnWithoutRespawn(vale_master_b, actor.getLoc(), 300);
                        r.addSpawnWithoutRespawn(pirates_zombie_captain_b, actor.getLoc(), 300);
                    }
                    _stage++;
                }
                break;
            default:
                break;
        }
        super.thinkAttack();
    }

    @Override
    public void onEvtDead(Creature killer) {
        Reflection r = actor.getReflection();
        r.setReenterTime(System.currentTimeMillis());
        actor.broadcastPacket(new PlaySound(PlaySound.Type.MUSIC, "BS02_D", 1, actor.getObjectId(), actor.getLoc()));
        super.onEvtDead(killer);
    }

    @Override
    public void teleportHome() {
    }
}