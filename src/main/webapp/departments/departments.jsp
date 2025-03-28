<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
<div class="container mb-5">
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

    <div class="d-flex justify-content-between align-items-center mt-3 mb-4">
        <h1>Liste des Départements</h1>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/departments/departmentForm.jsp">
            <i class="fas fa-plus me-2"></i>Ajouter un département
        </a>
    </div>

    <div class="table-responsive">
        <table class="table table-striped table-hover">
            <thead class="table-light">
            <tr>
                <th scope="col">ID</th>
                <th scope="col">Nom</th>
                <th scope="col">Description</th>
                <th scope="col" class="text-center">Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="department" items="${departments}">
                <tr>
                    <td><c:out value="${department.id}"/></td>
                    <td><c:out value="${department.name}"/></td>
                    <td><c:out value="${department.description}"/></td>
                    <td class="text-center">
                        <div class="btn-group" role="group" aria-label="Actions pour ce département">
                            <a class="btn btn-sm btn-warning" href="?action=edit&id=<c:out value='${department.id}'/>"
                               data-bs-toggle="tooltip" title="Modifier">
                                <i class="fas fa-edit"></i><span class="visually-hidden">Modifier</span>
                            </a>
                            <button type="button" class="btn btn-sm btn-danger"
                                    onclick="showDeleteModal('<c:out value='${department.id}'/>', '<c:out
                                            value='${department.name}'/>');"
                                    data-bs-toggle="tooltip" title="Supprimer">
                                <i class="fas fa-trash"></i><span class="visually-hidden">Supprimer</span>
                            </button>
                            <button type="button" class="btn btn-sm btn-success assign-btn"
                                    data-bs-toggle="modal" data-bs-target="#assignModal"
                                    data-department-id="<c:out value='${department.id}'/>"
                                    title="Assigner un employé">
                                <i class="fas fa-user-plus"></i><span class="visually-hidden">Assigner</span>
                            </button>
                            <a class="btn btn-sm btn-info"
                               href="?action=viewEmployees&id=<c:out value='${department.id}'/>"
                               data-bs-toggle="tooltip" title="Voir les employés">
                                <i class="fas fa-users"></i><span class="visually-hidden">Voir employés</span>
                            </a>
                        </div>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty departments}">
                <tr>
                    <td colspan="4" class="text-center">Aucun département trouvé</td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>

    <c:if test="${totalPages > 1}">
        <nav aria-label="Navigation des pages de départements">
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
                                <span class="page-link"><c:out value="${i}"/></span>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <li class="page-item">
                                <a class="page-link" href="?page=<c:out value='${i}'/>"><c:out value="${i}"/></a>
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

<!-- Assign Employee Modal -->
<div class="modal fade" id="assignModal" tabindex="-1" aria-labelledby="assignModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="assignModalLabel">Assigner un Employé</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form id="assignForm" action="?action=assign" method="post">
                <div class="modal-body">
                    <input type="hidden" name="departmentId" id="departmentId">

                    <div class="mb-3">
                        <label for="employeeSelect" class="form-label">Sélectionner un employé :</label>
                        <select class="form-select" name="employeeId" id="employeeSelect" required>
                            <option value="">Sélectionner un employé</option>
                            <c:forEach var="employee" items="${employees}">
                                <option value="<c:out value='${employee.id}'/>">
                                    <c:out value="${employee.nom} ${employee.prenom}"/>
                                </option>
                            </c:forEach>
                        </select>
                        <div class="invalid-feedback">Veuillez sélectionner un employé.</div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                    <button type="submit" class="btn btn-primary">Assigner</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Delete Confirmation Modal -->
<div class="modal fade" id="confirmDeleteModal" tabindex="-1" aria-labelledby="confirmDeleteModalLabel"
     aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="confirmDeleteModalLabel">Confirmation de suppression</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                Êtes-vous sûr de vouloir supprimer le département <strong id="departmentName"></strong> ?
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

<!-- JavaScript -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Initialize tooltips
    document.addEventListener("DOMContentLoaded", function () {
        // Enable Bootstrap tooltips
        const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl, {
                boundary: document.body
            });
        });

        // Handle assign button clicks
        const assignButtons = document.querySelectorAll(".assign-btn");
        assignButtons.forEach(button => {
            button.addEventListener("click", function () {
                document.getElementById("departmentId").value = this.getAttribute("data-department-id");
            });
        });

        // Form validation
        const forms = document.querySelectorAll('.needs-validation');
        Array.from(forms).forEach(form => {
            form.addEventListener('submit', event => {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            }, false);
        });
    });

    // Delete modal handler
    function showDeleteModal(departmentId, departmentName) {
        document.getElementById("departmentName").textContent = departmentName;
        document.getElementById("deleteId").value = departmentId;

        const modal = new bootstrap.Modal(document.getElementById("confirmDeleteModal"));
        modal.show();
    }
</script>
</body>
</html>