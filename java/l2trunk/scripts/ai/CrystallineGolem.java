package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CrystallineGolem extends Fighter {
    private static final int CORAL_GARDEN_SECRETGATE = 24220026; // Tears Door

    private static final int Crystal_Fragment = 9693;
    private static final List<String> says = List.of(
            "Yum, Yum !!!", "Give !!!", "I want to !!!", "moe !!!", "More !!!", "Food !!!");
    private static final List<String> says2 = List.of(
            "Give !!!",
            "Give !!!",
            "Greedy you, I'll leave up to you ...",
            "Where did it go?",
            "Perhaps it seemed ...");
    private static final Map<Integer, Info> instanceInfo = new HashMap<>();
    private ItemInstance itemToConsume = null;
    private Location lastPoint = null;

    public CrystallineGolem(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.isDead())
            return true;

        if (defThink) {
            doTask();
            return true;
        }

        if (itemToConsume != null)
            if (itemToConsume.isVisible()) {
                itemToConsume.deleteMe();
                itemToConsume = null;
            } else {
                itemToConsume = null;
                Functions.npcSay(actor, Rnd.get(says2));
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
                actor.broadcastPacket(new MagicSkillUse(actor, 5441));
                info.stage1 = true;
            }
        }

        if (!info.stage2) {
            int dx = actor.getX() - 139494;
            int dy = actor.getY() - 151668;
            if (dx * dx + dy * dy < 10000) {
                actor.broadcastPacket(new MagicSkillUse(actor, 5441));
                info.stage2 = true;
            }
        }

        if (!opened && info.stage1 && info.stage2)
            actor.getReflection().openDoor(CORAL_GARDEN_SECRETGATE);

        if (Rnd.chance(10))
            World.getAroundItems(actor, 300, 200)
                    .filter(item -> item.getItemId() == Crystal_Fragment)
                    .findFirst().ifPresent(item -> {
                if (Rnd.chance(50))
                    Functions.npcSay(actor, Rnd.get(says));
                itemToConsume = item;
                lastPoint = actor.getLoc();
                actor.setRunning();
                addTaskMove(item.getLoc(), false);
            });
        return randomAnimation();
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
    }

    @Override
    public void onEvtAggression(Creature target, int aggro) {
    }

    @Override
    public boolean randomWalk() {
        return false;
    }

    private static class Info {
        boolean stage1 = false;
        boolean stage2 = false;
    }
}