<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
 
 <style>
		.modal {
  text-align: center;
  padding: 0!important;
}

.modal efore {
  content: '';
  display: inline-block;
  height: 100%;
  vertical-align: middle;
  margin-right: -4px;
}

.modal-dialog {
  display: inline-block;
  text-align: left;
  vertical-align: middle;
}
	</style>
	<style>
.zoom {
    
}

.zoom over {
    transform: scale(1.2); /* (150% zoom - Note: if the zoom is too large, it will go outside of the viewport) */
	transition: all 600ms ease-in;
}


.modalbox.success,
.modalbox.error {
  box-shadow: 0 1px 3px rgba(0,0,0,0.12), 0 1px 2px rgba(0,0,0,0.24);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  -webkit-border-radius: 2px;
  -moz-border-radius: 2px;
  border-radius: 2px;
  background: #fff;
  padding: 25px 25px 15px;
  text-align: center;
}
.modalbox.success.animate .icon,
.modalbox.error.animate .icon {
  -webkit-animation: fall-in 0.75s;
  -moz-animation: fall-in 0.75s;
  -o-animation: fall-in 0.75s;
  animation: fall-in 0.75s;
  box-shadow: 0 1px 3px rgba(0,0,0,0.12), 0 1px 2px rgba(0,0,0,0.24);
}
.modalbox.success h1,
.modalbox.error h1 {
  font-family: 'Montserrat', sans-serif;
}
.modalbox.success p,
.modalbox.error p {
  font-family: 'Open Sans', sans-serif;
}
.modalbox.success button,
.modalbox.error button,
.modalbox.success button:active,
.modalbox.error button:active,
.modalbox.success button ocus,
.modalbox.error button ocus {
  -webkit-transition: all 0.1s ease-in-out;
  transition: all 0.1s ease-in-out;
  -webkit-border-radius: 30px;
  -moz-border-radius: 30px;
  border-radius: 30px;
  margin-top: 15px;
  width: 80%;
  background: transparent;
  color: #4caf50;
  border-color: #4caf50;
  outline: none;
}
.modalbox.success button over,
.modalbox.error button over,
.modalbox.success button:active over,
.modalbox.error button:active over,
.modalbox.success button ocus over,
.modalbox.error button ocus over {
  color: #fff;
  background: #4caf50;
  border-color: transparent;
}
.modalbox.success .icon,
.modalbox.error .icon {
  position: relative;
  margin: 0 auto;
  margin-top: -75px;
  background: #4caf50;
  height: 100px;
  width: 100px;
  border-radius: 50%;
}
.modalbox.success .icon span,
.modalbox.error .icon span {
  postion: absolute;
  font-size: 4em;
  color: #fff;
  text-align: center;
  padding-top: 20px;
}
.modalbox.error button,
.modalbox.error button:active,
.modalbox.error buttonover {
  color: #fff;
  background: #4caf50;
  border-color: transparent;
}
.modalbox.success .icon,
.modalbox.error .icon {
  position: relative;
  margin: 0 auto;
  margin-top: -75px;
  background: #4caf50;
  height: 100px;
  width: 100px;
  border-radius: 50%;
}
.modalbox.success .icon span,
.modalbox.error .icon span {
  postion: absolute;
  font-size: 4em;
  color: #fff;
  text-align: center;
  padding-top: 20px;
}
.modalbox.error button,
.modalbox.error button:active,
.modalbox.error button:focus {
  color: #f44336;
  border-color: #f44336;
}
.modalbox.error button:hover,
.modalbox.error button:active:hover,
.modalbox.error button:focus:hover {
  color: #fff;
  background: #f44336;
}
.modalbox.error .icon {
  background: #f44336;
}
.modalbox.error .icon span {
  padding-top: 25px;
}
.center {
  float: none;
  margin-left: auto;
  margin-right: auto;
/* stupid browser compat. smh */
}
.center .change {
  clearn: both;
  display: block;
  font-size: 10px;
  color: #ccc;
  margin-top: 10px;
}
@-webkit-keyframes fall-in {
  0% {
    -ms-transform: scale(3, 3);
    -webkit-transform: scale(3, 3);
    transform: scale(3, 3);
    opacity: 0;
  }
  50% {
    -ms-transform: scale(1, 1);
    -webkit-transform: scale(1, 1);
    transform: scale(1, 1);
    opacity: 1;
  }
  60% {
    -ms-transform: scale(1.1, 1.1);
    -webkit-transform: scale(1.1, 1.1);
    transform: scale(1.1, 1.1);
  }
  100% {
    -ms-transform: scale(1, 1);
    -webkit-transform: scale(1, 1);
    transform: scale(1, 1);
  }
}
@-moz-keyframes fall-in {
  0% {
    -ms-transform: scale(3, 3);
    -webkit-transform: scale(3, 3);
    transform: scale(3, 3);
    opacity: 0;
  }
  50% {
    -ms-transform: scale(1, 1);
    -webkit-transform: scale(1, 1);
    transform: scale(1, 1);
    opacity: 1;
  }
  60% {
    -ms-transform: scale(1.1, 1.1);
    -webkit-transform: scale(1.1, 1.1);
    transform: scale(1.1, 1.1);
  }
  100% {
    -ms-transform: scale(1, 1);
    -webkit-transform: scale(1, 1);
    transform: scale(1, 1);
  }
}
@-o-keyframes fall-in {
  0% {
    -ms-transform: scale(3, 3);
    -webkit-transform: scale(3, 3);
    transform: scale(3, 3);
    opacity: 0;
  }
  50% {
    -ms-transform: scale(1, 1);
    -webkit-transform: scale(1, 1);
    transform: scale(1, 1);
    opacity: 1;
  }
  60% {
    -ms-transform: scale(1.1, 1.1);
    -webkit-transform: scale(1.1, 1.1);
    transform: scale(1.1, 1.1);
  }
  100% {
    -ms-transform: scale(1, 1);
    -webkit-transform: scale(1, 1);
    transform: scale(1, 1);
  }
}
@-webkit-keyframes plunge {
  0% {
    margin-top: -100%;
  }
  100% {
    margin-top: 25%;
  }
}
@-moz-keyframes plunge {
  0% {
    margin-top: -100%;
  }
  100% {
    margin-top: 25%;
  }
}
@-o-keyframes plunge {
  0% {
    margin-top: -100%;
  }
  100% {
    margin-top: 25%;
  }
}
@-moz-keyframes fall-in {
  0% {
    -ms-transform: scale(3, 3);
    -webkit-transform: scale(3, 3);
    transform: scale(3, 3);
    opacity: 0;
  }
  50% {
    -ms-transform: scale(1, 1);
    -webkit-transform: scale(1, 1);
    transform: scale(1, 1);
    opacity: 1;
  }
  60% {
    -ms-transform: scale(1.1, 1.1);
    -webkit-transform: scale(1.1, 1.1);
    transform: scale(1.1, 1.1);
  }
  100% {
    -ms-transform: scale(1, 1);
    -webkit-transform: scale(1, 1);
    transform: scale(1, 1);
  }
}
@-webkit-keyframes fall-in {
  0% {
    -ms-transform: scale(3, 3);
    -webkit-transform: scale(3, 3);
    transform: scale(3, 3);
    opacity: 0;
  }
  50% {
    -ms-transform: scale(1, 1);
    -webkit-transform: scale(1, 1);
    transform: scale(1, 1);
    opacity: 1;
  }
  60% {
    -ms-transform: scale(1.1, 1.1);
    -webkit-transform: scale(1.1, 1.1);
    transform: scale(1.1, 1.1);
  }
  100% {
    -ms-transform: scale(1, 1);
    -webkit-transform: scale(1, 1);
    transform: scale(1, 1);
  }
}
@-o-keyframes fall-in {
  0% {
    -ms-transform: scale(3, 3);
    -webkit-transform: scale(3, 3);
    transform: scale(3, 3);
    opacity: 0;
  }
  50% {
    -ms-transform: scale(1, 1);
    -webkit-transform: scale(1, 1);
    transform: scale(1, 1);
    opacity: 1;
  }
  60% {
    -ms-transform: scale(1.1, 1.1);
    -webkit-transform: scale(1.1, 1.1);
    transform: scale(1.1, 1.1);
  }
  100% {
    -ms-transform: scale(1, 1);
    -webkit-transform: scale(1, 1);
    transform: scale(1, 1);
  }
}
@keyframes fall-in {
  0% {
    -ms-transform: scale(3, 3);
    -webkit-transform: scale(3, 3);
    transform: scale(3, 3);
    opacity: 0;
  }
  50% {
    -ms-transform: scale(1, 1);
    -webkit-transform: scale(1, 1);
    transform: scale(1, 1);
    opacity: 1;
  }
  60% {
    -ms-transform: scale(1.1, 1.1);
    -webkit-transform: scale(1.1, 1.1);
    transform: scale(1.1, 1.1);
  }
  100% {
    -ms-transform: scale(1, 1);
    -webkit-transform: scale(1, 1);
    transform: scale(1, 1);
  }
}
@-moz-keyframes plunge {
  0% {
    margin-top: -100%;
  }
  100% {
    margin-top: 15%;
  }
}
@-webkit-keyframes plunge {
  0% {
    margin-top: -100%;
  }
  100% {
    margin-top: 15%;
  }
}
@-o-keyframes plunge {
  0% {
    margin-top: -100%;
  }
  100% {
    margin-top: 15%;
  }
}
@keyframes plunge {
  0% {
    margin-top: -100%;
  }
  100% {
    margin-top: 15%;
  }
}

</style>
 
  <form:form name="form" id="form" action="raiseChargeBackDownload.do" method="POST"  ></form:form>
 <script>
 $(document).ready(function(){
	 whatsNew();
	 $('#form').submit(); 
 });
 function whatsNew() {
		$("#whatsNewMod").modal("show");
	  }
	
 </script>
 
 
 <!-- Modal -->
<div id="whatsNewMod" class="modal fade whatsnewModal" data-backdrop="static" role="dialog">
  <div class="modal-dialog" style="width: 30%;">

    <!-- Modal content-->
    <div class="modal-content" style="background: transparent">
      <div class="modal-header" style="display: none;">
        <button type="button" class="close" data-dismiss="modal">&times;</button> 
        <h4 class="modal-title"> Whats New on the System!!</h4>
      </div>
      <div class="modal-body" >
        <div class="modalbox success center animate" >
			<div class="icon">
				<span class="glyphicon glyphicon-ok"></span>
			</div>
			<h1>Success!</h1>
			<p>  ${ message } </p>
							<button type="button" data-dismiss="modal" class="redo btn">Ok</button>
		</div>
      </div>
      <!-- <div class="modal-footer" style="display: none;">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div> -->
    </div>

  </div>
</div>