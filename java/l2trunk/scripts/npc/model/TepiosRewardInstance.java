package l2trunk.scripts.npc.model;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.scripts.instances.SufferingHallAttack;
import l2trunk.scripts.instances.SufferingHallDefence;
import l2trunk.scripts.quests._694_BreakThroughTheHallOfSuffering;
import l2trunk.scripts.quests._695_DefendtheHallofSuffering;

import java.util.List;

public final class TepiosRewardInstance extends NpcInstance {
    private static final int MARK_OF_KEUCEREUS_STAGE_1 = 13691;
    private static final int MARK_OF_KEUCEREUS_STAGE_2 = 13692;
    private static final int SOE = 736; // Scroll of Escape
    private static final int SUPPLIES1 = 13777; // Jewel Ornamented Duel Supplies
    private static final int SUPPLIES2 = 13778; // Mother-of-Pearl Ornamented Duel Supplies
    private static final int SUPPLIES3 = 13779; // Gold-Ornamented Duel Supplies
    private static final int SUPPLIES4 = 13780; // Silver-Ornamented Duel Supplies
    private static final int SUPPLIES5 = 13781; // Bronze-Ornamented Duel Supplies
    private static final List<Integer> SUPPLIES6_10 = List.of(13782, // Non-Ornamented Duel Supplies
            13783, // Weak-Looking Duel Supplies
            13784, // Sad-Looking Duel Supplies
            13785, // Poor-Looking Duel Supplies
            13786 // Worthless Duel Supplies
    );
    private boolean _gotReward = false;


    public TepiosRewardInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (command.equalsIgnoreCase("getreward")) {
            if (_gotReward)
                return;

            if (player.isInParty() && player.getParty().getLeader() != player) {
                showChatWindow(player, 1);
                return;
            }

            int time1 = 0;
            if (getReflection().getInstancedZoneId() == 115)
                time1 = ((SufferingHallAttack) getReflection()).timeSpent;
            else if (getReflection().getInstancedZoneId() == 116)
                time1 = ((SufferingHallDefence) getReflection()).timeSpent;
            int time = time1;
            getReflection().getPlayers()
                    .filter(p -> !p.haveItem(MARK_OF_KEUCEREUS_STAGE_1))
                    .filter(p -> ! p.haveItem(MARK_OF_KEUCEREUS_STAGE_2))
                    .forEach(p -> {
                        ItemFunctions.addItem(p, MARK_OF_KEUCEREUS_STAGE_1, 1, "TepiosRewardInstance");
                        ItemFunctions.addItem(p, SOE, 1, "TepiosRewardInstance");

                        if (time > 0) {
                            if (time <= 20 * 60 + 59)
                                ItemFunctions.addItem(p, SUPPLIES1, 1, "TepiosRewardInstance");
                                // 21 мин - 22 мин 59 сек
                            else if (time <= 22 * 60 + 59)
                                ItemFunctions.addItem(p, SUPPLIES2, 1, "TepiosRewardInstance");
                                // 23 мин - 24 мин 59 сек
                            else if (time <= 24 * 60 + 59)
                                ItemFunctions.addItem(p, SUPPLIES3, 1, "TepiosRewardInstance");
                                // 25 мин - 26 мин 59 сек
                            else if (time <= 26 * 60 + 59)
                                ItemFunctions.addItem(p, SUPPLIES4, 1, "TepiosRewardInstance");
                                // 27 мин - 28 мин 59 сек
                            else if (time <= 28 * 60 + 59)
                                ItemFunctions.addItem(p, SUPPLIES5, 1, "TepiosRewardInstance");
                                // 29 мин - 60 мин
                            else ItemFunctions.addItem(p, Rnd.get(SUPPLIES6_10), 1, "TepiosRewardInstance");
                        }
                        QuestState st = p.getQuestState(_694_BreakThroughTheHallOfSuffering.class);
                        QuestState qs2 = p.getQuestState(_695_DefendtheHallofSuffering.class);
                        if (st != null && getReflection().getInstancedZoneId() == 115)
                            st.exitCurrentQuest();
                        if (qs2 != null && getReflection().getInstancedZoneId() == 116)
                            qs2.exitCurrentQuest();
                    });
            _gotReward = true;
            showChatWindow(player, 2);
        } else
            super.onBypassFeedback(player, command);
    }

    @Override
    public String getHtmlPath(int npcId, int val, Player player) {
        String htmlpath;
        if (val == 0) {
            if (_gotReward)
                htmlpath = "default/32530-3.htm";
            else
                htmlpath = "default/32530.htm";
        } else
            return super.getHtmlPath(npcId, val, player);
        return htmlpath;
    }
}