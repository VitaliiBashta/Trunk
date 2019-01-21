package l2trunk.gameserver.model.entity;

import l2trunk.commons.lang.Pair;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.utils.HtmlUtils;

import java.text.SimpleDateFormat;

public final class HeroDiary {
    public static final int ACTION_RAID_KILLED = 1;
    public static final int ACTION_HERO_GAINED = 2;
    public static final int ACTION_CASTLE_TAKEN = 3;
    private static final SimpleDateFormat SIMPLE_FORMAT = new SimpleDateFormat("HH:** dd.MM.yyyy");
    private final int _id;
    private final long _time;
    private final int _param;

    public HeroDiary(int id, long time, int param) {
        _id = id;
        _time = time;
        _param = param;
    }

    public Pair<String, String> toString(Player player) {
        CustomMessage message;
        switch (_id) {
            case ACTION_RAID_KILLED:
                message = new CustomMessage("l2trunk.gameserver.model.entity.Hero.RaidBossKilled", player).addString(HtmlUtils.htmlNpcName(_param));
                break;
            case ACTION_HERO_GAINED:
                message = new CustomMessage("l2trunk.gameserver.model.entity.Hero.HeroGained", player);
                break;
            case ACTION_CASTLE_TAKEN:
                message = new CustomMessage("l2trunk.gameserver.model.entity.Hero.CastleTaken", player).addString(HtmlUtils.htmlResidenceName(_param));
                break;
            default:
                return null;
        }

        return new Pair<>(SIMPLE_FORMAT.format(_time), message.toString());
    }
}
