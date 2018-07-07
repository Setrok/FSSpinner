package com.example.facebook.firestorespinner.WalletPager;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.facebook.firestorespinner.FireStore.WithdrawRecord;
import com.example.facebook.firestorespinner.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class WithdrawFragment extends Fragment {

    View mainView;
    RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private FirestoreRecyclerAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView =  inflater.inflate(R.layout.fragment_withdraw, container, false);

        initRecyclerView();

        return mainView;
    }

    private void initRecyclerView() {

        recyclerView = mainView.findViewById(R.id.withdraw_recyclerView);

        mAuth = FirebaseAuth.getInstance();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        getWithdrawList();



    }

    private void getWithdrawList() {

        Query query = FirebaseFirestore.getInstance()
                .collection("withdraw")
                .document(mAuth.getCurrentUser().getUid())
                .collection("withdrawRecords")
                .orderBy("timestamp")
                .limit(50);

        FirestoreRecyclerOptions<WithdrawRecord> response = new FirestoreRecyclerOptions.Builder<WithdrawRecord>()
                .setQuery(query, WithdrawRecord.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<WithdrawRecord, WithdrawHolder>(response) {
            @Override
            public void onBindViewHolder(WithdrawHolder holder, int position, WithdrawRecord model) {
                //progressBar.setVisibility(View.GONE);

                String amount  = model.getAmount() + "";
                String paytmNumber  = model.getPaytmNumber() + "";

                SimpleDateFormat formatter = new SimpleDateFormat("MM'/'dd'/'y hh:mm", Locale.US);
                String dateStr  = formatter.format(model.getTimestamp());


                holder.tvPaytmNumber.setText(paytmNumber);
                if(!amount.equals("0"))
                    holder.tvAmount.setText(amount);
                else
                    holder.tvAmount.setText("Done");
                holder.tvDate.setText(dateStr);

            }

            @Override
            public WithdrawHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.withdraw_list_item, group, false);

                return new WithdrawHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    public class WithdrawHolder extends RecyclerView.ViewHolder {

        TextView tvPaytmNumber;
        TextView tvAmount;
        TextView tvDate;

        public WithdrawHolder(View itemView) {
            super(itemView);
            //ButterKnife.bind(this, itemView);
            tvPaytmNumber = itemView.findViewById(R.id.withdraw_item_paytmNumber);
            tvAmount = itemView.findViewById(R.id.withdraw_item_amount);
            tvDate = itemView.findViewById(R.id.withdraw_item_date);
        }



    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            adapter.startListening();
        } catch (NullPointerException e){
            Log.i("InfoApp",e.getMessage()+"");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
