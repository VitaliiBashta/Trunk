package l2trunk.scripts.services.villagemasters;

import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.VillageMasterInstance;
import l2trunk.gameserver.scripts.Functions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static l2trunk.commons.lang.NumberUtils.toInt;
import static l2trunk.gameserver.model.base.ClassId.*;

public final class Occupation extends Functions {
    private final static int ORB_OF_ABYSS_ID = 1270;
    private final static int SteelrazorEvaluation = 9772;

    private final static int MARK_OF_PROSPERITY_ID = 3238;
    private final static int KamaelInquisitorMark = 9782;
    private final static int SB_Certificate = 9806;
    private final static int OrkurusRecommendation = 9760;
    private final static int MARK_OF_SEARCHER_ID = 2809;
    private final static int MARK_OF_GUILDSMAN_ID = 3119;
    private final static int MARK_OF_FAITH_ID = 1201;
    private final static int ETERNITY_DIAMOND_ID = 1230;
    private final static int LEAF_OF_ORACLE_ID = 1235;
    private final static int BEAD_OF_SEASON_ID = 1292;
    private final static int MEDALLION_OF_WARRIOR_ID = 1145;
    private final static int SWORD_OF_RITUAL_ID = 1161;
    private final static int REORIA_RECOMMENDATION_ID = 1217;
    private final static int MARK_OF_SUMMONER_ID = 3336;
    private final static int MARK_OF_PILGRIM_ID = 2721;
    private final static int MARK_OF_WARSPIRIT_ID = 2879;
    private final static int MARK_OF_REFORMER_ID = 2821;
    private final static int BEZIQUES_RECOMMENDATION_ID = 1190;
    private final static int MARK_OF_HEALER_ID = 2820;
    private final static int MARK_OF_LIFE_ID = 3140;
    private final static int GAZE_OF_ABYSS_ID = 1244;
    private final static int KHAVATARI_TOTEM_ID = 1615;
    private final static int MASK_OF_MEDIUM_ID = 1631;
    private final static int RING_OF_RAVEN_ID = 1642;
    private final static int MARK_OF_SEEKER_ID = 2673;
    private final static int MARK_OF_SAGITTARIUS_ID = 3293;
    private final static int GwainsRecommendation = 9753;
    private final static int PASS_FINAL_ID = 1635;
    private final static int MARK_OF_MAESTRO_ID = 2867;
    private final static int IRON_HEART_ID = 1252;
    private final static int JEWEL_OF_DARKNESS_ID = 1261;
    private final static int MARK_OF_RAIDER_ID = 1592;
    private final static int MARK_OF_TRUST_ID = 2734;
    private final static int MARK_OF_DUELIST_ID = 2762;
    private final static int ELVEN_KNIGHT_BROOCH_ID = 1204;
    private final static int MARK_OF_CHALLENGER_ID = 2627;
    private final static int MARK_OF_CHAMPION_ID = 3276;
    private final static int MARK_OF_DUTY_ID = 2633;
    private final static int MARK_OF_SCHOLAR_ID = 2674;
    private final static int MARK_OF_WITCHCRAFT_ID = 3307;
    private final static int MARK_OF_MAGUS_ID = 2840;
    private final static int MARK_OF_WITCHCRFAT_ID = 3307;
    private final static int MARK_OF_FATE_ID = 3172;
    private final static int MARK_OF_GLORY_ID = 3203;
    private final static int MARK_OF_LORD_ID = 3390;
    private final static Map<ClassId, List<Integer>> MARKS = new HashMap<>();
    private final static Map<ClassId, Integer> HTMLS = new HashMap<>();

