package l2trunk.gameserver.scripts;

import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.scripts.Scripts.ScriptClassAndMethod;
import l2trunk.gameserver.utils.Strings;
import l2trunk.scripts.actions.OnActionShift;

public final class Events {
    public static boolean onAction(Player player, GameObject obj, boolean shift) {
        OnActionShift act = new OnActionShift();
        if (shift) {
            if (player.getVarB("noShift")) {
                return false;
            }
            ScriptClassAndMethod handler = Scripts.onActionShift.get(obj.getL2ClassShortName());
            if ((handler == null) && obj.isNpc()) {
//                handler = Scripts.onActionShift.get("NpcInstance");
                return act.OnActionShift_NpcInstance(player, obj);
            }
            if ((handler == null) && obj.isPet()) {
//                handler = Scripts.onActionShift.get("PetInstance");
                return act.OnActionShift_PetInstance(player,obj);
            }
            if (handler == null) {
                return false;
            }
            return Strings.parseBoolean(Scripts.INSTANCE.callScripts(player, handler.className, handler.methodName, new Object[]{player, obj}));
        } else {
            ScriptClassAndMethod handler = Scripts.onAction.get(obj.getL2ClassShortName());
            if ((handler == null) && obj.isDoor()) {
//                handler = Scripts.onAction.get("DoorInstance");
                return act.OnActionShift_DoorInstance(player,obj);
            }
            if (handler == null) {
                return false;
            }
            return Strings.parseBoolean(Scripts.INSTANCE.callScripts(player, handler.className, handler.methodName, new Object[]{player, obj}));
        }
    }
}