package xoho.com.cam;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.List;

public class PictureView extends AppCompatActivity {
    ImageView imageView;
    TextView title;
    TextView dateTime;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_view);

        imageView = findViewById(R.id.imageView);
        imageUri = getIntent().getData();
        title=findViewById(R.id.details_title);
        dateTime=findViewById(R.id.details_datetime);
        title.setVisibility(View.GONE);
        dateTime.setVisibility(View.GONE);
        setImageDetails();

        Toolbar toolbar = findViewById(R.id.picture_view_toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Glide.with(this).load(imageUri).into(imageView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_picture_view,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpTo(this,getIntent());
                return true;
            case R.id.menu_action_send:
                sharePicture();
                return true;
            case R.id.menu_action_delete:
                deletePicture();
                return true;
            case R.id.menu_action_details:
                title.setVisibility(View.VISIBLE);
                dateTime.setVisibility(View.VISIBLE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sharePicture(){
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/");
        share.putExtra(Intent.EXTRA_STREAM,getIntent().getData() );
        share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(share,"Share Image"));
    }

    private void deletePicture(){
        final File f= new File(getIntent().getData().getPath());
        f.delete();
        Toast.makeText(this,"Picture Deleted",Toast.LENGTH_SHORT).show();
        finish();
    }

    private void setImageDetails(){
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imageUri.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        title.setText(imageUri.getLastPathSegment());
        dateTime.setText(exif.getAttribute(ExifInterface.TAG_DATETIME));

    }

}
