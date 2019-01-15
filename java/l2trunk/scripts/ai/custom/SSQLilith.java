package l2trunk.scripts.ai.custom;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Mystic;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.scripts.Functions;

import java.util.List;

public final class SSQLilith extends Mystic {
    private final List<String> chat = List.of(
            "You, such a fool! The victory over this war belongs to Shilen!!!",
            "How dare you try to contend against me in strength? Ridiculous.",
            "Anakim! In the name of Great Shilien, I will cut your throat!",
            "You cannot be the match of Lilith. I'll teach you a lesson!");

    private long _lastChatTime = 0;
    private long _lastSkillTime = 0;

    public SSQLilith(NpcInstance actor) {
        super(actor);
        actor.setHasChatWindow(false);
    }

    @Override
    public boolean thinkActive() {
        if (_lastChatTime < System.currentTimeMillis()) {
            Functions.npcSay(getActor(), Rnd.get(chat));
            _lastChatTime = System.currentTimeMillis() + 15 * 1000;
        }
        if (_lastSkillTime < System.currentTimeMillis()) {
            Reflection ref = getActor().getReflection();
            if (ref != null) {
                ref.getNpcs()
                        .filter(npc -> npc.getNpcId() == 32718)
                        .findFirst().ifPresent(anakim -> getActor().broadcastPacket(new MagicSkillUse(getActor(), anakim, 6187, 1, 5000, 10)));
            }
            _lastSkillTime = System.currentTimeMillis() + 6500;
        }
        return true;
    }

    @Override
    public boolean randomWalk() {
        return false;
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
    }

    @Override
    public void onEvtAggression(Creature attacker, int aggro) {
    }
}