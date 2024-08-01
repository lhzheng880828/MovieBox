package com.calvin.box.movie.db

import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.calvin.box.movie.bean.*
import com.calvin.box.movie.db.dao.FruittieDao
import com.calvin.box.movie.db.dao.*
import com.calvin.box.movie.model.Fruittie

@Database(entities = [Keep::class, Site::class, Live::class, Track::class, Config::class, Device::class, History::class, Download::class, Fruittie::class], version = MoiveDatabase.VERSION)
abstract class MoiveDatabase : RoomDatabase() {

    companion object {
        const val VERSION = 31
        const val NAME = "tv.db"
        const val SYMBOL = "@@@"
        private const val BACKUP_SUFFIX = "tv.backup"

        private lateinit var database: MoiveDatabase
        fun set(db: MoiveDatabase){
            database = db
        }
        fun get():MoiveDatabase{
            return database
        }

        /*fun backup() {
            if (Setting.getBackupMode() == 0) backup(object : com.calvin.box.moive.impl.Callback {
                override fun success() {
                    TODO("Not yet implemented")
                }

                override fun error(msg: String) {
                    TODO("Not yet implemented")
                }

            })
        }

        fun backup(callback: com.calvin.box.moive.impl.Callback) {
            App.execute {
                val restore = Path.restore()
                if (!restore.exists()) return@execute
                val db = App.get().getDatabasePath(NAME).absoluteFile
                val wal = App.get().getDatabasePath("$NAME-wal").absoluteFile
                val shm = App.get().getDatabasePath("$NAME-shm").absoluteFile
                if (db.exists()) Path.copy(db, File(restore, db.name))
                if (wal.exists()) Path.copy(wal, File(restore, wal.name))
                if (shm.exists()) Path.copy(shm, File(restore, shm.name))
                Prefers.backup(File(restore, "$NAME-pref"))
                val time = Util.format(SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault()), File(restore, db.name).lastModified())
                val file = File(Path.tv(), "$time.$BACKUP_SUFFIX")
                FileUtil.zipFolder(restore, file)
                App.post { callback.success(file.absolutePath) }
            }
        }

        fun restore(file: File, callback: Callback) {
            App.execute {
                val restore = Path.restore()
                if (!restore.exists()) return@execute
                FileUtil.extractZip(file, restore)
                val db = File(restore, NAME)
                val wal = File(restore, "$NAME-wal")
                val shm = File(restore, "$NAME-shm")
                val pref = File(restore, "$NAME-pref")
                if (db.exists()) Path.copy(db, App.get().getDatabasePath(db.name).absoluteFile)
                if (wal.exists()) Path.copy(wal, App.get().getDatabasePath(wal.name).absoluteFile)
                if (shm.exists()) Path.copy(shm, App.get().getDatabasePath(shm.name).absoluteFile)
                if (pref.exists()) Prefers.restore(pref)
                App.post { callback.success() }
            }
        }*/

    }

    abstract fun getKeepDao(): KeepDao
    abstract fun getSiteDao(): SiteDao
    abstract fun getLiveDao(): LiveDao
    abstract fun getTrackDao(): TrackDao
    abstract fun getConfigDao(): ConfigDao
    abstract fun getDeviceDao(): DeviceDao
    abstract fun getHistoryDao(): HistoryDao
    abstract fun getDownloadDao(): DownloadDao
    abstract fun fruittieDao(): FruittieDao
}

fun <T : RoomDatabase> RoomDatabase.Builder<T>.addMoiveMigrations(): RoomDatabase.Builder<T> {
    return this.apply {
        addMigrations(MIGRATION_11_12)
        addMigrations(MIGRATION_12_13)
        addMigrations(MIGRATION_13_14)
        addMigrations(MIGRATION_14_15)
        addMigrations(MIGRATION_15_16)
        addMigrations(MIGRATION_16_17)
        addMigrations(MIGRATION_17_18)
        addMigrations(MIGRATION_18_19)
        addMigrations(MIGRATION_19_20)
        addMigrations(MIGRATION_20_21)
        addMigrations(MIGRATION_21_22)
        addMigrations(MIGRATION_22_23)
        addMigrations(MIGRATION_23_24)
        addMigrations(MIGRATION_24_25)
        addMigrations(MIGRATION_25_26)
        addMigrations(MIGRATION_26_27)
        addMigrations(MIGRATION_27_28)
        addMigrations(MIGRATION_28_29)
        addMigrations(MIGRATION_29_30)
        addMigrations(MIGRATION_30_31)
        // .allowMainThreadQueries()
        // .fallbackToDestructiveMigration()
    }
}


// Migration objects
val MIGRATION_11_12 = object : Migration(11, 12) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE Config ADD COLUMN type INTEGER DEFAULT 0 NOT NULL")
        connection.execSQL("ALTER TABLE Config ADD COLUMN home TEXT DEFAULT NULL")
    }
}



