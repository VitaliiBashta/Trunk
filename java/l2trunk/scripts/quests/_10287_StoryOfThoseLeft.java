package l2trunk.scripts.quests;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.utils.ReflectionUtils;

import java.util.List;
import java.util.StringTokenizer;

import static l2trunk.scripts.quests._10283_RequestOfIceMerchant.JINIA;
import static l2trunk.scripts.quests._10283_RequestOfIceMerchant.RAFFORTY;
import static l2trunk.scripts.quests._10285_MeetingSirra.JINIA_2;
import static l2trunk.scripts.quests._10285_MeetingSirra.KEGOR;

public final class _10287_StoryOfThoseLeft extends Quest {
    private static List<Integer> forgottenScrols = List.of(
            10549, 10550, 10551, 10552, 10553, 14219);

    public _10287_StoryOfThoseLeft() {
        super(false);
        addStartNpc(RAFFORTY);
        addTalkId(JINIA, JINIA_2, KEGOR);
    }

    static void enterInstance(Player player) {
        Reflection r = player.getActiveReflection();
        if (player.canReenterInstance(141))
            if (r != null) player.teleToLocation(r.getTeleportLoc(), r);
            else ReflectionUtils.enterReflection(player, 141);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("rafforty_q10287_02.htm".equalsIgnoreCase(event)) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("enterinstance".equalsIgnoreCase(event)) {
            st.setCond(2);
            enterInstance(st.player);
            return null;
        } else if ("jinia_q10287_03.htm".equalsIgnoreCase(event))
            st.setCond(3);
        else if ("kegor_q10287_03.htm".equalsIgnoreCase(event))
            st.setCond(4);
        else if ("exitinstance".equalsIgnoreCase(event)) {
            st.setCond(5);
            st.player.getReflection().collapse();
            return null;
        } else if (event.startsWith("exgivebook")) {
            StringTokenizer str = new StringTokenizer(event);
            str.nextToken();
            int id = Integer.parseInt(str.nextToken());
            htmltext = "rafforty_q10287_05.htm";

            st.giveItems(forgottenScrols.get(id + 1));

            st.complete();
            st.finish();
        }

        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == RAFFORTY) {
            if (cond == 0) {
                if (st.player.getLevel() >= 82 && st.player.isQuestCompleted(_10286_ReunionWithSirra.class))
                    htmltext = "rafforty_q10287_01.htm";
                else {
                    htmltext = "rafforty_q10287_00.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond >= 1 && cond < 5)
                htmltext = "rafforty_q10287_02.htm";
            else if (cond == 5)
                htmltext = "rafforty_q10287_03.htm";
            else
                htmltext = "rafforty_q10287_06.htm";
        } else if (npcId == JINIA) {
            if (cond == 2)
                htmltext = "jinia_q10287_01.htm";
            else if (cond == 3)
                htmltext = "jinia_q10287_04.htm";
            else if (cond == 4)
                htmltext = "jinia_q10287_05.htm";
        } else if (npcId == KEGOR) {
            if (cond == 3)
                htmltext = "kegor_q10287_01.htm";
            else if (cond == 2 || cond == 4)
                htmltext = "kegor_q10287_04.htm";
        }
        return htmltext;
    }
}