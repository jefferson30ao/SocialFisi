package com.example.mnu92.socialapp.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mnu92.socialapp.R;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public ImageView authorPhoto;
    public TextView authorName;
    public TextView postContent;
    public ImageView postMG;
    public TextView totalPostMg;
    public ImageView image;
    public LinearLayout likeLayout;
    public LinearLayout comentLayout;
    public TextView totalPostResponses;
    public ImageView editPost;
    public ImageView deletePost;


    public PostViewHolder(@NonNull View itemView) {
        super(itemView);

        authorPhoto = itemView.findViewById(R.id.photo);
        authorName = itemView.findViewById(R.id.author);
        postContent = itemView.findViewById(R.id.content);
        postMG = itemView.findViewById(R.id.like);
        totalPostMg = itemView.findViewById(R.id.num_likes);
        image = itemView.findViewById(R.id.image);
        likeLayout = itemView.findViewById(R.id.like_layout);
        comentLayout = itemView.findViewById(R.id.coment_layout);
        totalPostResponses = itemView.findViewById(R.id.num_responses);
        editPost = itemView.findViewById(R.id.editPost);
        deletePost = itemView.findViewById(R.id.deletePost);



    }
}
