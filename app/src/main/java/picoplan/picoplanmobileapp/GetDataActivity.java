package picoplan.picoplanmobileapp;

import java.lang.String;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.app.Activity;

/**
 * Created by roland on 4/8/15.
 */
public class GetDataActivity extends Activity {
    TextView picoIsConnected;
    String datAskedFor;
    String dataValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_data_activity);

        Intent intent = getIntent();
        datAskedFor = intent.getStringExtra(MainActivity.DATA);
        dataValue = intent.getStringExtra(MainActivity.VALUE);

        picoIsConnected = (TextView) findViewById(R.id.pico_is_connected);

        // Check wether is connected
        if(isConnected()){
            picoIsConnected.setBackgroundColor(0xFF00CC00);
            picoIsConnected.setText("You are connected");
        }
        else{
            picoIsConnected.setText("You are not connected");
        }

        String url = "http://5.196.160.179/app_dev.php/api/";
        String trailString;
        switch (datAskedFor) {
            case "users":
                trailString = "users/" + dataValue;
                break;
            case "teams":
                trailString = getString(R.string.teams_trailstring);
                break;
            case "leagues":
                trailString = getString(R.string.leagues_trailstring);
                break;
            default:
                throw new RuntimeException("Unknown trailString");
        }
        url +=trailString;


        // Using AsyncTask for network operations on separate thread
        new HttpAsyncTask().execute(url);
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

            switch (datAskedFor) {
                case "users":
                    displayUser(result);
                    break;
                case "teams":
                    displayTeams(result);
                    break;
                case "leagues":
                    displayLeagues(result);
                    break;
                default:
                    throw new RuntimeException("Unkow data asked for");

            }
        }

        private void displayTeams(String result) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.data_layout);

            try {
                JSONArray jsonResponse = new JSONArray(result);
                ArrayList<TextView> viewList = new ArrayList<TextView>();
                ArrayList<String> teamName = new ArrayList<String>();
                ArrayList<String> teamDescription = new ArrayList<String>();
                ArrayList<String> teamScore = new ArrayList<String>();

                setTeamViewList(jsonResponse, viewList, teamName, teamDescription, teamScore);
                addTeamViewsToLayout(layout, viewList);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        private void addTeamViewsToLayout(LinearLayout layout, ArrayList<TextView> viewList) {
            for (TextView item : viewList) {
                layout.addView(item);
            }
        }

        private void setTeamViewList(JSONArray jsonResponse, ArrayList<TextView> viewList, ArrayList<String> teamName, ArrayList<String> teamDescription, ArrayList<String> teamScore) throws JSONException {
            for (int i = 0; i <  jsonResponse.length(); i++) {
                JSONObject team = jsonResponse.getJSONObject(i);
                teamName.add(i, "Nom : " + team.getString("nom"));
                teamDescription.add(i, "Description : " + team.getString("description"));
                teamScore.add(i, "Score : " + team.getString("score"));
                TextView picoTeamName = new TextView(getApplicationContext());
                TextView picoTeamDescription = new TextView(getApplicationContext());
                TextView picoTeamScore = new TextView(getApplicationContext());
                picoTeamName.setText(teamName.get(i));
                picoTeamDescription.setText(teamDescription.get(i));
                picoTeamScore.setText(teamScore.get(i));
                viewList.add(picoTeamName);
                viewList.add(picoTeamDescription);
                viewList.add(picoTeamScore);

            }
        }

        private void displayLeagues(String result) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.data_layout);

            try {
                JSONArray jsonResponse = new JSONArray(result);
                ArrayList<TextView> viewList = new ArrayList<TextView>();

                ArrayList<String> leagueName = new ArrayList<String>();
                ArrayList<String> leagueDescription = new ArrayList<String>();
                ArrayList<String> leagueCreator = new ArrayList<String>();

                for (int i = 0; i < jsonResponse.length(); i++) {
                    JSONObject league = jsonResponse.getJSONObject(i);
                    leagueName.add(i, "Nom : " + league.getString("nom"));
                    leagueCreator.add(i, "Créateur : " + league.getJSONObject("user_creator").getString("used_name"));
                    leagueDescription.add(i, "Description : " + league.getString("description"));
                    TextView picoLeagueName = new TextView(getApplicationContext());
                    TextView picoLeagueCreator = new TextView(getApplicationContext());
                    TextView picoLeagueDescription = new TextView(getApplicationContext());
                    picoLeagueName.setText(leagueName.get(i));
                    picoLeagueCreator.setText(leagueCreator.get(i));
                    picoLeagueDescription.setText(leagueDescription.get(i));
                    viewList.add(picoLeagueName);
                    viewList.add(picoLeagueCreator);
                    viewList.add(picoLeagueDescription);
                }
                for (TextView item : viewList) {
                    layout.addView(item);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void displayUser(String result) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.data_layout);
            ArrayList<TextView> viewList = new ArrayList<>();

            try {
                JSONObject jsonResponse = new JSONObject(result);

                String userName;
                userName = jsonResponse.getString("used_name");
                TextView picoUserName = new TextView(getApplicationContext());
                picoUserName.setText(userName);
                viewList.add(picoUserName);

                String lastName = "Nom : ";
                lastName += jsonResponse.getString("last_name");
                TextView picoLastName = new TextView(getApplicationContext());
                picoLastName.setText(lastName);
                viewList.add(picoLastName);

                String firstName = "Prénom : ";
                firstName += jsonResponse.getString("first_name");
                TextView picoFirstName = new TextView(getApplicationContext());
                picoFirstName.setText(firstName);
                viewList.add(picoFirstName);

                String email = "Email : ";
                email += jsonResponse.getString("email");
                TextView picoUserEmail = new TextView(getApplicationContext());
                picoUserEmail.setText(email);
                viewList.add(picoUserEmail);

                addTeamViewsToLayout(layout, viewList);

            } catch (JSONException e) {
                e.printStackTrace();
                String userDoesNotExist = "L'utilisateur " + dataValue + " n'existe pas";
                TextView picoUserDoesNotExist = new TextView(getApplicationContext());
                picoUserDoesNotExist.setText(userDoesNotExist);
                viewList.add(picoUserDoesNotExist);
                addTeamViewsToLayout(layout, viewList);
            }
        }
    }
}
