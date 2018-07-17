package xoho.com.cam;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private final int PERMISSION_STORAGE_ACCESS = 10;
    private final int IMAGE_CAPTURE_REQUEST_CODE = 1;
    static String mCurrentPhotoPath;
    static File userDir;
    static String userId;
    static FirebaseUser fbUser;
    private FirebaseAuth fbAuth;
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fbAuth = FirebaseAuth.getInstance();
            startSignInActivity();

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, PERMISSION_STORAGE_ACCESS);
    }

    private void startSignInActivity() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                takePicture();
                return true;
            case R.id.action_signout:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(MainActivity.this,"Signed Out",Toast.LENGTH_SHORT).show();
                            }
                        });
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClick(View view) {
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, PERMISSION_STORAGE_ACCESS);
        switch (view.getId()) {
            case R.id.takePictureButton:
                takePicture();
                break;
            case R.id.selectPictureButton:
                selectPicture();
                break;
            case R.id.sharePictureButton:
                openUploads();
                break;
        }
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "xoho.com.cam.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, IMAGE_CAPTURE_REQUEST_CODE);
            }
        }
    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File image = File.createTempFile(imageFileName, ".jpg", userDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void selectPicture() {
        Intent selectPicture = new Intent(this, Gallery.class);
        selectPicture.setData(Uri.fromFile(userDir));
        startActivity(selectPicture);
    }

    private void openUploads() {
        Intent selectPicture = new Intent(this, UploadsActivity.class);
        startActivity(selectPicture);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        switch (requestCode) {
            case IMAGE_CAPTURE_REQUEST_CODE:

                if (resultCode==RESULT_OK) {
                    Uri imageUri = Uri.fromFile(new File(mCurrentPhotoPath));
                    Intent viewIntent = new Intent(this, PictureView.class);
                    viewIntent.setData(imageUri);
                    startActivity(viewIntent);
                }
                else{
                    File file = new File(mCurrentPhotoPath);
                    file.delete();
                }
                break;
            case RC_SIGN_IN:
                IdpResponse response = IdpResponse.fromResultIntent(intent);
                if (response != null) {
                    // Successfully signed in
                    fbUser = fbAuth.getCurrentUser();
                    if (fbUser != null) {
                        userId = fbUser.getEmail();
                        userDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), userId);
                        if (!userDir.isDirectory()) {
                            userDir.mkdir();
                        }
                        Toast.makeText(this, "Welcome " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + "!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    finish();
                    Toast.makeText(this, "SignIn Failed", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    private void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!(grantResults.length > 0)) {
            Toast.makeText(this, "Please provide requested permissions to use all features", Toast.LENGTH_LONG).show();
        }
    }

}

