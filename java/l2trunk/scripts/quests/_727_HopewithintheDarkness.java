package l2trunk.scripts.quests;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Party;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.SystemMessage;

public final class _727_HopewithintheDarkness extends Quest {
    // ITEMS
    private static final int KnightsEpaulette = 9912;

    // MOB's
    private static final int KanadisGuide3 = 25661;

    public _727_HopewithintheDarkness() {
        super(true);

        addStartNpc(36403, 36404, 36405, 36406, 36407, 36408, 36409, 36410, 36411);
        addKillId(KanadisGuide3);
    }

    private static boolean checkAllDestroyed(int refId) {
        return GameObjectsStorage.getAllByNpcId(_727_HopewithintheDarkness.KanadisGuide3, true)
                .noneMatch(npc -> npc.getReflectionId() == refId);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int cond = st.getCond();
        Player player = st.player;

        if (event.equals("dcw_q727_4.htm") && cond == 0) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if (event.equals("reward") && cond == 1 && player.isVarSet("q727")) {
            player.unsetVar("q727");
            player.unsetVar("q727done");
            st.giveItems(KnightsEpaulette, 159);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
            return null;
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        Player player = st.player;
        QuestState qs726 = player.getQuestState(_726_LightwithintheDarkness.class);

        if (!check(st.player)) {
            st.exitCurrentQuest();
            return "dcw_q727_1a.htm";
        }
        if (qs726 != null) {
            st.exitCurrentQuest();
            return "dcw_q727_1b.htm";
        } else if (cond == 0) {
            if (st.player.getLevel() >= 70)
                htmltext = "dcw_q727_1.htm";
            else {
                htmltext = "dcw_q727_0.htm";
                st.exitCurrentQuest();
            }
        } else if (cond == 1)
            if (player.isVarSet("q727done"))
                htmltext = "dcw_q727_6.htm";
            else
                htmltext = "dcw_q727_5.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        Player player = st.player;
        Party party = player.getParty();

        if (cond == 1 && npcId == KanadisGuide3 && checkAllDestroyed(player.getReflectionId())) {
            if (player.isInParty())
                for (Player member : party.getMembers())
                    if (!member.isDead() && member.getParty().isInReflection()) {
                        member.sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(1));
                        member.setVar("q727");
                        member.setVar("q727done");
                        st.playSound(SOUND_ITEMGET);
                    }
            player.getReflection().startCollapseTimer(60 * 1000L);
        }
    }

    private boolean check(Player player) {
        Castle castle = ResidenceHolder.getResidenceByObject(Castle.class, player);
        if (castle == null)
            return false;
        Clan clan = player.getClan();
        if (clan == null)
            return false;
        return clan.clanId() == castle.getOwnerId();
    }
}