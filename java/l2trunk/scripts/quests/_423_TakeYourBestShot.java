package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class _423_TakeYourBestShot extends Quest {
    private static final int Johnny = 32744;
    private static final int Batracos = 32740;
    private static final int TantaGuard = 18862;
    private static final int SeerUgorosPass = 15496;
    private static final List<Integer> TantaClan = List.of(
            22768, 22769, 22770, 22771, 22772, 22773, 22774);

    public _423_TakeYourBestShot() {
        super(true);
        addStartNpc(Johnny);
        addTalkId(Batracos);
        addKillId(TantaGuard);
        addKillId(TantaClan);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("johnny_q423_04.htm".equalsIgnoreCase(event))
            st.exitCurrentQuest();
        else if ("johnny_q423_05.htm".equalsIgnoreCase(event)) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == Johnny) {
            if (cond == 0) {
                if (st.player.getLevel() >= 82 && st.player.isQuestCompleted(_249_PoisonedPlainsOfTheLizardmen.class))
                    htmltext = "johnny_q423_01.htm";
                else {
                    htmltext = "johnny_q423_00.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "johnny_q423_06.htm";
            else if (cond == 2)
                htmltext = "johnny_q423_07.htm";
        } else if (npcId == Batracos) {
            if (cond == 1)
                htmltext = "batracos_q423_01.htm";
            else if (cond == 2) {
                htmltext = "batracos_q423_02.htm";
                st.giveItems(SeerUgorosPass);
                st.exitCurrentQuest();
            }
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (st.getCond() == 1) {
            if (TantaClan.contains(npcId) && Rnd.chance(2)) {
                addSpawn(TantaGuard, st.player.getLoc(), 100, 120000);
            } else if (npcId == TantaGuard && !st.haveQuestItem(SeerUgorosPass))
                st.setCond(2);
        }
    }

}