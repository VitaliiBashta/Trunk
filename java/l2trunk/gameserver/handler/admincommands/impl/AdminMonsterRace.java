package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.MonsterRace;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.DeleteObject;
import l2trunk.gameserver.network.serverpackets.MonRaceInfo;
import l2trunk.gameserver.network.serverpackets.PlaySound;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.utils.Location;

public class AdminMonsterRace implements IAdminCommandHandler {
    private static int state = -1;

    @Override
    public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
        Commands command = (Commands) comm;

        if (fullString.equalsIgnoreCase("admin_mons")) {
            if (!activeChar.getPlayerAccess().MonsterRace)
                return false;
            handleSendPacket(activeChar);
        }

        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private void handleSendPacket(Player activeChar) {
        /*
         * -1 0 to initial the race 0 15322 to start race 13765 -1 in middle of race
         * -1 0 to end the race
         *
         * 8003 to 8027
         */

        int[][] codes = {{-1, 0}, {0, 15322}, {13765, -1}, {-1, 0}};
        MonsterRace race = MonsterRace.INSTANCE;

        if (state == -1) {
            state++;
            race.newRace();
            race.newSpeeds();
            activeChar.broadcastPacket(new MonRaceInfo(codes[state][0], codes[state][1], race.getMonsters(), race.getSpeeds()));
        } else if (state == 0) {
            state++;
            activeChar.sendPacket(SystemMsg.THEYRE_OFF);
            activeChar.broadcastPacket(new PlaySound("S_Race"));
            //TODO исправить 121209259 - обжект айди, ток неизвестно какого обьекта (VISTALL)
            activeChar.broadcastPacket(new PlaySound(PlaySound.Type.SOUND, "ItemSound2.race_start", 1, 121209259, new Location(12125, 182487, -3559)));
            activeChar.broadcastPacket(new MonRaceInfo(codes[state][0], codes[state][1], race.getMonsters(), race.getSpeeds()));

            ThreadPoolManager.INSTANCE.schedule(new RunRace(codes, activeChar), 5000);
        }
    }

    private enum Commands {
        admin_mons
    }

    class RunRace extends RunnableImpl {
        private final int[][] codes;
        private final Player activeChar;

        RunRace(int[][] codes, Player activeChar) {
            this.codes = codes;
            this.activeChar = activeChar;
        }

        @Override
        public void runImpl() {
            // int[][] speeds1 = MonsterRace.INSTANCE().getSpeeds();
            // MonsterRace.INSTANCE().newSpeeds();
            // int[][] speeds2 = MonsterRace.INSTANCE().getSpeeds();
            /*
             * int[] speed = new int[8]; for(int i=0; i<8; i++) { for(int j=0; j<20;
             * j++) { //_log.info.println("Adding "+speeds1[i][j] +" and "+
             * speeds2[i][j]); speed[i] += (speeds1[i][j]*1);// + (speeds2[i][j]*1); }
             * _log.info.println("Total speed for "+(i+1)+" = "+speed[i]); }
             */

            activeChar.broadcastPacket(new MonRaceInfo(codes[2][0], codes[2][1], MonsterRace.INSTANCE.getMonsters(), MonsterRace.INSTANCE.getSpeeds()));
            ThreadPoolManager.INSTANCE.schedule(new RunEnd(activeChar), 30000);
        }
    }

    class RunEnd extends RunnableImpl {
        private final Player activeChar;

        RunEnd(Player activeChar) {
            this.activeChar = activeChar;
        }

        @Override
        public void runImpl() {
            NpcInstance obj;

            for (int i = 0; i < 8; i++) {
                obj = MonsterRace.INSTANCE.getMonsters()[i];
                // FIXME i don't know, if it's needed (Styx)
                // L2World.removeObject(obj);
                activeChar.broadcastPacket(new DeleteObject(obj));

            }
            state = -1;
        }
    }
}