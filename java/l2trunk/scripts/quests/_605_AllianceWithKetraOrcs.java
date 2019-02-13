package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static l2trunk.scripts.quests._611_AllianceWithVarkaSilenos.*;

public final class _605_AllianceWithKetraOrcs extends Quest {
    static final List<Integer> VARKA_SOLDIERS = List.of(21350, 21351, 21353, 21354, 21355);
    static final List<Integer> VARKA_CAPTAINS = List.of(21357, 21358, 21360, 21361, 21362, 21369, 21370);
    static final List<Integer> VARKA_GENERALS = List.of(21365, 21366, 21368, 21371, 21372, 21373, 21374, 21375);
    static final List<Integer> KETRA_NPC_LIST = Stream.of(KETRA_SOILDERS, KETRA_CAPTAINS, KETRA_GENERALS)
            .flatMap(List::stream).collect(Collectors.toList());
    // items
    public static final List<Integer> KETRA_MARKS = List.of(7211, 7212, 7213, 7214, 7215);
    private static final int VB_SOLDIER = 7216;
    private static final int VB_CAPTAIN = 7217;
    private static final int VB_GENERAL = 7218;
    private static final int TOTEM_OF_VALOR = 7219;
    private static final int TOTEM_OF_WISDOM = 7220;
    private static final int Wahkan = 31371;

    public _605_AllianceWithKetraOrcs() {
        super(true);

        addStartNpc(Wahkan);

        addKillId(KETRA_NPC_LIST);

        //hunt for soldier
        addKillId(VARKA_SOLDIERS);

        //hunt for captain
        addKillId(VARKA_CAPTAINS);

        //hunt for general
        addKillId(VARKA_GENERALS);

        addQuestItem(VB_SOLDIER);
        addQuestItem(VB_CAPTAIN);
        addQuestItem(VB_GENERAL);
    }

    private static boolean checkNextLevel(QuestState st, int soilder_count, int capitan_count, int general_count, int other_item, boolean take) {
        if (!st.haveQuestItem(VB_SOLDIER, soilder_count))
            return false;
        if (!st.haveQuestItem(VB_CAPTAIN, capitan_count))
            return false;
        if (!st.haveQuestItem(VB_GENERAL, general_count))
            return false;
        if (other_item > 0 && !st.haveQuestItem(other_item))
            return false;

        if (take) {
            st.takeItems(VB_SOLDIER, soilder_count);
            st.takeItems(VB_CAPTAIN, capitan_count);
            st.takeItems(VB_GENERAL, general_count);
            if (other_item > 0)
                st.takeItems(other_item);
            st.takeItems(KETRA_MARKS);
        }
        return true;
    }

    static void checkMarks(QuestState st, List<Integer> MARKS) {
        if (st.getCond() == 0)
            return;
        for (int i = 0; i < MARKS.size(); i++)
            if (st.haveQuestItem(MARKS.get(i)))
                st.setCond(i + 1);
    }

    @Override
    public void onAbort(QuestState st) {
        st.takeItems(KETRA_MARKS);
        st.setCond(0);
        st.player.updateKetraVarka();
        st.playSound(SOUND_MIDDLE);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("first-2.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
            return event;
        }

        checkMarks(st, KETRA_MARKS);
        int cond = st.getCond();

        if ("first-have-2.htm".equalsIgnoreCase(event) && (cond == 1) && checkNextLevel(st, 100, 0, 0, 0, true)) {
            st.giveItems(KETRA_MARKS.get(0));
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if ("second-have-2.htm".equalsIgnoreCase(event) && (cond == 2) && checkNextLevel(st, 200, 100, 0, 0, true)) {
            st.giveItems(KETRA_MARKS.get(1));
            st.setCond(3);
            st.playSound(SOUND_MIDDLE);
        } else if ("third-have-2.htm".equalsIgnoreCase(event) && (cond == 3) && checkNextLevel(st, 300, 200, 100, 0, true)) {
            st.giveItems(KETRA_MARKS.get(2));
            st.setCond(4);
            st.playSound(SOUND_MIDDLE);
        } else if ("fourth-have-2.htm".equalsIgnoreCase(event) && (cond == 4) && checkNextLevel(st, 300, 300, 200, TOTEM_OF_VALOR, true)) {
            st.giveItems(KETRA_MARKS.get(3));
            st.setCond(5);
            st.playSound(SOUND_MIDDLE);
        } else if ("fifth-have-2.htm".equalsIgnoreCase(event) && cond == 5 && checkNextLevel(st, 400, 400, 200, TOTEM_OF_WISDOM, true)) {
            st.giveItems(KETRA_MARKS.get(4));
            st.setCond(6);
            st.playSound(SOUND_MIDDLE);
        } else if ("quit-2.htm".equalsIgnoreCase(event)) {
            st.takeItems(KETRA_MARKS);
            st.setCond(0);
            st.playSound(SOUND_MIDDLE);
            st.exitCurrentQuest(true);
        }
        st.player.updateKetraVarka();
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        if (st.player.getVarka() > 0) {
            st.exitCurrentQuest(true);
            return "isvarka.htm";
        }
        checkMarks(st, KETRA_MARKS);
        if (st.getState() == CREATED)
            st.setCond(0);
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == 31371) {
            if (cond == 0) {
                if (st.player.getLevel() < 74) {
                    st.exitCurrentQuest(true);
                    return "no-occupation.htm";
                }
                return "first.htm";
            }
            if (cond == 1)
                return checkNextLevel(st, 100, 0, 0, 0, false) ? "first-have.htm" : "first-havenot.htm";
            if (cond == 2)
                return checkNextLevel(st, 200, 100, 0, 0, false) ? "second-have.htm" : "second.htm";
            if (cond == 3)
                return checkNextLevel(st, 300, 200, 100, 0, false) ? "third-have.htm" : "third.htm";
            if (cond == 4)
                return checkNextLevel(st, 300, 300, 200, TOTEM_OF_VALOR, false) ? "fourth-have.htm" : "fourth.htm";
            if (cond == 5)
                return checkNextLevel(st, 400, 400, 200, TOTEM_OF_WISDOM, false) ? "fifth-have.htm" : "fifth.htm";
            if (cond == 6)
                return "high.htm";
        }
        return "noquest";
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (KETRA_NPC_LIST.contains(npcId)) {
            for (int i = 4; i > 0; i--) {
                if (st.haveQuestItem(KETRA_MARKS.get(i))) {
                    st.takeItems(KETRA_MARKS);
                    st.giveItems(KETRA_MARKS.get(i - 1));
                    st.player.updateKetraVarka();
                    break;
                }
            }
            if (st.player.getKetra() > 0) {
                st.player.updateKetraVarka();
                st.exitCurrentQuest(true);
                return;
            }
        }

        if (st.haveQuestItem(KETRA_MARKS.get(4)))
            return;

        int cond = st.getCond();
        if (VARKA_SOLDIERS.contains(npcId) && cond > 0)
            st.rollAndGive(VB_SOLDIER, 1, 60);
        if (VARKA_CAPTAINS.contains(npcId) && cond > 1)
            st.rollAndGive(VB_CAPTAIN, 1, 70);
        if (VARKA_GENERALS.contains(npcId) && cond > 2)
            st.rollAndGive(VB_GENERAL, 1, 80);
    }
}