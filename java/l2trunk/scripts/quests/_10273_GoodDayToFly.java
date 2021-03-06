package l2trunk.scripts.quests;

import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.tables.SkillTable;

public final class _10273_GoodDayToFly extends Quest {
    private final static int Lekon = 32557;
    private final static int VultureRider1 = 22614;
    private final static int VultureRider2 = 22615;

    private final static int Mark = 13856;

    public _10273_GoodDayToFly() {
        addStartNpc(Lekon);

        addQuestItem(Mark);

        addKillId(VultureRider1, VultureRider2);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        Player player = st.player;

        if ("32557-06.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("32557-09.htm".equalsIgnoreCase(event)) {
            if (player.isTrasformed()) {
                player.sendPacket(Msg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
                return null;
            }
            st.set("transform");
            SkillTable.INSTANCE.getInfo(5982).getEffects(player);
        } else if ("32557-10.htm".equalsIgnoreCase(event)) {
            if (player.isTrasformed()) {
                player.sendPacket(Msg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
                return null;
            }
            SkillTable.INSTANCE.getInfo(5983).getEffects(player);
        } else if ("32557-13.htm".equalsIgnoreCase(event)) {
            if (player.isTrasformed()) {
                player.sendPacket(Msg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
                return null;
            }
            if (st.isSet("transform") )
                SkillTable.INSTANCE.getInfo(5982).getEffects(player);
            else if (st.getInt("transform") == 2)
                SkillTable.INSTANCE.getInfo(5983).getEffects(player);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext;
        int id = st.getState();
        int transform = st.getInt("transform");

        if (id == COMPLETED)
            htmltext = "32557-0a.htm";
        else if (id == CREATED)
            if (st.player.getLevel() < 75)
                htmltext = "32557-00.htm";
            else
                htmltext = "32557-01.htm";
        else if (st.getQuestItemsCount(Mark) >= 5) {
            htmltext = "32557-14.htm";
            if (transform == 1)
                st.giveItems(13553);
            else if (transform == 2)
                st.giveItems(13554);
            st.takeItems(Mark);
            st.giveItems(13857);
            st.addExpAndSp(25160, 2525);
            st.finish();
            st.playSound(SOUND_FINISH);
        } else if (transform < 1)
            htmltext = "32557-07.htm";
        else
            htmltext = "32557-11.htm";

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getState() != STARTED)
            return;

        int cond = st.getCond();
        long count = st.getQuestItemsCount(Mark);
        if (cond == 1 && count < 5) {
            st.giveItems(Mark);
            if (count == 4) {
                st.playSound(SOUND_MIDDLE);
                st.setCond(2);
            } else
                st.playSound(SOUND_ITEMGET);
        }
    }
}