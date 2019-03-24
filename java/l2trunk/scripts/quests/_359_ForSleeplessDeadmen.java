package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.instancemanager.QuestManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _359_ForSleeplessDeadmen extends Quest {

    //Variables
    private static final int DROP_RATE = 10;

    private static final int REQUIRED = 60; //how many items will be paid for a reward

    //Quest items
    private static final int REMAINS = 5869;

    //Rewards
    private static final int PhoenixEarrPart = 6341;
    private static final int MajEarrPart = 6342;
    private static final int PhoenixNeclPart = 6343;
    private static final int MajNeclPart = 6344;
    private static final int PhoenixRingPart = 6345;
    private static final int MajRingPart = 6346;

    private static final int DarkCryShieldPart = 5494;
    private static final int NightmareShieldPart = 5495;

    //NPCs
    private static final int ORVEN = 30857;

    //Mobs
    private static final int DOOMSERVANT = 21006;
    private static final int DOOMGUARD = 21007;
    private static final int DOOMARCHER = 21008;
    private static final int DOOMTROOPER = 21009;

    public _359_ForSleeplessDeadmen() {
        addStartNpc(ORVEN);

        addKillId(DOOMSERVANT,DOOMGUARD,DOOMARCHER,DOOMTROOPER);

        addQuestItem(REMAINS);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("30857-06.htm".equalsIgnoreCase(event)) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("30857-07.htm".equalsIgnoreCase(event)) {
            // 713 quest hook
            Castle castle = ResidenceHolder.getCastle(5);
            if (castle.getOwner() != null) {
                Player castleOwner = castle.getOwner().getLeader().player;
                Quest quest = QuestManager.getQuest(_713_PathToBecomingALordAden.class);
                if (castleOwner != null && castleOwner != st.player && castleOwner.getClan() == st.player.getClan() && castleOwner.getQuestState(quest) != null && castleOwner.getQuestState(quest).getCond() == 2) {
                    if (castleOwner.getQuestState(quest).getInt("questsDone") != 0) {
                        if (castleOwner.getQuestState(quest).getInt("questsDone") < 5)
                            castleOwner.getQuestState(quest).inc("questsDone");
                        else
                            castleOwner.getQuestState(quest).setCond(4);
                    } else
                        castleOwner.getQuestState(quest).set("questsDone");

                }
            }
            //---------------
            st.exitCurrentQuest();
            st.playSound(SOUND_FINISH);
        } else if ("30857-08.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            //Vibor nagradi
            int chance = Rnd.get(100);
            int item;
            if (chance <= 16)
                item = PhoenixNeclPart;
            else if (chance <= 33)
                item = PhoenixEarrPart;
            else if (chance <= 50)
                item = PhoenixRingPart;
            else if (chance <= 58)
                item = MajNeclPart;
            else if (chance <= 67)
                item = MajEarrPart;
            else if (chance <= 76)
                item = MajRingPart;
            else if (chance <= 84)
                item = DarkCryShieldPart;
            else
                item = NightmareShieldPart;
            st.giveItems(item, 4, true);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int id = st.getState();
        int cond = st.getCond();
        if (id == CREATED) {
            if (st.player.getLevel() < 60) {
                st.exitCurrentQuest();
                htmltext = "30857-01.htm";
            } else
                htmltext = "30857-02.htm";
        } else if (id == STARTED) {
            if (cond == 3)
                htmltext = "30857-03.htm";
            else if (cond == 2 && st.haveQuestItem(REMAINS, REQUIRED)) {
                st.takeItems(REMAINS);
                st.setCond(3);
                htmltext = "30857-04.htm";
            }
        } else
            htmltext = "30857-05.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        long count = st.getQuestItemsCount(REMAINS);
        if (count < REQUIRED && Rnd.chance(DROP_RATE)) {
            st.giveItems(REMAINS);
            if (count + 1 >= REQUIRED) {
                st.playSound(SOUND_MIDDLE);
                st.setCond(2);
            } else
                st.playSound(SOUND_ITEMGET);
        }
    }
}