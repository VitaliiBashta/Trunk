package l2trunk.scripts.events.TheFlowOfTheHorror;

import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.instancemanager.ServerVariables;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class TheFlowOfTheHorror extends Functions implements ScriptFile {
    private static final Logger LOG = LoggerFactory.getLogger(TheFlowOfTheHorror.class);
    private static final int Gilmore = 30754;
    private static final int Shackle = 20235;
    private static final List<MonsterInstance> _spawns = new ArrayList<>();
    private static final List<Location> points11 = new ArrayList<>();
    private static final List<Location> points12 = new ArrayList<>();
    private static final List<Location> points13 = new ArrayList<>();
    private static final List<Location> points21 = new ArrayList<>();
    private static final List<Location> points22 = new ArrayList<>();
    private static final List<Location> points23 = new ArrayList<>();
    private static final List<Location> points31 = new ArrayList<>();
    private static final List<Location> points32 = new ArrayList<>();
    private static final List<Location> points33 = new ArrayList<>();
    private static NpcInstance _oldGilmoreRef = null;
    private static int stage = 1;

    public static void spawnNewWave() {
        spawn(Shackle, points11);
        spawn(Shackle, points12);
        spawn(Shackle, points13);
        spawn(Shackle, points21);
        spawn(Shackle, points22);
        spawn(Shackle, points23);
        spawn(Shackle, points31);
        spawn(Shackle, points32);
        spawn(Shackle, points33);

        stage = 2;
    }

    private static void spawn(int id, List<Location> points) {
        NpcTemplate template = NpcHolder.getTemplate(id);
        MonsterInstance monster = new MonsterInstance(IdFactory.getInstance().getNextId(), template);
        monster.setFullHpMp();
        monster.setLoc(points.get(0));
        MonstersAI ai = new MonstersAI(monster);
        ai.setPoints(points);
        monster.setAI(ai);
        monster.spawnMe();
        _spawns.add(monster);
    }

    private static boolean isActive() {
        return ServerVariables.isSet("TheFlowOfTheHorror");
    }

    public static int getStage() {
        return stage;
    }

    @Override
    public void onLoad() {
        //Рукав 1, линия 1
        points11.add(Location.of(84211, 117965, -3020));
        points11.add(Location.of(83389, 117590, -3036));
        points11.add(Location.of(82226, 117051, -3150));
        points11.add(Location.of(80902, 116155, -3533));
        points11.add(Location.of(79832, 115784, -3733));
        points11.add(Location.of(78442, 116510, -3823));
        points11.add(Location.of(76299, 117355, -3786));
        points11.add(Location.of(74244, 117674, -3785));

        //Рукав 1, линия 2
        points12.add(Location.of(84231, 117597, -3020));
        points12.add(Location.of(82536, 116986, -3093));
        points12.add(Location.of(79428, 116341, -3749));
        points12.add(Location.of(76970, 117362, -3771));
        points12.add(Location.of(74322, 117845, -3767));

        //Рукав 1, линия 3
        points13.add(Location.of(83962, 118387, -3022));
        points13.add(Location.of(81960, 116925, -3216));
        points13.add(Location.of(80223, 116059, -3665));
        points13.add(Location.of(78214, 116783, -3854));
        points13.add(Location.of(76208, 117462, -3791));
        points13.add(Location.of(74278, 117454, -3804));

        //Рукав 2, линия 1
        points21.add(Location.of(79192, 111481, -3011));
        points21.add(Location.of(79014, 112396, -3090));
        points21.add(Location.of(79309, 113692, -3437));
        points21.add(Location.of(79350, 115337, -3758));
        points21.add(Location.of(78390, 116309, -3772));
        points21.add(Location.of(76794, 117092, -3821));
        points21.add(Location.of(74451, 117623, -3797));

        //Рукав 2, линия 2
        points22.add(Location.of(79297, 111456, -3017));
        points22.add(Location.of(79020, 112217, -3087));
        points22.add(Location.of(79167, 113236, -3289));
        points22.add(Location.of(79513, 115408, -3752));
        points22.add(Location.of(78555, 116816, -3812));
        points22.add(Location.of(76932, 117277, -3781));
        points22.add(Location.of(75422, 117788, -3755));
        points22.add(Location.of(74223, 117898, -3753));

        //Рукав 2, линия 3

        points23.add(Location.of(79635, 110741, -3003));
        points23.add(Location.of(78994, 111858, -3061));
        points23.add(Location.of(79088, 112949, -3226));
        points23.add(Location.of(79424, 114499, -3674));
        points23.add(Location.of(78913, 116266, -3779));
        points23.add(Location.of(76930, 117137, -3819));
        points23.add(Location.of(75533, 117569, -3781));
        points23.add(Location.of(74255, 117398, -3804));

        //Рукав 3, линия 1
        points31.add(Location.of(83128, 111358, -3663));
        points31.add(Location.of(81538, 111896, -3631));
        points31.add(Location.of(80312, 113837, -3752));
        points31.add(Location.of(79012, 115998, -3772));
        points31.add(Location.of(77377, 117052, -3812));
        points31.add(Location.of(75394, 117608, -3772));
        points31.add(Location.of(73998, 117647, -3784));

        //Рукав 3, линия 2
        points32.add(Location.of(83245, 110790, -3772));
        points32.add(Location.of(81832, 111379, -3641));
        points32.add(Location.of(81405, 112403, -3648));
        points32.add(Location.of(79827, 114496, -3752));
        points32.add(Location.of(78174, 116968, -3821));
        points32.add(Location.of(75944, 117653, -3777));
        points32.add(Location.of(74379, 117939, -3755));

        //Рукав 3, линия 3
        points33.add(Location.of(82584, 111930, -3568));
        points33.add(Location.of(81389, 111989, -3647));
        points33.add(Location.of(80129, 114044, -3748));
        points33.add(Location.of(79190, 115579, -3743));
        points33.add(Location.of(77989, 116811, -3849));
        points33.add(Location.of(76009, 117405, -3800));
        points33.add(Location.of(74113, 117441, -3797));

        if (isActive()) {
            activateAI();
            LOG.info("Loaded Event: The Flow Of The Horror [state: activated]");
        } else
            LOG.info("Loaded Event: The Flow Of The Horror [state: deactivated]");
    }

    private void activateAI() {
        NpcInstance target = GameObjectsStorage.getByNpcId(Gilmore);
        if (target != null) {
            _oldGilmoreRef = target;
            target.decayMe();

            NpcTemplate template = NpcHolder.getTemplate(Gilmore);
            MonsterInstance monster = new MonsterInstance(IdFactory.getInstance().getNextId(), template);
            monster.setFullHpMp();
            monster.setLoc(Location.of(73329, 117705, -3741));
            GilmoreAI ai = new GilmoreAI(monster);
            monster.setAI(ai);
            monster.spawnMe();
            _spawns.add(monster);
        }
    }

    private void deactivateAI() {
        _spawns.stream()
                .filter(Objects::nonNull)
                .forEach(GameObject::deleteMe);


        NpcInstance GilmoreInstance = _oldGilmoreRef;
        if (GilmoreInstance != null)
            GilmoreInstance.spawnMe();
    }

    public void startEvent() {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (!isActive()) {
            ServerVariables.set("TheFlowOfTheHorror");
            activateAI();
            System.out.println("Event 'The Flow Of The Horror' started.");
        } else
            player.sendMessage("Event 'The Flow Of The Horror' already started.");

        show("admin/events/events.htm", player);
    }

    public void stopEvent() {
        if (!player.getPlayerAccess().IsEventGm)
            return;
        if (isActive()) {
            ServerVariables.unset("TheFlowOfTheHorror");
            deactivateAI();
            System.out.println("Event 'The Flow Of The Horror' stopped.");
        } else
            player.sendMessage("Event 'The Flow Of The Horror' not started.");

        show("admin/events/events.htm", player);
    }

    @Override
    public void onReload() {
        deactivateAI();
    }

    @Override
    public void onShutdown() {
        deactivateAI();
    }


}