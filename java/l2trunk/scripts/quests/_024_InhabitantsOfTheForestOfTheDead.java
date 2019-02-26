package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _024_InhabitantsOfTheForestOfTheDead extends Quest {
    // Список NPC
    private final int DORIAN = 31389;
    private final int TOMBSTONE = 31531;
    private final int MAID_OF_LIDIA = 31532;
    private final int MYSTERIOUS_WIZARD = 31522;
    //private final int BENEDICT = 31349;

    // Список итемов
    private final int LIDIA_HAIR_PIN = 7148;
    private final int SUSPICIOUS_TOTEM_DOLL = 7151;
    private final int FLOWER_BOUQUET = 7152;
    private final int SILVER_CROSS_OF_EINHASAD = 7153;
    private final int BROKEN_SILVER_CROSS_OF_EINHASAD = 7154;
    private final int SUSPICIOUS_TOTEM_DOLL1 = 7156;

    // Список мобов
    // Bone Snatchers, Bone Shapers, Bone Collectors, Bone Animators, Bone Slayers, Skull Collectors, Skull Animators
    private final List<Integer> MOBS = List.of(
            21557, 21558, 21560, 21561, 21562, 21563, 21564, 21565, 21566, 21567);

    public _024_InhabitantsOfTheForestOfTheDead() {
        super(false);

        addStartNpc(DORIAN);

        addTalkId(TOMBSTONE, MAID_OF_LIDIA, MYSTERIOUS_WIZARD);

        addKillId(MOBS);

        addQuestItem(SUSPICIOUS_TOTEM_DOLL);
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState qs) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = qs.getCond();

        if (npcId == DORIAN) {
            if (cond == 0) {
                if (qs.player.isQuestCompleted(_023_LidiasHeart.class))
                    htmltext = "31389-01.htm";
                else
                    htmltext = "31389-02.htm"; // Если 23 квест не пройден
            } else if (cond == 1 && qs.haveQuestItem(FLOWER_BOUQUET))
                htmltext = "31389-04.htm"; // Если букет еще в руках
            else if (cond == 2 && qs.getQuestItemsCount(FLOWER_BOUQUET) == 0)
                htmltext = "31389-05.htm";
            else if (cond == 3 && qs.getQuestItemsCount(SILVER_CROSS_OF_EINHASAD) == 1)
                htmltext = "31389-14.htm";
            else if (cond == 4 && qs.haveQuestItem(BROKEN_SILVER_CROSS_OF_EINHASAD)) {
                htmltext = "31389-15.htm";
                qs.takeItems(BROKEN_SILVER_CROSS_OF_EINHASAD, -1);
            } else if (cond == 7 && qs.getQuestItemsCount(LIDIA_HAIR_PIN) == 0) {
                htmltext = "31389-21.htm";
                qs.giveItems(LIDIA_HAIR_PIN, 1);
                qs.setCond(8);
            }
        } else if (npcId == TOMBSTONE) {
            if (cond == 1 && qs.haveQuestItem(FLOWER_BOUQUET))
                htmltext = "31531-01.htm";
            else if (cond == 2 && qs.getQuestItemsCount(FLOWER_BOUQUET) == 0)
                htmltext = "31531-03.htm"; // Если букет уже оставлен
        } else if (npcId == MAID_OF_LIDIA) {
            if (cond == 5)
                htmltext = "31532-01.htm";
            else if (cond == 6)
                if (qs.getQuestItemsCount(LIDIA_HAIR_PIN) == 0) {
                    htmltext = "31532-07.htm";
                    qs.setCond(7);
                } else
                    htmltext = "31532-05.htm";
            else if (cond == 8 && qs.haveQuestItem(LIDIA_HAIR_PIN))
                htmltext = "31532-10.htm";
            qs.takeItems(LIDIA_HAIR_PIN, -1);
        } else if (npcId == MYSTERIOUS_WIZARD) {
            if (cond == 10 && qs.haveQuestItem(SUSPICIOUS_TOTEM_DOLL))
                htmltext = "31522-01.htm";
            else if (cond == 11 && !qs.isRunningQuestTimer("To talk with Mystik") && qs.getQuestItemsCount(SUSPICIOUS_TOTEM_DOLL1) == 0)
                htmltext = "31522-09.htm";
            else if (cond == 11 && qs.haveQuestItem(SUSPICIOUS_TOTEM_DOLL1))
                htmltext = "31522-22.htm";
        }
        return htmltext;
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.startsWith("seePlayer")) {
            if (st.haveQuestItem(SILVER_CROSS_OF_EINHASAD)) {
                st.takeItems(SILVER_CROSS_OF_EINHASAD);
                st.giveItems(BROKEN_SILVER_CROSS_OF_EINHASAD);
                st.playSound(SOUND_HORROR2);
                st.setCond(4);
            }
            event = null;
        } else if ("31389-03.htm".equalsIgnoreCase(event)) {
            st.giveItems(FLOWER_BOUQUET);
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("31531-02.htm".equalsIgnoreCase(event)) {
            st.takeItems(FLOWER_BOUQUET);
            st.setCond(2);
        } else if ("31389-13.htm".equalsIgnoreCase(event)) {
            st.giveItems(SILVER_CROSS_OF_EINHASAD);
            st.setCond(3);
        } else if ("31389-19.htm".equalsIgnoreCase(event))
            st.setCond(5);
        else if ("31532-04.htm".equalsIgnoreCase(event)) {
            st.setCond(6);
            st.startQuestTimer("Lidias Letter", 7000);
        } else if ("Lidias Letter".equalsIgnoreCase(event))
            return "lidias_letter.htm";
        else if ("31532-06.htm".equalsIgnoreCase(event))
            st.takeItems(LIDIA_HAIR_PIN);
        else if ("31532-19.htm".equalsIgnoreCase(event))
            st.setCond(9);
        else if ("31522-03.htm".equalsIgnoreCase(event))
            st.takeItems(SUSPICIOUS_TOTEM_DOLL);
        else if ("31522-08.htm".equalsIgnoreCase(event)) {
            st.setCond(11);
            st.startQuestTimer("To talk with Mystik", 600000);
        } else if ("31522-21.htm".equalsIgnoreCase(event)) {
            st.giveItems(SUSPICIOUS_TOTEM_DOLL1);
            st.startQuestTimer("html", 5);
            return "Congratulations! You are completed this quest!" + " \n The Quest \"Hiding Behind the Truth\"" + " become available.\n Show Suspicious Totem Doll to " + " Priest Benedict.";
        } else if ("html".equalsIgnoreCase(event)) {
            st.playSound(SOUND_FINISH);
            st.addExpAndSp(242105, 22529);
            st.finish();
            return "31522-22.htm";
        }
        return event;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState qs) {
        if (qs == null || qs.getState() != STARTED)
            return;

        if (qs.getCond() == 9 && MOBS.contains(npc.getNpcId()) && Rnd.chance(70)) {
            qs.giveItems(SUSPICIOUS_TOTEM_DOLL);
            qs.playSound(SOUND_MIDDLE);
            qs.setCond(10);
        }
    }

    @Override
    public void onAbort(QuestState st) {
        st.giveItemIfNotHave(LIDIA_HAIR_PIN);
    }
}