    static {
        MARKS.put(warrior, List.of(MEDALLION_OF_WARRIOR_ID));
        MARKS.put(knight, List.of(SWORD_OF_RITUAL_ID));
        MARKS.put(rogue, List.of(BEZIQUES_RECOMMENDATION_ID));
        MARKS.put(wizard, List.of(BEAD_OF_SEASON_ID));
        MARKS.put(cleric, List.of(MARK_OF_FAITH_ID));
        MARKS.put(elvenKnight, List.of(ELVEN_KNIGHT_BROOCH_ID));
        MARKS.put(elvenScout, List.of(REORIA_RECOMMENDATION_ID));
        MARKS.put(elvenWizard, List.of(ETERNITY_DIAMOND_ID));
        MARKS.put(oracle, List.of(LEAF_OF_ORACLE_ID));
        MARKS.put(palusKnight, List.of(GAZE_OF_ABYSS_ID));
        MARKS.put(assassin, List.of(IRON_HEART_ID));
        MARKS.put(darkWizard, List.of(JEWEL_OF_DARKNESS_ID));
        MARKS.put(shillienOracle, List.of(ORB_OF_ABYSS_ID));
        MARKS.put(orcRaider, List.of(MARK_OF_RAIDER_ID));
        MARKS.put(orcMonk, List.of(KHAVATARI_TOTEM_ID));
        MARKS.put(orcShaman, List.of(MASK_OF_MEDIUM_ID));
        MARKS.put(scavenger, List.of(RING_OF_RAVEN_ID));
        MARKS.put(artisan, List.of(PASS_FINAL_ID));

        MARKS.put(gladiator, List.of(MARK_OF_CHALLENGER_ID, MARK_OF_TRUST_ID, MARK_OF_DUELIST_ID));
        MARKS.put(warlord, List.of(MARK_OF_CHALLENGER_ID, MARK_OF_TRUST_ID, MARK_OF_CHAMPION_ID));
        MARKS.put(paladin, List.of(MARK_OF_DUTY_ID, MARK_OF_TRUST_ID, MARK_OF_HEALER_ID));
        MARKS.put(darkAvenger, List.of(MARK_OF_DUTY_ID, MARK_OF_TRUST_ID, MARK_OF_WITCHCRAFT_ID));
        MARKS.put(treasureHunter, List.of(MARK_OF_SEEKER_ID, MARK_OF_TRUST_ID, MARK_OF_SEARCHER_ID));
        MARKS.put(hawkeye, List.of(MARK_OF_SEEKER_ID, MARK_OF_TRUST_ID, MARK_OF_SAGITTARIUS_ID));

        MARKS.put(sorceror, List.of(MARK_OF_SCHOLAR_ID, MARK_OF_TRUST_ID, MARK_OF_MAGUS_ID));

        MARKS.put(necromancer, List.of(MARK_OF_SCHOLAR_ID, MARK_OF_TRUST_ID, MARK_OF_WITCHCRFAT_ID));
        MARKS.put(warlock, List.of(MARK_OF_SCHOLAR_ID, MARK_OF_TRUST_ID, MARK_OF_SUMMONER_ID));
        MARKS.put(bishop, List.of(MARK_OF_PILGRIM_ID, MARK_OF_TRUST_ID, MARK_OF_HEALER_ID));
        MARKS.put(prophet, List.of(MARK_OF_PILGRIM_ID, MARK_OF_TRUST_ID, MARK_OF_REFORMER_ID));

        MARKS.put(templeKnight, List.of(MARK_OF_DUTY_ID, MARK_OF_LIFE_ID, MARK_OF_HEALER_ID));
        MARKS.put(swordSinger, List.of(MARK_OF_CHALLENGER_ID, MARK_OF_LIFE_ID, MARK_OF_DUELIST_ID));
        MARKS.put(plainsWalker, List.of(MARK_OF_SEEKER_ID, MARK_OF_LIFE_ID, MARK_OF_SEARCHER_ID));
        MARKS.put(silverRanger, List.of(MARK_OF_SEEKER_ID, MARK_OF_LIFE_ID, MARK_OF_SAGITTARIUS_ID));

        MARKS.put(spellsinger, List.of(MARK_OF_SCHOLAR_ID, MARK_OF_LIFE_ID, MARK_OF_MAGUS_ID));
        MARKS.put(elementalSummoner, List.of(MARK_OF_SCHOLAR_ID, MARK_OF_LIFE_ID, MARK_OF_SUMMONER_ID));
        MARKS.put(elder, List.of(MARK_OF_PILGRIM_ID, MARK_OF_LIFE_ID, MARK_OF_HEALER_ID));

        MARKS.put(shillienKnight, List.of(MARK_OF_DUTY_ID, MARK_OF_FATE_ID, MARK_OF_WITCHCRAFT_ID));
        MARKS.put(bladedancer, List.of(MARK_OF_CHALLENGER_ID, MARK_OF_FATE_ID, MARK_OF_DUELIST_ID));
        MARKS.put(abyssWalker, List.of(MARK_OF_SEEKER_ID, MARK_OF_FATE_ID, MARK_OF_SEARCHER_ID));
        MARKS.put(phantomRanger, List.of(MARK_OF_SEEKER_ID, MARK_OF_FATE_ID, MARK_OF_SAGITTARIUS_ID));
        MARKS.put(spellhowler, List.of(MARK_OF_SCHOLAR_ID, MARK_OF_FATE_ID, MARK_OF_MAGUS_ID));
        MARKS.put(phantomSummoner, List.of(MARK_OF_SCHOLAR_ID, MARK_OF_FATE_ID, MARK_OF_SUMMONER_ID));
        MARKS.put(shillienElder, List.of(MARK_OF_PILGRIM_ID, MARK_OF_FATE_ID, MARK_OF_REFORMER_ID));

        MARKS.put(destroyer, List.of(MARK_OF_CHALLENGER_ID, MARK_OF_GLORY_ID, MARK_OF_CHAMPION_ID));
        MARKS.put(tyrant, List.of(MARK_OF_CHALLENGER_ID, MARK_OF_GLORY_ID, MARK_OF_DUELIST_ID));

        MARKS.put(overlord, List.of(MARK_OF_PILGRIM_ID, MARK_OF_GLORY_ID, MARK_OF_LORD_ID));
        MARKS.put(warcryer, List.of(MARK_OF_PILGRIM_ID, MARK_OF_GLORY_ID, MARK_OF_WARSPIRIT_ID));

        MARKS.put(bountyHunter, List.of(MARK_OF_SEARCHER_ID, MARK_OF_GUILDSMAN_ID, MARK_OF_PROSPERITY_ID));
        MARKS.put(warsmith, List.of(MARK_OF_MAESTRO_ID, MARK_OF_GUILDSMAN_ID, MARK_OF_PROSPERITY_ID));

        MARKS.put(trooper, List.of(GwainsRecommendation));
        MARKS.put(warder, List.of(SteelrazorEvaluation));
        MARKS.put(berserker, List.of(OrkurusRecommendation));
        MARKS.put(maleSoulbreaker, List.of(SB_Certificate));
        MARKS.put(femaleSoulbreaker, List.of(SB_Certificate));
        MARKS.put(arbalester, List.of(KamaelInquisitorMark));
    }

