package com.example

import com.zaxxer.hikari.HikariDataSource
import com.zaxxer.hikari.HikariConfig
import com.sun.xml.internal.ws.encoding.xml.XMLMessage.getDataSource
import java.security.MessageDigest
import java.sql.*
import java.sql.DatabaseMetaData
import java.time.Instant
import java.time.LocalDate
import java.util.Date

public var hikariConfig = HikariConfig()

public fun initHikari()
{
    hikariConfig.jdbcUrl = "jdbc:derby:ktor_project_db"
    hikariConfig.username = "admin"
    hikariConfig.password = "admin"
    hikariConfig.addDataSourceProperty("cachePrepStmts", "true")
    hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250")
    hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
}

public fun testConnection(hikariDS: HikariDataSource)
{
    var statement: PreparedStatement? = null
    var resultSet: ResultSet? = null
    var hikariConnection: Connection? = null
    try
    {
        val hikariDataSource = hikariDS
        hikariConnection = hikariDataSource.getConnection()
        val databaseMetaData = hikariConnection.getMetaData()

        println("Hikari test")

        val names = arrayOf("TABLE")
        resultSet = databaseMetaData.getTables("tableNames", null, null, null)

        while (resultSet.next()) {
            val tab = resultSet.getString("TABLE_NAME")
            if(tab == "USERS")
            {
                println("Table: " + tab + " exists")
            }
            if(tab == "ACTIVITIES")
            {
                println("Table: " + tab + " exists")
            }
        }
    }
    catch (e: Exception)
    {
        try
        {
            hikariConnection!!.rollback()
        }
        catch (e1: SQLException)
        {
            e1.printStackTrace()
        }

        e.printStackTrace()
    }
}

public fun testUserCredentials(connection: Connection, login: String?, password: String?, confirmPassword: String?): String?
{
    var errorMessage: String
    errorMessage = ""
    try {
        if(login == "")
        {
            errorMessage += "Nie podano loginu!\n"
        }
        else
        {
            if(login!!.length < 6)
            {
                errorMessage += "Login nie może być krótszy niż 6 znaków!\n"
            }
            else
            {
                var isLoginAvailable: Boolean
                isLoginAvailable = checkLoginAvailibilty(connection, login)
                if(isLoginAvailable == false)
                {
                    errorMessage += "Login już zajęty!\n"
                }
            }
        }
        if(password == "")
        {
            errorMessage += "Nie podano hasła!\n"
        }
        else
        {
            if(password!!.length < 5)
            {
                errorMessage += "Hasło nie może być krótsze niż 5 znaków!\n"
            }
        }
        if(confirmPassword == "")
        {
            errorMessage += "Nie podano ponownie hasła!\n"
        }
        if(password != confirmPassword)
        {
            errorMessage += "Podane hasła różnią się między sobą!\n"
        }
        else
        {
            val regex = "^(?=.{5,})(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$".toRegex()
            if(!regex.matches(password))
            {
                errorMessage += "Hasło musi zawierać minimum jedną dużą literę i minimum jeden znak specjalny!\n"
            }
        }
    }
    catch(e: SQLException)
    {
        e.printStackTrace()
    }
    return errorMessage
}

public fun checkLoginAvailibilty(connection: Connection, login: String): Boolean
{
    var isAvailable: Boolean
    isAvailable = true

    try
    {
        var statement: Statement? = null
        statement = connection.createStatement()
        val results = statement.executeQuery("select * from ktor_project_db.users WHERE ktor_project_db.users.login = '" + login + "'")

        if(results.next())
        {
            isAvailable = false
        }
        results.close()
        statement.close()
    }
    catch (e: SQLException)
    {
        e.printStackTrace()
    }

    return isAvailable
}

public fun checkUserExistence(connection: Connection, login: String?, password: String?): Boolean
{
    var isExists: Boolean
    isExists = false

    var hashedPassword: String
    hashedPassword = sha256hashing(password)

    try
    {
        var statement: Statement? = null
        statement = connection.createStatement()
        val results = statement.executeQuery("select * from ktor_project_db.users WHERE ktor_project_db.users.login = '" + login + "' AND ktor_project_db.users.password = '" + hashedPassword + "'")
        println("select * from ktor_project_db.users WHERE ktor_project_db.users.login = '" + login + "' AND ktor_project_db.users.password = '" + hashedPassword + "'")
        if(results.next())
        {
            isExists = true
        }
        results.close()
        statement.close()
    }
    catch (e: SQLException)
    {
        e.printStackTrace()
    }

    return isExists
}

