package ro.vodafone.mcare.android.application.realm;

import android.util.Log;
import android.util.Pair;

import java.util.LinkedList;
import java.util.List;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import ro.vodafone.mcare.android.application.realm.migrations.Drop2Migration;
import ro.vodafone.mcare.android.application.realm.migrations.EbuOffersMigration;
import ro.vodafone.mcare.android.application.realm.migrations.FUPMigration;
import ro.vodafone.mcare.android.application.realm.migrations.GdprMigration;
import ro.vodafone.mcare.android.application.realm.migrations.GreenTaxMigration;
import ro.vodafone.mcare.android.application.realm.migrations.LoyaltyMigration;
import ro.vodafone.mcare.android.application.realm.migrations.MarchCampaignMigration;
import ro.vodafone.mcare.android.application.realm.migrations.PrePaidRoamingMigration;
import ro.vodafone.mcare.android.application.realm.migrations.ProfileMigration;
import ro.vodafone.mcare.android.application.realm.migrations.Sprint11D1Migration;
import ro.vodafone.mcare.android.application.realm.migrations.Sprint12Migration;
import ro.vodafone.mcare.android.application.realm.migrations.Sprint14Migration;
import ro.vodafone.mcare.android.application.realm.migrations.Sprint15Migration;
import ro.vodafone.mcare.android.application.realm.migrations.Sprint16Migration;
import ro.vodafone.mcare.android.application.realm.migrations.SprintDeltaMigration;
import ro.vodafone.mcare.android.application.realm.migrations.TvHierarchyMigration;
import ro.vodafone.mcare.android.utils.D;

public class VFRealmMigration implements RealmMigration {
    private static final List<Pair<Long, RealmMigration>> migrations = new LinkedList<>();
    static {
        migrations.add(new Pair<Long,RealmMigration>(311l, new LoyaltyMigration()));
        migrations.add(new Pair<Long,RealmMigration>(315l, new Drop2Migration()));
        migrations.add(new Pair<Long,RealmMigration>(319l, new FUPMigration()));
        migrations.add(new Pair<Long,RealmMigration>(321l, new MarchCampaignMigration()));
        migrations.add(new Pair<Long, RealmMigration>(325l, new Sprint11D1Migration()));
        migrations.add(new Pair<Long, RealmMigration>(327l, new EbuOffersMigration()));
        migrations.add(new Pair<Long, RealmMigration>(327l, new GreenTaxMigration()));
        migrations.add(new Pair<Long, RealmMigration>(330l, new GdprMigration()));
        migrations.add(new Pair<Long, RealmMigration>(336l, new Sprint12Migration()));
        migrations.add(new Pair<Long, RealmMigration>(338l, new Sprint14Migration()));
        migrations.add(new Pair<Long, RealmMigration>(341l, new Sprint15Migration()));
        migrations.add(new Pair<Long, RealmMigration>(346l, new Sprint16Migration()));
        migrations.add(new Pair<Long, RealmMigration>(348l, new SprintDeltaMigration()));
        migrations.add(new Pair<Long, RealmMigration>(3410l, new PrePaidRoamingMigration()));
        migrations.add(new Pair<Long, RealmMigration>(3411l, new TvHierarchyMigration()));
        migrations.add(new Pair<Long, RealmMigration>(3412l, new ProfileMigration()));
        //register migrations here
    }

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        Log.d("VFRealmMigration", String.format("Migrating from DB v %d to DB v %d", oldVersion, newVersion));
        for(Pair<Long, RealmMigration> migration: migrations){
            if(migration.first.longValue() > oldVersion) {
                migration.second.migrate(realm, oldVersion, newVersion);
            }
        }
    }
    private static void logNewFieldAdded(String table,String fieldName, Class type){
        D.w("Migrating to: "+table+" adding new field: "+fieldName+" class: "+type +" to existing schema");

    }

    @Override
    public int hashCode() {
        return 37;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof VFRealmMigration);
    }
}