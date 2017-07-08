package cs5450.lab4;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView descView;
    public TextView authorView;
    public TextView bodyView;
    public ImageView imageView;
    public StorageReference mStorage;

    public PostViewHolder(View itemView) {
        super(itemView);
        descView = (TextView) itemView.findViewById(R.id.post_desc);
        authorView = (TextView) itemView.findViewById(R.id.post_author);
        bodyView = (TextView) itemView.findViewById(R.id.post_body);
        mStorage = FirebaseStorage.getInstance().getReference();;
        imageView = (ImageView) itemView.findViewById(R.id.image_preview);
    }

    public void bindToPost(Post post) {
        descView.setText(post.desc);
        authorView.setText(post.author);
        bodyView.setText(post.uri);
    }
}
