
function getColumnlist(){
	
	var tbl = document.getElementById("setltbl").value; 
	var typetbl= document.getElementById("typetbl");
	if(tbl!="") {
	$.ajax({
		 
		 type:"POST",
		 url :'GetColumnList.do',
		 data:{tableName:tbl},
		 success:function(typelist){
			
			
			 var select = document.getElementById("columnSel");
			 var seloption = document.createElement('option');
				seloption.value = '' ;
				seloption.text = '---Select---';
				select.appendChild(seloption);
				
				for(var i=0; i<typelist.length; i++ ){
					
					var option = document.createElement('option');
					option.value = typelist[i] ;
					option.text = typelist[i];
					select.appendChild(option);
					
				}
				
				document.getElementById("coltr").style.display="";
		 }
		 });
	}
	
}

	function getType(){
		
		var tbl = document.getElementById("setltbl").value; 
		var typetbl= document.getElementById("typetbl");
		
		if(tbl!="") {
			$.ajax({
			 
			 type:"POST",
			 url :'GetSettelmentType.do',
			 data:{tableName:tbl},
			 success:function(typelist){
				 
				 if(typelist!="error"){
					
					
					 
					console.log(typelist)
					
					document.getElementById("typList").value = typelist;
					var splhdr = document.getElementById("typList").value.split(',');
						
					var toAppend = document.getElementById('selectTd');
					
					if(toAppend.childElementCount > 0) {
						
						toAppend.removeChild(toAppend.childNodes["0"])
					}
					var select = document.createElement('select');
					select.id ='settlementType';
					select.style.size="100px";
					toAppend.appendChild(select);
					
					
					
					
					for(var i=0; i<splhdr.length; i++ ){
						
						var option = document.createElement('option');
						option.value = splhdr[i] ;
						option.text = splhdr[i];
						select.appendChild(option);
						
					}
						
					 typetbl.style.display="";
				 }
			 },error: function(){
				
				 alert("Error Occured");
				 
			 },
			 
		 });
		}
	}

	function getReconData() {
		
		var tbl = document.getElementById("setltbl").value; 
		var datepicker = document.getElementById("datepicker").value;
		if(tbl != "" && datepicker != ""){
		
			window.open("../DebitCard_Recon/GetReconData.do?tbl="+tbl+" &date=" +datepicker+" &type=ONUS-MATCHED" , 'window', 'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');
		
		}else {
			
			alert("Please Fill All The Details.");
			return false;
		}
		
		
		
	}
	function getTableData(e){
		var tablename = document.getElementById("setltbl").value;
		var type = e.value;
		
		if(type=="RECON") {
			

			/* $.ajax({
			        type: "POST",
			        url: "GetSettelmentTypedtls.do",
			        dataType: "json",
			        success: function(data){
			           console.log(data);
			    }
			});
			*/
			 $("#jqGrid").jqGrid({
				 	type:"POST",
	                url: 'GetSettelmentTypedtls.do',
	                datatype: "json",
	                success: function(data){
				           console.log(data);
				    },
	                colModel: [			
						{ label: 'account_NUMBER', name: 'account_NUMBER', width: 45, key: true },
						{ label: 'balance', name: 'balance', width: 75 },
						{ label: 'remarks', name: 'remarks', width: 90 },
						{ label: 'createddate', name: 'createddate', width: 100 },
						{ label: 'createdby', name: 'createdby', width: 80 },
						// sorttype is used only if the data is loaded locally or loadonce is set to true
						{ label: 'tran_AMT', name: 'tran_AMT', width: 80 }                   
	                ],
					loadonce: true,
					viewrecords: true,
	                width: 780,
	                height: 200,
	                rowNum: 20,
					rowList : [20,30,50],
	                rownumbers: true, 
	                rownumWidth: 25, 
	                multiselect: true,
	                pager: "#jqGridPager"
	            });
	        

			
			/*getReconData(type);*/
			
		}else{
			
			getOtherData(type);
			
		}
		
		
	    
	   /* $.ajax({
	    	 type: "POST",
	    	url: "getEmployeeMasterWorklist", 
	    	 dataType: "json",
	    	
	    	success: function(data){
	    		debugger;
	       alert(data);
	    }});*/
	    

	    
	    
	   /* $.ajax({
	        type: 'POST',
	        url: "getEmployeeMasterWorklist",
	        dataType: 'json',
	       
	        success: function(data) {
	        	debugger;
	        	alert(data);

	        },
	        error: function(jqXHR, textStatus, errorThrown) {
	            alert("error");
	        }*/

	}
	
	function getSelectedRows() {
        var grid = $("#jqGrid");
        var rowKey = grid.getGridParam("selrow");

        if (!rowKey)
            alert("No rows are selected");
        else {
            var selectedIDs = grid.getGridParam("selarrrow");
            var result = "";
            for (var i = 0; i < selectedIDs.length; i++) {
                result += selectedIDs[i] + ",";
            }

            alert(result);
        } 
	}
	/*
	function getReconData(type){
		
		var tablename = document.getElementById("setltbl").value;
		
		$('#TypeDetailTableContainer').jtable({
	        title : 'Type Details',
	        selecting: true, //Enable selecting
            multiselect: true, //Allow multiple selecting
            selectingCheckboxes: true,
	        paging: true, //Enable paging
	        pageSize: 10, //Set page size (default: 10)
	        sorting: true, //Enable sorting          
	        defaultSorting: 'createdby ASC',
	       
	       

	        actions : {
	            listAction : 'GetSettelmentTypedtls.do?tablename='+tablename+'&action='+type,
	            createAction:'save1',
	            updateAction: 'editsave',
	            deleteAction: 'deleteemp'
	            

	        },
	       
	        beforeSend:function(){
	          showLoader();
	          },
	         complete:function(data){
	          hideLoader(); 
	        	},
	         error:function(XMLHttpRequest, textStatus, errorThrown){
	         hideLoader();
	          }, 
	       
	        fields : {
	        	RowCheckbox: {
	                 title: 'Status',
	                 width: '12%',
	                 type: 'checkbox',
	                 values: { 'false': 'Passive', 'true': 'Active' },
	                 defaultValue: 'true'
	             },
	        	
	        	account_NUMBER : {
	                title : 'ACCOUNT_NUMBER',
	                width : '30%',
	                key : true,
	                list : true,
	                edit : false,
	            },
	            balance : {
	                title : 'BALANCE',
	                width : '30%',
	                edit : true,
	                display : function(data){
	                	debugger;
	                	alert("Error is"+data.record.name);
	                  
	                }
	            },     contra_ACCOUNT: {
	                title : 'STUDENT ID',
	                width : '30%',
	                key : true,
	                list : true,
	                edit : false,
	            },
	            createdby:  {
	                title : 'CREATEDBY',
	                width : '30%',
	                key : true,
	                list : true,
	                edit : false,
	            },
	            createddate: {
	                title : 'CREATEDDATE',
	                width : '30%',
	                key : true,
	                list : true,
	            },
	           
	            remarks: {
	                title : 'REMARKS',
	                width : '30%',
	                key : true,
	                list : true,
	                edit : false,
	            },
	            tran_AMT: {
	                title : 'TRAN_AMT',
	                width : '30%',
	                key : true,
	                list : true,
	                edit : false,
	            },
	            tran_ID : {
	                title : 'TRAN_ID',
	                width : '30%',
	                key : true,
	                list : true,
	                edit : false,
	            }
	            

	        }
	    });
	    $('#TypeDetailTableContainer').jtable('load');
		
	}*/
	
	
	