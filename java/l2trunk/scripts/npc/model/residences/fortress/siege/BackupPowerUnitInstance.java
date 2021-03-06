package l2trunk.scripts.npc.model.residences.fortress.siege;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public final class BackupPowerUnitInstance extends NpcInstance {
    protected static final int LIMIT = 3;

    private static final int COND_NO_ENTERED = 0;
    private static final int COND_ENTERED = 1;
    private static final int COND_ALL_OK = 2;
    private static final int COND_FAIL = 3;
    private static final int COND_TIMEOUT = 4;

    private final List<Integer> _generated = new ArrayList<>();
    private int _index;
    private int _tryCount;
    private long _invalidatePeriod;

    public BackupPowerUnitInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (_index >= LIMIT)
            return;

        StringTokenizer token = new StringTokenizer(command);
        token.nextToken();    // step
        int val = Integer.parseInt(token.nextToken());

        if (player.getClassId() == ClassId.warsmith || player.getClassId() == ClassId.maestro) {
            if (_tryCount == 0)
                _tryCount++;
            else
                _index++;
        } else {
            if (_generated.get(_index) == val)
                _index++;
            else
                _tryCount++;
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

        if (_invalidatePeriod > 0 && _invalidatePeriod < System.currentTimeMillis())
            generate();

        int cond = getCond();
        switch (cond) {
            case COND_ALL_OK:
                message.setFile("residence2/fortress/fortress_subpower002.htm");
                onDecay();
                break;
            case COND_TIMEOUT:
                message.setFile("residence2/fortress/fortress_subpower003.htm");
                break;
            case COND_FAIL:
                message.setFile("residence2/fortress/fortress_subpower003.htm");
                _invalidatePeriod = System.currentTimeMillis() + 30000L;
                break;
            case COND_ENTERED:
                message.setFile("residence2/fortress/fortress_subpower004.htm");
                message.replaceNpcString("%password%", _index == 0 ? NpcString.THE_MARKS_HAVE_NOT_BEEN_ASSEMBLED : _index == 1 ? NpcString.THE_1ST_MARK_IS_CORRECT : NpcString.THE_2ND_MARK_IS_CORRECT);
                message.replaceNpcString("%try_count%", NpcString.ATTEMPT_S1__3_IS_IN_PROGRESS, _tryCount);
                break;
            case COND_NO_ENTERED:
                message.setFile("residence2/fortress/fortress_subpower001.htm");
                break;
        }
        player.sendPacket(message);
    }

    private void generate() {
        _invalidatePeriod = 0;
        _tryCount = 0;
        _index = 0;

        for (int i = 0; i < LIMIT; i++)
            _generated.set(i, -1);

        int j = 0;
        while (j != LIMIT) {
            int val = Rnd.get(0, LIMIT);
            if (_generated.contains(val))
                continue;
            _generated.set(j++, val);
        }
    }

    private int getCond() {
        if (_invalidatePeriod > System.currentTimeMillis())
            return COND_TIMEOUT;
        else if (_tryCount >= LIMIT)  // максимум лимит
            return COND_FAIL;
        else if (_index == 0 && _tryCount == 0)  // ищо ничего никто не клацал
            return COND_NO_ENTERED;
        else if (_index == LIMIT)   // все верно
            return COND_ALL_OK;
        else // не все удал
            return COND_ENTERED;
    }

    public List<Integer> getGenerated() {
        return _generated;
    }
}
