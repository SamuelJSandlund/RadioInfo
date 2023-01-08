package controllers;

import models.*;
import org.w3c.dom.Document;
import views.RadioInfoGUI;

import javax.swing.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles user inputs from the applications user interface
 * Communicates between the view and models, also manages the timer for automatic data updates
 * @author Samuel Sandlund
 * @version 1.0
 * @since 2023-01-08
 */
public class RadioInfoController {
    private RadioInfoGUI gui;
    private final EpisodeListCache savedChannelSchedules;
    private final Timer updateTimer;

    /**
     * Creates a new controller and starts the update timer
     */
    public RadioInfoController(){
        savedChannelSchedules = new EpisodeListCache();
        updateTimer = new Timer(1000 * 3600, e -> updateEpisodeLists());
        updateTimer.setRepeats(true);
        updateTimer.start();
        openGui();
    }

    /**
     * Creates a new view managed by this controller
     */
    private void openGui(){
        //get available channels
        List<Document> channelInformation = new APIHandler().getChannels();
        List<ChannelModel> availableChannels = new XMLParser().parseChannels(channelInformation);
        //create new view
        SwingUtilities.invokeLater( () -> {
            gui = new RadioInfoGUI(availableChannels);
            gui.setController(this);
        });
    }

    /**
     * Gets information about radio-programs that are broadcast on the given channel
     * between 6 hours before and 12 hours after current time
     * @param channel The channel to get episodes from
     */
    public void getScheduledEpisodes(ChannelModel channel){
        SwingWorker worker = new SwingWorker() {
            RadioChannelTableModel channelTableModel;
            boolean success = true;
            List<EpisodeModel> episodes;
            @Override
            protected Object doInBackground(){
                //if the channels schedule has not been taken from the API yet, get it now, else get from cache
                if (!savedChannelSchedules.hasSavedEpisodeList(channel.getId())) {
                    gui.showLoadingScreen();
                    APIHandler apiHandler = new APIHandler();
                    List<Document> documentList = apiHandler.getScheduledEpisodes(channel.getId());
                    if (documentList == null){
                        gui.showErrorMessage(apiHandler.getErrorMessage());
                        success = false;
                        return null;
                    }
                    episodes = new XMLParser().parseEpisodes(documentList);
                    episodes = trimEpisodeList(episodes);
                    savedChannelSchedules.saveEpisodeList(channel, episodes);
                }
                channelTableModel = savedChannelSchedules.getEpisodeList(channel.getId());
                return null;
            }
            @Override
            protected void done(){
                if(success){
                    gui.setCurrentChannel(channelTableModel);
                }
                else{
                    gui.showStartScreen();
                }
            }
        };
        worker.execute();
    }

    /**
     * Loops through the entries in the EpisodeListCache and updates the list of episodes for all saved channels
     * the updates run on a background thread using a SwingWorker.
     * This also resets the automatic update timer, so there will always be an hour to the next scheduled update
     * no matter how the update was triggered.
     * If an error occurs while fetching new episodes, tells the view to show a relevant error message
     */
    public void updateEpisodeLists(){
        updateTimer.restart();
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground(){
                for(int key : savedChannelSchedules.keySet()){
                    APIHandler apiHandler = new APIHandler();
                    List<Document> documentList = apiHandler.getScheduledEpisodes(key);
                    if(documentList == null){
                        gui.showErrorMessage("Uppdatering av tablådata misslyckades\n"+apiHandler.getErrorMessage());
                        return null;
                    }
                    List<EpisodeModel> episodes = new XMLParser().parseEpisodes(documentList);
                    episodes = trimEpisodeList(episodes);
                    savedChannelSchedules.getEpisodeList(key).updateEpisodeList(episodes);
                }
                return null;
            }
        };
        worker.execute();
    }

    /**
     * Removes episode models with a start time of 6 hours before or 12 hours after current time from the given list
     * @param episodes list containing channel models
     * @return list with only channel models that start within the defined time-span
     */
    private List<EpisodeModel> trimEpisodeList(List<EpisodeModel> episodes){
        ArrayList<EpisodeModel> result = new ArrayList<>();
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        for(EpisodeModel e : episodes){
            ZonedDateTime episodeStart = ZonedDateTime.ofInstant(e.getStartTime(), ZoneId.of("UTC"));
            if (episodeStart.isAfter(now.minusHours(6)) && episodeStart.isBefore(now.plusHours(12))){
                result.add(e);
            }
        }
        return result;
    }
}