package com.pnf.penequillaunch.equilsdk;

import android.os.Environment;

import java.io.File;

public class Const {
    public static String SAMPLE_FOLDER_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + "neolab_data";


    public class Setting {
        public final static String KEY_PASSWORD = "password";
        public final static String KEY_PEN_COLOR = "pen_color";
        public final static String KEY_AUTO_POWER_ON = "auto_power_onoff";
        public final static String KEY_BEEP = "beep_onoff";
        public final static String KEY_AUTO_POWER_OFF_TIME = "auto_power_off_time";
        public final static String KEY_SENSITIVITY = "sensitivity";
        public final static String KEY_OFFLINE_DATA_SAVE = "offlinedata_save";
        public final static String KEY_HOVER_MODE = "hover_onoff";
        public final static String KEY_PEN_CAP_ON = "pencap_onoff";


    }


    public class Coordinates {
        public final static float xOffset = 1.2f;
        public final static float yOffset = 1.2f;
        public final static float pageEndX = 90f;

        public final static double pageVerticalPixels = 124.6f;
        public final static double pageHorizontalPixels = 84.49f;
        
        public final static float defaultAgeStartX = 28f;
        public final static float defaultAgeEndX = 51f;
        public final static float defaultAgeStartY = 31f;
        public final static float defaultAgeEndY = 36f;
        public final static float defaultMobileStartX = 51f;
        public final static float defaultMobileEndX = pageEndX;
        public final static float defaultMobileStartY = 31f;
        public final static float defaultMobileEndY = 36f;
        public final static float defaultNextPageStartX = 67.67f;
        public final static float defaultNextPageEndX = pageEndX;
        public final static float defaultNextPageStartY = 108.11f;
        public final static float defaultNextPageEndY = 115.5f;
        public final static float defaultGenderStartX = 0f;
        public final static float defaultGenderEndX = 28f;
        public final static float defaultGenderStartY = 31f;
        public final static float defaultGenderEndY = 36f;
        public final static float defaultOthersStartX = 0f;
        public final static float defaultOthersEndX = 0f;
        public final static float defaultOthersStartY = 0f;
        public final static float defaultOthersEndY = 0f;
        public final static float defaultPrescriptionStartX = 0f;
        public final static float defaultPrescriptionEndX = pageEndX;
        public final static float defaultPrescriptionStartY = 45f;
        public final static float defaultPrescriptionEndY = (float) pageVerticalPixels;
        public final static float defaultNameStartX = 0f;
        public final static float defaultNameEndX = 51f;
        public final static float defaultNameStartY = 20.71f;
        public final static float defaultNameEndY = 31.01f;

        public final static float defaultFollowUPStartX = 0f;
        public final static float defaultFollowUPEndX = 30f;
        public final static float defaultFollowUPStartY = 108.11f;
        public final static float defaultFollowUPEndY = 115.5f;

        public final static float defaultDiagnoseStartX = 0f;
        public final static float defaultDiagnoseEndX = pageEndX;
        public final static float defaultDiagnoseStartY = 36f;
        public final static float defaultDiagnoseEndY = 46.01f;


        public final static String ageStartX = "age start x";
        public final static String ageEndX = "age end x";
        public final static String ageStartY = "age start y";
        public final static String ageEndY = "age end y";
        public final static String mobileStartX = "mobile start x";
        public final static String mobileEndX = "mobile end x";
        public final static String mobileStartY = "mobile start y";
        public final static String mobileEndY = "mobile end y";
        public final static String nextPageStartX = "date start x";
        public final static String nextPageEndX = "date end x";
        public final static String nextPageStartY = "date start y";
        public final static String nextPageEndY = "date end y";
        public final static String genderStartX = "gender start x";
        public final static String genderEndX = "gender end x";
        public final static String genderStartY = "gender start y";
        public final static String genderEndY = "gender end y";
        public final static String othersStartX = "others start x";
        public final static String othersEndX = "others end x";
        public final static String othersStartY = "others start y";
        public final static String othersEndY = "others end y";
        public final static String prescriptionStartX = "doctor start x";
        public final static String prescriptionEndX = "doctor end x";
        public final static String prescriptionStartY = "doctor start y";
        public final static String prescriptionEndY = "doctor end y";
        public final static String nameStartX = "name start x";
        public final static String nameEndX = "name end x";
        public final static String nameStartY = "name start y";
        public final static String nameEndY = "name end y";
        
