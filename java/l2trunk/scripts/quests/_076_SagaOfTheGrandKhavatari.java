package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class _076_SagaOfTheGrandKhavatari extends SagasSuperclass {
    public _076_SagaOfTheGrandKhavatari() {
        super(false);

        NPC = List.of(31339, 31624, 31589, 31290, 31637, 31646, 31647, 31652, 31654, 31655, 31659, 31290);
        items = List.of(7080, 7539, 7081, 7491, 7274, 7305, 7336, 7367, 7398, 7429, 7099, 0);
        mob = List.of(27293, 27226, 27284);
        classid = ClassId.grandKhauatari;
        locs = List.of(
                Location.of(161719, -92823, -1893),
                Location.of(124355, 82155, -2803),
                Location.of(124376, 82127, -2796));
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