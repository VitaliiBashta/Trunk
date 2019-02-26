package l2trunk.scripts.quests;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;

import java.util.Map;

public final class _004_LongLivethePaagrioLord extends Quest {
    private final int HONEY_KHANDAR = 1541;
    private final int BEAR_FUR_CLOAK = 1542;
    private final int BLOODY_AXE = 1543;
    private final int ANCESTOR_SKULL = 1544;
    private final int SPIDER_DUST = 1545;
    private final int DEEP_SEA_ORB = 1546;

    private final Map<Integer, Integer> NPC_GIFTS = Map.of(
            30585, BEAR_FUR_CLOAK,
            30566, HONEY_KHANDAR,
            30562, BLOODY_AXE,
            30560, ANCESTOR_SKULL,
            30559, SPIDER_DUST,
            30587, DEEP_SEA_ORB);

    public _004_LongLivethePaagrioLord() {
        super(false);
        addStartNpc(30578);

        addTalkId(30559, 30560, 30562, 30566, 30578, 30585, 30587);

        addQuestItem(SPIDER_DUST, ANCESTOR_SKULL, BLOODY_AXE, HONEY_KHANDAR, BEAR_FUR_CLOAK, DEEP_SEA_ORB);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("30578-03.htm")) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == 30578) {
            if (cond == 0) {
                if (st.player.getRace() != Race.orc) {
                    htmltext = "30578-00.htm";
                    st.exitCurrentQuest();
                } else if (st.player.getLevel() >= 2)
                    htmltext = "30578-02.htm";
                else {
                    htmltext = "30578-01.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "30578-04.htm";
            else if (cond == 2) {
                htmltext = "30578-06.htm";
                    st.takeItems(NPC_GIFTS.values());
                st.giveItems(4);
                st.giveItems(ADENA_ID, (int) ((Config.RATE_QUESTS_REWARD - 1) * 590 + 1850 * Config.RATE_QUESTS_REWARD), false); // T2
                st.player.addExpAndSp(4254, 335);
                if (st.player.getClassId().occupation() == 0 && !st.player.isVarSet("ng1"))
                    st.player.sendPacket(new ExShowScreenMessage("  Delivery duty complete.\nGo find the Newbie Guide."));
                st.playSound(SOUND_FINISH);
                st.finish();
            }
        } else if (cond == 1)
            if (NPC_GIFTS.containsKey(npcId)) {
                int item = NPC_GIFTS.get(npcId);
                if (st.getQuestItemsCount(item) > 0)
                    htmltext = npc + "-02.htm";
                else {
                    st.giveItems(item);
                    htmltext = npc + "-01.htm";
                    int count = (int) NPC_GIFTS.values().stream()
                            .mapToLong(st::getQuestItemsCount)
                            .sum();
                    if (count == 6) {
                        st.setCond(2);
                        st.playSound(SOUND_MIDDLE);
                    } else
                        st.playSound(SOUND_ITEMGET);
                }
                return htmltext;
            }
        return htmltext;
    }
}