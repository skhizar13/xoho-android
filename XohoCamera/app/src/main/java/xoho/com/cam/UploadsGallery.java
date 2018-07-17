package xoho.com.cam;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class UploadsGallery extends AppCompatActivity implements ImagesAdapter.OnRecyclerItemClickListener{
    private RecyclerView recyclerView;
    private ImagesAdapter imagesAdapter;

    private ProgressBar progressCircle;

    private FirebaseStorage fbStorage;
    private DatabaseReference databaseRef;
    private ValueEventListener dbListener;

    private List<Upload> uploadList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploads_gallery);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressCircle = findViewById(R.id.progress_circle);
        progressCircle.setVisibility(View.INVISIBLE);

        uploadList = new ArrayList<>();

        imagesAdapter = new ImagesAdapter(this);
        imagesAdapter.setUploadedImagesList(uploadList);
        recyclerView.setAdapter(imagesAdapter);
        imagesAdapter.setOnItemClickListner(this);

        fbStorage = FirebaseStorage.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        dbListener = databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uploadList.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    upload.setKey(postSnapshot.getKey());
                    uploadList.add(upload);
                }

                imagesAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UploadsGallery.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressCircle.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this,"Normal Click at position "+position,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShareClick(int position) {
        Toast.makeText(this,"Share option not implemented for uploadList"+position,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(int position) {
        final Upload selectedItem = uploadList.get(position);
        final String selectedItemKey = selectedItem.getKey();

        StorageReference imageRef = fbStorage.getReferenceFromUrl(selectedItem.getImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                databaseRef.child(selectedItemKey).removeValue();
                Toast.makeText(UploadsGallery.this,"Image Deleted from Uploads ",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseRef.removeEventListener(dbListener);
    }
}
