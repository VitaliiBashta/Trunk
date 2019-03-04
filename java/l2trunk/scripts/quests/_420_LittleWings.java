package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.Map;
import java.util.stream.IntStream;

public final class _420_LittleWings extends Quest {
    // NPCs
    private static final int Cooper = 30829;
    private static final int Cronos = 30610;
    private static final int Byron = 30711;
    private static final int Maria = 30608;
    private static final int Mimyu = 30747;
    private static final int Exarion = 30748;
    private static final int Zwov = 30749;
    private static final int Kalibran = 30750;
    private static final int Suzet = 30751;
    private static final int Shamhai = 30752;
    // Mobs
    private static final int Enchanted_Valey_First = 20589;
    private static final int Enchanted_Valey_Last = 20599;
    private static final int Toad_Lord = 20231;
    private static final int Marsh_Spider = 20233;
    private static final int Leto_Lizardman_Warrior = 20580;
    private static final int Road_Scavenger = 20551;
    private static final int Breka_Orc_Overlord = 20270;
    private static final int Dead_Seeker = 20202;
    // items
    private static final int Coal = 1870;
    private static final int Charcoal = 1871;
    private static final int Silver_Nugget = 1873;
    private static final int Stone_of_Purity = 1875;
    private static final int GemstoneD = 2130;
    private static final int GemstoneC = 2131;
    private static final int Dragonflute_of_Wind = 3500;
    private static final int Dragonflute_of_Twilight = 3502;
    private static final int Hatchlings_Soft_Leather = 3912;
    private static final int Hatchlings_Mithril_Coat = 3918;
    private static final int Food_For_Hatchling = 4038;
    // Quest items
    private static final int Fairy_Dust = 3499;
    private static final int Fairy_Stone = 3816;
    private static final int Deluxe_Fairy_Stone = 3817;
    private static final int Fairy_Stone_List = 3818;
    private static final int Deluxe_Fairy_Stone_List = 3819;
    private static final int Toad_Lord_Back_Skin = 3820;
    private static final int Juice_of_Monkshood = 3821;

    private static final int Scale_of_Drake_Exarion = 3822;
    private static final int Scale_of_Drake_Zwov = 3824;
    private static final int Scale_of_Drake_Kalibran = 3826;
    private static final int Scale_of_Wyvern_Suzet = 3828;
    private static final int Scale_of_Wyvern_Shamhai = 3830;

    private static final int Egg_of_Drake_Exarion = 3823;
    private static final int Egg_of_Drake_Zwov = 3825;
    private static final int Egg_of_Drake_Kalibran = 3827;
    private static final int Egg_of_Wyvern_Suzet = 3829;
    private static final int Egg_of_Wyvern_Shamhai = 3831;

    // Chances
    private static final int Toad_Lord_Back_Skin_Chance = 30;
    private static final int Egg_Chance = 50;
    private static final int Pet_Armor_Chance = 35;

    private static final Map<Integer, Integer> Fairy_Stone_Items = Map.of(
            Coal, 10,
            Charcoal, 10,
            GemstoneD, 1,
            Silver_Nugget, 3,
            Toad_Lord_Back_Skin, 10);

    private static final Map<Integer, Integer> Delux_Fairy_Stone_Items = Map.of(
            Coal, 10,
            Charcoal, 10,
            GemstoneC, 1,
            Stone_of_Purity, 1,
            Silver_Nugget, 5,
            Toad_Lord_Back_Skin, 20);

    private static final int[][] wyrms = {
            {
                    Leto_Lizardman_Warrior,
                    Exarion,
                    Scale_of_Drake_Exarion,
                    Egg_of_Drake_Exarion
            },
            {
                    Marsh_Spider,
                    Zwov,
                    Scale_of_Drake_Zwov,
                    Egg_of_Drake_Zwov
            },
            {
                    Road_Scavenger,
                    Kalibran,
                    Scale_of_Drake_Kalibran,
                    Egg_of_Drake_Kalibran
            },
            {
                    Breka_Orc_Overlord,
                    Suzet,
                    Scale_of_Wyvern_Suzet,
                    Egg_of_Wyvern_Suzet
            },
            {
                    Dead_Seeker,
                    Shamhai,
                    Scale_of_Wyvern_Shamhai,
                    Egg_of_Wyvern_Shamhai
            }
    };

