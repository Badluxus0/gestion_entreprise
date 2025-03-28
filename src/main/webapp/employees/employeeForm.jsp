<%--employeeForm.jsp--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Formulaire Employé</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/form.css" rel="stylesheet">
</head>
<body>
<%@include file="../utils/navbar.jsp" %>

<div class="container mt-4">
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success alert-dismissible fade show mt-3" role="alert">
            <c:out value="${successMessage}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show mt-3" role="alert">
            <c:out value="${errorMessage}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <div class="d-flex justify-content-center">
        <div class="card w-50 shadow">
            <div class="card-header bg-primary text-white">
                <h3 class="m-0">${empty employee ? 'Ajouter' : 'Modifier'} un employé</h3>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/employees" method="post">
                    <input type="hidden" name="action" value="${empty employee ? 'add' : 'edit'}">
                    <input type="hidden" name="id" value="${not empty employee ? employee.id : ''}">
                    <div class="mb-3">
                        <label for="nom" class="form-label">Nom</label>
                        <input type="text" class="form-control" name="nom" id="nom"
                               value="${not empty employee ? employee.nom : ''}" required>
                    </div>
                    <div class="mb-3">
                        <label for="prenom" class="form-label">Prénom</label>
                        <input type="text" class="form-control" name="prenom" id="prenom"
                               value="${not empty employee ? employee.prenom : ''}" required>
                    </div>
                    <div class="mb-3">
                        <label for="email" class="form-label">Email</label>
                        <input type="email" class="form-control" name="email" id="email"
                               value="${not empty employee ? employee.email : ''}" required>
                    </div>
                    <div class="mb-3">
                        <label for="role" class="form-label">Rôle</label>
                        <select class="form-select" name="role" id="role" required>
                            <option value="">-- Sélectionner un rôle --</option>
                            <option value="Administrateur" ${not empty employee && employee.role == 'Administrateur' ? 'selected' : ''}>
                                Administrateur
                            </option>
                            <option value="Gestionnaire" ${not empty employee && employee.role == 'Gestionnaire' ? 'selected' : ''}>
                                Gestionnaire
                            </option>
                            <option value="Employe" ${not empty employee && employee.role == 'Employe' ? 'selected' : ''}>
                                Employé
                            </option>
                        </select>
                    </div>

                    <div class="mt-4">
                        <button class="btn btn-primary" type="submit">
                            ${empty employee ? 'Ajouter' : 'Modifier'} l'employé
                        </button>
                        <a class="btn btn-secondary ms-2" href="${pageContext.request.contextPath}/employees">
                            Annuler
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<%@include file="../utils/footer.jsp" %>
</body>
</html>