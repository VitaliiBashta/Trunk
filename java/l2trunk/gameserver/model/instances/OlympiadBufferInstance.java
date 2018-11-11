package l2trunk.gameserver.model.instances;

import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.network.serverpackets.MyTargetSelected;
import l2trunk.gameserver.network.serverpackets.ValidateLocation;
import l2trunk.gameserver.scripts.Events;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.util.*;

public final class OlympiadBufferInstance extends NpcInstance {
    private final Set<Integer> buffs = new HashSet<>();

    public OlympiadBufferInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onAction(Player player, boolean shift) {
        if (Events.onAction(player, this, shift)) {
            player.sendActionFailed();
            return;
        }

        if (this != player.getTarget()) {
            player.setTarget(this);
            MyTargetSelected my = new MyTargetSelected(getObjectId(), player.getLevel() - getLevel());
            player.sendPacket(my);
            player.sendPacket(new ValidateLocation(this));
        } else {
            MyTargetSelected my = new MyTargetSelected(getObjectId(), player.getLevel() - getLevel());
            player.sendPacket(my);
            if (!isInRange(player, INTERACTION_DISTANCE))
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
            else if (buffs.size() > 4)
                showChatWindow(player, 1);
            else
                showChatWindow(player, 0);
            player.sendActionFailed();
        }
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (buffs.size() > 4)
            showChatWindow(player, 1);

        if (command.startsWith("Buff")) {
            StringTokenizer st = new StringTokenizer(command, " ");
            st.nextToken();
            int id = Integer.parseInt(st.nextToken());
            int lvl = Integer.parseInt(st.nextToken());
            Skill skill = SkillTable.getInstance().getInfo(id, lvl);
            List<Creature> target = new ArrayList<>();
            target.add(player);
            broadcastPacket(new MagicSkillUse(this, player, id, lvl, 0, 0));
            callSkill(skill, target, true);
            buffs.add(id);
            if (buffs.size() > 4)
                showChatWindow(player, 1);
            else
                showChatWindow(player, 0);
        } else
            showChatWindow(player, 0);
    }

    @Override
    public String getHtmlPath(int npcId, int val, Player player) {
        String pom;
        if (val == 0)
            pom = "buffer";
        else
            pom = "buffer-" + val;

        // If the file is not found, the standard message "I have nothing to say to you" is returned
        return "olympiad/" + pom + ".htm";
    }
}