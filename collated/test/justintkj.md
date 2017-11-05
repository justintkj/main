# justintkj
###### \java\seedu\address\logic\commands\RedoCommandTest.java
``` java
    @Test
    public void alternative() throws Exception {
        UndoRedoStack undoRedoStack = prepareStack(
                Collections.emptyList(), Arrays.asList(deleteCommandOne, deleteCommandOne));
        RedoCommand redoCommand = new RedoCommand(2);
        redoCommand.setData(model, EMPTY_COMMAND_HISTORY, undoRedoStack);
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        // multiple commands in redoStack
        deleteFirstPerson(expectedModel);
        deleteFirstPerson(expectedModel);
        assertCommandSuccess(redoCommand, model, RedoCommand.MESSAGE_SUCCESS, expectedModel);

        // no command in redoStack
        assertCommandFailure(redoCommand, model, RedoCommand.MESSAGE_FAILURE);
    }
}
```
###### \java\seedu\address\logic\commands\RemarkCommandTest.java
``` java
public class RemarkCommandTest {

    @Test
    public void equals() {
        final RemarkCommand standardCommand = new RemarkCommand(INDEX_FIRST_PERSON, VALID_REMARK_AMY);
        // same values -> returns true
        RemarkCommand commandWithSameValues = new RemarkCommand(INDEX_FIRST_PERSON, VALID_REMARK_AMY);
        assertTrue(standardCommand.equals(commandWithSameValues));
        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));
        // null -> returns false
        assertFalse(standardCommand.equals(null));
        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));
        // different index -> returns false
        assertFalse(standardCommand.equals(new RemarkCommand(INDEX_SECOND_PERSON, VALID_REMARK_AMY)));
        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new RemarkCommand(INDEX_FIRST_PERSON, VALID_REMARK_BOB)));
    }
}
```
###### \java\seedu\address\logic\commands\SortCommandTest.java
``` java
/**
 * Contains integration tests (interaction with the Model) for {@code SortCommand}.
 */
public class SortCommandTest {

    public static final String SORT_NAME_ARG = "name";
    public static final String SORT_EMAIL_ARG = "email";
    public static final String SORT_ADDRESS_ARG = "address";
    public static final String SORT_NUM_ARG = "number";
    public static final String SORT_NUM_ARG_CAMELCASE = "NuMBeR";
    private Model model;
    private Model expectedModel;
    private SortCommand sortCommand;

    @Before
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }
    @Test
    public void execute_showsSameList() {
        //Sort name -> command parsed successful
        sortCommand = new SortCommand(SORT_NAME_ARG);
        sortCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        assertCommandSuccess(sortCommand, model, SortCommand.MESSAGE_SORT_SUCCESS + SORT_NAME_ARG, expectedModel);

        //Sort email -> command parsed successful
        expectedModel = new ModelManager(getSortedEmailAddressBook(), new UserPrefs());
        sortCommand = new SortCommand(SORT_EMAIL_ARG);
        sortCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        assertCommandSuccess(sortCommand, model, SortCommand.MESSAGE_SORT_SUCCESS + SORT_EMAIL_ARG, expectedModel);

        //Sort address -> command parsed successful
        expectedModel = new ModelManager(getSortedAddressAddressBook(), new UserPrefs());
        sortCommand = new SortCommand(SORT_ADDRESS_ARG);
        sortCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        assertCommandSuccess(sortCommand, model, SortCommand.MESSAGE_SORT_SUCCESS + SORT_ADDRESS_ARG, expectedModel);

        //Sort number -> command parsed successful
        expectedModel = new ModelManager(getSortedNumAddressBook(), new UserPrefs());
        sortCommand = new SortCommand(SORT_NUM_ARG);
        sortCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        assertCommandSuccess(sortCommand, model, SortCommand.MESSAGE_SORT_SUCCESS + SORT_NUM_ARG, expectedModel);

        //Sort number CamelCase -> command parsed successful
        expectedModel = new ModelManager(getSortedNumAddressBook(), new UserPrefs());
        sortCommand = new SortCommand(SORT_NUM_ARG);
        sortCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        assertCommandSuccess(sortCommand, model, SortCommand.MESSAGE_SORT_SUCCESS + SORT_NUM_ARG, expectedModel);

    }
}

```
###### \java\seedu\address\logic\commands\UndoCommandTest.java
``` java
    @Test
    public void alternative() throws Exception {
        UndoRedoStack undoRedoStack = prepareStack(
                Arrays.asList(deleteCommandOne, deleteCommandTwo), Collections.emptyList());
        UndoCommand undoCommand = new UndoCommand(2);
        undoCommand.setData(model, EMPTY_COMMAND_HISTORY, undoRedoStack);
        deleteCommandOne.execute();
        deleteCommandTwo.execute();

        // multiple commands in undoStack
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());;
        assertCommandSuccess(undoCommand, model, UndoCommand.MESSAGE_SUCCESS, expectedModel);

        // single command in undoStack
        undoRedoStack = prepareStack(
                Arrays.asList(deleteCommandOne), Collections.emptyList());
        undoCommand = new UndoCommand();
        undoCommand.setData(model, EMPTY_COMMAND_HISTORY, undoRedoStack);
        deleteCommandOne.execute();
        expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        assertCommandSuccess(undoCommand, model, UndoCommand.MESSAGE_SUCCESS, expectedModel);
    }
}
```
###### \java\seedu\address\logic\parser\AddCommandParserTest.java
``` java
    @Test
    public void parse_allFieldsPresentAlternative_success() {
        Person expectedPerson = new PersonBuilder().withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withEmail(VALID_EMAIL_BOB).withAddress(VALID_ADDRESS_BOB)
                .withTags().withBirthday("11-11-2010").build();
        //Valid input format - Accepted
        assertParseSuccess(parser, "Bob Choo, 22222222 Block 123 Bobby Street 3 #01-123 "
                + "bob@example.com" + " 11-11-2010", new AddCommand(expectedPerson));

        //Multiple phone, 1st Phone accepted - Accepted
        assertParseSuccess(parser, "Bob Choo, 22222222 33333333 Block 123 Bobby Street 3 #01-123 "
                + "bob@example.com" + " 11-11-2010", new AddCommand(expectedPerson));

        //Multiple Email, 1st Email accepted - Accepted
        assertParseSuccess(parser, "Bob Choo, 22222222 Block 123 Bobby Street 3 #01-123 "
                + "bob@example.com  pop@example.com" + " 11-11-2010", new AddCommand(expectedPerson));

        //Multiple Addresses, 1st Address accepted - Accepted
        assertParseSuccess(parser, "Bob Choo, 22222222 Block 123 Bobby Street 3 #01-123 "
                + "bob@example.com  Block 122 Poppy Street 88 #11-111" + " 11-11-2010", new AddCommand(expectedPerson));

        //Multiple Birthdays, 1st Birthday accepted - Accepted
        assertParseSuccess(parser, "Bob Choo, 22222222 Block 123 Bobby Street 3 #01-123 "
                + "bob@example.com " + " 11-11-2010" + " 12-12-2012", new AddCommand(expectedPerson));

    }
```
###### \java\seedu\address\logic\parser\AddCommandParserTest.java
``` java
    @Test
    public void parse_altcompulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);

        // missing name field
        assertParseFailure(parser, "22222222 Block 123 Bobby Street 3 #01-123 "
                + "bob@example.com" + " 11-11-2010", "Missing Name!\n" + AddCommand.MESSAGE_USAGE_ALT);
        // missing phone field
        assertParseFailure(parser, "JohnDoe, Block 123 Bobby Street 3 #01-123 "
                + "bob@example.com" + " 11-11-2010", "Number should be 8 digits long!\n"
                + AddCommand.MESSAGE_USAGE_ALT);
        // missing Address/Block field
        assertParseFailure(parser, "JohnDoe, 11111111 Bobby Street 3 #01-123 "
                + "bob@example.com" + " 11-11-2010", "invalid address, Block Number. \nExample: Block 123"
                + AddCommand.MESSAGE_USAGE_ALT);
        // missing Address/Street field
        assertParseFailure(parser, "JohnDoe, 11111111 Block 123 #01-123 "
                + "bob@example.com" + " 11-11-2010", "invalid address, Street. \nExample: Jurong Street 11"
                + AddCommand.MESSAGE_USAGE_ALT);
        // missing Address/Unit field
        assertParseFailure(parser, "JohnDoe, 11111111 Block 123 Bobby Street 3 "
                + "bob@example.com" + " 11-11-2010", "invalid address, Unit. \n Example: #01-12B"
                + AddCommand.MESSAGE_USAGE_ALT);
    }
```
###### \java\seedu\address\logic\parser\EmailCommandParserTest.java
``` java
/**
 * Tests for all valid Email types of command.
 */
public class EmailCommandParserTest {

    private EmailCommandParser parser = new EmailCommandParser();

    @Test
    public void parse_validArgs_returnsEmailCommand() {
        //VALID EMAIL FORMAT
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + "," + "subject" + "," + "body";
        EmailCommand expectedCommand = new EmailCommand(INDEX_FIRST_PERSON, "subject", "body");
        assertParseSuccess(parser, userInput, expectedCommand);

        //VALID EMAIL FORMAT - extra whitespaces
        targetIndex = INDEX_FIRST_PERSON;
        userInput = " " + targetIndex.getOneBased() + "  ," + "subject" + "," + "body";
        expectedCommand = new EmailCommand(INDEX_FIRST_PERSON, "subject", "body");
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        //No Index selected
        assertParseFailure(parser, "a", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                EmailCommand.MESSAGE_USAGE));

        //Too many inputs
        assertParseFailure(parser, "1,subject,body,extra", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                EmailCommand.MESSAGE_USAGE));

        //Too little input
        assertParseFailure(parser, "1,subject", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                EmailCommand.MESSAGE_USAGE));
    }
}
```
###### \java\seedu\address\logic\parser\FavouriteCommandParserTest.java
``` java
/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the FavouriteCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the DeleteCommand, and therefore we test only one of them.
 * The path variation for those two cases occur inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class FavouriteCommandParserTest {

    public static final String FIRST_PERSON = "1";
    private FavouriteCommandParser parser = new FavouriteCommandParser();

    @Test
    public void parse_validArgs_returnsFavouriteCommand() throws IllegalValueException {
        assertParseSuccess(parser, FIRST_PERSON, new FavouriteCommand(INDEX_FIRST_PERSON,
                new Favourite("true")));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                FavouriteCommand.MESSAGE_USAGE));
    }
}
```
###### \java\seedu\address\logic\parser\RedoCommandParserTest.java
``` java
/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the MapCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the MapCommand, and therefore we test only one of them.
 * The path variation for those two cases occur inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class RedoCommandParserTest {

    private RedoCommandParser parser = new RedoCommandParser();

    @Test
    public void parse_validArgs_returnsRedoCommand() {
        assertParseSuccess(parser, "1", new RedoCommand(1));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                RedoCommand.MESSAGE_USAGE));
    }
}
```
###### \java\seedu\address\logic\parser\RemarkCommandParserTest.java
``` java
public class RemarkCommandParserTest {
    private RemarkCommandParser parser = new RemarkCommandParser();

    @Test
    public void parse_indexSpecified_failure() throws Exception {
        final String remark = "Some remark";

        //Have remarks
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + " " + PREFIX_REMARK + " " + remark;
        RemarkCommand expectedCommand = new RemarkCommand(INDEX_FIRST_PERSON, remark);
        assertParseSuccess(parser, userInput, expectedCommand);

        //No remarks
        userInput = targetIndex.getOneBased() + " " + PREFIX_REMARK;
        expectedCommand = new RemarkCommand(INDEX_FIRST_PERSON, "");
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_noFieldSpecified_failure() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE);

        // nothing at all
        assertParseFailure(parser, RemarkCommand.COMMAND_WORD, expectedMessage);
    }

}
```
###### \java\seedu\address\logic\parser\SortCommandParserTest.java
``` java
/**
 * Tests for all possible type of arguments possible for sortCommand.
 */
public class SortCommandParserTest {
    private SortCommandParser parser = new SortCommandParser();

    //Tests for valid argument, sort number
    @Test
    public void parseNumber_returnsSortCommand() {
        try {
            SortCommand newCommand = parser.parse("number");
            if (newCommand.equals(new SortCommand("number"))) {
                assert true;
            } else {
                assert false;
            }
        } catch (ParseException pe) {
            assert false;
        }
    }
    //Tests for valid argument, sort name
    @Test
    public void parseName_returnsSortCommand() {
        try {
            SortCommand newCommand = parser.parse("name");
            if (newCommand.equals(new SortCommand("name"))) {
                assert true;
            } else {
                assert false;
            }
        } catch (ParseException pe) {
            assert false;
        }
    }
    //Tests for valid argument, sort address
    @Test
    public void parseAddress_returnsSortCommand() {
        try {
            SortCommand newCommand = parser.parse("address");
            if (newCommand.equals(new SortCommand("address"))) {
                assert true;
            } else {
                assert false;
            }
        } catch (ParseException pe) {
            assert false;
        }
    }
    //Tests for valid argument, sort email
    @Test
    public void parseEmail_returnsSortCommand() {
        try {
            SortCommand newCommand = parser.parse("email");
            if (newCommand.equals(new SortCommand("email"))) {
                assert true;
            } else {
                assert false;
            }
        } catch (ParseException pe) {
            assert false;
        }
    }
    //Tests for valid argument, sort birthday
    @Test
    public void parseBirthday_returnsSortCommand() {
        try {
            SortCommand newCommand = parser.parse("birthday");
            if (newCommand.equals(new SortCommand("birthday"))) {
                assert true;
            } else {
                assert false;
            }
        } catch (ParseException pe) {
            assert false;
        }
    }
    //Tests for valid argument, sort remark
    @Test
    public void parseRemark_returnsSortCommand() {
        try {
            SortCommand newCommand = parser.parse("remark");
            if (newCommand.equals(new SortCommand("remark"))) {
                assert true;
            } else {
                assert false;
            }
        } catch (ParseException pe) {
            assert false;
        }
    }
    //Tests for valid argument, sort number with CamelCase
    @Test
    public void parseCamelCase_returnsSortCommand() {
        try {
            SortCommand newCommand = parser.parse("NuMbEr");
            if (newCommand.equals(new SortCommand("number"))) {
                assert true;
            } else {
                assert false;
            }
        } catch (ParseException pe) {
            assert false;
        }
    }

    //Tests for invalid Argument
    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
    }

    //Tests for invalid Argument , Empty
    @Test
    public void parse_emptyInvalidArgs_throwsParseException() {
        assertParseFailure(parser, "", String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
    }

    //Tests for invalid Argument , multiple valid argument
    @Test
    public void parse_multipleInvalidArgs_throwsParseException() {
        assertParseFailure(parser, "number name", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                SortCommand.MESSAGE_USAGE));
    }
}
```
###### \java\seedu\address\logic\parser\UndoCommandParserTest.java
``` java
/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the MapCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the MapCommand, and therefore we test only one of them.
 * The path variation for those two cases occur inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class UndoCommandParserTest {

    private UndoCommandParser parser = new UndoCommandParser();

    @Test
    public void parse_validArgs_returnsUndoCommand() {
        assertParseSuccess(parser, "1", new UndoCommand(1));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", String.format(MESSAGE_INVALID_COMMAND_FORMAT, UndoCommand.MESSAGE_USAGE));
    }
}
```
###### \java\systemtests\SortCommandSystemTest.java
``` java
public class SortCommandSystemTest extends AddressBookSystemTest {
    @Test
    public void sort() throws Exception {
        Model model = getModel();

        /* Case: Sort all persons by name, CamelCase */
        String command = SortCommand.COMMAND_WORD + " nAmE";
        String expectedResultMessage = SortCommand.MESSAGE_SORT_SUCCESS + "name";
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: Sort all persons by null*/
        command = SortCommand.COMMAND_WORD + "  ";
        expectedResultMessage = SortCommand.MESSAGE_SORT_FAILURE + " \n" + SortCommand.MESSAGE_USAGE;
        assertCommandFailure(command, expectedResultMessage);

        /* Case: Sort all persons by name */
        command = SortCommand.COMMAND_WORD + " name";
        expectedResultMessage = SortCommand.MESSAGE_SORT_SUCCESS + "name";
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: Sort all persons by address */
        command = SortCommand.COMMAND_WORD + " address";
        expectedResultMessage = SortCommand.MESSAGE_SORT_SUCCESS + "address";
        model.sortPerson("address");
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: Sort all persons by number */
        command = SortCommand.COMMAND_WORD + " number";
        expectedResultMessage = SortCommand.MESSAGE_SORT_SUCCESS + "number";
        model.sortPerson("number");
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: Sort all persons by remark */
        command = SortCommand.COMMAND_WORD + " remark";
        expectedResultMessage = SortCommand.MESSAGE_SORT_SUCCESS + "remark";
        model.sortPerson("remark");
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: Sort all persons by birthday */
        command = SortCommand.COMMAND_WORD + " birthday";
        expectedResultMessage = SortCommand.MESSAGE_SORT_SUCCESS + "birthday";
        model.sortPerson("birthday");
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: Sort all persons by invalid argument */
        command = SortCommand.COMMAND_WORD + " xxx";
        expectedResultMessage = SortCommand.MESSAGE_SORT_FAILURE + " \n" + SortCommand.MESSAGE_USAGE;
        assertCommandFailure(command, expectedResultMessage);

        /* Adds a person AMY to the addressBook */
        ReadOnlyPerson toAdd = AMY;
        command = "   " + AddCommand.COMMAND_WORD + "  " + NAME_DESC_AMY + "  " + PHONE_DESC_AMY + " "
                + EMAIL_DESC_AMY + "   " + ADDRESS_DESC_AMY + "   " + TAG_DESC_FRIEND + " ";
        assertCommandSuccess(command, toAdd);
        model.addPerson(toAdd);

        /* Case: Sort all persons by number */
        command = SortCommand.COMMAND_WORD + " number";
        expectedResultMessage = SortCommand.MESSAGE_SORT_SUCCESS + "number";
        model.sortPerson("number");
        assertCommandSuccess(command, model, expectedResultMessage);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String, ReadOnlyPerson)} except that the result
     * display box displays {@code expectedResultMessage} and the model related components equal to
     * {@code expectedModel}.
     */
    private void assertCommandSuccess(String command, Model expectedModel, String expectedResultMessage) {
        executeCommand(command);
        assertApplicationDisplaysExpected("", expectedResultMessage, expectedModel);
        assertSelectedCardUnchanged();
        assertCommandBoxShowsDefaultStyle();
    }
    /**
     * Performs the same verification as {@code assertCommandSuccess(ReadOnlyPerson)}. Executes {@code command}
     * instead.
     * @see AddCommandSystemTest#assertCommandSuccess(ReadOnlyPerson)
     */
    private void assertCommandSuccess(String command, ReadOnlyPerson toAdd) {
        Model expectedModel = getModel();
        try {
            expectedModel.addPerson(toAdd);
        } catch (DuplicatePersonException dpe) {
            throw new IllegalArgumentException("toAdd already exists in the model.");
        }
        String expectedResultMessage = String.format(AddCommand.MESSAGE_SUCCESS, toAdd);

        assertCommandSuccess(command, expectedModel, expectedResultMessage);
    }
    /**
     * Executes {@code command} and verifies that the command box displays {@code command}, the result display
     * box displays {@code expectedResultMessage} and the model related components equal to the current model.
     * These verifications are done by
     * {@code AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * Also verifies that the browser url, selected card and status bar remain unchanged, and the command box has the
     * error style.
     * @see AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandFailure(String command, String expectedResultMessage) {
        Model expectedModel = getModel();
        executeCommand(command);
        assertApplicationDisplaysExpected(command, expectedResultMessage, expectedModel);
        assertSelectedCardUnchanged();
        assertCommandBoxShowsErrorStyle();
        assertStatusBarUnchanged();
    }

}
```
