<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.esmt.gestionentreprise.service.JwtTokenService" %>
<%@ page import="com.esmt.gestionentreprise.utils.CookieUtil" %>

<%
    // Récupération du token depuis les cookies
    String token = CookieUtil.getCookieValue(request, "token");

// Extraction du rôle si token existe
    String userRole = null;
    if (token != null) {
        JwtTokenService jwtService = new JwtTokenService();
        userRole = jwtService.extractRole(token);
        request.setAttribute("userRole", userRole);
    }
%>

<style>
    .navbar {
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    }

    .navbar-brand {
        font-weight: 700;
        letter-spacing: 1px;
    }

    .nav-item.disabled {
        pointer-events: none;
        opacity: 0.6;
    }
</style>

<header>
    <nav class="navbar navbar-expand-md navbar-dark bg-dark">
        <div class="container">
            <a href="<c:url value="/dashboard"/>" class="navbar-brand">
                <i class="fas fa-building me-2"></i>Gestion Entreprise
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarContent">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item">
                        <a class="nav-link active" href="<c:url value="/dashboard"/>">
                            <i class="fas fa-tachometer-alt me-1"></i>Dashboard
                        </a>
                    </li>

                    <!-- Menu Employés - Visible seulement pour Admin -->
                    <li class="nav-item <c:if test='${requestScope.userRole != "Administrateur"}'>disabled</c:if>">
                        <a class="nav-link" href="<c:url value="/employees"/>">
                            <i class="fas fa-users me-1"></i>Employes
                        </a>
                    </li>

                    <!-- Menu Départements - Visible pour Admin et Gestionnaire -->
                    <li class="nav-item <c:if test='${requestScope.userRole == "Employe"}'>disabled</c:if>">
                        <a class="nav-link" href="<c:url value="/departments"/>">
                            <i class="fas fa-sitemap me-1"></i>Departements
                        </a>
                    </li>

                    <!-- Menu Projets - Visible pour Admin et Gestionnaire -->
                    <li class="nav-item <c:if test='${requestScope.userRole == "Employe"}'>disabled</c:if>">
                        <a class="nav-link" href="<c:url value="/projects"/>">
                            <i class="fas fa-project-diagram me-1"></i>Projets
                        </a>
                    </li>

                    <!-- Menu Tâches - Visible pour tous -->
                    <li class="nav-item">
                        <a class="nav-link" href="<c:url value="/tasks"/>">
                            <i class="fas fa-tasks me-1"></i>Taches
                        </a>
                    </li>
                </ul>

                <div class="d-flex align-items-center">
                    <span class="text-light me-3">
                        <i class="fas fa-user-circle me-1"></i>
                        <c:if test="${not empty userRole}">
                            ${userRole}
                        </c:if>
                    </span>
                    <a href="#" class="btn btn-danger" onclick="showLogoutModal()">
                        <i class="fas fa-sign-out-alt me-1"></i>Deconnexion
                    </a>
                </div>
            </div>
        </div>
    </nav>
</header>

<!-- Modal de confirmation de déconnexion -->
<div class="modal fade" id="confirmLogoutModal" tabindex="-1" aria-labelledby="confirmLogoutModalLabel"
     aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="confirmLogoutModalLabel">Confirmation de deconnexion</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                Etes-vous sur de vouloir vous deconnecter ?
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                <a href="<c:url value="/logout"/>" class="btn btn-danger">Deconnexion</a>
            </div>
        </div>
    </div>
</div>

<script>
    function showLogoutModal() {
        var myModal = new bootstrap.Modal(document.getElementById("confirmLogoutModal"));
        myModal.show();
    }
</script>