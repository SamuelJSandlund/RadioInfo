import controllers.RadioInfoContoller;
import models.APIHandler;
import models.ChannelModel;
import models.EpisodeModel;
import views.RadioInfoGUI;

import javax.swing.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        APIHandler apiHandler = new APIHandler();
        List<ChannelModel> channelList = apiHandler.getChannels();

        for(ChannelModel c : channelList){
            System.out.println("Name: " + c.getName() + " id: " + c.getId());
        }

        List<EpisodeModel> episodeList = apiHandler.getScheduledEpisodes(channelList.get(10));

        for(EpisodeModel e : episodeList){
            System.out.println("\nEpisode Name: " + e.getTitle() + "\n" + e.getDescription() +"\nEpisode id: " + e.getId());
        }

        RadioInfoContoller contoller = new RadioInfoContoller();
        contoller.newGui();
    }
}
