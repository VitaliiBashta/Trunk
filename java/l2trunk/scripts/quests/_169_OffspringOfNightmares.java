package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;

public final class _169_OffspringOfNightmares extends Quest {
    //NPC
    private static final int Vlasty = 30145;
    //QuestItem
    private static final int CrackedSkull = 1030;
    private static final int PerfectSkull = 1031;
    //Item
    private static final int BoneGaiters = 31;
    //MOB
    private static final int DarkHorror = 20105;
    private static final int LesserDarkHorror = 20025;

    public _169_OffspringOfNightmares() {
        addStartNpc(Vlasty);

        addKillId(DarkHorror, LesserDarkHorror);

        addQuestItem(CrackedSkull, PerfectSkull);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("30145-04.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("30145-08.htm".equalsIgnoreCase(event)) {
            st.takeItems(CrackedSkull);
            st.takeItems(PerfectSkull);
            st.giveItems(BoneGaiters);
            st.giveItems(ADENA_ID, 17050, true);
            st.player.addExpAndSp(17475, 818);

            if (st.player.getClassId().occupation() == 0 && !st.player.isVarSet("p1q4")) {
                st.player.setVar("p1q4");
                st.player.sendPacket(new ExShowScreenMessage("Now go find the Newbie Guide."));
            }

            st.playSound(SOUND_FINISH);
            st.finish();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == Vlasty)
            if (cond == 0) {
                if (st.player.getRace() != Race.darkelf) {
                    htmltext = "30145-00.htm";
                    st.exitCurrentQuest();
                } else if (st.player.getLevel() >= 15)
                    htmltext = "30145-03.htm";
                else {
                    htmltext = "30145-02.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1) {
                if (st.getQuestItemsCount(CrackedSkull) == 0)
                    htmltext = "30145-05.htm";
                else
                    htmltext = "30145-06.htm";
            } else if (cond == 2)
                htmltext = "30145-07.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1) {
            if (Rnd.chance(20) && st.getQuestItemsCount(PerfectSkull) == 0) {
                st.giveItems(PerfectSkull);
                st.playSound(SOUND_MIDDLE);
                st.setCond(2);
                st.start();
            }
            if (Rnd.chance(70)) {
                st.giveItems(CrackedSkull);
                st.playSound(SOUND_ITEMGET);
            }
        }
    }
}