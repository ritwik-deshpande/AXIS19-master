package com.developer.app.axis19;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Competition extends Fragment {


    View v;
    private RecyclerView recyclerView;

    List<Event> lst = new ArrayList<>() ;

    DatabaseReference rootRef,imagesRef;
    ValueEventListener valueEventListener;


    public Competition(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v= inflater.inflate(R.layout.competitions_fragment,container,false);

       // runAnimation(recyclerView,0);
        recyclerView = (RecyclerView)v.findViewById(R.id.competition_recyclerview);

        //RecyclerViewAdapter recyclerViewAdapter=new RecyclerViewAdapter(getContext(),lst);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        //recyclerView.setAdapter(recyclerViewAdapter);
        ConnectivityManager conMan = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        //mobile
        NetworkInfo.State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();

        //wifi
        NetworkInfo.State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

        if (mobile == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTED) {

        } else  {
            try {
                Snackbar.make(getActivity().findViewById(R.id.nav_view),"No Internet Connection",Snackbar.LENGTH_LONG).show();
            }catch (Exception e)
            {           }

            Toast.makeText(getActivity(),"Unable to fetch latest data",Toast.LENGTH_SHORT).show();

        }
        Competition.FetchEventList fel = new Competition.FetchEventList();
        fel.execute();

        return v;

    }

    private void runAnimation(RecyclerView recyclerView, int type) {

        Context context = recyclerView.getContext();
        LayoutAnimationController controller=null;

        // 0 denotes fall_down animation
        if(type==0){
            controller= AnimationUtils.loadLayoutAnimation(context,R.anim.item_falldown_animation);

        }

        recyclerView.setLayoutAnimation(controller);
        //recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();

    }

    public void updateUI()
    {
        RecyclerViewAdapter recyclerViewAdapter=new RecyclerViewAdapter(getContext(),lst);
        //recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    public class FetchEventList extends AsyncTask<Void,Void,ArrayList<Event>> {

        @Override
        protected void onPreExecute() {
            //bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Event> doInBackground(Void... params) {

            lst.clear();
            rootRef = FirebaseDatabase.getInstance().getReference();
            imagesRef = rootRef.child("Events").child("Competitions");
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Toast.makeText(getContext(),"retrieving data",Toast.LENGTH_SHORT).show();
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {

                        lst.add((ds.getValue(Event.class)));
                        Log.d("TAG","firebase created event object");
                    }
                    //Log.d("Size of list is ","size=" + ((Integer) lst.size()).toString());

                    //runAnimation(recyclerView,0);
                    updateUI();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            imagesRef.addListenerForSingleValueEvent(valueEventListener);
            return null;
        }
    }
}
