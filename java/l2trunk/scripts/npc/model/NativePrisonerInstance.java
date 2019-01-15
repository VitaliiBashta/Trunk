package l2trunk.scripts.npc.model;

import l2trunk.gameserver.instancemanager.HellboundManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.skills.AbnormalEffect;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.util.StringTokenizer;


public final class NativePrisonerInstance extends NpcInstance {
    public NativePrisonerInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    protected void onSpawn() {
        startAbnormalEffect(AbnormalEffect.HOLD_2);
        super.onSpawn();
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this) || isBusy())
            return;

        StringTokenizer st = new StringTokenizer(command);
        if (st.nextToken().equals("rescue")) {
            stopAbnormalEffect(AbnormalEffect.HOLD_2);
            Functions.npcSay(this, "Thank you for saving me! Guards are coming, run!");
            HellboundManager.addConfidence(15);
            deleteMe();
        } else
            super.onBypassFeedback(player, command);
    }
}