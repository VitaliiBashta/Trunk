package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.ArrayList;
import java.util.List;

public final class _135_TempleExecutor extends Quest {
    // NPCs
    private final static int Shegfield = 30068;
    private final static int Pano = 30078;
    private final static int Alex = 30291;
    private final static int Sonin = 31773;

    // Mobs
    private final static List<Integer> mobs = List.of(
            20781, 21104, 21105, 21106, 21107);

    // Quest items
    private final static int Stolen_Cargo = 10328;
    private final static int Hate_Crystal = 10329;
    private final static int Old_Treasure_Map = 10330;
    private final static int Sonins_Credentials = 10331;
    private final static int Panos_Credentials = 10332;
    private final static int Alexs_Credentials = 10333;

    // items
    private final static int Badge_Temple_Executor = 10334;

    public _135_TempleExecutor() {
        addStartNpc(Shegfield);
        addTalkId(Alex,Sonin,Pano);
        addKillId(mobs);
        addQuestItem(Stolen_Cargo,Hate_Crystal,Old_Treasure_Map,
                Sonins_Credentials,Panos_Credentials,Alexs_Credentials);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int state = st.getState();
        if ("shegfield_q0135_03.htm".equalsIgnoreCase(event) && state == CREATED) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("shegfield_q0135_13.htm".equalsIgnoreCase(event) && state == STARTED) {
            st.playSound(SOUND_FINISH);
            st.unset("Report");
            st.giveAdena( 16924);
            st.addExpAndSp(30000, 2000);
            st.giveItems(Badge_Temple_Executor);
            st.finish();
        } else if ("shegfield_q0135_04.htm".equalsIgnoreCase(event) && state == STARTED) {
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if ("alankell_q0135_07.htm".equalsIgnoreCase(event) && state == STARTED) {
            st.setCond(3);
            st.playSound(SOUND_MIDDLE);
        }

        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int _state = st.getState();
        if (_state == COMPLETED)
            return "completed";

        int npcId = npc.getNpcId();
        if (_state == CREATED) {
            if (npcId != Shegfield)
                return "noquest";
            if (st.player.getLevel() < 35) {
                st.exitCurrentQuest();
                return "shegfield_q0135_02.htm";
            }
            st.setCond(0);
            return "shegfield_q0135_01.htm";
        }

        int cond = st.getCond();

        if (npcId == Shegfield && _state == STARTED) {
            if (cond == 1)
                return "shegfield_q0135_03.htm";
            if (cond == 5) {
                if (st.isSet("Report"))
                    return "shegfield_q0135_09.htm";
                if (st.haveAllQuestItems(Sonins_Credentials,Panos_Credentials,Alexs_Credentials) ) {
                    st.takeAllItems(Panos_Credentials,Sonins_Credentials,Alexs_Credentials);
                    st.set("Report");
                    return "shegfield_q0135_08.htm";
                }
                return "noquest";
            }
            return "shegfield_q0135_06.htm";
        }

        if (npcId == Alex && _state == STARTED) {
            if (cond == 2)
                return "alankell_q0135_02.htm";
            if (cond == 3)
                return "alankell_q0135_08.htm";
            if (cond == 4) {
                if (st.haveAllQuestItems(Sonins_Credentials,Panos_Credentials)) {
                    st.setCond(5);
                    st.takeItems(Old_Treasure_Map);
                    st.giveItems(Alexs_Credentials);
                    st.playSound(SOUND_MIDDLE);
                    return "alankell_q0135_10.htm";
                }
                return "alankell_q0135_09.htm";
            }
            if (cond == 5)
                return "alankell_q0135_11.htm";
        }

        if (npcId == Sonin && _state == STARTED) {
            if (st.getQuestItemsCount(Stolen_Cargo) < 10)
                return "warehouse_keeper_sonin_q0135_04.htm";
            st.takeItems(Stolen_Cargo);
            st.giveItems(Sonins_Credentials);
            st.playSound(SOUND_MIDDLE);
            return "warehouse_keeper_sonin_q0135_03.htm";
        }

        if (npcId == Pano && _state == STARTED && cond == 4) {
            if (st.getQuestItemsCount(Hate_Crystal) < 10)
                return "pano_q0135_04.htm";
            st.takeItems(Hate_Crystal);
            st.giveItems(Panos_Credentials);
            st.playSound(SOUND_MIDDLE);
            return "pano_q0135_03.htm";
        }

        return "noquest";
    }

    @Override
    public void onKill(NpcInstance npc, QuestState qs) {
        if (qs.getState() == STARTED && qs.getCond() == 3) {
            List<Integer> drops = new ArrayList<>();
            if (qs.getQuestItemsCount(Stolen_Cargo) < 10)
                drops.add(Stolen_Cargo);
            if (qs.getQuestItemsCount(Hate_Crystal) < 10)
                drops.add(Hate_Crystal);
            if (qs.getQuestItemsCount(Old_Treasure_Map) < 10)
                drops.add(Old_Treasure_Map);
            if (drops.isEmpty())
                return;
            int drop = Rnd.get(drops);
            qs.giveItems(drop);
            if (drops.size() == 1 && qs.getQuestItemsCount(drop) >= 10) {
                qs.setCond(4);
                qs.playSound(SOUND_MIDDLE);
                return;
            }
            qs.playSound(SOUND_ITEMGET);
        }
    }
}