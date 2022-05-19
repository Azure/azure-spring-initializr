package com.azure.spring.initializr.metadata.scm.push;

import java.util.HashMap;
import java.util.Map;

public class GitPush {

    private Map<String, OAuthApp> oAuthApps = new HashMap<>();
    private final Config config = new Config();

    public static class Config {

        private String initDefaultBranch = "main";

        private final InitCommit initCommit = new InitCommit();

        public String getInitDefaultBranch() {
            return initDefaultBranch;
        }

        public void setInitDefaultBranch(String initDefaultBranch) {
            this.initDefaultBranch = initDefaultBranch;
        }

        public InitCommit getInitCommit() {
            return initCommit;
        }

    }

    public static class InitCommit {
        private String userEmail = "SpringIntegSupport@microsoft.com";
        private String message = "Initial commit from Azure Spring Initializr";

        public String getUserEmail() {
            return userEmail;
        }

        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public Map<String, OAuthApp> getoAuthApps() {
        return oAuthApps;
    }

    public void setoAuthApps(Map<String, OAuthApp> oAuthApps) {
        this.oAuthApps = oAuthApps;
    }

    public Config getConfig() {
        return config;
    }
}
