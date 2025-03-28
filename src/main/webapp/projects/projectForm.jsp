<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Formulaire Projet</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/form.css">
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
                <h2>${project == null ? 'Ajouter' : 'Modifier'} un projet</h2>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/projects" method="post">
                    <input type="hidden" name="action" value="${empty project ? 'add' : 'edit'}">
                    <input type="hidden" name="id" value="${not empty project ? project.id : ''}">
                    <div class="mb-3">
                        <label for="nom">Nom</label>
                        <input type="text" class="form-control" name="nom" id="nom"
                               value="${not empty project ? project.nom : ''}" required>
                    </div>
                    <div class="mb-3">
                        <label for="description">Description</label>
                        <textarea class="form-control" name="description" id="description"
                                  placeholder="Entrez la description du projet" required>
                            ${project != null ? project.description : ''}
                        </textarea>
                    </div>
                    <c:if test="${project != null}">
                        <div class="mb-3">
                            <label for="status">Statut</label>
                            <select class="form-select" name="status" id="status" required>
                                <option value="En cours" ${project.statut == 'En cours' ? 'selected' : ''}>En cours
                                </option>
                                <option value="En attente" ${project.statut == 'En attente' ? 'selected' : ''}>En
                                    attente
                                </option>
                                <option value="Termine" ${project.statut == 'Termine' ? 'selected' : ''}>Terminée
                                </option>
                                <option value="Annule" ${project.statut == 'Annule' ? 'selected' : ''}>Annulée
                                </option>
                                <option value="Bloque" ${project.statut == 'Bloque' ? 'selected' : ''}>Bloquée
                                </option>
                            </select>
                        </div>
                    </c:if>
                    <button class="btn btn-primary" type="submit">${project == null ? 'Ajouter' : 'Modifier'} le projet
                    </button>
                </form>
                <br>
                <a class="btn btn-danger" href="${pageContext.request.contextPath}/projects">Retour à la liste des
                    projets</a>
            </div>
        </div>
    </div>

    <%@include file="../utils/footer.jsp" %>
</body>
</html>

