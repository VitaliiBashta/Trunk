package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.utils.Location;

import static l2trunk.gameserver.model.base.ClassId.orcFighter;
import static l2trunk.gameserver.model.base.ClassId.orcRaider;

public final class _414_PathToOrcRaider extends Quest {
    private static final int MARK_OF_RAIDER = 1592;
    //npc
    private final int KARUKIA = 30570;
    private final int KASMAN = 30501;
    private final int TAZEER = 31978;
    //mobs
    private final int GOBLIN_TOMB_RAIDER_LEADER = 20320;
    private final int KURUKA_RATMAN_LEADER = 27045;
    private final int UMBAR_ORC = 27054;
    private final int TIMORA_ORC = 27320;
    //items
    private final int GREEN_BLOOD = 1578;
    private final int GOBLIN_DWELLING_MAP = 1579;
    private final int KURUKA_RATMAN_TOOTH = 1580;
    private final int BETRAYER_UMBAR_REPORT = 1589;
    private final int HEAD_OF_BETRAYER = 1591;
    private final int TIMORA_ORCS_HEAD = 8544;

    public _414_PathToOrcRaider() {
        addStartNpc(KARUKIA);

        addTalkId(KASMAN, TAZEER);

        addKillId(GOBLIN_TOMB_RAIDER_LEADER, KURUKA_RATMAN_LEADER, UMBAR_ORC, TIMORA_ORC);

        addQuestItem(KURUKA_RATMAN_TOOTH, GOBLIN_DWELLING_MAP, GREEN_BLOOD, HEAD_OF_BETRAYER, BETRAYER_UMBAR_REPORT, TIMORA_ORCS_HEAD);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("prefect_karukia_q0414_05.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.giveItems(GOBLIN_DWELLING_MAP);
            st.playSound(SOUND_ACCEPT);
        } else if ("to_Gludin".equalsIgnoreCase(event)) {
            htmltext = "prefect_karukia_q0414_07a.htm";
            st.takeAllItems(KURUKA_RATMAN_TOOTH,GOBLIN_DWELLING_MAP);
            st.playSound(SOUND_MIDDLE);
            st.giveItems(BETRAYER_UMBAR_REPORT);
            st.addRadar(Location.of(-74490, 83275, -3374));
            st.setCond(3);
        } else if ("to_Schuttgart".equalsIgnoreCase(event)) {
            htmltext = "prefect_karukia_q0414_07b.htm";
            st.takeAllItems(KURUKA_RATMAN_TOOTH,GOBLIN_DWELLING_MAP);
            st.addRadar(Location.of(90000, -143286, -1520));
            st.playSound(SOUND_MIDDLE);
            st.setCond(5);
        } else if ("prefect_tazar_q0414_02.htm".equalsIgnoreCase(event)) {
            st.addRadar(Location.of(57502, -117576, -3700));
            st.setCond(6);
            st.playSound(SOUND_MIDDLE);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        ClassId playerClassID = st.player.getClassId();
        int playerLvl = st.player.getLevel();
        if (npcId == KARUKIA) {
            if (cond < 1) {
                if (playerLvl >= 18 && playerClassID == orcFighter && !st.haveQuestItem(MARK_OF_RAIDER) && !st.haveQuestItem(GOBLIN_DWELLING_MAP))
                    htmltext = "prefect_karukia_q0414_01.htm";
                else if (playerClassID != orcFighter) {
                    if (playerClassID == orcRaider)
                        htmltext = "prefect_karukia_q0414_02a.htm";
                    else
                        htmltext = "prefect_karukia_q0414_03.htm";
                } else if (playerLvl < 18)
                    htmltext = "prefect_karukia_q0414_02.htm";
                else if (st.haveQuestItem(MARK_OF_RAIDER))
                    htmltext = "prefect_karukia_q0414_04.htm";
                else
                    htmltext = "prefect_karukia_q0414_02.htm";
            } else if (cond == 1 && st.getQuestItemsCount(GOBLIN_DWELLING_MAP) > 0 && st.getQuestItemsCount(KURUKA_RATMAN_TOOTH) < 10)
                htmltext = "prefect_karukia_q0414_06.htm";
            else if (cond == 2 && st.getQuestItemsCount(GOBLIN_DWELLING_MAP) > 0 && st.getQuestItemsCount(KURUKA_RATMAN_TOOTH) > 9)
                htmltext = "prefect_karukia_q0414_07.htm";
            else if (cond == 3 && st.getQuestItemsCount(BETRAYER_UMBAR_REPORT) > 0 && st.getQuestItemsCount(HEAD_OF_BETRAYER) < 2)
                htmltext = "prefect_karukia_q0414_08.htm";
            else if (cond == 4 && st.haveQuestItem(BETRAYER_UMBAR_REPORT) && st.haveQuestItem(HEAD_OF_BETRAYER, 2))
                htmltext = "prefect_karukia_q0414_09.htm";
        } else if (npcId == KASMAN && cond > 0) {
            if (cond == 3 && st.haveQuestItem(BETRAYER_UMBAR_REPORT) && st.getQuestItemsCount(HEAD_OF_BETRAYER) < 1)
                htmltext = "prefect_kasman_q0414_01.htm";
            else if (cond == 3 && st.haveQuestItem(HEAD_OF_BETRAYER) && st.getQuestItemsCount(HEAD_OF_BETRAYER) < 2)
                htmltext = "prefect_kasman_q0414_02.htm";
            else if (cond == 4 && st.haveQuestItem(HEAD_OF_BETRAYER)) {
                htmltext = "prefect_kasman_q0414_03.htm";
                st.exitCurrentQuest();
                if (st.player.getClassId().occupation() == 0) {
                    st.giveItems(MARK_OF_RAIDER);
                    st.addExpAndSp(228064, 16455);
                    st.giveAdena(81900);
                }
                st.playSound(SOUND_FINISH);
            }
        } else if (npcId == TAZEER)
            if (cond == 5)
                htmltext = "prefect_tazar_q0414_01b.htm";
            else if (cond == 6 && st.getQuestItemsCount(TIMORA_ORCS_HEAD) < 1)
                htmltext = "prefect_tazar_q0414_03.htm";
            else if (cond == 7 && st.haveQuestItem(TIMORA_ORCS_HEAD)) {
                htmltext = "prefect_tazar_q0414_05.htm";
                st.exitCurrentQuest();
                if (st.player.getClassId().occupation() == 0) {
                    st.giveItems(MARK_OF_RAIDER);
                    st.addExpAndSp(228064, 16455);
                    st.giveAdena(81900);
                }
                st.playSound(SOUND_FINISH);
            }
        return htmltext;
		/*
		1	Defeat Ratman Leader			Quest that must be fulfilled to change occupation to Orc Raider. Prefect Karukia says that Orc Raiders must prove that their courage and loyalty are without fault. To prove your courage you must destroy the Goblins and their Kuruka Ratmen helpers that are ruining this land. Kill Goblin Tomb Raider Leaders and Kuruka Ratman Leaders.\n
		2	Return to Prefect Karukia		You have killed all the Kuruka Ratman Leaders. Now, return to Prefect Karukia of Orc Fortress.\n
		3	Kill the Betrayers!				Prefect Karukia orders you to kill two traitors who betrayed their tribe and went into hiding with Umbar tribe. Take their heads and go to Prefect Kasman of Gludin Village. Read the betrayer's report and by using the information in it, slay the Orc betrayers!\n
		4	Visit Prefect Kasman			You have slain the betrayerous Orc who hid out with the Umbar tribe. Take the head to Prefect Kasman in Gludin Village.\n
		5	Toward the Town of Schuttgart	Go to Prefect Tazeer of Schuttgart. He will advise you on what to do.\n
		6	Defeat the Timora Orc!			Prefect Tazeer tells you to kill the Orc, Timora, the betrayer, who is hiding among the Ragna Orcs. Return with the traitor's head.\n
		7	Return to Tazeer				You have claimed the Orc Timora's head. Return to Prefect Tazeer in the Town of Schuttgart.\n
		 */
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == GOBLIN_TOMB_RAIDER_LEADER && cond == 1) {
            if (st.getQuestItemsCount(GOBLIN_DWELLING_MAP) == 1 && st.getQuestItemsCount(KURUKA_RATMAN_TOOTH) < 10 && st.getQuestItemsCount(GREEN_BLOOD) < 40)
                if (st.getQuestItemsCount(GREEN_BLOOD) > 20 && Rnd.chance((st.getQuestItemsCount(GREEN_BLOOD) - 20) * 5)) {
                    st.takeItems(GREEN_BLOOD);
                    st.addSpawn(27045);
                } else {
                    st.giveItems(GREEN_BLOOD);
                    st.playSound(SOUND_ITEMGET);
                }
        } else if (npcId == KURUKA_RATMAN_LEADER && cond == 1) {
            if (st.getQuestItemsCount(GOBLIN_DWELLING_MAP) > 0 && st.getQuestItemsCount(KURUKA_RATMAN_TOOTH) < 10) {
                st.giveItems(KURUKA_RATMAN_TOOTH);
                if (st.getQuestItemsCount(KURUKA_RATMAN_TOOTH) > 9) {
                    st.setCond(2);
                    st.playSound(SOUND_MIDDLE);
                } else
                    st.playSound(SOUND_ITEMGET);
            }
        } else if (npcId == UMBAR_ORC && cond == 3) {
            if (st.haveQuestItem(BETRAYER_UMBAR_REPORT) && st.getQuestItemsCount(HEAD_OF_BETRAYER) < 2) {
                st.giveItems(HEAD_OF_BETRAYER);
                if (st.haveQuestItem(HEAD_OF_BETRAYER)) {
                    st.setCond(4);
                    st.addRadar(Location.of(-80450, 153410, -3175));
                    st.playSound(SOUND_MIDDLE);
                } else
                    st.playSound(SOUND_ITEMGET);
            }
        } else if (npcId == TIMORA_ORC && cond == 6)
            if (st.getQuestItemsCount(TIMORA_ORCS_HEAD) < 1 && Rnd.chance(50)) {
                st.giveItems(TIMORA_ORCS_HEAD);
                st.addRadar(Location.of(90000, -143286, -1520));
                st.setCond(7);
                st.playSound(SOUND_MIDDLE);
            }
    }
}