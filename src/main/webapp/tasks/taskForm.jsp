<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Formulaire Tâche</title>
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
                <h2>${task == null ? 'Ajouter' : 'Modifier'} une tâche</h2>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/tasks" method="post">
                    <input type="hidden" name="action" value="${empty task ? 'add' : 'edit'}">
                    <input type="hidden" name="id" value="${task != null ? task.id : ''}">

                    <div class="mb-3">
                        <label for="description">Description</label>
                        <textarea class="form-control" name="description" id="description" rows="4"
                                  placeholder="Entrez la description de la tache" required>${task != null ? task.description : ''}</textarea>
                    </div>

                    <c:if test="${task != null}">
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
                    <button class="btn btn-primary" type="submit">${task == null ? 'Ajouter' : 'Modifier'} la tâche
                    </button>
                </form>
                <br>
                <a class="btn btn-danger" href="${pageContext.request.contextPath}/tasks">Retour à la liste des
                    tâches</a>
            </div>
        </div>
    </div>

    <%@include file="../utils/footer.jsp" %>
</body>
</html>
