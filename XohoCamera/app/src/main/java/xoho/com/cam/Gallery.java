package xoho.com.cam;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Gallery extends AppCompatActivity implements ImagesAdapter.OnRecyclerItemClickListener {
    private RecyclerView recyclerView;
    private ImagesAdapter imagesAdapter;
    private List<File> imageFiles;

    @Override
    public void onItemClick(int position) {
        Intent viewIntent = new Intent(this, PictureView.class);
        viewIntent.setData(Uri.fromFile(imageFiles.get(position)));
        startActivity(viewIntent);
        Toast.makeText(this, "Normal Click at position " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShareClick(int position) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/");
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageFiles.get(position)));
        share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(share, "Share Image"));
        Toast.makeText(this, "Choose sharing options ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(int position) {
        imageFiles.get(position).delete();
        imagesAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Picture Deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        recyclerView = findViewById(R.id.recycler_view_local);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        imageFiles = Arrays.asList(MainActivity.userDir.listFiles());
        if (imageFiles.size() > 0) {
            imagesAdapter = new ImagesAdapter(this, imageFiles);
            recyclerView.setAdapter(imagesAdapter);

            imagesAdapter.setOnItemClickListner(this);
        }

    }


}