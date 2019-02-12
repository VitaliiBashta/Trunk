package l2trunk.scripts.npc.model;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.instances.MerchantInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.SocialAction;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.templates.item.ItemTemplate;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.HtmlUtils;
import l2trunk.gameserver.utils.Util;

import java.util.StringTokenizer;


public final class ClassMasterInstance extends MerchantInstance {
    public ClassMasterInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    private String makeMessage(Player player) {
        ClassId classId = player.getClassId();
        int jobLevelTemp =classId.occupation();

        int jobLevel = classId.occupation();
        int level = player.getLevel();

        StringBuilder html = new StringBuilder();
        if (Config.ALLOW_CLASS_MASTERS_LIST.isEmpty() || !Config.ALLOW_CLASS_MASTERS_LIST.contains(jobLevelTemp))
            jobLevel = 3;

        if ((level >= 20 && jobLevel == 0 || level >= 40 && jobLevel == 1 || level >= 76 && jobLevel == 2) && Config.ALLOW_CLASS_MASTERS_LIST.contains(jobLevelTemp)) {
            ItemTemplate item = ItemHolder.getTemplate(Config.CLASS_MASTERS_PRICE_ITEM);
            if (Config.CLASS_MASTERS_PRICE_LIST[jobLevel] > 0)
                html.append("Price: ").append(Util.formatAdena(Config.CLASS_MASTERS_PRICE_LIST[jobLevel])).append(" ").append(item.getName()).append("<br1>");
            for (ClassId cid : ClassId.VALUES) {
                // Inspector is heir trooper and warder, but to replace it as a profession can not be
                // As this subclass. Inherited from their parents in order to obtain skills.
                if (cid != ClassId.inspector) {
                    if (cid.childOf(classId) && cid.occupation() == classId.occupation() +1)
                        html.append("<a action=\"bypass -h npc_").append(objectId()).append("_change_class ").append(cid.id).append(" ").append(Config.CLASS_MASTERS_PRICE_LIST[jobLevel]).append("\">").append(HtmlUtils.htmlClassName(cid.id)).append("</a><br>");
                }
            }
            player.sendPacket(new NpcHtmlMessage(player, this).setHtml(html.toString()));
        } else
            switch (jobLevel) {
                case 1:
                    html.append("Come back here when you reached occupation 20 to change your class.");
                    break;
                case 2:
                    html.append("Come back here when you reached occupation 40 to change your class.");
                    break;
                case 3:
                    html.append("Come back here when you reached occupation 76 to change your class.");
                    break;
                case 0:
                    html.append("There is no class changes for you any more.");
                    break;
                default:
                    html.append("There is no class changes for you right now !");
                    break;
            }
        return html.toString();
    }

    @Override
    public void showChatWindow(Player player, int val) {
        NpcHtmlMessage msg = new NpcHtmlMessage(player, this);
        msg.setFile("custom/31860.htm");
        msg.replace("%classmaster%", makeMessage(player));
        msg.replace("%nick%", player.getName());
        player.sendPacket(msg);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        StringTokenizer st = new StringTokenizer(command);
        if ("change_class".equals(st.nextToken())) {
            int val = Integer.parseInt(st.nextToken());
            long price = Long.parseLong(st.nextToken());
            if (player.getInventory().destroyItemByItemId(Config.CLASS_MASTERS_PRICE_ITEM, price, "ClassMasterInstance"))
                changeClass(player, val);
            else if (Config.CLASS_MASTERS_PRICE_ITEM == 57)
                player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            else
                player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
        } else
            super.onBypassFeedback(player, command);
    }

    private void changeClass(Player player, int val) {
        if (player.getClassId().occupation() == 2)
            player.sendPacket(Msg.YOU_HAVE_COMPLETED_THE_QUEST_FOR_3RD_OCCUPATION_CHANGE_AND_MOVED_TO_ANOTHER_CLASS_CONGRATULATIONS); // ??? 3 ?????
        else
            player.sendPacket(Msg.CONGRATULATIONS_YOU_HAVE_TRANSFERRED_TO_A_NEW_CLASS); // ??? 1 ? 2 ?????

        player.setClassId(val, false, false);
        player.broadcastPacket(new SocialAction(player.objectId(), SocialAction.VICTORY));
        final MagicSkillUse msu = new MagicSkillUse(player,  2527,  0, 500);
        player.broadcastPacket(msu);
        player.broadcastCharInfo();
    }
}