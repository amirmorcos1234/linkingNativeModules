package ro.vodafone.mcare.android.client;


import android.os.Build;
import android.util.Log;

import ro.vodafone.mcare.android.BuildConfig;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;

public class Hosts {

	//Set up environment here
	private static Environment environment = BuildConfig.SERVER_ENVIRONMENT;
//	private static Environment environment = Environment.DEV;

	//TODO: DO NOT Commit to Git or build in PET or PROD releases
	//workaround only for development purpose
	public static String hardcodedSeamlessMsisdn = null; //default null, Seamless logic is applied
	//public static String hardcodedSeamlessMsisdn = "40737390870"; //postpaid   - default null, Seamless logic is applied
	//public static String hardcodedSeamlessMsisdn = "40720492986"; //prepaid
	//DEV: public static String seamlessWorkaroundMsisdn = "40724283218"; //alinsec1
	//PET: public static String seamlessWorkaroundMsisdn = "40752088449"; //tahayari

	public static String getHost() {
		// Log.e("HOST", VodafoneController.getInstance().getString(environment.host));
		return VodafoneController.getInstance().getString(environment.host);
	}

	public static String getHostNoHttps() {
		//Log.e("HOST", VodafoneController.getInstance().getString(environment.host));
		String host = VodafoneController.getInstance().getString(environment.host);
		return host.replaceFirst("https://", "http://") + "/";
		//return VodafoneController.getInstance().getString(environment.host);
	}

	public static String getJustHost() {

		String host = VodafoneController.getInstance().getString(environment.host);
		return host.contains("https") ?
						host.replaceFirst("https://", "") :
						host.replaceFirst("http://", "");
		//return VodafoneController.getInstance().getString(environment.host);
	}

	public static boolean isHostWithSSL() {
		String host = VodafoneController.getInstance().getString(environment.host);
		return host.contains("https");
	}

	public static String getAutoHost() {
		Log.e("auto_host", VodafoneController.getInstance().getString(environment.auto_host));
		return VodafoneController.getInstance().getString(environment.auto_host);
	}

	public static String[] getHostCertificates() {
		return BuildConfig.HOST_CERTIFICATES;
	}

	public static String[] getAutoHostCertificates() {
		return BuildConfig.AUTH_CERTIFICATES;
	}

	public static Environment getEnviroment() {
		return environment;
	}


  /*

    public static String getCookieInitialDomain() {
        return VodafoneApplication.getInstance().getString(environment.cookieInitialDomain);
    }
    public static String getCookieDomain() {
        return VodafoneApplication.getInstance().getString(environment.cookieDomain);
    }*/

    public static String getCookieDomain() {
        return VodafoneController.getInstance().getString(environment.cookieDomain);
    }

    public static enum Environment {

        LOCAL(R.string.host_local, R.string.cookie_domain_st, R.string.cookie_initial_domain_local, R.string.host_local),
        DEV(R.string.host_dev, R.string.cookie_domain_dev, R.string.cookie_initial_domain_dev, R.string.host_dev),
		DEV2(R.string.host_dev2, R.string.cookie_domain_dev2, R.string.cookie_initial_domain_dev2, R.string.host_dev2),
        DEV_MOCK(R.string.host_dev_mock, R.string.cookie_domain_dev, R.string.cookie_initial_domain_dev, R.string.host_dev_mock),
        ST1(R.string.host_st, R.string.cookie_domain_st, R.string.cookie_initial_domain_st, R.string.host_st),
        ST2(R.string.host_st2, R.string.cookie_domain_st2, R.string.cookie_initial_domain_st2, R.string.host_st2),
        ST3(R.string.host_st3, R.string.cookie_domain_st2, R.string.cookie_initial_domain_st3, R.string.host_st3),
        ST4(R.string.host_st4, R.string.cookie_domain_st2, R.string.cookie_initial_domain_st4, R.string.host_st4),
        PET(R.string.host_pet, R.string.cookie_domain_pet, R.string.cookie_initial_domain_pet, R.string.auto_pet),
        PET_OT(R.string.host_pet_ot, R.string.cookie_domain_pet_ot, R.string.cookie_initial_domain_pet_ot, R.string.auto_pet_ot),
        PET_OT_AUTH(R.string.host_pet_ot_auth, R.string.cookie_domain_pet_ot, R.string.cookie_initial_domain_pet_ot, R.string.auto_pet_ot),
        UAT(R.string.host_uat, R.string.cookie_domain_uat, R.string.cookie_initial_domain_uat, R.string.auto_uat),
        PROD(R.string.host_prod, R.string.cookie_domain_prod, R.string.cookie_initial_domain_prod, R.string.auto_prod),
        PROD_AUTH(R.string.host_prod_auth, R.string.cookie_domain_prod_auth, R.string.cookie_initial_domain_prod_auth, R.string.auto_prod);

        private int host;
        private int cookieInitialDomain;
        private int cookieDomain;
        private int auto_host;

        private Environment(int host, int cookieDomain, int cookieInitialDomain, int auto_host) {
            this.cookieInitialDomain = cookieInitialDomain;
            this.host = host;
            this.cookieDomain = cookieDomain;
            this.auto_host = auto_host;
        }

    }


}
