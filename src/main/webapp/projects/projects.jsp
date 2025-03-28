<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestion des Projets</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
</head>
<body>
<%@include file="../utils/navbar.jsp" %>
<div class="container mb-5">
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success alert-dismissible fade show mt-3" role="alert">
                ${successMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show mt-3" role="alert">
                ${errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <div class="d-flex justify-content-between align-items-center mt-3 mb-4">
        <h1>Liste des Projets</h1>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/projects/projectForm.jsp">
            <i class="fas fa-plus me-2"></i>Ajouter un projet
        </a>
    </div>
    <div class="table-responsive">
        <table class="table table-striped table-hover">
            <thead class="table-light">
            <tr>
                <th scope="col">ID</th>
                <th scope="col">Nom</th>
                <th scope="col">Description</th>
                <th scope="col">Statut</th>
                <th scope="col">Date de début</th>
                <th scope="col" class="text-center">Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:if test="${empty projects}">
                <tr>
                    <td colspan="6" class="text-center">Aucun projet trouvé</td>
                </tr>
            </c:if>
            <c:forEach var="project" items="${projects}">
                <tr>
                    <td>${project.id}</td>
                    <td>${project.nom}</td>
                    <td>${project.description}</td>
                    <td>${project.statut}</td>
                    <td>${project.dateDebut}</td>
                    <td class="text-center">
                        <div class="btn-group" role="group" aria-label="Actions pour ce département">
                            <a class="btn btn-sm btn-warning" href="?action=edit&id=${project.id}"><i
                                    class="fas fa-edit"></i></a>
                            <a class="btn btn-sm btn-danger"
                               onclick="showDeleteModal('${project.id}', '${project.nom}');">
                                <i class="fas fa-trash"></i>
                            </a>
                            <button class="btn btn-sm btn-success assign-btn" data-bs-toggle="modal"
                                    data-bs-target="#assignModal"
                                    data-project-id="${project.id}">
                                <i class="fas fa-user-plus"></i>
                            </button>
                        </div>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty projects}">
                <tr>
                    <td colspan="4" class="text-center">Aucun projet trouvé</td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>

    <c:if test="${totalPages > 1}">
        <nav aria-label="Navigation des pages de projet">
            <ul class="pagination justify-content-center">
                <li class="page-item ${page <= 1 ? 'disabled' : ''}">
                    <a class="page-link" href="?page=${page - 1}"
                       aria-label="Précédent" ${page <= 1 ? 'tabindex="-1" aria-disabled="true"' : ''}>
                        <span aria-hidden="true">&laquo;</span> Précédent
                    </a>
                </li>

                <c:forEach var="i" begin="1" end="${totalPages}">
                    <c:choose>
                        <c:when test="${i == page}">
                            <li class="page-item active" aria-current="page">
                                <span class="page-link">${i}</span>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <li class="page-item">
                                <a class="page-link" href="?page=<c:out value='${i}'/>">${i}</a>
                            </li>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>

                <li class="page-item ${page >= totalPages ? 'disabled' : ''}">
                    <a class="page-link" href="?page=${page + 1}"
                       aria-label="Suivant" ${page >= totalPages ? 'tabindex="-1" aria-disabled="true"' : ''}>
                        Suivant <span aria-hidden="true">&raquo;</span>
                    </a>
                </li>
            </ul>
        </nav>
    </c:if>
</div>
<%@include file="../utils/footer.jsp" %>
<div class="modal fade" id="assignModal" tabindex="-1" aria-labelledby="assignModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="assignModalLabel">Assigner un Employé</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <form id="assignForm" action="?action=assign" method="post">
                    <input type="hidden" name="projectId" id="projectId">
                    <label for="employeeSelect">Sélectionner un employé :</label>
                    <select class="form-select" name="employeeId" id="employeeSelect">
                        <c:forEach var="employee" items="${employees}">
                            <option value="${employee.id}">${employee.nom}</option>
                        </c:forEach>
                    </select>
                    <div class="modal-footer">
                        <button type="submit" class="btn btn-primary">Assigner</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<!-- Modal de confirmation -->
<div class="modal fade" id="confirmDeleteModal" tabindex="-1" aria-labelledby="confirmDeleteModalLabel"
     aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="confirmDeleteModalLabel">Confirmation de suppression</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                Êtes-vous sûr de vouloir supprimer le projet <strong id="projectName"></strong> ?
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                <form id="deleteForm" method="post" action="">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="id" id="deleteId">
                    <button type="submit" class="btn btn-danger">Supprimer</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        let assignButtons = document.querySelectorAll(".assign-btn");
        assignButtons.forEach(button => {
            button.addEventListener("click", function () {
                document.getElementById("projectId").value = button.getAttribute("data-project-id");
            });
        });
    });

    function showDeleteModal(projectId, projectName) {
        document.getElementById("projectName").textContent = projectName;
        document.getElementById("deleteId").value = projectId;

        const myModal = new bootstrap.Modal(document.getElementById("confirmDeleteModal"));
        myModal.show();
    }
</script>
</body>
</html>
