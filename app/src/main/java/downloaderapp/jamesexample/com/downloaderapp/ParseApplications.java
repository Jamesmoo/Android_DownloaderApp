package downloaderapp.jamesexample.com.downloaderapp;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by JamesMoo on 12/7/2015.
 */
public class ParseApplications {
    private String xmlData;
    private ArrayList<Application> applications;

    public ParseApplications(String xmlData) {
        this.xmlData = xmlData;
        applications = new ArrayList<Application>();
    }

    public ArrayList<Application> getApplications() {
        return applications;
    }

    public boolean process(){
        boolean status = true;
        Application currentRecord = null;
        boolean inEntry = false;
        String textValue = "";

        try{
            //this factory method is to help parse an XML
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();


            //true if the factory is configured to produce parsers which are namespace aware; false otherwise.
            factory.setNamespaceAware(true);


            //use the factory to create the xmlpullparser
            XmlPullParser xpp = factory.newPullParser();


            //now that the xml parser is set, need to pass a StringReader object into the
            //XmlPullParser
            StringReader myReader = new StringReader(this.xmlData);
            xpp.setInput(myReader);


            //XmlPullParser has an 'enumerator' that we use in eventType, so as the file is being read the number will update and will be able to know if the read was upsated
            int eventType = xpp.getEventType();


            //so long as the event type returned is not "the end"
            while(eventType != XmlPullParser.END_DOCUMENT){
                String tagName = xpp.getName();

                switch(eventType){

                    case XmlPullParser.START_TAG:
                        Log.d("ParseApplications", "start tag for " + tagName);
                        if (tagName.equalsIgnoreCase("entry")){

                            //this inEntry is important, it tell the next case statement that it is data we want to parse and look at
                            inEntry = true;

                            //application is a transport object, can pass in all the values found in the XML to the different properties/variables of the object
                            currentRecord = new Application();

                        }
                        break;

                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        Log.d("ParseApplications", "End tag for " + tagName);
                        if(inEntry){
                            if (tagName.equalsIgnoreCase("entry")){
                                applications.add(currentRecord);
                                inEntry = false;
                            }
                            else if (tagName.equalsIgnoreCase("name")){
                                currentRecord.setName(textValue);
                            }
                            else if (tagName.equalsIgnoreCase("artist")){
                                currentRecord.setArtist(textValue);
                            }
                            else if (tagName.equalsIgnoreCase("releaseDate")){
                                currentRecord.setReleaseDate(textValue);
                            }
                        }
                        break;

                    default:
                        //nothing
                }
                //will advanced the cursor to the next node in the XML, and will set the event type to that
                eventType = xpp.next();
            }
        }
        catch(Exception e){
            status = false;
            e.printStackTrace();
        }

        for (Application app: applications){
            Log.d("ParseApplications", "Name: " + app.getName());
            Log.d("ParseApplications", "Artist: " + app.getArtist());
            Log.d("ParseApplications", "Release Date: " + app.getReleaseDate());
        }
        return true;
    }
}
