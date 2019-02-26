package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.ReflectionUtils;

import java.util.List;

public final class _128_PailakaSongofIceandFire extends Quest {
    // NPC
    private static final int ADLER = 32497;
    private static final int ADLER2 = 32510;
    private static final int SINAI = 32500;
    private static final int TINSPECTOR = 32507;

    // BOSS
    private static final int HILLAS = 18610;
    private static final int PAPION = 18609;
    private static final int GARGOS = 18607;
    private static final int KINSUS = 18608;
    private static final int ADIANTUM = 18620;

    // MOBS
    private static final int Bloom = 18616;
    private static final int CrystalWaterBottle = 32492;
    private static final int BurningBrazier = 32493;

    // ITEMS
    private static final int PailakaInstantShield = 13032;
    private static final int QuickHealingPotion = 13033;
    private static final int FireAttributeEnhancer = 13040;
    private static final int WaterAttributeEnhancer = 13041;
    private static final int SpritesSword = 13034;
    private static final int EnhancedSpritesSword = 13035;
    private static final int SwordofIceandFire = 13036;
    private static final int EssenceofWater = 13038;
    private static final int EssenceofFire = 13039;

    private static final int TempleBookofSecrets1 = 13130;
    private static final int TempleBookofSecrets2 = 13131;
    private static final int TempleBookofSecrets3 = 13132;
    private static final int TempleBookofSecrets4 = 13133;
    private static final int TempleBookofSecrets5 = 13134;
    private static final int TempleBookofSecrets6 = 13135;
    private static final int TempleBookofSecrets7 = 13136;

    // REWARDS
    private static final int PailakaRing = 13294;
    private static final int PailakaEarring = 13293;
    private static final int ScrollofEscape = 736;

    private static final List<Integer> MOBS = List.of(18611, 18612, 18613, 18614, 18615);
    private static final List<Integer> HPHERBS = List.of(8600, 8601, 8602);
    private static final List<Integer> MPHERBS = List.of(8603, 8604, 8605);

    private static final int izId = 43;

