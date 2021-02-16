

var start_date = moment().format('YYYY-MM-DD');
var end_date = moment().add(7,'days').format('YYYY-MM-DD');


function printCellEvento(containerId, indiceEvento, cellId, titolo, linkEvento, comune, posto, linkPosto, data, descrizione, distanza, tags, meteo){
	document.getElementById(containerId).innerHTML = 	document.getElementById(containerId).innerHTML + 
			"<a name ='anchor_"+cellId+"' id ='anchor_"+cellId+"'/></a>" +
			" <div class='event_box'><div class='event_info'>" +
			"<div class='event_title' style='font-size:16px'>" +
			"<span style='font-size:24px; color:red;'>"+indiceEvento+".</span>&nbsp;&nbsp;&nbsp;<a href='"+ linkEvento +"' target=_'blank'>"+titolo+"</a></div> " +
			"<div class='speakers'><strong>Luogo: </strong><span> " + comune + "<a href='"+ linkPosto +"' target=_'blank'><b>"+posto+"</b></a></span></div>"
			+ distanza + data + meteo + tags +
			"<div><a data-toggle='collapse' data-target='#readMore"+cellId+"' aria-expanded='false' aria-controls='readMore"+cellId+"' style='text-decoration:underline; color:#f50136;''/><b>Leggi tutto &#187;</b></a></div>"+
			"<div class='collapse' id='readMore"+cellId+"'><div class ='card card-body'>" + descrizione + "</div>";
}

function printEventi(data,i, cellOffset, container){
	var obj = data;
	
	var dat_da = new Date(obj.data_da);
    var dat_a = new Date(obj.data_a);
    var da = dat_da.getDate()  + "/" + (dat_da.getMonth()+1) + "/" + dat_da.getFullYear();
    var a = dat_a.getDate()  + "/" + (dat_a.getMonth()+1) + "/" + dat_a.getFullYear();
    
    var distanza = "";
    if (obj.distanza != null && obj.distanza != ''){
    	distanza = "<div class='event_distance'><strong style='color: black;'>Distanza: </strong><span> circa "+parseInt(obj.distanza)+" km";
    	if(obj.centro_distanza != null && obj.centro_distanza != '') 
    		distanza = distanza + " da " + obj.centro_distanza;
    	distanza = distanza + "</span></div>";	
    }
    
    
    var meteoSingleLine = "";
    var meteo = "";
    if(obj.previsioni_evento && obj.previsioni_evento.length > 0){
    	if (obj.previsioni_evento.length == 1 && da == a){	
    		meteoSingleLine = "<span style='color:black'> | </span>" + obj.previsioni_evento[0].bollettino.condizioni + "  " + obj.previsioni_evento[0].bollettino.temp + "°C";
    	}
    	else{
    		meteoSingleLine= "<span style='color:black'> | </span><a data-toggle='collapse' data-target='#meteoCard"+cellOffset+"' aria-expanded='false' aria-controls='meteoCard"+cellOffset+"' style='text-decoration:underline;'/>Elenco previsioni meteo &#187;</a>"
    		meteo = "<div class='collapse event_meteo' id='meteoCard"+cellOffset+"'><div class ='col-sm-6 card card-body'><ul class='list-group list-group-flush'>";
    		var z;
    		for ( z = 0; z < obj.previsioni_evento.length; z++){
    			var dat = new Date(obj.previsioni_evento[z].bollettino.data);
    			var dataFormatted = dat.getDate()  + '/' + (dat.getMonth()+1) + '/' + dat.getFullYear();
    			
    			var startDate = new Date(start_date.slice(0,4), start_date.slice(5,7)-1, start_date.slice(8,10));
    			var endDate   = new Date(end_date.slice(0,4), end_date.slice(5,7)-1, end_date.slice(8,10));
    			var date_f  = new Date(dat.getFullYear(), dat.getMonth(), dat.getDate());
    					
    			var evidenzia = "";
    			if (date_f >= startDate && date_f <= endDate && date_f >= dat_da && date_f <= dat_a){
    				evidenzia = "style='color:black;'";
    			}
    			meteo = meteo + "<li class='list-group-item'"+evidenzia+">" + dataFormatted + ": " + obj.previsioni_evento[z].bollettino.condizioni + "  " + obj.previsioni_evento[z].bollettino.temp + "°C</li>";
    		}
    		meteo = meteo + "</ul></div></div>";
    	}
    }
    
    var date = "";
    if (da == a) date = "<div class='event_date'><span style='color:black'>Data:</span> "+da+ meteoSingleLine+"</div>";
    else date = "<div class='event_date'> <span style='color:black'>Dal:</span> "+da+" <span style='color:black'>al:</span> "+a+ meteoSingleLine+"</div>"; 
    
    var tags = "";
    if (obj.tags != null && obj.tags.length > 0){
    	tags = "<div class='event_tags'><span style='color:black'>Tags: </span>";
    	obj.tags.forEach(function(x, i, arr){ 
    		tags = tags + x; 
    		if (i < arr.length - 1) tags = tags + ", ";
    		});
    	tags = tags + "</div>";
    }
    	
    var comune = obj.comune;
    if (obj.posto_nome != "") comune = comune + " | ";
    
       
    printCellEvento(container, i+1, cellOffset, obj.titolo, obj.link, comune, obj.posto_nome, obj.posto_link, date, obj.descrizione, distanza, tags, meteo);
}


