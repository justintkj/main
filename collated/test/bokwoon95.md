# bokwoon95
###### /java/seedu/address/logic/parser/AddCommandParserTest.java
``` java
    @Test
    public void parse_remarkAndBirthday_success() {
        Person expectedPerson = new PersonBuilder().withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withEmail(VALID_EMAIL_BOB).withAddress(VALID_ADDRESS_BOB)
                .withTags().withRemark("Likes Trains").withBirthday("11-11-2010").build();

        //Remark & Birthday fields - Accepted
        assertParseSuccess(parser, "add n/Bob Choo p/22222222 a/Block 123, Bobby Street 3, #01-123 "
                + "e/bob@example.com" + " r/Likes Trains" + " b/11-11-2010", new AddCommand(expectedPerson));
    }
```
###### /java/seedu/address/logic/commands/FuzzyfindCommandTest.java
``` java
public class FuzzyfindCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        NameContainsSubstringsPredicate firstPredicate =
                new NameContainsSubstringsPredicate(Collections.singletonList("first"));
        NameContainsSubstringsPredicate secondPredicate =
                new NameContainsSubstringsPredicate(Collections.singletonList("second"));

        FuzzyfindCommand fuzzyfindFirstCommand = new FuzzyfindCommand(firstPredicate);
        FuzzyfindCommand fuzzyfindSecondCommand = new FuzzyfindCommand(secondPredicate);

        // same object -> returns true
        assertTrue(fuzzyfindFirstCommand.equals(fuzzyfindFirstCommand));

        // same values -> returns true
        FuzzyfindCommand findFirstCommandCopy = new FuzzyfindCommand(firstPredicate);
        assertTrue(fuzzyfindFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(fuzzyfindFirstCommand.equals(1));

        // null -> returns false
        assertFalse(fuzzyfindFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(fuzzyfindFirstCommand.equals(fuzzyfindSecondCommand));
    }

    @Test
    //Test basic Find Functionality in Fuzzyfind
    public void execute_zeroKeywords_noPersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        FuzzyfindCommand command = prepareCommand(" ");
        assertCommandSuccess(command, expectedMessage, Collections.emptyList());
    }

    @Test
    //Test basic Find Functionality in Fuzzyfind
    public void execute_multipleKeywords_multiplePersonsFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        FuzzyfindCommand command = prepareCommand("Kurz Elle Kunz");
        assertCommandSuccess(command, expectedMessage, Arrays.asList(CARL, ELLE, FIONA));
    }

    @Test
    //Test Fuzzy Find Functionality in Fuzzyfind
    public void execute_fuzzyFindv1() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 5);
        FuzzyfindCommand command = prepareCommand("e");
        assertCommandSuccess(command, expectedMessage, Arrays.asList(ALICE, BENSON, DANIEL, ELLE, GEORGE));
    }

    @Test
    //Test Fuzzy Find Functionality in Fuzzyfind
    public void execute_fuzzyFindv2() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        FuzzyfindCommand command = prepareCommand("eNsO");
        assertCommandSuccess(command, expectedMessage, Arrays.asList(BENSON));
    }

    /**
     * Parses {@code userInput} into a {@code FindCommand}.
     */
    private FuzzyfindCommand prepareCommand(String userInput) {

        //this is work that the parser will do
        String[] nameKeywords = userInput.split("\\s+");
        NameContainsSubstringsPredicate searchPredicate;
        searchPredicate = new NameContainsSubstringsPredicate(Arrays.asList(nameKeywords));

        FuzzyfindCommand command = new FuzzyfindCommand(searchPredicate);
        command.setData(model, new CommandHistory(), new UndoRedoStack());
        return command;
    }

    /**
     * Asserts that {@code command} is successfully executed, and<br>
     *     - the command feedback is equal to {@code expectedMessage}<br>
     *     - the {@code FilteredList<ReadOnlyPerson>} is equal to {@code expectedList}<br>
     *     - the {@code AddressBook} in model remains the same after executing the {@code command}
     */
    private void assertCommandSuccess(FuzzyfindCommand command, String expectedMessage,
                                      List<ReadOnlyPerson> expectedList) {
        AddressBook expectedAddressBook = new AddressBook(model.getAddressBook());
        CommandResult commandResult = command.execute();

        assertEquals(expectedMessage, commandResult.feedbackToUser);
        assertEquals(expectedList, model.getFilteredPersonList());
        assertEquals(expectedAddressBook, model.getAddressBook());
    }

}
```
###### /java/seedu/address/testutil/EditPersonDescriptorBuilder.java
``` java
    /**
     * Sets the {@code Remark} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withRemark(String remark) {
        try {
            ParserUtil.parseRemark(Optional.of(remark)).ifPresent(descriptor::setRemark);
        } catch (IllegalValueException ive) {
            throw new IllegalArgumentException("address is expected to be unique.");
        }
        return this;
    }

    /**
     * Sets the {@code Birthday} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withBirthday(String birthday) {
        try {
            ParserUtil.parseBirthday(Optional.of(birthday)).ifPresent(descriptor::setBirthday);
        } catch (IllegalValueException ive) {
            throw new IllegalArgumentException("address is expected to be unique.");
        }
        return this;
    }
```
###### /java/seedu/address/testutil/PersonBuilder.java
``` java
    /**
     * Sets the {@code Remark} of the {@code Person} that we are building.
     */
    public PersonBuilder withRemark (String remark) {
        this.person.setRemark(new Remark(remark));
        return this;
    }
```
