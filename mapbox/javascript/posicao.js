//var latitude = 38.702314954617066;
//var longitude = -9.249271785242797;

var defaultZoom = 14.03;
function fly_back(){
   read_cookies();
   if(firstTime){
       document.getElementById("starting").style.display="block";
   }
   if(window.location.href.search(defaultZoom)){
       if(longitude == 'undefined' || longitude == 'undefined' )
       //             && window.location.href.search(lat_start_lisbon) != -1)
       {
           latitude = getLatitude();
           longitude = getLongitude();
           if(timeoutLocation()){
               map.flyTo({center: [longitude, latitude], zoom: defaultZoom});
           }
       }else{

           map.flyTo({center: [longitude, latitude], zoom: defaultZoom});
           fechar_divs();
       }
   }
    add_logo();
}


function fly_back_map(){
    //document.getElementsByClxassName("mapboxgl-ctrl mapboxgl-ctrl-group")[0].style.display="none";
    document.getElementsByClassName("mapboxgl-ctrl-geolocate")[0].click();

}

function timeoutLocation(){
    setTimeout(function(){
        if(latitude == 'undefined' || longitude == 'undefined'){
            alert("Não foi possível obter a sua localização.");
            map.flyTo({center: [38.72091,-9.12751 ], zoom: 14});
            fechar_divs();
            return false;
        }else{
            map.flyTo({center: [longitude, latitude], zoom: 14});
            fly_back_map();
            fechar_divs();
            return true;
        }
    }, 5000);

}
function getLatitude(){
    if('geolocation' in navigator){
        navigator.geolocation.getCurrentPosition( 
            function(position) {
                latitude =  position.coords.latitude;
               
            },
            function(error) {
                console.log(error);
            },
            {timeout:7000});
    }

        return latitude;
}
function getLongitude(){
    if('geolocation' in navigator){
        navigator.geolocation.getCurrentPosition(
            (position) => {
                longitude =  position.coords.longitude;
            },
            (error) => {
                console.log(error);
            } ,{timeout:7000});
        
        
    }
    return longitude;
}
function save_cookies(cookie_name, cookie_value){
    document.cookie = cookie_name+"="+cookie_value;
}

function get_li_after_login(nome){
    if(nome.indexOf(' ') >= 0){
        nome = nome.split(" ")[0];
    }
    if(nome != null && nome.length > 0 && document.getElementById("li_login_a_link") != null
            && document.getElementById("sidebar_login_div") != null) {
        document.getElementById("li_login_a_link").innerHTML = nome;
        document.getElementById("sidebar_login_div").setAttribute("onclick", "open_div_login()");
    }

}
read_cookies();


