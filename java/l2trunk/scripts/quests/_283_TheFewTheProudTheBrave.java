package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;

public final class _283_TheFewTheProudTheBrave extends Quest {
    //NPCs
    private static final int PERWAN = 32133;
    //Mobs
    private static final int CRIMSON_SPIDER = 22244;
    //Quest items
    private static final int CRIMSON_SPIDER_CLAW = 9747;
    //Chances
    private static final int CRIMSON_SPIDER_CLAW_CHANCE = 34;

    public _283_TheFewTheProudTheBrave() {
        super(false);
        addStartNpc(PERWAN);
        addKillId(CRIMSON_SPIDER);
        addQuestItem(CRIMSON_SPIDER_CLAW);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int state = st.getState();
        if ("subelder_perwan_q0283_0103.htm".equalsIgnoreCase(event) && state == CREATED) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("subelder_perwan_q0283_0203.htm".equalsIgnoreCase(event) && state == STARTED) {
            long count = st.getQuestItemsCount(CRIMSON_SPIDER_CLAW);
            if (count > 0) {
                st.takeItems(CRIMSON_SPIDER_CLAW);
                st.giveItems(ADENA_ID, 45 * count);

                if (st.player.getClassId().occupation() == 0 && !st.player.isVarSet("p1q4")) {
                    st.player.setVar("p1q4");
                    st.player.sendPacket(new ExShowScreenMessage("Now go find the Newbie Guide."));
                }

                st.playSound(SOUND_MIDDLE);
            }
        } else if (event.equalsIgnoreCase("subelder_perwan_q0283_0204.htm") && state == STARTED) {
            st.takeItems(CRIMSON_SPIDER_CLAW);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        if (npc.getNpcId() != PERWAN)
            return htmltext;
        int _state = st.getState();

        if (_state == CREATED) {
            if (st.player.getLevel() >= 15) {
                htmltext = "subelder_perwan_q0283_0101.htm";
                st.setCond(0);
            } else {
                htmltext = "subelder_perwan_q0283_0102.htm";
                st.exitCurrentQuest();
            }
        } else if (_state == STARTED)
            htmltext = st.getQuestItemsCount(CRIMSON_SPIDER_CLAW) > 0 ? "subelder_perwan_q0283_0105.htm" : "subelder_perwan_q0283_0106.htm";

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState qs) {
        if (qs.getState() != STARTED)
            return;

        if (Rnd.chance(CRIMSON_SPIDER_CLAW_CHANCE)) {
            qs.giveItems(CRIMSON_SPIDER_CLAW);
            qs.playSound(SOUND_ITEMGET);
        }
    }
}