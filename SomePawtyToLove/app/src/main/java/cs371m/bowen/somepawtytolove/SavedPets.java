package cs371m.bowen.somepawtytolove;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import java.util.ArrayList;

public class SavedPets extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_pets);
        recyclerView = (RecyclerView) findViewById(R.id.savedPets);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        //Uncomment to test recycler view
//        ArrayList<Pet> temp = new ArrayList<>();
//        temp.add(new Pet("apple", "fruit","tree", "1"));
//        temp.add(new Pet("grape", "adfst","tree", "3"));
//        temp.add(new Pet("orang", "fsdfs","tree", "2"));
//        temp.add(new Pet("peach", "agdga","tree", "4"));
//        PetAdapter petAdapter = new PetAdapter(this, temp);
        PetAdapter petAdapter = new PetAdapter(this, MainActivity.savedPets);
        recyclerView.setAdapter(petAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    // figure out how to update adapter if this activity hasn't been opened yet.
    // maybe have calling activity pass in a list of new pets
    // maybe have a static saved pet list in main activity
}
