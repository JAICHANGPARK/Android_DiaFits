package com.dreamwalker.diabetesfits.consts;

public class GlucoseType {

    public static final String BSM_UNKNOWN = "Unknown";
    public static final String BSM_BEFORE_MEAL = "식전";
    public static final String BSM_AFTER_MEAL = "식후";

    public static final String FASTING = "공복";

    public static final String BREAKFAST_BEFORE = "아침 식전";
    public static final String BREAKFAST_AFTER = "아침 식후";

    public static final String LUNCH_BEFORE = "점심 식전";
    public static final String LUNCH_AFTER = "점심 식후";

    public static final String DINNER_BEFORE = "저녁 식전";
    public static final String DINNER_AFTER = "저녁 식후";

    public static final String FITNESS_BEFORE = "운동 전";
    public static final String FITNESS_AFTER = "운동 후";

    public static final String SLEEP = "취침 전";

    // TODO: 2018-07-25 혈당계 동기화 데이터 수정을 위해서 유형별 숫자 재정립 - 박제창
    public static final int TYPE_FASTING = 10;
    public static final int TYPE_SLEEP = 11;
    public static final int TYPE_BREAKFAST_BEFORE = 12;
    public static final int TYPE_BREAKFAST_AFTER = 13;
    public static final int TYPE_LUNCH_BEFORE = 14;
    public static final int TYPE_LUNCH_AFTER = 15;
    public static final int TYPE_DINNER_BEFORE = 16;
    public static final int TYPE_DINNER_AFTER = 17;
    public static final int TYPE_FITNESS_BEFORE = 18;
    public static final int TYPE_FITNESS_AFTER = 19;


}
