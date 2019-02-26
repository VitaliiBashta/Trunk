package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class _088_SagaOfTheArchmage extends SagasSuperclass {
    public _088_SagaOfTheArchmage() {
        super(false);

        NPC = List.of(30176, 31627, 31282, 31282, 31590, 31646, 31647, 31650, 31654, 31655, 31657, 31282);
        items = List.of(7080, 7529, 7081, 7503, 7286, 7317, 7348, 7379, 7410, 7441, 7082, 0);
        mob = List.of(27250, 27237, 27254);
        classid = ClassId.archmage;
        locs = List.of(
                Location.of(191046, -40640, -3042),
                Location.of(46066, -36396, -1685),
                Location.of(46087, -36372, -1685));
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