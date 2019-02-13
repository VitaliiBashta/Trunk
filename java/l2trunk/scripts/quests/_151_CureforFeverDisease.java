package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;

public final class _151_CureforFeverDisease extends Quest {
    private final int POISON_SAC = 703;
    private final int FEVER_MEDICINE = 704;
    private static final int ROUND_SHIELD = 102;

    public _151_CureforFeverDisease() {
        super(false);

        addStartNpc(30050);

        addTalkId(30032);

        addKillId(20103, 20106, 20108);

        addQuestItem(FEVER_MEDICINE, POISON_SAC);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("30050-03.htm".equals(event)) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int id = st.getState();
        int cond = 0;
        if (id != CREATED)
            cond = st.getCond();
        if (npcId == 30050) {
            if (cond == 0) {
                if (st.player.getLevel() >= 15)
                    htmltext = "30050-02.htm";
                else {
                    htmltext = "30050-01.htm";
                    st.exitCurrentQuest(true);
                }
            } else if (cond == 1 && st.getQuestItemsCount(POISON_SAC) == 0 && st.getQuestItemsCount(FEVER_MEDICINE) == 0)
                htmltext = "30050-04.htm";
            else if (cond == 1 && st.haveQuestItem(POISON_SAC))
                htmltext = "30050-05.htm";
            else if (cond == 3 && st.haveQuestItem(FEVER_MEDICINE) ) {
                st.takeItems(FEVER_MEDICINE);

                st.giveItems(ROUND_SHIELD);
                st.player.addExpAndSp(13106, 613);

                if (st.player.getClassId().occupation() == 0 && !st.player.isVarSet("p1q4")) {
                    st.player.setVar("p1q4", 1);
                    st.player.sendPacket(new ExShowScreenMessage("Now go find the Newbie Guide."));
                }

                htmltext = "30050-06.htm";
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(false);
            }
        } else if (npcId == 30032)
            if (cond == 2 && st.getQuestItemsCount(POISON_SAC) > 0) {
                st.giveItems(FEVER_MEDICINE);
                st.takeItems(POISON_SAC);
                st.setCond(3);
                htmltext = "30032-01.htm";
            } else if (cond == 3 && st.getQuestItemsCount(FEVER_MEDICINE) > 0)
                htmltext = "30032-02.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if ((npcId == 20103 || npcId == 20106 || npcId == 20108) && st.getQuestItemsCount(POISON_SAC) == 0 && st.getCond() == 1 && Rnd.chance(50)) {
            st.setCond(2);
            st.giveItems(POISON_SAC);
            st.playSound(SOUND_MIDDLE);
        }
    }
}