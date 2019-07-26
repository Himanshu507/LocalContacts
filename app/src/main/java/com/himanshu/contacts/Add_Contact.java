package com.himanshu.contacts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.himanshu.contacts.Room.MyAppDatabase;
import com.himanshu.contacts.Room.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class Add_Contact extends AppCompatActivity {

    private static final int REQUEST_CODE = 43;
    private static final int CAMERA_REQUEST = 52;
    //For Database
    public static MyAppDatabase myAppDatabase;
    static String name = "";
    static String phone = "";
    static String email = "";
    String imageFilePath;
    Uri fileuri;
    CircleImageView user_img, add_img;
    EditText user_name, phone_no, user_email;
    Boolean img_selected=false;
    byte[] user_img_array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__contact);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myAppDatabase = Room.databaseBuilder(getApplicationContext(), MyAppDatabase.class, "userdb").allowMainThreadQueries().build();

        init_Views();
        btn_Works();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_detail_confrm, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.submit) {
            Add_TO_DataBase();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void Add_TO_DataBase() {
        name = user_name.getText().toString().trim();
        phone = phone_no.getText().toString().trim();
        email = user_email.getText().toString().trim();
        if (name.isEmpty()) {
            user_name.setError("Please Enter a Name");
            user_name.setFocusable(true);
            return;
        }
        if (phone.isEmpty() || phone.length() != 10) {
            phone_no.setError("Enter a valid Number");
            user_name.setFocusable(true);
            return;
        } else {
            User user = new User();
            user.setName(name);
            user.setPhone_no(phone);
            user.setEmail(email);
            if (img_selected){
                user.setProfile(user_img_array);
            }
            Add_Contact.myAppDatabase.myDao().addUser(user);
            Toast.makeText(getApplicationContext(), "Successfully created", Toast.LENGTH_LONG).show();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);

        }
    }

    private void btn_Works() {
        user_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryOrCameraDialog();
            }
        });
        add_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryOrCameraDialog();
            }
        });
        user_name = findViewById(R.id.user_name);
        phone_no = findViewById(R.id.phone);
        user_email = findViewById(R.id.email);
    }

    private void init_Views() {
        user_img = findViewById(R.id.circleImageView);
        add_img = findViewById(R.id.add);
    }

    public void galleryOrCameraDialog() {
        //permissionManager.checkAndRequestPermissions(this);
        TextView galleryTv, cameraTv, noImageTv;
        View view = View.inflate(this, R.layout.dialog_camera_open, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .show();

        galleryTv = view.findViewById(R.id.galleryTv);
        cameraTv = view.findViewById(R.id.cameraTv);

        galleryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
                alertDialog.dismiss();
            }
        });

        cameraTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCameraIntent();
                alertDialog.dismiss();
            }
        });
    }

    private void openCameraIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            img_selected = false;
            if (data != null) {
                img_selected = true;
                fileuri = data.getData();
                Uri uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    user_img.setImageBitmap(bitmap);
                    BitmapToByteArray(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == CAMERA_REQUEST) {
            img_selected = false;
            if (data != null) {
                img_selected = true;
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                user_img.setImageBitmap(photo);
                BitmapToByteArray(photo);
            }
        }
    }

    private void BitmapToByteArray(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        user_img_array = stream.toByteArray();
        bitmap.recycle();
    }

    private void openImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE);
    }

}
