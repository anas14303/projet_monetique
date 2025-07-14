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
        timerProgressBar: true
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
    const isActive = button.getAttribute('data-active') === 'true';
    const username = button.getAttribute('data-username');
    
    const action = isActive ? 'désactiver' : 'activer';
    
    Swal.fire({
        title: isActive ? 'Désactiver l\'utilisateur' : 'Activer l\'utilisateur',
        html: `
            <div class="text-center">
                <i class="fas ${isActive ? 'fa-user-slash text-warning' : 'fa-user-check text-success'} fa-4x mb-3"></i>
                <p>Êtes-vous sûr de vouloir ${isActive ? 'désactiver' : 'activer'} l'utilisateur <strong>${username}</strong> ?</p>
            </div>
        `,
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: isActive ? '<i class="fas fa-user-slash me-1"></i> Désactiver' : '<i class="fas fa-user-check me-1"></i> Activer',
        cancelButtonText: '<i class="fas fa-times me-1"></i> Annuler',
        reverseButtons: true,
        customClass: {
            confirmButton: `btn ${isActive ? 'btn-warning' : 'btn-success'} me-2`,
            cancelButton: 'btn btn-secondary ms-2'
        },
        buttonsStyling: false,
        showLoaderOnConfirm: true,
        preConfirm: async () => {
            try {
                const response = await fetch(`/admin/users/toggle-active/${userId}`, {
                    method: 'POST',
                    credentials: 'same-origin'
                });
                
                if (!response.ok) {
                    throw new Error('Erreur lors de la mise à jour du statut');
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
            const newStatus = result.value.active;
            const row = button.closest('tr');
            
            // Mettre à jour l'interface utilisateur
            if (newStatus) {
                row.classList.remove('table-danger');
                row.classList.add('table-success');
                button.querySelector('i').classList.remove('fa-toggle-off');
                button.querySelector('i').classList.add('fa-toggle-on');
                button.setAttribute('title', 'Désactiver');
                
                const statusBadge = row.querySelector('.status-badge');
                if (statusBadge) {
                    statusBadge.className = 'status-badge status-active';
                    statusBadge.innerHTML = '<i class="fas fa-check-circle me-1"></i>Actif';
                }
            } else {
                row.classList.remove('table-success');
                row.classList.add('table-danger');
                button.querySelector('i').classList.remove('fa-toggle-on');
                button.querySelector('i').classList.add('fa-toggle-off');
                button.setAttribute('title', 'Activer');
                
                const statusBadge = row.querySelector('.status-badge');
                if (statusBadge) {
                    statusBadge.className = 'status-badge status-inactive';
                    statusBadge.innerHTML = '<i class="fas fa-times-circle me-1"></i>Inactif';
                }
            }
            
            // Mettre à jour l'attribut data-active
            button.setAttribute('data-active', newStatus);
            
            showAlert('success', 'Succès', `Utilisateur ${newStatus ? 'activé' : 'désactivé'} avec succès`);
        }
    });
}

// Fonction pour initialiser les gestionnaires d'événements
function initializeEventHandlers() {
    console.log('Initialisation des gestionnaires d\'événements');
    
    // Délégation d'événements pour les boutons de suppression
    $(document).off('click', '.delete-user').on('click', '.delete-user', function(e) {
        e.preventDefault();
        e.stopPropagation();
        handleDeleteUser(this);
    });
    
    // Délégation d'événements pour les boutons d'activation/désactivation
    $(document).off('click', '.toggle-active').on('click', '.toggle-active', function(e) {
        e.preventDefault();
        e.stopPropagation();
        handleToggleActive(this);
    });
    
    // Initialisation des tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.forEach(tooltipTriggerEl => {
        new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

// Initialisation au chargement du document
$(document).ready(function() {
    // Configuration AJAX globale pour inclure le token CSRF
    const token = $('meta[name="_csrf"]').attr('content');
    const header = $('meta[name="_csrf_header"]').attr('content');
    
    if (token && header) {
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });
    }
    
    // Initialisation des gestionnaires d'événements
    initializeEventHandlers();
    
    // Réinitialiser les gestionnaires d'événements après chaque redessinage de DataTables
    if ($.fn.DataTable.isDataTable('#usersTable')) {
        $('#usersTable').on('draw.dt', function() {
            initializeEventHandlers();
        });
    }
});
