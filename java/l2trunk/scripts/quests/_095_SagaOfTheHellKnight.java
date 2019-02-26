package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class _095_SagaOfTheHellKnight extends SagasSuperclass {
    public _095_SagaOfTheHellKnight() {
        super(false);

        NPC = List.of(31582, 31623, 31297, 31297, 31599, 31646, 31647, 31653, 31654, 31655, 31656, 31297);
        items = List.of(7080, 7532, 7081, 7510, 7293, 7324, 7355, 7386, 7417, 7448, 7086, 0);
        mob = List.of(27258, 27244, 27263);
        classid = ClassId.hellKnight;
        locs = List.of(
                Location.of(164650, -74121, -2871),
                Location.of(47391, -56929, -2370),
                Location.of(47429, -56923, -2383));
        text = List.of(
                "PLAYERNAME! Pursued to here! However, I jumped out of the Banshouren boundaries! You look at the giant as the sign of power!",
                "... Oh ... good! So it was ... let's begin!",
                "I do not have the patience ..! I have been a giant force ...! Cough chatter ah ah ah!",
                "Paying homage to those who disrupt the orderly will be PLAYERNAME's death!",
                "Now, my soul freed from the shackles of the millennium, Halixia, to the back side I come ...",
                "Why do you interfere others' battles?",
                "This is a waste of time.. say goodbye...!",
                "...That is the enemy",
                "...Goodness! PLAYERNAME you are still looking?",
                "PLAYERNAME ... Not just to whom the victory. Only personnel involved in the fighting are eligible to share in the victory.",
                "Your sword is not an ornament. Don't you think, PLAYERNAME?",
                "Goodness! I no longer sense a battle there now.",
                "let...",
                "Only engaged in the battle to bar their choice. Perhaps you should regret.",
                "The human nation was foolish to try and fight a giant's strength.",
                "Must...Retreat... Too...Strong.",
                "PLAYERNAME. Defeat...by...retaining...and...Mo...Hacker",
                "....! Fight...Defeat...It...Fight...Defeat...It...");
        registerNPCs();
    }
}