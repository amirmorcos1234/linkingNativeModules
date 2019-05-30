package ro.vodafone.mcare.android.client.model.stores;

/**
 * Created by Bogdan Marica on 7/10/2017.
 */

public class VodafoneJsonShop {


    String n;  // =        "n": "Magazin Vodafone - Bucuresti Hq",
    String c;  // =        "c": "Bucuresti",
    String d;  // =        "d": "Sector 2",
    String a;  // =        "a": "Str.Barbu Vacarescu, nr.201, Cladirea Globalworth Tower",
    String p;  // =        "p": "Luni-Vineri 9-19, S&#226;mbata-Duminica &#238;nchis",
    String la; // =        "la": "44.4794",
    String lo; // =        "lo": "26.1022",
    String s;  // =        "s": "1",
    String obs;// =        "obs": ""

    double distance;

    @Override
    public String toString() {
        return "n=" + n + "~~~" + "c=" + c + "~~~" + "d=" + d + "~~~" + "a=" + a + "~~~" + "p=" + p + "~~~" + "la=" + la + "~~~" + "lo=" + lo + "~~~" + "s=" + s + "~~~" + "obs=" + obs + "~~~" + "distance=" + getDistanceToUserInKm();
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public String getLa() {
        return la;
    }

    public void setLa(String la) {
        this.la = la;
    }

    public String getLo() {
        return lo;
    }

    public void setLo(String lo) {
        this.lo = lo;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public double getDistanceToUser() {
        return distance;
    }

    public VodafoneJsonShop setDistanceToUser(double distance) {
        this.distance = distance;
        return this;
    }

    public String getDistanceToUserInKm() {
        return String.format("%.2f", (distance / 1000));
//        return String.format( new Locale("english"),"%.2f", (distance/1000) );
    }
}
