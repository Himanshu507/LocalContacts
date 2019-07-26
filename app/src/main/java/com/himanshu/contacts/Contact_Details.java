package com.himanshu.contacts;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;

import com.himanshu.contacts.Room.MyAppDatabase;
import com.himanshu.contacts.Room.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class Contact_Details extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 52;
    private static final int REQUEST_CODE = 43;

    CircleImageView user_img, circleImageView_edit, circleImageView_add;
    TextView user_name, phone_number, user_email;
    EditText edit_user_name, edit_phone_no, edit_email;
    ImageView call_img, msg_img, email_img;
    MyAppDatabase myAppDatabase;
    User user;
    String phone_no = "", name = "", email = "";
    byte[] img;
    int user_id;
    boolean edit_contact = false;
    CardView call_card, email_card;
    RelativeLayout relativeLayout;
    LinearLayout name_layout, phone_linear, email_linear;
    Boolean img_selected = false;
    byte[] user_img_array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact__details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myAppDatabase = Room.databaseBuilder(getApplicationContext(), MyAppDatabase.class, "userdb").allowMainThreadQueries().build();

        init_Views();
        btn_Works();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            assert extras != null;
            int b = extras.getInt("Id");
            user_id = b;
            update_details(user_id);
        } else {
            onBackPressed();
        }

    }

    private void update_details(int b) {
        user = myAppDatabase.myDao().findDetails(b);
        phone_no = user.getPhone_no();
        email = user.getEmail();
        name = user.getName();
        Bitmap bitmapp;
        if (user.getProfile() != null) {
            img = user.getProfile();
            bitmapp = BitmapFactory.decodeByteArray(img, 0, img.length);
            user_img.setImageBitmap(bitmapp);
            circleImageView_edit.setImageBitmap(bitmapp);
        }
        user_name.setText(name);
        phone_number.setText(phone_no);
        user_email.setText(email);

        edit_user_name.setText(name);
        edit_phone_no.setText(phone_no);
        edit_email.setText(email);
    }

    private void btn_Works() {
        call_img.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                isPermissionGranted();
            }
        });

        msg_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri sms_uri = Uri.parse("smsto:" + phone_no);
                Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
                sms_intent.putExtra("sms_body", "");
                startActivity(sms_intent);
            }
        });

        email_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email)));
            }
        });

        circleImageView_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryOrCameraDialog();
            }
        });

        circleImageView_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryOrCameraDialog();
            }
        });
    }

    private void init_Views() {
        user_img = findViewById(R.id.circleImageView);
        user_name = findViewById(R.id.textView);
        call_img = findViewById(R.id.call);
        phone_number = findViewById(R.id.phone_number);
        msg_img = findViewById(R.id.msg);
        email_img = findViewById(R.id.email_img);
        user_email = findViewById(R.id.email_id);
        relativeLayout = findViewById(R.id.relativeLayout);
        name_layout = findViewById(R.id.name_linear);
        phone_linear = findViewById(R.id.phone_linear);
        email_linear = findViewById(R.id.email_linear);
        call_card = findViewById(R.id.cardView);
        email_card = findViewById(R.id.email_card);
        circleImageView_edit = findViewById(R.id.circleImageView_edit);
        edit_user_name = findViewById(R.id.user_name);
        edit_phone_no = findViewById(R.id.phone);
        edit_email = findViewById(R.id.email);
        circleImageView_add = findViewById(R.id.add);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.update_details, menu);

        final MenuItem deleteItem = menu.findItem(R.id.menu);
        deleteItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                myAppDatabase.myDao().delete(user);
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Toast.makeText(getApplicationContext(), "Successfully Deleted", Toast.LENGTH_SHORT).show();
                startActivity(i);
                return false;
            }
        });

        final MenuItem editItem = menu.findItem(R.id.action_settings);
        editItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (!edit_contact) {
                    editItem.setIcon(R.drawable.ic_check_white_24dp);
                    edit_contact = true;
                    hiding_Views();
                } else {
                    editItem.setIcon(R.drawable.ic_edit_black_24dp);
                    edit_contact = false;
                    save_Details();
                    update_details(user_id);
                    hiding_Views();
                }
                return false;
            }
        });

        return true;
    }

    public void save_Details() {
        String new_name = edit_user_name.getText().toString().trim();
        String new_phone_no = edit_phone_no.getText().toString().trim();
        String new_email = edit_email.getText().toString().trim();
        if (new_name.isEmpty()) {
            edit_user_name.setError("Please Enter a Name");
            edit_user_name.setFocusable(true);
            return;
        }
        if (new_phone_no.isEmpty()) {
            edit_phone_no.setError("Enter a Number");
            edit_phone_no.setFocusable(true);
            return;
        } else {
            User u = new User();
            u.setId(user_id);
            u.setName(new_name);
            u.setPhone_no(new_phone_no);
            if (new_email != null) {
                u.setEmail(new_email);
            } else {
                u.setEmail(" ");
            }
            if (img_selected) {
                u.setProfile(user_img_array);
            }
            myAppDatabase.myDao().updateDetails(u);
        }
    }

    public void hiding_Views() {
        if (edit_contact) {
            user_img.setVisibility(View.GONE);
            user_name.setVisibility(View.GONE);
            call_card.setVisibility(View.GONE);
            email_card.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
            name_layout.setVisibility(View.VISIBLE);
            phone_linear.setVisibility(View.VISIBLE);
            email_linear.setVisibility(View.VISIBLE);
        } else {
            user_img.setVisibility(View.VISIBLE);
            user_name.setVisibility(View.VISIBLE);
            call_card.setVisibility(View.VISIBLE);
            email_card.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);
            name_layout.setVisibility(View.GONE);
            phone_linear.setVisibility(View.GONE);
            email_linear.setVisibility(View.GONE);
            update_details(user_id);
        }
    }

    public boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phone_no));
                startActivity(callIntent);
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG", "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();

                    isPermissionGranted();

                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
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
                Uri uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    circleImageView_edit.setImageBitmap(bitmap);
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
                circleImageView_edit.setImageBitmap(photo);
                BitmapToByteArray(photo);
            }
        }
    }

    private void BitmapToByteArray(Bitmap bitmap) {
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
