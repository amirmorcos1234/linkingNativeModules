package ro.vodafone.mcare.android.service.tracking.adobe.target.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;

/**
 * Created by Bivol Pavel on 21.04.2018.
 */

public class AdobeMockService {

    private List<String> responses;
    private Random randomGenerator;

    private static final String RESPONSE_1 = "[\n" +
            "  {\n" +
            "    \"element\": \"nudge\",\n" +
            "    \"elementID\": \"002\",\n" +
            "    \"Xbutton\": \"show\",\n" +
            "    \"title\": \"Ne-ar plăcea să ne ajuţi cu recomandarea ta \",\n" +
            "    \"content\": \"Te rugăm, ai vrea să ne răspunzi la câteva întrebări?\",\n" +
            "    \"button1display\": \"hide\",\n" +
            "    \"button1label\": \"Nu acum\",\n" +
            "    \"button1urltype\": \"intern\",\n" +
            "    \"button1type\": \"secondary\",\n" +
            "    \"button1action\": \"http://m.vodafone.ro/vfrointegration/web/jsp/mcareRedirect.jsp?page_id=/new-mcare/extraoptiuni/index.htm\",\n" +
            "    \"button2label\": \"Sigur, vreau sa ajut\",\n" +
            "    \"button2urltype\": \"close\",\n" +
            "    \"button2type\": \"secondary\",\n" +
            "    \"button2action\": \"closeNudge\",\n" +
            "    \"recurrence\": \"5\",\n" +
            "    \"recurrenceTTL\": \"120\",\n" +
            "    \"timetodisplay\": \"3\"\n" +
            "  },\n" +
            "   {\n" +
            "    \"element\": \"Vov\",\n" +
            "    \"elementID\": \"001\",\n" +
            "    \"title\": \"Buna ziua\",\n" +
            "    \"content\": \"Avem oferte noi pentru tine\",\n" +
            "    \"priority\": \"10\",\n" +
            "    \"TTL\": \"session\",\n" +
            "    \"button1display\": \"hide\",\n" +
            "    \"button1label\": \"Vezi Oferte\",\n" +
            "    \"button1urltype\": \"Vezi Oferte\",\n" +
            "    \"button1action\": \"http://m.vodafone.ro/vfrointegration/web/jsp/mcareRedirect.jsp?page_id=/new-mcare/extraoptiuni/index.htm\",\n" +
            "    \"button2label\": \"Ok am inteles\",\n" +
            "    \"button2urltype\": \"close\",\n" +
            "    \"button2action\": \"closeVov\",\n" +
            "    \"recurrence\": \"300\",\n" +
            "    \"recurrenceTTL\": \"120\",\n" +
            "    \"timetodisplay\": \"3\"\n" +
            "  }\n" +
            "]";

    private static final String RESPONSE_3 = "[\n" +
            "  {\n" +
            "    \"element\": \"Vov\",\n" +
            "    \"elementID\": \"001\",\n" +
            "    \"title\": \"Buna ziua\",\n" +
            "    \"content\": \"Avem oferte noi pentru tine\",\n" +
            "    \"priority\": \"10\",\n" +
            "    \"TTL\": \"session\",\n" +
            "    \"button1display\": \"hide\",\n" +
            "    \"button1label\": \"Vezi Oferte\",\n" +
            "    \"button1urltype\": \"Vezi Oferte\",\n" +
            "    \"button1action\": \"http://m.vodafone.ro/vfrointegration/web/jsp/mcareRedirect.jsp?page_id=/new-mcare/extraoptiuni/index.htm\",\n" +
            "    \"button2label\": \"Ok am inteles\",\n" +
            "    \"button2urltype\": \"close\",\n" +
            "    \"button2action\": \"closeVov\",\n" +
            "    \"recurrence\": \"4\",\n" +
            "    \"recurrenceTTL\": \"120\",\n" +
            "    \"timetodisplay\": \"3\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"element\": \"Toast\",\n" +
            "    \"elementID\": \"004\",\n" +
            "    \"Type\": \"success\",\n" +
            "    \"content\": \"Welcome, we have cookies\",\n" +
            "    \"recurrence\": \"6\",\n" +
            "    \"recurrenceTTL\": \"120\",\n" +
            "    \"timetodisplay\": \"3\"\n" +
            "  }\n" +
            "]";

