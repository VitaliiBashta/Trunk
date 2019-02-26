package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.HashMap;
import java.util.Map;

public final class _633_InTheForgottenVillage extends Quest {
    // NPC
    private static final int MINA = 31388;
    // ITEMS
    private static final int RIB_BONE = 7544;
    private static final int Z_LIVER = 7545;

    // Mobid : DROP CHANCES
    private static final Map<Integer, Double> DAMOBS = new HashMap<>();
    private static final Map<Integer, Double> UNDEADS = new HashMap<>();

    public _633_InTheForgottenVillage() {
        super(true);

        DAMOBS.put(21557, 32.8); // Bone Snatcher
        DAMOBS.put(21558, 32.8); // Bone Snatcher
        DAMOBS.put(21559, 33.7); // Bone Maker
        DAMOBS.put(21560, 33.7); // Bone Shaper
        DAMOBS.put(21563, 34.2); // Bone Collector
        DAMOBS.put(21564, 34.8); // Skull Collector
        DAMOBS.put(21565, 35.1); // Bone Animator
        DAMOBS.put(21566, 35.9); // Skull Animator
        DAMOBS.put(21567, 35.9); // Bone Slayer
        DAMOBS.put(21572, 36.5); // Bone Sweeper
        DAMOBS.put(21574, 38.3); // Bone Grinder
        DAMOBS.put(21575, 38.3); // Bone Grinder
        DAMOBS.put(21580, 38.5); // Bone Caster
        DAMOBS.put(21581, 39.5); // Bone Puppeteer
        DAMOBS.put(21583, 39.7); // Bone Scavenger
        DAMOBS.put(21584, 40.1); // Bone Scavenger

        UNDEADS.put(21553, 34.7); // Trampled Man
        UNDEADS.put(21554, 34.7); // Trampled Man
        UNDEADS.put(21561, 45.0); // Sacrificed Man
        UNDEADS.put(21578, 50.1); // Behemoth Zombie
        UNDEADS.put(21596, 35.9); // Requiem Lord
        UNDEADS.put(21597, 37.0); // Requiem Behemoth
        UNDEADS.put(21598, 44.1); // Requiem Behemoth
        UNDEADS.put(21599, 39.5); // Requiem Priest
        UNDEADS.put(21600, 40.8); // Requiem Behemoth
        UNDEADS.put(21601, 41.1); // Requiem Behemoth

        addStartNpc(MINA);
        addQuestItem(RIB_BONE);
        addKillId(UNDEADS.keySet());
        addKillId(DAMOBS.keySet());
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("quest_accept".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
            htmltext = "day_mina_q0633_0104.htm";
        }
        if ("633_4".equals(event)) {
            st.takeItems(RIB_BONE);
            st.playSound(SOUND_FINISH);
            htmltext = "day_mina_q0633_0204.htm";
            st.exitCurrentQuest();
        } else if ("633_1".equals(event))
            htmltext = "day_mina_q0633_0201.htm";
        else if ("633_3".equals(event))
            if (st.getCond() == 2)
                if (st.haveQuestItem(RIB_BONE, 200)) {
                    st.takeItems(RIB_BONE);
                    st.giveItems(ADENA_ID, 25000);
                    st.addExpAndSp(305235, 0);
                    st.playSound(SOUND_FINISH);
                    st.setCond(1);
                    htmltext = "day_mina_q0633_0202.htm";
                } else
                    htmltext = "day_mina_q0633_0203.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        int id = st.getState();
        if (npcId == MINA)
            if (id == CREATED) {
                if (st.player.getLevel() >= 65)
                    htmltext = "day_mina_q0633_0101.htm";
                else {
                    htmltext = "day_mina_q0633_0103.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "day_mina_q0633_0106.htm";
            else if (cond == 2)
                htmltext = "day_mina_q0633_0105.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (UNDEADS.containsKey(npcId))
            st.rollAndGive(Z_LIVER, 1, UNDEADS.get(npcId));
        else if (DAMOBS.containsKey(npcId)) {
            long count = st.getQuestItemsCount(RIB_BONE);
            if (count < 200 && Rnd.chance(DAMOBS.get(npcId))) {
                st.giveItems(RIB_BONE);
                if (count >= 199) {
                    st.setCond(2);
                    st.playSound(SOUND_MIDDLE);
                } else
                    st.playSound(SOUND_ITEMGET);
            }
        }
    }
}