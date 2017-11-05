# liliwei25
###### \java\seedu\address\commons\events\ui\ChangeImageEvent.java
``` java
/**
 * Represents a image changing function call by user
 */
public class ChangeImageEvent extends BaseEvent {
    private final ReadOnlyPerson person;

    public ChangeImageEvent(ReadOnlyPerson person) {
        this.person = person;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public ReadOnlyPerson getPerson() {
        return person;
    }
}
```
###### \java\seedu\address\commons\events\ui\MapPersonEvent.java
``` java
/**
 * Represents a mapping function call by user
 */
public class MapPersonEvent extends BaseEvent {

    private final ReadOnlyPerson person;

    public MapPersonEvent(ReadOnlyPerson person) {
        this.person = person;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public ReadOnlyPerson getPerson() {
        return person;
    }
}
```
###### \java\seedu\address\logic\commands\BirthdayCommand.java
``` java
/**
 * Adds or Edits birthday field of selected person
 */
public class BirthdayCommand extends UndoableCommand {
    public static final String COMMAND_WORD = "birthday";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds/Edits the birthday of the person identified "
            + "by the index number used in the last person listing. "
            + "Existing birthday will be overwritten by the input.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[BIRTHDAY (dd-mm-yyyy)]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + "12-05-2016";

    public static final String MESSAGE_BIRTHDAY_PERSON_SUCCESS = "Birthday Updated success: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "Duplicate person in addressbook";
    public static final String MESSAGE_MISSING_PERSON = "The target person cannot be missing";

    private final Index index;
    private final Birthday birthday;

    public BirthdayCommand (Index index, Birthday birthday) {
        requireNonNull(index);
        requireNonNull(birthday);

        this.index = index;
        this.birthday = birthday;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        ReadOnlyPerson personToEdit = lastShownList.get(index.getZeroBased());
        ReadOnlyPerson editedPerson = new Person(personToEdit.getName(), personToEdit.getPhone(),
                personToEdit.getEmail(), personToEdit.getAddress(), personToEdit.getRemark(), birthday,
                personToEdit.getTags(), personToEdit.getPicture(), personToEdit.getFavourite());

        try {
            model.updatePerson(personToEdit, editedPerson);
        } catch (DuplicatePersonException dpe) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        } catch (PersonNotFoundException pnfe) {
            throw new AssertionError(MESSAGE_MISSING_PERSON);
        }
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        model.updateListToShowAll();
        return new CommandResult(String.format(MESSAGE_BIRTHDAY_PERSON_SUCCESS, editedPerson));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof BirthdayCommand // instanceof handles nulls
                && index.equals(((BirthdayCommand)other).index)
                && birthday.equals(((BirthdayCommand)other).birthday));
    }
}
```
###### \java\seedu\address\logic\commands\DeleteCommand.java
``` java
    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();
        StringJoiner joiner = new StringJoiner(", ");
        for (int i = targetIndex.length - 1; i >= 0; i--) {
            if (targetIndex[i].getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }

            ReadOnlyPerson personToDelete = lastShownList.get(targetIndex[i].getZeroBased());

            try {
                model.deletePerson(personToDelete);
                joiner.add(personToDelete.getName().toString());
            } catch (PersonNotFoundException pnfe) {
                assert false : MESSAGE_MISSING_PERSON;
            }
        }

        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, joiner.toString()));
    }
```
###### \java\seedu\address\logic\commands\EditCommand.java
``` java
    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(ReadOnlyPerson personToEdit,
                                             EditPersonDescriptor editPersonDescriptor) {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        Address updatedAddress = editPersonDescriptor.getAddress().orElse(personToEdit.getAddress());
        Remark updatedRemark = editPersonDescriptor.getRemark().orElse(personToEdit.getRemark());
        Birthday updatedBirthday = editPersonDescriptor.getBirthday().orElse(personToEdit.getBirthday());
        Set<Tag> updatedTags = editPersonDescriptor.getTags().orElse(personToEdit.getTags());
        ProfilePicture picture = personToEdit.getPicture();
        Favourite favourite = personToEdit.getFavourite();
        NumTimesSearched numTimesSearched = personToEdit.getNumTimesSearched();
        return new Person(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedRemark, updatedBirthday,
                updatedTags, picture, favourite, numTimesSearched);
    }
```
###### \java\seedu\address\logic\commands\ImageCommand.java
``` java
/**
 * Command to add/edit/remove image of Person
 */
public class ImageCommand extends UndoableCommand {
    public static final String COMMAND_WORD = "image";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Changes the profile picture of the specified person in the addressbook.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_IMAGE_SUCCESS = "Changed Profile Picture: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "Duplicate person in addressbook";
    public static final String DEFAULT = "default";
    private static final String MESSAGE_MISSING_PERSON = "The target person cannot be missing";

    public final Index index;
    public final boolean remove;

    public ImageCommand(Index index, boolean remove) {
        requireNonNull(index);

        this.index = index;
        this.remove = remove;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();
        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        ReadOnlyPerson personToEdit = lastShownList.get(index.getZeroBased());
        try {
            if (remove) {
                Person editedPerson = new Person(personToEdit.getName(), personToEdit.getPhone(), personToEdit.getEmail(),
                        personToEdit.getAddress(), personToEdit.getRemark(), personToEdit.getBirthday(),
                        personToEdit.getTags(), new ProfilePicture(DEFAULT), personToEdit.getFavourite());
                model.updatePerson(personToEdit, editedPerson);
            } else {
                model.changeImage(personToEdit);
                ReadOnlyPerson edited = lastShownList.get(index.getZeroBased());
                model.updatePerson(personToEdit, edited);
            }
        } catch (DuplicatePersonException dpe) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        } catch (PersonNotFoundException pnfe) {
            assert false : MESSAGE_MISSING_PERSON;
        }
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        model.updateListToShowAll();
        return new CommandResult(String.format(MESSAGE_IMAGE_SUCCESS, personToEdit));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ImageCommand // instanceof handles nulls
                && this.index.equals(((ImageCommand) other).index)); // state check
    }
}
```
###### \java\seedu\address\logic\commands\MapCommand.java
``` java
/**
 *  Shows a person's address on Google Maps in browser
 */
public class MapCommand extends UndoableCommand {
    public static final String COMMAND_WORD = "map";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows the address on Google Maps of the person "
            + "identified by the index number used in the last person listing. "
            + "Parameters: INDEX (must be a positive integer) "
            + "Example: " + COMMAND_WORD + " 1 ";
    public static final String MESSAGE_MAP_SHOWN_SUCCESS = "Map for Person: %1$s";
    private static final String MESSAGE_MISSING_PERSON = "The target person cannot be missing";

    public final Index index;

    public MapCommand (Index index) {
        this.index = index;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        ReadOnlyPerson personToShow = lastShownList.get(index.getZeroBased());

        try {
            model.mapPerson(personToShow);
        } catch (PersonNotFoundException pnfe) {
            assert false : MESSAGE_MISSING_PERSON;
        }

        return new CommandResult(String.format(MESSAGE_MAP_SHOWN_SUCCESS, personToShow));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof MapCommand // instanceof handles nulls
                && this.index.equals(((MapCommand) other).index)); // state check
    }
}
```
###### \java\seedu\address\logic\commands\RemoveTagCommand.java
``` java
/**
 * Removes the specified tag from all persons in addressbook
 */
public class RemoveTagCommand extends UndoableCommand {
    public static final String COMMAND_WORD = "removetag";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Removes the specified tag from all persons in the addressbook.\n"
            + "Parameters: TAG (must be a valid tag)\n"
            + "Example: " + COMMAND_WORD + " friends";

    public static final String MESSAGE_REMOVE_TAG_SUCCESS = "Removed Tag: %1$s";

    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";

    public static final String MESSAGE_TAG_NOT_FOUND = "Specified tag is not found";
    public static final String MESSAGE_MISSING_PERSON = "The target person cannot be missing";

    public final Tag target;

    public RemoveTagCommand(Tag target) {
        this.target = target;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();
        try {
            model.removeTag(target);
        } catch (UniqueTagList.TagNotFoundException tnf) {
            throw new CommandException(MESSAGE_TAG_NOT_FOUND);
        }
        for (ReadOnlyPerson person: lastShownList) {
            if (person.getTags().contains(target)) {
                Set<Tag> updatedTags = new HashSet<Tag>(person.getTags());
                updatedTags.remove(target);
                Person editedPerson = new Person(person.getName(), person.getPhone(), person.getEmail(),
                        person.getAddress(), person.getRemark(), person.getBirthday(), updatedTags,
                        person.getPicture(), person.getFavourite(), person.getNumTimesSearched());
                try {
                    model.updatePerson(person, editedPerson);
                } catch (DuplicatePersonException dpe) {
                    throw new CommandException(MESSAGE_DUPLICATE_PERSON);
                } catch (PersonNotFoundException pnfe) {
                    throw new AssertionError(MESSAGE_MISSING_PERSON);
                }
            }
        }
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_REMOVE_TAG_SUCCESS, target));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof RemoveTagCommand // instanceof handles nulls
                && this.target.equals(((RemoveTagCommand) other).target)); // state check
    }
}
```
###### \java\seedu\address\logic\parser\BirthdayCommandParser.java
``` java
/**
 * Parses arguments and returns BirthdayCommand
 */
public class BirthdayCommandParser implements Parser<BirthdayCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the BirthdayCommand
     * and returns a BirthdayCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public BirthdayCommand parse(String args) throws ParseException {
        requireNonNull(args);

        String[] splitArgs = args.trim().split(" ");

        Index index;
        Birthday birthday;
        try {
            index = ParserUtil.parseIndex(splitArgs[0]);
            if (splitArgs.length < 2) {
                throw new IllegalValueException(Birthday.MESSAGE_BIRTHDAY_CONSTRAINTS);
            }
            birthday = new Birthday(splitArgs[1]);
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, BirthdayCommand.MESSAGE_USAGE), ive);
        }

        return new BirthdayCommand(index, birthday);
    }
}
```
###### \java\seedu\address\logic\parser\DeleteCommandParser.java
``` java
    /**
     * Parses the given {@code String} of arguments in the context of the DeleteCommand
     * and returns an DeleteCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public DeleteCommand parse(String args) throws ParseException {
        try {
            String[] arguments = args.trim().split(" ");
            Index[] index = new Index[arguments.length];
            int i = 0;
            for (String e: arguments) {
                index[i++] = ParserUtil.parseIndex(e);
            }
            return new DeleteCommand(index);
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        }
    }
```
###### \java\seedu\address\logic\parser\ImageCommandParser.java
``` java
/**
 * Parses input arguments and creates a new ImageCommand object
 */
public class ImageCommandParser implements Parser<ImageCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the ImageCommand
     * and returns an ImageCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public ImageCommand parse(String args) throws ParseException {
        String[] splitArgs = args.trim().split(" ");
        try {
            Index index = ParserUtil.parseIndex(splitArgs[0]);
            if (splitArgs.length > 1 && splitArgs[1].toLowerCase().equals("remove")) {
                return new ImageCommand(index, true);
            } else {
                return new ImageCommand(index, false);
            }
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ImageCommand.MESSAGE_USAGE));
        }
    }
}
```
###### \java\seedu\address\logic\parser\MapCommandParser.java
``` java
/**
 * Parses input arguments and creates a new MapCommand object
 */
public class MapCommandParser implements Parser<MapCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the MapCommand
     * and returns an MapCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public MapCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new MapCommand(index);
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, MapCommand.MESSAGE_USAGE));
        }
    }
}
```
###### \java\seedu\address\logic\parser\RemoveTagCommandParser.java
``` java
/**
 * Parses input arguments and creates a new RemoveTagCommand object
 */
public class RemoveTagCommandParser implements Parser<RemoveTagCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the RemoveTagCommand
     * and returns an RemoveTagCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public RemoveTagCommand parse(String arg) throws ParseException {
        try {
            Tag t = ParserUtil.parseTag(arg);
            return new RemoveTagCommand(t);
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemoveTagCommand.MESSAGE_USAGE));
        }
    }
}
```
###### \java\seedu\address\MainApp.java
``` java
    /**
     * Start a tray icon running in background
     */
    private void startTray() {
        Platform.setImplicitExit(false);
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        Image image = new Image("images/address_book_15.png");

        PopupMenu popup = new PopupMenu();
        trayIcon = new TrayIcon(SwingFXUtils.fromFXImage(image, null), "PocketBook", popup);
        tray = SystemTray.getSystemTray();

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        stop();
                    }
                });
            }
        });

        trayIcon.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 2 && !e.isConsumed()) {
                    e.consume();
                    if (ui.isShowing()) {
                        Platform.runLater(new Runnable() {
                            @Override public void run() {
                                ui.hide();
                            }
                        });
                    } else {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                ui.show();
                            }
                        });
                    }
                }
            }
        });
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }
    }
```
###### \java\seedu\address\model\AddressBook.java
``` java
    public void removeTag(Tag t) throws UniqueTagList.TagNotFoundException {
        tags.remove(t);
    }
```
###### \java\seedu\address\model\BirthdayNotifier.java
``` java
/**
 * Checks current date against birthday of all persons
 */
public class BirthdayNotifier {
    public BirthdayNotifier(List<ReadOnlyPerson> list) {
        LocalDateTime now = LocalDateTime.now();
        int date = now.getDayOfMonth();
        int month = now.getMonthValue();
        ArrayList<String> people = new ArrayList<>();

        for (ReadOnlyPerson e: list) {
            if (e.getDay() == date && e.getMonth() == month) {
                people.add(e.getName().toString());
            }
        }
        if (people.size() > 0) {
            createPopup(people.toArray(new String[people.size()]));
        }
    }
    void createPopup(String[] person) {
        new BirthdayPopup(person);
    }
}
```
###### \java\seedu\address\model\Model.java
``` java
    /**
     * Deletes the given tag from all persons in addressbook
     */
    void removeTag(Tag target) throws UniqueTagList.TagNotFoundException;

    /**
     * Shows the map for selected person in browser
     */
    void mapPerson(ReadOnlyPerson target) throws PersonNotFoundException;

    /**
     * Edits the profile picture for selected person
     */
    void changeImage(ReadOnlyPerson target) throws PersonNotFoundException;
}
```
###### \java\seedu\address\model\ModelManager.java
``` java
    @Override
    public void removeTag(Tag target) throws UniqueTagList.TagNotFoundException {
        addressBook.removeTag(target);
        indicateAddressBookChanged();
    }

    @Override
    public void mapPerson(ReadOnlyPerson target) throws PersonNotFoundException {
        raise(new MapPersonEvent(target));
    }

    @Override
    public void changeImage(ReadOnlyPerson target) throws PersonNotFoundException {
        raise(new ChangeImageEvent(target));
    }

```
###### \java\seedu\address\model\person\Birthday.java
``` java
/**
 * Represents a Person's birthday in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidBirthday(String)}
 */
public class Birthday {

    public static final String MESSAGE_BIRTHDAY_CONSTRAINTS =
            "Birthdays can only contain numbers, and should be in the format dd-mm--yyyy";
    public static final String BIRTHDAY_VALIDATION_REGEX = "^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1"
            + "|(?:(?:29|30)(\\/|-|\\.)(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\"
            + "/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468]"
            + "[048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\"
            + "4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";
    public final String value;
    public final int day; //Not implemented yet
    public final int month;
    public final int year;

    /**
     * Validates given birthday.
     *
     * @throws IllegalValueException if given birthday string is invalid.
     */
    public Birthday(String birthday) throws IllegalValueException {
        requireNonNull(birthday);
        String trimmedBirthday = birthday.trim();
        if (birthday.equals("") || birthday.equals("Not Set") || birthday.equals("remove")) {
            this.value = "Not Set";
            day = month = year = 0;
        } else if (!isValidBirthday(trimmedBirthday) || !isDateCorrect(trimmedBirthday.split("-"))) {
            throw new IllegalValueException(MESSAGE_BIRTHDAY_CONSTRAINTS);
        } else {
            this.value = trimmedBirthday;
            String[] splitBirthday = value.split("-");
            this.day = Integer.parseInt(splitBirthday[0]);
            this.month = Integer.parseInt(splitBirthday[1]);
            this.year = Integer.parseInt(splitBirthday[2]);
        }

    }

    /**
     * Determines if date entered by user is correct and ensures that it is not after current date
     * @param birthday
     * @return
     */
    public static boolean isDateCorrect(String[] birthday) {
        LocalDateTime now = LocalDateTime.now();
        int day = now.getDayOfMonth();
        int month = now.getMonthValue();
        int year = now.getYear();

        int inputDay = Integer.parseInt(birthday[0]);
        int inputMonth = Integer.parseInt(birthday[1]);
        int inputYear = Integer.parseInt(birthday[2]);

        if (inputYear > year) {
            return false;
        } else if (inputYear == year && inputMonth > month) {
            return false;
        } else if (inputYear == year && inputMonth == month && inputDay > day) {
            return false;
        } else {
            return true;
        }
    }
    /**
     * Returns true if a given string is a valid person birthday.
     */
    public static boolean isValidBirthday(String test) {
        return test.matches(BIRTHDAY_VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Birthday // instanceof handles nulls
                && this.value.equals(((Birthday) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

}
```
###### \java\seedu\address\model\person\ProfilePicture.java
``` java
/**
 * Represents a profile picture for Person
 */
public class ProfilePicture {

    public final String imageLocation;

    /**
     * Validates given name.
     *
     * @throws IllegalValueException if given name string is invalid.
     */
    public ProfilePicture(String location) {
        requireNonNull(location);
        imageLocation = location;
    }

    public String getLocation() {
        return imageLocation;
    }
    @Override
    public String toString() {
        return imageLocation;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ProfilePicture // instanceof handles nulls
                && this.imageLocation.equals(((ProfilePicture) other).imageLocation)); // state check
    }

    @Override
    public int hashCode() {
        return imageLocation.hashCode();
    }
}
```
###### \java\seedu\address\storage\XmlImageStorage.java
``` java
/**
 * Creates folder to store all images saved by user
 */
public class XmlImageStorage {

    private static final Logger logger = LogsCenter.getLogger(XmlImageStorage.class);

    /**
     * Save selected image to image folder
     * @throws IOException
     */
    public void saveImage(File image, String name) throws IOException {
        requireNonNull(image);
        requireNonNull(name);

        File file = new File(name + ".png");
        Files.copy(image.toPath(), file.toPath(), REPLACE_EXISTING);
    }

    /**
     * Deletes the selected image from folder
     * @param image
     * @throws IOException
     */
    public void removeImage(File image) throws IOException {
        requireNonNull(image);

        File[] files = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath())
                .listFiles();

        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().equals(image.getName())) {
                files[i].delete();
            }
        }
    }
}
```
###### \java\seedu\address\ui\BirthdayPopup.java
``` java
/**
 * Creates a birthday notification
 */
public class BirthdayPopup {
    private JDialog frame = new JDialog();
    private GridBagConstraints constraints = new GridBagConstraints();

    public BirthdayPopup(String[] person) {
        createFrame(person.length);
        createIcon();
        createCloseButton();
        createMessage(person);
        createPopup();
    }

    /**
     * Creates the dialog for the popup
     * @param size
     */
    void createFrame(int size) {
        frame.setSize(300, 125 + size * 50);
        frame.setUndecorated(true);
        frame.setLayout(new GridBagLayout());
        Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
        Insets toolHeight = Toolkit.getDefaultToolkit().getScreenInsets(frame.getGraphicsConfiguration());
        frame.setLocation(scrSize.width - frame.getWidth(), scrSize.height - toolHeight.bottom - frame.getHeight());
        frame.setAlwaysOnTop(true);
    }

    /**
     * Creates the icon for the popup
     */
    void createIcon() {
        String header = "Birthday Alert!";

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.0f;
        constraints.weighty = 1.0f;
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.fill = GridBagConstraints.BOTH;

        Icon headingIcon = new ImageIcon("src/main/resources/images/birthday_cake.png", "birthday icon");
        JLabel headingLabel = new JLabel(header);
        headingLabel.setIcon(headingIcon);
        headingLabel.setOpaque(false);
        frame.add(headingLabel, constraints);
    }

    /**
     * Creates the close button for the popup
     */
    void createCloseButton() {
        constraints.gridx++;
        constraints.weightx = 0f;
        constraints.weighty = 0f;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.NORTH;
        JButton closeButton = new JButton(new AbstractAction("x") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                frame.dispose();
            }
        });
        closeButton.setMargin(new Insets(1, 4, 1, 4));
        closeButton.setFocusable(false);
        frame.add(closeButton, constraints);
    }

    /**
     * create the message for all the person with birthday
     * @param person
     */
    private void createMessage(String[] person) {
        String message = "There are birthdays today: \n";

        constraints.gridx = 0;
        constraints.gridy++;
        constraints.weightx = 1.0f;
        constraints.weighty = 1.0f;
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel messageLabel = new JLabel(message);
        frame.add(messageLabel, constraints);
        for (String e: person) {
            constraints.gridy++;
            messageLabel = new JLabel(e);
            frame.add(messageLabel, constraints);
        }
        frame.setVisible(true);
    }

    /**
     * create a timer for the popup
     */
    private void createPopup() {
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000); // time after which pop up will be disappeared.
                    frame.dispose();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
        }.start();
    }
}
```
###### \java\seedu\address\ui\MainWindow.java
``` java
    /**
     * Opens the map window.
     */
    private void handleMapEvent(ReadOnlyPerson person) {
        MapWindow mapWindow = new MapWindow(person);
        mapWindow.show();
    }

    /**
     * Opens file browser.
     */
    private void handleImageEvent(ReadOnlyPerson person) {
        Stage parent = new Stage();
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter =
                new FileChooser.ExtensionFilter("Any Image files", "*.jpg", "*.png", "*.jpeg");
        fileChooser.getExtensionFilters().add(filter);
        File result = fileChooser.showOpenDialog(parent);
        if (result != null) {
            try {
                imageStorage.saveImage(result, person.getName().toString());
            } catch (IOException io) {
                logger.warning("failed to copy image");
            }
            person.setImage(person.getName().toString() + ".png");
        } else {
            raise(new NewResultAvailableEvent("Image Change Cancelled"));
        }
    }
```
###### \java\seedu\address\ui\MainWindow.java
``` java
    @Subscribe
    private void handleMapPanelEvent(MapPersonEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        handleMapEvent(event.getPerson());
    }
```
###### \java\seedu\address\ui\MainWindow.java
``` java
    @Subscribe
    private void handleChangeImageEvent(ChangeImageEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        handleImageEvent(event.getPerson());
    }
```
###### \java\seedu\address\ui\MapWindow.java
``` java
/**
 * Shows the map in a pop-up browser
 */
public class MapWindow extends UiPart<Region> {

    public static final String GOOGLE_MAPS_URL_PREFIX = "https://www.google.com.sg/maps?safe=off&q=";
    public static final String GOOGLE_SEARCH_URL_SUFFIX = "&cad=h";

    private static final Logger logger = LogsCenter.getLogger(MapWindow.class);
    private static final String ICON = "/images/help_icon.png";
    private static final String FXML = "MapWindow.fxml";
    private static final String TITLE = "Map";

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

        String userGuideUrl = GOOGLE_MAPS_URL_PREFIX + person.getAddress().toString().replaceAll(" ", "+")
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
        logger.fine("Showing map window for selected person.");
        dialogStage.showAndWait();
    }
}
```
###### \java\seedu\address\ui\PersonInfoPanel.java
``` java
/**
 * Shows the selected Person's info
 */
public class PersonInfoPanel extends UiPart<Region> {

    private static final String FXML = "PersonInfoPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(PersonInfoPanel.class);

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
        registerAsAnEventHandler(this);
    }

    public PersonInfoPanel() {
        super(FXML);
        setDefaultConnections();
        registerAsAnEventHandler(this);
    }

    public void updateConnections(ReadOnlyPerson person) {
        setConnections(person);
    }

    private void setDefaultConnections() {
        name.setText("");
        phone.setText("");
        address.setText("");
        email.setText("");
        birthday.setText("");
        remark.setText("");
        tags.setAccessibleText("");
        profileImage.setImage(new Image("profiles/default.png"));
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
            if (loc.equals("default")) {
                image = new Image("profiles/default.png");
            } else {
                File img = new File(loc);
                image = new Image(img.toURI().toString());
            }
            profileImage.setImage(image);
        } catch (IllegalArgumentException iae) {
            profileImage.setImage(new Image("profiles/default.png"));
        }
    }

    @Subscribe
    private void handlePersonPanelSelectionChangedEvent(PersonPanelSelectionChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        setConnections(event.getNewSelection().person);
    }
}
```
###### \resources\view\BrightTheme.css
``` css
.background {
    -fx-background-color: derive(#efefef, 20%);
    background-color: #ffffff; /* Used in the default.html file */
}

.label {
    -fx-font-size: 11pt;
    -fx-font-family: "Segoe UI Semibold";
    -fx-text-fill: #555555;
    -fx-opacity: 0.9;
}

.label-bright {
    -fx-font-size: 11pt;
    -fx-font-family: "Segoe UI Semibold";
    -fx-text-fill: white;
    -fx-opacity: 1;
}

.label-header {
    -fx-font-size: 32pt;
    -fx-font-family: "Segoe UI Light";
    -fx-text-fill: black;
    -fx-opacity: 1;
}

.text-field {
    -fx-font-size: 12pt;
    -fx-font-family: "Segoe UI Semibold";
}

.tab-pane {
    -fx-padding: 0 0 0 1;
}

.tab-pane .tab-header-area {
    -fx-padding: 0 0 0 0;
    -fx-min-height: 0;
    -fx-max-height: 0;
}

.table-view {
    -fx-base: #efefef;
    -fx-control-inner-background: #efefef;
    -fx-background-color: #efefef;
    -fx-table-cell-border-color: transparent;
    -fx-table-header-border-color: transparent;
    -fx-padding: 5;
}

.table-view .column-header-background {
    -fx-background-color: transparent;
}

.table-view .column-header, .table-view .filler {
    -fx-size: 35;
    -fx-border-width: 0 0 1 0;
    -fx-background-color: transparent;
    -fx-border-color:
        transparent
        transparent
        derive(-fx-base, 80%)
        transparent;
    -fx-border-insets: 0 10 1 0;
}

.table-view .column-header .label {
    -fx-font-size: 20pt;
    -fx-font-family: "Segoe UI Light";
    -fx-text-fill: black;
    -fx-alignment: center-left;
    -fx-opacity: 1;
}

.table-view:focused .table-row-cell:filled:focused:selected {
    -fx-background-color: -fx-focus-color;
}

.split-pane:horizontal .split-pane-divider {
    -fx-background-color: derive(#efefef, 20%);
    -fx-border-color: transparent transparent transparent #4d4d4d;
}

.split-pane {
    -fx-border-radius: 1;
    -fx-border-width: 1;
    -fx-background-color: derive(#efefef, 20%);
}

.list-view {
    -fx-background-insets: 0;
    -fx-padding: 0;
}

.list-cell {
    -fx-label-padding: 0 0 0 0;
    -fx-graphic-text-gap : 0;
    -fx-padding: 0 0 0 0;
}

.list-cell:filled:even {
    -fx-background-color: #f7f7f7;
}

.list-cell:filled:odd {
    -fx-background-color: #fefefe;
}

.list-cell:filled:selected {
    -fx-background-color: #f2f1f1;
}

.list-cell:filled:selected #cardPane {
    -fx-border-color: #3e7b91;
    -fx-border-width: 1;
}

.list-cell .label {
    -fx-text-fill: black;
}

.cell_big_label {
    -fx-font-family: "Segoe UI Semibold";
    -fx-font-size: 16px;
    -fx-text-fill: #010504;
}

.cell_small_label {
    -fx-font-family: "Segoe UI Semibold";
    -fx-font-size: 18px;
    -fx-font-style: oblique;
    -fx-text-fill: #555555;
    -fx-min-width: 70;
    -fx-min-height: 30;
}

#personInfoPanel #name {
    -fx-font-size: 28px;
}
.cell_small_label1 {
    -fx-font-family: "Segoe UI Semibold";
    -fx-font-size: 18px;
    -fx-text-fill: black;
}
.anchor-pane {
    -fx-background-color: derive(#ffffff, 20%);
    -fx-padding: 10;
    -fx-text-fill: black;
}

.pane-with-border {
    -fx-background-color: derive(#ffffff, 20%);
    -fx-border-color: derive(#efefef, 10%);
    -fx-padding: 0;
    -fx-text-fill: black;
     -fx-border-top-width: 1px;
}

.status-bar {
    -fx-background-color: derive(#ffffff, 20%);
    -fx-text-fill: black;
}

.result-display {
    -fx-background-color: transparent;
    -fx-font-family: "Segoe UI Light";
    -fx-font-size: 13pt;
    -fx-text-fill: black;
    -fx-wrap-text: true;
}

.result-display .label {
    -fx-text-fill: black !important;
}

.status-bar .label {
    -fx-font-family: "Segoe UI Light";
    -fx-text-fill: black;
}

.status-bar-with-border {
    -fx-background-color: derive(#efefef, 30%);
    -fx-border-color: derive(#1d1d1d, 25%);
    -fx-border-width: 1px;
}

.status-bar-with-border .label {
    -fx-text-fill: black;
}

.grid-pane {
    -fx-background-color: derive(#efefef, 30%);
    -fx-border-width: 1px;
    -fx-text-fill: black;
}

.grid-pane .anchor-pane {
    -fx-background-color: derive(#ffffff, 30%);
    -fx-padding: 10;
}

.context-menu {
    -fx-background-color: derive(#efefef, 50%);
}

.context-menu .label {
    -fx-text-fill: black;
}

.menu-bar {
    -fx-background-color: derive(#efefef, 20%);
}

.menu-bar .label {
    -fx-font-size: 14pt;
    -fx-font-family: "Segoe UI Light";
    -fx-text-fill: black;
    -fx-opacity: 0.9;
}

.menu .left-container {
    -fx-background-color: white;
}

/*
 * Metro style Push Button
 * Author: Pedro Duque Vieira
 * http://pixelduke.wordpress.com/2012/10/23/jmetro-windows-8-controls-on-java/
 */
.button {
    -fx-padding: 5 22 5 22;
    -fx-border-color: #020202;
    -fx-border-width: 2;
    -fx-background-radius: 0;
    -fx-background-color: #fefefe;
    -fx-font-family: "Segoe UI", Helvetica, Arial, sans-serif;
    -fx-font-size: 11pt;
    -fx-text-fill: black;
    -fx-background-insets: 0 0 0 0, 0, 1, 2;
}

.button:hover {
    -fx-background-color: #3a3a3a;
}

.button:pressed, .button:default:hover:pressed {
  -fx-background-color: white;
  -fx-text-fill: #1d1d1d;
}

.button:focused {
    -fx-border-color: white, white;
    -fx-border-width: 1, 1;
    -fx-border-style: solid, segments(1, 1);
    -fx-border-radius: 0, 0;
    -fx-border-insets: 1 1 1 1, 0;
}

.button:disabled, .button:default:disabled {
    -fx-opacity: 0.4;
    -fx-background-color: #efefef;
    -fx-text-fill: black;
}

.button:default {
    -fx-background-color: -fx-focus-color;
    -fx-text-fill: #ffffff;
}

.button:default:hover {
    -fx-background-color: derive(-fx-focus-color, 30%);
}

.dialog-pane {
    -fx-background-color: #fefefe;
}

.dialog-pane > *.button-bar > *.container {
    -fx-background-color: #fefefe;
}

.dialog-pane > *.label.content {
    -fx-font-size: 14px;
    -fx-font-weight: bold;
    -fx-text-fill: black;
}

.dialog-pane:header *.header-panel {
    -fx-background-color: derive(#fefefe, 25%);
}

.dialog-pane:header *.header-panel *.label {
    -fx-font-size: 18px;
    -fx-font-style: italic;
    -fx-fill: white;
    -fx-text-fill: black;
}

.scroll-bar {
    -fx-background-color: derive(#efefef, 20%);
}

.scroll-bar .thumb {
    -fx-background-color: derive(#efefef, 50%);
    -fx-background-insets: 3;
}

.scroll-bar .increment-button, .scroll-bar .decrement-button {
    -fx-background-color: transparent;
    -fx-padding: 0 0 0 0;
}

.scroll-bar .increment-arrow, .scroll-bar .decrement-arrow {
    -fx-shape: " ";
}

.scroll-bar:vertical .increment-arrow, .scroll-bar:vertical .decrement-arrow {
    -fx-padding: 1 8 1 8;
}

.scroll-bar:horizontal .increment-arrow, .scroll-bar:horizontal .decrement-arrow {
    -fx-padding: 8 1 8 1;
}

#cardPane {
    -fx-background-color: transparent;
    -fx-border-width: 0;
}

#commandTypeLabel {
    -fx-font-size: 11px;
    -fx-text-fill: #F70D1A;
}

#commandTextField {
    -fx-background-color: transparent #383838 transparent #383838;
    -fx-background-insets: 0;
    -fx-border-color: #383838 #383838 #383838 #383838;
    -fx-border-insets: 0;
    -fx-border-width: 2;
    -fx-border-radius: 20;
    -fx-prompt-text-fill: #0000fa;
    -fx-font-family: "Segoe UI Light";
    -fx-font-size: 13pt;
    -fx-text-fill: black;
}

#filterField, #personListPanel, #personWebpage {
    -fx-effect: innershadow(gaussian, black, 10, 0, 0, 0);
}

#resultDisplay .content {
    -fx-background-color: transparent, #e3e3e3, transparent, #e3e3e3;
    -fx-background-radius: 0;
}

#tags {
    -fx-hgap: 7;
    -fx-vgap: 3;
}

#tags .label {
    -fx-text-fill: white;
    -fx-background-color: #3e7b91;
    -fx-padding: 1 3 1 3;
    -fx-border-radius: 4;
    -fx-background-radius: 2;
    -fx-font-size: 15;
}

```
###### \resources\view\MainWindow.fxml
``` fxml
<VBox xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1" >
  <stylesheets>
    <URL value="@BrightTheme.css" />
    <URL value="@Extensions.css" />
  </stylesheets>
  <MenuBar fx:id="menuBar" VBox.vgrow="NEVER">
    <Menu mnemonicParsing="false" text="File">
      <MenuItem mnemonicParsing="false" onAction="#handleExit" text="Exit" />
    </Menu>
    <Menu mnemonicParsing="false" text="Help">
      <MenuItem fx:id="helpMenuItem" mnemonicParsing="false" onAction="#handleHelp" text="Help" />
    </Menu>
  </MenuBar>

  <SplitPane id="splitPane" fx:id="splitPane" dividerPositions="0.4" VBox.vgrow="ALWAYS">
    <VBox fx:id="personList" minWidth="340" prefWidth="2000" SplitPane.resizableWithParent="false">
      <padding>
        <Insets top="10" right="10" bottom="10" left="10" />
      </padding>
      <StackPane VBox.vgrow="NEVER" fx:id="commandBoxPlaceholder" styleClass="pane-with-border">
        <padding>
          <Insets top="5" right="10" bottom="5" left="10" />
        </padding>
      </StackPane>
      <StackPane VBox.vgrow="NEVER" fx:id="resultDisplayPlaceholder" styleClass="pane-with-border" minHeight="100" prefHeight="100" maxHeight="100">
        <padding>
          <Insets top="5" right="10" bottom="5" left="10" />
        </padding>
      </StackPane>
      <StackPane fx:id="personListPanelPlaceholder" VBox.vgrow="ALWAYS"/>
    </VBox>
    <VBox minWidth="800" SplitPane.resizableWithParent="false">
      <StackPane fx:id="infoPlaceholder" prefWidth="340" >
        <padding>
          <Insets top="10" right="10" bottom="10" left="10" />
        </padding>
      </StackPane>
      <StackPane fx:id="browserPlaceholder" prefWidth="340" >
        <padding>
            <Insets top="10" right="10" bottom="10" left="10" />
        </padding>
      </StackPane>
    </VBox>
  </SplitPane>

  <StackPane fx:id="statusbarPlaceholder" VBox.vgrow="NEVER" />
</VBox>
```
###### \resources\view\MapWindow.fxml
``` fxml
<StackPane fx:id="mapWindowRoot" xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1">
    <WebView fx:id="browser" />
</StackPane>
```
###### \resources\view\PersonInfoPanel.fxml
``` fxml
<VBox xmlns="http://javafx.com/javafx/8.0.111" xmlns:fx="http://javafx.com/fxml/1">
    <GridPane fx:id="personInfoPanel" styleClass="grid-pane" xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1">
        <ImageView fx:id="profileImage" fitHeight="200.0" fitWidth="200" preserveRatio="true" GridPane.columnIndex="0" />
      <GridPane GridPane.columnIndex="1" GridPane.hgrow="ALWAYS" GridPane.rowSpan="2" >
         <columnConstraints>
           <ColumnConstraints hgrow="NEVER" minWidth="10.0" prefWidth="100.0" />
           <ColumnConstraints hgrow="ALWAYS" minWidth="10.0" prefWidth="500.0" maxWidth="Infinity"/>
         </columnConstraints>
         <Label fx:id="name" styleClass="cell_small_label1" text="\$name" GridPane.columnIndex="0" GridPane.columnSpan="2" />
         <Label styleClass="cell_small_label" text="Phone:" GridPane.columnIndex="0" GridPane.rowIndex="1" />
         <Label fx:id="phone" styleClass="cell_small_label1" text="\$phone" GridPane.columnIndex="1" GridPane.rowIndex="1" />
         <Label styleClass="cell_small_label" text="Address:" GridPane.columnIndex="0" GridPane.rowIndex="2" />
         <Label fx:id="address" styleClass="cell_small_label1" text="\$address" GridPane.columnIndex="1" GridPane.rowIndex="2" />
         <Label styleClass="cell_small_label" text="Email:" GridPane.columnIndex="0" GridPane.rowIndex="3" />
         <Label fx:id="email" styleClass="cell_small_label1" text="\$email" GridPane.columnIndex="1"  GridPane.rowIndex="3" />
         <Label styleClass="cell_small_label" text="Birthday:" GridPane.columnIndex="0" GridPane.rowIndex="4" />
         <Label fx:id="birthday" styleClass="cell_small_label1" text="\$birthday" GridPane.columnIndex="1" GridPane.rowIndex="4" />
         <Label styleClass="cell_small_label" text="Remarks:" GridPane.columnIndex="0" GridPane.rowIndex="5" />
         <Label fx:id="remark" styleClass="cell_small_label1" text="\$remark" GridPane.columnIndex="1" GridPane.rowIndex="5" />
         <FlowPane fx:id="tags" prefHeight="23.0" prefWidth="172.0" GridPane.columnIndex="1" GridPane.rowIndex="6" />
      </GridPane>
        <columnConstraints>
            <ColumnConstraints maxWidth="410.0" minWidth="-Infinity" prefWidth="220.0" />
        </columnConstraints>
        <rowConstraints>
            <RowConstraints maxHeight="200.0" minHeight="50.0" prefHeight="200.0" />
        </rowConstraints>
    </GridPane>
</VBox>
```
