package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.data.xml.holder.SoulCrystalHolder;
import l2trunk.gameserver.model.Party;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.templates.SoulCrystal;
import l2trunk.gameserver.templates.npc.AbsorbInfo;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public final class _350_EnhanceYourWeapon extends Quest {
    private static final int RED_SOUL_CRYSTAL0_ID = 4629;
    private static final int GREEN_SOUL_CRYSTAL0_ID = 4640;
    private static final int BLUE_SOUL_CRYSTAL0_ID = 4651;
    private static final int Jurek = 30115;
    private static final int Gideon = 30194;
    private static final int Winonin = 30856;
    public _350_EnhanceYourWeapon() {
        super(false);
        addStartNpc(Jurek);
        addStartNpc(Gideon);
        addStartNpc(Winonin);

        for (NpcTemplate template : NpcHolder.getAll()) {
            if (template != null && !template.getAbsorbInfo().isEmpty())
                addKillId(template.npcId);
        }
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase(Jurek + "-04.htm") || event.equalsIgnoreCase(Gideon + "-04.htm") || event.equalsIgnoreCase(Winonin + "-04.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        }
        if (event.equalsIgnoreCase(Jurek + "-09.htm") || event.equalsIgnoreCase(Gideon + "-09.htm") || event.equalsIgnoreCase(Winonin + "-09.htm"))
            st.giveItems(RED_SOUL_CRYSTAL0_ID, 1);
        if (event.equalsIgnoreCase(Jurek + "-10.htm") || event.equalsIgnoreCase(Gideon + "-10.htm") || event.equalsIgnoreCase(Winonin + "-10.htm"))
            st.giveItems(GREEN_SOUL_CRYSTAL0_ID, 1);
        if (event.equalsIgnoreCase(Jurek + "-11.htm") || event.equalsIgnoreCase(Gideon + "-11.htm") || event.equalsIgnoreCase(Winonin + "-11.htm"))
            st.giveItems(BLUE_SOUL_CRYSTAL0_ID, 1);
        if (event.equalsIgnoreCase("exit.htm"))
            st.exitCurrentQuest(true);
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext;
        int id = st.getState();
        if (st.getQuestItemsCount(RED_SOUL_CRYSTAL0_ID) == 0 && st.getQuestItemsCount(GREEN_SOUL_CRYSTAL0_ID) == 0 && st.getQuestItemsCount(BLUE_SOUL_CRYSTAL0_ID) == 0)
            if (id == CREATED)
                htmltext = npcId + "-01.htm";
            else
                htmltext = npcId + "-21.htm";
        else {
            if (id == CREATED) {
                st.setCond(1);
                st.setState(STARTED);
            }
            htmltext = npcId + "-03.htm";
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState qs) {
        Player player = qs.player;
        if (player != null && npc instanceof MonsterInstance) {
            List<PlayerResult> list;
            Party party = player.getParty();
            if (party == null) {
                list = List.of(new PlayerResult(player));
            } else {
                list = party.getMembers().stream()
                        .filter(m -> m.isInRange(npc.getLoc(), Config.ALT_PARTY_DISTRIBUTION_RANGE))
                        .map(PlayerResult::new)
                        .collect(Collectors.toList());
                list.add(new PlayerResult(player)); // DS: у убившего двойной шанс, из ai
            }

            for (AbsorbInfo info : npc.getTemplate().getAbsorbInfo())
                calcAbsorb(list, (MonsterInstance) npc, info);

            list.forEach(PlayerResult::send);
        }

    }

    private void calcAbsorb(List<PlayerResult> players, MonsterInstance npc, AbsorbInfo info) {
        int memberSize;
        List<PlayerResult> targets;
        switch (info.getAbsorbType()) {
            case LAST_HIT:
                targets = List.of(players.get(0));
                break;
            case PARTY_ALL:
                targets = players;
                break;
            case PARTY_RANDOM:
                memberSize = players.size();
                if (memberSize == 1)
                    targets = Collections.singletonList(players.get(0));
                else {
                    int size = Rnd.get(memberSize);
                    targets = new ArrayList<>(size);
                    List<PlayerResult> temp = new ArrayList<>(players);
                    Collections.shuffle(temp);
                    for (int i = 0; i < size; i++)
                        targets.add(temp.get(i));
                }
                break;
            case PARTY_ONE:
                memberSize = players.size();
                if (memberSize == 1)
                    targets = Collections.singletonList(players.get(0));
                else {
                    int rnd = Rnd.get(memberSize);
                    targets = Collections.singletonList(players.get(rnd));
                }
                break;
            default:
                return;
        }

        for (PlayerResult target : targets) {
            if (target == null || !(target.getMessage() == null || target.getMessage() == SystemMsg.THE_SOUL_CRYSTAL_IS_REFUSING_TO_ABSORB_THE_SOUL))
                continue;
            Player targetPlayer = target.player;
            if (info.isSkill() && !npc.isAbsorbed(targetPlayer))
                continue;
            if (targetPlayer.getQuestState(_350_EnhanceYourWeapon.class) == null)
                continue;

            boolean resonation = false;
            SoulCrystal soulCrystal = null;
            List<ItemInstance> items = targetPlayer.getInventory().getItems();
            for (ItemInstance item : items) {
                SoulCrystal crystal = SoulCrystalHolder.getInstance().getCrystal(item.getItemId());
                if (crystal == null)
                    continue;

                if (soulCrystal != null) {
                    target.setMessage(SystemMsg.THE_SOUL_CRYSTAL_CAUSED_RESONATION_AND_FAILED_AT_ABSORBING_A_SOUL);
                    resonation = true;
                    break;
                }
                soulCrystal = crystal;
            }

            if (resonation)
                continue;

            if (soulCrystal == null)
                continue;

            if (!info.canAbsorb(soulCrystal.getLevel() + 1)) {
                target.setMessage(SystemMsg.THE_SOUL_CRYSTAL_IS_REFUSING_TO_ABSORB_THE_SOUL);
                continue;
            }

            int nextItemId = 0;
            if (info.getCursedChance() > 0 && soulCrystal.getCursedNextItemId() > 0)
                nextItemId = Rnd.chance(info.getCursedChance()) ? soulCrystal.getCursedNextItemId() : 0;

            if (nextItemId == 0)
                nextItemId = Rnd.chance(info.getChance()) ? soulCrystal.getNextItemId() : 0;

            if (nextItemId == 0) {
                target.setMessage(SystemMsg.THE_SOUL_CRYSTAL_WAS_NOT_ABLE_TO_ABSORB_THE_SOUL);
                continue;
            }

            if (targetPlayer.consumeItem(soulCrystal.getItemId(), 1)) {
                targetPlayer.getInventory().addItem(nextItemId, 1, "_350_EnhanceYourWeapon");
                targetPlayer.sendPacket(SystemMessage2.obtainItems(nextItemId, 1, 0));

                if (targetPlayer.getCounters().maxSoulCrystalLevel > soulCrystal.getLevel() + 1)
                    targetPlayer.getCounters().maxSoulCrystalLevel = soulCrystal.getLevel() + 1;

                target.setMessage(SystemMsg.THE_SOUL_CRYSTAL_SUCCEEDED_IN_ABSORBING_A_SOUL);
            } else
                target.setMessage(SystemMsg.THE_SOUL_CRYSTAL_WAS_NOT_ABLE_TO_ABSORB_THE_SOUL);
        }
    }

    private static class PlayerResult {
        private final Player player;
        private SystemMsg message;

        PlayerResult(Player player) {
            this.player = player;
        }

        SystemMsg getMessage() {
            return message;
        }

        void setMessage(SystemMsg message) {
            this.message = message;
        }

        void send() {
            if (message != null) {
                player.sendPacket(message);
                message = null;
            }
        }
    }
}