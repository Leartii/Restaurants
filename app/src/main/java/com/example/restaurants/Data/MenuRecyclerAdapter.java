package com.example.restaurants.Data;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurants.Add_Menu;
import com.example.restaurants.Model.Menu_Items;
import com.example.restaurants.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


import de.hdodenhof.circleimageview.CircleImageView;

public class MenuRecyclerAdapter extends RecyclerView.Adapter<MenuRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Menu_Items> menu_items;


    public MenuRecyclerAdapter(Context context, List<Menu_Items> menu_items) {
        this.context = context;
        this.menu_items = menu_items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.menu_item, parent, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Menu_Items menu_item = menu_items.get(position);

        Picasso.with(context).load(menu_item.getImage()).placeholder(android.R.drawable.ic_btn_speak_now).into(holder.imgView);
        holder.nameV.setText(menu_item.getName());
        holder.priceV.setText(menu_item.getPrice()+"€");
    }

    @Override
    public int getItemCount() {

            return menu_items.size();


    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private CircleImageView imgView;
        private TextView priceV, nameV;
        private ImageButton edit,remove;
        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            imgView  = (CircleImageView) itemView.findViewById(R.id.menuCircleImg);
            priceV = (TextView) itemView.findViewById(R.id.price_item);
            nameV = (TextView) itemView.findViewById(R.id.item_name);
            edit = (ImageButton) itemView.findViewById(R.id.edit_item);
            remove = (ImageButton) itemView.findViewById(R.id.delete_item);

            edit.setOnClickListener(this);
            remove.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.edit_item : {
                    Menu_Items menu = menu_items.get(getAdapterPosition());
                    Intent intent = new Intent(context, Add_Menu.class);
                    String key = menu.getParent_key();
                    intent.putExtra("key", key);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                } break;
                case R.id.delete_item: {
                    Menu_Items meni = menu_items.get(getAdapterPosition());
                    DatabaseReference dbreference = FirebaseDatabase.getInstance().getReference().child("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Menu")
                            .child(meni.getParent_key());
                    dbreference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Successfullly deleted", Toast.LENGTH_SHORT).show();
                        }
                    });

                }break;
            }


        }
    }
}
