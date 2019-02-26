package l2trunk.scripts.quests;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _10288_SecretMission extends Quest {
    // NPC's
    private static final int DOMINIC = 31350;
    private static final int AQUILANI = 32780;
    private static final int GREYMORE = 32757;
    // items
    private static final int LETTER = 15529;

    public _10288_SecretMission() {
        super(false);

        addStartNpc(DOMINIC, AQUILANI);
        addTalkId(DOMINIC, GREYMORE, AQUILANI);
        addFirstTalkId(AQUILANI);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int npcId = npc.getNpcId();
        if (npcId == DOMINIC) {
            if ("31350-05.htm".equalsIgnoreCase(event)) {
                st.start();
                st.setCond(1);
                st.giveItems(LETTER);
                st.playSound(SOUND_ACCEPT);
            }
        } else if (npcId == GREYMORE && "32757-03.htm".equalsIgnoreCase(event)) {
            st.unset("cond");
            st.takeItems(LETTER);
            st.giveItems(57, 106583);
            st.addExpAndSp(417788, 46320);
            st.playSound(SOUND_FINISH);
            st.finish();
        } else if (npcId == AQUILANI) {
            if (st.getState() == STARTED) {
                if (event.equalsIgnoreCase("32780-05.htm")) {
                    st.setCond(2);
                    st.playSound(SOUND_MIDDLE);
                }
            } else if (st.getState() == COMPLETED && "teleport".equalsIgnoreCase(event)) {
                st.player.teleToLocation(118833, -80589, -2688);
                return null;
            }
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        if (npcId == DOMINIC) {
            switch (st.getState()) {
                case CREATED:
                    if (st.player.getLevel() >= 82)
                        htmltext = "31350-01.htm";
                    else
                        htmltext = "31350-00.htm";
                    break;
                case STARTED:
                    if (st.getCond() == 1)
                        htmltext = "31350-06.htm";
                    else if (st.getCond() == 2)
                        htmltext = "31350-07.htm";
                    break;
                case COMPLETED:
                    htmltext = "31350-08.htm";
                    break;
            }
        } else if (npcId == AQUILANI) {
            if (st.getCond() == 1)
                htmltext = "32780-03.htm";
            else if (st.getCond() == 2)
                htmltext = "32780-06.htm";
        } else if (npcId == GREYMORE && st.getCond() == 2)
            htmltext = "32757-01.htm";

        return htmltext;
    }

    @Override
    public String onFirstTalk(NpcInstance npc, Player player) {
        QuestState st = player.getQuestState(this);
        if (st == null) {
            newQuestState(player, CREATED);
            st = player.getQuestState(this);
        }
        if (npc.getNpcId() == AQUILANI) {
            if (st.getState() == COMPLETED)
                return "32780-01.htm";
            else
                return "32780-00.htm";
        }
        return null;
    }
}