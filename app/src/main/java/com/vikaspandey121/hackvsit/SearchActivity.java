package com.vikaspandey121.hackvsit;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.crime.missingcrime.Model.ReportModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.Picasso;
import com.vikaspandey121.hackvsit.Model.ReportModel;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private EditText search;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private Query mRef;
    ArrayList<String> key;
    ArrayList<String> range;
    ArrayList<String> location;
    ArrayList<String> logoHotel;
    ArrayList<String> title;
    SearchAdapter searchAdapter;
    MaterialSearchView searchView;
    private static Context context;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private FirebaseRecyclerAdapter<ReportModel,SearchActivity.ReportViewHolder> mFirebaseAdapter;
    private RecyclerView JobList;
    String hotelsearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = getApplicationContext();
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);   //disable back button
        getSupportActionBar().setHomeButtonEnabled(false);
        searchView=(MaterialSearchView)findViewById(R.id.search_view);
        edittext();
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        hotelsearch = extras.getString("hotelsearch");
        JobList = (RecyclerView) findViewById(R.id.hotellist);
        JobList.setHasFixedSize(true);
        JobList.setLayoutManager(new LinearLayoutManager(this));
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference().child("reports").orderByChild("status").equalTo("1");

    }
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main_menu, menu);
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;

    }
    public boolean isServicesOK(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(SearchActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Toast.makeText(this, "isServicesOK: Google Play Services is working", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Toast.makeText(this, "isServicesOK: an error occured but we can fix it", Toast.LENGTH_SHORT).show();
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(SearchActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_home:
                Intent intentMapView = new Intent(this, MainActivity.class);
                startActivity(intentMapView);
                break;
            case R.id.menu_add_crime:
                Intent intentAddCrime = new Intent(this, AddReportActivity.class);
                startActivity(intentAddCrime);
                break;

            case R.id.menu_logout:
                if(isServicesOK()){
                    signOut();
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    void signOut()
    {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(SearchActivity.this, loginActivity.class);
        startActivity(intent);
        finish();
        return;
    }
    private void edittext() {

        location=new ArrayList<>();
        title=new ArrayList<>();
        key=new ArrayList<>();
        range=new ArrayList<>();
        logoHotel=new ArrayList<>();
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!query.toString().isEmpty())
                {
                    setAdapter(hotelsearch);
                }
                else
                {
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!newText.toString().isEmpty())
                {
                    setAdapter(hotelsearch);
                }
                else
                {
                }
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                searchView.clearFocus();            }
        });
    }

    private void loginReflect() {
        Intent intent = new Intent(this, loginActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAdapter=new FirebaseRecyclerAdapter<ReportModel,SearchActivity.ReportViewHolder>(
                ReportModel.class,
                R.layout.report_items,
                SearchActivity.ReportViewHolder.class,
                mRef

        ){

            @Override
            protected void populateViewHolder(SearchActivity.ReportViewHolder viewHolder, ReportModel model, int position) {
                final String post_key=getRef(position).getKey();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setLocation(model.getLocation());
                viewHolder.setType(model.getType());
                viewHolder.setPostTime(model.getPostTime());
                viewHolder.setImage(model.getImage());
//                viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View view) {
//                        mRef.child(post_key).removeValue();
//                        return false;
//                    }
//                });
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle b=new Bundle();
                        b.putString("id", post_key);
                        Intent intentUserList = new Intent(SearchActivity.this, ReportViewActivity.class);
                        intentUserList.putExtras(b);
                        startActivity(intentUserList);
                    }
                });
            }
        };
        JobList.setAdapter(mFirebaseAdapter);

    }
    public static class ReportViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public ReportViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setTitle(String text)
        {
            TextView hotelTitle=(TextView)mView.findViewById(R.id.textViewJobTitle);
            hotelTitle.setText(text);
        }
        public void setImage(String text)
        {
            ImageView image=(ImageView)mView.findViewById(R.id.imageViewRow);
            Picasso.with(context)
                    .load(text)
                    .resize(120,120)
                    .centerCrop()
                    .into(image);
        }
        public void setLocation(String edu)
        {
            TextView hotelPrice=(TextView)mView.findViewById(R.id.textViewLocation);
            hotelPrice.setText(edu);

        }
        public void setType(String exp)
        {
            TextView hotelEmail=(TextView)mView.findViewById(R.id.textViewType);
            hotelEmail.setText(exp);

        }
        public void setPostTime(String time)
        {
            TextView postTime=(TextView)mView.findViewById(R.id.textViewPostTime);
            postTime.setText(time);
        }


    }
    private void setAdapter(final String searchedString) {
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*
                 * Clear the list for every new search
                 * */
                location.clear();
                title.clear();
                key.clear();
                range.clear();
                key.clear();
                logoHotel.clear();
                JobList.removeAllViews();

                int counter = 0;

                /*
                 * Search all users for matching searched string
                 * */
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uid = snapshot.getKey();
                    String titleX = snapshot.child("title").getValue(String.class);
                    String locationX = snapshot.child("location").getValue(String.class);
                    String rangeX = snapshot.child("type").getValue(String.class);
                    String keyX = snapshot.child("id").getValue(String.class);
                    String logoX = snapshot.child("image").getValue(String.class);

                    if (titleX.toLowerCase().contains(searchedString.toLowerCase())) {
                        title.add(titleX);
                        location.add(locationX);
                        range.add(rangeX);
                        key.add(keyX);
                        logoHotel.add(logoX);
                        counter++;
                    } else if (locationX.toLowerCase().contains(searchedString.toLowerCase())) {
                        title.add(titleX);
                        location.add(locationX);
                        range.add(rangeX);
                        logoHotel.add(logoX);
                        key.add(keyX);
                        counter++;
                    }

                    /*
                     * Get maximum of 15 searched results only
                     * */
                    if (counter == 15)
                        break;
                }

                searchAdapter = new SearchAdapter(SearchActivity.this, title, location,range,key,logoHotel);
                JobList.setAdapter(searchAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}
