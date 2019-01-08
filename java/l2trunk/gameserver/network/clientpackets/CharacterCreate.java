package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.dao.CharacterDAO;
import l2trunk.gameserver.data.xml.holder.SkillAcquireHolder;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.instancemanager.QuestManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SkillLearn;
import l2trunk.gameserver.model.actor.instances.player.ShortCut;
import l2trunk.gameserver.model.base.AcquireType;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.base.Experience;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.network.GameClient;
import l2trunk.gameserver.network.serverpackets.CharacterCreateFail;
import l2trunk.gameserver.network.serverpackets.CharacterCreateSuccess;
import l2trunk.gameserver.network.serverpackets.CharacterSelectionInfo;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.templates.PlayerTemplate;
import l2trunk.gameserver.templates.item.CreateItem;
import l2trunk.gameserver.templates.item.ItemTemplate;
import l2trunk.gameserver.utils.ItemFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public final class CharacterCreate extends L2GameClientPacket {
    private static final Logger LOG = LoggerFactory.getLogger(CharacterCreate.class);
    private static final String[] ALLOWED_LETTERS = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "a", "s", "d", "f", "g", "h", "j", "k", "l", "z", "x", "c", "v", "b", "n", "m"};
    // cSdddddddddddd
    private String _name;
    private int _sex;
    private int _classId;
    private int _hairStyle;
    private int _hairColor;
    private int _face;

    public static boolean checkName(String name) {
        char[] chars = name.toCharArray();
        for (char c : chars) {
            String letter = String.valueOf(c);
            boolean foundLetter = false;
            for (String allowed : ALLOWED_LETTERS)
                if (letter.equalsIgnoreCase(allowed))
                    foundLetter = true;
            if (!foundLetter)
                return false;
        }
        return true;
    }

    private static void startTutorialQuest(Player player) {
        Quest q = QuestManager.getQuest(255);
        if (q != null)
            q.newQuestState(player, Quest.CREATED);
    }

    @Override
    protected void readImpl() {
        _name = readS();
        readD(); // race
        _sex = readD();
        _classId = readD();
        readD(); // int
        readD(); // str
        readD(); // con
        readD(); // men
        readD(); // dex
        readD(); // wit
        _hairStyle = readD();
        _hairColor = readD();
        _face = readD();
    }

    @Override
    protected void runImpl() {
        for (ClassId cid : ClassId.VALUES)
            if (cid.getId() == _classId && cid.getLevel() != 1)
                return;
        if (CharacterDAO.accountCharNumber(getClient().getLogin()) >= 8) {
            sendPacket(CharacterCreateFail.REASON_TOO_MANY_CHARACTERS);
            return;
        }
        if (!checkName(_name) || _name.length() > 16) {
            sendPacket(CharacterCreateFail.REASON_16_ENG_CHARS);
            return;
        } else if (CharacterDAO.getObjectIdByName(_name) > 0) {
            sendPacket(CharacterCreateFail.REASON_NAME_ALREADY_EXISTS);
            return;
        }

        if ((_face > 2) || (_face < 0)) {
            return;
        }
        if ((_hairStyle < 0) || ((_sex == 0) && (_hairStyle > 4)) || ((_sex != 0) && (_hairStyle > 6))) {
            return;
        }
        if ((_hairColor > 3) || (_hairColor < 0)) {
            return;
        }

        Player newChar = Player.create(_classId, _sex, getClient().getLogin(), _name, _hairStyle, _hairColor, _face);
        if (newChar == null)
            return;

        sendPacket(CharacterCreateSuccess.STATIC);

        initNewChar(getClient(), newChar);
    }

    private void initNewChar(GameClient client, Player newChar) {
        PlayerTemplate template = newChar.getTemplate();

        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            Player.restoreCharSubClasses(newChar, con);
        } catch (SQLException e) {
            LOG.error("Error while restoring Subclasses on initNewChar ", e);
        }

        if (Config.STARTING_ADENA > 0) {
            newChar.addAdena(Config.STARTING_ADENA, "Starting Adena");
        }

        if (Config.STARTING_LVL != 0)
            newChar.addExpAndSp(Experience.LEVEL[Config.STARTING_LVL] - newChar.getExp(), 0, 0, 0, false, false);

        if (Config.SPAWN_CHAR)
            newChar.teleToLocation(Config.SPAWN_X, Config.SPAWN_Y, Config.SPAWN_Z);
        else
            newChar.setLoc(template.spawnLoc);

        if (Config.CHAR_TITLE)
            newChar.setTitle(Config.ADD_CHAR_TITLE);
        else
            newChar.setTitle("");


        for (CreateItem i : template.getItems()) {
            ItemInstance item = ItemFunctions.createItem(i.getItemId());
            newChar.getInventory().addItem(item, "New Char Item");

            if (i.getShortcut() - 1 > -1) // tutorial book
                newChar.registerShortCut(new ShortCut(Math.min(i.getShortcut() - 1, 11), 0, ShortCut.TYPE_ITEM, item.getObjectId(), -1, 1));

            if (i.isEquipable() && item.isEquipable() && (newChar.getActiveWeaponItem() == null || item.getTemplate().getType2() != ItemTemplate.TYPE2_WEAPON))
                newChar.getInventory().equipItem(item);
        }

        ClassId nclassId = ClassId.VALUES.get(_classId);
        if (Config.ALLOW_START_ITEMS) {
            if (nclassId.isMage()) {
                for (int i = 0; i < Config.START_ITEMS_MAGE.size(); i++) {
                    ItemInstance item = ItemFunctions.createItem(Config.START_ITEMS_MAGE.get(i));
                    item.setCount(Config.START_ITEMS_MAGE_COUNT.get(i));
                    newChar.getInventory().addItem(item, "New Char Item");
                    if (item.isEquipable())
                        newChar.getInventory().equipItem(item);
                }
            } else {
                for (int i = 0; i < Config.START_ITEMS_FITHER.size(); i++) {
                    ItemInstance item = ItemFunctions.createItem(Config.START_ITEMS_FITHER.get(i));
                    item.setCount(Config.START_ITEMS_FITHER_COUNT.get(i));
                    newChar.getInventory().addItem(item, "New Char Item");
                    if (item.isEquipable())
                        newChar.getInventory().equipItem(item);
                }
            }
        }
        // Adventurer's Scroll of Escape
        ItemInstance item = ItemFunctions.createItem(10650);
        item.setCount(5);
        newChar.getInventory().addItem(item, "New Char Item");

        // Scroll of Escape: Town Of Giran
        item = ItemFunctions.createItem(7126);
        item.setCount(10);
        newChar.getInventory().addItem(item, "New Char Item");

        for (SkillLearn skill : SkillAcquireHolder.getAvailableSkills(newChar, AcquireType.NORMAL))
            newChar.addSkill(skill.getId(), skill.getLevel(), true);

        if (newChar.getSkillLevel(1001) > 0) // Soul Cry
            newChar.registerShortCut(new ShortCut(1, 0, ShortCut.TYPE_SKILL, 1001, 1, 1));
        if (newChar.getSkillLevel(1177) > 0) // Wind Strike
            newChar.registerShortCut(new ShortCut(1, 0, ShortCut.TYPE_SKILL, 1177, 1, 1));
        if (newChar.getSkillLevel(1216) > 0) // Self Heal
            newChar.registerShortCut(new ShortCut(2, 0, ShortCut.TYPE_SKILL, 1216, 1, 1));

        // add attack, take, sit shortcut
        newChar.registerShortCut(new ShortCut(0, 0, ShortCut.TYPE_ACTION, 2, -1, 1));
        newChar.registerShortCut(new ShortCut(3, 0, ShortCut.TYPE_ACTION, 5, -1, 1));
        newChar.registerShortCut(new ShortCut(10, 0, ShortCut.TYPE_ACTION, 0, -1, 1));
        // I understood a panel display. NC Soft 10-11 panel made (by VISTALL)
        // fly transform
        newChar.registerShortCut(new ShortCut(0, ShortCut.PAGE_FLY_TRANSFORM, ShortCut.TYPE_SKILL, 911, 1, 1));
        newChar.registerShortCut(new ShortCut(3, ShortCut.PAGE_FLY_TRANSFORM, ShortCut.TYPE_SKILL, 884, 1, 1));
        newChar.registerShortCut(new ShortCut(4, ShortCut.PAGE_FLY_TRANSFORM, ShortCut.TYPE_SKILL, 885, 1, 1));
        // air ship
        newChar.registerShortCut(new ShortCut(0, ShortCut.PAGE_AIRSHIP, ShortCut.TYPE_ACTION, 70, 0, 1));

        startTutorialQuest(newChar);

        newChar.setFullHpMp();
        newChar.setCurrentCp(0); // retail
        newChar.setOnlineStatus(false);

        newChar.store(false);
        newChar.getInventory().store();
        newChar.deleteMe();

        client.setCharSelection(CharacterSelectionInfo.loadCharacterSelectInfo(client.getLogin()));
    }
}