package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.scripts.bosses.FourSepulchersManager;

import java.util.List;
import java.util.stream.IntStream;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class _620_FourGoblets extends Quest {
    final static int Sealed_Box = 7255;
    // NPC
    private static final int NAMELESS_SPIRIT = 31453;
    private static final int GHOST_OF_WIGOTH_1 = 31452;
    private static final int GHOST_OF_WIGOTH_2 = 31454;
    private static final int CONQ_SM = 31921;
    private static final int EMPER_SM = 31922;
    private static final int SAGES_SM = 31923;
    private static final int JUDGE_SM = 31924;
    private static final int GHOST_CHAMBERLAIN_1 = 31919;
    private static final int GHOST_CHAMBERLAIN_2 = 31920;
    // ITEMS
    private static final int GRAVE_PASS = 7261;
    private static final List<Integer> GOBLETS = List.of(7256, 7257, 7258, 7259);
    private static final int RELIC = 7254;
    // REWARDS
    private static final int ANTIQUE_BROOCH = 7262;
    private static final List<Integer> RCP_REWARDS = List.of(
            6881, 6883, 6885, 6887, 6891, 6893, 6895, 6897, 6899, 7580);

    public _620_FourGoblets() {
        addStartNpc(NAMELESS_SPIRIT, CONQ_SM, EMPER_SM, SAGES_SM, JUDGE_SM, GHOST_CHAMBERLAIN_1, GHOST_CHAMBERLAIN_2);

        addTalkId(GHOST_OF_WIGOTH_1, GHOST_OF_WIGOTH_2);

        addQuestItem(Sealed_Box, GRAVE_PASS);
        addQuestItem(GOBLETS);

        addKillId(IntStream.rangeClosed(18120, 18256).toArray());
    }

    private static String onOpenBoxes(QuestState st, String count) {
        try {
            return new OpenSealedBox(st, Integer.parseInt(count)).apply();
        } catch (Exception e) {
            e.printStackTrace();
            return "Dont try to cheat with me!";
        }
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        Player player = st.player;
        int cond = st.getCond();

        if (event.equalsIgnoreCase("Enter")) {
            FourSepulchersManager.tryEntry(npc, player);
            return null;
        }

        if (event.equalsIgnoreCase("accept")) {
            if (cond == 0) {
                if (st.player.getLevel() >= 74) {
                    st.start();
                    st.playSound(SOUND_ACCEPT);
                    st.setCond(1);
                    return "31453-13.htm";
                }
                st.exitCurrentQuest();
                return "31453-12.htm";
            }
        } else if (event.startsWith("openBoxes "))
            return onOpenBoxes(st, event.replace("openBoxes ", "").trim());
        if ("12".equals(event)) {
            if (!st.haveAllItems(GOBLETS))
                return "31453-14.htm";
            st.takeItems(GOBLETS);
            st.giveItems(ANTIQUE_BROOCH);
            st.setCond(2);
            st.playSound(SOUND_FINISH);
            return "31453-16.htm";
        } else if ("13".equals(event)) {
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
            return "31453-18.htm";
        } else if (event.equals("14")) {
            if (cond == 2)
                return "31453-19.htm";
            return "31453-13.htm";
        }
        // Ghost Chamberlain of Elmoreden: teleport to 4th sepulcher
        else if (event.equals("15")) {
            if (st.getQuestItemsCount(ANTIQUE_BROOCH) >= 1) {
                st.player.teleToLocation(178298, -84574, -7216);
                return null;
            }
            if (st.haveQuestItem(GRAVE_PASS)) {
                st.takeItems(GRAVE_PASS, 1);
                st.player.teleToLocation(178298, -84574, -7216);
                return null;
            }
            return "" + npc.getNpcId() + "-0.htm";
        }
        // Ghost Chamberlain of Elmoreden: teleport to Imperial Tomb entrance
        else if ("16".equals(event)) {
            if (st.haveQuestItem(ANTIQUE_BROOCH)) {
                st.player.teleToLocation(186942, -75602, -2834);
                return null;
            }
            if (st.haveQuestItem(GRAVE_PASS)) {
                st.takeItems(GRAVE_PASS, 1);
                st.player.teleToLocation(186942, -75602, -2834);
                return null;
            }
            return "" + npc.getNpcId() + "-0.htm";
        }
        // teleport to Pilgrims Temple
        else if (event.equals("17")) {
            if (st.haveQuestItem(ANTIQUE_BROOCH))
                st.player.teleToLocation(169590, -90218, -2914);
            else {
                st.takeItems(GRAVE_PASS, 1);
                st.player.teleToLocation(169590, -90218, -2914);
            }
            return "31452-6.htm";
        } else if (event.equals("18")) {
            if (st.getSumQuestItemsCount(GOBLETS) < 3)
                return "31452-3.htm";
            if (st.getSumQuestItemsCount(GOBLETS) == 3)
                return "31452-4.htm";
            if (st.getSumQuestItemsCount(GOBLETS) >= 4)
                return "31452-5.htm";
        } else if (event.equals("19"))
            return new OpenSealedBox(st, 1).apply();
        else if (event.startsWith("19 "))
            return onOpenBoxes(st, event.replaceFirst("19 ", ""));
        else if (event.equals("11"))
            return "<html><body><a action=\"bypass -h Quest _620_FourGoblets 19\">\"Please open a box.\"</a><br><a action=\"bypass -h Quest _620_FourGoblets 19 5\">\"Please open 5 boxes.\"</a><br><a action=\"bypass -h Quest _620_FourGoblets 19 10\">\"Please open 10 boxes.\"</a><br><a action=\"bypass -h Quest _620_FourGoblets 19 50\">\"Please open 50 boxes.\"</a><br></body></html>";
        else {
            int id = toInt(event);
            if (RCP_REWARDS.contains(id)) {
                st.takeItems(RELIC, 1000);
                st.giveItems(id);
                return "31454-17.htm";
            }
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int id = st.getState();
        int cond = st.getCond();
        if (id == CREATED)
            st.setCond(0);
        if (npcId == NAMELESS_SPIRIT) {
            if (cond == 0)
                if (st.player.getLevel() >= 74)
                    htmltext = "31453-1.htm";
                else {
                    htmltext = "31453-12.htm";
                    st.exitCurrentQuest();
                }
            else if (cond == 1)
                if (st.haveAllItems(GOBLETS))
                    htmltext = "31453-15.htm";
                else
                    htmltext = "31453-14.htm";
            else if (cond == 2)
                htmltext = "31453-17.htm";
        } else if (npcId == GHOST_OF_WIGOTH_1) {
            if (cond == 2)
                htmltext = "31452-2.htm";
            else if (cond == 1)
                if (st.getSumQuestItemsCount(GOBLETS) == 1)
                    htmltext = "31452-1.htm";
                else if (st.getSumQuestItemsCount(GOBLETS) > 1)
                    htmltext = "31452-2.htm";
        } else if (npcId == GHOST_OF_WIGOTH_2) {
            if (st.getQuestItemsCount(RELIC) >= 1000)
                if (st.getQuestItemsCount(Sealed_Box) >= 1)
                    if (st.haveAllItems(GOBLETS))
                        htmltext = "31454-4.htm";
                    else if (st.haveAllItems(GOBLETS))
                        htmltext = "31454-8.htm";
                    else
                        htmltext = "31454-12.htm";
                else if (st.haveAllItems(GOBLETS))
                    htmltext = "31454-3.htm";
                else if (st.getSumQuestItemsCount(GOBLETS) > 1)
                    htmltext = "31454-7.htm";
                else
                    htmltext = "31454-11.htm";
            else if (st.getQuestItemsCount(Sealed_Box) >= 1)
                if (st.haveAllItems(GOBLETS))
                    htmltext = "31454-2.htm";
                else if (st.getSumQuestItemsCount(GOBLETS) > 1)
                    htmltext = "31454-6.htm";
                else
                    htmltext = "31454-10.htm";
            else if (st.haveAllItems(GOBLETS))
                htmltext = "31454-1.htm";
            else if (st.getSumQuestItemsCount(GOBLETS) > 1)
                htmltext = "31454-5.htm";
            else
                htmltext = "31454-9.htm";
        } else if (npcId == CONQ_SM)
            htmltext = "31921-E.htm";
        else if (npcId == EMPER_SM)
            htmltext = "31922-E.htm";
        else if (npcId == SAGES_SM)
            htmltext = "31923-E.htm";
        else if (npcId == JUDGE_SM)
            htmltext = "31924-E.htm";
        else if (npcId == GHOST_CHAMBERLAIN_1)
            htmltext = "31919-1.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if ((cond == 1 || cond == 2) && npcId >= 18120 && npcId <= 18256 && Rnd.chance(30)) {
            st.giveItems(Sealed_Box);
            st.playSound(SOUND_ITEMGET);
        }
    }
}