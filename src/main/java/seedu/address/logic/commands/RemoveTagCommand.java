package seedu.address.logic.commands;

import static seedu.address.commons.core.Messages.MESSAGE_DUPLICATE_PERSON;
import static seedu.address.commons.core.Messages.MESSAGE_MISSING_PERSON;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.Person;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.UniqueTagList;

//@@author liliwei25
/**
 * Removes the specified tag from all persons in addressbook
 */
public class RemoveTagCommand extends UndoableCommand {
    public static final String COMMAND_WORD = "removetag";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Removes the specified tag from all persons in the addressbook.\n"
            + "Parameters: TAG (must be a valid tag)\n"
            + "Example: " + COMMAND_WORD + " friends";

    public static final String MESSAGE_TAG_NOT_FOUND = "Specified tag is not found";

    public static final String MESSAGE_REMOVE_TAG_SUCCESS = "Removed Tag: %1$s";

    public final Tag target;

    public RemoveTagCommand(Tag target) {
        this.target = target;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        removeTagFromAllPerson();
        removeTagFromModel();
        return new CommandResult(String.format(MESSAGE_REMOVE_TAG_SUCCESS, target));
    }

    /**
     * Removes selected {@code Tag} from all {@code Person} in address book
     *
     * @throws CommandException When selected {@code Tag} is not found in address book
     */
    private void removeTagFromAllPerson() throws CommandException {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();
        for (ReadOnlyPerson person: lastShownList) {
            if (person.getTags().contains(target)) {
                Set<Tag> updatedTags = new HashSet<>(person.getTags());

                updatedTags.remove(target);

                Person editedPerson = getEditedPerson(person, updatedTags);

                updateModel(person, editedPerson);
            }
        }
    }

    /**
     * Removes selected {@code Tag} from address book
     *
     * @throws CommandException When selected {@code Tag} is not found in address book
     */
    private void removeTagFromModel() throws CommandException {
        try {
            model.removeTag(target);
        } catch (UniqueTagList.TagNotFoundException tnf) {
            throw new CommandException(MESSAGE_TAG_NOT_FOUND);
        }
    }

    /**
     * Updates model with new {@code Person} after removing selected {@code Tag}
     *
     * @param person {@code Person} with old data
     * @param editedPerson {@code Person} with new data
     * @throws CommandException When there is duplicate of the new person found in the address book
     */
    private void updateModel(ReadOnlyPerson person, Person editedPerson) throws CommandException {
        try {
            model.updatePerson(person, editedPerson);
        } catch (DuplicatePersonException dpe) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        } catch (PersonNotFoundException pnfe) {
            throw new AssertionError(MESSAGE_MISSING_PERSON);
        }
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    /**
     * Creates a new person with the new list of {@code tag}
     *
     * @param person Selected {@code Person}
     * @param updatedTags New list of {@code Tag}
     * @return Updated {@code Person} with new list
     */
    private Person getEditedPerson(ReadOnlyPerson person, Set<Tag> updatedTags) {
        return new Person(person.getName(), person.getPhone(), person.getEmail(),
                person.getAddress(), person.getRemark(), person.getBirthday(), updatedTags,
                person.getPicture(), person.getFavourite(), person.getNumTimesSearched());
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof RemoveTagCommand // instanceof handles nulls
                && this.target.equals(((RemoveTagCommand) other).target)); // state check
    }
}
