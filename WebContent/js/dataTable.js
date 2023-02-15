$(document).ready(function() {

    $('#recondata').DataTable( {
        "iDisplayLength": 15,
       "pagingType": "full_numbers",
       
        dom: 'Bfrtip',

        buttons: [

            'copy', 'csv', 'excel', 'pdf', 'print'

        ]
         

    } );

} );