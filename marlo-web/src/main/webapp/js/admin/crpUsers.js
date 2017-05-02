$(document).ready(init);

function init() {
  console.log('Hi');

  $('.usersTable').DataTable({
      dom: 'Bfrtip',
      buttons: [
          'copy', 'csv'
      ],
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

function getDateString() {
  var today = new Date();
  return today.toISOString();
}