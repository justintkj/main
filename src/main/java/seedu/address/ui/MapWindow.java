package seedu.address.ui;

import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.FxViewUtil;
import seedu.address.model.person.ReadOnlyPerson;

//@@author liliwei25
/**
 * Shows the map in a pop-up browser
 */
public class MapWindow extends UiPart<Region> {

    private static final String GOOGLE_MAPS_URL_PREFIX = "https://www.google.com.sg/maps?safe=off&q=";
    private static final String GOOGLE_SEARCH_URL_SUFFIX = "&cad=h";

    private static final Logger logger = LogsCenter.getLogger(MapWindow.class);
    private static final String ICON = "/images/help_icon.png";
    private static final String FXML = "MapWindow.fxml";
    private static final String TITLE = "Map";
    private static final String SPACE = " ";
    private static final String PLUS = "+";
    private static final String MESSAGE_SHOW_MAP = "Showing map window for selected person.";

    @FXML
    private WebView browser;

    private final Stage dialogStage;

    public MapWindow(ReadOnlyPerson person) {
        super(FXML);
        Scene scene = new Scene(getRoot());
        //Null passed as the parent stage to make it non-modal.
        dialogStage = createDialogStage(TITLE, null, scene);
        dialogStage.setMaximized(false);
        FxViewUtil.setStageIcon(dialogStage, ICON);

        String userGuideUrl = GOOGLE_MAPS_URL_PREFIX + person.getAddress().toString().replaceAll(SPACE, PLUS)
                + GOOGLE_SEARCH_URL_SUFFIX;
        browser.getEngine().load(userGuideUrl);
    }

    /**
     * Shows the map window.
     * @throws IllegalStateException
     * <ul>
     *     <li>
     *         if this method is called on a thread other than the JavaFX Application Thread.
     *     </li>
     *     <li>
     *         if this method is called during animation or layout processing.
     *     </li>
     *     <li>
     *         if this method is called on the primary stage.
     *     </li>
     *     <li>
     *         if {@code dialogStage} is already showing.
     *     </li>
     * </ul>
     */
    public void show() {
        logger.fine(MESSAGE_SHOW_MAP);
        dialogStage.showAndWait();
    }
}
