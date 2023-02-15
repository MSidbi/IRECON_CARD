$(document).ready(function() {
    debugger;
   //alert("jsfile");
    var tablename=localStorage.getItem("tablename");
   // alert(tablename);
    if(tablename=='SETTLEMENT_AMEX_CBS' || tablename == 'SETTLEMENT_RUPAY_CBS')
    	{
    	amex_cbs();
    	}
    else if(tablename=='SETTLEMENT_ONUS_CBS')
    	{
    	onus_cbs();
    	}
        	  
});

function onus_cbs()
{
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
        	
        	aCCOUNT_NUMBER : {
                title : 'ACCOUNT_NUMBER',
                width : '30%',
                key : true,
                list : true,
                edit : false,
            },
            cONTRA_ACCOUNT : {
                title : 'CONTRA_ACCOUNT',
                width : '30%',
                key : true,
                list : true,
                edit : false,
                
              
            }, tRANDATE: {
                title : 'TRAN DATE',
                width : '30%',
                key : true,
                list : true,
                edit : false,
                
                
            }, tRAN_PARTICULAR: {
                title : 'TRAN_PARTICULAR',
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
            }
            
        
        }
        
        
    });
    $('#reconData').jtable('load');
     

	}


function amex_cbs()
{
	//alert('inside amex');
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
        	
        	foracid : {
                title : 'ACCOUNT_NUMBER',
                width : '30%',
                key : true,
                list : true,
                edit : false,
            },
            tran_date : {
                title : 'TRAN_DATE',
                width : '30%',
                key : true,
                list : true,
                edit : false,
                
              
            }, cONTRA_ACCOUNT: {
                title : 'CONTRA_ACCOUNT',
                width : '30%',
                key : true,
                list : true,
                edit : false,
                
                
            }, dcrs_remarks: {
                title : 'DCRS_REMARKS',
                width : '30%',
                key : true,
                list : true,
                edit : false,
            },
            
            
            particulars:  {
                title : 'PARTICULARS',
                width : '30%',
                key : true,
                list : true,
                edit : true,
            },
        
        }
        
        
    });
    $('#reconData').jtable('load');
     

	}