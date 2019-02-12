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
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.actor.listener.CharListenerList;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static l2trunk.commons.lang.NumberUtils.toInt;
import static l2trunk.commons.lang.NumberUtils.toLong;
import static l2trunk.gameserver.utils.ItemFunctions.addItem;

public final class HuntersGuild extends Functions implements ScriptFile, IVoicedCommandHandler, OnDeathListener {
    private static final Logger LOG = LoggerFactory.getLogger(HuntersGuild.class);

    private static boolean checkTarget(NpcTemplate npc) {

        if (!npc.type.equalsIgnoreCase("monster"))
            return false;
        if (npc.rewardExp == 0)
            return false;
        return GameObjectsStorage.getByNpcId(npc.getNpcId()) != null;
    }

    private static void doReward(Player player) {
        if (!Config.EVENT_BOUNTY_HUNTERS_ENABLED)
            return;
        int rewardid = player.getVarInt("bhRewardId");
        long rewardcount = player.getVarLong("bhRewardCount");
        player.unsetVar("bhMonstersId");
        player.unsetVar("bhMonstersNeeded");
        player.unsetVar("bhMonstersKilled");
        player.unsetVar("bhRewardId");
        player.unsetVar("bhRewardCount");
        player.setVar("bhsuccess");
        addItem(player, rewardid, rewardcount);
        show(new CustomMessage("scripts.events.bountyhunters.TaskCompleted").addNumber(rewardcount).addItemName(rewardid), player);
    }

    @Override
    public void onLoad() {
        CharListenerList.addGlobal(this);
        if (!Config.EVENT_BOUNTY_HUNTERS_ENABLED)
            return;
        VoicedCommandHandler.INSTANCE.registerVoicedCommandHandler(this);
        LOG.info("Loaded Event: Bounty Hunters Guild");
    }

    private void getTask(Player player, int id) {
        if (!Config.EVENT_BOUNTY_HUNTERS_ENABLED)
            return;
        NpcTemplate target;
        double mod = 1.;
        if (id == 0) {
            List<NpcTemplate> monsters = NpcHolder.getAllOfLevel(player.getLevel());
            if (monsters == null || monsters.isEmpty()) {
                show(new CustomMessage("scripts.events.bountyhunters.NoTargets"), player);
                return;
            }
            List<NpcTemplate> targets = new ArrayList<>();
            for (NpcTemplate npc : monsters)
                if (checkTarget(npc))
                    targets.add(npc);
            if (targets.isEmpty()) {
                show(new CustomMessage("scripts.events.bountyhunters.NoTargets"), player);
                return;
            }
            target = Rnd.get(targets);
        } else {
            target = NpcHolder.getTemplate(id);
            if (target == null || !checkTarget(target)) {
                show(new CustomMessage("scripts.events.bountyhunters.WrongTarget"), player);
                return;
            }
            if (player.getLevel() - target.level > 5) {
                show(new CustomMessage("scripts.events.bountyhunters.TooEasy"), player);
                return;
            }
            mod = 0.5 * (10 + target.level - player.getLevel()) / 10.;
        }

        int mobcount = target.level + Rnd.get(25, 50);
        player.setVar("bhMonstersId", target.getNpcId());
        player.setVar("bhMonstersNeeded", mobcount);
        player.setVar("bhMonstersKilled", 0);

        int fails = player.getVarInt("bhfails") * 5;
        int success = player.getVarInt("bhsuccess") * 5;

        double reputation = Math.min(Math.max((100 + success - fails) / 100., .25), 2.) * mod;

        long adenarewardvalue = Math.round((target.level * Math.max(Math.log(target.level), 1) * 10
                + Math.max((target.level - 60) * 33, 0)
                + Math.max((target.level - 65) * 50, 0)) * target.rateHp * mobcount * Config.RATE_DROP_ADENA * player.getRateAdena() * reputation * .15);
        if (Rnd.chance(30)) // Адена, 30% случаев
        {
            player.setVar("bhRewardId", 57);
            player.setVar("bhRewardCount", adenarewardvalue);
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
            player.setVar("bhRewardId", crystal);
            player.setVar("bhRewardCount", adenarewardvalue / ItemHolder.getTemplate(crystal).referencePrice);
        }
        show(new CustomMessage("scripts.events.bountyhunters.TaskGiven").addNumber(mobcount).addString(target.name), player);
    }

    @Override
    public void onDeath(Creature cha, Creature killer) {
        if (!Config.EVENT_BOUNTY_HUNTERS_ENABLED)
            return;
        if (!(killer instanceof Playable))
            return;
        Player player = ((Playable) killer).getPlayer();
        if (cha instanceof MonsterInstance && !cha.isRaid() && player.getVarInt("bhMonstersId") == cha.getNpcId()) {
            player.incVar("bhMonstersKilled");
            int count = player.getVarInt("bhMonstersKilled");
            int needed = player.getVarInt("bhMonstersNeeded");
            if (count >= needed)
                doReward(player);
            else
                player.sendMessage(new CustomMessage("scripts.events.bountyhunters.NotifyKill", player).addNumber(needed - count));
        }
    }

    @Override
    public List<String> getVoicedCommandList() {
        return List.of("gettask", "declinetask");
    }

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String target) {
        if (activeChar == null || !Config.EVENT_BOUNTY_HUNTERS_ENABLED)
            return false;
        if (activeChar.getLevel() < 20) {
            activeChar.sendMessage(new CustomMessage("scripts.events.bountyhunters.TooLowLevel"));
            return true;
        }
        if ("gettask".equalsIgnoreCase(command)) {
            if (activeChar.isVarSet("bhMonstersId")) {
                int mobid = activeChar.getVarInt("bhMonstersId");
                int mobcount = activeChar.getVarInt("bhMonstersNeeded") - activeChar.getVarInt("bhMonstersKilled");
                show(new CustomMessage("scripts.events.bountyhunters.TaskGiven").addNumber(mobcount).addString(NpcHolder.getTemplate(mobid).name), activeChar);
                return true;
            }
            int id = 0;
            if (target != null && target.trim().matches("[\\d]{1,9}"))
                id = toInt(target);
            getTask(activeChar, id);
            return true;
        }
        if (command.equalsIgnoreCase("declinetask")) {
            if (!activeChar.isVarSet("bhMonstersId")) {
                activeChar.sendMessage(new CustomMessage("scripts.events.bountyhunters.NoTask"));
                return true;
            }
            activeChar.unsetVar("bhMonstersId");
            activeChar.unsetVar("bhMonstersNeeded");
            activeChar.unsetVar("bhMonstersKilled");
            activeChar.unsetVar("bhRewardId");
            activeChar.unsetVar("bhRewardCount");
            activeChar.incVar("bhfails");

            show(new CustomMessage("scripts.events.bountyhunters.TaskCanceled"), activeChar);
            return true;
        }
        return false;
    }
}