package picoplan.picoplanmobileapp;

import android.os.Bundle;
import android.view.View;

import android.app.Activity;

import android.content.Intent;
import android.widget.EditText;

import java.util.HashMap;


public class MainActivity extends Activity {

    public final static String DATA = "picoplan.picoplanmobileapp.DATA";
    public final static String VALUE = "picoplan.picoplanmobileapp.VALUE";
    protected String data_asked_for;
    protected String data_value;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void setAskedData(View v){

        switch(v.getId()){
            case R.id.btn_user:
                EditText user = (EditText) findViewById(R.id.search_user);
                data_asked_for = "users";
                data_value = user.getText().toString();
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
        intent.putExtra(VALUE, data_value);
        startActivity(intent);
    }
}
