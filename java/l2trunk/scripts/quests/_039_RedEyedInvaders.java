package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _039_RedEyedInvaders extends Quest {
    private final int BBN = 7178;
    private final int RBN = 7179;
    private final int IP = 7180;
    private final int GML = 7181;
    private final List<Integer> REW = List.of(6521, 6529, 6535);

    public _039_RedEyedInvaders() {
        addStartNpc(30334);

        addTalkId(30332);

        addKillId(20919, 20920, 20921, 20925);

        addQuestItem(BBN, IP, RBN, GML);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        switch (event) {
            case "guard_babenco_q0039_0104.htm":
                st.setCond(1);
                st.start();
                st.playSound(SOUND_ACCEPT);
                break;
            case "captain_bathia_q0039_0201.htm":
                st.setCond(2);
                st.playSound(SOUND_ACCEPT);
                break;
            case "captain_bathia_q0039_0301.htm":
                if (st.haveQuestItem(BBN, 100) && st.haveQuestItem(RBN, 100)) {
                    st.setCond(4);
                    st.takeAllItems(BBN, RBN);
                    st.playSound(SOUND_ACCEPT);
                } else
                    htmltext = "captain_bathia_q0039_0203.htm";
                break;
            case "captain_bathia_q0039_0401.htm":
                if (st.getQuestItemsCount(IP) == 30 && st.getQuestItemsCount(GML) == 30) {
                    st.takeAllItems(IP, GML);
                    st.giveItems(REW.get(0), 60);
                    st.giveItems(REW.get(1));
                    st.giveItems(REW.get(2), 500);
                    st.addExpAndSp(62366, 2783);
                    st.setCond(0);
                    st.playSound(SOUND_FINISH);
                    st.finish();
                } else
                    htmltext = "captain_bathia_q0039_0304.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == 30334) {
            if (cond == 0) {
                if (st.player.getLevel() < 20) {
                    htmltext = "guard_babenco_q0039_0102.htm";
                    st.exitCurrentQuest();
                } else htmltext = "guard_babenco_q0039_0101.htm";
            } else if (cond == 1)
                htmltext = "guard_babenco_q0039_0105.htm";
        } else if (npcId == 30332)
            if (cond == 1)
                htmltext = "captain_bathia_q0039_0101.htm";
            else if (cond == 2 && (st.getQuestItemsCount(BBN) < 100 || st.getQuestItemsCount(RBN) < 100))
                htmltext = "captain_bathia_q0039_0203.htm";
            else if (cond == 3 && st.getQuestItemsCount(BBN) == 100 && st.getQuestItemsCount(RBN) == 100)
                htmltext = "captain_bathia_q0039_0202.htm";
            else if (cond == 4 && (st.getQuestItemsCount(IP) < 30 || st.getQuestItemsCount(GML) < 30))
                htmltext = "captain_bathia_q0039_0304.htm";
            else if (cond == 5 && st.getQuestItemsCount(IP) == 30 && st.getQuestItemsCount(GML) == 30)
                htmltext = "captain_bathia_q0039_0303.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 2) {
            if ((npcId == 20919 || npcId == 20920) && st.getQuestItemsCount(BBN) <= 99)
                st.giveItems(BBN);
            else if (npcId == 20921 && st.getQuestItemsCount(RBN) <= 99)
                st.giveItems(RBN);
            st.playSound(SOUND_ITEMGET);
            if (st.getQuestItemsCount(BBN) + st.getQuestItemsCount(RBN) == 200) {
                st.setCond(3);
                st.playSound(SOUND_MIDDLE);
            }
        }

        if (cond == 4) {
            if ((npcId == 20920 || npcId == 20921) && st.getQuestItemsCount(IP) <= 29)
                st.giveItems(IP);
            else if (npcId == 20925 && st.getQuestItemsCount(GML) <= 29)
                st.giveItems(GML);
            st.playSound(SOUND_ITEMGET);
            if (st.getQuestItemsCount(IP) + st.getQuestItemsCount(GML) == 60) {
                st.setCond(5);
                st.playSound(SOUND_MIDDLE);
            }
        }
    }
}