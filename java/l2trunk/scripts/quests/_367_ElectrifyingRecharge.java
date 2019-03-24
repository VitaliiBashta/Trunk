package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.stream.IntStream;

public final class _367_ElectrifyingRecharge extends Quest {
    //NPCs
    private static final int LORAIN = 30673;
    //Mobs
    private static final int CATHEROK = 21035;
    //Quest items
    private static final int TITAN_LAMP_FIRST = 5875;
    private static final int TITAN_LAMP_LAST = 5879;
    private static final int Broken_Titan_Lamp = 5880;
    //Chances
    private static final int broke_chance = 3;
    private static final int uplight_chance = 7;

    public _367_ElectrifyingRecharge() {
        addStartNpc(LORAIN);
        addKillId(CATHEROK);
        addQuestItem(IntStream.rangeClosed(TITAN_LAMP_FIRST, TITAN_LAMP_LAST).toArray());
        addQuestItem(Broken_Titan_Lamp);
    }

    private static boolean takeAllLamps(QuestState st) {
        boolean result = false;
        for (int Titan_Lamp_id = TITAN_LAMP_FIRST; Titan_Lamp_id <= TITAN_LAMP_LAST; Titan_Lamp_id++)
            if (st.haveQuestItem(Titan_Lamp_id)) {
                result = true;
                st.takeItems(Titan_Lamp_id);
            }
        if (st.haveQuestItem(Broken_Titan_Lamp)) {
            result = true;
            st.takeItems(Broken_Titan_Lamp);
        }
        return result;
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int state = st.getState();
        if ("30673-03.htm".equalsIgnoreCase(event) && state == CREATED) {
            takeAllLamps(st);
            st.giveItems(TITAN_LAMP_FIRST);
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("30673-07.htm".equalsIgnoreCase(event) && state == STARTED) {
            takeAllLamps(st);
            st.giveItems(TITAN_LAMP_FIRST);
        } else if ("30673-08.htm".equalsIgnoreCase(event) && state == STARTED) {
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        if (npc.getNpcId() != LORAIN)
            return htmltext;
        int state = st.getState();

        if (state == CREATED) {
            if (st.player.getLevel() < 37) {
                htmltext = "30673-02.htm";
                st.exitCurrentQuest();
            } else {
                htmltext = "30673-01.htm";
                st.setCond(0);
            }
        } else if (state == STARTED)
            if (st.haveQuestItem(TITAN_LAMP_LAST)) {
                htmltext = "30673-06.htm";
                takeAllLamps(st);
                st.giveItems(4553 + Rnd.get(12));
                st.playSound(SOUND_MIDDLE);
            } else if (st.haveQuestItem(Broken_Titan_Lamp)) {
                htmltext = "30673-05.htm";
                takeAllLamps(st);
                st.giveItems(TITAN_LAMP_FIRST);
            } else
                htmltext = "30673-04.htm";

        return htmltext;
    }

    @Override
    public void onAttack(NpcInstance npc, QuestState qs) {
        if (qs.getState() != STARTED)
            return;
        if (qs.haveQuestItem(Broken_Titan_Lamp))
            return;

        if (Rnd.chance(uplight_chance))
            for (int Titan_Lamp_id = TITAN_LAMP_FIRST; Titan_Lamp_id < TITAN_LAMP_LAST; Titan_Lamp_id++)
                if (qs.haveQuestItem(Titan_Lamp_id)) {
                    int Titan_Lamp_Next = Titan_Lamp_id + 1;
                    takeAllLamps(qs);
                    qs.giveItems(Titan_Lamp_Next);
                    if (Titan_Lamp_Next == TITAN_LAMP_LAST) {
                        qs.setCond(2);
                        qs.playSound(SOUND_MIDDLE);
                    } else
                        qs.playSound(SOUND_ITEMGET);
                    npc.doCast(4072, 4, qs.player, true);
                    return;
                } else if (Rnd.chance(broke_chance))
                    if (takeAllLamps(qs))
                        qs.giveItems(Broken_Titan_Lamp);

    }
}