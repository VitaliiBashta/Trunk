package l2trunk.gameserver.model.actor.instances.player;

import l2trunk.gameserver.utils.Location;

public final class BookMark {
    public final Location loc;
    private int icon;
    private String name, acronym;

    public BookMark(Location loc, int aicon, String aname, String aacronym) {
        this.loc = loc;
        this.setIcon(aicon)
                .setName(aname)
                .setAcronym(aacronym);
    }

    public int getIcon() {
        return icon;
    }

    public BookMark setIcon(int val) {
        icon = val;
        return this;
    }

    public String getName() {
        return name;
    }

    public BookMark setName(String val) {
        name = val.length() > 32 ? val.substring(0, 32) : val;
        return this;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String val) {
        acronym = val.length() > 4 ? val.substring(0, 4) : val;
    }
}