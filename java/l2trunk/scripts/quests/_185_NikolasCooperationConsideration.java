package l2trunk.scripts.quests;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;

public final class _185_NikolasCooperationConsideration extends Quest {
    private static final int Lorain = 30673;
    private static final int Nikola = 30621;
    private static final int Device = 32366;
    private static final int Alarm = 32367;

    private static final int Certificate = 10362;
    private static final int Metal = 10359;
    private static final int BrokenMetal = 10360;
    private static final int NicolasMap = 10361;

    public _185_NikolasCooperationConsideration() {
        // Нет стартового NPC, чтобы квест не появлялся в списке раньше времени
        addTalkId(Lorain, Nikola, Device, Alarm);
        addQuestItem(NicolasMap, BrokenMetal, Metal);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        Player player = st.player;

        if ("30621-01.htm".equalsIgnoreCase(event)) {
            if (player.getLevel() < 40)
                htmltext = "30621-00.htm";
        } else if ("30621-04.htm".equalsIgnoreCase(event)) {
            st.playSound(SOUND_ACCEPT);
            st.setCond(1);
            st.giveItems(NicolasMap);
        } else if ("30673-03.htm".equalsIgnoreCase(event)) {
            st.playSound(SOUND_MIDDLE);
            st.setCond(2);
            st.takeItems(NicolasMap);
        } else if ("30673-05.htm".equalsIgnoreCase(event)) {
            st.playSound(SOUND_MIDDLE);
            st.setCond(3);
        } else if ("30673-09.htm".equalsIgnoreCase(event)) {
            if (st.haveQuestItem(BrokenMetal) )
                htmltext = "30673-10.htm";
            else if (st.haveQuestItem(Metal) )
                st.giveItems(Certificate);
            st.giveAdena( 72527);
            st.addExpAndSp(203717, 14032);
            st.finish();
            st.playSound(SOUND_FINISH);
        } else if ("32366-02.htm".equalsIgnoreCase(event)) {
            NpcInstance alarm = st.addSpawn(Alarm, Location.of(16491, 113563, -9064));
            st.set("step");
            st.playSound("ItemSound3.sys_siren");
            st.startQuestTimer("1", 60000, alarm);
            Functions.npcSay(alarm, "Intruder Alert! The alarm will getPlayer-destruct in 1 minutes.");
        } else if ("32366-05.htm".equalsIgnoreCase(event)) {
            st.unset("step");
            st.playSound(SOUND_MIDDLE);
            st.setCond(5);
            st.giveItems(BrokenMetal);
        } else if ("32366-06.htm".equalsIgnoreCase(event)) {
            st.unset("step");
            st.playSound(SOUND_MIDDLE);
            st.setCond(4);
            st.giveItems(Metal);
        } else if ("32367-02.htm".equalsIgnoreCase(event))
            st.unset("pass");
        else if (event.startsWith("correct")) {
            st.inc("pass");
            htmltext = event.substring(8);
            if (htmltext.equals("32367-07.htm"))
                if (st.getInt("pass") == 4) {
                    st.set("step", 3);
                    st.cancelQuestTimer("1");
                    st.cancelQuestTimer("2");
                    st.cancelQuestTimer("3");
                    st.cancelQuestTimer("4");
                    st.unset("pass");
                    npc.deleteMe();
                } else
                    htmltext = "32367-06.htm";
        } else if (event.equals("1")) {
            Functions.npcSay(npc, "The alarm will self-destruct in 60 seconds. Enter passcode to override.");
            st.startQuestTimer("2", 30000, npc);
            return null;
        } else if (event.equals("2")) {
            Functions.npcSay(npc, "The alarm will self-destruct in 30 seconds. Enter passcode to override.");
            st.startQuestTimer("3", 20000, npc);
            return null;
        } else if (event.equals("3")) {
            Functions.npcSay(npc, "The alarm will self-destruct in 10 seconds. Enter passcode to override.");
            st.startQuestTimer("4", 10000, npc);
            return null;
        } else if (event.equals("4")) {
            Functions.npcSay(npc, "Recorder crushed.");
            npc.deleteMe();
            st.set("step", 2);
            return null;
        }

        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();

        if (st.isStarted())
            if (npcId == Nikola) {
                if (cond == 0)
                    if (st.player.getLevel() < 40)
                        htmltext = "30621-00.htm";
                    else
                        htmltext = "30621-01.htm";
                else if (cond == 1)
                    htmltext = "30621-05.htm";
            } else if (npcId == Lorain) {
                if (cond == 1)
                    htmltext = "30673-01.htm";
                else if (cond == 2)
                    htmltext = "30673-04.htm";
                else if (cond == 3)
                    htmltext = "30673-06.htm";
                else if (cond == 4 || cond == 5)
                    htmltext = "30673-07.htm";
            } else if (npcId == Device) {
                int step = st.getInt("step");
                if (cond == 3)
                    if (step == 0)
                        htmltext = "32366-01.htm";
                    else if (step == 1)
                        htmltext = "32366-02.htm";
                    else if (step == 2)
                        htmltext = "32366-04.htm";
                    else if (step == 3)
                        htmltext = "32366-03.htm";
            } else if (npcId == Alarm)
                htmltext = "32367-01.htm";

        return htmltext;
    }
}