package models;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all interaction between the application and the Swedish public radio API
 * @author Samuel Sandlund
 * @version 1.1 getImage return type changed from ImageIcon to abstract Image
 * @since 2023-02-07
 */
public class APIHandler {
    private String errorMessage = "ERROR: ";

    /**
     * Gets info on all radio channels from the Swedish public radio (SR) API
     * if an error occurs the function returns null and the error message is set accordingly
     * @return List of documents with channel info in XML format or null if an error occured
     */
    public List<Document> getChannels(){
        try{
            //send request to API
            URL endpoint = new URL("http://api.sr.se/api/v2/channels");
            HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                errorMessage += connection.getResponseCode() + " fel vid anslutning till SRs API\n";
                return null;
            }

            //parse document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document doc = documentBuilder.parse(endpoint.openStream());
            //normalize
            doc.getDocumentElement().normalize();


            //get page count
            NodeList pageCountList = doc.getElementsByTagName("totalpages");
            Node noPagesNode = pageCountList.item(0);
            Element noPagesElement = (Element) noPagesNode;
            int pageCount = Integer.parseInt(noPagesElement.getTextContent());

            //get document for all pages
            ArrayList<Document> documentList = new ArrayList<>();
            documentList.add(doc);
            for(int i = 2 ; i <= pageCount ; i++){
                endpoint = new URL("http://api.sr.se/api/v2/channels?page=" + i);
                connection = (HttpURLConnection) endpoint.openConnection();
                if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                    errorMessage += connection.getResponseCode() + " anslutningen till SRs API avbr??ts\n";
                    return null;
                }
                doc = documentBuilder.parse(endpoint.openStream());
                documentList.add(doc);
            }
            return documentList;
        } catch (ProtocolException e) {
            errorMessage += "Fel har uppst??tt i protokollet som anv??nds f??r att h??mta kanaler fr??n SRs API\n";
            return null;
        } catch (MalformedURLException e) {
            errorMessage += "URL som anv??nds f??r att h??mta kanaler fr??n SRs API ??r felformatterad\n";
            return null;
        } catch (IOException e) {
            errorMessage += "Ett fel uppstod n??r tillg??ngliga kanaler skulle h??mtas fr??n SRs API\n";
            return null;
        } catch (ParserConfigurationException e) {
            errorMessage += "Ett fel uppstod vid f??rberedelse att l??sa dokument fr??n SRs API\n";
            return null;
        } catch (SAXException e) {
            errorMessage += "Ett fel uppstod vid l??sning av dokument fr??n SRs API\n";
            return null;
        }
    }

    /**
     * Gets info on scheduledEpisodes on a given radio channel from the Swedish public radio API
     * Before 6:00 (AM) UTC the list includes schedules from the previous day and after 12:00 (AM) it contains
     * episodes from the next day
     * if an error occurs the function returns null and the error message is set accordingly
     * @param channelId id for the channel to get episodes from
     * @return List of documents with info on scheduled episodes in XML format
     */
    public List<Document> getScheduledEpisodes(int channelId){
        ArrayList<Document> documentList = new ArrayList<>();
        //determine for which days to get schedules
        ArrayList<String> days = new ArrayList<>();
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        days.add(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        ZonedDateTime maxLimit = now.plusHours(12);
        ZonedDateTime minLimit = now.minusHours(6);
        if (maxLimit.getDayOfMonth() != now.getDayOfMonth()){
            days.add(maxLimit.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        } else if (minLimit.getDayOfMonth() != now.getDayOfMonth()) {
            days.add(minLimit.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        //get documents for the determined days
        for (String day : days){
            try {
                URL endpoint = new URL("http://api.sr.se/v2/scheduledepisodes?channelid=" + channelId +
                        "&date=" + day);
                HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
                if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                    errorMessage += connection.getResponseCode() + " fel vid anslutning till SRs API\n";
                    return null;
                }

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = factory.newDocumentBuilder();
                Document doc = documentBuilder.parse(endpoint.openStream());

                NodeList pageCountList = doc.getElementsByTagName("totalpages");
                Node noPagesNode = pageCountList.item(0);
                Element noPagesElement = (Element) noPagesNode;
                int pageCount = Integer.parseInt(noPagesElement.getTextContent());
                documentList.add(doc);
                for(int i = 2 ; i <= pageCount ; i++){
                    endpoint = new URL("http://api.sr.se/v2/scheduledepisodes?channelid=" + channelId +
                            "&date=" + day + "&page=" + i);
                    connection = (HttpURLConnection) endpoint.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                        errorMessage += connection.getResponseCode() + " anslutningen till SRs API avbr??ts\n";
                        return null;
                    }
                    doc = documentBuilder.parse(endpoint.openStream());
                    documentList.add(doc);
                }
            } catch (MalformedURLException e) {
                errorMessage += "URL som anv??nds f??r att h??mta avsnitt fr??n SRs API ??r felformatterad\n";
                return null;
            } catch (ProtocolException e) {
                errorMessage += "Fel har uppst??tt i protokollet som anv??nds f??r att h??mta avsnitt fr??n SRs API\n";
                return null;
            } catch (IOException e) {
                errorMessage += "Ett fel uppstod n??r avsnitt skulle h??mtas fr??n SRs API\n";
                return null;
            } catch (ParserConfigurationException e) {
                errorMessage += "Ett fel uppstod vid f??rberedelse att l??sa dokument fr??n SRs API\n";
                return null;
            } catch (SAXException e) {
                errorMessage += "Ett fel uppstod vid l??sning av dokument fr??n SRs API\n";
                return null;
            }
        }
        return documentList;
    }

    /**
     * Gets an image icon from a given url
     * @param url link to the image
     * @return ImageIcon or null if the image could not be accessed
     */
    public Image getImage(String url){
        try{
            return ImageIO.read(new URL(url)).
                    getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        }catch (IOException e) {
            return null; //skip the image if it could not be accessed
        }
    }

    /**
     * Returns the error message from this object.
     * @return Error message
     */
    public String getErrorMessage(){
        return errorMessage;
    }
}