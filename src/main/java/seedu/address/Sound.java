package seedu.address;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import seedu.address.commons.core.LogsCenter;

/**
 * The Playlist for the background music.
 */
public class Sound {
    private static final Logger logger = LogsCenter.getLogger("Error Sound");
    private static ArrayList<String> musicList = new ArrayList<String>(Arrays.asList("ErrorSound.mp3"));
    private static int curr = 0;
    private static String bip;
    private static Media hit;
    private static MediaPlayer mediaPlayer;


    /**
     * start playing the first error music on the playlist.
     */

    public static void music() {
        try {
            bip = musicList.get(curr);
            hit = new Media(new File(bip).toURI().toString());
            mediaPlayer = new MediaPlayer(hit);
            mediaPlayer.play();
        } catch (Exception ex) {
            logger.info("Error with playing sound.");
            ex.printStackTrace();
        }
    }
}