public fun addNewUser(connection: Connection, login: String?, password: String?)
{
    var hashedPassword: String
    hashedPassword = sha256hashing(password)
    try {
        var statement: Statement? = null
        statement = connection.createStatement()
        statement.execute("insert into " + "ktor_project_db.users" + " values (" +
                "default," +
                "'" + login + "'," +
                "'" + hashedPassword + "')")
        statement.close()
    }
    catch (e: SQLException)
    {
        e.printStackTrace()
    }
}

public fun testUserPassword(connection: Connection, login: String?, oldPassword: String?, newPassword: String?, confirmNewPassword: String?): String?
{
    var errorMessage: String
    errorMessage = ""
    try {
        if(login == "" || login == null)
        {
            errorMessage += "Brak loginu?\n"
        }
        if(oldPassword == "")
        {
            errorMessage += "Nie podano hasła!\n"
        }
        else
        {
            var isPasswordCorrect: Boolean
            isPasswordCorrect = checkPasswordCorrectness(connection, login, oldPassword)
            if(isPasswordCorrect == false)
            {
                errorMessage += "Stare hasło jest nieprawidłowe!\n"
            }
        }
        if(newPassword == "")
        {
            errorMessage += "Nie podano ponownie hasła!\n"
        }
        else
        {
            if(newPassword!!.length < 5)
            {
                errorMessage += "Nowe hasło nie może być krótsze niż 5 znaków!\n"
            }
        }
        if(newPassword != confirmNewPassword)
        {
            errorMessage += "Podane nowe hasła różnią się między sobą!\n"
        }
        else
        {
            val regex = "^(?=.{5,})(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$".toRegex()
            if(!regex.matches(newPassword))
            {
                errorMessage += "Nowe hasło musi zawierać minimum jedną dużą literę i minimum jeden znak specjalny!\n"
            }
        }
    }
    catch(e: SQLException)
    {
        e.printStackTrace()
    }
    return errorMessage
}


public fun checkPasswordCorrectness(connection: Connection, login: String?, password: String?): Boolean
{
    var isCorrect: Boolean
    isCorrect = false

    var hashedPassword: String
    hashedPassword = sha256hashing(password)

    try
    {
        var statement: Statement? = null
        var storedPassword: String? = null
        statement = connection.createStatement()
        val results = statement.executeQuery("select ktor_project_db.users.password from ktor_project_db.users WHERE ktor_project_db.users.login = '" + login + "'");
        println("select ktor_project_db.users.password from ktor_project_db.users WHERE ktor_project_db.users.login = '" + login + "'")
        while(results.next())
        {
            storedPassword = results.getString(1)
        }
        if(hashedPassword.equals(storedPassword))
        {
            isCorrect = true
        }
        results.close()
        statement.close()
    }
    catch (e: SQLException)
    {
        e.printStackTrace()
    }

    return isCorrect
}

public fun changeUserPassword(connection: Connection, login: String?, newPassword: String?)
{
    var hashedPassword: String
    hashedPassword = sha256hashing(newPassword)
    try {
        var statement: Statement? = null
        statement = connection.createStatement()
        println("UPDATE ktor_project_db.users" + " set ktor_project_db.users.password = '" + hashedPassword + "' where ktor_project_db.users.login = '" + login + "'")
        statement.execute("UPDATE ktor_project_db.users" + " set ktor_project_db.users.password = '" + hashedPassword + "' where ktor_project_db.users.login = '" + login + "'")
        statement.close()
    }
    catch (e: SQLException)
    {
        e.printStackTrace()
    }
}