    private static final String RESPONSE_4 = "{ \n" +
            "\"element\":\"Vov\",\n" +
            "\"elementID\":\"001\",\n" +
            "\"title\":\"Buna ziua\",\n" +
            "\"content\":\"Avem oferte noi pentru tine\",\n" +
            "\"priority\":\"10\",\n" +
            "\"TTL\":\"session\",\n" +
            "\"button1display\":\"show\",\n" +
            "\"button1label\":\"Vezi Oferte\",\n" +
            "\"button1urltype\":\"close\",\n" +
            "\"button1action\":\"http://google.com\",\n" +
            "\"button2display\":\"hide\",\n" +
            "\"button2label\":\"Ok am inteles\",\n" +
            "\"button2urltype\":\"close\",\n" +
            "\"button2action\":\"closeVov\",\n" +
            "\"recurrence\":\"1000\",\n" +
            "\"recurrenceTTL\":\"120\",\n" +
            "\"timetodisplay\":\"3\"\n" +
            "}";

    private static final String RESPONSE_5 = "{ \n" +
            "\"element\":\"nudge\", \n" +
            "\"elementID\":\"002\", \n" +
            "\"Xbutton\":\"show\", \n" +
            "\"title\":\"Ne-ar plăcea să ne ajuţi cu recomandarea ta \",\n" +
            "\"content\":\"Te rugăm, ai vrea să ne răspunzi la câteva întrebări?\",\n" +
            "\"button1display\":\"show\",\n" +
            "\"button1label\":\"Nu acum\",\n" +
            "\"button1urltype\":\"intern\",\n" +
            "\"button1type\":\"secondary\",\n" +
            "\"button1action\":\"http://m.vodafone.ro/vfrointegration/web/jsp/mcareRedirect.jsp?page_id=/new-mcare/extraoptiuni/index.htm\",\n" +
            "\"button2display\":\"hide\",\n" +
            "\"button2label\":\"Sigur, vreau sa ajut\",\n" +
            "\"button2urltype\":\"close\",\n" +
            "\"button2type\":\"secondary\",\n" +
            "\"button2action\":\"closeNudge\",\n" +
            "\"recurrence\":\"1000\",\n" +
            "\"recurrenceTTL\":\"120\",\n" +
            "\"timetodisplay\":\"3\"\n" +
            "}";

    private static final String RESPONSE_6 = "{ \n" +
            "\"element\":\"Toast\", \n" +
            "\"elementID\":\"004\", \n" +
            "\"Type\":\"success\", \n" +
            "\"content\":\"Welcome, we have cookies\",\n" +
            "\"recurrence\":\"100\",\n" +
            "\"recurrenceTTL\":\"120\",\n" +
            "\"timetodisplay\":\"3\"\n" +
            "}";

