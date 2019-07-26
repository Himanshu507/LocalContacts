package com.himanshu.contacts;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.himanshu.contacts.Room.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SimpleRecyclerAdapter extends RecyclerView.Adapter<SimpleRecyclerAdapter.SimpleViewHolder> {

    Context mContext;
    List<User> user;
    int count;

    public SimpleRecyclerAdapter(Context context, List<User> user) {
        this.mContext = context;
        // this.mData = mData;
        this.user = user;
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.contacts_item, viewGroup, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SimpleViewHolder simpleViewHolder, int i) {
        simpleViewHolder.pic_layout.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_transition));
        simpleViewHolder.name_layout.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.item_animation));
        simpleViewHolder.title.setText(user.get(i).getName());
        final int a = i;
        if (user.get(a).getProfile() != null){
            byte[] b = user.get(a).getProfile();
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            simpleViewHolder.imageView.setImageBitmap(bitmap);
        }
        simpleViewHolder.name_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, Contact_Details.class);
                i.putExtra("Id", user.get(a).getId());
                simpleViewHolder.name_layout.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return user.size();
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public CircleImageView imageView;
        public LinearLayout name_layout, pic_layout;

        public SimpleViewHolder(@NonNull View itemView) {
            super(itemView);
            name_layout = itemView.findViewById(R.id.name_layout);
            pic_layout = itemView.findViewById(R.id.pic_layout);
            imageView = itemView.findViewById(R.id.contact_img);
            title = itemView.findViewById(R.id.name);

        }
    }
}