package com.hundreddaysloan.activity;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.hundreddaysloan.R;
import com.hundreddaysloan.adapter.PaymentHistoryAdapter;
import com.hundreddaysloan.model.LoanDetails;
import com.hundreddaysloan.model.PaymentHistory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PaymentDetailActivity extends AppCompatActivity {

    private List<PaymentHistory> loanDetailList = new ArrayList<>();
    private RecyclerView paydetail_recycler_view;
    private PaymentHistoryAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    Dialog payHisDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        paydetail_recycler_view  = (RecyclerView) findViewById(R.id.paydetail_recycler_view);

        mAdapter = new PaymentHistoryAdapter(loanDetailList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        paydetail_recycler_view.setLayoutManager(mLayoutManager);
        paydetail_recycler_view.setItemAnimator(new DefaultItemAnimator());
        paydetail_recycler_view.setAdapter(mAdapter);

        preparePaymentHistoryData();



        FloatingActionButton pay_fab = (FloatingActionButton) findViewById(R.id.pay_fab);
        pay_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Added loan detail", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                addToLoanDetailData();
                addPaymentPopup("");
            }
        });
    }

    private void addPaymentPopup(String msg){

        payHisDialog = new Dialog(PaymentDetailActivity.this);
        payHisDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        payHisDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        payHisDialog.setContentView(R.layout.add_loan_layout);
//        addloanDialog.setCancelable(false);
        TextView today_date = payHisDialog.findViewById(R.id.today_date);
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        String todaydate = format.format(today);

        today_date.setText(todaydate);

        Button btn_alert_ok = payHisDialog.findViewById(R.id.btn_alert_ok);
        btn_alert_ok.setText("Submit");

        btn_alert_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                payHisDialog.dismiss();
            }
        });
        payHisDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.e("dsfgds","dsgsdgsd");
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void preparePaymentHistoryData() {
//        LoanDetails(String loanid, int loan_amount, String loan_start_date, String loan_end_date, boolean loan_status)
        PaymentHistory lDetail = new PaymentHistory("HDL00001", "15,000", "02/07/2018","05/07/2018", true);
        loanDetailList.add(lDetail);

        lDetail = new PaymentHistory("HDL00002", "15,000", "05/08/2018","07/08/2018", true);
        loanDetailList.add(lDetail);
        lDetail = new PaymentHistory("HDL00003", "15,000", "07/09/2018","08/09/2018", true);
        loanDetailList.add(lDetail);
        lDetail = new PaymentHistory("HDL00004", "15,000", "08/10/2018","09/10/2018", true);
        loanDetailList.add(lDetail);
        lDetail = new PaymentHistory("HDL00005", "15,000", "09/11/2018","10/12/2018", true);
        loanDetailList.add(lDetail);
        lDetail = new PaymentHistory("HDL00006", "15,000", "10/12/2018","11/12/2018", true);
        loanDetailList.add(lDetail);
        lDetail = new PaymentHistory("HDL00007", "15,000", "11/13/2018","12/14/2018", true);
        loanDetailList.add(lDetail);
        lDetail = new PaymentHistory("HDL00008", "15,000", "12/15/2018","01/16/2019", true);
        loanDetailList.add(lDetail);
        lDetail = new PaymentHistory("HDL00009", "15,000", "01/17/2019","04/16/2019", false);
        loanDetailList.add(lDetail);

        mAdapter.notifyDataSetChanged();
    }
}

