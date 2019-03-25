package com.hundreddaysloan.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hundreddaysloan.R;
import com.hundreddaysloan.activity.PaymentDetailActivity;
import com.hundreddaysloan.model.LoanDetails;

import java.util.List;

public class LoanDetailsAdapter extends RecyclerView.Adapter<LoanDetailsAdapter.MyViewHolder> {

    private List<LoanDetails> loanDetailList;
    private Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        public TextView usr_name, loan_amount, loan_strtdate, loan_enddate, loan_status;
        RelativeLayout card_layout;
        IMyViewHolderClicks mListener;

        public MyViewHolder(View view, IMyViewHolderClicks listener) {
            super(view);
            mListener = listener;
            usr_name = (TextView) view.findViewById(R.id.usr_name);
            loan_amount = (TextView) view.findViewById(R.id.loan_amount);
            loan_strtdate = (TextView) view.findViewById(R.id.loan_strtdate);
            loan_enddate = (TextView) view.findViewById(R.id.loan_enddate);
            loan_status = (TextView) view.findViewById(R.id.loan_status);
            card_layout = (RelativeLayout) view.findViewById(R.id.card_layout);
            card_layout.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            if (v instanceof RelativeLayout){
                mListener.loadOnLoanDetail((RelativeLayout)v);
            } else {
                mListener.viewLoanDetail(v);
            }
        }

        public static interface IMyViewHolderClicks {
            public void viewLoanDetail(View lDetail);
            public void loadOnLoanDetail(RelativeLayout loanDetail);
        }
    }


    public LoanDetailsAdapter(List<LoanDetails> loanDetailList, Context context) {
        this.context = context;
        this.loanDetailList = loanDetailList;
    }

//    @Override
//    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.user_list_item, parent, false);
//
//
//        return new MyViewHolder(itemView);
//    }

    @Override
    public LoanDetailsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.loan_list_item, parent, false);

        LoanDetailsAdapter.MyViewHolder vh = new MyViewHolder(v, new LoanDetailsAdapter.MyViewHolder.IMyViewHolderClicks() {
            public void viewLoanDetail(View lDetail) {
                Log.e("invokePaymentHistory", "11");
                invokePaymentHistory();
                Log.e("invokePaymentHistory", "11");
            };
            public void loadOnLoanDetail(RelativeLayout loanDetail) {
                Log.e("invokePaymentHistory", "22");
                invokePaymentHistory();
                Log.e("invokePaymentHistory", "22");
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        LoanDetails loanDetail = loanDetailList.get(position);
        holder.usr_name.setText(loanDetail.getLoanId());
        holder.loan_amount.setText(loanDetail.getLoanAmount());
        holder.loan_strtdate.setText(loanDetail.getLoanStartDate());
        holder.loan_enddate.setText(loanDetail.getLoanEndDate());
        if(loanDetail.getLoanStatus()){
            holder.loan_status.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            holder.loan_status.setText("Closed");
        }else{
            holder.loan_status.setTextColor(ContextCompat.getColor(context, R.color.red_800));
            holder.loan_status.setText("Active");
        }
    }

    @Override
    public int getItemCount() {
        return loanDetailList.size();
    }

    private void invokePaymentHistory(){
        Log.d("invokePaymentHistory", "Success");
        Intent intent= new Intent(context, PaymentDetailActivity.class);
        context.startActivity(intent);
    }
}