package com.softdesign.devintensive.data.storage.models;

import com.softdesign.devintensive.data.network.res.UserModel;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Модель данных репозиториев пользователя в БД
 */
@Entity(active = true, nameInDb = "REPOSITOROES")
public class Repository {

    @Id
    private Long id;

    @NotNull
    @Unique
    private String remoteId;

    private String reposotoryName;

    private String userRemoteId;

    /** Used for active entity operations. */
    @Generated(hash = 332345895)
    private transient RepositoryDao myDao;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    public Repository(UserModel.Repo repositoryRes, String userId) {
        this.remoteId = repositoryRes.getId();
        this.reposotoryName = repositoryRes.getGit();
        this.userRemoteId = userId;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 636002579)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getRepositoryDao() : null;
    }

    public String getUserRemoteId() {
        return this.userRemoteId;
    }

    public void setUserRemoteId(String userRemoteId) {
        this.userRemoteId = userRemoteId;
    }

    public String getReposotoryName() {
        return this.reposotoryName;
    }

    public void setReposotoryName(String reposotoryName) {
        this.reposotoryName = reposotoryName;
    }

    public String getRemoteId() {
        return this.remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 56722605)
    public Repository(Long id, @NotNull String remoteId, String reposotoryName,
            String userRemoteId) {
        this.id = id;
        this.remoteId = remoteId;
        this.reposotoryName = reposotoryName;
        this.userRemoteId = userRemoteId;
    }

    @Generated(hash = 984204935)
    public Repository() {
    }
}
