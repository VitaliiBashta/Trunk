package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;

import java.util.HashMap;
import java.util.Map;

public final class CrystallineGolem extends Fighter {
    private static final int CORAL_GARDEN_SECRETGATE = 24220026; // Tears Door

    private static final int Crystal_Fragment = 9693;

    private ItemInstance itemToConsume = null;
    private Location lastPoint = null;

    private static final String[] says = new String[]{"Yum, Yum !!!", "Give !!!", "I want to !!!", "moe !!!", "More !!!", "Food !!!"};

    private static final String[] says2 = new String[]{
            "Give !!!",
            "Give !!!",
            "Greedy you, I'll leave up to you ...",
            "Where did it go?",
            "Perhaps it seemed ..."};

    private static final Map<Integer,Info> instanceInfo = new HashMap<>();

    private static class Info {
        boolean stage1 = false;
        boolean stage2 = false;
    }

    public CrystallineGolem(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.isDead())
            return true;

        if (_def_think) {
            doTask();
            return true;
        }

        if (itemToConsume != null)
            if (itemToConsume.isVisible()) {
                itemToConsume.deleteMe();
                itemToConsume = null;
            } else {
                itemToConsume = null;
                Functions.npcSay(actor, says2[Rnd.get(says2.length)]);
                actor.setWalking();
                addTaskMove(lastPoint, true);
                lastPoint = null;
                return true;
            }

        Info info = instanceInfo.get(actor.getReflectionId());
        if (info == null) {
            info = new Info();
            instanceInfo.put(actor.getReflectionId(), info);
        }

        boolean opened = info.stage1 && info.stage2;

        if (!info.stage1) {
            int dx = actor.getX() - 142999;
            int dy = actor.getY() - 151671;
            if (dx * dx + dy * dy < 10000) {
                actor.broadcastPacket(new MagicSkillUse(actor, actor, 5441, 1, 1, 0));
                info.stage1 = true;
            }
        }

        if (!info.stage2) {
            int dx = actor.getX() - 139494;
            int dy = actor.getY() - 151668;
            if (dx * dx + dy * dy < 10000) {
                actor.broadcastPacket(new MagicSkillUse(actor, actor, 5441, 1, 1, 0));
                info.stage2 = true;
            }
        }

        if (!opened && info.stage1 && info.stage2)
            actor.getReflection().openDoor(CORAL_GARDEN_SECRETGATE);

        if (Rnd.chance(10))
            for (GameObject obj : World.getAroundObjects(actor, 300, 200))
                if (obj.isItem()) {
                    ItemInstance item = (ItemInstance) obj;
                    if (item.getItemId() == Crystal_Fragment) {
                        if (Rnd.chance(50))
                            Functions.npcSay(actor, says[Rnd.get(says.length)]);
                        itemToConsume = item;
                        lastPoint = actor.getLoc();
                        actor.setRunning();
                        addTaskMove(item.getLoc(), false);
                        return true;
                    }
                }

        return randomAnimation();

    }

    @Override
    protected void onEvtAttacked(Creature attacker, int damage) {
    }

    @Override
    protected void onEvtAggression(Creature target, int aggro) {
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}