    public _420_LittleWings() {
        super(false);

        addStartNpc(Cooper);

        addTalkId(Cronos, Mimyu, Byron, Maria);

        addKillId(Toad_Lord);
        addKillId(IntStream.rangeClosed(Enchanted_Valey_First, Enchanted_Valey_Last).toArray());

        for (int[] wyrm : wyrms) {
            addTalkId(wyrm[1]);
            addKillId(wyrm[0]);
        }

        addQuestItem(Fairy_Dust, Fairy_Stone, Deluxe_Fairy_Stone, Fairy_Stone_List, Deluxe_Fairy_Stone_List,
                Toad_Lord_Back_Skin, Juice_of_Monkshood, Scale_of_Drake_Exarion, Scale_of_Drake_Zwov,
                Scale_of_Drake_Kalibran, Scale_of_Wyvern_Suzet, Scale_of_Wyvern_Shamhai, Egg_of_Drake_Exarion,
                Egg_of_Drake_Zwov, Egg_of_Drake_Kalibran, Egg_of_Wyvern_Suzet, Egg_of_Wyvern_Shamhai);
    }

    private static int getWyrmScale(int npc_id) {
        for (int[] wyrm : wyrms)
            if (npc_id == wyrm[1])
                return wyrm[2];
        return 0;
    }

    private static int getWyrmEgg(int npc_id) {
        for (int[] wyrm : wyrms)
            if (npc_id == wyrm[1])
                return wyrm[3];
        return 0;
    }

    private static int isWyrmStoler(int npc_id) {
        for (int[] wyrm : wyrms)
            if (npc_id == wyrm[0])
                return wyrm[1];
        return 0;
    }

    private static int getNeededSkins(QuestState st) {
        if (st.haveQuestItem(Deluxe_Fairy_Stone_List))
            return 20;
        if (st.haveQuestItem(Fairy_Stone_List))
            return 10;
        return -1;
    }

    private static boolean checkFairyStoneItems(QuestState st, Map<Integer, Integer> item_list) {
        return item_list.entrySet().stream()
                .allMatch(e -> st.haveQuestItem(e.getKey(), e.getValue()));

    }

