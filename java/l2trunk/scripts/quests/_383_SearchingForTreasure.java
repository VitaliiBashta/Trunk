package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.utils.Location;

import java.util.ArrayList;
import java.util.List;


public final class _383_SearchingForTreasure extends Quest {
    // Items
    private static final int PIRATES_TREASURE_MAP = 5915;

    // NPC
    private static final int SHARK = 20314;
    private static final int ESPEN = 30890;
    private static final int PIRATES_CHEST = 31148;

    private class rewardInfo {
        final int id;
        final int count;
        final int chance;

        rewardInfo(int _id, int _count, int _chance) {
            id = _id;
            count = _count;
            chance = _chance;
        }
    }

    private static final List<rewardInfo> rewards = new ArrayList<>();

    public _383_SearchingForTreasure() {
        super(false);

        addStartNpc(ESPEN);
        addTalkId(PIRATES_CHEST);
        addQuestItem(PIRATES_TREASURE_MAP);

        rewards.add(new rewardInfo(952, 1, 8));
        rewards.add(new rewardInfo(956, 1, 15));
        rewards.add(new rewardInfo(1337, 1, 130));
        rewards.add(new rewardInfo(1338, 2, 150));
        rewards.add(new rewardInfo(2450, 1, 2));
        rewards.add(new rewardInfo(2451, 1, 2));
        rewards.add(new rewardInfo(3452, 1, 140));
        rewards.add(new rewardInfo(3455, 1, 120));
        rewards.add(new rewardInfo(4408, 1, 220));
        rewards.add(new rewardInfo(4409, 1, 220));
        rewards.add(new rewardInfo(4418, 1, 220));
        rewards.add(new rewardInfo(4419, 1, 220));
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("30890-03.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.setState(STARTED);
        } else if ("30890-07.htm".equalsIgnoreCase(event)) {
            if (st.haveQuestItem(PIRATES_TREASURE_MAP) ) {
                st.setCond(2);
                st.takeItems(PIRATES_TREASURE_MAP, 1);
                st.addSpawn(PIRATES_CHEST, Location.of(106583, 197747, -4209),0, 900000);
                st.addSpawn(SHARK, Location.of(106570, 197740, -4209),0, 900000);
                st.addSpawn(SHARK, Location.of(106580, 197747, -4209),0, 900000);
                st.addSpawn(SHARK, Location.of(106590, 197743, -4209),0, 900000);
                st.playSound(SOUND_ACCEPT);
            } else {
                htmltext = "You don't have required items";
                st.exitCurrentQuest(true);
            }
        } else if ("30890-02b.htm".equalsIgnoreCase(event)) {
            if (st.haveQuestItem(PIRATES_TREASURE_MAP) ) {
                st.giveItems(ADENA_ID, 1000);
                st.playSound("ItemSound.quest_finish");
            } else
                htmltext = "You don't have required items";
            st.exitCurrentQuest(true);
        } else if ("31148-02.htm".equalsIgnoreCase(event))
            if (st.haveQuestItem(1661) ) {
                st.takeItems(1661, 1);
                st.giveItems(ADENA_ID, 500 + Rnd.get(5) * 300);
                int count = 0;
                while (count < 1)
                    for (rewardInfo reward : rewards) {
                        int id = reward.id;
                        int qty = reward.count;
                        int chance = reward.chance;
                        if (Rnd.get(1000) < chance && count < 2) {
                            st.giveItems(id, Rnd.get(qty) + 1);
                            count += 1;
                        }
                        if (count < 2)
                            for (int i = 4481; i <= 4505; i++)
                                if (Rnd.get(500) == 1 && count < 2) {
                                    st.giveItems(i);
                                    count += 1;
                                }
                    }
                st.playSound("ItemSound.quest_finish");
                st.exitCurrentQuest(true);
            } else
                htmltext = "31148-03.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        if (st.getState() == CREATED) {
            if (st.player.getLevel() >= 42) {
                if (st.haveQuestItem(PIRATES_TREASURE_MAP) )
                    htmltext = "30890-01.htm";
                else {
                    htmltext = "30890-00.htm";
                    st.exitCurrentQuest(true);
                }
            } else {
                htmltext = "30890-01a.htm";
                st.exitCurrentQuest(true);
            }
        } else if (npcId == ESPEN)
            htmltext = "30890-03a.htm";
        else if (npcId == PIRATES_CHEST && st.getCond() == 2 && st.getState() == STARTED)
            htmltext = "31148-01.htm";
        return htmltext;
    }
}