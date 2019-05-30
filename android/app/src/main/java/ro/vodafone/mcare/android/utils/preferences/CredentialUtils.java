package ro.vodafone.mcare.android.utils.preferences;

import android.os.Build;

import devliving.online.securedpreferencestore.SecuredPreferenceStore;

import static ro.vodafone.mcare.android.client.model.realm.UserProfile.PASSWORD;
import static ro.vodafone.mcare.android.client.model.realm.UserProfile.USER_NAME;

/**
 * Created by Victor Radulescu on 1/8/2018.
 */

public class CredentialUtils {

    public  static void saveUsername(String username){
        if(Build.VERSION.SDK_INT >= 24 ) {
            SecuredPreferenceStore prefStore = SecuredPreferenceStore.getSharedInstance();
            prefStore.edit().putString(USER_NAME, username).apply();
        }
    }

  public  static void savePassword(String password){
      if(Build.VERSION.SDK_INT >= 24 ) {
          SecuredPreferenceStore prefStore = SecuredPreferenceStore.getSharedInstance();
          prefStore.edit().putString(PASSWORD, password).apply();
      }
    }

  public  static String getUsername(){
      if(Build.VERSION.SDK_INT >= 24 ) {
          SecuredPreferenceStore prefStore = SecuredPreferenceStore.getSharedInstance();
          return prefStore.getString(USER_NAME, null);
      }else{
          return null;
      }
    }

  public  static String getPassword(){
      if(Build.VERSION.SDK_INT >= 24 ) {
          SecuredPreferenceStore prefStore = SecuredPreferenceStore.getSharedInstance();
          return prefStore.getString(PASSWORD, null);
      }else{
          return null;
      }
    }

    public static  void clearCredentials(){
        if(Build.VERSION.SDK_INT >= 24 ) {
            SecuredPreferenceStore preferences = SecuredPreferenceStore.getSharedInstance();
            preferences.edit().remove(USER_NAME).remove(PASSWORD).commit();
        }else {
            return;
        }
    }


    public static boolean haveValidCredentials() {
        return getUsername() != null && getPassword() != null;
    }
}
