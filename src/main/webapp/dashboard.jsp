<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestion Entreprise - Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/dashboard.css">
</head>
<body>
<%@include file="utils/navbar.jsp" %>
<!-- Dash Header -->
<div class="dash-header">
    <div class="container">
        <div class="row align-items-center">
            <div class="col-md-6">
                <h2><i class="fas fa-tachometer-alt me-2"></i>Dashboard</h2>
                <p class="mb-0">Bienvenue sur votre portail de gestion d'entreprise</p>
            </div>
            <div class="col-md-6 text-md-end">
                <p class="mb-0">
                    <i class="far fa-calendar-alt me-1"></i> <span id="currentDate">22 Mars 2025</span> |
                    <i class="far fa-clock me-1"></i> <span id="currentTime">Chargement...</span>
                </p>
            </div>
        </div>
    </div>
</div>

<div class="container">
    <!-- User Welcome -->
    <div class="user-welcome mb-4">
        <div class="row align-items-center">
            <div class="col-md-2 text-center">
                <img src="img/user-avatar.png" alt="User Avatar" class="rounded-circle"
                     style="width: 80px; height: 80px;">
            </div>
            <div class="col-md-10">
                <h4>Bonjour, ${userName} ! <small class="text-muted">(${role})</small></h4>
            </div>
        </div>
    </div>

    <!-- Stats Cards -->
    <!-- ... (les statistiques restent inchangées) ... -->

    <!-- Quick Actions - Adaptées selon le rôle -->
    <div class="quick-actions mb-4">
        <h5 class="mb-3"><i class="fas fa-bolt me-2"></i>Actions rapides</h5>
        <div class="row">
            <!-- Nouvel employé - Seulement pour Admin -->
            <div class="col-md-3 col-sm-6 mb-2">
                <c:choose>
                    <c:when test="${role == 'Administrateur'}">
                        <a href="${pageContext.request.contextPath}/employees/employeeForm.jsp"
                           class="btn btn-outline-primary w-100">
                            <i class="fas fa-user-plus me-2"></i>Nouvel employé
                        </a>
                    </c:when>
                    <c:otherwise>
                        <button class="btn btn-outline-secondary w-100" disabled>
                            <i class="fas fa-user-plus me-2"></i>Nouvel employé
                        </button>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Nouveau département - Pour Admin et Gestionnaire -->
            <div class="col-md-3 col-sm-6 mb-2">
                <c:choose>
                    <c:when test="${role == 'Administrateur' || role == 'Gestionnaire'}">
                        <a href="${pageContext.request.contextPath}/departments/departmentForm.jsp"
                           class="btn btn-outline-success w-100">
                            <i class="fas fa-sitemap me-2"></i>Nouveau département
                        </a>
                    </c:when>
                    <c:otherwise>
                        <button class="btn btn-outline-secondary w-100" disabled>
                            <i class="fas fa-sitemap me-2"></i>Nouveau département
                        </button>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Nouveau projet - Pour Admin et Gestionnaire -->
            <div class="col-md-3 col-sm-6 mb-2">
                <c:choose>
                    <c:when test="${role == 'Administrateur' || role == 'Gestionnaire'}">
                        <a href="${pageContext.request.contextPath}/projects/projectForm.jsp"
                           class="btn btn-outline-warning w-100">
                            <i class="fas fa-clipboard-list me-2"></i>Nouveau projet
                        </a>
                    </c:when>
                    <c:otherwise>
                        <button class="btn btn-outline-secondary w-100" disabled>
                            <i class="fas fa-clipboard-list me-2"></i>Nouveau projet
                        </button>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Nouvelle tâche - Pour tous -->
            <div class="col-md-3 col-sm-6 mb-2">
                <a href="${pageContext.request.contextPath}/tasks/taskForm.jsp" class="btn btn-outline-danger w-100">
                    <i class="fas fa-chart-bar me-2"></i>Nouvelle tâche
                </a>
            </div>
        </div>
    </div>

    <!-- Main Features Cards - Adaptées selon le rôle -->
    <h4 class="mb-3"><i class="fas fa-th-large me-2"></i>Gestion principale</h4>
    <div class="row mb-4">
        <!-- Carte Employés - Seulement pour Admin -->
        <div class="col-md-3 col-sm-6 mb-4">
            <div class="feature-card card h-100 ${role != 'Administrateur' ? 'disabled-card' : ''}">
                <img src="img/employee-icon.png" alt="Employés" class="card-img-top">
                <div class="card-body">
                    <div class="feature-icon">
                        <i class="fas fa-users"></i>
                    </div>
                    <h5 class="card-title">Employés</h5>
                    <p class="card-text">Gérer les employés de l'entreprise et leurs informations personnelles</p>
                    <c:choose>
                        <c:when test="${role == 'Administrateur'}">
                            <a href="employees" class="btn btn-primary">
                                <i class="fas fa-arrow-right me-1"></i> Accéder
                            </a>
                        </c:when>
                        <c:otherwise>
                            <button class="btn btn-secondary" disabled>
                                <i class="fas fa-lock me-1"></i> Accès restreint
                            </button>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- Carte Départements - Pour Admin et Gestionnaire -->
        <div class="col-md-3 col-sm-6 mb-4">
            <div class="feature-card card h-100 ${role == 'Employe' ? 'disabled-card' : ''}">
                <img src="img/department-icon.png" alt="Départements" class="card-img-top">
                <div class="card-body">
                    <div class="feature-icon">
                        <i class="fas fa-sitemap"></i>
                    </div>
                    <h5 class="card-title">Départements</h5>
                    <p class="card-text">Gérer les départements et les équipes de l'entreprise</p>
                    <c:choose>
                        <c:when test="${role != 'Employe'}">
                            <a href="departments" class="btn btn-primary">
                                <i class="fas fa-arrow-right me-1"></i> Accéder
                            </a>
                        </c:when>
                        <c:otherwise>
                            <button class="btn btn-secondary" disabled>
                                <i class="fas fa-lock me-1"></i> Accès restreint
                            </button>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- Carte Projets - Pour Admin et Gestionnaire -->
        <div class="col-md-3 col-sm-6 mb-4">
            <div class="feature-card card h-100 ${role == 'Employe' ? 'disabled-card' : ''}">
                <img src="img/project-icon.jpg" alt="Projets" class="card-img-top">
                <div class="card-body">
                    <div class="feature-icon">
                        <i class="fas fa-project-diagram"></i>
                    </div>
                    <h5 class="card-title">Projets</h5>
                    <p class="card-text">Gérer les projets et suivre leur progression</p>
                    <c:choose>
                        <c:when test="${role != 'Employe'}">
                            <a href="projects" class="btn btn-primary">
                                <i class="fas fa-arrow-right me-1"></i> Accéder
                            </a>
                        </c:when>
                        <c:otherwise>
                            <button class="btn btn-secondary" disabled>
                                <i class="fas fa-lock me-1"></i> Accès restreint
                            </button>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- Carte Tâches - Pour tous -->
        <div class="col-md-3 col-sm-6 mb-4">
            <div class="feature-card card h-100">
                <img src="img/task-icon.jpg" alt="Tâches" class="card-img-top">
                <div class="card-body">
                    <div class="feature-icon">
                        <i class="fas fa-tasks"></i>
                    </div>
                    <h5 class="card-title">Tâches</h5>
                    <p class="card-text">Gérer les tâches et les assignations</p>
                    <a href="tasks" class="btn btn-primary">
                        <i class="fas fa-arrow-right me-1"></i> Accéder
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<%@include file="utils/footer.jsp" %>

<script>
    // Affichage de la date et de l'heure
    function updateDateTime() {
        const now = new Date();
        const options = {weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'};
        document.getElementById('currentDate').textContent = now.toLocaleDateString('fr-FR', options);

        const timeOptions = {hour: '2-digit', minute: '2-digit', second: '2-digit'};
        document.getElementById('currentTime').textContent = now.toLocaleTimeString('fr-FR', timeOptions);
    }

    updateDateTime();
    setInterval(updateDateTime, 1000);
</script>
</body>
</html>