package l2trunk.scripts.quests;

import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class _079_SagaOfTheAdventurer extends SagasSuperclass {
    public _079_SagaOfTheAdventurer() {
        super(false);

        NPC = List.of(31603, 31584, 31579, 31615, 31619, 31646, 31647, 31651, 31654, 31655, 31658, 31616);
        Items = List.of(7080, 7516, 7081, 7494, 7277, 7308, 7339, 7370, 7401, 7432, 7102, 0);
        Mob = List.of(27299, 27228, 27302);
        classid = 93;
        prevclass = 0x08;
        locs = List.of(
                new Location(119518, -28658, -3811),
                new Location(181205, 36676, -4816),
                new Location(181215, 36676, -4812));
        Text = List.of(
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
                "....! Fight...Defeat...It...Fight...Defeat...It..."
        );

        registerNPCs();
    }
}