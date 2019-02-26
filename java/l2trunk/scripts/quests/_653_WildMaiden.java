package l2trunk.scripts.quests;

import l2trunk.gameserver.instancemanager.SpawnManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Spawner;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;

import java.util.ArrayList;
import java.util.List;

public final class _653_WildMaiden extends Quest {
    // items
    private static final int SOE = 736;
    // Npc
    private final int SUKI = 32013;
    private final int GALIBREDO = 30181;

    public _653_WildMaiden() {
        super(false);

        addStartNpc(SUKI);
        addTalkId(GALIBREDO);
    }

    private NpcInstance findNpc(Player player) {
        NpcInstance instance = null;
        List<NpcInstance> npclist = new ArrayList<>();
        for (Spawner spawn : SpawnManager.INSTANCE.getSpawners("NONE"))
            if (spawn.getCurrentNpcId() == 32013) {
                instance = spawn.getLastSpawn();
                npclist.add(instance);
            }

        for (NpcInstance npc : npclist)
            if (player.isInRange(npc, 1600))
                return npc;

        return instance;
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        Player player = st.player;
        if (event.equalsIgnoreCase("spring_girl_sooki_q0653_03.htm")) {
            if (st.getQuestItemsCount(SOE) > 0) {
                st.setCond(1);
                st.start();
                st.playSound(SOUND_ACCEPT);
                st.takeItems(SOE, 1);
                htmltext = "spring_girl_sooki_q0653_04a.htm";
                NpcInstance n = findNpc(player);
                n.broadcastPacket(new MagicSkillUse(n, 2013, 20000));
                st.startQuestTimer("suki_timer", 20000);
            }
        } else if (event.equalsIgnoreCase("spring_girl_sooki_q0653_03.htm")) {
            st.finish();
            st.playSound(SOUND_GIVEUP);
        } else if ("suki_timer".equalsIgnoreCase(event)) {
            NpcInstance n = findNpc(player);
            n.deleteMe();
            htmltext = null;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";

        int npcId = npc.getNpcId();
        int id = st.getState();
        if (npcId == SUKI && id == CREATED) {
            if (st.player.getLevel() >= 36)
                htmltext = "spring_girl_sooki_q0653_01.htm";
            else {
                htmltext = "spring_girl_sooki_q0653_01a.htm";
                st.finish();
            }
        } else if (npcId == GALIBREDO && st.getCond() == 1) {
            htmltext = "galicbredo_q0653_01.htm";
            st.giveItems(ADENA_ID, 2883);
            st.playSound(SOUND_FINISH);
            st.finish();
        }
        return htmltext;
    }
}