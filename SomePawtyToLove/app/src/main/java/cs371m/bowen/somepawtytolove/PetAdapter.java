package cs371m.bowen.somepawtytolove;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.DynamicViewHolder> {
    private ArrayList<Pet> pets;
    private Context context;

    public class DynamicViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView name;
        TextView breed;
        TextView location;

        public DynamicViewHolder(View theView) {
            super(theView);
            thumbnail = theView.findViewById(R.id.thumbnail);
            name = theView.findViewById(R.id.name);
            breed = theView.findViewById(R.id.breed);
            location = theView.findViewById(R.id.location);
            // possibly set an on click listener to pull up more info on pet
            //find a way to set email to link to email
        }
    }

    public PetAdapter(Context context, List<Pet> pets){
        this.context = context;
        this.pets = (ArrayList<Pet>) pets;
    }

    @NonNull
    @Override
    public DynamicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_pet, parent, false);
        return new DynamicViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DynamicViewHolder holder, int position) {
        Pet pet = pets.get(position);
        holder.location.setText(pet.getLocation());
        holder.breed.setText(pet.getBreed());
        holder.name.setText(pet.getName());
        //use glide to set image
    }

    @Override
    public int getItemCount() {
        return pets.size();
    }
}
