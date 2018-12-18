package l2trunk.scripts.events.bountyhunters;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.actor.listener.CharListenerList;
import l2trunk.gameserver.model.instances.*;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.scripts.npc.model.QueenAntLarvaInstance;
import l2trunk.scripts.npc.model.SquashInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static l2trunk.commons.lang.NumberUtils.toInt;
import static l2trunk.commons.lang.NumberUtils.toLong;

public final class HuntersGuild extends Functions implements ScriptFile, IVoicedCommandHandler, OnDeathListener {
    private static final List<String> COMMAND_LIST = List.of("gettask", "declinetask");
    private static final Logger LOG = LoggerFactory.getLogger(HuntersGuild.class);

    @Override
    public void onLoad() {
        CharListenerList.addGlobal(this);
        if (!Config.EVENT_BOUNTY_HUNTERS_ENABLED)
            return;
        VoicedCommandHandler.INSTANCE.registerVoicedCommandHandler(this);
        LOG.info("Loaded Event: Bounty Hunters Guild");
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    private static boolean checkTarget(NpcTemplate npc) {
        if (!npc.isInstanceOf(MonsterInstance.class))
            return false;
        if (npc.rewardExp == 0)
            return false;
        if (npc.isInstanceOf(RaidBossInstance.class))
            return false;
        if (npc.isInstanceOf(QueenAntLarvaInstance.class))
            return false;
        if (npc.isInstanceOf(SquashInstance.class))
            return false;
        if (npc.isInstanceOf(MinionInstance.class))
            return false;
        if (npc.isInstanceOf(FestivalMonsterInstance.class))
            return false;
        if (npc.isInstanceOf(TamedBeastInstance.class))
            return false;
        if (npc.isInstanceOf(DeadManInstance.class))
            return false;
        if (npc.isInstanceOf(ChestInstance.class))
            return false;
        if (npc.title.contains("Quest Monster"))
            return false;
        return GameObjectsStorage.getByNpcId(npc.getNpcId()) != null;
    }

    private void getTask(Player player, int id) {
        if (!Config.EVENT_BOUNTY_HUNTERS_ENABLED)
            return;
        NpcTemplate target;
        double mod = 1.;
        if (id == 0) {
            List<NpcTemplate> monsters = NpcHolder.getAllOfLevel(player.getLevel());
            if (monsters == null || monsters.isEmpty()) {
                show(new CustomMessage("scripts.events.bountyhunters.NoTargets", player), player);
                return;
            }
            List<NpcTemplate> targets = new ArrayList<>();
            for (NpcTemplate npc : monsters)
                if (checkTarget(npc))
                    targets.add(npc);
            if (targets.isEmpty()) {
                show(new CustomMessage("scripts.events.bountyhunters.NoTargets", player), player);
                return;
            }
            target = targets.get(Rnd.get(targets.size()));
        } else {
            target = NpcHolder.getTemplate(id);
            if (target == null || !checkTarget(target)) {
                show(new CustomMessage("scripts.events.bountyhunters.WrongTarget", player), player);
                return;
            }
            if (player.getLevel() - target.level > 5) {
                show(new CustomMessage("scripts.events.bountyhunters.TooEasy", player), player);
                return;
            }
            mod = 0.5 * (10 + target.level - player.getLevel()) / 10.;
        }

        int mobcount = target.level + Rnd.get(25, 50);
        player.setVar("bhMonstersId", String.valueOf(target.getNpcId()), -1);
        player.setVar("bhMonstersNeeded", String.valueOf(mobcount), -1);
        player.setVar("bhMonstersKilled", "0", -1);

        int fails = player.getVar("bhfails") == null ? 0 : toInt(player.getVar("bhfails")) * 5;
        int success = player.getVar("bhsuccess") == null ? 0 : toInt(player.getVar("bhsuccess")) * 5;

        double reputation = Math.min(Math.max((100 + success - fails) / 100., .25), 2.) * mod;

        long adenarewardvalue = Math.round((target.level * Math.max(Math.log(target.level), 1) * 10
                + Math.max((target.level - 60) * 33, 0)
                + Math.max((target.level - 65) * 50, 0)) * target.rateHp * mobcount * Config.RATE_DROP_ADENA * player.getRateAdena() * reputation * .15);
        if (Rnd.chance(30)) // Адена, 30% случаев
        {
            player.setVar("bhRewardId", "57", -1);
            player.setVar("bhRewardCount", String.valueOf(adenarewardvalue), -1);
        } else { // Кристаллы, 70% случаев
            int crystal;
            if (target.level <= 39)
                crystal = 1458; // D
            else if (target.level <= 51)
                crystal = 1459; // C
            else if (target.level <= 60)
                crystal = 1460; // B
            else if (target.level <= 75)
                crystal = 1461; // A
            else
                crystal = 1462; // S
            player.setVar("bhRewardId", String.valueOf(crystal), -1);
            player.setVar("bhRewardCount", String.valueOf(adenarewardvalue / ItemHolder.getTemplate(crystal).getReferencePrice()), -1);
        }
        show(new CustomMessage("scripts.events.bountyhunters.TaskGiven", player).addNumber(mobcount).addString(target.name), player);
    }

    @Override
    public void onDeath(Creature cha, Creature killer) {
        if (!Config.EVENT_BOUNTY_HUNTERS_ENABLED)
            return;
        if (cha.isMonster() && !cha.isRaid() && killer != null && killer.getPlayer() != null && killer.getPlayer().getVar("bhMonstersId") != null && toInt(killer.getPlayer().getVar("bhMonstersId")) == cha.getNpcId()) {
            int count = toInt(killer.getPlayer().getVar("bhMonstersKilled")) + 1;
            killer.getPlayer().setVar("bhMonstersKilled", String.valueOf(count), -1);
            int needed = toInt(killer.getPlayer().getVar("bhMonstersNeeded"));
            if (count >= needed)
                doReward(killer.getPlayer());
            else
                sendMessage(new CustomMessage("scripts.events.bountyhunters.NotifyKill", killer.getPlayer()).addNumber(needed - count), killer.getPlayer());
        }
    }

    private static void doReward(Player player) {
        if (!Config.EVENT_BOUNTY_HUNTERS_ENABLED)
            return;
        int rewardid = toInt(player.getVar("bhRewardId"));
        long rewardcount = toLong(player.getVar("bhRewardCount"));
        player.unsetVar("bhMonstersId");
        player.unsetVar("bhMonstersNeeded");
        player.unsetVar("bhMonstersKilled");
        player.unsetVar("bhRewardId");
        player.unsetVar("bhRewardCount");
        if (player.getVar("bhsuccess") != null)
            player.setVar("bhsuccess", String.valueOf(toInt(player.getVar("bhsuccess")) + 1), -1);
        else
            player.setVar("bhsuccess", "1", -1);
        addItem(player, rewardid, rewardcount, "HuntersGuild");
        show(new CustomMessage("scripts.events.bountyhunters.TaskCompleted", player).addNumber(rewardcount).addItemName(rewardid), player);
    }

    @Override
    public List<String> getVoicedCommandList() {
        return COMMAND_LIST;
    }

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String target) {
        if (activeChar == null || !Config.EVENT_BOUNTY_HUNTERS_ENABLED)
            return false;
        if (activeChar.getLevel() < 20) {
            sendMessage(new CustomMessage("scripts.events.bountyhunters.TooLowLevel", activeChar), activeChar);
            return true;
        }
        if (command.equalsIgnoreCase("gettask")) {
            if (activeChar.getVar("bhMonstersId") != null) {
                int mobid = toInt(activeChar.getVar("bhMonstersId"));
                int mobcount = toInt(activeChar.getVar("bhMonstersNeeded")) - toInt(activeChar.getVar("bhMonstersKilled"));
                show(new CustomMessage("scripts.events.bountyhunters.TaskGiven", activeChar).addNumber(mobcount).addString(NpcHolder.getTemplate(mobid).name), activeChar);
                return true;
            }
            int id = 0;
            if (target != null && target.trim().matches("[\\d]{1,9}"))
                id = toInt(target);
            getTask(activeChar, id);
            return true;
        }
        if (command.equalsIgnoreCase("declinetask")) {
            if (activeChar.getVar("bhMonstersId") == null) {
                sendMessage(new CustomMessage("scripts.events.bountyhunters.NoTask", activeChar), activeChar);
                return true;
            }
            activeChar.unsetVar("bhMonstersId");
            activeChar.unsetVar("bhMonstersNeeded");
            activeChar.unsetVar("bhMonstersKilled");
            activeChar.unsetVar("bhRewardId");
            activeChar.unsetVar("bhRewardCount");
            if (activeChar.getVar("bhfails") != null)
                activeChar.setVar("bhfails", String.valueOf(toInt(activeChar.getVar("bhfails")) + 1), -1);
            else
                activeChar.setVar("bhfails", "1", -1);
            show(new CustomMessage("scripts.events.bountyhunters.TaskCanceled", activeChar), activeChar);
            return true;
        }
        return false;
    }
}