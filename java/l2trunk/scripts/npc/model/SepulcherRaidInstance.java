package l2trunk.scripts.npc.model;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.RaidBossInstance;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.scripts.bosses.FourSepulchersSpawn;
import l2trunk.scripts.quests._620_FourGoblets;

import java.util.Map;
import java.util.concurrent.Future;

import static l2trunk.gameserver.utils.ItemFunctions.addItem;

public final class SepulcherRaidInstance extends RaidBossInstance {
    public int mysteriousBoxId = 0;
    private Future<?> _onDeadEventTask = null;
    private final static int OLD_BROOCH = 7262;
    private final static Map<Integer, Integer> NPC_CUPS = Map.of(
            25339, 7256,
            25342, 7257,
            25346, 7258,
            25349, 7259);

    public SepulcherRaidInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    protected void onDeath(Creature killer) {
        super.onDeath(killer);

        Player player = killer instanceof Playable ? ((Playable) killer).getPlayer() : null;
        if (player != null)
            giveCup(player);
        if (_onDeadEventTask != null)
            _onDeadEventTask.cancel(false);
        _onDeadEventTask = ThreadPoolManager.INSTANCE.schedule(new OnDeadEvent(this), 8500);
    }

    @Override
    protected void onDelete() {
        if (_onDeadEventTask != null) {
            _onDeadEventTask.cancel(false);
            _onDeadEventTask = null;
        }

        super.onDelete();
    }

    private void giveCup(Player player) {
        int cupId = NPC_CUPS.get(getNpcId());

        if (player.getParty() != null)
            for (Player mem : player.getParty().getMembers()) {
                QuestState qs = mem.getQuestState(_620_FourGoblets.class);
                if (qs != null && (qs.isStarted() || qs.isCompleted()) && !mem.haveItem(OLD_BROOCH)  && player.isInRange(mem, 700))
                    addItem(mem, cupId, 1);
            }
        else {
            QuestState qs = player.getQuestState(_620_FourGoblets.class);
            if (qs != null && (qs.isStarted() || qs.isCompleted()) && !player.haveItem(OLD_BROOCH))
                addItem(player, cupId, 1);
        }
    }

    private class OnDeadEvent extends RunnableImpl {
        final SepulcherRaidInstance _activeChar;

        OnDeadEvent(SepulcherRaidInstance activeChar) {
            _activeChar = activeChar;
        }

        @Override
        public void runImpl() {
            FourSepulchersSpawn.spawnEmperorsGraveNpc(_activeChar.mysteriousBoxId);
        }
    }

    @Override
    public boolean canChampion() {
        return false;
    }
}