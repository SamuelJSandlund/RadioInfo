package models;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class APIHandler {

    /**
     * Gets info on all radio channels from the Swedish public radio (SR) API
     * @return List of documents with channel info in XML format
     */
    public List<Document> getChannels(){
        ArrayList<ChannelModel> results = new ArrayList<>();
        try{
            //send request to API
            URL endpoint = new URL("http://api.sr.se/api/v2/channels");
            HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new RuntimeException("Non \"OK\" response code received: " + connection.getResponseCode());
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
                    throw new RuntimeException("Non \"OK\" response code received: " + connection.getResponseCode());
                }
                doc = documentBuilder.parse(endpoint.openStream());
                documentList.add(doc);
            }
            return documentList;
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets info on scheduledEpisodes on a given radio channel from the Swedish public radio API
     * @param channelId id for the channel to get episodes from
     * @return List of documents with info on scheduled episodes in XML format
     */
    public List<Document> getScheduledEpisodes(int channelId){
        try {
            URL endpoint = new URL("http://api.sr.se/v2/scheduledepisodes?channelid=" + channelId);
            HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new RuntimeException("Non \"OK\" response code received: " + connection.getResponseCode());
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document doc = documentBuilder.parse(endpoint.openStream());
            doc.getDocumentElement().normalize();

            NodeList pageCountList = doc.getElementsByTagName("totalpages");
            Node noPagesNode = pageCountList.item(0);
            Element noPagesElement = (Element) noPagesNode;
            int pageCount = Integer.parseInt(noPagesElement.getTextContent());

            ArrayList<Document> documentList = new ArrayList<>();
            documentList.add(doc);
            for(int i = 2 ; i <= pageCount ; i++){
                endpoint = new URL("http://api.sr.se/v2/scheduledepisodes?channelid=" + channelId + "&page=" + i);
                connection = (HttpURLConnection) endpoint.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                    throw new RuntimeException("Non \"OK\" response code received: " + connection.getResponseCode());
                }
                doc = documentBuilder.parse(endpoint.openStream());
                documentList.add(doc);
            }
            return documentList;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }
}