    // ============================================
    static {
        HTMLS.put(elvenWizard, 15);
        HTMLS.put(oracle, 19);
        HTMLS.put(wizard, 23);
        HTMLS.put(cleric, 27);

        HTMLS.put(swordSinger, 40);
        HTMLS.put(paladin, 44);
        HTMLS.put(darkAvenger, 48);
        HTMLS.put(treasureHunter, 52);
        HTMLS.put(hawkeye, 56);
        HTMLS.put(plainsWalker, 60);
        HTMLS.put(silverRanger, 64);
        HTMLS.put(gladiator, 68);
        HTMLS.put(warlord, 72);

        //Church guild
        HTMLS.put(elder, 12);
        HTMLS.put(bishop, 16);
        HTMLS.put(prophet, 20);
        //Orc 1st
        HTMLS.put(orcRaider, 9);
        HTMLS.put(orcMonk, 13);
        HTMLS.put(orcShaman, 17);
        //Magic guild
        HTMLS.put(spellsinger, 18);
        HTMLS.put(elementalSummoner, 22);
        HTMLS.put(sorceror, 26);
        HTMLS.put(necromancer, 30);
        HTMLS.put(warlock, 34);

        HTMLS.put(elvenKnight, 18);
        HTMLS.put(elvenScout, 22);
        HTMLS.put(warrior, 26);
        HTMLS.put(knight, 30);
        HTMLS.put(rogue, 34);

        HTMLS.put(scavenger, 5);
        HTMLS.put(artisan, 5);
        // Dark guild 1st
        HTMLS.put(palusKnight, 15);
        HTMLS.put(assassin, 19);
        HTMLS.put(darkWizard, 23);
        HTMLS.put(shillienOracle, 27);

        // Dark guild 2nd
        HTMLS.put(shillienKnight, 26);
        HTMLS.put(bladedancer, 30);
        HTMLS.put(shillienElder, 34);
        HTMLS.put(abyssWalker, 38);
        HTMLS.put(phantomRanger, 42);
        HTMLS.put(spellhowler, 46);
        HTMLS.put(phantomSummoner, 50);

        // Orc guild
        HTMLS.put(tyrant, 16);
        HTMLS.put(destroyer, 20);
        HTMLS.put(overlord, 24);
        HTMLS.put(warcryer, 28);

        HTMLS.put(templeKnight, 37);
        //dwarf guild
        HTMLS.put(bountyHunter, 5);
        HTMLS.put(warsmith, 5);
        //Kamael 1st
        HTMLS.put(trooper, 1);
        HTMLS.put(warder, 1);
        // Kamael 2nd
        HTMLS.put(arbalester, 3);
        HTMLS.put(femaleSoulbreaker, 7);
        HTMLS.put(berserker, 12);
        HTMLS.put(maleSoulbreaker, 16);
    }

    private boolean haveAllMarks(ClassId newClassId) {
        return player.haveAllItems(MARKS.get(newClassId));
    }

    private void removeAllMarks(ClassId newClassId) {
        MARKS.get(newClassId).forEach(mark -> player.getInventory().destroyItemByItemId(mark, "Change occupation"));
    }

    private boolean notMatch() {
        if (player == null || npc == null)
            return true;
        if (!(npc instanceof VillageMasterInstance)) {
            show("I have nothing to say you", player, npc);
            return true;
        }
        return false;
    }

