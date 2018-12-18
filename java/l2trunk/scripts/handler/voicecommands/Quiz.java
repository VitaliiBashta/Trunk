package l2trunk.scripts.handler.voicecommands;

import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.scripts.custom.QuizEvent;

import java.util.List;


public class Quiz implements IVoicedCommandHandler, ScriptFile {
    private static final List<String> VOICED_COMMANDS = List.of("quiz", "1", "2", "3");

    @Override
    public List<String> getVoicedCommandList() {
        return VOICED_COMMANDS;
    }

    @Override
    public boolean useVoicedCommand(String command, Player activeChar,
                                    String target) {

        if (command.equalsIgnoreCase("1") && QuizEvent._quizRunning) {
            QuizEvent.setAnswer(activeChar, 1);
        }

        if (command.equalsIgnoreCase("2") && QuizEvent._quizRunning) {
            QuizEvent.setAnswer(activeChar, 2);
        }

        if (command.equalsIgnoreCase("3") && QuizEvent._quizRunning) {
            QuizEvent.setAnswer(activeChar, 3);
        }
        return true;
    }

    @Override
    public void onLoad() {
        System.out.println("Loading Quiz.java");
        VoicedCommandHandler.INSTANCE.registerVoicedCommandHandler(this);
        new QuizEvent();
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {

    }
}