package com.example

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
import kotlinx.css.*
import freemarker.cache.*
import io.ktor.freemarker.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.http.content.resources
import io.ktor.http.content.static
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.features.StatusPages
import io.ktor.sessions.*
import io.ktor.util.hex
import kotlinx.io.core.String
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.util.*
import java.sql.SQLException
import java.sql.ResultSetMetaData




fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    val properties = Properties()

    //Populate the properties file with user name and password
    with(properties){
        put("user", "admin")
        put("password", "admin")
    }

    var connection: Connection? = null
    var statement: Statement? = null

    com.example.initHikari()
    var hikariDS: HikariDataSource = HikariDataSource(com.example.hikariConfig)


    //Open a connection to the database
    println("check connection via hikari:")
    testConnection(hikariDS)
    println("conn test:")
    connection = DriverManager.getConnection("jdbc:derby:ktor_project_db;create=true", properties)

    //resetDatabase(connection)

    println("select test:")
    com.example.selectUsersTest(connection)
    println("----------------------------------")
    com.example.selectActivitiesTest(connection)==
    println("====done====")

    val client = HttpClient(Apache) {
    }

    install(Sessions) {
        cookie<LoggingSession>("SESSION_ID")
        {
            transform(SessionTransportTransformerMessageAuthentication(hashKey))
            cookie.path = "/"
        }

    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        route("/login") {
            get {
                call.respond(FreeMarkerContent("login.ftl", null))
            }
            post{
                val post = call.receiveParameters()
                var isUserExists: Boolean
                isUserExists = com.example.checkUserExistence(connection, post["username"], post["password"])
                if (isUserExists)
                {
                    call.sessions.set(LoggingSession(loggedUser = post["username"]!!))
                    val session = call.sessions.get<LoggingSession>()
                    call.respond(FreeMarkerContent("welcome.ftl", mapOf("welcome" to "Witaj ${session?.loggedUser}!")))
                }
                else
                {
                    call.respond(FreeMarkerContent("login.ftl", mapOf("error" to "Błędne dane logowania!")))
                }
            }
        }

        route("/changePassword") {
            get {
                val checkSession: LoggingSession? = call.sessions.get<LoggingSession>()
                if (checkSession == null)
                {
                    call.respond(FreeMarkerContent("login.ftl", null))
                }
                else
                {
                    call.respond(FreeMarkerContent("password.ftl", null))
                }

            }
            post{
                val post = call.receiveParameters()
                var errorMessages: String?
                val session = call.sessions.get<LoggingSession>()
                errorMessages = com.example.testUserPassword(connection, session?.loggedUser, post["oldPassword"], post["newPassword"], post["confirmNewPassword"])
                if(errorMessages == null || errorMessages == "")
                {
                    com.example.changeUserPassword(connection, session?.loggedUser, post["newPassword"])
                    call.respond(FreeMarkerContent("welcome.ftl", mapOf("response" to "Hasło zostało pomyślnie zmienione!")))
                }
                else
                {
                    call.respond(FreeMarkerContent("password.ftl", mapOf("error" to errorMessages)))
                }
            }
        }

        route("/myActivities")
        {
            get {
                val checkSession: LoggingSession? = call.sessions.get<LoggingSession>()
                if (checkSession == null)
                {
                    call.respond(FreeMarkerContent("login.ftl", null))
                }
                else
                {
                    var activitiesList: ArrayList<activityData>
                    val session = call.sessions.get<LoggingSession>()
                    activitiesList = pullActivitiesOfLoggedUser(connection, session?.loggedUser)
                    call.respond(FreeMarkerContent("myactivities.ftl", mapOf("activitiesData" to activitiesList) ,"")) //to IndexData(listOf(1, 2, 3))), ""))
                }
            }
        }

        route("/addActivity") {
            get {
                val checkSession: LoggingSession? = call.sessions.get<LoggingSession>()
                if (checkSession == null)
                {
                    call.respond(FreeMarkerContent("login.ftl", null))
                }
                else
                {
                    call.respond(FreeMarkerContent("addactivity.ftl", null))
                }
            }

            post{
                val post = call.receiveParameters()
                val session = call.sessions.get<LoggingSession>()
                var errorMessages: String? = ""
                if(post["newActivityText"].equals("") || post["newActivityText"] == null)
                {
                    errorMessages = "Proszę coś wpisać!"
                }
                if(errorMessages == null || errorMessages == "")
                {
                    com.example.addNewActivity(connection, session?.loggedUser, post["newActivityText"])
                    call.respond(FreeMarkerContent("welcome.ftl", mapOf("response" to "Aktywność dodana.")))
                }
                else
                {
                    call.respond(FreeMarkerContent("addactivity.ftl", mapOf("error" to errorMessages)))
                }
            }
        }

        route("/modifyActivity") {
            get {
                val checkSession: LoggingSession? = call.sessions.get<LoggingSession>()
                if (checkSession == null)
                {
                    call.respond(FreeMarkerContent("login.ftl", null))
                }
                else
                {
                    var pulledActivityTextt = pullActivityText(connection, call.parameters.get("activityID"))
                    call.respond(FreeMarkerContent("modifyActivity.ftl", mapOf("activityID" to call.parameters.get("activityID"), "pulledActivityText" to pulledActivityTextt), ""))
                }
            }

            post{
                val post = call.receiveParameters()
                val session = call.sessions.get<LoggingSession>()
                var errorMessages: String? = ""
                if(post["newActivityText"].equals("") || post["newActivityText"] == null)
                {
                    errorMessages = "Proszę coś wpisać!"
                }
                if(errorMessages == null || errorMessages == "")
                {
                    com.example.modifyActivity(connection, session?.loggedUser, post["newActivityText"], post["activityID"])
                    call.respond(FreeMarkerContent("welcome.ftl", mapOf("response" to "Aktywność zmodyfikowana.")))
                }
                else
                {
                    call.respond(FreeMarkerContent("modifyActivity.ftl", mapOf("error" to errorMessages)))
                }
            }
        }

        route("/welcome") {
            get {
                val checkSession: LoggingSession? = call.sessions.get<LoggingSession>()
                if (checkSession == null)
                {
                    call.respond(FreeMarkerContent("login.ftl", null))
                }
                else
                {
                    call.respond(FreeMarkerContent("welcome.ftl", null))
                }
            }
        }

        route("/deleteActivity"){
            post {
                val post = call.receiveParameters()
                com.example.deleteActivity(connection, post["activityID"])
                call.respond(FreeMarkerContent("welcome.ftl", mapOf("response" to "Aktywność " + post["activityID"] + " usunięta.")))
            }
        }

        route("/logout"){
            get {
                call.sessions.set(LoggingSession(loggedUser = ""))
                call.sessions.clear<LoggingSession>()
                call.respond(FreeMarkerContent("login.ftl", mapOf("information" to "Wylogowano!")))
            }
        }

        route("/register") {
            get {
                call.respond(FreeMarkerContent("register.ftl", null))
            }
            post{
                val post = call.receiveParameters()
                var errorMessages: String?
                errorMessages = com.example.testUserCredentials(connection, post["login"], post["password"], post["confirmPassword"])
                if(errorMessages == null || errorMessages == "")
                {
                    com.example.addNewUser(connection, post["login"], post["password"])
                    call.respond(FreeMarkerContent("login.ftl", mapOf("information" to "Konto zostało utworzone! Teraz możesz się zalogować.")))
                }
                else
                {
                    call.respond(FreeMarkerContent("register.ftl", mapOf("error" to errorMessages)))
                }
            }
        }

        static("/static") {
            resources("static")
        }
    }
}

data class IndexData(val items: List<Int>)

data class activityData(var id: String, var date: String, var text: String, var useractivity: String)

data class LoggingSession(val loggedUser: String?)

val hashKey = hex("6819b57a326945c1968f45236581")

fun FlowOrMetaDataContent.styleCss(builder: CSSBuilder.() -> Unit) {
    style(type = ContentType.Text.CSS.toString()) {
        +CSSBuilder().apply(builder).toString()
    }
}

fun CommonAttributeGroupFacade.style(builder: CSSBuilder.() -> Unit) {
    this.style = CSSBuilder().apply(builder).toString().trim()
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}

fun resetDatabase(connection: Connection)
{
    println("Resetting database...")
    println("drop tables table")
    com.example.dropActivitiesTable(connection)
    com.example.dropUsersTable(connection)

    println("create tables:")
    com.example.createUsersTable(connection)
    com.example.createActivitiesTable(connection)
    println("insert test:")
    com.example.insertUserTest(connection)
    com.example.insertActivityTest(connection)
    com.example.insertActivityTest(connection)
}
