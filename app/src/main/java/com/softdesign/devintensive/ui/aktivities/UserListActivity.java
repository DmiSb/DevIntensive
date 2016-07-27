package com.softdesign.devintensive.ui.aktivities;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.redmadrobot.chronos.gui.activity.ChronosAppCompatActivity;
import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.helpers.LoaderUsersFromDb;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.ui.adapters.UsersAdapter;
import com.softdesign.devintensive.ui.fragments.RetainFragment;
import com.softdesign.devintensive.ui.helpers.ItemTouchHelperCallback;
import com.softdesign.devintensive.utils.AppConfig;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.TransformRoundedImage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Список пользователей
 */
public class UserListActivity extends ChronosAppCompatActivity {

    private static final String TAG = ConstantManager.TAG_PREFIX + "UserListActivity";

    @BindView(R.id.main_coordinator_container)  CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)                     Toolbar mToolbar;
    @BindView(R.id.navigation_drawer)           DrawerLayout mNavigationDrawer;
    @BindView(R.id.user_list)                   RecyclerView mRecyclerView;

    private DataManager mDataManager;
    private UsersAdapter mUsersAdapter;
    private List<User> mUsers;
    private ImageView mAvatar;
    private RetainFragment mRetainFragment;
    private String mQuery;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        ButterKnife.bind(this);

        mDataManager = DataManager.getInstance();
        mDataManager.getPreferencesManager().saveLastActivity("UserListActivity");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        setUpItemTouchHelper();

        mHandler = new Handler();

        setupToolbar();
        setupDrawer();

        if (savedInstanceState == null) {
            mRetainFragment = new RetainFragment();
            getSupportFragmentManager().beginTransaction().add(mRetainFragment, RetainFragment.TAG).commit();
            loadUsersFromDb();
        } else {
            mRetainFragment = (RetainFragment) getSupportFragmentManager().findFragmentByTag(RetainFragment.TAG);
            mUsers = mRetainFragment.getUserList();
            showUsers(mUsers);
        }

        if (mAvatar != null)
            DataManager.getInstance().getPicasso()
                    .load(mDataManager.getPreferencesManager().loadUserAvatar())
                    .transform(new TransformRoundedImage())
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.avatar_empty)
                    .into(mAvatar, new Callback() {

                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Load avatar from cache");
                        }

                        @Override
                        public void onError() {
                            DataManager.getInstance().getPicasso()
                                    .load(mDataManager.getPreferencesManager().loadUserAvatar())
                                    .transform(new TransformRoundedImage())
                                    .placeholder(R.drawable.avatar_empty)
                                    .into(mAvatar, new Callback() {

                                        @Override
                                        public void onSuccess() {
                                            Log.d(TAG, "Load avatar from network");
                                        }

                                        @Override
                                        public void onError() {
                                            Log.d(TAG, "Error load avatar from network");
                                        }
                                    });
                        }
                    });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRetainFragment.setUsersList(mUsers);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("Введите имя пользователя");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                showUsersByQuery(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");

        if(mNavigationDrawer.isDrawerOpen(GravityCompat.START)) {
            mNavigationDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void loadUsersFromDb() {
        Log.d(TAG, "Start chronos");
        runOperation(new LoaderUsersFromDb());
    }

    public void onOperationFinished(final LoaderUsersFromDb.Result result) {
        Log.d(TAG, "Finished chronos");
        if (result.isSuccessful()) {
            if (result.getOutput().size() == 0) {
                showSnackBar(ConstantManager.ERROR_USER_LIST);
            } else {
                showUsers(result.getOutput());
            }
        } else {
            showSnackBar(ConstantManager.ERROR_USER_LIST);
        }
    }

    /**
     * Показ сообщения в нижней части
     * @param message - текст сообщения
     */
    public void showSnackBar(String message) {
        Log.d(TAG, "showSnackBar");

        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void showUsers(List<User> users) {
        mUsers = users;
        mUsersAdapter = new UsersAdapter(mUsers, new UsersAdapter.UserViewHolder.CustomClickListener() {
            @Override
            public void onUserItemClickListener(int position) {
                UserDTO userDTO = new UserDTO(mUsers.get(position));
                Intent profileIntent = new Intent(UserListActivity.this, ProfileUserActivity.class);
                profileIntent.putExtra(ConstantManager.PARCELABLE_KEY, userDTO);
                startActivity(profileIntent);
            }
        });
        mRecyclerView.swapAdapter(mUsersAdapter, false);

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(mUsersAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void showUsersByQuery(String query) {
        mQuery = query;

        Runnable searchUsers = new Runnable() {
            @Override
            public void run() {
                showUsers(mDataManager.getUserListByName(mQuery));
            }
        };

        mHandler.removeCallbacks(searchUsers);
        if (query.trim().isEmpty()) {
            mHandler.postDelayed(searchUsers, 0);
        } else {
            mHandler.postDelayed(searchUsers, AppConfig.SEARCH_DELAY);
        }
    }

    /**
     * Настройка боково панели
     */
    private void setupDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    mNavigationDrawer.closeDrawer(GravityCompat.START);
                    showSnackBar(item.getTitle().toString());
                    item.setChecked(true);
                    switch (item.getItemId()) {
                        case R.id.user_profile_menu:
                            Intent mainIntent = new Intent(UserListActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                            break;
                        case R.id.team_menu:
                            break;
                    }
                    return false;
                }
            });
            mAvatar = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.user_avatar);
        }
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * This is the standard support library way of implementing "swipe to delete" feature. You can do custom drawing in onChildDraw method
     * but whatever you draw will disappear once the swipe is over, and while the items are animating to their new position the recycler view
     * background will be visible. That is rarely an desired effect.
     */
    private void setUpItemTouchHelper() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            // we want to cache these and not allocate anything repeatedly in the onChildDraw method
            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                xMark = ContextCompat.getDrawable(UserListActivity.this, R.drawable.ic_clear_24dp);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) UserListActivity.this.getResources().getDimension(R.dimen.spacing_normal_16);
                initiated = true;
            }

            // not important, we don't want drag & drop
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                UsersAdapter testAdapter = (UsersAdapter) recyclerView.getAdapter();
                if (testAdapter.isUndoOn() && testAdapter.isPendingRemoval(position)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
                UsersAdapter adapter = (UsersAdapter)mRecyclerView.getAdapter();
                boolean undoOn = adapter.isUndoOn();
                if (undoOn) {
                    adapter.pendingRemoval(swipedPosition);
                } else {
                    adapter.remove(swipedPosition);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }

                if (!initiated) {
                    init();
                }

                // draw red background
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();

                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                xMark.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }
}
