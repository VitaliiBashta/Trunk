package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;

public final class _450_GraveRobberMemberRescue extends Quest {

    private static final int KANEMIKA = 32650;
    private static final int WARRIOR_NPC = 32651;

    private static final int WARRIOR_MON = 22741;

    private static final int EVIDENCE_OF_MIGRATION = 14876;

    public _450_GraveRobberMemberRescue() {
        super(false);

        addStartNpc(KANEMIKA);
        addTalkId(WARRIOR_NPC);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("32650-05.htm")) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(final NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int id = st.getState();
        int cond = st.getCond();
        Player player = st.player;

        if (npcId == KANEMIKA) {
            if (id == CREATED) {
                if (player.getLevel() < 80) {
                    htmltext = "32650-00.htm";
                    st.exitCurrentQuest();
                } else if (!canEnter(player)) {
                    htmltext = "32650-09.htm";
                    st.exitCurrentQuest();
                } else
                    htmltext = "32650-01.htm";
            } else if (cond == 1) {
                if (st.getQuestItemsCount(EVIDENCE_OF_MIGRATION) >= 1)
                    htmltext = "32650-07.htm";
                else
                    htmltext = "32650-06.htm";
            } else if (cond == 2 && st.getQuestItemsCount(EVIDENCE_OF_MIGRATION) == 10) {
                htmltext = "32650-08.htm";
                st.giveAdena( 65000);
                st.takeItems(EVIDENCE_OF_MIGRATION);
                st.exitCurrentQuest();
                st.playSound(SOUND_FINISH);
                st.player.setVar(name, System.currentTimeMillis());
            }
        } else if (cond == 1 && npcId == WARRIOR_NPC)
            if (Rnd.chance(50)) {
                htmltext = "32651-01.htm";
                st.giveItems(EVIDENCE_OF_MIGRATION);
                st.playSound(SOUND_ITEMGET);
                npc.moveToLocation(Location.of(npc.getX() + 200, npc.getY() + 200, npc.getZ()), 0, false);

                ThreadPoolManager.INSTANCE.schedule(npc::deleteMe, 2500L);

                if (st.getQuestItemsCount(EVIDENCE_OF_MIGRATION) == 10) {
                    st.setCond(2);
                    st.playSound(SOUND_MIDDLE);
                }
            } else {
                player.sendPacket(new ExShowScreenMessage("The grave robber warrior has been filled with dark energy and is attacking you!", 4000, ScreenMessageAlign.MIDDLE_CENTER, false));
                NpcInstance warrior = st.addSpawn(WARRIOR_MON, npc.getLoc(), 100, 120000);
                warrior.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, Rnd.get(1, 100));

                if (Rnd.chance(50))
                    Functions.npcSay(warrior, "...Grunt... oh...");
                else
                    Functions.npcSay(warrior, "Grunt... What's... wrong with me...");

                npc.decayMe();

                return null;
            }

        return htmltext;
    }

    private boolean canEnter(Player player) {
        if (player.isGM())
            return true;
        if (player.isVarSet(name)) {
            return player.getVarLong(name) - System.currentTimeMillis() > 24 * 60 * 60 * 1000;
        }
        return true;
    }
}