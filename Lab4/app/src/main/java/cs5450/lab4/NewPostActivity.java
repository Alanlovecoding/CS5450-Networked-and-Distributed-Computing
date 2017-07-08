package cs5450.lab4;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class NewPostActivity extends BaseActivity implements View.OnClickListener {

    private StorageReference mStorage;
    private DatabaseReference mDataBase;

    private EditText mDesc;
    private Button mSelectImage;
    private Switch mIsPriSwitch;
    private TextView mUserName;

    private static final int GALLARY_INTENT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        mStorage = FirebaseStorage.getInstance().getReference();
        mDataBase = FirebaseDatabase.getInstance().getReference();

        mDesc = (EditText) findViewById(R.id.description);
        mIsPriSwitch = (Switch) findViewById(R.id.is_pri_switch);
        mSelectImage = (Button) findViewById(R.id.select_img);
        mUserName = (TextView) findViewById(R.id.user_name_text);
        mUserName.setText(usernameFromEmail());
        mSelectImage.setOnClickListener(this);
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLARY_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLARY_INTENT && resultCode == RESULT_OK) {
            //showProgressDialog();
            Uri uri = data.getData();
            String desc = mDesc.getText().toString();
            boolean isPri = mIsPriSwitch.isChecked();
            String author = usernameFromEmail();
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference filePath = mStorage.child("public").child(uri.getLastPathSegment());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //hideProgressDialog();
                    Toast.makeText(NewPostActivity.this, "Upload Success", Toast.LENGTH_LONG).show();
                }
            });
            Post p = new Post(uid, author, desc, uri);
            if (isPri) {
                mDataBase.child("private").child(uid).push().setValue(p.toMap());
            } else {
                mDataBase.child("public").push().setValue(p.toMap());
            }
            startActivity(new Intent(NewPostActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.select_img) {
            selectImage();
        }
    }
}
