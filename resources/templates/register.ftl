<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Rejestracja</title>
        <link rel="stylesheet" href="/static/styles.css">
    </head>
    <body>

        <br /><br /><br /><br /><br />
        <p><h1>Moduł Rejestracji</h1></p><br /><br />

        <form action="/register" method="post" enctype="application/x-www-form-urlencoded">
            <table align="center" class="form-table">
                <td>Login:</td>
                <td><input type="text" name="login" /></td>
                <td>Hasło:</td>
                <td><input type="password" name="password" /></td>
                <td>Powtórz Hasło:</td>
                <td><input type="password" name="confirmPassword" /></td>
                <td class="blank-row"></td>
                <td><input type="submit" name="button" value="Zarejestruj się!"/></td>
            </table>
        </form>

        <#if error??>
            <table align="center" class="error-msg-table">
            <td>${error}</td>
            </table>
        </#if>
        <br />

        <form action="/login">
            <table align="center" class="form-table">
                <td><input type="submit" value="Cofnij" /></td>
            </table>
        </form>

    </body>
</html>