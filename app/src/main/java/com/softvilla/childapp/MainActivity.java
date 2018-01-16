package com.softvilla.childapp;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ////////////////////////
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");
    public static final String TAG = MainActivity.class.getSimpleName();
    //////////////////////////

    private PolicyManager policyManager;
    List<ApplicationInfo> packages;
    int count = 0;
    ProgressDialog dialog;

    private int MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 10;
    private static final int REQUEST_CODE = 0;
    int scale;
    int level;
    float percentage;
    long time1;
    Calendar calander;
    SimpleDateFormat simpledateformat;
    String Date;
    String appName = null;
    String packageName = null;


    private EditText userName,password;
    private Button logIn;
    private SimpleDateFormat dateFormatter;
    private android.app.DatePickerDialog DatePickerDialog;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        policyManager = new PolicyManager(this);
        IntentFilter iF = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent intent = MainActivity.this.registerReceiver(null,iF);
        scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        percentage = level / (float) scale;
        calander = Calendar.getInstance();
        simpledateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date = simpledateformat.format(calander.getTime());


        time1 = 0;



        final PackageManager pm = getPackageManager();

        packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        dialog = new ProgressDialog(this);
       // AppUsage(count);



        /*if(!isMyServiceRunning(TrackingService.class,this)){
            startService(new Intent(this,TrackingService.class));
        }*/
///////////////////////
        /*try {
            // Initiate DevicePolicyManager.
            DevicePolicyManager policyMgr = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

            // Set DeviceAdminDemo Receiver for active the component with different option
            ComponentName componentName = new ComponentName(this, DeviceAdminComponent.class);

            if (!policyMgr.isAdminActive(componentName)) {
                // try to become active
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                        "Click on Activate button to protect your application from uninstalling!");
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        startService(new Intent(this, BackgroundService.class));*/

    //////////////////////////////////////////////////
        //statusCheck();


        /*if(android.os.Build.VERSION.SDK_INT >= 21)
        {
            finishAndRemoveTask();
        }
        else
        {
            finish();
        }*/

        /*Dexter.withActivity(this)
                .withPermissions(

                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {  }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {  }
        }).check();*/
