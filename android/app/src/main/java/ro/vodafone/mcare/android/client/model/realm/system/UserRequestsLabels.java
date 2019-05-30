package ro.vodafone.mcare.android.client.model.realm.system;

/**
 * Created by alexandrulepadatu on 3/7/18.
 */

public class UserRequestsLabels extends AppLabels
{
    public static String getUserRequestsCardTitle(){
        return getLabelWithPrimaryKey("user_requests_confirmation_card_title","Cereri de la utilizatori");
    }

    public static String getUserRequestsCardSubTitle(){
        return getLabelWithPrimaryKey("user_requests_card_sub_title","Aprobă/Refuză cererile utilizatorilor");
    }

    public static String getUserRequestsPendingRequests(){
        return getLabelWithPrimaryKey("user_requests_pending_requests","În așteptare");
    }

    public static String getUserRequestsAcceptedRequests(){
        return getLabelWithPrimaryKey("user_requests_accepted_requests","Acceptate");
    }

    public static String getUserRequestsDeniedRequests(){
        return getLabelWithPrimaryKey("user_requests_denied_requests","Refuzate");
    }

    public static String getApproveRequestTitle(){
        return getLabelWithPrimaryKey("user_requests_approve_request_title","Aprobă cererea");
    }

    public static String getApproveRequestDescription(){
        return getLabelWithPrimaryKey("user_requests_approve_request_description","Ești sigur că dorești să aprobi cererea?");
    }

    public static String getRejectRequestTitle(){
        return getLabelWithPrimaryKey("user_requests_reject_request_title","Respinge cererea");
    }

    public static String getRejectRequestDescription(){
        return getLabelWithPrimaryKey("user_requests_reject_request_description","Ești sigur că dorești să respingi cererea?");
    }

    public static String getRequestProcessed(){
        return getLabelWithPrimaryKey("user_requests_request_processed","Solicitarea a fost procesată");
    }

    public static String getFraudCaseMessage(){
        return getLabelWithPrimaryKey("user_requests_fraud_case","Ne pare rău, nu poţi aproba cererile primite de la utilizatori. Pentru mai multe detalii, contactează Departamentul de Relaţii cu clienţii.");
    }

    public static String getNoPendingUserRequest(){
        return getLabelWithPrimaryKey("user_requests_reject_no_pending_user_request","Nu ai nicio cerere de la utilizatori activă");
    }

    public static String getNoRejectedUserRequest(){
        return getLabelWithPrimaryKey("user_requests_reject_no_rejected_user_request","Nu ai nicio cerere de la utilizatori refuzată");
    }

    public static String getNoActiveUserRequest(){
        return getLabelWithPrimaryKey("user_requests_reject_no_active_user_request","Nu ai nicio cerere de la utilizatori acceptată");
    }
}