        public final static String followUpStartX = "followUp start x";
        public final static String followUpEndX = "followUp end x";
        public final static String followUpStartY = "followUp start y";
        public final static String followUpEndY = "followUp end y";
        
        public final static String diagnoseStartX = "diagnose start x";
        public final static String diagnoseEndX = "diagnose end x";
        public final static String diagnoseStartY = "diagnose start y";
        public final static String diagnoseEndY = "diagnose end y";


    }


    public class JsonTag {
        public final static String STRING_PROTOCOL_VERSION = "protocol_version";
        public final static String INT_TIMEZONE_OFFSET = "timezone";
        public final static String LONG_TIMETICK = "timetick";
        public final static String INT_MAX_FORCE = "force_max";
        public final static String INT_BATTERY_STATUS = "battery";
        public final static String INT_MEMORY_STATUS = "used_memory";
        public final static String INT_PEN_COLOR = "pen_tip_color";
        public final static String BOOL_AUTO_POWER_ON = "auto_power_onoff";
        public final static String BOOL_PEN_CAP_ON = "pencap_onoff";

        public final static String BOOL_ACCELERATION_SENSOR = "acceleration_sensor_onoff";
        public final static String BOOL_HOVER = "hover_mode";
        public final static String BOOL_OFFLINE_DATA_SAVE = "offlinedata_save";
        public final static String BOOL_BEEP = "beep";
        public final static String INT_AUTO_POWER_OFF_TIME = "auto_power_off_time";
        public final static String INT_PEN_SENSITIVITY = "sensitivity";

        public final static String INT_TOTAL_SIZE = "total_size";
        public final static String INT_SENT_SIZE = "sent_size";
        public final static String INT_RECEIVED_SIZE = "received_size";

        public final static String INT_SECTION_ID = "section_id";
        public final static String INT_OWNER_ID = "owner_id";
        public final static String INT_NOTE_ID = "note_id";
        public final static String INT_PAGE_ID = "page_id";
        public final static String STRING_FILE_PATH = "file_path";

        public final static String INT_PASSWORD_RETRY_COUNT = "retry_count";
        public final static String INT_PASSWORD_RESET_COUNT = "reset_count";

        public final static String BOOL_RESULT = "result";
    }

    public class Broadcast {
        public static final String ACTION_PEN_MESSAGE = "action_pen_message";
        public static final String MESSAGE_TYPE = "message_type";
        public static final String CONTENT = "content";

        public static final String ACTION_SYMBOL_ACTION = "symbol_action";
        public static final String ACTION_WRITE_PAGE_CHANGED = "write_page_changed";
        public static final String EXTRA_SECTION_ID = "sectionId";
        public static final String EXTRA_OWNER_ID = "ownerId";
        public static final String EXTRA_BOOKCODE_ID = "bookcodeId";
        public static final String EXTRA_PAGE_NUMBER = "page_number";
        public static final String EXTRA_SYMBOL_ID = "symbolId";

        public static final String ACTION_PEN_DOT = "action_pen_dot";
        public static final String ACTION_OFFLINE_STROKES = "action_offline_strokes";
        public static final String EXTRA_DOT = "dot";
        public static final String EXTRA_OFFLINE_STROKES = "offline_strokes";
//		public static final String SECTION_ID = "sectionId";
//		public static final String OWNER_ID = "ownerId";
//		public static final String NOTE_ID = "noteId";
//		public static final String PAGE_ID = "pageId";
//		public static final String X = "x";
//		public static final String Y = "y";
//		public static final String FX = "fx";
//		public static final String FY = "fy";
//		public static final String PRESSURE = "pressure";
//		public static final String TIMESTAMP = "timestamp";
//		public static final String TYPE = "type";
//		public static final String COLOR = "color";

        public static final String ACTION_PEN_UPDATE = "action_firmware_update";
    }
}
