package l2trunk.scripts.services.villagemasters;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.instances.VillageMasterInstance;
import l2trunk.gameserver.scripts.Functions;

public final class Occupation extends Functions {
    private int KamaelInquisitorMark = 9782;
    private int SB_Certificate = 9806;
    private int OrkurusRecommendation = 9760;
    private int MARK_OF_SEARCHER_ID = 2809;
    private int MARK_OF_GUILDSMAN_ID = 3119;
    private int MARK_OF_PROSPERITY_ID = 3238;
    private int MARK_OF_FAITH_ID = 1201;
    private int ETERNITY_DIAMOND_ID = 1230;
    private int LEAF_OF_ORACLE_ID = 1235;
    private int BEAD_OF_SEASON_ID = 1292;
    private int MEDALLION_OF_WARRIOR_ID = 1145;
    private int SWORD_OF_RITUAL_ID = 1161;
    private int BEZIQUES_RECOMMENDATION_ID = 1190;
    private int ELVEN_KNIGHT_BROOCH_ID = 1204;
    private int REORIA_RECOMMENDATION_ID = 1217;
    private int MARK_OF_CHALLENGER_ID = 2627;
    private int MARK_OF_PILGRIM_ID = 2721;
    private int MARK_OF_DUELIST_ID = 2762;
    private int MARK_OF_WARSPIRIT_ID = 2879;
    private int MARK_OF_GLORY_ID = 3203;
    private int MARK_OF_CHAMPION_ID = 3276;
    private int MARK_OF_LORD_ID = 3390;
    private int RING_OF_RAVEN_ID = 1642;
    private int MARK_OF_RAIDER_ID = 1592;
    private int KHAVATARI_TOTEM_ID = 1615;
    private int MASK_OF_MEDIUM_ID = 1631;
    private int MARK_OF_DUTY_ID = 2633;
    private int MARK_OF_SEEKER_ID = 2673;
    private int MARK_OF_SCHOLAR_ID = 2674;
    private int MARK_OF_REFORMER_ID = 2821;
    private int MARK_OF_MAGUS_ID = 2840;
    private int MARK_OF_FATE_ID = 3172;
    private int MARK_OF_SAGITTARIUS_ID = 3293;
    private int MARK_OF_WITCHCRAFT_ID = 3307;
    private int MARK_OF_SUMMONER_ID = 3336;
    private int GwainsRecommendation = 9753;
    private int PASS_FINAL_ID = 1635;
    private int MARK_OF_MAESTRO_ID = 2867;
    private int MARK_OF_TRUST_ID = 2734;
    private int MARK_OF_HEALER_ID = 2820;
    private int MARK_OF_LIFE_ID = 3140;
    private int MARK_OF_WITCHCRFAT_ID = 3307;
    private int GAZE_OF_ABYSS_ID = 1244;
    private int IRON_HEART_ID = 1252;
    private int JEWEL_OF_DARKNESS_ID = 1261;
    private int ORB_OF_ABYSS_ID = 1270;
    private int SteelrazorEvaluation = 9772;

    public void onTalk30026() {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }

        String htmltext;
        ClassId classId = player.getClassId();

        //fighter
        if (classId == ClassId.fighter)
            htmltext = "bitz003h.htm";

            //warrior, knight, rogue
        else if (classId == ClassId.warrior || classId == ClassId.knight || classId == ClassId.rogue)
            htmltext = "bitz004.htm";
            //warlord, paladin, treasureHunter
        else if (classId == ClassId.warlord || classId == ClassId.paladin || classId == ClassId.treasureHunter)
            htmltext = "bitz005.htm";
            //gladiator, darkAvenger, hawkeye
        else if (classId == ClassId.gladiator || classId == ClassId.darkAvenger || classId == ClassId.hawkeye)
            htmltext = "bitz005.htm";
        else
            htmltext = "bitz002.htm";

