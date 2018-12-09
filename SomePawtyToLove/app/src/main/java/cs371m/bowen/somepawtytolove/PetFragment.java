package cs371m.bowen.somepawtytolove;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static cs371m.bowen.somepawtytolove.MainActivity.setTxtOr;

public class PetFragment extends Fragment {
    private Bundle bundle;
    // If create is false, log in screen and log in action, otherwise create account screen and action
    static PetFragment newInstance() {
        PetFragment imageFragment = new PetFragment();
        Bundle b = new Bundle();
        //b.putParcelable("bitmap", bitmap);
        imageFragment.setArguments(b);
        return imageFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.pet_fragment, container, false);
        bundle = this.getArguments();
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View v = getView();
        // XXX Write me.  I display a bitmap and if clicked, I disappear
        TextView name = getView().findViewById(R.id.nameTxt);
        setTxtOr(name, bundle.getString("name"), "No Name");
        TextView type = getView().findViewById(R.id.speciesTxt);
        setTxtOr(type, bundle.getString("breed"), "Breed Unknown");
        TextView location = getView().findViewById(R.id.locationTxt);
        setTxtOr(location, bundle.getString("loc"), "Location Unknown");
        TextView age = getView().findViewById(R.id.ageTxt);
        setTxtOr(age, bundle.getString("age"), "Age Unknown");
        TextView bio = getView().findViewById(R.id.descriptionTxt);
        bio.setMovementMethod(new ScrollingMovementMethod());
        setTxtOr(bio, bundle.getString("description"), "");
        final String emailAddr = bundle.getString("email");

        Button email = getView().findViewById(R.id.button);
        email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (emailAddr != null){
                            Intent emailIntent = new Intent(Intent.ACTION_SEND);
                            emailIntent.setType("message/rfc822");
                            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddr});
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Pet Adoption");
                            getActivity().startActivity(Intent.createChooser(emailIntent, "Choose email client"));
                        } else {
                            Toast.makeText(getActivity(), "Email unavailable", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        //Net.getInstance().glideFetch(savedInstanceState.getString("petURL"), getView().findViewById(R.id.profileImage));

    }
}
