package com.sky.app.domain

/** Hebrew strings ported from the web app's translations object in sky.js. */
object HebrewStrings {
    // Lunar phases
    const val NEW_MOON = "ירח חדש"
    const val WAXING_CRESCENT = "ירח בתהליך התמלאות ראשוני"
    const val FIRST_QUARTER = "רבע ראשון"
    const val WAXING_GIBBOUS = "ירח גבנוני בתהליך התמלאות"
    const val FULL_MOON = "ירח מלא"
    const val WANING_GIBBOUS = "ירח גבנוני בתהליך התמעטות"
    const val LAST_QUARTER = "רבע אחרון"
    const val WANING_CRESCENT = "ירח בתהליך התמעטות סופי"

    // Seasons
    const val WINTER = "חורף"
    const val SPRING = "אביב"
    const val SUMMER = "קיץ"
    const val FALL = "סתיו"

    // Card headers & labels
    const val TITLE = "הַשָּׁמַ֗יִם מְסַפְּרִ֥ים כְּֽבוֹד־אֵ֑ל וּמַֽעֲשֵׂ֥ה יָ֝דָ֗יו מַגִּ֥יד הָֽרָקִֽיעַ׃"
    const val LUNAR_DAY = "יום ירחי"
    const val SEASON = "עונה"
    const val CURRENT_HOUR = "השעה כעת"
    const val DAY = "יום"
    const val DAYS_OF = "מתוך"
    const val DAYS_REMAINING = "ימים נותרו"
    const val DAY_HOUR = "שעת יום"
    const val NIGHT_HOUR = "שעת לילה"
    const val DAY_HOURS = "שעות יום"
    const val NIGHT_HOURS = "שעות לילה"

    // Weekday names (index 0 = Sunday, matching Date.getDay()).
    val WEEKDAYS = listOf(
        "יום ראשון",
        "יום שני",
        "יום שלישי",
        "יום רביעי",
        "יום חמישי",
        "יום שישי",
        "יום שבת"
    )
}
