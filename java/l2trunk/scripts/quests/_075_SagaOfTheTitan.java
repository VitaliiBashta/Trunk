package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class _075_SagaOfTheTitan extends SagasSuperclass {
    public _075_SagaOfTheTitan() {
        super(false);

        NPC = List.of(31327, 31624, 31289, 31290, 31607, 31646, 31649, 31651, 31654, 31655, 31658, 31290);
        items = List.of(7080, 7539, 7081, 7490, 7273, 7304, 7335, 7366, 7397, 7428, 7098, 0);
        mob = List.of(27292, 27224, 27283);
        classid = ClassId.titan;
        locs = List.of(
                Location.of(119518, -28658, -3811),
                Location.of(181215, 36676, -4812),
                Location.of(181227, 36703, -4816));
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