package com.sameera.loadimage;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CAMERA = 200;
    public static final int SELECT_FILE = 300;
    public static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 400;
    public static final String IMAGE_DIRECTORY_NAME = "LoadImage";
    private String mFileName;
    private String userChoosenTask;
    private Bitmap mBitmap;
    private ImageView imageView;
    private File mcheckFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        selectImage();
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery",
                "Cancel"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Select Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    cameraIntent();
                } else if (items[item].equals("Choose from Gallery")) {
                    userChoosenTask = "Choose from Gallery";
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        startActivityForResult(intent, REQUEST_CAMERA);

    }

    private void galleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_FILE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onCaptureImageResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }


    /*

    private void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            uri = data.getData();
            try {
                Bitmap mBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                mIvMyProfileImage.setImageBitmap(mBitmap);

                File mcheckFile = new File(getPath(uri));
                FileOutputStream out = new FileOutputStream(mcheckFile);

                if (mBitmap.getByteCount()/1024 > 2048) {
                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 30, out);
                } else if (mBitmap.getByteCount()/1024 > 1024) {
                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
                } else if (mBitmap.getByteCount()/1024 > 512) {
                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
                } else if (mBitmap.getByteCount()/1024 < 512) {
                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                } else {
                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
*/


    private void onCaptureImageResult(Intent data) {
        if (data != null) {
            try {

                if (data.getData() != null) {
                    mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                } else {
                    mBitmap = (Bitmap) data.getExtras().get("data");
                }

                mFileName = UUID.randomUUID().toString();
                imageView.setImageBitmap(mBitmap);


                //TODO - if you need to create file, you can do it here :)
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
//                    }
//                    return;
//                }
//                mcheckFile = saveToExternal(mBitmap, mFileName);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Could not load the image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }

    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public File saveToExternal(Bitmap bitmap, String fileName) {
        File filesDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
        // Create the storage directory if it does not exist
        if (!filesDir.exists()) {
            if (!filesDir.mkdirs()) {
                Log.e(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
            }
        }
        File checkFile = new File(filesDir, fileName + ".png");

//        mBitmap = checkRotation(mBitmap, mcheckFile.getPath());

        if (filesDir != null && filesDir.isDirectory() && filesDir.exists()) {
            try {
                FileOutputStream out = new FileOutputStream(checkFile);

                if (bitmap.getByteCount() / 1024 > 2048) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 30, out);
                } else if (bitmap.getByteCount() / 1024 > 1024) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
                } else if (bitmap.getByteCount() / 1024 > 512) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
                } else if (bitmap.getByteCount() / 1024 < 512) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                } else {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                }
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return checkFile;
    }
}
