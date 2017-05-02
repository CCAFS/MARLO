$(document).ready(init);

function init() {
  console.log('Hi');

  $('.usersTable').DataTable({
      dom: 'Bfrtip',
      buttons: [
          'copy', 'csv'
      ]
  });

  // Attaching events
  attachEvents();
}

function attachEvents() {

}
