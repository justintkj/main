package seedu.address.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showFirstPersonOnly;
import static seedu.address.logic.commands.ImageCommand.DEFAULT;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.ProfilePicture;
import seedu.address.model.person.ReadOnlyPerson;

public class ImageCommandTest {
    public static final boolean REMOVE = true;
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() throws Exception {
        ReadOnlyPerson person = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        ImageCommand imageCommand = prepareCommand(INDEX_FIRST_PERSON, !REMOVE);

        String expectedMessage = String.format(ImageCommand.MESSAGE_IMAGE_SUCCESS, person);

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.changeImage(person);

        assertCommandSuccess(imageCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validIndexFilterListRemoveSuccess() throws Exception {
        ReadOnlyPerson person = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        ImageCommand imageCommand = prepareCommand(INDEX_SECOND_PERSON, REMOVE);

        String expectedMessage = String.format(ImageCommand.MESSAGE_IMAGE_SUCCESS, person);

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        ReadOnlyPerson personToEdit = expectedModel.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        Person editedPerson = new Person(personToEdit.getName(), personToEdit.getPhone(), personToEdit.getEmail(),
                personToEdit.getAddress(), personToEdit.getRemark(), personToEdit.getBirthday(),
                personToEdit.getTags(), new ProfilePicture(DEFAULT), personToEdit.getFavourite());
        expectedModel.updatePerson(personToEdit, editedPerson);
        assertCommandSuccess(imageCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() throws Exception {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        ImageCommand imageCommand = prepareCommand(outOfBoundIndex, !REMOVE);

        assertCommandFailure(imageCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showFirstPersonOnly(model);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        ImageCommand imageCommand = prepareCommand(outOfBoundIndex, REMOVE);

        assertCommandFailure(imageCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        ImageCommand imageFirstCommand = new ImageCommand(INDEX_FIRST_PERSON, !REMOVE);
        ImageCommand imageSecondCommand = new ImageCommand(INDEX_SECOND_PERSON, !REMOVE);

        // same object -> returns true
        assertTrue(imageFirstCommand.equals(imageFirstCommand));

        // same values -> returns true
        ImageCommand imageFirstCommandCopy = new ImageCommand(INDEX_FIRST_PERSON, !REMOVE);
        assertTrue(imageFirstCommand.equals(imageFirstCommandCopy));

        // different types -> returns false
        assertFalse(imageFirstCommand.equals(1));

        // null -> returns false
        assertFalse(imageFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(imageFirstCommand.equals(imageSecondCommand));
    }

    /**
     * Returns a {@code MapCommand} with the parameter {@code index}.
     */
    private ImageCommand prepareCommand(Index index, boolean remove) {
        ImageCommand imageCommand = new ImageCommand(index, remove);
        imageCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        return imageCommand;
    }
}