    private static final String RESPONSE_7 = "[\n" +
            "  {\n" +
            "    \"element\": \"Overlay2\",\n" +
            "    \"elementID\": \"006\",\n" +
            "    \"Xbutton\": \"show\",\n" +
            "    \"title\": \"Titlu\",\n" +
            "    \"content\": \"Intră şi activează promoţia cea mai minunată\",\n" +
            "    \"bannerimage\": \"source/image.png\",\n" +
            "    \"bannerurl\": \"http://m.vodafone.ro/vfrointegration/web/jsp/mcareRedirect.jsp?page_id=/new-mcare/extraoptiuni/index.htm\",\n" +
            "    \"button1display\": \"show\",\n" +
            "    \"button1label\": \"Accepta\",\n" +
            "    \"button1urltype\": \"intern\",\n" +
            "    \"button1type\": \"primary\",\n" +
            "    \"button1action\": \"http://m.vodafone.ro/vfrointegration/web/jsp/mcareRedirect.jsp?page_id=/new-mcare/extraoptiuni/index.htm\",\n" +
            "    \"button2display\": \"show\",\n" +
            "    \"button2label\": \"Ok am inteles\",\n" +
            "    \"button2urltype\": \"close\",\n" +
            "    \"button2type\": \"secondary\",\n" +
            "    \"button2action\": \"closeVov\",\n" +
            "    \"recurrence\": \"33\",\n" +
            "    \"recurrenceTTL\": \"120\",\n" +
            "    \"timetodisplay\": \"3\"\n" +
            "  }\n" +
            "]";

    private static final String RESPONSE_8 = "{ \n" +
            "\"element\":\"Overlay2\", \n" +
            "\"elementID\":\"006\", \n" +
            "\"Xbutton\":\"show\", \n" +
            "\"title\":\"Titlu\", \n" +
            "\"content\":\" Intră şi activează promoţia cea mai minunată \",\n" +
            "\"bannerimage\":\"source/image.png\",\n" +
            "\"bannerurl\":\"http://m.vodafone.ro/vfrointegration/web/jsp/mcareRedirect.jsp?page_id=/new-mcare/extraoptiuni/index.htm\",\n" +
            "\"button1display\":\"show\",\n" +
            "\"button1label\":\"Accepta\",\n" +
            "\"button1urltype\":\"intern\",\n" +
            "\"button1type\":\"primary\",\n" +
            "\"button1action\":\"http://m.vodafone.ro/vfrointegration/web/jsp/mcareRedirect.jsp?page_id=/new-mcare/extraoptiuni/index.htm\",\n" +
            "\"button2display\":\"show\",\n" +
            "\"button2label\":\"Ok am inteles\",\n" +
            "\"button2urltype\":\"close\",\n" +
            "\"button2type\":\"secondary\",\n" +
            "\"button2action\":\"closeVov\",\n" +
            "\"recurrence\":\"100\",\n" +
            "\"recurrenceTTL\":\"120\",\n" +
            "\"timetodisplay\":\"3\"\n" +
            "}";


    public AdobeMockService() {
        buildResponseList();
        randomGenerator = new Random();
    }

    private void buildResponseList(){
        responses = new ArrayList<String>();
       // responses.add(RESPONSE_1);
        //responses.add(RESPONSE_2);
       // responses.add(RESPONSE_3);
       // responses.add(RESPONSE_4);
        //responses.add(RESPONSE_5);
        //responses.add(RESPONSE_6);
        responses.add(RESPONSE_7);
        //responses.add(RESPONSE_8);
    }

    public String getTargetResponse(){
        return responses.get(randomGenerator.nextInt(responses.size()));
    }

    public String getTargetResponse(String pageName){

        String response;

        switch (pageName){
            case AdobePageNamesConstants.LOGIN_PAGE:
                response = RESPONSE_8;
                break;
            case AdobePageNamesConstants.PG_DASHBOARD:
                response = RESPONSE_8;
                break;
            case AdobePageNamesConstants.PG_SERVICES:
                response = RESPONSE_8;
                break;
            case AdobePageNamesConstants.PG_OFFERS_EXTRAS:
                response = RESPONSE_8;
                break;
            case AdobePageNamesConstants.PG_PAYBILL:
                response = RESPONSE_8;
                break;
            case AdobePageNamesConstants.PG_PAYBILL_ANONIM:
                response = RESPONSE_8;
                break;
            case AdobePageNamesConstants.PG_PAYBILL_OWN:
                response = RESPONSE_8;
                break;
            default:
                response = RESPONSE_8;
                break;
        }
        return response;
    }
}
