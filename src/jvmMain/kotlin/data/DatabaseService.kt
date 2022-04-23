package data

import config.ConfigService
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import util.Util.logger

object DatabaseService {

    private val log = logger()

    fun init() {
        with(ConfigService.config.database) {
            if(useSqliteInstead) {
                Database.connect("jdbc:sqlite:${sqliteDbFile}", "org.sqlite.JDBC")
                log.info("Using sqlite db at $sqliteDbFile")
            } else {
                val url = "jdbc:mysql://${host}:${port}/${db}"
                Database.connect(
                    url = url,
                    driver = "com.mysql.cj.jdbc.Driver",
                    user = user,
                    password = password
                )

                log.info("Connecting to database at $url")
            }
        }

        initializeDatabase()
    }

    fun initializeDatabase() {
        transaction {
            addLogger(StdOutSqlLogger)

            SchemaUtils.createMissingTablesAndColumns(Users)
        }

        transaction {

            if(User.find { Users.fafId eq 1 }.empty()) {
                User.new {
                    fafId = 1
                    username = "geo"
                }
                User.new {
                    fafId = 2
                    username = "test"
                }
                User.new {
                    fafId = 79110
                    username = "geosearchef"
                }
            }
        }
    }

}