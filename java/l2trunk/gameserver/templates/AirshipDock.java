package l2trunk.gameserver.templates;

import l2trunk.gameserver.model.entity.events.objects.BoatPoint;
import l2trunk.gameserver.network.serverpackets.components.SceneMovie;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class AirshipDock {
    private final int id;
    private List<BoatPoint> teleportList;
    private List<AirshipPlatform> platformList;

    public AirshipDock(int id, List<BoatPoint> teleport, List<AirshipPlatform> platformList) {
        this.id = id;
        teleportList = teleport;
        this.platformList = platformList;
    }

    public int getId() {
        return id;
    }

    public List<BoatPoint> getTeleportList() {
        return teleportList;
    }

    public AirshipPlatform getPlatform(int id) {
        return platformList.get(id);
    }

    public static class AirshipPlatform {
        private final SceneMovie _oustMovie;
        private final Location _oustLoc;
        private final Location _spawnLoc;
        private List<BoatPoint> _arrivalPoints;
        private List<BoatPoint> _departPoints;

        public AirshipPlatform(SceneMovie movie, Location oustLoc, Location spawnLoc, List<BoatPoint> arrival, List<BoatPoint> depart) {
            _oustMovie = movie;
            _oustLoc = oustLoc;
            _spawnLoc = spawnLoc;
            _arrivalPoints = arrival;
            _departPoints = depart;
        }

        public SceneMovie getOustMovie() {
            return _oustMovie;
        }

        public Location getOustLoc() {
            return _oustLoc;
        }

        public Location getSpawnLoc() {
            return _spawnLoc;
        }

        public List<BoatPoint> getArrivalPoints() {
            return _arrivalPoints;
        }

        public List<BoatPoint> getDepartPoints() {
            return _departPoints;
        }
    }
}
