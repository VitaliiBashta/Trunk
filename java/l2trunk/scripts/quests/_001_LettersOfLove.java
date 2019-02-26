package l2trunk.scripts.quests;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;

public final class _001_LettersOfLove extends Quest {
    private final static int DARIN = 30048;
    private final static int ROXXY = 30006;
    private final static int BAULRO = 30033;

    private final static int DARINGS_LETTER = 687;
    private final static int ROXXY_KERCHIEF = 688;
    private final static int DARINGS_RECEIPT = 1079;
    private final static int BAULS_POTION = 1080;
    private final static int NECKLACE = 906;

    public _001_LettersOfLove() {
        super(false);

        addStartNpc(DARIN);
        addTalkId(ROXXY,BAULRO);
        addQuestItem(DARINGS_LETTER,ROXXY_KERCHIEF,DARINGS_RECEIPT,BAULS_POTION);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("quest_accept".equalsIgnoreCase(event)) {
            htmltext = "daring_q0001_06.htm";
            st.setCond(1);
            st.start();
            st.giveItems(DARINGS_LETTER);
            st.playSound(SOUND_ACCEPT);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        switch (npcId) {
            case DARIN:
                if (cond == 0) {
                    if (st.player.getLevel() >= 2)
                        htmltext = "daring_q0001_02.htm";
                    else {
                        htmltext = "daring_q0001_01.htm";
                        st.exitCurrentQuest();
                    }
                } else if (cond == 1)
                    htmltext = "daring_q0001_07.htm";
                else if (cond == 2 && st.getQuestItemsCount(ROXXY_KERCHIEF) == 1) {
                    htmltext = "daring_q0001_08.htm";
                    st.takeItems(ROXXY_KERCHIEF);
                    st.giveItems(DARINGS_RECEIPT);
                    st.setCond(3);
                    st.playSound(SOUND_MIDDLE);
                } else if (cond == 3)
                    htmltext = "daring_q0001_09.htm";
                else if (cond == 4 && st.getQuestItemsCount(BAULS_POTION) == 1) {
                    htmltext = "daring_q0001_10.htm";
                    st.takeItems(BAULS_POTION);
                    st.giveItems(NECKLACE);
                    if (st.player.getClassId().occupation() == 0 && !st.player.isVarSet("ng1"))
                        st.player.sendPacket(new ExShowScreenMessage("  Delivery duty complete.\nGo find the Newbie Guide."));
                    st.giveItems(ADENA_ID, (int) ((Config.RATE_QUESTS_REWARD - 1) * 1200 + 2466 * Config.RATE_QUESTS_REWARD), false); // T2
                    st.player.addExpAndSp(5672, 446);
                    st.playSound(SOUND_FINISH);
                    st.finish();
                }
                break;
            case ROXXY:
                if (cond == 1 && st.getQuestItemsCount(ROXXY_KERCHIEF) == 0 && st.getQuestItemsCount(DARINGS_LETTER) > 0) {
                    htmltext = "rapunzel_q0001_01.htm";
                    st.takeItems(DARINGS_LETTER);
                    st.giveItems(ROXXY_KERCHIEF);
                    st.setCond(2);
                    st.playSound(SOUND_MIDDLE);
                } else if (cond == 2 && st.getQuestItemsCount(ROXXY_KERCHIEF) > 0)
                    htmltext = "rapunzel_q0001_02.htm";
                else if (cond > 2 && (st.getQuestItemsCount(BAULS_POTION) > 0 || st.getQuestItemsCount(DARINGS_RECEIPT) > 0))
                    htmltext = "rapunzel_q0001_03.htm";
                break;
            case BAULRO:
                if (cond == 3 && st.getQuestItemsCount(DARINGS_RECEIPT) == 1) {
                    htmltext = "baul_q0001_01.htm";
                    st.takeItems(DARINGS_RECEIPT);
                    st.giveItems(BAULS_POTION);
                    st.setCond(4);
                    st.playSound(SOUND_MIDDLE);
                } else if (cond == 4)
                    htmltext = "baul_q0001_02.htm";
                break;
        }
        return htmltext;
    }
}