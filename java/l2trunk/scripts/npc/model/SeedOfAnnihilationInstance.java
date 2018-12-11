package l2trunk.scripts.npc.model;


import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.templates.npc.MinionData;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.util.Arrays;
import java.util.List;

public final class SeedOfAnnihilationInstance extends MonsterInstance {
    private static final List<Integer> BISTAKON_MOBS = Arrays.asList(22750, 22751, 22752, 22753);
    private static final List<Integer> COKRAKON_MOBS = Arrays.asList(22763, 22764, 22765);
    private static final int[][] BISTAKON_MINIONS = new int[][]{
            {22746, 22746, 22746},
            {22747, 22747, 22747},
            {22748, 22748, 22748},
            {22749, 22749, 22749}};
    private static final int[][] COKRAKON_MINIONS = new int[][]{
            {22760, 22760, 22761},
            {22760, 22760, 22762},
            {22761, 22761, 22760},
            {22761, 22761, 22762},
            {22762, 22762, 22760},
            {22762, 22762, 22761}};

    public SeedOfAnnihilationInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        if (BISTAKON_MOBS.contains(template.getNpcId()))
            addMinions(Rnd.get(BISTAKON_MINIONS), template);
        else if (COKRAKON_MOBS.contains(template.getNpcId()))
            addMinions(Rnd.get(COKRAKON_MINIONS), template);
    }

    private static void addMinions(int[] minions, NpcTemplate template) {
        if (minions != null && minions.length > 0)
            for (int id : minions)
                template.addMinion(new MinionData(id, 1));
    }

    @Override
    protected void onDeath(Creature killer) {
        getMinionList().unspawnMinions();
        super.onDeath(killer);
    }

    @Override
    public boolean canChampion() {
        return false;
    }
}