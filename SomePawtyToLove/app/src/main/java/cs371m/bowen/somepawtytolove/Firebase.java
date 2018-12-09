package cs371m.bowen.somepawtytolove;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;

import java.util.HashMap;

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
                collection("saved").add(pet);
    }

    Query getSavedPetsQuery(String uid) {
        Query query = db.collection("users").document(uid).collection(
                "saved");
        return query;
    }

    Pet getPet(String id, final PetJson.IPetJson petJson) {
        DocumentReference docRef = db.collection("pets").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        Pet pet = document.toObject(Pet.class);
                        petJson.fetchPet(pet);
                    } else {
                        Log.d("firebase", "No such document");
                    }
                } else {
                    Log.d("firebase", "get failed with ", task.getException());
                }
            }
        });
        Query query = db.collection("pets");

        return null;
    }

    public void getSettings(final Settings.ISettings callback) {
        DocumentReference docRef = db.collection("users")
                .document(user.getUid())
                .collection("settings")
                .document("settings");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    HashMap<String, String> settings = new HashMap<>();
                    settings.put("Age", null);
                    settings.put("Species", null);
                    settings.put("Breed", null);
                    settings.put("Sex", null);
                    if (doc != null){
                        Log.i("Settings", doc.getData().toString());
                        if (doc.get("Age") != null){
                            settings.put("Age", doc.get("Age").toString());
                        }
                        if (doc.get("Species") != null){
                            settings.put("Species", doc.get("Species").toString());
                        }
                        if (doc.get("Breed") != null){
                            settings.put("Breed", doc.get("Breed").toString());
                        }
                        if (doc.get("Sex") != null){
                            settings.put("Sex", doc.get("Sex").toString());
                        }
                        callback.loadSettings(settings);
                    } else {
                        callback.loadSettings(settings);
                    }
                } else {
                    Log.d("firebase", "get failed with ", task.getException());
                }
            }
        });
    }

    public void saveSettings(HashMap<String, String> settings) {
        db.collection("users").
                document(user.getUid()).
                collection("settings").
                document("settings").set(settings);
    }
}
