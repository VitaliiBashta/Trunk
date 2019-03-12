package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import static l2trunk.gameserver.model.base.ClassId.elvenMage;
import static l2trunk.gameserver.model.base.ClassId.elvenWizard;

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

        addTalkId(GREENIS,THALIA,NORTHWIND);

        addKillId(DRYAD_ELDER,PINCER_SPIDER,SUKAR_WERERAT_LEADER);

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
        switch (event) {
            case "1":
                if (st.player.getClassId() != elvenMage) {
                    if (st.player.getClassId() == elvenWizard)
                        htmltext = "rogellia_q0408_02a.htm";
                    else
                        htmltext = "rogellia_q0408_03.htm";
                } else if (st.player.getLevel() < 18)
                    htmltext = "rogellia_q0408_04.htm";
                else if (st.haveQuestItem(ETERNITY_DIAMOND_ID) )
                    htmltext = "rogellia_q0408_05.htm";
                else {
                    st.start();
                    st.setCond(1);
                    st.playSound(SOUND_ACCEPT);
                    st.giveItems(FERTILITY_PERIDOT_ID);
                    htmltext = "rogellia_q0408_06.htm";
                }
                break;
            case "408_1":
                if (st.haveQuestItem(MAGICAL_POWERS_RUBY_ID) )
                    htmltext = "rogellia_q0408_10.htm";
                else if (!st.haveQuestItem(MAGICAL_POWERS_RUBY_ID)  && st.haveQuestItem(FERTILITY_PERIDOT_ID)) {
                    st.giveItems(ROGELLIAS_LETTER_ID);
                    htmltext = "rogellia_q0408_07.htm";
                }
                break;
            case "408_4":
                if (st.haveQuestItem(ROGELLIAS_LETTER_ID) ) {
                    st.takeItems(ROGELLIAS_LETTER_ID);
                    st.giveItems(CHARM_OF_GRAIN_ID);
                    htmltext = "grain_q0408_02.htm";
                }
                break;
            case "408_2":
                if (st.haveQuestItem(PURE_AQUAMARINE_ID) )
                    htmltext = "rogellia_q0408_13.htm";
                else if (!st.haveQuestItem(PURE_AQUAMARINE_ID) && st.haveQuestItem(FERTILITY_PERIDOT_ID) ) {
                    st.giveItems(APPETIZING_APPLE_ID);
                    htmltext = "rogellia_q0408_14.htm";
                }
                break;
            case "408_5":
                if (st.haveQuestItem(APPETIZING_APPLE_ID) ) {
                    st.takeItems(APPETIZING_APPLE_ID);
                    st.giveItems(SAP_OF_WORLD_TREE_ID);
                    htmltext = "thalya_q0408_02.htm";
                }
                break;
            case "408_3":
                if (st.haveQuestItem(NOBILITY_AMETHYST_ID) )
                    htmltext = "rogellia_q0408_17.htm";
                else if (!st.haveQuestItem(NOBILITY_AMETHYST_ID) && st.haveQuestItem(FERTILITY_PERIDOT_ID) ) {
                    st.giveItems(IMMORTAL_LOVE_ID);
                    htmltext = "rogellia_q0408_18.htm";
                }
                break;
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
            else if (st.haveQuestItem(CHARM_OF_GRAIN_ID) ) {
                if (!st.haveQuestItem(RED_DOWN_ID, 5))
                    htmltext = "rogellia_q0408_09.htm";
                else if (st.haveQuestItem(RED_DOWN_ID, 5))
                    htmltext = "rogellia_q0408_25.htm";
                else if (st.getQuestItemsCount(GOLD_LEAVES_ID) > 4)
                    htmltext = "rogellia_q0408_26.htm";
            } else if (st.getQuestItemsCount(APPETIZING_APPLE_ID) > 0)
                htmltext = "rogellia_q0408_15.htm";
            else if (st.haveQuestItem(IMMORTAL_LOVE_ID) )
                htmltext = "rogellia_q0408_19.htm";
            else if (st.haveQuestItem(SAP_OF_WORLD_TREE_ID)  && st.getQuestItemsCount(GOLD_LEAVES_ID) < 5)
                htmltext = "rogellia_q0408_16.htm";
            else if (st.haveQuestItem(LUCKY_POTPOURI_ID) ) {
                if (st.getQuestItemsCount(AMETHYST_ID) < 2)
                    htmltext = "rogellia_q0408_20.htm";
                else
                    htmltext = "rogellia_q0408_27.htm";
            } else if (st.getQuestItemsCount(ROGELLIAS_LETTER_ID) > 0)
                htmltext = "rogellia_q0408_08.htm";
            else if (st.getQuestItemsCount(ROGELLIAS_LETTER_ID) < 1 && st.getQuestItemsCount(APPETIZING_APPLE_ID) < 1 && st.getQuestItemsCount(IMMORTAL_LOVE_ID) < 1 && st.getQuestItemsCount(CHARM_OF_GRAIN_ID) < 1 && st.getQuestItemsCount(SAP_OF_WORLD_TREE_ID) < 1 && st.getQuestItemsCount(LUCKY_POTPOURI_ID) < 1 && st.getQuestItemsCount(FERTILITY_PERIDOT_ID) > 0)
                if (!(st.getQuestItemsCount(MAGICAL_POWERS_RUBY_ID) < 1 | st.getQuestItemsCount(NOBILITY_AMETHYST_ID) < 1 | st.getQuestItemsCount(PURE_AQUAMARINE_ID) < 1)) {
                    if (st.haveAllQuestItems(MAGICAL_POWERS_RUBY_ID,NOBILITY_AMETHYST_ID,PURE_AQUAMARINE_ID) ) {
                        st.takeAllItems(MAGICAL_POWERS_RUBY_ID,PURE_AQUAMARINE_ID,NOBILITY_AMETHYST_ID,FERTILITY_PERIDOT_ID);
                        htmltext = "rogellia_q0408_24.htm";
                        if (st.player.getClassId().occupation() == 0) {
                            st.giveItems(ETERNITY_DIAMOND_ID);
                            if (!st.player.isVarSet("prof1")) {
                                st.player.setVar("prof1");
                                st.addExpAndSp(295862, 17964);
                                st.giveAdena( 81900);
                            }
                        }
                        st.playSound(SOUND_FINISH);
                        st.exitCurrentQuest();
                    }
                } else {
                    htmltext = "rogellia_q0408_11.htm";
                }
        } else if (npcId == GREENIS && cond > 0) {
            if (st.haveQuestItem(ROGELLIAS_LETTER_ID) )
                htmltext = "grain_q0408_01.htm";
            else if (st.haveQuestItem(CHARM_OF_GRAIN_ID) )
                if (st.getQuestItemsCount(RED_DOWN_ID) < 5)
                    htmltext = "grain_q0408_03.htm";
                else {
                    st.takeAllItems(RED_DOWN_ID,CHARM_OF_GRAIN_ID);
                    st.giveItems(MAGICAL_POWERS_RUBY_ID);
                    htmltext = "grain_q0408_04.htm";
                }
        } else if (npcId == THALIA && cond > 0) {
            if (st.haveQuestItem(APPETIZING_APPLE_ID) )
                htmltext = "thalya_q0408_01.htm";
            else if (st.haveQuestItem(SAP_OF_WORLD_TREE_ID) )
                if (st.getQuestItemsCount(GOLD_LEAVES_ID) < 5)
                    htmltext = "thalya_q0408_03.htm";
                else {
                    st.takeAllItems(GOLD_LEAVES_ID,SAP_OF_WORLD_TREE_ID);
                    st.giveItems(PURE_AQUAMARINE_ID);
                    htmltext = "thalya_q0408_04.htm";
                }
        } else if (npcId == NORTHWIND && cond > 0)
            if (st.haveQuestItem(IMMORTAL_LOVE_ID)) {
                st.takeItems(IMMORTAL_LOVE_ID);
                st.giveItems(LUCKY_POTPOURI_ID);
                htmltext = "northwindel_q0408_01.htm";
            } else if (st.haveQuestItem(LUCKY_POTPOURI_ID))
                if (st.getQuestItemsCount(AMETHYST_ID) < 2)
                    htmltext = "northwindel_q0408_02.htm";
                else {
                    st.takeAllItems(AMETHYST_ID,LUCKY_POTPOURI_ID);
                    st.giveItems(NOBILITY_AMETHYST_ID);
                    htmltext = "northwindel_q0408_03.htm";
                }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == PINCER_SPIDER) {
            if (cond > 0 && st.haveQuestItem(CHARM_OF_GRAIN_ID) && st.getQuestItemsCount(RED_DOWN_ID) < 5 && Rnd.chance(70)) {
                st.giveItems(RED_DOWN_ID);
                if (st.getQuestItemsCount(RED_DOWN_ID) < 5)
                    st.playSound(SOUND_ITEMGET);
                else
                    st.playSound(SOUND_MIDDLE);
            }
        } else if (npcId == DRYAD_ELDER) {
            if (cond > 0 && st.haveQuestItem(SAP_OF_WORLD_TREE_ID) && st.getQuestItemsCount(GOLD_LEAVES_ID) < 5 && Rnd.chance(40)) {
                st.giveItems(GOLD_LEAVES_ID);
                if (st.getQuestItemsCount(GOLD_LEAVES_ID) < 5)
                    st.playSound(SOUND_ITEMGET);
                else
                    st.playSound(SOUND_MIDDLE);
            }
        } else if (npcId == SUKAR_WERERAT_LEADER)
            if (cond > 0 && st.haveQuestItem(LUCKY_POTPOURI_ID)  && st.getQuestItemsCount(AMETHYST_ID) < 2 && Rnd.chance(40)) {
                st.giveItems(AMETHYST_ID);
                if (st.getQuestItemsCount(AMETHYST_ID) < 2)
                    st.playSound(SOUND_ITEMGET);
                else
                    st.playSound(SOUND_MIDDLE);
            }
    }
}