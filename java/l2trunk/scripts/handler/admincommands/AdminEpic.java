package l2trunk.scripts.handler.admincommands;

import l2trunk.scripts.bosses.EpicBossState.State;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.handler.admincommands.AdminCommandHandler;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.scripts.bosses.BaiumManager;
import l2trunk.scripts.bosses.EpicBossState;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

public final class AdminEpic implements IAdminCommandHandler, ScriptFile {
    private enum Commands {
        admin_epic,
        admin_epic_edit
    }

    @Override
    public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player player) {
        Commands command = (Commands) comm;
        StringTokenizer st = new StringTokenizer(fullString);
        switch (command) {
            case admin_epic: {
                st.nextToken();

                if (st.hasMoreTokens())
                    showEpicEditPage(player, Integer.parseInt(st.nextToken()));
                else
                    showEpicIndexPage(player);

                break;
            }
            case admin_epic_edit:
                st.nextToken();
                int boss = Integer.parseInt(st.nextToken());
                EpicBossState state = EpicBossState.getState(boss);
                if (state == null) {
                    player.sendMessage("Error: AdminEpic.edit -> Can't find state for boss id " + boss);
                    return false;
                }

                Calendar calendar = (Calendar) Calendar.getInstance().clone();
                for (int i = 2; i < wordList.length; i++) {
                    int type;
                    int val = Integer.parseInt(wordList[i]);
                    switch (i) {
                        case 2:
                            type = Calendar.HOUR_OF_DAY;
                            break;
                        case 3:
                            type = Calendar.MINUTE;
                            break;
                        case 4:
                            type = Calendar.DAY_OF_MONTH;
                            break;
                        case 5:
                            type = Calendar.MONTH;
                            val -= 1;
                            break;
                        case 6:
                            type = Calendar.YEAR;
                            break;
                        default:
                            continue;
                    }
                    calendar.set(type, val);
                }

                calendar.set(Calendar.SECOND, 0);
                if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                    state.setState(State.NOTSPAWN);
                    state.setRespawnDateFull(0);
                    if (state.getBossId() == 29020) //Baium
                    {
                        NpcInstance baiumNpc = GameObjectsStorage.getByNpcId(29025); //BaiumNpc
                        if (baiumNpc == null)
                            BaiumManager.getInstance()._statueSpawn.doSpawn(true);
                    }
                } else {
                    state.setRespawnDateFull(calendar.getTimeInMillis());
                    state.setState(State.INTERVAL);
                }

                state.update();
                useAdminCommand(Commands.admin_epic, null, "admin_epic " + boss, player);
                break;
        }
        return true;
    }

    private void showEpicIndexPage(Player player) {
        NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

        String html = HtmCache.INSTANCE.getNotNull("admin/epic/index.htm", player);

        int i = 1;

        for (EpicBossState epic : EpicBossState.getEpics()) {
            int id = epic.getBossId();
            NpcTemplate template = NpcHolder.getTemplate(id);

            html = html.replace("<?id_" + i + "?>", String.valueOf(id));
            html = html.replace("<?name_" + i + "?>", template.getName());
            html = html.replace("<?state_" + i + "?>", getStatusNote(epic.getState()));

            i++;
        }

        adminReply.setHtml(html);
        player.sendPacket(adminReply);
    }

    private void showEpicEditPage(Player player, int epic) {
        NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

        String html = HtmCache.INSTANCE.getNotNull("admin/epic/edit.htm", player);
        EpicBossState boss = EpicBossState.getState(epic);

        int id = boss.getBossId();
        NpcTemplate template = NpcHolder.getTemplate(id);

        html = html.replace("<?id?>", String.valueOf(id));

        html = html.replace("<?name?>", template.getName());
        html = html.replace("<?state?>", getStatusNote(boss.getState()));
        long time = boss.getRespawnDate();

        if (time > 0)
            html = html.replace("<?resp?>", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(time)));
        else
            html = html.replace("<?resp?>", "<font color=\"LEVEL\">...</font>");

        adminReply.setHtml(html);
        player.sendPacket(adminReply);
    }

    private String getStatusNote(State state) {
        switch (state) {
            case ALIVE:
                return "<font color=\"CC3333\">Under Attack</font>";
            case NOTSPAWN:
                return "<font color=\"99CC33\">Alive</font>";
            case DEAD:
            case INTERVAL:
                return "<font color=\"FF3333\">Death</font>";
        }
        return null;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    @Override
    public void onLoad() {
        AdminCommandHandler.INSTANCE.registerAdminCommandHandler(this);
    }

}