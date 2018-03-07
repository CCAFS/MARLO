$(document).ready(init);

function init() {
  console.log('Hi');

  $('.usersTable').DataTable({
      dom: 'Bfrtip',
      buttons: [
          {
              extend: 'copy',
              title: 'Data export'
          }, {
              extend: 'csv',
              title: 'Data_export_' + getDateString()
          }
      ]
  });

  // Attaching events
  attachEvents();
}

function attachEvents() {

}
