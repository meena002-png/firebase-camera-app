package com.indev.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
//import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.view.CameraView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    static ImageButton btnPhoto,gall;

    private Executor executor = Executors.newSingleThreadExecutor();
    CameraSelector cameraSelector;
    CameraView mCameraView;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;

    String imgUri;

    private int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.RECORD_AUDIO"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        gall = findViewById(R.id.gal);

        gall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,DisplayImageActivity.class);
                startActivity(intent);
            }
        });

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS);
        }
    }

    private void startCamera() {
        btnPhoto = findViewById(R.id.btnPhoto);
        mCameraView = findViewById(R.id.view_finder);
        mCameraView.setFlash(ImageCapture.FLASH_MODE_AUTO);
        ImageCapture.Builder builder = new ImageCapture.Builder();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mCameraView.bindToLifecycle((LifecycleOwner) MainActivity.this);

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
                final File file1 = new File(getBatchDirectoryName(), mDateFormat.format(new Date()) + ".jpg");

                mCameraView.setCaptureMode(CameraView.CaptureMode.IMAGE);
                ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file1).build();
                mCameraView.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Uri f= Uri.fromFile(file1);
                                int pathSeparator = String.valueOf(file1).lastIndexOf('/');
                                int extensionSeparator = String.valueOf(file1).lastIndexOf('.');
                                String filename = pathSeparator >= 0 ? String.valueOf(file1).substring(pathSeparator + 1) : String.valueOf(file1);
                                upload(filename,f);
                                Toast.makeText(MainActivity.this,"hello"+f,Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException error) {
                        error.printStackTrace();
                    }
                });

            }
        });


    }

    private void upload(String name, Uri path){
        StorageReference storageReference = mStorageReference.child("img"+name);
        storageReference.putFile(path).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                mStorageReference.child("img"+name).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
//                        String imgUri = task.getResult().toString();
                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                        String cdate = df.format(new Date());
                        Log.d("upload",cdate);
                        Member member =new Member(name, task.getResult().toString(),cdate);
                        String uploadId = mDatabaseReference.push().getKey();

                        mDatabaseReference.child(uploadId).setValue(member);

                    }
                });

//                Log.d("upload",member.getLink());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"failed",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        finish();
    }


    public boolean allPermissionsGranted(){
        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermissionsGranted()){
                startCamera();
            } else{
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }


    public String getBatchDirectoryName() {
        String app_folder_path;
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            app_folder_path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        } else {
            app_folder_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera";
        }

        File dir = new File(app_folder_path);
        if (!dir.exists() && !dir.mkdirs()) {
        }
        return app_folder_path;
    }
}