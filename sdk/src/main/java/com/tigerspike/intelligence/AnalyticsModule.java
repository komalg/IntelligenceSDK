package com.tigerspike.intelligence;

import android.app.Application;
import android.location.Location;
import android.text.TextUtils;

import com.tigerspike.intelligence.exceptions.IntelligenceException;
import com.tigerspike.intelligence.exceptions.IntelligenceInvalidParameterException;
import com.tigerspike.intelligence.exceptions.IntelligenceServerException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;

public final class AnalyticsModule extends IntelligenceModule implements Analytics {

    private static final int MAX_EVENTS_TO_SEND = 100;
    private static final String KEY_STORAGE_EVENT_QUEUE = "eventQueue";
    private static final String TRACK_EVENTS_ENDPOINT = "projects/{PROJECT_ID}/events";


    private DataStore mDataStore;
    private OAuth mOAuth;
    private Configuration mConfiguration;
    private Application mApplication;
    private TaskExecutor mTaskExecutor;

    private Location mLastKnownLocation;
    private Installation mInstallation;
    private RequestURLBuilder mRequestURLBuilder;

    private boolean mIsBusy = false;

    private LinkedList<JSONObject> mEventQueue;

    public AnalyticsModule(Configuration configuration, TaskExecutor taskExecutor, DataStore dataStore, OAuth oAuth, Application application) {

        mConfiguration = configuration;
        mTaskExecutor = taskExecutor;
        mOAuth = oAuth;
        mDataStore = dataStore;
        mApplication = application;

        mRequestURLBuilder = new RequestURLBuilder(configuration);

        mEventQueue = new LinkedList<>();

        // load stored event queue
        String eventQueueString = mDataStore.get(KEY_STORAGE_EVENT_QUEUE);


        if (eventQueueString != null && !eventQueueString.isEmpty()) {

            try {
                JSONArray eventQueueArray = new JSONArray(eventQueueString);
                for (int i = 0; i < eventQueueArray.length(); i++) {
                    mEventQueue.add(eventQueueArray.getJSONObject(i));
                }
            } catch (JSONException exception) {
                // Nothing much we can do here.
            }

        }

    }

    @Override
    void startUp() {
        process(false);
    }

    @Override
    public void trackEvent(AnalyticsEvent analyticsEvent) {

        if (mInstallation == null) {
            mInstallation = new Installation(mApplication);
        }

        // Only set installation ID when it was provided by the back-end, aka when it has a back-end ID.
        /*if (mInstallation.getID() != null) {
            analyticsEvent.setInstallationID(mInstallation.getInstallationID());
        }*/

        if (!TextUtils.isEmpty(Utils.getInstallationId(mDataStore))) {
            analyticsEvent.setInstallationID(Utils.getInstallationId(mDataStore));
        }

        analyticsEvent.setApplicationVersion(mInstallation.getInstalledVersion());
        analyticsEvent.setDeviceType(mInstallation.getModelReference());
        analyticsEvent.setOperatingSystem("Android " + mInstallation.getOperatingSystemVersion());

        analyticsEvent.setApplicationID(mConfiguration.getApplicationID());
        analyticsEvent.setProjectId(mConfiguration.getProjectID());
        analyticsEvent.setUserID(mOAuth.getCurrentUserId());
        analyticsEvent.setLocation(mLastKnownLocation);

        synchronized (this) {
            mEventQueue.add(analyticsEvent.toJSONObject());
        }

        process(false);

    }

    @Override
    public void trackScreenViewed(String screenName, Double timeViewed) {
        trackEvent(new AnalyticsEventScreenViewed(screenName, timeViewed));
    }

    void process(boolean inProgress) {

        if (!inProgress && mIsBusy) {
            return;
        }

        if (mEventQueue.size() == 0) {
            mIsBusy = false;
            return;
        }

        mIsBusy = true;

        mTaskExecutor.addTask(new ProcessQueueTask());

    }

    private void onDone(Boolean progress) {
        if (progress) {
            process(true);
        } else {
            mIsBusy = false;
        }
    }

    private Request createAnalyticsRequest(JSONArray eventsData) {

        URL url = null;

        try {
            url = mRequestURLBuilder.analyticsBaseURL().urlPath(TRACK_EVENTS_ENDPOINT).url();
        } catch (IntelligenceInvalidParameterException ignored) {
        }

        return RequestBuilder.POST(url)
                .authentication(mOAuth.getCurrentAuthenticationToken())
                .body(eventsData.toString())
                .build();

    }

    private void processQueue() throws Exception {

        boolean doContinue = true;

        filterInvalidEventsInQueue();

        while (doContinue) {

            // -- SAVE Current EventQueue --
            JSONArray eventQueueJsonArray;
            synchronized (AnalyticsModule.this) {
                eventQueueJsonArray = new JSONArray(mEventQueue);
            }

            mDataStore.set(KEY_STORAGE_EVENT_QUEUE, eventQueueJsonArray.toString());

            if (eventQueueJsonArray.length() <= 0) {
                doContinue = false;
            } else {

                JSONArray eventsData = new JSONArray();

                for (int eventIndex = 0; eventIndex < MAX_EVENTS_TO_SEND && eventIndex < eventQueueJsonArray.length(); eventIndex++) {
                    eventsData.put(eventQueueJsonArray.opt(eventIndex));
                }

                // Create and execute request
                Response response = createAnalyticsRequest(eventsData).execute();

                // A non success HTTP code would not trigger an exception, but we should treat it as
                // such so that we don't delete events not submitted but where not successfully treated
                // by the backend.
                if (response.isSuccess() && response.exception() == null) {

                    synchronized (AnalyticsModule.this) {
                        for (int i = 0; i < eventsData.length(); i++) {
                            mEventQueue.removeFirst();
                        }
                    }

                } else {

                    // TODO This should be done via tasks. The handleError method code is duplicated within the fromResponse method.
                    IntelligenceServerException intelligenceServerException = IntelligenceServerException.fromResponse(response.code(), response.bodyData());

                    // If we receive "invalid_request" server error response the sent events are wrong / out of date.
                    // We clear the events and continue with the rest of the events list.
                    // If we receive a different server error we throw an exception.
                    if (intelligenceServerException.getServerError().equalsIgnoreCase("invalid_request")) {
                        synchronized (AnalyticsModule.this) {
                            for (int i = 0; i < eventsData.length(); i++) {
                                mEventQueue.removeFirst();
                            }
                        }
                    } else {
                        throw (intelligenceServerException);
                    }

                }

            }

        }

    }

    private void filterInvalidEventsInQueue() {
        synchronized (this) {
            Iterator<JSONObject> iterator = mEventQueue.iterator();
            while (iterator.hasNext()) {
                JSONObject object = iterator.next();
                if (AnalyticsEvent.shouldDropJSONObjectFromQueue(object)) {
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public void setLastKnownLocation(Location location) {
        mLastKnownLocation = location;
    }

    private class ProcessQueueTask extends IntelligenceTask {

        private boolean mSuccess;

        public ProcessQueueTask() {
            super();
        }

        @Override
        void onPostExecute() {
            onDone(mSuccess);
        }

        @Override
        void execute() throws Exception {
            processQueue();
        }

        @Override
        void onError(IntelligenceException intelligenceException) {
            super.onError(intelligenceException);
            onDone(false);
        }
    }


}
