package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.instancemanager.ServerVariables;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.utils.Location;

public final class _610_MagicalPowerofWater2 extends Quest {
    // NPC
    private static final int ASEFA = 31372;
    private static final int VARKAS_HOLY_ALTAR = 31560;

    // Quest items
    private static final int GREEN_TOTEM = 7238;
    private final int ICE_HEART_OF_ASHUTAR = 7239;

    private static final int Reward_First = 4589;
    private static final int Reward_Last = 4594;

    private static final int SoulOfWaterAshutar = 25316;
    private NpcInstance SoulOfWaterAshutarSpawn = null;

    public _610_MagicalPowerofWater2() {
        super(true);

        addStartNpc(ASEFA);

        addTalkId(VARKAS_HOLY_ALTAR);

        addKillId(SoulOfWaterAshutar);

        addQuestItem(ICE_HEART_OF_ASHUTAR);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        NpcInstance isQuest = GameObjectsStorage.getByNpcId(SoulOfWaterAshutar);
        String htmltext = event;
        if ("quest_accept".equalsIgnoreCase(event)) {
            htmltext = "shaman_asefa_q0610_0104.htm";
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("610_1".equals(event)) {
            if (ServerVariables.getLong(getClass().getSimpleName()) + 3 * 60 * 60 * 1000 > System.currentTimeMillis())
                htmltext = "totem_of_barka_q0610_0204.htm";
            else if (st.haveQuestItem(GREEN_TOTEM) && isQuest == null) {
                st.takeItems(GREEN_TOTEM, 1);
                SoulOfWaterAshutarSpawn = st.addSpawn(SoulOfWaterAshutar, Location.of(104825, -36926, -1136));
                SoulOfWaterAshutarSpawn.addListener(new DeathListener());
                st.playSound(SOUND_MIDDLE);
            } else
                htmltext = "totem_of_barka_q0610_0203.htm";
        } else if ("610_3".equals(event))
            if (st.haveQuestItem(ICE_HEART_OF_ASHUTAR) ) {
                st.takeItems(ICE_HEART_OF_ASHUTAR);
                st.giveItems(Rnd.get(Reward_First, Reward_Last), 5, true);
                st.playSound(SOUND_FINISH);
                htmltext = "shaman_asefa_q0610_0301.htm";
                st.exitCurrentQuest();
            } else
                htmltext = "shaman_asefa_q0610_0302.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        NpcInstance isQuest = GameObjectsStorage.getByNpcId(SoulOfWaterAshutar);
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == ASEFA) {
            if (cond == 0) {
                if (st.player.getLevel() >= 75) {
                    if (st.haveQuestItem(GREEN_TOTEM))
                        htmltext = "shaman_asefa_q0610_0101.htm";
                    else {
                        htmltext = "shaman_asefa_q0610_0102.htm";
                        st.exitCurrentQuest();
                    }
                } else {
                    htmltext = "shaman_asefa_q0610_0103.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "shaman_asefa_q0610_0105.htm";
            else if (cond == 2)
                htmltext = "shaman_asefa_q0610_0202.htm";
            else if (cond == 3 && st.haveQuestItem(ICE_HEART_OF_ASHUTAR))
                htmltext = "shaman_asefa_q0610_0201.htm";
        } else if (npcId == VARKAS_HOLY_ALTAR)
            if (!npc.isBusy()) {
                if (ServerVariables.getLong(getClass().getSimpleName()) + 3 * 60 * 60 * 1000 > System.currentTimeMillis())
                    htmltext = "totem_of_barka_q0610_0204.htm";
                else if (cond == 1)
                    htmltext = "totem_of_barka_q0610_0101.htm";
                else if (cond == 2 && isQuest == null) {
                    SoulOfWaterAshutarSpawn = st.addSpawn(SoulOfWaterAshutar, Location.of(104825, -36926, -1136));
                    SoulOfWaterAshutarSpawn.addListener(new DeathListener());
                    htmltext = "totem_of_barka_q0610_0204.htm";
                }
            } else
                htmltext = "totem_of_barka_q0610_0202.htm";
        return htmltext;
    }

    private static class DeathListener implements OnDeathListener {
        @Override
        public void onDeath(Creature actor, Creature killer) {
            ServerVariables.set(getClass().getSimpleName(), String.valueOf(System.currentTimeMillis()));
        }
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (npc.getNpcId() == SoulOfWaterAshutar) {
            st.giveItemIfNotHave(ICE_HEART_OF_ASHUTAR);
            st.setCond(3);
            if (SoulOfWaterAshutarSpawn != null)
                SoulOfWaterAshutarSpawn.deleteMe();
            SoulOfWaterAshutarSpawn = null;
        }
    }
}