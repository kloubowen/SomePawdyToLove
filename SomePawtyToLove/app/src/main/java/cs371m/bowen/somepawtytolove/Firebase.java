package cs371m.bowen.somepawtytolove;

import android.util.Log;


import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.HashSet;

public class Firebase {
    protected FirebaseFirestore db;
    protected FirebaseUser user;

    private static class Holder {
        public static Firebase helper = new Firebase();
    }
    // Every time you need the Net object, you must get it via this helper function
    public static Firebase getInstance() {
        return Holder.helper;
    }
    // Call init before using instance
    public static synchronized void init() {
        //todo: figure out why it fails at this line
        Holder.helper.db = FirebaseFirestore.getInstance();
        if( Holder.helper.db == null ) {
            Log.d("firestore", "XXX FirebaseFirestore is null!");
        }
        Holder.helper.user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        Holder.helper.db.setFirestoreSettings(settings);
    }

    public void savePet(Pet pet) {
        db.collection("users").
                document(user.getUid()).
                collection("saved").add(pet.id);
        db.collection("pets").document(pet.getID()).set(pet);
    }
}
