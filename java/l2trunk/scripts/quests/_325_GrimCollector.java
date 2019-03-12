package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _325_GrimCollector extends Quest {
    private static final int ZOMBIE_HEAD = 1350;
    private static final int ZOMBIE_HEART = 1351;
    private static final int ZOMBIE_LIVER = 1352;
    private static final int SKULL = 1353;
    private static final int RIB_BONE = 1354;
    private static final int SPINE = 1355;
    private static final int ARM_BONE = 1356;
    private static final int THIGH_BONE = 1357;
    private static final int COMPLETE_SKELETON = 1358;
    private static final List<Integer> items = List.of(
            ZOMBIE_HEAD, ZOMBIE_HEART, ZOMBIE_LIVER, SKULL, RIB_BONE, SPINE, ARM_BONE, THIGH_BONE, COMPLETE_SKELETON);
    private final int ANATOMY_DIAGRAM = 1349;

    public _325_GrimCollector() {
        super(false);

        addStartNpc(30336);

        addTalkId(30336,30342,30434);

        addKillId(20026,20029,20035,20042,20045,20457,20458,20051,20514,20515);

        addQuestItem(items);
        addQuestItem(ANATOMY_DIAGRAM);
    }

    private long pieces(QuestState st) {
        return st.getQuestItemsCount(ZOMBIE_HEAD)
                + st.getQuestItemsCount(SPINE)
                + st.getQuestItemsCount(ARM_BONE)
                + st.getQuestItemsCount(ZOMBIE_HEART)
                + st.getQuestItemsCount(ZOMBIE_LIVER)
                + st.getQuestItemsCount(SKULL)
                + st.getQuestItemsCount(RIB_BONE)
                + st.getQuestItemsCount(THIGH_BONE)
                + st.getQuestItemsCount(COMPLETE_SKELETON);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("guard_curtiz_q0325_03.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("samed_q0325_03.htm".equalsIgnoreCase(event))
            st.giveItems(ANATOMY_DIAGRAM);
        else if ("samed_q0325_06.htm".equalsIgnoreCase(event)) {
            if (pieces(st) > 0) {
                st.giveItems(ADENA_ID, 30 * st.getQuestItemsCount(ZOMBIE_HEAD) + 20 * st.getQuestItemsCount(ZOMBIE_HEART) + 20 * st.getQuestItemsCount(ZOMBIE_LIVER) + 50 * st.getQuestItemsCount(SKULL) + 15 * st.getQuestItemsCount(RIB_BONE) + 10 * st.getQuestItemsCount(SPINE) + 10 * st.getQuestItemsCount(ARM_BONE) + 10 * st.getQuestItemsCount(THIGH_BONE) + 2000 * st.getQuestItemsCount(COMPLETE_SKELETON));
                takeQuestItems(st);
            }
            st.takeItems(ANATOMY_DIAGRAM);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        } else if ("samed_q0325_07.htm".equalsIgnoreCase(event) && pieces(st) > 0) {
            st.giveItems(ADENA_ID, 30 * st.getQuestItemsCount(ZOMBIE_HEAD) + 20 * st.getQuestItemsCount(ZOMBIE_HEART) + 20 * st.getQuestItemsCount(ZOMBIE_LIVER) + 50 * st.getQuestItemsCount(SKULL) + 15 * st.getQuestItemsCount(RIB_BONE) + 10 * st.getQuestItemsCount(SPINE) + 10 * st.getQuestItemsCount(ARM_BONE) + 10 * st.getQuestItemsCount(THIGH_BONE) + 2000 * st.getQuestItemsCount(COMPLETE_SKELETON));
            takeQuestItems(st);
        } else if ("samed_q0325_09.htm".equalsIgnoreCase(event)) {
            st.giveItems(ADENA_ID, 2000 * st.getQuestItemsCount(COMPLETE_SKELETON));
            st.takeItems(COMPLETE_SKELETON, -1);
        } else if ("varsak_q0325_03.htm".equalsIgnoreCase(event))
            if (st.getQuestItemsCount(SPINE) != 0 && st.getQuestItemsCount(ARM_BONE) != 0 && st.getQuestItemsCount(SKULL) != 0 && st.getQuestItemsCount(RIB_BONE) != 0 && st.getQuestItemsCount(THIGH_BONE) != 0) {
                st.takeItems(SPINE, 1);
                st.takeItems(SKULL, 1);
                st.takeItems(ARM_BONE, 1);
                st.takeItems(RIB_BONE, 1);
                st.takeItems(THIGH_BONE, 1);
                if (Rnd.chance(80))
                    st.giveItems(COMPLETE_SKELETON);
                else
                    htmltext = "varsak_q0325_04.htm";
            } else
                htmltext = "varsak_q0325_02.htm";
        return htmltext;
    }

    private void takeQuestItems(QuestState st) {
        st.takeItems(items);
        st.takeItems(COMPLETE_SKELETON);
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int id = st.getState();
        int cond = st.getCond();
        if (id == CREATED)
            st.setCond(0);
        if (npcId == 30336 && cond == 0) {
            if (st.player.getLevel() >= 15) {
                htmltext = "guard_curtiz_q0325_02.htm";
                return htmltext;
            }
            htmltext = "guard_curtiz_q0325_01.htm";
            st.exitCurrentQuest();
        } else if (npcId == 30336 && cond > 0)
            if (st.getQuestItemsCount(ANATOMY_DIAGRAM) == 0)
                htmltext = "guard_curtiz_q0325_04.htm";
            else
                htmltext = "guard_curtiz_q0325_05.htm";
        else if (npcId == 30434 && cond > 0) {
            if (st.getQuestItemsCount(ANATOMY_DIAGRAM) == 0)
                htmltext = "samed_q0325_01.htm";
            else if (st.haveQuestItem(ANATOMY_DIAGRAM)  && pieces(st) == 0)
                htmltext = "samed_q0325_04.htm";
            else if (st.haveQuestItem(ANATOMY_DIAGRAM)  && pieces(st) > 0 && st.getQuestItemsCount(COMPLETE_SKELETON) == 0)
                htmltext = "samed_q0325_05.htm";
            else if (st.haveQuestItem(ANATOMY_DIAGRAM)  && pieces(st) > 0 && st.haveQuestItem(COMPLETE_SKELETON) )
                htmltext = "samed_q0325_08.htm";
        } else if (npcId == 30342 && cond > 0 && st.haveQuestItem(ANATOMY_DIAGRAM) )
            htmltext = "varsak_q0325_01.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (!st.haveQuestItem(ANATOMY_DIAGRAM) )
            return;
        int n = Rnd.get(100);
        if (npcId == 20026) {
            if (n < 90) {
                st.playSound(SOUND_ITEMGET);
                if (n < 40)
                    st.giveItems(ZOMBIE_HEAD);
                else if (n < 60)
                    st.giveItems(ZOMBIE_HEART);
                else
                    st.giveItems(ZOMBIE_LIVER);
            }
        } else if (npcId == 20029) {
            st.playSound(SOUND_ITEMGET);
            if (n < 44)
                st.giveItems(ZOMBIE_HEAD);
            else if (n < 66)
                st.giveItems(ZOMBIE_HEART);
            else
                st.giveItems(ZOMBIE_LIVER);
        } else if (npcId == 20035) {
            if (n < 79) {
                st.playSound(SOUND_ITEMGET);
                if (n < 5)
                    st.giveItems(SKULL);
                else if (n < 15)
                    st.giveItems(RIB_BONE);
                else if (n < 29)
                    st.giveItems(SPINE);
                else
                    st.giveItems(THIGH_BONE);
            }
        } else if (npcId == 20042) {
            if (n < 86) {
                st.playSound(SOUND_ITEMGET);
                if (n < 6)
                    st.giveItems(SKULL);
                else if (n < 19)
                    st.giveItems(RIB_BONE);
                else if (n < 69)
                    st.giveItems(ARM_BONE);
                else
                    st.giveItems(THIGH_BONE);
            }
        } else if (npcId == 20045) {
            if (n < 97) {
                st.playSound(SOUND_ITEMGET);
                if (n < 9)
                    st.giveItems(SKULL);
                else if (n < 59)
                    st.giveItems(SPINE);
                else if (n < 77)
                    st.giveItems(ARM_BONE);
                else
                    st.giveItems(THIGH_BONE);
            }
        } else if (npcId == 20051) {
            if (n < 99) {
                st.playSound(SOUND_ITEMGET);
                if (n < 9)
                    st.giveItems(SKULL);
                else if (n < 59)
                    st.giveItems(RIB_BONE);
                else if (n < 79)
                    st.giveItems(SPINE);
                else
                    st.giveItems(ARM_BONE);
            }
        } else if (npcId == 20514) {
            if (n < 51) {
                st.playSound(SOUND_ITEMGET);
                if (n < 2)
                    st.giveItems(SKULL);
                else if (n < 8)
                    st.giveItems(RIB_BONE);
                else if (n < 17)
                    st.giveItems(SPINE);
                else if (n < 18)
                    st.giveItems(ARM_BONE);
                else
                    st.giveItems(THIGH_BONE);
            }
        } else if (npcId == 20515) {
            if (n < 60) {
                st.playSound(SOUND_ITEMGET);
                if (n < 3)
                    st.giveItems(SKULL);
                else if (n < 11)
                    st.giveItems(RIB_BONE);
                else if (n < 22)
                    st.giveItems(SPINE);
                else if (n < 24)
                    st.giveItems(ARM_BONE);
                else
                    st.giveItems(THIGH_BONE);
            }
        } else if (npcId == 20457 || npcId == 20458) {
            st.playSound(SOUND_ITEMGET);
            if (n < 42)
                st.giveItems(ZOMBIE_HEAD);
            else if (n < 67)
                st.giveItems(ZOMBIE_HEART);
            else
                st.giveItems(ZOMBIE_LIVER);
        }
    }
}