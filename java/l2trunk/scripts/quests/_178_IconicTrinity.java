package l2trunk.scripts.quests;

import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _178_IconicTrinity extends Quest {
    //NPC
    private static final int Kekropus = 32138;
    private static final int IconOfThePast = 32255;
    private static final int IconOfThePresent = 32256;
    private static final int IconOfTheFuture = 32257;
    //items
    private static final int EnchantD = 956;

    public _178_IconicTrinity() {
        super(false);

        addStartNpc(Kekropus);
        addTalkId(IconOfThePast,IconOfThePresent,IconOfTheFuture);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("32138-02.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("32255-03.htm") || event.equalsIgnoreCase("32256-03.htm") || event.equalsIgnoreCase("32257-03.htm"))
            st.set("id", "");
        else if (event.equalsIgnoreCase("32255-09.htm")) {
            st.set("id", "");
            st.setCond(2);
            st.start();
        } else if ("32256-09.htm".equalsIgnoreCase(event)) {
            st.set("id", "");
            st.setCond(3);
            st.start();
        } else if ("32257-06.htm".equalsIgnoreCase(event)) {
            st.set("id", "");
            st.setCond(4);
            st.start();
        } else if (event.length() == 1) {
            int cond = st.getCond();
            int len = 0;
            if (!event.equals("0")) {
                if (st.get("id") == null)
                    st.set("id", "");
                String id = st.get("id");
                st.set("id", id + event);
                len = st.get("id").length();
            }
            if (!event.equals("0") && len == 4 && (cond == 1 || cond == 2) || len == 5 && cond == 3) {
                if (cond == 1 && st.get("id") != null && "CRTR".equalsIgnoreCase(st.get("id")))
                    htmltext = "32255-04.htm";
                else if (cond == 2 && st.get("id") != null && "CNCL".equalsIgnoreCase(st.get("id")))
                    htmltext = "32256-04.htm";
                else if (cond == 3 && st.get("id") != null && "CHAOS".equalsIgnoreCase(st.get("id")))
                    htmltext = "32257-04.htm";
                else {
                    htmltext = "<html><body>Quest Failed</body></html>";
                    st.exitCurrentQuest();
                }
            } else {
                if (cond == 1)
                    htmltext = HtmCache.INSTANCE.getNotNull("quests/_178_IconicTrinity/32255-03.htm", st.player);
                else if (cond == 2)
                    htmltext = HtmCache.INSTANCE.getNotNull("quests/_178_IconicTrinity/32256-03.htm", st.player);
                else
                    htmltext = HtmCache.INSTANCE.getNotNull("quests/_178_IconicTrinity/32257-03.htm", st.player);

                if (len == 0)
                    htmltext = htmltext.replace("Password :  ", "").replace("#N", "first");
                else if (len == 1)
                    htmltext = htmltext.replace("Password :  ", "*").replace("#N", "second");
                else if (len == 2)
                    htmltext = htmltext.replace("Password :  ", "**").replace("#N", "third");
                else if (len == 3)
                    htmltext = htmltext.replace("Password :  ", "***").replace("#N", "fourth");
                else if (len == 4)
                    htmltext = htmltext.replace("Password :  ", "****").replace("#N", "fifth");
            }
        } else if ("32138-04.htm".equalsIgnoreCase(event)) {
            st.giveItems(EnchantD, 1, true);
            st.addExpAndSp(20123, 976);
            st.playSound(SOUND_FINISH);
            st.finish();
        }
        if (event.equalsIgnoreCase("32255-07.htm") || event.equalsIgnoreCase("32255-09.htm") || event.equalsIgnoreCase("32256-07.htm") || event.equalsIgnoreCase("32256-08.htm") || event.equalsIgnoreCase("32256-09.htm") || event.equalsIgnoreCase("32257-06.htm")) {
            htmltext = HtmCache.INSTANCE.getNotNull("quests/_178_IconicTrinity/" + event, st.player);
            htmltext = htmltext.replace("%player_name%", st.player.getName());
        }

        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == Kekropus) {
            if (cond == 0) {
                if (st.player.getRace() != Race.kamael) {
                    htmltext = "32138-05.htm";
                    st.exitCurrentQuest();
                } else if (st.player.getLevel() < 17) {
                    htmltext = "32138-00.htm";
                    st.exitCurrentQuest();
                } else
                    htmltext = "32138-01.htm";
            } else if (cond == 4)
                htmltext = "32138-03.htm";
        } else if (npcId == IconOfThePast && cond == 1)
            htmltext = "32255-01.htm";
        else if (npcId == IconOfThePresent && cond == 2)
            htmltext = "32256-01.htm";
        else if (npcId == IconOfTheFuture && cond == 3)
            htmltext = "32257-01.htm";
        return htmltext;
    }
}