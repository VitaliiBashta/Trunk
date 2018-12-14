package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.network.serverpackets.components.NpcString;

public class ExShowScreenMessage extends NpcStringContainer {
    public static final int SYSMSG_TYPE = 0;
    private static final int STRING_TYPE = 1;
    private final int type, sysMessageId;
    private final boolean bigFont, effect;
    private final ScreenMessageAlign textAlign;
    private final int time;

    public ExShowScreenMessage(String text ) {
        this(text, 5000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, false);
    }
    public ExShowScreenMessage(String text, int time ) {
        this(text, time, ScreenMessageAlign.TOP_CENTER, true, 1, -1, false);
    }

    public ExShowScreenMessage(String text, int time,  boolean big_font) {
        this(text, time, ScreenMessageAlign.TOP_CENTER, big_font, 1, -1, false);
    }

//    public ExShowScreenMessage(String text, int time, ScreenMessageAlign text_align, boolean big_font) {
//        this(text, time, text_align, big_font, 1, -1, false);
//    }

    public ExShowScreenMessage(String text, int time, ScreenMessageAlign text_align, boolean big_font) {
        this(text, time, text_align, big_font, 1, -1, false);
    }

    private ExShowScreenMessage(String text, int time, ScreenMessageAlign text_align, boolean big_font, int type, int messageId, boolean showEffect) {
        super(NpcString.NONE, text);
        this.type = type;
        sysMessageId = messageId;
        this.time = time;
        textAlign = text_align;
        bigFont = big_font;
        effect = showEffect;
    }

    public ExShowScreenMessage(NpcString t,  String... params) {
        this(t, 5000, ScreenMessageAlign.TOP_CENTER, true, STRING_TYPE, -1, false, params);
    }

    public ExShowScreenMessage(NpcString t, int time,  String... params) {
        this(t, time, ScreenMessageAlign.TOP_CENTER, true, STRING_TYPE, -1, false, params);
    }


    public ExShowScreenMessage(NpcString npcString, int time, ScreenMessageAlign text_align, boolean big_font, String... params) {
        this(npcString, time, text_align, big_font, STRING_TYPE, -1, false, params);
    }


    public ExShowScreenMessage(NpcString npcString, int time, ScreenMessageAlign text_align, boolean big_font, boolean showEffect, String... params) {
        this(npcString, time, text_align, big_font, STRING_TYPE, -1, showEffect, params);
    }

    public ExShowScreenMessage(NpcString npcString, int time, ScreenMessageAlign textAlign, boolean bigFont, int type, int systemMsg, boolean showEffect, String... params) {
        super(npcString, params);
        this.type = type;
        sysMessageId = systemMsg;
        this.time = time;
        this.textAlign = textAlign;
        this.bigFont = bigFont;
        effect = showEffect;
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x39);
        writeD(type); // 0 - system messages, 1 - your defined text
        writeD(sysMessageId); // system message id (type must be 0 otherwise no effect)
        writeD(textAlign.ordinal() + 1); // размещение текста
        writeD(0x00); // ?
        writeD(bigFont ? 0 : 1); // размер текста
        writeD(0x00); // ?
        writeD(0x00); // ?
        writeD(effect ? 1 : 0); // upper effect (0 - disabled, 1 enabled) - _position must be 2 (center) otherwise no effect
        writeD(time); // время отображения сообщения в милисекундах
        writeD(0x01); // ?
        writeElements();
    }

    public enum ScreenMessageAlign {
        TOP_LEFT,
        TOP_CENTER,
        MIDDLE_CENTER,
        BOTTOM_RIGHT,
    }
}