package com.n17r_fizmat.kzqrs;

import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by Alisher on 7/4/2016.
 */
public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "b2HNOm6xDZhEkFJTvkEHXmthS7vgu50oLEMXilhb", "NtAqM05DlbxOtVu4yCIpuREyRUAqoDcuR42hDCOy");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
