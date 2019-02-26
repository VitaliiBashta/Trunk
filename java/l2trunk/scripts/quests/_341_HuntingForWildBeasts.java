package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _341_HuntingForWildBeasts extends Quest {
    //NPCs
    private static final int PANO = 30078;
    //Mobs
    private static final int Red_Bear = 20021;
    private static final int Dion_Grizzly = 20203;
    private static final int Brown_Bear = 20310;
    private static final int Grizzly_Bear = 20335;
    //Quest items
    private static final int BEAR_SKIN = 4259;
    //Chances
    private static final int BEAR_SKIN_CHANCE = 40;

    public _341_HuntingForWildBeasts() {
        super(false);
        addStartNpc(PANO);
        addKillId(Red_Bear,Dion_Grizzly,Brown_Bear,Grizzly_Bear);
        addQuestItem(BEAR_SKIN);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("quest_accept".equalsIgnoreCase(event) && st.getState() == CREATED) {
            htmltext = "pano_q0341_04.htm";
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        if (npc.getNpcId() != PANO)
            return htmltext;
        int state = st.getState();
        if (state == CREATED) {
            if (st.player.getLevel() >= 20) {
                htmltext = "pano_q0341_01.htm";
                st.setCond(0);
            } else {
                htmltext = "pano_q0341_02.htm";
                st.exitCurrentQuest();
            }
        } else if (state == STARTED)
            if (st.haveQuestItem(BEAR_SKIN, 20)) {
                htmltext = "pano_q0341_05.htm";
                st.takeItems(BEAR_SKIN);
                st.giveItems(ADENA_ID, 3710);
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest();
            } else
                htmltext = "pano_q0341_06.htm";

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState qs) {
        if (qs.getState() != STARTED)
            return;

        long BEAR_SKIN_COUNT = qs.getQuestItemsCount(BEAR_SKIN);
        if (BEAR_SKIN_COUNT < 20 && Rnd.chance(BEAR_SKIN_CHANCE)) {
            qs.giveItems(BEAR_SKIN);
            if (BEAR_SKIN_COUNT == 19) {
                qs.setCond(2);
                qs.playSound(SOUND_MIDDLE);
            } else
                qs.playSound(SOUND_ITEMGET);
        }
    }

}