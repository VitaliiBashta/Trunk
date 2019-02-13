package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.WarehouseInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ArenaManagerInstance extends WarehouseInstance {
    private final static Map<Integer, Integer> _arenaBuff = Map.of(
            // ID, warrior = 0, mage = 1, both = 2
            6803, 0, // Arena Haste
            6804, 2, // Arena Wind Walk
            6805, 1, // Arena Empower
            6806, 1, // Arena Acumen
            6807, 1, // Arena Concentration
            6808, 2, // Arena Might
            6809, 0, // Arena Guidance
            6810, 0, // Arena Focus
            6811, 0, // Arena Death Whisper
            6812, 2); // Arena Berserker Spirit


    public ArenaManagerInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (command.equalsIgnoreCase("ArenaBuff")) {
            // Prevent a cursed weapon weilder of being buffed
            if (player.isCursedWeaponEquipped() || player.isInZone(Zone.ZoneType.battle_zone))
                return;
            int neededmoney = 2000;
            long currentmoney = player.getAdena();
            if (neededmoney > currentmoney) {
                player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                return;
            }
            player.reduceAdena(neededmoney, true, "ArenaManagerInstance");
            List<Creature> target = new ArrayList<>();
            target.add(player);
            _arenaBuff.forEach((k, v) -> {
                if (player.isMageClass() && player.getTemplate().race != Race.orc) {
                    if (v == 1 || v == 2) {
                        broadcastPacket(new MagicSkillUse(this, player, k));
                        callSkill(k, target, true);
                    }
                } else {
                    if (v == 0 || v == 2) {
                        broadcastPacket(new MagicSkillUse(this, player, k));
                        callSkill(k, target, true);
                    }
                }
            });
        } else if (command.equals("CPRecovery")) {
            if (player.isCursedWeaponEquipped() || player.isInZone(Zone.ZoneType.battle_zone))
                return;

            int neededmoney = 1000;
            long currentmoney = player.getAdena();
            if (neededmoney > currentmoney) {
                player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                return;
            }
            player.reduceAdena(neededmoney, true, "ArenaManagerInstance");
            player.setFullCp();
            player.sendPacket(new SystemMessage2(SystemMsg.S1_CP_HAS_BEEN_RESTORED).addName(player));
        } else if (command.equals("HPRecovery")) {
            if (player.isCursedWeaponEquipped() || player.isInZone(Zone.ZoneType.battle_zone))
                return;

            int neededmoney = 1000;
            long currentmoney = player.getAdena();
            if (neededmoney > currentmoney) {
                player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                return;
            }
            player.reduceAdena(neededmoney, true, "ArenaManagerInstance");
            player.setCurrentHp(player.getMaxHp(), false);
            player.sendPacket(new SystemMessage2(SystemMsg.S1_HP_HAS_BEEN_RESTORED).addName(player));
        } else
            super.onBypassFeedback(player, command);
    }
}
