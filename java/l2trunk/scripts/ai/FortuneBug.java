package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.NpcUtils;

import java.util.List;
import java.util.Objects;

public final class FortuneBug extends DefaultAI {
    private static final int MAX_RADIUS = 500;

    private final static int s_display_bug_of_fortune1 = 6045;
    private final static int s_display_jackpot_firework = 5778;

    private final int Wingless_Luckpy = 2502;
    private final int Wingless_Luckpy_Gold = 2503;

    private final List<Integer> Cristall = List.of(9552, 9553, 9554, 9555, 9556, 9557);
    private final List<Integer> Cristall_Dush = List.of(5577, 5578, 5579);

    private long _nextEat;
    private int i_ai0;
    private int i_ai1;

    public FortuneBug(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        addTimer(7778, 1000);
        int i_ai2;
        i_ai0 = i_ai1 = i_ai2 = 0;
    }

    @Override
    public void onEvtArrived() {
        super.onEvtArrived();
        NpcInstance actor = getActor();
        if (actor == null)
            return;

        if (actor.getNpcId() == Wingless_Luckpy || actor.getNpcId() == Wingless_Luckpy_Gold)
            return;

        if (_nextEat < System.currentTimeMillis()) {
            ItemInstance closestItem = World.getAroundItems(actor, 20, 200)
                    .filter(ItemInstance::isAdena)
                    .findFirst().orElse(null);

            if (closestItem != null) {
                closestItem.deleteMe();
                actor.altUseSkill(s_display_bug_of_fortune1, actor);
                Functions.npcSayInRange(actor, 600, NpcString.YUMYUM_YUMYUM);

                i_ai0++;
                if (i_ai0 > 3 && i_ai0 <= 4)
                    i_ai1 = 20;
                else if (i_ai0 > 4 && i_ai0 <= 6)
                    i_ai1 = 30;
                else if (i_ai0 > 6 && i_ai0 <= 8)
                    i_ai1 = 50;
                else if (i_ai0 > 8 && i_ai0 < 10)
                    i_ai1 = 80;
                else if (i_ai0 >= 10)
                    i_ai1 = 100;

                if (Rnd.chance(i_ai1)) {
                    final NpcInstance npc = NpcUtils.spawnSingle(Rnd.chance(30) ? Wingless_Luckpy : Wingless_Luckpy_Gold, actor.getLoc(), actor.getReflection());

                    switch (actor.getLevel()) {
                        case 52:
                            npc.addSkill(24009);
                            break;
                        case 70:
                            npc.addSkill(24009, 2);
                            break;
                        case 80:
                            npc.addSkill(24009, 3);
                            break;
                    }
                    npc.setLevel(actor.getLevel());
                    npc.altUseSkill(s_display_jackpot_firework, npc);
                    actor.deleteMe();
                }
                _nextEat = System.currentTimeMillis() + 10000;
            }
        } else if (_nextEat + 10 * 60 * 1000 <= System.currentTimeMillis()) {
            actor.deleteMe();
        }
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor == null || actor.isDead())
            return true;

        if (actor.getNpcId() == Wingless_Luckpy || actor.getNpcId() == Wingless_Luckpy_Gold)
            return true;

        if (!actor.isMoving && _nextEat < System.currentTimeMillis()) {
            World.getAroundItems(actor, MAX_RADIUS, 200)
                    .filter(ItemInstance::isAdena)
                    .findFirst().ifPresent(closestItem -> actor.moveToLocation(closestItem.getLoc(), 0, true));
        }

        return false;
    }

    @Override
    public void onEvtDead(Creature killer) {
        super.onEvtDead(killer);
        NpcInstance actor = getActor();

        if (actor == null)
            return;

        int lvl = actor.getLevel();

        Player player = killer.getPlayer();
        if (player != null) {
            if (actor.getNpcId() == Wingless_Luckpy)
                switch (lvl) {
                    case 52:
                        actor.dropItem(killer.getPlayer(), 8755, Rnd.get(1, 2));
                        return;
                    case 70:
                        actor.dropItem(killer.getPlayer(), Rnd.get(Cristall_Dush), Rnd.get(1, 2));
                        return;
                    case 80:
                        actor.dropItem(killer.getPlayer(), Rnd.get(Cristall), Rnd.get(1, 2));
                        return;
                }
            if (actor.getNpcId() == Wingless_Luckpy_Gold)
                switch (lvl) {
                    case 52:
                        actor.dropItem(killer.getPlayer(), 8755, Rnd.get(1, 2));
                        actor.dropItem(killer.getPlayer(), 14678, 1);
                        return;
                    case 70:
                        actor.dropItem(killer.getPlayer(), Rnd.get(Cristall_Dush), Rnd.get(1, 2));
                        actor.dropItem(killer.getPlayer(), 14679, 1);
                        return;
                    case 80:
                        actor.dropItem(killer.getPlayer(), Rnd.get(Cristall), Rnd.get(1, 2));
                        actor.dropItem(killer.getPlayer(), 14680, 1);
                }
        }
    }

    @Override
    public void onEvtTimer(int timerId) {
        NpcInstance actor = getActor();
        if (actor == null)
            return;

        if (actor.getNpcId() == Wingless_Luckpy || actor.getNpcId() == Wingless_Luckpy_Gold)
            return;

        if (timerId == 7778) {
            switch (i_ai0) {
                case 0:
                    Functions.npcSayInRange(actor, 600, Rnd.chance(50) ? NpcString.IF_YOU_HAVE_ITEMS_PLEASE_GIVE_THEM_TO_ME : NpcString.MY_STOMACH_IS_EMPTY);
                    break;
                case 1:
                    Functions.npcSayInRange(actor, 600, Rnd.chance(50) ? NpcString.IM_HUNGRY_IM_HUNGRY : NpcString.IM_STILL_NOT_FULL);
                    break;
                case 2:
                    Functions.npcSayInRange(actor, 600, Rnd.chance(50) ? NpcString.IM_STILL_HUNGRY : NpcString.I_FEEL_A_LITTLE_WOOZY);
                    break;
                case 3:
                    Functions.npcSayInRange(actor, 600, Rnd.chance(50) ? NpcString.GIVE_ME_SOMETHING_TO_EAT : NpcString.NOW_ITS_TIME_TO_EAT);
                    break;
                case 4:
                    Functions.npcSayInRange(actor, 600, Rnd.chance(50) ? NpcString.I_ALSO_NEED_A_DESSERT : NpcString.IM_STILL_HUNGRY_);
                    break;
                case 5:
                    Functions.npcSayInRange(actor, 600, NpcString.IM_FULL_NOW_I_DONT_WANT_TO_EAT_ANYMORE);
                    break;
            }
            addTimer(7778, 10000 + Rnd.get(10) * 1000);
        } else
            super.onEvtTimer(timerId);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
    }

    @Override
    public void onEvtAggression(Creature target, int aggro) {
    }
}
