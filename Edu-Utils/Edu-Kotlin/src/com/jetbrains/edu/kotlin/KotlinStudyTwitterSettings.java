package com.jetbrains.edu.kotlin;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("MethodMayBeStatic")
@State(name = "KotlinStudyTwitterSettings", storages = @Storage("kotlin_study_twitter_settings.xml"))
class KotlinStudyTwitterSettings implements PersistentStateComponent<KotlinStudyTwitterSettings.State> {

    private State myState = new State();


    public static class State {
        public boolean askToTweet = true;
        public String accessToken = "";
        public String tokenSecret = "";
    }

    static KotlinStudyTwitterSettings getInstance(@NotNull final Project project) {
        return ServiceManager.getService(project, KotlinStudyTwitterSettings.class);
    }
    @Nullable
    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(State state) {
        myState = state;
    }

    boolean askToTweet() {
        return myState.askToTweet;
    }

    void setAskToTweet(final boolean askToTweet) {
        myState.askToTweet = askToTweet;
    }

    @NotNull
    String getAccessToken() {
        return myState.accessToken;
    }

    void setAccessToken(@NotNull String accessToken) {
        myState.accessToken = accessToken;
    }

    @NotNull
    String getTokenSecret() {
        return myState.tokenSecret;
    }

    void setTokenSecret(@NotNull String tokenSecret) {
        myState.tokenSecret = tokenSecret;
    }
}
