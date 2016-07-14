package com.softdesign.devintensive.ui.aktivities;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.ui.adapters.RepositoriesAdapter;
import com.softdesign.devintensive.utils.ConstantManager;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileUserActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.collapsing_toolbar)  CollapsingToolbarLayout mCollapsingToolbar;

    @BindView(R.id.repositories_list) ListView mRepoListView;
    @BindView(R.id.user_photo_img) ImageView mUserPhoto;
    @BindView(R.id.self_et) EditText mUserSelf;

    @BindView(R.id.rating_value) TextView mUserRating;
    @BindView(R.id.rating_code_line) TextView mUseeCodeLines;
    @BindView(R.id.rating_project) TextView mUserProjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        ButterKnife.bind(this);

        setupToolBar();
        initProfileData();
    }

    private void setupToolBar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initProfileData() {
        UserDTO userDTO = getIntent().getParcelableExtra(ConstantManager.PARCELABLE_KEY);
        final List<String> repoList = userDTO.getRepositories();
        RepositoriesAdapter repositoriesAdapter = new RepositoriesAdapter(this, repoList);
        mRepoListView.setAdapter(repositoriesAdapter);

        mRepoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Snackbar.make(mCollapsingToolbar, "Репозиторий : " + repoList.get(position), Snackbar.LENGTH_LONG).show();
                String urlGit = "https://" + repoList.get(position);
                Intent gitIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlGit));
                startActivity(gitIntent);
            }
        });

        mUserSelf.setText(userDTO.getSelf());
        mUserRating.setText(userDTO.getRating());
        mUseeCodeLines.setText(userDTO.getCodeLines());
        mUserProjects.setText(userDTO.getProjects());

        mCollapsingToolbar.setTitle(userDTO.getFullName());

        Picasso.with(this)
                .load(userDTO.getPhoto())
                .placeholder(R.drawable.user_bg)
                .error(R.drawable.user_bg)
                .into(mUserPhoto);
    }
}
