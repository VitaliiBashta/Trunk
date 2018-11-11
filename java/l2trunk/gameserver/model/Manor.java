package l2trunk.gameserver.model;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.instancemanager.CastleManorManager;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.templates.item.ItemTemplate;
import l2trunk.gameserver.templates.manor.CropProcure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

public final class Manor {
    private static final Logger _log = LoggerFactory.getLogger(Manor.class);
    private static Manor _instance;

    private static Map<Integer, SeedData> _seeds;

    private Manor() {
        _seeds = new ConcurrentHashMap<>();
        parseData();
    }

    public static Manor getInstance() {
        if (_instance == null)
            _instance = new Manor();
        return _instance;
    }

    public List<Integer> getAllCrops() {
        List<Integer> crops = new ArrayList<>();
        for (SeedData seed : _seeds.values())
            if (!crops.contains(seed.getCrop()) && seed.getCrop() != 0 && !crops.contains(seed.getCrop()))
                crops.add(seed.getCrop());
        return crops;
    }

    public Map<Integer, SeedData> getAllSeeds() {
        return _seeds;
    }

    public int getSeedBasicPrice(int seedId) {
        ItemTemplate seedItem = ItemHolder.getInstance().getTemplate(seedId);
        if (seedItem != null)
            return seedItem.getReferencePrice();
        return 0;
    }

    public int getSeedBasicPriceByCrop(int cropId) {
        for (SeedData seed : _seeds.values())
            if (seed.getCrop() == cropId)
                return getSeedBasicPrice(seed.getId());
        return 0;
    }

    public int getCropBasicPrice(int cropId) {
        ItemTemplate cropItem = ItemHolder.getInstance().getTemplate(cropId);
        if (cropItem != null)
            return cropItem.getReferencePrice();
        return 0;
    }

    public int getMatureCrop(int cropId) {
        for (SeedData seed : _seeds.values())
            if (seed.getCrop() == cropId)
                return seed.getMature();
        return 0;
    }

    /**
     * Returns price which lord pays to buy one seed
     */
    public long getSeedBuyPrice(int seedId) {
        long buyPrice = getSeedBasicPrice(seedId) / 10;
        return buyPrice >= 0 ? buyPrice : 1;
    }

    public int getSeedMinLevel(int seedId) {
        SeedData seed = _seeds.get(seedId);
        if (seed != null)
            return seed.getLevel() - 5;
        return -1;
    }

    public int getSeedMaxLevel(int seedId) {
        SeedData seed = _seeds.get(seedId);
        if (seed != null)
            return seed.getLevel() + 5;
        return -1;
    }

    public int getSeedLevelByCrop(int cropId) {
        for (SeedData seed : _seeds.values())
            if (seed.getCrop() == cropId)
                return seed.getLevel();
        return 0;
    }

    public int getSeedLevel(int seedId) {
        SeedData seed = _seeds.get(seedId);
        if (seed != null)
            return seed.getLevel();
        return -1;
    }

    public boolean isAlternative(int seedId) {
        for (SeedData seed : _seeds.values())
            if (seed.getId() == seedId)
                return seed.isAlternative();
        return false;
    }

    public int getCropType(int seedId) {
        SeedData seed = _seeds.get(seedId);
        if (seed != null)
            return seed.getCrop();
        return -1;
    }

    public synchronized int getRewardItem(int cropId, int type) {
        for (SeedData seed : _seeds.values())
            if (seed.getCrop() == cropId)
                return seed.getReward(type); // there can be several
        // seeds with same crop, but
        // reward should be the same for
        // all
        return -1;
    }

    public synchronized long getRewardAmountPerCrop(int castle, int cropId, int type) {
        final CropProcure cs = ResidenceHolder.getInstance().getResidence(Castle.class, castle).getCropProcure(CastleManorManager.PERIOD_CURRENT).get(cropId);
        for (SeedData seed : _seeds.values())
            if (seed.getCrop() == cropId)
                return cs.getPrice() / getCropBasicPrice(seed.getReward(type));
        return -1;
    }

    public synchronized int getRewardItemBySeed(int seedId, int type) {
        SeedData seed = _seeds.get(seedId);
        if (seed != null)
            return seed.getReward(type);
        return 0;
    }

