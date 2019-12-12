<#-- @ftlvariable name="activitiesData" type="ArrayList<activityData>" -->
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Aktywności</title>
        <link rel="stylesheet" href="/static/styles.css">
    </head>
    <body>

        <br /><br />
        <h1><center>Moja lista aktywności</center></h1><br /><br />

        <table align="center" border ="1" style="border-collapse: collapse; padding: 10px; width: 25%" class="description-table">
            <tr>
                <td>ID</td>
                <td>Dodano</td>
                <td>Zadanie</td>
            </tr>
            <#list activitiesData as data>
                     <tr>
                        <td>${data.id}</td>
                        <td>${data.date}</td>
                        <td>${data.text}</td>

                    <#if data.text == "Brak aktywności">
                    <#else>
                        <#if data.useractivity == "yes">
                            <td>
                                <form action="/deleteActivity" method="post">
                                    <input type="hidden" name="activityID" value=${data.id}>
                                    <input type="submit" align="middle" value="Usuń"/>
                                </form>
                            </td>
                            <td>
                                <form action="/modifyActivity">
                                    <input type="hidden" name="activityText" value=${data.text}>
                                    <input type="hidden" name="activityID" value=${data.id}>
                                    <input type="submit" align="middle" value="Modyfikuj"/>
                                </form>
                            </td>
                        </#if>
                    </#if>
                    </tr>
            </#list>
        </table>

        <br /><br />

        <form action="/addActivity">
            <table align="center" class="form-table">
                <td><input type="submit" value="Dodaj aktywność"/></td>
            </table>
        </form>

        <br /><br />

        <form action="/welcome">
            <table align="center" class="form-table">
                <td><input type="submit" value="Cofnij" /></td>
            </table>
        </form>

    </body>
</html>