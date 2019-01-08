package l2trunk.scripts.npc.model;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.Say2;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.gameserver.utils.PositionUtils;
import l2trunk.scripts.bosses.FourSepulchersManager;
import l2trunk.scripts.bosses.FourSepulchersSpawn;
import l2trunk.scripts.bosses.FourSepulchersSpawn.GateKeeper;

import java.util.concurrent.Future;

public final class SepulcherNpcInstance extends NpcInstance {
    private final static String HTML_FILE_PATH = "SepulcherNpc/";
    private final static int HALLS_KEY = 7260;
    private Future<?> _closeTask = null;
    private Future<?> _spawnMonsterTask = null;

    public SepulcherNpcInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    protected void onDelete() {
        if (_closeTask != null) {
            _closeTask.cancel(false);
            _closeTask = null;
        }
        if (_spawnMonsterTask != null) {
            _spawnMonsterTask.cancel(false);
            _spawnMonsterTask = null;
        }
        super.onDelete();
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        if (isDead()) {
            player.sendActionFailed();
            return;
        }

        switch (getNpcId()) {
            case 31468:
            case 31469:
            case 31470:
            case 31471:
            case 31472:
            case 31473:
            case 31474:
            case 31475:
            case 31476:
            case 31477:
            case 31478:
            case 31479:
            case 31480:
            case 31481:
            case 31482:
            case 31483:
            case 31484:
            case 31485:
            case 31486:
            case 31487:
                doDie(player);
                if (_spawnMonsterTask != null)
                    _spawnMonsterTask.cancel(false);
                _spawnMonsterTask = ThreadPoolManager.INSTANCE.schedule(() -> FourSepulchersSpawn.spawnMonster(getNpcId()), 3500);
                return;

            case 31455:
            case 31456:
            case 31457:
            case 31458:
            case 31459:
            case 31460:
            case 31461:
            case 31462:
            case 31463:
            case 31464:
            case 31465:
            case 31466:
            case 31467:
                if (player.isInParty() && !hasPartyAKey(player.getParty().getLeader())) {
                    Functions.addItem(player.getParty().getLeader(), HALLS_KEY, 1, "SepulcherNpcInstance");
                    doDie(player);
                }
                return;
        }
        super.showChatWindow(player, val);
    }

    @Override
    public String getHtmlPath(int npcId, int val, Player player) {
        String pom;
        if (val == 0)
            pom = String.valueOf(npcId);
        else
            pom = npcId + "-" + val;
        return HTML_FILE_PATH + pom + ".htm";
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (command.startsWith("open_gate")) {
            ItemInstance hallsKey = player.getInventory().getItemByItemId(HALLS_KEY);
            if (hallsKey == null)
                showHtmlFile(player);
            else if (FourSepulchersManager.isAttackTime()) {
                switch (getNpcId()) {
                    case 31929:
                    case 31934:
                    case 31939:
                    case 31944:
                        if (!FourSepulchersSpawn.isShadowAlive(getNpcId()))
                            FourSepulchersSpawn.spawnShadow(getNpcId());
                }

                // Moved here from switch-default
                openNextDoor(getNpcId());
                if (player.getParty() != null)
                    for (Player mem : player.getParty().getMembers()) {
                        hallsKey = mem.getInventory().getItemByItemId(HALLS_KEY);
                        if (hallsKey != null)
                            Functions.removeItem(mem, HALLS_KEY, hallsKey.getCount(), "SepulcherNpcInstance");
                    }
                else
                    Functions.removeItem(player, HALLS_KEY, hallsKey.getCount(), "SepulcherNpcInstance");
            }
        } else
            super.onBypassFeedback(player, command);
    }

    private void openNextDoor(int npcId) {
        GateKeeper gk = FourSepulchersManager.getHallGateKeeper(npcId);
        gk.door.openMe();

        if (_closeTask != null)
            _closeTask.cancel(false);
        _closeTask = ThreadPoolManager.INSTANCE.schedule(new CloseNextDoor(gk), 10000);
    }

    public void sayInShout(String msg) {
        if (msg == null || msg.isEmpty())
            return; //wrong usage

        Say2 sm = new Say2(0, ChatType.SHOUT, getName(), msg);
        GameObjectsStorage.getAllPlayersStream()
                .filter(p -> PositionUtils.checkIfInRange(15000, p, this, true))
                .forEach(p -> p.sendPacket(sm));

    }

    private void showHtmlFile(Player player) {
        NpcHtmlMessage html = new NpcHtmlMessage(player, this);
        html.setFile("SepulcherNpc/" + "Gatekeeper-no.htm");
        html.replace("%npcname%", getName());
        player.sendPacket(html);
    }

    private boolean hasPartyAKey(Player player) {
        return player.getParty().getMembers().stream()
        .anyMatch(m -> (ItemFunctions.getItemCount(m, HALLS_KEY) > 0));
    }

    private class CloseNextDoor extends RunnableImpl {
        private final GateKeeper _gk;
        private int state = 0;

        CloseNextDoor(GateKeeper gk) {
            _gk = gk;
        }

        @Override
        public void runImpl() {
            if (state == 0) {
                _gk.door.closeMe();
                state++;
                _closeTask = ThreadPoolManager.INSTANCE.schedule(this, 10000);
            } else if (state == 1) {
                FourSepulchersSpawn.spawnMysteriousBox(_gk.template.npcId);
                _closeTask = null;
            }
        }
    }

}