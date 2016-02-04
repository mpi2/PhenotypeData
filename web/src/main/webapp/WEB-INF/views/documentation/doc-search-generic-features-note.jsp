<%--
  Created by IntelliJ IDEA.
  User: ckc
  Date: 20/01/2016
  Time: 16:23
  To change this template use File | Settings | File Templates.
--%>

<html>
<head>
    <title></title>
</head>
<body>
<div id="note">For more information about the page layout and the basic search functionalities please consult the <a href="#tabs-1">Generic Search Features</a> tab.</div><br>


<script>
    $(function() {
        $('a').click(function){
            $("#tabs").tabs({active: 0});
        }
    });
</script>

</body>
</html>
