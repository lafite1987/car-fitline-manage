<!-- basic scripts -->

		<!--[if !IE]> -->
		<script type="text/javascript">
			window.jQuery || document.write("<script src='<%=request.getContextPath() %>/resources/ace/assets/js/jquery.min.js'>"+"<"+"/script>");
		</script>

		<!-- <![endif]-->

		<!--[if IE]>
		<script type="text/javascript">
 			window.jQuery || document.write("<script src='<%=request.getContextPath() %>/resources/ace/assets/js/jquery1x.min.js'>"+"<"+"/script>");
		</script>
		<![endif]-->
		<script type="text/javascript">
			if('ontouchstart' in document.documentElement) document.write("<script src='<%=request.getContextPath() %>/resources/ace/assets/js/jquery.mobile.custom.min.js'>"+"<"+"/script>");
		</script>
		<script src="<%=request.getContextPath() %>/resources/ace/assets/js/bootstrap.min.js"></script>

		<!-- page specific plugin scripts -->

		<!--[if lte IE 8]>
		  <script src="<%=request.getContextPath() %>/resources/ace/assets/js/excanvas.min.js"></script>
		<![endif]-->
		<script src="<%=request.getContextPath() %>/resources/ace/assets/js/jquery-ui.custom.min.js"></script>
		<script src="<%=request.getContextPath() %>/resources/ace/assets/js/jquery.ui.touch-punch.min.js"></script>
		<script src="<%=request.getContextPath() %>/resources/ace/assets/js/jquery.easypiechart.min.js"></script>
		<script src="<%=request.getContextPath() %>/resources/ace/assets/js/jquery.sparkline.min.js"></script>
		<script src="<%=request.getContextPath() %>/resources/ace/assets/js/flot/jquery.flot.min.js"></script>
		<script src="<%=request.getContextPath() %>/resources/ace/assets/js/flot/jquery.flot.pie.min.js"></script>
		<script src="<%=request.getContextPath() %>/resources/ace/assets/js/flot/jquery.flot.resize.min.js"></script>

		<!-- ace scripts -->
		<script src="<%=request.getContextPath() %>/resources/ace/assets/js/ace-elements.min.js"></script>
		<script src="<%=request.getContextPath() %>/resources/ace/assets/js/ace.min.js"></script>
		
