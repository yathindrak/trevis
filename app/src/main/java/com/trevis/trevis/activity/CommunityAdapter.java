package com.trevis.trevis.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.trevis.trevis.R;
import com.trevis.trevis.modal.User;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder>  {
    private List<User> friendslist;
    private Context context;

    public CommunityAdapter(List<User> friendslist, Context context) {
        this.friendslist = friendslist;
        this.context = context;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        public TextView nameTextView;
        public TextView userStatusView;
        public CircleImageView userImageView;

        // each data item is just a string in this case
        //public TextView mTextView;
        public ViewHolder(View v) {
            super(v);

            mView = itemView;
            nameTextView = (TextView) v.findViewById(R.id.name);
            userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);

            //Picasso.get().load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CommunityAdapter(List<User> list) {
        friendslist = list;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.users_single_layout, parent, false);


        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        User user = friendslist.get(position);

        holder.nameTextView.setText(user.getName());
        holder.userStatusView.setText(user.getStatus());

        Picasso.get().load(user.getThumbimage()).placeholder(R.drawable.default_avatar).into(holder.userImageView);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (friendslist != null)
            return friendslist.size();
        return 0;
    }

    public void setFilter(ArrayList<User> users){
        friendslist = new ArrayList<>();
        friendslist.addAll(users);

        //Refresh the adapter
        notifyDataSetChanged();
    }
}