        npc.showChatWindow(player, "villagemaster/30026/" + htmltext);
    }

    public void onTalk30031() {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == ClassId.wizard || classId == ClassId.cleric)
            htmltext = "06.htm";
        else if (classId == ClassId.sorceror || classId == ClassId.necromancer || classId == ClassId.warlock || classId == ClassId.bishop || classId == ClassId.prophet)
            htmltext = "07.htm";
        else if (classId == ClassId.mage)
            htmltext = "01.htm";
        else
            // All other Races must be out
            htmltext = "08.htm";

        npc.showChatWindow(player, "villagemaster/30031/" + htmltext);
    }

    private void onTalk30037() {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == ClassId.elvenMage)
            htmltext = "01.htm";
        else if (classId == ClassId.mage)
            htmltext = "08.htm";
        else if (classId == ClassId.wizard || classId == ClassId.cleric || classId == ClassId.elvenWizard || classId == ClassId.oracle)
            htmltext = "31.htm";
        else if (classId == ClassId.sorceror || classId == ClassId.necromancer || classId == ClassId.bishop || classId == ClassId.warlock || classId == ClassId.prophet)
            htmltext = "32.htm";
        else if (classId == ClassId.spellsinger || classId == ClassId.elder || classId == ClassId.elementalSummoner)
            htmltext = "32.htm";
        else
            htmltext = "33.htm";

        npc.showChatWindow(player, "villagemaster/30037/" + htmltext);
    }

    public void onChange30037(String[] args) {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }


        int classid = Integer.parseInt(args[0]);

        int Level = player.getLevel();
        String htmltext = "33.htm";

        if (classid == 26 && player.getClassId() == ClassId.elvenMage) {
            if (Level <= 19 && player.getInventory().getItemByItemId(ETERNITY_DIAMOND_ID) == null)
                htmltext = "15.htm";
            else if (Level <= 19 && player.getInventory().getItemByItemId(ETERNITY_DIAMOND_ID) != null)
                htmltext = "16.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(ETERNITY_DIAMOND_ID) == null)
                htmltext = "17.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(ETERNITY_DIAMOND_ID) != null) {
                player.getInventory().destroyItemByItemId(ETERNITY_DIAMOND_ID, "onChange30037");
                player.setClassId(classid, false, true);
                htmltext = "18.htm";
            }
        } else if (classid == 29 && player.getClassId() == ClassId.elvenMage) {
            if (Level <= 19 && player.getInventory().getItemByItemId(LEAF_OF_ORACLE_ID) == null)
                htmltext = "19.htm";
            if (Level <= 19 && player.getInventory().getItemByItemId(LEAF_OF_ORACLE_ID) != null)
                htmltext = "20.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(LEAF_OF_ORACLE_ID) == null)
                htmltext = "21.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(LEAF_OF_ORACLE_ID) != null) {
                player.getInventory().destroyItemByItemId(LEAF_OF_ORACLE_ID, "onChange30037");
                player.setClassId(classid, false, true);
                htmltext = "22.htm";
            }
        } else if (classid == 11 && player.getClassId() == ClassId.mage) {
            if (Level <= 19 && player.getInventory().getItemByItemId(BEAD_OF_SEASON_ID) == null)
                htmltext = "23.htm";
            if (Level <= 19 && player.getInventory().getItemByItemId(BEAD_OF_SEASON_ID) != null)
                htmltext = "24.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(BEAD_OF_SEASON_ID) == null)
                htmltext = "25.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(BEAD_OF_SEASON_ID) != null) {
                player.getInventory().destroyItemByItemId(BEAD_OF_SEASON_ID, "onChange30037");
                player.setClassId(classid, false, true);
                htmltext = "26.htm";
            }
        } else if (classid == 15 && player.getClassId() == ClassId.mage) {
            if (Level <= 19 && player.getInventory().getItemByItemId(MARK_OF_FAITH_ID) == null)
                htmltext = "27.htm";
            if (Level <= 19 && player.getInventory().getItemByItemId(MARK_OF_FAITH_ID) != null)
                htmltext = "28.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(MARK_OF_FAITH_ID) == null)
                htmltext = "29.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(MARK_OF_FAITH_ID) != null) {
                player.getInventory().destroyItemByItemId(MARK_OF_FAITH_ID, "onChange30037");
                player.setClassId(classid, false, true);
                htmltext = "30.htm";
            }
        }

        npc.showChatWindow(player, "villagemaster/30037/" + htmltext);
    }

    private void onTalk30066() {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == ClassId.elvenFighter)
            htmltext = "01.htm";
        else if (classId == ClassId.fighter)
            htmltext = "08.htm";
        else if (classId == ClassId.elvenKnight || classId == ClassId.elvenScout || classId == ClassId.warrior || classId == ClassId.knight || classId == ClassId.rogue)
            htmltext = "38.htm";
        else if (classId == ClassId.templeKnight || classId == ClassId.plainsWalker || classId == ClassId.swordSinger || classId == ClassId.silverRanger)
            htmltext = "39.htm";
        else if (classId == ClassId.warlord || classId == ClassId.paladin || classId == ClassId.treasureHunter)
            htmltext = "39.htm";
        else if (classId == ClassId.gladiator || classId == ClassId.darkAvenger || classId == ClassId.hawkeye)
            htmltext = "39.htm";
        else
            htmltext = "40.htm";

        npc.showChatWindow(player, "villagemaster/30066/" + htmltext);
    }

    public void onChange30066(String[] args) {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }


        int newclass = Integer.parseInt(args[0]);

        int Level = player.getLevel();
        ClassId classId = player.getClassId();
        String htmltext = "No Quest";

        if (newclass == 19 && classId == ClassId.elvenFighter) {
            if (Level <= 19 && player.haveItem(ELVEN_KNIGHT_BROOCH_ID))
                htmltext = "18.htm";
            if (Level <= 19 && player.haveItem(ELVEN_KNIGHT_BROOCH_ID))
                htmltext = "19.htm";
            if (Level >= 20 && !player.haveItem(ELVEN_KNIGHT_BROOCH_ID))
                htmltext = "20.htm";
            if (Level >= 20 && player.haveItem(ELVEN_KNIGHT_BROOCH_ID)) {
                player.getInventory().destroyItemByItemId(ELVEN_KNIGHT_BROOCH_ID, "onChange30066");
                player.setClassId(newclass, false, true);
                htmltext = "21.htm";
            }
        }

        if (newclass == 22 && classId == ClassId.elvenFighter) {
            if (Level <= 19 && player.getInventory().getItemByItemId(REORIA_RECOMMENDATION_ID) == null)
                htmltext = "22.htm";
            if (Level <= 19 && player.haveItem(REORIA_RECOMMENDATION_ID))
                htmltext = "23.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(REORIA_RECOMMENDATION_ID) == null)
                htmltext = "24.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(REORIA_RECOMMENDATION_ID) != null) {
                player.getInventory().destroyItemByItemId(REORIA_RECOMMENDATION_ID, "onChange30066");
                player.setClassId(newclass, false, true);
                htmltext = "25.htm";
            }
        }

        if (newclass == 1 && classId == ClassId.fighter) {
            if (Level <= 19 && player.getInventory().getItemByItemId(MEDALLION_OF_WARRIOR_ID) == null)
                htmltext = "26.htm";
            if (Level <= 19 && player.getInventory().getItemByItemId(MEDALLION_OF_WARRIOR_ID) != null)
                htmltext = "27.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(MEDALLION_OF_WARRIOR_ID) == null)
                htmltext = "28.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(MEDALLION_OF_WARRIOR_ID) != null) {
                player.getInventory().destroyItemByItemId(MEDALLION_OF_WARRIOR_ID, "onChange30066");
                player.setClassId(newclass, false, true);
                htmltext = "29.htm";
            }
        }

        if (newclass == 4 && classId == ClassId.fighter) {
            if (Level <= 19 && player.getInventory().getItemByItemId(SWORD_OF_RITUAL_ID) == null)
                htmltext = "30.htm";
            if (Level <= 19 && player.getInventory().getItemByItemId(SWORD_OF_RITUAL_ID) != null)
                htmltext = "31.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(SWORD_OF_RITUAL_ID) == null)
                htmltext = "32.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(SWORD_OF_RITUAL_ID) != null) {
                player.getInventory().destroyItemByItemId(SWORD_OF_RITUAL_ID, "onChange30066");
                player.setClassId(newclass, false, true);
                htmltext = "33.htm";
            }
        }

        if (newclass == 7 && classId == ClassId.fighter) {
            if (Level <= 19 && player.getInventory().getItemByItemId(BEZIQUES_RECOMMENDATION_ID) == null)
                htmltext = "34.htm";
            if (Level <= 19 && player.getInventory().getItemByItemId(BEZIQUES_RECOMMENDATION_ID) != null)
                htmltext = "35.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(BEZIQUES_RECOMMENDATION_ID) == null)
                htmltext = "36.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(BEZIQUES_RECOMMENDATION_ID) != null) {
                player.getInventory().destroyItemByItemId(BEZIQUES_RECOMMENDATION_ID, "onChange30066");
                player.setClassId(newclass, false, true);
                htmltext = "37.htm";
            }
        }

        npc.showChatWindow(player, "villagemaster/30066/" + htmltext);
    }

    public void onTalk30511() {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == ClassId.scavenger)
            htmltext = "01.htm";
        else if (classId == ClassId.dwarvenFighter)
            htmltext = "09.htm";
        else if (classId == ClassId.bountyHunter || classId == ClassId.warsmith)
            htmltext = "10.htm";
        else
            htmltext = "11.htm";

        npc.showChatWindow(player, "villagemaster/30511/" + htmltext);
    }

    public void onChange30511(String[] args) {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }


        int newclass = Integer.parseInt(args[0]);

        int Level = player.getLevel();
        ClassId classId = player.getClassId();
        String htmltext = "No Quest";

        if (newclass == 55 && classId == ClassId.scavenger)
            if (Level <= 39) {
                if (player.getInventory().getItemByItemId(MARK_OF_SEARCHER_ID) == null || player.getInventory().getItemByItemId(MARK_OF_GUILDSMAN_ID) == null || player.getInventory().getItemByItemId(MARK_OF_PROSPERITY_ID) == null)
                    htmltext = "05.htm";
                else
                    htmltext = "06.htm";
            } else if (player.getInventory().getItemByItemId(MARK_OF_SEARCHER_ID) == null || player.getInventory().getItemByItemId(MARK_OF_GUILDSMAN_ID) == null || player.getInventory().getItemByItemId(MARK_OF_PROSPERITY_ID) == null)
                htmltext = "07.htm";
            else {
                player.getInventory().destroyItemByItemId(MARK_OF_SEARCHER_ID, "onChange30511");
                player.getInventory().destroyItemByItemId(MARK_OF_GUILDSMAN_ID, "onChange30511");
                player.getInventory().destroyItemByItemId(MARK_OF_PROSPERITY_ID, "onChange30511");
                player.setClassId(newclass, false, true);
                htmltext = "08.htm";
            }

        npc.showChatWindow(player, "villagemaster/30511/" + htmltext);
    }

    public void onTalk30070() {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == ClassId.elvenMage)
            htmltext = "01.htm";
        else if (classId == ClassId.wizard || classId == ClassId.cleric || classId == ClassId.elvenWizard || classId == ClassId.oracle)
            htmltext = "31.htm";
        else if (classId == ClassId.sorceror || classId == ClassId.necromancer || classId == ClassId.bishop || classId == ClassId.warlock || classId == ClassId.prophet || classId == ClassId.spellsinger || classId == ClassId.elder || classId == ClassId.elementalSummoner)
            htmltext = "32.htm";
        else if (classId == ClassId.mage)
            htmltext = "08.htm";
        else
            htmltext = "33.htm";

        npc.showChatWindow(player, "villagemaster/30070/" + htmltext);
    }

    public void onChange30070(String[] args) {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }


        int event = Integer.parseInt(args[0]);

        int Level = player.getLevel();
        ClassId classId = player.getClassId();
        String htmltext = "No Quest";

        if (event == 26 && classId == ClassId.elvenMage) {
            if (Level <= 19 && player.getInventory().getItemByItemId(ETERNITY_DIAMOND_ID) == null)
                htmltext = "15.htm";
            if (Level <= 19 && player.getInventory().getItemByItemId(ETERNITY_DIAMOND_ID) != null)
                htmltext = "16.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(ETERNITY_DIAMOND_ID) == null)
                htmltext = "17.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(ETERNITY_DIAMOND_ID) != null) {
                player.getInventory().destroyItemByItemId(ETERNITY_DIAMOND_ID, "onChange30070");
                player.setClassId(event, false, true);
                htmltext = "18.htm";
            }
        } else if (event == 29 && classId == ClassId.elvenMage) {
            if (Level <= 19 && player.getInventory().getItemByItemId(LEAF_OF_ORACLE_ID) == null)
                htmltext = "19.htm";
            if (Level <= 19 && player.getInventory().getItemByItemId(LEAF_OF_ORACLE_ID) != null)
                htmltext = "20.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(LEAF_OF_ORACLE_ID) == null)
                htmltext = "21.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(LEAF_OF_ORACLE_ID) != null) {
                player.getInventory().destroyItemByItemId(LEAF_OF_ORACLE_ID, "onChange30070");
                player.setClassId(event, false, true);
                htmltext = "22.htm";
            }
        } else if (event == 11 && classId == ClassId.mage) {
            if (Level <= 19 && player.getInventory().getItemByItemId(BEAD_OF_SEASON_ID) == null)
                htmltext = "23.htm";
            if (Level <= 19 && player.getInventory().getItemByItemId(BEAD_OF_SEASON_ID) != null)
                htmltext = "24.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(BEAD_OF_SEASON_ID) == null)
                htmltext = "25.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(BEAD_OF_SEASON_ID) != null) {
                player.getInventory().destroyItemByItemId(BEAD_OF_SEASON_ID, "onChange30070");
                player.setClassId(event, false, true);
                htmltext = "26.htm";
            }
        } else if (event == 15 && classId == ClassId.mage) {
            if (Level <= 19 && player.getInventory().getItemByItemId(MARK_OF_FAITH_ID) == null)
                htmltext = "27.htm";
            if (Level <= 19 && player.getInventory().getItemByItemId(MARK_OF_FAITH_ID) != null)
                htmltext = "28.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(MARK_OF_FAITH_ID) == null)
                htmltext = "29.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(MARK_OF_FAITH_ID) != null) {
                player.getInventory().destroyItemByItemId(MARK_OF_FAITH_ID, "onChange30070");
                player.setClassId(event, false, true);
                htmltext = "30.htm";
            }
        }

        npc.showChatWindow(player, "villagemaster/30070/" + htmltext);
    }

    public void onTalk30154() {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == ClassId.elvenFighter)
            htmltext = "01.htm";
        else if (classId == ClassId.elvenMage)
            htmltext = "02.htm";
        else if (classId == ClassId.elvenWizard || classId == ClassId.oracle || classId == ClassId.elvenKnight || classId == ClassId.elvenScout)
            htmltext = "12.htm";
        else if (player.getRace() == Race.elf)
            htmltext = "13.htm";
        else
            htmltext = "11.htm";

        npc.showChatWindow(player, "villagemaster/30154/" + htmltext);
    }

    public void onTalk30358() {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == ClassId.darkFighter)
            htmltext = "01.htm";
        else if (classId == ClassId.darkMage)
            htmltext = "02.htm";
        else if (classId == ClassId.darkWizard || classId == ClassId.shillienOracle || classId == ClassId.palusKnight || classId == ClassId.assassin)
            htmltext = "12.htm";
        else if (player.getRace() == Race.darkelf)
            htmltext = "13.htm";
        else
            htmltext = "11.htm";

        npc.showChatWindow(player, "villagemaster/30358/" + htmltext);
    }

    public void onTalk30498() {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == ClassId.dwarvenFighter)
            htmltext = "01.htm";
        else if (classId == ClassId.scavenger || classId == ClassId.artisan)
            htmltext = "09.htm";
        else if (player.getRace() == Race.dwarf)
            htmltext = "10.htm";
        else
            htmltext = "11.htm";

        npc.showChatWindow(player, "villagemaster/30498/" + htmltext);
    }

    public void onChange30498(String[] args) {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }


        int event = Integer.parseInt(args[0]);

        int Level = player.getLevel();
        ClassId classId = player.getClassId();
        String htmltext = "No Quest";

        if (event == 54 && classId == ClassId.dwarvenFighter) {
            if (Level <= 19 && player.getInventory().getItemByItemId(RING_OF_RAVEN_ID) == null)
                htmltext = "05.htm";
            if (Level <= 19 && player.getInventory().getItemByItemId(RING_OF_RAVEN_ID) != null)
                htmltext = "06.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(RING_OF_RAVEN_ID) == null)
                htmltext = "07.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(RING_OF_RAVEN_ID) != null) {
                player.getInventory().destroyItemByItemId(RING_OF_RAVEN_ID, "onChange30498");
                player.setClassId(event, false, true);
                htmltext = "08.htm";
            }
        }

        npc.showChatWindow(player, "villagemaster/30498/" + htmltext);
    }

    public void onTalk30499() {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == ClassId.dwarvenFighter)
            htmltext = "01.htm";
        else if (classId == ClassId.scavenger || classId == ClassId.artisan)
            htmltext = "09.htm";
        else if (player.getRace() == Race.dwarf)
            htmltext = "10.htm";
        else
            htmltext = "11.htm";

        npc.showChatWindow(player, "villagemaster/30499/" + htmltext);
    }

    public void onChange30499(String[] args) {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }


        int event = Integer.parseInt(args[0]);

        int Level = player.getLevel();
        ClassId classId = player.getClassId();
        String htmltext = "No Quest";

        if (event == 56 && classId == ClassId.dwarvenFighter) {
            if (Level <= 19 && player.getInventory().getItemByItemId(PASS_FINAL_ID) == null)
                htmltext = "05.htm";
            if (Level <= 19 && player.getInventory().getItemByItemId(PASS_FINAL_ID) != null)
                htmltext = "06.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(PASS_FINAL_ID) == null)
                htmltext = "07.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(PASS_FINAL_ID) != null) {
                player.getInventory().destroyItemByItemId(PASS_FINAL_ID, "onChange30499");
                player.setClassId(event, false, true);
                htmltext = "08.htm";
            }
        }

        npc.showChatWindow(player, "villagemaster/30499/" + htmltext);
    }

    public void onTalk30525() {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == ClassId.dwarvenFighter)
            htmltext = "01.htm";
        else if (classId == ClassId.artisan)
            htmltext = "05.htm";
        else if (classId == ClassId.warsmith)
            htmltext = "06.htm";
        else
            htmltext = "07.htm";

        npc.showChatWindow(player, "villagemaster/30525/" + htmltext);
    }

    public void onTalk30520() {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == ClassId.dwarvenFighter)
            htmltext = "01.htm";
        else if (classId == ClassId.artisan || classId == ClassId.scavenger)
            htmltext = "05.htm";
        else if (classId == ClassId.warsmith || classId == ClassId.bountyHunter)
            htmltext = "06.htm";
        else
            htmltext = "07.htm";

        npc.showChatWindow(player, "villagemaster/30520/" + htmltext);
    }

    public void onTalk30512() {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == ClassId.artisan)
            htmltext = "01.htm";
        else if (classId == ClassId.dwarvenFighter)
            htmltext = "09.htm";
        else if (classId == ClassId.warsmith || classId == ClassId.bountyHunter)
            htmltext = "10.htm";
        else
            htmltext = "11.htm";

        npc.showChatWindow(player, "villagemaster/30512/" + htmltext);
    }

    public void onChange30512(String[] args) {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }


        int event = Integer.parseInt(args[0]);

        int Level = player.getLevel();
        ClassId classId = player.getClassId();
        String htmltext = "No Quest";

        if (event == 57 && classId == ClassId.artisan)
            if (Level <= 39)
                if (player.getInventory().getItemByItemId(MARK_OF_MAESTRO_ID) == null || player.getInventory().getItemByItemId(MARK_OF_GUILDSMAN_ID) == null || player.getInventory().getItemByItemId(MARK_OF_PROSPERITY_ID) == null)
                    htmltext = "05.htm";
                else
                    htmltext = "06.htm";
            else if (player.getInventory().getItemByItemId(MARK_OF_MAESTRO_ID) == null || player.getInventory().getItemByItemId(MARK_OF_GUILDSMAN_ID) == null || player.getInventory().getItemByItemId(MARK_OF_PROSPERITY_ID) == null)
                htmltext = "07.htm";
            else {
                player.getInventory().destroyItemByItemId(MARK_OF_GUILDSMAN_ID, "onChange30512");
                player.getInventory().destroyItemByItemId(MARK_OF_MAESTRO_ID, "onChange30512");
                player.getInventory().destroyItemByItemId(MARK_OF_PROSPERITY_ID, "onChange30512");
                player.setClassId(event, false, true);
                htmltext = "08.htm";
            }

        npc.showChatWindow(player, "villagemaster/30512/" + htmltext);
    }

    private void onTalk30565() {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == ClassId.orcFighter)
            htmltext = "01.htm";
        else if (classId == ClassId.orcRaider || classId == ClassId.orcMonk || classId == ClassId.orcShaman)
            htmltext = "09.htm";
        else if (classId == ClassId.orcMage)
            htmltext = "16.htm";
        else if (player.getRace() == Race.orc)
            htmltext = "10.htm";
        else
            htmltext = "11.htm";

        npc.showChatWindow(player, "villagemaster/30565/" + htmltext);
    }

    public void onTalk30109() {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == ClassId.elvenKnight)
            htmltext = "01.htm";
        else if (classId == ClassId.knight)
            htmltext = "08.htm";
        else if (classId == ClassId.rogue)
            htmltext = "15.htm";
        else if (classId == ClassId.elvenScout)
            htmltext = "22.htm";
        else if (classId == ClassId.warrior)
            htmltext = "29.htm";
        else if (classId == ClassId.elvenFighter || classId == ClassId.fighter)
            htmltext = "76.htm";
        else if (classId == ClassId.templeKnight || classId == ClassId.plainsWalker || classId == ClassId.swordSinger || classId == ClassId.silverRanger)
            htmltext = "77.htm";
        else if (classId == ClassId.warlord || classId == ClassId.paladin || classId == ClassId.treasureHunter)
            htmltext = "77.htm";
        else if (classId == ClassId.gladiator || classId == ClassId.darkAvenger || classId == ClassId.hawkeye)
            htmltext = "77.htm";
        else
            htmltext = "78.htm";

        npc.showChatWindow(player, "villagemaster/30109/" + htmltext);
    }

    public void onChange30109(String[] args) {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }


        int event = Integer.parseInt(args[0]);

        int Level = player.getLevel();
        ClassId classId = player.getClassId();
        String htmltext = "No Quest";

        if (event == 20 && classId == ClassId.elvenKnight)
            if (Level <= 39)
                if (player.getInventory().getItemByItemId(MARK_OF_DUTY_ID) != null && player.getInventory().getItemByItemId(MARK_OF_LIFE_ID) != null && player.getInventory().getItemByItemId(MARK_OF_HEALER_ID) != null) {
                    htmltext = "37.htm";
                } else {
                    htmltext = "36.htm";
                }
            else if (player.getInventory().getItemByItemId(MARK_OF_DUTY_ID) != null && player.getInventory().getItemByItemId(MARK_OF_LIFE_ID) != null && player.getInventory().getItemByItemId(MARK_OF_HEALER_ID) != null) {
                player.getInventory().destroyItemByItemId(MARK_OF_DUTY_ID, "onChange30109");
                player.getInventory().destroyItemByItemId(MARK_OF_LIFE_ID, "onChange30109");
                player.getInventory().destroyItemByItemId(MARK_OF_HEALER_ID, "onChange30109");
                player.setClassId(event, false, true);
                htmltext = "39.htm";
            } else {
                htmltext = "38.htm";
            }

        else if (event == 21 && classId == ClassId.elvenKnight)
            if (Level <= 39)
                if (player.getInventory().getItemByItemId(MARK_OF_CHALLENGER_ID) == null || player.getInventory().getItemByItemId(MARK_OF_LIFE_ID) == null || player.getInventory().getItemByItemId(MARK_OF_DUELIST_ID) == null)
                    htmltext = "40.htm";
                else
                    htmltext = "41.htm";
            else if (player.getInventory().getItemByItemId(MARK_OF_CHALLENGER_ID) == null || player.getInventory().getItemByItemId(MARK_OF_LIFE_ID) == null || player.getInventory().getItemByItemId(MARK_OF_DUELIST_ID) == null)
                htmltext = "42.htm";
            else {
                player.getInventory().destroyItemByItemId(MARK_OF_CHALLENGER_ID, "onChange30109");
                player.getInventory().destroyItemByItemId(MARK_OF_LIFE_ID, "onChange30109");
                player.getInventory().destroyItemByItemId(MARK_OF_DUELIST_ID, "onChange30109");
                player.setClassId(event, false, true);
                htmltext = "43.htm";
            }

        else if (event == 5 && classId == ClassId.knight)
            if (Level <= 39)
                if (player.getInventory().getItemByItemId(MARK_OF_DUTY_ID) == null || player.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || player.getInventory().getItemByItemId(MARK_OF_HEALER_ID) == null)
                    htmltext = "44.htm";
                else
                    htmltext = "45.htm";
            else if (player.getInventory().getItemByItemId(MARK_OF_DUTY_ID) == null || player.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || player.getInventory().getItemByItemId(MARK_OF_HEALER_ID) == null)
                htmltext = "46.htm";
            else {
                player.getInventory().destroyItemByItemId(MARK_OF_DUTY_ID, "onChange30109");
                player.getInventory().destroyItemByItemId(MARK_OF_TRUST_ID, "onChange30109");
                player.getInventory().destroyItemByItemId(MARK_OF_HEALER_ID, "onChange30109");
                player.setClassId(event, false, true);
                htmltext = "47.htm";
            }

        else if (event == 6 && classId == ClassId.knight)
            if (Level <= 39)
                if (player.getInventory().getItemByItemId(MARK_OF_DUTY_ID) == null || player.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || player.getInventory().getItemByItemId(MARK_OF_WITCHCRAFT_ID) == null)
                    htmltext = "48.htm";
                else
                    htmltext = "49.htm";
            else if (player.getInventory().getItemByItemId(MARK_OF_DUTY_ID) == null || player.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || player.getInventory().getItemByItemId(MARK_OF_WITCHCRAFT_ID) == null)
                htmltext = "50.htm";
            else {
                player.getInventory().destroyItemByItemId(MARK_OF_DUTY_ID, "onChange30109");
                player.getInventory().destroyItemByItemId(MARK_OF_TRUST_ID, "onChange30109");
                player.getInventory().destroyItemByItemId(MARK_OF_WITCHCRAFT_ID, "onChange30109");
                player.setClassId(event, false, true);
                htmltext = "51.htm";
            }

        else if (event == 8 && classId == ClassId.rogue)
            if (Level <= 39)
                if (player.getInventory().getItemByItemId(MARK_OF_SEEKER_ID) == null || player.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || player.getInventory().getItemByItemId(MARK_OF_SEARCHER_ID) == null)
                    htmltext = "52.htm";
                else
                    htmltext = "53.htm";
            else if (player.getInventory().getItemByItemId(MARK_OF_SEEKER_ID) == null || player.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || player.getInventory().getItemByItemId(MARK_OF_SEARCHER_ID) == null)
                htmltext = "54.htm";
            else {
                player.getInventory().destroyItemByItemId(MARK_OF_SEEKER_ID, "onChange30109");
                player.getInventory().destroyItemByItemId(MARK_OF_TRUST_ID, "onChange30109");
                player.getInventory().destroyItemByItemId(MARK_OF_SEARCHER_ID, "onChange30109");
                player.setClassId(event, false, true);
                htmltext = "55.htm";
            }

        else if (event == 9 && classId == ClassId.rogue)
            if (Level <= 39)
                if (player.getInventory().getItemByItemId(MARK_OF_SEEKER_ID) == null || player.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || player.getInventory().getItemByItemId(MARK_OF_SAGITTARIUS_ID) == null)
                    htmltext = "56.htm";
                else
                    htmltext = "57.htm";
            else if (player.getInventory().getItemByItemId(MARK_OF_SEEKER_ID) == null || player.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || player.getInventory().getItemByItemId(MARK_OF_SAGITTARIUS_ID) == null)
                htmltext = "58.htm";
            else {
                player.getInventory().destroyItemByItemId(MARK_OF_SEEKER_ID, "onChange30109");
                player.getInventory().destroyItemByItemId(MARK_OF_TRUST_ID, "onChange30109");
                player.getInventory().destroyItemByItemId(MARK_OF_SAGITTARIUS_ID, "onChange30109");
                player.setClassId(event, false, true);
                htmltext = "59.htm";
            }

        else if (event == 23 && classId == ClassId.elvenScout)
            if (Level <= 39)
                if (player.getInventory().getItemByItemId(MARK_OF_SEEKER_ID) == null || player.getInventory().getItemByItemId(MARK_OF_LIFE_ID) == null || player.getInventory().getItemByItemId(MARK_OF_SEARCHER_ID) == null)
                    htmltext = "60.htm";
                else
                    htmltext = "61.htm";
            else if (player.getInventory().getItemByItemId(MARK_OF_SEEKER_ID) == null || player.getInventory().getItemByItemId(MARK_OF_LIFE_ID) == null || player.getInventory().getItemByItemId(MARK_OF_SEARCHER_ID) == null)
                htmltext = "62.htm";
            else {
                player.getInventory().destroyItemByItemId(MARK_OF_SEEKER_ID, "onChange30109");
                player.getInventory().destroyItemByItemId(MARK_OF_LIFE_ID, "onChange30109");
                player.getInventory().destroyItemByItemId(MARK_OF_SEARCHER_ID, "onChange30109");
                player.setClassId(event, false, true);
                htmltext = "63.htm";
            }

        else if (event == 24 && classId == ClassId.elvenScout)
            if (Level <= 39)
                if (player.getInventory().getItemByItemId(MARK_OF_SEEKER_ID) == null || player.getInventory().getItemByItemId(MARK_OF_LIFE_ID) == null || player.getInventory().getItemByItemId(MARK_OF_SAGITTARIUS_ID) == null)
                    htmltext = "64.htm";
                else
                    htmltext = "65.htm";
            else if (player.getInventory().getItemByItemId(MARK_OF_SEEKER_ID) == null || player.getInventory().getItemByItemId(MARK_OF_LIFE_ID) == null || player.getInventory().getItemByItemId(MARK_OF_SAGITTARIUS_ID) == null)
                htmltext = "66.htm";
            else {
                player.getInventory().destroyItemByItemId(MARK_OF_SEEKER_ID, "onChange30109");
                player.getInventory().destroyItemByItemId(MARK_OF_LIFE_ID, "onChange30109");
                player.getInventory().destroyItemByItemId(MARK_OF_SAGITTARIUS_ID, "onChange30109");
                player.setClassId(event, false, true);
                htmltext = "67.htm";
            }

        else if (event == 2 && classId == ClassId.warrior)
            if (Level <= 39)
                if (player.getInventory().getItemByItemId(MARK_OF_CHALLENGER_ID) == null || player.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || player.getInventory().getItemByItemId(MARK_OF_DUELIST_ID) == null)
                    htmltext = "68.htm";
                else
                    htmltext = "69.htm";
            else if (player.getInventory().getItemByItemId(MARK_OF_CHALLENGER_ID) == null || player.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || player.getInventory().getItemByItemId(MARK_OF_DUELIST_ID) == null)
                htmltext = "70.htm";
            else {
                player.getInventory().destroyItemByItemId(MARK_OF_CHALLENGER_ID, "onChange30109");
                player.getInventory().destroyItemByItemId(MARK_OF_TRUST_ID, "onChange30109");
                player.getInventory().destroyItemByItemId(MARK_OF_DUELIST_ID, "onChange30109");
                player.setClassId(event, false, true);
                htmltext = "71.htm";
            }

        else if (event == 3 && classId == ClassId.warrior)
            if (Level <= 39)
                if (player.getInventory().getItemByItemId(MARK_OF_CHALLENGER_ID) == null || player.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || player.getInventory().getItemByItemId(MARK_OF_CHAMPION_ID) == null)
                    htmltext = "72.htm";
                else
                    htmltext = "73.htm";
            else if (player.getInventory().getItemByItemId(MARK_OF_CHALLENGER_ID) == null || player.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || player.getInventory().getItemByItemId(MARK_OF_CHAMPION_ID) == null)
                htmltext = "74.htm";
            else {
                player.getInventory().destroyItemByItemId(MARK_OF_CHALLENGER_ID, "onChange30109");
                player.getInventory().destroyItemByItemId(MARK_OF_TRUST_ID, "onChange30109");
                player.getInventory().destroyItemByItemId(MARK_OF_CHAMPION_ID, "onChange30109");
                player.setClassId(event, false, true);
                htmltext = "75.htm";
            }

        npc.showChatWindow(player, "villagemaster/30109/" + htmltext);
    }

    public void onTalk30115() {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == ClassId.elvenWizard)
            htmltext = "01.htm";
        else if (classId == ClassId.wizard)
            htmltext = "08.htm";
        else if (classId == ClassId.sorceror || classId == ClassId.necromancer || classId == ClassId.warlock)
            htmltext = "39.htm";
        else if (classId == ClassId.spellsinger || classId == ClassId.elementalSummoner)
            htmltext = "39.htm";
        else if ((player.getRace() == Race.elf || player.getRace() == Race.human) && classId.isMage)
            htmltext = "38.htm";
        else
            htmltext = "40.htm";

        npc.showChatWindow(player, "villagemaster/30115/" + htmltext);
    }

    public void onChange30115(String[] args) {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }


        int event = Integer.parseInt(args[0]);

        int Level = player.getLevel();
        ClassId classId = player.getClassId();
        String htmltext = "No Quest";

        if (event == 27 && classId == ClassId.elvenWizard)
            if (Level <= 39)
                if (player.getInventory().getItemByItemId(MARK_OF_SCHOLAR_ID) == null || player.getInventory().getItemByItemId(MARK_OF_LIFE_ID) == null || player.getInventory().getItemByItemId(MARK_OF_MAGUS_ID) == null)
                    htmltext = "18.htm";
                else
                    htmltext = "19.htm";
            else if (player.getInventory().getItemByItemId(MARK_OF_SCHOLAR_ID) == null || player.getInventory().getItemByItemId(MARK_OF_LIFE_ID) == null || player.getInventory().getItemByItemId(MARK_OF_MAGUS_ID) == null)
                htmltext = "20.htm";
            else {
                player.getInventory().destroyItemByItemId(MARK_OF_SCHOLAR_ID, "onChange30115");
                player.getInventory().destroyItemByItemId(MARK_OF_LIFE_ID, "onChange30115");
                player.getInventory().destroyItemByItemId(MARK_OF_MAGUS_ID, "onChange30115");
                player.setClassId(event, false, true);
                htmltext = "21.htm";
            }

        else if (event == 28 && classId == ClassId.elvenWizard)
            if (Level <= 39)
                if (player.getInventory().getItemByItemId(MARK_OF_SCHOLAR_ID) == null || player.getInventory().getItemByItemId(MARK_OF_LIFE_ID) == null || player.getInventory().getItemByItemId(MARK_OF_SUMMONER_ID) == null)
                    htmltext = "22.htm";
                else
                    htmltext = "23.htm";
            else if (player.getInventory().getItemByItemId(MARK_OF_SCHOLAR_ID) == null || player.getInventory().getItemByItemId(MARK_OF_LIFE_ID) == null || player.getInventory().getItemByItemId(MARK_OF_SUMMONER_ID) == null)
                htmltext = "24.htm";
            else {
                player.getInventory().destroyItemByItemId(MARK_OF_SCHOLAR_ID, "onChange30115");
                player.getInventory().destroyItemByItemId(MARK_OF_LIFE_ID, "onChange30115");
                player.getInventory().destroyItemByItemId(MARK_OF_SUMMONER_ID, "onChange30115");
                player.setClassId(event, false, true);
                htmltext = "25.htm";
            }

        else if (event == 12 && classId == ClassId.wizard)
            if (Level <= 39)
                if (player.getInventory().getItemByItemId(MARK_OF_SCHOLAR_ID) == null || player.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || player.getInventory().getItemByItemId(MARK_OF_MAGUS_ID) == null)
                    htmltext = "26.htm";
                else
                    htmltext = "27.htm";
            else if (player.getInventory().getItemByItemId(MARK_OF_SCHOLAR_ID) == null || player.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || player.getInventory().getItemByItemId(MARK_OF_MAGUS_ID) == null)
                htmltext = "28.htm";
            else {
                player.getInventory().destroyItemByItemId(MARK_OF_SCHOLAR_ID, "onChange30115");
                player.getInventory().destroyItemByItemId(MARK_OF_TRUST_ID, "onChange30115");
                player.getInventory().destroyItemByItemId(MARK_OF_MAGUS_ID, "onChange30115");
                player.setClassId(event, false, true);
                htmltext = "29.htm";
            }

        else if (event == 13 && classId == ClassId.wizard)
            if (Level <= 39)
                if (player.getInventory().getItemByItemId(MARK_OF_SCHOLAR_ID) == null || player.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || player.getInventory().getItemByItemId(MARK_OF_WITCHCRFAT_ID) == null)
                    htmltext = "30.htm";
                else
                    htmltext = "31.htm";
            else if (player.getInventory().getItemByItemId(MARK_OF_SCHOLAR_ID) == null || player.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || player.getInventory().getItemByItemId(MARK_OF_WITCHCRFAT_ID) == null)
                htmltext = "32.htm";
            else {
                player.getInventory().destroyItemByItemId(MARK_OF_SCHOLAR_ID, "onChange30115");
                player.getInventory().destroyItemByItemId(MARK_OF_TRUST_ID, "onChange30115");
                player.getInventory().destroyItemByItemId(MARK_OF_WITCHCRFAT_ID, "onChange30115");
                player.setClassId(event, false, true);
                htmltext = "33.htm";
            }

        else if (event == 14 && classId == ClassId.wizard)
            if (Level <= 39)
                if (player.getInventory().getItemByItemId(MARK_OF_SCHOLAR_ID) == null || player.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || player.getInventory().getItemByItemId(MARK_OF_SUMMONER_ID) == null)
                    htmltext = "34.htm";
                else
                    htmltext = "35.htm";
            else if (player.getInventory().getItemByItemId(MARK_OF_SCHOLAR_ID) == null || player.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || player.getInventory().getItemByItemId(MARK_OF_SUMMONER_ID) == null)
                htmltext = "36.htm";
            else {
                player.getInventory().destroyItemByItemId(MARK_OF_SCHOLAR_ID, "onChange30115");
                player.getInventory().destroyItemByItemId(MARK_OF_TRUST_ID, "onChange30115");
                player.getInventory().destroyItemByItemId(MARK_OF_SUMMONER_ID, "onChange30115");
                player.setClassId(event, false, true);
                htmltext = "37.htm";
            }

        npc.showChatWindow(player, "villagemaster/30115/" + htmltext);
    }

    public void onTalk30120() {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == ClassId.oracle)
            htmltext = "01.htm";
        else if (classId == ClassId.cleric)
            htmltext = "05.htm";
        else if (classId == ClassId.elder || classId == ClassId.bishop || classId == ClassId.prophet)
            htmltext = "25.htm";
        else if ((player.getRace() == Race.human || player.getRace() == Race.elf) && classId.isMage)
            htmltext = "24.htm";
        else
            htmltext = "26.htm";

        npc.showChatWindow(player, "villagemaster/30120/" + htmltext);
    }

    public void onChange30120(String[] args) {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }


        int event = Integer.parseInt(args[0]);

        int Level = player.getLevel();
        ClassId classId = player.getClassId();
        String htmltext = "No Quest";

        if (event == 30 || classId == ClassId.oracle)
            if (Level <= 39)
                if (player.getInventory().getItemByItemId(MARK_OF_PILGRIM_ID) == null || player.getInventory().getItemByItemId(MARK_OF_LIFE_ID) == null || player.getInventory().getItemByItemId(MARK_OF_HEALER_ID) == null)
                    htmltext = "12.htm";
                else
                    htmltext = "13.htm";
            else if (player.getInventory().getItemByItemId(MARK_OF_PILGRIM_ID) == null || player.getInventory().getItemByItemId(MARK_OF_LIFE_ID) == null || player.getInventory().getItemByItemId(MARK_OF_HEALER_ID) == null)
                htmltext = "14.htm";
            else {
                player.getInventory().destroyItemByItemId(MARK_OF_PILGRIM_ID, "onChange30120");
                player.getInventory().destroyItemByItemId(MARK_OF_LIFE_ID, "onChange30120");
                player.getInventory().destroyItemByItemId(MARK_OF_HEALER_ID, "onChange30120");
                player.setClassId(event, false, true);
                htmltext = "15.htm";
            }

        else if (event == 16 && classId == ClassId.cleric)
            if (Level <= 39)
                if (player.getInventory().getItemByItemId(MARK_OF_PILGRIM_ID) == null || player.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || player.getInventory().getItemByItemId(MARK_OF_HEALER_ID) == null)
                    htmltext = "16.htm";
                else
                    htmltext = "17.htm";
            else if (player.getInventory().getItemByItemId(MARK_OF_PILGRIM_ID) == null || player.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || player.getInventory().getItemByItemId(MARK_OF_HEALER_ID) == null)
                htmltext = "18.htm";
            else {
                player.getInventory().destroyItemByItemId(MARK_OF_PILGRIM_ID, "onChange30120");
                player.getInventory().destroyItemByItemId(MARK_OF_TRUST_ID, "onChange30120");
                player.getInventory().destroyItemByItemId(MARK_OF_HEALER_ID, "onChange30120");
                player.setClassId(event, false, true);
                htmltext = "19.htm";
            }

        else if (event == 17 && classId == ClassId.cleric)
            if (Level <= 39)
                if (player.getInventory().getItemByItemId(MARK_OF_PILGRIM_ID) == null || player.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || player.getInventory().getItemByItemId(MARK_OF_REFORMER_ID) == null)
                    htmltext = "20.htm";
                else
                    htmltext = "21.htm";
            else if (player.getInventory().getItemByItemId(MARK_OF_PILGRIM_ID) == null || player.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || player.getInventory().getItemByItemId(MARK_OF_REFORMER_ID) == null)
                htmltext = "22.htm";
            else {
                player.getInventory().destroyItemByItemId(MARK_OF_PILGRIM_ID, "onChange30120");
                player.getInventory().destroyItemByItemId(MARK_OF_TRUST_ID, "onChange30120");
                player.getInventory().destroyItemByItemId(MARK_OF_REFORMER_ID, "onChange30120");
                player.setClassId(event, false, true);
                htmltext = "23.htm";
            }

        npc.showChatWindow(player, "villagemaster/30120/" + htmltext);
    }

    public void onTalk30500() {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == ClassId.orcFighter)
            htmltext = "01.htm";
        else if (classId == ClassId.orcMage)
            htmltext = "06.htm";
        else if (classId == ClassId.orcRaider || classId == ClassId.orcMonk || classId == ClassId.orcShaman)
            htmltext = "21.htm";
        else if (classId == ClassId.destroyer || classId == ClassId.tyrant || classId == ClassId.overlord || classId == ClassId.warcryer)
            htmltext = "22.htm";
        else
            htmltext = "23.htm";

        npc.showChatWindow(player, "villagemaster/30500/" + htmltext);
    }

    public void onChange30500(String[] args) {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }


        int event = Integer.parseInt(args[0]);

        int Level = player.getLevel();
        ClassId classId = player.getClassId();
        String htmltext = "No Quest";

        if (event == 45 && classId == ClassId.orcFighter) {
            if (Level <= 19 && player.getInventory().getItemByItemId(MARK_OF_RAIDER_ID) == null)
                htmltext = "09.htm";
            if (Level <= 19 && player.getInventory().getItemByItemId(MARK_OF_RAIDER_ID) != null)
                htmltext = "10.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(MARK_OF_RAIDER_ID) == null)
                htmltext = "11.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(MARK_OF_RAIDER_ID) != null) {
                player.getInventory().destroyItemByItemId(MARK_OF_RAIDER_ID, "onChange30500");
                player.setClassId(event, false, true);
                htmltext = "12.htm";
            }
        } else if (event == 47 && classId == ClassId.orcFighter) {
            if (Level <= 19 && player.getInventory().getItemByItemId(KHAVATARI_TOTEM_ID) == null)
                htmltext = "13.htm";
            if (Level <= 19 && player.getInventory().getItemByItemId(KHAVATARI_TOTEM_ID) != null)
                htmltext = "14.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(KHAVATARI_TOTEM_ID) == null)
                htmltext = "15.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(KHAVATARI_TOTEM_ID) != null) {
                player.getInventory().destroyItemByItemId(KHAVATARI_TOTEM_ID, "onChange30500");
                player.setClassId(event, false, true);
                htmltext = "16.htm";
            }
        } else if (event == 50 && classId == ClassId.orcMage) {
            if (Level <= 19 && player.getInventory().getItemByItemId(MASK_OF_MEDIUM_ID) == null)
                htmltext = "17.htm";
            if (Level <= 19 && player.getInventory().getItemByItemId(MASK_OF_MEDIUM_ID) != null)
                htmltext = "18.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(MASK_OF_MEDIUM_ID) == null)
                htmltext = "19.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(MASK_OF_MEDIUM_ID) != null) {
                player.getInventory().destroyItemByItemId(MASK_OF_MEDIUM_ID, "onChange30500");
                player.setClassId(event, false, true);
                htmltext = "20.htm";
            }
        }

        npc.showChatWindow(player, "villagemaster/30500/" + htmltext);
    }

    public void onTalk30290() {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == ClassId.darkFighter)
            htmltext = "01.htm";
        else if (classId == ClassId.darkMage)
            htmltext = "08.htm";
        else if (classId == ClassId.palusKnight || classId == ClassId.assassin || classId == ClassId.darkWizard || classId == ClassId.shillienOracle)
            htmltext = "31.htm";
        else if (player.getRace() == Race.darkelf)
            htmltext = "32.htm";
        else
            htmltext = "33.htm";

        npc.showChatWindow(player, "villagemaster/30290/" + htmltext);
    }

    public void onChange30290(String[] args) {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }


        int event = Integer.parseInt(args[0]);

        int Level = player.getLevel();
        ClassId classId = player.getClassId();
        String htmltext = "No Quest";

        if (event == 32 && classId == ClassId.darkFighter) {
            if (Level <= 19 && player.getInventory().getItemByItemId(GAZE_OF_ABYSS_ID) == null)
                htmltext = "15.htm";
            if (Level <= 19 && player.getInventory().getItemByItemId(GAZE_OF_ABYSS_ID) != null)
                htmltext = "16.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(GAZE_OF_ABYSS_ID) == null)
                htmltext = "17.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(GAZE_OF_ABYSS_ID) != null) {
                player.getInventory().destroyItemByItemId(GAZE_OF_ABYSS_ID, "onChange30290");
                player.setClassId(event, false, true);
                htmltext = "18.htm";
            }
        } else if (event == 35 && classId == ClassId.darkFighter) {
            if (Level <= 19 && player.getInventory().getItemByItemId(IRON_HEART_ID) == null)
                htmltext = "19.htm";
            if (Level <= 19 && player.getInventory().getItemByItemId(IRON_HEART_ID) != null)
                htmltext = "20.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(IRON_HEART_ID) == null)
                htmltext = "21.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(IRON_HEART_ID) != null) {
                player.getInventory().destroyItemByItemId(IRON_HEART_ID, "onChange30290");
                player.setClassId(event, false, true);
                htmltext = "22.htm";
            }
        } else if (event == 39 && classId == ClassId.darkMage) {
            if (Level <= 19 && player.getInventory().getItemByItemId(JEWEL_OF_DARKNESS_ID) == null)
                htmltext = "23.htm";
            if (Level <= 19 && player.getInventory().getItemByItemId(JEWEL_OF_DARKNESS_ID) != null)
                htmltext = "24.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(JEWEL_OF_DARKNESS_ID) == null)
                htmltext = "25.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(JEWEL_OF_DARKNESS_ID) != null) {
                player.getInventory().destroyItemByItemId(JEWEL_OF_DARKNESS_ID, "onChange30290");
                player.setClassId(event, false, true);
                htmltext = "26.htm";
            }
        } else if (event == 42 && classId == ClassId.darkMage) {
            if (Level <= 19 && player.getInventory().getItemByItemId(ORB_OF_ABYSS_ID) == null)
                htmltext = "27.htm";
            if (Level <= 19 && player.getInventory().getItemByItemId(ORB_OF_ABYSS_ID) != null)
                htmltext = "28.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(ORB_OF_ABYSS_ID) == null)
                htmltext = "29.htm";
            if (Level >= 20 && player.getInventory().getItemByItemId(ORB_OF_ABYSS_ID) != null) {
                player.getInventory().destroyItemByItemId(ORB_OF_ABYSS_ID, "onChange30290");
                player.setClassId(event, false, true);
                htmltext = "30.htm";
            }
        }

        npc.showChatWindow(player, "villagemaster/30290/" + htmltext);
    }

    public void onTalk30513() {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == ClassId.orcMonk)
            htmltext = "01.htm";
        else if (classId == ClassId.orcRaider)
            htmltext = "05.htm";
        else if (classId == ClassId.orcShaman)
            htmltext = "09.htm";
        else if (classId == ClassId.destroyer || classId == ClassId.tyrant || classId == ClassId.overlord || classId == ClassId.warcryer)
            htmltext = "32.htm";
        else if (classId == ClassId.orcFighter || classId == ClassId.orcMage)
            htmltext = "33.htm";
        else
            htmltext = "34.htm";

        npc.showChatWindow(player, "villagemaster/30513/" + htmltext);
    }

    public void onChange30513(String[] args) {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }


        int event = Integer.parseInt(args[0]);

        int Level = player.getLevel();
        ClassId classId = player.getClassId();
        String htmltext = "No Quest";

        if (event == 48 && classId == ClassId.orcMonk)
            if (Level <= 39)
                if (player.getInventory().getItemByItemId(MARK_OF_CHALLENGER_ID) == null || player.getInventory().getItemByItemId(MARK_OF_GLORY_ID) == null || player.getInventory().getItemByItemId(MARK_OF_DUELIST_ID) == null)
                    htmltext = "16.htm";
                else
                    htmltext = "17.htm";
            else if (player.getInventory().getItemByItemId(MARK_OF_CHALLENGER_ID) == null || player.getInventory().getItemByItemId(MARK_OF_GLORY_ID) == null || player.getInventory().getItemByItemId(MARK_OF_DUELIST_ID) == null)
                htmltext = "18.htm";
            else {
                player.getInventory().destroyItemByItemId(MARK_OF_CHALLENGER_ID, "onChange30513");
                player.getInventory().destroyItemByItemId(MARK_OF_GLORY_ID, "onChange30513");
                player.getInventory().destroyItemByItemId(MARK_OF_DUELIST_ID, "onChange30513");
                player.setClassId(event, false, true);
                htmltext = "19.htm";
            }

        else if (event == 46 && classId == ClassId.orcRaider)
            if (Level <= 39)
                if (player.getInventory().getItemByItemId(MARK_OF_CHALLENGER_ID) == null || player.getInventory().getItemByItemId(MARK_OF_GLORY_ID) == null || player.getInventory().getItemByItemId(MARK_OF_CHAMPION_ID) == null)
                    htmltext = "20.htm";
                else
                    htmltext = "21.htm";
            else if (player.getInventory().getItemByItemId(MARK_OF_CHALLENGER_ID) == null || player.getInventory().getItemByItemId(MARK_OF_GLORY_ID) == null || player.getInventory().getItemByItemId(MARK_OF_CHAMPION_ID) == null)
                htmltext = "22.htm";
            else {
                player.getInventory().destroyItemByItemId(MARK_OF_CHALLENGER_ID, "onChange30513");
                player.getInventory().destroyItemByItemId(MARK_OF_GLORY_ID, "onChange30513");
                player.getInventory().destroyItemByItemId(MARK_OF_CHAMPION_ID, "onChange30513");
                player.setClassId(event, false, true);
                htmltext = "23.htm";
            }

        else if (event == 51 && classId == ClassId.orcShaman)
            if (Level <= 39)
                if (player.getInventory().getItemByItemId(MARK_OF_PILGRIM_ID) == null || player.getInventory().getItemByItemId(MARK_OF_GLORY_ID) == null || player.getInventory().getItemByItemId(MARK_OF_LORD_ID) == null)
                    htmltext = "24.htm";
                else
                    htmltext = "25.htm";
            else if (player.getInventory().getItemByItemId(MARK_OF_PILGRIM_ID) == null || player.getInventory().getItemByItemId(MARK_OF_GLORY_ID) == null || player.getInventory().getItemByItemId(MARK_OF_LORD_ID) == null)
                htmltext = "26.htm";
            else {
                player.getInventory().destroyItemByItemId(MARK_OF_PILGRIM_ID, "onChange30513");
                player.getInventory().destroyItemByItemId(MARK_OF_GLORY_ID, "onChange30513");
                player.getInventory().destroyItemByItemId(MARK_OF_LORD_ID, "onChange30513");
                player.setClassId(event, false, true);
                htmltext = "27.htm";
            }

        else if (event == 52 && classId == ClassId.orcShaman)
            if (Level <= 39)
                if (player.getInventory().getItemByItemId(MARK_OF_PILGRIM_ID) == null || player.getInventory().getItemByItemId(MARK_OF_GLORY_ID) == null || player.getInventory().getItemByItemId(MARK_OF_WARSPIRIT_ID) == null)
                    htmltext = "28.htm";
                else
                    htmltext = "29.htm";
            else if (player.getInventory().getItemByItemId(MARK_OF_PILGRIM_ID) == null || player.getInventory().getItemByItemId(MARK_OF_GLORY_ID) == null || player.getInventory().getItemByItemId(MARK_OF_WARSPIRIT_ID) == null)
                htmltext = "30.htm";
            else {
                player.getInventory().destroyItemByItemId(MARK_OF_PILGRIM_ID, "onChange30513");
                player.getInventory().destroyItemByItemId(MARK_OF_GLORY_ID, "onChange30513");
                player.getInventory().destroyItemByItemId(MARK_OF_WARSPIRIT_ID, "onChange30513");
                player.setClassId(event, false, true);
                htmltext = "31.htm";
            }

        npc.showChatWindow(player, "villagemaster/30513/" + htmltext);
    }

    public void onTalk30474() {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }

        String htmltext;
        ClassId classId = player.getClassId();

        if (npc.getNpcId() == 30175) {
            if (classId == ClassId.shillienOracle)
                htmltext = "08.htm";
            else if (classId == ClassId.darkWizard)
                htmltext = "19.htm";
            else if (classId == ClassId.spellhowler || classId == ClassId.shillienElder || classId == ClassId.phantomSummoner)
                htmltext = "54.htm";
            else if (classId == ClassId.darkMage)
                htmltext = "55.htm";
            else
                htmltext = "56.htm";
        } else if (classId == ClassId.palusKnight)
            htmltext = "01.htm";
        else if (classId == ClassId.shillienOracle)
            htmltext = "08.htm";
        else if (classId == ClassId.assassin)
            htmltext = "12.htm";
        else if (classId == ClassId.darkWizard)
            htmltext = "19.htm";
        else if (classId == ClassId.shillienKnight || classId == ClassId.abyssWalker || classId == ClassId.bladedancer || classId == ClassId.phantomRanger)
            htmltext = "54.htm";
        else if (classId == ClassId.spellhowler || classId == ClassId.shillienElder || classId == ClassId.phantomSummoner)
            htmltext = "54.htm";
        else if (classId == ClassId.darkFighter || classId == ClassId.darkMage)
            htmltext = "55.htm";
        else
            htmltext = "56.htm";

        npc.showChatWindow(player, "villagemaster/30474/" + htmltext);
    }

    public void onChange30474(String[] args) {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }


        int event = Integer.parseInt(args[0]);

        int Level = player.getLevel();
        ClassId classId = player.getClassId();
        String htmltext = "No Quest";

        if (event == 33 && classId == ClassId.palusKnight)
            if (Level <= 39)
                if (player.getInventory().getItemByItemId(MARK_OF_DUTY_ID) == null || player.getInventory().getItemByItemId(MARK_OF_FATE_ID) == null || player.getInventory().getItemByItemId(MARK_OF_WITCHCRAFT_ID) == null)
                    htmltext = "26.htm";
                else
                    htmltext = "27.htm";
            else if (player.getInventory().getItemByItemId(MARK_OF_DUTY_ID) == null || player.getInventory().getItemByItemId(MARK_OF_FATE_ID) == null || player.getInventory().getItemByItemId(MARK_OF_WITCHCRAFT_ID) == null)
                htmltext = "28.htm";
            else {
                player.getInventory().destroyItemByItemId(MARK_OF_DUTY_ID, "onChange30474");
                player.getInventory().destroyItemByItemId(MARK_OF_FATE_ID, "onChange30474");
                player.getInventory().destroyItemByItemId(MARK_OF_WITCHCRAFT_ID, "onChange30474");
                player.setClassId(event, false, true);
                htmltext = "29.htm";
            }

        else if (event == 34 && classId == ClassId.palusKnight)
            if (Level <= 39)
                if (player.getInventory().getItemByItemId(MARK_OF_CHALLENGER_ID) == null || player.getInventory().getItemByItemId(MARK_OF_FATE_ID) == null || player.getInventory().getItemByItemId(MARK_OF_DUELIST_ID) == null)
                    htmltext = "30.htm";
                else
                    htmltext = "31.htm";
            else if (player.getInventory().getItemByItemId(MARK_OF_CHALLENGER_ID) == null || player.getInventory().getItemByItemId(MARK_OF_FATE_ID) == null || player.getInventory().getItemByItemId(MARK_OF_DUELIST_ID) == null)
                htmltext = "32.htm";
            else {
                player.getInventory().destroyItemByItemId(MARK_OF_CHALLENGER_ID, "onChange30474");
                player.getInventory().destroyItemByItemId(MARK_OF_FATE_ID, "onChange30474");
                player.getInventory().destroyItemByItemId(MARK_OF_DUELIST_ID, "onChange30474");
                player.setClassId(event, false, true);
                htmltext = "33.htm";
            }

        else if (event == 43 && classId == ClassId.shillienOracle)
            if (Level <= 39)
                if (player.getInventory().getItemByItemId(MARK_OF_PILGRIM_ID) == null || player.getInventory().getItemByItemId(MARK_OF_FATE_ID) == null || player.getInventory().getItemByItemId(MARK_OF_REFORMER_ID) == null)
                    htmltext = "34.htm";
                else
                    htmltext = "35.htm";
            else if (player.getInventory().getItemByItemId(MARK_OF_PILGRIM_ID) == null || player.getInventory().getItemByItemId(MARK_OF_FATE_ID) == null || player.getInventory().getItemByItemId(MARK_OF_REFORMER_ID) == null)
                htmltext = "36.htm";
            else {
                player.getInventory().destroyItemByItemId(MARK_OF_PILGRIM_ID, "onChange30474");
                player.getInventory().destroyItemByItemId(MARK_OF_FATE_ID, "onChange30474");
                player.getInventory().destroyItemByItemId(MARK_OF_REFORMER_ID, "onChange30474");
                player.setClassId(event, false, true);
                htmltext = "37.htm";
            }

        else if (event == 36 && classId == ClassId.assassin)
            if (Level <= 39)
                if (player.getInventory().getItemByItemId(MARK_OF_SEEKER_ID) == null || player.getInventory().getItemByItemId(MARK_OF_FATE_ID) == null || player.getInventory().getItemByItemId(MARK_OF_SEARCHER_ID) == null)
                    htmltext = "38.htm";
                else
                    htmltext = "39.htm";
            else if (player.getInventory().getItemByItemId(MARK_OF_SEEKER_ID) == null || player.getInventory().getItemByItemId(MARK_OF_FATE_ID) == null || player.getInventory().getItemByItemId(MARK_OF_SEARCHER_ID) == null)
                htmltext = "40.htm";
            else {
                player.getInventory().destroyItemByItemId(MARK_OF_SEEKER_ID, "onChange30474");
                player.getInventory().destroyItemByItemId(MARK_OF_FATE_ID, "onChange30474");
                player.getInventory().destroyItemByItemId(MARK_OF_SEARCHER_ID, "onChange30474");
                player.setClassId(event, false, true);
                htmltext = "41.htm";
            }

        else if (event == 37 && classId == ClassId.assassin)
            if (Level <= 39)
                if (player.getInventory().getItemByItemId(MARK_OF_SEEKER_ID) == null || player.getInventory().getItemByItemId(MARK_OF_FATE_ID) == null || player.getInventory().getItemByItemId(MARK_OF_SAGITTARIUS_ID) == null)
                    htmltext = "42.htm";
                else
                    htmltext = "43.htm";
            else if (player.getInventory().getItemByItemId(MARK_OF_SEEKER_ID) == null || player.getInventory().getItemByItemId(MARK_OF_FATE_ID) == null || player.getInventory().getItemByItemId(MARK_OF_SAGITTARIUS_ID) == null)
                htmltext = "44.htm";
            else {
                player.getInventory().destroyItemByItemId(MARK_OF_SEEKER_ID, "onChange30474");
                player.getInventory().destroyItemByItemId(MARK_OF_FATE_ID, "onChange30474");
                player.getInventory().destroyItemByItemId(MARK_OF_SAGITTARIUS_ID, "onChange30474");
                player.setClassId(event, false, true);
                htmltext = "45.htm";
            }

        else if (event == 40 && classId == ClassId.darkWizard)
            if (Level <= 39)
                if (player.getInventory().getItemByItemId(MARK_OF_SCHOLAR_ID) == null || player.getInventory().getItemByItemId(MARK_OF_FATE_ID) == null || player.getInventory().getItemByItemId(MARK_OF_MAGUS_ID) == null)
                    htmltext = "46.htm";
                else
                    htmltext = "47.htm";
            else if (player.getInventory().getItemByItemId(MARK_OF_SCHOLAR_ID) == null || player.getInventory().getItemByItemId(MARK_OF_FATE_ID) == null || player.getInventory().getItemByItemId(MARK_OF_MAGUS_ID) == null)
                htmltext = "48.htm";
            else {
                player.getInventory().destroyItemByItemId(MARK_OF_SCHOLAR_ID, "onChange30474");
                player.getInventory().destroyItemByItemId(MARK_OF_FATE_ID, "onChange30474");
                player.getInventory().destroyItemByItemId(MARK_OF_MAGUS_ID, "onChange30474");
                player.setClassId(event, false, true);
                htmltext = "49.htm";
            }

        else if (event == 41 && classId == ClassId.darkWizard)
            if (Level <= 39)
                if (player.getInventory().getItemByItemId(MARK_OF_SCHOLAR_ID) == null || player.getInventory().getItemByItemId(MARK_OF_FATE_ID) == null || player.getInventory().getItemByItemId(MARK_OF_SUMMONER_ID) == null)
                    htmltext = "50.htm";
                else
                    htmltext = "51.htm";
            else if (player.getInventory().getItemByItemId(MARK_OF_SCHOLAR_ID) == null || player.getInventory().getItemByItemId(MARK_OF_FATE_ID) == null || player.getInventory().getItemByItemId(MARK_OF_SUMMONER_ID) == null)
                htmltext = "52.htm";
            else {
                player.getInventory().destroyItemByItemId(MARK_OF_SCHOLAR_ID, "onChange30474");
                player.getInventory().destroyItemByItemId(MARK_OF_FATE_ID, "onChange30474");
                player.getInventory().destroyItemByItemId(MARK_OF_SUMMONER_ID, "onChange30474");
                player.setClassId(event, false, true);
                htmltext = "53.htm";
            }

        npc.showChatWindow(player, "villagemaster/30474/" + htmltext);
    }

    public void onChange32145(String[] args) {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }


        int event = Integer.parseInt(args[0]);

        int Level = player.getLevel();
        ClassId classId = player.getClassId();
        String htmltext = "04.htm";

        if (event == 126 && classId == ClassId.femaleSoldier)
            if (Level >= 20 && player.getInventory().getItemByItemId(SteelrazorEvaluation) != null) {
                player.getInventory().destroyItemByItemId(SteelrazorEvaluation, "onChange32145");
                player.setClassId(event, false, true);
                htmltext = "03.htm";
            }

        npc.showChatWindow(player, "villagemaster/32145/" + htmltext);
    }

    public void onTalk32145() {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == ClassId.femaleSoldier)
            htmltext = "01.htm";
        else
            htmltext = "02.htm";

        npc.showChatWindow(player, "villagemaster/32145/" + htmltext);
    }

    public void onChange32146(String[] args) {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }


        int event = Integer.parseInt(args[0]);

        int Level = player.getLevel();
        ClassId classId = player.getClassId();
        String htmltext = "04.htm";

        if (event == 125 && classId == ClassId.maleSoldier)
            if (Level >= 20 && player.getInventory().getItemByItemId(GwainsRecommendation) != null) {
                player.getInventory().destroyItemByItemId(GwainsRecommendation, "onChange32146");
                player.setClassId(event, false, true);
                htmltext = "03.htm";
            }

        npc.showChatWindow(player, "villagemaster/32146/" + htmltext);
    }

    public void onTalk32146() {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == ClassId.maleSoldier)
            htmltext = "01.htm";
        else
            htmltext = "02.htm";

        npc.showChatWindow(player, "villagemaster/32146/" + htmltext);
    }

    private void onTalk32199() {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == ClassId.warder)
            htmltext = "01.htm";
        else if (classId == ClassId.trooper)
            htmltext = "11.htm";
        else
            htmltext = "02.htm";

        npc.showChatWindow(player, "villagemaster/32199/" + htmltext);
    }

    public void onTalk32157() {
        String prefix = "head_blacksmith_mokabred";
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }

        String htmltext;
        ClassId classId = player.getClassId();
        Race race = player.getRace();

        if (race != Race.dwarf)
            htmltext = "002.htm";
        else if (classId == ClassId.dwarvenFighter)
            htmltext = "003f.htm";
        else if (classId.occupation() == 2)
            htmltext = "004.htm";
        else
            htmltext = "005.htm";

        npc.showChatWindow(player, "villagemaster/32157/" + prefix + htmltext);
    }

    public void onTalk32160() {
        String prefix = "grandmagister_devon";
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }

        String htmltext;
        ClassId classId = player.getClassId();
        Race race = player.getRace();

        if (race != Race.darkelf)
            htmltext = "002.htm";
        else if (classId == ClassId.darkFighter)
            htmltext = "003f.htm";
        else if (classId == ClassId.darkMage)
            htmltext = "003m.htm";
        else if (classId.occupation() == 2)
            htmltext = "004.htm";
        else
            htmltext = "005.htm";

        npc.showChatWindow(player, "villagemaster/32160/" + prefix + htmltext);
    }

    private void onChange32199(String[] args) {
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }


        int classid = Integer.parseInt(args[0]);

        int Level = player.getLevel();
        String htmltext = "02.htm";

        if (classid == 130 && player.getClassId() == ClassId.warder) {
            if (Level <= 39 && player.getInventory().getItemByItemId(KamaelInquisitorMark) == null)
                htmltext = "03.htm";
            else if (Level <= 39 && player.getInventory().getItemByItemId(KamaelInquisitorMark) != null)
                htmltext = "04.htm";
            if (Level >= 40 && player.getInventory().getItemByItemId(KamaelInquisitorMark) == null)
                htmltext = "05.htm";
            if (Level >= 40 && player.getInventory().getItemByItemId(KamaelInquisitorMark) != null) {
                player.getInventory().destroyItemByItemId(KamaelInquisitorMark, "onChange32199");
                player.setClassId(classid, false, true);
                htmltext = "06.htm";
            }
        } else if (classid == 129 && player.getClassId() == ClassId.warder) {
            if (Level <= 39 && player.getInventory().getItemByItemId(SB_Certificate) == null)
                htmltext = "07.htm";
            else if (Level <= 39 && player.getInventory().getItemByItemId(SB_Certificate) != null)
                htmltext = "08.htm";
            if (Level >= 40 && player.getInventory().getItemByItemId(SB_Certificate) == null)
                htmltext = "09.htm";
            if (Level >= 40 && player.getInventory().getItemByItemId(SB_Certificate) != null) {
                player.getInventory().destroyItemByItemId(SB_Certificate, "onChange32199");
                player.setClassId(classid, false, true);
                htmltext = "10.htm";
            }
        } else if (classid == 127 && player.getClassId() == ClassId.trooper) {
            if (Level <= 39 && player.getInventory().getItemByItemId(OrkurusRecommendation) == null)
                htmltext = "12.htm";
            else if (Level <= 39 && player.getInventory().getItemByItemId(OrkurusRecommendation) != null)
                htmltext = "13.htm";
            if (Level >= 40 && player.getInventory().getItemByItemId(OrkurusRecommendation) == null)
                htmltext = "14.htm";
            if (Level >= 40 && player.getInventory().getItemByItemId(OrkurusRecommendation) != null) {
                player.getInventory().destroyItemByItemId(OrkurusRecommendation, "onChange32199'");
                player.setClassId(classid, false, true);
                htmltext = "15.htm";
            }
        } else if (classid == 128 && player.getClassId() == ClassId.trooper) {
            if (Level <= 39 && player.getInventory().getItemByItemId(SB_Certificate) == null)
                htmltext = "16.htm";
            else if (Level <= 39 && player.getInventory().getItemByItemId(SB_Certificate) != null)
                htmltext = "17.htm";
            if (Level >= 40 && player.getInventory().getItemByItemId(SB_Certificate) == null)
                htmltext = "18.htm";
            if (Level >= 40 && player.getInventory().getItemByItemId(SB_Certificate) != null) {
                player.getInventory().destroyItemByItemId(SB_Certificate, "onChange32199");
                player.setClassId(classid, false, true);
                htmltext = "19.htm";
            }
        }
        npc.showChatWindow(player, "villagemaster/32199/" + htmltext);
    }

    public void onTalk32158() {
        String prefix = "warehouse_chief_fisser";
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }

        String htmltext;
        ClassId classId = player.getClassId();
        Race race = player.getRace();

        if (race != Race.dwarf)
            htmltext = "002.htm";
        else if (classId == ClassId.dwarvenFighter)
            htmltext = "003f.htm";
        else if (classId.occupation() == 2)
            htmltext = "004.htm";
        else
            htmltext = "005.htm";

        npc.showChatWindow(player, "villagemaster/32158/" + prefix + htmltext);
    }

    public void onTalk32171() {
        String prefix = "warehouse_chief_hufran";
        if (player == null || npc == null)
            return;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return;
        }

        String htmltext;
        ClassId classId = player.getClassId();
        Race race = player.getRace();

        if (race != Race.dwarf)
            htmltext = "002.htm";
        else if (classId == ClassId.dwarvenFighter)
            htmltext = "003f.htm";
        else if (classId.occupation() == 2)
            htmltext = "004.htm";
        else
            htmltext = "005.htm";

        npc.showChatWindow(player, "villagemaster/32171/" + prefix + htmltext);
    }

    public void onTalk32213() {
        onTalk32199();
    }

    public void onChange32213(String[] args) {
        onChange32199(args);
    }

    public void onTalk32214() {
        onTalk32199();
    }

    public void onChange32214(String[] args) {
        onChange32199(args);
    }

    public void onTalk32217() {
        onTalk32199();
    }

    public void onChange32217(String[] args) {
        onChange32199(args);
    }

    public void onTalk32218() {
        onTalk32199();
    }

    public void onChange32218(String[] args) {
        onChange32199(args);
    }

    public void onTalk32221() {
        onTalk32199();
    }

    public void onChange32221(String[] args) {
        onChange32199(args);
    }

    public void onTalk32222() {
        onTalk32199();
    }

    public void onChange32222(String[] args) {
        onChange32199(args);
    }

    public void onTalk32205() {
        onTalk32199();
    }

    public void onChange32205(String[] args) {
        onChange32199(args);
    }

    public void onTalk32206() {
        onTalk32199();
    }

    public void onChange32206(String[] args) {
        onChange32199(args);
    }

    public void onTalk32147() {
        onTalk30037();
    }

    public void onTalk32150() {
        onTalk30565();
    }

    public void onTalk32153() {
        onTalk30037();
    }

    public void onTalk32154() {
        onTalk30066();
    }

    public void onTalk32226() {
        onTalk32199();
    }

    public void onTalk32225() {
        onTalk32199();
    }

    public void onTalk32230() {
        onTalk32199();
    }

    public void onTalk32229() {
        onTalk32199();
    }

    public void onTalk32233() {
        onTalk32199();
    }

    public void onTalk32234() {
        onTalk32199();
    }

    public void onTalk32202() {
        onTalk32199();
    }

    public void onTalk32210() {
        onTalk32199();
    }

    public void onTalk32209() {
        onTalk32199();
    }
}