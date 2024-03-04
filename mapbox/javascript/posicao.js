//var latitude = 38.702314954617066;
//var longitude = -9.249271785242797;
let latitude;
let longitude;
function fly_back(){
   latitude = getLatitude();
   longitude = getLongitude();

    if(timeoutLocation()){
      map.flyTo({center: [longitude, latitude], zoom: 14});
  }
}

function fly_back_map(){
    //document.getElementsByClxassName("mapboxgl-ctrl mapboxgl-ctrl-group")[0].style.display="none";
    document.getElementsByClassName("mapboxgl-ctrl-geolocate")[0].click();

}

function timeoutLocation(){
    setTimeout(function(){
        if(latitude == undefined || longitude == undefined){
            alert("Não foi possível obter a sua localização.");
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

