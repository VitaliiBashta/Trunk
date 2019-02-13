package l2trunk.scripts.quests;


import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _692_HowtoOpposeEvil extends Quest {
    //NPC
    private static final int Dilios = 32549;
    private static final int Kutran = 32550;
    private static final int Lekon = 32557;

    //QuestItem
    private static final int NucleusofanIncompleteSoul = 13863;
    private static final int FleetSteedTroupsTotem = 13865;
    private static final int PortionofaSoul = 13866;
    private static final int BreathofTiat = 13867;

    private static final int ConcentratedSpiritEnergy = 15535;
    private static final int SpiritStoneDust = 15536;

    private static final int NucleusofaFreedSoul = 13796;
    private static final int FleetSteedTroupsCharm = 13841;
    private static final int SpiritStoneFragment = 15486;

    private static final List<Integer> SOD = List.of(
            22552, 22541, 22550, 22551, 22596, 22544, 22540, 22547,
            22542, 22543, 22539, 22546, 22548, 22536, 22538, 22537);
    private static final List<Integer> SOI = List.of(
            22509, 22510, 22511, 22512, 22513, 22514, 22515, 22520, 22522, 22527, 22531,
            22535, 22516, 22517, 22518, 22519, 22521, 22524, 22528, 22532, 22530, 22535);

    private static final List<Integer> SOA = List.of(
            22746, 22747, 22748, 22749, 22750, 22751, 22752, 22753, 22754, 22755,
            22756, 22757, 22758, 22759, 22760, 22761, 22762, 22763, 22764, 22765);

    public _692_HowtoOpposeEvil() {
        super(true);
        addStartNpc(Dilios);
        addTalkId(Kutran, Lekon);

        addKillId(SOD);
        addKillId(SOI);
        addKillId(SOA);

        addQuestItem(NucleusofanIncompleteSoul, FleetSteedTroupsTotem, PortionofaSoul);
        addQuestItem(BreathofTiat, ConcentratedSpiritEnergy, SpiritStoneDust);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int cond = st.getCond();
        if ("take_test".equalsIgnoreCase(event) && cond == 0) {
            if (st.player.isQuestCompleted(_10273_GoodDayToFly.class)) {
                st.setCond(2);
                st.setState(STARTED);
                st.playSound(SOUND_ACCEPT);
                htmltext = "dilios_q692_4.htm";
            } else {
                st.setCond(1);
                st.setState(STARTED);
                st.playSound(SOUND_ACCEPT);
                htmltext = "dilios_q692_3.htm";
            }
        } else if ("lekon_q692_2.htm".equalsIgnoreCase(event) && cond == 1)
            st.exitCurrentQuest(true);
        else if ("kutran_q692_2.htm".equalsIgnoreCase(event) && cond == 2) {
            st.setCond(3);
            st.playSound(SOUND_MIDDLE);
        } else if (event.equalsIgnoreCase("exchange_sod") && cond == 3) {
            if (st.getQuestItemsCount(FleetSteedTroupsTotem) < 5)
                htmltext = "kutran_q692_7.htm";
            else {
                int _charmstogive = Math.round(st.getQuestItemsCount(FleetSteedTroupsTotem) / 5);
                st.takeItems(FleetSteedTroupsTotem, 5 * _charmstogive);
                st.giveItems(FleetSteedTroupsCharm, _charmstogive);
                htmltext = "kutran_q692_4.htm";
            }
        } else if (event.equalsIgnoreCase("exchange_soi") && cond == 3) {
            if (st.getQuestItemsCount(NucleusofanIncompleteSoul) < 5)
                htmltext = "kutran_q692_7.htm";
            else {
                int _soulstogive = Math.round(st.getQuestItemsCount(NucleusofanIncompleteSoul) / 5);
                st.takeItems(NucleusofanIncompleteSoul, 5 * _soulstogive);
                st.giveItems(NucleusofaFreedSoul, _soulstogive);
                htmltext = "kutran_q692_5.htm";
            }
        } else if (event.equalsIgnoreCase("exchange_soa") && cond == 3) {
            if (st.getQuestItemsCount(SpiritStoneDust) < 5)
                htmltext = "kutran_q692_7.htm";
            else {
                int _soulstogive = Math.round(st.getQuestItemsCount(SpiritStoneDust) / 5);
                st.takeItems(SpiritStoneDust, 5 * _soulstogive);
                st.giveItems(SpiritStoneFragment, _soulstogive);
                htmltext = "kutran_q692_5.htm";
            }
        } else if ("exchange_breath".equalsIgnoreCase(event) && cond == 3) {
            if (st.getQuestItemsCount(BreathofTiat) == 0)
                htmltext = "kutran_q692_7.htm";
            else {
                st.giveItems(ADENA_ID, st.getQuestItemsCount(BreathofTiat) * 2500);
                st.takeItems(BreathofTiat);
                htmltext = "kutran_q692_5.htm";
            }
        } else if ("exchange_portion".equalsIgnoreCase(event) && cond == 3) {
            if (st.getQuestItemsCount(PortionofaSoul) == 0)
                htmltext = "kutran_q692_7.htm";
            else {
                st.giveItems(ADENA_ID, st.getQuestItemsCount(PortionofaSoul) * 2500);
                st.takeItems(PortionofaSoul);
                htmltext = "kutran_q692_5.htm";
            }
        } else if ("exchange_energy".equalsIgnoreCase(event) && cond == 3) {
            if (st.getQuestItemsCount(ConcentratedSpiritEnergy) == 0)
                htmltext = "kutran_q692_7.htm";
            else {
                st.giveItems(ADENA_ID, st.getQuestItemsCount(ConcentratedSpiritEnergy) * 25000);
                st.takeItems(ConcentratedSpiritEnergy, -1);
                htmltext = "kutran_q692_5.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();

        if (npcId == Dilios) {
            if (cond == 0)
                if (st.player.getLevel() >= 75)
                    htmltext = "dilios_q692_1.htm";
                else {
                    htmltext = "dilios_q692_0.htm";
                    st.exitCurrentQuest(true);
                }
        } else if (npcId == Kutran) {
            if (cond == 2)
                htmltext = "kutran_q692_1.htm";
            else if (cond == 3)
                htmltext = "kutran_q692_3.htm";
        } else if (npcId == Lekon)
            if (cond == 1)
                htmltext = "lekon_q692_1.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 3) {
            if (SOD.contains(npcId))
                st.rollAndGive(FleetSteedTroupsTotem, (int) Config.RATE_QUESTS_REWARD, 17); //Шанс на глаз и с потолка. Умножается на рейт квестов и делится на кол-во членов группы. Для нас это (nRate*3)/9
            else if (SOI.contains(npcId))
                st.rollAndGive(NucleusofanIncompleteSoul, (int) Config.RATE_QUESTS_REWARD, 17); //Шанс на глаз и с потолка. Умножается на рейт квестов и делится на кол-во членов группы. Для нас это (nRate*3)/9
            else if (SOA.contains(npcId))
                st.rollAndGive(SpiritStoneDust, (int) Config.RATE_QUESTS_REWARD, 20); //Шанс на глаз и с потолка. Умножается на рейт квестов и делится на кол-во членов группы. Для нас это (nRate*3)/9
        }
    }
}