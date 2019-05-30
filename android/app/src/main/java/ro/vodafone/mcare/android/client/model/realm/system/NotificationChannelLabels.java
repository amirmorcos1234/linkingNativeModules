package ro.vodafone.mcare.android.client.model.realm.system;

public class NotificationChannelLabels extends AppLabels {

	public static String getDownloadBillChannelName(){
		return  getLabelWithPrimaryKey("notification_channel_name_download_bill","Descărcare factură");
	}

	public static String getDownloadBillChannelDescription(){
		return  getLabelWithPrimaryKey("notification_channel_description_download_bill","Modul prin care My Vodafone descarcă factura pe telefonul tău");
	}

	public static String getChatChannelName(){
		return  getLabelWithPrimaryKey("notification_channel_name_chat","Mesaje Chat");
	}

	public static String getChatChannelDescription(){
		return  getLabelWithPrimaryKey("notification_channel_description_chat","Modul prin care vizualizezi mesajele trimise prin Chat");
	}

}