    private String getHtml(ClassId newClass) {
        int currentLevel = player.getLevel();
        int neededLevel = 20;
        int htmlIndex = HTMLS.get(newClass);
        if (newClass.occupation() == 1)
            neededLevel = 40;
        if (player.getClassId() == newClass.parent) {
            boolean haveItem = haveAllMarks(newClass);
            if (currentLevel < neededLevel && !haveItem)
                return htmlIndex + ".html";
            else if (currentLevel < neededLevel)
                return (htmlIndex + 1) + ".html";
            if (!haveItem)
                return (htmlIndex + 2) + ".html";
            removeAllMarks(newClass);
            player.setClassId(newClass, false, true);
            return (htmlIndex + 3) + ".html";
        }
        return "No Quest";
    }

    public void onChange30120(String[] args) {
        if (notMatch()) return;
        ClassId newClass = getById(args[0]);
        String htmltext = "No Quest";
        if (newClass == elder || newClass == bishop || newClass == prophet)
            htmltext = getHtml(newClass);
        npc.showChatWindow(player, "villagemaster/30120/" + htmltext);
    }

    public void onChange30500(String[] args) {
        if (notMatch()) return;
        ClassId newClass = ClassId.getById(args[0]);
        String htmltext = "No Quest";
        if (newClass.race == Race.orc && newClass.occupation() ==1) {
            htmltext = getHtml(newClass);
        }
        npc.showChatWindow(player, "villagemaster/30500/" + htmltext);
    }

    public void onChange30290(String[] args) {
        if (notMatch()) return;
        ClassId newClass = ClassId.getById(args[0]);
        String htmltext = "No Quest";
        if (newClass == palusKnight || newClass == assassin || newClass == darkWizard || newClass == shillienOracle) {
            htmltext = getHtml(newClass);
        }
        npc.showChatWindow(player, "villagemaster/30290/" + htmltext);
    }

    public void onChange30513(String[] args) {
        if (notMatch()) return;
        ClassId newClass = getById(args[0]);
        String htmltext = "No Quest";
        if (newClass == tyrant || newClass == destroyer || newClass == overlord || newClass == warcryer)
            htmltext = getHtml(newClass);
        npc.showChatWindow(player, "villagemaster/30513/" + htmltext);
    }

    public void onChange30474(String[] args) {
        if (notMatch()) return;
        ClassId newClass = ClassId.getById(args[0]);
        String htmltext = "No Quest";
        if (newClass == shillienKnight || newClass == bladedancer || newClass == shillienElder || newClass == abyssWalker
                || newClass == phantomRanger || newClass == spellhowler || newClass == phantomSummoner)
            htmltext = getHtml(newClass);
        npc.showChatWindow(player, "villagemaster/30474/" + htmltext);
    }

    public void onChange32145(String[] args) {
        if (notMatch()) return;
        ClassId newClass = ClassId.getById(args[0]);
        String htmltext = "04.htm";
        if (newClass == warder && player.getClassId() == newClass.parent)
            if (player.getLevel() >= 20 && player.haveItem(SteelrazorEvaluation)) {
                player.getInventory().destroyItemByItemId(SteelrazorEvaluation, "onChange32145");
                player.setClassId(newClass, false, true);
                htmltext = "03.htm";
            }
        npc.showChatWindow(player, "villagemaster/32145/" + htmltext);
    }

    public void onChange32146(String[] args) {
        if (notMatch()) return;
        ClassId newClass = ClassId.getById(args[0]);
        String htmltext = "04.htm";
        if (newClass == trooper)
            htmltext = getHtml(newClass);
        npc.showChatWindow(player, "villagemaster/32146/" + htmltext);
    }

    public void onChange32199(String[] args) {
        if (notMatch()) return;
        ClassId newClass = getById(args[0]);
        String htmltext = "02.htm";
        if (newClass.race == Race.kamael && newClass.occupation() ==2) {
            htmltext = getHtml(newClass);
        }
        npc.showChatWindow(player, "villagemaster/32199/" + htmltext);
    }

    public void onChange32213(String[] args) {
        onChange32199(args);
    }

    public void onChange32214(String[] args) {
        onChange32199(args);
    }

    public void onChange32217(String[] args) {
        onChange32199(args);
    }

    public void onChange32218(String[] args) {
        onChange32199(args);
    }

    public void onChange32221(String[] args) {
        onChange32199(args);
    }

    public void onChange32222(String[] args) {
        onChange32199(args);
    }

    public void onChange32205(String[] args) {
        onChange32199(args);
    }

    public void onChange32206(String[] args) {
        onChange32199(args);
    }

    public void onTalk32145() {
        if (notMatch()) return;

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == femaleSoldier)
            htmltext = "01.htm";
        else
            htmltext = "02.htm";

