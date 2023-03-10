package views;

import controllers.RadioInfoController;
import models.ChannelModel;
import models.EpisodeModel;
import models.RadioChannelTableModel;

import javax.swing.*;
import java.awt.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * A user interface for the RadioInfo application
 * @author Samuel Sandlund
 * @version 2.0
 * @since 2023-02-07
 */
public class RadioInfoGUI {
    private RadioInfoController currentController;
    private final List<ChannelModel> channelList;
    private ChannelModel currentChannel;
    private JFrame window;
    private JPanel contentPanel;
    private JPanel tablePanel;
    private JPanel loadingScreen;
    private JTable programTable;
    private JMenu radioMenu;

    /**
     * Creates and displays a new user interface for RadioInfo
     * @param channelList list of available channels to select from
     */
    public RadioInfoGUI(List<ChannelModel> channelList){
        this.channelList = channelList;
        initWindow();
        initContentPanel();
        initLoadingScreen();

        JMenuBar menuBar = new JMenuBar();
        initRadioMenu();
        menuBar.add(radioMenu);
        window.add(menuBar, BorderLayout.NORTH);

        showStartScreen();
        window.setVisible(true);
    }

    /**
     * Sets the active controller for this view
     * @param controller new controller
     */
    public void setController(RadioInfoController controller){
        currentController = controller;
    }

    /**
     * Shows a dialogue box with the given message
     * @param message a string with the text to show in the dialogue box
     */
    public void showErrorMessage(String message){
        JOptionPane.showMessageDialog(window, message);
    }

