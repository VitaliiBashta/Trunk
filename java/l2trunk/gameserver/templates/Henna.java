package l2trunk.gameserver.templates;

import l2trunk.gameserver.model.Player;

import java.util.List;

public final class Henna {
    private final int symbolId;
    private final int dyeId;
    private final long price;
    private final long drawCount;
    private final int statINT;
    private final int statSTR;
    private final int statCON;
    private final int statMEN;
    private final int statDEX;
    private final int statWIT;
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

    public int getStatINT() {
        return statINT;
    }

    public int getStatSTR() {
        return statSTR;
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