package l2trunk.scripts.quests;

import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.ScriptFile;

public final class _143_FallenAngelRequestOfDusk extends Quest {
    // NPCs
    private final static int NATOOLS = 30894;
    private final static int TOBIAS = 30297;
    private final static int CASIAN = 30612;
    private final static int ROCK = 32368;
    private final static int ANGEL = 32369;

    private final static int MonsterAngel = 27338;

    // items
    private final static int SEALED_PATH = 10354;
    private final static int PATH = 10355;
    private final static int EMPTY_CRYSTAL = 10356;
    private final static int MEDICINE = 10357;
    private final static int MESSAGE = 10358;

    public _143_FallenAngelRequestOfDusk() {
        super(false);

        // Нет стартового NPC, чтобы квест не появлялся в списке раньше времени
        addTalkId(NATOOLS, TOBIAS, CASIAN, ROCK, ANGEL);
        addQuestItem(SEALED_PATH, PATH, EMPTY_CRYSTAL, MEDICINE, MESSAGE);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("start".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
            htmltext = "warehouse_chief_natools_q0143_01.htm";
        } else if ("warehouse_chief_natools_q0143_04.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.start();
            st.playSound(SOUND_MIDDLE);
            st.giveItems(SEALED_PATH, 1);
        } else if ("master_tobias_q0143_05.htm".equalsIgnoreCase(event)) {
            st.setCond(3);
            st.start();
            st.unset("talk");
            st.playSound(SOUND_MIDDLE);
            st.giveItems(PATH);
            st.giveItems(EMPTY_CRYSTAL);
        } else if ("sage_kasian_q0143_09.htm".equalsIgnoreCase(event)) {
            st.setCond(4);
            st.start();
            st.unset("talk");
            st.giveItems(MEDICINE);
            st.playSound(SOUND_MIDDLE);
        } else if ("stained_rock_q0143_05.htm".equalsIgnoreCase(event)) {
            if (GameObjectsStorage.getByNpcId(MonsterAngel) != null)
                htmltext = "stained_rock_q0143_03.htm";
            else if (GameObjectsStorage.getByNpcId(ANGEL) != null)
                htmltext = "stained_rock_q0143_04.htm";
            else {
                st.addSpawn(ANGEL, 180000);
                st.playSound(SOUND_MIDDLE);
            }
        } else if ("q_fallen_angel_npc_q0143_14.htm".equalsIgnoreCase(event)) {
            st.setCond(5);
            st.start();
            st.unset("talk");
            st.takeItems(EMPTY_CRYSTAL);
            st.giveItems(MESSAGE);
            st.playSound(SOUND_MIDDLE);

            NpcInstance n = GameObjectsStorage.getByNpcId(ANGEL);
            if (n != null)
                n.deleteMe();
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        int npcId = npc.getNpcId();
        if (npcId == NATOOLS) {
            if (cond == 1 || st.isStarted() && cond == 0)
                htmltext = "warehouse_chief_natools_q0143_01.htm";
            else if (cond == 2)
                htmltext = "warehouse_chief_natools_q0143_05.htm";
        } else if (npcId == TOBIAS) {
            if (cond == 2)
                if (st.isSet("talk"))
                    htmltext = "master_tobias_q0143_03.htm";
                else {
                    htmltext = "master_tobias_q0143_02.htm";
                    st.takeItems(SEALED_PATH);
                    st.set("talk");
                }
            else if (cond == 3)
                htmltext = "master_tobias_q0143_06.htm";
            else if (cond == 5) {
                htmltext = "master_tobias_q0143_07.htm";
                st.playSound(SOUND_FINISH);
                st.giveItems(ADENA_ID, 89046);
                st.finish();
            }
        } else if (npcId == CASIAN) {
            if (cond == 3) {
                if (st.isSet("talk"))
                    htmltext = "sage_kasian_q0143_03.htm";
                else {
                    htmltext = "sage_kasian_q0143_02.htm";
                    st.takeItems(PATH);
                    st.set("talk");
                }
            } else if (cond == 4)
                htmltext = "sage_kasian_q0143_09.htm";
        } else if (npcId == ROCK) {
            if (cond <= 3)
                htmltext = "stained_rock_q0143_01.htm";
            else if (cond == 4)
                htmltext = "stained_rock_q0143_02.htm";
            else
                htmltext = "stained_rock_q0143_06.htm";
        } else if (npcId == ANGEL) {
            if (cond == 4)
                if (st.isSet("talk"))
                    htmltext = "q_fallen_angel_npc_q0143_04.htm";
                else {
                    htmltext = "q_fallen_angel_npc_q0143_03.htm";
                    st.takeItems(MEDICINE);
                    st.set("talk");
                }
        } else if (cond == 5)
            htmltext = "q_fallen_angel_npc_q0143_14.htm";
        return htmltext;
    }
}