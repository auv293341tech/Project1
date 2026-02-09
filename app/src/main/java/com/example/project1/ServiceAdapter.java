package com.example.project1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ServiceAdapter
        extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private final List<Service> serviceList;
    private final OnServiceClickListener listener;

    // 🔔 Click callback
    public interface OnServiceClickListener {
        void onServiceClick(Service service);
    }

    public ServiceAdapter(
            List<Service> serviceList,
            OnServiceClickListener listener
    ) {
        this.serviceList = serviceList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ServiceViewHolder holder,
            int position
    ) {
        Service service = serviceList.get(position);

        // ✅ SAFE TEXT BINDING
        holder.tvServiceTitle.setText(
                service.getTitle() != null
                        ? service.getTitle()
                        : "Service"
        );

        holder.tvServiceDescription.setText(
                service.getDescription() != null
                        ? service.getDescription()
                        : "Tap to continue"
        );

        // ✅ CLICK HANDLER
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onServiceClick(service);
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceList != null ? serviceList.size() : 0;
    }

    // ================= VIEW HOLDER =================
    static class ServiceViewHolder extends RecyclerView.ViewHolder {

        TextView tvServiceTitle;
        TextView tvServiceDescription;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceTitle = itemView.findViewById(R.id.tvServiceTitle);
            tvServiceDescription = itemView.findViewById(R.id.tvServiceDescription);
        }
    }
}
