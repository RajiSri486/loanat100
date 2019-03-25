package com.hundreddaysloan.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hundreddaysloan.R;
import com.hundreddaysloan.model.PaymentHistory;

import java.util.List;

public class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.MyViewHolder> {

    private List<PaymentHistory> paymentHistoryList;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView recvd_amnt, recvd_date;

        private MyViewHolder(View view) {
            super(view);
            recvd_amnt = (TextView) view.findViewById(R.id.recvd_amnt);
            recvd_date = (TextView) view.findViewById(R.id.recvd_date);
        }
    }


    public PaymentHistoryAdapter(List<PaymentHistory> paymentHistoryList) {
        this.paymentHistoryList = paymentHistoryList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.payment_history, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PaymentHistory loanDetail = paymentHistoryList.get(position);
        String pay_recvd_amnt = "RECEIVED AMOUNT <b>Rs."+loanDetail.getLoanAmount()+"</b>";
        holder.recvd_amnt.setText(Html.fromHtml(pay_recvd_amnt));
        holder.recvd_date.setText(loanDetail.getEMIPayDate());
    }

    @Override
    public int getItemCount() {
        return paymentHistoryList.size();
    }
}