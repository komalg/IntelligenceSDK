package com.tigerspike.intelligence;

import android.support.annotation.Nullable;

import com.tigerspike.intelligence.exceptions.IntelligenceRequestException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LocationGetGeoFencesTask extends IntelligenceTask {

    private LocationModule mLocation;
    public List<IntelligenceGeofence> geoFences;

    private Double mLongitude;
    private Double mLatitude;
    private Double mRadius;
    private Integer mPageSize;
    private Integer mPageNumber;

    public LocationGetGeoFencesTask(LocationModule location, TaskListener taskListener) {
        super(taskListener);
        mLocation = location;
    }

    public LocationGetGeoFencesTask(LocationModule location,
                                    @Nullable Double longitude,
                                    @Nullable Double latitude,
                                    @Nullable Double radius,
                                    @Nullable Integer pageSize,
                                    @Nullable Integer pageNumber,
                                    TaskListener taskListener) {
        super(taskListener);
        mLocation = location;

        mLongitude = longitude;
        mLatitude = latitude;
        mRadius = radius;
        mPageSize = pageSize;
        mPageNumber = pageNumber;

    }

    @Override
    void execute() throws Exception {
        Response response = mLocation.createGetGeofencesRequest(mLongitude, mLatitude, mRadius, mPageSize, mPageNumber).execute();

        //If this is an error then exception is thrown and code execution is stopped
        handleError(response);

        try {

            JSONObject jsonObject = new JSONObject(response.bodyData());
            JSONArray jsonArray = jsonObject.getJSONArray("Data");

            geoFences = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); ++i) {
                IntelligenceGeofence geoFence = new IntelligenceGeofence(jsonArray.getJSONObject(i));
                geoFences.add(geoFence);
            }

        } catch (JSONException e) {
            throw new IntelligenceRequestException(IntelligenceRequestException.ErrorCode.ParseError, "Could not parse geofences").addCause(e);
        }

    }

}
