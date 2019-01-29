package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;

public final class ZakenCandleInstance extends NpcInstance {
    private static final int OHS_Weapon = 15280; // spark
    private static final int THS_Weapon = 15281; // red
    private static final int BOW_Weapon = 15302; // blue
    private static final int Anchor = 32468;
    private boolean used = false;

    public ZakenCandleInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        setRHandId(OHS_Weapon);
        _hasRandomAnimation = false;
    }

    @Override
    public void showChatWindow(Player player, int val) {
        Reflection r = getReflection();
        if (r.isDefault() || used)
            return;

        getAroundNpc(1000, 100)
                .filter(npc -> npc.getNpcId() == Anchor)
                .findFirst().ifPresent(npc -> {
            setRHandId(BOW_Weapon);
            broadcastCharInfo();
            used = true;
        });
        setRHandId(THS_Weapon);
        broadcastCharInfo();
        used = true;
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
    }
}