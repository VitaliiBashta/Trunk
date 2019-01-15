package l2trunk.scripts.ai.custom;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;

public final class Scrubwoman extends DefaultAI {
    private static final int MAX_RADIUS = 900;
    private long _nextEat;

    public Scrubwoman(NpcInstance actor) {
        super(actor);
        this.AI_TASK_ACTIVE_DELAY = 5000; //30 sek
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    public void onEvtArrived() {
        super.onEvtArrived();
        NpcInstance actor = getActor();
        if (actor == null)
            return;

        if (_nextEat < System.currentTimeMillis()) {
            ItemInstance closestItem = World.getAroundObjects(actor, MAX_RADIUS, 200)
                    .filter(GameObject::isItem)
                    .map(obj -> (ItemInstance) obj)
                    .filter(i -> i.getItemId() > 1)
                    .findFirst().orElse(null);

            if (closestItem != null) {
                closestItem.deleteMe();
                Functions.npcSay(actor, "What a mess! Already throw things! Neither shame nor conscience!");

                if (getFirstSpawned(actor)) {
                    NpcInstance npc = NpcHolder.getTemplate(getCurrActor()).getNewInstance();
                    npc.setLevel(actor.getLevel());
                    npc.setSpawnedLoc(actor.getLoc());
                    npc.setReflection(actor.getReflection());
                    npc.setFullHpMp();
                    npc.spawnMe(npc.getSpawnedLoc());
                    actor.doDie(actor);
                    actor.deleteMe();
                }
                _nextEat = System.currentTimeMillis() + 1000;
            }

        }
    }

    private int getCurrActor() {
        if (Rnd.chance(90))
            return 18583;
        return 18583;

    }

    private boolean getFirstSpawned(NpcInstance actor) {
        return actor.getNpcId() != 18583;
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor == null || actor.isDead())
            return true;
        if (!actor.isMoving && _nextEat < System.currentTimeMillis()) {
            World.getAroundObjects(actor, MAX_RADIUS, 200)
                    .filter(GameObject::isItem)
                    .map(obj -> (ItemInstance) obj)
                    .filter(i -> i.getItemId() > 1)
                    .findFirst().ifPresent(closestItem -> actor.moveToLocation(closestItem.getLoc(), 0, true));
        }
        return false;
    }
}