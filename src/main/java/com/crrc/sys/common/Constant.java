package com.crrc.sys.common;

import java.util.regex.Pattern;

public interface Constant {

    String CORE_SERVICE_NAME = "workplace-core";

    String ERROR_MESSAGE = "errorMessage";


    String MENU_PERMISSION_KEY = "crrc_Menu_permission";

    String DICT_DATA_PREFIX1 = "DICT_DATA_CACHE:";
    String DICT_INDEX_PREFIX2 = "DICT_INDEX_CACHE";
    String DICT_CODE_PREFIX3 = ":CODE_CACHE";
    String DICT_ITEM_PREFIX3 = ":ITEM_CACHE:";
    String WEB_SOCKET_SESSIONS_KEY = "WS:SESSIONS";
    String WEB_SOCKET_SESSION_PUBSUB = "WS:PUBSUB";
    // 密码正则匹配 匹配
    String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&.])[A-Za-z\\d$@$!%*?&.]{8,16}";
    // 邮箱正则匹配
    String EMAIL_REGEX = "^[a-zA-Z0-9_\\-]+@[a-zA-Z0-9\\-]+(\\.[a-zA-Z0-9\\-]+)*\\.[a-zA-Z]{2,}$";
    // 电话号码正则匹配
    String PHONE_REGEX = "(^(\\+86-)?[1][3,4,5,6,7,8,9][0-9]{9}$)|(^$)";
    Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);
}