function createPlaceModal(placeId, placeName){
	
	placeModal =	'<div class="modal fade" id="placeModal'+placeId+'" tabindex="-1" role="dialog" aria-labelledby="placeModalLabel'+placeId+'" aria-hidden="true">'
					+'<div class="modal-dialog" role="document"><div class="modal-content"><div class="modal-header">'
					+'<h5 class="modal-title" id="placeModalLabel'+placeId+'">Aggiungi '+placeName+'</h5><button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>'
					+'</button></div><div class="form" id="placeEvalForm" method="post" enctype="multipart/form-data"><div class="modal-body">'
					
					+'<span><b>Seleziona la compagnia e lo stato emotivo in cui ti trovavi quando hai visitato questo posto</b></span>'
					
					+'<div class="form-group form-inline">'
					
					+'<input type="hidden" id="placeEvalId" value='+placeId+'>'
					+'<div><select class="form-control " id="emotionEval'+placeId+'">'
					+'<option value="joyful">Felice</option><option value ="sad">Triste</option><option value="angry">Arrabbiato</option></select></div>'
					+'<div><select class="form-control" id="companionshipEval'+placeId+'">'
					+'<option value="withFriends">In compagnia</option><option value="alone">Solo</option></select></div></div>'
					
					+'<small class="form-text text-muted"><i>Aggiungendo questo luogo alla tua lista dei posti visitati riceverai suggerimenti più accurati!</i></small>'
				
					+'</div><div class="modal-footer"><button type="button" class="btn btn-primary" onclick="sendSingleRating('+placeId+');" data-dismiss="modal">Conferma</button>'
					+'<button type="button" class="btn btn-secondary" data-dismiss="modal">Chiudi</button>'
					
					+'</div></form></div></div></div>';
	return placeModal;
}

function sendEval(placeId,emotion,companionship, callback){	
	for (var i = 0; i < allPlaces.length; i++){
		if(allPlaces[i].placeId == placeId)
			allPlaces[i].valutato = true;
	}
	
	
    $.ajax({
        type: 'post',
        url: 'http://127.0.0.1:8000/api/addRating/',
        data:{
        	'username': sessionStorage.getItem('userPugliaEvent'),
        	'place-id':placeId,
        	"emotion":emotion,
        	"companionship":companionship
        },
        success: function (response) {
        	document.getElementById("buttonStatus" + placeId).innerHTML = '<span class="badge badge-info" data-toggle="tooltip" data-placement="right" title="Luogo visitato"><i class="fa fa-check"></i></span>';	
        	console.log("rating added to " + placeId);
        	if(callback != null) {
        		callback();
        	}
        },
        error: function(e) {
          var je = JSON.parse(e.responseText);
           alert("ko");
        }
    });

}

function sendSingleRating(placeId){
	var mood = $("#emotionEval"+placeId).val(); 
	var comp = $("#companionshipEval"+placeId).val(); 
	sendEval(placeId,mood, comp,null);
}

function sendConfigRating(placeId){
	if (ancoraDaInserire == 0)
		sendEval(placeId,targetMood, targetCompanionship, window.location.reload.bind(window.location));
	else
		sendEval(placeId,targetMood, targetCompanionship, checkConfigIsDone);
}


