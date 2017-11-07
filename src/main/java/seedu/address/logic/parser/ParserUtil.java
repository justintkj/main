package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.StringUtil;
import seedu.address.model.person.Address;
import seedu.address.model.person.Birthday;
import seedu.address.model.person.Email;
import seedu.address.model.person.Favourite;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Remark;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 * {@code ParserUtil} contains methods that take in {@code Optional} as parameters. However, it goes against Java's
 * convention (see https://stackoverflow.com/a/39005452) as {@code Optional} should only be used a return type.
 * Justification: The methods in concern receive {@code Optional} return values from other methods as parameters and
 * return {@code Optional} values based on whether the parameters were present. Therefore, it is redundant to unwrap the
 * initial {@code Optional} before passing to {@code ParserUtil} as a parameter and then re-wrap it into an
 * {@code Optional} return value inside {@code ParserUtil} methods.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index is not a non-zero unsigned integer.";
    public static final String MESSAGE_INSUFFICIENT_PARTS = "Number of parts must be more than 1.";
    public static final String MESSAGE_INVALID_SORT = "Sort type is not a valid sort type.";
    public static final String SORTNAME_ARG = "name";
    public static final String SORTNUM_ARG = "number";
    public static final String SORTADD_ARG = "address";
    public static final String SORTEMAIL_ARG = "email";
    public static final String SORTREMARK_ARG = "remark";
    public static final String SORTBIRTHDAY_ARG = "birthday";
    public static final String SORTFAVOURITE_ARG = "favourite";
    public static final String EMPTY_STRING = "";
    public static final String SPACE_STRING = " ";
    public static final String COMMA_STRING = ",";
    public static final String COMMA_SPACE_STRING = ", ";
    public static final String SPACE_COMMMA_STRING = " ,";
    public static final int INDEX_ZERO = 0;

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws IllegalValueException if the specified index is invalid (not non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws IllegalValueException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new IllegalValueException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }
    //@@author justintkj
    /**
     * Parses {@code number} into an {@code Integer} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws IllegalValueException if the specified number is invalid (not non-zero unsigned integer).
     */
    public static int parseNumber(String number) throws IllegalValueException {
        String trimmedNumber = number.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedNumber)) {
            throw new IllegalValueException(MESSAGE_INVALID_INDEX);
        }
        return Integer.parseInt(trimmedNumber);
    }

    /**
     * Parses {@code sortType}returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws IllegalValueException if the specified index is invalid (not valid sorting type).
     */
    public static String parseSortType(String sortType) throws IllegalValueException {
        String toSort = sortType.trim().toLowerCase();
        if (!toSort.equals(SORTNAME_ARG) && !toSort.equals(SORTNUM_ARG)
            && !toSort.equals(SORTADD_ARG) && !toSort.equals(SORTEMAIL_ARG)
            && !toSort.equals(SORTREMARK_ARG) && !toSort.equals(SORTBIRTHDAY_ARG)
            && !toSort.equals(SORTFAVOURITE_ARG)) {
            throw new IllegalValueException(MESSAGE_INVALID_SORT);
        }
        return toSort;
    }
    //@@author

    /**
     * Parses a {@code Optional<String> remark} into an {@code Optional<remark>} if {@code remark} is present.
     * See header comment of this class regarding the use of {@code Optional} parameters.
     */
    public static Optional<Favourite> parseFavourite(Optional<String> favourite) throws IllegalValueException {
        requireNonNull(favourite);
        return favourite.isPresent() ? Optional.of(new Favourite(favourite.get())) : Optional.of(new Favourite(""));
    }

    /**
     * Parses a {@code Optional<String> name} into an {@code Optional<Name>} if {@code name} is present.
     * See header comment of this class regarding the use of {@code Optional} parameters.
     */
    public static Optional<Name> parseName(Optional<String> name) throws IllegalValueException {
        requireNonNull(name);
        return name.isPresent() ? Optional.of(new Name(name.get())) : Optional.empty();
    }

    /**
     * Parses a {@code Optional<String> phone} into an {@code Optional<Phone>} if {@code phone} is present.
     * See header comment of this class regarding the use of {@code Optional} parameters.
     */
    public static Optional<Phone> parsePhone(Optional<String> phone) throws IllegalValueException {
        requireNonNull(phone);
        return phone.isPresent() ? Optional.of(new Phone(phone.get())) : Optional.empty();
    }

    /**
     * Parses a {@code Optional<String> address} into an {@code Optional<Address>} if {@code address} is present.
     * See header comment of this class regarding the use of {@code Optional} parameters.
     */
    public static Optional<Address> parseAddress(Optional<String> address) throws IllegalValueException {
        requireNonNull(address);
        return address.isPresent() ? Optional.of(new Address(address.get())) : Optional.empty();
    }

    //@@author bokwoon95
    /**
     * Parses a {@code Optional<String> remark} into an {@code Optional<remark>} if {@code remark} is present.
     * See header comment of this class regarding the use of {@code Optional} parameters.
     */
    public static Optional<Remark> parseRemark(Optional<String> remark) throws IllegalValueException {
        requireNonNull(remark);
        return remark.isPresent() ? Optional.of(new Remark(remark.get())) : Optional.empty();
    }

    /**
     * Parses a {@code Optional<String> remark} into an {@code Optional<remark>} if {@code remark} is present.
     * See header comment of this class regarding the use of {@code Optional} parameters.
     */
    public static Optional<Birthday> parseBirthday(Optional<String> birthday) throws IllegalValueException {
        requireNonNull(birthday);
        return birthday.isPresent() ? Optional.of(new Birthday(birthday.get())) : Optional.empty();
    }
    //@@author

    /**
     * Parses a {@code Optional<String> email} into an {@code Optional<Email>} if {@code email} is present.
     * See header comment of this class regarding the use of {@code Optional} parameters.
     */
    public static Optional<Email> parseEmail(Optional<String> email) throws IllegalValueException {
        requireNonNull(email);
        return email.isPresent() ? Optional.of(new Email(email.get())) : Optional.empty();
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws IllegalValueException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(new Tag(tagName));
        }
        return tagSet;
    }

    /**
     * Parses {@code String tag} into a {@code Tag}.
     */
    public static Tag parseTag(String tag) throws IllegalValueException {
        requireNonNull(tag);
        return new Tag(tag.trim());
    }
}
