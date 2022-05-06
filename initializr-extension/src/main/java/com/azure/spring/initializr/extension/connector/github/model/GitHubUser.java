package com.azure.spring.initializr.extension.connector.github.model;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.Date;

public class GitHubUser {
    private String login;
    private int id;
    @JsonAlias("node_id")
    private String nodeId;
    @JsonAlias("avatar_url")
    private String avatarUrl;
    @JsonAlias("gravatar_id")
    private String gravatarId;
    private String url;
    @JsonAlias("html_url")
    private String htmlUrl;
    @JsonAlias("followers_url")
    private String followersUrl;
    @JsonAlias("following_url")
    private String followingUrl;
    @JsonAlias("gists_url")
    private String gistsUrl;
    @JsonAlias("starred_url")
    private String starredUrl;
    @JsonAlias("subscriptions_url")
    private String subscriptionsUrl;
    @JsonAlias("organizations_url")
    private String organizationsUrl;
    @JsonAlias("repos_url")
    private String reposUrl;
    @JsonAlias("events_url")
    private String eventsUrl;
    @JsonAlias("received_events_url")
    private String receivedEventsUrl;
    private String type;
    @JsonAlias("site_admin")
    private boolean siteAdmin;
    private String name;
    private String company;
    private String blog;
    private String location;
    private String email;
    private boolean hireable;
    private String bio;
    @JsonAlias("twitter_username")
    private String twitterUsername;
    @JsonAlias("public_repos")
    private int publicRepos;
    @JsonAlias("public_gists")
    private int publicGists;
    private int followers;
    private int following;
    @JsonAlias("created_at")
    private Date createdAt;
    @JsonAlias("updated_at")
    private Date updatedAt;

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setGravatarId(String gravatarId) {
        this.gravatarId = gravatarId;
    }

    public String getGravatarId() {
        return gravatarId;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setFollowersUrl(String followersUrl) {
        this.followersUrl = followersUrl;
    }

    public String getFollowersUrl() {
        return followersUrl;
    }

    public void setFollowingUrl(String followingUrl) {
        this.followingUrl = followingUrl;
    }

    public String getFollowingUrl() {
        return followingUrl;
    }

    public void setGistsUrl(String gistsUrl) {
        this.gistsUrl = gistsUrl;
    }

    public String getGistsUrl() {
        return gistsUrl;
    }

    public void setStarredUrl(String starredUrl) {
        this.starredUrl = starredUrl;
    }

    public String getStarredUrl() {
        return starredUrl;
    }

    public void setSubscriptionsUrl(String subscriptionsUrl) {
        this.subscriptionsUrl = subscriptionsUrl;
    }

    public String getSubscriptionsUrl() {
        return subscriptionsUrl;
    }

    public void setOrganizationsUrl(String organizationsUrl) {
        this.organizationsUrl = organizationsUrl;
    }

    public String getOrganizationsUrl() {
        return organizationsUrl;
    }

    public void setReposUrl(String reposUrl) {
        this.reposUrl = reposUrl;
    }

    public String getReposUrl() {
        return reposUrl;
    }

    public void setEventsUrl(String eventsUrl) {
        this.eventsUrl = eventsUrl;
    }

    public String getEventsUrl() {
        return eventsUrl;
    }

    public void setReceivedEventsUrl(String receivedEventsUrl) {
        this.receivedEventsUrl = receivedEventsUrl;
    }

    public String getReceivedEventsUrl() {
        return receivedEventsUrl;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setSiteAdmin(boolean siteAdmin) {
        this.siteAdmin = siteAdmin;
    }

    public boolean getSiteAdmin() {
        return siteAdmin;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompany() {
        return company;
    }

    public void setBlog(String blog) {
        this.blog = blog;
    }

    public String getBlog() {
        return blog;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setHireable(boolean hireable) {
        this.hireable = hireable;
    }

    public boolean getHireable() {
        return hireable;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getBio() {
        return bio;
    }

    public void setTwitterUsername(String twitterUsername) {
        this.twitterUsername = twitterUsername;
    }

    public String getTwitterUsername() {
        return twitterUsername;
    }

    public void setPublicRepos(int publicRepos) {
        this.publicRepos = publicRepos;
    }

    public int getPublicRepos() {
        return publicRepos;
    }

    public void setPublicGists(int publicGists) {
        this.publicGists = publicGists;
    }

    public int getPublicGists() {
        return publicGists;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public int getFollowing() {
        return following;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
}

