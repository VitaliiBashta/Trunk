package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.templates.item.ItemTemplate;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.ItemFunctions;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class AdminClientSupport implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player player) {
        if (!player.getPlayerAccess().CanEditChar)
            return false;

        Commands c = (Commands) comm;
        GameObject target = player.getTarget();
        switch (c) {
            case admin_setskill:
                if (wordList.length != 3)
                    return false;


                if (!(target instanceof Player))
                    return false;
                Skill skill = SkillTable.INSTANCE.getInfo(toInt(wordList[1]), toInt(wordList[2]));
                if (skill == null) {
                    player.sendMessage("Too big occupation, max:" + SkillTable.INSTANCE.getMaxLevel(toInt(wordList[1])));
                    return false;
                }
                ((Player)target).addSkill(skill, true);
                ((Player)target).sendPacket(new SystemMessage2(SystemMsg.YOU_HAVE_EARNED_S1_SKILL).addSkillName(skill.id, skill.level));
                break;
            case admin_summon:
                if (wordList.length != 3)
                    return false;

                int id = toInt(wordList[1]);
                long count = Long.parseLong(wordList[2]);

                if (id >= 1000000) {
                    if (target == null)
                        target = player;

                    NpcTemplate template = NpcHolder.getTemplate(id - 1000000);

                    for (int i = 0; i < count; i++) {
                        NpcInstance npc = template.getNewInstance();
                        npc.setSpawnedLoc(target.getLoc());
                        npc.setFullHpMp();

                        npc.spawnMe(npc.getSpawnedLoc());
                    }
                } else {
                    if (target == null)
                        target = player;

                    if (!(target instanceof Player))
                        return false;

                    ItemTemplate template = ItemHolder.getTemplate(id);
                    if (template == null)
                        return false;

                    if (template.stackable()) {
                        ItemInstance item = ItemFunctions.createItem(id);
                        item.setCount(count);

                        ((Player)target).getInventory().addItem(item, "admin_summon");
                        ((Player)target).sendPacket(SystemMessage2.obtainItems(item));
                    } else {
                        for (int i = 0; i < count; i++) {
                            ItemInstance item = ItemFunctions.createItem(id);

                            ((Player)target).getInventory().addItem(item, "admin_summon");
                            ((Player)target).sendPacket(SystemMessage2.obtainItems(item));
                        }
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private enum Commands {
        admin_setskill,
        admin_summon
    }
}
