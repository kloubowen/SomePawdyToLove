package cs371m.bowen.somepawtytolove;

import android.content.Context;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestoreException;

import static android.support.v7.widget.RecyclerView.NO_POSITION;

public class PetFirebaseAdapter extends FirestoreRecyclerAdapter<Pet,
        PetFirebaseAdapter.DynamicViewHolder> {
    private PetSwipeDetector detector;

    public class DynamicViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView name;
        TextView breed;
        TextView location;
        String email;

        public DynamicViewHolder(final View theView) {
            super(theView);
            thumbnail = theView.findViewById(R.id.thumbnail);
            name = theView.findViewById(R.id.name);
            breed = theView.findViewById(R.id.breed);
            location = theView.findViewById(R.id.location);
            // possibly set an on click listener to pull up more info on pet
            TextView emailText = theView.findViewById(R.id.email);
            emailText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (email != null){
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setType("message/rfc822");
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Pet Adoption");
                        theView.getContext().startActivity(Intent.createChooser(emailIntent, "Choose email client"));
                    } else {
                        Toast.makeText(theView.getContext(), "Email unavailable", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            theView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Add check for if swipe was detected
                    int position = getAdapterPosition();
                    if (position == NO_POSITION){
                        return;
                    }

                    if (detector.swipeDetected()){
                        Log.i("Swipe", "detected");
                        removeItem(position);
                    }
                }
            });
        }
    }
    PetFirebaseAdapter(@NonNull FirestoreRecyclerOptions<Pet> options) {
        super(options);
        detector = new PetSwipeDetector();
    }
    @Override
    public void onBindViewHolder(DynamicViewHolder holder, int position, Pet pet) {
        holder.location.setText(pet.getLocation());
        holder.breed.setText(pet.getBreed());
        holder.name.setText(pet.getName());
        holder.email = pet.getEmail();
        Net.getInstance().glideFetch(pet.getPic(), holder.thumbnail);
    }

    @Override
    public DynamicViewHolder onCreateViewHolder(ViewGroup group, int i) {
        // Create a new instance of the ViewHolder, in this case we are using a custom
        // layout called R.layout.message for each item
        View v = LayoutInflater.from(group.getContext()).inflate(R.layout.single_pet, group, false);
        return new DynamicViewHolder(v);
    }

    @Override
    public void onError(FirebaseFirestoreException e) {
        // Called when there is an error getting a query snapshot. You may want to update
        // your UI to display an error message to the user.
        Log.d("fb", "Error " + e.getMessage());
    }


    public void removeItem(int position){
        MainActivity.savedPets.remove(position);
        notifyDataSetChanged();
        Log.i("Swipe", "Removed");
    }
};
