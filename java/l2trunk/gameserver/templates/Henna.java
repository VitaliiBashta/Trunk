package l2trunk.gameserver.templates;

import l2trunk.gameserver.model.Player;

import java.util.List;

public final class Henna {
    public final int symbolId;
    public final int dyeId;
    public final long price;
    public final long drawCount;
    public final int statINT;
    public final int statSTR;
    public final int statCON;
    public final int statMEN;
    public final int statDEX;
    public final int statWIT;
    private final List<Integer> classes;

    public Henna(int symbolId, int dyeId, long price, long drawCount, int wit, int statINT, int con, int str, int dex, int men, List<Integer> classes) {
        this.symbolId = symbolId;
        this.dyeId = dyeId;
        this.price = price;
        this.drawCount = drawCount;
        this.statINT = statINT;
        statSTR = str;
        statCON = con;
        statMEN = men;
        statDEX = dex;
        statWIT = wit;
        this.classes = classes;
    }

    public int getSymbolId() {
        return symbolId;
    }

    public int getDyeId() {
        return dyeId;
    }

    public long getPrice() {
        return price;
    }

    public int getStatCON() {
        return statCON;
    }

    public int getStatMEN() {
        return statMEN;
    }

    public int getStatDEX() {
        return statDEX;
    }

    public int getStatWIT() {
        return statWIT;
    }

    public boolean isForThisClass(Player player) {
        return classes.contains(player.getActiveClassId());
    }

    public long getDrawCount() {
        return drawCount;
    }
}