    /**
     * Shows a screen informing the user that the application is waiting on data from the API
     */
    public void showLoadingScreen(){
        clearContentPanel();
        contentPanel.add(loadingScreen);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Sets the content panel to display information about a given radio channel
     * @param channel ChannelModel for the channel to show
     * @param episodes List of current episodes on the channel
     */
    public void setCurrentChannel(ChannelModel channel, List<EpisodeModel> episodes){
        currentChannel = channel;
        buildChannelTablePanel(channel, episodes);
        showChannelTable();
    }

    /**
     * Sets the contentPanel to a panel containing basic user instructions
     */
    public void showStartScreen(){
        clearContentPanel();
        JPanel greetingScreen = new JPanel();
        greetingScreen.setLayout(new BoxLayout(greetingScreen, BoxLayout.Y_AXIS));
        JLabel spacing = new JLabel(" ");
        spacing.setFont(new Font("spacing", Font.PLAIN, 20));
        JLabel title = new JLabel("Radio info", SwingConstants.CENTER);
        title.setFont(new Font("Title", Font.PLAIN, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel message = new JLabel("F??r att komma ig??ng v??lj en kanal i menyn uppe till v??nster");
        JLabel instruction = new JLabel("Radio -> V??lj kanal");
        message.setAlignmentX(Component.CENTER_ALIGNMENT);
        instruction.setAlignmentX(Component.CENTER_ALIGNMENT);
        greetingScreen.add(spacing);
        greetingScreen.add(title);
        greetingScreen.add(spacing);
        greetingScreen.add(message);
        greetingScreen.add(instruction);
        contentPanel.add(greetingScreen);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * If a current channel has been set: updates list of episodes and re-renders information in the content panel
     */
    public void updateCurrentChannel(){
        if(currentChannel != null){
            currentController.getScheduledEpisodes(currentChannel);
        }
    }

    /**
     * Sets the table panel to show information from the given channelTableModel
     * @param channel ChannelModel for the channel to show
     * @param episodes List of current episodes on the channel
     */
    private void buildChannelTablePanel(ChannelModel channel, List<EpisodeModel> episodes){
        RadioChannelTableModel channelTableModel = new RadioChannelTableModel(episodes);
        tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        JLabel name = new JLabel(channel.getName(), SwingConstants.CENTER);
        name.setFont(new Font("Title", Font.PLAIN, 20));
        tablePanel.add(name, BorderLayout.NORTH);
        if(channelTableModel.isEmpty()){
            JLabel message = new JLabel("Kanalen har inga planerade s??ndningar", SwingConstants.CENTER);
            tablePanel.add(message);
        }
        else {
            programTable = new JTable(channelTableModel);
            programTable.getSelectionModel().addListSelectionListener(e->{
                int selectedRow = programTable.getSelectedRow();
                if(selectedRow >= 0 && selectedRow < programTable.getRowCount()){
                    EpisodeModel episode = channelTableModel.getEpisode(selectedRow);
                    if(!e.getValueIsAdjusting()){
                        showEpisodeInfo(episode);
                    }
                }
            });
            JScrollPane scrollPane = new JScrollPane(programTable);
            tablePanel.add(scrollPane, BorderLayout.CENTER);
        }
    }

    /**
     * Shows more detailed information about the radio program from the given EpisodeModel
     * @param episode episode to display information about
     */
    private void showEpisodeInfo(EpisodeModel episode){
        JFrame infoFrame = new JFrame();
        infoFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        infoFrame.setSize(420, 400);
        JPanel episodeInfoPanel = new JPanel(new BorderLayout());
        JPanel boxPanel = new JPanel();
        boxPanel.setLayout(new BoxLayout(boxPanel, BoxLayout.Y_AXIS));
        boxPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel icon = new JLabel();
        if(episode.getImage() != null){
            icon.setIcon(new ImageIcon(episode.getImage()));
        }
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        boxPanel.add(icon);

        JLabel header = new JLabel(episode.getTitle() + " " + episode.getSubTitle());
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.setFont(new Font("header", Font.PLAIN, 20));
        boxPanel.add(header);

        JTextArea details = new JTextArea(episode.getDescription());
        details.setMaximumSize(new Dimension(400, 100));
        details.setLineWrap(true);
        details.setWrapStyleWord(true);
        details.setEditable(false);
        boxPanel.add(details);

        JLabel broadcastTimeLabel = new JLabel("Avsnittet s??nds mellan:");
        String startTime = episode.getStartTime().atZone(ZoneId.systemDefault()).toLocalDateTime().
                format(DateTimeFormatter.ofPattern("HH:mm"));
        String endTime = episode.getEndTime().atZone(ZoneId.systemDefault()).toLocalDateTime().
                format(DateTimeFormatter.ofPattern("HH:mm"));
        JLabel broadcastTime = new JLabel(startTime + " och " + endTime);
        broadcastTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        broadcastTime.setAlignmentX(Component.CENTER_ALIGNMENT);
        boxPanel.add(broadcastTimeLabel);
        boxPanel.add(broadcastTime);
        episodeInfoPanel.add(boxPanel, BorderLayout.CENTER);

        infoFrame.add(episodeInfoPanel);
        infoFrame.setVisible(true);
    }

    /**
     * Sets the content panel to the currently selected channel
     */
    private void showChannelTable(){
        clearContentPanel();
        contentPanel.add(tablePanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Sets up the JFrame for the application
     */
    private void initWindow() {
        window = new JFrame("RadioInfo");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setSize(680, 500);
        window.setLayout(new BorderLayout());
    }

    /**
     * Sets up the central panel of the interface where content is displayed
     */
    private void initContentPanel(){
        contentPanel = new JPanel(new BorderLayout());
        window.add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Sets up the menu from which channels can be seleced and updates started
     */
    private void initRadioMenu(){
        radioMenu = new JMenu("Radio");
        JMenuItem updateOption = new JMenuItem("Uppdatera tabl??er");
        updateOption.addActionListener(e -> currentController.updateEpisodeLists());
        JMenu channelsMenu = new JMenu("V??lj kanal");
        radioMenu.add(channelsMenu);
        radioMenu.addSeparator();
        radioMenu.add(updateOption);
        JMenu p2Menu = new JMenu("P2");
        JMenu p3Menu = new JMenu("P3");
        JMenu p4Menu = new JMenu("P4");
        JMenu srMenu = new JMenu("SR");
        for(ChannelModel c : channelList){
            //add the channel to the menu
            JMenuItem item = new JMenuItem(c.getName());
            item.addActionListener(e -> currentController.getScheduledEpisodes(c));
            //sort channels in menu by start of name
            switch (c.getName().substring(0, 2)) {
                case "P2" -> p2Menu.add(item);
                case "P3" -> p3Menu.add(item);
                case "P4" -> p4Menu.add(item);
                case "SR" -> srMenu.add(item);
                default -> channelsMenu.add(item);
            }
        }
        channelsMenu.addSeparator();
        channelsMenu.add(p2Menu);
        channelsMenu.add(p3Menu);
        channelsMenu.add(p4Menu);
        channelsMenu.add(srMenu);
    }

    /**
     * Sets up the loading screen that can be shown when waiting for a channels episodes to load
     */
    private void initLoadingScreen(){
        loadingScreen = new JPanel(new BorderLayout());
        JLabel loadingText = new JLabel("H??mtar kanaldata fr??n SR...", SwingConstants.CENTER);
        loadingScreen.add(loadingText, BorderLayout.CENTER);
    }

    /**
     * Removes all components from the contentPanel
     * Used by other methods to update the information on the screen
     */
    private void clearContentPanel(){
        Component[] components = contentPanel.getComponents();
        for(Component c : components){
            contentPanel.remove(c);
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}