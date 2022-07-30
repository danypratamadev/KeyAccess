package com.pratama.dany.keyaccess

import android.provider.BaseColumns

object SqliteContract {

    class NightModeEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "nightmode"
            val COLUMN_NIGHT_ID = "id"
            val COLUMN_STATUS = "status"
        }
    }

    class FingerprintEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "fingerprint"
            val COLUMN_NIGHT_ID = "id"
            val COLUMN_STATUS = "status"
        }
    }

    class OpenAutomaticallyEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "automatically"
            val COLUMN_NIGHT_ID = "id"
            val COLUMN_STATUS = "status"
        }
    }

    class NotificationEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "notification"
            val COLUMN_NIGHT_ID = "id"
            val COLUMN_STATUS = "status"
        }
    }

}