package l2trunk.gameserver.utils;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.SkillAcquireHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SkillLearn;
import l2trunk.gameserver.model.SubClass;
import l2trunk.gameserver.model.base.AcquireType;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.base.ClassType2;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.SkillList;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.Functions;

import java.util.Collection;
import java.util.Map;

import static l2trunk.gameserver.utils.ItemFunctions.addItem;

public final class CertificationFunctions {
    private static final String PATH = "villagemaster/certification/";

    public static void showCertificationList(NpcInstance npc, Player player) {
        if (checkConditions(65, npc, player, true)) {
            Functions.show(PATH + "certificatelist.htm", player, npc);
        }
    }

    public static void getCertification65(NpcInstance npc, Player player) {
        if (checkConditions(65, npc, player, Config.ALT_GAME_SUB_BOOK)) {
            SubClass clzz = player.getActiveClass();
            if (clzz.isCertificationGet(SubClass.CERTIFICATION_65)) {
                Functions.show(PATH + "certificate-already.htm", player, npc);
                return;
            }

            addItem(player, 10280, 1);
            clzz.addCertification(SubClass.CERTIFICATION_65);
            player.store(true);
        }

    }

    public static void getCertification70(NpcInstance npc, Player player) {
        if (checkConditions(70, npc, player, Config.ALT_GAME_SUB_BOOK)) {
            SubClass clzz = player.getActiveClass();

            if (clzz.isCertificationGet(SubClass.CERTIFICATION_70)) {
                Functions.show(PATH + "certificate-already.htm", player, npc);
                return;
            }

            addItem(player, 10280, 1);
            clzz.addCertification(SubClass.CERTIFICATION_70);
            player.store(true);
        }

    }

    public static void getCertification75List(NpcInstance npc, Player player) {
        if (checkConditions(75, npc, player, Config.ALT_GAME_SUB_BOOK)) {
            if (player.getActiveClass().isCertificationGet(SubClass.CERTIFICATION_75))
                Functions.show(PATH + "certificate-already.htm", player, npc);
            else
                Functions.show(PATH + "certificate-choose.htm", player, npc);
        }

    }

    public static void getCertification75(NpcInstance npc, Player player, boolean classCertifi) {
        if (checkConditions(75, npc, player, Config.ALT_GAME_SUB_BOOK)) {
            SubClass clzz = player.getActiveClass();

            if (player.getActiveClass().isCertificationGet(SubClass.CERTIFICATION_75)) {
                Functions.show(PATH + "certificate-already.htm", player, npc);
                return;
            }

            if (classCertifi) {
                ClassId cl = clzz.getClassId();
                if (cl.getType2() == null)
                    return;


                addItem(player, cl.getType2().certificate(), 1);
            } else {
                addItem(player, 10612, 1); // master ability
            }

            clzz.addCertification(SubClass.CERTIFICATION_75);
            player.store(true);
        }

    }

    public static void getCertification80(NpcInstance npc, Player player) {
        if (!checkConditions(80, npc, player, Config.ALT_GAME_SUB_BOOK)) {
            return;
        }

        SubClass clzz = player.getActiveClass();

        if (clzz.isCertificationGet(SubClass.CERTIFICATION_80)) {
            Functions.show(PATH + "certificate-already.htm", player, npc);
            return;
        }

        ClassId cl = clzz.getClassId();
        if (cl.getType2() == null)
            return;

        addItem(player, cl.getType2().transformation(), 1);
        clzz.addCertification(SubClass.CERTIFICATION_80);
        player.store(true);
    }

    public static void cancelCertification(Player player) {
        if (player.inventory.getAdena() < 10_000_000) {
            player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            return;
        }

        if (!player.getActiveClass().isBase())
            return;

        player.getInventory().reduceAdena(10000000, "cancelCertification");

        for (ClassType2 classType2 : ClassType2.VALUES) {
            player.getInventory().destroyItemByItemId(classType2.certificate, player.getInventory().getCountOf(classType2.certificate), "cancelCertification");
            player.getInventory().destroyItemByItemId(classType2.transformation, player.getInventory().getCountOf(classType2.transformation), "cancelCertification");
        }

        Collection<SkillLearn> skillLearnList = SkillAcquireHolder.getAvailableSkills(null, AcquireType.CERTIFICATION);
        for (SkillLearn learn : skillLearnList) {
            player.removeSkill(learn.id(), true);
        }

        player.getSubClasses().values().stream()
                .filter(subClass -> !subClass.isBase())
                .forEach(subClass -> subClass.setCertification(0));

        player.sendPacket(new SkillList(player));
        Functions.show(new CustomMessage("scripts.services.SubclassSkills.SkillsDeleted"), player);
    }

    private static boolean checkConditions(int level, NpcInstance npc, Player player, boolean first) {
        if (player.getLevel() < level) {
            Functions.show(PATH + "certificate-nolevel.htm", player, npc, Map.of("%level%", "" + level));
            return false;
        }

        if (player.getActiveClass().isBase()) {
            Functions.show(PATH + "certificate-nosub.htm", player, npc);
            return false;
        }

        if (first) {
            return true;
        }

        for (ClassType2 type : ClassType2.VALUES) {
            if (player.haveAnyItem(type.certificate(), type.transformation())) {
                Functions.show(PATH + "certificate-already.htm", player, npc);
                return false;
            }
        }

        return true;
    }
}
