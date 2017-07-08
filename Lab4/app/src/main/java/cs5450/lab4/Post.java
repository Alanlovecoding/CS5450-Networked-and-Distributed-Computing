package cs5450.lab4;

import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

public class Post {

    public String uid;
    public String author;
    public String desc;
    public String uri;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String uid, String author, String desc, Uri uri) {
        this.uid = uid;
        this.author = author;
        this.desc = desc;
        this.uri = uri.toString();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        map.put("author", author);
        map.put("desc", desc);
        map.put("uri", uri);
        return map;
    }
}
