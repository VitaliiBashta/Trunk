package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.SocialAction;
import l2trunk.gameserver.scripts.Functions;

import java.util.List;

public final class GeneralDilios extends DefaultAI {
    private static final int GUARD_ID = 32619;
    private static final List<String> diliosText = List.of(
            "Messenger, inform the patrons of the Keucereus Alliance Base! The Seed of Infinity is currently secured under the flag of the Keucereus Alliance!",
            "Messenger, inform the patrons of the Keucereus Alliance Base! We're gathering brave adventurers to attack Tiat's Mounted Troop that's rooted in the Seed of Destruction.",
            "Messenger, inform the brothers in Keucereus's clan outpost! Brave adventurers are currently eradicating Undead that are widespread in Seed of Immortality's Hall of Suffering and Hall of Erosion!",
            "Stabbing three times!");
    private long _wait_timeout = 0;

    public GeneralDilios(NpcInstance actor) {
        super(actor);
        AI_TASK_ATTACK_DELAY = 10000;
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();

        if (System.currentTimeMillis() > _wait_timeout) {
            _wait_timeout = System.currentTimeMillis() + 60000;
            int j = Rnd.get(1, 3);
            if (j != 3) {
                Functions.npcSay(actor, diliosText.get(j));
                return false;
            } else {
                Functions.npcSay(actor, diliosText.get(j));
                actor.getAroundNpc(1500, 100)
                        .filter(guard -> guard.getNpcId() == GUARD_ID)
                        .filter(guard -> !(guard instanceof MonsterInstance))
                        .forEach(guard -> guard.broadcastPacket(new SocialAction(guard.objectId(), 4)));
            }
        }
        return false;
    }
}