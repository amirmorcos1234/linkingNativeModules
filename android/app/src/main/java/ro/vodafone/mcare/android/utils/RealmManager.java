package ro.vodafone.mcare.android.utils;

import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import ro.vodafone.mcare.android.BuildConfig;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.realm.VFRealmMigration;
import ro.vodafone.mcare.android.client.model.realm.system.TimeToLeaveMap;
import ro.vodafone.mcare.android.crypto.StoragePasswordUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Victor Radulescu on 1/9/2017.
 */

public class RealmManager {


    private static final String TAG = "RealmManager";

    public static RealmModel getRealmObject(Class neededClass) {
        RealmModel realmObject = null;
        try {
            Realm realm = getDefaultInstance();
            //realm.beginTransaction();
            realmObject = realm.where(neededClass).findFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return realmObject;
    }

    public static RealmModel getRealmObject(Realm realm, Class neededClass) {
        RealmModel realmObject = null;
        try {
            realmObject = realm.where(neededClass).findFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return realmObject;
    }

    public static RealmResults getAllRealmObject(Class neededClass) {
        RealmResults realmObject = null;
        try {
            Realm realm =getDefaultInstance();
            //realm.beginTransaction();
            realmObject = realm.where(neededClass).findAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return realmObject;
    }

    public static RealmResults getAllRealmObject(Realm realm, Class neededClass) {
        RealmResults realmObject = null;
        try {
            realmObject = realm.where(neededClass).findAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return realmObject;
    }

    public static RealmResults getRealmObjectQuery(Class neededClass, int key) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery query = realm.where(neededClass);
        query = query.equalTo("id", key);
        return query.findAll();
    }

    public static RealmResults getRealmObjectQuery(Realm realm, Class neededClass, int key) {
        RealmQuery query = realm.where(neededClass);
        query = query.equalTo("id", key);
        return query.findAll();
    }

    public static void deleteRealmObjectQuery(Class neededClass, int key) {
        Realm realm = Realm.getDefaultInstance();
        if (!realm.isInTransaction())
            realm.beginTransaction();

        RealmResults results = realm.where(neededClass)
                .equalTo("id", key)
                .findAll();

        results.deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();
    }

    public static Observable<RealmModel> getRealmObjectObservable(Class neededClass) {
        Observable<RealmModel> realmObject = null;
        try {
            Realm realm = getDefaultInstance();
            //realm.beginTransaction();
            realmObject = realm.where(neededClass).findAllAsync().asObservable().subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread()).first();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return realmObject;
    }

    //TODO Code cleanup - gdprKey change it to a generic name. "id" should pe a parameter
    public static Observable getUnmanagedRealmObjectObservableByKey(Class neededClass, int gdprKey){
        Observable realmObject = null;
        try {

            Realm realm = getDefaultInstance();
            //realm.beginTransaction();
            realmObject = realm.where(neededClass).equalTo("id", gdprKey).findAllAsync().asObservable().subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread()).first();

        }catch (Exception e){
            e.printStackTrace();
        }
        return realmObject;
    }

    public static synchronized Realm getDefaultInstance() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
        } catch (Exception e) {
            initRealm(VodafoneController.getInstance());
            realm = Realm.getDefaultInstance();
        }
        return realm;
    }

