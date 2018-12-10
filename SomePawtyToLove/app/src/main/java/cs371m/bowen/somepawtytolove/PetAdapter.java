package cs371m.bowen.somepawtytolove;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.support.v7.widget.RecyclerView.NO_POSITION;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.DynamicViewHolder> {
    private Context context;
    private PetSwipeDetector detector;

    public class DynamicViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView name;
        TextView breed;
        TextView location;
        String email;

        public DynamicViewHolder(View theView) {
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
                        context.startActivity(Intent.createChooser(emailIntent, "Choose email client"));
                    } else {
                        Toast.makeText(context, "Email unavailable", Toast.LENGTH_SHORT).show();
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

    public PetAdapter(Context context, RecyclerView rv){
        this.context = context;
        detector = new PetSwipeDetector();
        rv.addOnItemTouchListener(detector);
    }

    public void removeItem(int position){
        MainActivity.savedPets.remove(position);
        notifyDataSetChanged();
        Log.i("Swipe", "Removed");
    }

    @NonNull
    @Override
    public DynamicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_pet, parent, false);
        return new DynamicViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DynamicViewHolder holder, int position) {
        Pet pet = MainActivity.savedPets.get(position);
        holder.location.setText(pet.getLocation());
        holder.breed.setText(pet.getBreed());
        holder.name.setText(pet.getName());
        holder.email = pet.getEmail();
        Net.getInstance().glideFetch(pet.getPic(), holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return MainActivity.savedPets.size();
    }
}