function printCellPlace(containerId, indicePlace, cellId, nome, link, comune, indirizzo, contatti, tipo, distanza, tags, eventiProgrammati, buttonEvaluated, placeModal){
	document.getElementById(containerId).innerHTML = 	document.getElementById(containerId).innerHTML + "<div class='col-sm-6'>" + 
	"<a name ='anchor_"+cellId+"' id ='anchor_"+cellId+"'/></a>" +
	" <div class='event_box'><div class='event_info'>" +
	"<div class='event_title' style='font-size:16px'>" +
	"<span style='font-size:24px; color:red;'>"+indicePlace+".</span>&nbsp;&nbsp;&nbsp;<a href='"+ link +"' target=_'blank' rel='noopener noreferrer'>"+nome+"</a>"+ buttonEvaluated + "</div>" +
	"<div class='speakers'><span> " + comune + "<b>"+tipo+"</b></span></div>"+ distanza + contatti + tags + eventiProgrammati + "</div>"+placeModal;		
}




function printPlaces(data,i, cellOffset, container){
	var obj = data;

	var buttonEvaluated = "";
	var placeModal="";
	if(obj.valutato){
		buttonEvaluated = '&nbsp;&nbsp;<span id="buttonStatus'+obj.placeId+'"><span class="badge badge-info" data-toggle="tooltip" data-placement="right" title="Luogo visitato"><i class="fa fa-check"></i></span></span>';
	}
	else if(!isFirstConfig) {
		buttonEvaluated = '&nbsp;&nbsp;<span id="buttonStatus'+obj.placeId+'"><button type="button" class="btn-val btn btn-danger btn-sm" data-toggle="modal" data-target="#placeModal'+obj.placeId+'"><i class="fa fa-plus"></i></button></span>';
		placeModal = createPlaceModal(obj.placeId, obj.name);
	}
	else{
		buttonEvaluated = '&nbsp;&nbsp;<span id="buttonStatus'+obj.placeId+'"><button type="button" class="btn-val btn btn-danger btn-sm" onClick="sendConfigRating('+obj.placeId+')"><i class="fa fa-plus"></i></button></span>';
	}
	
	
    var distanza = "";
    if (obj.distanza != null && obj.distanza != ''){
    	distanza = "<div class='event_distance'><strong style='color: black;'>Distanza: </strong><span> circa "+parseInt(obj.distanza)+" km";
    	if(obj.centro_distanza != null && obj.centro_distanza != '') 
    		distanza = distanza + " da " + obj.centro_distanza;
    	distanza = distanza + "</span></div>";	
    }
    
    var tags = "";
    if (obj.tags != null && obj.tags.length > 0){
    	tags = "<div class='event_tags'><span style='color:black'>Tags: </span>";
    	obj.tags.forEach(function(x, i, arr){ 
    		tags = tags + x; 
    		if (i < arr.length - 1) tags = tags + ", ";
    		});
    	tags = tags + "</div>";
    }
    	
    var comune = obj.location;
    if (obj.tipo != "") comune = comune + " | ";
        
    contatti = "<div class='event_date'><span style='color:black'>Contatti: </span>" + obj.telefono; 
    if (obj.sitoweb !="") contatti = contatti + "<span style='color:black'> | </span>" + "<a href='" + obj.sitoweb + "' target = '_blank' rel='noopener noreferrer'/>sito web &#187;</a>" ;
    contatti = contatti + "</div>";
	
    eventiProgrammati = "";
    if(obj.eventi_programmati.length > 0){
    	eventiProgrammati = "<div><a data-toggle='collapse' data-target='#readEvents"+cellOffset+"' aria-expanded='false' aria-controls='readEvents"+cellOffset+"' style='text-decoration:underline; color:#f50136;'/><b>Eventi programmati &#187;</b></a></div>"+
    	"<div class='collapse' id='readEvents"+cellOffset+"'><div class ='card card-body'><ul class='list-group list-group-flush'>";
    	var z;
		for ( z = 0; z < obj.eventi_programmati.length; z++){
			var dat_da = new Date(obj.eventi_programmati[z].data_da);
		    var dat_a = new Date(obj.eventi_programmati[z].data_a);
		    var dataInizio = dat_da.getDate()  + "/" + (dat_da.getMonth()+1) + "/" + dat_da.getFullYear();
		    var dataFine = dat_a.getDate()  + "/" + (dat_a.getMonth()+1) + "/" + dat_a.getFullYear();
			
		    eventiProgrammati = eventiProgrammati + "<li class='list-group-item'><a href='"+obj.eventi_programmati[z].link+"' target='_blank' style='text-decoration:underline;'><b>"+ obj.eventi_programmati[z].titolo+"</b></a><br>";
			if(dataInizio == dataFine)
				eventiProgrammati = eventiProgrammati + "Data: "+ dataInizio + "</li>";
			else
				eventiProgrammati = eventiProgrammati + "Dal: "+ dataInizio + " al: " + dataFine + "</li>";
			
		}
    	
    	eventiProgrammati = eventiProgrammati + "</ul></div>";
    }
       
    printCellPlace(container, i+1, cellOffset, obj.name, obj.link, comune, obj.indirizzo, contatti, obj.tipo, distanza, tags, eventiProgrammati, buttonEvaluated, placeModal);
}













