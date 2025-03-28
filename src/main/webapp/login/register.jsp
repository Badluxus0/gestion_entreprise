<%@ page contentType="text/html;charset=UTF-8" %>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inscription - Votre Application</title>
    <!-- Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <!-- Google Fonts - Nunito -->
    <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@300;400;600;700&display=swap" rel="stylesheet">
    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/login/login.css">
</head>
<body>
<div class="half">
    <div class="bg order-1 order-md-2"
         style="background-image: url('${pageContext.request.contextPath}/img/Background_home.png');">
        <div class="overlay"></div>
    </div>
    <div class="contents order-2 order-md-1">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-12">
                    <div class="form-block">
                        <div class="text-center mb-4">
                            <h3 class="fw-bold">Créer un compte</h3>
                            <p class="text-muted">Veuillez remplir le formulaire ci-dessous pour vous inscrire</p>
                        </div>

                        <% String error = (String) request.getAttribute("error"); %>
                        <% if (error != null) { %>
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="fas fa-exclamation-circle me-2"></i>
                            <%= error %>
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                        <% } %>

                        <form action="${pageContext.request.contextPath}/register" method="post" id="registrationForm">
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
                                <small class="form-text text-muted">Le mot de passe doit contenir au moins 8 caractères,
                                    incluant des lettres et des chiffres</small>
                            </div>

                            <div class="form-group">
                                <label for="confirmPassword">Confirmer le mot de passe</label>
                                <div class="input-group">
                                    <span class="input-group-text">
                                        <i class="fas fa-lock"></i>
                                    </span>
                                    <input
                                            type="password"
                                            name="confirmPassword"
                                            id="confirmPassword"
                                            class="form-control"
                                            placeholder="Confirmer votre mot de passe"
                                            required
                                    />
                                    <button class="btn btn-outline-secondary" type="button" id="toggleConfPassword">
                                        <i class="fas fa-eye"></i>
                                    </button>
                                </div>
                                <small class="form-text text-muted password-match"></small>
                            </div>

                            <div class="form-group">
                                <button
                                        type="submit"
                                        class="btn btn-primary w-100"
                                >
                                    <i class="fas fa-user-plus me-2"></i>S'inscrire
                                </button>
                            </div>

                            <div class="text-center mt-4">
                                <p class="mb-0">
                                    Vous avez déjà un compte?
                                    <a href="${pageContext.request.contextPath}/auth" class="fw-bold">Se connecter</a>
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
    const toggleConfPassword = document.getElementById('toggleConfPassword');
    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('confirmPassword');

    togglePassword.addEventListener('click', function () {
        const type = password.getAttribute('type') === 'password' ? 'text' : 'password';
        password.setAttribute('type', type);
        this.querySelector('i').classList.toggle('fa-eye');
        this.querySelector('i').classList.toggle('fa-eye-slash');
    });

    toggleConfPassword.addEventListener('click', function () {
        const type = confirmPassword.getAttribute('type') === 'password' ? 'text' : 'password';
        confirmPassword.setAttribute('type', type);
        this.querySelector('i').classList.toggle('fa-eye');
        this.querySelector('i').classList.toggle('fa-eye-slash');
    });
</script>

<!-- Bootstrap JS Bundle with Popper -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>