<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Système de gestion des départements">
    <title>Gestion des Départements</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/form.css" rel="stylesheet">
</head>
<body>
<%@include file="../utils/navbar.jsp" %>

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

<div class="container mt-4">
    <div class="card shadow">
        <div class="card-header bg-primary text-white">
            <h3 class="m-0">${department == null ? 'Ajouter' : 'Modifier'} un département</h3>
        </div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/departments?action=${department == null ? 'add' : 'edit'}" method="post">
                <input type="hidden" name="id" value="${department != null ? department.id : ''}" />

                <div class="form-group mb-3">
                    <label for="departmentName" class="form-label fw-bold">Nom</label>
                    <input type="text" class="form-control" id="departmentName" name="name"
                           value="${department != null ? department.name : ''}" required
                           placeholder="Entrez le nom du département" />
                </div>

                <div class="form-group mb-3">
                    <label for="departmentDescription" class="form-label fw-bold">Description</label>
                    <textarea class="form-control" id="departmentDescription" name="description"
                              rows="4" placeholder="Entrez la description du département">${department != null ? department.description : ''}</textarea>
                </div>

                <div class="d-flex mt-4">
                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-save me-1"></i>${department == null ? 'Ajouter' : 'Modifier'} le département
                    </button>
                    <a href="${pageContext.request.contextPath}/departments/list" class="btn btn-secondary ms-2">
                        <i class="bi bi-arrow-left me-1"></i>Retour à la liste des départements
                    </a>
                </div>
            </form>
        </div>
    </div>
</div>
<%@include file="../utils/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>