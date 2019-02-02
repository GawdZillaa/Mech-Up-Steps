package com.zero.dee.steps;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends Activity implements SyncData{

    EditText UserField, PassField;
    Button submitButton;

    int counter = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserField = (EditText) findViewById(R.id.user);
        PassField = (EditText) findViewById(R.id.pass);

    }


    public void OnLogin(View v){
            String username = UserField.getText().toString();
            String password = PassField.getText().toString();

            String type = "login";

            DatabaseHandler databaseHandler = new DatabaseHandler(this, MainActivity.this);
            databaseHandler.execute(type, username, password);
        }



    @Override
    public void loginSync(String result, String userName) {
        if (result.equals("Success"))
        {
            UserData userData = new UserData(userName);
            userData.CurrentUser = userData;

            Intent i = new Intent(MainActivity.this, dash.class);
            i.putExtra("UserDatas", userData);
            startActivity(i);
        }
    }

}
