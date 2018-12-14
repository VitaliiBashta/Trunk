package l2trunk.scripts.quests;

import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class _071_SagaOfEvasTemplar extends SagasSuperclass implements ScriptFile {
    public _071_SagaOfEvasTemplar() {
        super(false);

        NPC = List.of(30852, 31624, 31278, 30852, 31638, 31646, 31648, 31651, 31654, 31655, 31658, 31281);
        Items = List.of(7080, 7535, 7081, 7486, 7269, 7300, 7331, 7362, 7393, 7424, 7094, 6482);
        Mob = List.of(27287, 27220, 27279);
        classid = 99;
        prevclass = 0x14;
        locs = List.of(
                new Location(119518, -28658, -3811),
                new Location(181215, 36676, -4812),
                new Location(181227, 36703, -4816));
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
                "....! Fight...Defeat...It...Fight...Defeat...It...");

        registerNPCs();
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }
}