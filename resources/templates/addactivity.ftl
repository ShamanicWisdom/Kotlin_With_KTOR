<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Dodanie Aktywności</title>
        <link rel="stylesheet" href="/static/styles.css">
    </head>
    <body>

        <br /><br /><br /><br /><br />
        <p><h1>Dodanie nowej aktywności</h1></p><br /><br />

        <form action="/addActivity" method="post" enctype="application/x-www-form-urlencoded">
            <table align="center" class="form-table">
                <td>Aktywność:</td>
                <td><input type="text" name="newActivityText" /></td>
                <td class="blank-row"></td>
                <td><input type="submit" name="button" value="Akceptuj"/></td>
            </table>
        </form>

        <#if error??>
            <table align="center" class="error-msg-table">
            <td>${error}</td>
            </table>
        </#if>
        <br />

        <form action="/myActivities">
            <table align="center" class="form-table">
                <td><input type="submit" value="Cofnij" /></td>
            </table>
        </form>

    </body>
</html>