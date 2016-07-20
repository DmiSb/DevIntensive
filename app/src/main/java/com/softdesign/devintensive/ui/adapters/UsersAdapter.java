package com.softdesign.devintensive.ui.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.ui.helpers.ItemTouchHelperAdapter;
import com.softdesign.devintensive.ui.views.AspectRatioImageView;
import com.softdesign.devintensive.utils.ConstantManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Адаптер списка пользователей
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder>
        implements ItemTouchHelperAdapter {

    private static final String TAG = ConstantManager.TAG_PREFIX + " UsersAdapter";

    private Context mContext;
    private List<User> mUsers;
    private List<User> mRemovalUsers;
    private UserViewHolder.CustomClickListener mCustomClickListener;
    private boolean mUndoOn;

    private Handler mHandler = new Handler();
    HashMap<User, Runnable> mPendingRunnables = new HashMap<>();

    public UsersAdapter(List<User> users, UserViewHolder.CustomClickListener customClickListener) {
        mUsers = users;
        mCustomClickListener = customClickListener;
        mRemovalUsers = new ArrayList<>();
    }

    @Override
    public UsersAdapter.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list, parent, false);
        return new UserViewHolder(convertView, mCustomClickListener);
    }

    @Override
    public void onBindViewHolder(final UsersAdapter.UserViewHolder holder, int position) {

        final User user = mUsers.get(position);
        final String userPhoto;

        if (user.getPhoto().isEmpty()) {
            userPhoto = "null";
            Log.d(TAG, "onBindViewHolder: User " + user.getFullName() + " not has photo");
        } else {
            userPhoto = user.getPhoto();
        }

        DataManager.getInstance().getPicasso()
                .load(userPhoto)
                .fit()
                .centerCrop()
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(holder.mDummy)
                .error(holder.mDummy)
                .into(holder.mUserPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "User " + user.getFullName() + " load photo from cache");
                    }

                    @Override
                    public void onError() {
                        DataManager.getInstance().getPicasso()
                                .load(userPhoto)
                                .fit()
                                .centerCrop()
                                .placeholder(holder.mDummy)
                                .error(holder.mDummy)
                                .into(holder.mUserPhoto, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d(TAG, "User " + user.getFullName() + " load photo from network");
                                    }

                                    @Override
                                    public void onError() {
                                        Log.d(TAG, "Could not fetch photo from user " + user.getFullName());
                                    }
                                });
                    }
                });

        holder.mFullName.setText(user.getFullName());
        holder.mRating.setText(String.valueOf(user.getRating()));
        holder.mCodeLines.setText(String.valueOf(user.getCodeLines()));
        holder.mProjects.setText(String.valueOf(user.getProjects()));

        if (user.getSelf() == null || user.getSelf().isEmpty()) {
            holder.mSelf.setVisibility(View.GONE);
        } else {
            holder.mSelf.setVisibility(View.VISIBLE);
            holder.mSelf.setText(user.getSelf());
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mUsers, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);

        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        mUsers.remove(position);
        notifyItemRemoved(position);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        protected AspectRatioImageView mUserPhoto;
        protected TextView mFullName, mRating, mCodeLines, mProjects, mSelf;
        protected Button mShowMore;
        protected Drawable mDummy;

        private CustomClickListener mListener;

        public UserViewHolder(View itemView, CustomClickListener customClickListener) {
            super(itemView);

            mListener = customClickListener;

            mUserPhoto = (AspectRatioImageView) itemView.findViewById(R.id.user_photo_img);
            mFullName = (TextView) itemView.findViewById(R.id.user_full_name_txt);
            mRating = (TextView) itemView.findViewById(R.id.rating_value);
            mCodeLines = (TextView) itemView.findViewById(R.id.rating_code_line);
            mProjects = (TextView) itemView.findViewById(R.id.rating_project);
            mSelf = (TextView) itemView.findViewById(R.id.user_self_txt);
            mShowMore = (Button) itemView.findViewById(R.id.more_info_btn);
            mDummy = mUserPhoto.getContext().getResources().getDrawable(R.drawable.user_bg);

            mShowMore.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onUserItemClickListener(getAdapterPosition());
            }
        }

        public interface CustomClickListener {

            void onUserItemClickListener(int position);

        }
    }

    public void setSearch(List<? extends User> searchUsers) {
        mUsers = new ArrayList<>();
        mUsers.addAll(searchUsers);
        notifyDataSetChanged();
    }

    public boolean isUndoOn() {
        return mUndoOn;
    }

    public void pendingRemoval(int position) {
        final User item = mUsers.get(position);
        if (!mRemovalUsers.contains(item)) {
            mRemovalUsers.add(item);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the item
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    remove(mUsers.indexOf(item));
                }
            };
            mHandler.postDelayed(pendingRemovalRunnable, ConstantManager.PENDING_REMOVAL_TIMEOUT);
            mPendingRunnables.put(item, pendingRemovalRunnable);
        }
    }

    public void remove(int position) {
        User item = mUsers.get(position);
        if (mRemovalUsers.contains(item)) {
            mRemovalUsers.remove(item);
        }
        if (mUsers.contains(item)) {
            mUsers.remove(position);
            notifyItemRemoved(position);
        }
    }

    public boolean isPendingRemoval(int position) {
        User item = mUsers.get(position);
        return mRemovalUsers.contains(item);
    }
}


