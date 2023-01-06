package controllers;

import models.*;
import org.w3c.dom.Document;
import views.RadioInfoGUI;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RadioInfoContoller {

    private RadioInfoGUI gui;
    private EpisodeListCache savedEpisodeList;

    public RadioInfoContoller(){
        savedEpisodeList = new EpisodeListCache();
    }

    public void newGui(){
        //get available channels
        List<Document> channelInformation = new APIHandler().getChannels();
        List<ChannelModel> availableChannels = new XMLParser().parseChannels(channelInformation);
        //create new view
        SwingUtilities.invokeLater( () -> {
            gui = new RadioInfoGUI(availableChannels);
            gui.setController(this);
        });
    }

    public void getScheduledEpisodes(ChannelModel channel){
        SwingWorker worker = new SwingWorker() {
            RadioChannelTableModel channelTableModel;
            List<EpisodeModel> episodes;
            @Override
            protected Object doInBackground(){
                //get episode list from memory if available
                if (savedEpisodeList.hasSavedEpisodeList(channel.getId())){
                    channelTableModel = savedEpisodeList.getEpisodeList(channel.getId());
                }
                //else get channels from the API
                else{
                    List<Document> documentList = new APIHandler().getScheduledEpisodes(channel.getId());
                    episodes = new XMLParser().parseEpisodes(documentList);
                    savedEpisodeList.saveEpisodeList(channel.getId(), episodes);
                    channelTableModel = savedEpisodeList.getEpisodeList(channel.getId());
                }
                for(EpisodeModel e : episodes){
                    System.out.println(e.getTitle() + " " + e.getSubTitle());
                }
                return null;
            }
            @Override
            protected void done(){
                gui.setCurrentChannel(channelTableModel);
            }
        };
        worker.execute();
    }

    public void updateEpisodeLists(){
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground(){
                HashMap<Integer, RadioChannelTableModel> table = savedEpisodeList.getTable();
                for(int key : table.keySet()){
                    if(!table.containsKey(key)){continue;}
                    List<Document> documentList = new APIHandler().getScheduledEpisodes(key);
                    List<EpisodeModel> episodes = new XMLParser().parseEpisodes(documentList);
                    savedEpisodeList.getEpisodeList(key).updateEpisodeList(episodes);
                }
                return null;
            }
        };
        worker.execute();
    }
}
