package l2trunk.scripts.quests;

import l2trunk.gameserver.scripts.ScriptFile;

public class _100_SagaOfTheMaestro extends SagasSuperclass implements ScriptFile {
    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _100_SagaOfTheMaestro() {
        super(false);

        NPC = new int[]{
                31592,
                31273,
                31597,
                31597,
                31596,
                31646,
                31648,
                31653,
                31654,
                31655,
                31656,
                31597
        };
        Items = new int[]{
                7080,
                7607,
                7081,
                7515,
                7298,
                7329,
                7360,
                7391,
                7422,
                7453,
                7108,
                0
        };
        Mob = new int[]{
                27260,
                27249,
                27308
        };
        classid = 118;
        prevclass = 0x39;
        X = new int[]{
                164650,
                47429,
                47391
        };
        Y = new int[]{
                -74121,
                -56923,
                -56929
        };
        Z = new int[]{
                -2871,
                -2383,
                -2370
        };
        Text = new String[]{
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
        };

        registerNPCs();
    }
}