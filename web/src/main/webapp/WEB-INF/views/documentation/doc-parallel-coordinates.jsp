<%--
  Created by IntelliJ IDEA.
  User: ilinca
  Date: 08/02/2017
  Time: 15:11
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>IMPC Documentation Parallel Coordinates</title>
    </head>
    <body>
        <h2>Parallel coordinates </h2>

        <p> The parallel coordinates tool allows users to compare strains across different parameters.
            Hover over a row in the table to highlight the corresponding line on the chart.</p>

        <p> To start using the tool select one or more procedures from the drop-down select box.
            Once this is done you can filter the data based on the phenotyping center.</p>

        <p> The values displayed are the genotype effect, which accounts for different variation sources.
            Information about this and the statistical methods used is available in the <a href="statistics-help">statistics
                documentation</a>.</p>

        <p>  </p>

        <br/>
        <img class="well" src="img/parallel.png"/>
        <br/>
        <p> The tool allows <b>filtering</b> on each axis (parameter) by selecting the region of interest with the mouse.</p>
        <img class="well" src="img/parallel-filtered.png"/>
        <br/><br/>
        <p>The <b>clear</b> button removes existing filters.</p>
        <p>The <b>export</b> button generates an export of the values in the table. If any filter is set, only the data
            displayed in the table will be exported.</p>

        <p> The generation of this chart is computationally intensive and the number of parameters that can be plotted may vary
            from one machine to the other.
            If you notice the tool becoming too slow, please consider selecting fewer procedures.</p>


</body>
</html>