    private static void takeFairyStoneItems(QuestState st, Map<Integer, Integer> item_list) {
        item_list.forEach(st::takeItems);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int state = st.getState();
        int cond = st.getCond();
        if ("30829-02.htm".equalsIgnoreCase(event) && state == CREATED) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if (("30610-05.htm".equalsIgnoreCase(event) || "30610-12.htm".equalsIgnoreCase(event)) && state == STARTED && cond == 1) {
            st.setCond(2);
            st.takeAllItems(Fairy_Stone, Deluxe_Fairy_Stone, Fairy_Stone_List, Deluxe_Fairy_Stone_List);
            st.giveItems(Fairy_Stone_List);
            st.playSound(SOUND_MIDDLE);
        } else if (("30610-06.htm".equalsIgnoreCase(event) || "30610-13.htm".equalsIgnoreCase(event)) && state == STARTED && cond == 1) {
            st.setCond(2);
            st.takeAllItems(Fairy_Stone, Deluxe_Fairy_Stone, Fairy_Stone_List, Deluxe_Fairy_Stone_List);
            st.giveItems(Deluxe_Fairy_Stone_List);
            st.playSound(SOUND_MIDDLE);
        } else if ("30608-03.htm".equalsIgnoreCase(event) && state == STARTED && cond == 2 && st.haveQuestItem(Fairy_Stone_List)) {
            if (checkFairyStoneItems(st, Fairy_Stone_Items)) {
                st.setCond(3);
                takeFairyStoneItems(st, Fairy_Stone_Items);
                st.giveItems(Fairy_Stone);
                st.playSound(SOUND_MIDDLE);
            } else
                return "30608-01.htm";
        } else if ("30608-03a.htm".equalsIgnoreCase(event) && state == STARTED && cond == 2 && st.haveQuestItem(Deluxe_Fairy_Stone_List)) {
            if (checkFairyStoneItems(st, Delux_Fairy_Stone_Items)) {
                st.setCond(3);
                takeFairyStoneItems(st, Delux_Fairy_Stone_Items);
                st.giveItems(Deluxe_Fairy_Stone);
                st.playSound(SOUND_MIDDLE);
            } else
                return "30608-01a.htm";
        } else if ("30711-03.htm".equalsIgnoreCase(event) && state == STARTED && cond == 3 && st.haveAnyQuestItems(Fairy_Stone,Deluxe_Fairy_Stone) ) {
            st.setCond(4);
            st.playSound(SOUND_MIDDLE);
            if (st.haveQuestItem(Deluxe_Fairy_Stone))
                return st.isSet("broken") ? "30711-04a.htm" : "30711-03a.htm";
            if (st.isSet("broken"))
                return "30711-04.htm";
        } else if ("30747-02.htm".equalsIgnoreCase(event) && state == STARTED && cond == 4 && st.haveQuestItem(Fairy_Stone)) {
            st.takeItems(Fairy_Stone);
            st.set("takedStone");
        } else if ("30747-02a.htm".equalsIgnoreCase(event) && state == STARTED && cond == 4 && st.haveQuestItem(Deluxe_Fairy_Stone)) {
            st.takeItems(Deluxe_Fairy_Stone);
            st.set("takedStone", 2);
            st.giveItems(Fairy_Dust);
            st.playSound(SOUND_ITEMGET);
        } else if ("30747-04.htm".equalsIgnoreCase(event) && state == STARTED && cond == 4 && st.getInt("takedStone") > 0) {
            st.setCond(5);
            st.unset("takedStone");
            st.giveItems(Juice_of_Monkshood);
            st.playSound(SOUND_ITEMGET);
        } else if ("30748-02.htm".equalsIgnoreCase(event) && cond == 5 && state == STARTED && st.haveQuestItem(Juice_of_Monkshood)) {
            st.setCond(6);
            st.takeItems(Juice_of_Monkshood);
            st.giveItems(3822);
            st.playSound(SOUND_ITEMGET);
        } else if ("30749-02.htm".equalsIgnoreCase(event) && cond == 5 && state == STARTED && st.getQuestItemsCount(Juice_of_Monkshood) > 0) {
            st.setCond(6);
            st.takeItems(Juice_of_Monkshood);
            st.giveItems(3824);
            st.playSound(SOUND_ITEMGET);
        } else if ("30750-02.htm".equalsIgnoreCase(event) && cond == 5 && state == STARTED && st.getQuestItemsCount(Juice_of_Monkshood) > 0) {
            st.setCond(6);
            st.takeItems(Juice_of_Monkshood);
            st.giveItems(3826);
            st.playSound(SOUND_ITEMGET);
        } else if ("30751-02.htm".equalsIgnoreCase(event) && cond == 5 && state == STARTED && st.getQuestItemsCount(Juice_of_Monkshood) > 0) {
            st.setCond(6);
            st.takeItems(Juice_of_Monkshood);
            st.giveItems(3828);
            st.playSound(SOUND_ITEMGET);
        } else if ("30752-02.htm".equalsIgnoreCase(event) && cond == 5 && state == STARTED && st.getQuestItemsCount(Juice_of_Monkshood) > 0) {
            st.setCond(6);
            st.takeItems(Juice_of_Monkshood);
            st.giveItems(3830);
            st.playSound(SOUND_ITEMGET);
        } else if ("30747-09.htm".equalsIgnoreCase(event) && state == STARTED && cond == 7) {
            int egg_id = 0;
            for (int[] wyrm : wyrms)
                if (st.getQuestItemsCount(wyrm[2]) == 0 && st.getQuestItemsCount(wyrm[3]) >= 1) {
                    egg_id = wyrm[3];
                    break;
                }
            if (egg_id == 0)
                return "noquest";
            st.takeItems(egg_id);
            st.giveItems(Rnd.get(Dragonflute_of_Wind, Dragonflute_of_Twilight));
            if (st.haveQuestItem(Fairy_Dust)) {
                st.playSound(SOUND_MIDDLE);
                return "30747-09a.htm";
            }
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        } else if ("30747-10.htm".equalsIgnoreCase(event) && state == STARTED && cond == 7) {
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        } else if ("30747-11.htm".equalsIgnoreCase(event) && state == STARTED && cond == 7) {
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
            if (st.getQuestItemsCount(Fairy_Dust) == 0)
                return "30747-10.htm";
            st.takeItems(Fairy_Dust);
            if (Rnd.chance(Pet_Armor_Chance)) {
                int armor_id = Hatchlings_Soft_Leather + Rnd.get((int) st.getRateQuestsReward());
                if (armor_id > Hatchlings_Mithril_Coat)
                    armor_id = Hatchlings_Mithril_Coat;
                st.giveItems(armor_id);
            } else
                st.giveItems(Food_For_Hatchling, 20, true);
        }

        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int _state = st.getState();
        int npcId = npc.getNpcId();
        if (_state == CREATED) {
            if (npcId != Cooper)
                return "noquest";
            if (st.player.getLevel() < 35) {
                st.exitCurrentQuest();
                return "30829-00.htm";
            }
            st.setCond(0);
            return "30829-01.htm";
        }

        if (_state != STARTED)
            return "noquest";
        int cond = st.getCond();
        int broken = st.getInt("broken");

        if (npcId == Cooper) {
            if (cond == 1)
                return "30829-02.htm";
            return "30829-03.htm";
        }

        if (npcId == Cronos) {
            if (cond == 1)
                return broken == 1 ? "30610-10.htm" : "30610-01.htm";
            if (cond == 2)
                return "30610-07.htm";
            if (cond == 3)
                return broken == 1 ? "30610-14.htm" : "30610-08.htm";
            if (cond == 4)
                return "30610-09.htm";
            if (cond > 4)
                return "30610-11.htm";
        }

        if (npcId == Maria)
            if (cond == 2) {
                if (st.getQuestItemsCount(Deluxe_Fairy_Stone_List) > 0)
                    return checkFairyStoneItems(st, Delux_Fairy_Stone_Items) ? "30608-02a.htm" : "30608-01a.htm";
                if (st.getQuestItemsCount(Fairy_Stone_List) > 0)
                    return checkFairyStoneItems(st, Fairy_Stone_Items) ? "30608-02.htm" : "30608-01.htm";
            } else if (cond > 2)
                return "30608-04.htm";

        if (npcId == Byron) {
            if (cond == 1 && broken == 1)
                return "30711-06.htm";
            if (cond == 2 && broken == 1)
                return "30711-07.htm";
            if (cond == 3 && st.getQuestItemsCount(Fairy_Stone) + st.getQuestItemsCount(Deluxe_Fairy_Stone) > 0)
                return "30711-01.htm";
            if (cond >= 4 && st.getQuestItemsCount(Deluxe_Fairy_Stone) > 0)
                return "30711-05a.htm";
            if (cond >= 4 && st.getQuestItemsCount(Fairy_Stone) > 0)
                return "30711-05.htm";
        }

        if (npcId == Mimyu) {
            if (cond == 4 && st.haveAnyQuestItems(Deluxe_Fairy_Stone))
                return "30747-01a.htm";
            if (cond == 4 && st.haveAnyQuestItems(Fairy_Stone))
                return "30747-01.htm";
            if (cond == 5)
                return "30747-05.htm";
            if (cond == 6) {
                for (int[] wyrm : wyrms)
                    if (st.getQuestItemsCount(wyrm[2]) == 0 && st.getQuestItemsCount(wyrm[3]) >= 20)
                        return "30747-07.htm";
                return "30747-06.htm";
            }
            if (cond == 7)
                for (int[] wyrm : wyrms)
                    if (st.getQuestItemsCount(wyrm[2]) == 0 && st.getQuestItemsCount(wyrm[3]) >= 1)
                        return "30747-08.htm";
        }

        if (npcId >= Exarion && npcId <= Shamhai) {
            if (cond == 5 && st.haveAnyQuestItems(Juice_of_Monkshood))
                return npcId + "-01.htm";
            if (cond == 6 && st.haveQuestItem(getWyrmScale(npcId))) {
                int egg_id = getWyrmEgg(npcId);
                if (st.getQuestItemsCount(egg_id) < 20)
                    return npcId + "-03.htm";
                st.takeItems(getWyrmScale(npcId));
                st.takeItems(egg_id);
                st.giveItems(egg_id);
                st.setCond(7);
                return npcId + "-04.htm";
            }
            if (cond == 7 && st.getQuestItemsCount(getWyrmEgg(npcId)) == 1)
                return npcId + "-05.htm";
        }

        return "noquest";
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getState() != STARTED)
            return;
        int npcId = npc.getNpcId();
        int cond = st.getCond();

