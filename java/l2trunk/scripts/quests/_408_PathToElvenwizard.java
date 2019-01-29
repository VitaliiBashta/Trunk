package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _408_PathToElvenwizard extends Quest {
    //npc
    private final int GREENIS = 30157;
    private final int THALIA = 30371;
    private final int ROSELLA = 30414;
    private final int NORTHWIND = 30423;
    //mobs
    private final int DRYAD_ELDER = 20019;
    private final int PINCER_SPIDER = 20466;
    private final int SUKAR_WERERAT_LEADER = 20047;
    //items
    private final int ROGELLIAS_LETTER_ID = 1218;
    private final int RED_DOWN_ID = 1219;
    private final int MAGICAL_POWERS_RUBY_ID = 1220;
    private final int PURE_AQUAMARINE_ID = 1221;
    private final int APPETIZING_APPLE_ID = 1222;
    private final int GOLD_LEAVES_ID = 1223;
    private final int IMMORTAL_LOVE_ID = 1224;
    private final int AMETHYST_ID = 1225;
    private final int NOBILITY_AMETHYST_ID = 1226;
    private final int FERTILITY_PERIDOT_ID = 1229;
    private final int ETERNITY_DIAMOND_ID = 1230;
    private final int CHARM_OF_GRAIN_ID = 1272;
    private final int SAP_OF_WORLD_TREE_ID = 1273;
    private final int LUCKY_POTPOURI_ID = 1274;

    public _408_PathToElvenwizard() {
        super(false);

        addStartNpc(ROSELLA);

        addTalkId(GREENIS);
        addTalkId(THALIA);
        addTalkId(NORTHWIND);

        addKillId(DRYAD_ELDER);
        addKillId(PINCER_SPIDER);
        addKillId(SUKAR_WERERAT_LEADER);

        addQuestItem(ROGELLIAS_LETTER_ID,
                FERTILITY_PERIDOT_ID,
                IMMORTAL_LOVE_ID,
                APPETIZING_APPLE_ID,
                CHARM_OF_GRAIN_ID,
                MAGICAL_POWERS_RUBY_ID,
                SAP_OF_WORLD_TREE_ID,
                PURE_AQUAMARINE_ID,
                LUCKY_POTPOURI_ID,
                NOBILITY_AMETHYST_ID,
                GOLD_LEAVES_ID,
                RED_DOWN_ID,
                AMETHYST_ID);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("1")) {
            if (st.getPlayer().getClassId().id != 0x19) {
                if (st.getPlayer().getClassId().id == 0x1a)
                    htmltext = "rogellia_q0408_02a.htm";
                else
                    htmltext = "rogellia_q0408_03.htm";
            } else if (st.getPlayer().getLevel() < 18)
                htmltext = "rogellia_q0408_04.htm";
            else if (st.getQuestItemsCount(ETERNITY_DIAMOND_ID) > 0)
                htmltext = "rogellia_q0408_05.htm";
            else {
                st.setState(STARTED);
                st.setCond(1);
                st.playSound(SOUND_ACCEPT);
                st.giveItems(FERTILITY_PERIDOT_ID);
                htmltext = "rogellia_q0408_06.htm";
            }
        } else if (event.equalsIgnoreCase("408_1")) {
            if (st.getQuestItemsCount(MAGICAL_POWERS_RUBY_ID) > 0)
                htmltext = "rogellia_q0408_10.htm";
            else if (st.getQuestItemsCount(MAGICAL_POWERS_RUBY_ID) < 1 && st.getQuestItemsCount(FERTILITY_PERIDOT_ID) > 0) {
                st.giveItems(ROGELLIAS_LETTER_ID, 1);
                htmltext = "rogellia_q0408_07.htm";
            }
        } else if (event.equalsIgnoreCase("408_4")) {
            if (st.getQuestItemsCount(ROGELLIAS_LETTER_ID) > 0) {
                st.takeItems(ROGELLIAS_LETTER_ID);
                st.giveItems(CHARM_OF_GRAIN_ID);
                htmltext = "grain_q0408_02.htm";
            }
        } else if (event.equalsIgnoreCase("408_2")) {
            if (st.getQuestItemsCount(PURE_AQUAMARINE_ID) > 0)
                htmltext = "rogellia_q0408_13.htm";
            else if (st.getQuestItemsCount(PURE_AQUAMARINE_ID) < 1 && st.getQuestItemsCount(FERTILITY_PERIDOT_ID) > 0) {
                st.giveItems(APPETIZING_APPLE_ID);
                htmltext = "rogellia_q0408_14.htm";
            }
        } else if (event.equalsIgnoreCase("408_5")) {
            if (st.getQuestItemsCount(APPETIZING_APPLE_ID) > 0) {
                st.takeItems(APPETIZING_APPLE_ID);
                st.giveItems(SAP_OF_WORLD_TREE_ID);
                htmltext = "thalya_q0408_02.htm";
            }
        } else if (event.equalsIgnoreCase("408_3"))
            if (st.getQuestItemsCount(NOBILITY_AMETHYST_ID) > 0)
                htmltext = "rogellia_q0408_17.htm";
            else if (st.getQuestItemsCount(NOBILITY_AMETHYST_ID) < 1 && st.getQuestItemsCount(FERTILITY_PERIDOT_ID) > 0) {
                st.giveItems(IMMORTAL_LOVE_ID, 1);
                htmltext = "rogellia_q0408_18.htm";
            }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == ROSELLA) {
            if (cond < 1)
                htmltext = "rogellia_q0408_01.htm";
            else if (st.getQuestItemsCount(CHARM_OF_GRAIN_ID) > 0) {
                if (st.getQuestItemsCount(RED_DOWN_ID) < 5)
                    htmltext = "rogellia_q0408_09.htm";
                else if (st.getQuestItemsCount(RED_DOWN_ID) > 4)
                    htmltext = "rogellia_q0408_25.htm";
                else if (st.getQuestItemsCount(GOLD_LEAVES_ID) > 4)
                    htmltext = "rogellia_q0408_26.htm";
            } else if (st.getQuestItemsCount(APPETIZING_APPLE_ID) > 0)
                htmltext = "rogellia_q0408_15.htm";
            else if (st.getQuestItemsCount(IMMORTAL_LOVE_ID) > 0)
                htmltext = "rogellia_q0408_19.htm";
            else if (st.getQuestItemsCount(SAP_OF_WORLD_TREE_ID) > 0 && st.getQuestItemsCount(GOLD_LEAVES_ID) < 5)
                htmltext = "rogellia_q0408_16.htm";
            else if (st.getQuestItemsCount(LUCKY_POTPOURI_ID) > 0) {
                if (st.getQuestItemsCount(AMETHYST_ID) < 2)
                    htmltext = "rogellia_q0408_20.htm";
                else
                    htmltext = "rogellia_q0408_27.htm";
            } else if (st.getQuestItemsCount(ROGELLIAS_LETTER_ID) > 0)
                htmltext = "rogellia_q0408_08.htm";
            else if (st.getQuestItemsCount(ROGELLIAS_LETTER_ID) < 1 && st.getQuestItemsCount(APPETIZING_APPLE_ID) < 1 && st.getQuestItemsCount(IMMORTAL_LOVE_ID) < 1 && st.getQuestItemsCount(CHARM_OF_GRAIN_ID) < 1 && st.getQuestItemsCount(SAP_OF_WORLD_TREE_ID) < 1 && st.getQuestItemsCount(LUCKY_POTPOURI_ID) < 1 && st.getQuestItemsCount(FERTILITY_PERIDOT_ID) > 0)
                if (st.getQuestItemsCount(MAGICAL_POWERS_RUBY_ID) < 1 | st.getQuestItemsCount(NOBILITY_AMETHYST_ID) < 1 | st.getQuestItemsCount(PURE_AQUAMARINE_ID) < 1)
                    htmltext = "rogellia_q0408_11.htm";
                else if (st.getQuestItemsCount(MAGICAL_POWERS_RUBY_ID) > 0 && st.getQuestItemsCount(NOBILITY_AMETHYST_ID) > 0 && st.getQuestItemsCount(PURE_AQUAMARINE_ID) > 0) {
                    st.takeItems(MAGICAL_POWERS_RUBY_ID);
                    st.takeItems(PURE_AQUAMARINE_ID, st.getQuestItemsCount(PURE_AQUAMARINE_ID));
                    st.takeItems(NOBILITY_AMETHYST_ID, st.getQuestItemsCount(NOBILITY_AMETHYST_ID));
                    st.takeItems(FERTILITY_PERIDOT_ID, st.getQuestItemsCount(FERTILITY_PERIDOT_ID));
                    htmltext = "rogellia_q0408_24.htm";
                    if (st.getPlayer().getClassId().getLevel() == 1) {
                        st.giveItems(ETERNITY_DIAMOND_ID, 1);
                        if (!st.getPlayer().getVarB("prof1")) {
                            st.getPlayer().setVar("prof1", "1", -1);
                            st.addExpAndSp(295862, 17964);
                            st.giveItems(ADENA_ID, 81900);
                        }
                    }
                    st.playSound(SOUND_FINISH);
                    st.exitCurrentQuest(true);
                }
        } else if (npcId == GREENIS && cond > 0) {
            if (st.getQuestItemsCount(ROGELLIAS_LETTER_ID) > 0)
                htmltext = "grain_q0408_01.htm";
            else if (st.getQuestItemsCount(CHARM_OF_GRAIN_ID) > 0)
                if (st.getQuestItemsCount(RED_DOWN_ID) < 5)
                    htmltext = "grain_q0408_03.htm";
                else {
                    st.takeItems(RED_DOWN_ID, -1);
                    st.takeItems(CHARM_OF_GRAIN_ID, -1);
                    st.giveItems(MAGICAL_POWERS_RUBY_ID, 1);
                    htmltext = "grain_q0408_04.htm";
                }
        } else if (npcId == THALIA && cond > 0) {
            if (st.getQuestItemsCount(APPETIZING_APPLE_ID) > 0)
                htmltext = "thalya_q0408_01.htm";
            else if (st.getQuestItemsCount(SAP_OF_WORLD_TREE_ID) > 0)
                if (st.getQuestItemsCount(GOLD_LEAVES_ID) < 5)
                    htmltext = "thalya_q0408_03.htm";
                else {
                    st.takeItems(GOLD_LEAVES_ID);
                    st.takeItems(SAP_OF_WORLD_TREE_ID);
                    st.giveItems(PURE_AQUAMARINE_ID);
                    htmltext = "thalya_q0408_04.htm";
                }
        } else if (npcId == NORTHWIND && cond > 0)
            if (st.getQuestItemsCount(IMMORTAL_LOVE_ID) > 0) {
                st.takeItems(IMMORTAL_LOVE_ID);
                st.giveItems(LUCKY_POTPOURI_ID);
                htmltext = "northwindel_q0408_01.htm";
            } else if (st.getQuestItemsCount(LUCKY_POTPOURI_ID) > 0)
                if (st.getQuestItemsCount(AMETHYST_ID) < 2)
                    htmltext = "northwindel_q0408_02.htm";
                else {
                    st.takeItems(AMETHYST_ID);
                    st.takeItems(LUCKY_POTPOURI_ID);
                    st.giveItems(NOBILITY_AMETHYST_ID);
                    htmltext = "northwindel_q0408_03.htm";
                }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == PINCER_SPIDER) {
            if (cond > 0 && st.getQuestItemsCount(CHARM_OF_GRAIN_ID) > 0 && st.getQuestItemsCount(RED_DOWN_ID) < 5 && Rnd.chance(70)) {
                st.giveItems(RED_DOWN_ID, 1);
                if (st.getQuestItemsCount(RED_DOWN_ID) < 5)
                    st.playSound(SOUND_ITEMGET);
                else
                    st.playSound(SOUND_MIDDLE);
            }
        } else if (npcId == DRYAD_ELDER) {
            if (cond > 0 && st.getQuestItemsCount(SAP_OF_WORLD_TREE_ID) > 0 && st.getQuestItemsCount(GOLD_LEAVES_ID) < 5 && Rnd.chance(40)) {
                st.giveItems(GOLD_LEAVES_ID, 1);
                if (st.getQuestItemsCount(GOLD_LEAVES_ID) < 5)
                    st.playSound(SOUND_ITEMGET);
                else
                    st.playSound(SOUND_MIDDLE);
            }
        } else if (npcId == SUKAR_WERERAT_LEADER)
            if (cond > 0 && st.getQuestItemsCount(LUCKY_POTPOURI_ID) > 0 && st.getQuestItemsCount(AMETHYST_ID) < 2 && Rnd.chance(40)) {
                st.giveItems(AMETHYST_ID, 1);
                if (st.getQuestItemsCount(AMETHYST_ID) < 2)
                    st.playSound(SOUND_ITEMGET);
                else
                    st.playSound(SOUND_MIDDLE);
            }
        return null;
    }
}