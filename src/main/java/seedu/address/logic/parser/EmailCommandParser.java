package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.ParserUtil.COMMA_STRING;
import static seedu.address.logic.parser.ParserUtil.EMPTY_STRING;
import static seedu.address.logic.parser.ParserUtil.SPACE_STRING;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.EmailCommand;
import seedu.address.logic.parser.exceptions.ParseException;
//@@author justintkj
/**
 * Parses input arguments and creates a new EmailCommand object
 */
public class EmailCommandParser implements Parser<EmailCommand> {

    public static final int FIRST_PART_MESSAGE = 0;
    public static final int SECOND_PART_MESSAGE = 1;
    public static final int THIRD_PART_MESSAGE = 2;
    public static final int THREE_PARTS = 3;

    /**
     * Parses the given {@code String} of arguments in the context of the EmailCommand
     * and returns a Email object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public EmailCommand parse(String args) throws ParseException {
        requireNonNull(args);
        assert args != EMPTY_STRING;
        Index index;
        String subject;
        String message;
        try {
            String[] messages = args.trim().split(COMMA_STRING);
            checkValidNumberOfArguments(messages);
            String[] splitArgs = messages[FIRST_PART_MESSAGE].trim().split(SPACE_STRING);
            index = ParserUtil.parseIndex(splitArgs[FIRST_PART_MESSAGE]);
            subject = (messages[SECOND_PART_MESSAGE]);
            message = (messages[THIRD_PART_MESSAGE]);
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EmailCommand.MESSAGE_USAGE));
        }
        return new EmailCommand(index, subject, message);
    }


    /**
     * Validates there are three different message parts
     *
     * @param messages Arrays of string message
     * @throws ParseException if messsages is not of three parts
     */
    private void checkValidNumberOfArguments(String[] messages) throws ParseException {
        if (messages.length != THREE_PARTS) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EmailCommand.MESSAGE_USAGE));
        }
    }
}
