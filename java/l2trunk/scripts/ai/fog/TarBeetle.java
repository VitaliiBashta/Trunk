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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public final class TarBeetle extends DefaultAI {
    private static final List<Location> POSITIONS =List.of(
            new Location(179256, -117160, -3608),
            new Location(179752, -115000, -3608),
            new Location(177944, -119528, -4112),
            new Location(177144, -120808, -4112),
            new Location(181224, -120088, -3672),
            new Location(181960, -117864, -3328),
            new Location(186200, -118120, -3272),
            new Location(188840, -118696, -3288),
            new Location(185448, -120536, -3088),
            new Location(183672, -119048, -3088),
            new Location(188072, -120824, -3088),
            new Location(189592, -120392, -3048),
            new Location(189448, -117464, -3288),
            new Location(188456, -115816, -3288),
            new Location(186424, -114440, -3280),
            new Location(185112, -113272, -3280),
            new Location(187768, -112952, -3288),
            new Location(189176, -111672, -3288),
            new Location(189960, -108712, -3288),
            new Location(187816, -110536, -3288),
            new Location(185368, -109880, -3288),
            new Location(181848, -109368, -3664),
            new Location(181816, -112392, -3664),
            new Location(180136, -112632, -3664),
            new Location(183608, -111432, -3648),
            new Location(178632, -108568, -3664),
            new Location(176264, -109448, -3664),
            new Location(176072, -112952, -3488),
            new Location(175720, -112136, -5520),
            new Location(178504, -112712, -5816),
            new Location(180248, -116136, -6104),
            new Location(182552, -114824, -6104),
            new Location(184248, -116600, -6104),
            new Location(181336, -110536, -5832),
            new Location(182088, -106664, -6000),
            new Location(178808, -107736, -5832),
            new Location(178776, -110120, -5824));
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
            for (Player player : World.getAroundPlayers(actor))
                if (player.getTarget() == actor) {
                    player.setTarget(null);
                    player.abortAttack(true, false);
                    player.abortCast(true, true);
                }
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
            for (Player player : World.getAroundPlayers(actor, 500, 200))
                addEffect(actor, player);
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

            actor.broadcastPacketToOthers(new MagicSkillUse(actor,  4671,  500));
            ThreadPoolManager.INSTANCE.schedule(new Teleport(new Location(x, y, z)), 500);
            LAST_TELEPORT = System.currentTimeMillis();
            break;
        }
        return super.thinkActive();
    }

    private void addEffect(NpcInstance actor, Player player) {
        List<Effect> effect = player.getEffectList().getEffectsBySkillId(6142);
        if (effect != null) {
            int level = effect.get(0).getSkill().getLevel();
            if (level < 3) {
                effect.get(0).exit();
                Skill skill = SkillTable.INSTANCE.getInfo(6142, level + 1);
                skill.getEffects(actor, player);
                actor.broadcastPacket(new MagicSkillUse(actor, player, 6142,level + 1));
            }
        } else {
            Skill skill = SkillTable.INSTANCE.getInfo(6142);
            skill.getEffects(actor, player);
            actor.broadcastPacket(new MagicSkillUse(actor, player, 6142 ));
        }
    }
}