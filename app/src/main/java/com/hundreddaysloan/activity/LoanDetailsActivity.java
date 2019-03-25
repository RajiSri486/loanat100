package com.hundreddaysloan.activity;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.hundreddaysloan.R;
import com.hundreddaysloan.adapter.LoanDetailsAdapter;
import com.hundreddaysloan.model.LoanDetails;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class LoanDetailsActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    Dialog addloanDialog;

    private List<LoanDetails> loanDetailList = new ArrayList<>();
    private RecyclerView loan_recycler_view;
    private LoanDetailsAdapter mAdapter;

    LoanDetails lDetail;
    int loanCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        firebaseAuth = FirebaseAuth.getInstance();

        loan_recycler_view  = (RecyclerView) findViewById(R.id.loan_recycler_view);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLoanPopup("");
            }
        });

        mAdapter = new LoanDetailsAdapter(loanDetailList, getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        loan_recycler_view.setLayoutManager(mLayoutManager);
        loan_recycler_view.setItemAnimator(new DefaultItemAnimator());
        loan_recycler_view.setAdapter(mAdapter);


        prepareLoanDetailData();
        sortLoanDetails();
    }


    private void sortLoanDetails(){

        Collections.sort(loanDetailList, new Comparator<LoanDetails>(){
            public int compare(LoanDetails obj1, LoanDetails obj2) {
                // ## Ascending order
//                return obj1.getLoanAmount().compareToIgnoreCase(obj2.getLoanAmount());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    return Boolean.compare(obj1.getLoanStatus(), obj2.getLoanStatus());
                }else {
                    return Boolean.valueOf(obj1.getLoanStatus()).compareTo(Boolean.valueOf(obj2.getLoanStatus()));
                }
                // To compare string values
                // return Integer.valueOf(obj1.empId).compareTo(Integer.valueOf(obj2.empId)); // To compare integer values

                // ## Descending order
                // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
            }
        });
    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }

    private void addToLoanDetailData(){
        loanCount = loanCount + 1;
        lDetail = new LoanDetails("HDL0000" + loanCount, "15,000", "01/17/2019", "04/16/2019", false);
        loanDetailList.add(lDetail);

        sortLoanDetails();

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

    private void prepareLoanDetailData() {
//        LoanDetails(String loanid, int loan_amount, String loan_start_date, String loan_end_date, boolean loan_status)
        lDetail = new LoanDetails("HDL0000"+loanCount, "15,000", "02/07/2018","05/07/2018", true);
        loanDetailList.add(lDetail);

        loanCount = loanCount+1;
        lDetail = new LoanDetails("HDL0000"+loanCount, "15,000", "05/08/2018","07/08/2018", true);
        loanDetailList.add(lDetail);

        loanCount = loanCount+1;
        lDetail = new LoanDetails("HDL0000"+loanCount, "15,000", "07/09/2018","08/09/2018", true);
        loanDetailList.add(lDetail);

        loanCount = loanCount+1;
        lDetail = new LoanDetails("HDL0000"+loanCount, "15,000", "08/10/2018","09/10/2018", true);
        loanDetailList.add(lDetail);

        loanCount = loanCount+1;
        lDetail = new LoanDetails("HDL0000"+loanCount, "15,000", "09/11/2018","10/12/2018", true);
        loanDetailList.add(lDetail);

        loanCount = loanCount+1;
        lDetail = new LoanDetails("HDL0000"+loanCount, "15,000", "10/12/2018","11/12/2018", true);
        loanDetailList.add(lDetail);

        loanCount = loanCount+1;
        lDetail = new LoanDetails("HDL0000"+loanCount, "15,000", "11/13/2018","12/14/2018", true);
        loanDetailList.add(lDetail);

        loanCount = loanCount+1;
        lDetail = new LoanDetails("HDL0000"+loanCount, "15,000", "12/15/2018","01/16/2019", true);
        loanDetailList.add(lDetail);

        loanCount = loanCount+1;
        lDetail = new LoanDetails("HDL0000"+loanCount, "15,000", "01/17/2019","04/16/2019", false);
        loanDetailList.add(lDetail);

        mAdapter.notifyDataSetChanged();
    }


    private void addLoanPopup(String msg){

        addloanDialog =  new Dialog(LoanDetailsActivity.this);
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
                addToLoanDetailData();
                addloanDialog.dismiss();
            }
        });
        addloanDialog.show();
    }
}


