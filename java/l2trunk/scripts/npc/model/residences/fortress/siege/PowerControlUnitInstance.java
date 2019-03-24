package l2trunk.scripts.npc.model.residences.fortress.siege;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Spawner;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.entity.events.impl.FortressSiegeEvent;
import l2trunk.gameserver.model.entity.events.objects.SpawnExObject;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public final class PowerControlUnitInstance extends NpcInstance {
    private static final int LIMIT = 3;

    private static final int COND_NO_ENTERED = 0;
    private static final int COND_ENTERED = 1;
    private static final int COND_ALL_OK = 2;
    private static final int COND_FAIL = 3;
    private static final int COND_TIMEOUT = 4;

    private final List<Integer> generated = new ArrayList<>();
    private int index;
    private int tryCount;
    private long invalidatePeriod;

    public PowerControlUnitInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        StringTokenizer token = new StringTokenizer(command);
        token.nextToken();    // step
        int val = Integer.parseInt(token.nextToken());

        if (player.getClassId() == ClassId.warsmith || player.getClassId() == ClassId.maestro) {
            if (tryCount == 0)
                tryCount++;
            else
                index++;
        } else {
            if (generated.get(index) == val)
                index++;
            else
                tryCount++;
        }

        showChatWindow(player, 0);
    }


    @Override
    public void onSpawn() {
        super.onSpawn();

        generate();
    }

    @Override
    public void showChatWindow(Player player, int val) {
        NpcHtmlMessage message = new NpcHtmlMessage(player, this);

        if (invalidatePeriod > 0 && invalidatePeriod < System.currentTimeMillis())
            generate();

        int cond = getCond();
        switch (cond) {
            case COND_ALL_OK:
                message.setFile("residence2/fortress/fortress_inner_controller002.htm");

                FortressSiegeEvent event = getEvent(FortressSiegeEvent.class);
                if (event != null) {
                    SpawnExObject exObject = event.getFirstObject(FortressSiegeEvent.SIEGE_COMMANDERS);
                    Spawner spawn = exObject.getSpawns().get(3); // spawn of Main Machine

                    MainMachineInstance machineInstance = (MainMachineInstance) spawn.getFirstSpawned();
                    machineInstance.powerOff(this);

                    onDecay();
                }
                break;
            case COND_TIMEOUT:
                message.setFile("residence2/fortress/fortress_inner_controller003.htm");
                break;
            case COND_FAIL:
                message.setFile("residence2/fortress/fortress_inner_controller003.htm");
                invalidatePeriod = System.currentTimeMillis() + 30000L;
                break;
            case COND_ENTERED:
                message.setFile("residence2/fortress/fortress_inner_controller004.htm");
                message.replaceNpcString("%password%", index == 0 ? NpcString.PASSWORD_HAS_NOT_BEEN_ENTERED : index == 1 ? NpcString.FIRST_PASSWORD_HAS_BEEN_ENTERED : NpcString.SECOND_PASSWORD_HAS_BEEN_ENTERED);
                message.replaceNpcString("%try_count%", NpcString.ATTEMPT_S1__3_IS_IN_PROGRESS, tryCount);
                break;
            case COND_NO_ENTERED:
                message.setFile("residence2/fortress/fortress_inner_controller001.htm");
                break;
        }
        player.sendPacket(message);
    }

    private void generate() {
        invalidatePeriod = 0;
        tryCount = 0;
        index = 0;

        for (int i = 0; i < generated.size(); i++)
            generated.set(i, -1);

        int j = 0;
        while (j != LIMIT) {
            int val = Rnd.get(0, 9);
            if (generated.contains(val))
                continue;
            generated.set(j++, val);
        }
    }

    private int getCond() {
        if (invalidatePeriod > System.currentTimeMillis())
            return COND_TIMEOUT;
        else if (tryCount >= LIMIT)  // максимум лимит
            return COND_FAIL;
        else if (index == 0 && tryCount == 0)  // ищо ничего никто не клацал
            return COND_NO_ENTERED;
        else if (index == LIMIT)   // все верно
            return COND_ALL_OK;
        else // не все удал
            return COND_ENTERED;
    }

    public List<Integer> getGenerated() {
        return generated;
    }
}
