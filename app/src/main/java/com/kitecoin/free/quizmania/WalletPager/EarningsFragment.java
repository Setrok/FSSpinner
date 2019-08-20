package com.kitecoin.free.quizmania.WalletPager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kitecoin.free.quizmania.FireStore.EarningRecord;
import com.kitecoin.free.quizmania.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class EarningsFragment extends Fragment {

    View mainView;

    RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private FirestoreRecyclerAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView  = inflater.inflate(R.layout.fragment_earnings, container, false);

        initRecyclerView();

        return mainView;
    }

    private void initRecyclerView() {

        recyclerView = mainView.findViewById(R.id.earnings_recyclerView);

        mAuth = FirebaseAuth.getInstance();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        getEarningsList();



    }

    private void getEarningsList() {

        Query query = FirebaseFirestore.getInstance()
                .collection("earnings")
                .document(mAuth.getCurrentUser().getUid())
                .collection("earningRecords")
                .orderBy("timestamp");
//                .limit(50);

        FirestoreRecyclerOptions<EarningRecord> response = new FirestoreRecyclerOptions.Builder<EarningRecord>()
                .setQuery(query, EarningRecord.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<EarningRecord, EarningsHolder>(response) {
            @Override
            public void onBindViewHolder(EarningsHolder holder, int position, EarningRecord model) {
                //progressBar.setVisibility(View.GONE);

                String amount  = model.getAmount() + "";

                SimpleDateFormat formatter = new SimpleDateFormat("MM'/'dd'/'y hh:mm", Locale.US);
                String dateStr  = formatter.format(model.getTimestamp());

                holder.tvSourceName.setText(model.getSourceName());
                holder.tvAmount.setText(amount);
                holder.tvDate.setText(dateStr);

            }

            @Override
            public EarningsHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.earnings_list_item, group, false);

                return new EarningsHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    public class EarningsHolder extends RecyclerView.ViewHolder {

        TextView tvSourceName;
        TextView tvAmount;
        TextView tvDate;

        public EarningsHolder(View itemView) {
            super(itemView);
            //ButterKnife.bind(this, itemView);
            tvSourceName = itemView.findViewById(R.id.earnings_item_sourceName);
            tvAmount = itemView.findViewById(R.id.earnings_item_amount);
            tvDate = itemView.findViewById(R.id.earnings_item_date);
        }



    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


}
