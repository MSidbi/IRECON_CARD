$(document).ready(function() {

    $('#newtable').DataTable( {
       "pagingType": "full_numbers",
       "pageLength": "10000",
       	scrollY: 300,  
       	scrollCollapse: true,
        dom: 'Bfrtip',
       
        buttons: [

            'copy', 'csv', 'excel', 'pdf', 'print'

        ]


    } );

} );