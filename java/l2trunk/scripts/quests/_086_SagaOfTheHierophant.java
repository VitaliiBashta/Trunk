package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class _086_SagaOfTheHierophant extends SagasSuperclass {
    public _086_SagaOfTheHierophant() {
        super(false);

        NPC = List.of(30191, 31626, 31588, 31280, 31591, 31646, 31648, 31652, 31654, 31655, 31659, 31280);
        items = List.of(7080, 7523, 7081, 7501, 7284, 7315, 7346, 7377, 7408, 7439, 7089, 0);
        mob = List.of(27269, 27235, 27275);
        classid = ClassId.hierophant;

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