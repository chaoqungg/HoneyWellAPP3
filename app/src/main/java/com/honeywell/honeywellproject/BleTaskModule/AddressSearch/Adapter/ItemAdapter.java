package com.honeywell.honeywellproject.BleTaskModule.AddressSearch.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.honeywellproject.R;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Integer> mints;
    Drawable drawableGray,drawableYellow,drawableGreen;

    public static class ImageViewHolder extends RecyclerView.ViewHolder{


        private ImageView mImageView;

        public ImageViewHolder(View view) {
            super(view);
            mImageView=(ImageView) view.findViewById(R.id.iv_item);
        }
    }

    public static class TextViewHolder extends RecyclerView.ViewHolder{

        private TextView mTextView;

        public TextViewHolder(View view) {
            super(view);
            mTextView=(TextView) view.findViewById(R.id.tv_item);
        }
    }


    public ItemAdapter(List<Integer> intInfos){
        mints=intInfos;
    }


    public int getItemViewType(int position) {
        return  position%11==0 ? 1:0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mContext = parent.getContext();

         drawableGray = mContext.getResources().getDrawable(R.drawable.gray);
         drawableYellow = mContext.getResources().getDrawable(R.drawable.yellow);
         drawableGreen = mContext.getResources().getDrawable(R.drawable.green);
        LayoutInflater mInflater = LayoutInflater.from(mContext);

        switch (viewType) {

            case 0:
                return new ImageViewHolder(mInflater.inflate(R.layout.item_image, parent, false));

            case 1:
                return new TextViewHolder(mInflater.inflate(R.layout.item_text, parent, false));

        }
        return null;

        /*if(viewType==0){
            return new ImageViewHolder(mInflater.inflate(R.layout.item_image, parent, false));
        }else if(viewType==1){
            return new TextViewHolder(mInflater.inflate(R.layout.item_text, parent, false));
        }
        return null;*/
    }


    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);
        switch (viewType){
            case 0:
                if(mints.get(position)==0)
                ((ImageViewHolder)holder).mImageView.setImageDrawable(drawableGray);
                else if(mints.get(position)==1)
                    ((ImageViewHolder)holder).mImageView.setImageDrawable(drawableGreen);
                else if(mints.get(position)==2)
                    ((ImageViewHolder)holder).mImageView.setImageDrawable(drawableYellow);
                break;
            case 1:
                ((TextViewHolder) holder).mTextView.setText(String.valueOf(position/11*10));
                break;
        }
    }
    @Override
    public int getItemCount() {
        return mints.size();
    }
}




