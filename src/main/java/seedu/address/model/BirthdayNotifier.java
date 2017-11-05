package seedu.address.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.ui.BirthdayPopup;

//@@author liliwei25
/**
 * Checks current date against birthday of all persons
 */
public class BirthdayNotifier {
    public BirthdayNotifier(List<ReadOnlyPerson> list) {
        String[] people = getBirthdaysToday(list);
        if (people.length > 0) {
            createPopup(people);
        }
    }

    private String[] getBirthdaysToday(List<ReadOnlyPerson> list) {
        LocalDate now = LocalDate.now();
        int date = now.getDayOfMonth();
        int month = now.getMonthValue();

        ArrayList<String> people = new ArrayList<>();

        for (ReadOnlyPerson e: list) {
            if (e.getDay() == date && e.getMonth() == month) {
                people.add(e.getName().toString());
            }
        }
        return people.toArray(new String[people.size()]);
    }

    private void createPopup(String[] person) {
        new BirthdayPopup(person);
    }
}
