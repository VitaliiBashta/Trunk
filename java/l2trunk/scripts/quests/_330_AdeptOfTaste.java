package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _330_AdeptOfTaste extends Quest {
    // NPCs
    private static final int Sonia = 30062;
    private static final int Glyvka = 30067;
    private static final int Rollant = 30069;
    private static final int Jacob = 30073;
    private static final int Pano = 30078;
    private static final int Mirien = 30461;
    private static final int Jonas = 30469;
    // Mobs
    private static final int Hobgoblin = 20147;
    private static final int Mandragora_Sprout = 20154;
    private static final int Mandragora_Sapling = 20155;
    private static final int Mandragora_Blossom = 20156;
    private static final int Bloody_Bee = 20204;
    private static final int Mandragora_Sprout2 = 20223;
    private static final int Gray_Ant = 20226;
    private static final int Giant_Crimson_Ant = 20228;
    private static final int Stinger_Wasp = 20229;
    private static final int Monster_Eye_Searcher = 20265;
    private static final int Monster_Eye_Gazer = 20266;
    // items
    private static final int Ingredient_List = 1420;
    private static final int Sonias_Botany_Book = 1421;
    private static final int Red_Mandragora_Root = 1422;
    private static final int White_Mandragora_Root = 1423;
    private static final int Red_Mandragora_Sap = 1424;
    private static final int White_Mandragora_Sap = 1425;
    private static final int Jacobs_Insect_Book = 1426;
    private static final int Nectar = 1427;
    private static final int Royal_Jelly = 1428;
    private static final int Honey = 1429;
    private static final int Golden_Honey = 1430;
    private static final int Panos_Contract = 1431;
    private static final int Hobgoblin_Amulet = 1432;
    private static final int Dionian_Potato = 1433;
    private static final int Glyvkas_Botany_Book = 1434;
    private static final int Green_Marsh_Moss = 1435;
    private static final int Brown_Marsh_Moss = 1436;
    private static final int Green_Moss_Bundle = 1437;
    private static final int Brown_Moss_Bundle = 1438;
    private static final int Rollants_Creature_Book = 1439;
    private static final int Body_of_Monster_Eye = 1440;
    private static final int Meat_of_Monster_Eye = 1441;
    private static final List<Integer> Jonass_Steak_Dishes = List.of(
            1442, 1443, 1444, 1445, 1446);
    private static final List<Integer> Miriens_Reviews = List.of(
            1447, 1448, 1449, 1450, 1451);

    private static final List<Integer> ingredients = List.of(
            Red_Mandragora_Sap,
            Honey,
            Dionian_Potato,
            Green_Moss_Bundle,
            Meat_of_Monster_Eye);
    private static final List<Integer> spec_ingredients = List.of(
            White_Mandragora_Sap, Golden_Honey, Brown_Moss_Bundle);
    private static final List<Integer> rewards = List.of(
            0, 0, 1455, 1456, 1457);
    private static final int[] adena_rewards = {
            10000,
            14870,
            6490,
            12220,
            16540
    };

    public _330_AdeptOfTaste() {
        super(false);

        addStartNpc(Jonas);
        addTalkId(Sonia,Glyvka,Rollant,Jacob,Pano,Mirien);

        addKillId(Hobgoblin,Mandragora_Sprout,Mandragora_Sapling,Mandragora_Blossom,Bloody_Bee,
                Mandragora_Sprout2,Gray_Ant,Giant_Crimson_Ant,Stinger_Wasp,Monster_Eye_Searcher,Monster_Eye_Gazer);

        addQuestItem(Ingredient_List,Sonias_Botany_Book,Red_Mandragora_Root,White_Mandragora_Root,Jacobs_Insect_Book,
               Nectar,Royal_Jelly,Panos_Contract,Hobgoblin_Amulet,Glyvkas_Botany_Book,Green_Marsh_Moss,Brown_Marsh_Moss,
               Rollants_Creature_Book,Body_of_Monster_Eye);

        addQuestItem(ingredients);
        addQuestItem(spec_ingredients);
        addQuestItem(Jonass_Steak_Dishes);
        addQuestItem(Miriens_Reviews);
    }

    private static void MandragoraDrop(QuestState st, int i1, int i2) {
        int i = Rnd.get(100);
        if (i < i1)
            st.rollAndGive(Red_Mandragora_Root, 1, 1, 40, 100);
        else if (i < i2)
            st.rollAndGive(White_Mandragora_Root, 1, 1, 40, 100);
    }

    private static void BeeDrop(QuestState st, int i1, int i2) {
        int i = Rnd.get(100);
        if (i < i1)
            st.rollAndGive(Nectar, 1, 1, 20, 100);
        else if (i < i2)
            st.rollAndGive(Royal_Jelly, 1, 1, 10, 100);
    }

    private static void AntDrop(QuestState st, int i1, int i2) {
        int i = Rnd.get(100);
        if (i < i1)
            st.rollAndGive(Green_Marsh_Moss, 1, 1, 20, 100);
        else if (i < i2)
            st.rollAndGive(Brown_Marsh_Moss, 1, 1, 20, 100);
    }

    private static void root2Sap(QuestState st, int sap_id) {
        st.takeAllItems(Sonias_Botany_Book,White_Mandragora_Root,Red_Mandragora_Root);
        st.playSound(SOUND_MIDDLE);
        st.giveItems(sap_id);
    }

    private static void Moss2Bundle(QuestState st, int bundle_id) {
        st.takeAllItems(Glyvkas_Botany_Book,Brown_Marsh_Moss,Green_Marsh_Moss);
        st.playSound(SOUND_MIDDLE);
        st.giveItems(bundle_id);
    }

    private static void Nectar2Honey(QuestState st, int honey_id) {
        st.takeAllItems(Jacobs_Insect_Book,Nectar,Royal_Jelly);
        st.playSound(SOUND_MIDDLE);
        st.giveItems(honey_id);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int state = st.getState();
        if ("30469_03.htm".equalsIgnoreCase(event) && state == CREATED) {
            st.giveItemIfNotHave(Ingredient_List);
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("30062_05.htm".equalsIgnoreCase(event) && state == STARTED) {
            if (st.getQuestItemsCount(White_Mandragora_Root) + st.getQuestItemsCount(Red_Mandragora_Root) < 40)
                return null;
            root2Sap(st, Red_Mandragora_Sap);
        } else if ("30067_05.htm".equalsIgnoreCase(event) && state == STARTED) {
            if (st.getQuestItemsCount(Brown_Marsh_Moss) + st.getQuestItemsCount(Green_Marsh_Moss) < 20)
                return null;
            Moss2Bundle(st, Green_Moss_Bundle);
        } else if ("30073_05.htm".equalsIgnoreCase(event) && state == STARTED) {
            if (st.getQuestItemsCount(Nectar) < 20)
                return null;
            Nectar2Honey(st, Honey);
        }

        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int _state = st.getState();
        int npcId = npc.getNpcId();

        if (_state == CREATED) {
            if (npcId != Jonas)
                return "noquest";
            if (st.player.getLevel() < 24) {
                st.exitCurrentQuest();
                return "30469_01.htm";
            }
            st.setCond(0);
            return "30469_02.htm";
        }

        if (_state != STARTED)
            return "noquest";

        long ingredients_count = st.getQuestItemsCount(ingredients);
        long spec_ingredients_count = st.getQuestItemsCount(spec_ingredients);
        long all_ingredients_count = ingredients_count + spec_ingredients_count;
        boolean hasIngredientList = st.haveQuestItem(Ingredient_List) ;

        if (npcId == Jonas) {
            if (hasIngredientList) {
                if (all_ingredients_count < 5)
                    return "30469_04.htm";

                st.takeItems(Ingredient_List);
                st.takeItems(ingredients);
                st.takeItems(spec_ingredients);
                if (spec_ingredients_count > 3)
                    spec_ingredients_count = 3;

                spec_ingredients_count += Rnd.get(0, 1);
                st.playSound(spec_ingredients_count == 4 ? SOUND_JACKPOT : SOUND_MIDDLE);
                st.giveItems(Jonass_Steak_Dishes.get((int) spec_ingredients_count));
                spec_ingredients_count++;
                return "30469_05t" + spec_ingredients_count + ".htm";
            }

            if (all_ingredients_count == 0) {
                long Jonass_Steak_Dish_count = st.getQuestItemsCount(Jonass_Steak_Dishes);
                long Miriens_Review_count = st.getQuestItemsCount(Miriens_Reviews);
                if (Jonass_Steak_Dish_count > 0 && Miriens_Review_count == 0)
                    return "30469_06.htm";
                if (Jonass_Steak_Dish_count == 0 && Miriens_Review_count > 0)
                    for (int i = Miriens_Reviews.size(); i > 0; i--)
                        if (st.getQuestItemsCount(Miriens_Reviews.get(i - 1)) > 0) {
                            st.takeItems(Miriens_Reviews);
                            st.giveAdena(adena_rewards[i - 1]);
                            if (rewards.get(i - 1) > 0)
                                st.giveItems(rewards.get(i - 1));
                            st.playSound(SOUND_FINISH);
                            st.exitCurrentQuest();
                            return "30469_06t" + i + ".htm";
                        }
            }
        }

        if (npcId == Mirien) {
            if (hasIngredientList)
                return "30461_01.htm";
            if (all_ingredients_count == 0) {
                if (st.getQuestItemsCount(Miriens_Reviews) > 0)
                    return "30461_04.htm";
                for (int i = Jonass_Steak_Dishes.size(); i > 0; i--)
                    if (st.getQuestItemsCount(Jonass_Steak_Dishes.get(i - 1)) > 0) {
                        st.takeItems(Jonass_Steak_Dishes);
                        st.playSound(SOUND_MIDDLE);
                        st.giveItems(Miriens_Reviews.get(i - 1));
                        return "30461_02t" + i + ".htm";
                    }
            }
        }

        if (!(hasIngredientList && all_ingredients_count < 5))
            return "noquest";

        if (npcId == Sonia) {
            boolean has_sap = st.getQuestItemsCount(Red_Mandragora_Sap) > 0 || st.getQuestItemsCount(White_Mandragora_Sap) > 0;
            if (st.getQuestItemsCount(Sonias_Botany_Book) > 0) {
                if (!has_sap) {
                    long rootCount = st.getQuestItemsCount(White_Mandragora_Root);
                    if (rootCount >= 40) {
                        root2Sap(st, White_Mandragora_Sap);
                        return "30062_06.htm";
                    }
                    rootCount += st.getQuestItemsCount(Red_Mandragora_Root);
                    return rootCount < 40 ? "30062_02.htm" : "30062_03.htm";
                }
            } else if (has_sap)
                return "30062_07.htm";
            else {
                st.giveItems(Sonias_Botany_Book);
                return "30062_01.htm";
            }
        }

        if (npcId == Glyvka) {
            boolean has_bundle = st.getQuestItemsCount(Green_Moss_Bundle) > 0 || st.getQuestItemsCount(Brown_Moss_Bundle) > 0;
            if (st.getQuestItemsCount(Glyvkas_Botany_Book) > 0) {
                if (!has_bundle) {
                    long moss_count = st.getQuestItemsCount(Brown_Marsh_Moss);
                    if (moss_count >= 20) {
                        Moss2Bundle(st, Brown_Moss_Bundle);
                        return "30067_06.htm";
                    }
                    moss_count += st.getQuestItemsCount(Green_Marsh_Moss);
                    return moss_count < 20 ? "30067_02.htm" : "30067_03.htm";
                }
            } else if (has_bundle)
                return "30067_07.htm";
            {
                st.giveItems(Glyvkas_Botany_Book, 1);
                return "30067_01.htm";
            }
        }

        if (npcId == Rollant) {
            boolean has_meat = st.getQuestItemsCount(Meat_of_Monster_Eye) > 0;
            if (st.getQuestItemsCount(Rollants_Creature_Book) > 0) {
                if (!has_meat) {
                    if (st.getQuestItemsCount(Body_of_Monster_Eye) < 30)
                        return "30069_02.htm";
                    st.takeAllItems(Rollants_Creature_Book,Body_of_Monster_Eye);
                    st.playSound(SOUND_MIDDLE);
                    st.giveItems(Meat_of_Monster_Eye);
                    return "30069_03.htm";
                }
            } else if (has_meat)
                return "30069_04.htm";
            else {
                st.giveItems(Rollants_Creature_Book);
                return "30069_01.htm";
            }
        }

        if (npcId == Jacob) {
            boolean has_honey = st.haveAnyQuestItems(Honey,Golden_Honey) ;
            if (st.haveQuestItem(Jacobs_Insect_Book)) {
                if (!has_honey) {
                    if (st.getQuestItemsCount(Nectar) < 20)
                        return "30073_02.htm";
                    if (st.getQuestItemsCount(Royal_Jelly) < 10)
                        return "30073_03.htm";
                    Nectar2Honey(st, Golden_Honey);
                    return "30073_06.htm";
                }
            } else if (has_honey)
                return "30073_07.htm";
            else {
                st.giveItems(Jacobs_Insect_Book);
                return "30073_01.htm";
            }
        }

        if (npcId == Pano) {
            boolean has_potato = st.haveQuestItem(Dionian_Potato);
            if (st.haveQuestItem(Panos_Contract) ) {
                if (!has_potato) {
                    if (st.getQuestItemsCount(Hobgoblin_Amulet) < 30)
                        return "30078_02.htm";
                    st.takeAllItems(Panos_Contract,Hobgoblin_Amulet);
                    st.playSound(SOUND_MIDDLE);
                    st.giveItems(Dionian_Potato);
                    return "30078_03.htm";
                }
            } else if (has_potato)
                return "30078_04.htm";
            else {
                st.giveItems(Panos_Contract);
                return "30078_01.htm";
            }
        }

        return "noquest";
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getState() != STARTED)
            return;
        int npcId = npc.getNpcId();
        long ingredients_count = st.getQuestItemsCount(ingredients);
        long spec_ingredients_count = st.getQuestItemsCount(spec_ingredients);
        long all_ingredients_count = ingredients_count + spec_ingredients_count;
        boolean Has_Ingredient_List = st.getQuestItemsCount(Ingredient_List) > 0;
        if (!(Has_Ingredient_List && all_ingredients_count < 5))
            return;

        if (npcId == Hobgoblin && st.getQuestItemsCount(Panos_Contract) > 0)
            st.rollAndGive(Hobgoblin_Amulet, 1, 1, 30, 100);
        else if (npcId == Mandragora_Sprout && st.haveQuestItem(Sonias_Botany_Book) )
            MandragoraDrop(st, 70, 77);
        else if (npcId == Mandragora_Sapling && st.getQuestItemsCount(Sonias_Botany_Book) > 0)
            MandragoraDrop(st, 77, 85);
        else if (npcId == Mandragora_Blossom && st.getQuestItemsCount(Sonias_Botany_Book) > 0)
            MandragoraDrop(st, 87, 96);
        else if (npcId == Mandragora_Sprout2 && st.getQuestItemsCount(Sonias_Botany_Book) > 0)
            MandragoraDrop(st, 70, 77);
        else if (npcId == Bloody_Bee && st.getQuestItemsCount(Jacobs_Insect_Book) > 0)
            BeeDrop(st, 80, 95);
        else if (npcId == Stinger_Wasp && st.getQuestItemsCount(Jacobs_Insect_Book) > 0)
            BeeDrop(st, 92, 100);
        else if (npcId == Gray_Ant && st.getQuestItemsCount(Glyvkas_Botany_Book) > 0)
            AntDrop(st, 87, 96);
        else if (npcId == Giant_Crimson_Ant && st.getQuestItemsCount(Glyvkas_Botany_Book) > 0)
            AntDrop(st, 90, 100);
        else if (npcId == Monster_Eye_Searcher && st.getQuestItemsCount(Rollants_Creature_Book) > 0)
            st.rollAndGive(Body_of_Monster_Eye, 1, 3, 30, 97);
        else if (npcId == Monster_Eye_Gazer && st.getQuestItemsCount(Rollants_Creature_Book) > 0)
            st.rollAndGive(Body_of_Monster_Eye, 1, 2, 30, 100);

    }
}