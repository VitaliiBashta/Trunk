package l2trunk.gameserver.utils;

import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.ExServerPrimitive;

import java.awt.*;

public class GeodataUtils {
    private static final byte EAST = 1;
    private static final byte WEST = 2;
    private static final byte SOUTH = 4;
    private static final byte NORTH = 8;

    private static Color getDirectionColor(int x, int y, int z, int geoIndex, byte NSWE) {
        if ((GeoEngine.getNSWE(x, y, z, geoIndex) & NSWE) != 0) {
            return Color.GREEN;
        }
        return Color.RED;
    }

    public static void debugGrid(Player player) {
        int geoRadius = 10;
        int blocksPerPacket = 10;

        int iBlock = blocksPerPacket;
        int iPacket = 0;

        ExServerPrimitive exsp = null;
        int playerGx = GeoEngine.getGeoX(player.getX());
        int playerGy = GeoEngine.getGeoY(player.getY());
        for (int dx = -geoRadius; dx <= geoRadius; ++dx) {
            for (int dy = -geoRadius; dy <= geoRadius; ++dy) {
                if (iBlock >= blocksPerPacket) {
                    iBlock = 0;
                    if (exsp != null) {
                        ++iPacket;
                        player.sendPacket(exsp);
                    }
                    exsp = new ExServerPrimitive("DebugGrid_" + iPacket, player.getX(), player.getY(), -16000);
                }

                int gx = playerGx + dx;
                int gy = playerGy + dy;

                int geoIndex = player.getReflection().getGeoIndex();
                int x = GeoEngine.getWorldX(gx);
                int y = GeoEngine.getWorldY(gy);
                int z = GeoEngine.getHeight(player.getLoc(), geoIndex);
                // north arrow
                Color col = getDirectionColor(x, y, z, geoIndex, NORTH);
                exsp.addLine(col, x - 1, y - 7, z, x + 1, y - 7, z);
                exsp.addLine(col, x - 2, y - 6, z, x + 2, y - 6, z);
                exsp.addLine(col, x - 3, y - 5, z, x + 3, y - 5, z);
                exsp.addLine(col, x - 4, y - 4, z, x + 4, y - 4, z);

                // east arrow
                col = getDirectionColor(x, y, z, geoIndex, EAST);
                exsp.addLine(col, x + 7, y - 1, z, x + 7, y + 1, z);
                exsp.addLine(col, x + 6, y - 2, z, x + 6, y + 2, z);
                exsp.addLine(col, x + 5, y - 3, z, x + 5, y + 3, z);
                exsp.addLine(col, x + 4, y - 4, z, x + 4, y + 4, z);

                // south arrow
                col = getDirectionColor(x, y, z, geoIndex, SOUTH);
                exsp.addLine(col, x - 1, y + 7, z, x + 1, y + 7, z);
                exsp.addLine(col, x - 2, y + 6, z, x + 2, y + 6, z);
                exsp.addLine(col, x - 3, y + 5, z, x + 3, y + 5, z);
                exsp.addLine(col, x - 4, y + 4, z, x + 4, y + 4, z);

                col = getDirectionColor(x, y, z, geoIndex, WEST);
                exsp.addLine(col, x - 7, y - 1, z, x - 7, y + 1, z);
                exsp.addLine(col, x - 6, y - 2, z, x - 6, y + 2, z);
                exsp.addLine(col, x - 5, y - 3, z, x - 5, y + 3, z);
                exsp.addLine(col, x - 4, y - 4, z, x - 4, y + 4, z);

                ++iBlock;
            }
        }

        player.sendPacket(exsp);
    }
}