<%--employees.jsp--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Système de gestion des employés">
    <title>Gestion des Employés</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/employees/employees.css" rel="stylesheet">
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

    <div class="d-flex justify-content-between align-items-center  mt-3 mb-3">
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/employees/employeeForm.jsp">
            <i class="fas fa-plus me-2"></i>Ajouter un employé
        </a>
        <div class="container">
            <form action="${pageContext.request.contextPath}/employees" method="get">
                <div class="input-group">
                    <label>
                        <input type="text" class="form-control" name="search"
                               placeholder="Rechercher un employé">
                    </label>
                    <input type="hidden" name="action" value="search">
                    <button class="btn btn-outline-secondary" type="submit"><i class="fas fa-search"></i></button>
                    <c:if test="${not empty param.search}">
                        <a href="${pageContext.request.contextPath}/employees" class="btn btn-outline-danger">Réinitialiser</a>
                    </c:if>
                </div>
            </form>
        </div>
    </div>
    <h1>Liste des Employés</h1>
    <div class="table-responsive">
        <table class="table table-striped table-hover">
            <thead class="table-light">
            <tr>
                <th scope="col">ID</th>
                <th scope="col">Nom</th>
                <th scope="col">Prénom</th>
                <th scope="col">Role</th>
                <th scope="col" class="text-center">Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="employee" items="${employees}">
                <tr>
                    <td>${employee.id}</td>
                    <td>${employee.nom}</td>
                    <td>${employee.prenom}</td>
                    <td>${employee.role}</td>
                    <td class="text-center">
                        <div class="btn-group" role="group" aria-label="Actions pour ce département">
                            <a class="btn btn-sm btn-warning" href="?action=edit&id=${employee.id}"><i
                                    class="fas fa-edit"></i><span class="visually-hidden">Modifier</span>
                            </a>
                            <button type="button" class="btn btn-sm btn-danger"
                                    onclick="showDeleteModal('${employee.id}', '${employee.nom} ${employee.prenom}')"
                                    data-bs-toggle="tooltip"
                                    title="Supprimer">
                                <i class="fas fa-trash"></i><span class="visually-hidden">Supprimer</span>
                            </button>
                        </div>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty employees}">
                <tr>
                    <td colspan="5" class="text-center">Aucun employé trouvé</td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>

    <c:if test="${totalPages > 1}">
        <nav aria-label="Navigation des pages d'employés">
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
                Êtes-vous sûr de vouloir supprimer l'employé <strong id="employeeName"></strong> ?
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
    function showDeleteModal(employeeId, employeeName) {
        document.getElementById("employeeName").textContent = employeeName;
        document.getElementById("deleteId").value = employeeId;

        const myModal = new bootstrap.Modal(document.getElementById("confirmDeleteModal"));
        myModal.show();
    }
</script>
</body>
</html>
