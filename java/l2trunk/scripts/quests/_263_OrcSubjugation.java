package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.ScriptFile;

public final class _263_OrcSubjugation extends Quest implements ScriptFile {
    // NPC
    private final int KAYLEEN = 30346;

    // MOBS
    private final int BALOR_ORC_ARCHER = 20385;
    private final int BALOR_ORC_FIGHTER = 20386;
    private final int BALOR_ORC_FIGHTER_LEADER = 20387;
    private final int BALOR_ORC_LIEUTENANT = 20388;

    private final int ORC_AMULET = 1116;
    private final int ORC_NECKLACE = 1117;

    public _263_OrcSubjugation() {
        super(false);
        addStartNpc(KAYLEEN);
        addKillId(
                BALOR_ORC_ARCHER,
                BALOR_ORC_FIGHTER,
                BALOR_ORC_FIGHTER_LEADER,
                BALOR_ORC_LIEUTENANT
        );
        addQuestItem(ORC_AMULET, ORC_NECKLACE);
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equals("sentry_kayleen_q0263_03.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equals("sentry_kayleen_q0263_06.htm")) {
            st.setCond(0);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest(true);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (cond == 0) {
            if (st.getPlayer().getLevel() >= 8 && st.getPlayer().getRace() == Race.darkelf) {
                htmltext = "sentry_kayleen_q0263_02.htm";
                return htmltext;
            } else if (st.getPlayer().getRace() != Race.darkelf) {
                htmltext = "sentry_kayleen_q0263_00.htm";
                st.exitCurrentQuest(true);
            } else if (st.getPlayer().getLevel() < 8) {
                htmltext = "sentry_kayleen_q0263_01.htm";
                st.exitCurrentQuest(true);
            }
        } else if (cond == 1)
            if (st.getQuestItemsCount(ORC_AMULET) == 0 && st.getQuestItemsCount(ORC_NECKLACE) == 0)
                htmltext = "sentry_kayleen_q0263_04.htm";
            else if (st.getQuestItemsCount(ORC_AMULET) + st.getQuestItemsCount(ORC_NECKLACE) >= 10) {
                htmltext = "sentry_kayleen_q0263_05.htm";
                st.giveItems(ADENA_ID, st.getQuestItemsCount(ORC_AMULET) * 20 + st.getQuestItemsCount(ORC_NECKLACE) * 30 + 1100);
                st.takeItems(ORC_AMULET, -1);
                st.takeItems(ORC_NECKLACE, -1);
            } else {
                htmltext = "sentry_kayleen_q0263_05.htm";
                st.giveItems(ADENA_ID, st.getQuestItemsCount(ORC_AMULET) * 20 + st.getQuestItemsCount(ORC_NECKLACE) * 30);
                st.takeItems(ORC_AMULET, -1);
                st.takeItems(ORC_NECKLACE, -1);
            }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (st.getCond() == 1 && Rnd.chance(60)) {
            if (npcId == BALOR_ORC_ARCHER)
                st.giveItems(ORC_AMULET, 1);
            else if (npcId == BALOR_ORC_FIGHTER || npcId == BALOR_ORC_FIGHTER_LEADER || npcId == BALOR_ORC_LIEUTENANT)
                st.giveItems(ORC_NECKLACE, 1);
            st.playSound(SOUND_ITEMGET);
        }
        return null;
    }
}