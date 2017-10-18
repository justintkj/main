package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getSortedAddressAddressBook;
import static seedu.address.testutil.TypicalPersons.getSortedEmailAddressBook;
import static seedu.address.testutil.TypicalPersons.getSortedNumAddressBook;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.Before;
import org.junit.Test;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

public class SortCommandTest {

    public static final String SORT_NAME_ARG = "name";
    public static final String SORT_EMAIL_ARG = "email";
    public static final String SORT_ADDRESS_ARG = "address";
    public static final String SORT_NUM_ARG = "number";
    public static final String SORT_NUM_ARG_CAMELCASE = "NuM";
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

        //Sort num -> command parsed successful
        expectedModel = new ModelManager(getSortedNumAddressBook(), new UserPrefs());
        sortCommand = new SortCommand(SORT_NUM_ARG);
        sortCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        assertCommandSuccess(sortCommand, model, SortCommand.MESSAGE_SORT_SUCCESS + SORT_NUM_ARG, expectedModel);
    }
}