    public _128_PailakaSongofIceandFire() {
        super(false);

        addStartNpc(ADLER);
        addTalkId(ADLER2, SINAI);
        addFirstTalkId(TINSPECTOR);
        addKillId(HILLAS, PAPION, ADIANTUM, KINSUS, GARGOS, Bloom, CrystalWaterBottle, BurningBrazier);
        addKillId(MOBS);
        addQuestItem(SpritesSword, EnhancedSpritesSword, SwordofIceandFire, EssenceofWater, EssenceofFire);
        addQuestItem(TempleBookofSecrets1, TempleBookofSecrets2, TempleBookofSecrets3, TempleBookofSecrets4, TempleBookofSecrets5, TempleBookofSecrets6, TempleBookofSecrets7);
        addQuestItem(PailakaInstantShield, QuickHealingPotion, FireAttributeEnhancer, WaterAttributeEnhancer);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        Player player = st.player;
        int refId = player.getReflectionId();
        String htmltext = event;

        if ("Enter".equalsIgnoreCase(event)) {
            enterInstance(player);
            return null;
        } else if ("32497-04.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("32500-06.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
            st.giveItems(SpritesSword);
            st.giveItems(TempleBookofSecrets1);
        } else if ("32507-03.htm".equalsIgnoreCase(event)) {
            st.setCond(4);
            st.playSound(SOUND_MIDDLE);
            st.takeItems(TempleBookofSecrets2);
            st.giveItems(TempleBookofSecrets3);
            if (st.haveQuestItem(EssenceofWater) ) {
                st.takeItems(SpritesSword);
                st.takeItems(EssenceofWater);
                st.giveItems(EnhancedSpritesSword);
            } else {
                htmltext = "32507-01.htm";
            }
            addSpawnToInstance(PAPION, Location.of(-53903, 181484, -4555, 30456), refId);
        } else if ("32507-07.htm".equalsIgnoreCase(event)) {
            st.setCond(7);
            st.playSound(SOUND_MIDDLE);
            st.takeItems(TempleBookofSecrets5);
            st.giveItems(TempleBookofSecrets6);
            if (st.haveQuestItem(EssenceofFire)) {
                st.takeItems(EnhancedSpritesSword);
                st.takeItems(EssenceofFire);
                st.giveItems(SwordofIceandFire);
            } else {
                htmltext = "32507-04.htm";
            }
            addSpawnToInstance(GARGOS, Location.of(-61354, 183624, -4821, 63613), refId);
        } else if ("32510-02.htm".equalsIgnoreCase(event)) {
            st.giveItems(PailakaRing);
            st.giveItems(PailakaEarring);
            st.giveItems(ScrollofEscape);
            st.addExpAndSp(810000, 50000);
            st.playSound(SOUND_FINISH);
            st.finish();
            player.setVitality(Config.VITALITY_LEVELS.get(4));
            player.getReflection().startCollapseTimer(60000);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        int id = st.getState();
        Player player = st.player;
        if (npcId == ADLER) {
            if (cond == 0)
                if (player.getLevel() >= 36 && player.getLevel() <= 42) {
                    return "32497-01.htm";
                } else {
                    htmltext = "32497-no.htm";
                    st.exitCurrentQuest();
                }
            else if (id == COMPLETED)
                htmltext = "32497-no.htm";
            else
                return "32497-05.htm";
        } else if (npcId == SINAI) {
            if (cond == 1)
                htmltext = "32500-01.htm";
            else
                htmltext = "32500-06.htm";
        } else if (npcId == ADLER2)
            if (cond == 9)
                htmltext = "32510-01.htm";
            else if (id == COMPLETED)
                htmltext = "32510-02.htm";
        return htmltext;
    }

    @Override
    public String onFirstTalk(NpcInstance npc, Player player) {
        String htmltext = "noquest";
        QuestState st = player.getQuestState(this);
        if (st == null || st.isCompleted())
            return htmltext;
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == TINSPECTOR)
            if (cond == 2)
                htmltext = "32507-01.htm";
            else if (cond == 3)
                htmltext = "32507-02.htm";
            else if (cond == 6)
                htmltext = "32507-05.htm";
            else
                htmltext = "32507-04.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        Player player = st.player;
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        int refId = player.getReflectionId();
        if (MOBS.contains(npcId)) {
            if (Rnd.chance( 50))
                st.dropItem(npc, Rnd.get(HPHERBS));
            if (Rnd.get(100) < 50)
                st.dropItem(npc, Rnd.get(MPHERBS));
        } else if (npcId == HILLAS && cond == 2) {
            st.takeItems(TempleBookofSecrets1);
            st.giveItems(EssenceofWater);
            st.giveItems(TempleBookofSecrets2);
            st.setCond(3);
            st.playSound(SOUND_MIDDLE);
        } else if (npcId == PAPION && cond == 4) {
            st.takeItems(TempleBookofSecrets3);
            st.giveItems(TempleBookofSecrets4);
            st.setCond(5);
            st.playSound(SOUND_MIDDLE);
            addSpawnToInstance(KINSUS, Location.of(-61404, 181351, -4815, 63953), refId);
        } else if (npcId == KINSUS && cond == 5) {
            st.takeItems(TempleBookofSecrets4);
            st.giveItems(EssenceofFire);
            st.giveItems(TempleBookofSecrets5);
            st.setCond(6);
            st.playSound(SOUND_MIDDLE);
        } else if (npcId == GARGOS && cond == 7) {
            st.takeItems(TempleBookofSecrets6);
            st.giveItems(TempleBookofSecrets7);
            st.setCond(8);
            st.playSound(SOUND_MIDDLE);
            addSpawnToInstance(ADIANTUM,  Location.of(-53297, 185027, -4617, 1512), refId);
        } else if (npcId == ADIANTUM && cond == 8) {
            st.takeItems(TempleBookofSecrets7);
            st.setCond(9);
            st.playSound(SOUND_MIDDLE);
            addSpawnToInstance(ADLER2, npc.getLoc(), refId);
        } else if (npcId == Bloom) {
            if (Rnd.chance(50))
                st.dropItem(npc, PailakaInstantShield, Rnd.get(1, 7));
            if (Rnd.chance(30))
                st.dropItem(npc, QuickHealingPotion, Rnd.get(1, 7));
        } else if (npcId == CrystalWaterBottle) {
            if (Rnd.chance(50))
                st.dropItem(npc, PailakaInstantShield, Rnd.get(1, 10));
            if (Rnd.chance(30))
                st.dropItem(npc, QuickHealingPotion, Rnd.get(1, 10));
            if (Rnd.chance(10))
                st.dropItem(npc, WaterAttributeEnhancer, Rnd.get(1, 5));
        } else if (npcId == BurningBrazier) {
            if (Rnd.chance(50))
                st.dropItem(npc, PailakaInstantShield, Rnd.get(1, 10));
            if (Rnd.chance(30))
                st.dropItem(npc, QuickHealingPotion, Rnd.get(1, 10));
            if (Rnd.chance(10))
                st.dropItem(npc, FireAttributeEnhancer, Rnd.get(1, 5));
        }
    }

    private void enterInstance(Player player) {
        Reflection r = player.getActiveReflection();
        if (r != null) {
            if (player.canReenterInstance(izId))
                player.teleToLocation(r.getTeleportLoc(), r);
        } else if (player.canEnterInstance(izId)) {
            ReflectionUtils.enterReflection(player, izId);
        }
    }
}