package ro.vodafone.mcare.android.service.tracking;

/**
 * Class handling the tracking of the application.
 *
 * @author Andrei DOLTU
 */

import java.util.HashMap;

import ro.vodafone.mcare.android.application.VodafoneController;

public class TrackingAppMeasurement {

    // new props
    public String prop5;
    public String prop7;
    public String prop8;
    public String prop11;
    public String prop12;
    public String prop15;
    public String prop17;
    public String prop20;
    public String prop24;
    public String prop25;
    public String prop27;
    public String prop29;
    public String prop31;
    public String prop32;
    public String prop42;
    public String prop55;

    //new evars
    public String eVar2;
    public String eVar6;
    public String eVar7;
    public String eVar9;
    public String eVar10;
    public String eVar11;
    public String eVar13;
    public String eVar14;
    public String eVar18;
    public String eVar19;
    public String eVar20;
    public String eVar24;;
    public String eVar27;
    public String eVar39;
    public String eVar46;
    public String eVar47;
    public String eVar49;
    public String eVar61;
    public String eVar63;
    public String eVar73;
    public String eVar81;
    public String eVar82;

    //new mcare events variables
    public String event1;
    public String event3;
    public String event5;
    public String event6;
    public String event7;
    public String event8;
    public String event9;
    public String event10;
    public String event11;
    public String event13;
    public String event16;
    public String event17;
    public String event19;
    public String event65;
    public String event70;

    //retention predefined events
	public String scAdd;
	public String scOpen;

    //AS IS variables
    public String account;
	public boolean ssl;
	public boolean debugTracking;
	public String trackingServer;
	public String channel;
	public String pageName;
	public String hier1;
	public String events;
	public String pageType;

	// asis props
	public String prop6;
	public String prop9;
	public String prop46;
	public String prop16;
	public String prop19;
	public String prop21;
	public String prop28;
	public String prop75;
	public String prop1;
	public String prop3;
	public String prop74;
	public String prop53;
	public String prop13;
	public String prop10;
	public String prop22;
	public String prop23;
	//prop: 66,69,70,71,72,73,13,74
	public String prop66; // Network Operator Name
	public String prop69; // Network tpy
	//public String prop70; //Cell location removed
	public String prop71; // Is Roaming Network
	public String prop72; // Device Software Version
	public String prop73; //  Network Speed

	//asis evars
	public String eVar1;
	public String eVar3;
	public String eVar5;
	public String eVar8;
	public String eVar12;
	public String eVar15;
	public String eVar23;
	public String eVar52;
	public String eVar62;
	public String eVar65;
    //evar: 45,56,71,76,77,78,79,80
    public String eVar45;
    public String eVar56;
    //public String eVar71; cell location removed
    public String eVar76;
    public String eVar77;
    public String eVar78;
    public String eVar79;
    //public String eVar80; location removed

	//declaring contextData for Adobe Analytics
	public static  HashMap<String, Object> contextData = new HashMap<String, Object>();
	private  VodafoneController applicationContext;


	public HashMap<String, Object> getContextData() {
		return contextData;
	}
	public void setContextData(HashMap<String, Object> contextData) {
		this.contextData = contextData;
	}
	public VodafoneController getApplicationContext() {
		return applicationContext;
	}
	public void setApplicationContext(VodafoneController applicationContext) {
		this.applicationContext = applicationContext;
	}

	
}
