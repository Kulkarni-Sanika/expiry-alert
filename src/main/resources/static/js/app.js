// Fills the delete confirmation pop-up with the chosen document.
document.addEventListener('DOMContentLoaded', function () {
    var modal = document.getElementById('deleteModal');
    if (!modal) {
        return;
    }
    modal.addEventListener('show.bs.modal', function (event) {
        var button = event.relatedTarget;
        var id = button.getAttribute('data-id');
        var name = button.getAttribute('data-name');
        modal.querySelector('#deleteDocName').textContent = name;
        modal.querySelector('form').setAttribute('action', '/documents/' + id + '/delete');
    });
});
