package cs371m.bowen.somepawtytolove;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
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

public class PetFirebaseAdapter extends FirestoreRecyclerAdapter<Pet,
        PetFirebaseAdapter.DynamicViewHolder> {
    private Context context;

    public class DynamicViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView name;
        TextView breed;
        TextView location;
        String email;
        View view;

        public DynamicViewHolder(final View theView, final Context mContext) {
            super(theView);
            view = theView;
            thumbnail = theView.findViewById(R.id.thumbnail);
            name = theView.findViewById(R.id.name);
            breed = theView.findViewById(R.id.breed);
            location = theView.findViewById(R.id.location);
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
        }
    }
    PetFirebaseAdapter(@NonNull FirestoreRecyclerOptions<Pet> options, Context context) {
        super(options);
        this.context = context;
    }
    @Override
    public void onBindViewHolder(DynamicViewHolder holder, int position, final Pet pet) {
        holder.location.setText(pet.getLocation());
        holder.breed.setText(pet.getBreed());
        holder.name.setText(pet.getName());
        holder.email = pet.getEmail();
        final String imageUrl = pet.getPic();
        Net.getInstance().glideFetch(imageUrl, holder.thumbnail);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("save","clicked on thumbnail");
                FragmentManager fm = ((FragmentActivity)context).getFragmentManager();
                Bundle b = new Bundle();

                b.putString("name", pet.getName());
                b.putString("email", pet.getEmail());
                b.putString("picURL", imageUrl);
                b.putString("description", pet.getDescription());
                b.putString("breed", pet.getBreed());
                b.putString("loc", pet.getLocation());
                b.putString("age", pet.getAge());

                PetFragment myFragment = new PetFragment();
                myFragment.setArguments(b);

                fm.beginTransaction().replace(R.id.frame, myFragment).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public DynamicViewHolder onCreateViewHolder(ViewGroup group, int i) {
        View v = LayoutInflater.from(group.getContext()).inflate(R.layout.single_pet, group, false);
        return new DynamicViewHolder(v, context);
    }

    @Override
    public void onError(FirebaseFirestoreException e) {
        Log.d("fb", "Error " + e.getMessage());
    }


    public void removeItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }
};