//        ActivityCompat.requestPermissions(this,
//                new String[]{Manifest.permission.RECEIVE_SMS},
//                MY_PERMISSIONS_REQUEST_SMS_RECEIVE);

        userName = (EditText) findViewById(R.id.txt_email);
        password = (EditText) findViewById(R.id.txt_password);

        password.setInputType(InputType.TYPE_NULL);
        //password.requestFocusFromTouch();
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog.show();
            }
        });

        logIn = (Button) findViewById(R.id.loginBtn);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        //userLogin();
        setDateTimeField();

        List<Utilities> utilities = Utilities.listAll(Utilities.class);

        if (utilities.size() == 0) {
            Utilities obj = new Utilities();
            obj.LATEST_CALL_LOG_ID = 0;
            obj.LATEST_SMS_ID = 0;
            obj.LATEST_CONTACT_ID = 0;
            obj.save();
        }

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS,Manifest.permission.RECEIVE_SMS,Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_SMS,Manifest.permission.READ_CONTACTS,Manifest.permission.READ_SMS,Manifest.permission.ACCESS_COARSE_LOCATION},10);
        SharedPreferences sharedPreferences1= this.getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String isLogin = sharedPreferences1.getString("isLogin",null);
        if(isLogin!=null){
            Intent intent1 = new Intent(this, QRCode.class);
            startActivity(intent1);
            this.finish();
        }
    }
    private void setDateTimeField() {


        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                password.setText(String.valueOf(dateFormatter.format(newDate.getTime())));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    /*public void doLogin(View view) {
        String email=((EditText)findViewById(R.id.txt_email)).getText().toString();
        String password=((EditText)findViewById(R.id.txt_password)).getText().toString();
        DoLogin doLoginObj=new DoLogin(this);
        doLoginObj.execute(email,password);
    }

    public void gotoSignup(View view) {
        Intent signup=new Intent(MainActivity.this,Signup.class);
        startActivity(signup);
    }*/

    public void uploadInstalledApp(final int counter){

        if(counter < packages.size()){

           // Toast.makeText(this, "Enter If "+counter +packages.size(), Toast.LENGTH_SHORT).show();

            appName = packages.get(counter).loadLabel(getPackageManager()).toString();
            packageName = packages.get(counter).packageName;
            String icon = null;
            Drawable drawable =  packages.get(counter).loadIcon(getPackageManager());
            Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();

           // Toast.makeText(this, appName+"\n"+packageName, Toast.LENGTH_SHORT).show();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            final byte[] imageBytes = byteArrayOutputStream.toByteArray();
            icon = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            dialog.setTitle("Synching Installed Applications");
            dialog.setMessage("It Will Take some time...\n\n" + String.valueOf(counter + 1) + "/" +String.valueOf(packages.size()));
            dialog.setCancelable(false);
            dialog.show();

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            AndroidNetworking.post("http://noorpublicschool.com/ApiPractice/uploadAppInfo")
                    .addBodyParameter("appName",appName)
                    .addBodyParameter("pkgName", packageName)
                    .addBodyParameter("chidId", preferences.getString("child_id", ""))
                    .addBodyParameter("img", icon)
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            uploadInstalledApp(counter + 1);
                            /*PackageManager packageManager = getPackageManager();
                            ComponentName componentName = new ComponentName(MainActivity.this,MainActivity.class);
                            packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);*/

                            // do anything with response
                           // Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
                           // dialog.dismiss();
                        }
                        @Override
                        public void onError(ANError error) {
                            // handle error
                            Toast.makeText(MainActivity.this,"Server Error...!" /*error.toString()*/, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
        }
        else {
           // Toast.makeText(MainActivity.this,"Successfully ...... ", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this,QRCode.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }


    }


    public void userLogin() {

        /*SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LogIn.this);

        if(!preferences.getString("isLogin","").equalsIgnoreCase("1")){
            Intent intent = new Intent(LogIn.this, LogIn.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            LogIn.this.finish();
        }
        else {
            Intent intent = new Intent(LogIn.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            LogIn.this.finish();
        }*/



        String username = userName.getText().toString();
        String Password = password.getText().toString();



        boolean isEmptyName=false;
        if(TextUtils.isEmpty(userName.getText().toString())) {
            userName.setError("Enter Your Name");
            isEmptyName = true;
        }

        boolean isEmptyPass=false;
        if(TextUtils.isEmpty(password.getText().toString())) {
            password.setError("Enter D-O-B");
            isEmptyPass = true;
        }

        if(!isEmptyName && !isEmptyPass) {

            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Signing in...");
            pDialog.setCancelable(false);
            double lat = 0.0; double lng = 0.0;
//                            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                            pDialog.setIndeterminate(true);
            pDialog.show();

            AndroidNetworking.post("http://noorpublicschool.com/ApiPractice/SignUp")
                    .addBodyParameter("name", username)
                    .addBodyParameter("dob", Password)
                    .addBodyParameter("coordinates", String.valueOf(lat)+","+String.valueOf(lng))
                    .addBodyParameter("bettaryscale", String.valueOf(scale))
                    .addBodyParameter("bettarylevel", String.valueOf(level))
                    .addBodyParameter("bettarypercentage", String.valueOf(percentage))
                    .addBodyParameter("date", Date)
                    //.addBodyParameter("child_id", id)

                    //.addBodyParameter("phone", Phno)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("child_id",response);
                            editor.putString("isLogin","true");
                            editor.putString("pin","12345");
                            editor.apply();
                            // String response_id = response;
                           // Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();

                            // do anything with response
                            uploadInstalledApp(count);

                            startService(new Intent(MainActivity.this,CheckForeGroundAppService.class));

                            /* HIDE aPP Icon */
                            /*SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                            SharedPreferences.Editor editor1 = preferences1.edit();
                           // Intent myIntent = new Intent(MainActivity.this, QRCode.class);
                            SharedPreferences sharedPreferences1 = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                            editor1.putString("isLogin","true");

                            editor1.putString("pin","12345");
                            editor1.apply();*/
                   /* sharedPreferences.edit().putString("isLogin","true").apply();
                    sharedPreferences.edit().putString("userId",jsonObj.getString("id")).apply();
                    sharedPreferences.edit().putString("password",jsonObj.getString("password")).apply();*/
                           // startActivity(myIntent);
                            //finish();
                           pDialog.dismiss();


                        }


                        @Override
                        public void onError(ANError error) {
                            Toast.makeText(MainActivity.this, "Server Error ....!"/*error.toString()+"klk"*/, Toast.LENGTH_SHORT).show();
                            // handle error
                            pDialog.dismiss();
                        }
                    });


        }
    }


    public void Map(View view) {
        userLogin();
        //startActivity(new Intent(this,MainActivity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("Call request code", "This is request======>"+String.valueOf(requestCode));
        if (requestCode == MY_PERMISSIONS_REQUEST_SMS_RECEIVE) {
            // YES!!
            Log.i("TAG", "MY_PERMISSIONS_REQUEST_SMS_RECEIVE --> YES");
        }
    }


   /* public static void exitApplication(Context context)
    {
        Intent intent = new Intent(context, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        context.startActivity(intent);
    }
*/

    public void statusCheck() {
        final LocationManager manager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.LightDialogTheme);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        MainActivity.this.finish();
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        MainActivity.this.overridePendingTransition(0,0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        exitApplication(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        exitApplication(this);
    }*/

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.activate_admin:
                if (!policyManager.isAdminActive()) {
                    Intent activateDeviceAdmin = new Intent(
                            DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    activateDeviceAdmin.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, policyManager.getAdminComponent());
                    activateDeviceAdmin.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                                    "After activating admin, you will be able to block application uninstallation.");
                    startActivityForResult(activateDeviceAdmin,
                            PolicyManager.DPM_ACTIVATION_REQUEST_CODE);
                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode == Activity.RESULT_OK && requestCode == PolicyManager.DPM_ACTIVATION_REQUEST_CODE) {
            // handle code for successfull enable of admin
            /*Intent passwordChangeIntent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
            passwordChangeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(passwordChangeIntent);*/
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //////////////////////////////

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("ResourceType")
    public static void getStats(Context context) {
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
        int interval = UsageStatsManager.INTERVAL_YEARLY;
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();
        Log.d(TAG, "Range start:" + dateFormat.format(startTime));
        Log.d(TAG, "Range end:" + dateFormat.format(endTime));
        UsageEvents uEvents = usm.queryEvents(startTime, endTime);
        while (uEvents.hasNextEvent()) {
            UsageEvents.Event e = new UsageEvents.Event();
            uEvents.getNextEvent(e);
            if (e != null) {
               // Toast.makeText(context, "Event: " + e.getPackageName() + "\t" + e.getTimeStamp(), Toast.LENGTH_SHORT).show();
                // Log.d(TAG, "Event: " + e.getPackageName() + "\t" + e.getTimeStamp());
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static List<UsageStats> getUsageStatsList(Context context) {
        UsageStatsManager usm = getUsageStatsManager(context);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();
        Log.d(TAG, "Range start:" + dateFormat.format(startTime));
        Log.d(TAG, "Range end:" + dateFormat.format(endTime));
        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
        String value = null;
        for (UsageStats u : usageStatsList) {
            if (u.getPackageName().equals("com.softvilla.parentalapp")) {
                u.getLastTimeUsed();
                Log.d(TAG, "Pkg: " + u.getPackageName().equalsIgnoreCase("com.softvilla.childapp") + "\t" + "ForegroundTime: "
                        + u.getTotalTimeInForeground());
                value = "Pkg: " + u.getPackageName() + "\t" + "ForegroundTime: " + TimeUnit.MILLISECONDS.toMinutes(u.getTotalTimeInForeground());

            }
        }
        return usageStatsList;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void printUsageStats(List<UsageStats> usageStatsList) {
        String value = null;
        for (UsageStats u : usageStatsList) {
            if(u.getPackageName().equals(packageName)){
                u.getLastTimeUsed();

                // Toast.makeText(, "Pkg: " + u.getPackageName() + "\t" + "ForegroundTime: " + u.getTotalTimeInForeground(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Pkg: " + u.getPackageName() + "\t" + "ForegroundTime: "
                        + TimeUnit.MILLISECONDS.toMinutes(u.getTotalTimeInForeground()));
                value = "Pkg: " + u.getPackageName() + "\t" + "ForegroundTime: " + u.getTotalTimeInForeground();
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void printCurrentUsageStatus(Context context) {
        printUsageStats(getUsageStatsList(context));
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static String printUsageStatus(Context context) {
        return printUsageStatss(getUsageStatsList(context));
    }
    @SuppressWarnings("ResourceType")
    private static UsageStatsManager getUsageStatsManager(Context context) {
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
        return usm;
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static String printUsageStatss(List<UsageStats> usageStatsList) {
        String value = null;
        for (UsageStats u : usageStatsList) {
            if(u.getPackageName().equals("com.softvilla.parentalapp")){
                u.getLastTimeUsed();
                Log.d(TAG, "Pkg: " + u.getPackageName().equalsIgnoreCase("com.softvilla.childapp") + "\t" + "ForegroundTime: "
                        + u.getTotalTimeInForeground());
                value = "Pkg: " + u.getPackageName() + "\t" + "ForegroundTime: " + TimeUnit.MILLISECONDS.toMinutes(u.getTotalTimeInForeground());
            }


        }
        return value;
    }

//////////////////////////////////////////////////////


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void AppUsage(int counter) {
        //for (int i = 1;i < packages.size();i++) {
        //if(counter<5){


            packageName = packages.get(counter).packageName;
            UsageStatsManager usm = getUsageStatsManager(this);
            Calendar calendar = Calendar.getInstance();
            long endTime = calendar.getTimeInMillis();
            calendar.add(Calendar.YEAR, -1);
            long startTime = calendar.getTimeInMillis();
            Log.d(TAG, "Range start:" + dateFormat.format(startTime));
            Log.d(TAG, "Range end:" + dateFormat.format(endTime));
            List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
            String value = null;

            //counter = usageStatsList.size();

           // for(int i = 1; i<usageStatsList.size();i++){

               // AppUsage(counter+1);
            for (UsageStats u : usageStatsList) {
                // String p = String.valueOf(u.getPackageName().equalsIgnoreCase(packageName));
                if (u.getPackageName().equalsIgnoreCase(packageName)) {
                    String value1 = u.getPackageName();
                    u.getLastTimeUsed();
                    String value2 = String.valueOf(TimeUnit.MILLISECONDS.toMinutes(u.getTotalTimeInForeground()));
                   // Toast.makeText(this, value1 + "\n" + value2 + "minuts", Toast.LENGTH_SHORT).show();
                    // AppUsage(counter+1);
                   /* Log.d(TAG, "Pkg: " + u.getPackageName().equalsIgnoreCase("com.softvilla.childapp") + "\t" + "ForegroundTime: "
                            + u.getTotalTimeInForeground());
                    value = "Pkg: " + u.getPackageName() + "\t" + "ForegroundTime: " + TimeUnit.MILLISECONDS.toMinutes(u.getTotalTimeInForeground());
*/
               // }
            }
          // }
            //AppUsage(counter+1);
        }
        //AppUsage(counter+1);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass,Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
