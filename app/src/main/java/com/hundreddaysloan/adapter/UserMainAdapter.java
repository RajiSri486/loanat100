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
import com.hundreddaysloan.activity.LoanDetailsActivity;
import com.hundreddaysloan.model.LoanDetails;
import com.hundreddaysloan.model.UserDetails;

import java.util.List;

public class UserMainAdapter extends RecyclerView.Adapter<UserMainAdapter.MyViewHolder> {

    private List<UserDetails> userDetailList;
    private Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        public TextView usr_name, mobile_no, user_status;
        RelativeLayout card_layout;
        IMyViewHolderClicks mListener;

        public MyViewHolder(View view, IMyViewHolderClicks listener) {
            super(view);
            mListener = listener;
            usr_name = (TextView) view.findViewById(R.id.usr_name);
            mobile_no = (TextView) view.findViewById(R.id.loan_amount);
            user_status = (TextView) view.findViewById(R.id.loan_status);
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


    public UserMainAdapter(List<UserDetails> loanDetailList, Context context) {
        this.context = context;
        this.userDetailList = loanDetailList;
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
    public UserMainAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);

        UserMainAdapter.MyViewHolder vh = new MyViewHolder(v, new UserMainAdapter.MyViewHolder.IMyViewHolderClicks() {
            public void viewLoanDetail(View lDetail) {
                Log.e("invokePaymentHistory", "11");
                invokeLoanDetailsActivity();
                Log.e("invokePaymentHistory", "11");
            };
            public void loadOnLoanDetail(RelativeLayout loanDetail) {
                Log.e("invokePaymentHistory", "22");
                invokeLoanDetailsActivity();
                Log.e("invokePaymentHistory", "22");
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        UserDetails loanDetail = userDetailList.get(position);
        holder.usr_name.setText(loanDetail.getUserName());
        holder.mobile_no.setText(loanDetail.getMobileNo());
        if(loanDetail.getUserStatus()){
            holder.user_status.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            holder.user_status.setText("Inactive");
        }else{
            holder.user_status.setTextColor(ContextCompat.getColor(context, R.color.red_800));
            holder.user_status.setText("Active");
        }
    }

    @Override
    public int getItemCount() {
        return userDetailList.size();
    }

    private void invokeLoanDetailsActivity(){
        Log.d("invokePaymentHistory", "Success");
        Intent intent= new Intent(context, LoanDetailsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}