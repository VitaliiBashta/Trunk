package l2trunk.scripts.quests;

import l2trunk.gameserver.instancemanager.ServerVariables;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;

public final class _604_DaimontheWhiteEyedPart2 extends Quest {
    //NPC
    private static final int EYE = 31683;
    private static final int ALTAR = 31541;
    //MOBS
    private static final int DAIMON = 25290;
    //ITEMS
    private static final int U_SUMMON = 7192;
    private static final int S_SUMMON = 7193;
    private static final int ESSENCE = 7194;

    //REWARDS dye +2int-2men/+2int-2wit/+2men-2int/+2men-2wit/+2wit-2int/+2wit-2men
    private static final int INT_MEN = 4595;
    private static final int INT_WIT = 4596;
    private static final int MEN_INT = 4597;
    private static final int MEN_WIT = 4598;
    private static final int WIT_INT = 4599;
    private static final int WIT_MEN = 4600;

    public _604_DaimontheWhiteEyedPart2() {
        super(true);

        addStartNpc(EYE);
        addTalkId(ALTAR);
        addKillId(DAIMON);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        NpcInstance isQuest = GameObjectsStorage.getByNpcId(DAIMON);
        if ("31683-02.htm".equalsIgnoreCase(event)) {
            if (st.player.getLevel() < 73) {
                st.exitCurrentQuest();
                return "31683-00b.htm";
            }
            st.setCond(1);
            st.start();
            st.takeItems(U_SUMMON, 1);
            st.giveItems(S_SUMMON);
            st.playSound(SOUND_ACCEPT);
        } else if ("31541-02.htm".equalsIgnoreCase(event)) {
            if (!st.haveQuestItem(S_SUMMON) )
                return "31541-04.htm";
            if (isQuest != null)
                return "31541-03.htm";
            if (ServerVariables.getLong(getClass().getSimpleName(), 0) + 3 * 60 * 60 * 1000 > System.currentTimeMillis())
                return "31541-05.htm";
            st.takeItems(S_SUMMON, 1);
            isQuest = st.addSpawn(DAIMON, Location.of(186320, -43904, -3175));
            Functions.npcSay(isQuest, "Who called me?");
            isQuest.addListener(new DeathListener());
            st.playSound(SOUND_MIDDLE);
            st.setCond(2);
            st.start();
            st.player.sendMessage("Daimon the White-Eyed has spawned in 186320, -43904, -3175");
            st.startQuestTimer("DAIMON_Fail", 12000000);
        } else if ("31683-04.htm".equalsIgnoreCase(event)) {
            if (st.haveQuestItem(ESSENCE) )
                return "list.htm";
            st.exitCurrentQuest();
            return "31683-05.htm";
        } else if ("INT_MEN".equalsIgnoreCase(event)) {
            st.giveItems(INT_MEN, 5, true);
            st.takeItems(ESSENCE, 1);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
            return null;
        } else if ("INT_WIT".equalsIgnoreCase(event)) {
            st.giveItems(INT_WIT, 5, true);
            st.takeItems(ESSENCE, 1);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
            return null;
        } else if ("MEN_INT".equalsIgnoreCase(event)) {
            st.giveItems(MEN_INT, 5, true);
            st.takeItems(ESSENCE, 1);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
            return null;
        } else if ("MEN_WIT".equalsIgnoreCase(event)) {
            st.giveItems(MEN_WIT, 5, true);
            st.takeItems(ESSENCE, 1);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
            return null;
        } else if ("WIT_INT".equalsIgnoreCase(event)) {
            st.giveItems(WIT_INT, 5, true);
            st.takeItems(ESSENCE, 1);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
            return null;
        } else if ("WIT_MEN".equalsIgnoreCase(event)) {
            st.giveItems(WIT_MEN, 5, true);
            st.takeItems(ESSENCE, 1);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
            return null;
        } else if ("DAIMON_Fail".equalsIgnoreCase(event) && isQuest != null) {
            Functions.npcSay(isQuest, "Darkness could not have ray?");
            isQuest.deleteMe();
            return null;
        }
        return event;
    }

    private static class DeathListener implements OnDeathListener {
        @Override
        public void onDeath(Creature actor, Creature killer) {
            ServerVariables.set(getClass().getSimpleName(), String.valueOf(System.currentTimeMillis()));
        }
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        NpcInstance isQuest = GameObjectsStorage.getByNpcId(DAIMON);
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 0) {
            if (npcId == EYE)
                if (st.haveQuestItem(U_SUMMON))
                    htmltext = "31683-01.htm";
                else
                    htmltext = "31683-00a.htm";
        } else if (cond == 1) {
            if (npcId == EYE)
                htmltext = "31683-02a.htm";
            else if (npcId == ALTAR)
                if (ServerVariables.getLong(getClass().getSimpleName(), 0) + 3 * 60 * 60 * 1000 > System.currentTimeMillis())
                    htmltext = "31541-05.htm";
                else
                    htmltext = "31541-01.htm";
        } else if (cond == 2) {
            if (npcId == ALTAR)
                if (isQuest != null)
                    htmltext = "31541-03.htm";
                else if (ServerVariables.getLong(getClass().getSimpleName(), 0) + 3 * 60 * 60 * 1000 > System.currentTimeMillis())
                    htmltext = "31541-05.htm";
                else {
                    isQuest = st.addSpawn(DAIMON, Location.of(186320, -43904, -3175));
                    Functions.npcSay(isQuest, "Who called me?");
                    st.playSound(SOUND_MIDDLE);
                    st.start();
                    st.player.sendMessage("Daimon the White-Eyed has spawned in 186320, -43904, -3175");
                    isQuest.addListener(new DeathListener());
                    st.startQuestTimer("DAIMON_Fail", 12000000);
                }
        } else if (cond == 3) {
            if (npcId == EYE)
                if (st.haveQuestItem(ESSENCE))
                    htmltext = "31683-03.htm";
                else
                    htmltext = "31683-06.htm";
            if (npcId == ALTAR)
                htmltext = "31541-05.htm";
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.haveQuestItem(S_SUMMON)) {
            st.takeItems(S_SUMMON, 1);
            st.giveItems(ESSENCE);
            st.setCond(3);
            st.start();
            st.playSound(SOUND_MIDDLE);
        }
    }
}