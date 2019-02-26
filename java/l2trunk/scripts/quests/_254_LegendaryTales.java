package l2trunk.scripts.quests;

import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;
import java.util.StringTokenizer;

import static l2trunk.commons.lang.NumberUtils.toInt;


public final class _254_LegendaryTales extends Quest {
    private static final int Gilmore = 30754;
    private static final int LargeBone = 17249;
    private static final List<Integer> raids = List.of(
            25718, 25719, 25720, 25721, 25722, 25723, 25724);
    private static final List<Integer> items = List.of(
            13467, 13462, 13464, 13461, 13465, 13463, 13460, 13466, 13459, 13457, 13458);


    public _254_LegendaryTales() {
        super(PARTY_ALL);
        addStartNpc(Gilmore);
        addKillId(raids);
        addQuestItem(LargeBone);
    }

    public static void checkKilledRaids(Player player, int var) {
        player.sendMessage("=== Remaining Dragon(s) ===");
        for (int i : raids) {
            int mask = 1;
            for (int raid : raids) {
                if (raid == i) {
                    break;
                }
                mask = mask << 1;
            }

            if ((var & mask) == 0) {// этого босса еще не убивали
                String name = NpcHolder.getTemplate(i).name();
                player.sendMessage(name);
            }
        }
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("gilmore_q254_05.htm".equalsIgnoreCase(event)) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if (event.startsWith("gilmore_q254_09.htm")) {
            st.takeItems(LargeBone);
            StringTokenizer tokenizer = new StringTokenizer(event);
            tokenizer.nextToken();
            int i = toInt(tokenizer.nextToken()) + 1;
            st.giveItems(items.get(i));
            st.playSound(SOUND_FINISH);
            st.complete();
            st.finish();
            htmltext = "gilmore_q254_09.htm";
        }

        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npc.getNpcId() == Gilmore) {
            if (cond == 0) {
                if (st.player.getLevel() >= 80)
                    htmltext = "gilmore_q254_01.htm";
                else {
                    htmltext = "gilmore_q254_00.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "gilmore_q254_06.htm";
            else if (cond == 2)
                htmltext = "gilmore_q254_07.htm";
        }

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int cond = st.getCond();
        if (cond == 1) {
            int mask = 1;
            int var = npc.getNpcId();
            for (int raid : raids) {
                if (raid == var)
                    break;
                mask = mask << 1;
            }
            var = st.getInt("RaidsKilled");
            if ((var & mask) == 0) { // этого босса еще не убивали
                var |= mask;
                st.set("RaidsKilled", var);
                st.giveItems(LargeBone);
                if (st.getQuestItemsCount(LargeBone) >= 7)
                    st.setCond(2);

            }
            checkKilledRaids(st.player, var);
        }
    }
}