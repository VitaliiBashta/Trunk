package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;

public final class _104_SpiritOfMirror extends Quest {
    private static final int GALLINS_OAK_WAND = 748;
    private static final int WAND_SPIRITBOUND1 = 1135;
    private static final int WAND_SPIRITBOUND2 = 1136;
    private static final int WAND_SPIRITBOUND3 = 1137;
    private static final int WAND_OF_ADEPT = 747;

    private static final SystemMessage2 CACHE_SYSMSG_GALLINS_OAK_WAND = SystemMessage2.removeItems(GALLINS_OAK_WAND, 1);

    public _104_SpiritOfMirror() {
        super(PARTY_NONE);
        addStartNpc(30017);
        addTalkId(30041, 30043, 30045);
        addKillId(27003, 27004, 27005);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("gallin_q0104_03.htm")) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
            st.giveItems(GALLINS_OAK_WAND, 3);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == 30017) {
            if (cond == 0)
                if (st.player.getRace() != Race.human) {
                    htmltext = "gallin_q0104_00.htm";
                    st.exitCurrentQuest();
                } else if (st.player.getLevel() >= 10) {
                    htmltext = "gallin_q0104_02.htm";
                    return htmltext;
                } else {
                    htmltext = "gallin_q0104_06.htm";
                    st.exitCurrentQuest();
                }
            else if (cond == 1 && st.haveQuestItem(GALLINS_OAK_WAND)  && (st.getQuestItemsCount(WAND_SPIRITBOUND1) == 0 || st.getQuestItemsCount(WAND_SPIRITBOUND2) == 0 || st.getQuestItemsCount(WAND_SPIRITBOUND3) == 0))
                htmltext = "gallin_q0104_04.htm";
            else if (cond == 3 && st.getQuestItemsCount(WAND_SPIRITBOUND1) >= 1 && st.getQuestItemsCount(WAND_SPIRITBOUND2) >= 1 && st.getQuestItemsCount(WAND_SPIRITBOUND3) >= 1) {
                st.takeAllItems(WAND_SPIRITBOUND1, WAND_SPIRITBOUND2, WAND_SPIRITBOUND3);

                st.giveItems(WAND_OF_ADEPT);
                st.giveItems(ADENA_ID, 16866, false);
                st.player.addExpAndSp(39750, 3407);

                if (st.player.getClassId().occupation() == 0 && !st.player.isVarSet("p1q3")) {
                    st.player.setVar("p1q3"); // flag for helper
                    st.player.sendPacket(new ExShowScreenMessage("Now go find the Newbie Guide."));
                    st.giveItems(1060, 100); // healing potion
                    for (int item = 4412; item <= 4417; item++)
                        st.giveItems(item, 10); // echo cry
                    if (st.player.getClassId().isMage()) {
                        st.playTutorialVoice("tutorial_voice_027");
                        st.giveItems(5790, 3000); // newbie sps
                    } else {
                        st.playTutorialVoice("tutorial_voice_026");
                        st.giveItems(5789, 6000); // newbie ss
                    }
                }

                htmltext = "gallin_q0104_05.htm";
                st.finish();
                st.playSound(SOUND_FINISH);
            }
        } else if ((npcId == 30041 || npcId == 30043 || npcId == 30045) && cond == 1) {
            if (npcId == 30041 && !st.isSet("id1") )
                st.set("id1");
            htmltext = "arnold_q0104_01.htm";
            if (npcId == 30043 && !st.isSet("id2"))
                st.set("id2");
            htmltext = "johnson_q0104_01.htm";
            if (npcId == 30045 && !st.isSet("id3") )
                st.set("id3");
            htmltext = "ken_q0104_01.htm";
            if (st.isSet("id1") && st.isSet("id2") && st.isSet("id3")) {
                st.setCond(2);
                st.unset("id1");
                st.unset("id2");
                st.unset("id3");
            }
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int cond = st.getCond();
        int npcId = npc.getNpcId();

        if ((cond == 1 || cond == 2) && st.player.getActiveWeaponInstance() != null && st.player.getActiveWeaponInstance().getItemId() == GALLINS_OAK_WAND) {
            ItemInstance weapon = st.player.getActiveWeaponInstance();
            boolean haveAllitems = st.haveAllQuestItems(WAND_SPIRITBOUND1,WAND_SPIRITBOUND2,WAND_SPIRITBOUND3);
            if (npcId == 27003 && st.getQuestItemsCount(WAND_SPIRITBOUND1) == 0) {
                if (st.player.getInventory().destroyItem(weapon, 1L, "_104_SpiritOfMirror")) {
                    st.giveItems(WAND_SPIRITBOUND1);
                    st.player.sendPacket(CACHE_SYSMSG_GALLINS_OAK_WAND);
                    if (haveAllitems) {
                        st.setCond(3);
                        st.playSound(SOUND_MIDDLE);
                    } else
                        st.playSound(SOUND_ITEMGET);
                }
            } else if (npcId == 27004 && st.getQuestItemsCount(WAND_SPIRITBOUND2) == 0) {
                if (st.player.getInventory().destroyItem(weapon, 1L, "_104_SpiritOfMirror")) {
                    st.giveItems(WAND_SPIRITBOUND2);
                    st.player.sendPacket(CACHE_SYSMSG_GALLINS_OAK_WAND);
                    if (haveAllitems ) {
                        st.setCond(3);
                        st.playSound(SOUND_MIDDLE);
                    } else
                        st.playSound(SOUND_ITEMGET);
                }
            } else if (npcId == 27005 && st.getQuestItemsCount(WAND_SPIRITBOUND3) == 0)
                if (st.player.getInventory().destroyItem(weapon, 1L, "_104_SpiritOfMirror")) {
                    st.giveItems(WAND_SPIRITBOUND3);
                    st.player.sendPacket(CACHE_SYSMSG_GALLINS_OAK_WAND);
                    if (haveAllitems ) {
                        st.setCond(3);
                        st.playSound(SOUND_MIDDLE);
                    } else
                        st.playSound(SOUND_ITEMGET);
                }
        }
    }
}