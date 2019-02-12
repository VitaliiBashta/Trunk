package l2trunk.scripts.ai.isle_of_prayer;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Mystic;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Party;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;

import java.util.List;

public final class IsleOfPrayerMystic extends Mystic {
    private static final List<Integer> PENALTY_MOBS =List.of(18364, 18365, 18366);
    private static final int YELLOW_CRYSTAL = 9593;
    private static final int GREEN_CRYSTAL = 9594;
    private static final int RED_CRYSTAL = 9596;
    private boolean _penaltyMobsNotSpawned = true;

    public IsleOfPrayerMystic(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (_penaltyMobsNotSpawned && attacker instanceof Playable && ((Playable)attacker).getPlayer() != null) {
            Party party = ((Playable)attacker).getPlayer().getParty();
            if (party != null && party.size() > 2) {
                _penaltyMobsNotSpawned = false;
                for (int i = 0; i < 2; i++) {
                    MonsterInstance npc = new MonsterInstance(IdFactory.getInstance().getNextId(), NpcHolder.getTemplate(Rnd.get(PENALTY_MOBS)));
                    npc.setSpawnedLoc(((MonsterInstance) actor).getMinionPosition());
                    npc.setReflection(actor.getReflection());
                    npc.setFullHpMp();
                    npc.spawnMe(npc.getSpawnedLoc());
                    npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, Rnd.get(1, 100));
                }
            }
        }

        super.onEvtAttacked(attacker, damage);
    }

    @Override
    public void onEvtDead(Creature killer) {
        _penaltyMobsNotSpawned = true;
        if (killer != null) {
            final Player player = killer.getPlayer();
            if (player != null) {
                final NpcInstance actor = getActor();
                switch (actor.getNpcId()) {
                    case 22261: // Seychelles
                        if (Rnd.chance(12))
                            actor.dropItem(player, GREEN_CRYSTAL, 1);
                        break;
                    case 22265: // Chrysocolla
                        if (Rnd.chance(6))
                            actor.dropItem(player, RED_CRYSTAL, 1);
                        break;
                    case 22260: // Kleopora
                        if (Rnd.chance(23))
                            actor.dropItem(player, YELLOW_CRYSTAL, 1);
                        break;
                    case 22262: // Naiad
                        if (Rnd.chance(12))
                            actor.dropItem(player, GREEN_CRYSTAL, 1);
                        break;
                    case 22264: // Castalia
                        if (Rnd.chance(12))
                            actor.dropItem(player, GREEN_CRYSTAL, 1);
                        break;
                    case 22266: // Pythia
                        if (Rnd.chance(5))
                            actor.dropItem(player, RED_CRYSTAL, 1);
                        break;
                    case 22257: // Island Guardian
                        if (Rnd.chance(21))
                            actor.dropItem(player, YELLOW_CRYSTAL, 1);
                        break;
                    case 22258: // White Sand Mirage
                        if (Rnd.chance(22))
                            actor.dropItem(player, YELLOW_CRYSTAL, 1);
                        break;
                }
            }
        }
        super.onEvtDead(killer);
    }
}