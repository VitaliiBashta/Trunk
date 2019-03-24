package l2trunk.scripts.quests;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.ReflectionUtils;

import java.util.List;

public final class _10293_SevenSignsForbiddenBook extends Quest {
    private static final int Elcardia = 32784;
    private static final int Sophia = 32596;

    private static final int SophiaInzone1 = 32861;
    private static final int ElcardiaInzone1 = 32785;
    private static final int SophiaInzone2 = 32863;

    private static final int SolinasBiography = 17213;

    private static final List<Integer> books = List.of(32809, 32810, 32811, 32812, 32813);

    public _10293_SevenSignsForbiddenBook() {
        addStartNpc(Elcardia);
        addTalkId(Sophia, SophiaInzone1, ElcardiaInzone1, SophiaInzone2);
        addTalkId(books);
        addQuestItem(SolinasBiography);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        Player player = st.player;
        if ("elcardia_q10293_3.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("enter_library".equalsIgnoreCase(event)) {
            enterInstance(player);
            return null;
        } else if ("sophia2_q10293_4.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if ("sophia2_q10293_8.htm".equalsIgnoreCase(event)) {
            st.setCond(4);
            st.playSound(SOUND_MIDDLE);
        } else if ("elcardia2_q10293_4.htm".equalsIgnoreCase(event)) {
            st.setCond(5);
            st.playSound(SOUND_MIDDLE);
        } else if ("sophia2_q10293_10.htm".equalsIgnoreCase(event)) {
            st.setCond(6);
            st.playSound(SOUND_MIDDLE);
        } else if ("teleport_in".equalsIgnoreCase(event)) {
            st.player.teleToLocation(37348, -50383, -1168);
            teleportElcardia(player);
            return null;
        } else if ("teleport_out".equalsIgnoreCase(event)) {
            st.player.teleToLocation(37205, -49753, -1128);
            teleportElcardia(player);
            return null;
        } else if ("book_q10293_3a.htm".equalsIgnoreCase(event)) {
            st.giveItems(SolinasBiography);
            st.setCond(7);
            st.playSound(SOUND_MIDDLE);
        } else if ("elcardia_q10293_7.htm".equalsIgnoreCase(event)) {
            st.addExpAndSp(15000000, 1500000);
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
        Player player = st.player;
        if (player.getBaseClassId() != player.getActiveClassId())
            return "no_subclass_allowed.htm";
        switch (npcId) {
            case Elcardia:
                if (cond == 0) {
                    if (player.getLevel() >= 81 && player.isQuestCompleted(_10292_SevenSignsGirlOfDoubt.class))
                        htmltext = "elcardia_q10293_1.htm";
                    else {
                        htmltext = "elcardia_q10293_0.htm";
                        st.exitCurrentQuest();
                    }
                } else if (cond >= 1 && cond < 8)
                    htmltext = "elcardia_q10293_4.htm";
                else if (cond == 8)
                    htmltext = "elcardia_q10293_5.htm";
                break;
            case Sophia:
                if (cond >= 1 && cond <= 7)
                    htmltext = "sophia_q10293_1.htm";
                break;
            case SophiaInzone1:
                if (cond == 1)
                    htmltext = "sophia2_q10293_1.htm";
                else if (cond == 2 || cond == 4 || cond == 7 || cond == 8)
                    htmltext = "sophia2_q10293_5.htm";
                else if (cond == 3)
                    htmltext = "sophia2_q10293_6.htm";
                else if (cond == 5)
                    htmltext = "sophia2_q10293_9.htm";
                else if (cond == 6)
                    htmltext = "sophia2_q10293_11.htm";
                break;
            case ElcardiaInzone1:
                if (cond == 1 || cond == 3 || cond == 5 || cond == 6)
                    htmltext = "elcardia2_q10293_1.htm";
                else if (cond == 2) {
                    st.setCond(3);
                    htmltext = "elcardia2_q10293_2.htm";
                } else if (cond == 4)
                    htmltext = "elcardia2_q10293_3.htm";
                else if (cond == 7) {
                    st.setCond(8);
                    htmltext = "elcardia2_q10293_5.htm";
                } else if (cond == 8)
                    htmltext = "elcardia2_q10293_5.htm";

                break;
            case SophiaInzone2:
                if (cond == 6 || cond == 7)
                    htmltext = "sophia3_q10293_1.htm";
                else if (cond == 8)
                    htmltext = "sophia3_q10293_4.htm";
                break;
            // Books
            case 32809:
                htmltext = "book_q10293_3.htm";
                break;
            case 32811:
                htmltext = "book_q10293_1.htm";
                break;
            case 32812:
                htmltext = "book_q10293_2.htm";
                break;
            case 32810:
                htmltext = "book_q10293_4.htm";
                break;
            case 32813:
                htmltext = "book_q10293_5.htm";
                break;

        }
        return htmltext;
    }

    private void enterInstance(Player player) {
        Reflection r = player.getActiveReflection();
        if (r != null) {
            if (player.canReenterInstance(156))
                player.teleToLocation(r.getTeleportLoc(), r);
        } else if (player.canEnterInstance(156)) {
            ReflectionUtils.enterReflection(player, 156);
        }
    }

    private void teleportElcardia(Player player) {
        player.getReflection().getNpcs()
                .filter(n -> n.getNpcId() == ElcardiaInzone1)
                .forEach(n -> n.teleToLocation(Location.findPointToStay(player, 60)));
    }
}