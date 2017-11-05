package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.ImageCommand;
import seedu.address.logic.parser.exceptions.ParseException;

//@@author liliwei25
/**
 * Parses input arguments and creates a new ImageCommand object
 */
public class ImageCommandParser implements Parser<ImageCommand> {

    private static final String REMOVE = "remove";
    private static final boolean REMOVE_IMAGE = true;
    private static final String SPACE = " ";
    private static final int INDEX_POS = 0;
    private static final int SELECT_POS = 1;

    /**
     * Parses the given {@code String} of arguments in the context of the ImageCommand
     * and returns an ImageCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public ImageCommand parse(String args) throws ParseException {
        try {
            return getImageCommand(args);
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ImageCommand.MESSAGE_USAGE));
        }
    }

    private ImageCommand getImageCommand(String args) throws IllegalValueException {
        String[] splitArgs = args.trim().split(SPACE);
        Index index = ParserUtil.parseIndex(splitArgs[INDEX_POS]);
        if (splitArgs.length > 1 && splitArgs[SELECT_POS].toLowerCase().equals(REMOVE)) {
            return new ImageCommand(index, REMOVE_IMAGE);
        } else {
            return new ImageCommand(index, !REMOVE_IMAGE);
        }
    }
}
