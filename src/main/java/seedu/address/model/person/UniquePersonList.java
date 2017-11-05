package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.ParserUtil.SORTADD_ARG;
import static seedu.address.logic.parser.ParserUtil.SORTBIRTHDAY_ARG;
import static seedu.address.logic.parser.ParserUtil.SORTEMAIL_ARG;
import static seedu.address.logic.parser.ParserUtil.SORTFAVOURITE_ARG;
import static seedu.address.logic.parser.ParserUtil.SORTNAME_ARG;
import static seedu.address.logic.parser.ParserUtil.SORTNUM_ARG;
import static seedu.address.logic.parser.ParserUtil.SORTREMARK_ARG;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.fxmisc.easybind.EasyBind;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;

/**
 * A list of persons that enforces uniqueness between its elements and does not allow nulls.
 *
 * Supports a minimal set of list operations.
 *
 * @see Person#equals(Object)
 */
public class UniquePersonList implements Iterable<Person> {

    private final ObservableList<Person> internalList = FXCollections.observableArrayList();
    // used by asObservableList()
    private final ObservableList<ReadOnlyPerson> mappedList = EasyBind.map(internalList, (person) -> person);

    private Map<String, Comparator<Person>> comparatorMap;

    //@@author justintkj
    /**
     * Installs the comparator for the different sortType into comparatorMap
     */
    public UniquePersonList () {
        comparatorMap = new HashMap<String, Comparator<Person>>();
        comparatorMap.put(SORTNAME_ARG, Comparator.comparing(Person::getName));
        comparatorMap.put(SORTNUM_ARG, Comparator.comparing(Person::getPhone));
        comparatorMap.put(SORTADD_ARG, Comparator.comparing(Person::getAddress));
        comparatorMap.put(SORTEMAIL_ARG, Comparator.comparing(Person::getEmail));
        comparatorMap.put(SORTREMARK_ARG, Comparator.comparing(Person::getRemark));
        comparatorMap.put(SORTBIRTHDAY_ARG, Comparator.comparing(Person::getBirthday));
        comparatorMap.put(SORTFAVOURITE_ARG, Comparator.comparing(Person::getFavourite));
    }
    //@@author

    /**
     * Returns true if the list contains an equivalent person as the given argument.
     */
    public boolean contains(ReadOnlyPerson toCheck) {
        requireNonNull(toCheck);
        return internalList.contains(toCheck);
    }
    //@@author justintkj
    /**
     * Sorts the internalList as declared by the arguments
     */
    public void sort(String sortType) {
        Collections.sort(internalList, comparatorMap.get(sortType));
    }
    //@@author

    /**
     * Adds a person to the list.
     *
     * @throws DuplicatePersonException if the person to add is a duplicate of an existing person in the list.
     */
    public void add(ReadOnlyPerson toAdd) throws DuplicatePersonException {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicatePersonException();
        }
        internalList.add(new Person(toAdd));
    }

    /**
     * Replaces the person {@code target} in the list with {@code editedPerson}.
     *
     * @throws DuplicatePersonException if the replacement is equivalent to another existing person in the list.
     * @throws PersonNotFoundException if {@code target} could not be found in the list.
     */
    public void setPerson(ReadOnlyPerson target, ReadOnlyPerson editedPerson)
            throws DuplicatePersonException, PersonNotFoundException {
        requireNonNull(editedPerson);

        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new PersonNotFoundException();
        }

        if (!target.equals(editedPerson) && internalList.contains(editedPerson)) {
            throw new DuplicatePersonException();
        }

        internalList.set(index, new Person(editedPerson));
    }

    /**
     * Removes the equivalent person from the list.
     *
     * @throws PersonNotFoundException if no such person could be found in the list.
     */
    public boolean remove(ReadOnlyPerson toRemove) throws PersonNotFoundException {
        requireNonNull(toRemove);
        final boolean personFoundAndDeleted = internalList.remove(toRemove);
        if (!personFoundAndDeleted) {
            throw new PersonNotFoundException();
        }
        return personFoundAndDeleted;
    }

    public void setPersons(UniquePersonList replacement) {
        this.internalList.setAll(replacement.internalList);
    }

    public void setPersons(List<? extends ReadOnlyPerson> persons) throws DuplicatePersonException {
        final UniquePersonList replacement = new UniquePersonList();
        for (final ReadOnlyPerson person : persons) {
            replacement.add(new Person(person));
        }
        setPersons(replacement);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<ReadOnlyPerson> asObservableList() {
        return FXCollections.unmodifiableObservableList(mappedList);
    }

    @Override
    public Iterator<Person> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UniquePersonList // instanceof handles nulls
                        && this.internalList.equals(((UniquePersonList) other).internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }
}
