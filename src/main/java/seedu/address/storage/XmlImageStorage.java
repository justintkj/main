package seedu.address.storage;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;

//@@author liliwei25
/**
 * Creates folder to store all images saved by user
 */
public class XmlImageStorage {

    private static final Logger logger = LogsCenter.getLogger(XmlImageStorage.class);
    private static final String PNG = ".png";

    /**
     * Save selected image to image folder
     *
     * @throws IOException when image copy fails
     */
    public void saveImage(File image, String name) throws IOException {
        requireNonNull(image);
        requireNonNull(name);

        File file = new File(name + PNG);
        Files.copy(image.toPath(), file.toPath(), REPLACE_EXISTING);
    }

    /**
     * Deletes the selected image from folder
     * @param image to delete
     * @throws IOException when image deletion fails
     */
    public void removeImage(File image) throws IOException {
        requireNonNull(image);

        File[] files = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath())
                .listFiles();

        for (File file : files) {
            if (file.getName().equals(image.getName())) {
                file.delete();
            }
        }
    }
}