        npc.showChatWindow(player, "villagemaster/32145/" + htmltext);
    }

    public void onTalk32199() {
        if (notMatch()) return;

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == warder)
            htmltext = "01.htm";
        else if (classId == trooper)
            htmltext = "11.htm";
        else
            htmltext = "02.htm";

        npc.showChatWindow(player, "villagemaster/32199/" + htmltext);
    }

    public void onTalk32157() {
        String prefix = "head_blacksmith_mokabred";
        if (notMatch()) return;

        String htmltext;
        ClassId classId = player.getClassId();
        Race race = player.getRace();

        if (race != Race.dwarf)
            htmltext = "002.htm";
        else if (classId == dwarvenFighter)
            htmltext = "003f.htm";
        else if (classId.occupation() == 2)
            htmltext = "004.htm";
        else
            htmltext = "005.htm";

        npc.showChatWindow(player, "villagemaster/32157/" + prefix + htmltext);
    }

    public void onTalk32160() {
        String prefix = "grandmagister_devon";
        if (notMatch()) return;

        String htmltext;
        ClassId classId = player.getClassId();
        Race race = player.getRace();

        if (race != Race.darkelf)
            htmltext = "002.htm";
        else if (classId == darkFighter)
            htmltext = "003f.htm";
        else if (classId == darkMage)
            htmltext = "003m.htm";
        else if (classId.occupation() == 2)
            htmltext = "004.htm";
        else
            htmltext = "005.htm";

        npc.showChatWindow(player, "villagemaster/32160/" + prefix + htmltext);
    }

    public void onTalk32146() {
        if (notMatch()) return;
        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == maleSoldier)
            htmltext = "01.htm";
        else
            htmltext = "02.htm";

        npc.showChatWindow(player, "villagemaster/32146/" + htmltext);
    }

    public void onTalk32171() {
        String prefix = "warehouse_chief_hufran";
        if (notMatch()) return;

        String htmltext;
        ClassId classId = player.getClassId();
        Race race = player.getRace();

        if (race != Race.dwarf)
            htmltext = "002.htm";
        else if (classId == dwarvenFighter)
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

    public void onTalk32158() {
        String prefix = "warehouse_chief_fisser";
        if (notMatch()) return;

        String htmltext;
        ClassId classId = player.getClassId();
        Race race = player.getRace();

        if (race != Race.dwarf)
            htmltext = "002.htm";
        else if (classId == dwarvenFighter)
            htmltext = "003f.htm";
        else if (classId.occupation() == 2)
            htmltext = "004.htm";
        else
            htmltext = "005.htm";

        npc.showChatWindow(player, "villagemaster/32158/" + prefix + htmltext);
    }

    public void onTalk32214() {
        onTalk32199();
    }

    public void onTalk32217() {
        onTalk32199();
    }

    public void onTalk32218() {
        onTalk32199();
    }

    public void onTalk32221() {
        onTalk32199();
    }

    public void onTalk32222() {
        onTalk32199();
    }

    public void onTalk32205() {
        onTalk32199();
    }

    public void onTalk32206() {
        onTalk32199();
    }

    public void onTalk30500() {
        if (notMatch()) return;
        String htmltext;
        ClassId classId = player.getClassId();
        if (classId == orcFighter)
            htmltext = "01.htm";
        else if (classId == orcMage)
            htmltext = "06.htm";
        else if (classId == orcRaider || classId == orcMonk || classId == orcShaman)
            htmltext = "21.htm";
        else if (classId == destroyer || classId == tyrant || classId == overlord || classId == warcryer)
            htmltext = "22.htm";
        else
            htmltext = "23.htm";

        npc.showChatWindow(player, "villagemaster/30500/" + htmltext);
    }

    public void onTalk30290() {
        if (notMatch()) return;

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == darkFighter)
            htmltext = "01.htm";
        else if (classId == darkMage)
            htmltext = "08.htm";
        else if (classId == palusKnight || classId == assassin || classId == darkWizard || classId == shillienOracle)
            htmltext = "31.htm";
        else if (player.getRace() == Race.darkelf)
            htmltext = "32.htm";
        else
            htmltext = "33.htm";

        npc.showChatWindow(player, "villagemaster/30290/" + htmltext);
    }

    public void onTalk30513() {
        if (notMatch()) return;

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == orcMonk)
            htmltext = "01.htm";
        else if (classId == orcRaider)
            htmltext = "05.htm";
        else if (classId == orcShaman)
            htmltext = "09.htm";
        else if (classId == destroyer || classId == tyrant || classId == overlord || classId == warcryer)
            htmltext = "32.htm";
        else if (classId == orcFighter || classId == orcMage)
            htmltext = "33.htm";
        else
            htmltext = "34.htm";

        npc.showChatWindow(player, "villagemaster/30513/" + htmltext);
    }

    public void onTalk30474() {
        if (notMatch()) return;

        String htmltext;
        ClassId classId = player.getClassId();

        if (npc.getNpcId() == 30175) {
            if (classId == shillienOracle)
                htmltext = "08.htm";
            else if (classId == darkWizard)
                htmltext = "19.htm";
            else if (classId == spellhowler || classId == shillienElder || classId == phantomSummoner)
                htmltext = "54.htm";
            else if (classId == darkMage)
                htmltext = "55.htm";
            else
                htmltext = "56.htm";
        } else if (classId == palusKnight)
            htmltext = "01.htm";
        else if (classId == shillienOracle)
            htmltext = "08.htm";
        else if (classId == assassin)
            htmltext = "12.htm";
        else if (classId == darkWizard)
            htmltext = "19.htm";
        else if (classId == shillienKnight || classId == abyssWalker
                || classId == bladedancer || classId == phantomRanger)
            htmltext = "54.htm";
        else if (classId == spellhowler || classId == shillienElder || classId == phantomSummoner)
            htmltext = "54.htm";
        else if (classId == darkFighter || classId == darkMage)
            htmltext = "55.htm";
        else
            htmltext = "56.htm";

        npc.showChatWindow(player, "villagemaster/30474/" + htmltext);
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

    public void onChange30109(String[] args) {
        if (notMatch()) return;
        ClassId newClass = ClassId.getById(args[0]);
        String htmltext = "No Quest";
        if (newClass == templeKnight || newClass == swordSinger
                || newClass == paladin || newClass == darkAvenger
                || newClass == treasureHunter || newClass == hawkeye
                || newClass == plainsWalker || newClass == silverRanger
                || newClass == gladiator || newClass == warlord)
            htmltext = getHtml(newClass);
        npc.showChatWindow(player, "villagemaster/30109/" + htmltext);
    }

    public void onTalk30115() {
        if (notMatch()) return;

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == elvenWizard)
            htmltext = "01.htm";
        else if (classId == wizard)
            htmltext = "08.htm";
        else if (classId == sorceror || classId == necromancer || classId == warlock)
            htmltext = "39.htm";
        else if (classId == spellsinger || classId == elementalSummoner)
            htmltext = "39.htm";
        else if ((player.getRace() == Race.elf || player.getRace() == Race.human) && classId.isMage())
            htmltext = "38.htm";
        else
            htmltext = "40.htm";

        npc.showChatWindow(player, "villagemaster/30115/" + htmltext);
    }

    public void onChange30115(String[] args) {
        if (notMatch()) return;
        ClassId newClass = ClassId.getById(args[0]);
        String htmltext = "No Quest";
        if (newClass == spellsinger || newClass == elementalSummoner
                || newClass == sorceror || newClass == necromancer || newClass == warlock)
            htmltext = getHtml(newClass);

        npc.showChatWindow(player, "villagemaster/30115/" + htmltext);
    }

    public void onTalk30120() {
        if (notMatch()) return;

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == oracle)
            htmltext = "01.htm";
        else if (classId == cleric)
            htmltext = "05.htm";
        else if (classId == elder || classId == bishop || classId == prophet)
            htmltext = "25.htm";
        else if ((player.getRace() == Race.human || player.getRace() == Race.elf) && classId.isMage())
            htmltext = "24.htm";
        else
            htmltext = "26.htm";

        npc.showChatWindow(player, "villagemaster/30120/" + htmltext);
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

    public void onChange30037(String[] args) {
        if (notMatch()) return;

        ClassId newClass = getById(args[0]);
        String htmltext = "33.htm";
        if (newClass == elvenWizard || newClass == oracle || newClass == wizard || newClass == cleric)
            htmltext = getHtml(newClass);
        npc.showChatWindow(player, "villagemaster/30037/" + htmltext);
    }

    public void onChange30066(String[] args) {
        if (notMatch()) return;
        ClassId newclass = getById(args[0]);
        String htmltext = "No Quest";
        if (newclass == elvenKnight || newclass == elvenScout
                || newclass == warrior || newclass == knight || newclass == rogue) {
            htmltext = getHtml(newclass);
        }
        npc.showChatWindow(player, "villagemaster/30066/" + htmltext);
    }

    public void onChange30511(String[] args) {
        if (notMatch()) return;
        ClassId newClass = getById(args[0]);
        String htmltext = "No Quest";
        if (newClass == bountyHunter)
            htmltext = getHtml(newClass);
        npc.showChatWindow(player, "villagemaster/30511/" + htmltext);
    }

    public void onChange30498(String[] args) {
        if (notMatch()) return;
        ClassId newClass = ClassId.getById(args[0]);
        String htmltext = "No Quest";
        if (newClass == scavenger) htmltext = getHtml(newClass);
        npc.showChatWindow(player, "villagemaster/30498/" + htmltext);
    }

    public void onChange30499(String[] args) {
        if (notMatch()) return;
        ClassId newClass = ClassId.getById(args[0]);
        String htmltext = "No Quest";
        if (newClass == artisan) htmltext = getHtml(newClass);
        npc.showChatWindow(player, "villagemaster/30499/" + htmltext);
    }

    public void onChange30512(String[] args) {
        if (notMatch()) return;
        ClassId newClass = ClassId.getById(args[0]);
        String htmltext = "No Quest";
        if (newClass == warsmith)
            htmltext = getHtml(newClass);

        npc.showChatWindow(player, "villagemaster/30512/" + htmltext);
    }

    public void onChange30070(String[] args) {
        if (notMatch()) return;
        ClassId newClass = getById(args[0]);
        String htmltext = "No Quest";
        if (newClass == elvenWizard || newClass == oracle
                || newClass == wizard || newClass == cleric) {
            htmltext = getHtml(newClass);
        }
        npc.showChatWindow(player, "villagemaster/30070/" + htmltext);
    }

    public void onTalk30026() {
        if (notMatch()) return;

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == fighter)
            htmltext = "bitz003h.htm";
        else if (classId == warrior || classId == knight || classId == rogue)
            htmltext = "bitz004.htm";
        else if (classId == warlord || classId == paladin || classId == treasureHunter)
            htmltext = "bitz005.htm";
        else if (classId == gladiator || classId == darkAvenger || classId == hawkeye)
            htmltext = "bitz005.htm";
        else
            htmltext = "bitz002.htm";

        npc.showChatWindow(player, "villagemaster/30026/" + htmltext);
    }

    public void onTalk30031() {
        if (notMatch()) return;

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == wizard || classId == cleric)
            htmltext = "06.htm";
        else if (classId == sorceror || classId == necromancer
                || classId == warlock || classId == bishop || classId == prophet)
            htmltext = "07.htm";
        else if (classId == mage)
            htmltext = "01.htm";
        else
            // All other Races must be out
            htmltext = "08.htm";

        npc.showChatWindow(player, "villagemaster/30031/" + htmltext);
    }

    public void onTalk30037() {
        if (notMatch()) return;

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == elvenMage)
            htmltext = "01.htm";
        else if (classId == mage)
            htmltext = "08.htm";
        else if (classId == wizard || classId == cleric || classId == elvenWizard || classId == oracle)
            htmltext = "31.htm";
        else if (classId == sorceror || classId == necromancer
                || classId == bishop || classId == warlock || classId == prophet)
            htmltext = "32.htm";
        else if (classId == spellsinger || classId == elder || classId == elementalSummoner)
            htmltext = "32.htm";
        else
            htmltext = "33.htm";

        npc.showChatWindow(player, "villagemaster/30037/" + htmltext);
    }

    public void onTalk30066() {
        if (notMatch()) return;

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == elvenFighter)
            htmltext = "01.htm";
        else if (classId == fighter)
            htmltext = "08.htm";
        else if (classId == elvenKnight || classId == elvenScout
                || classId == warrior || classId == knight || classId == rogue)
            htmltext = "38.htm";
        else if (classId == templeKnight || classId == plainsWalker
                || classId == swordSinger || classId == silverRanger)
            htmltext = "39.htm";
        else if (classId == warlord || classId == paladin || classId == treasureHunter)
            htmltext = "39.htm";
        else if (classId == gladiator || classId == darkAvenger || classId == hawkeye)
            htmltext = "39.htm";
        else
            htmltext = "40.htm";

        npc.showChatWindow(player, "villagemaster/30066/" + htmltext);
    }

    public void onTalk30511() {
        if (notMatch()) return;

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == scavenger)
            htmltext = "01.htm";
        else if (classId == dwarvenFighter)
            htmltext = "09.htm";
        else if (classId == bountyHunter || classId == warsmith)
            htmltext = "10.htm";
        else
            htmltext = "11.htm";

        npc.showChatWindow(player, "villagemaster/30511/" + htmltext);
    }

    public void onTalk30070() {
        if (notMatch()) return;

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == elvenMage)
            htmltext = "01.htm";
        else if (classId == wizard || classId == cleric || classId == elvenWizard || classId == oracle)
            htmltext = "31.htm";
        else if (classId == sorceror || classId == necromancer
                || classId == bishop || classId == warlock
                || classId == prophet || classId == spellsinger
                || classId == elder || classId == elementalSummoner)
            htmltext = "32.htm";
        else if (classId == mage)
            htmltext = "08.htm";
        else
            htmltext = "33.htm";

        npc.showChatWindow(player, "villagemaster/30070/" + htmltext);
    }

    public void onTalk30154() {
        if (notMatch()) return;

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == elvenFighter)
            htmltext = "01.htm";
        else if (classId == elvenMage)
            htmltext = "02.htm";
        else if (classId == elvenWizard || classId == oracle || classId == elvenKnight || classId == elvenScout)
            htmltext = "12.htm";
        else if (player.getRace() == Race.elf)
            htmltext = "13.htm";
        else
            htmltext = "11.htm";

        npc.showChatWindow(player, "villagemaster/30154/" + htmltext);
    }

    public void onTalk30358() {
        if (notMatch()) return;

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == darkFighter)
            htmltext = "01.htm";
        else if (classId == darkMage)
            htmltext = "02.htm";
        else if (classId == darkWizard || classId == shillienOracle || classId == palusKnight || classId == assassin)
            htmltext = "12.htm";
        else if (player.getRace() == Race.darkelf)
            htmltext = "13.htm";
        else
            htmltext = "11.htm";

        npc.showChatWindow(player, "villagemaster/30358/" + htmltext);
    }

    public void onTalk30498() {
        if (notMatch()) return;

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == dwarvenFighter)
            htmltext = "01.htm";
        else if (classId == scavenger || classId == artisan)
            htmltext = "09.htm";
        else if (player.getRace() == Race.dwarf)
            htmltext = "10.htm";
        else
            htmltext = "11.htm";

        npc.showChatWindow(player, "villagemaster/30498/" + htmltext);
    }

    public void onTalk30499() {
        if (notMatch()) return;

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == dwarvenFighter)
            htmltext = "01.htm";
        else if (classId == scavenger || classId == artisan)
            htmltext = "09.htm";
        else if (player.getRace() == Race.dwarf)
            htmltext = "10.htm";
        else
            htmltext = "11.htm";

        npc.showChatWindow(player, "villagemaster/30499/" + htmltext);
    }

    public void onTalk30525() {
        if (notMatch()) return;

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == dwarvenFighter)
            htmltext = "01.htm";
        else if (classId == artisan)
            htmltext = "05.htm";
        else if (classId == warsmith)
            htmltext = "06.htm";
        else
            htmltext = "07.htm";

        npc.showChatWindow(player, "villagemaster/30525/" + htmltext);
    }

    public void onTalk30520() {
        if (notMatch()) return;

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == dwarvenFighter)
            htmltext = "01.htm";
        else if (classId == artisan || classId == scavenger)
            htmltext = "05.htm";
        else if (classId == warsmith || classId == bountyHunter)
            htmltext = "06.htm";
        else
            htmltext = "07.htm";

        npc.showChatWindow(player, "villagemaster/30520/" + htmltext);
    }

    public void onTalk30512() {
        if (notMatch()) return;

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == artisan)
            htmltext = "01.htm";
        else if (classId == dwarvenFighter)
            htmltext = "09.htm";
        else if (classId == warsmith || classId == bountyHunter)
            htmltext = "10.htm";
        else
            htmltext = "11.htm";

        npc.showChatWindow(player, "villagemaster/30512/" + htmltext);
    }

    private void onTalk30565() {
        if (notMatch()) return;

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == orcFighter)
            htmltext = "01.htm";
        else if (classId == orcRaider || classId == orcMonk || classId == orcShaman)
            htmltext = "09.htm";
        else if (classId == orcMage)
            htmltext = "16.htm";
        else if (player.getRace() == Race.orc)
            htmltext = "10.htm";
        else
            htmltext = "11.htm";

        npc.showChatWindow(player, "villagemaster/30565/" + htmltext);
    }

    public void onTalk30109() {
        if (notMatch()) return;

        String htmltext;
        ClassId classId = player.getClassId();

        if (classId == elvenKnight)
            htmltext = "01.htm";
        else if (classId == knight)
            htmltext = "08.htm";
        else if (classId == rogue)
            htmltext = "15.htm";
        else if (classId == elvenScout)
            htmltext = "22.htm";
        else if (classId == warrior)
            htmltext = "29.htm";
        else if (classId == elvenFighter || classId == fighter)
            htmltext = "76.htm";
        else if (classId == templeKnight || classId == plainsWalker || classId == swordSinger || classId == silverRanger)
            htmltext = "77.htm";
        else if (classId == warlord || classId == paladin || classId == treasureHunter)
            htmltext = "77.htm";
        else if (classId == gladiator || classId == darkAvenger || classId == hawkeye)
            htmltext = "77.htm";
        else
            htmltext = "78.htm";

        npc.showChatWindow(player, "villagemaster/30109/" + htmltext);
    }
}