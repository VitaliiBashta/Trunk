package l2trunk.scripts.quests;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.PlaySound;

import java.util.List;

public final class _111_ElrokianHuntersProof extends Quest {
    private static final int Marquez = 32113;
    private static final int Asamah = 32115;
    private static final int Kirikachin = 32116;

    private static final List<Integer> Velociraptor = List.of(
            22196, 22197, 22198, 22218, 22223);
    private static final List<Integer> Ornithomimus = List.of(
            22200, 22201, 22202, 22219, 22224, 22744, 22742);
    private static final List<Integer> Deinonychus = List.of(
            22203, 22204, 22205, 22220, 22225, 22745, 22743);
    private static final List<Integer> Pachycephalosaurus = List.of(
            22208, 22209, 22210, 22221, 22226);

    private static final int DiaryFragment = 8768;
    private static final int OrnithomimusClaw = 8770;
    private static final int DeinonychusBone = 8771;
    private static final int PachycephalosaurusSkin = 8772;

    private static final int ElrokianTrap = 8763;
    private static final int TrapStone = 8764;

    public _111_ElrokianHuntersProof() {
        super(true);
        addStartNpc(Marquez);
        addTalkId(Asamah, Kirikachin);

        addKillId(Velociraptor);
        addKillId(Ornithomimus);
        addKillId(Deinonychus);
        addKillId(Pachycephalosaurus);

        addQuestItem(DiaryFragment, OrnithomimusClaw, DeinonychusBone, PachycephalosaurusSkin);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int cond = st.getCond();
        Player player = st.player;

        if (event.equalsIgnoreCase("marquez_q111_2.htm") && cond == 0) {
            st.setCond(2);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("asamah_q111_2.htm".equalsIgnoreCase(event)) {
            st.setCond(3);
            st.playSound(SOUND_MIDDLE);
        } else if ("marquez_q111_4.htm".equalsIgnoreCase(event)) {
            st.setCond(4);
            st.playSound(SOUND_MIDDLE);
        } else if ("marquez_q111_6.htm".equalsIgnoreCase(event)) {
            st.setCond(6);
            st.takeItems(DiaryFragment);
            st.playSound(SOUND_MIDDLE);
        } else if (event.equalsIgnoreCase("kirikachin_q111_2.htm")) {
            st.setCond(7);
            player.sendPacket(new PlaySound("EtcSound.elcroki_song_full"));
        } else if (event.equalsIgnoreCase("kirikachin_q111_3.htm")) {
            st.setCond(8);
            st.playSound(SOUND_MIDDLE);
        } else if (event.equalsIgnoreCase("asamah_q111_4.htm")) {
            st.setCond(9);
            st.playSound(SOUND_MIDDLE);
        } else if (event.equalsIgnoreCase("asamah_q111_5.htm")) {
            st.setCond(10);
            st.playSound(SOUND_MIDDLE);
        } else if (event.equalsIgnoreCase("asamah_q111_7.htm")) {
            st.takeItems(OrnithomimusClaw, -1);
            st.takeItems(DeinonychusBone, -1);
            st.takeItems(PachycephalosaurusSkin, -1);
            st.setCond(12);
            st.playSound(SOUND_MIDDLE);
        } else if ("asamah_q111_8.htm".equalsIgnoreCase(event)) {
            st.giveItems(ADENA_ID, 1071691);
            st.giveItems(ElrokianTrap);
            st.giveItems(TrapStone, 100);
            st.complete();
            st.finish();
            st.playSound(SOUND_FINISH);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();

        if (npcId == Marquez) {
            if (st.player.getLevel() >= 75 && cond == 0)
                htmltext = "marquez_q111_1.htm";
            else if (st.player.getLevel() < 75 && cond == 0)
                htmltext = "marquez_q111_0.htm";
            else if (cond == 3)
                htmltext = "marquez_q111_3.htm";
            else if (cond == 5)
                htmltext = "marquez_q111_5.htm";
        } else if (npcId == Asamah) {
            if (cond == 2)
                htmltext = "asamah_q111_1.htm";
            else if (cond == 8)
                htmltext = "asamah_q111_3.htm";
            else if (cond == 11)
                htmltext = "asamah_q111_6.htm";
        } else if (npcId == Kirikachin)
            if (cond == 6)
                htmltext = "kirikachin_q111_1.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int id = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 4) {
            if (Velociraptor.contains(id)) {
                st.giveItemIfNotHave(DiaryFragment, 50);
                if (st.haveQuestItem(DiaryFragment, 50)) {
                    st.playSound(SOUND_MIDDLE);
                    st.setCond(5);
                }
            }
        } else if (cond == 10) {
            if (Ornithomimus.contains(id))
                st.giveItemIfNotHave(OrnithomimusClaw, 10);
            if (Deinonychus.contains(id))
                st.giveItemIfNotHave(DeinonychusBone, 10);
            if (Pachycephalosaurus.contains(id))
                st.giveItemIfNotHave(PachycephalosaurusSkin, 10);
            if (st.haveQuestItem(OrnithomimusClaw, 10)
                    && st.haveQuestItem(DeinonychusBone, 10)
                    && st.haveQuestItem(PachycephalosaurusSkin, 10)) {
                st.setCond(11);
                st.playSound(SOUND_MIDDLE);
            }
        }
    }
}