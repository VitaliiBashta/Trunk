package l2trunk.gameserver.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ManageBbsBuffer {
    @SuppressWarnings("unused")
    private static final Logger _log = LoggerFactory.getLogger(ManageBbsBuffer.class);

    private static final ManageBbsBuffer _instance = new ManageBbsBuffer();
    private final List<SBufferScheme> listScheme;

    private ManageBbsBuffer() {
        this.listScheme = new ArrayList<>();
    }

    private static ManageBbsBuffer getInstance() {
        return _instance;
    }

    public static SBufferScheme getScheme(int id, int obj_id) {
        for (SBufferScheme scheme : getInstance().listScheme)
            if (scheme.id == id && scheme.obj_id == obj_id)
                return scheme;
        return null;
    }

    private static int getAutoIncrement(int ain) {
        int count = 0;
        for (SBufferScheme scheme : getInstance().listScheme)
            if (ain == scheme.id)
                count++;
        if (count == 0)
            return ain;
        return getAutoIncrement(ain + 1);
    }

    public static List<Integer> StringToInt(String list) {
        List<Integer> skills_id = new ArrayList<>();

        String[] s_id = list.split(";");

        for (String aS_id : s_id) skills_id.add(Integer.parseInt(aS_id));
        return skills_id;
    }

    public static String IntToString(List<Integer> id) {
        String buff_list = "";
        for (Integer anId : id) buff_list = buff_list + new StringBuilder().append(anId).append(";").toString();
        return buff_list;
    }

    public static List<SBufferScheme> getSchemeList() {
        return getInstance().listScheme;
    }

    public static int getCountOnePlayer(int obj_id) {
        int count = 0;
        for (SBufferScheme scheme : getInstance().listScheme)
            if (obj_id == scheme.obj_id)
                count++;
        return count;
    }

    public static boolean existName(int obj_id, String name) {
        for (SBufferScheme scheme : getInstance().listScheme)
            if (obj_id == scheme.obj_id && name == scheme.name)
                return true;
        return false;
    }

    public static List<SBufferScheme> getSchemePlayer(int obj_id) {
        List<SBufferScheme> list = new ArrayList<>();
        for (SBufferScheme sm : getInstance().listScheme)
            if (sm.obj_id == obj_id)
                list.add(sm);
        return list;
    }

    public static class SBufferScheme {
        final int id;
        final int obj_id;
        final String name;
        final List<Integer> skills_id;

        public SBufferScheme() {
            this.id = 0;
            this.obj_id = 0;
            this.name = "";
            this.skills_id = new ArrayList<>();
        }
    }
}