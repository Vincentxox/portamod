        function thumbRef() {        	
    		jq(".thumbnail-ref").addClass("visible");
    		jq(".thumbnail-ref").removeClass("no-visible");
    		jq(".thumbnail-vacio").removeClass("visible");
    		jq(".thumbnail-vacio").addClass("no-visible");
        }
        function thumbVacio() {
    		jq(".thumbnail-vacio").addClass("visible");
    		jq(".thumbnail-vacio").removeClass("no-visible");
    		jq(".thumbnail-ref").removeClass("visible");
    		jq(".thumbnail-ref").addClass("no-visible");
        }
        
        function reset(){
	        jq(".panel1").show();
	        jq(".panel2").hide();
	        jq(".panel3").hide();
	        jq(":text").each(function(){	
				jq(jq(this)).val('');
	        });
	        jq('input:checkbox').removeAttr('checked');
	        jq('input:radio').prop('checked', false);
	        thumbVacio();
        }        
        function showPanel1(){
	        jq(".panel1").show();
	        jq(".panel2").hide();
	        jq(".panel3").hide();
        }
        function showPanel2(){
        	jq(".panel1").hide();
	        jq(".panel2").show();
	        jq(".panel3").hide();
        }
        function showPanel3(){
        	jq(".panel1").hide();
	        jq(".panel2").hide();
	        jq(".panel3").show();
        }
        function centrar(){
        	var ventana_ancho = jq(window).width();
        	var popup_ancho = jq('.modal-num-ant').width();
        	var ventana_alto = jq(window).height();
        	var popup_alto = jq('.modal-num-ant').height();
        	var espacio_sinuso = ventana_ancho-popup_ancho;
	        jq('.modal-num-ant').css("left", espacio_sinuso/2);
	        jq('.modal-num-ant').css("right", espacio_sinuso/2);
	        if (ventana_alto < popup_alto){
	        	jq('.z-window-content').css("height", popup_alto);
	        }
        }