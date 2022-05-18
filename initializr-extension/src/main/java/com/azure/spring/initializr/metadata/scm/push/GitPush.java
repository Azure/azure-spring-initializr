package com.azure.spring.initializr.metadata.scm.push;

import java.util.HashMap;
import java.util.Map;

public class GitPush {

    private Map<String, OAuthApp> oAuthApps = new HashMap<>();

    private String gitPushInitEmail = "SpringIntegSupport@microsoft.com";

    private String gitPushInitMessage = "Initial commit from Azure Spring Initializr";

    private String gitPushInitBranch = "main";

    public String getGitPushInitEmail() {
        return gitPushInitEmail;
    }

    public void setGitPushInitEmail(String gitPushInitEmail) {
        this.gitPushInitEmail = gitPushInitEmail;
    }

    public String getGitPushInitMessage() {
        return gitPushInitMessage;
    }

    public void setGitPushInitMessage(String gitPushInitMessage) {
        this.gitPushInitMessage = gitPushInitMessage;
    }

    public String getGitPushInitBranch() {
        return gitPushInitBranch;
    }

    public void setGitPushInitBranch(String gitPushInitBranch) {
        this.gitPushInitBranch = gitPushInitBranch;
    }

    public Map<String, OAuthApp> getoAuthApps() {
        return oAuthApps;
    }

    public void setoAuthApps(Map<String, OAuthApp> oAuthApps) {
        this.oAuthApps = oAuthApps;
    }
}
