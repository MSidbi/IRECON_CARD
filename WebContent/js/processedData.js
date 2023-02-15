
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

function chngsubcat(e){
	document.getElementById("category").value =e.value;
	if(e.value=="RUPAY" || e.value == "VISA" || e.value == "NFS" || e.value == "CASHNET" || e.value == "MASTERCARD"){
	/*if(e.value=="RUPAY" || e.value == "VISA"){*/
				
			document.getElementById("trsubcategory").style.display="";
			getFiles();
			
		}else{
			
			document.getElementById("trsubcategory").style.display="none";
			document.getElementById("SubCat").value="-";
			getFiles();
			
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

	function getFiles() {
		
		debugger;
		var category = document.getElementById("category").value;
		//var subcat = "-";
		var subcat = document.getElementById("SubCat").value;
		
		
		if(category!="") { 
		$.ajax({
			 
			 type:'POST',
			 url :'getFiledetails.do',
			 data:{category:category,subcat:subcat},
			 success:function(response){
				 
				
				// response = $.parseJSON(response);
				 var length =response.Records.length;
				
				 var compareFile1 = document.getElementById("setltbl");
				 //var compareFile2 = document.getElementById("Man_file");
				 
				 var rowcount = compareFile1.childNodes.length;
					
					for(var j =1;j<=rowcount;j++ )
					{
						compareFile1.removeChild(compareFile1.lastChild);
						//compareFile2.removeChild(compareFile2.lastChild);
					}
				
				 var option1= document.createElement("option")
				 option1.value="0";
				 option1.text="--Select--";
				 var opt1= document.createElement("option")
				 opt1.value="0";
				 opt1.text="--Select--";
				 compareFile1.appendChild(option1);
				//compareFile2.appendChild(opt1)
				 
				 for(var i =0;i<length;i++ ) {
					
					 var option= document.createElement("option")
					  option.value = response.Records[i].inFileId;
					 option.text= response.Records[i].stFileName;
					 compareFile1.appendChild(option);
				 }
				/* for(var i =0;i<length;i++ ) {
						
					 var option= document.createElement("option")
					  option.value = response.Records[i].inFileId;
					 option.text= response.Records[i].stFileName;
						compareFile2.appendChild(option)
				 }*/
				
				 //document.getElementById("trbtn").style.display="none";
				// document.getElementById("category").disabled="disabled";
				 //document.getElementById("SubCat").disabled="disabled";
				 //displayContent();
				
				 

			 },error: function(){
				
				 alert("Error Occured");
				 
			 },
			 
		 });
		}else {
			
			
			alert("Please Select Category.")
			
		}
		
		
	}
	
	
function getFilestype() {
		
		debugger;
		var category = document.getElementById("category").value;
		var type = $("#setltbl :selected").text();
		//var type = document.getElementById("setltbl").text;
		//var subcat = "-";
		var subcat = document.getElementById("SubCat").value;
		var tablename="";
		if(category=="NFS" || category == "CASHNET") {
			
			 tablename='SETTLEMENT'+'_'+category+'_'+subcat.substring(0,3)+'_'+type
		} else {
		 tablename='SETTLEMENT'+'_'+category+'_'+type;
		}
		//var tablename= document.getElementById('dataType');
		//tablename.options[tablename.options.length]= new Option(concatabl, '1');
		if(category!="") { 
		$.ajax({
			 
			 type:'POST',
			 url :'getFileTypedetails.do',
			 data:{category:category,subcat:subcat,tablename:tablename},
			 success:function(response){
				 
				debugger;
				// response = $.parseJSON(response);
				 var length =response.Records.length;
				
				 var compareFile1 = document.getElementById("dataType");
				 //var compareFile2 = document.getElementById("Man_file");
				 
				 var rowcount = compareFile1.childNodes.length;
					
					for(var j =1;j<=rowcount;j++ )
					{
						compareFile1.removeChild(compareFile1.lastChild);
						//compareFile2.removeChild(compareFile2.lastChild);
					}
				
				 var option1= document.createElement("option")
				 option1.value="0";
				 option1.text="--Select--";
				 var opt1= document.createElement("option")
				 opt1.value="0";
				 opt1.text="--Select--";
				 compareFile1.appendChild(option1);
				//compareFile2.appendChild(opt1)
				 
				 for(var i =0;i<length;i++ ) {
					
					 var option= document.createElement("option");
					  //option.value = response.Records[i].inFileId;
					 option.text= response.Records[i].remarks;
					 compareFile1.appendChild(option);
				 }
				/* for(var i =0;i<length;i++ ) {
						
					 var option= document.createElement("option")
					  option.value = response.Records[i].inFileId;
					 option.text= response.Records[i].stFileName;
						compareFile2.appendChild(option)
				 }*/
				
				 //document.getElementById("trbtn").style.display="none";
				 //document.getElementById("category").disabled="disabled";
				 //document.getElementById("SubCat").disabled="disabled";
				 //displayContent();
				
				 

			 },error: function(){
				
				 alert("Error Occured");
				 
			 },
			 
		 });
		}else {
			
			
			alert("Please Select Category.")
			
		}
		
		
	}
	function getReconData() {
		
		
		var tbl = document.getElementById("setltbl").value; 
		var dataType = document.getElementById("dataType").value;
		var datepicker = document.getElementById("datepicker").value;
		var searchValue = document.getElementById("searchValue").value;
		if(tbl != "" && datepicker != "" && dataType != ""){
			
			$.ajax({
			 type:"POST",
			 url :'GetReconDataCount.do',
			 data:{tbl:tbl, date:datepicker,type:dataType,searchValue:searchValue},
			 success:function(count){
			      
				 if(count>1000){
				 
					 alert("Data limit exceeded.\n" +
					 		"Please Click on Generate Report to get Report");
					
				 }else if(count==0){
					 
					 alert("No Data Found.");
				 } else {
					 
					 window.open("../DebitCard_Recon/GetReconData.do?tbl="+tbl+" &date=" +datepicker+" &type="+dataType+" &searchValue="+searchValue , 'window', 'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');
				 }
				 
			 }
			});
		
			
		
		}else {
			
			alert("Please Fill All The Details.");
			return false;
		}
		
		
		
	}
function getjtbleReconData(){
		//GetJtableReconData
	debugger;
	
	var category = document.getElementById("category").value;
	var type = $("#setltbl :selected").text();
	//var type = document.getElementById("setltbl").text;
	var subcat = "-";
	
	var tbl='SETTLEMENT'+'_'+category+'_'+type;
	
	 localStorage.setItem("tablename", tbl);
	//var tbl = document.getElementById("setltbl").text; 
	var dataType = document.getElementById("dataType").value;
	var datepicker = document.getElementById("datepicker").value;
	var searchValue = document.getElementById("searchValue").value;
	if(tbl != "" && datepicker != "" && dataType != ""){
		
		$.ajax({
		 type:"POST",
		 url :'GetReconDataCount.do',
		 data:{tbl:tbl, date:datepicker,type:dataType,searchValue:searchValue},
		 success:function(count){
		      
			 debugger;
			 
			 if(count>1000){
			 
				 alert("Data limit exceeded.\n" +
				 		"Please Click on Generate Report to get Report");
				
			 }else if(count==0){
				 
				 alert("No Data Found.");
			 } else {
				 
				 window.open("../DebitCard_Recon/GetJtableReconData.do?tbl="+tbl+"&date=" +datepicker+" &type="+dataType+" &searchValue="+searchValue , 'window', 'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');
			 }
			 
		 }
		});
	
		
	
	}else {
		
		alert("Please Fill All The Details.");
		return false;
	}
		
		
	}
	
	
	
	function generateReport() 
	{
		debugger;
		var tbl = document.getElementById("category").value; 
		var dataType = document.getElementById("dataType").value;
		var datepicker = document.getElementById("datepicker").value;
		
		
		
		if(tbl != "" && datepicker != "" && dataType != ""){
			
			return true;
		
			/*$.ajax({
				 

				 type:"POST",
				 url :'GenerateTTUM.do',
				 data:{tbl:tbl, date:datepicker,type:dataType},
				 success: function() {
				      
				    }*/
			//window.open("../DebitCard_Recon/GenerateTTUM.do?tbl="+tbl+" &date=" +datepicker+" &type="+dataType , 'window', 'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');
		
			/*});*/
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
	
	
	
	