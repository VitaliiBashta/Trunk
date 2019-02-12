package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;
import java.util.StringTokenizer;

public final class _905_RefinedDragonBlood extends Quest {
    private static final List<Integer> SeparatedSoul = List.of(32864, 32865, 32866, 32867, 32868, 32869, 32870);
    private static final List<Integer> AntharasDragonsBlue = List.of(22852, 22853, 22844, 22845);
    private static final List<Integer> AntharasDragonsRed = List.of(22848, 22849, 22850, 22851);

    private static final int UnrefinedRedDragonBlood = 21913;
    private static final int UnrefinedBlueDragonBlood = 21914;

    public _905_RefinedDragonBlood() {
        super(PARTY_ALL);
        addStartNpc(SeparatedSoul);
        addKillId(AntharasDragonsBlue);
        addKillId(AntharasDragonsRed);
        addQuestItem(UnrefinedRedDragonBlood, UnrefinedBlueDragonBlood);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("sepsoul_q905_05.htm".equalsIgnoreCase(event)) {
            st.setState(STARTED);
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if (event.startsWith("sepsoul_q905_08.htm")) {
            st.takeItems(AntharasDragonsBlue);
            st.takeItems(AntharasDragonsRed);

            StringTokenizer tokenizer = new StringTokenizer(event);
            tokenizer.nextToken();
            String s = tokenizer.nextToken();
            if ("1".equals(s)) {
                st.giveItems(21903);
            } else if ("2".equals(s)) {
                st.giveItems(21904);
            }
            htmltext = "sepsoul_q905_08.htm";
            st.setState(COMPLETED);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest(this);
        }

        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (SeparatedSoul.contains(npc.getNpcId())) {
            switch (st.getState()) {
                case CREATED:
                    if (st.isNowAvailable()) {
                        if (st.player.getLevel() >= 83)
                            htmltext = "sepsoul_q905_01.htm";
                        else {
                            htmltext = "sepsoul_q905_00.htm";
                            st.exitCurrentQuest(true);
                        }
                    } else
                        htmltext = "sepsoul_q905_00a.htm";
                    break;
                case STARTED:
                    if (cond == 1)
                        htmltext = "sepsoul_q905_06.htm";
                    else if (cond == 2)
                        htmltext = "sepsoul_q905_07.htm";
                    break;
            }
        }

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int cond = st.getCond();
        if (cond == 1) {
            if (AntharasDragonsBlue.contains(npc.getNpcId())) {
                if (st.getQuestItemsCount(UnrefinedBlueDragonBlood) < 10 && Rnd.chance(70))
                    st.giveItems(UnrefinedBlueDragonBlood);
            } else if (AntharasDragonsRed.contains(npc.getNpcId())) {
                if (st.getQuestItemsCount(UnrefinedRedDragonBlood) < 10 && Rnd.chance(70))
                    st.giveItems(UnrefinedRedDragonBlood);
            }
            if (st.getQuestItemsCount(UnrefinedBlueDragonBlood) >= 10 && st.getQuestItemsCount(UnrefinedRedDragonBlood) >= 10)
                st.setCond(2);
        }
    }
}