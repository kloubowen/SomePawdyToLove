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
        }
    }

    public PetAdapter(Context context, List<Pet> pets){
        this.context = context;
        this.pets = (ArrayList<Pet>) pets;
    }

    public void addPet(Pet newPet){
        pets.add(newPet);
        notifyItemChanged(getItemCount() - 1);
        // may need to change to getItemCount() - 2
    }

    @NonNull
    @Override
    public DynamicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_pet, parent, false);
        return new DynamicViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DynamicViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return pets.size();
    }
}
