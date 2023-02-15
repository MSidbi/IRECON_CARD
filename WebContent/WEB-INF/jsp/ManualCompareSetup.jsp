<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<script type="text/javascript" src="js/jquery.fancyform.js"></script>
<script type="text/javascript" src="js/manualCompareSetup.js" ></script>
<script type="text/javascript" src="js/commonScript.js"></script> 
<link href="css/fancyform.css" type="text/css" rel="stylesheet" />


<body >
<div class="container">
 <form:form id="reconform" name="form" action="saveManCompareSetup.do" method="POST" commandName="ManualCompBean">  
	<!--  <div id="id1" class="tabcontent" style="display: block;" > -->
	

<div id="id1" style="display: block;">
	<div class="jtable-main-container">
				<div class="jtable-title">
					<div class="jtable-title-text">CONFIGURATION</div>
				</div>
	</div>
		<table align="center" class="table"  >
			<tr>
				<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Category </th>
			 	<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
					<td align="left">
						<form:select path="category" id="category" name="category" onchange="getFiles()">
						<form:option value="">--Select--</form:option>
						<form:option value="ONUS">ONUS</form:option>
						<form:option value="AMEX">AMEX</form:option>
						
						
					</form:select>
				</td>
			</tr>

			<tr class="evenRow" id="file1">
				<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;First File :</th>
			 	<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
					<td align="left">
						<select name="Comp_File" id="Comp_File" onchange="setcompvalue(this)">
							 <option value="">--Select--</option>
								<%-- <c:forEach var="file_list" items="${file_list}">
									<option id="${file_list.inFileId}" value="${file_list.inFileId}" >${file_list.stFileName}</option>
								</c:forEach>  --%>
							
							</select>
							<form:hidden path="stComp_File" id="stComp_File"/>
							<form:hidden path="comp_File" id="comp_File"/>
							
						
				</td>
			</tr>
			<tr class="evenRow" id="file2" >
				<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Manual File :</th>
			 	<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
					<td align="left">
						<select name="Man_file" id="Man_file" onchange="setmanfileValue(this);">
							<option value="0">--Select--</option>
							<%-- <c:forEach var="file_list" items="${file_list}">
								<option id="${file_list.inFileId}" value="${file_list.inFileId}" >${file_list.stFileName}</option>
							</c:forEach> --%>
						</select>
					<form:hidden path="stMan_file" id="stMan_file"/>
					<form:hidden path="man_File" id="man_File"/>
				</td>
			</tr>
			
			 
			
		</table>
		
		<!-- file_dtl_list -->
		
			
				
				<table id="coldtl" align="center" style="display: none" width="100%" border="1">
				
				
				</table>
				<input type="hidden" id="headerlist1" value="">
				<input type="hidden" id="headerlist2" value="">
				
				<input class="form-button" type="button" onclick="setFile(this);" value="Set Match Value" id="setvalue" >
			<!-- 	<input class="form-button" align="left" type="button" id="nxt1" value="SET PARAMETERS" onclick="displaymanDtl();">
				 --><input type="hidden" id="count" value="0"/><%-- ${fn:length(CompareSetupBean.columnDtls)} --%>
				
				<!-- <table>
				<tr>
					<td><input class="form-button" type="button" value="Add Row" onclick="addrow();"></td>
					<td><input class="form-button" type="button" id="nxt1" value="NEXT" onclick="displayDtl();"></td>
				</table> -->
				
		</div>
		<div id="matchdiv" style="display: none"  >
		
			<table align="center" id="matchingtbl" style="size: 100%" border="1" rules="values">
			
				<tr>
					<th>File</th>
					<th>File Header</th>
					<th>Search Pattern</th>
					<th>Padding</th>
					<th>Start_CharPosition</th>
					<th>CharSize</th>
					<th>Condition</th>
				</tr>
				<tr>
				
				<c:set var="rowClass" value="oddRow"/>
			<c:forEach var="cnv" items="${ManualCompBean.comp_dtl_list}" varStatus="loop">
			<tr class="${rowClass}">
			<%-- <td>
				<form:input path="comp_dtl_list[${loop.index}].stHeader"/>
			</td> --%>
				<td align="center" id="selectTd">
					 
					<form:select path="comp_dtl_list[${loop.index}].refFileId" id="comp_dtl_list[${loop.index}].refFileId" onchange="setManValue(this);">
					</form:select>
					 
				</td>
				
				<td align="center" id="selectTd">
					 
				<form:select path="comp_dtl_list[${loop.index}].refFileHdr" id="comp_dtl_list[${loop.index}].refFileHdr">
					</form:select>
					 
				</td>
				<td align="center">
					 <form:input path="comp_dtl_list[${loop.index}].stSearch_Pattern" id="comp_dtl_list[${loop.index}].stSearch_Pattern" maxlength="50" cssClass="srch_pattern" onkeypress="return setValueType(this,'search')"/> 
					
				</td>
				<td align="center" class="lD">
					<%--  <form:input path="comp_dtl_list[${loop.index}].stPadding" maxlength="50" cssClass="place" /> --%> 
					<form:select path="comp_dtl_list[${loop.index}].stPadding" id="comp_dtl_list[${loop.index}].stPadding">
						<form:option value=""> --Select-- </form:option>
						<form:option value="Y">Yes</form:option>
						<form:option value="N">No</form:option>
					</form:select>
				</td>
				<td align="center" class="lD">
					 <form:input path="comp_dtl_list[${loop.index}].stChar_Pos" id="comp_dtl_list[${loop.index}].stChar_Pos" maxlength="6" cssClass="char_pos" onkeypress="return setValueType(this,'numeric')"/> 
				</td> 
				<td align="center" class="lD">
					 <form:input path="comp_dtl_list[${loop.index}].stChar_Size" id="comp_dtl_list[${loop.index}].stChar_Size" maxlength="6" cssClass="char_pos" onkeypress="return setValueType(this,'numeric')"/> 
				</td>
				<td align="center" class="lD">
					 <form:select path="comp_dtl_list[${loop.index}].condition" id="comp_dtl_list[${loop.index}].condition" cssClass="char_pos">
					 	<form:option value="">--Select--</form:option>
					 	<form:option value="=">=</form:option>
					 	<form:option value="!=">!=</form:option>
					 	<form:option value="LIKE">LIKE</form:option>
					 </form:select> 
				</td> 
	
			<%-- 	<td align="center" class="lD"><input type="button" id="del${loop.index}" name="del${loop.index}" class="delButton" value="Delete"></td> --%>
			</tr>
			
			
			 
			<c:choose>
				<c:when test="${rowClass eq 'oddRow' }"><c:set var="rowClass" value="evenRow" /></c:when>
				<c:when test="${rowClass eq 'evenRow' }"><c:set var="rowClass" value="oddRow" /></c:when>
			</c:choose>
			
			</c:forEach>
				
				</tr>
				
			</table>
			<input class="form-button" type="button" value="Add Row" onclick="addManRow()">
		</div>
				
	<div id="cmpdiv" style="display: none">
			
			
		<table align="center" id="displaytbl" style="size: 100%" border="1" rules="values">

			<tr style="size: 100%;">

				<!-- <th style="width: 50px">Matching id</th> -->
				<th style="width: 120px">File Name</th>
				<th style="width:100px">Headers</th>
				<th style="width: 80px">Value</th>
				<th>Condition </th>
				<th>Padding</th>
				<th style="width: 50px">Starting Position</th>
				<th style="width: 50px">Char Size</th>
				<th>Data Type</th>
				<th>Data Pattern</th>
				

			</tr>

			<tr style="size: 100%;" id="First File">
				<!-- <td><input type="text" style="width: 50px;" readonly="readonly"	 id="reconcount" value="1" ></td> -->
				<td align="center" style="width: 250px;"><label  style="width: 150px;">File1 Headers</label></td>
				<td align="center"><select style="width:100px" id="fileselect1"><option	value="">--Select--</option></select></td>
				<td align="center" style="width: 80px"><input type="text"  style="width: 40px" id="value1" value=""	maxlength="6" cssClass="char_pos" onkeypress="return setValueType(this,'search')"> </td>
				<td align="center"><select id="condn1"><option value="">--Select--</option>
						<option value="=">=</option>
				 	<option value="!=">!=</option>
				 	<option value="LIKE">LIKE</option></select></td>
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
				

			</tr>
			<tr style="size: 100%;" id="Second File">

				<!-- <td><input type="text" style="width: 50px;"	readonly="readonly"	 id="reconcount2" value="1"></td> -->
				<td align="center" style="width: 250px;"><label style="width: 150px;">File2 Headers</label></td>
				<td align="center" ><select style="width:100px" id="fileselect2"><option
							value="">--Select--</option></select></td>
				<td align="center"  style="width: 80px"><input type="text"  style="width: 40px" id="value2" value="" maxlength="6" cssClass="char_pos" onkeypress="return setValueType(this,'search')"> </td>
				<td align="center"><select id="condn2"><option value="">--Select--</option>
						<option value="=">=</option>
				 	<option value="!=">!=</option>
				 	<option value="LIKE">LIKE</option></select></td>
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
				
			</tr>


		</table>
		<input type="hidden" id="datacount" value="0" > <input type="button" class="form-button" value="Add" class="" id="reconadd" onclick="compRow();" >


		<table id="detailtbl" style="display: none" >
		
				<tr>
					<th>File1 Header</th>
					<th>File2 Header</th>
				
				</tr>
			<c:forEach var="cnv" items="${ManualCompBean.columnDtls}" varStatus="loop">
			
		
		<%-- <form:hidden   path="file1" value="${CompareSetupBean.columnDtls}" />	 --%>
			
			
		
	<%-- 	<form:hidden    path="file1" value="1" />
		<form:hidden   path="file1" value="2" />
		<form:hidden     path="file1" value="3"/> --%>
		
		<tr>
		
				 <td>
				<form:input   id="columnDtls[${loop.index}].fileColumn1"  path="columnDtls[${loop.index}].fileColumn1" value="0" readonly="true" />
				<form:hidden  id="columnDtls[${loop.index}].stPadding"   path="columnDtls[${loop.index}].stPadding" value="0"/>
				<form:hidden  id="columnDtls[${loop.index}].inStart_Char_Position"   path="columnDtls[${loop.index}].inStart_Char_Position" value="0"/>
				<form:hidden  id="columnDtls[${loop.index}].inEnd_char_position"   path="columnDtls[${loop.index}].inEnd_char_position" value="0"/>
				<form:hidden  id="columnDtls[${loop.index}].dataType"   path="columnDtls[${loop.index}].dataType" value="0"/>
				<form:hidden  id="columnDtls[${loop.index}].datpattern"  path="columnDtls[${loop.index}].dataPattern" value="0"/>
				
				<form:hidden  id="columnDtls[${loop.index}].condn1"  path="columnDtls[${loop.index}].condn1" value="" />
				
				</td>
				
			
				 <td><form:input   id="columnDtls[${loop.index}].fileColumn2"  path="columnDtls[${loop.index}].fileColumn2" value="0" readonly="true" />
				<form:hidden  id="columnDtls[${loop.index}].stPadding2"  path="columnDtls[${loop.index}].stPadding2" value=""/>
				<form:hidden  id="columnDtls[${loop.index}].inStart_Char_Position2" path="columnDtls[${loop.index}].inStart_Char_Position2" value="0"/>
				<form:hidden  id="columnDtls[${loop.index}].inEnd_char_position2" path="columnDtls[${loop.index}].inEnd_char_position2" value="0"/>
				<form:hidden  id="columnDtls[${loop.index}].dataType2" path="columnDtls[${loop.index}].dataType2" value="0"/>
				<form:hidden  id="columnDtls[${loop.index}].datpattern2" path="columnDtls[${loop.index}].datpattern2" value="0"/>
			
				<form:hidden  id="columnDtls[${loop.index}].condn2"  path="columnDtls[${loop.index}].condn2" value="" /><input type="hidden" id="rowid" value=""></td>
				

			</tr>
					
			</c:forEach>
			
		
		</table>
		
	<input type="submit" align="left" class="form-button" value="Save" id="reconsave" onclick="return savemanData();"  >


			
			
			
			
			
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
