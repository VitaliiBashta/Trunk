package l2trunk.gameserver.handler.admincommands.impl;

import Elemental.managers.GmEventManager;
import Elemental.managers.GmEventManager.EventParameter;
import Elemental.managers.GmEventManager.StateEnum;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;

import java.util.StringTokenizer;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class AdminGmEvent implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {
        final StringTokenizer st = new StringTokenizer(fullString, " ");
        st.nextToken();

        if (!st.hasMoreTokens()) {
            showMainMenu(activeChar);
            return true;
        }

        switch (st.nextToken()) {
            case "create":
                try {
                    if (GmEventManager.INSTANCE.getEventStatus() != StateEnum.INACTIVE) {
                        activeChar.sendMessage("There is already a created event");
                        showMainMenu(activeChar);
                        return true;
                    }

                    StringBuilder eventName = new StringBuilder(st.nextToken());
                    while (st.hasMoreTokens())
                        eventName.append(" ").append(st.nextToken());

                    GmEventManager.INSTANCE.createEvent(activeChar, eventName.toString());
                } catch (Exception e) {
                    activeChar.sendMessage("Uso: //gmevent create [eventName]");
                }
                break;
            case "setminlvl":
                final int minLvl = toInt(st.nextToken());
                GmEventManager.INSTANCE.changeEventParameter(EventParameter.MIN_LVL, minLvl);
                break;
            case "setmaxlvl":
                final int maxLvl = toInt(st.nextToken());
                GmEventManager.INSTANCE.changeEventParameter(EventParameter.MAX_LVL, maxLvl);
                break;
            case "setmintime":
                final int minTime = toInt(st.nextToken());
                GmEventManager.INSTANCE.changeEventParameter(EventParameter.MIN_TIME, minTime);
                break;
            case "setmaxtime":
                final int maxTime = toInt(st.nextToken());
                GmEventManager.INSTANCE.changeEventParameter(EventParameter.MAX_TIME, maxTime);
                break;
            case "setpvpevent":
                final int isPvp = toInt(st.nextToken());
                GmEventManager.INSTANCE.changeEventParameter(EventParameter.IS_PVP_EVENT, isPvp);
                break;
            case "setpeaceevent":
                final int isPeace = toInt(st.nextToken());
                GmEventManager.INSTANCE.changeEventParameter(EventParameter.IS_PEACE_EVENT, isPeace);
                break;
            case "setautores":
                final int autoRes = toInt(st.nextToken());
                GmEventManager.INSTANCE.changeEventParameter(EventParameter.IS_AUTO_RES, autoRes);
                break;
            case "register":
                GmEventManager.INSTANCE.startRegistration();
                break;
            case "start":
                GmEventManager.INSTANCE.startEvent();
                break;
            case "stop":
                GmEventManager.INSTANCE.stopEvent();
                break;
            case "menu":
                showMainMenu(activeChar);
                break;
            default:
                return false;
        }

        showMainMenu(activeChar);
        return true;
    }

    private void showMainMenu(Player activeChar) {
        // Si no hay ningun evento creado, mostramos la ventana de creacion
        if (GmEventManager.INSTANCE.getEventStatus() == StateEnum.INACTIVE) {
            final NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
            adminReply.setFile("admin/events/gmevent_create.htm");
            activeChar.sendPacket(adminReply);
            return;
        }

        // Si el evento esta siendo creado, mostramos los comandos para controlar el evento
        final NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
        adminReply.setFile("admin/events/gmevent_control.htm");

        // Segun que estado tenga el evento van a aparecer diferentes botones
        switch (GmEventManager.INSTANCE.getEventStatus()) {
            case STARTING:
                adminReply.replace("%startEvent%", "<button value=\"Start Register\" action=\"bypass -h admin_gmevent register\" width=140 height=20 back=L2UI_ct1.button_df fore=L2UI_ct1.button_df>");
                break;
            case REGISTERING:
                adminReply.replace("%startEvent%", "<button value=\"Start Event\" action=\"bypass -h admin_gmevent start\" width=140 height=20 back=L2UI_ct1.button_df fore=L2UI_ct1.button_df>");
                break;
            default:
                adminReply.replace("%startEvent%", "");
                break;
        }

        // Botones para cambiar configs de evento
        final String pvpButton;
        if (GmEventManager.INSTANCE.isPvPEvent())
            pvpButton = "value=\"Disable\" action=\"bypass -h admin_gmevent setpvpevent 0\"";
        else
            pvpButton = "value=\"Enable\" action=\"bypass -h admin_gmevent setpvpevent 1\"";

        final String peaceButton;
        if (GmEventManager.INSTANCE.isPeaceEvent())
            peaceButton = "value=\"Disable\" action=\"bypass -h admin_gmevent setpeaceevent 0\"";
        else
            peaceButton = "value=\"Enable\" action=\"bypass -h admin_gmevent setpeaceevent 1\"";

        final String resButton;
        if (GmEventManager.INSTANCE.isAutoRes())
            resButton = "value=\"Disable\" action=\"bypass -h admin_gmevent setautores 0\"";
        else
            resButton = "value=\"Enable\" action=\"bypass -h admin_gmevent setautores 1\"";

        // Reemplazamos variables
        adminReply.replace("%eventName%", GmEventManager.INSTANCE.getEventName());
        adminReply.replace("%minLvl%", GmEventManager.INSTANCE.getMinLvl());
        adminReply.replace("%maxLvl%", GmEventManager.INSTANCE.getMaxLvl());
        adminReply.replace("%minTime%", GmEventManager.INSTANCE.getMinOnlineTime());
        adminReply.replace("%maxTime%", GmEventManager.INSTANCE.getMaxOnlineTime());
        adminReply.replace("%isPvPEvent%", GmEventManager.INSTANCE.isPvPEvent() ? "Enabled" : "Disabled");
        adminReply.replace("%isPeaceEvent%", GmEventManager.INSTANCE.isPeaceEvent() ? "Enabled" : "Disabled");
        adminReply.replace("%isAutoRes%", GmEventManager.INSTANCE.isAutoRes() ? "Enabled" : "Disabled");
        adminReply.replace("%pvpButton%", pvpButton);
        adminReply.replace("%peaceButton%", peaceButton);
        adminReply.replace("%resButton%", resButton);

        activeChar.sendPacket(adminReply);
    }

    @Override
    public String getAdminCommand() {
        return "admin_gmevent";
    }

}
