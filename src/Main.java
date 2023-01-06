import controllers.RadioInfoContoller;
import models.APIHandler;
import models.ChannelModel;
import models.EpisodeModel;
import views.RadioInfoGUI;

import javax.swing.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        RadioInfoContoller contoller = new RadioInfoContoller();
        contoller.newGui();
    }
}
