package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_BIRTHDAY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Birthday;
import seedu.address.model.person.Email;
import seedu.address.model.person.Favourite;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.ProfilePicture;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.Remark;
import seedu.address.model.tag.Tag;

//@@author justintkj
/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    public static final int SIZE_2 = 2;

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG,
                        PREFIX_REMARK, PREFIX_BIRTHDAY);
        try {
            if (containsAnyPrefix(args)) {
                validatesAllPrefixPresent(argMultimap);
                return createNewPerson(argMultimap);
            } else {
                return alternativeCreateNewPerson(args);
            }
        } catch (IllegalValueException ive) {
            throw new ParseException(ive.getMessage(), ive);
        }
    }

    /**
     * Creates a new person without using any prefix
     * @param args input string given
     * @return New AddCommand with a new person
     * @throws IllegalValueException Invalid parameter for any of person's details
     */
    private AddCommand alternativeCreateNewPerson(String args) throws IllegalValueException {
        String[] allArgs = args.split(",");
        checkNameFormat(allArgs);

        //Initial person's details
        Name name = new Name(allArgs[0]);
        Remark remark = new Remark("");
        Birthday birthday = new Birthday("");
        Email email;
        Phone phone;
        String blocknum;
        String streetnum;
        String unitnum;
        String postalnum = "";
        Favourite favourite = new Favourite("false");
        ProfilePicture picture = new ProfilePicture("default");

        //Generate person's details
        email = getEmailFromString(args);
        blocknum = getBlockFromString(args);
        streetnum = getStreetFromString(args);
        unitnum = getUnitFromString(args);
        postalnum = getPostalFromString(args, postalnum);
        phone = getPhoneFromString(args);
        birthday = getBirthdayFromString(args, birthday);
        Address address = new Address(blocknum + ", " + streetnum + ", " + unitnum + postalnum);
        Set<Tag> tagList = new HashSet<>();
        ReadOnlyPerson person = new Person(name, phone, email, address, remark, birthday, tagList, picture,
                favourite);
        return new AddCommand(person);
    }

    /**
     * Go through the list of string to look for a valid birthday parameter
     * Chooses the first valid argument
     * @param args input string given by user
     * @param birthday Birthday object to store birthday details of a person
     * @return new Birthday if found a valid birthday
     * @throws IllegalValueException Nothing conforms to legal birthday format
     */
    private Birthday getBirthdayFromString(String args, Birthday birthday) throws IllegalValueException {
        Matcher matcher;
        boolean matchFound;
        Pattern birthpattern = Pattern.compile("\\d{1,2}-\\d{1,2}-\\d{4,4}",
                Pattern.CASE_INSENSITIVE);
        matcher = birthpattern.matcher(args);
        matchFound = matcher.find();
        if (matchFound) {
            if (Birthday.isValidBirthday(matcher.group(0))) {
                birthday = new Birthday(matcher.group(0));
            } else {
                throw new IllegalValueException("invalid birthday,\n Example: 12-09-1994");
            }
        }
        return birthday;
    }

    /**
     * Go through the list of string to look for a valid phone number parameter
     * Chooses the first valid argument
     * @param args input string given by user
     * @return new Phone if found a valid birthday
     * @throws IllegalValueException Nothing conforms to legal birthday format
     */
    private Phone getPhoneFromString(String args) throws IllegalValueException {
        Matcher matcher;
        boolean matchFound;
        Phone phone;
        Pattern phonepattern = Pattern.compile("\\ {0,1}\\d{8}\\ {0,1}");
        matcher = phonepattern.matcher(args);
        matchFound = matcher.find();
        if (!matchFound) {
            phonepattern = Pattern.compile("\\,{0,1}\\d{8}\\,{0,1}");
            matcher = phonepattern.matcher(args);
            matchFound = matcher.find();
        }
        if (matchFound) {
            phone = new Phone(matcher.group(0).trim().replace(",", ""));
        } else {
            throw new IllegalValueException("Number should be 8 digits long!\n" + AddCommand.MESSAGE_USAGE_ALT);
        }
        return phone;
    }

    /**
     * Go through the list of string to look for a valid postal parameter
     * Chooses the first valid argument
     * No exception as this field is optional
     * @param args input string given by user
     * @param postalnum empty postal number
     * @return new Postal if found a valid Postal Number
     */
    private String getPostalFromString(String args, String postalnum) {
        Matcher matcher;
        boolean matchFound;
        Pattern postal = Pattern.compile("singapore \\d{6,6}", Pattern.CASE_INSENSITIVE);
        matcher = postal.matcher(args);
        matchFound = matcher.find();
        if (matchFound) {
            postalnum = ", " + matcher.group(0);
        }
        return postalnum;
    }

    /**
     * Go through the list of string to look for a valid unit parameter
     * Chooses the first valid argument
     * @param args input string given by user
     * @return new unit if found a valid unit number
     * @throws IllegalValueException Nothing conforms to legal unit format
     */
    private String getUnitFromString(String args) throws IllegalValueException {
        Matcher matcher;
        boolean matchFound;
        String unitnum;
        Pattern unit = Pattern.compile("#\\d\\d-\\d{1,3}[a-zA-Z]{0,1}", Pattern.CASE_INSENSITIVE);
        matcher = unit.matcher(args);
        matchFound = matcher.find();
        if (matchFound) {
            unitnum = matcher.group(0);
        } else {
            throw new IllegalValueException("invalid address, Unit. \n Example: #01-12B"
                + AddCommand.MESSAGE_USAGE_ALT);
        }
        return unitnum;
    }

    /**
     * Go through the list of string to look for a valid street parameter
     * Chooses the first valid argument
     * @param args input string given by user
     * @return new street if found a valid street format
     * @throws IllegalValueException Nothing conforms to legal street format
     */
    private String getStreetFromString(String args) throws IllegalValueException {
        Matcher matcher;
        boolean matchFound;
        String streetnum;
        Pattern street = Pattern.compile("[a-zA-z]+ street \\d{1,2}", Pattern.CASE_INSENSITIVE);
        matcher = street.matcher(args);
        matchFound = matcher.find();
        if (matchFound) {
            streetnum = matcher.group(0);
        } else {
            throw new IllegalValueException("invalid address, Street. \nExample: Jurong Street 11"
                + AddCommand.MESSAGE_USAGE_ALT);
        }
        return streetnum;
    }

    /**
     * Go through the list of string to look for a valid block parameter
     * Chooses the first valid argument
     * @param args input string given by user
     * @return new block if found a valid block format
     * @throws IllegalValueException Nothing conforms to legal block format
     */
    private String getBlockFromString(String args) throws IllegalValueException {
        Matcher matcher;
        boolean matchFound;
        String blocknum;
        Pattern block = Pattern.compile("block \\d{1,3}", Pattern.CASE_INSENSITIVE);
        matcher = block.matcher(args);
        Pattern blk = Pattern.compile("blk \\d{1,3}", Pattern.CASE_INSENSITIVE);
        Matcher blkmatcher = blk.matcher(args);
        matchFound = matcher.find();
        if (!matchFound) {
            matchFound = blkmatcher.find();
            matcher = blkmatcher;
        }
        if (matchFound) {
            blocknum = matcher.group(0);
        } else {
            throw new IllegalValueException("invalid address, Block Number. \nExample: Block 123"
                + AddCommand.MESSAGE_USAGE_ALT);
        }
        return blocknum;
    }

    /**
     * Go through the list of string to look for a valid email parameter
     * Chooses the first valid argument
     * @param args input string given by user
     * @return new email if found a valid email format
     * @throws IllegalValueException Nothing conforms to legal email format
     */
    private Email getEmailFromString(String args) throws IllegalValueException {
        Matcher matcher;
        boolean matchFound;
        Email email;
        Pattern emailpattern = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+");
        matcher = emailpattern.matcher(args);
        matchFound = matcher.find();
        if (matchFound) {
            email = new Email(matcher.group(0));
        } else {
            throw new IllegalValueException("invalid email\n Example: Jason@example.com");
        }
        return email;
    }

    /**
     *  Go through the list of string to look for a valid email parameter
     * @param allArgs input string given by user
     * @throws IllegalValueException Nothing conforms to legal email format
     */
    private void checkNameFormat(String[] allArgs) throws IllegalValueException {
        if (allArgs.length < SIZE_2) {
            throw new IllegalValueException("Missing Name!\n" + AddCommand.MESSAGE_USAGE_ALT);
        }
    }

    /**
     * Adds a person using fields formatted by prefixes
     * @param argMultimap All the prefix that should be used.
     * @return A new add command with the new person.
     * @throws IllegalValueException invalid parameter type
     */
    private AddCommand createNewPerson(ArgumentMultimap argMultimap) throws IllegalValueException {
        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME)).get();
        Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE)).get();
        Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL)).get();
        Address address = ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS)).get();
        Remark remark = ParserUtil.parseRemark(argMultimap.getValue(PREFIX_REMARK)).get();
        Birthday birthday = ParserUtil.parseBirthday(argMultimap.getValue(PREFIX_BIRTHDAY)).get();
        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));
        Favourite favourite = new Favourite(Favourite.COLOR_OFF);
        ProfilePicture picture = new ProfilePicture("default");
        ReadOnlyPerson person = new Person(name, phone, email, address, remark, birthday, tagList, picture,
                favourite);
        return new AddCommand(person);
    }

    /**
     * Checks if all prefixes are present
     * @param argMultimap All the prefixes to be used
     * @throws ParseException Missing prefix
     */
    private void validatesAllPrefixPresent(ArgumentMultimap argMultimap) throws ParseException {
        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_ADDRESS, PREFIX_PHONE, PREFIX_EMAIL)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Checks if user input uses any prefix of any type
     * @param args User input
     * @return true if contains prefix, false if does not
     */
    private boolean containsAnyPrefix(String args) {
        return args.contains(PREFIX_NAME.toString()) || args.contains(PREFIX_ADDRESS.toString())
            || args.contains(PREFIX_EMAIL.toString()) || args.contains(PREFIX_PHONE.toString())
            || args.contains(PREFIX_REMARK.toString()) || args.contains(PREFIX_TAG.toString())
            || args.contains(PREFIX_BIRTHDAY.toString());
    }
    //@@author
    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
