package l2trunk.scripts.ai.freya;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Mystic;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.utils.Location;
import l2trunk.scripts.quests._288_HandleWithCare;

public final class SeerUgoros extends Mystic {
    private final static int priestsIre = 6426;
    private int _weeds = 0;

    public SeerUgoros(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        super.thinkActive();
        getActor().getReflection().getPlayers().forEach(p ->
                notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 5000));
        return true;
    }

    @Override
    public void thinkAttack() {
        NpcInstance actor = getActor();
        if (!actor.isMuted(SkillTable.INSTANCE.getInfo(priestsIre)) && actor.getCurrentHpPercents() < 80) {
            actor.getAroundNpc(2000, 300)
                    .filter(n -> n.getNpcId() == 18867)
                    .filter(n -> !n.isDead())
                    .findFirst().ifPresent(n -> {
                actor.doCast(priestsIre, n, true);
                actor.setCurrentHp(actor.getMaxHp(), false);
                actor.broadcastCharInfo();
                _weeds++;
            });
        }
        super.thinkAttack();
    }

    @Override
    public void onEvtDead(Creature killer) {
        QuestState qs = killer.getPlayer().getQuestState(_288_HandleWithCare.class);
        if (qs != null && qs.getCond() == 1) {
            if (_weeds < 5) {
                qs.giveItems(15497, 1);
                qs.setCond(3);
            } else {
                qs.giveItems(15498, 1);
                qs.setCond(2);
            }
        }
        _weeds = 0;
        if (!getActor().getReflection().isDefault())
            getActor().getReflection().addSpawnWithoutRespawn(32740, new Location(95688, 85688, -3757, 0), 0);
        super.onEvtDead(killer);
    }
}