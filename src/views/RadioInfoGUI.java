package views;

import controllers.RadioInfoContoller;
import models.ChannelModel;
import models.RadioChannelTableModel;

import javax.swing.*;
import java.util.List;
import java.awt.*;

public class RadioInfoGUI {
    private RadioInfoContoller currentController;
    private List<ChannelModel> channelList;

    private JFrame window;
    private JPanel contentPanel;
    private JPanel tablePanel;
    private JTable channelTable;
    private JMenu channelsMenu;



    public RadioInfoGUI(List<ChannelModel> channelList){
        this.channelList = channelList;
        initWindow();
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        window.add(contentPanel, BorderLayout.CENTER);
        JPanel greetingScreen = new JPanel();
        greetingScreen.add(new JLabel("Välj kanal i menyn!"));
        contentPanel.add(greetingScreen);
        JMenuBar menuBar = new JMenuBar();
        initChannelMenu();
        JMenu settingsMenu = new JMenu("Inställningar");
        menuBar.add(channelsMenu);
        menuBar.add(settingsMenu);

        window.add(menuBar, BorderLayout.NORTH);

        window.setVisible(true);
    }

    public void setController(RadioInfoContoller controller){
        currentController = controller;
    }

    public void clear(){
        clearContentPanel();
    }

    private void clearContentPanel(){
        Component[] components = contentPanel.getComponents();
        for(Component c : components){
            contentPanel.remove(c);
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void setCurrentChannel(RadioChannelTableModel channelTableModel){
        buildChannelTablePanel(channelTableModel);
        showChannelTable();
    }

    private void buildChannelTablePanel(RadioChannelTableModel channelTableModel){
        tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        if(channelTableModel.isEmpty()){
            JLabel message = new JLabel("Kanalen har inga planerade sändningar");
            tablePanel.add(message);
        }
        else {
            channelTable = new JTable(channelTableModel);
            JScrollPane scrollPane = new JScrollPane(channelTable);
            tablePanel.add(scrollPane, BorderLayout.CENTER);
            JButton updateButton = new JButton("Uppdatera");
            updateButton.addActionListener(e -> currentController.updateEpisodeLists());
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(updateButton);
            tablePanel.add(buttonPanel, BorderLayout.SOUTH);
        }
    }

    private void showChannelTable(){
        clearContentPanel();
        contentPanel.add(tablePanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void initWindow() {
        window = new JFrame("RadioInfo");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setSize(680, 500);
        window.setLayout(new BorderLayout());
    }

    private void initChannelMenu(){
        channelsMenu = new JMenu("Kanaler");
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
}
