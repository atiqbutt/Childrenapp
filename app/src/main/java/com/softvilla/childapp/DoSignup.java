package com.softvilla.childapp;

import android.os.AsyncTask;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;

/**
 * Created by Salman on 2/26/2017.
 */

public class DoSignup extends AsyncTask<String,String,String>{

    private ProgressDialog progressDialog;
    InputStream inputStream = null;
    String result = "";
    JSONObject jsonObj;
    private Context mContext;

    public DoSignup (Context context){
        mContext = context;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog=new ProgressDialog(mContext);
        progressDialog.setMessage("Creating account...");
        progressDialog.show();
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface arg0) {
                DoSignup.this.cancel(true);
            }
        });
    }

    @Override
    protected String doInBackground(String... params) {

        String name=params[0];
        String email= params[1];
        String password= params[2];
        String url_select = null;
        try {
            url_select = "https://teensafe.000webhostapp.com/api/signup?name="+URLEncoder.encode(name,"utf-8")+"&email="+URLEncoder.encode(email,"utf-8")+"&password="+URLEncoder.encode(password,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(url_select);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
                Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)
            }
            result = buffer.toString();


        } catch (MalformedURLException | SocketTimeoutException e) {
            e.printStackTrace();
            return "Network Error! Timeout";
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "Network Error!";
        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    } // protected Void doInBackground(String... params)

    protected void onPostExecute(String res) {
        if (res.equals("Network Error!")) {
            this.showErrorDialog();
        }else {
            //parse JSON data
            try {
                this.jsonObj = new JSONObject(this.result);
                String response = jsonObj.getString("response");
                if (response.equals("1")) {
                    this.progressDialog.dismiss();
                    ((Signup)mContext).finish();
                    Toast.makeText(mContext, "Account Created, Please Login!", Toast.LENGTH_SHORT).show();
                } else {
                    this.progressDialog.dismiss();
                    new AlertDialog.Builder(mContext)
                            .setTitle("Alert")
                            .setMessage(jsonObj.getString("description"))
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIconAttribute(android.R.attr.alertDialogIcon)
                            .show();
                }

            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
            } // catch (JSONException e)
        }
    } // protected void onPostExecute(Void v)

    private void showErrorDialog() {
        this.progressDialog.dismiss();
        new AlertDialog.Builder(mContext)
                .setTitle("Alert")
                .setMessage("Network error")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .show();
}

        }
