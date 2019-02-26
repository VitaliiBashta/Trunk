package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _379_FantasyWine extends Quest {
    //NPC
    private final int HARLAN = 30074;
    //Mobs
    private final int Enku_Orc_Champion = 20291;
    private final int Enku_Orc_Shaman = 20292;
    //Quest Item
    private final int LEAF_OF_EUCALYPTUS = 5893;
    private final int STONE_OF_CHILL = 5894;
    //Item
    private final List<Integer> REWARD = List.of(5956, 5957, 5958);

    public _379_FantasyWine() {
        super(false);

        addStartNpc(HARLAN);

        addKillId(Enku_Orc_Champion, Enku_Orc_Shaman);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("hitsran_q0379_06.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();;
            st.playSound(SOUND_ACCEPT);
        } else if ("reward".equalsIgnoreCase(event)) {
            st.takeAllItems(LEAF_OF_EUCALYPTUS, STONE_OF_CHILL);
            int rand = Rnd.get(100);
            if (rand < 25) {
                st.giveItems(REWARD.get(0));
                htmltext = "hitsran_q0379_11.htm";
            } else if (rand < 50) {
                st.giveItems(REWARD.get(1));
                htmltext = "hitsran_q0379_12.htm";
            } else {
                st.giveItems(REWARD.get(2));
                htmltext = "hitsran_q0379_13.htm";
            }
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        } else if ("hitsran_q0379_05.htm".equalsIgnoreCase(event))
            st.exitCurrentQuest();
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int id = st.getState();
        int cond = 0;
        boolean haveLeafs = st.haveQuestItem(LEAF_OF_EUCALYPTUS, 80);
        boolean haveStones = st.haveQuestItem(STONE_OF_CHILL, 100);
        if (id != CREATED)
            cond = st.getCond();
        if (npcId == HARLAN)
            if (cond == 0) {
                if (st.player.getLevel() < 20) {
                    htmltext = "hitsran_q0379_01.htm";
                    st.exitCurrentQuest();
                } else
                    htmltext = "hitsran_q0379_02.htm";
            } else if (cond == 1) {
                if (!haveLeafs && !haveStones)
                    htmltext = "hitsran_q0379_07.htm";
                else if (haveLeafs && !haveStones)
                    htmltext = "hitsran_q0379_08.htm";
                else if (!haveLeafs)
                    htmltext = "hitsran_q0379_09.htm";
                else
                    htmltext = "hitsran_q0379_02.htm";
            } else if (cond == 2)
                if (haveLeafs && haveStones)
                    htmltext = "hitsran_q0379_10.htm";
                else {
                    st.setCond(1);
                    if (!haveLeafs && !haveStones)
                        htmltext = "hitsran_q0379_07.htm";
                    else if (haveLeafs)
                        htmltext = "hitsran_q0379_08.htm";
                    else
                        htmltext = "hitsran_q0379_09.htm";
                }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (st.getCond() == 1) {
            if (npcId == Enku_Orc_Champion && !st.haveQuestItem(LEAF_OF_EUCALYPTUS, 80))
                st.giveItems(LEAF_OF_EUCALYPTUS);
            else if (npcId == Enku_Orc_Shaman && !st.haveQuestItem(STONE_OF_CHILL, 100))
                st.giveItems(STONE_OF_CHILL);
            if (st.haveQuestItem(LEAF_OF_EUCALYPTUS, 80) && st.haveQuestItem(STONE_OF_CHILL, 100)) {
                st.playSound(SOUND_MIDDLE);
                st.setCond(2);
            } else
                st.playSound(SOUND_ITEMGET);
        }
    }
}