package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _902_ReclaimOurEra extends Quest {
    private static final int Mathias = 31340;
    private static final List<Integer> OrcsSilenos = List.of(25309, 25312, 25315, 25299, 25302, 25305);
    private static final List<Integer> CannibalisticStakatoChief = List.of(25667, 25668, 25669, 25670);
    private static final int Anais = 25701;

    private static final int ShatteredBones = 21997;
    private static final int CannibalisticStakatoLeaderClaw = 21998;
    private static final int AnaisScroll = 21999;

    public _902_ReclaimOurEra() {
        super(PARTY_ALL);
        addStartNpc(Mathias);
        addKillId(OrcsSilenos);
        addKillId(CannibalisticStakatoChief);
        addKillId(Anais);
        addQuestItem(ShatteredBones, CannibalisticStakatoLeaderClaw, AnaisScroll);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("mathias_q902_04.htm".equalsIgnoreCase(event)) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("mathias_q902_05.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
        } else if ("mathias_q902_06.htm".equalsIgnoreCase(event)) {
            st.setCond(3);
        } else if ("mathias_q902_07.htm".equalsIgnoreCase(event)) {
            st.setCond(4);
        } else if ("mathias_q902_09.htm".equalsIgnoreCase(event)) {
            if (st.haveQuestItem(ShatteredBones) ) {
                st.takeItems(ShatteredBones);
                st.giveItems(21750);
                st.giveItems(ADENA_ID, 134038);
            } else if (st.haveQuestItem(CannibalisticStakatoLeaderClaw)) {
                st.takeItems(CannibalisticStakatoLeaderClaw);
                st.giveItems(21750, 3);
                st.giveItems(ADENA_ID, 210119);
            } else if (st.haveQuestItem(AnaisScroll) ) {
                st.takeItems(AnaisScroll);
                st.giveItems(21750, 3);
                st.giveItems(ADENA_ID, 348155);
            }
            st.complete();
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest(this);
        }

        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npc.getNpcId() == Mathias) {
            switch (st.getState()) {
                case CREATED:
                    if (st.isNowAvailable()) {
                        if (st.player.getLevel() >= 80)
                            htmltext = "mathias_q902_01.htm";
                        else {
                            htmltext = "mathias_q902_00.htm";
                            st.exitCurrentQuest();
                        }
                    } else
                        htmltext = "mathias_q902_00a.htm";
                    break;
                case STARTED:
                    if (cond == 1)
                        htmltext = "mathias_q902_04.htm";
                    else if (cond == 2)
                        htmltext = "mathias_q902_05.htm";
                    else if (cond == 3)
                        htmltext = "mathias_q902_06.htm";
                    else if (cond == 4)
                        htmltext = "mathias_q902_07.htm";
                    else if (cond == 5)
                        htmltext = "mathias_q902_08.htm";
                    break;
            }
        }

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int cond = st.getCond();
        if (cond == 2 && OrcsSilenos.contains(npc.getNpcId())) {
            st.giveItems(ShatteredBones);
            st.setCond(5);
        } else if (cond == 3 && CannibalisticStakatoChief.contains(npc.getNpcId())) {
            st.giveItems(CannibalisticStakatoLeaderClaw);
            st.setCond(5);
        } else if (cond == 4 && npc.getNpcId() == Anais) {
            st.giveItems(AnaisScroll);
            st.setCond(5);
        }
    }

}