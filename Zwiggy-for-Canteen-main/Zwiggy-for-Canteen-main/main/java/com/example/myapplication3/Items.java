package com.example.myapplication3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Space;

import com.example.myapplication3.adapter.MyDrinkAdapter;
import com.example.myapplication3.eventbus.MyUpdateCartEvent;
import com.example.myapplication3.listener.ICartLoadListener;
import com.example.myapplication3.listener.IDrinkLoadListener;
import com.example.myapplication3.model.CartModel;
import com.example.myapplication3.model.DrinkModel;
import com.example.myapplication3.utils.SpaceItemDecoration;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Items extends AppCompatActivity implements IDrinkLoadListener, ICartLoadListener {
    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;
    @BindView(R.id.mainLayout)
    RelativeLayout mainLayout;
    @BindView(R.id.badge)
    NotificationBadge badge;
    @BindView(R.id.btnCart)
    FrameLayout btnCart;
    IDrinkLoadListener drinkLoadListener;
    ICartLoadListener cartLoadListener;
    FirebaseUser user ;
    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        if(EventBus.getDefault().hasSubscriberForEvent(MyUpdateCartEvent.class))
        {
            EventBus.getDefault().removeStickyEvent(MyUpdateCartEvent.class);
        }
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onUpdateCart(MyUpdateCartEvent event){
        countCartItem();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        init();
        loadDrinkFromFirebase();
        countCartItem();


    }
    private void loadDrinkFromFirebase(){
        List<DrinkModel>drinkModels=new ArrayList<>();
        FirebaseDatabase.getInstance()
                .getReference("Food Items").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            for(DataSnapshot drinkSnapshot:snapshot.getChildren())
                            {
                                DrinkModel drinkModel = drinkSnapshot.getValue(DrinkModel.class);
                                drinkModel.setKey(drinkSnapshot.getKey());
                                drinkModels.add(drinkModel);
                            }
                            drinkLoadListener.onDrinkLoadSuccess(drinkModels);
                        }
                        else
                        {
                            drinkLoadListener.onDrinkLoadFailed("Cant Find Food Item");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        drinkLoadListener.onDrinkLoadFailed(error.getMessage());
                    }
                });
    }
    private void init(){
        ButterKnife.bind(this);
        drinkLoadListener = this;
        cartLoadListener = this;
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);
        recycler_view.setLayoutManager(gridLayoutManager);
        recycler_view.addItemDecoration(new SpaceItemDecoration());
        btnCart.setOnClickListener(View -> startActivity(new Intent(this,Cart.class)));
    }

    @Override
    public void onDrinkLoadSuccess(List<DrinkModel> drinkModelList) {

        MyDrinkAdapter adapter=new MyDrinkAdapter(this,drinkModelList,cartLoadListener);
        recycler_view.setAdapter(adapter);
    }

    @Override
    public void onDrinkLoadFailed(String message) {
        Snackbar.make(mainLayout,message,Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onCartLoadSuccess(List<CartModel> cartModelList) {
        int cartSum=0;
        for(CartModel cartModel:cartModelList)
        {
            cartSum+=cartModel.getQuantity();
        }
        badge.setNumber(cartSum);
    }

    @Override
    public void onCartLoadFailed(String message) {
        Snackbar.make(mainLayout,message,Snackbar.LENGTH_LONG).show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        countCartItem();
    }
    private void countCartItem() {
        List<CartModel> cartModels=new ArrayList<>();
        user= FirebaseAuth.getInstance().getCurrentUser();
        String userId=user.getUid();
        FirebaseDatabase.getInstance().getReference("Cart").
                child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot cartSnapshot : snapshot.getChildren())
                        {
                            CartModel cartModel=cartSnapshot.getValue(CartModel.class);
                            cartModel.setKey(cartSnapshot.getKey());
                            cartModels.add(cartModel);
                        }
                        cartLoadListener.onCartLoadSuccess(cartModels);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        cartLoadListener.onCartLoadFailed(error.getMessage());
                    }
                });

    }

}