val MIGRATION_12_13 = object : Migration(12, 13) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE Keep ADD COLUMN type INTEGER DEFAULT 0 NOT NULL")
    }
}

val MIGRATION_13_14 = object : Migration(13, 14) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("DROP INDEX IF EXISTS index_Config_url")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_Config_url_type ON Config(url, type)")
    }
}

val MIGRATION_14_15 = object : Migration(14, 15) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE History ADD COLUMN scale INTEGER DEFAULT -1 NOT NULL")
    }
}

val MIGRATION_15_16 = object : Migration(15, 16) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE History ADD COLUMN speed REAL DEFAULT 1 NOT NULL")
        connection.execSQL("ALTER TABLE History ADD COLUMN player INTEGER DEFAULT -1 NOT NULL")
    }
}

val MIGRATION_16_17 = object : Migration(16, 17) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `Track` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `type` INTEGER NOT NULL, `group` INTEGER NOT NULL, `track` INTEGER NOT NULL, `player` INTEGER NOT NULL, `key` TEXT, `name` TEXT, `selected` INTEGER NOT NULL)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_Track_key_player_type` ON `Track` (`key`, `player`, `type`)")
    }
}

val MIGRATION_17_18 = object : Migration(17, 18) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE Config ADD COLUMN parse TEXT DEFAULT NULL")
    }
}

val MIGRATION_18_19 = object : Migration(18, 19) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE Site ADD COLUMN changeable INTEGER DEFAULT 1")
    }
}

val MIGRATION_19_20 = object : Migration(19, 20) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE Config ADD COLUMN name TEXT DEFAULT NULL")
    }
}

val MIGRATION_20_21 = object : Migration(20, 21) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `Device` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `uuid` TEXT, `name` TEXT, `ip` TEXT)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_Device_uuid_name` ON `Device` (`uuid`, `name`)")
    }
}

val MIGRATION_21_22 = object : Migration(21, 22) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE Device ADD COLUMN type INTEGER DEFAULT 0 NOT NULL")
    }
}

val MIGRATION_22_23 = object : Migration(22, 23) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("UPDATE History SET player = 2 WHERE player = 0")
        // 注意: 这里的 `Setting.getLivePlayer` 和 `Setting.putLivePlayer` 需要适当的KMP实现
       // if (Setting.getLivePlayer() == 0) Setting.putLivePlayer(2)
       // if (Setting.getPlayer() == 0) Setting.putPlayer(2)
    }
}

val MIGRATION_23_24 = object : Migration(23, 24) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE Track ADD COLUMN `adaptive` INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_24_25 = object : Migration(24, 25) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE Site ADD COLUMN recordable INTEGER DEFAULT 1")
    }
}

val MIGRATION_25_26 = object : Migration(25, 26) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE Site_Backup (`key` TEXT NOT NULL, name TEXT, searchable INTEGER, changeable INTEGER, recordable INTEGER, PRIMARY KEY (`key`))")
        connection.execSQL("INSERT INTO Site_Backup SELECT `key`, name, searchable, changeable, recordable FROM Site")
        connection.execSQL("DROP TABLE Site")
        connection.execSQL("ALTER TABLE Site_Backup RENAME to Site")
    }
}

val MIGRATION_26_27 = object : Migration(26, 27) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `Live` (`name` TEXT NOT NULL, `boot` INTEGER NOT NULL, `pass` INTEGER NOT NULL, PRIMARY KEY(`name`))")
    }
}

val MIGRATION_27_28 = object : Migration(27, 28) {
    override fun migrate(connection: SQLiteConnection) {
        // 注意: 这里的 `Prefers.remove` 需要适当的KMP实现
      //  Prefers.remove("danmu_size")
    }
}

val MIGRATION_28_29 = object : Migration(28, 29) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE Site_Backup (`key` TEXT NOT NULL, searchable INTEGER, changeable INTEGER, PRIMARY KEY (`key`))")
        connection.execSQL("INSERT INTO Site_Backup SELECT `key`, searchable, changeable FROM Site")
        connection.execSQL("DROP TABLE Site")
        connection.execSQL("ALTER TABLE Site_Backup RENAME to Site")
    }
}

val MIGRATION_29_30 = object : Migration(29, 30) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE Config ADD COLUMN logo TEXT DEFAULT NULL")
    }
}



// ... (other migration objects follow the same pattern)

val MIGRATION_30_31 = object : Migration(30, 31) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE Download (`id` TEXT NOT NULL, vodPic TEXT, vodName TEXT, url TEXT, header TEXT, createTime INTEGER NOT NULL, PRIMARY KEY (`id`))")
    }
}