package l2trunk.scripts.services;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Summon;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.instances.SummonInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.scripts.Functions;

import java.util.ArrayList;
import java.util.List;

public final class SupportMagic extends Functions {
    private final static int[][] _mageBuff = new int[][]{
            // minlevel maxlevel skill skilllevel
            {6, 75, 4322, 1}, // windwalk
            {6, 75, 4323, 1}, // shield
            {6, 75, 5637, 1}, // Magic Barrier 1
            {6, 75, 4328, 1}, // blessthesoul
            {6, 75, 4329, 1}, // acumen
            {6, 75, 4330, 1}, // concentration
            {6, 75, 4331, 1}, // empower
            {16, 34, 4338, 1}, // life cubic
    };

    private final static int[][] _warrBuff = new int[][]{
            // minlevel maxlevel skill
            {6, 75, 4322, 1}, // windwalk
            {6, 75, 4323, 1}, // shield
            {6, 75, 5637, 1}, // Magic Barrier 1
            {6, 75, 4324, 1}, // btb
            {6, 75, 4325, 1}, // vampirerage
            {6, 75, 4326, 1}, // regeneration
            {6, 39, 4327, 1}, // haste 1
            {40, 75, 5632, 1}, // haste 2
            {16, 34, 4338, 1}, // life cubic
    };

    private final static int[][] _summonBuff = new int[][]{
            // minlevel maxlevel skill
            {6, 75, 4322, 1}, // windwalk
            {6, 75, 4323, 1}, // shield
            {6, 75, 5637, 1}, // Magic Barrier 1
            {6, 75, 4324, 1}, // btb
            {6, 75, 4325, 1}, // vampirerage
            {6, 75, 4326, 1}, // regeneration
            {6, 75, 4328, 1}, // blessthesoul
            {6, 75, 4329, 1}, // acumen
            {6, 75, 4330, 1}, // concentration
            {6, 75, 4331, 1}, // empower
            {6, 39, 4327, 1}, // haste 1
            {40, 75, 5632, 1}, // haste 2
    };


    private final static int minSupLvl = 6;
    private final static int maxSupLvl = 75;

    public void getSupportMagic() {
        doSupportMagic(npc, player, false);
    }

    public void getSupportServitorMagic() {
        doSupportMagic(npc, player, true);
    }

    public void getProtectionBlessing() {
        if (npc == null || !npc.isInRange(player, 1000L))
            return;

        // Не выдаём блессиг протекшена ПКшникам.
        if (player.getKarma() > 0)
            return;
        if (player.getLevel() > 39 || player.getClassId().occupation() > 1) {
            show("default/newbie_blessing_no.htm", player, npc);
            return;
        }
        npc.doCast(5182, player, true);
    }

    private static void doSupportMagic(NpcInstance npc, Player player, boolean servitor) {
        // Prevent a cursed weapon weilder of being buffed
        if (npc == null || !npc.isInRange(player, 1000L) || player.isCursedWeaponEquipped())
            return;
        int lvl = player.getLevel();
        Summon pet = player.getPet();
        if (servitor && (!(pet instanceof SummonInstance))) {
            show("default/newbie_nosupport_servitor.htm", player, npc);
            return;
        } else {
            if (lvl < minSupLvl) {
                show("default/newbie_nosupport_min.htm", player, npc);
                return;
            }
            if (lvl > maxSupLvl) {
                show("default/newbie_nosupport_max.htm", player, npc);
                return;
            }
        }

        List<Creature> target = new ArrayList<>();

        if (servitor) {
            target.add(pet);

            for (int[] buff : _summonBuff)
                if (lvl >= buff[0] && lvl <= buff[1]) {
                    npc.broadcastPacket(new MagicSkillUse(npc, pet, buff[2], buff[3]));
                    npc.callSkill(buff[2], buff[3], target, true);
                }
        } else {
            target.add(player);

            if (!player.isMageClass() || player.getTemplate().race == Race.orc) {
                for (int[] buff : _warrBuff)
                    if (lvl >= buff[0] && lvl <= buff[1]) {
                        npc.broadcastPacket(new MagicSkillUse(npc, player, buff[2], buff[3]));
                        npc.callSkill(buff[2], buff[3], target, true);
                    }
            } else
                for (int[] buff : _mageBuff)
                    if (lvl >= buff[0] && lvl <= buff[1]) {
                        npc.broadcastPacket(new MagicSkillUse(npc, player, buff[2], buff[3]));
                        npc.callSkill(buff[2], buff[3], target, true);
                    }
        }
    }

}
