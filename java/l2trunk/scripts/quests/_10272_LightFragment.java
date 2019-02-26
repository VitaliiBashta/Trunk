package l2trunk.scripts.quests;

import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.utils.Location;

public final class _10272_LightFragment extends Quest {
    // NPC's
    private static final int Orbyu = 32560;
    private static final int Artius = 32559;
    private static final int Lelikia = 32567;
    private static final int Ginby = 32566;
    private static final int Lekon = 32557;

    //Monsters (every monster in SoD when stage is "Attack")

    // ITEMS
    private static final int DestroyedDarknessFragmentPowder = 13853;
    private static final int DestroyedLightFragmentPowder = 13854;
    private static final int SacredLightFragment = 13855;

    private static final Location LELIKIA_POSITION = Location.of(-170936, 247768, 1102);
    private static final Location BASE_POSITION = Location.of(-185032, 242824, 1553);

    public _10272_LightFragment() {
        super(true);

        addStartNpc(Orbyu);
        addTalkId(Orbyu,Artius,Lelikia,Ginby,Lekon);

        addKillId(22552, 22541, 22550, 22551, 22596, 22544, 22540, 22547, 22542, 22543, 22539, 22546, 22548, 22536, 22538, 22537);

        addQuestItem(DestroyedDarknessFragmentPowder,DestroyedLightFragmentPowder,SacredLightFragment);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int cond = st.getCond();
        String htmltext = event;

        if ("orbyu_q10272_2.htm".equalsIgnoreCase(event) && cond == 0) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("artius_q10272_2.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if ("artius_q10272_4.htm".equalsIgnoreCase(event)) {
            st.setCond(3);
            st.playSound(SOUND_MIDDLE);
        } else if ("tele_to_lelikia".equalsIgnoreCase(event)) {
            if (st.getQuestItemsCount(ADENA_ID) >= 10000) {
                st.takeItems(ADENA_ID, 10000);
                st.player.teleToLocation(LELIKIA_POSITION);
                return null;
            } else {
                st.player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                return null;
            }
        } else if ("lelikia_q10272_2.htm".equalsIgnoreCase(event)) {
            st.setCond(4);
            st.playSound(SOUND_MIDDLE);
        } else if ("tele_to_base".equalsIgnoreCase(event)) {
            st.player.teleToLocation(BASE_POSITION);
            return null;
        } else if ("artius_q10272_7.htm".equalsIgnoreCase(event)) {
            st.setCond(5);
            st.playSound(SOUND_MIDDLE);
        } else if ("artius_q10272_9.htm".equalsIgnoreCase(event)) {
            st.setCond(6);
            st.playSound(SOUND_MIDDLE);
        } else if ("artius_q10272_11.htm".equalsIgnoreCase(event)) {
            st.setCond(7);
            st.playSound(SOUND_MIDDLE);
        } else if ("lekon_q10272_2.htm".equalsIgnoreCase(event)) {
            if (st.getQuestItemsCount(DestroyedLightFragmentPowder) >= 100) {
                st.takeItems(DestroyedLightFragmentPowder);
                st.giveItems(SacredLightFragment);
                st.setCond(8);
                st.playSound(SOUND_MIDDLE);
            } else
                htmltext = "lekon_q10272_1a.htm";
        } else if ("artius_q10272_12.htm".equalsIgnoreCase(event)) {
            st.giveItems(ADENA_ID, 556980);
            st.addExpAndSp(1009016, 91363);
            st.complete();
            st.finish();
            st.playSound(SOUND_FINISH);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();

        if (npcId == Orbyu) {
            if (cond == 0) {
                if (st.player.getLevel() >= 75 && st.player.isQuestCompleted(_10271_TheEnvelopingDarkness.class))
                    htmltext = "orbyu_q10272_1.htm";
                else {
                    htmltext = "orbyu_q10272_0.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 4)
                htmltext = "orbyu_q10271_4.htm";
        } else if (npcId == Artius) {
            if (cond == 1)
                htmltext = "artius_q10272_1.htm";
            else if (cond == 2)
                htmltext = "artius_q10272_3.htm";
            else if (cond == 4)
                htmltext = "artius_q10272_5.htm";
            else if (cond == 5) {
                if (st.getQuestItemsCount(DestroyedDarknessFragmentPowder) >= 100)
                    htmltext = "artius_q10272_8.htm";
                else
                    htmltext = "artius_q10272_8a.htm";
            } else if (cond == 6) {
                if (st.getQuestItemsCount(DestroyedLightFragmentPowder) >= 100)
                    htmltext = "artius_q10272_10.htm";
                else
                    htmltext = "artius_q10272_10a.htm";
            } else if (cond == 8)
                htmltext = "artius_q10272_12.htm";

        } else if (npcId == Ginby) {
            if (cond == 3)
                htmltext = "ginby_q10272_1.htm";
        } else if (npcId == Lelikia) {
            if (cond == 3)
                htmltext = "lelikia_q10272_1.htm";
        } else if (npcId == Lekon)
            if (cond == 7 && st.getQuestItemsCount(DestroyedLightFragmentPowder) >= 100)
                htmltext = "lekon_q10272_1.htm";
            else
                htmltext = "lekon_q10272_1a.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 5)
            if (st.getQuestItemsCount(DestroyedDarknessFragmentPowder) <= 100) {
                st.giveItems(DestroyedDarknessFragmentPowder);
                st.playSound(SOUND_ITEMGET);
            }
    }
}
