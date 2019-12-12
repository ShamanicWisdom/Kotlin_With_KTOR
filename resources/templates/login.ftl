<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Logowanie</title>
        <link rel="stylesheet" href="/static/styles.css">
    </head>
    <body>

        <br /><br /><br /><br /><br />
        <h1>Moduł Logowania</h1><br /><br />

        <form action="/login" method="post" enctype="application/x-www-form-urlencoded">
            <table align="center" class="form-table">
                <td>Login: <input type="text" name="username" /></td>
                <td>Hasło: <input type="password" name="password" /></td>
                <td class="blank-row"></td>
                <td><input type="submit" value="Zaloguj" /></td>
            </table>
        </form>

        <#if error??>
            <table align="center" class="error-msg-table">
            <td>${error}</td>
            </table>
        </#if>
        <#if information??>
            <table align="center" class="error-msg-table">
            <td>${information}</td>
            </table>
        </#if>
        <br />

        <form action="/register">
            <table align="center" class="form-table">
                <td><input type="submit" value="Zarejestruj się" /></td>
            </table>
        </form>
    </body>
</html>
