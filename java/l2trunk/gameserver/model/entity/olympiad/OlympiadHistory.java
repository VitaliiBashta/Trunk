package l2trunk.gameserver.model.entity.olympiad;

import l2trunk.gameserver.data.StringHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.utils.TimeUtils;

public final class OlympiadHistory {
    public final int objectId1;
    public final int objectId2;

    public final int classId1;
    public final int classId2;

    public final String name1;
    public final String name2;

    public final long gameStartTime;
    public final int gameTime;
    public final int gameStatus; // 1 - выиграл 1, 2 выиграл 2, 0 - "устали"
    public final int gameType;

    public OlympiadHistory(int objectId1, int objectId2, int classId1, int classId2, String name1, String name2, long gameStartTime, int gameTime, int gameStatus, int gameType) {
        this.objectId1 = objectId1;
        this.objectId2 = objectId2;

        this.classId1 = classId1;
        this.classId2 = classId2;

        this.name1 = name1;
        this.name2 = name2;

        this.gameStartTime = gameStartTime;
        this.gameTime = gameTime;
        this.gameStatus = gameStatus;
        this.gameType = gameType;
    }

    public String toString(Player player, int target, int wins, int loss, int tie) {
        int team = objectId1 == target ? 1 : 2;
        String main;
        if (gameStatus == 0)
            main = StringHolder.INSTANCE.getNotNull(player, "hero.history.tie");
        else if (team == gameStatus)
            main = StringHolder.INSTANCE.getNotNull(player, "hero.history.win");
        else
            main = StringHolder.INSTANCE.getNotNull(player, "hero.history.loss");

        main = main.replace("%classId%", String.valueOf(team == 1 ? classId2 : classId1));
        main = main.replace("%name%", team == 1 ? name2 : name1);
        main = main.replace("%date%", TimeUtils.toSimpleFormat(gameStartTime));
        int m = gameTime / 60;
        int s = gameTime % 60;
        main = main.replace("%time%", (m <= 9 ? "0" : "") + m + ":" + (s <= 9 ? "0" : "") + s);
        main = main.replace("%victory_count%", String.valueOf(wins));
        main = main.replace("%tie_count%", String.valueOf(tie));
        main = main.replace("%loss_count%", String.valueOf(loss));
        return main;
    }

}