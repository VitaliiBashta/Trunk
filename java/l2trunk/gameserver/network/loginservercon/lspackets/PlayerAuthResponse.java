package l2trunk.gameserver.network.loginservercon.lspackets;

import javafx.util.Pair;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.dao.AccountBonusDAO;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.actor.instances.player.Bonus;
import l2trunk.gameserver.network.GameClient;
import l2trunk.gameserver.network.loginservercon.AuthServerCommunication;
import l2trunk.gameserver.network.loginservercon.ReceivablePacket;
import l2trunk.gameserver.network.loginservercon.SessionKey;
import l2trunk.gameserver.network.loginservercon.gspackets.PlayerInGame;
import l2trunk.gameserver.network.serverpackets.CharacterSelectionInfo;
import l2trunk.gameserver.network.serverpackets.LoginFail;
import l2trunk.gameserver.network.serverpackets.ServerClose;

public class PlayerAuthResponse extends ReceivablePacket {
    private String account;
    private boolean authed;
    private int playOkId1;
    private int playOkId2;
    private int loginOkId1;
    private int loginOkId2;
    private double bonus;
    private int bonusExpire;

    @Override
    public void readImpl() {
        account = readS();
        authed = readC() == 1;
        if (authed) {
            playOkId1 = readD();
            playOkId2 = readD();
            loginOkId1 = readD();
            loginOkId2 = readD();
            bonus = readF();
            bonusExpire = readD();
        }
        String hwid = readS();
    }

    @Override
    protected void runImpl() {
        SessionKey skey = new SessionKey(loginOkId1, loginOkId2, playOkId1, playOkId2);
        GameClient client = AuthServerCommunication.getInstance().removeWaitingClient(account);
        if (client == null)
            return;

        if (authed && client.getSessionKey().equals(skey)) {
            client.setAuthed(true);
            client.setState(GameClient.GameClientState.AUTHED);
            switch (Config.SERVICES_RATE_TYPE) {
                case Bonus.NO_BONUS:
                    bonus = 0;
                    bonusExpire = 0;
                    break;
                case Bonus.BONUS_GLOBAL_ON_GAMESERVER:
                    Pair<Integer, Integer> bonuses = AccountBonusDAO.getInstance().getBonuses(account);
                    bonus = bonuses.getKey();
                    bonusExpire = bonuses.getValue();
                    break;
            }
            client.setBonus((int) bonus);
            client.setBonusExpire(bonusExpire);

            GameClient oldClient = AuthServerCommunication.getInstance().addAuthedClient(client);
            if (oldClient != null) {
                oldClient.setAuthed(false);
                Player activeChar = oldClient.getActiveChar();
                if (activeChar != null) {
                    activeChar.sendPacket(Msg.ANOTHER_PERSON_HAS_LOGGED_IN_WITH_THE_SAME_ACCOUNT);
                    activeChar.logout();
                } else {
                    oldClient.close(ServerClose.STATIC);
                }
            }

            sendPacket(new PlayerInGame(client.getLogin()));

            CharacterSelectionInfo csi = new CharacterSelectionInfo(client.getLogin(), client.getSessionKey().playOkID1);
            client.sendPacket(csi);
            client.setCharSelection(csi.getCharInfo());
            //client.checkHwid(hwid);
        } else {
            client.close(new LoginFail(LoginFail.ACCESS_FAILED_TRY_LATER));
        }
    }
}