package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _003_WilltheSealbeBroken extends Quest {
    private static final int StartNpc = 30141;
    private final List<Integer> Monster = List.of(
            20031, 20041, 20046, 20048, 20052, 20057);

    private final int OnyxBeastEye = 1081;
    private final int TaintStone = 1082;
    private final int SuccubusBlood = 1083;

    public _003_WilltheSealbeBroken() {
        super(false);
        addStartNpc(StartNpc);
        addKillId(Monster);
        addQuestItem(OnyxBeastEye, TaintStone, SuccubusBlood);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("quest_accept")) {
            htmltext = "redry_q0003_03.htm";
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int id = st.getState();
        if (id == CREATED)
            if (st.getPlayer().getRace() != Race.darkelf) {
                htmltext = "redry_q0003_00.htm";
                st.exitCurrentQuest(true);
            } else if (st.getPlayer().getLevel() >= 16) {
                htmltext = "redry_q0003_02.htm";
                return htmltext;
            } else {
                htmltext = "redry_q0003_01.htm";
                st.exitCurrentQuest(true);
            }
        else if (id == STARTED)
            if (st.getQuestItemsCount(OnyxBeastEye) > 0 && st.getQuestItemsCount(TaintStone) > 0 && st.getQuestItemsCount(SuccubusBlood) > 0) {
                htmltext = "redry_q0003_06.htm";
                st.takeItems(OnyxBeastEye, -1);
                st.takeItems(TaintStone, -1);
                st.takeItems(SuccubusBlood, -1);
                st.giveItems(956, 1, true);
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(false);
            } else
                htmltext = "redry_q0003_04.htm";
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int id = st.getState();
        if (id == STARTED) {
            if (npcId == Monster.get(0) && st.getQuestItemsCount(OnyxBeastEye) == 0) {
                st.giveItems(OnyxBeastEye, 1, false);
                st.playSound(SOUND_ITEMGET);
            } else if ((npcId == Monster.get(1) || npcId == Monster.get(2)) && st.getQuestItemsCount(TaintStone) == 0) {
                st.giveItems(TaintStone, 1, false);
                st.playSound(SOUND_ITEMGET);
            } else if ((npcId == Monster.get(3) || npcId == Monster.get(4) || npcId == Monster.get(5) && st.getQuestItemsCount(SuccubusBlood) == 0)) {
                st.giveItems(SuccubusBlood, 1, false);
                st.playSound(SOUND_ITEMGET);
            }
            if (st.getQuestItemsCount(OnyxBeastEye) > 0 && st.getQuestItemsCount(TaintStone) > 0 && st.getQuestItemsCount(SuccubusBlood) > 0) {
                st.setCond(2);
                st.playSound(SOUND_MIDDLE);
            }
        }
        return null;
    }
}