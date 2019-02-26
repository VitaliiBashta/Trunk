package l2trunk.scripts.ai.dragonvalley;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.AggroList.HateInfo;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.PlayerGroup;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.utils.NpcUtils;
import l2trunk.scripts.quests._456_DontKnowDontCare;

import java.util.Map;


public final class DrakeBosses extends Fighter {
    public DrakeBosses(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance corpse = null;
        switch (getActor().getNpcId()) {
            case 25725:
                corpse = NpcUtils.spawnSingle(32884, getActor().getLoc(), 300000);
                break;
            case 25726:
                corpse = NpcUtils.spawnSingle(32885, getActor().getLoc(), 300000);
                break;
            case 25727:
                corpse = NpcUtils.spawnSingle(32886, getActor().getLoc(), 300000);
                break;
        }

        if (killer != null && corpse != null) {
            final Player player = killer.getPlayer();
            if (player != null) {
                PlayerGroup pg = player.getPlayerGroup();
                if (pg != null) {
                    QuestState qs;
                    Map<Playable, HateInfo> aggro = getActor().getAggroList().getPlayableMap();
                    for (Player pl : pg) {
                        if (pl != null && !pl.isDead() && aggro.containsKey(pl)) {
                            if (!getActor().isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE)) {
                                getActor().isInRangeZ(killer, Config.ALT_PARTY_DISTRIBUTION_RANGE);
                            }

                            qs = pl.getQuestState(_456_DontKnowDontCare.class);
                            if (qs != null && qs.getCond() == 1)
                                qs.set("RaidKilled", corpse.objectId());
                        }
                    }
                }
            }
        }

        super.onEvtDead(killer);
        getActor().endDecayTask();
    }
}