    /**
     * Return all crops which can be purchased by given castle
     */
    public List<Integer> getCropsForCastle(int castleId) {
        List<Integer> crops = new ArrayList<>();
        for (SeedData seed : _seeds.values())
            if (seed.getManorId() == castleId && !crops.contains(seed.getCrop()))
                crops.add(seed.getCrop());
        return crops;
    }

    /**
     * Return list of seed ids, which belongs to castle with given id
     *
     * @param castleId - id of the castle
     * @return seedIds - list of seed ids
     */
    public List<Integer> getSeedsForCastle(int castleId) {
        List<Integer> seedsID = new ArrayList<>();
        for (SeedData seed : _seeds.values())
            if (seed.getManorId() == castleId && !seedsID.contains(seed.getId()))
                seedsID.add(seed.getId());
        return seedsID;
    }

    /**
     * Returns castle id where seed can be sowned<br>
     */
    public int getCastleIdForSeed(int seedId) {
        SeedData seed = _seeds.get(seedId);
        if (seed != null)
            return seed.getManorId();
        return 0;
    }

    public long getSeedSaleLimit(int seedId) {
        SeedData seed = _seeds.get(seedId);
        if (seed != null)
            return seed.getSeedLimit();
        return 0;
    }

    public long getCropPuchaseLimit(int cropId) {
        for (SeedData seed : _seeds.values())
            if (seed.getCrop() == cropId)
                return seed.getCropLimit();
        return 0;
    }

    private void parseData() {
        Path seedData = Config.DATAPACK_ROOT.resolve("data/seeds.csv");
        try (LineNumberReader lnr = new LineNumberReader(Files.newBufferedReader(seedData))) {
            String line;
            while ((line = lnr.readLine()) != null) {
                if (line.trim().length() == 0 || line.startsWith("#"))
                    continue;
                SeedData seed = parseList(line);
                _seeds.put(seed.getId(), seed);
            }

            _log.info("ManorManager: Loaded " + _seeds.size() + " seeds");
        } catch (FileNotFoundException e) {
            _log.info("seeds.csv is missing in data folder!", e);
        } catch (IOException e) {
            _log.error("Error while loading seeds!", e);
        }
    }

    private SeedData parseList(String line) {
        StringTokenizer st = new StringTokenizer(line, ";");

        int seedId = Integer.parseInt(st.nextToken()); // seed id
        int level = Integer.parseInt(st.nextToken()); // seed level
        int cropId = Integer.parseInt(st.nextToken()); // crop id
        int matureId = Integer.parseInt(st.nextToken()); // mature crop id
        int type1R = Integer.parseInt(st.nextToken()); // type I reward
        int type2R = Integer.parseInt(st.nextToken()); // type II reward
        int manorId = Integer.parseInt(st.nextToken()); // id of manor, where seed can be farmed
        int isAlt = Integer.parseInt(st.nextToken()); // alternative seed
        long limitSeeds = Math.round(Integer.parseInt(st.nextToken()) * Config.RATE_MANOR); // limit for seeds
        long limitCrops = Math.round(Integer.parseInt(st.nextToken()) * Config.RATE_MANOR); // limit for crops

        SeedData seed = new SeedData(level, cropId, matureId);
        seed.setData(seedId, type1R, type2R, manorId, isAlt, limitSeeds, limitCrops);

        return seed;
    }

    private class SeedData {
        private int id;
        private final int level; // seed level
        private final int crop; // crop type
        private final int mature; // mature crop type
        private int type1;
        private int type2;
        private int manorId; // id of manor (castle id) where seed can be farmed
        private int isAlternative;
        private long limitSeeds;
        private long limitCrops;

        SeedData(int level, int crop, int mature) {
            this.level = level;
            this.crop = crop;
            this.mature = mature;
        }

        void setData(int id, int t1, int t2, int manorId, int isAlt, long lim1, long lim2) {
            this.id = id;
            type1 = t1;
            type2 = t2;
            this.manorId = manorId;
            isAlternative = isAlt;
            limitSeeds = lim1;
            limitCrops = lim2;
        }

        int getManorId() {
            return manorId;
        }

        int getId() {
            return id;
        }

        int getCrop() {
            return crop;
        }

        int getMature() {
            return mature;
        }

        int getReward(int type) {
            return type == 1 ? type1 : type2;
        }

        int getLevel() {
            return level;
        }

        boolean isAlternative() {
            return isAlternative == 1;
        }

        long getSeedLimit() {
            return limitSeeds;
        }

        long getCropLimit() {
            return limitCrops;
        }
    }
}