    /**
     * Initialization of Realm. Use it in application class.
     *
     * @param context of application
     */
    public synchronized static void initRealm(Context context) {
        try {
            try {
                Realm.init(context);
                Realm.setDefaultConfiguration(buildRealmConfiguration(context));
                //try the configuration, not just init it
                Realm.getDefaultInstance().close();
            } catch (Exception e) {
                Crashlytics.logException(new Exception("Could not initialize Realm, deleting DB.", e));
                e.printStackTrace();
                try {
                    final Realm realmInstance = Realm.getDefaultInstance();
                    Realm.deleteRealm(realmInstance.getConfiguration());
                    realmInstance.close();
                } catch (Exception ex) {
//                    Crashlytics.logException(new Exception("Second ", ex));
                    try {
                        final RealmConfiguration configuration = buildRealmConfiguration(context);
                        new File(configuration.getRealmDirectory(), configuration.getRealmFileName()).delete();
                        Realm.init(context);
                    } catch (Exception exc) {
                        exc.printStackTrace();
                        //Crashlytics.logException(new Exception("Third ", exc));
                        //probably file isn't there and reinitialize realm
                        Realm.init(context);
                    }
                }
                Realm.setDefaultConfiguration(buildRealmConfiguration(context));
                //try the configuration, not just init it
                Realm.getDefaultInstance().close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            if (BuildConfig.USE_CRASHLYTICS)
                Crashlytics.logException(ex);
        }
    }

    /**
     * Internal initialization of Realm.
     *
     * @param context of application
     */
    public static RealmConfiguration buildRealmConfiguration(Context context) {
        RealmConfiguration.Builder builder = new RealmConfiguration
                .Builder()
                .encryptionKey(StoragePasswordUtil.getStoragePassword(context.getApplicationContext()))
                .schemaVersion(BuildConfig.VERSION_CODE)
                .migration(new VFRealmMigration());

        if (BuildConfig.DELETE_REALM_IF_MIGRATION_NEEDED) {
//            Crashlytics.logException(new Exception("Delete realm "));
            builder.deleteRealmIfMigrationNeeded();
        }
        return builder.build();
    }

    /**
     Please use this method only for debugging purpose.
     Don't forget to delete the calls before committing.
     */
    public static void checkNumberOfRealmInstances() {
        RealmConfiguration realmConfiguration = Realm.getDefaultConfiguration();
        if(realmConfiguration != null) {
            int globalInstanceCount = Realm.getGlobalInstanceCount(realmConfiguration);
            int localInstanceCount = Realm.getLocalInstanceCount(realmConfiguration);
            Log.d(TAG, "getGlobalInstanceCount: " + globalInstanceCount);
            Log.d(TAG, "getLocalInstanceCount: " + localInstanceCount);
        }

    }

    public static void update(final RealmObject realmObject) {
        Realm realm = getDefaultInstance();
        if (!realm.isInTransaction()) {
            realm.beginTransaction();
        }
        realm.copyToRealmOrUpdate(realmObject);
        realm.commitTransaction();
        realm.close();
    }

    public static void update(Realm realm, final RealmObject realmObject) {
        if (!realm.isInTransaction()) {
            realm.beginTransaction();
        }
        realm.copyToRealmOrUpdate(realmObject);
        realm.commitTransaction();
    }

    /**
     * Beware insert operation don't check for duplication. Set @PrimaryKey for that object to avoid duplications
     *
     * @param realmList
     */
    public static void insert(final List<RealmObject> realmList) {
        if (realmList == null || realmList.isEmpty()) {
            return;
        }
        Realm realm = getDefaultInstance();
        if (!realm.isInTransaction()) {
            realm.beginTransaction();
        }
        realm.copyToRealmOrUpdate(realmList);
        realm.commitTransaction();
        realm.close();
    }

    public static void updateAsync(final RealmObject realmObject) {
        Realm realm = getDefaultInstance();
        if (!realm.isInTransaction()) {

        }
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(realmObject);
            }
        });
        realm.close();
    }

    public static void disconnect() {
        Realm realm = getDefaultInstance();
        realm.close();
    }

    /**
     * Please end transaction, calling disconnect
     */
    public static synchronized Realm startTransaction() {
        Realm realm = getDefaultInstance();
        if (!realm.isInTransaction()) {
            realm.beginTransaction();
        }
        return realm;
    }

    public static synchronized void startTransaction(Realm realm) {
        if (!realm.isInTransaction()) {
            realm.beginTransaction();
        }
    }

    /**
     * Please end transaction, calling disconnect
     */

    public static synchronized void commitTransaction() {
        Realm realm = getDefaultInstance();
        if (realm.isInTransaction()) {
            realm.commitTransaction();
        }
    }

    public static synchronized void commitTransaction(Realm realm) {
        if (realm.isInTransaction()) {
            realm.commitTransaction();
        }
    }

    public static void deleteObjects(Class className) {
        Realm realm = Realm.getDefaultInstance();
        if (!realm.isInTransaction()) {
            realm.beginTransaction();
        }
        realm.delete(className);
        realm.commitTransaction();
        realm.close();
    }

    /**
     * User to clean entity ( realmObject) from memory
     *
     * @param realmObject
     */
    public static void delete(RealmObject realmObject) {
        if (realmObject.isValid()) {
            Realm realm = getDefaultInstance();
            if (!realm.isInTransaction()) {
                realm.beginTransaction();
            }
            realm.delete(realmObject.getClass());
            realm.commitTransaction();
            realm.close();
        }
    }

    public static void delete(Class<? extends RealmObject> className) {

        RealmManager.deleteValues(TimeToLeaveMap.class, TimeToLeaveMap.KEY, className.getName());

        Realm realm = getDefaultInstance();
        if (!realm.isInTransaction()) {
            realm.beginTransaction();
        }
        realm.delete(className);
        realm.commitTransaction();
        realm.close();
    }

    public static void delete(Realm realm, Class<? extends RealmObject> className) {

        RealmManager.deleteValues(TimeToLeaveMap.class, TimeToLeaveMap.KEY, className.getName());

        if (!realm.isInTransaction()) {
            realm.beginTransaction();
        }
        realm.delete(className);
        realm.commitTransaction();
    }

    public static void deleteAfterKey(Class<? extends RealmObject> className, int key) {

        RealmManager.deleteValues(TimeToLeaveMap.class, TimeToLeaveMap.KEY, className.getName() + key);
        deleteRealmObjectQuery(className, key);

    }

    public static void deleteMultiple(Class<? extends RealmObject>[] classArrayList) {
        Realm realm = getDefaultInstance();
        if (!realm.isInTransaction()) {
            realm.beginTransaction();
        }
        for (Class className : classArrayList) {
            safeDelete(realm, className);
        }
        realm.commitTransaction();
        realm.close();
    }

    public static void deleteMultiple(Realm realm, Class<? extends RealmObject>[] classArrayList) {
        if (!realm.isInTransaction()) {
            realm.beginTransaction();
        }
        for (Class className : classArrayList) {
            safeDelete(realm, className);
        }
        realm.commitTransaction();
    }

    public static void deleteValues(Class<? extends RealmObject> className, String fieldName, boolean value) {
        try {
            Realm realm = startTransaction();
            RealmResults rows = realm.where(className).equalTo(fieldName, value).findAll();
            Log.d("RealmManager", String.valueOf(rows.size()));
            rows.deleteAllFromRealm();
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void deleteValues(Realm realm, Class<? extends RealmObject> className, String fieldName, boolean value) {
        try {
            realm = startTransaction();
            RealmResults rows = realm.where(className).equalTo(fieldName, value).findAll();
            Log.d("RealmManager", String.valueOf(rows.size()));
            rows.deleteAllFromRealm();
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void deleteValues(Class<? extends RealmObject> className, String fieldName, String value) {
        try {
            Realm realm = startTransaction();
            RealmResults rows = realm.where(className).equalTo(fieldName, value).findAll();
            Log.d("RealmManager", String.valueOf(rows.size()));
            rows.deleteAllFromRealm();
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void safeDelete(Realm realm, Class<? extends RealmObject> realmClass) {
        try {
            realm.delete(realmClass);
        } catch (Exception e) {
            Log.e("RealManager", "delete error " + e.getMessage());
        }
    }

    public static RealmObject getRealmObjectAfterStringField(Class<? extends RealmObject> realmClass,
                                                             String key,
                                                             String value) {
        Realm realm = getDefaultInstance();
        RealmObject realmObject = null;
        try {
            realmObject = realm.where(realmClass).equalTo(

                    key, value).findFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return realmObject;
    }

    public static RealmObject getRealmObjectAfterStringField(Realm realm,
                                                             Class<? extends RealmObject> realmClass,
                                                             String key,
                                                             String value) {
        RealmObject realmObject = null;
        try {
            realmObject = realm.where(realmClass).equalTo(

                    key, value).findFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return realmObject;
    }

    public static RealmObject getRealmObjectContainsStringField(Class<? extends RealmObject> realmClass,
                                                                String key,
                                                                String value) {
        Realm realm = Realm.getDefaultInstance();
        RealmObject realmObject = null;
        try {
            realmObject = realm.where(realmClass).contains(key, value).findFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return realmObject;
    }

    public static RealmObject getRealmObjectContainsStringField(Realm realm,
                                                                Class<? extends RealmObject> realmClass,
                                                                String key,
                                                                String value) {
        RealmObject realmObject = null;
        try {
            realmObject = realm.where(realmClass).contains(key, value).findFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return realmObject;
    }

    public static RealmObject getRealmObjectAfterLongField(Class<? extends RealmObject> realmClass,
                                                           String key,
                                                           long value) {
        Realm realm = getDefaultInstance();
        RealmObject realmObject = null;
        try {
            realmObject = realm.where(realmClass).equalTo(key, value).findFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return realmObject;
    }

    public static RealmObject getRealmObjectAfterStringFieldThatContains(Class<? extends RealmObject> realmClass,
                                                                         String key,
                                                                         String value) {
        Realm realm = getDefaultInstance();
        RealmObject realmObject = null;
        try {
            realmObject = realm.where(realmClass).contains(key, value).findFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return realmObject;
    }

    public static RealmModel getUnManagedRealmObject(Realm realm, RealmObject realmObject) {
        RealmModel unManagedRealmObject = null;
        try {
            if(realmObject != null) {
                unManagedRealmObject = realm.copyFromRealm(realmObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return unManagedRealmObject;

    }

    public static RealmModel getUnManagedRealmObject(Realm realm, Class neededClass) {
        RealmModel managedRealmObject = null;
        RealmModel unManagedRealmObject = null;

        try {
            managedRealmObject = realm.where(neededClass).findFirst();
            if(managedRealmObject != null) {
                unManagedRealmObject = realm.copyFromRealm(managedRealmObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return unManagedRealmObject;

    }
}