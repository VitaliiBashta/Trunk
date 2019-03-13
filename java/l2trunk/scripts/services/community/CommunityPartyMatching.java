package l2trunk.scripts.services.community;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.handler.bbs.CommunityBoardManager;
import l2trunk.gameserver.handler.bbs.ICommunityBoardHandler;
import l2trunk.gameserver.listener.actor.player.OnAnswerListener;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Party;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SubClass;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.base.Experience;
import l2trunk.gameserver.network.serverpackets.ConfirmDlg;
import l2trunk.gameserver.network.serverpackets.JoinParty;
import l2trunk.gameserver.network.serverpackets.ShowBoard;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.BbsUtil;

import java.util.*;
import java.util.stream.Collectors;

import static l2trunk.commons.lang.NumberUtils.toInt;
import static l2trunk.gameserver.model.base.ClassId.*;

public final class CommunityPartyMatching extends Functions implements ScriptFile, ICommunityBoardHandler {
    private static final int CHECKED_COUNT = 9; // last checked + 1
    private static final int MAX_PER_PAGE = 14;

    private static String canJoinParty(Player player) {
        String name = player.getName();
        if (player.isGM())
            return "Don't invite GMs...";
        if (player.getParty() != null)
            return name + " has already found a party.";
        if (player.isInOlympiadMode())
            return name + " is currently fighting in the Olympiad.";
        if (player.isInObserverMode())
            return name + " is currently observing an Olympiad Match.";
        if (player.getCursedWeaponEquippedId() != 0)
            return name + " cannot join the party because he is holding a cursed weapon.";
        if (!player.isPartyMatchingVisible())
            return name + " doesn't want to join any party.";
        if (player.getPrivateStoreType() > 0)
            return name + " cannot join the party because he is currently having a private store.";
        return "";
    }

    @Override
    public List<String> getBypassCommands() {
        return List.of("_partymatching");
    }

    @Override
    public void onBypassCommand(Player player, String bypass) {
        // Bypass: bbslink_class_sort_asc_charpage_char_classpage sometimes _invClassId

        StringTokenizer st = new StringTokenizer(bypass, "_");
        String mainStringToken = st.nextToken(); // bbslink
        if (mainStringToken.equals("partymatching")) {
            if (!st.hasMoreTokens())
                showMainPage(player, 0, 0, 0, 0, 0);
            else {
                int classesSortType = toInt(st.nextToken());
                int sortType = toInt(st.nextToken());
                int asc = toInt(st.nextToken(), 0);
                int page = toInt(st.nextToken());
                int charObjId = toInt(st.nextToken());
                showMainPage(player, classesSortType, sortType, asc, page, charObjId);

                if (st.hasMoreTokens()) {
                    int nextNumber = toInt(st.nextToken());

                    if (nextNumber == -1) // Show/Hide on list
                    {
                        player.setPartyMatchingVisible();
                        if (player.isPartyMatchingVisible())
                            player.sendMessage("You are now visible on Party Matching list!");
                        else
                            player.sendMessage("You are NO LONGER visible on Party Matching list!");
                        showMainPage(player, classesSortType, sortType, asc, page, charObjId);
                    } else { // Invite to party
                        Player invited = GameObjectsStorage.getPlayer(charObjId);
                        if (invited != null && player != invited && invited.getParty() == null) {
                            String partyMsg = canJoinParty(invited);
                            if (partyMsg.isEmpty()) {
                                ConfirmDlg packet = new ConfirmDlg(SystemMsg.S1, 60000).addString("Do you want to join " + player.getName() + "'s party?");
                                invited.ask(packet, new InviteAnswer(invited, player));
                                player.sendMessage("Invitation has been sent!");
                            } else
                                player.sendMessage(partyMsg);
                        }
                    }
                }
            }
        }
    }

    private void showMainPage(Player player, int classesSortType, int sortType, int asc, int page, int charObjId) {
        String html = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "bbs_partymatching.htm", player);
        html = html.replace("%characters%", getCharacters(player, sortType, asc, classesSortType, page, charObjId));
        html = html.replace("%visible%", player.isPartyMatchingVisible() ? "Hide from list" : "Show on list");
        html = replace(html, classesSortType, sortType, asc, page, charObjId);

        for (int i = 0; i < CHECKED_COUNT; i++)
            html = html.replace("%checked" + i + "%", getChecked(i, classesSortType));

