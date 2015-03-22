package picoplan.picoplanmobileapp;

import java.lang.String;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;


public class MainActivity extends Activity {
    TextView picoUserName;
    TextView picoLastName;
    TextView picoFirstName;
    TextView picoUserEmail;
    TextView picoIsConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get references to the views
        picoUserName = (TextView) findViewById(R.id.picoUserName);
        picoLastName = (TextView) findViewById(R.id.picoLastName);
        picoFirstName = (TextView) findViewById(R.id.picoFirstName);
        picoUserEmail = (TextView) findViewById(R.id.picoUserEmail);
        picoIsConnected = (TextView) findViewById(R.id.picoIsConnected);

        // Check wether is connected
        if(isConnected()){
            picoIsConnected.setBackgroundColor(0xFF00CC00);
            picoIsConnected.setText("You are connected");
        }
        else{
            picoIsConnected.setText("You are not connected");
        }

        // Using AsyncTask for network operations on separate thread
        new HttpAsyncTask().execute("http://5.196.160.179/app_dev.php/api/users/usertest");
    }

    public static String GET(String url){
        InputStream inputStream;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);
            }
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null) {
            result += line;
        }

        inputStream.close();
        return result;

    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Done !", Toast.LENGTH_LONG).show();

            JSONObject jsonResponse = null;
            try {
                jsonResponse = new JSONObject(result);
                String userName;
                userName = jsonResponse.getString("used_name");
                picoUserName.setText(userName);

                String lastName = "Nom : ";
                lastName += jsonResponse.getString("last_name");
                picoLastName.setText(lastName);

                String firstName = "Prénom : ";
                firstName += jsonResponse.getString("first_name");
                picoFirstName.setText(firstName);

                String email = "Email : ";
                email += jsonResponse.getString("email");
                picoUserEmail.setText(email);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
