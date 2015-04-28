package picoplan.picoplanmobileapp;

import android.os.Bundle;
import android.view.View;

import android.app.Activity;

import android.content.Intent;


public class MainActivity extends Activity {

    public final static String DATA = "picoplan.picoplanmobileapp.DATA";
    protected String data_asked_for;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void setAskedData(View v){

        switch(v.getId()){
            case R.id.btn_user:
                data_asked_for = "user";
                break;
            case R.id.btn_league:
                data_asked_for = "leagues";
                break;
            case R.id.btn_team:
                data_asked_for = "teams";
                break;
            default:
                throw new RuntimeException("Unknow button ID");
        }

        Intent intent;
        intent = new Intent(this, GetDataActivity.class);

        intent.putExtra(DATA, data_asked_for);
        startActivity(intent);
    }
}
