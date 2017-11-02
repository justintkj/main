# justintkj
###### /java/seedu/address/logic/commands/EmailCommand.java
``` java
/**
 * Email a person chosen by index
 */
public class EmailCommand extends Command {

    public static final String COMMAND_WORD = "email";
    public static final String COMMAND_ALIAS = "e";

    public static final String MESSAGE_SUCCESS = "Email sent to :";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Email the person identified "
            + "by the index number used in the last person listing. "
            + "An email will be sent to the chosen person.\n"
            + "Parameters: INDEX (must be a positive integer),SUBJECT,MESSAGE "
            + "[Email Index,Subject,Message]\n"
            + "Example: " + COMMAND_WORD + " 1"
            + ",subjectmessage,textmessage";
    public final Index index;
    public final String subject;
    public final String message;

    public EmailCommand(Index index, String subject, String message) {
        this.index = index;
        this.subject = subject;
        this.message = message;
    }

    @Override
    public CommandResult execute() throws CommandException {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        ReadOnlyPerson personToEmail = lastShownList.get(index.getZeroBased());

        try {
            String host = "smtp.gmail.com";
            String user = "cs2103f09b3@gmail.com";
            String pass = "pocketbook";
            String to = personToEmail.getEmail().toString();
            String from = "cs2103f09b3@gmail.com";
            String subject = this.subject;
            String newLine = "";
            for (int i = 0; i < 5; i++) {
                newLine += System.getProperty("line.separator");
            }
            String messageText = this.message + newLine + "This is a generated mail provided by CS2103F09B3 Team.";
            boolean sessionDebug = false;

            Properties props = System.getProperties();

            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.required", "true");

            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            Session mailSession = Session.getDefaultInstance(props, null);
            mailSession.setDebug(sessionDebug);
            Message msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress(from));
            InternetAddress[] address = {new InternetAddress(to)};
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject(subject);
            msg.setSentDate(new Date());
            msg.setText(messageText);

            Transport transport = mailSession.getTransport("smtp");
            transport.connect(host, user, pass);
            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();

        } catch (Exception ex) {
            throw new CommandException("Incorrect Email format");
        }
        return new CommandResult("Email successfully sent to : " + personToEmail.getName());
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }
        // instanceof handles nulls
        if (!(other instanceof BirthdayCommand)) {
            return false;
        }

        // state check
        EmailCommand e = (EmailCommand) other;
        return index.equals(e.index) && subject.equals(e.subject) && message.equals(e.message);
    }
}
```
###### /java/seedu/address/logic/commands/FavouriteCommand.java
``` java
/**
 * Favourites a person identified using it's last displayed index from the address book.
 */
public class FavouriteCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "favourite";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Favourites/Highlights the person identified by the index number used in the last person listing.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_FAVOURITE_SUCCESS = "Favourite Person: %1$s";

    private final Index targetIndex;
    private final Favourite favourite;

    public FavouriteCommand(Index targetIndex, Favourite favourite) {
        requireNonNull(targetIndex);
        this.targetIndex = targetIndex;
        this.favourite = favourite;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {

        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        ReadOnlyPerson personToEdit = lastShownList.get(targetIndex.getZeroBased());
        ReadOnlyPerson editedPerson = personToEdit;
        if (editedPerson.getFavourite().toString().equals("true")) {
            favourite.inverse();
        }
        editedPerson.setFavourite(favourite);

        try {
            model.updatePerson(personToEdit, editedPerson);
        } catch (DuplicatePersonException dpe) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        } catch (PersonNotFoundException pnfe) {
            throw new AssertionError("The target person cannot be missing");
        }
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_FAVOURITE_SUCCESS, editedPerson));

    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }
        // instanceof handles nulls
        if (!(other instanceof FavouriteCommand)) {
            return false;
        }

        // state check
        FavouriteCommand e = (FavouriteCommand) other;
        return targetIndex.equals(e.targetIndex) && favourite.equals(e.favourite);
    }
}
```
###### /java/seedu/address/logic/commands/RedoCommand.java
``` java
    public RedoCommand(Index numRedo) {
        this.numRedo = numRedo;
    }
    public RedoCommand() {
        try {
            numRedo = ParserUtil.parseIndex("1");
        } catch (IllegalValueException ex) {
            System.out.println("Shouldn't reach here");
        }
    }
    @Override
    public CommandResult execute() throws CommandException {
        requireAllNonNull(model, undoRedoStack);

        if (undoRedoStack.getRedoStackSize() == 0) {
            throw new CommandException("No more commands to redo!");
        }
        if (numRedo.getOneBased() > undoRedoStack.getRedoStackSize()) {
            throw new CommandException("Maximum redo size: " + undoRedoStack.getRedoStackSize());
        }

        for (int i = 0; i < numRedo.getOneBased(); i++) {
            if (!undoRedoStack.canRedo()) {
                throw new CommandException(MESSAGE_FAILURE);
            }

            undoRedoStack.popRedo().redo();
        }

        return new CommandResult(MESSAGE_SUCCESS);
    }
```
###### /java/seedu/address/logic/commands/RemarkCommand.java
``` java
/**
 * Edits the remark of the person identified by index number in person listing.
 */

public class RemarkCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "remark";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the remark of the person identified "
            + "by the index number used in the last person listing. "
            + "Existing remark will be overwritten by the input.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + PREFIX_REMARK + "[REMARK]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_REMARK + "Likes to swim.";

    private final Index index;
    private final Remark remark;

    public RemarkCommand (Index index, Remark remark) {
        requireNonNull(index);
        requireNonNull(remark);

        this.index = index;
        this.remark = remark;
    }

    public RemarkCommand (Index index, String remark) {
        requireNonNull(index);
        requireNonNull(remark);

        this.index = index;
        this.remark = new Remark(remark);
    }

    public CommandResult executeUndoableCommand() throws CommandException {
        throw new CommandException(index + " " + remark);
    }

    @Override
     public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }
        // instanceof handles nulls
        if (!(other instanceof RemarkCommand)) {
            return false;
        }

        // state check
        RemarkCommand e = (RemarkCommand) other;
        return index.equals(e.index) && remark.equals(e.remark);
    }
}
```
###### /java/seedu/address/logic/commands/SortCommand.java
``` java
/**
 * Sorts a the list of persons in ascending alphabetical order
 */
public class SortCommand extends UndoableCommand {
    public static final String COMMAND_WORD = "sort";
    public static final String MESSAGE_SORT_SUCCESS = "Sorted in ascending order: ";
    public static final String MESSAGE_SORT_FAILURE = "Invalid command format!";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Sorts the person identified by the index number used in the last person listing.\n"
            + "Parameters: name/num/address/email (the type of sort to be executed)\n"
            + "Example: " + COMMAND_WORD + " name"
            + "Example: " + COMMAND_WORD + " number"
            + "Example: " + COMMAND_WORD + " address"
            + "Example: " + COMMAND_WORD + " remark"
            + "Example: " + COMMAND_WORD + " email"
            + "Example: " + COMMAND_WORD + " birthday";


    private String sortType;

    public SortCommand(String sortType) {
        this.sortType = sortType.toLowerCase();
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        model.sortPerson(sortType);
        return new CommandResult(MESSAGE_SORT_SUCCESS + sortType);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof SortCommand // instanceof handles nulls
                && this.sortType.equals(((SortCommand) other).sortType)); // state check
    }
}
```
###### /java/seedu/address/logic/commands/UndoCommand.java
``` java
    public UndoCommand(Index numUndo) {
        this.numUndo = numUndo;
    }
    public UndoCommand() {
        try {
            numUndo = ParserUtil.parseIndex("1");
        } catch (IllegalValueException ex) {
            System.out.println("Shouldn't reach here");
        }

    }

    @Override
    public CommandResult execute() throws CommandException {
        requireAllNonNull(model, undoRedoStack);

        if (undoRedoStack.getUndoStackSize() == 0) {
            throw new CommandException("No more commands to undo!");
        }
        if (numUndo.getOneBased() > undoRedoStack.getUndoStackSize()) {
            throw new CommandException("Maximum undo size: " + undoRedoStack.getUndoStackSize());
        }

        for (int i = 0; i < numUndo.getOneBased(); i++) {
            if (!undoRedoStack.canUndo()) {
                throw new CommandException(MESSAGE_FAILURE);
            }

            undoRedoStack.popUndo().undo();
        }
        return new CommandResult(MESSAGE_SUCCESS);
    }
```
###### /java/seedu/address/logic/parser/AddCommandParser.java
``` java
/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

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
            ProfilePicture picture = new ProfilePicture("default");
            if (args.contains(PREFIX_NAME.toString()) || args.contains(PREFIX_ADDRESS.toString())
                || args.contains(PREFIX_EMAIL.toString()) || args.contains(PREFIX_PHONE.toString())
                || args.contains(PREFIX_REMARK.toString()) || args.contains(PREFIX_TAG.toString())) {
                if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_ADDRESS, PREFIX_PHONE, PREFIX_EMAIL)) {
                    throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
                }
                Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME)).get();
                Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE)).get();
                Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL)).get();
                Address address = ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS)).get();
                Remark remark = ParserUtil.parseRemark(argMultimap.getValue(PREFIX_REMARK)).get();
                Birthday birthday = ParserUtil.parseBirthday(argMultimap.getValue(PREFIX_BIRTHDAY)).get();
                Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));
                Favourite favourite = new Favourite("false");
                ReadOnlyPerson person = new Person(name, phone, email, address, remark, birthday, tagList, picture,
                        favourite);
                return new AddCommand(person);
            } else {
                Remark remark = new Remark("");
                Birthday birthday = new Birthday("");
                String[] allArgs = args.split(",");
                if (allArgs.length < 2) {
                    throw new IllegalValueException("Missing Name!\n" + AddCommand.MESSAGE_USAGE_ALT);
                }
                Name name = new Name(allArgs[0]);
                Email email;
                Phone phone;
                String blocknum;
                String streetnum;
                String unitnum;
                String postalnum = "";
                Favourite favourite = new Favourite("false");

                Pattern emailpattern = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+");
                Matcher matcher = emailpattern.matcher(args);
                boolean matchFound = matcher.find();
                if (matchFound) {
                    email = new Email(matcher.group(0));
                } else {
                    throw new IllegalValueException("invalid email\n Example: Jason@example.com");
                }
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
                Pattern street = Pattern.compile("[a-zA-z]+ street \\d{1,2}", Pattern.CASE_INSENSITIVE);
                matcher = street.matcher(args);
                matchFound = matcher.find();
                if (matchFound) {
                    streetnum = matcher.group(0);
                } else {
                    throw new IllegalValueException("invalid address, Street. \nExample: Jurong Street 11"
                        + AddCommand.MESSAGE_USAGE_ALT);
                }
                Pattern unit = Pattern.compile("#\\d\\d-\\d{1,3}[a-zA-Z]{0,1}", Pattern.CASE_INSENSITIVE);
                matcher = unit.matcher(args);
                matchFound = matcher.find();
                if (matchFound) {
                    unitnum = matcher.group(0);
                } else {
                    throw new IllegalValueException("invalid address, Unit. \n Example: #01-12B"
                        + AddCommand.MESSAGE_USAGE_ALT);
                }
                Pattern postal = Pattern.compile("singapore \\d{6,6}", Pattern.CASE_INSENSITIVE);
                matcher = postal.matcher(args);
                matchFound = matcher.find();
                if (matchFound) {
                    postalnum = ", " + matcher.group(0);
                }
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
                Pattern birthpattern = Pattern.compile("\\d{1,2}-\\d{1,2}-\\d{4,4}", Pattern.CASE_INSENSITIVE);
                matcher = birthpattern.matcher(args);
                matchFound = matcher.find();
                if (matchFound) {
                    if (Birthday.isValidBirthday(matcher.group(0))) {
                        birthday = new Birthday(matcher.group(0));
                    } else {
                        throw new IllegalValueException("invalid birthday,\n Example: 12-09-1994");
                    }
                }

                Address address = new Address(blocknum + ", " + streetnum + ", " + unitnum + postalnum);
                Set<Tag> tagList = new HashSet<>();
                ReadOnlyPerson person = new Person(name, phone, email, address, remark, birthday, tagList, picture,
                        favourite);
                return new AddCommand(person);
            }
        } catch (IllegalValueException ive) {
            throw new ParseException(ive.getMessage(), ive);
        }
    }
```
###### /java/seedu/address/logic/parser/AddressBookParser.java
``` java
    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string, adds suggestion to Autocomplete.xml if valid command
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public Command parseCommand(String userInput) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");
        switch (commandWord) {

        case EmailCommand.COMMAND_WORD:
            Command emailCommand = new EmailCommandParser().parse(arguments);
            try {
                CommandBox.setAddSuggestion(userInput);
            } catch (Exception ex) {
                throw new ParseException(MISSING_AUTOCOMPLETEFILE);
            }
            return emailCommand;

        case SortCommand.COMMAND_WORD:
            return new SortCommandParser().parse(arguments);

        case AddCommand.COMMAND_WORD:
        case AddCommand.COMMAND_ALIAS:
            Command addCommand = new AddCommandParser().parse(arguments);
            try {
                CommandBox.setAddSuggestion(userInput);
            } catch (Exception ex) {
                throw new ParseException(MISSING_AUTOCOMPLETEFILE);
            }
            return addCommand;

        case EditCommand.COMMAND_WORD:
        case EditCommand.COMMAND_ALIAS:
            Command editCommand = new EditCommandParser().parse(arguments);
            try {
                CommandBox.setAddSuggestion(userInput);
            } catch (Exception ex) {
                throw new ParseException(MISSING_AUTOCOMPLETEFILE);
            }
            return editCommand;

        case SelectCommand.COMMAND_WORD:
        case SelectCommand.COMMAND_ALIAS:
            Command selectCommand = new SelectCommandParser().parse(arguments);
            try {
                CommandBox.setAddSuggestion(userInput);
            } catch (Exception ex) {
                throw new ParseException(MISSING_AUTOCOMPLETEFILE);
            }
            return selectCommand;

        case DeleteCommand.COMMAND_WORD:
        case DeleteCommand.COMMAND_ALIAS:
            Command deleteCommand = new DeleteCommandParser().parse(arguments);
            try {
                CommandBox.setAddSuggestion(userInput);
            } catch (Exception ex) {
                throw new ParseException(MISSING_AUTOCOMPLETEFILE);
            }
            return deleteCommand;

        case RemoveTagCommand.COMMAND_WORD:
            Command removeTagCommand = new RemoveTagCommandParser().parse(arguments);
            try {
                CommandBox.setAddSuggestion(userInput);
            } catch (Exception ex) {
                throw new ParseException(MISSING_AUTOCOMPLETEFILE);
            }
            return removeTagCommand;

        case ClearCommand.COMMAND_WORD:
        case ClearCommand.COMMAND_ALIAS:
            return new ClearCommand();

        case FindCommand.COMMAND_WORD:
        case FindCommand.COMMAND_ALIAS:
            Command findCommand = new FindCommandParser().parse(arguments);
            try {
                CommandBox.setAddSuggestion(userInput);
            } catch (Exception ex) {
                throw new ParseException(MISSING_AUTOCOMPLETEFILE);
            }
            return findCommand;

        case FuzzyfindCommand.COMMAND_WORD:
        case FuzzyfindCommand.COMMAND_ALIAS:
            return new FuzzyfindCommandParser().parse(arguments);

        case ListCommand.COMMAND_WORD:
        case ListCommand.COMMAND_ALIAS:
            return new ListCommand();

        case HistoryCommand.COMMAND_WORD:
        case HistoryCommand.COMMAND_ALIAS:
            return new HistoryCommand();

        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();

        case HelpCommand.COMMAND_WORD:
            return new HelpCommand();

        case UndoCommand.COMMAND_WORD:
        case UndoCommand.COMMAND_ALIAS:
            Command undoCommand = new UndoCommandParser().parse(arguments);
            try {
                CommandBox.setAddSuggestion(userInput);
            } catch (Exception ex) {
                throw new ParseException(MISSING_AUTOCOMPLETEFILE);
            }
            return undoCommand;

        case RedoCommand.COMMAND_WORD:
        case RedoCommand.COMMAND_ALIAS:
            Command redoCommand = new RedoCommandParser().parse(arguments);
            try {
                CommandBox.setAddSuggestion(userInput);
            } catch (Exception ex) {
                throw new ParseException(MISSING_AUTOCOMPLETEFILE);
            }
            return redoCommand;

        case RemarkCommand.COMMAND_WORD:
            Command remarkCommand = new RemarkCommandParser().parse(arguments);
            try {
                CommandBox.setAddSuggestion(userInput);
            } catch (Exception ex) {
                throw new ParseException(MISSING_AUTOCOMPLETEFILE);
            }
            return remarkCommand;

        case BirthdayCommand.COMMAND_WORD:
            Command birthdayCommand = new BirthdayCommandParser().parse(arguments);
            try {
                CommandBox.setAddSuggestion(userInput);
            } catch (Exception ex) {
                throw new ParseException(MISSING_AUTOCOMPLETEFILE);
            }
            return birthdayCommand;

        case MapCommand.COMMAND_WORD:
            Command mapCommand = new MapCommandParser().parse(arguments);
            try {
                CommandBox.setAddSuggestion(userInput);
            } catch (Exception ex) {
                throw new ParseException(MISSING_AUTOCOMPLETEFILE);
            }
            return mapCommand;

        case ImageCommand.COMMAND_WORD:
            return new ImageCommandParser().parse(arguments);

        case FavouriteCommand.COMMAND_WORD:
            Command favouriteCommand = new FavouriteCommandParser().parse(arguments);
            try {
                CommandBox.setAddSuggestion(userInput);
            } catch (Exception ex) {
                throw new ParseException(MISSING_AUTOCOMPLETEFILE);
            }
            return favouriteCommand;

        default:
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }

}
```
###### /java/seedu/address/logic/parser/EmailCommandParser.java
``` java
/**
 * Parses input arguments and creates a new EmailCommand object
 */
public class EmailCommandParser implements Parser<EmailCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the EmailCommand
     * and returns a Email object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public EmailCommand parse(String args) throws ParseException {
        requireNonNull(args);

        Index index;
        String subject;
        String message;
        try {
            String[] messages = args.trim().split(",");
            if (messages.length < 3) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EmailCommand.MESSAGE_USAGE));
            }

            String[] splitArgs = messages[0].trim().split(" ");
            index = ParserUtil.parseIndex(splitArgs[0]);
            subject = (messages[1]);
            message = (messages[2]);
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EmailCommand.MESSAGE_USAGE));
        }
        return new EmailCommand(index, subject, message);
    }
}
```
###### /java/seedu/address/logic/parser/FavouriteCommandParser.java
``` java
/**
 * Parses arguments and returns FavouriteCommand
 */
public class FavouriteCommandParser implements Parser<FavouriteCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the BirthdayCommand
     * and returns a BirthdayCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public FavouriteCommand parse(String args) throws ParseException {
        requireNonNull(args);

        Index index;
        Favourite favourite;
        try {
            index = ParserUtil.parseIndex(args);
            favourite = new Favourite("true");
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                FavouriteCommand.MESSAGE_USAGE), ive);
        }

        return new FavouriteCommand(index, favourite);
    }
}
```
###### /java/seedu/address/logic/parser/RedoCommandParser.java
``` java
/**
* Parses input arguments and creates a new RedoCommand object
*/
public class RedoCommandParser implements Parser<RedoCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the UndoCommand
     * and returns an RedoCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public RedoCommand parse(String args) throws ParseException {
        String[] splitArgs = args.trim().split(" ");

        Index index;
        try {
            if (splitArgs[0].trim().equals("")) {
                index = ParserUtil.parseIndex("1");
            } else {
                index = ParserUtil.parseIndex(splitArgs[0]);
            }
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RedoCommand.MESSAGE_USAGE), ive);
        }

        return new RedoCommand(index);
    }

}
```
###### /java/seedu/address/logic/parser/RemarkCommandParser.java
``` java
/**
 * Parses input arguments and creates a new AddCommand object
 */
public class RemarkCommandParser implements Parser<RemarkCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the RemarkCommand
     * and returns a RemarkCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public RemarkCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_REMARK);

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE));
        }
        Remark remark = new Remark(argMultimap.getValue(PREFIX_REMARK).orElse(""));
        return new RemarkCommand(index, remark);
    }
}
```
###### /java/seedu/address/logic/parser/SortCommandParser.java
``` java
/**
 * Parses input arguments and creates a new SortCommand object
 */
public class SortCommandParser implements Parser<SortCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the SortCommand
     * and returns an SortCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public SortCommand parse(String args) throws ParseException {
        try {
            args = args.toLowerCase();
            String toSort = ParserUtil.parseSortType(args);
            return new SortCommand(toSort);
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
        }
    }
}
```
###### /java/seedu/address/logic/parser/UndoCommandParser.java
``` java
/**
 * Parses input arguments and creates a new UndoCommand object
 */
public class UndoCommandParser implements Parser<UndoCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the UndoCommand
     * and returns an UndoCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public UndoCommand parse(String args) throws ParseException {

        String[] splitArgs = args.trim().split(" ");

        Index index;
        try {
            if (splitArgs[0].trim().equals("")) {
                index = ParserUtil.parseIndex("1");
            } else {
                index = ParserUtil.parseIndex(splitArgs[0]);
            }
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, UndoCommand.MESSAGE_USAGE), ive);
        }

        return new UndoCommand(index);
    }

}
```
###### /java/seedu/address/logic/UndoRedoStack.java
``` java
    /**
     * Gets number of undoable tasks in integer
     */
    public int getUndoStackSize() {
        return undoStack.size();
    }

    /**
     * Gets number of redoable tasks in integer
     */
    public int getRedoStackSize() {
        return redoStack.size();
    }

```
###### /java/seedu/address/model/AddressBook.java
``` java
    /**
     *     Sort Persons according to sortType
     */
    public void sortPersons(String sortType) {
        persons.sort(sortType);
        syncMasterTagListWith(persons);
    }
```
###### /java/seedu/address/model/Model.java
``` java
    /**Sorts all the people in the current database*/
    void sortPerson(String sortType);
```
###### /java/seedu/address/model/person/Favourite.java
``` java
/**
 * Represents a person importance in the addressBook
 * Guarantees: immutable;
 */
public class Favourite {

    public static final String MESSAGE_FAVOURITE_CONSTRAINTS =
            "Person favourite should only be true or false, and it should not be blank";

    private Boolean value = false;

    /**
     * Validates given Favourite.
     *
     * @throws IllegalValueException if given favourite string is invalid.
     */
    public Favourite(String input) throws IllegalValueException {
        if (input == null) {
            input = "false";
        }
        String trimmedinput = input.trim();
        if (!isValidInput(trimmedinput)) {
            throw new IllegalValueException(MESSAGE_FAVOURITE_CONSTRAINTS);
        }
        if (trimmedinput.equals("true") && !value) {
            this.value = true;
        } else {
            this.value = false;
        }
    }

    /**
     * Inverses the current state of Favourite
     */
    public void inverse() {
        value = false;
    }
    /**
     * Returns true if a given string is a valid person name.
     */
    public boolean isValidInput(String input) {
        if (input.toLowerCase().equals("true") || input.toLowerCase().equals("false")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        if (value) {
            return "true";
        } else {
            return "false";
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Favourite // instanceof handles nulls
                && this.value.equals(((Favourite) other).value)); // state check
    }
}
```
###### /java/seedu/address/model/person/UniquePersonList.java
``` java
    /**
     * Sorts the internalList as declared by the arguments
     */
    public void sort(String sortType) {
        if (sortType.equals(SORTNAME_ARG)) {
            Collections.sort(internalList, (Person p1, Person p2) ->
                p1.getName().toString().compareTo(p2.getName().toString()));
        } else if (sortType.equals(SORTNUM_ARG)) {
            Collections.sort(internalList, (Person p1, Person p2) ->
                p1.getPhone().toString().compareTo(p2.getPhone().toString()));
        } else if (sortType.equals(SORTADD_ARG)) {
            Collections.sort(internalList, (Person p1, Person p2) ->
                p1.getAddress().toString().compareTo(p2.getAddress().toString()));
        } else if (sortType.equals(SORTEMAIL_ARG)) {
            Collections.sort(internalList, (Person p1, Person p2) ->
                p1.getEmail().toString().compareTo(p2.getEmail().toString()));
        } else if (sortType.equals(SORTREMARK_ARG)) {
            Collections.sort(internalList, (Person p1, Person p2) ->
                p1.getRemark().toString().compareTo(p2.getRemark().toString()));
        } else if (sortType.equals(SORTBIRTHDAY_ARG)) {
            Collections.sort(internalList, (Person p1, Person p2) ->
                p1.getBirthday().toString().compareTo(p2.getBirthday().toString()));
        }
    }
```
###### /java/seedu/address/Sound.java
``` java
/**
 * Plays the errorSound.
 */
public class Sound {
    private static final Logger logger = LogsCenter.getLogger("Error Sound");
    private static ArrayList<String> musicList = new ArrayList<String>(Arrays.asList("ErrorSound.mp3"));
    private static int curr = 0;
    private static String bip;
    private static Media hit;
    private static MediaPlayer mediaPlayer;


    /**
     * start playing the first error music on the playlist.
     */

    public static void music() {
        try {
            bip = musicList.get(curr);
            hit = new Media(Thread.currentThread().getContextClassLoader().getResource(bip).toURI().toString());
            mediaPlayer = new MediaPlayer(hit);
            mediaPlayer.play();
        } catch (Exception ex) {
            logger.info("Error with playing sound.");
            ex.printStackTrace();
        }
    }
}
```
###### /java/seedu/address/ui/CommandBox.java
``` java
/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String AUTOCOMPLETE_FILE_NAME = "Autocomplete.xml";
    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";
    private static String[] possibleSuggestion = {"add", "clear", "list",
        "edit", "find", "delete", "select", "history", "undo", "redo", "exit", "sort", "sort name",
        "sort num", "sort email", "sort address", "sort remark", "exit"};
    private static ArrayList<String> mainPossibleSuggestion = new ArrayList<String>(Arrays.asList(possibleSuggestion));
    private final Logger logger = LogsCenter.getLogger(CommandBox.class);
    private final Logic logic;
    private ListElementPointer historySnapshot;

    @FXML
    private TextField commandTextField;
    private ArrayList<String> prevText = new ArrayList<String>();
    private AutoCompletionBinding autocompletionbinding;
    public CommandBox(Logic logic) {
        super(FXML);
        this.logic = logic;
        // calls #setStyleToDefault() whenever there is a change to the text of the command box.
        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> setStyleToDefault());
        historySnapshot = logic.getHistorySnapshot();
        try {
            XMLDecoder e = new XMLDecoder(new FileInputStream("Autocomplete.xml"));
            mainPossibleSuggestion = ((ArrayList<String>) e.readObject());
            e.close();
        } catch (Exception ex) {
            try {
                File file = new File("Autocomplete.xml");
                file.createNewFile();
            } catch (IOException ioe) {
                raise(new DataSavingExceptionEvent(ex));
            }
        }
        autocompletionbinding = TextFields.bindAutoCompletion(commandTextField, mainPossibleSuggestion);
    }
```
###### /java/seedu/address/ui/CommandBox.java
``` java

    /**
     * Adds in a valid command string into autocomplete.xml storage
     * @param commandWord
     * @throws CommandException if autocomplete.xml cannot be made.
     */
    public static void setAddSuggestion(String commandWord) throws CommandException {
        if (!mainPossibleSuggestion.contains(commandWord)) {
            try {
                mainPossibleSuggestion.add(commandWord.trim());
                XMLEncoder e = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(AUTOCOMPLETE_FILE_NAME)));
                e.writeObject(mainPossibleSuggestion);
                e.close();
            } catch (Exception ex) {
                try {
                    File file = new File("Autocomplete.xml");
                    file.createNewFile();
                } catch (IOException ioe) {
                    throw new CommandException("Unable to create file Autocomplete.xml");
                }
            }
        }
    }
```
###### /java/seedu/address/ui/PersonCard.java
``` java
    /**
     * Binds the individual UI elements to observe their respective {@code Person} properties
     * so that they will be notified of any changes.
     * Changes the color code of the person if favourite.
     */
    private void bindListeners(ReadOnlyPerson person) {
        name.textProperty().bind(Bindings.convert(person.nameProperty()));
        if (person.getFavourite().toString().equals("true")) {
            name.setStyle("-fx-background-color : #ff0000");
        } else if (person.getFavourite().toString().equals("false")) {
            name.setStyle("-fx-background-color : transparent");
        }
    }
```
