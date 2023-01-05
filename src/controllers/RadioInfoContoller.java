package controllers;

import models.APIHandler;
import models.ChannelModel;
import models.EpisodeModel;
import views.RadioInfoGUI;

import javax.swing.*;
import java.util.List;

public class RadioInfoContoller {

    private RadioInfoGUI gui;
    private APIHandler apiHandler;

    public RadioInfoContoller(){
        apiHandler = new APIHandler();
    }

    public void newGui(){
        SwingUtilities.invokeLater( () -> {
            gui = new RadioInfoGUI(apiHandler.getChannels());
            gui.setController(this);
        });
    }

    public void getChannelSchedule(ChannelModel channel){
        List<EpisodeModel> episodes = apiHandler.getScheduledEpisodes(channel);
        for(EpisodeModel e : episodes){
            System.out.println(e.getTitle() + " " + e.getSubTitle());
        }
        gui.showChannel(channel.getName());
    }
}
