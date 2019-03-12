package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class _501_ProofOfClanAlliance extends Quest {
    // Quest Npcs
    private static final int SIR_KRISTOF_RODEMAI = 30756;
    private static final int STATUE_OF_OFFERING = 30757;
    private static final int WITCH_ATHREA = 30758;
    private static final int WITCH_KALIS = 30759;

    // Quest items
    private static final int HERB_OF_HARIT = 3832;
    private static final int HERB_OF_VANOR = 3833;
    private static final int HERB_OF_OEL_MAHUM = 3834;
    private static final int BLOOD_OF_EVA = 3835;
    private static final int SYMBOL_OF_LOYALTY = 3837;
    private static final int PROOF_OF_ALLIANCE = 3874;
    private static final int VOUCHER_OF_FAITH = 3873;
    private static final int ANTIDOTE_RECIPE = 3872;
    private static final int POTION_OF_RECOVERY = 3889;

    // Quest mobs, drop, rates and prices
    private static final List<Integer> CHESTS = List.of(27173, 27174, 27175, 27176, 27177);
    private static final Map<Integer, Integer> MOBS = Map.of(
            20685, HERB_OF_VANOR,
            20644, HERB_OF_HARIT,
            20576, HERB_OF_OEL_MAHUM);

    private static final int RATE = 35;
    // stackable items paid to retry chest game: (default 10k adena)
    private static final int RETRY_PRICE = 10000;

    public _501_ProofOfClanAlliance() {
        super(PARTY_NONE);

        addStartNpc(SIR_KRISTOF_RODEMAI, STATUE_OF_OFFERING, WITCH_ATHREA);

        addTalkId(WITCH_KALIS);

        addQuestItem(SYMBOL_OF_LOYALTY, ANTIDOTE_RECIPE);

        addKillId(MOBS.keySet());
        addQuestItem(MOBS.values());

        addKillId(CHESTS);
    }

    private QuestState getLeader(QuestState st) {
        Clan clan = st.player.getClan();
        QuestState leader = null;
        if (clan != null && clan.getLeader() != null && clan.getLeader().getPlayer() != null)
            leader = clan.getLeader().getPlayer().getQuestState(this);
        return leader;
    }

    private void removeQuestFromMembers(QuestState st, boolean leader) {
        removeQuestFromOfflineMembers(st);
        removeQuestFromOnlineMembers(st, leader);
    }

    private void removeQuestFromOfflineMembers(QuestState st) {
        if (st.player == null || st.player.getClan() == null) {
            st.exitCurrentQuest();
            return;
        }

        int clan = st.player.getClan().clanId();
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement offline = con.prepareStatement("DELETE FROM character_quests WHERE name = ? AND char_id IN (SELECT obj_id FROM characters WHERE clanId = ? AND online = 0)")) {
            offline.setString(1, name);
            offline.setInt(2, clan);
            offline.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removeQuestFromOnlineMembers(QuestState st, boolean leader) {
        if (st.player == null || st.player.getClan() == null) {
            st.exitCurrentQuest();
            return;
        }

        QuestState l;
        Player pleader = null;

        if (leader) {
            l = getLeader(st);
            if (l != null)
                pleader = l.player;
        }

        if (pleader != null) {
            pleader.stopImmobilized();
            pleader.getEffectList().stopEffect(4082);
        }
        for (Player pl : st.player.getClan().getOnlineMembers(st.player.getClan().getLeaderId()))
            if (pl != null && pl.getQuestState(this) != null)
                pl.getQuestState(this).exitCurrentQuest();
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (st.player == null || st.player.getClan() == null) {
            st.exitCurrentQuest();
            return "noquest";
        }

        QuestState leader = getLeader(st);
        if (leader == null) {
            removeQuestFromMembers(st, true);
            return "Quest Failed";
        }

        String htmltext = event;

        /* ##### Leaders area ###### */
        if (st.player.isClanLeader())
            // SIR_KRISTOF_RODEMAI
            if (event.equalsIgnoreCase("30756-03.htm")) {
                st.setCond(1);
                st.start();
                st.playSound(SOUND_ACCEPT);
            }

            // WITCH_KALIS
            else if ("30759-03.htm".equalsIgnoreCase(event)) {
                st.setCond(2);
                st.set("dead_list", " ");
            } else if ("30759-07.htm".equalsIgnoreCase(event)) {
                st.takeItems(SYMBOL_OF_LOYALTY);
                st.giveItems(ANTIDOTE_RECIPE);
                st.addNotifyOfDeath(st.player, false);
                st.setCond(3);
                st.unset("chest_count");
                st.unset("chest_game");
                st.unset("chest_try");
                st.startQuestTimer("poison_timer", 3600000);
                st.player.altUseSkill(4082, st.player);
                st.player.startImmobilized();
                htmltext = "30759-07.htm";
            }

        // Timers
        if ("poison_timer".equalsIgnoreCase(event)) {
            removeQuestFromMembers(st, true);
            htmltext = "30759-09.htm";
        } else if ("chest_timer".equalsIgnoreCase(event)) {
            htmltext = "";
            if (leader.getInt("chest_game") < 2)
                stop_chest_game(st);
        }

        /* ##### Members area ###### */

        // STATUE_OF_OFFERING
        else if ("30757-04.htm".equalsIgnoreCase(event)) {
            List<String> deadlist = new ArrayList<>(List.of(leader.get("dead_list").split(" ")));
            deadlist.add(st.player.getName());
            StringBuilder deadstr = new StringBuilder();
            for (String s : deadlist)
                deadstr.append(s).append(" ");
            leader.set("dead_list", deadstr.toString());
            st.addNotifyOfDeath(leader.player, false);
            if (Rnd.chance(50))
                st.player.reduceCurrentHp(st.player.getCurrentHp() * 8, st.player, null, true, true, false, false, false, false, false);
            st.giveItems(SYMBOL_OF_LOYALTY);
            st.playSound(SOUND_ACCEPT);
        } else if ("30757-05.htm".equalsIgnoreCase(event))
            st.exitCurrentQuest();

            // WITCH_ATHREA
        else if ("30758-03.htm".equalsIgnoreCase(event))
            start_chest_game(st);
        else if ("30758-07.htm".equalsIgnoreCase(event))
            if (st.haveQuestItem(ADENA_ID, RETRY_PRICE))
                htmltext = "30758-06.htm";
            else
                st.takeItems(ADENA_ID, RETRY_PRICE);

        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();

        if (st.player == null || st.player.getClan() == null) {
            st.exitCurrentQuest();
            return htmltext;
        }

        QuestState leader = getLeader(st);
        if (leader == null) {
            removeQuestFromMembers(st, true);
            return "Quest Failed";
        }

        int npcId = npc.getNpcId();
        if (npcId == SIR_KRISTOF_RODEMAI) {
            if (!st.player.isClanLeader()) {
                st.exitCurrentQuest();
                return "30756-10.htm";
            } else if (st.player.getClan().getLevel() <= 2) {
                st.exitCurrentQuest();
                return "30756-08.htm";
            } else if (st.player.getClan().getLevel() >= 4) {
                st.exitCurrentQuest();
                return "30756-09.htm";
            } else if (st.haveQuestItem(VOUCHER_OF_FAITH)) {
                st.playSound(SOUND_FANFARE2);
                st.takeItems(VOUCHER_OF_FAITH);
                st.giveItems(PROOF_OF_ALLIANCE);
                st.addExpAndSp(0, 120000);
                htmltext = "30756-07.htm";
                st.exitCurrentQuest();
            } else if (cond == 1 || cond == 2)
                return "30756-06.htm";
            else if (!st.haveQuestItem(PROOF_OF_ALLIANCE)) {
                st.setCond(0);
                return "30756-01.htm";
            } else {
                st.exitCurrentQuest();
                return htmltext;
            }
        } else if (npcId == WITCH_KALIS) {
            if (st.player.isClanLeader()) {
                if (cond == 1)
                    return "30759-01.htm";
                else if (cond == 2) {
                    htmltext = "30759-05.htm";
                    if (st.haveQuestItem(SYMBOL_OF_LOYALTY, 3)) {
                        int deads = 0;
                        try {
                            deads = st.get("dead_list").split(" ").length;
                        } finally {
                            if (deads == 3)
                                htmltext = "30759-06.htm";
                        }
                    }
                } else if (cond == 3)
                    if (st.haveAllQuestItems(HERB_OF_HARIT, HERB_OF_VANOR, HERB_OF_OEL_MAHUM, BLOOD_OF_EVA, ANTIDOTE_RECIPE)) {
                        st.takeAllItems(ANTIDOTE_RECIPE, HERB_OF_HARIT, HERB_OF_VANOR, HERB_OF_OEL_MAHUM, BLOOD_OF_EVA);
                        st.giveItems(POTION_OF_RECOVERY);
                        st.giveItems(VOUCHER_OF_FAITH);
                        st.cancelQuestTimer("poison_timer");
                        removeQuestFromMembers(st, false);
                        st.player.stopImmobilized();
                        st.player.getEffectList().stopEffect(4082);
                        st.setCond(4);
                        st.playSound(SOUND_FINISH);
                        return "30759-08.htm";
                    } else if (!st.haveQuestItem(VOUCHER_OF_FAITH))
                        return "30759-10.htm";
            } else if (leader.getCond() == 3)
                return "30759-11.htm";
        } else if (npcId == STATUE_OF_OFFERING) {
            if (st.player.isClanLeader())
                return "30757-03.htm";
            else if (st.player.getLevel() <= 39) {
                st.exitCurrentQuest();
                return "30757-02.htm";
            } else {
                String[] dlist;
                int deads;
                try {
                    dlist = leader.get("dead_list").split(" ");
                    deads = dlist.length;
                } catch (Exception e) {
                    removeQuestFromMembers(st, true);
                    return "Who are you?";
                }
                if (deads < 3) {
                    return "30757-01.htm";
                }
            }
        } else if (npcId == WITCH_ATHREA) {
            if (st.player.isClanLeader())
                return "30757-03.htm";

            // Проверяем, участвует ли в квесте
            String[] dlist;
            try {
                dlist = leader.get("dead_list").split(" ");
            } catch (Exception e) {
                st.exitCurrentQuest();
                return "Who are you?";
            }
            boolean flag = false;
            for (String str : dlist)
                if (st.player.getName().equalsIgnoreCase(str))
                    flag = true;
            if (!flag) {
                st.exitCurrentQuest();
                return "Who are you?";
            }

            int game_state = leader.getInt("chest_game");
            if (game_state == 0) {
                if (!leader.isSet("chest_try"))
                    return "30758-01.htm";
                return "30758-05.htm";
            } else if (game_state == 1)
                return "30758-09.htm";
            else if (game_state == 2) {
                st.playSound(SOUND_FINISH);
                st.giveItems(BLOOD_OF_EVA);
                st.cancelQuestTimer("chest_timer");
                stop_chest_game(st);
                leader.set("chest_game", 3);
                return "30758-08.htm";
            }
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.player == null || st.player.getClan() == null) {
            st.exitCurrentQuest();
            return;
        }

        QuestState leader = getLeader(st);
        if (leader == null) {
            removeQuestFromMembers(st, true);
            return;
        }

        // first part, general checking
        int npcId = npc.getNpcId();

        if (!leader.isRunningQuestTimer("poison_timer")) {
            stop_chest_game(st);
            return;
        }

        // second part, herbs gathering
        MOBS.forEach((k, v) -> {
            if (npcId == k && !st.isSet("" + v))
                if (Rnd.chance(RATE)) {
                    st.giveItems(v);
                    leader.set("" + v);
                    st.playSound(SOUND_MIDDLE);
                }
        });
        // third part, chest game
        for (int i : CHESTS)
            if (npcId == i) {
                if (!leader.isRunningQuestTimer("chest_timer")) {
                    stop_chest_game(st);
                    return;
                }
                if (Rnd.chance(25)) {
                    Functions.npcSay(npc, "###### BINGO! ######");
                    leader.inc("chest_count");
                    if (leader.getInt("chest_count") >= 4) {
                        stop_chest_game(st);
                        leader.set("chest_game", 2);
                        leader.cancelQuestTimer("chest_timer");
                        st.playSound(SOUND_MIDDLE);
                    } else
                        st.playSound(SOUND_ITEMGET);
                }
                return;
            }
    }

    private void start_chest_game(QuestState st) {
        if (st.player == null || st.player.getClan() == null) {
            st.exitCurrentQuest();
            return;
        }

        QuestState leader = getLeader(st);
        if (leader == null) {
            removeQuestFromMembers(st, true);
            return;
        }

        leader.set("chest_game");
        leader.unset("chest_count");
        leader.inc("chest_try");

        GameObjectsStorage.getAllByNpcId(CHESTS, false).forEach(GameObject::deleteMe);

        for (int n = 1; n <= 5; n++)
            for (int i : CHESTS)
                leader.addSpawn(i, Location.of(102100, 103450, -3400), 100, 60000);
        leader.startQuestTimer("chest_timer", 60000);
    }

    private void stop_chest_game(QuestState st) {
        QuestState leader = getLeader(st);
        if (leader == null) {
            removeQuestFromMembers(st, true);
            return;
        }

        GameObjectsStorage.getAllByNpcId(CHESTS, false).forEach(GameObject::deleteMe);

        leader.unset("chest_game");
    }

    @Override
    public void onDeath(Creature npc, Creature pc, QuestState st) {
        if (st.player == null || st.player.getClan() == null) {
            st.exitCurrentQuest();
            return;
        }

        QuestState leader = getLeader(st);
        if (leader == null) {
            removeQuestFromMembers(st, true);
            return;
        }

        if (st.player == pc) {
            leader.cancelQuestTimer("poison_timer");
            leader.cancelQuestTimer("chest_timer");

            removeQuestFromMembers(st, true);
        }
    }
}