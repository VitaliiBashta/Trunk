package l2trunk.scripts.ai;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.scripts.events.PiratesTreasure.PiratesTreasure;

public final class PiratesKing extends Fighter {

    public PiratesKing(NpcInstance actor) {
        super(actor);
    }

    private boolean isFind = false;
    private long _wait_timeout = 0;
    private boolean isFirst = true;

    @Override
    public void onEvtSpawn() {
        NpcInstance actor = getActor();
        actor.setTargetable(false); //Not yet found a pirate, take it to the Target can not be

        ThreadPoolManager.INSTANCE.schedule(new RunnableImpl() // The problem of it OnDespawn
        {
            @Override
            public void runImpl() {
                NpcInstance actor = getActor();
                actor.deleteMe();
            }
        }, 30 * 60000); // 30 minutes to find and kill
        super.onEvtSpawn();
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor == null || actor.isDead())
            return true;

        if (_wait_timeout < System.currentTimeMillis() && !isFind) {
            _wait_timeout = System.currentTimeMillis() + 60000;
            ThreadPoolManager.INSTANCE.schedule(new RunnableImpl() {
                @Override
                public void runImpl() {
                    PiratesTreasure.annoncePointInfo();
                }
            }, 60000);
            return true;
        }
        if (PiratesTreasure.eventStoped) // likely delirium
            actor.deleteMe();
        return super.thinkActive();
    }

    @Override
    public void onIntentionAttack(Creature target) {
        NpcInstance actor = getActor();
        actor.setTargetable(true);

        if (isFirst) {
            ItemFunctions.addItem((Player) target, 6673, 5, "PirateKing"); // Awards first finder
            isFirst = false; // protection against cheating
            Announcements.INSTANCE.announceToAll("The Pirate King of Darkness founded!");
        }
        isFind = true;
        super.onIntentionAttack(target);
    }

    @Override
    public void onEvtDead(Creature killer) {
        Announcements.INSTANCE.announceToAll("The Pirate King of Darkness is defeated!");
        super.onEvtDead(killer);
    }

    @Override
    public boolean randomWalk() {
        return false;
    }

    @Override
    public boolean randomAnimation() {
        return false;
    }

    @Override
    public boolean canSeeInSilentMove(Playable target) {
        return true;
    }

    @Override
    public boolean canSeeInHide(Playable target) {
        return true;
    }

}
