package l2trunk.gameserver.model.instances;

import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public final class FurnaceInstance extends NpcInstance {
    private static final Logger _log = LoggerFactory.getLogger(FurnaceInstance.class);
    private static final int MobsID = 18914;
    private static final List<Location> locs = List.of(
            Location.of(113125, -73174, -598),
            Location.of(113126, -73289, -598),
            Location.of(113126, -73403, -598),
            Location.of(113126, -73517, -598),
            Location.of(113122, -71873, -600),
            Location.of(113121, -72011, -600),
            Location.of(113120, -72125, -600),
            Location.of(113120, -72243, -600),
            Location.of(112385, -80802, -1639),
            Location.of(112383, -80913, -1639),
            Location.of(112384, -81024, -1639),
            Location.of(112383, -81131, -1639),
            Location.of(112384, -79512, -1639),
            Location.of(112383, -79628, -1638),
            Location.of(112383, -79734, -1638),
            Location.of(112383, -79841, -1638),
            Location.of(108528, -76098, -1120),
            Location.of(108408, -76096, -1120),
            Location.of(108300, -76097, -1120),
            Location.of(108178, -76095, -1120),
            Location.of(109468, -76098, -1119), 
            Location.of(109574, -76094, -1119),
            Location.of(109682, -76095, -1119),
            Location.of(109803, -76093, -1119));

    public FurnaceInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onSpawn() {
        try {
            int idm = 1;
            for (Location loc : locs) {
                NpcInstance burner = new NpcInstance(IdFactory.getInstance().getNextId(), NpcHolder.getTemplate(MobsID));
                burner.setSpawnedLoc(loc);
                burner.onSpawn();
                {
                    switch (idm) {
                        case 1: {
                            burner.setTitle("Furnace of Magic Power");
                            idm++;
                            break;
                        }
                        case 2: {
                            burner.setTitle("Furnace of Fighting Spirit");
                            idm++;
                            break;
                        }
                        case 3: {
                            burner.setTitle("Furnace of Protection");
                            idm++;
                            break;
                        }
                        case 4: {
                            burner.setTitle("Furnace of Balance");
                            idm = 1;
                            break;
                        }
                    }
                }

                burner.spawnMe(burner.getSpawnedLoc());

            }
        } catch (RuntimeException e) {
            _log.error("Could not spawn Npc " + MobsID, e);
        }
        super.onSpawn();
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        return false;
    }

    @Override
    public boolean isAttackable(Creature attacker) {
        return true;
    }

    @Override
    public boolean isInvul() {
        return true;
    }

    @Override
    public boolean isFearImmune() {
        return true;
    }

    @Override
    public boolean isParalyzeImmune() {
        return true;
    }

    @Override
    public boolean isLethalImmune() {
        return true;
    }

}