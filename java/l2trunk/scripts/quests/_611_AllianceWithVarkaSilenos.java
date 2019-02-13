package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;
import java.util.stream.Collectors;

import static l2trunk.scripts.quests._605_AllianceWithKetraOrcs.*;

public final class _611_AllianceWithVarkaSilenos extends Quest {
    // Items
    public static final List<Integer> VARKA_MARKS = List.of(7221, 7222, 7223, 7224, 7225);
    private static final int KB_SOLDIER = 7226;
    private static final int KB_CAPTAIN = 7227;
    private static final int KB_GENERAL = 7228;
    private static final int TOTEM_OF_VALOR = 7229;
    private static final int TOTEM_OF_WISDOM = 7230;
    //hunt for soldier
    static final List<Integer> KETRA_SOILDERS = List.of(21327, 21324, 21328, 21325, 21329);
    //hunt for captain
    static final List<Integer> KETRA_CAPTAINS = List.of(
            21338, 21331, 21332, 21335, 21334, 21343, 21344, 21336);
    //hunt for general
    static final List<Integer> KETRA_GENERALS = List.of(
            21340, 21339, 21342, 21347, 21375,
            21348, 21349, 21345, 21346);
    //Varka mobs
    static final List<Integer> VARKA_NPC_LIST = List.of(VARKA_SOLDIERS,VARKA_CAPTAINS,VARKA_GENERALS).stream()
            .flatMap(List::stream).collect(Collectors.toList());

    public _611_AllianceWithVarkaSilenos() {
        super(true);

        addStartNpc(31378);


        addKillId(VARKA_NPC_LIST);

        //hunt for soldier
        addKillId(KETRA_SOILDERS);

        //hunt for captain
        addKillId(KETRA_CAPTAINS);

        //hunt for general
        addKillId(KETRA_GENERALS);

        addQuestItem(KB_SOLDIER);
        addQuestItem(KB_CAPTAIN);
        addQuestItem(KB_GENERAL);
    }

    private static void takeAllMarks(QuestState st) {
        st.takeItems(VARKA_MARKS);
    }


    private static boolean checkNextLevel(QuestState st, int soilder_count, int capitan_count, int general_count, int other_item, boolean take) {
        if (soilder_count > 0 && st.getQuestItemsCount(KB_SOLDIER) < soilder_count)
            return false;
        if (capitan_count > 0 && st.getQuestItemsCount(KB_CAPTAIN) < capitan_count)
            return false;
        if (general_count > 0 && st.getQuestItemsCount(KB_GENERAL) < general_count)
            return false;
        if (other_item > 0 && !st.haveQuestItem(other_item))
            return false;

        if (take) {
            if (soilder_count > 0)
                st.takeItems(KB_SOLDIER, soilder_count);
            if (capitan_count > 0)
                st.takeItems(KB_CAPTAIN, capitan_count);
            if (general_count > 0)
                st.takeItems(KB_GENERAL, general_count);
            if (other_item > 0)
                st.takeItems(other_item);
            st.takeItems(VARKA_MARKS);
        }
        return true;
    }

