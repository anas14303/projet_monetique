// Fonction utilitaire pour afficher une notification
function showAlert(icon, title, text) {
    return Swal.fire({
        icon: icon,
        title: title,
        text: text,
        toast: true,
        position: 'top-end',
        showConfirmButton: false,
        timer: 3000,
        timerProgressBar: true,
        didOpen: (toast) => {
            toast.addEventListener('mouseenter', Swal.stopTimer);
            toast.addEventListener('mouseleave', Swal.resumeTimer);
        }
    });
}

// Gestion de la suppression d'un utilisateur
function handleDeleteUser(button) {
    const userId = button.getAttribute('data-id');
    const username = button.getAttribute('data-username');
    
    Swal.fire({
        title: 'Confirmer la suppression',
        html: `
            <div class="text-center">
                <i class="fas fa-exclamation-triangle text-warning fa-4x mb-3"></i>
                <h5>Êtes-vous sûr de vouloir supprimer l'utilisateur <strong>${username}</strong> ?</h5>
                <p class="text-muted">Cette action est irréversible.</p>
            </div>
        `,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: '<i class="fas fa-trash me-1"></i> Oui, supprimer',
        cancelButtonText: '<i class="fas fa-times me-1"></i> Annuler',
        reverseButtons: true,
        customClass: {
            confirmButton: 'btn btn-danger me-2',
            cancelButton: 'btn btn-secondary ms-2'
        },
        buttonsStyling: false,
        showLoaderOnConfirm: true,
        preConfirm: async () => {
            try {
                const response = await fetch(`/admin/users/${userId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    [csrfHeader]: csrfToken
                },
                credentials: 'same-origin'
            });
                
                if (!response.ok) {
                    throw new Error('Erreur lors de la suppression');
                }
                
                return await response.json();
            } catch (error) {
                Swal.showValidationMessage(`Erreur: ${error.message}`);
                return false;
            }
        },
        allowOutsideClick: () => !Swal.isLoading()
    }).then((result) => {
        if (result.isConfirmed && result.value) {
            // Supprimer la ligne du tableau
            const row = button.closest('tr');
            row.style.transition = 'opacity 0.3s';
            row.style.opacity = '0';
            
            setTimeout(() => {
                row.remove();
                showAlert('success', 'Succès', 'Utilisateur supprimé avec succès');
            }, 300);
        }
    });
}

// Gestion de l'activation/désactivation d'un utilisateur
function handleToggleActive(button) {
    const userId = button.getAttribute('data-id');
    const username = button.getAttribute('data-username');
    const isActive = button.getAttribute('data-active') === 'true';
    const newStatus = !isActive;
    
    // Désactiver le bouton pendant le traitement
    const originalHTML = button.innerHTML;
    button.disabled = true;
    button.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>';
    
    fetch(`/admin/users/${userId}/toggle-active`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': csrfToken
        },
        credentials: 'same-origin'
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(err => { 
                throw new Error(err.message || 'Erreur lors de la mise à jour'); 
            });
        }
        return response.json();
    })
    .then(data => {
        // Mettre à jour l'interface utilisateur
        const row = button.closest('tr');
        const statusBadge = row.querySelector('.status-badge');
        const icon = button.querySelector('i');
        
        // Mettre à jour les classes et le texte
        if (newStatus) {
            row.classList.remove('table-light');
            row.classList.add('table-success');
            statusBadge.className = 'badge bg-success';
            statusBadge.innerHTML = '<i class="fas fa-check-circle me-1"></i> Actif';
            icon.className = 'fas fa-toggle-on';
            button.title = 'Désactiver';
            button.classList.remove('btn-outline-secondary');
            button.classList.add('btn-outline-success');
        } else {
            row.classList.remove('table-success');
            row.classList.add('table-light');
            statusBadge.className = 'badge bg-secondary';
            statusBadge.innerHTML = '<i class="fas fa-times-circle me-1"></i> Inactif';
            icon.className = 'fas fa-toggle-off';
            button.title = 'Activer';
            button.classList.remove('btn-outline-success');
            button.classList.add('btn-outline-secondary');
        }
        
        // Mettre à jour l'attribut data-active
        button.setAttribute('data-active', newStatus);
        
        // Réactiver le bouton
        button.disabled = false;
        
        return showAlert('success', 'Succès', data.message || 'Statut mis à jour avec succès');
    })
    .catch(error => {
        console.error('Error:', error);
        button.disabled = false;
        button.innerHTML = originalHTML;
        return showAlert('error', 'Erreur', error.message || 'Une erreur est survenue lors de la mise à jour');
    });
}

// Gestion de la réinitialisation du mot de passe
function handleResetPassword(button) {
    const userId = button.getAttribute('data-id');
    const username = button.getAttribute('data-username');
    
    Swal.fire({
        title: 'Réinitialiser le mot de passe',
        html: `
            <div class="text-center">
                <i class="fas fa-key text-info fa-4x mb-3"></i>
                <p>Êtes-vous sûr de vouloir réinitialiser le mot de passe de <strong>${username}</strong> ?</p>
                <p class="text-muted small">Un nouveau mot de passe temporaire sera généré et affiché.</p>
            </div>
        `,
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: '<i class="fas fa-redo me-1"></i> Réinitialiser',
        cancelButtonText: '<i class="fas fa-times me-1"></i> Annuler',
        reverseButtons: true,
        customClass: {
            confirmButton: 'btn btn-info me-2',
            cancelButton: 'btn btn-secondary ms-2'
        },
        buttonsStyling: false,
        showLoaderOnConfirm: true,
        preConfirm: async () => {
            try {
                const response = await fetch(`/admin/users/${userId}/reset-password`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CSRF-TOKEN': csrfToken
                    },
                    credentials: 'same-origin'
                });
                
                if (!response.ok) {
                    const error = await response.json();
                    throw new Error(error.message || 'Erreur lors de la réinitialisation du mot de passe');
                }
                
                return await response.json();
            } catch (error) {
                Swal.showValidationMessage(`Erreur: ${error.message}`);
                return false;
            }
        },
        allowOutsideClick: () => !Swal.isLoading()
    }).then((result) => {
        if (result.isConfirmed && result.value) {
            const newPassword = result.value.newPassword || 'password123';
            
            Swal.fire({
                title: 'Mot de passe réinitialisé',
                html: `
                    <div class="text-center">
                        <i class="fas fa-key text-success fa-4x mb-3"></i>
                        <p>Le mot de passe de <strong>${username}</strong> a été réinitialisé avec succès.</p>
                        <div class="alert alert-warning text-start mt-3">
                            <p class="mb-1"><strong>Nouveau mot de passe :</strong></p>
                            <div class="d-flex align-items-center">
                                <input type="text" 
                                       class="form-control form-control-sm" 
                                       id="newPassword" 
                                       value="${newPassword}" 
                                       readonly
                                       onclick="this.select()">
                                <button class="btn btn-sm btn-outline-secondary ms-2" onclick="copyToClipboard('${newPassword}')">
                                    <i class="fas fa-copy"></i>
                                </button>
                            </div>
                        </div>
                        <p class="text-muted small mt-2">Demandez à l'utilisateur de changer ce mot de passe dès sa première connexion.</p>
                    </div>
                `,
                confirmButtonText: 'Fermer',
                customClass: {
                    confirmButton: 'btn btn-primary'
                }
            });
        }
    });
}

// Fonction utilitaire pour copier du texte dans le presse-papier
function copyToClipboard(text) {
    navigator.clipboard.writeText(text).then(() => {
        const copyButton = event.target.closest('button');
        const originalHTML = copyButton.innerHTML;
        copyButton.innerHTML = '<i class="fas fa-check"></i>';
        copyButton.classList.remove('btn-outline-secondary');
        copyButton.classList.add('btn-outline-success');
        
        setTimeout(() => {
            copyButton.innerHTML = originalHTML;
            copyButton.classList.remove('btn-outline-success');
            copyButton.classList.add('btn-outline-secondary');
        }, 2000);
    }).catch(err => {
        console.error('Erreur lors de la copie :', err);
        showAlert('error', 'Erreur', 'Impossible de copier le mot de passe');
    });
}

// Fonction pour initialiser les gestionnaires d'événements
function initializeEventHandlers() {
    console.log('Initialisation des gestionnaires d\'événements');
    
    // Gestion de la suppression d'un utilisateur
    $(document).off('click', '.delete-user').on('click', '.delete-user', function(e) {
        e.preventDefault();
        e.stopPropagation();
        handleDeleteUser(this);
    });
    
    // Gestion de l'activation/désactivation
    $(document).off('click', '.toggle-active').on('click', '.toggle-active', function(e) {
        e.preventDefault();
        e.stopPropagation();
        handleToggleActive(this);
    });
    
    // Gestion de la réinitialisation du mot de passe
    $(document).off('click', '.reset-password').on('click', '.reset-password', function(e) {
        e.preventDefault();
        e.stopPropagation();
        handleResetPassword(this);
    });
    
    // Initialisation des tooltips Bootstrap
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl, {
            trigger: 'hover',
            boundary: 'window'
        });
    });
    
    // Désactiver le comportement de tri lors du clic sur les boutons d'action
    $('.action-buttons .btn').on('click', function(e) {
        e.stopPropagation();
    });
    
    // Initialisation des tooltips après le chargement de la page
    $(document).ready(function() {
        // Réinitialiser les tooltips après un redessin de DataTables
        if ($.fn.DataTable.isDataTable('#usersTable')) {
            $('#usersTable').on('draw.dt', function() {
                const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
                tooltipTriggerList.map(function (tooltipTriggerEl) {
                    return new bootstrap.Tooltip(tooltipTriggerEl, {
                        trigger: 'hover',
                        boundary: 'window'
                    });
                });
            });
        }
    });
}

// Configuration AJAX globale pour inclure le token CSRF
const csrfToken = $('meta[name="_csrf"]').attr('content');
const csrfHeader = $('meta[name="_csrf_header"]').attr('content');

// Initialisation au chargement du document
$(document).ready(function() {
    // Configuration AJAX globale
    $.ajaxSetup({
        beforeSend: function(xhr) {
            if (csrfToken && csrfHeader) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            }
        },
        error: function(xhr, status, error) {
            console.error('AJAX Error:', status, error);
            showAlert('error', 'Erreur', 'Une erreur est survenue lors de la communication avec le serveur.');
        }
    });
    
    // Initialisation des gestionnaires d'événements
    initializeEventHandlers();
    
    // Réinitialiser les gestionnaires d'événements après chaque redessinage de DataTables
    if ($.fn.DataTable.isDataTable('#usersTable')) {
        const table = $('#usersTable').DataTable();
        table.on('draw', function() {
            initializeEventHandlers();
        });
    }
});