        if (cond == 2 && npcId == Toad_Lord) {
            int needed_skins = getNeededSkins(st);
            if (st.getQuestItemsCount(Toad_Lord_Back_Skin) < needed_skins && Rnd.chance(Toad_Lord_Back_Skin_Chance)) {
                st.giveItems(Toad_Lord_Back_Skin, 1);
                st.playSound(st.getQuestItemsCount(Toad_Lord_Back_Skin) < needed_skins ? SOUND_ITEMGET : SOUND_MIDDLE);
            }
            return;
        }

        if (npcId >= Enchanted_Valey_First && npcId <= Enchanted_Valey_Last && st.haveQuestItem(Deluxe_Fairy_Stone)) {
            st.takeItems(Deluxe_Fairy_Stone, 1);
            st.set("broken");
            st.setCond(1);
            return;
        }

        if (cond == 6) {
            int wyrm_id = isWyrmStoler(npcId);
            if (wyrm_id > 0 && st.getQuestItemsCount(getWyrmScale(wyrm_id)) > 0 && st.getQuestItemsCount(getWyrmEgg(wyrm_id)) < 20 && Rnd.chance(Egg_Chance)) {
                st.giveItems(getWyrmEgg(wyrm_id));
                st.playSound(st.getQuestItemsCount(getWyrmEgg(wyrm_id)) < 20 ? SOUND_ITEMGET : SOUND_MIDDLE);
            }
        }
    }
}