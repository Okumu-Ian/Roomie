package com.company.roomie.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.company.roomie.R;
import com.company.roomie.models.HouseComment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {

    class CommentHolder extends RecyclerView.ViewHolder{

        private AppCompatTextView email,comment;

        CommentHolder(@NonNull View itemView) {
            super(itemView);
            email = itemView.findViewById(R.id.txt_comment_email);
            comment = itemView.findViewById(R.id.txt_comment_text);
        }
    }

    private Context mCtx;
    private List<HouseComment> comments;

    public CommentAdapter(Context mCtx, List<HouseComment> comments) {
        this.mCtx = mCtx;
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentHolder(LayoutInflater.from(mCtx).inflate(R.layout.house_comment_row,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {

       HouseComment houseComment = comments.get(position);
       holder.email.setText(houseComment.getUser_id());
       holder.comment.setText(houseComment.getComment_text());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}
