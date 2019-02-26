package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class _098_SagaOfTheShillienSaint extends SagasSuperclass {
    public _098_SagaOfTheShillienSaint() {
        super(false);

        NPC = List.of(31581, 31626, 31588, 31287, 31621, 31646, 31647, 31651, 31654, 31655, 31658, 31287);
        items = List.of(7080, 7525, 7081, 7513, 7296, 7327, 7358, 7389, 7420, 7451, 7090, 0);
        mob = List.of(27270, 27247, 27277);
        classid = ClassId.shillienSaint;
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