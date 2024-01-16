package com.example.myapplication3.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication3.R;
import com.example.myapplication3.eventbus.MyUpdateCartEvent;
import com.example.myapplication3.model.CartModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyCartViewHolder> {
    private Context context;
    private List<CartModel> cartModelList;
    FirebaseUser user ;
    public MyCartAdapter(Context context, List<CartModel> cartModelList) {
        this.context = context;
        this.cartModelList = cartModelList;

    }

    @NonNull
    @Override
    public MyCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyCartViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_cart_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyCartViewHolder holder, int position) {
        Glide.with(context).load(cartModelList.get(position).getImage()).into(holder.imageView);
        holder.txtPrice.setText(new StringBuilder("Rs").append(cartModelList.get(position).getPrice()));
        holder.txtName.setText(new StringBuilder().append(cartModelList.get(position).getName()));
        holder.txtQuantity.setText(new StringBuilder().append(cartModelList.get(position).getQuantity()));
        holder.btnMinus.setOnClickListener(View -> {
            minusCartItem(holder,cartModelList.get(position));
        });
        holder.btnPlus.setOnClickListener(View -> {
            plusCartItem(holder,cartModelList.get(position));
        });
        holder.btnDelete.setOnClickListener(View -> {
            AlertDialog dialog=new AlertDialog.Builder(context).setTitle("Delete Item").
                    setMessage("Do you really want to Delete the Item").setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss()).setPositiveButton("OK", (dialogInterface, i) -> {
                        notifyItemRemoved(position);
                        deleteFromFirebase(cartModelList.get(position));
                        dialogInterface.dismiss();

                    }).create();
                    dialog.show();
        });
    }

    private void deleteFromFirebase(CartModel cartModel) {
        user= FirebaseAuth.getInstance().getCurrentUser();
        String userId=user.getUid();

        FirebaseDatabase.getInstance().getReference("Cart").
                child(userId).child(cartModel.getKey()).removeValue().
                addOnSuccessListener(unused -> EventBus.getDefault().postSticky(new MyUpdateCartEvent()));
    }
    private void plusCartItem(MyCartViewHolder holder,CartModel cartModel){
        cartModel.setQuantity(cartModel.getQuantity() + 1);
        cartModel.setTotalPrice(cartModel.getQuantity()*Float.parseFloat(cartModel.getPrice()));
        holder.txtQuantity.setText(new StringBuilder().append(cartModel.getQuantity()));
        updateFirebase(cartModel);
    }
    private void minusCartItem(MyCartViewHolder holder,CartModel cartModel){
        if(cartModel.getQuantity() > 1)
        {
            cartModel.setQuantity(cartModel.getQuantity() - 1);
            cartModel.setTotalPrice(cartModel.getQuantity()*Float.parseFloat(cartModel.getPrice()));
            holder.txtQuantity.setText(new StringBuilder().append(cartModel.getQuantity()));
            updateFirebase(cartModel);
        }
    }

    private void updateFirebase(CartModel cartModel)
    {
        user=FirebaseAuth.getInstance().getCurrentUser();
        String userId=user.getUid();

        FirebaseDatabase.getInstance().getReference("Cart").
                child(userId).child(cartModel.getKey()).setValue(cartModel).
                addOnSuccessListener(unused -> EventBus.getDefault().postSticky(new MyUpdateCartEvent()));
    }

    @Override
    public int getItemCount() {
        return cartModelList.size();
    }

    public class MyCartViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.btnMinus)
        ImageView btnMinus;
        @BindView(R.id.btnPlus)
        ImageView btnPlus;
        @BindView(R.id.btnDelete)
        ImageView btnDelete;
        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.txtName)
        TextView txtName;
        @BindView(R.id.txtPrice)
        TextView txtPrice;
        @BindView(R.id.txtQuantity)
        TextView txtQuantity;
        Unbinder unbinder;


        public MyCartViewHolder(@NonNull View itemView) {

            super(itemView);
            unbinder= ButterKnife.bind(this,itemView);

        }
    }

}
