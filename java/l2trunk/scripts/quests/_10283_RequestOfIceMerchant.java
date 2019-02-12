package l2trunk.scripts.quests;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.utils.Location;

public final class _10283_RequestOfIceMerchant extends Quest {
    // NPC's
    static final int RAFFORTY = 32020;
    private static final int KIER = 32022;
    static final int JINIA = 32760;

    public _10283_RequestOfIceMerchant() {
        super(false);

        addStartNpc(RAFFORTY);
        addTalkId(RAFFORTY);
        addTalkId(KIER);
        addTalkId(JINIA);
        addFirstTalkId(JINIA);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (npc == null)
            return null;

        int npcId = npc.getNpcId();
        if (npcId == RAFFORTY) {
            if (event.equalsIgnoreCase("32020-03.htm")) {
                st.setState(STARTED);
                st.setCond(1);
                st.playSound(SOUND_ACCEPT);
            } else if (event.equalsIgnoreCase("32020-07.htm")) {
                st.setCond(2);
                st.playSound(SOUND_MIDDLE);
            }
        } else if (npcId == KIER && event.equalsIgnoreCase("spawn")) {
            addSpawn(JINIA, new Location(104322, -107669, -3680, 44954), 0, 60000);
            return null;
        } else if (npcId == JINIA && event.equalsIgnoreCase("32760-04.htm")) {
            st.giveItems(57, 190000);
            st.addExpAndSp(627000, 50300);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest(false);
            npc.deleteMe();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        if (npcId == RAFFORTY) {
            switch (st.getState()) {
                case CREATED:
                    if (st.player.isQuestCompleted(_115_TheOtherSideOfTruth.class) && st.player.getLevel() >= 82)
                        htmltext = "32020-01.htm";
                    else {
                        htmltext = "32020-00.htm";
                        st.exitCurrentQuest(true);
                    }
                    break;
                case STARTED:
                    if (st.getCond() == 1)
                        htmltext = "32020-04.htm";
                    else if (st.getCond() == 2)
                        htmltext = "32020-08.htm";
                    break;
                case COMPLETED:
                    htmltext = "31350-08.htm";
                    break;
            }
        } else if (npcId == KIER && st.getCond() == 2)
            htmltext = "32022-01.htm";
        else if (npcId == JINIA && st.getCond() == 2)
            htmltext = "32760-02.htm";

        return htmltext;
    }

    @Override
    public String onFirstTalk(NpcInstance npc, Player player) {
        QuestState st = player.getQuestState(this);
        if (st == null)
            return null;
        if (npc.getNpcId() == JINIA && st.getCond() == 2)
            return "32760-01.htm";
        return null;
    }
}
