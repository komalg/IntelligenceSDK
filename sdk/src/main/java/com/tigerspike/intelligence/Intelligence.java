package com.tigerspike.intelligence;

import android.app.Application;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tigerspike.intelligence.exceptions.IntelligenceConfigurationException;
import com.tigerspike.intelligence.exceptions.IntelligenceException;
import com.tigerspike.intelligence.exceptions.IntelligenceRequestException;


final public class Intelligence {

    private static final int MAX_RETRY_COUNT = 3;
    private Application mApplication;
    private Configuration mConfiguration;
    private OAuth mOAuth;
    private TaskExecutor mTaskExecutor;

    public static DefaultRequestSecurityPolicy defaultRequestSecurityPolicy;

    final public Identity identity;
    final public Location location;
    final public Analytics analytics;

    private boolean mStart = false;
    private boolean mIsStarted = false;
    private OnStartUpListener mOnStartUpListener;
    private TimerActivityLifecycleCallbacks mActivityLifecycleCallback;

    /**
     * Convenience constructor, tries to automatic configure the Intelligence instance by looking for setting in a .json file in assets (e.g):
     * <p/>
     * Example: intelligence.json in /assets/
     * {"region":"us","client_id":"XYZ","client_secret":"XYZ","application_id":"34234","project_id":"34234"}
     *
     * @param application  Application object
     * @param jsonFileName Path of intelligence json configuration file located in assets folder
     * @throws IntelligenceConfigurationException
     */
    public Intelligence(Application application, String jsonFileName) throws IntelligenceConfigurationException {
        this(application, Configuration.createFromJSON(application, jsonFileName));
    }

    /**
     * Instantiates Intelligence instance with the configuration passed by the Configuration object
     *
     * @param application   Application object
     * @param configuration Intelligence configuration object
     * @throws IntelligenceConfigurationException
     */
    public Intelligence(Application application, Configuration configuration) throws IntelligenceConfigurationException {

        mApplication = application;

        mConfiguration = new Configuration(configuration);

        if (mConfiguration.hasMissingProperty()) {
            throw (new IntelligenceConfigurationException(IntelligenceConfigurationException.ErrorCode.MissingProperty));
        }

        DataStore dataStore = new SharedPreferencesDataStore(mApplication);

        // Create Authentication class
        mOAuth = new OAuth(mConfiguration, dataStore);

        // Create Task Executor
        mTaskExecutor = new TaskExecutor(mApplication, mOAuth);
        mTaskExecutor.addNetworkChangeListener(mOnNetworkChangeListener);

        identity = new IdentityModule(mConfiguration, mTaskExecutor, dataStore, mOAuth, mApplication);
        analytics = new AnalyticsModule(mConfiguration, mTaskExecutor, dataStore, mOAuth, mApplication);
        location = new LocationModule(mConfiguration, mTaskExecutor, dataStore, mOAuth, mApplication, analytics);

        // Register to lifecycle callbacks.
        mActivityLifecycleCallback = new TimerActivityLifecycleCallbacks(analytics, dataStore);
        mApplication.registerActivityLifecycleCallbacks(mActivityLifecycleCallback);

        // Create defaultRequestSecurityPolicy
        defaultRequestSecurityPolicy = new DefaultRequestSecurityPolicy(mConfiguration.getCertificateTrustPolicy(), mConfiguration.getEnvironment());
        RequestBuilder.setRequestSecurityPolicy(defaultRequestSecurityPolicy);

    }

    private TaskExecutor.OnNetworkChangeListener mOnNetworkChangeListener = new TaskExecutor.OnNetworkChangeListener() {
        @Override
        public void onNetworkLost() {

        }

        @Override
        public void onNetworkDetected() {
            if (mIsStarted) {
                ((AnalyticsModule) analytics).process(false);
                ((LocationModule) location).updateGeofences();
            } else if (mStart) {
                startUp(null);
            }
        }
    };

    /**
     * @return Returns a copy of the configuration.
     */
    public Configuration getConfiguration() {
        return new Configuration(mConfiguration);
    }

    /**
     * Validates configuration and initializes the Intelligence instance.
     *
     * @param onStartUpListener Listener that will be triggered after initializing
     */
    public void startUp(@Nullable final OnStartUpListener onStartUpListener) {
        mOnStartUpListener = onStartUpListener;

        if (!mIsStarted) {
            mStart = true;

            if (!mTaskExecutor.hasConnection()) {
                if (onStartUpListener != null) {
                    onStartUpListener.onStartup(new IntelligenceRequestException(IntelligenceRequestException.ErrorCode.InternetOfflineError, "No network available, auto starting when available"));
                }
                return;
            }

            mTaskExecutor.addTask(new IntelligenceStartupTask(mOAuth, mConfiguration, mOnStartUpFinishedListener));
        } else {
            if (onStartUpListener != null) {
                onStartUpListener.onStartup(null);
            }
        }
    }

    private final IntelligenceTask.TaskListener mOnStartUpFinishedListener = new IntelligenceTask.TaskListener() {

        @Override
        public void onTaskFinish(IntelligenceTask intelligenceTask) {
            ((IntelligenceModule) analytics).startUp();
            ((IntelligenceModule) identity).startUp();
            ((IntelligenceModule) location).startUp();


            analytics.trackEvent(new AnalyticsEventApplicationOpened(String.valueOf(mConfiguration.getApplicationID())));

            mIsStarted = true;

            if (mOnStartUpListener != null) {
                mOnStartUpListener.onStartup(null);
            }
        }

        @Override
        public void onTaskError(IntelligenceTask intelligenceTask, IntelligenceException e) {
            Log.e(Intelligence.class.getName(), "Could not startUp Intelligence Instance");

            if (mOnStartUpListener != null) {
                mOnStartUpListener.onStartup(e);
            }
        }

    };

    /**
     * Cleans up Intelligence class and modules.
     */
    public void shutdown() {
        mApplication.unregisterActivityLifecycleCallbacks(mActivityLifecycleCallback);
        ((IntelligenceModule) analytics).shutdown();
        ((IntelligenceModule) identity).shutdown();
        ((IntelligenceModule) location).shutdown();
        mTaskExecutor.removeNetworkChangeListener(mOnNetworkChangeListener);
        mTaskExecutor.shutdown();
    }

    // Listener interfaces
    public interface OnStartUpListener {
        void onStartup(IntelligenceException intelligenceException);
    }


}
