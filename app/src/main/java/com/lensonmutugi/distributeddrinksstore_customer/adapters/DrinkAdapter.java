package com.lensonmutugi.distributeddrinksstore_customer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lensonmutugi.distributeddrinksstore_customer.R;
import com.lensonmutugi.distributeddrinksstore_customer.models.Drink;

import java.util.List;

public class DrinkAdapter extends RecyclerView.Adapter<DrinkAdapter.ViewHolder> {

    private final List<Drink> drinks;

    public DrinkAdapter(List<Drink> drinks) {
        this.drinks = drinks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_drink, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Drink drink = drinks.get(position);

        holder.txtName.setText(drink.getName());
        holder.txtQty.setText(String.valueOf(drink.getQuantity()));

        holder.btnPlus.setOnClickListener(v -> {
            drink.increase();
            holder.txtQty.setText(String.valueOf(drink.getQuantity()));
        });

        holder.btnMinus.setOnClickListener(v -> {
            if (drink.getQuantity() > 0) {
                drink.decrease();
                holder.txtQty.setText(String.valueOf(drink.getQuantity()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return drinks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtQty;
        Button btnPlus, btnMinus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtDrinkName);
            txtQty = itemView.findViewById(R.id.txtQuantity);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
        }
    }
}
