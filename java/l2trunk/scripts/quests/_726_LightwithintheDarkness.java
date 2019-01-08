package l2trunk.scripts.quests;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Party;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.residence.Fortress;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.SystemMessage;

public final class _726_LightwithintheDarkness extends Quest {
    // ITEMS
    private static final int KnightsEpaulette = 9912;

    // MOB's
    private static final int KanadisGuide3 = 25661;

    public _726_LightwithintheDarkness() {
        super(true);

        addStartNpc(35666, 35698, 35735, 35767, 35804, 35835, 35867, 35904, 35936, 35974, 36011, 36043, 36081, 36118, 36149, 36181, 36219, 36257, 36294, 36326, 36364);
        addKillId(KanadisGuide3);
    }

    private static boolean checkAllDestroyed(int refId) {
        return GameObjectsStorage.getAllByNpcId(_726_LightwithintheDarkness.KanadisGuide3, true).stream()
                .noneMatch(npc -> npc.getReflectionId() == refId);
    }


    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int cond = st.getCond();
        Player player = st.getPlayer();

        if (event.equals("dcw_q726_4.htm") && cond == 0) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equals("reward") && cond == 1 && "done".equalsIgnoreCase(player.getVar("q726"))) {
            player.unsetVar("q726");
            player.unsetVar("q726done");
            st.giveItems(KnightsEpaulette, 152);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest(true);
            return null;
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        Player player = st.getPlayer();
        QuestState qs727 = player.getQuestState(_727_HopewithintheDarkness.class);

        if (!check(st.getPlayer())) {
            st.exitCurrentQuest(true);
            return "dcw_q726_1a.htm";
        }
        if (qs727 != null) {
            st.exitCurrentQuest(true);
            return "dcw_q726_1b.htm";
        } else if (cond == 0) {
            if (st.getPlayer().getLevel() >= 70)
                htmltext = "dcw_q726_1.htm";
            else {
                htmltext = "dcw_q726_0.htm";
                st.exitCurrentQuest(true);
            }
        } else if (cond == 1)
            if (player.getVar("q726done") != null)
                htmltext = "dcw_q726_6.htm";
            else
                htmltext = "dcw_q726_5.htm";
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        Player player = st.getPlayer();
        Party party = player.getParty();

        if (cond == 1 && npcId == KanadisGuide3 && checkAllDestroyed(player.getReflectionId())) {
            if (player.isInParty())
                for (Player member : party.getMembers())
                    if (!member.isDead() && member.getParty().isInReflection()) {
                        member.sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(1));
                        member.setVar("q726", "done", -1);
                        member.setVar("q726done", "done", -1);
                        st.playSound(SOUND_ITEMGET);
                    }
            player.getReflection().startCollapseTimer(60 * 1000L);
        }
        return null;
    }

    private boolean check(Player player) {
        Fortress fort = ResidenceHolder.getResidenceByObject(Fortress.class, player);
        if (fort == null)
            return false;
        Clan clan = player.getClan();
        if (clan == null)
            return false;
        if (clan.getClanId() != fort.getOwnerId())
            return false;
        return fort.getContractState() == 1;
    }
}