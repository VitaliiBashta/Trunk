package l2trunk.scripts.ai.dragonvalley;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

import java.util.Arrays;
import java.util.List;

public final class DrakeMagma extends Patrollers {
    public DrakeMagma(NpcInstance actor) {
        super(actor);

        if (equals(actor.getLoc(), 122888, 110664, -3728)) {

            points = List.of(
                   Location.of(122888, 110664, -3728),
                   Location.of(121320, 112440, -3792),
                   Location.of(120024, 112712, -3744),
                   Location.of(119640, 114792, -3608),

                   Location.of(117240, 118935, -3712),
                   Location.of(110264, 123464, -3632),
                   Location.of(109416, 126088, -3696),
                   Location.of(110456, 125976, -3686),

                   Location.of(111336, 123208, -3712),
                   Location.of(114200, 121528, -3744),
                   Location.of(115880, 122984, -3240),
                   Location.of(118040, 123112, -3072),

                   Location.of(121880, 119352, -3136),
                   Location.of(123512, 118968, -3200),
                   Location.of(124600, 118632, -3056),
                   Location.of(126520, 118056, -3104),

                   Location.of(126936, 115032, -3728),
                   Location.of(124040, 108632, -2992),
                   Location.of(122344, 108200, -2992),
                   Location.of(117688, 110296, -2944),

                   Location.of(117160, 113768, -3056),
                   Location.of(113640, 116328, -3200),
                   Location.of(111128, 119368, -3056),
                   Location.of(109816, 119336, -3072),

                   Location.of(108568, 117928, -3048),
                   Location.of(110872, 110600, -3056),
                   Location.of(114392, 110632, -3024),
                   Location.of(113448, 113175, -2984),

                   Location.of(112568, 113064, -2784),
                   Location.of(111688, 112360, -2784));
        } else {
            points = List.of(
                   Location.of(80232, 110248, -3040),
                   Location.of(81864, 109368, -3120),
                   Location.of(83736, 108312, -3072),
                   Location.of(85496, 106440, -3216),
                   Location.of(91800, 108600, -3024),
                   Location.of(92168, 110664, -3008),
                   Location.of(92376, 111512, -3024),
                   Location.of(94040, 112776, -3040),
                   Location.of(93944, 114312, -3104),
                   Location.of(95064, 116200, -3056),
                   Location.of(94328, 117720, -3024),
                   Location.of(93032, 118312, -3024),
                   Location.of(90792, 117928, -3072),
                   Location.of(89880, 115624, -3040),
                   Location.of(88952, 114904, -3040),
                   Location.of(87304, 114552, -2976));
        }
    }

    private boolean equals(Location loc, int x, int y, int z) {
        return loc.x == x &&
                loc.y == y &&
                loc.z == z;
    }
}
