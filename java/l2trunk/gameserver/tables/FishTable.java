package l2trunk.gameserver.tables;

import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.reward.RewardData;
import l2trunk.gameserver.templates.FishTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public enum FishTable {
    INSTANCE;
    private static final Logger LOG = LoggerFactory.getLogger(FishTable.class);

    private final Map<Integer, List<FishTemplate>> fishes = new HashMap<>();
    private final Map<Integer, List<RewardData>> fishRewards = new HashMap<>();


    public void init() {
        load();
    }

    public void reload() {
        load();
    }

    private void load() {
        int count = 0;
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            PreparedStatement statement = con.prepareStatement("SELECT id, level, name, hp, hpregen, fish_type, fish_group, fish_guts, guts_check_time, wait_time, combat_time FROM fish ORDER BY id");
            ResultSet resultSet = statement.executeQuery();

            FishTemplate fish;
            List<FishTemplate> fishes;
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int lvl = resultSet.getInt("level");
                int hp = resultSet.getInt("hp");
                int hpreg = resultSet.getInt("hpregen");
                int type = resultSet.getInt("fish_type");
                int group = resultSet.getInt("fish_group");
                int fish_guts = resultSet.getInt("fish_guts");
                int guts_check_time = resultSet.getInt("guts_check_time");
                int wait_time = resultSet.getInt("wait_time");
                int combat_time = resultSet.getInt("combat_time");

                fish = new FishTemplate(id, lvl, hp, hpreg, type, group, fish_guts, guts_check_time, wait_time, combat_time);
                if ((fishes = this.fishes.get(group)) == null)
                    this.fishes.put(group, fishes = new ArrayList<>());
                fishes.add(fish);
                count++;
            }

            LOG.info("FishTable: Loaded " + count + " fishes.");

            count = 0;

            statement = con.prepareStatement("SELECT fishid, rewardid, min, max, chance FROM fishreward ORDER BY fishid");
            resultSet = statement.executeQuery();

            RewardData reward;
            List<RewardData> rewards;
            while (resultSet.next()) {
                int fishid = resultSet.getInt("fishid");
                int rewardid = resultSet.getInt("rewardid");
                int mindrop = resultSet.getInt("min");
                int maxdrop = resultSet.getInt("max");
                int chance = resultSet.getInt("chance");

                reward = new RewardData(rewardid, mindrop, maxdrop, chance * 10000.);
                if ((rewards = fishRewards.get(fishid)) == null)
                    fishRewards.put(fishid, rewards = new ArrayList<>());

                rewards.add(reward);
                count++;
            }

            LOG.info("FishTable: Loaded " + count + " fish rewards.");
        } catch (SQLException e) {
            LOG.error("Error while loading Fishes", e);
        }
    }

    public Set<Integer> getFishIds() {
        return fishRewards.keySet();
    }

    public List<FishTemplate> getFish(int group, int type, int lvl) {
        List<FishTemplate> fishs = fishes.get(group);
        if (fishs == null) {
            LOG.warn("No fishes defined for group : " + group + "!");
            return null;
        }

        List<FishTemplate> result = fishs.stream()
                .filter(f -> f.type == type)
                .filter(f -> f.level == lvl)
                .collect(Collectors.toList());

        if (result.isEmpty())
            LOG.warn("No fishes for group : " + group + " type: " + type + " occupation: " + lvl + "!");

        return result;
    }

    public List<RewardData> getFishReward(int fishid) {
        List<RewardData> result = fishRewards.get(fishid);

        if (result.isEmpty())
            LOG.warn("No fish rewards for fish id: " + fishid + "!");

        return result;
    }
}