package gui;

import Components.HotelOffer;
import Components.RButton;
import Components.RoomOffer;
import Components.SQButton;
import DataBase.FireBaseService;
import DataBase.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Objects;

public class ProfilePanel extends BasePanel {
    private final User user;
    private ArrayList<RoomOffer> array;
    private void reservation(ActionEvent e, ArrayList<RoomOffer> roomOffers, int index, User user){
        BaseFrame addReservationFrame = new BaseFrame(1400, 600);
        AddReservation addReservation = new AddReservation(addReservationFrame, roomOffers.get(index), user);
        addReservationFrame.add(addReservation);
        addReservationFrame.setLocationRelativeTo(null);
        addReservationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addReservationFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                addReservationFrame.dispose();
            }
        });
        addReservationFrame.setVisible(true);

    }

    public ProfilePanel(BaseFrame baseFrame, User u, ArrayList<RoomOffer> a) {
        user = u;
        array = a;
        setSize(baseFrame.getWidth(), baseFrame.getHeight());
        setLayout(null);
        setBackground(Color.decode("#F2F2F2"));
        addComponents(baseFrame);
    }

    @Override
    public void addComponents(BaseFrame baseFrame, JPanel componentsPanel) {
        // Not used, but needs to be implemented due to abstract method in BasePanel
    }

    @Override
    public void addComponents(BaseFrame frame) {
        Cursor cursor = new Cursor(Cursor.HAND_CURSOR);
        setLayout(new BorderLayout());

        ImageIcon profile = new ImageIcon(Objects.requireNonNull(getClass().getResource("/Profile.png")));
        Image newProfile = profile.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        profile = new ImageIcon(newProfile);

        JButton profileButton = new JButton();
        profileButton.setBounds(5, 5, profile.getIconWidth(), profile.getIconHeight());
        profileButton.setCursor(cursor);
        profileButton.setIcon(profile);
        profileButton.setFocusPainted(false);
        profileButton.setContentAreaFilled(false);
        profileButton.setBorderPainted(false);
        profileButton.addActionListener(e -> {
            frame.changePanel(new ProfilePanel(frame, user, array));
        });
        add(profileButton, BorderLayout.NORTH);

        String welcomeMessage = "<html><body style='text-align:center'><b>"+Translator.getValue("hello") + user.getFirst_name() + " " + user.getLast_name() + "</b></body></html>";
        JLabel welcomeMessageLabel = new JLabel(welcomeMessage);
        welcomeMessageLabel.setBounds(80, 20, getWidth() - 10, 40);
        welcomeMessageLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
        welcomeMessageLabel.setHorizontalAlignment(SwingConstants.LEFT);
        add(welcomeMessageLabel, BorderLayout.NORTH);

        // Title
        JLabel travelAppLabel = new JLabel("BookNgo");
        travelAppLabel.setBounds(0, 20, super.getWidth(), 60);
        travelAppLabel.setFont(new Font("Dialog", Font.BOLD, 50));
        travelAppLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(travelAppLabel, BorderLayout.NORTH);

        // Container panel with GridBagLayout
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Menu panel
        JPanel menuPanel = createMenuPanel(frame);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        gbc.weighty = 1;
        containerPanel.add(menuPanel, gbc);

        // Offers panel
        JPanel offersPanel = new JPanel();
        offersPanel.setLayout(new BoxLayout(offersPanel, BoxLayout.Y_AXIS));
        for (int i = 0; i < array.size(); i++) {
            JPanel offerPanel = createOfferPanel(array.get(i), i, array);
            offerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            offersPanel.add(offerPanel);
            offersPanel.add(Box.createVerticalStrut(10));
        }

        // Add the offers panel to a JScrollPane
        JScrollPane offersScrollPane = new JScrollPane(offersPanel);
        offersScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.8;
        gbc.weighty = 1;
        containerPanel.add(offersScrollPane, gbc);

        add(containerPanel, BorderLayout.CENTER);
    }


    private JPanel createOfferPanel(RoomOffer offer, int index, ArrayList<RoomOffer> roomOffers) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        panel.setPreferredSize(new Dimension(700, 300)); // Set a preferred size for the panel

        JLabel roomTypeLabel = new JLabel(Translator.getValue(offer.getRoomType()));
        roomTypeLabel.setFont(new Font("Dialog", Font.BOLD, 20));
        roomTypeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel roomPriceLabel = new JLabel(Translator.getValue("price") + offer.getRoomPricePerNight());
        roomPriceLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
        roomPriceLabel.setHorizontalAlignment(SwingConstants.LEFT);

        ImageIcon imageIcon = null;
        if (!offer.getRoomImages().isEmpty()) {
            Image image = offer.getRoomImages().get(0);
            Image scaledImage = image.getScaledInstance(500, 500, Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(scaledImage);
        }
        JLabel imageLabel = new JLabel(imageIcon);
        imageLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        RButton bookButton = new RButton(Translator.getValue("book"), Color.decode("#7A4641"), Color.decode("#512E2B"), Color.decode("#8D4841"));
        bookButton.setFont(new Font("Dialog", Font.PLAIN, 16));
        bookButton.setForeground(Color.WHITE);
        bookButton.setPreferredSize(new Dimension(100, 40));

        // Add action listener to the book button to handle the action
        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("Clicked on Book Now for offer #" + (index + 1));
                reservation(e, roomOffers, index, user);
            }
        });

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(roomTypeLabel);
        infoPanel.add(roomPriceLabel);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(bookButton);

        panel.add(imageLabel, BorderLayout.EAST);
        panel.add(infoPanel, BorderLayout.WEST);

        return panel;
    }
    private JPanel createMenuPanel(BaseFrame baseFrame) {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 0, 5, 0); // Spacing between buttons

        SQButton homeButton = new SQButton(Translator.getValue("home"), Color.decode("#7A4641"), Color.decode("#512E2B"), Color.decode("#8D4841"));
        homeButton.setFont(new Font("Dialog", Font.BOLD, 23));
        homeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        homeButton.setForeground(Color.WHITE);

        homeButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
        homeButton.setMinimumSize(new Dimension(450, 50));
        homeButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        homeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                homeButton.setBackground(Color.decode("#555555"));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                homeButton.setBackground(Color.decode("#7A4641"));
            }
        });
        menuPanel.add(homeButton, gbc);

        SQButton reservationsButton  = new SQButton(Translator.getValue("reservations"), Color.decode("#7A4641"), Color.decode("#512E2B"), Color.decode("#8D4841"));
        reservationsButton.setFont(new Font("Dialog", Font.BOLD, 23));
        reservationsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        reservationsButton.setForeground(Color.WHITE);

        reservationsButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
        reservationsButton.setMinimumSize(new Dimension(450, 50));
        reservationsButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        reservationsButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                homeButton.setBackground(Color.decode("#555555"));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                homeButton.setBackground(Color.decode("#7A4641"));
            }
        });
        reservationsButton.addActionListener(e -> {
            baseFrame.changePanel(new ViewReservationsPanel(baseFrame, user));
        });
        menuPanel.add(reservationsButton, gbc);

        SQButton settingsButton  = new SQButton(Translator.getValue("settings"), Color.decode("#7A4641"), Color.decode("#512E2B"), Color.decode("#8D4841"));
        settingsButton.setFont(new Font("Dialog", Font.BOLD, 23));
        settingsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        settingsButton.setForeground(Color.WHITE);

        settingsButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
        settingsButton.setMinimumSize(new Dimension(450, 50));
        settingsButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        settingsButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                homeButton.setBackground(Color.decode("#555555"));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                homeButton.setBackground(Color.decode("#7A4641"));
            }
        });
        menuPanel.add(settingsButton, gbc);

        // Create the panel for language settings
        JPanel languagePanel = new JPanel();
        languagePanel.setLayout(new FlowLayout());
        languagePanel.setBorder(BorderFactory.createTitledBorder("Language Settings"));
        languagePanel.setVisible(false);

        // The button that changes the langauge of the application
        JButton changeLanguageButton = new JButton(Translator.getValue("changeLanguage"));

        // Actions to perform when "Change language" button is clicked
        changeLanguageButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Language selectedLanguage = (Language) JOptionPane.showInputDialog(null, "BookNGo",
                        Translator.getValue("selectLanguage"), JOptionPane.QUESTION_MESSAGE, null, Language.values(),
                        Language.ENG.toString());

                if (selectedLanguage != null)
                    Translator.setLanguage(selectedLanguage);
                else
                    return;

                Translator.getMessagesFromXML();

                baseFrame.dispose(); // Dispose of the current frame

                // Create a new BaseFrame
                BaseFrame newFrame = new BaseFrame(baseFrame.getWidth(), baseFrame.getHeight());
                // Add the ProfilePanel to the new frame
                ProfilePanel newProfilePanel = new ProfilePanel(newFrame, user,array);
                newFrame.add(newProfilePanel);
                newFrame.setLocationRelativeTo(null);
                newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                newFrame.setVisible(true); // Show the new frame
            }
        });
        changeLanguageButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
        changeLanguageButton.setBounds(480, 365, 135, 25);
        languagePanel.add(changeLanguageButton, gbc);
        menuPanel.add(languagePanel,gbc);

        // Add action listener to the settings button to toggle visibility of the language panel
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                languagePanel.setVisible(!languagePanel.isVisible());
            }
        });

        SQButton logoutButton  = new SQButton(Translator.getValue("logout"), Color.decode("#7A4641"), Color.decode("#512E2B"), Color.decode("#8D4841"));
        logoutButton.setFont(new Font("Dialog", Font.BOLD, 23));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setForeground(Color.WHITE);

        logoutButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
        logoutButton.setMinimumSize(new Dimension(450, 50));
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        logoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                homeButton.setBackground(Color.decode("#555555"));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                homeButton.setBackground(Color.decode("#7A4641"));
            }
        });

        logoutButton.addActionListener(e -> {
            // Remove the current ProfilePanel from the frame
            baseFrame.remove(ProfilePanel.this);

            // Create and add the LoginPanel to the frame
            baseFrame.changePanel(new LoginPanel(baseFrame));
        });

        menuPanel.add(logoutButton, gbc);
        return menuPanel;
    }

}