public fun pullUserID(connection: Connection, login: String?): Int
{
    var userID = 0

    try
    {
        var statement: Statement? = null
        statement = connection.createStatement()
        val results = statement.executeQuery("select ktor_project_db.users.id from ktor_project_db.users WHERE ktor_project_db.users.login = '" + login + "'")

        if(results.next())
        {
            userID = results.getInt(1)
        }
        results.close()
        statement.close()
    }
    catch (e: SQLException)
    {
        e.printStackTrace()
    }

    return userID
}

public fun pullActivitiesOfLoggedUser(connection: Connection, login: String?): ArrayList<activityData>
{
    var activitiesList: ArrayList<activityData> = ArrayList<activityData>()

    //activitiesList.add()
    try {
        var userID = pullUserID(connection, login)
        var statement: Statement? = null
        statement = connection.createStatement()
        val results = statement.executeQuery("select * from ktor_project_db.activities")// WHERE ktor_project_db.activities.user_id = " + userID)

        var loggedUserActivity: String

        while (results.next()) {
            if(results.getInt(2) == userID)
            {
                loggedUserActivity = "yes"
            }
            else
            {
                loggedUserActivity = "no"
            }

            var singleActivity: activityData = activityData(results.getString(1), results.getString(3), results.getString(4), loggedUserActivity)

            println("ACTIV: " + singleActivity.id + ", " + singleActivity.date + ", " + singleActivity.text)
            activitiesList.add(singleActivity)
        }
        if(activitiesList.isEmpty())
        {
            var singleActivity: activityData = activityData("", "", "Brak aktywności", "")

            println("EMPTY ACTIV")
            activitiesList.add(singleActivity)
        }
        results.close()
        statement.close()
    } catch (sqlExcept: SQLException) {
        sqlExcept.printStackTrace()
    }

    return activitiesList
}

public fun pullActivityText(connection: Connection, activityID: String?): String?
{
    var activityText: String? = null
    try {
        var statement: Statement? = null
        statement = connection.createStatement()
        val results = statement.executeQuery("select ktor_project_db.activities.activity from ktor_project_db.activities WHERE ktor_project_db.activities.id = " + activityID)

        if(results.next())
        {
            activityText = results.getString(1)
        }

        results.close()
        statement.close()
    } catch (sqlExcept: SQLException) {
        sqlExcept.printStackTrace()
    }

    return activityText
}

public fun addNewActivity(connection: Connection, login: String?, activityText: String?)
{
    try {
        var userID = pullUserID(connection, login)
        var statement: Statement? = null
        statement = connection.createStatement()
        statement.execute("insert into " + "ktor_project_db.activities" + " values (" +
                "default," +
                + userID + "," +
                "'" + LocalDate.now() + "'," +
                "'" + activityText + "')")
        statement.close()
    }
    catch (e: SQLException)
    {
        e.printStackTrace()
    }
}

public fun modifyActivity(connection: Connection, login: String?, activityText: String?, chosenActivityID: String?)
{
    try {
        var userID = pullUserID(connection, login)
        var statement: Statement? = null
        statement = connection.createStatement()
        println("update ktor_project_db.activities" + " set ktor_project_db.activities.activity = '" + activityText +
                "', ktor_project_db.activities.inject_date = '" + LocalDate.now() + "' where ktor_project_db.activities.id = " + chosenActivityID +
                " and ktor_project_db.activities.user_id = " + userID)

        statement.execute("update ktor_project_db.activities" + " set ktor_project_db.activities.activity = '" + activityText +
                "', ktor_project_db.activities.inject_date = '" + LocalDate.now() + "' where ktor_project_db.activities.id = " + chosenActivityID +
                " and ktor_project_db.activities.user_id = " + userID)
        statement.close()
    }


    catch (e: SQLException)
    {
        e.printStackTrace()
    }
}

public fun deleteActivity(connection: Connection, activityID: String?)
{
    try {
        var statement: Statement? = null
        statement = connection.createStatement()
        statement.execute("delete from " + "ktor_project_db.activities" + " where ktor_project_db.activities.id = " + activityID)
        statement.close()
    }
    catch (e: SQLException)
    {
        e.printStackTrace()
    }
}

fun sha256hashing(password: String?): String {
    val bytes = password.toString().toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.fold("", { str, it -> str + "%02x".format(it) })
}