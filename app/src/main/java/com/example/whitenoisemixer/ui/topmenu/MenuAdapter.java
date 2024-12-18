package com.example.whitenoisemixer.ui.topmenu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whitenoisemixer.R;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {
    private final List<TopMenuItem> menuItems;
    private final OnMenuItemClickListener listener;

    public interface OnMenuItemClickListener {
        void onMenuItemClick(TopMenuItem item);
    }

    public MenuAdapter(List<TopMenuItem> menuItems, OnMenuItemClickListener listener) {
        this.menuItems = menuItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        TopMenuItem item = menuItems.get(position);
        holder.menuTitle.setText(item.getTitle());
        holder.menuDescription.setText(item.getDescription());
        holder.menuImage.setImageResource(item.getImageResId());
        holder.menuActionButton.setImageResource(item.getImageBtnId());
        holder.menuActionButton.setOnClickListener(v -> listener.onMenuItemClick(item));
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView menuTitle, menuDescription;
        ImageView menuImage;
        ImageButton menuActionButton;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            menuTitle = itemView.findViewById(R.id.titleTextView);
            menuDescription = itemView.findViewById(R.id.composerTextView);
            menuImage = itemView.findViewById(R.id.albumImageView);
            menuActionButton = itemView.findViewById(R.id.imageButton);
        }
    }
}
