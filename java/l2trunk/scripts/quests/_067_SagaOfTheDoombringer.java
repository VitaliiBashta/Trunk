package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class _067_SagaOfTheDoombringer extends SagasSuperclass {
    public _067_SagaOfTheDoombringer() {
        super(false);

        NPC = List.of(
                32138, 31627, 32223, 32227, 32254, 31646, 31647, 31650, 31654, 31655, 31656, 32227);
        items = List.of(
                7080, 9721, 7081, 9740, 9722, 9725, 9728, 9731, 9734, 9737, 9717, 0);
        mob = List.of(27324, 27325, 27326);
        classid = ClassId.doombringer;
        locs = List.of(
                 Location.of(191046, -40640, -3042),
                 Location.of(47429, -56923, -2383),
                 Location.of(47391, -56929, -2370)
        );
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