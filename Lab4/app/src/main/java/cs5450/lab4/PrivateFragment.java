package cs5450.lab4;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class PrivateFragment extends ItemsFragment {

    public PrivateFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return databaseReference.child("private").child(getUid());
        }
        return null;
    }

}
