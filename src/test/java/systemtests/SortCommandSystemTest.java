package systemtests;

import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.BIRTHDAY_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static seedu.address.testutil.TypicalPersons.AMY;

import org.junit.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.SortCommand;
import seedu.address.model.Model;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.DuplicatePersonException;

//@@author justintkj
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
                + EMAIL_DESC_AMY + "   " + ADDRESS_DESC_AMY + "   " + " r/" + AMY.getRemark().value + " "
                + BIRTHDAY_DESC_BOB + " " + TAG_DESC_FRIEND + " ";
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
