package group.tonight.workbooksdk;


import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CustomActivityOnCrash.install(this);
    }
}
