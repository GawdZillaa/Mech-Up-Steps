package com.zero.dee.steps;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class DatabaseHandler extends AsyncTask <String, Void, String>{

//    string serverMainName = "mysql.hostinger.com";
//    string serverUserName = "u888093937_dee";
//    string serverPassword = "Deedee426";
//    string serverDatabase = "u888093937_zero";
//    string serverKeycode = "defaultkey123";
//
//    string url_setValue = "http://dee-server.xyz/Sql_CheckValue.php";
//    string url_setValue2 = "http://dee-server.xyz/Sql_UpdateValue.php";

    Context context;

    AlertDialog alertDialog;

    String outResult = "";

    private  SyncData syncData;
    String tryUser = "";

    DatabaseHandler(Context ctx, SyncData dataTo)
    {
        context = ctx;
        this.syncData = dataTo;
    }

    @Override
    protected String doInBackground(String ... params) {
        String type = params[0];
        String login_url = "http://dee-server.xyz/CheckValueTwo.php";
        if (type.equals("login")){
            try {
                String user_name = params[1];
                String pass_word = params[2];

                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("check_username","UTF-8")+"="+URLEncoder.encode(user_name, "UTF-8")+"&"
                                    +URLEncoder.encode("check_password" , "UTF-8")+"="+URLEncoder.encode(pass_word, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line = "";
                while((line = bufferedReader.readLine())!=null){
                    result += line;

                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return result;

            } catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result.equals("Incorrect"))
        {
            alertDialog.setMessage("Incorrect Password");
            alertDialog.show();
        }
        else if (result.equals(("Unknown")))
        {
            alertDialog.setMessage("Incorrect User Name");
            alertDialog.show();
        }
        else if (result.equals("Error"))
        {
            alertDialog.setMessage("Database Error");
            alertDialog.show();
        }
        else if (result.equals("Success"))
        {
            alertDialog.setMessage("Login Success");
            alertDialog.show();
            syncData.loginSync(result, tryUser);

            alertDialog.dismiss();
            alertDialog = null;

        }


        this.outResult = result;
    }
    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Login Status");
    }




    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    public String getResult()
    {
        return this.outResult;
    }

}
