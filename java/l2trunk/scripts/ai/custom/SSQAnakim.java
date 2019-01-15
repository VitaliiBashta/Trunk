package l2trunk.scripts.ai.custom;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Mystic;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.scripts.Functions;

import java.util.List;

public final class SSQAnakim extends Mystic {
    private static final String PLAYER_NAME = "%playerName%";

    private static final List<String> chat = List.of(
            "For the eternity of Einhasad!!!",
            "Dear Shillien's offspring! You are not capable of confronting us!",
            "I'll show you the real power of Einhasad!",
            "Dear Military Force of Light! Go destroy the offspring of Shillien!!!");

    private static final List<String> pms = List.of(
            "My power's weakening.. Hurry and turn on the sealing device!!!",
            "All 4 sealing devices must be turned on!!!",
            "Lilith's attack is getting stronger! Go ahead and turn it on!",
            PLAYER_NAME + ", hold on. We're almost done!");

    private long _lastChatTime = 0;
    private long _lastPMTime = 0;
    private long _lastSkillTime = 0;

    public SSQAnakim(NpcInstance actor) {
        super(actor);
        actor.setHasChatWindow(false);
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
    }

    @Override
    public boolean thinkActive() {
        if (_lastChatTime < System.currentTimeMillis()) {
            Functions.npcSay(getActor(), Rnd.get(chat));
            _lastChatTime = System.currentTimeMillis() + 12 * 1000;
        }
        if (_lastPMTime < System.currentTimeMillis()) {
            Player player = getPlayer();
            if (player != null) {
                String text = Rnd.get(pms);
                if (text.contains(PLAYER_NAME))
                    text = text.replace(PLAYER_NAME, player.getName());
                Functions.npcSayToPlayer(getActor(), player, text);
            }
            _lastPMTime = System.currentTimeMillis() + 20 * 1000;
        }
        if (_lastSkillTime < System.currentTimeMillis()) {
            if (getLilith() != null)
                getActor().broadcastPacket(new MagicSkillUse(getActor(), getLilith(), 6191, 1, 5000, 10));
            _lastSkillTime = System.currentTimeMillis() + 6500;
        }
        return true;
    }

    private NpcInstance getLilith() {
        return getActor().getAroundNpc(1000, 300)
                .filter(npc -> npc.getNpcId() == 32715)
                .findFirst().orElse(null);
    }

    private Player getPlayer() {
        Reflection reflection = getActor().getReflection();
        if (reflection == null)
            return null;
        return reflection.getPlayers()
                .findFirst().orElse(null);
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