package seedu.address.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.ui.BirthdayPopup;

/**
 * Checks current date against birthday of all persons
 */
public class BirthdayNotifier {
    public BirthdayNotifier(List<ReadOnlyPerson> list) {
        LocalDateTime now = LocalDateTime.now();
        int date = now.getDayOfMonth();
        int month = now.getMonthValue();
        ArrayList<String> people = new ArrayList<>();

        for (ReadOnlyPerson e: list) {
            if (e.getDay() == date && e.getMonth() == month) {
                people.add(e.getName().toString());
            }
        }
        if (people.size() > 0) {
            createPopup(people.toArray(new String[people.size()]));
        }
    }
    void createPopup(String[] person) {
        new BirthdayPopup(person);
    }
}
