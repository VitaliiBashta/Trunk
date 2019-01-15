package l2trunk.gameserver.model.entity.SevenSignsFestival;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class FestivalSpawn {
    static final List<List<NpcLocation>> FESTIVAL_DUSK_PRIMARY_SPAWNS = List.of(
            /* Level 31 and Below - Offering of the Branded */
            List.of(
                    new NpcLocation(-76542, 89653, -5151, -1, 18009),
                    new NpcLocation(-76509, 89637, -5151, -1, 18010),
                    new NpcLocation(-76548, 89614, -5151, -1, 18010),
                    new NpcLocation(-76539, 88326, -5151, -1, 18009),
                    new NpcLocation(-76512, 88289, -5151, -1, 18010),
                    new NpcLocation(-76546, 88287, -5151, -1, 18010),
                    new NpcLocation(-77879, 88308, -5151, -1, 18012),
                    new NpcLocation(-77886, 88310, -5151, -1, 18013),
                    new NpcLocation(-77879, 88278, -5151, -1, 18014),
                    new NpcLocation(-77857, 89605, -5151, -1, 18015),
                    new NpcLocation(-77858, 89658, -5151, -1, 18017),
                    new NpcLocation(-77891, 89633, -5151, -1, 18018),
                    // Archers and Marksmen
                    new NpcLocation(-76728, 88962, -5151, -1, 18011),
                    new NpcLocation(-77194, 88494, -5151, -1, 18011),
                    new NpcLocation(-77660, 88896, -5151, -1, 18016),
                    new NpcLocation(-77195, 89438, -5151, -1, 18016)),
            /* Level 42 and Below - Apostate's Offering */
            List.of(
                    new NpcLocation(-77585, 84650, -5151, -1, 18019),
                    new NpcLocation(-77628, 84643, -5151, -1, 18020),
                    new NpcLocation(-77607, 84613, -5151, -1, 18020),
                    new NpcLocation(-76603, 85946, -5151, -1, 18019),
                    new NpcLocation(-77606, 85994, -5151, -1, 18020),
                    new NpcLocation(-77638, 85959, -5151, -1, 18020),
                    new NpcLocation(-76301, 85960, -5151, -1, 18022),
                    new NpcLocation(-76257, 85972, -5151, -1, 18023),
                    new NpcLocation(-76286, 85992, -5151, -1, 18024),
                    new NpcLocation(-76281, 84667, -5151, -1, 18025),
                    new NpcLocation(-76291, 84611, -5151, -1, 18027),
                    new NpcLocation(-76257, 84616, -5151, -1, 18028),
                    // Archers and Marksmen
                    new NpcLocation(-77419, 85307, -5151, -1, 18021),
                    new NpcLocation(-76952, 85768, -5151, -1, 18021),
                    new NpcLocation(-76477, 85312, -5151, -1, 18026),
                    new NpcLocation(-76942, 84832, -5151, -1, 18026)),
            /* Level 53 and Below - Witch's Offering */
            List.of(
                    new NpcLocation(-74211, 86494, -5151, -1, 18029),
                    new NpcLocation(-74200, 86449, -5151, -1, 18030),
                    new NpcLocation(-74167, 86464, -5151, -1, 18030),
                    new NpcLocation(-75495, 86482, -5151, -1, 18029),
                    new NpcLocation(-75540, 86473, -5151, -1, 18030),
                    new NpcLocation(-75509, 86445, -5151, -1, 18030),
                    new NpcLocation(-75509, 87775, -5151, -1, 18032),
                    new NpcLocation(-75518, 87826, -5151, -1, 18033),
                    new NpcLocation(-75542, 87780, -5151, -1, 18034),
                    new NpcLocation(-74214, 87789, -5151, -1, 18035),
                    new NpcLocation(-74169, 87801, -5151, -1, 18037),
                    new NpcLocation(-74198, 87827, -5151, -1, 18038),
                    // Archers and Marksmen
                    new NpcLocation(-75324, 87135, -5151, -1, 18031),
                    new NpcLocation(-74852, 87606, -5151, -1, 18031),
                    new NpcLocation(-74388, 87146, -5151, -1, 18036),
                    new NpcLocation(-74856, 86663, -5151, -1, 18036)),
            /* Level 64 and Below - Dark Omen Offering */
            List.of(
                    new NpcLocation(-79560, 89007, -5151, -1, 18039),
                    new NpcLocation(-79521, 89016, -5151, -1, 18040),
                    new NpcLocation(-79544, 89047, -5151, -1, 18040),
                    new NpcLocation(-79552, 87717, -5151, -1, 18039),
                    new NpcLocation(-79552, 87673, -5151, -1, 18040),
                    new NpcLocation(-79510, 87702, -5151, -1, 18040),
                    new NpcLocation(-80866, 87719, -5151, -1, 18042),
                    new NpcLocation(-80897, 87689, -5151, -1, 18043),
                    new NpcLocation(-80850, 87685, -5151, -1, 18044),
                    new NpcLocation(-80848, 89013, -5151, -1, 18045),
                    new NpcLocation(-80887, 89051, -5151, -1, 18047),
                    new NpcLocation(-80891, 89004, -5151, -1, 18048),
                    // Archers and Marksmen
                    new NpcLocation(-80205, 87895, -5151, -1, 18041),
                    new NpcLocation(-80674, 88350, -5151, -1, 18041),
                    new NpcLocation(-80209, 88833, -5151, -1, 18046),
                    new NpcLocation(-79743, 88364, -5151, -1, 18046)),
            /* No Level Limit - Offering of Forbidden Path */
            List.of(
                    new NpcLocation(-80624, 84060, -5151, -1, 18049),
                    new NpcLocation(-80621, 84007, -5151, -1, 18050),
                    new NpcLocation(-80590, 84039, -5151, -1, 18050),
                    new NpcLocation(-80605, 85349, -5151, -1, 18049),
                    new NpcLocation(-80639, 85363, -5151, -1, 18050),
                    new NpcLocation(-80611, 85385, -5151, -1, 18050),
                    new NpcLocation(-79311, 85353, -5151, -1, 18052),
                    new NpcLocation(-79277, 85384, -5151, -1, 18053),
                    new NpcLocation(-79273, 85539, -5151, -1, 18054),
                    new NpcLocation(-79297, 84054, -5151, -1, 18055),
                    new NpcLocation(-79285, 84006, -5151, -1, 18057),
                    new NpcLocation(-79260, 84040, -5151, -1, 18058),
                    // Archers and Marksmen
                    new NpcLocation(-79945, 85171, -5151, -1, 18051),
                    new NpcLocation(-79489, 84707, -5151, -1, 18051),
                    new NpcLocation(-79952, 84222, -5151, -1, 18056),
                    new NpcLocation(-80423, 84703, -5151, -1, 18056)));
    static final List<List<NpcLocation>> FESTIVAL_DAWN_SECONDARY_SPAWNS = List.of(
            /* 31 and Below */
            List.of(
                    new NpcLocation(-78757, 112834, -4895, -1, 18016),
                    new NpcLocation(-78581, 112834, -4895, -1, 18016),
                    new NpcLocation(-78822, 112526, -4895, -1, 18011),
                    new NpcLocation(-78822, 113702, -4895, -1, 18011),
                    new NpcLocation(-78822, 113874, -4895, -1, 18011),
                    new NpcLocation(-79524, 113546, -4895, -1, 18011),
                    new NpcLocation(-79693, 113546, -4895, -1, 18011),
                    new NpcLocation(-79858, 113546, -4895, -1, 18011),
                    new NpcLocation(-79545, 112757, -4895, -1, 18016),
                    new NpcLocation(-79545, 112586, -4895, -1, 18016)),
            /* 42 and Below */
            List.of(
                    new NpcLocation(-75565, 110580, -4895, -1, 18026),
                    new NpcLocation(-75565, 110740, -4895, -1, 18026),
                    new NpcLocation(-75577, 109776, -4895, -1, 18021),
                    new NpcLocation(-75413, 109776, -4895, -1, 18021),
                    new NpcLocation(-75237, 109776, -4895, -1, 18021),
                    new NpcLocation(-76274, 109468, -4895, -1, 18021),
                    new NpcLocation(-76274, 109635, -4895, -1, 18021),
                    new NpcLocation(-76274, 109795, -4895, -1, 18021),
                    new NpcLocation(-76351, 110500, -4895, -1, 18056),
                    new NpcLocation(-76528, 110500, -4895, -1, 18056)),
            /* 53 and Below */
            List.of(
                    new NpcLocation(-74191, 111527, -4895, -1, 18036),
                    new NpcLocation(-74191, 111362, -4895, -1, 18036),
                    new NpcLocation(-73495, 111611, -4895, -1, 18031),
                    new NpcLocation(-73327, 111611, -4895, -1, 18031),
                    new NpcLocation(-73154, 111611, -4895, -1, 18031),
                    new NpcLocation(-73473, 112301, -4895, -1, 18031),
                    new NpcLocation(-73473, 112475, -4895, -1, 18031),
                    new NpcLocation(-73473, 118036, -4895, -1, 18031),
                    new NpcLocation(-74270, 112326, -4895, -1, 18036),
                    new NpcLocation(-74443, 112326, -4895, -1, 18036)),
            /* 64 and Below */
            List.of(
                    new NpcLocation(-75738, 113439, -4895, -1, 18046),
                    new NpcLocation(-75571, 113439, -4895, -1, 18046),
                    new NpcLocation(-75824, 114141, -4895, -1, 18041),
                    new NpcLocation(-75824, 114309, -4895, -1, 18041),
                    new NpcLocation(-75824, 114477, -4895, -1, 18041),
                    new NpcLocation(-76513, 114158, -4895, -1, 18041),
                    new NpcLocation(-76683, 114158, -4895, -1, 18041),
                    new NpcLocation(-76857, 114158, -4895, -1, 18041),
                    new NpcLocation(-76535, 113357, -4895, -1, 18056),
                    new NpcLocation(-76535, 113190, -4895, -1, 18056)),
            /* No Level Limit */
            List.of(
                    new NpcLocation(-79350, 109894, -4895, -1, 18056),
                    new NpcLocation(-79534, 109894, -4895, -1, 18056),
                    new NpcLocation(-79285, 109187, -4895, -1, 18051),
                    new NpcLocation(-79285, 109019, -4895, -1, 18051),
                    new NpcLocation(-79285, 108860, -4895, -1, 18051),
                    new NpcLocation(-78587, 109172, -4895, -1, 18051),
                    new NpcLocation(-78415, 109172, -4895, -1, 18051),
                    new NpcLocation(-78249, 109172, -4895, -1, 18051),
                    new NpcLocation(-78575, 109961, -4895, -1, 18056),
                    new NpcLocation(-78575, 110130, -4895, -1, 18056)));
    static final List<List<NpcLocation>> FESTIVAL_DUSK_SECONDARY_SPAWNS = List.of(
            /* 31 and Below */
            List.of(
                    new NpcLocation(-76844, 89304, -5151, -1, 18011),
                    new NpcLocation(-76844, 89479, -5151, -1, 18011),
                    new NpcLocation(-76844, 89649, -5151, -1, 18011),
                    new NpcLocation(-77544, 89326, -5151, -1, 18011),
                    new NpcLocation(-77716, 89326, -5151, -1, 18011),
                    new NpcLocation(-77881, 89326, -5151, -1, 18011),
                    new NpcLocation(-77561, 88530, -5151, -1, 18016),
                    new NpcLocation(-77561, 88364, -5151, -1, 18016),
                    new NpcLocation(-76762, 88615, -5151, -1, 18016),
                    new NpcLocation(-76594, 88615, -5151, -1, 18016)),
            /* 42 and Below */
            List.of(
                    new NpcLocation(-77307, 84969, -5151, -1, 18021),
                    new NpcLocation(-77307, 84795, -5151, -1, 18021),
                    new NpcLocation(-77307, 84623, -5151, -1, 18021),
                    new NpcLocation(-76614, 84944, -5151, -1, 18021),
                    new NpcLocation(-76433, 84944, -5151, -1, 18021),
                    new NpcLocation(-76261, 84944, -5151, -1, 18021),
                    new NpcLocation(-76594, 85745, -5151, -1, 18026),
                    new NpcLocation(-76594, 85910, -5151, -1, 18026),
                    new NpcLocation(-77384, 85660, -5151, -1, 18026),
                    new NpcLocation(-77555, 85660, -5151, -1, 18026)),
            /* 53 and Below */
            List.of(
                    new NpcLocation(-74517, 86782, -5151, -1, 18031),
                    new NpcLocation(-74344, 86782, -5151, -1, 18031),
                    new NpcLocation(-74185, 86782, -5151, -1, 18031),
                    new NpcLocation(-74496, 87464, -5151, -1, 18031),
                    new NpcLocation(-74496, 87636, -5151, -1, 18031),
                    new NpcLocation(-74496, 87815, -5151, -1, 18031),
                    new NpcLocation(-75298, 87497, -5151, -1, 18036),
                    new NpcLocation(-75460, 87497, -5151, -1, 18036),
                    new NpcLocation(-75219, 86712, -5151, -1, 18036),
                    new NpcLocation(-75219, 86531, -5151, -1, 18036)),
            /* 64 and Below */
            List.of(
                    new NpcLocation(-79851, 88703, -5151, -1, 18041),
                    new NpcLocation(-79851, 88868, -5151, -1, 18041),
                    new NpcLocation(-79851, 89040, -5151, -1, 18041),
                    new NpcLocation(-80548, 88722, -5151, -1, 18041),
                    new NpcLocation(-80711, 88722, -5151, -1, 18041),
                    new NpcLocation(-80883, 88722, -5151, -1, 18041),
                    new NpcLocation(-80565, 87916, -5151, -1, 18046),
                    new NpcLocation(-80565, 87752, -5151, -1, 18046),
                    new NpcLocation(-79779, 87996, -5151, -1, 18046),
                    new NpcLocation(-79613, 87996, -5151, -1, 18046)),
            /* No Level Limit */
            List.of(
                    new NpcLocation(-79271, 84330, -5151, -1, 18051),
                    new NpcLocation(-79448, 84330, -5151, -1, 18051),
                    new NpcLocation(-79601, 84330, -5151, -1, 18051),
                    new NpcLocation(-80311, 84367, -5151, -1, 18051),
                    new NpcLocation(-80311, 84196, -5151, -1, 18051),
                    new NpcLocation(-80311, 84015, -5151, -1, 18051),
                    new NpcLocation(-80556, 85049, -5151, -1, 18056),
                    new NpcLocation(-80384, 85049, -5151, -1, 18056),
                    new NpcLocation(-79598, 85127, -5151, -1, 18056),
                    new NpcLocation(-79598, 85303, -5151, -1, 18056)));
    static final List<List<NpcLocation>> FESTIVAL_DAWN_CHEST_SPAWNS = List.of(
            /* Level 31 and Below */
            List.of(
                    new NpcLocation(-78999, 112957, -4927, -1, 18109),
                    new NpcLocation(-79153, 112873, -4927, -1, 18109),
                    new NpcLocation(-79256, 112873, -4927, -1, 18109),
                    new NpcLocation(-79368, 112957, -4927, -1, 18109),
                    new NpcLocation(-79481, 113124, -4927, -1, 18109),
                    new NpcLocation(-79481, 113275, -4927, -1, 18109),
                    new NpcLocation(-79364, 113398, -4927, -1, 18109),
                    new NpcLocation(-79213, 113500, -4927, -1, 18109),
                    new NpcLocation(-79099, 113500, -4927, -1, 18109),
                    new NpcLocation(-78960, 113398, -4927, -1, 18109),
                    new NpcLocation(-78882, 113235, -4927, -1, 18109),
                    new NpcLocation(-78882, 113099, -4927, -1, 18109)),
            /* Level 42 and Below */
            List.of(
                    new NpcLocation(-76119, 110383, -4927, -1, 18110),
                    new NpcLocation(-75980, 110442, -4927, -1, 18110),
                    new NpcLocation(-75848, 110442, -4927, -1, 18110),
                    new NpcLocation(-75720, 110383, -4927, -1, 18110),
                    new NpcLocation(-75625, 110195, -4927, -1, 18110),
                    new NpcLocation(-75625, 110063, -4927, -1, 18110),
                    new NpcLocation(-75722, 109908, -4927, -1, 18110),
                    new NpcLocation(-75863, 109832, -4927, -1, 18110),
                    new NpcLocation(-75989, 109832, -4927, -1, 18110),
                    new NpcLocation(-76130, 109908, -4927, -1, 18110),
                    new NpcLocation(-76230, 110079, -4927, -1, 18110),
                    new NpcLocation(-76230, 110215, -4927, -1, 18110)),
            /* Level 53 and Below */
            List.of(
                    new NpcLocation(-74055, 111781, -4927, -1, 18111),
                    new NpcLocation(-74144, 111938, -4927, -1, 18111),
                    new NpcLocation(-74144, 112075, -4927, -1, 18111),
                    new NpcLocation(-74055, 112173, -4927, -1, 18111),
                    new NpcLocation(-73885, 112289, -4927, -1, 18111),
                    new NpcLocation(-73756, 112289, -4927, -1, 18111),
                    new NpcLocation(-73574, 112141, -4927, -1, 18111),
                    new NpcLocation(-73511, 112040, -4927, -1, 18111),
                    new NpcLocation(-73511, 111912, -4927, -1, 18111),
                    new NpcLocation(-73574, 111772, -4927, -1, 18111),
                    new NpcLocation(-73767, 111669, -4927, -1, 18111),
                    new NpcLocation(-73899, 111669, -4927, -1, 18111)),
            /* Level 64 and Below */
            List.of(
                    new NpcLocation(-76008, 113566, -4927, -1, 18112),
                    new NpcLocation(-76159, 113485, -4927, -1, 18112),
                    new NpcLocation(-76267, 113485, -4927, -1, 18112),
                    new NpcLocation(-76386, 113566, -4927, -1, 18112),
                    new NpcLocation(-76482, 113748, -4927, -1, 18112),
                    new NpcLocation(-76482, 113885, -4927, -1, 18112),
                    new NpcLocation(-76371, 114029, -4927, -1, 18112),
                    new NpcLocation(-76220, 114118, -4927, -1, 18112),
                    new NpcLocation(-76092, 114118, -4927, -1, 18112),
                    new NpcLocation(-75975, 114029, -4927, -1, 18112),
                    new NpcLocation(-75861, 113851, -4927, -1, 18112),
                    new NpcLocation(-75861, 113713, -4927, -1, 18112)),
            /* No Level Limit */
            List.of(
                    new NpcLocation(-79100, 109782, -4927, -1, 18113),
                    new NpcLocation(-78962, 109853, -4927, -1, 18113),
                    new NpcLocation(-78851, 109853, -4927, -1, 18113),
                    new NpcLocation(-78721, 109782, -4927, -1, 18113),
                    new NpcLocation(-78615, 109596, -4927, -1, 18113),
                    new NpcLocation(-78615, 109453, -4927, -1, 18113),
                    new NpcLocation(-78746, 109300, -4927, -1, 18113),
                    new NpcLocation(-78881, 109203, -4927, -1, 18113),
                    new NpcLocation(-79027, 109203, -4927, -1, 18113),
                    new NpcLocation(-79159, 109300, -4927, -1, 18113),
                    new NpcLocation(-79240, 109480, -4927, -1, 18113),
                    new NpcLocation(-79240, 109615, -4927, -1, 18113)));
    static final List<List<NpcLocation>> FESTIVAL_DUSK_CHEST_SPAWNS = List.of(
            /* Level 31 and Below */
            List.of(
                    new NpcLocation(-77016, 88726, -5183, -1, 18114),
                    new NpcLocation(-77136, 88646, -5183, -1, 18114),
                    new NpcLocation(-77247, 88646, -5183, -1, 18114),
                    new NpcLocation(-77380, 88726, -5183, -1, 18114),
                    new NpcLocation(-77512, 88883, -5183, -1, 18114),
                    new NpcLocation(-77512, 89053, -5183, -1, 18114),
                    new NpcLocation(-77378, 89287, -5183, -1, 18114),
                    new NpcLocation(-77254, 89238, -5183, -1, 18114),
                    new NpcLocation(-77095, 89238, -5183, -1, 18114),
                    new NpcLocation(-76996, 89287, -5183, -1, 18114),
                    new NpcLocation(-76901, 89025, -5183, -1, 18114),
                    new NpcLocation(-76901, 88891, -5183, -1, 18114)),
            /* Level 42 and Below */
            List.of(
                    new NpcLocation(-77128, 85553, -5183, -1, 18115),
                    new NpcLocation(-77036, 85594, -5183, -1, 18115),
                    new NpcLocation(-76919, 85594, -5183, -1, 18115),
                    new NpcLocation(-76755, 85553, -5183, -1, 18115),
                    new NpcLocation(-76635, 85392, -5183, -1, 18115),
                    new NpcLocation(-76635, 85216, -5183, -1, 18115),
                    new NpcLocation(-76761, 85025, -5183, -1, 18115),
                    new NpcLocation(-76908, 85004, -5183, -1, 18115),
                    new NpcLocation(-77041, 85004, -5183, -1, 18115),
                    new NpcLocation(-77138, 85025, -5183, -1, 18115),
                    new NpcLocation(-77268, 85219, -5183, -1, 18115),
                    new NpcLocation(-77268, 85410, -5183, -1, 18115)),
            /* Level 53 and Below */
            List.of(
                    new NpcLocation(-75150, 87303, -5183, -1, 18116),
                    new NpcLocation(-75150, 87175, -5183, -1, 18116),
                    new NpcLocation(-75150, 87175, -5183, -1, 18116),
                    new NpcLocation(-75150, 87303, -5183, -1, 18116),
                    new NpcLocation(-74943, 87433, -5183, -1, 18116),
                    new NpcLocation(-74767, 87433, -5183, -1, 18116),
                    new NpcLocation(-74556, 87306, -5183, -1, 18116),
                    new NpcLocation(-74556, 87184, -5183, -1, 18116),
                    new NpcLocation(-74556, 87184, -5183, -1, 18116),
                    new NpcLocation(-74556, 87306, -5183, -1, 18116),
                    new NpcLocation(-74757, 86830, -5183, -1, 18116),
                    new NpcLocation(-74927, 86830, -5183, -1, 18116)),
            /* Level 64 and Below */
            List.of(
                    new NpcLocation(-80010, 88128, -5183, -1, 18117),
                    new NpcLocation(-80113, 88066, -5183, -1, 18117),
                    new NpcLocation(-80220, 88066, -5183, -1, 18117),
                    new NpcLocation(-80359, 88128, -5183, -1, 18117),
                    new NpcLocation(-80467, 88267, -5183, -1, 18117),
                    new NpcLocation(-80467, 88436, -5183, -1, 18117),
                    new NpcLocation(-80381, 88639, -5183, -1, 18117),
                    new NpcLocation(-80278, 88577, -5183, -1, 18117),
                    new NpcLocation(-80142, 88577, -5183, -1, 18117),
                    new NpcLocation(-80028, 88639, -5183, -1, 18117),
                    new NpcLocation(-79915, 88466, -5183, -1, 18117),
                    new NpcLocation(-79915, 88322, -5183, -1, 18117)),
            /* No Level Limit */
            List.of(
                    new NpcLocation(-80153, 84947, -5183, -1, 18118),
                    new NpcLocation(-80003, 84962, -5183, -1, 18118),
                    new NpcLocation(-79848, 84962, -5183, -1, 18118),
                    new NpcLocation(-79742, 84947, -5183, -1, 18118),
                    new NpcLocation(-79668, 84772, -5183, -1, 18118),
                    new NpcLocation(-79668, 84619, -5183, -1, 18118),
                    new NpcLocation(-79772, 84471, -5183, -1, 18118),
                    new NpcLocation(-79888, 84414, -5183, -1, 18118),
                    new NpcLocation(-80023, 84414, -5183, -1, 18118),
                    new NpcLocation(-80166, 84471, -5183, -1, 18118),
                    new NpcLocation(-80253, 84600, -5183, -1, 18118),
                    new NpcLocation(-80253, 84780, -5183, -1, 18118)));
    static final int FESTIVAL_DEFAULT_RESPAWN = 60; // Specify in seconds!
    static final List<Location> FESTIVAL_DUSK_PLAYER_SPAWNS = List.of(
            new Location(-77200, 88966, -5151, 0), // 31 and below
            new Location(-76941, 85307, -5151, 0), // 42 and below
            new Location(-74855, 87135, -5151, 0), // 53 and below
            new Location(-80208, 88222, -5151, 0), // 64 and below
            new Location(-79954, 84697, -5151, 0)); // No level limit
    static final List<NpcLocation> FESTIVAL_DAWN_WITCH_SPAWNS = List.of(
            new NpcLocation(-79183, 113052, -4891, 0, 31132), // 31 and below
            new NpcLocation(-75916, 110270, -4891, 0, 31133), // 42 and below
            new NpcLocation(-73979, 111970, -4891, 0, 31134), // 53 and below
            new NpcLocation(-76174, 113663, -4891, 0, 31135), // 64 and below
            new NpcLocation(-78930, 109664, -4891, 0, 31136)); // No level limit
    static final List<NpcLocation> FESTIVAL_DUSK_WITCH_SPAWNS = List.of(
            new NpcLocation(-77199, 88830, -5147, 0, 31142), // 31 and below
            new NpcLocation(-76942, 85438, -5147, 0, 31143), // 42 and below
            new NpcLocation(-74990, 87135, -5147, 0, 31144), // 53 and below
            new NpcLocation(-80207, 88222, -5147, 0, 31145), // 64 and below
            new NpcLocation(-79952, 84833, -5147, 0, 31146)); // No level limit
    static final List<List<NpcLocation>> FESTIVAL_DAWN_PRIMARY_SPAWNS = List.of(
            /* Level 31 and Below - Offering of the Branded */
            List.of(
                    new NpcLocation(-78537, 113839, -4895, -1, 18009),
                    new NpcLocation(-78466, 113852, -4895, -1, 18010),
                    new NpcLocation(-78509, 113899, -4895, -1, 18010),
                    new NpcLocation(-78481, 112557, -4895, -1, 18009),
                    new NpcLocation(-78559, 112504, -4895, -1, 18010),
                    new NpcLocation(-78489, 112494, -4895, -1, 18010),
                    new NpcLocation(-79803, 112543, -4895, -1, 18012),
                    new NpcLocation(-79854, 112492, -4895, -1, 18013),
                    new NpcLocation(-79886, 112557, -4895, -1, 18014),
                    new NpcLocation(-79821, 113811, -4895, -1, 18015),
                    new NpcLocation(-79857, 113896, -4895, -1, 18017),
                    new NpcLocation(-79878, 113816, -4895, -1, 18018),
                    // Archers and Marksmen \\
                    new NpcLocation(-79190, 113660, -4895, -1, 18011),
                    new NpcLocation(-78710, 113188, -4895, -1, 18011),
                    new NpcLocation(-79190, 112730, -4895, -1, 18016),
                    new NpcLocation(-79656, 113188, -4895, -1, 18016)),
            /* Level 42 and Below - Apostate Offering */
            List.of(
                    new NpcLocation(-76558, 110784, -4895, -1, 18019),
                    new NpcLocation(-76607, 110815, -4895, -1, 18020), // South West
                    new NpcLocation(-76559, 110820, -4895, -1, 18020),
                    new NpcLocation(-75277, 110792, -4895, -1, 18019),
                    new NpcLocation(-75225, 110801, -4895, -1, 18020), // South East
                    new NpcLocation(-75262, 110832, -4895, -1, 18020),
                    new NpcLocation(-75249, 109441, -4895, -1, 18022),
                    new NpcLocation(-75278, 109495, -4895, -1, 18023), // North East
                    new NpcLocation(-75223, 109489, -4895, -1, 18024),
                    new NpcLocation(-76556, 109490, -4895, -1, 18025),
                    new NpcLocation(-76607, 109469, -4895, -1, 18027), // North West
                    new NpcLocation(-76561, 109450, -4895, -1, 18028),
                    // Archers and Marksmen \\
                    new NpcLocation(-76399, 110144, -4895, -1, 18021),
                    new NpcLocation(-75912, 110606, -4895, -1, 18021),
                    new NpcLocation(-75444, 110144, -4895, -1, 18026),
                    new NpcLocation(-75930, 109665, -4895, -1, 18026)),
            /* Level 53 and Below - Witch's Offering */
            List.of(
                    new NpcLocation(-73184, 111319, -4895, -1, 18029),
                    new NpcLocation(-73135, 111294, -4895, -1, 18030), // South West
                    new NpcLocation(-73185, 111281, -4895, -1, 18030),
                    new NpcLocation(-74477, 111321, -4895, -1, 18029),
                    new NpcLocation(-74523, 111293, -4895, -1, 18030), // South East
                    new NpcLocation(-74481, 111280, -4895, -1, 18030),
                    new NpcLocation(-74489, 112604, -4895, -1, 18032),
                    new NpcLocation(-74491, 112660, -4895, -1, 18033), // North East
                    new NpcLocation(-74527, 112629, -4895, -1, 18034),
                    new NpcLocation(-73197, 112621, -4895, -1, 18035),
                    new NpcLocation(-73142, 112631, -4895, -1, 18037), // North West
                    new NpcLocation(-73182, 112656, -4895, -1, 18038),
                    // Archers and Marksmen \\
                    new NpcLocation(-73834, 112430, -4895, -1, 18031),
                    new NpcLocation(-74299, 111959, -4895, -1, 18031),
                    new NpcLocation(-73841, 111491, -4895, -1, 18036),
                    new NpcLocation(-73363, 111959, -4895, -1, 18036)),
            /* Level 64 and Below - Dark Omen Offering */
            List.of(
                    new NpcLocation(-75543, 114461, -4895, -1, 18039),
                    new NpcLocation(-75514, 114493, -4895, -1, 18040), // South West
                    new NpcLocation(-75488, 114456, -4895, -1, 18040),
                    new NpcLocation(-75521, 113158, -4895, -1, 18039),
                    new NpcLocation(-75504, 113110, -4895, -1, 18040), // South East
                    new NpcLocation(-75489, 113142, -4895, -1, 18040),
                    new NpcLocation(-76809, 113143, -4895, -1, 18042),
                    new NpcLocation(-76860, 113138, -4895, -1, 18043), // North East
                    new NpcLocation(-76831, 113112, -4895, -1, 18044),
                    new NpcLocation(-76831, 114441, -4895, -1, 18045),
                    new NpcLocation(-76840, 114490, -4895, -1, 18047), // North West
                    new NpcLocation(-76864, 114455, -4895, -1, 18048),
                    // Archers and Marksmen \\
                    new NpcLocation(-75703, 113797, -4895, -1, 18041),
                    new NpcLocation(-76180, 114263, -4895, -1, 18041),
                    new NpcLocation(-76639, 113797, -4895, -1, 18046),
                    new NpcLocation(-76180, 113337, -4895, -1, 18046)),
            /* No Level Limit - Offering of Forbidden Path */
            List.of(
                    new NpcLocation(-79576, 108881, -4895, -1, 18049),
                    new NpcLocation(-79592, 108835, -4895, -1, 18050), // South West
                    new NpcLocation(-79614, 108871, -4895, -1, 18050),
                    new NpcLocation(-79586, 110171, -4895, -1, 18049),
                    new NpcLocation(-79589, 110216, -4895, -1, 18050), // South East
                    new NpcLocation(-79620, 110177, -4895, -1, 18050),
                    new NpcLocation(-78825, 110182, -4895, -1, 18052),
                    new NpcLocation(-78238, 110182, -4895, -1, 18053), // North East
                    new NpcLocation(-78266, 110218, -4895, -1, 18054),
                    new NpcLocation(-78275, 108883, -4895, -1, 18055),
                    new NpcLocation(-78267, 108839, -4895, -1, 18057), // North West
                    new NpcLocation(-78241, 108871, -4895, -1, 18058),
                    // Archers and Marksmen
                    new NpcLocation(-79394, 109538, -4895, -1, 18051),
                    new NpcLocation(-78929, 109992, -4895, -1, 18051),
                    new NpcLocation(-78454, 109538, -4895, -1, 18056),
                    new NpcLocation(-78929, 109053, -4895, -1, 18056)));
    static final List<Location> FESTIVAL_DAWN_PLAYER_SPAWNS = List.of(
            new Location(-79187, 113186, -4895, 0), // 31 and below
            new Location(-75918, 110137, -4895, 0), // 42 and below
            new Location(-73835, 111969, -4895, 0), // 53 and below
            new Location(-76170, 113804, -4895, 0), // 64 and below
            new Location(-78927, 109528, -4895, 0)); // No level limit
    public final Location loc;
    public final int npcId;

    FestivalSpawn(NpcLocation npcLocation) {
        this.loc = npcLocation.getLoc();
        npcId = npcLocation.npcId;
    }

    FestivalSpawn(Location loc) {
        this.loc = loc;
        // Generate a random heading if no positive one given.
        this.loc.h = loc.h < 0 ? Rnd.get(65536) : loc.h;
        npcId = -1;
    }

    public static class NpcLocation {
        int x;
        int y;
        int z;
        int heading;
        int npcId;

        NpcLocation(int x, int y, int z, int heading, int npcId) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.heading = heading < 0 ? Rnd.get(65536) : heading;
            this.npcId = npcId;
        }

        public Location getLoc() {
            return new Location(x, y, z, heading);
        }
    }

}