        html = BbsUtil.htmlAll(html, player);
        ShowBoard.separateAndSend(html, player);
    }

    private String replace(String text, int classesSortType, int sortType, int asc, int page, int charObjId) {
        text = text.replace("%class%", String.valueOf(classesSortType));
        text = text.replace("%sort%", String.valueOf(sortType));
        text = text.replace("%asc%", String.valueOf(asc));
        text = text.replace("%asc2%", String.valueOf(asc == 0 ? 1 : 0));
        text = text.replace("%page%", String.valueOf(page));
        text = text.replace("%char%", String.valueOf(charObjId));
        return text;
    }

    private String getCharacters(Player visitor, int charSort, int asc, int classSort, int page, int charToView) {
        String html = "";
        List<Player> allPlayers = getPlayerList(visitor, charSort, asc, classSort);
        int badCharacters = 0;
        boolean isThereNextPage = true;

        for (int i = MAX_PER_PAGE * page; i < (MAX_PER_PAGE + badCharacters + page * MAX_PER_PAGE); i++) {
            if (allPlayers.size() <= i) {
                isThereNextPage = false;
                break;
            }
            Player player = allPlayers.get(i);

            if (!isClassTestPassed(player, classSort)) {
                badCharacters++;
                continue;
            }

            html += "<table bgcolor=" + getLineColor(i) + " width=760 border=0 cellpadding=0 cellspacing=0><tr>";
            html += "<td width=180><center><font color=" + getTextColor(i) + ">" + player.getName() + "</font></center></td>";
            html += "<td width=130><center><font color=" + getTextColor(i) + ">" + player.getClassId().name + "</font></center></td>";
            html += "<td width=75><center><font color=" + getTextColor(i) + ">" + player.getLevel() + "</font></center></td>";
            html += "<td width=75><center><font color=" + getTextColor(i) + ">" + (player.getBaseClassId() == player.getActiveClassId() ? "Yes" : "No") + "</font></center></td>";
            html += "<td width=180><center><font color=" + getTextColor(i) + ">" + (player.getClan() != null ? player.getClan().getName() : "<br>") + "</font></center></td>";
            if (!player.equals(visitor) || player.getParty() != null)
                html += "<td width=120><center><button value=\"Invite\" action=\"bypass _partymatching_%class%_%sort%_%asc%_%page%_" + player.objectId() + "_0\" width=70 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"><center></td>";
            else
                html += "<td width=120><br></td>";

            html += "</tr></table>";
        }
        html += "<center><table><tr>";
        if (page > 0)
            html += "<td><button value=\"Prev\" action=\"bypass _partymatching_%class%_%sort%_%asc%_" + (page - 1) + "_%char%\" width=80 height=18 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></td>";
        if (isThereNextPage)
            html += "<td><button value=\"Next\" action=\"bypass _partymatching_%class%_%sort%_%asc%_" + (page + 1) + "_%char%\" width=80 height=18 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></td>";
        html += "</tr></table></center>";
        return html;
    }

    private boolean isClassTestPassed(Player player, int classSortType) {
        for (ClassId clazz : getNeededClasses(classSortType)) {
            for (SubClass sub : player.getSubClasses().values()) {
                if (clazz == sub.getClassId())
                    return true;
            }
        }
        return false;
    }

    private List<Player> getPlayerList(Player player, int sortType, int asc, int classSortType) {

        List<Player> allPlayers = new ArrayList<>();
        if (classSortType == 8) { // Party
            if (player.getParty() == null)
                allPlayers.add(player);
            else {
                allPlayers = player.getParty().getMembersStream().collect(Collectors.toList());
            }
        } else {
            allPlayers =GameObjectsStorage.getAllPlayersStream()
                    .filter(p -> canJoinParty(p).isEmpty())
                    .filter(p -> isClassTestPassed(p, classSortType))
                    .collect(Collectors.toList());
        }

        allPlayers.sort(new CharComparator(sortType, classSortType, asc));
        return allPlayers;
    }

    private int getMaxLevel(Player player, int classSortType) {
        List<ClassId> group = getNeededClasses(classSortType);
        int maxLevel = 0;

        for (SubClass sub : player.getSubClasses().values()) {
            if (group.contains(sub.getClassId())) {
                if (Experience.getLevel(sub.getExp()) > maxLevel)
                    maxLevel = Experience.getLevel(sub.getExp());
            }
        }
        return maxLevel;
    }

    private int getUnlocksSize(Player player) {
        return player.getSubClasses().size();
    }

    private List<ClassId> getNeededClasses(int type) {
        switch (type) {
            case 1: //Buffers
                return List.of(inspector, judicator, oracle, orcShaman, prophet, warcryer, overlord, shillienElder, shillienSaint, hierophant, evaSaint, shillienSaint, dominator, doomcryer);
            case 2: //BD
                return List.of(bladedancer, spectralDancer);
            case 3: //SWS
                return List.of(swordSinger, swordMuse);
            case 4: //Healers
                return List.of(bishop, shillienElder, cardinal, evaSaint, shillienSaint);
            case 5: //Tanks
                return List.of(knight, darkAvenger, paladin, palusKnight, shillienKnight, shillienTemplar, phoenixKnight, hellKnight, evaTemplar, shillienTemplar);
            case 6: //Mage DD
                return List.of(elvenMage, mage, orcShaman, darkMage, wizard, warcryer, overlord, spellsinger, spellhowler, necromancer, sorceror, archmage, soultaker, arcanaLord, mysticMuse, elementalMaster, stormScreamer, spectralMaster, dominator, doomcryer);
            case 7: //Fighter DD
                return List.of(inspector, judicator, abyssWalker, swordSinger, swordMuse, assassin, berserker, bountyHunter, artisan, arbalester, darkFighter, destroyer, doombringer, elvenFighter, darkFighter, dreadnought, warlord, warsmith, warrior, femaleSoldier, bladedancer, spectralDancer, femaleSoulbreaker, femaleSoulhound, maleSoldier, maleSoulbreaker, maleSoulhound, maestro, hawkeye, treasureHunter, titan, trickster, trooper, tyrant, gladiator, duelist, phantomRanger, plainsWalker, rogue, silverRanger, orcRaider, orcFighter, orcMonk, dreadnought, duelist, adventurer, sagittarius, windRider, moonlightSentinel, ghostHunter, ghostSentinel, titan, grandKhauatari, fortuneSeeker);
        }
        return List.of(ClassId.values());
    }

    private String getChecked(int i, int classSortType) {
        if (classSortType == i)
            return "L2UI.Checkbox_checked";

        return "L2UI.CheckBox";
    }

    private String getLineColor(int i) {
        if (i % 2 == 0)
            return "18191e";

        return "22181a";
    }

    private String getTextColor(int i) {
        if (i % 2 == 0)
            return "8f3d3f";

        return "327b39";
    }

    @Override
    public void onLoad() {
        CommunityBoardManager.registerHandler(this);
    }

    @Override
    public void onReload() {
        CommunityBoardManager.removeHandler(this);
    }

    public static class InviteAnswer implements OnAnswerListener {
        private final Player _invited;
        private final Player _inviter;

        InviteAnswer(Player invited, Player inviter) {
            _invited = invited;
            _inviter = inviter;
        }

        @Override
        public void sayYes() {
            String inviteMsg = canJoinParty(_invited);
            if (!inviteMsg.isEmpty()) {
                _inviter.sendMessage(inviteMsg);
                return;
            }
            // Joining Party
            Party party = _inviter.getParty();
            if (party == null)
                _inviter.setParty(party = new Party(_inviter, 0));
            _invited.joinParty(party);
            _invited.sendPacket(JoinParty.SUCCESS);
            _inviter.sendPacket(JoinParty.SUCCESS);
        }

        @Override
        public void sayNo() {
            _inviter.sendMessage(_invited.getName() + " declined your party request!");
        }
    }

    private class CharComparator implements Comparator<Player> {
        final int type;
        final int classType;
        final int asc;

        private CharComparator(int sortType, int classType, int asc) {
            type = sortType;
            this.classType = classType;
            this.asc = asc;
        }

        @Override
        public int compare(Player o1, Player o2) {
            if (asc == 1) {
                Player temp = o1;
                o1 = o2;
                o2 = temp;
            }
            if (type == 0) // Name
                return o1.getName().compareTo(o2.getName());
            if (type == 1) // lvl
                return Integer.compare(getMaxLevel(o2, classType), getMaxLevel(o1, classType));
            if (type == 2) // unlocks
                return Integer.compare(getUnlocksSize(o2), getUnlocksSize(o1));
            return 0;
        }
    }
}
