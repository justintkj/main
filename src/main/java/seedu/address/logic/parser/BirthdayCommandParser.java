package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.BirthdayCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Birthday;

/**
 * Parses arguments and returns BirthdayCommand
 */
public class BirthdayCommandParser implements Parser<BirthdayCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the BirthdayCommand
     * and returns a BirthdayCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public BirthdayCommand parse(String args) throws ParseException {
        requireNonNull(args);

        String[] splitArgs = args.trim().split(" ");

        Index index;
        Birthday birthday;
        try {
            index = ParserUtil.parseIndex(splitArgs[0]);
            birthday = new Birthday(splitArgs[1]);
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, BirthdayCommand.MESSAGE_USAGE));
        }

        return new BirthdayCommand(index, birthday);
    }
}