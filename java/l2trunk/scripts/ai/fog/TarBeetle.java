package l2trunk.scripts.ai.fog;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.utils.Location;

import java.util.List;
import java.util.Optional;

public final class TarBeetle extends DefaultAI {
    private static final List<Location> POSITIONS = List.of(
           Location.of(179256, -117160, -3608),
           Location.of(179752, -115000, -3608),
           Location.of(177944, -119528, -4112),
           Location.of(177144, -120808, -4112),
           Location.of(181224, -120088, -3672),
           Location.of(181960, -117864, -3328),
           Location.of(186200, -118120, -3272),
           Location.of(188840, -118696, -3288),
           Location.of(185448, -120536, -3088),
           Location.of(183672, -119048, -3088),
           Location.of(188072, -120824, -3088),
           Location.of(189592, -120392, -3048),
           Location.of(189448, -117464, -3288),
           Location.of(188456, -115816, -3288),
           Location.of(186424, -114440, -3280),
           Location.of(185112, -113272, -3280),
           Location.of(187768, -112952, -3288),
           Location.of(189176, -111672, -3288),
           Location.of(189960, -108712, -3288),
           Location.of(187816, -110536, -3288),
           Location.of(185368, -109880, -3288),
           Location.of(181848, -109368, -3664),
           Location.of(181816, -112392, -3664),
           Location.of(180136, -112632, -3664),
           Location.of(183608, -111432, -3648),
           Location.of(178632, -108568, -3664),
           Location.of(176264, -109448, -3664),
           Location.of(176072, -112952, -3488),
           Location.of(175720, -112136, -5520),
           Location.of(178504, -112712, -5816),
           Location.of(180248, -116136, -6104),
           Location.of(182552, -114824, -6104),
           Location.of(184248, -116600, -6104),
           Location.of(181336, -110536, -5832),
           Location.of(182088, -106664, -6000),
           Location.of(178808, -107736, -5832),
           Location.of(178776, -110120, -5824));
    private static final long TAR_BEETLE = 18804;
    private static final long TELEPORT_PERIOD = 3 * 60 * 1000;
    private boolean CAN_DEBUF = false;
    private long LAST_TELEPORT = System.currentTimeMillis();


    public TarBeetle(NpcInstance actor) {
        super(actor);
    }

    public boolean randomWalk() {
        return false;
    }

    private void CancelTarget(NpcInstance actor) {
        if (TAR_BEETLE != actor.getDisplayId()) {
            World.getAroundPlayers(actor)
                    .filter(p -> p.getTarget() == actor)
                    .forEach(p -> {
                        p.setTarget(null);
                        p.abortAttack(true, false);
                        p.abortCast(true, true);
                    });
        }
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        CancelTarget(actor);
        super.onEvtAttacked(attacker, damage);
    }

    @Override
    public boolean thinkActive() {

        NpcInstance actor = getActor();
        CancelTarget(actor);

        if (Rnd.chance(1))
            CAN_DEBUF = true;

        if (CAN_DEBUF) {
            World.getAroundPlayers(actor, 500, 200)
                    .forEach(p -> addEffect(actor, p));
            CAN_DEBUF = false;
        }

        if (System.currentTimeMillis() - LAST_TELEPORT < TELEPORT_PERIOD)
            return false;

        for (Location POSITION : POSITIONS) {
            Location loc = Rnd.get(POSITIONS);
            if (actor.getLoc().equals(loc))
                continue;

            int x = loc.x + Rnd.get(1, 8);
            int y = loc.y + Rnd.get(1, 8);
            int z = GeoEngine.getHeight(x, y, loc.z, actor.getReflection().getGeoIndex());

            actor.broadcastPacketToOthers(new MagicSkillUse(actor, 4671, 500));
            ThreadPoolManager.INSTANCE.schedule(new Teleport(new Location(x, y, z)), 500);
            LAST_TELEPORT = System.currentTimeMillis();
            break;
        }
        return super.thinkActive();
    }

    private void addEffect(NpcInstance actor, Player player) {
        Optional<Effect> effect = player.getEffectList().getEffectsBySkillId(6142).findFirst();
        if (effect.isPresent()) {
            int level = effect.get().skill.level;
            if (level < 3) {
                effect.get().exit();
                SkillTable.INSTANCE.getInfo(6142, level + 1).getEffects(actor, player);
                actor.broadcastPacket(new MagicSkillUse(actor, player, 6142, level + 1));
            }
        } else {
            SkillTable.INSTANCE.getInfo(6142).getEffects(actor, player);
            actor.broadcastPacket(new MagicSkillUse(actor, player, 6142));
        }
    }
}