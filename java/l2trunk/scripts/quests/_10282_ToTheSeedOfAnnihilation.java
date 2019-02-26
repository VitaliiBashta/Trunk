package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _10282_ToTheSeedOfAnnihilation extends Quest {
    private final static int KBALDIR = 32733;
    private final static int KLEMIS = 32734;

    private final static int SOA_ORDERS = 15512;

    public _10282_ToTheSeedOfAnnihilation() {
        super(false);

        addStartNpc(KBALDIR);
        addTalkId(KLEMIS);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("32733-07.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.giveItems(SOA_ORDERS);
            st.playSound(SOUND_ACCEPT);
        } else if ("32734-02.htm".equalsIgnoreCase(event)) {
            st.unset("cond");
            st.addExpAndSp(1148480, 99110);
            st.takeItems(SOA_ORDERS);
            st.finish();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int id = st.getState();
        int npcId = npc.getNpcId();
        if (id == COMPLETED) {
            if (npcId == KBALDIR)
                htmltext = "32733-09.htm";
            else if (npcId == KLEMIS)
                htmltext = "32734-03.htm";
        } else if (id == CREATED) {
            if (st.player.getLevel() >= 84)
                htmltext = "32733-01.htm";
            else
                htmltext = "32733-00.htm";
        } else {
            if (st.getCond() == 1)
                if (npcId == KBALDIR)
                    htmltext = "32733-08.htm";
                else if (npcId == KLEMIS)
                    htmltext = "32734-01.htm";
        }
        return htmltext;
    }
}