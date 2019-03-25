package com.hundreddaysloan.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hundreddaysloan.R;
import com.hundreddaysloan.adapter.UserMainAdapter;
import com.hundreddaysloan.model.LoanDetails;
import com.hundreddaysloan.model.UserDetails;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    Dialog hCommonAlertDialog;
    Dialog addloanDialog;

    private List<UserDetails> loanDetailList = new ArrayList<>();
    private RecyclerView loan_recycler_view;
    private UserMainAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    TextView userName, userEMail;
    CircleImageView nav_user_img;
    String mCurrentPhotoPath;

    UserDetails lDetail;
    int loanCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        firebaseAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loan_recycler_view  = (RecyclerView) findViewById(R.id.loan_recycler_view);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Added loan detail", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                addToLoanDetailData();
                addLoanPopup("");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        mAdapter = new UserMainAdapter(loanDetailList, getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        loan_recycler_view.setLayoutManager(mLayoutManager);
        loan_recycler_view.setItemAnimator(new DefaultItemAnimator());
        loan_recycler_view.setAdapter(mAdapter);


        nav_user_img = headerView.findViewById(R.id.nav_user_img);
        userName = headerView.findViewById(R.id.userName);
        userEMail = headerView.findViewById(R.id.userEMail);

        String imageFileName = "100d_user";

//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        String file = null;
        try {
            file = storageDir.getAbsolutePath()+"/"+imageFileName+".jpg";
        } catch (Exception e) {
            e.printStackTrace();
        }

        File image = null;
        if (file != null) {
            image = new File(file);
            if(image.exists()){
                mCurrentPhotoPath = image.getAbsolutePath();
                Log.e("Image","::"+mCurrentPhotoPath);
                setPic();
            }
        }

        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }
        String[] alternativeDispName = new String[0];
        try {
            if(null != firebaseAuth.getCurrentUser().getEmail()) {
                Log.e("HomeActivity", "" + firebaseAuth.getCurrentUser().getEmail());
                userEMail.setText(firebaseAuth.getCurrentUser().getEmail());
                alternativeDispName = firebaseAuth.getCurrentUser().getEmail().split("\\@");
                Log.e("alternativeDispName", "" + alternativeDispName);
            }else{
                userEMail.setText("noemail@noemail.com");
            }
            if(null != firebaseAuth.getCurrentUser().getDisplayName()) {
                Log.e("HomeActivity", "" + firebaseAuth.getCurrentUser().getDisplayName());
                userName.setText(firebaseAuth.getCurrentUser().getDisplayName());
            }else{
                userName.setText(alternativeDispName[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("HomeActivity", "" + firebaseAuth.getCurrentUser().getPhoneNumber());
        Log.e("HomeActivity", "isEmailVerified+" + firebaseAuth.getCurrentUser().isEmailVerified());
        Log.e("HomeActivity", "PhotoUrl+" + firebaseAuth.getCurrentUser().getPhotoUrl());

        prepareUserDetailData();
        sortUserDetails();
    }

    private void setPic() {

        // Get the dimensions of the View
        int targetW = nav_user_img.getWidth();
        int targetH = nav_user_img.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = 0;
        try {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap;
        bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        if(null == bitmap){
//            try {
//                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//                        .permitAll().build();
//                StrictMode.setThreadPolicy(policy);
//                URL url = new URL("https://lh3.googleusercontent.com/-e22PiQ2L3v4/AAAAAAAAAAI/AAAAAAAAAjo/0QlT8UseUD4/photo.jpg");
//                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//            } catch(IOException e) {
//                System.out.println(e);
//            }
//        }
        nav_user_img.setImageBitmap(bitmap);
    }

    private void sortUserDetails(){

        Collections.sort(loanDetailList, new Comparator<UserDetails>(){
            public int compare(UserDetails obj1, UserDetails obj2) {
                // ## Ascending order
//                return obj1.getLoanAmount().compareToIgnoreCase(obj2.getLoanAmount());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    return Boolean.compare(obj1.getUserStatus(), obj2.getUserStatus());
                }else {
                    return Boolean.valueOf(obj1.getUserStatus()).compareTo(Boolean.valueOf(obj2.getUserStatus()));
                }
                // To compare string values
                // return Integer.valueOf(obj1.empId).compareTo(Integer.valueOf(obj2.empId)); // To compare integer values

                // ## Descending order
                // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
            }
        });
    }

    private void initFirebaseDatabase()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
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
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_user_prof)
        {
            startActivity(new Intent(getApplicationContext(),UserProfileActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(getApplicationContext(),UserInfoActivity.class));
        } else if (id == R.id.nav_faq) {
            startActivity(new Intent(getApplicationContext(),UserInfoActivity.class));
        } else if(id == R.id.nav_logout){
            logoutPopup("Are you sure to logout?");
        }

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoutPopup(String msg){

        hCommonAlertDialog =  new Dialog(HomeActivity.this);
        hCommonAlertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        hCommonAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        hCommonAlertDialog.setContentView(R.layout.common_alert);
//        hCommonAlertDialog.setCancelable(false);
        TextView show_msg = hCommonAlertDialog.findViewById(R.id.show_msg);
        show_msg.setText(msg);
        Button btn_alert_ok = hCommonAlertDialog.findViewById(R.id.btn_alert_ok);
        Button btn_alert_cancel = hCommonAlertDialog.findViewById(R.id.btn_alert_cancel);
        btn_alert_cancel.setVisibility(View.VISIBLE);
        btn_alert_ok.setText("Confirm");
        btn_alert_cancel.setText("Cancel");

        btn_alert_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hCommonAlertDialog.dismiss();
                logout();
            }
        });

        btn_alert_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hCommonAlertDialog.dismiss();
            }
        });

        hCommonAlertDialog.show();
    }
    private void logout(){
        firebaseAuth.signOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    private void addToUserDetailData(){
        loanCount = loanCount + 1;
        lDetail = new UserDetails("New User " + loanCount, "98401 98401", false);
        loanDetailList.add(lDetail);

        sortUserDetails();

        mAdapter.notifyDataSetChanged();
        try {
            if(loanDetailList.size()>0)
                loan_recycler_view.getLayoutManager().scrollToPosition(0);
            Snackbar.make(loan_recycler_view, "Added loan detail", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prepareUserDetailData() {
//        LoanDetails(String loanid, int loan_amount, String loan_start_date, String loan_end_date, boolean loan_status)
        lDetail = new UserDetails("User "+loanCount, "98401 98401", true);
        loanDetailList.add(lDetail);

        loanCount = loanCount+1;
        lDetail = new UserDetails("User "+loanCount, "98401 98401", true);
        loanDetailList.add(lDetail);

        loanCount = loanCount+1;
        lDetail = new UserDetails("User "+loanCount, "98401 98401", true);
        loanDetailList.add(lDetail);

        loanCount = loanCount+1;
        lDetail = new UserDetails("User "+loanCount, "98401 98401", true);
        loanDetailList.add(lDetail);

        loanCount = loanCount+1;
        lDetail = new UserDetails("User "+loanCount, "98401 98401",  true);
        loanDetailList.add(lDetail);

        loanCount = loanCount+1;
        lDetail = new UserDetails("User "+loanCount, "98401 98401", true);
        loanDetailList.add(lDetail);

        loanCount = loanCount+1;
        lDetail = new UserDetails("User "+loanCount, "98401 98401", true);
        loanDetailList.add(lDetail);

        loanCount = loanCount+1;
        lDetail = new UserDetails("User "+loanCount, "98401 98401",  true);
        loanDetailList.add(lDetail);

        loanCount = loanCount+1;
        lDetail = new UserDetails("User "+loanCount, "98401 98401",  false);
        loanDetailList.add(lDetail);

        mAdapter.notifyDataSetChanged();
    }


    private void addLoanPopup(String msg){

        addloanDialog =  new Dialog(HomeActivity.this);
        addloanDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addloanDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        addloanDialog.setContentView(R.layout.add_loan_layout);
//        addloanDialog.setCancelable(false);
        TextView today_date = addloanDialog.findViewById(R.id.today_date);
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        String todaydate = format.format(today);

        today_date.setText(todaydate);

        Button btn_alert_ok = addloanDialog.findViewById(R.id.btn_alert_ok);
        btn_alert_ok.setText("Submit");

        btn_alert_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addToUserDetailData();
                addloanDialog.dismiss();
            }
        });
        addloanDialog.show();
    }
}

