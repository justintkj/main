# liliwei25
###### /java/guitests/guihandles/InfoPanelHandle.java
``` java
/**
 * A handler for the {@code InfoPanel} of the UI.
 */
public class InfoPanelHandle extends NodeHandle<Node> {
    public static final String INFO_PANEL_ID = "#personInfoPanel";

    private static final String NAME_FIELD_ID = "#name";
    private static final String ADDRESS_FIELD_ID = "#address";
    private static final String PHONE_FIELD_ID = "#phone";
    private static final String EMAIL_FIELD_ID = "#email";
    private static final String TAGS_FIELD_ID = "#tags";

    private final Label nameLabel;
    private final Label addressLabel;
    private final Label phoneLabel;
    private final Label emailLabel;
    private final List<Label> tagLabels;

    public InfoPanelHandle(Node cardNode) {
        super(cardNode);

        this.nameLabel = getChildNode(NAME_FIELD_ID);
        this.addressLabel = getChildNode(ADDRESS_FIELD_ID);
        this.phoneLabel = getChildNode(PHONE_FIELD_ID);
        this.emailLabel = getChildNode(EMAIL_FIELD_ID);

        Region tagsContainer = getChildNode(TAGS_FIELD_ID);
        this.tagLabels = tagsContainer
                .getChildrenUnmodifiable()
                .stream()
                .map(Label.class::cast)
                .collect(Collectors.toList());
    }

    public String getName() {
        return nameLabel.getText();
    }

    public String getAddress() {
        return addressLabel.getText();
    }

    public String getPhone() {
        return phoneLabel.getText();
    }

    public String getEmail() {
        return emailLabel.getText();
    }

    public List<String> getTags() {
        return tagLabels
                .stream()
                .map(Label::getText)
                .collect(Collectors.toList());
    }
}
```
###### /java/seedu/address/logic/commands/BirthdayCommandTest.java
``` java
/**
 * Test BirthdayCommand
 */
public class BirthdayCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        BirthdayCommand birthdayCommand = prepareCommand(outOfBoundIndex, BIRTHDAY_BOB);

        assertCommandFailure(birthdayCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of address book
     */
    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        showFirstPersonOnly(model);
        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        BirthdayCommand birthdayCommand = prepareCommand(outOfBoundIndex, BIRTHDAY_BOB);

        assertCommandFailure(birthdayCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final BirthdayCommand standardCommand = new BirthdayCommand(INDEX_FIRST_PERSON, BIRTHDAY_AMY);

        // same values -> returns true
        BirthdayCommand commandWithSameValues = new BirthdayCommand(INDEX_FIRST_PERSON, BIRTHDAY_AMY);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new BirthdayCommand(INDEX_SECOND_PERSON, BIRTHDAY_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new BirthdayCommand(INDEX_FIRST_PERSON, BIRTHDAY_BOB)));
    }

    /**
     * Returns an {@code BirthdayCommand} with parameters {@code index} and {@code birthday}
     */
    private BirthdayCommand prepareCommand(Index index, Birthday birthday) {
        BirthdayCommand birthdayCommand = new BirthdayCommand(index, birthday);
        birthdayCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        return birthdayCommand;
    }
}
```
###### /java/seedu/address/logic/commands/DeleteCommandTest.java
``` java
/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() throws Exception {
        ReadOnlyPerson personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = prepareCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, personToDelete.getName());

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() throws Exception {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeleteCommand deleteCommand = prepareCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() throws Exception {
        showFirstPersonOnly(model);

        ReadOnlyPerson personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = prepareCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, personToDelete.getName());

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);
        showNoPerson(expectedModel);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showFirstPersonOnly(model);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        DeleteCommand deleteCommand = prepareCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        Index[] firstPersonArray = new Index[] {INDEX_FIRST_PERSON};

        DeleteCommand deleteFirstCommand = new DeleteCommand(firstPersonArray);
        DeleteCommand deleteSecondCommand = new DeleteCommand(new Index[] {INDEX_SECOND_PERSON});

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteCommand deleteFirstCommandCopy = new DeleteCommand(firstPersonArray);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    /**
     * Returns a {@code DeleteCommand} with the parameter {@code index}.
     */
    private DeleteCommand prepareCommand(Index index) {
        DeleteCommand deleteCommand = new DeleteCommand(new Index[] {index});
        deleteCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        return deleteCommand;
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoPerson(Model model) {
        model.updateFilteredPersonList(p -> false);

        assert model.getFilteredPersonList().isEmpty();
    }
}
```
###### /java/seedu/address/logic/commands/MapCommandTest.java
``` java
/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code MapCommand}.
 */
public class MapCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() throws Exception {
        ReadOnlyPerson personToMap = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        MapCommand mapCommand = prepareCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(MapCommand.MESSAGE_MAP_SHOWN_SUCCESS, personToMap);

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.mapPerson(personToMap);

        assertCommandSuccess(mapCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() throws Exception {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        MapCommand mapCommand = prepareCommand(outOfBoundIndex);

        assertCommandFailure(mapCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showFirstPersonOnly(model);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        MapCommand mapCommand = prepareCommand(outOfBoundIndex);

        assertCommandFailure(mapCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        MapCommand mapFirstCommand = new MapCommand(INDEX_FIRST_PERSON);
        MapCommand mapSecondCommand = new MapCommand(INDEX_SECOND_PERSON);

        // same object -> returns true
        assertTrue(mapFirstCommand.equals(mapFirstCommand));

        // same values -> returns true
        MapCommand mapFirstCommandCopy = new MapCommand(INDEX_FIRST_PERSON);
        assertTrue(mapFirstCommand.equals(mapFirstCommandCopy));

        // different types -> returns false
        assertFalse(mapFirstCommand.equals(1));

        // null -> returns false
        assertFalse(mapFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(mapFirstCommand.equals(mapSecondCommand));
    }

    /**
     * Returns a {@code MapCommand} with the parameter {@code index}.
     */
    private MapCommand prepareCommand(Index index) {
        MapCommand mapCommand = new MapCommand(index);
        mapCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        return mapCommand;
    }
}
```
###### /java/seedu/address/logic/parser/BirthdayCommandParserTest.java
``` java
/**
 * Test BirthdayCommandParser
 */
public class BirthdayCommandParserTest {

    private BirthdayCommandParser parser = new BirthdayCommandParser();

    @Test
    public void parse_validArgs_returnsBirthdayCommand() {
        assertParseSuccess(parser, " 1 " + "30-03-2002",
                new BirthdayCommand(INDEX_FIRST_PERSON, BIRTHDAY_BOB));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                BirthdayCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid birthday
        assertParseFailure(parser, BirthdayCommand.COMMAND_WORD + " 1 " + "0000",
                "Invalid command format! \n" + BirthdayCommand.MESSAGE_USAGE);

        // invalid index
        assertParseFailure(parser, BirthdayCommand.COMMAND_WORD + " -1 " + "12-12-2012",
                "Invalid command format! \n" + BirthdayCommand.MESSAGE_USAGE);
    }
}
```
###### /java/seedu/address/logic/parser/MapCommandParserTest.java
``` java
/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the MapCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the MapCommand, and therefore we test only one of them.
 * The path variation for those two cases occur inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class MapCommandParserTest {

    private MapCommandParser parser = new MapCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteCommand() {
        assertParseSuccess(parser, "1", new MapCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", String.format(MESSAGE_INVALID_COMMAND_FORMAT, MapCommand.MESSAGE_USAGE));
    }
}
```
###### /java/seedu/address/model/person/BirthdayTest.java
``` java
/**
 * Test Birthday class
 */
public class BirthdayTest {

    @Test
    public void isValidBirthday() {
        // invalid birthday
        assertFalse(Birthday.isValidBirthday("1")); // incorrect format
        assertFalse(Birthday.isValidBirthday("50-03-1995")); // incorrect day

        // valid birthday
        assertTrue(Birthday.isValidBirthday("01-01-2001"));
        assertTrue(Birthday.isValidBirthday("06-06-2006"));
        assertTrue(Birthday.isValidBirthday("12-12-2012"));
    }
}
```