function pagin(tipo){
$('#pagination-demo').twbsPagination({
        totalPages: numev,
        visiblePages: 7,
        onPageClick: function (event, page) {
            $('#page-content').text('Page ' + page);
            if (tipo == 0)loadPageEvents(page);
            else loadPagePlaces(page);
            


        }
    });
  }

function makePages(len, tipo){
	var numP = parseInt(len);
	numev = Math.floor((numP / 10));
	//console.log("len: " + len + " pages: " + numev);
    if(numP%10 != 0){
        numev = Math.floor((numP / 10)+1);
    }
    pagin(tipo);
}


function createPayloadData(topicData, noWeatherData){	
	var location = '"location":' +'"'+ document.getElementById("comune").value+'"';
	var range = '"range":' + document.getElementById("slider-value").innerHTML;
	var weather = '"weather":' + document.getElementById("sliderMeteo-value").innerHTML;
	var NoWeatherData = '"no-weather-data":'+ noWeatherData;
	 var behavior = '"behavior":{"empathy":'+ sessionStorage.getItem('empathy') +
		 			', "agreeableness":'+ sessionStorage.getItem('agreeableness') +
		 			', "conscientiousness":'+ sessionStorage.getItem('conscientiousness') +
		 			', "extroversion":'+ sessionStorage.getItem('extroversion') +
		 			', "neuroticism":'+ sessionStorage.getItem('neuroticism') +
		 			', "openness":'+ sessionStorage.getItem('openness') +
		 			'}';
 	var topics = '"topics":'+JSON.stringify(topicData.results.entry);
	var payload = '{"data":{'+location+ ', '+ range + ', ' + weather + ', ' + NoWeatherData + ', ' + behavior + ", " + topics + '}}';
	return payload;  
}



$( "#comune" ).autocomplete({ minLength: 0 });
$( "#comune" ).autocomplete( "option", "minLength", 2 );
$( "#nomeLuogo" ).autocomplete({ minLength: 0 });
$( "#nomeLuogo" ).autocomplete( "option", "minLength", 4 );
$( "#nomeEvento" ).autocomplete({ minLength: 0 });
$( "#nomeEvento" ).autocomplete( "option", "minLength", 4 );


$(function() {
	$( "#comune" ).autocomplete({
		source: function( request, response ) {
			$.ajax({
				type: 'get',
				crossDomain: true,
				//url: 'http://127.0.0.1:8080/PugliaEventi/rest/services/getComuni/'+request.term,
				url: DJANGO_API_ADDR + 'getComuni/'+request.term,
				contentType: "application/json",
                success: function( data ) {
                	response( data );
                }
            });
        },
        change: function (event, ui) {
            if(!ui.item){
                $("#comune").val("");
            }
        },
    });
    $( "#nomeEvento" ).autocomplete({
    	source: function( request, response ) {
    		$.ajax({
    			type: 'get',
    			crossDomain: true,
                //url: 'http://127.0.0.1:8080/PugliaEventi/rest/services/getEvento/'+request.term,
    			url: DJANGO_API_ADDR + 'getEventi/'+request.term,
                contentType: "application/json",
                success: function( data ) {
                	response( data );
                }
    		});
    	},
    });
    $( "#nomeLuogo" ).autocomplete({
    	source: function( request, response ) {
    		$.ajax({
    			type: 'get',
    			crossDomain: true,
    			url: DJANGO_API_ADDR + 'getLuoghi/'+request.term,
                contentType: "application/json",
                success: function( data ) {
                	response( data );
                }
    		});
    	},
    });
});













