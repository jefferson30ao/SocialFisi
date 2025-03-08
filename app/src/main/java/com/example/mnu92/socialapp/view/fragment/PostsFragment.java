package com.example.mnu92.socialapp.view.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mnu92.socialapp.GlideApp;
import com.example.mnu92.socialapp.R;
import com.example.mnu92.socialapp.model.Post;
import com.example.mnu92.socialapp.view.PostViewHolder;
import com.example.mnu92.socialapp.view.activity.EditPostActivity;
import com.example.mnu92.socialapp.view.activity.MediaActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;


public class PostsFragment extends Fragment {
    public DatabaseReference mReference;
    public FirebaseUser mUser;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posts, container, false);

        mReference = FirebaseDatabase.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Post>()
                .setIndexedQuery(setQuery(), mReference.child("posts/data"), Post.class)
                .setLifecycleOwner(this)
                .build();

        RecyclerView recycler = view.findViewById(R.id.rvPosts);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setAdapter(new FirebaseRecyclerAdapter<Post, PostViewHolder>(options) {
            @Override
            public PostViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new PostViewHolder(inflater.inflate(R.layout.item_post, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(final PostViewHolder viewHolder, int position, final Post post) {
                final String postKey = getRef(position).getKey();

                viewHolder.authorName.setText(post.author);
                GlideApp.with(PostsFragment.this).load(post.authorPhotoUrl).circleCrop().into(viewHolder.authorPhoto);

                if (post.likes.containsKey(mUser.getUid())) {
                    viewHolder.postMG.setImageResource(R.drawable.heart_on);
                    viewHolder.totalPostMg.setTextColor(getResources().getColor(R.color.red));
                } else {
                    viewHolder.postMG.setImageResource(R.drawable.heart_off);
                    viewHolder.totalPostMg.setTextColor(getResources().getColor(R.color.grey));
                }

                viewHolder.postContent.setText(post.content);

                if (post.mediaUrl != null) {
                    viewHolder.image.setVisibility(View.VISIBLE);
                    if ("audio".equals(post.mediaType)) {
                        viewHolder.image.setImageResource(R.drawable.audio);
                    } else {
                        GlideApp.with(PostsFragment.this).load(post.mediaUrl).centerCrop().into(viewHolder.image);

                    }
                    viewHolder.image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), MediaActivity.class);
                            intent.putExtra("mediaUrl", post.mediaUrl);
                            intent.putExtra("mediaType", post.mediaType);
                            startActivity(intent);
                        }
                    });
                } else {
                    viewHolder.image.setVisibility(View.GONE);
                }

                viewHolder.totalPostMg.setText(String.valueOf(post.likes.size()));

                viewHolder.likeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (post.likes.containsKey(mUser.getUid())) {
                            mReference.child("posts/data").child(postKey).child("likes").child(mUser.getUid()).setValue(null);
                            mReference.child("posts/user-likes").child(mUser.getUid()).child(postKey).setValue(null);
                        } else {
                            mReference.child("posts/data").child(postKey).child("likes").child(mUser.getUid()).setValue(true);
                            mReference.child("posts/user-likes").child(mUser.getUid()).child(postKey).setValue(true);
                        }
                    }

                });

                viewHolder.deletePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder dialogo = new AlertDialog.Builder(PostsFragment.this.getContext());
                        dialogo.setTitle(R.string.alert_delete_title);
                        dialogo.setMessage(R.string.content_delete_action);
                        dialogo.setPositiveButton("si", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mReference.child("posts/data").child(postKey).setValue(null);
                            }
                        });
                       dialogo.setNegativeButton("no", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) { }
                        });

                        dialogo.create();
                        dialogo.show();
                    }
                });

                viewHolder.editPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext().getApplicationContext(), EditPostActivity.class);
                        intent.putExtra("ID",postKey);
                        startActivity(intent);
                    }
                });
            }
        });

        return view;
    }

    Query setQuery(){
        return  mReference.child("posts/all-posts").limitToFirst(100);
    }
}
