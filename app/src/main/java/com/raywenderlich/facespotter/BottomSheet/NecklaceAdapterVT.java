package com.raywenderlich.facespotter.BottomSheet;

import android.animation.Animator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.raywenderlich.facespotter.FaceActivity;
import com.raywenderlich.facespotter.R;

import java.util.List;

public class NecklaceAdapterVT extends RecyclerView.Adapter<NecklaceAdapterVT.NecklaceAdapterViewHolder> {

    Context context;
    List<VirtualTryModel> virtualTryModelList;

    public NecklaceAdapterVT(Context context, List<VirtualTryModel> virtualTryModelList) {
        this.context = context;
        this.virtualTryModelList = virtualTryModelList;
    }

    @NonNull
    @Override
    public NecklaceAdapterVT.NecklaceAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_item_vt_neck, parent, false);
        return new NecklaceAdapterVT.NecklaceAdapterViewHolder(view);
    }

    public VirtualTryModel getItemAt(int position) {
        return virtualTryModelList.get(position);
    }

    @Override
    public void onBindViewHolder(@NonNull final NecklaceAdapterVT.NecklaceAdapterViewHolder holder, final int position) {

        Glide.with(context).load(virtualTryModelList.get(position).getImage()).placeholder(R.drawable.upicon).error(R.drawable.down_icon).into(holder.virtualtry_imgneck);
        holder.virtualtry_knowbtnneck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FaceActivity faceActivity = new FaceActivity();
                faceActivity.getDownloadedDrawable(virtualTryModelList.get(position).getImage(), context, virtualTryModelList.get(position).getType());
                //Toast.makeText(context, "Know More : "+virtualTryModelList.get(position).getId(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return virtualTryModelList.size();
    }

    public static class NecklaceAdapterViewHolder extends RecyclerView.ViewHolder {

        ImageView virtualtry_imgneck;
        Button virtualtry_knowbtnneck;


        public NecklaceAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            virtualtry_imgneck = itemView.findViewById(R.id.virtualtry_imgneck);
            virtualtry_knowbtnneck = itemView.findViewById(R.id.virtualtry_knowbtnneck);


        }
    }


}
