package com.softdesign.devintensive.data.storage.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object class
 */
public class UserDTO implements Parcelable {

    private String mPhoto;
    private String mFullName;
    private String mRating;
    private String mCodeLines;
    private String mProjects;
    private String mSelf;
    private List<String> mRepositories;

    public UserDTO(UserListRes.UserData userData) {
        mPhoto = userData.getPublicInfo().getPhoto();
        mFullName = userData.getFullName();
        mRating = String.valueOf(userData.getProfileValues().getRating());
        mCodeLines = String.valueOf(userData.getProfileValues().getLinesCode());
        mProjects = String.valueOf(userData.getProfileValues().getProjects());
        mSelf = userData.getPublicInfo().getBio();

        List<String> repoLinks = new ArrayList<>();
        for (UserModel.Repo gitLink: userData.getRepositories().getRepo()) {
            repoLinks.add(gitLink.getGit());
        }
        mRepositories = repoLinks;
    }

    protected UserDTO(Parcel in) {
        mPhoto = in.readString();
        mFullName = in.readString();
        mRating = in.readString();
        mCodeLines = in.readString();
        mProjects = in.readString();
        mSelf = in.readString();
        if (in.readByte() == 0x01) {
            mRepositories = new ArrayList<String>();
            in.readList(mRepositories, String.class.getClassLoader());
        } else {
            mRepositories = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPhoto);
        dest.writeString(mFullName);
        dest.writeString(mRating);
        dest.writeString(mCodeLines);
        dest.writeString(mProjects);
        dest.writeString(mSelf);
        if (mRepositories == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mRepositories);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UserDTO> CREATOR = new Parcelable.Creator<UserDTO>() {
        @Override
        public UserDTO createFromParcel(Parcel in) {
            return new UserDTO(in);
        }

        @Override
        public UserDTO[] newArray(int size) {
            return new UserDTO[size];
        }
    };

    public String getPhoto() {
        return mPhoto;
    }

    public String getFullName() {
        return mFullName;
    }

    public String getRating() {
        return mRating;
    }

    public String getCodeLines() {
        return mCodeLines;
    }

    public String getProjects() {
        return mProjects;
    }

    public String getSelf() {
        return mSelf;
    }

    public List<String> getRepositories() {
        return mRepositories;
    }
}
