package com.example.myapplication3.listener;

import com.example.myapplication3.model.CartModel;
import com.example.myapplication3.model.DrinkModel;

import java.util.List;

public interface ICartLoadListener {
    void onCartLoadSuccess(List<CartModel> cartModelList);
    void onCartLoadFailed(String message);
}
