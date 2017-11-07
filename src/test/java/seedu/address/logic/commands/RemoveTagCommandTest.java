package seedu.address.logic.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static seedu.address.commons.core.Messages.MESSAGE_DUPLICATE_PERSON;
import static seedu.address.commons.core.Messages.MESSAGE_MISSING_PERSON;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javafx.collections.ObservableList;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.ModelStub;

//@@author liliwei25
public class RemoveTagCommandTest {
    private static final String INVALID_TAG = "a";
    private static final int FIRST_TAG = 1;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validTagRemove_success() throws Exception {
        Tag firstTag = model.getAddressBook().getTagList().get(FIRST_TAG);
        RemoveTagCommand removeTagCommand = prepareCommand(firstTag);

        String expectedMessage = String.format(RemoveTagCommand.MESSAGE_REMOVE_TAG_SUCCESS, firstTag);

        assertEquals(removeTagCommand.executeUndoableCommand().feedbackToUser, expectedMessage);
    }

    @Test
    public void execute_notFoundTag_failure() throws Exception {
        RemoveTagCommand removeTagCommand = prepareCommand(new Tag(INVALID_TAG));

        assertCommandFailure(removeTagCommand, model, RemoveTagCommand.MESSAGE_TAG_NOT_FOUND);
    }

    @Test
    public void execute_duplicatePerson_failure() throws Exception {
        Tag firstTag = model.getAddressBook().getTagList().get(FIRST_TAG);
        RemoveTagCommand removeTagCommand = prepareCommandForDuplicateException(firstTag);

        thrown.expect(CommandException.class);
        thrown.expectMessage(MESSAGE_DUPLICATE_PERSON);

        removeTagCommand.execute();
    }

    @Test
    public void execute_missingPerson_failure() throws Exception {
        Tag firstTag = model.getAddressBook().getTagList().get(FIRST_TAG);
        RemoveTagCommand removeTagCommand = prepareCommandForNotFoundException(firstTag);

        thrown.expect(AssertionError.class);
        thrown.expectMessage(MESSAGE_MISSING_PERSON);

        removeTagCommand.execute();
    }

    @Test
    public void equals() throws Exception {
        final RemoveTagCommand standardCommand = new RemoveTagCommand(new Tag(VALID_TAG_FRIEND));

        // same values -> returns true
        RemoveTagCommand commandWithSameValues = new RemoveTagCommand(new Tag(VALID_TAG_FRIEND));
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new RemoveTagCommand(new Tag(VALID_TAG_HUSBAND))));
    }

    /**
     * Returns an {@code BirthdayCommand} with parameters {@code index} and {@code birthday}
     */
    private RemoveTagCommand prepareCommand(Tag target) {
        RemoveTagCommand removeTagCommand = new RemoveTagCommand(target);
        removeTagCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        return removeTagCommand;
    }

    /**
     * Returns an {@code RemoveTagCommand} with parameters {@code tag}
     * to test {@code DuplicatePersonException}
     */
    private RemoveTagCommand prepareCommandForDuplicateException(Tag tag) {
        RemoveTagCommand removeTagCommand = new RemoveTagCommand(tag);
        removeTagCommand.setData(new ModelStubThrowingDuplicatePersonException(), new CommandHistory(),
                new UndoRedoStack());
        return removeTagCommand;
    }

    /**
     * Returns an {@code RemoveTagCommand} with parameters {@code tag}
     * to test {@code PersonNotFoundException}
     */
    private RemoveTagCommand prepareCommandForNotFoundException(Tag tag) {
        RemoveTagCommand removeTagCommand = new RemoveTagCommand(tag);
        removeTagCommand.setData(new ModelStubThrowingPersonNotFoundException(), new CommandHistory(),
                new UndoRedoStack());
        return removeTagCommand;
    }

    /**
     * A Model stub that always throw a DuplicatePersonException when trying to update model.
     */
    private class ModelStubThrowingDuplicatePersonException extends ModelStub {
        @Override
        public ObservableList<ReadOnlyPerson> getFilteredPersonList() {
            return model.getFilteredPersonList();
        }

        @Override
        public void updatePerson(ReadOnlyPerson target, ReadOnlyPerson editedPerson)
                throws DuplicatePersonException {
            throw new DuplicatePersonException();
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return new AddressBook();
        }
    }

    /**
     * A Model stub that always throw a {@code PersonNotFoundException} when trying to update model.
     */
    private class ModelStubThrowingPersonNotFoundException extends ModelStub {
        @Override
        public ObservableList<ReadOnlyPerson> getFilteredPersonList() {
            return model.getFilteredPersonList();
        }

        @Override
        public void updatePerson(ReadOnlyPerson target, ReadOnlyPerson editedPerson) throws PersonNotFoundException {
            throw new PersonNotFoundException();
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return new AddressBook();
        }
    }
}
