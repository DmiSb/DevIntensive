package com.softdesign.devintensive.data.network.res;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель данных пользователя
 */
public class UserModel {

    private User user;

    public User getUser() {
        return user;
    }

    public class User {

        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("first_name")
        @Expose
        private String firstName;
        @SerializedName("second_name")
        @Expose
        private String secondName;
        @SerializedName("__v")
        @Expose
        private int v;
        @SerializedName("repositories")
        @Expose
        private Repositories repositories;
        @SerializedName("contacts")
        @Expose
        private Contacts contacts;
        @SerializedName("profileValues")
        @Expose
        private ProfileValues profileValues;
        @SerializedName("publicInfo")
        @Expose
        private PublicInfo publicInfo;
        @SerializedName("specialization")
        @Expose
        private String specialization;
        @SerializedName("role")
        @Expose
        private String role;
        @SerializedName("updated")
        @Expose
        private String updated;

        public String getId() {
            return id;
        }

        public ProfileValues getProfileValues() {
            return profileValues;
        }

        public Contacts getContacts() {
            return contacts;
        }

        public Repositories getRepositories() {
            return repositories;
        }

        public PublicInfo getPublicInfo() {
            return publicInfo;
        }
    }

    public class Repositories {

        @SerializedName("repo")
        @Expose
        private List<Repo> repo = new ArrayList<Repo>();
        @SerializedName("updated")
        @Expose
        private String updated;

        public List<Repo> getRepo() {
            return repo;
        }
    }

    public class Repo {

        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("git")
        @Expose
        private String git;
        @SerializedName("title")
        @Expose
        private String title;

        public String getGit() {
            return git;
        }
    }

    public class Contacts {

        @SerializedName("vk")
        @Expose
        private String vk;
        @SerializedName("phone")
        @Expose
        private String phone;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("updated")
        @Expose
        private String updated;

        public String getVk() {
            return vk;
        }

        public String getPhone() {
            return phone;
        }

        public String getEmail() {
            return email;
        }
    }

    public class ProfileValues {

        @SerializedName("homeTask")
        @Expose
        private int homeTask;
        @SerializedName("projects")
        @Expose
        private int projects;
        @SerializedName("linesCode")
        @Expose
        private int linesCode;
        @SerializedName("rait")
        @Expose
        private int rait;
        @SerializedName("updated")
        @Expose
        private String updated;

        public int getProjects() {
            return projects;
        }

        public int getLinesCode() {
            return linesCode;
        }

        public int getRating() {
            return rait;
        }
    }

    public class PublicInfo {

        @SerializedName("bio")
        @Expose
        private String bio;
        @SerializedName("avatar")
        @Expose
        private String avatar;
        @SerializedName("photo")
        @Expose
        private String photo;
        @SerializedName("updated")
        @Expose
        private String updated;

        public String getBio() {
            return bio;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getPhoto() {
            return photo;
        }

        public String getLastUpdated() {
            return updated;
        }
    }

}
