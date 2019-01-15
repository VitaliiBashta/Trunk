package l2trunk.scripts.ai.selmahum;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;

public final class SelSquadLeader extends Fighter {
    private static final NpcString[] phrase = {NpcString.SCHOOL4, NpcString.SCHOOL5, NpcString.SCHOOL6};
    private static final int NPC_ID_FIRE = 18927;
    private static final int NPC_ID_FIRE_FEED = 18933;
    private boolean isBusy;
    private boolean isImmobilized;
    private long busyTimeout = 0;
    private long idleTimeout = 0;
    private long _lookTimeout = 0;

    public SelSquadLeader(NpcInstance actor) {
        super(actor);
        MAX_PURSUE_RANGE = 6000;
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.isDead())
            return true;

        final long currentTime = System.currentTimeMillis();
        if (!isBusy) {
            if (currentTime > idleTimeout && currentTime > _lookTimeout) {
                actor.getAroundNpc(600, 300)
                        .findFirst().ifPresent(npc -> {
                    if (npc.getNpcId() == NPC_ID_FIRE_FEED && GeoEngine.canSeeTarget(actor, npc, false)) {// Cauldron
                        isBusy = true;
                        actor.setRunning();
                        actor.setNpcState(1); // Yummi State
                        busyTimeout = currentTime + (60 + Rnd.get(15)) * 1000L;
                        addTaskMove(Location.findPointToStay(npc, 50, 150), true);
                        if (Rnd.chance(40))
                            Functions.npcSay(actor, phrase[Rnd.get(2)]);
                    } else if (npc.getNpcId() == NPC_ID_FIRE && npc.getNpcState() == 1 && GeoEngine.canSeeTarget(actor, npc, false)) {
                        isBusy = true;
                        actor.setNpcState(2); // Sleepy State
                        busyTimeout = currentTime + (60 + Rnd.get(60)) * 1000L;
                        addTaskMove(Location.findPointToStay(npc, 50, 150), true);
                    }
                });
            } else
                _lookTimeout = currentTime + 2 * 1000;
        } else {
            if (currentTime > busyTimeout) {
                wakeUp();
                actor.setWalking();
                addTaskMove(actor.getSpawnedLoc(), true);
                return true;
            }
        }

        if (isImmobilized)
            return true;

        return super.thinkActive();
    }

    private void wakeUp() {
        NpcInstance actor = getActor();

        if (isBusy) {
            isBusy = false;

            busyTimeout = 0;
            idleTimeout = System.currentTimeMillis() + Rnd.get(3 * 60, 5 * 60) * 1000L;

            if (isImmobilized) {
                isImmobilized = false;
                actor.stopImmobilized();
                actor.setNpcState(3);
                actor.setRHandId(0);
                actor.broadcastCharInfo();
            }
        }
    }

    @Override
    public void onEvtArrived() {
        NpcInstance actor = getActor();

        super.onEvtArrived();

        if (isBusy) {
            isImmobilized = true;
            actor.startImmobilized();
            actor.setRHandId(15280);
            actor.broadcastCharInfo();
        }
    }

    @Override
    public void onIntentionActive() {
        //таймаут после атаки
        idleTimeout = System.currentTimeMillis() + Rnd.get(60, 5 * 60) * 1000L;

        super.onIntentionActive();
    }

    @Override
    public void onIntentionAttack(Creature target) {
        wakeUp();

        super.onIntentionAttack(target);
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    public void returnHome(boolean clearAggro, boolean teleport) {
    }
}