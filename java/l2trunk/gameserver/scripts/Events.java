package l2trunk.gameserver.scripts;

import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.scripts.Scripts.ScriptClassAndMethod;
import l2trunk.gameserver.utils.Strings;
import l2trunk.scripts.actions.OnActionShift;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Events {
    private static final Logger LOG= LoggerFactory.getLogger(Events.class);
    public static boolean onAction(Player player, GameObject obj, boolean shift) {
        OnActionShift act = new OnActionShift();
        ScriptClassAndMethod handler = null ;
        if (shift) {
            if (player.getVarB("noShift")) {
                return false;
            }
//            handler = Scripts.onActionShift.get(obj.getL2ClassShortName());
            if ((handler == null) && obj.isNpc()) {
                return act.OnActionShift_NpcInstance(player, obj);
            }
            if ((handler == null) && obj.isPet()) {
                return act.OnActionShift_PetInstance(player,obj);
            }
            return false;
            //            return Strings.parseBoolean(Scripts.INSTANCE.callScripts(player, handler.className, handler.methodName, new Object[]{player, obj}));
        } else {
            handler = Scripts.onAction.get(obj.getL2ClassShortName());
                if ((handler == null) && obj.isDoor()) {
                    return act.OnActionShift_DoorInstance(player,obj);
                }
                if (handler == null) {
                    LOG.error("no handlers for: " + obj);
                    return false;
            }
            return Strings.parseBoolean(Scripts.INSTANCE.callScripts(player, handler.className, handler.methodName, new Object[]{player, obj}));
        }
    }
}