package ro.vodafone.mcare.android.application.realm.migrations;


import com.crashlytics.android.Crashlytics;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import ro.vodafone.mcare.android.utils.D;

/**
 * Created by Victor Radulescu on 12/21/2017.
 */

public abstract class  BaseMigration implements RealmMigration {

    protected boolean showCrashLogs = false;

    protected void logCrash(Class className, boolean succes) {
        if (showCrashLogs) {
            Crashlytics.logException(new Exception(className + (succes ? " trying to create or add (no error)" : "failed maybe already exists")));
        }
    }

    protected static void logNewFieldAdded(String table, String fieldName, Class type) {
        D.w("Migrating to: " + table + " adding new field: " + fieldName + " class: " + type + " to existing schema");

    }

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        try{
            safeMigrate(realm,oldVersion,newVersion);
        }catch (Exception e){
            Crashlytics.logException(e);
            Crashlytics.logException(new Exception("Old migration failed version "+oldVersion+". New version "+newVersion));
        }
    }
    public abstract void safeMigrate(DynamicRealm realm, long oldVersion, long newVersion);
}