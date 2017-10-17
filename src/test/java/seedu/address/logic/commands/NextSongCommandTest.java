package seedu.address.logic.commands;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NextSongCommandTest {

    public static final String EXPECTED_OUTPUT_SONG = "Currently Playing..KissTheRain.mp3";

    @Test
    public void execute() {
        NextSongCommand test = new NextSongCommand();
        CommandResult result = test.execute();
        assertEquals(EXPECTED_OUTPUT_SONG, result.feedbackToUser);
    }
}
