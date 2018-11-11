package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Player;

/**
 * @author VISTALL
 */
public class RequestExBR_LectureMark extends L2GameClientPacket {
    private static final int INITIAL_MARK = 1;
    private static final int EVANGELIST_MARK = 2;
    private static final int OFF_MARK = 3;

    private int _mark;

    @Override
    protected void readImpl() {
        _mark = readC();
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if ((player == null) || !Config.EX_LECTURE_MARK) {
            return;
        }

        switch (_mark) {
            case INITIAL_MARK:
            case EVANGELIST_MARK:
            case OFF_MARK:
                // TODO [VISTALL]check whether you can include - from the first 6 months of the chara
                player.setLectureMark(_mark);
                player.broadcastUserInfo(true);
                break;
        }
    }
}