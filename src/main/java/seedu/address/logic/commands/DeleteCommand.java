package seedu.address.logic.commands;

import static seedu.address.commons.core.Messages.MESSAGE_MISSING_PERSON;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.PersonNotFoundException;

/**
 * Deletes a person identified using it's last displayed index from the address book.
 */
public class DeleteCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "delete";
    public static final String COMMAND_ALIAS = "d";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the person identified by the index number(s) used in the last person listing.\n"
            + "Parameters: INDEX(ES) (must be a positive integer(s))\n"
            + "Example: " + COMMAND_WORD + " 1 2 3";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Person(s): %1$s";
    private static final String DELIMITER = ", ";

    public final Index[] targetIndex;

    public DeleteCommand(Index[] targetIndex) {
        this.targetIndex = targetIndex;
    }

    //@@author liliwei25
    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        StringJoiner deletedPersons = deleteAllSelectedPersonFromAddressBook();
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, deletedPersons.toString()));
    }

    /**
     * Deletes all the selected person from address book and returns a StringJoiner containing their names
     *
     * @return A {@code StringJoiner} that includes all the names that were deleted
     * @throws CommandException when person selected is not found
     */
    private StringJoiner deleteAllSelectedPersonFromAddressBook() throws CommandException {
        StringJoiner joiner = new StringJoiner(DELIMITER);
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();
        for (int i = targetIndex.length - 1; i >= 0; i--) {
            if (targetIndex[i].getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }

            ReadOnlyPerson personToDelete = lastShownList.get(targetIndex[i].getZeroBased());

            deletePersonFromAddressBook(joiner, personToDelete);
        }
        return joiner;
    }

    /**
     * Delete selected person from address book
     *
     * @param joiner {@code StringJoiner} to join several names together if necessary
     * @param personToDelete Selected person
     */
    private void deletePersonFromAddressBook(StringJoiner joiner, ReadOnlyPerson personToDelete) {
        try {
            model.deletePerson(personToDelete);
            joiner.add(personToDelete.getName().toString());
        } catch (PersonNotFoundException pnfe) {
            assert false : MESSAGE_MISSING_PERSON;
        }
    }

    //@@author
    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteCommand // instanceof handles nulls
                && Arrays.equals(this.targetIndex, ((DeleteCommand) other).targetIndex)); // state check
    }
}
