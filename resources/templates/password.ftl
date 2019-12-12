<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Zmiana Hasła</title>
        <link rel="stylesheet" href="/static/styles.css">
    </head>
    <body>

        <br /><br /><br /><br /><br />
        <p><h1>Zmiana Hasła</h1></p><br /><br />

        <form action="/changePassword" method="post" enctype="application/x-www-form-urlencoded">
            <table align="center" class="form-table">
                <td>Stare Hasło:</td>
                <td><input type="password" name="oldPassword" /></td>
                <td>Nowe Hasło:</td>
                <td><input type="password" name="newPassword" /></td>
                <td>Powtórz Nowe Hasło:</td>
                <td><input type="password" name="confirmNewPassword" /></td>
                <td class="blank-row"></td>
                <td><input type="submit" name="button" value="Zatwierdź"/></td>
            </table>
        </form>

        <#if error??>
                    <table align="center" class="error-msg-table">
                    <td>${error}</td>
                    </table>
                </#if>
                <br />

        <form action="/welcome">
            <table align="center" class="form-table">
                <td><input type="submit" value="Cofnij" /></td>
            </table>
        </form>

    </body>
</html>
