package seedu.address.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

/**
 * Creates a birthday notification
 */
public class BirthdayPopup {
    private JDialog frame = new JDialog();
    private GridBagConstraints constraints = new GridBagConstraints();

    public BirthdayPopup(String[] person) {
        createFrame(person.length);
        createIcon();
        createCloseButton();
        createMessage(person);
        createPopup();
    }

    /**
     * Creates the dialog for the popup
     * @param size
     */
    void createFrame(int size) {
        frame.setSize(300, 125 + size * 50);
        frame.setUndecorated(true);
        frame.setLayout(new GridBagLayout());
        Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
        Insets toolHeight = Toolkit.getDefaultToolkit().getScreenInsets(frame.getGraphicsConfiguration());
        frame.setLocation(scrSize.width - frame.getWidth(), scrSize.height - toolHeight.bottom - frame.getHeight());
        frame.setAlwaysOnTop(true);
    }

    /**
     * Creates the icon for the popup
     */
    void createIcon() {
        String header = "Birthday Alert!";

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.0f;
        constraints.weighty = 1.0f;
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.fill = GridBagConstraints.BOTH;

        Icon headingIcon = new ImageIcon("src/main/resources/images/birthday_cake.png", "birthday icon");
        JLabel headingLabel = new JLabel(header);
        headingLabel.setIcon(headingIcon);
        headingLabel.setOpaque(false);
        frame.add(headingLabel, constraints);
    }

    /**
     * Creates the close button for the popup
     */
    void createCloseButton() {
        constraints.gridx++;
        constraints.weightx = 0f;
        constraints.weighty = 0f;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.NORTH;
        JButton closeButton = new JButton(new AbstractAction("x") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                frame.dispose();
            }
        });
        closeButton.setMargin(new Insets(1, 4, 1, 4));
        closeButton.setFocusable(false);
        frame.add(closeButton, constraints);
    }

    /**
     * create the message for all the person with birthday
     * @param person
     */
    private void createMessage(String[] person) {
        String message = "There are birthdays today: \n";

        constraints.gridx = 0;
        constraints.gridy++;
        constraints.weightx = 1.0f;
        constraints.weighty = 1.0f;
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel messageLabel = new JLabel(message);
        frame.add(messageLabel, constraints);
        for (String e: person) {
            constraints.gridy++;
            messageLabel = new JLabel(e);
            frame.add(messageLabel, constraints);
        }
        frame.setVisible(true);
    }

    /**
     * create a timer for the popup
     */
    private void createPopup() {
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000); // time after which pop up will be disappeared.
                    frame.dispose();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
        }.start();
    }
}
