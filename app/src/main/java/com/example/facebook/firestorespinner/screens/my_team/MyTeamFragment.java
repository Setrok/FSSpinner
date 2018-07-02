package com.example.facebook.firestorespinner.screens.my_team;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.facebook.firestorespinner.FireStore.EarningRecord;
import com.example.facebook.firestorespinner.FireStore.User;
import com.example.facebook.firestorespinner.FireStore.Users;
import com.example.facebook.firestorespinner.R;
import com.example.facebook.firestorespinner.WalletPager.EarningsFragment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyTeamFragment extends Fragment{

    View mainView;

    RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private FirestoreRecyclerAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_my_team, container, false);

        initRecyclerView();

        return mainView;
    }

    private void initRecyclerView() {

        recyclerView = mainView.findViewById(R.id.my_team_recyclerView);

        mAuth = FirebaseAuth.getInstance();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //Users.getUserCounter(mAuth.getCurrentUser().getUid(),this);
        getEarningsList(mAuth.getCurrentUser().getUid());

    }

    private void getEarningsList(String uid) {

//        Log.i("InfoApp",counter+"");
        Query query = FirebaseFirestore.getInstance()
                .collection("users")
                .whereEqualTo("refFrom",uid)
                .limit(50);

        FirestoreRecyclerOptions<User> response = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<User, UserHolder>(response) {
            @Override
            public void onBindViewHolder(UserHolder holder, int position, User model) {
                //progressBar.setVisibility(View.GONE);

                String counter = model.getCounter()+"";

                holder.tvUserName.setText(model.getName());
                holder.tvRefCode.setText(counter);

            }

            @Override
            public UserHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.my_team_list_item, group, false);

                return new UserHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
//        adapter.startListening();
    }

    public class UserHolder extends RecyclerView.ViewHolder {

        TextView tvUserName;
        TextView tvRefCode;

        public UserHolder(View itemView) {
            super(itemView);
            //ButterKnife.bind(this, itemView);
            tvUserName = itemView.findViewById(R.id.my_team_nameFld);
            tvRefCode = itemView.findViewById(R.id.my_team_referalFld);
        }



    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            adapter.startListening();
        } catch (NullPointerException e){

        }
        }

    @Override
    public void onStop() {
        super.onStop();
        try {
            adapter.stopListening();
        } catch (NullPointerException e){

        }
    }

}
