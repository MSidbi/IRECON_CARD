$(document).ready(function() {
    debugger;
   //alert("jsfile");
    $('#reconData').jtable({
        title : 'Recon Data',
       /* paging : true, //Enable paging
        pageSize : 10, //Set page size (default: 10)
*/      paging : true,
		columnResizable : false,
		clientSort : true,
		sorting : false,
		pageSize : 50,
        actions : {
            listAction : 'GetJtableData.do',
            updateAction: 'editsave.do'
            	


        },
       /* PAN,TERMID,TRACE,remarks*/
        fields : {
        	
        	pan : {
                title : 'PAN',
                width : '30%',
                key : true,
                list : true,
                edit : false,
            },
            tERMID : {
                title : 'TERM ID',
                width : '30%',
                key : true,
                list : true,
                edit : false,
                
              
            }, tRACE: {
                title : 'TRACE ID',
                width : '30%',
                key : true,
                list : true,
                edit : false,
            },
            rEMARKS:  {
                title : 'Remarks',
                width : '30%',
                key : true,
                list : true,
                edit : true,
            },setltbl:  {
                title : 'setltbl',
                width : '30%',
                key : true,
                list : true,
                visibility: 'hidden',
            },fileDate:  {
                title : 'FileDate',
                width : '30%',
                key : true,
                list : true,
                visibility: 'hidden',
            }, toolbar: { 
            	   items: [{   
            		     icon: '/images/excel.png',
            		     text: 'Export to Excel',
            		     click: function () {   
            		         //perform your custom job...     
            		   }    },{  
            		      icon: '/images/pdf.png', 
            		       text: 'Export to Pdf',
            		        click: function () {
            		            //perform your custom job...
            		        }    }]
            		}
            
        
        }
        
        
    });
    $('#reconData').jtable('load');
    
  
    
    	  
});
