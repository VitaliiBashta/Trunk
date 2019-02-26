package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _274_SkirmishWithTheWerewolves extends Quest {

    private static final int MARAKU_WEREWOLF_HEAD = 1477;
    private static final int NECKLACE_OF_VALOR = 1507;
    private static final int NECKLACE_OF_COURAGE = 1506;
    private static final int MARAKU_WOLFMEN_TOTEM = 1501;

    public _274_SkirmishWithTheWerewolves() {
        super(false);
        addStartNpc(30569);

        addKillId(20363,20364);

        addQuestItem(MARAKU_WEREWOLF_HEAD);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equals("prefect_brukurse_q0274_03.htm")) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int id = st.getState();
        int cond = st.getCond();
        if (id == CREATED)
            if (st.player.getRace() != Race.orc) {
                htmltext = "prefect_brukurse_q0274_00.htm";
                st.exitCurrentQuest();
            } else if (st.player.getLevel() < 9) {
                htmltext = "prefect_brukurse_q0274_01.htm";
                st.exitCurrentQuest();
            } else if (st.getQuestItemsCount(NECKLACE_OF_VALOR) > 0 || st.getQuestItemsCount(NECKLACE_OF_COURAGE) > 0) {
                htmltext = "prefect_brukurse_q0274_02.htm";
                return htmltext;
            } else
                htmltext = "prefect_brukurse_q0274_07.htm";
        else if (cond == 1)
            htmltext = "prefect_brukurse_q0274_04.htm";
        else if (cond == 2)
            if (st.getQuestItemsCount(MARAKU_WEREWOLF_HEAD) < 40)
                htmltext = "prefect_brukurse_q0274_04.htm";
            else {
                st.takeItems(MARAKU_WEREWOLF_HEAD, -1);
                st.giveItems(ADENA_ID, 3500, true);
                if (st.getQuestItemsCount(MARAKU_WOLFMEN_TOTEM) >= 1) {
                    st.giveItems(ADENA_ID, st.getQuestItemsCount(MARAKU_WOLFMEN_TOTEM) * 600, true);
                    st.takeItems(MARAKU_WOLFMEN_TOTEM, -1);
                }
                htmltext = "prefect_brukurse_q0274_05.htm";
                st.exitCurrentQuest();
                st.playSound(SOUND_FINISH);
            }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1 && st.getQuestItemsCount(MARAKU_WEREWOLF_HEAD) < 40) {
            if (st.getQuestItemsCount(MARAKU_WEREWOLF_HEAD) < 39)
                st.playSound(SOUND_ITEMGET);
            else {
                st.playSound(SOUND_MIDDLE);
                st.setCond(2);
            }
            st.giveItems(MARAKU_WEREWOLF_HEAD);
        }
        if (Rnd.chance(5))
            st.giveItems(MARAKU_WOLFMEN_TOTEM);
    }
}