package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _162_CurseOfUndergroundFortress extends Quest {
    private final int BONE_FRAGMENT3 = 1158;
    private final int ELF_SKULL = 1159;
    private final int BONE_SHIELD = 625;

    public _162_CurseOfUndergroundFortress() {
        super(false);

        addStartNpc(30147);

        addTalkId(30147);

        addKillId(20033,20345,20371,20463,20464,20504);

        addQuestItem(ELF_SKULL,
                BONE_FRAGMENT3);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("30147-04.htm".equals(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
            htmltext = "30147-04.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (cond == 0) {
            if (st.player.getRace() == Race.darkelf)
                htmltext = "30147-00.htm";
            else if (st.player.getLevel() >= 12)
                htmltext = "30147-02.htm";
            else {
                htmltext = "30147-01.htm";
                st.exitCurrentQuest();
            }
        } else if (cond == 1 && st.getQuestItemsCount(ELF_SKULL) + st.getQuestItemsCount(BONE_FRAGMENT3) < 13)
            htmltext = "30147-05.htm";
        else if (cond == 2 && st.haveQuestItem(ELF_SKULL,3) && st.haveQuestItem(BONE_FRAGMENT3,10)) {
            htmltext = "30147-06.htm";
            st.giveItems(BONE_SHIELD);
            st.addExpAndSp(22652, 1004);
            st.giveItems(ADENA_ID, 24000);
            st.takeAllItems(ELF_SKULL,BONE_FRAGMENT3);
            st.setCond(0);
            st.playSound(SOUND_FINISH);
            st.finish();
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if ((npcId == 20463 || npcId == 20464 || npcId == 20504) && cond == 1 && Rnd.chance(25) && st.getQuestItemsCount(BONE_FRAGMENT3) < 10) {
            st.giveItems(BONE_FRAGMENT3);
            if (st.haveQuestItem(BONE_FRAGMENT3, 10))
                st.playSound(SOUND_MIDDLE);
            else
                st.playSound(SOUND_ITEMGET);
        } else if ((npcId == 20033 || npcId == 20345 || npcId == 20371) && cond == 1 && Rnd.chance(25) && st.getQuestItemsCount(ELF_SKULL) < 3) {
            st.giveItems(ELF_SKULL);
            if (st.haveQuestItem(ELF_SKULL, 3))
                st.playSound(SOUND_MIDDLE);
            else
                st.playSound(SOUND_ITEMGET);
        }
        if (st.haveQuestItem(BONE_FRAGMENT3,10) && st.haveQuestItem(ELF_SKULL, 3))
            st.setCond(2);
    }
}