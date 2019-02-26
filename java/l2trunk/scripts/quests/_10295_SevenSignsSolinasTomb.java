package l2trunk.scripts.quests;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.EventTrigger;
import l2trunk.gameserver.network.serverpackets.ExStartScenePlayer;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class _10295_SevenSignsSolinasTomb extends Quest {
    private static final int ErisEvilThoughts = 32792;
    private static final int ElcardiaInzone1 = 32787;
    private static final int TeleportControlDevice = 32820;
    private static final int PowerfulDeviceStaff = 32838;
    private static final int PowerfulDeviceBook = 32839;
    private static final int PowerfulDeviceSword = 32840;
    private static final int PowerfulDeviceShield = 32841;
    private static final int AltarofHallowsStaff = 32857;
    private static final int AltarofHallowsSword = 32858;
    private static final int AltarofHallowsBook = 32859;
    private static final int AltarofHallowsShield = 32860;

    private static final int TeleportControlDevice2 = 32837;
    private static final int TeleportControlDevice3 = 32842;
    private static final int TomboftheSaintess = 32843;

    private static final int ScrollofAbstinence = 17228;
    private static final int ShieldofSacrifice = 17229;
    private static final int SwordofHolySpirit = 17230;
    private static final int StaffofBlessing = 17231;

    private static final int Solina = 32793;

    private static final List<Integer> SolinaGuardians = List.of(18952, 18953, 18954, 18955);
    private static final List<Integer> TombGuardians = List.of(18956, 18957, 18958, 18959);

    static {
        Location[] minions1 = {Location.of(55672, -252120, -6760), Location.of(55752, -252120, -6760), Location.of(55656, -252216, -6760), Location.of(55736, -252216, -6760)};
        Location[] minions2 = {Location.of(55672, -252728, -6760), Location.of(55752, -252840, -6760), Location.of(55768, -252840, -6760), Location.of(55752, -252712, -6760)};
        Location[] minions3 = {Location.of(56504, -252840, -6760), Location.of(56504, -252728, -6760), Location.of(56392, -252728, -6760), Location.of(56408, -252840, -6760)};
        Location[] minions4 = {Location.of(56520, -252232, -6760), Location.of(56520, -252104, -6760), Location.of(56424, -252104, -6760), Location.of(56440, -252216, -6760)};
    }

    public _10295_SevenSignsSolinasTomb() {
        super(false);
        addStartNpc(ErisEvilThoughts);
        addTalkId(ElcardiaInzone1, TeleportControlDevice, PowerfulDeviceStaff, PowerfulDeviceBook, PowerfulDeviceSword, PowerfulDeviceShield);
        addTalkId(AltarofHallowsStaff, AltarofHallowsSword, AltarofHallowsBook, AltarofHallowsShield);
        addTalkId(TeleportControlDevice2, TomboftheSaintess, TeleportControlDevice3, Solina);
        addQuestItem(ScrollofAbstinence, ShieldofSacrifice, SwordofHolySpirit, StaffofBlessing);
        addKillId(SolinaGuardians);
        addKillId(TombGuardians);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        Player player = st.player;
        String htmltext = event;
        if ("eris_q10295_5.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("teleport_in".equalsIgnoreCase(event)) {
            player.teleToLocation(Location.of(45512, -249832, -6760));
            teleportElcardia(player);
            return null;
        } else if ("teleport_out".equalsIgnoreCase(event)) {
            player.teleToLocation(Location.of(120664, -86968, -3392));
            teleportElcardia(player);
            return null;
        } else if ("use_staff".equalsIgnoreCase(event)) {
            if (st.getQuestItemsCount(StaffofBlessing) > 0) {
                st.takeItems(StaffofBlessing);
                removeInvincibility(player, 18953);
                return null;
            } else
                htmltext = "powerful_q10295_0.htm";
        } else if ("use_book".equalsIgnoreCase(event)) {
            if (st.getQuestItemsCount(ScrollofAbstinence) > 0) {
                st.takeItems(ScrollofAbstinence);
                removeInvincibility(player, 18954);
                return null;
            } else
                htmltext = "powerful_q10295_0.htm";
        } else if ("use_sword".equalsIgnoreCase(event)) {
            if (st.getQuestItemsCount(SwordofHolySpirit) > 0) {
                st.takeItems(SwordofHolySpirit);
                removeInvincibility(player, 18955);
                return null;
            } else
                htmltext = "powerful_q10295_0.htm";
        } else if ("use_shield".equalsIgnoreCase(event)) {
            if (st.getQuestItemsCount(ShieldofSacrifice) > 0) {
                st.takeItems(ShieldofSacrifice);
                removeInvincibility(player, 18952);
                return null;
            } else
                htmltext = "powerful_q10295_0.htm";
        } else if ("altarstaff_q10295_2.htm".equalsIgnoreCase(event)) {
            if (st.getQuestItemsCount(StaffofBlessing) == 0)
                st.giveItems(StaffofBlessing);
            else
                htmltext = "atlar_q10295_0.htm";
        } else if ("altarbook_q10295_2.htm".equalsIgnoreCase(event)) {
            if (st.getQuestItemsCount(ScrollofAbstinence) == 0)
                st.giveItems(ScrollofAbstinence);
            else
                htmltext = "atlar_q10295_0.htm";
        } else if (event.equalsIgnoreCase("altarsword_q10295_2.htm")) {
            if (st.getQuestItemsCount(SwordofHolySpirit) == 0)
                st.giveItems(SwordofHolySpirit);
            else
                htmltext = "atlar_q10295_0.htm";
        } else if (event.equalsIgnoreCase("altarshield_q10295_2.htm")) {
            if (st.getQuestItemsCount(ShieldofSacrifice) == 0)
                st.giveItems(ShieldofSacrifice);
            else
                htmltext = "atlar_q10295_0.htm";
        } else if ("teleport_solina".equalsIgnoreCase(event)) {
            player.teleToLocation(Location.of(56033, -252944, -6760));
            teleportElcardia(player);
            return null;
        } else if ("tombsaintess_q10295_2.htm".equalsIgnoreCase(event)) {
            if (!player.getReflection().getDoor(21100101).isOpen())
                activateTombGuards(player);
            else
                htmltext = "tombsaintess_q10295_3.htm";
        } else if ("teleport_realtomb".equalsIgnoreCase(event)) {
            player.teleToLocation(Location.of(56081, -250391, -6760));
            teleportElcardia(player);
            player.showQuestMovie(ExStartScenePlayer.SCENE_SSQ2_ELYSS_NARRATION);
            return null;
        } else if ("solina_q10295_4.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if ("solina_q10295_8.htm".equalsIgnoreCase(event)) {
            st.setCond(3);
            st.playSound(SOUND_MIDDLE);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        Player player = st.player;
        if (player.getBaseClassId() != player.getActiveClassId())
            return "no_subclass_allowed.htm";
        if (npcId == ErisEvilThoughts) {
            if (cond == 0) {
                if (player.getLevel() >= 81 && player.isQuestCompleted(_10294_SevenSignsMonasteryofSilence.class))
                    htmltext = "eris_q10295_1.htm";
                else {
                    htmltext = "eris_q10295_0a.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "eris_q10295_6.htm";
            else if (cond == 2)
                htmltext = "eris_q10295_7.htm";
            else if (cond == 3) {
                if (player.getLevel() >= 81) {
                    htmltext = "eris_q10295_8.htm";
                    st.addExpAndSp(125000000, 12500000);
                    st.complete();
                    st.playSound(SOUND_FINISH);
                    st.finish();
                } else
                    htmltext = "eris_q10295_0.htm";
            }
        } else if (npcId == ElcardiaInzone1) {
            htmltext = "elcardia_q10295_1.htm";
        } else if (npcId == TeleportControlDevice) {
            if (!checkGuardians(player, SolinaGuardians))
                htmltext = "teleport_device_q10295_1.htm";
            else
                htmltext = "teleport_device_q10295_2.htm";

        } else if (npcId == PowerfulDeviceStaff) {
            htmltext = "powerfulstaff_q10295_1.htm";
        } else if (npcId == PowerfulDeviceBook) {
            htmltext = "powerfulbook_q10295_1.htm";
        } else if (npcId == PowerfulDeviceSword) {
            htmltext = "powerfulsword_q10295_1.htm";
        } else if (npcId == PowerfulDeviceShield) {
            htmltext = "powerfulsheild_q10295_1.htm";
        } else if (npcId == AltarofHallowsStaff) {
            htmltext = "altarstaff_q10295_1.htm";
        } else if (npcId == AltarofHallowsSword) {
            htmltext = "altarsword_q10295_1.htm";
        } else if (npcId == AltarofHallowsBook) {
            htmltext = "altarbook_q10295_1.htm";
        } else if (npcId == AltarofHallowsShield) {
            htmltext = "altarshield_q10295_1.htm";
        } else if (npcId == TeleportControlDevice2) {
            htmltext = "teleportdevice2_q10295_1.htm";
        } else if (npcId == TomboftheSaintess) {
            htmltext = "tombsaintess_q10295_1.htm";
        } else if (npcId == TeleportControlDevice3) {
            htmltext = "teleportdevice3_q10295_1.htm";
        } else if (npcId == Solina) {
            if (cond == 1)
                htmltext = "solina_q10295_1.htm";
            else if (cond == 2)
                htmltext = "solina_q10295_4.htm";
            else if (cond == 3)
                htmltext = "solina_q10295_8.htm";
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        Player player = st.player;
        if (SolinaGuardians.contains(npcId) && checkGuardians(player, SolinaGuardians)) {
            player.showQuestMovie(ExStartScenePlayer.SCENE_SSQ2_SOLINA_TOMB_CLOSING);
            player.broadcastPacket(new EventTrigger(21100100, false));
            player.broadcastPacket(new EventTrigger(21100102, true));
        }
        if (TombGuardians.contains(npcId)) {
            if (checkGuardians(player, TombGuardians))
                player.getReflection().openDoor(21100018);
            switch (npcId) {
                case 18956:
                    player.getReflection().despawnByGroup("tombguards3");
                    break;
                case 18957:
                    player.getReflection().despawnByGroup("tombguards2");
                    break;
                case 18958:
                    player.getReflection().despawnByGroup("tombguards1");
                    break;
                case 18959:
                    player.getReflection().despawnByGroup("tombguards4");
                    break;
            }
        }
    }

    private void teleportElcardia(Player player) {
        player.getReflection().getNpcs()
                .filter(n -> n.getNpcId() == ElcardiaInzone1)
                .forEach(n -> n.teleToLocation(Location.findPointToStay(player, 100)));
    }

    private void removeInvincibility(Player player, int mobId) {
        player.getReflection().getNpcs()
                .filter(n -> n.getNpcId() == mobId)
                .forEach(n -> n.getEffectList().getAllEffects().stream()
                        .filter(e -> e.skill.id == 6371)
                        .forEach(Effect::exit));
    }

    private boolean checkGuardians(Player player, List<Integer> npcIds) {
        return player.getReflection().getNpcs()
                .filter(n -> npcIds.contains(n.getNpcId()))
                .allMatch(Creature::isDead);
    }

    private void activateTombGuards(Player player) {
        Reflection r = player.getReflection();
        if (r == null || r.isDefault())
            return;

        r.openDoor(21100101);
        r.openDoor(21100102);
        r.openDoor(21100103);
        r.openDoor(21100104);
        r.spawnByGroup("tombguards1");
        r.spawnByGroup("tombguards2");
        r.spawnByGroup("tombguards3");
        r.spawnByGroup("tombguards4");
    }
}