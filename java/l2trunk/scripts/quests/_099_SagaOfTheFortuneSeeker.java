package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class _099_SagaOfTheFortuneSeeker extends SagasSuperclass {
    public _099_SagaOfTheFortuneSeeker() {
        super(false);

        NPC = List.of(31594, 31623, 31600, 31600, 31601, 31646, 31649, 31650, 31654, 31655, 31657, 31600);
        items = List.of(7080, 7608, 7081, 7514, 7297, 7328, 7359, 7390, 7421, 7452, 7109, 0);
        mob = List.of(27259, 27248, 27309);
        classid = ClassId.fortuneSeeker;
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