    @Override
    public void onAbort(QuestState st) {
        takeAllMarks(st);
        st.setCond(0);
        st.player.updateKetraVarka();
        st.playSound(SOUND_MIDDLE);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("herald_naran_q0611_04.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
            return event;
        }

        checkMarks(st, VARKA_MARKS);
        int cond = st.getCond();

        if ("herald_naran_q0611_12.htm".equalsIgnoreCase(event) && cond == 1 && checkNextLevel(st, 100, 0, 0, 0, true)) {
            st.giveItems(VARKA_MARKS.get(0));
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if ("herald_naran_q0611_15.htm".equalsIgnoreCase(event) && cond == 2 && checkNextLevel(st, 200, 100, 0, 0, true)) {
            st.giveItems(VARKA_MARKS.get(1));
            st.setCond(3);
            st.playSound(SOUND_MIDDLE);
        } else if ("herald_naran_q0611_18.htm".equalsIgnoreCase(event) && cond == 3 && checkNextLevel(st, 300, 200, 100, 0, true)) {
            st.giveItems(VARKA_MARKS.get(2));
            st.setCond(4);
            st.playSound(SOUND_MIDDLE);
        } else if ("herald_naran_q0611_21.htm".equalsIgnoreCase(event) && cond == 4 && checkNextLevel(st, 300, 300, 200, TOTEM_OF_VALOR, true)) {
            st.giveItems(VARKA_MARKS.get(3));
            st.setCond(5);
            st.playSound(SOUND_MIDDLE);
        } else if ("herald_naran_q0611_23.htm".equalsIgnoreCase(event) && cond == 5 && checkNextLevel(st, 400, 400, 200, TOTEM_OF_WISDOM, true)) {
            st.giveItems(VARKA_MARKS.get(4));
            st.setCond(6);
            st.playSound(SOUND_MIDDLE);
        } else if ("herald_naran_q0611_26.htm".equalsIgnoreCase(event)) {
            takeAllMarks(st);
            st.setCond(0);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest(true);
        }
        st.player.updateKetraVarka();
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        if (st.player.getKetra() > 0) {
            st.exitCurrentQuest(true);
            return "herald_naran_q0611_02.htm";
        }
        int npcId = npc.getNpcId();
        checkMarks(st, VARKA_MARKS);
        if (st.getState() == CREATED)
            st.setCond(0);
        int cond = st.getCond();
        if (npcId == 31378) {
            if (cond == 0) {
                if (st.player.getLevel() < 74) {
                    st.exitCurrentQuest(true);
                    return "herald_naran_q0611_03.htm";
                }
                return "herald_naran_q0611_01.htm";
            }
            switch (cond) {
                case 1:
                    return checkNextLevel(st, 100, 0, 0, 0, false) ? "herald_naran_q0611_11.htm" : "herald_naran_q0611_10.htm";
                case 2:
                    return checkNextLevel(st, 200, 100, 0, 0, false) ? "herald_naran_q0611_14.htm" : "herald_naran_q0611_13.htm";
                case 3:
                    return checkNextLevel(st, 300, 200, 100, 0, false) ? "herald_naran_q0611_17.htm" : "herald_naran_q0611_16.htm";
                case 4:
                    return checkNextLevel(st, 300, 300, 200, TOTEM_OF_VALOR, false) ? "herald_naran_q0611_20.htm" : "herald_naran_q0611_19.htm";
                case 5:
                    return checkNextLevel(st, 400, 400, 200, TOTEM_OF_WISDOM, false) ? "herald_naran_q0611_27.htm" : "herald_naran_q0611_22.htm";
                case 6:
                    return "herald_naran_q0611_24.htm";
            }
        }
        return "noquest";
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (VARKA_NPC_LIST.contains(npcId))
            if (st.haveQuestItem(VARKA_MARKS.get(4))) {
                st.takeItems(VARKA_MARKS);
                st.giveItems(VARKA_MARKS.get(3));
                st.player.updateKetraVarka();
                checkMarks(st, VARKA_MARKS);
            } else if (st.haveQuestItem(VARKA_MARKS.get(3))) {
                takeAllMarks(st);
                st.giveItems(VARKA_MARKS.get(2));
                st.player.updateKetraVarka();
                checkMarks(st, VARKA_MARKS);
            } else if (st.haveQuestItem(VARKA_MARKS.get(2))) {
                takeAllMarks(st);
                st.giveItems(VARKA_MARKS.get(1));
                st.player.updateKetraVarka();
                checkMarks(st, VARKA_MARKS);
            } else if (st.haveQuestItem(VARKA_MARKS.get(1))) {
                takeAllMarks(st);
                st.giveItems(VARKA_MARKS.get(0));
                st.player.updateKetraVarka();
                checkMarks(st, VARKA_MARKS);
            } else if (st.haveQuestItem(VARKA_MARKS.get(0))) {
                takeAllMarks(st);
                st.player.updateKetraVarka();
                checkMarks(st, VARKA_MARKS);
            } else if (st.player.getVarka() > 0) {
                st.player.updateKetraVarka();
                st.exitCurrentQuest(true);
                return;
            }

        if (st.haveQuestItem(VARKA_MARKS.get(4)))
            return;

        int cond = st.getCond();
        if (KETRA_SOILDERS.contains(npcId) && cond > 0)
            st.rollAndGive(KB_SOLDIER, 1, 60);
        if (KETRA_CAPTAINS.contains(npcId) && cond > 1)
            st.rollAndGive(KB_CAPTAIN, 1, 70);
        if (KETRA_GENERALS.contains(npcId) && cond > 2)
            st.rollAndGive(KB_GENERAL, 1, 80);
    }
}