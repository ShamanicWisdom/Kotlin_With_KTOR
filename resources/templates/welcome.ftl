<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Zalogowano</title>
        <link rel="stylesheet" href="/static/styles.css">
    </head>
    <body>

        <br /><br /><br /><br /><br />
        <#if welcome??>
            <table align="center" class="error-msg-table">
            <td>${welcome}</td>
            </table>
        </#if>
        <br />

        <form action="/myActivities">
            <table align="center" class="form-table">
                <td><input type="submit" value="Moje Aktywności"/></td>
            </table>
        </form>

        <form action="/changePassword">
            <table align="center" class="form-table">
                <td><input type="submit" value="Zmień Hasło"/></td>
            </table>
        </form>

        <#if response??>
            <table align="center" class="error-msg-table">
            <td>${response}</td>
            </table>
        </#if>
        <br />

        <form action="/logout">
            <table align="center" class="form-table">
                <td><input type="submit" value="Wyloguj" /></td>
            </table>
        </form>
    </body>