package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _032_AnObviousLie extends Quest {
    //MOBS
    private static final int ALLIGATOR = 20135;
    //CHANCE FOR DROP
    private static final int CHANCE_FOR_DROP = 30;
    //REWARDS
    private static final int RACCOON_EAR = 7680;
    private static final int CAT_EAR = 6843;
    private static final int RABBIT_EAR = 7683;
    //NPC
    private final int MAXIMILIAN = 30120;
    private final int GENTLER = 30094;
    private final int MIKI_THE_CAT = 31706;
    //ITEMS
    private final int MAP = 7165;
    private final int MEDICINAL_HERB = 7166;
    private final int SPIRIT_ORES = 3031;
    private final int THREAD = 1868;
    private final int SUEDE = 1866;

    public _032_AnObviousLie() {
        addStartNpc(MAXIMILIAN);
        addTalkId(GENTLER,MIKI_THE_CAT);

        addKillId(ALLIGATOR);

        addQuestItem(MEDICINAL_HERB,MAP);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("30120-1.htm".equals(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("30094-1.htm".equals(event)) {
            st.giveItems(MAP);
            st.setCond(2);
        } else if ("31706-1.htm".equals(event)) {
            st.takeItems(MAP);
            st.setCond(3);
        } else if ("30094-4.htm".equals(event)) {
            if (st.haveQuestItem(MEDICINAL_HERB, 20)) {
                st.takeItems(MEDICINAL_HERB);
                st.setCond(5);
            } else {
                htmltext = "You don't have enough materials";
                st.setCond(3);
            }
        } else if ("30094-7.htm".equals(event)) {
            if (st.haveQuestItem(SPIRIT_ORES, 500)) {
                st.takeItems(SPIRIT_ORES, 500);
                st.setCond(6);
            } else
                htmltext = "You don't have enough materials";
        } else if ("31706-4.htm".equals(event))
            st.setCond(7);
        else if ("30094-10.htm".equals(event))
            st.setCond(8);
        else if ("30094-13.htm".equals(event)) {
            if (st.getQuestItemsCount(THREAD) < 1000 || st.getQuestItemsCount(SUEDE) < 500)
                htmltext = "You don't have enough materials";
        } else if ("cat".equalsIgnoreCase(event) || "racoon".equalsIgnoreCase(event) || "rabbit".equalsIgnoreCase(event))
            if (st.getCond() == 8 && st.getQuestItemsCount(THREAD) >= 1000 && st.getQuestItemsCount(SUEDE) >= 500) {
                st.takeItems(THREAD, 1000);
                st.takeItems(SUEDE, 500);
                if ("cat".equalsIgnoreCase(event))
                    st.giveItems(CAT_EAR);
                else if ("racoon".equalsIgnoreCase(event))
                    st.giveItems(RACCOON_EAR);
                else if ("rabbit".equalsIgnoreCase(event))
                    st.giveItems(RABBIT_EAR);
                st.unset("cond");
                st.playSound(SOUND_FINISH);
                htmltext = "30094-14.htm";
                st.finish();
            } else
                htmltext = "You don't have enough materials";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == MAXIMILIAN)
            if (cond == 0) {
                if (st.player.getLevel() >= 45)
                    htmltext = "30120-0.htm";
                else {
                    htmltext = "30120-0a.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "30120-2.htm";
        if (npcId == GENTLER)
            if (cond == 1)
                htmltext = "30094-0.htm";
            else if (cond == 2)
                htmltext = "30094-2.htm";
            else if (cond == 3)
                htmltext = "30094-forgot.htm";
            else if (cond == 4)
                htmltext = "30094-3.htm";
            else if (cond == 5 && st.getQuestItemsCount(SPIRIT_ORES) < 500)
                htmltext = "30094-5.htm";
            else if (cond == 5 && st.getQuestItemsCount(SPIRIT_ORES) >= 500)
                htmltext = "30094-6.htm";
            else if (cond == 6)
                htmltext = "30094-8.htm";
            else if (cond == 7)
                htmltext = "30094-9.htm";
            else if (cond == 8 && (st.getQuestItemsCount(THREAD) < 1000 || st.getQuestItemsCount(SUEDE) < 500))
                htmltext = "30094-11.htm";
            else if (cond == 8 && st.getQuestItemsCount(THREAD) >= 1000 && st.getQuestItemsCount(SUEDE) >= 500)
                htmltext = "30094-12.htm";
        if (npcId == MIKI_THE_CAT)
            if (cond == 2)
                htmltext = "31706-0.htm";
            else if (cond == 3)
                htmltext = "31706-2.htm";
            else if (cond == 6)
                htmltext = "31706-3.htm";
            else if (cond == 7)
                htmltext = "31706-5.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        long count = st.getQuestItemsCount(MEDICINAL_HERB);
        if (Rnd.chance(CHANCE_FOR_DROP) && st.getCond() == 3)
            if (count < 20) {
                st.giveItems(MEDICINAL_HERB);
                if (count == 19) {
                    st.playSound(SOUND_MIDDLE);
                    st.setCond(4);
                } else
                    st.playSound(SOUND_ITEMGET);
            }
    }
}