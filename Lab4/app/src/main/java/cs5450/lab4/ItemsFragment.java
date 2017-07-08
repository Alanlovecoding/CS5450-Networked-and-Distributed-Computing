package cs5450.lab4;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ItemsFragment extends Fragment {

    private DatabaseReference mDatabase;
    FirebaseRecyclerAdapter<Post, PostViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    public String query;

    public ItemsFragment() {}

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_item_list, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRecycler = (RecyclerView) rootView.findViewById(R.id.messages_list);
        mRecycler.setHasFixedSize(true);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        final Query postsQuery = getQuery(mDatabase);
        if (postsQuery == null) {
            mAdapter = null;
        } else {
            mAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(Post.class, R.layout.fragment_item, PostViewHolder.class, postsQuery) {
                @Override
                protected void populateViewHolder(final PostViewHolder viewHolder, final Post model, final int position) {
                    Log.e("Frag", "Query: " + query);
                    if (query == null || model.desc.contains(query)) {
                        viewHolder.bindToPost(model);
                    } else {
                        viewHolder.itemView.setVisibility(View.GONE);
                    }
                }
            };
        }
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public Query getQuery(DatabaseReference databaseReference) {
        Query publicQuery = databaseReference.child("public").limitToFirst(100);
        return publicQuery;
    }
}
