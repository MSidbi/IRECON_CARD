<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<script type="text/javascript" src="js/reconFilesetup.js"></script>
<script type="text/javascript" src="js/reconSetup.js"></script>
<script type="text/javascript" src="js/jquery.fancyform.js"></script>
<script type="text/javascript" src="js/commonScript.js"></script> 
<script type="text/javascript">
$(document).ready(function () {
	clearvalue();
});


</script>


<link href="css/fancyform.css" type="text/css" rel="stylesheet" />


<body >
<div class="container">
 <form:form id="reconform" name="form" action="saveCompareSetup.do" method="POST" commandName="CompareSetupBean">  
	<!--  <div id="id1" class="tabcontent" style="display: block;" > -->
	

<div id="id1" style="display: block;">
	<div class="jtable-main-container">
				<div class="jtable-title">
					<div class="jtable-title-text">Compare CONFIGURATION</div>
				</div>
	</div>
		<table align="center" class="table" >
			<tr>
				<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Category </th>
			 	<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
					<td align="left">
						<select  id="Categry" name="category" onchange="displaycatg(this)">
								<option value="">--Select--</option>
								<option value="ONUS">ONUS</option>
								<option value="RUPAY">RUPAY</option>
								<option value="AMEX">AMEX</option>
								<option value="VISA">VISA</option>
								<option value="MASTERCARD">MASTERCARD</option>
								<option value="CARDTOCARD">CARD TO CARD</option>
								<option value="POS">ONUS POS</option>

						</select>
						<form:hidden path="category" id="category"/>
				</td>
			</tr>
			<tr id="trsubcategory" style="display: none">
				<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Sub Category </th>
			 	<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
					<td align="left">
						<select  id="SubCat" name="SubCat" onchange="setSubCat();">
							<option value="-">--Select--</option>
							<option value="DOMESTIC">DOMESTIC</option>
							<option value="INTERNATIONAL">INTERNATIONAL</option>
							<option value="SURCHARGE">SURCHARGE</option>
							<option value="ISSUER">ISSUER</option>
							<option value="ACQUIRER">ACQUIRER</option>
							<option value="ONUS">ONUS</option>
						</select>
						<form:hidden path="stSubCategory" value=""/>
				</td>
			</tr>
			
			<tr class="oddRow" id="trbtn">
				<td>&nbsp;&nbsp;&nbsp;&nbsp;<input class ="form-button"  type="button" value="Get Files" onclick="getFiles()">
				</td>
				<th align="right">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
				<th align="right">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
					
			</tr>
			
			<%-- <tr class="evenRow">
				<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Recon Layer </th>
			 	<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
					<td align="left">
						<form:select path="layerCount" id="layerCount" onchange="displayContent(this);">
							<form:option value="">--Select--</form:option>
							<form:option value="2">TWO</form:option>
							
					</form:select>
				</td>
			</tr> --%>
			<tr class="evenRow" id="file1" style="display: none;">
				<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;First File </th>
			 	<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
					<td align="left">
						<form:select path="compareFile1" id="compareFile1" onchange="setValue(this)">
			
							</form:select>
							<form:hidden path="compreFileName1" id="compreFileName1"/>
						
				</td>
			</tr>
			<tr class="evenRow" id="file2" style="display: none">
				<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Second File </th>
			 	<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
					<td align="left">
						<form:select path="compareFile2" id="compareFile2" onchange="setValue(this);">
							
								
					</form:select>
					<form:hidden path="compreFileName2" id="compreFileName2"/>
				</td>
			</tr>
			<tr class="evenRow" id="trfile1match" style="display: none">
				<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;FILE1_MATCHED </th>
			 	<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
					<td align="left">
						<input type="checkbox" id="chkfile1match" onchange="chngval1(this)"/>
						<form:hidden path="file1match" id="file1match" value="N"/>
				</td>
			</tr>
			
			<tr class="evenRow" id="trfile2match" style="display: none">
			<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;FILE2_MATCHED </th>
			 	<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
					<td align="left">
						<input type="checkbox" id="chkfile2match" onchange="chngval2(this)"/>
						<form:hidden path="file2match" id="file2match" value="N"/>
				</td>
				</tr>
			
			<!-- <tr><td><input class="form-button" type="button" value="Configure Column" onclick="displayDtl();"> </td></tr> -->
			
		</table>
		
		<!-- file_dtl_list -->
		
			
				
				<table id="coldtl" align="center" style="display: none" width="100%" border="1">
				
				
				</table>
				<input type="hidden" id="headerlist1" value="">
				<input type="hidden" id="headerlist2" value="">
				<input class="form-button" align="left" type="button" id="nxt1" value="SET RECON PARAM" onclick="displayDtl(this);">
				<input type="hidden" id="count" value="1"/><%-- ${fn:length(CompareSetupBean.columnDtls)} --%>
				
				<!-- <table>
				<tr>
					<td><input class="form-button" type="button" value="Add Row" onclick="addrow();"></td>
					<td><input class="form-button" type="button" id="nxt1" value="NEXT" onclick="displayDtl();"></td>
				</table> -->
				
		</div>
		
		<div id="id3" style="display: none" >
		
			<table align="center" cellpadding="0" cellspacing="0" width="100%" id="ReconTbl" border="1" rules="values" >
			
				<tr class="tableHeader">
				<th>File</th>
				<th>File Header </th>
				<th>Search Pattern</th>
				<th>Padding</th>
				<th>Start Char Position</th>
				<th>End Char Position</th>
				<th>Condition</th>
				<th>Matching Condition </th>
				
				
			</tr>
		
		<c:set var="rowClass" value="oddRow"/>
			<c:forEach var="cnv" items="${CompareSetupBean.setup_dtl_list}" varStatus="loop">
			 <tr class="${rowClass}">
				<td  align="center">
					 	<form:select style="width: 100px" path="setup_dtl_list[${loop.index}].inFileId" id="setup_dtl_list[${loop.index}].inFileId" cssClass="header" onchange="setHeader(this);"></form:select>
				</td>
					<td align="center" id="compareselectTd">
						<form:select path="setup_dtl_list[${loop.index}].table_header" id="setup_dtl_list[${loop.index}].table_header" cssClass="header"></form:select> 
						<%--   <form:input path="stHeader"/>   --%>
					
						 
					</td>
					<td align="center"  style="width: 160px">
						 <form:input style="width: 160px" path="setup_dtl_list[${loop.index}].srch_Pattern" id="setup_dtl_list[${loop.index}].srch_Pattern" maxlength="50" class="header" cssClass="srch_pattern" onkeypress="return setValueType(this,'search')"/> 
						
					</td>
					<td align="center" class="lD">
						<form:select path="setup_dtl_list[${loop.index}].stPadding" id="setup_dtl_list[${loop.index}].stPadding">
							<form:option value=""> --Select-- </form:option>
							<form:option value="Y">Yes</form:option>
							<form:option value="N">No</form:option>
						</form:select>
					</td>
					 
					<td align="center"  class="lD">
						 <form:input  path="setup_dtl_list[${loop.index}].start_charpos" id="setup_dtl_list[${loop.index}].start_charpos" maxlength="6" cssClass="char_pos" onkeypress="return setValueType(this,'numeric')"/> 
					</td> 
					<td align="center"  class="lD">
						 <form:input  path="setup_dtl_list[${loop.index}].charsize" id="setup_dtl_list[${loop.index}].charsize" maxlength="6" cssClass="char_pos" onkeypress="return setValueType(this,'numeric')"/> 
					</td>
					<td align="center" class="lD">
						 <form:select path="setup_dtl_list[${loop.index}].condition" id="setup_dtl_list[${loop.index}].condition" cssClass="char_pos">
						 	<form:option value="">--Select--</form:option>
						 	<form:option value="=">=</form:option>
						 	<form:option value="!=">!=</form:option>
						 	<form:option value="like">LIKE</form:option>
						 </form:select> 
					</td>
					
					<td>
					
						<form:select path="setup_dtl_list[${loop.index}].matchCondn" id="setup_dtl_list[${loop.index}].matchCondn">
							<form:option value=""> --Select-- </form:option>
							<form:option value="Y">Yes</form:option>
							<form:option value="N">No</form:option>
						</form:select>
					
					</td>
					
		
				<%-- 	<td align="center" class="lD"><input type="button" id="del${loop.index}" name="del${loop.index}" class="delButton" value="Delete"></td> --%>
				
			</tr>
			<c:choose>
				<c:when test="${rowClass eq 'oddRow' }"><c:set var="rowClass" value="evenRow" /></c:when>
				<c:when test="${rowClass eq 'evenRow' }"><c:set var="rowClass" value="oddRow" /></c:when>
			</c:choose>
			
			</c:forEach>
		
			</table>
			
			<table align="center" cellpadding="0" cellspacing="0" width="100%" class="table">
			<tr class="row1">
				<th align="left" style="padding-left: 25px; background-color: #d7dbdd;">
					<input class="form-button" onclick="addreconRow();" type="button" id="addRow2" name="addRow1" value="Add Row" />
					<input type="hidden" id="reconParam" value="0">
				</th>
				
			</tr>
			</table>
		
		
		
		
		
		</div>
				
	<div id="id2" style="display: none;">
			
			
		<table align="center" id="displaytbl" style="size: 100%" border="1" rules="values">

			<tr style="size: 100%;">

				<!-- <th style="width: 50px">Matching id</th> -->
				<th style="width: 120px">File Name</th>
				<th style="width:100px">Headers</th>
				<th>Padding</th>
				<th style="width: 50px">Starting Position</th>
				<th style="width: 50px">Char Size</th>
				<th>Data Type</th>
				<th>Data Pattern</th>
				<th>Relax Parameter </th>
				

			</tr>

			<tr style="size: 100%;" id="First File">
				<!-- <td><input type="text" style="width: 50px;" readonly="readonly"	 id="reconcount" value="1" ></td> -->
				<td align="center" style="width: 250px;"><label  style="width: 150px;">File1 Headers</label></td>
				<td align="center"><select style="width:100px" id="fileselect1"><option	value="">--Select--</option></select></td>
				<td align="center"><select id="staPdding1"><option value="">--Select--</option>
						<option value="Y">YES</option>
						<option value="N">NO</option></select></td>
				
				<td align="center" style="width: 50px" ><input type="text" style="width: 50px" id="startPos1"  value="0"	maxlength="6" cssClass="char_pos" onkeypress="return setValueType(this,'numeric')"></td>
				<td align="center" style="width: 50px"><input type="text" style="width: 50px" id="charsize1"  maxlength="6" cssClass="char_pos" value="0" onkeypress="return setValueType(this,'numeric')"></td>
				<td align="center"><select id="dataType1"><option
							value="">--Select--</option>
						<option value="DATE">DATE</option>
						<option value="TIME">TIME</option>
						<option value="NUMBER">NUMBER</option></select></td>
				<td align="center" style="width: 80px"><input style="width: 80px" type="text" id="datpattern1" value="0" onkeypress="return setValueType(this,'pattern')"></td>
				 <td align="center"><select id="stRelaxParam1"><option
							value="">--Select--</option>
						<option value="Y">YES</option>
						<option value="N">NO</option></select></td> 
				

			</tr>
			<tr style="size: 100%;" id="Second File">

				<!-- <td><input type="text" style="width: 50px;"	readonly="readonly"	 id="reconcount2" value="1"></td> -->
				<td align="center" style="width: 250px;"><label style="width: 150px;">File2 Headers</label></td>
				<td align="center" ><select style="width:100px" id="fileselect2"><option
							value="">--Select--</option></select></td>
				
				<td align="center"><select id="staPdding2"><option
							value="">--Select--</option>
						<option value="Y">YES</option>
						<option value="N">NO</option></select></td>
				
				<td align="center" style="width: 50px"><input style="width: 50px" type="text" id="startPos2" value=0 maxlength="6" cssClass="char_pos"></td>
				<td align="center" style="width: 50px"><input style="width: 50px" type="text" id="charsize2" value=0></td>
				<td align="center"><select id="dataType2"><option
							value="">--Select--</option>
						<option value="DATE">DATE</option>
						<option value="TIME">TIME</option>
						<option value="NUMBER">NUMBER</option></select></td>
				<td align="center" style="width: 80px"><input type="text" style="width: 80px" id="datpattern2"  value="0" onkeypress="return setValueType(this,'pattern')"></td>
				
				 <td align="center"><select id="stRelaxParam2"><option
							value="">--Select--</option>
						<option value="Y">YES</option>
						<option value="N">NO</option></select></td>
 
			</tr>
			


		</table>
		<input type="hidden" id="datacount" value="0" > <input type="button" class="form-button" value="Add" class="" id="reconadd" onclick="addRow();" >


		<table id="detailtbl" >
		
				<tr>
					<th>File1 Header</th>
					<th>File2 Header</th>
				<!-- 	<td id="ThirdFile" style="display: none;">File3 Header</td> -->
				</tr>
			<c:forEach var="cnv" items="${CompareSetupBean.columnDtls}" varStatus="loop">
			
		
		<tr>
				<td><form:hidden   id="columnDtls[${loop.index}].matchCount"  path="columnDtls[${loop.index}].matchCount"  value="0" />
				<form:input   id="columnDtls[${loop.index}].fileColumn1"  path="columnDtls[${loop.index}].fileColumn1" value="0" readonly="true" />
				<form:hidden  id="columnDtls[${loop.index}].stPadding"   path="columnDtls[${loop.index}].stPadding" value="0"/>
				<form:hidden  id="columnDtls[${loop.index}].inStart_Char_Position"   path="columnDtls[${loop.index}].inStart_Char_Position" value="0"/>
				<form:hidden  id="columnDtls[${loop.index}].inEnd_char_position"   path="columnDtls[${loop.index}].inEnd_char_position" value="0"/>
				<form:hidden  id="columnDtls[${loop.index}].dataType"   path="columnDtls[${loop.index}].dataType" value="0"/>
				<form:hidden  id="columnDtls[${loop.index}].datpattern"  path="columnDtls[${loop.index}].datpattern" value="0"/>
				 <form:hidden  id="columnDtls[${loop.index}].RelaxParam1"  path="columnDtls[${loop.index}].RelaxParam1" value="0"/> 
				
				
				</td>
				
			
				<td><form:input   id="columnDtls[${loop.index}].fileColumn2"  path="columnDtls[${loop.index}].fileColumn2" value="0" readonly="true" />
				<form:hidden  id="columnDtls[${loop.index}].stPadding2"  path="columnDtls[${loop.index}].stPadding2" value=""/>
				<form:hidden  id="columnDtls[${loop.index}].inStart_Char_Position2" path="columnDtls[${loop.index}].inStart_Char_Position2" value="0"/>
				<form:hidden  id="columnDtls[${loop.index}].inEnd_char_position2" path="columnDtls[${loop.index}].inEnd_char_position2" value="0"/>
				<form:hidden  id="columnDtls[${loop.index}].dataType2" path="columnDtls[${loop.index}].dataType2" value="0"/>
				<form:hidden  id="columnDtls[${loop.index}].datpattern2" path="columnDtls[${loop.index}].datpattern2" value="0"/>
				<form:hidden  id="columnDtls[${loop.index}].RelaxParam2"  path="columnDtls[${loop.index}].RelaxParam2" value="0"/>  
				<input type="hidden" id="rowid" value="">
				</td>
				
				
			</tr>
					
			</c:forEach>
			
		
		</table>
		
	<input type="submit" align="left" class="form-button" value="Save" id="reconsave" onclick="return saveData();"  >


			
			
			
			
			
	</div>
			
	
 
	
	
	<!-- <input class="form-button" align="left" type="button" id="nxt1" value="NEXT" onclick="displayDtl();">
	 --%> -->
	
	<!-- </div>  -->

<!-- DIV 2 -->
	<%--  <div id="id2" class="tabcontent" align="center">
	<jsp:include page="ReconSetup.jsp"></jsp:include>

	<input type="button" class="form-button" value="Previous" id="prev2" onclick="previous();" style="display:none;"/>
	<input type="submit" align="left" class="form-button" value="Save" id="reconsave" onclick="return saveData();"  >


	 </div>  --%>
	
	</form:form>
	
	</div>
