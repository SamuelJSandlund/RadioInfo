package views;

import controllers.RadioInfoContoller;
import models.ChannelModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.awt.*;

public class RadioInfoGUI {
    private RadioInfoContoller currentController;
    private List<ChannelModel> channelList;
    private JPanel contentPanel;

    private CardLayout contentCardLayout;
    private JMenu channelsMenu;
    private JMenu p2Menu;
    private JMenu p3Menu;
    private JMenu p4Menu;
    private JMenu srMenu;

    public RadioInfoGUI(List<ChannelModel> channelList){
        this.channelList = channelList;
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setSize(680, 480);
        window.setLayout(new BorderLayout());
        contentPanel = new JPanel();
        contentCardLayout = new CardLayout();
        contentPanel.setLayout(contentCardLayout);
        window.add(contentPanel, BorderLayout.CENTER);
        JPanel greetingScreen = new JPanel();
        greetingScreen.add(new JLabel("Välj kanal i menyn!"));
        contentPanel.add(greetingScreen, "HELLO");
        contentCardLayout.show(contentPanel, "HELLO");
        JMenuBar menuBar = new JMenuBar();
        channelsMenu = new JMenu("Kanaler");
        p2Menu = new JMenu("P2");
        p3Menu = new JMenu("P3");
        p4Menu = new JMenu("P4");
        srMenu = new JMenu("SR");
        updateChannelList();
        channelsMenu.addSeparator();
        channelsMenu.add(p2Menu);
        channelsMenu.add(p3Menu);
        channelsMenu.add(p4Menu);
        channelsMenu.add(srMenu);

        JMenu settingsMenu = new JMenu("Inställningar");

        menuBar.add(channelsMenu);
        menuBar.add(settingsMenu);
        window.add(menuBar, BorderLayout.NORTH);

        window.setVisible(true);
    }

    public void setController(RadioInfoContoller controller){
        currentController = controller;
    }

    public void showChannel(String channelName){
        contentCardLayout.show(contentPanel, channelName);
    }

    private void updateChannelList(){
        for(ChannelModel c : channelList){
            JMenuItem item = new JMenuItem(c.getName());
            JPanel channelPanel = new JPanel();
            channelPanel.add(new JLabel(c.getName()));
            contentPanel.add(channelPanel, c.getName());
            item.addActionListener(e -> currentController.getChannelSchedule(c));
            //sortera kanaler utifrån början på namn
            switch (c.getName().substring(0, 2)) {
                case "P2" -> p2Menu.add(item);
                case "P3" -> p3Menu.add(item);
                case "P4" -> p4Menu.add(item);
                case "SR" -> srMenu.add(item);
                default -> channelsMenu.add(item);
            }
        }
    }
}
