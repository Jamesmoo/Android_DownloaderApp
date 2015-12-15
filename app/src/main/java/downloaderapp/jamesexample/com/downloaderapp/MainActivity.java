package downloaderapp.jamesexample.com.downloaderapp;

import android.os.AsyncTask;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private Button btnParse;
    private ListView listApps;
    private String mFileContents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnParse = (Button) findViewById(R.id.btnParse);

        btnParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: add parse activation code
                ParseApplications parseApplications = new ParseApplications(mFileContents);
                parseApplications.process();

                ArrayAdapter<Application> arrayAdapter = new ArrayAdapter<Application>(
                        MainActivity.this, R.layout.list_item, parseApplications.getApplications());
                listApps.setAdapter(arrayAdapter);
            }
        });

        listApps = (ListView) findViewById(R.id.xmlListView);

        DownloadData downloadData = new DownloadData();

        //the 'execute' method is not declared by the user, it is a keyword to start the AsyncTask
        //this begins the 'doInBackground' portion of the task
        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //AsyncTask parameter explanation
    //  <the download location: where the file is, void but normally used for a progress bar, result: the response>
    private class DownloadData extends AsyncTask<String, Void, String>{


        @Override
        protected String doInBackground(String... params) {
            mFileContents = downloadXMLFile(params[0]);
            if (mFileContents == null){
                Log.d("DownloadData", "Error Downloading");
            }
            return mFileContents;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            Log.d("DownloadData", "Error Downloading: " + result);

            //this is where the user interface gets updated
            //any updates to the UI from anywhere else will fail

        }

        private String downloadXMLFile(String urlPath){
            //temporary buffer to store the contents of the file
            StringBuilder tempBuffer = new StringBuilder();

            try{

                //establish the connection
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();

                Log.d("DownloadData", "URL response code " + response);

                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);

                // == download the data == //
                //  declare a buffer
                int charRead;
                char[] inputBuffer = new char[500];

                while(true){
                    charRead = isr.read(inputBuffer);

                    //since the loop is "while true" the loop will read continuously
                    //if it reaches a point there is no data in the buffer, read is done
                    if (charRead <= 0 ){
                        break;
                    }
                    //append to a temp buffer before reading the next
                    tempBuffer.append(String.copyValueOf(inputBuffer,0,charRead));
                }
                return tempBuffer.toString();
            }
            catch(IOException e){
                Log.d("DownloadData", "IOException reading data " + e.getMessage());
                e.printStackTrace();
            }
            catch(SecurityException e){
                Log.d("DownloadData", "Security exception");
            }
            return null;
        }
    }
}
