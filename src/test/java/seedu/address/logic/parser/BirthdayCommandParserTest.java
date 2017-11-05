package seedu.address.logic.parser;

import static seedu.address.logic.commands.CommandTestUtil.BIRTHDAY_BOB;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.Test;

import seedu.address.logic.commands.BirthdayCommand;
import seedu.address.model.person.Birthday;

//@@author liliwei25
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
        assertParseFailure(parser, "a", ParserUtil.MESSAGE_INVALID_INDEX);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid birthday
        assertParseFailure(parser, " 1 " + "0000", Birthday.MESSAGE_WRONG_DATE);

        // invalid index
        assertParseFailure(parser,  " -1 " + "12-12-2012", ParserUtil.MESSAGE_INVALID_INDEX);
    }
}
