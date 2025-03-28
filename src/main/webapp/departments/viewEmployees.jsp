<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Liste des Employés</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<%@include file="../utils/navbar.jsp" %>

<div class="container">
    <a class="btn btn-primary mb-3" href="${pageContext.request.contextPath}/employees">Retour à la liste des Departements</a>
    <h1>Liste des Employés dans le Département ${departmentName}</h1>
    <table class="table table-hover mt-3">
        <thead>
        <tr>
            <th>ID</th>
            <th>Nom</th>
            <th>Prenom</th>
            <th>Email</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="employee" items="${employees}">
            <tr>
                <td>${employee.id}</td>
                <td>${employee.nom}</td>
                <td>${employee.prenom}</td>
                <td>${employee.email}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<%@include file="../utils/footer.jsp" %>
</body>
</html>
