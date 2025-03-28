<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Acces non autorisé</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="unauthorized.css">
</head>
<body>
<div class="container unauthorized-container">
    <div class="card p-4">
        <div class="text-center">
            <div class="icon-container">
                <i class="bi bi-shield-lock"></i>
            </div>
            <h1 class="h3 mb-3 fw-normal">Accès non autorisé</h1>
            <p class="mb-4">Désolé, vous n'avez pas les permissions nécessaires pour accéder à cette ressource.</p>

            <c:choose>
                <c:when test="${not empty sessionScope.user}">
                    <p>Vous êtes connecté en tant que <strong>${sessionScope.user.username}</strong>
                        (Rôle: ${sessionScope.user.role}).</p>
                </c:when>
                <c:otherwise>
                    <p>Vous n'êtes pas connecté.</p>
                </c:otherwise>
            </c:choose>

            <div class="d-grid gap-2">
                <c:choose>
                    <c:when test="${not empty sessionScope.user}">
                        <a href="${pageContext.request.contextPath}/dashboard"
                           class="btn btn-primary">
                            Retour au tableau de bord
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/auth"
                           class="btn btn-primary">
                            Se connecter
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap Icons -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
