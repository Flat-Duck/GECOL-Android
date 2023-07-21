package ly.smarthive.gecol;

public class COMMON {

    public static  String BASE_URL = "http://192.168.1.131:8081/gcol/public/api/";
    //public static  String BASE_URL = "http://10.0.2.2/school/public/api/";
    public static  String LOGIN_URL = BASE_URL + "login";
    public static  String REGISTER_URL = BASE_URL + "register";
    public static  String VERIFY_URL = BASE_URL + "verify";


    public static  String MAIN_URL = BASE_URL + "main";
    public static  String NOTICES_URL = BASE_URL + "notices";
    public static  String READINGS_URL = BASE_URL + "readings";
    public static  String PAY_URL = BASE_URL + "pay/";


    public static  String CURRENT_USER_EMAIL;
    public static  String CURRENT_USER_PASSWORD;
    public static  String USER_TOKEN ;
    // Shared pref mode
    public static final String PREF_NAME = "gecol_app";
    public static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    public static final String KEY_TOKEN = "accessToken";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_STATUS = "stat";
    public static final String KEY_STUDENT_ID = "student_id";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_MESSAGE_TIME_STAMP = "last_msg";

}
