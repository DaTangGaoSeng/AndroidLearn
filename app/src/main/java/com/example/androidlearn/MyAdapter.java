package com.example.androidlearn;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    final  private  int  TYPE_FOOT = 0;
    final  private  int  TYPE_ITEM = 1;
    private List<Category> list;
    private Context context;
    public MyAdapter(List<Category> list, Context context) {
        this.list = list;
        this.context = context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_FOOT){
           View footer = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer,parent,false);
            return  new Footer(footer);
        }
        else {
            View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_layout,parent,false);
            return new Item(item);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof Footer){
                Footer footer = (Footer) holder;
                footer.foot.setText("我是有底线的,上拉刷新");
            }
            else {
                Item item = (Item)holder;
                final Category category = list.get(position);
                item.type.setText(category.getList().get("type"));
                item.publishedAt.setText(category.getList().get("publishedAt"));
                item.who.setText(category.getList().get("who"));
                item.desc.setText(category.getList().get("desc"));
                RequestOptions options = new RequestOptions().placeholder(R.mipmap.ic_launcher).
                        error(R.mipmap.ic_launcher_round).fallback(R.mipmap.ic_launcher_round).override(500, 500);
                try {
                    //把图片网址的第一条作为封面图
                    Glide.with(context).load(category.getImages().get(0)).apply(options).into(item.imageView);
                } catch (Exception e) {
                    e.fillInStackTrace();
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, WebAvtivity.class);
                        intent.putExtra("http", category.getList().get("url"));
                        context.startActivity(intent);
                    }
                });
            }
    }

    @Override
    public int getItemViewType(int position) {
       if(position + 1 == getItemCount()){
           return TYPE_FOOT;
       }
       else
           return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }
    class Item extends RecyclerView.ViewHolder{
        TextView desc;
        TextView who;
        TextView publishedAt;
        TextView type;
        ImageView imageView;
        View view;
        public Item(@NonNull View itemView) {
            super(itemView);
            desc = itemView.findViewById(R.id.desc);
            who = itemView.findViewById(R.id.who);
            publishedAt = itemView.findViewById(R.id.publishedAt);
            type = itemView.findViewById(R.id.type);
            imageView = itemView.findViewById(R.id.image_1);
            view = itemView.findViewById(R.id.fen);
        }
    }
    class Footer extends RecyclerView.ViewHolder {
        private TextView foot;
        public Footer(@NonNull View itemView) {
            super(itemView);
            foot = itemView.findViewById(R.id.foot);
        }
    }
}