/*function countEv(){


$.ajax({
    type: 'get',
    url: 'http://127.0.0.1:8080/PugliaEventi/rest/services/countEvents/' + URIConstructor(),
    contentType: "application/json",
    success: function (response) {
        //var obj = response[0];
        var numP = parseInt(allEvents.length);
		
        //mostra numero di eventi trovati
        //showNumFoundEvents(numP);
        
        numev = Math.floor((numP / 10));

        if(numP%10 != 0){
            numev = Math.floor((numP / 10)+1);
        }
        console.log("min " + Math.min(7, numev));
        pagin();
      /*},

    error: function(e) {
      var je = JSON.parse(e.responseText);
       alert("ko");
    }
});
}*/
/*function URIConstructor(){
var radioData = document.getElementsByName('date');
var place = document.getElementsByName('comune')[0].value;
var range = document.getElementById("slider-value").innerHTML;
var interval = null;

if (place == "") place = "all-cities";
for (var i = 0, length = radioData.length; i < length; i++){
 if (radioData[i].checked){
  interval = radioData[i].value;
  break;
 }
}

	var URI = place + "/" + range + "/" + interval;
	console.log(URI);
	return URI;
}*/

/*function changePageEv(page) {
$.ajax({
	type: 'get',
    url: 'http://127.0.0.1:8080/PugliaEventi/rest/services/getEvents/' + URIConstructor()+ '/'+((page*10)-10),
    contentType: "application/json",
    success: function (response) {
          $("#eventiContainer").html("");
        for (var i = 0; i < 10; i++){
        	printEventi(response[i],i+((page*10)-10), i+((page*10)-10), "eventiContainer");
        }
        setMore();
      },
      error: function(e) {
        var je = JSON.parse(e.responseText);
         alert("ko");
      }
  });
};*/


/*function loadEv( ) {
    $.ajax({
        type: 'get',
        url: 'http://127.0.0.1:8080/PugliaEventi/rest/services/getEvents/' + URIConstructor()+ '/0',
        contentType: "application/json",
        success: function (response) {
   
          $("#eventiContainer").html("");
          for (var i = 0; i < Math.min(10, response.length); i++){
            //var obj = response[i];
            printEventi(response[i],i, i+0, "eventiContainer");
          }
          setMore();
        },
        error: function(e) {
          var je = JSON.parse(e.responseText);
           alert("ko");
        }
    });
};*/




/*function setMore(){
  var i = 0;
    $('.sliderb').each(function () {
                var current = $(this);
                current.attr("box_h", current.height()+50);
                current.css("overflow", "hidden");
                //current.css("height", sliderHeight);
                current.css("height", 0);
                current.css("display", "none");
                $("#slider_menu_"+i).html('<a><span style="color:#f50136;"><b>Leggi tutto &#187;</b></span></a>');
                $("#slider_menu_"+i+" span").attr("onclick","openSlider("+i+");");
                  i = i+ 1;
            });
            var url = location.href;               //Save down the URL without hash.
            location.href = "#top";                 //Go to the target element.
            history.replaceState(null,null,url);


}

function openSlider(i){
    var open_height = $("#event"+i).attr("box_h") + "px";
    $("#event"+i).css("display", "block");
    $("#event"+i).animate({"height": open_height}, {duration: "slow" });
    $("#slider_menu_"+i).html('<a><span style="color:black;"><b>Chiudi</b></span></a>');
    $("#slider_menu_"+i+" span").attr("onclick","closeSlider("+i+");");
}

function closeSlider( i){
    $("#event"+i).animate({"height": sliderHeight}, {duration: "slow" });
    $("#slider_menu_"+i).html('<a><span style="color:#f50136;"><b>Leggi tutto &#187;</b></span></a>');
    $("#slider_menu_"+i+" span").attr("onclick","openSlider("+i+");");
    var url = location.href;               //Save down the URL without hash.
    location.href = "#anchor_"+i;                 //Go to the target element.
    history.replaceState(null,null,url);
    window.scrollTo(window.scrollX, window.scrollY - 100);
    $("#event"+i).css("display", "none");
}*/

/*function showNumFoundEvents(n){
document.getElementById("counterEvents").innerHTML= " ("+ n + " trovati)";
}*/


/*function loadLocation(){
document.getElementById("comune").value = sessionStorage.getItem('userLocation');
}*/
