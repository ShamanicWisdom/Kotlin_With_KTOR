<html>
    <head>
        <link rel="stylesheet" href="/static/styles.css">
    </head>
    <body>
        <#list ['a', 'b', 'c', 'd']>
          <ul>
           <#items as x>
             <li>${x?index} : ${x} : XD</li>
           </#items>
          </ul>
        </#list>
    </body>
</html>








