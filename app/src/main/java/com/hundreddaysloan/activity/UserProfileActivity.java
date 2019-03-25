package com.hundreddaysloan.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.auth.FirebaseAuth;
import com.hundreddaysloan.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar toolbar;
    MenuItem editMenuItem;

    private TextView toolbarTitle, tvUserName, tvUserEmail;
    private ImageView home_action;
    private Button btn_done;
    String mCurrentPhotoPath;
    EditText etUserName;
    int loginType;
    Dialog dfCommonAlertDialog;
    SpinKitView df_progressbar;

    private static final int MY_CAMERA_REQUEST_CODE = 101;
    private int GALLERY = 111;
    private int REQUEST_IMAGE_CAPTURE = 100;

    LinearLayout df_progressbar_layout, userDisplayLayout;
    ImageView update_pic;

    LinearLayout regularLoginLayout;
    TextView pp_text;
    boolean editFlag;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        toolbar = findViewById(R.id.toolbar_top);
        toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        home_action = toolbar.findViewById(R.id.home_action);
        toolbarTitle.setText("Profile");
        home_action.setOnClickListener(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        regularLoginLayout = findViewById(R.id.regularLoginLayout);
        df_progressbar_layout = findViewById(R.id.df_progressbar_layout);
        df_progressbar = findViewById(R.id.df_progressbar);
        userDisplayLayout = findViewById(R.id.userDisplayLayout);

        pp_text = findViewById(R.id.pp_text);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);

        update_pic = findViewById(R.id.update_pic);
        final ImageView add_pic = findViewById(R.id.add_pic);


        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }
        String[] alternativeDispName = new String[0];
        try {
            if(null != firebaseAuth.getCurrentUser().getEmail()) {
                Log.e("UserProfileActivity", "" + firebaseAuth.getCurrentUser().getEmail());
                tvUserEmail.setText(firebaseAuth.getCurrentUser().getEmail());
                alternativeDispName = firebaseAuth.getCurrentUser().getEmail().split("\\@");
            }else{
                tvUserEmail.setText("noemail@noemail.com");
            }
            if(null != firebaseAuth.getCurrentUser().getDisplayName() &&
                    !firebaseAuth.getCurrentUser().getDisplayName().trim().equalsIgnoreCase("")) {
                tvUserName.setText(firebaseAuth.getCurrentUser().getDisplayName());
            }else{
                tvUserName.setText(alternativeDispName[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String imageFileName = "100d_user";

//        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        String file = null;
        try {
            file = storageDir.getAbsolutePath()+"/"+imageFileName+".jpg";
        } catch (Exception e) {
            e.printStackTrace();
        }

        File image = null;
        if (file != null) {
            image = new File(file);
        }
        if (image != null && image.exists()){
            mCurrentPhotoPath = image.getAbsolutePath();
            Log.e("Image","::"+mCurrentPhotoPath);
            setPic();
            update_pic.setVisibility(View.VISIBLE);
            pp_text.setVisibility(View.GONE);
        }else{
            Log.e("Image","doesn't exist");
            pp_text.setVisibility(View.VISIBLE);
            pp_text.setText("add photo");
            update_pic.setVisibility(View.VISIBLE);
        }

        btn_done = findViewById(R.id.btn_done);
        btn_done.setOnClickListener(this);

        pp_text.setOnClickListener(this);
        update_pic.setOnClickListener(this);
        add_pic.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.pp_text:
            case R.id.update_pic:
//                galleryAddPic();
//                break;
            case R.id.add_pic:
                showPictureDialog();
                break;
            case R.id.home_action:
            case R.id.btn_done:
                finish();
                break;
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
        String imageFileName = "100d_user";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                imageFileName,  // prefix
//                ".jpg",         // suffix
//                storageDir      // directory
//        );
        String file = storageDir.getAbsolutePath()+"/"+imageFileName+".jpg";
        File image =new File(file);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private File createImageFile1() throws IOException {
        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "100d_user";

//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        String file = storageDir.getAbsolutePath()+"/"+imageFileName+".jpg";
        File image = new File(file);
        Log.e("image",""+image);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();

        Log.e("mCurrentPhotoPath",""+mCurrentPhotoPath);
        return image;
    }


//    @TargetApi(Build.VERSION_CODES.M)
    private void dispatchTakePictureIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        MY_CAMERA_REQUEST_CODE);
            }else{
                takePic();
            }
        }else {
            takePic();
        }
    }

    private void takePic(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        takePictureIntent.setType("image/*");
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("IOException",""+ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.hundreddaysloan.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onActivityResult(final int requestCode,
                                 final int resultCode,
                                 final Intent data) {
        Log.e("onActivityResult","onActivityResult");
        Log.e("resultCode","::"+resultCode);
        Log.e("data","::"+data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setPic();
            if (data != null) {
                try {
                    Bitmap mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                    update_pic.setImageBitmap(mImageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else if (requestCode == GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                Uri contentURI = data.getData();
                Log.e("data",""+data.getData());
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
//                    Toast.makeText(UserProfileActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    Log.e("path",""+path);
                    update_pic.setImageBitmap(bitmap);
//                    Uri contentURI1 = Uri.parse(path);
//                    Bitmap bitmap1 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI1);
//                    update_pic.setImageBitmap(bitmap1);
                    pp_text.setVisibility(View.GONE);

                } catch (IOException e) {
                    e.printStackTrace();
//                    Toast.makeText(UserProfileActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        File f = new File(mCurrentPhotoPath);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
            Log.e("IOException",""+ex.getMessage());
        }
        Uri contentUri = Uri.fromFile(photoFile);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPic() {

        // Get the dimensions of the View
        int targetW = update_pic.getWidth();
        int targetH = update_pic.getHeight();

        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                imageFileName,  // prefix
//                ".jpg",         // suffix
//                storageDir      // directory
//        );
        String file = storageDir.getAbsolutePath()+"/100d_user.jpg";
        File image =new File(file);
        mCurrentPhotoPath = file;

        Uri contentURI1 = Uri.parse(file);
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        update_pic.setImageBitmap(bitmap);

//        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
//        String path = saveImage(bitmap);

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = 0;
        try {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap1;
        bitmap1 = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        if(null == bitmap){
//            try {
//                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//                        .permitAll().build();
//                StrictMode.setThreadPolicy(policy);
//                URL url = new URL("https://lh3.googleusercontent.com/-e22PiQ2L3v4/AAAAAAAAAAI/AAAAAAAAAjo/0QlT8UseUD4/photo.jpg");
//                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//            } catch(IOException e) {
//                System.out.println(e);
//            }
//        }
//        update_pic.setImageBitmap(bitmap);
        pp_text.setVisibility(View.GONE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_CAMERA_REQUEST_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                showPictureDialog();
            } else {
                Toast.makeText(this, "Access denied.\nNeed permission to access camera.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        editMenuItem = menu.findItem(R.id.toolbar_menu);
        editMenuItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.toolbar_menu:
                Log.e("toolbar_menu","Clicked"+editFlag);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void showMessage(String msg){

        dfCommonAlertDialog =  new Dialog(UserProfileActivity.this);
        dfCommonAlertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dfCommonAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dfCommonAlertDialog.setContentView(R.layout.common_alert);
        dfCommonAlertDialog.setCancelable(false);
        TextView show_msg = dfCommonAlertDialog.findViewById(R.id.show_msg);
        show_msg.setText(msg);
        Button btn_alert_ok = dfCommonAlertDialog.findViewById(R.id.btn_alert_ok);

        btn_alert_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dfCommonAlertDialog.dismiss();
            }
        });

        dfCommonAlertDialog.show();
    }


    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                dispatchTakePictureIntent();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

//        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
// Ensure that there's a camera activity to handle the intent
        if (pickIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;Uri photoURI = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("IOException", "" + ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.hundreddaysloan.fileprovider",
                        photoFile);
                pickIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(pickIntent, GALLERY);
            }
        }
    }
    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
            Log.e("IOException", "" + ex.getMessage());
        }


        try {
            FileOutputStream fo = new FileOutputStream(photoFile);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{photoFile.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.e("TAG", "File Saved::--->" + photoFile.getAbsolutePath());

            return photoFile.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }
}
