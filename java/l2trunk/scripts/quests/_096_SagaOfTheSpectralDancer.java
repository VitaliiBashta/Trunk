package l2trunk.scripts.quests;

import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class _096_SagaOfTheSpectralDancer extends SagasSuperclass implements ScriptFile {
    public _096_SagaOfTheSpectralDancer() {
        super(false);

        NPC = List.of(31582, 31623, 31284, 31284, 31611, 31646, 31649, 31653, 31654, 31655, 31656, 31284);
        Items = List.of(7080, 7527, 7081, 7511, 7294, 7325, 7356, 7387, 7418, 7449, 7092, 0);
        Mob = List.of(27272, 27245, 27264);
        classid = 107;
        prevclass = 0x22;
        locs = List.of(
                new Location(164650, -74121, -2871),
                new Location(47429, -56923, -2383),
                new Location(47391, -56929, -2370));
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