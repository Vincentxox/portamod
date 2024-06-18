var alto;
var alto_menu;
var alto_static_header

jq(window).load(function(){
	var ancho = jq(".static-header").width();
	jq(".app").css("max-width",ancho-93+"px");
	alto = jq(".principal-header").height();
	alto_static_header = jq(".static-header").height();
	alto_menu = alto + alto_static_header;
    jq(".menu").css("top",alto_menu+"px");
});

function reloadPage(){
	var ancho = jq(".static-header").width();
	jq(".app").css("max-width",ancho-93+"px");
	alto = jq(".principal-header").height();
	alto_static_header = jq(".static-header").height();
	alto_menu = alto + alto_static_header;
    jq(".menu").css("top",alto_menu+"px");
    jq(".dynamic-header").css("display","none");
}

//jq(":root").ready(function(){
//	jq(window).bind('scroll', function(e) {
//        var alto_dynamic_header = jq(".dynamic-header").height();
//		if ( jq(window).scrollTop() > alto){
//	        jq(".dynamic-header").css("display","block");
//	        var alto_dynamic_header = jq(".dynamic-header").height();
//	        jq(".menu").css("top",alto_dynamic_header+"px");	           
//	      } else {
//	    	jq(".dynamic-header").css("display","none");
//	    	jq(".menu").css("top",alto_menu+"px");
//	      } 
//
//	});
//	
//	jq(window).scroll(function() {	
//	      
//	});
//});

jq(window).resize(function() {
	var ancho = jq(".static-header").width();
	jq(".app").css("max-width",ancho-93+"px");
	
});


function show_menu() { 
	jq(".principal-menu span").hide();
	if(jq(".principal-menu").is(":visible")) {
		jq(".ico-menu img").fadeOut(300,"swing", function(){
			jq(".ico-menu img").attr("src","img/abrir.png");
			jq(".ico-menu img").fadeIn("slow", "swing");
												});
	} else {
		jq(".ico-menu img").fadeOut(300,"swing", function(){
			jq(".ico-menu img").attr("src","img/cerrar.png");
			jq(".ico-menu img").fadeIn("slow", "swing");
													});
	}
	jq(".background-menu").toggle("slow","swing");
	jq(".principal-menu").toggle("slow","swing", function(){
		if(jq(".principal-menu").is(":visible")) {
			jq(".principal-menu span").fadeIn("slow","swing");
		}
											});
}


function changeActive(value) {
	if(value == "true"){
		jq(".chkActive").attr("checked", true);
	} else {
		jq(".chkActive").attr("checked", false);
	}	
}