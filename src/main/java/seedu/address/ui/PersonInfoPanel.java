package seedu.address.ui;

import java.io.File;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.ui.PersonPanelSelectionChangedEvent;
import seedu.address.model.person.ReadOnlyPerson;

//@@author liliwei25
/**
 * Shows the selected Person's info
 */
public class PersonInfoPanel extends UiPart<Region> {

    private static final String FXML = "PersonInfoPanel.fxml";
    private static final String EMPTY = "";
    private static final String DEFAULT = "profiles/default.png";
    private static final String DEFAULT_TEXT = "default";
    private final Logger logger = LogsCenter.getLogger(PersonInfoPanel.class);
    private final ReadOnlyPerson person;

    @FXML
    private ImageView profileImage;
    @FXML
    private Label name;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private Label birthday;
    @FXML
    private Label remark;
    @FXML
    private FlowPane tags;

    public PersonInfoPanel(ReadOnlyPerson person) {
        super(FXML);
        setConnections(person);
        this.person = person;
        registerAsAnEventHandler(this);
    }

    public PersonInfoPanel() {
        super(FXML);
        setDefaultConnections();
        person = null;
        registerAsAnEventHandler(this);
    }

    public void updateConnections(ReadOnlyPerson person) {
        setConnections(person);
    }

    private void setDefaultConnections() {
        name.setText(EMPTY);
        phone.setText(EMPTY);
        address.setText(EMPTY);
        email.setText(EMPTY);
        birthday.setText(EMPTY);
        remark.setText(EMPTY);
        tags.setAccessibleText(EMPTY);
        profileImage.setImage(new Image(DEFAULT));
    }

    private void setConnections(ReadOnlyPerson person) {
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        address.setText(person.getAddress().value);
        email.setText(person.getEmail().value);
        birthday.setText(person.getBirthday().value);
        remark.setText(person.getRemark().value);
        tags.getChildren().clear();
        person.getTags().forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
        try {
            String loc = person.getPicture().getLocation();
            Image image;
            if (loc.equals(DEFAULT_TEXT)) {
                image = new Image(DEFAULT);
            } else {
                File img = new File(loc);
                image = new Image(img.toURI().toString());
            }
            profileImage.setImage(image);
        } catch (IllegalArgumentException iae) {
            profileImage.setImage(new Image(DEFAULT));
        }
    }

    @Subscribe
    private void handlePersonPanelSelectionChangedEvent(PersonPanelSelectionChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        setConnections(event.getNewSelection().person);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PersonInfoPanel)) {
            return false;
        }

        // state check
        PersonInfoPanel card = (PersonInfoPanel) other;
        return name.getText().equals(card.name.getText())
                && phone.getText().equals(card.phone.getText())
                && address.getText().equals(card.address.getText())
                && email.getText().equals(card.email.getText())
                && birthday.getText().equals(card.birthday.getText())
                && remark.getText().equals(card.remark.getText())
                && person.equals(card.person);
    }
}
