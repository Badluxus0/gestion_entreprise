<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Connexion</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="login/login.css">
</head>
<body>
<div class="half">
    <div class="bg order-1 order-md-2"
         style="background-image: url('${pageContext.request.contextPath}/img/Background_home.png')">
        <div class="overlay"></div>
    </div>
    <div class="contents order-2 order-md-1">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-12">
                    <div class="form-block">
                        <div class="text-center mb-4">
                            <h3 class="fw-bold">Connexion</h3>
                        </div>

                        <% String error = (String) request.getAttribute("error"); %>
                        <% if (error != null) { %>
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="fas fa-exclamation-circle me-2"></i>
                            <%= error %>
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                        <% } %>

                        <form action="${pageContext.request.contextPath}/auth" method="post">
                            <div class="form-group">
                                <label for="email">Email</label>
                                <div class="input-group">
                                    <span class="input-group-text">
                                        <i class="fas fa-envelope"></i>
                                    </span>
                                    <input
                                            type="email"
                                            name="email"
                                            id="email"
                                            class="form-control"
                                            placeholder="Votre adresse email"
                                            required
                                    />
                                </div>
                                <small class="form-text text-muted email-validation"></small>
                            </div>

                            <div class="form-group">
                                <label for="password">Mot de passe</label>
                                <div class="input-group">
                                    <span class="input-group-text">
                                        <i class="fas fa-lock"></i>
                                    </span>
                                    <input
                                            type="password"
                                            name="password"
                                            id="password"
                                            class="form-control"
                                            placeholder="Créer un mot de passe"
                                            required
                                    />
                                    <button class="btn btn-outline-secondary" type="button" id="togglePassword">
                                        <i class="fas fa-eye"></i>
                                    </button>
                                </div>
                                <div class="password-strength mt-2">
                                    <div class="password-strength-meter" id="passwordStrengthMeter"></div>
                                </div>
                                <small class="form-text text-muted">Le mot de passe doit contenir au moins 8 caractères,
                                    incluant des lettres et des chiffres</small>
                            </div>
                            <div class="form-group">
                                <button
                                        type="submit"
                                        class="btn btn-primary w-100"
                                >
                                    <i class="fas fa-key me-2"></i>Se connecter
                                </button>
                            </div>
                            <div class="text-center mt-4">
                                <p class="mb-0">
                                    <a href="${pageContext.request.contextPath}/register" class="fw-bold">Créer un
                                        compte</a>
                                </p>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    const togglePassword = document.getElementById('togglePassword');
    const password = document.getElementById('password');
    togglePassword.addEventListener('click', function () {
        const type = password.getAttribute('type') === 'password' ? 'text' : 'password';
        password.setAttribute('type', type);
        this.querySelector('i').classList.toggle('fa-eye');
        this.querySelector('i').classList.toggle('fa-eye-slash');
    });
</script>

<!-- Bootstrap JS Bundle with Popper -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
